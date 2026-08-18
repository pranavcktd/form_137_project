/*
 * Author : Madhav
 * SS Version : 5
 */
package com.tin.etbaf.form24G.util;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.security.Security;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.StringTokenizer;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

// sun.misc.BASE64Decoder/Encoder removed from the JDK in Java 9+; these two
// imports were unused dead code in the vendor source (grep confirms no
// other reference to either class in this file), so they're dropped here
// rather than patched — no behavior change, just what's needed to compile
// on a modern JDK.


/*
ErrorVal	Remarks
------------------------
0		Success (no errors)
1		Input File not found
2		Invalid Input File (
					i. Empty record found 
					ii. At FVU file is having sam/scm info 
					iii. At svu uploadedby is not TFC and file is having sam/scm info
				   )
3		FVU Version is either Incorrect or NULL
4		SAM Version is either Incorrect or NULL
5		SCM Version is either Incorrect or NULL
6		File Header Record Hash Code mismatch
7		Record Level Hash Code mismatch
8		Input File format Error (also if mismatch of number of in the input file.
					For a new file the last char in a record should be '^', 
					and for a file that is already hashed the last character should not be '^')
9		Mismatch of FVU File Level HashCode
10		Mismatch of SAM File Level HashCode
11		Mismatch of SCM File Level HashCode
12		Unknown Error. Exception occured
*/
public class Hash
{
   private String fhString;
   private long fileLevelHC;
   private long fhHC;
   private long fvuFLH;
   private long samFLH;
   private long scmFLH;
   private String fvuVersion = null;
   private String samVersion = null;
   private String scmVersion = null;
   private int utilityLevel;
   private int paperReturnIndiFlag;
   private String outputFileName;
   private String recStr;
   private int fhFieldCount;
   private BufferedReader br = null;
   private int iErrCode;
   public final static int FVU = 0;
   public final static int SAM = 1;
   public final static int SCM = 2;
   public final static int SVU = 3;
   private int recNo = 0;
   public int startProcessing(String inputFileName, String outputHashFile, int utilityLevel, int fhFieldCount, int paperReturnIndiFlag)
   {
      this.outputFileName = outputHashFile;
      this.utilityLevel = utilityLevel;
      this.fhFieldCount = fhFieldCount;
      this.paperReturnIndiFlag = paperReturnIndiFlag;
      iErrCode = 0;
      try
      {
         File file = new File(inputFileName);
         if (file.exists() == false)
         {
        	 
            iErrCode = 1; //input file not found
            return iErrCode;
         }
         br = new BufferedReader(new FileReader(file));
         recStr = br.readLine();
        
         if (recStr == null || recStr.length() == 0)
         {
        	
            iErrCode = 2; //empty record found
            return iErrCode;
         }
        
         tokenizeFileHeader();
       
        
         if (iErrCode == 2)
         {
        	
            recNo = 1;
            return iErrCode; //Invalid input file
         }
     
         switch (utilityLevel)
         {
          
            case FVU :
               processFVU();
               break;
            case SAM :
               processSAM();
               break;
            case SCM :
               processSCM();
               break;
            case SVU :
               processSVU();
               break;
         }
        
      }
      catch (Exception e)
      {
         recNo = 1;
         iErrCode = 12; //unknown error. exception occured.
         Log.tbaf_log.error("Exception", e);
         e.printStackTrace();
      }
      finally
      {
         try
         {
            if (br != null)
               br.close();
         }
         catch (IOException e1)
         {
            Log.tbaf_log.error("Exception", e1);
            e1.printStackTrace();
         }
      }
      
      return iErrCode;
    
   }
   //this method is called at FVU. If the file is not hashed then processFVU_FileNotHashed is called. else processFVU_FileHashed is called. 	
   private boolean processFVU()   {
      if (samVersion != null || samFLH > 0 || scmVersion != null || scmFLH > 0)
      {
         //In this case SAM version,SAM hash,SCM version,SCM hash 
         //should not be present.  
    	 Log.tbaf_log.info("Found Either Samversion or SAM File hash or SCM Version or SCM Hash. ");
         recNo = 1;
         iErrCode = 2; //Invalid Input file
         return false;
      }
      if (fvuVersion == null && fvuFLH == 0)
      {
         return processFVU_FileNotHashed();
      }
      else
      {
         return processFVU_FileHashed();
      }
   }
   //  this method is called at SAM. If the file is hashed processFVU_FileHashed is called to verify hash.
   private boolean processSAM()
   {
      
	   
      if (scmVersion != null || scmFLH > 0 || samVersion != null && samFLH > 0 || fvuVersion == null || fvuFLH == 0)
      {
         //in this case FVU version, FVU hash must be present and SAM version,SAM hash,SCM version,SCM hash 
         //should not be present.
    	 
         recNo = 1;
         iErrCode = 2; //Invalid Input file
         return false;
      }

      if (samVersion == null && samFLH == 0)
      {
    	 
         return processFVU_FileHashed();
      }
      else
      {
    	 
         recNo = 1;
         iErrCode = 2; //Invalid Input file
         return false;
      }

   }

