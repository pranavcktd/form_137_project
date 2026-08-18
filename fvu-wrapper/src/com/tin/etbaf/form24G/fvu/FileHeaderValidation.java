/**	
 * Class: FileHeaderValidation.java
 */
package com.tin.etbaf.form24G.fvu;
import java.util.*;

import com.tin.etbaf.form24G.bean.BHTDCompBean;
import com.tin.etbaf.form24G.bean.RawFileBean;
import com.tin.etbaf.form24G.bean.TBAFFileStatistics;
import com.tin.etbaf.form24G.util.Log;

/**
 *	This class is for validating the format of the fields of  
 *	File Header Record	for Regular and Correction Statements as per 	
 *	ETBAF File Format Version 1.3 
 *	
 *	@author TCS
 *	@version 9
 */ 
class FileHeaderValidation extends RecordValidation implements TBAFInterface
{
	private String lineNo = null; // Line Number 
	private String recType = null; // Record Type
	private String fileType = null; // File Type
//  private String fileCreationDate = null; //Date of Creation of File
	private String noOfBtchs = null; // Number Of Batches
	private String fhRecordHash = null; // File Header Record Hash
	private String fvuVersion = null; // FVU Version
	private String fvuFileLevelHash = null; // FVU File Level Hash
	private String samVersion = null; //	SAM Version
	private String samFileLevelHash = null; // SAM File Level Hash
	private String scmVersion = null; // SCM Version 
	private String scmFileLevelHash = null;// SCM File Level Hash
	public BHTDCompBean cBean = null;
	RecordValidation objRecVal = new RecordValidation();
	
	public String typeOfFile = "gauri";
	
	public static String sfile=null;
	
	/*******************************************FILE HEADER VALIDATION STARTS****************************************/

	/**
	 *	fhFieldValidator method is called from TBAFFormatValidator.java 
	 *	to validate Line No.1 of a '.txt' file which is a fILE header Record. 
	 * 	
	 *	@param objReadFVAL1 (Object of TBAFFormatValidator class)
	 *	@param lineCountP (Logical line number for each line in the file)
	 *	@param FhRecord (The File Header Record is taken as a String, line no.1 of the file)  	
	 *	@param errStrBuff (Object of TBAFErrorStringBuffer class. A string buffer in which the errors are appended)
	 *	
	 *	@return void
	 *	@throws Exception
	 */
	
