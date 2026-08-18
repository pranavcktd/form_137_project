/** 
 *	Class: TBAFFormatValidator.java
 */
package com.tin.etbaf.form24G.fvu;
import java.io.*;
import java.util.*;

import com.tin.etbaf.form24G.bean.BHTDCompBean;
import com.tin.etbaf.form24G.bean.RawFileBean;
import com.tin.etbaf.form24G.util.Log;

/**
 *	This class is called from the main class TBAFFVU.java and implemeents TBAFInterface.java. 
 *	This class calls all the validaton classes for File Header Record, Batch Header Record, 
 *	DDO Transaction Detail Record   
 *  
 * @author TCS
 * @version 10
 */
/**********************CLASS FOR CALLING FILE HEADER, BATCH HEADER AND TD RECORD VALIDATIONS***********************/
public class TBAFFormatValidator implements TBAFInterface
{
	FileReader fr = null;
	BufferedReader br = null;
	String errorFileName;
	RecordValidation objRecVal = new RecordValidation();
	int lineCount = 0;
	public TBAFErrorStringBuffer errStrBuff = new TBAFErrorStringBuffer();
	public StringBuffer statReportBuffer = new StringBuffer();
	HashSet hashSetTDTAN = new HashSet(); // Stroring Count of Distinct Number of TDs
	HashSet hashSetMonthYear = new HashSet(); // Storing Month & Year Of Payment
	HashSet hashSetMonthYearRevMode = new HashSet(); // Storing Month & Year Of Payment and Revision Mode
	ArrayList countValidTan = new ArrayList();
	
	HashSet hashSerialNo = new HashSet(); // Storing Serial Number
	Hashtable hashOldSerialNo = new Hashtable(); // Storing Old Serial Number
	public int zeroTDTaxAmtCounter = 0; // Count of DDO Records having Tax Amount as ZERO
	private int tdNumber = 0; // Initialize counter for DDO Record Number (Same as Serial Number)
	public String stmtType; // Statement Type
	public String uploadBy; // Uploader Type
	public String transType; // Transaction Type 
	public String finYear; // Financial Year
	public String lastFinYear; // Last Financial Year
	public String id; // AIN/Organization/TFC-ID
	public String countTD; // Count of DDO Records
	public String deductCatgry; // Deductor Category
	public String lastDeductCatgry; // Last Deductor Category
	public String quarter; // Quarter
	public String lastQuarter; // Last Quarter
	public int firstTDSrNo = 0; // Initialize Serial Number counter of First DDO Record
	public int previousTDSrNo = 0; // Initialize Serial No. of the previous DDO Record in the same file) 
	public int totalNoOfTDRead = 0; // Initialize counter for Total Number of DDO Records Read
	public int SerialCntForTDWithNMode = 0;  //To Keep track of increasing order of TD (DDO) records with N Mode
	
	//public HashMap corrTDConflictWithMdD = new HashMap(); //To keep track that a particular DDO serial Number is not more than once in D mode.
	public HashSet corrTDConflict = new HashSet(); //To keep track that a particular DDO serial Number is not more than once in N mode.
	public int totalNoOfTDWithNMode = 0; //Total number of DDO's with N Mode
	public String totalTax; // Total Tax Amount in Batch Header Record
	public double totalTaxDeleted = 0.00; // Tax Amount with Revision Mode 'D' in DDO Record
	public double totalTaxAdded = 0.00; // Tax Amount with Revision Mode 'A' in DDO Record
	
	public double totalRemittedTaxDeleted = 0.00; // Remitted Tax Amount with Revision Mode 'D' in DDO Record
	public double totalRemittedTaxAdded = 0.00; //Remitted Tax Amount with Revision Mode 'A' in DDO Record
	public double totalRemittedTaxUpdated=0.00;
	public double totalLastRemittedTaxUpdated=0.00;
	public boolean invalidTotalTax = false;
	public boolean invalidTotalRemAmt = false; //To keep track whether total Remitted amount in in batch header is valid or not
	public boolean invalidTaxAmt = false;
	public boolean invalidRemittedAmt = false;
	public boolean invalidLastRemittedAmt = false;
	public boolean inValidCaretCount = false;
	public boolean invalidRecord = false;
	public boolean errorFoundInBH = false;
	public boolean errorFoundInTD = false;
	public boolean invalidStatementType = false;
	public boolean taxDeletedIsGreater = false;
	public boolean firstTDWithNMode = false;    //To Track the first TD with N mode 
	public String transferVoucherMonth;   //TransferVoucherMonth in Batch
	public String batchMonthYear;
	public String fileCreationDate ; //This is the file Creation Date Field which has been newly added in FHRecord after File Type
	//public boolean isValid24GFile_in_yr = false;   //Added by Subhankar To check whether a file is a Valid 24G file depending on the Financial year and Month of Transfer voucher 
	//public boolean isValid24GFile_in_mon = false;
	public boolean isValid24GFile = false; //To check whether a file is a valid 24G according to the year and month as specified in the properties file
	public BHTDCompBean cBean = null;
	RawFileBean rawFileBean = RawFileBean.getInstance();
		