   private boolean processSAM_FileHashed()
   {

      long rlh;
      try
      {
         recNo = 1;
         if (Parameters.ignoreHashing == true)
            return true;

         recStr = recStr.substring(0, recStr.lastIndexOf('^', recStr.lastIndexOf('^', recStr.lastIndexOf('^') - 1) - 1));
         fileLevelHC = hashCode(recStr, true);

         if (Parameters.ignoreRecordLevelHashing == false)
         {
            while ((recStr = br.readLine()) != null)
            {
               recNo++;
               if (recStr.charAt(recStr.length() - 1) == '^')
               {
                  iErrCode = 8; //Input File format Error
                  return false;
               }
               try
               {
                  rlh = Long.parseLong((recStr.substring(recStr.lastIndexOf('^') + 1, recStr.length())).trim());
                  recStr = recStr.substring(0, recStr.lastIndexOf('^'));
                  if (rlh != hashCode(recStr, true))
                  {
                     iErrCode = 7; //Record Level Hash Code mismatch at line: recNo
                     return false;
                  }
                  fileLevelHC += (recNo * rlh);
               }
               catch (NumberFormatException e)
               {
                  iErrCode = 6;
                  Log.tbaf_log.error("Exception", e);//Input File format Error
                  return false;
               }
            }
         }
         else
         {
            while ((recStr = br.readLine()) != null)
            {
               recNo++;
               recStr = recStr.substring(0, recStr.lastIndexOf('^'));
               fileLevelHC += (recNo * hashCode(recStr, true));
            }
         }
         if (fileLevelHC != samFLH)
         {
            iErrCode = 10; //Mismatch of SAM Level HashCode
            return false;
         }
         return true;
      }
      catch (Exception e)
      {
    	 Log.tbaf_log.error("Exception", e);
         e.printStackTrace();
      }
      return false;

   }

   private boolean processSAM_FileNotHashed()
   {
      BufferedWriter bw = null;
      long pos = 0;
      try
      {
         recNo = 1;
         bw = new BufferedWriter(new FileWriter(outputFileName));
         recStr = recStr.substring(0, recStr.lastIndexOf('^', recStr.lastIndexOf('^', recStr.lastIndexOf('^') - 1) - 1));
         recStr = recStr + Parameters.tbafsamVersion[0] + '^';
         pos = recStr.length();
         fileLevelHC = hashCode(recStr, false);
         bw.write(recStr);
         bw.write(get20digitHashCode(fileLevelHC));
         bw.write("^^");
         bw.write('\n');
         while ((recStr = br.readLine()) != null)
         {
            recNo++;
            bw.write(recStr);
            bw.write('\n');
            fileLevelHC += (recNo * Long.parseLong(recStr.substring(recStr.lastIndexOf('^') + 1, recStr.length())));
         }
      }
      catch (Exception e)
      {
    	 Log.tbaf_log.error("Exception", e);
         e.printStackTrace();
      }
      finally
      {
         try
         {
            bw.close();
         }
         catch (IOException e2)
         {
        	 Log.tbaf_log.error("Exception", e2);
            e2.printStackTrace();
            return false;
         }
      }
      RandomAccessFile raf = null;
      try
      {
         raf = new RandomAccessFile(outputFileName, "rw");
         raf.seek(pos);
         raf.writeBytes(get20digitHashCode(fileLevelHC));
      }
      catch (IOException e1)
      {
         e1.printStackTrace();
         Log.tbaf_log.error("Exception", e1);
         return false;
      }
      finally
      {
         try
         {
            raf.close();
         }
         catch (IOException e2)
         {
            e2.printStackTrace();
            Log.tbaf_log.error("Exception", e2);
            return false;
         }
      }
      return true;
   }