	void fhFieldValidator(TBAFFormatValidator objReadFVAL1, int lineCountP, 
						  String fhRecord, TBAFErrorStringBuffer errStrBuff) throws Exception
	{
		try
		{
			boolean nullFieldBol = false;
			boolean fieldFoundBol = false;
			boolean carretBol = true;
			lineNo = "";
			recType = "";
			
			RawFileBean rawFileBean = RawFileBean.getInstance();
			/**
			 * Tokenize the File Header Record. Seperate the fields and '^' in the record
			 * '^' is the Field Seperator
			 */	
			StringTokenizer StrTokenizerFH = new StringTokenizer(fhRecord, TBAF_FIELD_SEPERATOR, true);
			int caretCounter = 0;
			int localFieldCountFH = 1; 
			while (StrTokenizerFH.hasMoreTokens())
			{
				String val = StrTokenizerFH.nextToken();
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
					if (caretCounter == 0 && localFieldCountFH == 1)
					{
						lineNo = "";
					}
					caretCounter++;
				}
				else
				{
					carretBol = false;
					fieldFoundBol = true;
				}
				//	If the number of fields found is greater than 15 in the File Header Record, reject the file(Previously it was 14 but File Creation Date is being added after File Type as per Client's new requirement)
				if (localFieldCountFH > 15)
				{
					break;
				}
				if (fieldFoundBol)
				{
					switch (localFieldCountFH)
					{
						case 1 :
							lineNo = val;
							break;
						case 2 :
							recType = val;
							break;
						case 3 :
							fileType = val;
							break;
						case 4 :
							objReadFVAL1.fileCreationDate = val;
							break;	
						case 5 :
							objReadFVAL1.stmtType = val;
							break;
						case 6 :
							objReadFVAL1.uploadBy = val;
							break;
						case 7 :
							objReadFVAL1.id = val;
							break;
						case 8 :
							noOfBtchs = val;
							break;
						case 9 :
							fhRecordHash = val;
							break;
						case 10 :
							fvuVersion = val;
							break;
						case 11 :
							fvuFileLevelHash = val;
							break;
						case 12 :
							samVersion = val;
							break;
						case 13 :
							samFileLevelHash = val;
							break;
						case 14 :
							scmVersion = val;
							break;
						case 15 :
							scmFileLevelHash = val;
							break;
					} // Closing SWITCH
					localFieldCountFH++;
				} // Closing If
			} // Closing inner while loop
			
			// Number of carets in the File Header Record must be exactly equal to 14.(Previously it was 13 ,as new field  File Creation Date is being added after File Type so the Total no. of carets became 14) 
			if (caretCounter != 14)
			{
				objReadFVAL1.inValidCaretCount = true;
				errStrBuff.append(TBAF_FHREC + "1" + "^" + "-" + "^^" + TBAF_FV_1000);
				return;
			}
			/**
			 *	Validation of LINE NUMBER(Field No.1 of File Header Record)	
			 * 
			 *  Line Number should be of length less than or equal to 9 digits.
			 *	Line Number should not have leading and trailing spaces.
			 *	Line Number should not have spaces in between the number.
			 *	Line Number should always be '1' for File Header Record.	
			 */
			if (lineNo.equals(TBAF_FIELD_NULL) || lineNo.equals(TBAF_FIELD_SEPERATOR))
			{
				errStrBuff.append(TBAF_FHREC + "-" + "^" + TBAF_FH_FIELD[1] + "^^" + TBAF_FV_1001);
				lineNo = "-";
			}
			else if (objRecVal.isFieldNull(lineNo))
			{
				errStrBuff.append(TBAF_FHREC + lineNo + "^" + TBAF_FH_FIELD[1] + "^^" + TBAF_FV_1002); 
			}
			else if (lineNo.trim().length() > 9 || lineNo.length() > 9)
			{
				errStrBuff.append(TBAF_FHREC + lineNo + "^" + TBAF_FH_FIELD[1] + "^^" + TBAF_FV_1005); 
			}
			else if (lineNo.length() <= 9)
			{
				String fhLineNum = objRecVal.trimInnerSpaces(lineNo);
				if (objRecVal.isInt(fhLineNum))
				{
					errStrBuff.append(TBAF_FHREC + lineNo + "^" + TBAF_FH_FIELD[1] + "^^" + TBAF_FV_1002); 
				}
				else if (!fhLineNum.equals(lineNo))
				{
					errStrBuff.append(TBAF_FHREC + lineNo + "^" + TBAF_FH_FIELD[1] + "^^" + TBAF_FV_1003); 
				}
				else if (Integer.parseInt(lineNo.trim()) != lineCountP)
				{
					errStrBuff.append(TBAF_FHREC + lineNo + "^" + TBAF_FH_FIELD[1] + "^^" + TBAF_FV_1004);
				}
			}
			else if (lineNo.trim().length() != lineNo.length())
			{
				errStrBuff.append(TBAF_FHREC + lineNo + "^" + TBAF_FH_FIELD[1] + "^^" + TBAF_FV_1003);
			}	// End of LINE NUMBER Validation
			
			/**	
			 *	Validation of RECORD TYPE(Field No.2 of File Header Record)
			 *
			 *	Record Type should be "FH" for File Header Record.
			 * 	Values other than "FH" are invalid.
			 */
			if (recType.equals(TBAF_FIELD_NULL) || recType.equals(TBAF_FIELD_SEPERATOR))
			{
				errStrBuff.append(TBAF_FHREC + lineNo + "^" + TBAF_FH_FIELD[2] + "^^" + TBAF_FV_1001);  // new error added in jan16 changed by puja
			}
			else if (!recType.equals(TBAF_FH_REC))
			{
				errStrBuff.append(TBAF_FHREC + lineNo + "^" + TBAF_FH_FIELD[2] + "^^" + TBAF_FV_1006);
			}	// End of RECORD TYPE Validation
			
			/**
			 *	Validation of FILE TYPE(Field No.3 of File Header Record)
			 *	
			 *	File Type should be "TBAF".
			 *	Value other than "TBAF" are invalid.
			 *	Gauri added a NEW file type validation for CR 89435, FVU 1.9
			 *	Validation to check FH File Type is written in BH validation
			 */
			
//			BatchValidation tempobj = new BatchValidation();
//			cBean = new BHTDCompBean();
//			String bhRecord = "2^BH^1^^1013180^^^1AS^???L?^KAMALA MILL^PAREL^MUMBAI^18^778877^67^7676666666^ABCD@GMAIL.COM^^CA^2025^^S^^^ABCD^EFG^HIJ^1^1000.00^^^^^01^A12^^^^KOLHAPUR^04^899889^^^XYZ@GMAIL.COM^8777887788^113^03^^^^1^1000.00^2000.00^0^0.00^0.00^0^0.00^0.00^0^0.00^0.00^^1^2000.00^0^0^0^^01^4323667178^AGRA10384D^AGRA10384D^^03^XYZ^PQR^STV^113^";
//			tempobj.bhFieldValidator(objReadFVAL1, cBean, 2, TBAFFormatValidator.fyy, errStrBuff);
//			String finYear = tempobj.financialYear;
//			Log.tbaf_log.debug("aaaaaaaaa string :- " + finYear);
//			int financialYear = Integer.parseInt(finYear);
////			Log.tbaf_log.debug("aaaaaaaaa :- " + financialYear);
//			Log.tbaf_log.debug("aaaaaaaaa :- " + financialYear);
//			System.out.println(financialYear);
			
			setFileType(fileType);
//			Log.tbaf_log.debug("inside fileheadervalidation setter "+fileType);
//			if (fileType.equals(TBAF_FIELD_NULL) || fileType.equals(TBAF_FIELD_SEPERATOR))
//			{
//				errStrBuff.append(TBAF_FHREC + lineNo + "^" + TBAF_FH_FIELD[3] + "^^" + TBAF_FV_1001);   // new error added in jan16  changed by puja
//			}
//			else if (!fileType.equals(TBAF_FILE_TYPE))
//			{
//				errStrBuff.append(TBAF_FHREC + lineNo + "^" + TBAF_FH_FIELD[3] + "^^" + TBAF_FV_1007);
//			}	// End of FILE TYPE Validation 		
//			
			//Gauri added year condition for Form Type for CR 89435, FVU 1.9::START
//			else if(financialYear < 2025) {
//				if(!fileType.equals(TBAF_FILE_TYPE)) {
//					errStrBuff.append(TBAF_FHREC + lineNo + "^" + TBAF_FH_FIELD[3] + "^^" + TBAF_FV_1007);
//				}
//			}
//			
//			else if(financialYear >= 2025) {
//				if(!fileType.equals(TBAF_NEW_FILE_TYPE)) {
//					errStrBuff.append(TBAF_FHREC + lineNo + "^" + TBAF_FH_FIELD[3] + "^^" + TBAF_FV_5067);
//				}
//			}
			//Gauri added year condition for Form Type for CR 89435, FVU 1.9::END
			
			/**
			 *	Validation of FILE CREATION DATE(Field No.4 "newly Added" of File Header Record)
			 *	
			 *	File Creation date is mandatory
			 *  It should not be future date
			 *  It should be in ddMMyyyy format
			
			 */
			if (objReadFVAL1.fileCreationDate.equals(TBAF_FIELD_NULL) || objReadFVAL1.fileCreationDate.equals(TBAF_FIELD_SEPERATOR))
			{
				errStrBuff.append(TBAF_FHREC + lineNo + "^" + TBAF_FH_FIELD[4] + "^^" + TBAF_FV_1001);  // new error added in jan16 changed by puja
			}
			else if (objRecVal.isFieldNull(objReadFVAL1.fileCreationDate))
			{
				errStrBuff.append(TBAF_FHREC + lineNo + "^" + TBAF_FH_FIELD[4] + "^^" + TBAF_FV_1002);  // new error added in jan16 changed by puja
			}
			else if( (objReadFVAL1.fileCreationDate.trim().length() != TBAF_FILE_CREATION_DATE_LEN) || (objReadFVAL1.fileCreationDate.length() != TBAF_FILE_CREATION_DATE_LEN))
			{
				errStrBuff.append(TBAF_FHREC + lineNo + "^" + TBAF_FH_FIELD[4] + "^^" + TBAF_FV_1025);
			}
			else if (objReadFVAL1.fileCreationDate.trim().length() != objReadFVAL1.fileCreationDate.length())
			{
				errStrBuff.append(TBAF_FHREC + lineNo + "^" + TBAF_FH_FIELD[4] + "^^" + TBAF_FV_1003);
			}
			else if(objRecVal.isInt(objReadFVAL1.fileCreationDate))
			{
				errStrBuff.append(TBAF_FHREC + lineNo + "^" + TBAF_FH_FIELD[4] + "^^" + TBAF_FV_1002);
			}
			else if(objRecVal.isDate(objReadFVAL1.fileCreationDate))
			{
				errStrBuff.append(TBAF_FHREC + lineNo + "^" + TBAF_FH_FIELD[4] + "^^" + TBAF_FV_1022);
			}
			else if(objRecVal.isFutureDate(objReadFVAL1.fileCreationDate))
			{
				errStrBuff.append(TBAF_FHREC + lineNo + "^" + TBAF_FH_FIELD[4] + "^^" + TBAF_FV_1023);
			}
			else
			{
				rawFileBean.setValue(objReadFVAL1.fileCreationDate,RawFileBean.FILE_CRTN_DT_FLDNUM); //Added by Bharath for Raw File Generation
			}
				
			
			
			
			
			  
			
			//End of File Creation Date validation
			
			
			
			/**
			 *	Validation of STATEMENT TYPE(Field No.5 of File Header Record)
			 *
			 *	Statement Type should be "O" for Regular/Original Statement and "C" for Correction statement.
			 *	Value other than "O" and "C" are invalid.
			 */ 
			if (objReadFVAL1.stmtType.equals(TBAF_FIELD_NULL) || objReadFVAL1.stmtType.equals(TBAF_FIELD_SEPERATOR))
			{
				objReadFVAL1.invalidStatementType = true;
				errStrBuff.append(TBAF_FHREC + lineNo + "^" + TBAF_FH_FIELD[5] + "^^" + TBAF_FV_1001); // new error added in jan16 changed by puja
			}
			else if (!objReadFVAL1.stmtType.equals(TBAF_TYPE_OF_STMT_ORIG)
						&& !objReadFVAL1.stmtType.equals(TBAF_TYPE_OF_STMT_CORR))
			{
				objReadFVAL1.invalidStatementType = true;
				errStrBuff.append(TBAF_FHREC + lineNo + "^" + TBAF_FH_FIELD[5] + "^^" + TBAF_FV_1008);
			}
			else
			{
		
				rawFileBean.setValue(objReadFVAL1.stmtType,RawFileBean.TYPE_OF_STMT_FLDNUM); //Added by Bharath for Raw File Generation
				objReadFVAL1.statReportBuffer.append(objReadFVAL1.stmtType + TBAF_FIELD_SEPERATOR);
			}	// End of STATEMENT TYPE Validation
			
			/**
			 *	Validation of UPLOADER TYPE(Field No.6 of File Header Record)
			 *
			 *	Uploader Type should be "D" for AO/Organization Upload and "T" for TFC upload.
			 *	Values other than "D" and "T" are invalid.
			 */
			if (objReadFVAL1.uploadBy.equals(TBAF_FIELD_NULL) || objReadFVAL1.uploadBy.equals(TBAF_FIELD_SEPERATOR))
			{
				errStrBuff.append(TBAF_FHREC + lineNo + "^" + TBAF_FH_FIELD[6] + "^^" + TBAF_FV_1001);  // new error added in jan16 changed by puja
			}
			else if (!objReadFVAL1.uploadBy.equals(TBAF_UPLOADED_BY_TFC) 
						&& !objReadFVAL1.uploadBy.equals(TBAF_UPLOADED_BY_AO))
			{
				errStrBuff.append(TBAF_FHREC + lineNo + "^" + TBAF_FH_FIELD[6] + "^^" + TBAF_FV_1009);
			}	// End of UPLOADER TYPE Validation
			
			/**
			 *	Validation of AIN/ORGANIZATION/TFC-ID(Field No.7 of File Header Record)
			 *
			 *	If uploader type is "D", then ID should be 7-digit integer or 6-character alpha-numeric.
			 *	Organization ID. If uploader type is "T", then ID should be 5-digit integer.
			 *	In case of 7-digit ID, the AIN should follow check-digit validation.
			 *	Check-digit: The 7-th digit of AIN should be mod 7 of first 6 digits.
			 */		
			if (objReadFVAL1.id.equals(TBAF_FIELD_NULL) || objReadFVAL1.id.equals(TBAF_FIELD_SEPERATOR))
			{
				errStrBuff.append(TBAF_FHREC + lineNo + "^" + TBAF_FH_FIELD[7] + "^^" + TBAF_FV_1001);  // new error added in jan16 changed by puja
			}
			else if (objRecVal.isFieldNull(objReadFVAL1.id))
			{
				errStrBuff.append(TBAF_FHREC + lineNo + "^" + TBAF_FH_FIELD[7] + "^^" + TBAF_FV_1002);  // new error added in jan16 changed by puja
			}
			else if (objReadFVAL1.id.trim().length() != objReadFVAL1.id.length())
			{
				errStrBuff.append(TBAF_FHREC + lineNo + "^" + TBAF_FH_FIELD[7] + "^^" + TBAF_FV_1003);  // new error added in jan16  changed by puja
			}
			else if (objReadFVAL1.uploadBy.equals(TBAF_UPLOADED_BY_TFC))
			{
				if ((objReadFVAL1.id.trim().length() != 5) && (objReadFVAL1.id.length() != 5))
				{
					errStrBuff.append(TBAF_FHREC + lineNo + "^" + TBAF_FH_FIELD[7] + "^^" + TBAF_FV_1010);   // new error added in jan16 changed by puja
				}
				else if ((objReadFVAL1.id.trim().length() == 5) && (objReadFVAL1.id.length() == 5))
				{
					if (objRecVal.isInt(objReadFVAL1.id))
					{
						errStrBuff.append(TBAF_FHREC + lineNo + "^" + TBAF_FH_FIELD[7] + "^^" + TBAF_FV_1002);   // new error added in jan16 changed by puja
					}
					else
					{
						objReadFVAL1.statReportBuffer.append(objReadFVAL1.id.trim() + TBAF_FIELD_SEPERATOR);
					}
				}
			}
			else if (objReadFVAL1.uploadBy.equals(TBAF_UPLOADED_BY_AO))
			{
				if (objReadFVAL1.id.trim().length() != 7 && objReadFVAL1.id.length() != 7 
					&& objReadFVAL1.id.trim().length() != 6 && objReadFVAL1.id.length() != 6)
				{
					errStrBuff.append(TBAF_FHREC + lineNo + "^" + TBAF_FH_FIELD[7] + "^^" + TBAF_FV_1011);   // new error added in jan16 changed by puja
				}
				else if (objReadFVAL1.id.trim().length() == 6 && objReadFVAL1.id.length() == 6)
				{
					if (objRecVal.isValidOrgID(objReadFVAL1.id))
					{
						errStrBuff.append(TBAF_FHREC + lineNo + "^" + TBAF_FH_FIELD[7] + "^^" + TBAF_FV_1002);  // new error added in jan16 changed by puja
					}
					else
					{
						objReadFVAL1.statReportBuffer.append(objReadFVAL1.id.trim() + TBAF_FIELD_SEPERATOR);
					}
				}
				else if (objReadFVAL1.id.trim().length() == 7 && objReadFVAL1.id.length() == 7)
				{
					if (objRecVal.isInt(objReadFVAL1.id))
					{
						objReadFVAL1.invalidStatementType = true;
						errStrBuff.append(TBAF_FHREC + lineNo + "^" + TBAF_FH_FIELD[7] + "^^" + TBAF_FV_1002);  // new error added in jan16 changed by puja
					}
					else if (objReadFVAL1.id.startsWith("0"))
					{
						objReadFVAL1.invalidStatementType = true;
						errStrBuff.append(TBAF_FHREC + lineNo + "^" + TBAF_FH_FIELD[7] + "^^" + TBAF_FV_1002);  // new error added in jan16 changed by puja
					}
					else if (objRecVal.checkID(objReadFVAL1.id))
					{
						objReadFVAL1.invalidStatementType = true;
						errStrBuff.append(TBAF_FHREC + lineNo + "^" + TBAF_FH_FIELD[7] + "^^" + TBAF_FV_1002);  // new error added in jan16 changed by puja
					}
					else
					{
						objReadFVAL1.statReportBuffer.append(objReadFVAL1.id.trim() + TBAF_FIELD_SEPERATOR);
					}
				}
			}	// End of AIN/Organization/TFC ID Validation
			
		    /**
			 * 	Validation of NUMBER OF BATCHES(Field No.8 Of File Header Record)
			 * 	
			 *  Number Of Batches should be of length less than or equal to 9 digits.
			 *	Number Of Batches should not have leading and trailing spaces.
			 *	Number Of Batches should not have spaces in between the number.
			 *	Number Of Batches should always have the value as '1'.
			 */
			if (noOfBtchs.equals(TBAF_FIELD_NULL) || noOfBtchs.equals(TBAF_FIELD_SEPERATOR))
			{
				errStrBuff.append(TBAF_FHREC + lineNo + "^" + TBAF_FH_FIELD[8] + "^^" + TBAF_FV_1001);  // New error code added in Jan16  changed by puja
			}
			else if (objRecVal.isFieldNull(noOfBtchs))
			{
				errStrBuff.append(TBAF_FHREC + lineNo + "^" + TBAF_FH_FIELD[8] + "^^" + TBAF_FV_1002);   // New error code added in Jan16  changed by puja
			}
			else if (noOfBtchs.length() > 9)
			{
				errStrBuff.append(TBAF_FHREC + lineNo + "^" + TBAF_FH_FIELD[8] + "^^" + TBAF_FV_1005);   // New error code added in Jan16  changed by puja
			}
			else if (noOfBtchs.length() <= 9)
			{
				String numOfBatches = objRecVal.trimInnerSpaces(noOfBtchs);
				if (objRecVal.isInt(numOfBatches))
				{
					errStrBuff.append(TBAF_FHREC + lineNo + "^" + TBAF_FH_FIELD[8] + "^^" + TBAF_FV_1002);   // New error code added in Jan16  changed by puja
				}
				else if (!numOfBatches.equals(noOfBtchs))
				{
					errStrBuff.append(TBAF_FHREC + lineNo + "^" + TBAF_FH_FIELD[8] + "^^" + TBAF_FV_1003);   // New error code added in Jan16  changed by puja 
				}
				else if (Integer.parseInt(noOfBtchs.trim()) != 1)
				{
					errStrBuff.append(TBAF_FHREC + lineNo + "^" + TBAF_FH_FIELD[8] + "^^" + TBAF_FV_1012);   // New error code added in Jan16  changed by puja
				}
				else
				{
					rawFileBean.setValue(noOfBtchs,RawFileBean.NO_OF_BTCH_FLDNUM); //Added by Bharath for Raw File Generation
				}
			}
			else if (noOfBtchs.trim().length() != noOfBtchs.length())
			{
				errStrBuff.append(TBAF_FHREC + lineNo + "^" + TBAF_FH_FIELD[8] + "^^" + TBAF_FV_1003);
			}	
			else
			{
				rawFileBean.setValue(noOfBtchs,RawFileBean.NO_OF_BTCH_FLDNUM); //Added by Bharath for Raw File Generation
			}
			
				
		
//			 End of NUMBER OF BATCHES Validation
			// Validation of FVU VERSION(Field No.10 of File Header Record)
			if (fvuVersion.equals(TBAF_FIELD_NULL) || fvuVersion.equals(TBAF_FIELD_SEPERATOR))
			{
				objReadFVAL1.statReportBuffer.append("-" + TBAF_FIELD_SEPERATOR);
			}
			else
			{
				objReadFVAL1.statReportBuffer.append(fvuVersion + TBAF_FIELD_SEPERATOR);
			}
			
			//	Validation of FVU FILE LEVEL HASH(Field No.11 of File Header Record)
			if (fvuFileLevelHash.equals(TBAF_FIELD_NULL) || fvuFileLevelHash.equals(TBAF_FIELD_SEPERATOR))
			{
				objReadFVAL1.statReportBuffer.append("-" + TBAF_FIELD_SEPERATOR);
			}
			else
			{
				objReadFVAL1.statReportBuffer.append(fvuFileLevelHash + TBAF_FIELD_SEPERATOR);
			}
			
			//	Validation of SAM VERSION(Field No.12 of File Header Record) 
			if (samVersion.equals(TBAF_FIELD_NULL) || samVersion.equals(TBAF_FIELD_SEPERATOR))
			{
				objReadFVAL1.statReportBuffer.append("-" + TBAF_FIELD_SEPERATOR);
			}
			else
			{
				objReadFVAL1.statReportBuffer.append(samVersion + TBAF_FIELD_SEPERATOR);
			}
			
			// Validation of SAM FILE LEVEL HASH(Field No.13 of File Header Record)
			if (samFileLevelHash.equals(TBAF_FIELD_NULL) || samFileLevelHash.equals(TBAF_FIELD_SEPERATOR))
			{
				objReadFVAL1.statReportBuffer.append("-" + TBAF_FIELD_SEPERATOR);
			}
			else
			{
				objReadFVAL1.statReportBuffer.append(samFileLevelHash + TBAF_FIELD_SEPERATOR);
			}
			
		} // end of try block
		/*******************************************FILE HEADER VALIDATION ENDS**************************************/
		catch (Exception e)
		{
			Log.tbaf_log.error("Exception", e);
			e.printStackTrace();
			Log.tbaf_log.error("FileHeaderValidation.java : " + e.toString());
		}
	}
	
	//Gauri added this method for CR 89435, FVU 1.9
	public static void setFileType(String file) {
		String temp = file;
		sfile = temp;
	}

	public static String getFileType() {
		return sfile;
	}
	
}	// End of fhFieldValidator method