	/** 
	 *	readFile method is used to read each and every line(record) of the file. This method is called from 
	 *	TBAFFVU class which contains the main() method.
	 *
	 *	@param inputFileName -> Name of the input file   
	 *	@param errorFileName -> Name of the error file generated	
	 *	@param tbafUtilityLevel	-> Utility level indicator 0 implies standalone FVU
	 *	@return void
	 *  @throws Exception
	 */
	public void readFile(String inputFileName, String errorFileName, int tbafUtilityLevel) throws Exception
	{
		FileHeaderValidation objFHVal = new FileHeaderValidation();
		BatchValidation objBHVal = new BatchValidation();
		TDValidation objTDVal = new TDValidation();
		this.errorFileName = errorFileName;
		errStrBuff = new TBAFErrorStringBuffer(errorFileName);
		try
		{
			FileReader fr = new FileReader(inputFileName);
			BufferedReader br = new BufferedReader(fr);
			String record = null;
			String lineNo = "-";
			String recType = null;
			boolean bhRecordFound = false;
			while ((record = br.readLine()) != null)
			{
				//	Line count is the logical counter which will increment when each record in the file is read.
				Log.tbaf_log.info("*************  The record length is: "+record.length());
				lineCount++;
				lineNo = "-";
				recType = "-";
				//StringTokenizer objStrTokenizer = new StringTokenizer(record, TBAF_FIELD_SEPERATOR, true);
				
				
				
				/*
				 * 
				 * Testing 
				 * 
				 * 
				 */
				
				
				boolean fieldFound = false;
				int caretCount = 0;
				int fieldLocation = 0;
				
				
				StringTokenizer strToken = new StringTokenizer(record,TBAF_FIELD_SEPERATOR,true);
				while(strToken.hasMoreTokens())
				{
					String val = strToken.nextToken();
					if( (val.equals(TBAF_FIELD_SEPERATOR)) && (caretCount == 0)  )
					{
						
						caretCount ++;     
						
					}
					else if(val.equals(TBAF_FIELD_SEPERATOR))
					{

						caretCount ++;

						
					}
					else
					{
						fieldFound = true;
						if(caretCount == 0)
								fieldLocation =1;
						else
						{
							fieldLocation = caretCount + 1;
						}
					}

					if(fieldFound)
					{
						switch(fieldLocation)
						{
						case 1:
							lineNo = val;
							break;
						case 2:
							recType = val;
							break;
						
						}
					}
					
					fieldFound = false;
				}
				
			
			
				
				
				
				
				
				/*
				 * 
				 * 
				 * Ens of Testing
				 * 
				 */
				
				
				
				
				
				
				
				
				
				//New Addition as on 27 Oct 2011
			/*	if (record.trim().length() <= 6)
				{
					invalidRecord = true;
					errStrBuff.append("-" + "^" + "-" + "^" + "-" + "^^" + TBAF_FV_5004);
					break;
				}
				
				
				String strArr[] = (record.substring(0, 6)).split("\\^");
				for(int i =0; i<strArr.length;i++ )
				{
					if(i >= 2)
					{
						break;
					}
				String str1 = strArr[i];
					   switch(i)
					   {
					   case 0:
					   {
						   if(str1 != "" && (str1.trim().length() != 0) )
							   lineNo =  str1;
						   break;
					   }
					   case 1:
					   {
						   if(str1 != "" && (str1.trim().length() != 0) )
							   recType = str1;
						   break;
					   }
					   default:
						   break;
					   
					   }
					   
				   }
				   
				  //End of New Addition as on 27 Oct 2011
				

				
				*/
				
				
				
			/*	while (objStrTokenizer.hasMoreTokens())
				{
					String val = objStrTokenizer.nextToken();
					nullFieldBol = false;
					fieldFoundBol = false;
					if ((val.equals(TBAF_FIELD_SEPERATOR) && carretBol) || val.trim().length() == 0)
					{
						nullFieldBol = true;
						fieldFoundBol = true;
					}
					if (val.equals(TBAF_FIELD_SEPERATOR))
					{
						carretBol = true;
						if (caretCounter == 0 && localFieldCount == 1)
						{
							val = "-";
						}
						caretCounter++;
					}
					else
					{
						carretBol = false;
						fieldFoundBol = true;
					}
					if (localFieldCount > 2)
					{
						break;
					}
					if (fieldFoundBol)
					{
						switch (localFieldCount)
						{
							case 1 :
								lineNo = val;
								break;
							case 2 :
								recType = val;
								break;
						} // Closing SWITCH
						localFieldCount++;
					} // Closing if (fieldFoundBol)
				}  */   // Closing while (objStrTokenizer.hasMoreTokens())
				//	Check for blank lines in the '.txt' file
				
				
				Log.tbaf_log.info("The line Number is: "+lineNo+" The recType is: "+recType);
				
				
				if (record.trim().length() == 0)
				{
					invalidRecord = true;
					errStrBuff.append("-" + "^" + "-" + "^" + "-" + "^^" + TBAF_FV_5004);
					break;
				}
				else if (lineCount == 1)
				{
					//	Call FILE HEADER Validation Function
					objFHVal.fhFieldValidator(this, lineCount, record, errStrBuff);
				}
				else if (lineCount != 1 && recType.equals(TBAF_FH_REC))
				{
					invalidRecord = true;
					errStrBuff.append("-" + "^" + lineCount + "^" + "-" + "^^" + TBAF_FV_5001);
					break;
				}
				else if (lineCount == 2)
				{
					cBean = new BHTDCompBean();
					bhRecordFound = true;
					//	Call BATCH HEADER Validation Function
					objBHVal.bhFieldValidator(this,cBean, lineCount, record, errStrBuff);
					
					if(! isValid24GFile)       //Added By Subhankar....
					{
						//errStrBuff.append("-" + "^" + lineCount + "^" + "-" + "^^" + TBAF_FV_5005);
						
						break;
					}
					if (errorFoundInBH)
					{
						
						break;
					}
					batchMonthYear = transferVoucherMonth + finYear;
				}
				else if (lineCount != 2 && recType.equals(TBAF_BH_REC))
				{
					invalidRecord = true;
					errStrBuff.append("-" + "^" + lineCount + "^" + "-" + "^^" + TBAF_FV_5002);
					break;
				}
				else if (recType.equals(TBAF_TD_REC))
				{
					
					// For C1 Correction, no TD records should be specified
					if (transType.equals(TBAF_TRANSACTION_TYPE_X))
					{
						invalidRecord = true;
						errStrBuff.append("-" + "^" + lineCount + "^" + "-" + "^^" + TBAF_FV_2055);
						
					}
					
					else
					{
					 totalNoOfTDRead++;
					 
					 //In case of transaction of Type M there can be no TD records present
					 if( (this.stmtType.equals(TBAF_TYPE_OF_STMT_CORR)) && (this.transType.equals(TBAF_TRANSACTION_TYPE_M)) && (Integer.parseInt(countTD) == 0) && (totalNoOfTDRead == 1) )
					 {
						 invalidRecord = true;
						 errStrBuff.append("-" + "^" + lineCount + "^" + "-" + "^^" + TBAF_FV_2088);
						 break;
					 }
					 //	Call TRANSACTION DETAIL Validation Function
					 objTDVal.tdFieldValidator(this,cBean, lineCount, record, errStrBuff);
					 tdNumber++;
					}		
				}
				
				//Commented on 27 Oct 2011 (FH,BH, AND TD CAN ONLY BE THERE IN THE FILE NO ONE ELSE)
				
				else if (!recType.equals(TBAF_TD_REC))
				{
					totalNoOfTDRead++;
					if (record.trim().length() == 0)
					{
						invalidRecord = true;
						errStrBuff.append("-" + "^" + "-" + "^" + "-" + "^^" + TBAF_FV_5004);
						break;
					}
					else
					{
						objTDVal.tdFieldValidator(this,cBean, lineCount, record, errStrBuff);
						tdNumber++;
					}
				} 
				
				
				
				else
				{
					invalidRecord = true;
					errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[2] + "^^" + TBAF_FV_5003);
				}
				
				
			} // Closing while ((record = br.readLine()) != null)
			
			
			
			/**
			 * Validations of some field as Added by Subhankar
			 * 
			 * 
			 * 
			 */
		
			
		
	 if(isValid24GFile && ! errorFoundInBH && ! invalidRecord)
	 {
		 
		 
		
		 //Validation for File Creation Date should be always greater than or equal to batch Date
		 String temptransferVoucherMonth;
		 if(Integer.parseInt(transferVoucherMonth) >= 1 && Integer.parseInt(transferVoucherMonth) <= 9)
		 {
			 temptransferVoucherMonth = "0"+Integer.parseInt(transferVoucherMonth);
		 }
		 else
		 {
			 temptransferVoucherMonth = transferVoucherMonth;
		 }
		 String batchDateMonthYear="01"+temptransferVoucherMonth+finYear;
		 Calendar batchDate = new GregorianCalendar(Integer.parseInt(batchDateMonthYear.substring(4, 8)), Integer.parseInt(batchDateMonthYear.substring(2, 4)) - 1, Integer.parseInt(batchDateMonthYear.substring(0, 2)));
		 Calendar fileDate = new GregorianCalendar(Integer.parseInt(fileCreationDate.substring(4, 8)), Integer.parseInt(fileCreationDate.substring(2, 4)) - 1, Integer.parseInt(fileCreationDate.substring(0, 2)));
		 if(batchDate.after(fileDate))
		 {
			 errStrBuff.append(TBAF_BHREC + "1" + "^" + "-" + "^^" + TBAF_FV_5030);
		 }
			
		 //End of Validation for File Creation Date should be always greater than or equal to batch Date
		 
		
			//For Form 24Q
			int cTD24Q = 0;
			try{
				cTD24Q = Integer.parseInt(cBean.getCountTD24Q());
			}
			catch(Exception e)
			{
				Log.tbaf_log.error("Exception", e);
				cTD24Q = 0;
			}
			
			double tTax24Q = 0.00;
			try{
				tTax24Q = Double.parseDouble(cBean.getTotalTax24Q());
			}
			catch(Exception e)
			{
				Log.tbaf_log.error("Exception", e);
				tTax24Q = 0.00;
			}
			
			
			double tRemit24Q = 0.00;
			try{
				 tRemit24Q = Double.parseDouble(cBean.getTotalRemittedAmt24Q());
			}
			catch(Exception e)
			{
				Log.tbaf_log.error("Exception", e);
				tRemit24Q = 0.00;
			}
			
			if(stmtType.equals(TBAF_TYPE_OF_STMT_ORIG) || (stmtType.equals(TBAF_TYPE_OF_STMT_CORR) && transType.equals(TBAF_TRANSACTION_TYPE_M) ))
			{
				
			
				if(cTD24Q != cBean.getCountNatOfDed24Q())
				{
					errStrBuff.append(TBAF_BHREC + "2" + "^" + TBAF_BH_FIELD[51] + "^^" + TBAF_FV_5006);
				}
				else if(cTD24Q == 0 && cBean.getCountNatOfDed24Q() ==0 && cBean.getCountLastNatOfDed24Q()==0)
					{
					
					   if (cBean.getTotalTaxTD24Q() != 0.00 || tTax24Q != 0.00 || cBean.getTotalRemittanceTD24Q() != 0.00 || tRemit24Q !=0.00)
					   {
						   errStrBuff.append(TBAF_BHREC + "2" + "^" + "-" + "^^" + TBAF_FV_5007);	    
					   }
					}
				else if(stmtType.equals(TBAF_TYPE_OF_STMT_ORIG) && (tTax24Q != cBean.getTotalTaxTD24Q()))
				{
					errStrBuff.append(TBAF_BHREC + "2" + "^" + TBAF_BH_FIELD[52] + "^^" + TBAF_FV_5008);
				}
				else if(stmtType.equals(TBAF_TYPE_OF_STMT_ORIG) && (tRemit24Q != cBean.getTotalRemittanceTD24Q()))
				{
					errStrBuff.append(TBAF_BHREC + "2" + "^" + TBAF_BH_FIELD[53] + "^^" + TBAF_FV_5009);
				}
				
				
				else if(stmtType.equals(TBAF_TYPE_OF_STMT_CORR) && (tTax24Q != (cBean.getTotalTaxAddedTD24Q()+cBean.getTotalTaxDeletedTD24Q()+cBean.getTotalTaxUpdatedTD24Q())) )
						
				{

					errStrBuff.append(TBAF_BHREC + "2" + "^" + TBAF_BH_FIELD[52] + "^^" + TBAF_FV_5032);
				}
				else if(stmtType.equals(TBAF_TYPE_OF_STMT_CORR) && (tRemit24Q != (cBean.getRemittedAmtAddedTD24Q()-cBean.getRemittedAmtDeletedTD24Q()+cBean.getRemittedAmtUpdatedTD24Q())) )
					
				{
					errStrBuff.append(TBAF_BHREC + "2" + "^" + TBAF_BH_FIELD[53] + "^^" + TBAF_FV_5033);
				}
				
				
			}
			
			//End For Form 24Q
			
			
			//For Form 26Q
			
			
			int cTD26Q = 0;
			try{
				cTD26Q = Integer.parseInt(cBean.getCountTD26Q());
			}
			catch(Exception e)
			{
				Log.tbaf_log.error("Exception", e);
				cTD26Q = 0;
			}
			
			
			double tTax26Q = 0.00;
			try{
				tTax26Q = Double.parseDouble(cBean.getTotalTax26Q());
			}
			catch(Exception e)
			{
				Log.tbaf_log.error("Exception", e);
				tTax26Q = 0.00;
			}
		
			double tRemit26Q = 0.00;
			try{
				 tRemit26Q = Double.parseDouble(cBean.getTotalRemittedAmt26Q());
			}
			catch(Exception e)
			{
				Log.tbaf_log.error("Exception", e);
				tRemit26Q = 0.00;
			}
			
			
			
			if(stmtType.equals(TBAF_TYPE_OF_STMT_ORIG) || (stmtType.equals(TBAF_TYPE_OF_STMT_CORR) && transType.equals(TBAF_TRANSACTION_TYPE_M) ))
			{
				if(cTD26Q != cBean.getCountNatOfDed26Q())
				{
					errStrBuff.append(TBAF_BHREC + "2" + "^" + TBAF_BH_FIELD[54] + "^^" + TBAF_FV_5010);
				}
				else if(cTD26Q == 0 && cBean.getCountNatOfDed26Q() ==0 && cBean.getCountLastNatOfDed26Q()==0)
					{
					   if (cBean.getTotalTaxTD26Q() != 0.00 || tTax26Q != 0.00 || cBean.getTotalRemittanceTD26Q() != 0.00 || tRemit26Q !=0.00)
					   {
						   errStrBuff.append(TBAF_BHREC + "2" + "^" + "-" + "^^" + TBAF_FV_5011);	    
					   }
					}
				else if(stmtType.equals(TBAF_TYPE_OF_STMT_ORIG) && (tTax26Q != cBean.getTotalTaxTD26Q()))
				{
					errStrBuff.append(TBAF_BHREC + "2" + "^" + TBAF_BH_FIELD[55] + "^^" + TBAF_FV_5012);
				}
				else if(stmtType.equals(TBAF_TYPE_OF_STMT_ORIG) && (tRemit26Q != cBean.getTotalRemittanceTD26Q()))
				{
					
					errStrBuff.append(TBAF_BHREC + "2" + "^" + TBAF_BH_FIELD[56] + "^^" + TBAF_FV_5013);
				}
				
				else if(stmtType.equals(TBAF_TYPE_OF_STMT_CORR) && (tTax26Q != (cBean.getTotalTaxAddedTD26Q()+cBean.getTotalTaxDeletedTD26Q()+cBean.getTotalTaxUpdatedTD26Q())) )
					
				{
					errStrBuff.append(TBAF_BHREC + "2" + "^" + TBAF_BH_FIELD[55] + "^^" + TBAF_FV_5034);
				}
				else if(stmtType.equals(TBAF_TYPE_OF_STMT_CORR) && (tRemit26Q != (cBean.getRemittedAmtAddedTD26Q()-cBean.getRemittedAmtDeletedTD26Q()+cBean.getRemittedAmtUpdatedTD26Q())) )
					
				{
					errStrBuff.append(TBAF_BHREC + "2" + "^" + TBAF_BH_FIELD[56] + "^^" + TBAF_FV_5035);
				}
				
				
			}
			
			
			//End For Form 26Q
			
			
//For Form 27Q
			
			int cTD27Q = 0;
			try{
				cTD27Q = Integer.parseInt(cBean.getCountTD27Q());
			}
			catch(Exception e)
			{
				Log.tbaf_log.error("Exception", e);
				cTD27Q = 0;
			}
			
			
			double tTax27Q = 0.00;
			try{
				tTax27Q = Double.parseDouble(cBean.getTotalTax27Q());
			}
			catch(Exception e)
			{
				Log.tbaf_log.error("Exception", e);
				tTax27Q = 0.00;
			}
		
			double tRemit27Q = 0.00;
			try{
				 tRemit27Q = Double.parseDouble(cBean.getTotalRemittedAmt27Q());
			}
			catch(Exception e)
			{
				Log.tbaf_log.error("Exception", e);
				tRemit27Q = 0.00;
			}
			
			
			
			
			if(stmtType.equals(TBAF_TYPE_OF_STMT_ORIG) || (stmtType.equals(TBAF_TYPE_OF_STMT_CORR) && transType.equals(TBAF_TRANSACTION_TYPE_M) ))
			{
				if(cTD27Q != cBean.getCountNatOfDed27Q())
				{
					errStrBuff.append(TBAF_BHREC + "2" + "^" + TBAF_BH_FIELD[57] + "^^" + TBAF_FV_5014);
				}
				else if(cTD27Q == 0 && cBean.getCountNatOfDed27Q() ==0 && cBean.getCountLastNatOfDed27Q()==0)
					{
					   if (cBean.getTotalTaxTD27Q() != 0.00 || tTax27Q != 0.00 || cBean.getTotalRemittanceTD27Q() != 0.00 || tRemit27Q !=0.00)
					   {
						   errStrBuff.append(TBAF_BHREC + "2" + "^" + "-" + "^^" + TBAF_FV_5015);	    
					   }
					}
				else if(stmtType.equals(TBAF_TYPE_OF_STMT_ORIG) && (tTax27Q != cBean.getTotalTaxTD27Q()))
				{
					errStrBuff.append(TBAF_BHREC + "2" + "^" + TBAF_BH_FIELD[58] + "^^" + TBAF_FV_5016);
				}
				else if(stmtType.equals(TBAF_TYPE_OF_STMT_ORIG) && (tRemit27Q != cBean.getTotalRemittanceTD27Q()))
				{
					
					errStrBuff.append(TBAF_BHREC + "2" + "^" + TBAF_BH_FIELD[59] + "^^" + TBAF_FV_5017);
				}
				
				else if(stmtType.equals(TBAF_TYPE_OF_STMT_CORR) && (tTax27Q != (cBean.getTotalTaxAddedTD27Q()+cBean.getTotalTaxDeletedTD27Q()+cBean.getTotalTaxUpdatedTD27Q())) )
				{
					
					errStrBuff.append(TBAF_BHREC + "2" + "^" + TBAF_BH_FIELD[58] + "^^" + TBAF_FV_5036);
				}
				else if(stmtType.equals(TBAF_TYPE_OF_STMT_CORR) && (tRemit27Q != (cBean.getRemittedAmtAddedTD27Q()-cBean.getRemittedAmtDeletedTD27Q()+cBean.getRemittedAmtUpdatedTD27Q())) )
					
				{
					errStrBuff.append(TBAF_BHREC + "2" + "^" + TBAF_BH_FIELD[59] + "^^" + TBAF_FV_5037);
				}
				
			}
			
			
			//End For Form 27Q
			
			
//For Form 27EQ
			
			
			
			int cTD27EQ = 0;
			try{
				cTD27EQ = Integer.parseInt(cBean.getCountTD27EQ());
			}
			catch(Exception e)
			{
				Log.tbaf_log.error("Exception", e);
				cTD27EQ = 0;
			}
			
			
			double tTax27EQ = 0.00;
			try{
				tTax27EQ = Double.parseDouble(cBean.getTotalTax27EQ());
			}
			catch(Exception e)
			{
				Log.tbaf_log.error("Exception", e);
				tTax27EQ = 0.00;
			}
		
			double tRemit27EQ = 0.00;
			try{
				 tRemit27EQ = Double.parseDouble(cBean.getTotalRemittedAmt27EQ());
			}
			catch(Exception e)
			{
				Log.tbaf_log.error("Exception", e);
				tRemit27EQ = 0.00;
			}
			
			
			if(stmtType.equals(TBAF_TYPE_OF_STMT_ORIG) || (stmtType.equals(TBAF_TYPE_OF_STMT_CORR) && transType.equals(TBAF_TRANSACTION_TYPE_M) ))
			{
				if(cTD27EQ != cBean.getCountNatOfDed27EQ())
				{
					errStrBuff.append(TBAF_BHREC + "2" + "^" + TBAF_BH_FIELD[60] + "^^" + TBAF_FV_5018);
				}
				else if((cTD27EQ == 0 && cBean.getCountNatOfDed27EQ() ==0 && cBean.getCountLastNatOfDed27EQ()==0))
					{
					   if (cBean.getTotalTaxTD27EQ() != 0.00 || tTax27EQ != 0.00 || cBean.getTotalRemittanceTD27EQ() != 0.00 || tRemit27EQ !=0.00)
					   {
						   errStrBuff.append(TBAF_BHREC + "2" + "^" + "-" + "^^" + TBAF_FV_5019);	    
					   }
					}
				else if(stmtType.equals(TBAF_TYPE_OF_STMT_ORIG) && (tTax27EQ != cBean.getTotalTaxTD27EQ()))
				{
					errStrBuff.append(TBAF_BHREC + "2" + "^" + TBAF_BH_FIELD[61] + "^^" + TBAF_FV_5020);
				}
				else if(stmtType.equals(TBAF_TYPE_OF_STMT_ORIG) && (tRemit27EQ != cBean.getTotalRemittanceTD27EQ()))
				{
					errStrBuff.append(TBAF_BHREC + "2" + "^" + TBAF_BH_FIELD[62] + "^^" + TBAF_FV_5021);
				}
				else if(stmtType.equals(TBAF_TYPE_OF_STMT_CORR) && (tTax27EQ != (cBean.getTotalTaxAddedTD27EQ()+cBean.getTotalTaxDeletedTD27EQ()+cBean.getTotalTaxUpdatedTD27EQ())) )
					
				{
					errStrBuff.append(TBAF_BHREC + "2" + "^" + TBAF_BH_FIELD[61] + "^^" + TBAF_FV_5038);
				}
				else if(stmtType.equals(TBAF_TYPE_OF_STMT_CORR) && (tRemit27EQ != (cBean.getRemittedAmtAddedTD27EQ()-cBean.getRemittedAmtDeletedTD27EQ()+cBean.getRemittedAmtUpdatedTD27EQ())) )
					
				{
					errStrBuff.append(TBAF_BHREC + "2" + "^" + TBAF_BH_FIELD[62] + "^^" + TBAF_FV_5039);
				}
			}
			
			
			//End For Form 27EQ
			
			
			//Validation of Distinct DDO's will be done for Original and M type Correction
			
			if(stmtType.equals(TBAF_TYPE_OF_STMT_ORIG) || (stmtType.equals(TBAF_TYPE_OF_STMT_CORR) && transType.equals(TBAF_TRANSACTION_TYPE_M) ))
			{	
			
			int dDDOCount = 0;
			try{
				dDDOCount = Integer.parseInt(cBean.getDistinctDDOCount());
			}
			catch(Exception e)
			{
				Log.tbaf_log.error("Exception", e);
				dDDOCount = 0;
			}
			
			if(dDDOCount != hashSetTDTAN.size())
			{
				errStrBuff.append(TBAF_BHREC + "2" + "^" + TBAF_BH_FIELD[64] + "^^" + TBAF_FV_5022);
			}
			}
			//end of Distinct DDO's Validation
			
			
			//Validation of Remitted Amt IS to be done Only for regulars as for Correction of type M Added and Deleted TD's amount is required to be taken into account
			
		if(stmtType.equals(TBAF_TYPE_OF_STMT_ORIG))
		{
			double tRemittedAmt = 0.00;
			try{
				
				tRemittedAmt = Double.parseDouble(cBean.getTotalRemittedAmt());
			}
			catch(Exception e)
			{
				Log.tbaf_log.error("Exception", e);
				tRemittedAmt = 0.00;
			}
			
			
			
			if(tRemittedAmt != totalRemittedTaxAdded)
			{
				errStrBuff.append(TBAF_BHREC + "2" + "^" + TBAF_BH_FIELD[65] + "^^" + TBAF_FV_5023);
			}
		}	
			
			//End of validation Remitted Amt
			
		//VALIDATION OF TOTAL TAX
		
		
		double totalTaxAmt = 0.00;
		try{
			
			totalTaxAmt = Double.parseDouble(this.totalTax);
		}
		catch(Exception e)
		{
			Log.tbaf_log.error("Exception", e);
			totalTaxAmt = 0.00;
		}
		
		
		
		if(totalTaxAmt != totalTaxAdded)
		{
			errStrBuff.append(TBAF_BHREC + "2" + "^" + TBAF_BH_FIELD[29] + "^^" + TBAF_FV_5031);
		}
		
		
		
		
		
		
		// End of Total TAX verification
		
		
		
		//Validation of DDO Added,Updated or Deleted can only be done for Regular and M type Correction not for X type as in X type there is no TD 
		
		
			//Validation of DDO added
			
		
		if(stmtType.equals(TBAF_TYPE_OF_STMT_ORIG) || (stmtType.equals(TBAF_TYPE_OF_STMT_CORR) && transType.equals(TBAF_TRANSACTION_TYPE_M) ))
		{
			int cDDOAdd = 0;
			try{
				cDDOAdd = Integer.parseInt(cBean.getCountDDOAdd());
			}
			catch(Exception e)
			{
				Log.tbaf_log.error("Exception", e);
				cDDOAdd = 0;
			}
			
			
			if(cDDOAdd != cBean.getCountDDOTDAdd())
			{
				errStrBuff.append(TBAF_BHREC + "2" + "^" + TBAF_BH_FIELD[66] + "^^" + TBAF_FV_5024);
			}
			
			if((cBean.getCountDDOTDAdd()+cBean.getCountDDOTDUpdated()) == 0)
			{
				if(cDDOAdd !=0)
				{
					errStrBuff.append(TBAF_BHREC + "2" + "^" + TBAF_BH_FIELD[66] + "^^"  + TBAF_FV_5027);
				}
			}
		}	
			//End of DDO Added
			
//Validation of DDO updated
			
		if(stmtType.equals(TBAF_TYPE_OF_STMT_ORIG) || (stmtType.equals(TBAF_TYPE_OF_STMT_CORR) && transType.equals(TBAF_TRANSACTION_TYPE_M) ))
		{
			int cDDOUpdated = 0;
			try{
				cDDOUpdated = Integer.parseInt(cBean.getCountDDOUpdated());
			}
			catch(Exception e)
			{
				Log.tbaf_log.error("Exception", e);
				cDDOUpdated = 0;
			}
			
			
			if(cDDOUpdated != cBean.getCountDDOTDUpdated())
			{
				errStrBuff.append(TBAF_BHREC + "2" + "^" + TBAF_BH_FIELD[67] + "^^" + TBAF_FV_5025);
			}
			
			if(cBean.getCountDDOTDUpdated() == 0)
			{
				if(cDDOUpdated !=0)
				{
					errStrBuff.append(TBAF_BHREC + "2" + "^" + TBAF_BH_FIELD[67] + "^^" + TBAF_FV_5028);
				}
			}
		}
			//End of DDO Updated
		
			
//Validation of DDO deleted
		
		if(stmtType.equals(TBAF_TYPE_OF_STMT_ORIG) || (stmtType.equals(TBAF_TYPE_OF_STMT_CORR) && transType.equals(TBAF_TRANSACTION_TYPE_M) ))
		{
			int cDDODeleted = 0;
			try{
				cDDODeleted = Integer.parseInt(cBean.getCountDDODeleted());
			}
			catch(Exception e)
			{
				Log.tbaf_log.error("Exception", e);
				cDDODeleted = 0;
			}
			
			
			
			
			if(cDDODeleted != cBean.getCountDDOTDDeleted())
			{
				errStrBuff.append(TBAF_BHREC + "2" + "^" + TBAF_BH_FIELD[68] + "^^" + TBAF_FV_5026);
			}
			
			if(cBean.getCountDDOTDDeleted() == 0)
			{
				if(cDDODeleted !=0)
				{
					errStrBuff.append(TBAF_BHREC + "2" + "^" + TBAF_BH_FIELD[68] + "^^" + TBAF_FV_5029);
				}
			}
		}
			//End of DDO Deleted
			rawFileBean.setValue(String.valueOf(countValidTan.size()+cBean.getCountTANAPPLIED()+cBean.getCountTANNOTAVBL()+cBean.getCountTANINVALID()+2),RawFileBean.TOTAL_NO_OF_RECORDS_FLDNUM); //Added by Bharath for Raw File Generation
			rawFileBean.setValue(String.valueOf(countValidTan.size()),RawFileBean.COUNT_OF_VALID_TAN_FLDNUM); //Added by Bharath for Raw File Generation
			rawFileBean.setValue(String.valueOf(cBean.getCountTANAPPLIED()),RawFileBean.COUNT_OF_TANAPPLIED_FLDNUM); //Added by Bharath for Raw File Generation
			rawFileBean.setValue(String.valueOf(cBean.getCountTANNOTAVBL()),RawFileBean.COUNT_OF_TANNOTAVBL_FLDNUM); //Added by Bharath for Raw File Generation
			rawFileBean.setValue(String.valueOf(cBean.getCountTANINVALID()),RawFileBean.COUNT_OF_TANINVALID_FLDNUM); //Added by Bharath for Raw File Generation
			
			int noOfDDORecords=Integer.parseInt(cBean.getCountTD24Q())+Integer.parseInt(cBean.getCountTD26Q())+Integer.parseInt(cBean.getCountTD27Q())+Integer.parseInt(cBean.getCountTD27EQ()); //Added by Bharath for Raw File Generation
			rawFileBean.setValue(String.valueOf(noOfDDORecords),RawFileBean.COUNT_OF_DDO_RECORDS_FLDNUM); //Added by Bharath for Raw File Generation
			
			//Addition of data in Raw File Buffer for For Form24G FVU correction Type
			
			if(stmtType.equals(TBAF_TYPE_OF_STMT_CORR))
			{
			rawFileBean.setValue(String.valueOf(cBean.getTotalTDAddedIn24Q()),RawFileBean.COUNT_OF_DDO_RECORDS_ADDED_24Q);
			rawFileBean.setValue(String.valueOf(cBean.getTotalTDDeletedIn24Q()),RawFileBean.COUNT_OF_DDO_RECORDS_DELETED_24Q);
			rawFileBean.setValue(String.valueOf(cBean.getTotalTDUpdatedIn24Q()),RawFileBean.Count_OF_DDO_RECORDS_UPDATED_24Q);
			rawFileBean.setValue(String.valueOf(cBean.getRemittedAmtAddedTD24Q()+cBean.getRemittedAmtUpdatedTD24Q()-cBean.getRemittedAmtDeletedTD24Q()),RawFileBean.TOTAL_AMOUNT_OF_ADDED_TAX_REMITTED_TO_GOVT_24Q);
					
			
			rawFileBean.setValue(String.valueOf(cBean.getTotalTDAddedIn26Q()),RawFileBean.COUNT_OF_DDO_RECORDS_ADDED_26Q);
			rawFileBean.setValue(String.valueOf(cBean.getTotalTDDeletedIn26Q()),RawFileBean.COUNT_OF_DDO_RECORDS_DELETED_26Q);
			rawFileBean.setValue(String.valueOf(cBean.getTotalTDUpdatedIn26Q()),RawFileBean.Count_OF_DDO_RECORDS_UPDATED_26Q);
			rawFileBean.setValue(String.valueOf(cBean.getRemittedAmtAddedTD26Q()+cBean.getRemittedAmtUpdatedTD26Q()-cBean.getRemittedAmtDeletedTD26Q()),RawFileBean.TOTAL_AMOUNT_OF_ADDED_TAX_REMITTED_TO_GOVT_26Q);
			
			
			
			rawFileBean.setValue(String.valueOf(cBean.getTotalTDAddedIn27Q()),RawFileBean.COUNT_OF_DDO_RECORDS_ADDED_27Q);
			rawFileBean.setValue(String.valueOf(cBean.getTotalTDDeletedIn27Q()),RawFileBean.COUNT_OF_DDO_RECORDS_DELETED_27Q);
			rawFileBean.setValue(String.valueOf(cBean.getTotalTDUpdatedIn27Q()),RawFileBean.Count_OF_DDO_RECORDS_UPDATED_27Q);
			rawFileBean.setValue(String.valueOf(cBean.getRemittedAmtAddedTD27Q()+cBean.getRemittedAmtUpdatedTD27Q()-cBean.getRemittedAmtDeletedTD27Q()),RawFileBean.TOTAL_AMOUNT_OF_ADDED_TAX_REMITTED_TO_GOVT_27Q);
			
			
			
			rawFileBean.setValue(String.valueOf(cBean.getTotalTDAddedIn27EQ()),RawFileBean.COUNT_OF_DDO_RECORDS_ADDED_27EQ);
			rawFileBean.setValue(String.valueOf(cBean.getTotalTDDeletedIn27EQ()),RawFileBean.COUNT_OF_DDO_RECORDS_DELETED_27EQ);
			rawFileBean.setValue(String.valueOf(cBean.getTotalTDUpdatedIn27EQ()),RawFileBean.Count_OF_DDO_RECORDS_UPDATED_27EQ);
			rawFileBean.setValue(String.valueOf(cBean.getRemittedAmtAddedTD27EQ()+cBean.getRemittedAmtUpdatedTD27EQ()-cBean.getRemittedAmtDeletedTD27EQ()),RawFileBean.TOTAL_AMOUNT_OF_ADDED_TAX_REMITTED_TO_GOVT_27EQ);
			
			double totalRemittedAmtToGovt = totalRemittedTaxAdded - totalRemittedTaxDeleted +totalRemittedTaxUpdated-totalLastRemittedTaxUpdated;
			rawFileBean.setValue(String.valueOf(totalRemittedAmtToGovt),RawFileBean.TOTAL_AMOUNT_OF_TAX_REMITTED_TO_GOVT);
			
		    //End of Addition			
					
			}
			if(stmtType.equals(TBAF_TYPE_OF_STMT_CORR) || stmtType.equals(TBAF_TYPE_OF_STMT_ORIG))
			{
			statReportBuffer.append(String.valueOf(countValidTan.size())+TBAF_FIELD_SEPERATOR);
			statReportBuffer.append(cBean.getCountTANAPPLIED()+TBAF_FIELD_SEPERATOR);
			statReportBuffer.append(cBean.getCountTANNOTAVBL()+TBAF_FIELD_SEPERATOR);
			statReportBuffer.append(cBean.getCountTANINVALID()+TBAF_FIELD_SEPERATOR);
			statReportBuffer.append(cBean.getTdRecordZeroTaxExD()+TBAF_FIELD_SEPERATOR);
			
			}
			
			//End of Validations Added By Subhankar
	 }		
		   /** 
			* ADDING Data in Statistics String Buffer
			* 
			* 1. Adding Number of Distinct TAN (Count of Distinct DDO TANs).
			* 2. Number of TDs with Tax Amt 0.00.
			* 3. Number of Lines in the File (Number of records to be charged).
			*/
			if (zeroTDTaxAmtCounter == 0)
			{
				statReportBuffer.append(hashSetTDTAN.size() + TBAF_FIELD_SEPERATOR + " " 
										+ TBAF_FIELD_SEPERATOR + lineCount);
			}
			else
			{
				statReportBuffer.append(hashSetTDTAN.size() + TBAF_FIELD_SEPERATOR + zeroTDTaxAmtCounter 
										+ TBAF_FIELD_SEPERATOR + lineCount);
			}
			// Checking Serial No. and Old Serial No. are not equal.
			int oldSerialNumSize = hashOldSerialNo.size();
			for (int i = 0; i <= oldSerialNumSize; i++)
			{
				if (hashSerialNo.contains(hashOldSerialNo.get(new Integer(i))))
				{
					errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[6] + "^" + tdNumber 
									  + TBAF_ERR_SEP + TBAF_FV_3013);
				}
			}
			/** 
			 *  In case of Correction statement, if the tax amount in "DEL" mode is greater than tax amount 
			 *  in "ADD" mode the Total tax specified must be "Negative".In all the other cases the 
			 *  validations done are same as in Regular. 
			 */
			
			
			/**
			 * 
			 * 
			 * Hashed as Negative amount in both Total Tax field and Total Remitted amount field for Correction of M type is allowed
			 * 
			 */
		/*	if (stmtType.equals(TBAF_TYPE_OF_STMT_CORR) 
				&& !transType.equals(TBAF_TRANSACTION_TYPE_C4)
				&& !transType.equals(TBAF_TRANSACTION_TYPE_C1)
				&& !transType.equals(TBAF_TRANSACTION_TYPE_X))
			{
				if (taxDeletedIsGreater == true)
				{
					if (objRecVal.isNegativeDecimalNumber(totalTax) || !totalTax.endsWith("00"))
					{
						if (totalTax.substring(0, 1).equals("-") 
						    && objRecVal.isInt(totalTax.substring(1, totalTax.length())) == false)
						{
							invalidTotalTax = true;
							errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[29] + "^^" + TBAF_FV_2046);
							errorFoundInBH = true;
						}
						else
						{
							invalidTotalTax = true;
							errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[29] + "^^" + TBAF_FV_2031);
							errorFoundInBH = true;
						}
					}
					else
					{
						statReportBuffer.append(totalTax + TBAF_FIELD_SEPERATOR);
					}
				}
				else if (objRecVal.isDecimalNumber(totalTax) || !totalTax.endsWith("00"))
				{
					if (totalTax.substring(0, 1).equals("-") 
						&& objRecVal.isInt(totalTax.substring(1, totalTax.length())) == false)
					{
						invalidTotalTax = true;
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[29] + "^^" + TBAF_FV_2046);
						errorFoundInBH = true;
					}
					else if (objRecVal.isInt(totalTax) == false)
					{
						invalidTotalTax = true;
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[29] + "^^" + TBAF_FV_2046);
						errorFoundInBH = true;
					}
					else
					{
						invalidTotalTax = true;
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[29] + "^^" + TBAF_FV_2031);
						errorFoundInBH = true;
					}
				}
				else
				{
					statReportBuffer.append(totalTax + TBAF_FIELD_SEPERATOR);
				}
			} */
			