   //  this method is called at SCM. If the file is hashed processSAM_FileHashed is called to verify hash.
   private boolean processSCM()
   {
	  if (paperReturnIndiFlag == 2)
	  {
	  	/* This is a PAPER RETURN FILE. FVU Hash and FVU Version will be checked.
	  	 * SAM Version and SAM Hash will not be present in file 
	  	 */
		return processSAM();
	  }
      else if(samVersion == null || samFLH == 0 || fvuVersion == null || fvuFLH == 0 || scmVersion != null || scmFLH > 0)
      {
         /* This is a ELECTRONIC RETURN FILE. In this case SAM version,SAM hash, FVU version,
          * FVU hash must be present and SCM version,SCM hash should not be present.
          */ 
    	 
         recNo = 1;
         iErrCode = 2; //Invalid Input file
         return false;
      }
      else
      {
    	 
         return processSAM_FileHashed();
      }
   }

   private boolean processSCM_FileNotHashed()
   {
      BufferedWriter bw = null;
      long pos = 0;
      try
      {
         recNo = 1;
         bw = new BufferedWriter(new FileWriter(outputFileName));
         recStr = recStr.substring(0, recStr.lastIndexOf('^')) + Parameters.tbafscmVersion[0] + '^';
         pos = recStr.length();
         fileLevelHC = hashCode(recStr, false);
         bw.write(recStr);
         bw.write(get20digitHashCode(fileLevelHC));
         bw.write('\n');
         while ((recStr = br.readLine()) != null)
         {
            recNo++;
            bw.write(recStr);
            bw.write('\n');
            fileLevelHC += (recNo * Long.parseLong(recStr.substring(recStr.lastIndexOf('^') + 1, recStr.length())));
            //				recStr = recStr.substring(0, recStr.lastIndexOf('^'));
            //				fileLevelHC += (recNo * hashCode(recStr));
         }
      }
      catch (Exception e)
      {
         e.printStackTrace();
         Log.tbaf_log.error("Exception", e);
      }
      finally
      {
         try
         {
            bw.close();
         }
         catch (IOException e2)
         {
            e2.printStackTrace();
            Log.tbaf_log.error("Exception", e2);
         }
      }
      RandomAccessFile raf = null;
      try
      {
         raf = new RandomAccessFile(outputFileName, "rw");
         raf.seek(pos);
         raf.writeBytes(get20digitHashCode(fileLevelHC));
      }
      catch (IOException e1)
      {
         e1.printStackTrace();
         Log.tbaf_log.error("Exception", e1);
      }
      finally
      {
         try
         {
            raf.close();
         }
         catch (IOException e2)
         {
            e2.printStackTrace();
            Log.tbaf_log.error("Exception", e2);
         }
      }
      return false;
   }
   //****************************
   //this method is called at svu.
   /*
    * FH Record hash, FVU and SAM File hash codes are not verified
    * FVU,SAM and SCM Versions are verified
    * Record level hash verification is parameterized
    * hashing and versioning verification are also parameterized
    */
   private boolean processSVU()
   {
      boolean isTFC;
      long rlh;
      long recLevelHC;
      int k;
      long fvuFileLevelHC = 0;
      //		long samFileLevelHC = 0;
      long samFileLevelHC = 0;   //Added By Subhankar....
     // long scmFileLevelHC = 0;
      if (recStr.indexOf("^T^") != -1)
      {
         isTFC = true; //uploaded by is TFC
      }
      else
      {
         isTFC = false; //uploaded by is Filer
      }
      try
      {
         //if upload by is Filer and sam, scm details are present in the file then reject the file...
         if (isTFC == false)
         {
            if (samVersion != null || samFLH > 0 || scmVersion != null || scmFLH > 0)
            {
               recNo = 1;
               iErrCode = 2; //Invalid File
               return false;
            }
         }
         else /*******SID ADD THIS IN AIR CODE ALSO*******START****/
            {
//            if (scmVersion == null || scmFLH == 0)
        	 //Added to remove scm hash validation. From now onwards file will not contain SCM hash.
        	 //File will be passed only through SAM.
        	 if ((scmVersion != null || scmFLH > 0) && (samVersion == null || samFLH <= 0 ) )
            {
               recNo = 1;
               iErrCode = 2; //Invalid File
               return false;
            }
         } /*******SID ADD THIS IN AIR CODE ALSO********END***/
         recNo = 1;
         /*****************************************************************************************************************
         			if (Parameters.ignoreRecordLevelHashing == false && fhHC != hashCode(fhString, true))
         			{
         				return false;
         			}
         *****************************************************************************************************************/
         if (Parameters.ignoreVersioning == false)
         {
            if (isValidFVUVersion(fvuVersion) == false)
            {
               iErrCode = 3; //Incorrect FVU Version
               return false;
            }
            //if upload by is TFC then sam and scm details cannot be null
            if (isTFC == true)
            {
               if (isValidSAMVersion(samVersion) == false)
               {
                  iErrCode = 4; //Incorrect SAM Version
                  return false;
               }
               //Commented to remove SCM hash.
//               if (isValidSCMVersion(scmVersion) == false)
//               {
//                  iErrCode = 5; //Incorrect SCM Version
//                  return false;
//               }
            }
         }
         if (Parameters.ignoreHashing == true)
            return true;
         k = recStr.lastIndexOf('^');    
         recStr = recStr.substring(0, k);
         //scmFileLevelHC = hashCode(recStr, true);   //Commented By Subhankar to stop SCM Hash
         k = recStr.lastIndexOf('^', k - 1);
         k = recStr.lastIndexOf('^', k - 1);
         recStr = recStr.substring(0, k);
        //	samFileLevelHC = hashCode(recStr, true);
         
         samFileLevelHC = hashCode(recStr, true);  //Added By Subhankar
         k = recStr.lastIndexOf('^', k - 1);
         k = recStr.lastIndexOf('^', k - 1);
         recStr = recStr.substring(0, k);
         fvuFileLevelHC = hashCode(recStr, true);
         fileLevelHC = 0;
         if (Parameters.ignoreRecordLevelHashing == true)
         {
            while ((recStr = br.readLine()) != null)
            {
               recNo++;
               //recStr = recStr.substring(0, recStr.lastIndexOf('^'));
               k = recStr.lastIndexOf('^');
               try
               {
                  recLevelHC = Long.parseLong(recStr.substring(k + 1, recStr.length()));
               }
               catch (NumberFormatException e)
               {
                  iErrCode = 6;
                  Log.tbaf_log.error("Exception", e);//Input File format Error
                  return false;
               }

               fileLevelHC += (recNo * recLevelHC);
            }
         }
         else
         {
            while ((recStr = br.readLine()) != null)
            {
               recNo++;
               k = recStr.lastIndexOf('^');
               try
               {
                  recLevelHC = Long.parseLong(recStr.substring(k + 1, recStr.length()));

               }
               catch (NumberFormatException e)
               {
                  iErrCode = 6;
                  Log.tbaf_log.error("Exception", e);//Input File format Error
                  return false;
               }

               recStr = recStr.substring(0, k);
               rlh = hashCode(recStr, true);
               if (rlh != recLevelHC)
               {
                  iErrCode = 7;
                  //mismatch of RecordLevel Hash Code at Record: recNo
                  return false;
               }
               fileLevelHC += (recNo * recLevelHC);
            }
         }
         fvuFileLevelHC += fileLevelHC;
         if (isTFC == true)
         {
            /*****************************************************************************************************************
            				//  sam is supposed to update the fh record-level hashcode and fvu file-level hashcode 
            				//  after making changes to the original file. If it does then uncomment the following block.
            				
            							if (fvuFileLevelHC != fvuFLH)
            							{
            								return false;
            							}
            				
            				//	samFileLevelHC += fileLevelHC;
            				//	scm is supposed to update the sam file-level hashcode 
            				//	after making changes to the file. If it does then uncomment the following block.
            								if (samFileLevelHC != samFLH)
            								{
            									return false;
            								}
            				//
            *****************************************************************************************************************/
          //  scmFileLevelHC += fileLevelHC;     //Commented By Subhankar to stop SCM Hash
        	 
        	 samFileLevelHC += fileLevelHC ;
        	 
            //	checking the details of scm
      	 
        /*    if (scmFileLevelHC != scmFLH)
            {
               iErrCode = 11; //mismatch of SCM File Level HashCode
               return false;
            }*/
        	 
        	 
        	 if (samFileLevelHC != samFLH)
             {
                iErrCode = 11; //mismatch of SAM File Level HashCode
                return false;
             }
        	 
        	 
         }
         else
         {
            if (fvuFileLevelHC != fvuFLH)
            {
               iErrCode = 9; //mismatch of FVU File Level HashCode
               return false;
            }
         }
         
         return true;
      
      }
      catch (Exception e)
      {
    	 
         e.printStackTrace();
         Log.tbaf_log.error("Exception", e);
      }
      return false;
   }

   private boolean processFVU_FileNotHashed()
   {
      long rlh = 0;
      recNo = 1;
      boolean retVal = true;
      BufferedWriter hf = null;
      RandomAccessFile raf = null;
      try
      {
         hf = new BufferedWriter(new FileWriter(outputFileName));
         recStr = getFileHeader();
         hf.write(recStr);
         hf.write('^');
         fileLevelHC = hashCode(recStr, true);
         //			if (Parameters.ignoreHashing == false)
         hf.write(get20digitHashCode(fileLevelHC));
         System.out.println("Cuurent fileLevelHC :"+fileLevelHC);
         hf.write("^^^^");
         hf.write('\n');
         while ((recStr = br.readLine()) != null)
         {
            recNo++;
            if (recStr.charAt(recStr.length() - 1) != '^')
            {
               iErrCode = 8; //Input File format Error
               retVal = false;
               break;
            }
            hf.write(recStr);
            rlh = hashCode(recStr, false);
            System.out.println(recStr);
            System.out.println("Record number "+recStr.substring(0, 4)+"RecType :"+recStr.substring(3, 8)+"Record level hash :"+rlh);
            fileLevelHC += (recNo * rlh);
            //				if (Parameters.ignoreRecordLevelHashing == false)
            hf.write(get20digitHashCode(rlh));
            System.out.println("Record level hash :"+get20digitHashCode(rlh));
            System.out.println( fileLevelHC);
            hf.write('\n');
         }
      }
      catch (Exception e)
      {
    	 
         e.printStackTrace();
         Log.tbaf_log.error("Exception", e);
      }
      finally
      {
         try
         {
            if (hf != null)
            {
               hf.close();
               if (retVal == false)
               {
                  File f = new File(outputFileName);
                  f.delete();
               }
            }
         }
         catch (IOException e)
         {
        	
            e.printStackTrace();
            Log.tbaf_log.error("Exception", e);
         }
      }
      if (retVal == true)
      {
         try
         {
            raf = new RandomAccessFile(outputFileName, "rw");
            //raf.seek(fhString.length() + 1 /*caret*/ + Parameters.maxHashCodeLength /*FH HC*/ + 1 + Parameters.maxVersionLength /*FVU Ver*/ + 1);
            raf.seek(fhString.length() + 3 + Parameters.maxHashCodeLength + fvuVersion.length());
            raf.writeBytes(get20digitHashCode(fileLevelHC));
         }
         catch (IOException e1)
         {
        	
            e1.printStackTrace();
            Log.tbaf_log.error("Exception", e1);
         }
         finally
         {
            if (raf != null)
            {
               try
               {
                  raf.close();
               }
               catch (IOException e2)
               {
            	  
                  e2.printStackTrace();
                  Log.tbaf_log.error("Exception", e2);
               }
            }
         }
      }
      return retVal;
   }
  
   
   