			if (invalidRecord == false)
			{
				
			//	Commented on Oct 29 as for X correction there will be no TD so no validation is required fro X transaction Type. 
		//		if (errorFoundInBH == false && inValidCaretCount == false && bhRecordFound 
			//		&& !transType.equals(TBAF_TRANSACTION_TYPE_C1))
				
				if (errorFoundInBH == false && inValidCaretCount == false && bhRecordFound &&  (   stmtType.equals(TBAF_TYPE_OF_STMT_ORIG)
								|| stmtType.equals(TBAF_TYPE_OF_STMT_CORR) ) ) 
				
				{
					//	Checking the Number of TD Records is less than count of DDO Records specified in Batch. 	 
					if (totalNoOfTDRead < Integer.parseInt(countTD.trim()) && isValid24GFile)  //Added By Subhankar
					{
						errStrBuff.append(TBAF_BHREC + "2" + "^" + TBAF_BH_FIELD[28] + "^^" + TBAF_FV_2052);
					}
					//	Checking the Number of TD Records is more than count of DDO Records specified in Batch.
					else if (totalNoOfTDRead > Integer.parseInt(countTD.trim()) && isValid24GFile)   //Added By Subhankar
					{
						errStrBuff.append(TBAF_BHREC + "2" + "^" + TBAF_BH_FIELD[28] + "^^" + TBAF_FV_2053);
					}
					else if (totalNoOfTDRead == Integer.parseInt(countTD.trim()) && isValid24GFile)
					{
						// Checking the sum of Remitted amounts in DDO Records are equal to the Total Remitted Amount in Batch for Correction File.
						if(stmtType.equals(TBAF_TYPE_OF_STMT_CORR) && transType.equals(TBAF_TRANSACTION_TYPE_M))
						{
						if (invalidRemittedAmt == false && invalidTotalRemAmt == false && invalidStatementType == false)
						{
							double tRemAmt = 0.00;
							try{
								tRemAmt = Double.parseDouble(cBean.getTotalRemittedAmt());
							}
							catch(Exception e)
							{
								Log.tbaf_log.error("Exception", e);
								tRemAmt = 0.00;
							}
							if (tRemAmt != (totalRemittedTaxAdded - totalRemittedTaxDeleted+(totalRemittedTaxUpdated-totalLastRemittedTaxUpdated)))
							{
								errStrBuff.append(TBAF_BHREC + "2" + "^" + TBAF_BH_FIELD[29] + "^^" + TBAF_FV_2054);
							}
						}
						}
					}
				}
			} // end of if (invalidRecord == false)
			br.close();
			fr.close();
		} // end of try block
		catch (FileNotFoundException e)
		{
			Log.tbaf_log.error("Exception", e);
			e.printStackTrace();
		}
		catch (IOException e)
		{
			Log.tbaf_log.error("Exception", e);
			e.printStackTrace();
		}
		catch (Exception e)
		{
			Log.tbaf_log.error("Exception", e);
			e.printStackTrace();
		}
	}	// end of readFile() method
	public StringBuffer getStatReportBuffer()
	{
		return statReportBuffer;
	}
	public void setStatReportBuffer(StringBuffer buffer)
	{
		statReportBuffer = buffer;
	}
	
	
} // End of TBAFFormatValidator Class