   private boolean processFVU_FileHashed()
   {
      try
      {
         recNo = 1;
         long rlh;

         if (Parameters.ignoreHashing == false && Parameters.ignoreRecordLevelHashing == false && fhHC != hashCode(fhString, true))
         {
        	Log.tbaf_log.info("File Header Has Code Taken: "+ fhHC);
        	Log.tbaf_log.info("Calculated File Header String Hash Code Now: "+ hashCode(fhString, true));
            iErrCode = 6; //File Header Hash Code mismatch
            return false;
         }

         //      utility level check added to ignore version check for SAM and SCM

         if (utilityLevel == 0 || utilityLevel == 3)
         {
            if (Parameters.ignoreVersioning == false && isValidFVUVersion(fvuVersion) == false)
            {
               iErrCode = 3; //Incorrect FVU Version
               return false;
            }
         }

         if (Parameters.ignoreHashing == true)
            return true;

         recStr = recStr.substring(0, recStr.lastIndexOf('^', recStr.lastIndexOf('^', recStr.lastIndexOf('^', recStr.lastIndexOf('^', recStr.lastIndexOf('^') - 1) - 1) - 1) - 1));
         fileLevelHC = hashCode(recStr, true);
         System.out.println("FILELEVELHC is :"+fileLevelHC);//added by puja

         if (Parameters.ignoreRecordLevelHashing == false)
         {
            while ((recStr = br.readLine()) != null)
            {
               recNo++;
               //recStr = recStr.substring(0, recStr.lastIndexOf('^'));
               if (recStr.charAt(recStr.length() - 1) == '^')
               {
                  iErrCode = 8; //Input File format Error
                  return false;
               }
               try
               {
                  rlh = Long.parseLong((recStr.substring(recStr.lastIndexOf('^') + 1, recStr.length())).trim());
                  System.out.println("RLC value is :"+rlh);//added by puja
                  recStr = recStr.substring(0, recStr.lastIndexOf('^'));
                  Log.tbaf_log.info("Record Level Hash at Record: "+ recNo +"; "+ hashCode(recStr, true));
                  if (rlh != hashCode(recStr, true))
                  {
                     iErrCode = 7;
                     //Record Level Hash Code mismatch at line: recNo
                     return false;
                  }
                  fileLevelHC += (recNo * rlh);
                  System.out.println("Final FILELEVELHC is :"+fileLevelHC);//added by puja
               }
               catch (NumberFormatException e)
               {
            	   Log.tbaf_log.error("Exception", e);
                  iErrCode = 6; //Input File format Error
                  return false;
               }
            }
         }
         else
         {
            while ((recStr = br.readLine()) != null)
            {
               recNo++;
               recStr = recStr.substring(0, recStr.lastIndexOf('^'));
               fileLevelHC += (recNo * hashCode(recStr, true));
            }
         }
         if (fileLevelHC != fvuFLH)
         {
        	 Log.tbaf_log.info("fileLevelHC Calculated: "+ fileLevelHC);
             Log.tbaf_log.info("FVU Lvl Hash Taken from File: "+ fvuFLH);
            iErrCode = 9; //Mismatch of File Level HashCode
            return false;
         }
         return true;
      }
      catch (Exception e)
      {
    	  Log.tbaf_log.error("Exception", e);
         e.printStackTrace();
      }
      return false;
   }

   public long hashCode(String recStr, boolean includeCaret)
   {
      long hc = 0;
      int i = 0;
      for (; i < recStr.length(); i++)
      {
         hc += (((int)recStr.charAt(i)) * (i + 1));
         //System.out.println("Hash value per line no :"+recStr.substring(0, 2)+"recSection :"+recStr.substring(2, 5)+"    "+recStr.charAt(i)+":"+(int) recStr.charAt(i));
         
      }
      //System.out.println("Total hash : "+hc);
      if (includeCaret == true)
         hc += ((i + 1) * ((int)'^'));
      return hc;
   }
   /**************************************************************************************************************
   		private String get10digitVersion(String version)
   		{
   			StringBuffer sb = null;
   			if (Parameters.ignoreVersioning == true)
   				sb = new StringBuffer();
   			else
   				sb = new StringBuffer(version);
   			for (int i = sb.length(); i < Parameters.maxVersionLength; i++)
   				sb.append(' ');
   			return sb.toString();
   		}
   **************************************************************************************************************/
   public String get20digitHashCode(long hashCode)
   {
      //		StringBuffer sb = null;
      //		if (Parameters.ignoreHashing == true)
      //			sb = new StringBuffer();
      //		else
      //			sb = new StringBuffer(Long.toString(hashCode));
      //
      //		for (int i = sb.length(); i < Parameters.maxHashCodeLength; i++)
      //			sb.append(' ');
      String hashCodeStr = Long.toString(hashCode);
      StringBuffer sb = new StringBuffer();
      for (int i = hashCodeStr.length(); i < Parameters.maxHashCodeLength; i++)
         sb.append('0');
      sb.append(hashCodeStr);
      return sb.toString();
   }
   private String getFileHeader()
   {
      StringBuffer sb = new StringBuffer(fhString);
      sb.append('^');
      sb.append(get20digitHashCode(hashCode(fhString, true)));
      sb.append('^');
      fvuVersion = Parameters.tbaffvuVersion[Parameters.tbaffvuVersion.length - 1];
      sb.append(fvuVersion);
      return sb.toString();
   }
   private boolean isValidFVUVersion(String fvu)
   {
      int i;
      if (fvu == null)
         return false;
      for (i = 0; i < Parameters.tbaffvuVersion.length; i++)
         if (Parameters.tbaffvuVersion[i].trim().equals(fvu.trim()))
            return true;
      return false;
   }
   private boolean isValidSAMVersion(String sam)
   {
      int i;
      if (sam == null)
         return false;
      for (i = 0; i < Parameters.tbafsamVersion.length; i++)
         if (Parameters.tbafsamVersion[i].trim().equals(sam.trim()))
            return true;
      return false;
   }
   private boolean isValidSCMVersion(String scm)
   {
      int i;
      if (scm == null)
         return false;
      for (i = 0; i < Parameters.tbafscmVersion.length; i++)
         if (Parameters.tbafscmVersion[i].trim().equals(scm.trim()))
            return true;
      return false;
   }
   private void tokenizeFileHeader() throws Exception
   {
      StringBuffer sb = null;
      try
      {
         NewStringTokenizer st = new NewStringTokenizer(recStr, "^");
         sb = new StringBuffer();
         String tokstr = st.nextToken();
         int tok = 0;
         int fhHCIndex = fhFieldCount + 1;
         int fvuVersionIndex = fhHCIndex + 1;
         int fvuFLHIndex = fvuVersionIndex + 1;
         int samVersionIndex = fvuFLHIndex + 1;
         int samFLHIndex = samVersionIndex + 1;
         int scmVersionIndex = samFLHIndex + 1;
         int scmFLHIndex = scmVersionIndex + 1;
         while (tokstr != null)
         {
            //		tokstr = tokstr.trim();
            tok++;
            if (tok < fhHCIndex)
            {
               sb.append(tokstr);
               if (tok != fhFieldCount)
                  sb.append('^');
               if (tok == 2)
               {
                  if (!tokstr.equals("FH"))
                  {
                     iErrCode = 2;
                     //throw new Exception("Invalid File");
                     break;
                  }
               }
            }
            else if (tok == fhHCIndex)
            {
               try
               {
                  fhHC = Long.parseLong(tokstr);
               }
               catch (NumberFormatException e)
               {
            	   
               }
            }
            else if (tok == fvuVersionIndex && tokstr.length() > 0)
            {
               fvuVersion = tokstr;
            }
            else if (tok == fvuFLHIndex)
            {
               try
               {
                  fvuFLH = Long.parseLong(tokstr);
               }
               catch (NumberFormatException e)
               {
            	   
               }
            }
            else if (tok == samVersionIndex && tokstr.length() > 0)
            {
               samVersion = tokstr;
            }
            else if (tok == samFLHIndex)
            {
               try
               {
                  samFLH = Long.parseLong(tokstr);
               }
               catch (NumberFormatException e)
               {
            	   
               }
            }
            else if (tok == scmVersionIndex && tokstr.length() > 0)
            {
               scmVersion = tokstr;
            }
            else if (tok == scmFLHIndex)
            {
               try
               {
                  scmFLH = Long.parseLong(tokstr);
               }
               catch (NumberFormatException e)
               {
            	   
               }
            }
            tokstr = st.nextToken();
         }
      }
      catch (Exception e)
      {
         iErrCode = 2;
         Log.tbaf_log.error("Exception", e);
         //e.printStackTrace();
         //throw new Exception("Invalid File");
      }
      finally
      {
         if (sb.length() > 0)
         {
            fhString = sb.toString();
         }
      }
   }
   public int getRecordNumber()
   {
      return recNo;
   }
}
