/**	
 * 	Class: TDValidation.java
 */
package com.tin.etbaf.form24G.fvu;
import java.util.*;

import com.tin.etbaf.form24G.bean.BHTDCompBean;
import com.tin.etbaf.form24G.util.Log;
/**
 *	This class is for validating the format of the fields of  
 *	DDO Transaction Detail Record for Regular and Correction Statements as per 	
 *	ETBAF File Format Version 1.3 
 *	
 *	@author TCS
 *	@version 11
 */ 
public class TDValidation extends RecordValidation implements TBAFInterface
{
	private String lineNo = null; // Line Number
	private String recType = null; // Record Type
	private String tdBatchNo = null; // Batch Number
	private String tdRevMode = null; // Revision Mode
	private String tdSerialNo = null; // Serial Number
	//private String tdOldSerialNo = null; // Ols serial Number
	private String tdLastTAN = null; // LAST DDO TAN        //Modified By Aditya
	private String tdTAN = null; // DDO TAN
	private String tdDDOName = null; // DDO Name
	private String tdDDOAddress1 = null; // DDO Address 1
	private String tdDDOAddress2 = null; // DDO Address 2
	private String tdDDOAddress3 = null; // DDO Address 3
	private String tdDDOAddress4 = null; // DDO Address 4
	private String tdDDOCity = null; // DDO Ciy
	private String tdDDOStateCode = null; // DDO State Code
	private String tdDDOPinCode = null; // DDO Pin Code
	private String taxAmt = null; // DDO Tax Amount
	private String formType = null; //Gauri added this for CR 89435 // Month & Year Of Payment     
	private String tdDDORegNo = null; //DDO Registration Number       //Added by Subhankar
	private String tdDDOCode = null; //DDO Code                    
	private String tdDDOEmailID = null; //DDO Email ID
	private String remittedAmt = null; //DDO Remitted Amount
	private String tdDDODeductionNature = null; //Nature of Deduction
	private String tdDDOMapping = null; //DDO Mapping and Update
	private String tdDDOSerialNo = null; //DDO Serial No.
	private String tdLastRemittedAmt = null; //DDO Last Remitted Amount
	private String tdLastDDORegNo = null; //DDO Last DDO RegNo
	private String tdLastDDOCode = null; //DDO Last DDO Code
	private String tdLastTaxAmt = null; //DDO Filler9
	private String tdLastDDODeductionNature = null; //DDO Filler10
	private String tdFiller_11 = null; //DDO Filler11
	private String tdFiller_12 = null; //DDO Filler12
	private String tdFiller_13 = null; //DDO Filler13              //End of Added by subhankar
	private String tdRecordHash = null; // Transaction Detail Record Hash
	private boolean inValidfirstSerialNo = false; // Used to check the first TD record has a valid Serial Number
	RecordValidation objRecVal = new RecordValidation();

	/*********************************DDO TRANSACTION DETAIL RECORD VALIDATION STARTS********************************/
	/**
	 *	tdFieldValidator method is called from TBAFFormatValidator.java 
	 *	to validate the DDO Transaction Details Records. 
	 * 	
	 *	@param objReadFVAL3 (Object of TBAFFormatValidator class)
	 *	@param lineCountP (Logical line number for each line in the file)
	 *	@param tdRecord (Each TD Record is taken as a String in the file)  	
	 *	@param errStrBuff (Object of TBAFErrorStringBuffer class. A string buffer in which the errors are appended)
	 *	
	 *	@return void
	 *	@throws Exception 
	 */

	void tdFieldValidator(TBAFFormatValidator objReadFVAL3, BHTDCompBean cBeanTD,int lineCountP, 
			String tdRecord, TBAFErrorStringBuffer errStrBuff) throws Exception
			{
		try
		{
			boolean inValidCorrectionTDRecord = false;
			boolean fieldFoundBol = false;
			boolean carretBol = true;
			lineNo = "";
			recType = "";
			/**
			 * Tokenizing the DDO Transaction Detail Record. Seperate the fields and '^' in the record
			 * '^' is the field seperator
			 */	
			StringTokenizer StrTokenizerTD = new StringTokenizer(tdRecord, TBAF_FIELD_SEPERATOR, true);
			int caretCounter = 0;
			int localFieldCountTD = 1;
			while (StrTokenizerTD.hasMoreTokens())
			{
				String val = StrTokenizerTD.nextToken();
				fieldFoundBol = false;
				if ((val.equals(TBAF_FIELD_SEPERATOR) && carretBol) || val.trim().length() == 0)
				{
					fieldFoundBol = true;
				}
				if (val.equals(TBAF_FIELD_SEPERATOR))
				{
					carretBol = true;
					if (caretCounter == 0 && localFieldCountTD == 1)
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
				//	If the number of fields found is greater than 33 in the File Header Record, reject the file
				if (localFieldCountTD > 33)       //Added by Subhankar
				{
					break;
				}
				if (fieldFoundBol)
				{
					switch (localFieldCountTD)
					{
					case 1 :
						lineNo = val;
						break;
					case 2 :
						recType = val;
						break;
					case 3 :
						tdBatchNo = val;
						break;
					case 4 :
						tdRevMode = val;
						break;
					case 5 :
						if (val.equals(TBAF_FIELD_NULL) || val.equals(TBAF_FIELD_SEPERATOR))
						{
							tdSerialNo = "-";
						}
						else
						{
							tdSerialNo = val;
						}
						break;
					case 6 :
						tdLastTAN = val;         //Added by Aditya
						break;
					case 7 :
						tdTAN = val;
						break;
					case 8 :
						tdDDOName = val;
						break;
					case 9 :
						tdDDOAddress1 = val;
						break;
					case 10 :
						tdDDOAddress2 = val;
						break;
					case 11 :
						tdDDOAddress3 = val;
						break;
					case 12 :
						tdDDOAddress4 = val;
						break;
					case 13 :
						tdDDOCity = val;
						break;
					case 14 :
						tdDDOStateCode = val;
						break;
					case 15 :
						tdDDOPinCode = val;
						break;
					case 16 :
						taxAmt = val;
						break;
					case 17 :
						//monthYear = val;     
						formType = val; //Gauri added this field for CR 89435, FVU 1.9
						break;
					case 18:
						tdDDORegNo = val;                        //Added by Subhankar
						break;
					case 19:
						tdDDOCode = val;
						break;
					case 20:
						tdDDOEmailID = val;
						break;
					case 21:
						remittedAmt = val  ;
						break;
					case 22:
						tdDDODeductionNature = val;
						break;
					case 23:
						tdDDOMapping = val;
						break;
					case 24:
						tdDDOSerialNo = val;
						break;
					case 25:
						tdLastRemittedAmt = val;
						break;
					case 26:
						tdLastDDORegNo = val;
						break;
					case 27:
						tdLastDDOCode = val;
						break;
					case 28:
						tdLastTaxAmt= val;
						break;
					case 29:
						tdLastDDODeductionNature = val;
						break;
					case 30:
						tdFiller_11 = val;
						break;
					case 31:
						tdFiller_12 = val;
						break;
					case 32:
						tdFiller_13 = val;                   //End of Added by Subhankar
						break;
					case  33:
						tdRecordHash = val;
						break;
					} // end of switch
					localFieldCountTD++;
				} // end of IF
			} // end of while loop

				//	Number of carets in the DDO Transaction Detail Record must be exactly equal to 32 as the total number of fields are 33.
			if (caretCounter != 32) //Added By Subhankar for File Format 24G
			{
				objReadFVAL3.inValidCaretCount = true;
				if (!lineNo.equals(TBAF_FIELD_NULL) && !lineNo.equals(TBAF_FIELD_SEPERATOR))
				{
					errStrBuff.append(TBAF_TDREC + lineNo + "^" + "-" + "^" + tdSerialNo + "^" + TBAF_FV_3000);
					objReadFVAL3.errorFoundInTD = true;  
				}
				else if (lineNo.equals(TBAF_FIELD_NULL) || lineNo.equals(TBAF_FIELD_SEPERATOR))
				{
					errStrBuff.append(TBAF_TDREC + lineCountP + "^" + "-" + "^" + tdSerialNo + "^" + TBAF_FV_3000);
					objReadFVAL3.errorFoundInTD = true;
				}
				return;
			}

			/**
			 *	VALIDATING LINE NUMBER(Field No.1 of DDO Transaction Detail Record)
			 *	
			 *	Line Number should not be NULL.
			 * 	Line Number should be of length less than or equal to 9 digits. 
			 *	Line Number should not have leading and trailing spaces.
			 *	Line Number should not have spaces in between the number.
			 *	Line Number should always be in sequence.
			 */




			if (lineNo.equals(TBAF_FIELD_NULL) || lineNo.equals(TBAF_FIELD_SEPERATOR))
			{
				errStrBuff.append(TBAF_TDREC + "-" + "^" + TBAF_TD_FIELD[1] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3001);
				lineNo = "-";
			}
			else if (objRecVal.isFieldNull(lineNo))
			{
				errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[1] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3002);  // New Error added in jan16 changed by puja
			}
			else if (lineNo.trim().length() > 9 || lineNo.length() > 9)
			{
				errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[1] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3005);
			}
			else if (lineNo.length() <= 9)
			{
				String tdLineNum = objRecVal.trimInnerSpaces(lineNo);
				if (objRecVal.isInt(tdLineNum))
				{
					errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[1] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3002); // New Error added in jan16 changed by puja
				}
				else if (!tdLineNum.equals(lineNo))
				{
					errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[1] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3003);
				}
				else if (Integer.parseInt(lineNo.trim()) != lineCountP)
				{
					errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[1] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3004);
				}
			}
			else if (lineNo.trim().length() != lineNo.length())
			{
				errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[1] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3048);
			}	// End of LINE NUMBER Validation

			/**	
			 *	Validation of RECORD TYPE(Field No.2 of DDO Transaction Detail Record)
			 * 	
			 * 	Record Type should not be NULL.
			 *	Record Type should be "TD" for DDO Transaction Detail Record. 
			 * 	Values other than "TD" are invalid.
			 */
			if (recType.equals(TBAF_FIELD_NULL) || recType.equals(TBAF_FIELD_SEPERATOR))
			{
				errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[2] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3001);  // New Error code added in jan16 changed by puja
			}
			else if (!recType.equals(TBAF_TD_REC))
			{
				errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[2] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3006);
			}	//	End of RECORD TYPE Validation

			/**
			 *	Validation of BATCH NUMBER(Field No.3 of DDO Transaction Detail Record)	
			 *
			 *	Batch Number should not be NULL.
			 * 	Batch Number should be of length less than or equal to 9 digits.
			 *	Batch Number should not have leading and trailing spaces.
			 *	Batch Number should not have spaces in between the number.
			 *	Batch Number should always have the value as '1'.
			 */
			if (tdBatchNo.equals(TBAF_FIELD_NULL) || tdBatchNo.equals(TBAF_FIELD_SEPERATOR))
			{
				errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[3] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3001); 
			}
			else if (objRecVal.isFieldNull(tdBatchNo))
			{
				errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[3] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3002); 
			}
			else if (tdBatchNo.trim().length() > 9 || tdBatchNo.length() > 9)
			{
				errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[3] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3005); 
			}
			else if (tdBatchNo.length() <= 9)
			{
				String tdBatchNumber = objRecVal.trimInnerSpaces(tdBatchNo);
				if (objRecVal.isInt(tdBatchNumber))
				{
					errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[3] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3002); 
				}
				else if (!tdBatchNumber.equals(tdBatchNo))
				{
					errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[3] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3003); 
				}
				else if (Integer.parseInt(tdBatchNo.trim()) != 1)
				{
					errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[3] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3007);
				}
			}
			else if (tdBatchNo.trim().length() != tdBatchNo.length())
			{
				errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[3] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3048);
			}	//	End of BATCH NUMBER Validation


			/**
			 *	Validation of REVISION MODE(Field No.4 of DDO Transaction Detail Record)
			 *
			 *	If the statement type is Regular or C4 Correction this field must be NULL.
			 *	In case of C2 and C3 Correction Revision Mode is MANDATORY.
			 *	Value should be "A" for Addition , "D" for Deletion and "U" for Updation.
			 *	Values other than "A","D" and "U" are invalid.
			 */
			if (objReadFVAL3.stmtType.equals(TBAF_TYPE_OF_STMT_ORIG) 
					|| (objReadFVAL3.stmtType.equals(TBAF_TYPE_OF_STMT_CORR) 
							&& objReadFVAL3.transType.equals(TBAF_TRANSACTION_TYPE_C4)))
			{
				if (!tdRevMode.equals(TBAF_FIELD_NULL) && !tdRevMode.equals(TBAF_FIELD_SEPERATOR))
				{
					objReadFVAL3.invalidRecord = true;
					errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[4] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3008);
				}
			}
			else if (objReadFVAL3.stmtType.equals(TBAF_TYPE_OF_STMT_CORR) && (objReadFVAL3.transType.equals(TBAF_TRANSACTION_TYPE_M))) 
				//|| objReadFVAL3.transType.equals(TBAF_TRANSACTION_TYPE_C3)) )
			{
				if (tdRevMode.equals(TBAF_FIELD_NULL) || tdRevMode.equals(TBAF_FIELD_SEPERATOR))
				{
					objReadFVAL3.invalidRecord = true;
					errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[4] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3001);  // New error code added in jan16 changed by puja
				}
				else if (objRecVal.isFieldNull(tdRevMode))
				{
					objReadFVAL3.invalidRecord = true;
					errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[4] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3002); // New Error code added in jan16 changed by puja
				}
				else if (!tdRevMode.equals(TBAF_REVISION_MODE_ADD) && !tdRevMode.equals(TBAF_REVISION_MODE_DEL) && !tdRevMode.equals(TBAF_REVISION_MODE_UPDATE))
				{
					objReadFVAL3.invalidRecord = true;
					errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[4] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3009);
				}
				
			}	//	End of REVISION MODE Validation

			/**
			 *	Validation of SERIAL NUMBER(Field No. 5 of DDO Transaction Detail Record)
			 *
			 * 	Serial Number is MANDATORY irrespective of the Statement Type.
			 *	Serial number should be a numeric value of length less than or equal to 9 digits.
			 *	Serial Number should not be "0" (Zero).  
			 * 	Serial Number should not have negative or decimal values 	
			 *  Serial Number must be in sequence.
			 *	No leading and trailing spaces should be specified.
			 */	
			if (tdSerialNo.equals("-"))
			{
				errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[5] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3001);   // New Error code added in jan16 changed  by puja
				objReadFVAL3.previousTDSrNo++;
			}
			else if (objRecVal.isFieldNull(tdSerialNo))
			{
				errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[5] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3002); // New Error code added in jan16 changed  by puja
				objReadFVAL3.previousTDSrNo++; 
			}
			else if (tdSerialNo.trim().length() > 9 || tdSerialNo.length() > 9)
			{
				errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[5] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3005);
				objReadFVAL3.previousTDSrNo++;
			}
			else if (objRecVal.isInt(tdSerialNo))
			{
				errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[5] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3002); // New Error code added in jan16 changed  by puja
				objReadFVAL3.previousTDSrNo++;
			}
			else if (tdSerialNo.trim().length() != tdSerialNo.length())
			{
				errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[5] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3048);
				objReadFVAL3.previousTDSrNo++;
			}
			else if (Integer.parseInt(tdSerialNo.trim()) < 1)
			{
				inValidfirstSerialNo = true;
				errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[5] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3002); // New Error code added in jan16 changed  by puja
			}




			//For Correction Type 'M' and Transaction Type 'N' the order of serial number should always be in sequence in increasing order
			else if(objReadFVAL3.stmtType.equals(TBAF_TYPE_OF_STMT_CORR) && objReadFVAL3.transType.equals(TBAF_TRANSACTION_TYPE_M) && tdRevMode.equals(TBAF_REVISION_MODE_ADD))
			{
				String serNoModeForN =  TBAF_REVISION_MODE_ADD + tdSerialNo;
				if(! objReadFVAL3.corrTDConflict.add(serNoModeForN))
				{
					inValidCorrectionTDRecord = true;
					errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[5] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3054);
				}

				if(! inValidCorrectionTDRecord)
				{
					objReadFVAL3.totalNoOfTDWithNMode++;
					if(objReadFVAL3.totalNoOfTDWithNMode == 1)
					{
						objReadFVAL3.SerialCntForTDWithNMode = Integer.parseInt(tdSerialNo);
						objReadFVAL3.SerialCntForTDWithNMode++;
					}
					else
					{
						if(Integer.parseInt(tdSerialNo) != objReadFVAL3.SerialCntForTDWithNMode)
						{
							errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[5] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3010);
						}
						else
						{
							objReadFVAL3.SerialCntForTDWithNMode++; 
						}
					}
				}

			}

			//End of Validation

			//For Correction Type 'M' and Transaction Type 'D' the Serial Number should always appear once in File  
			else if(objReadFVAL3.stmtType.equals(TBAF_TYPE_OF_STMT_CORR) && objReadFVAL3.transType.equals(TBAF_TRANSACTION_TYPE_M) && tdRevMode.equals(TBAF_REVISION_MODE_DEL))
			{
				String serNoModeForD =  TBAF_REVISION_MODE_DEL + tdSerialNo;
				if(! objReadFVAL3.corrTDConflict.add(serNoModeForD))
				{
					errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[5] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3055);
				}
			}

			//End of Validation


			else if (objReadFVAL3.stmtType.equals(TBAF_TYPE_OF_STMT_ORIG) || objReadFVAL3.transType.equals(TBAF_TRANSACTION_TYPE_C4))
			{
				if (objReadFVAL3.totalNoOfTDRead == 1)
				{
					// To check the serial number is not "0" (Zero).
					objReadFVAL3.firstTDSrNo = Integer.parseInt(tdSerialNo.trim());
					if (Integer.parseInt(tdSerialNo.trim()) != 1)
					{
						inValidfirstSerialNo = true;
						errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[5] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3010);
					}
					else
					{
						objReadFVAL3.previousTDSrNo = Integer.parseInt(tdSerialNo.trim());
					}
				}
				else if (objReadFVAL3.totalNoOfTDRead > 1 && inValidfirstSerialNo == false && objReadFVAL3.inValidCaretCount == false)
				{
					//	To check the serial numbers are in sequence.
					if (Integer.parseInt(tdSerialNo.trim()) != (objReadFVAL3.previousTDSrNo + 1))
					{
						errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[5] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3051);
					}
					else
					{
						objReadFVAL3.previousTDSrNo = Integer.parseInt(tdSerialNo.trim());
					}
				}
				else
				{
					//	Adding Serial No. to the HashSet.
					objReadFVAL3.hashSerialNo.add(new Integer(Integer.parseInt(tdSerialNo)));
				}
			}

			//Commented on 31st Oct as For Correction other than M and X  is not required

			/*	else if (objReadFVAL3.stmtType.equals(TBAF_TYPE_OF_STMT_CORR) 
					 && !objReadFVAL3.transType.equals(TBAF_TRANSACTION_TYPE_C4))
			{
				if (objReadFVAL3.totalNoOfTDRead == 1)
				{
					objReadFVAL3.firstTDSrNo = Integer.parseInt(tdSerialNo.trim());
					//	For Correction C2 & C3 the First Serial Number should be greater than 1.
					if (Integer.parseInt(tdSerialNo.trim()) == 1)
					{
						errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[5] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3011);
						inValidfirstSerialNo = true;
					}
					else
					{
						objReadFVAL3.previousTDSrNo = Integer.parseInt(tdSerialNo.trim());
					}
					//	Adding Serial No. to the HashSet.
					objReadFVAL3.hashSerialNo.add(new Integer(Integer.parseInt(tdSerialNo)));
				}
				else if (objReadFVAL3.totalNoOfTDRead > 1 && inValidfirstSerialNo == false 
						 && objReadFVAL3.inValidCaretCount == false)
				{
					//	To check the serial numbers are in sequence. 
					if (Integer.parseInt(tdSerialNo.trim()) != (objReadFVAL3.previousTDSrNo + 1))
					{
						errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[5] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3051);
					}
					else
					{
						objReadFVAL3.previousTDSrNo = Integer.parseInt(tdSerialNo.trim());
					}
					//	Adding Serial No. to the HashSet.
					objReadFVAL3.hashSerialNo.add(new Integer(Integer.parseInt(tdSerialNo)));
				}
			}	  */


			//	End of SERIAL NUMBER Validation

			/**
			 *	Validation of OLD SERIAL NUMBER(Field No. 6 of DDO Transaction Detail Record)
			 * 
			 *	For Regular and C4 Correction Old Serial Number should be NULL.
			 *  For C2, C3 Correction and Revision Mode is "ADD" Old Serial Number should be NULL. 
			 */
			/*	if (objReadFVAL3.stmtType.equals(TBAF_TYPE_OF_STMT_ORIG)
				|| (objReadFVAL3.stmtType.equals(TBAF_TYPE_OF_STMT_CORR) 
				&& !objReadFVAL3.transType.equals(TBAF_TRANSACTION_TYPE_C4) 
				&& tdRevMode.equals(TBAF_REVISION_MODE_ADD)))
			{
				if (!tdOldSerialNo.equals(TBAF_FIELD_NULL) && !tdOldSerialNo.equals(TBAF_FIELD_SEPERATOR))
				{
					errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[6] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3012);
				}
			}*/
			/**
			 *	For C2, C3 Correction and Revision Mode is "DEL" Old Serial Number is MANDATORY.
			 *	Old Serial Number should be a numeric value of length less than or equal to 9 digits.
			 *	No sequence check is needed.
			 *	No leading and trailing spaces should be specified.
			 *	Old Serial Number should not have "0"(Zero), Negative or Decimal values.
			 */	  
			/*else if ((objReadFVAL3.transType.equals(TBAF_TRANSACTION_TYPE_C2) 
					  || objReadFVAL3.transType.equals(TBAF_TRANSACTION_TYPE_C3)) 
					  && tdRevMode.equals(TBAF_REVISION_MODE_DEL))
			{
				countOldSerialNo++;
				if (tdOldSerialNo.equals(TBAF_FIELD_NULL) || tdOldSerialNo.equals(TBAF_FIELD_SEPERATOR))
				{
					errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[6] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3001);
				}
				else if (objRecVal.isFieldNull(tdOldSerialNo))
				{
					errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[6] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3002);
				}
				else if (tdOldSerialNo.trim().length() > 9 || tdOldSerialNo.length() > 9)
				{
					errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[6] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3005);
				}
				else if (objRecVal.isInt(tdOldSerialNo))
				{
					errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[6] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3002);
				}
				else if (tdOldSerialNo.trim().length() != tdOldSerialNo.length())
				{
					errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[6] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3003);
				}
				else if (Integer.parseInt(tdOldSerialNo.trim()) < 1)
				{
					errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[6] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3002);
				}
				else
				{
					//	Adding Old Serial Number to the HashTable.
					objReadFVAL3.hashOldSerialNo.put(new Integer(countOldSerialNo), new Integer(Integer.parseInt(tdOldSerialNo)));
				}
			}	*/    //	End of OLD SERIAL NUMBER Validation



			/**
			 * Validation of DDO LAST TAN(Field No. 6 Of DDO Transaction Detail Record)
			 *
			 * Value of Last TAN should be null for all modes except for the mode 'U'.
			 * Value is mandatory for the revision mode 'U'.
			 * TAN should be alpha numeric and length exactly equal to 10 digits.
			 * No leading and trailing spaces should be specified.
			 * Should follow check digit validation of TAN  
			 */

			boolean isLastTANInvalid = false;
			if(objReadFVAL3.stmtType.equals(TBAF_TYPE_OF_STMT_CORR) && objReadFVAL3.transType.equals(TBAF_TRANSACTION_TYPE_M) && tdRevMode.equals(TBAF_REVISION_MODE_UPDATE))
			{
				if (tdLastTAN.equals(TBAF_FIELD_NULL) || tdLastTAN.equals(TBAF_FIELD_SEPERATOR))
				{
					isLastTANInvalid=true;
					errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[6] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3001);  // New Error added in jan16 chnaged by puja
				}
				else if (objRecVal.isFieldNull(tdLastTAN))
				{
					isLastTANInvalid=true;
					errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[6] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3002); // New Error added in jan16 changed by puja
				}
				else if (tdLastTAN.trim().length() != tdLastTAN.length())
				{
					isLastTANInvalid=true;
					errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[6] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3048);
				}
				else if (tdLastTAN.trim().length() != 10 || tdLastTAN.length() != 10)
				{
					isLastTANInvalid=true;
					errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[6] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3014);
				}
				else if (tdLastTAN.trim().length() == 10 || tdLastTAN.length() == 10)
				{
					if ((objRecVal.isAlphaNum(tdLastTAN) && ! objRecVal.checkTanAgainstConstants(tdLastTAN)) || (objRecVal.checkTan(tdLastTAN) && ! objRecVal.checkTanAgainstConstants(tdLastTAN)))
					{
						isLastTANInvalid=true;
						errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[6] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3002);  // New Error added in jan16  changed by puja
					}
					else
					{
						
					}
				}
					//objReadFVAL3.statReportBuffer.append("-" + TBAF_FIELD_SEPERATOR);
				}
			else
			{
				if (!tdLastTAN.equals(TBAF_FIELD_NULL) && !tdLastTAN.equals(TBAF_FIELD_SEPERATOR))
				{
					errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[6] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3012);
				}
			}




			//End of Nature Of DDO Last TAN Validation





			/**
			 *	Validation of DDO TAN(Field No. 7 of DDO Transaction Detail Record)
			 * 
			 *	This field is MANDATORY irrespective of the Statement Types and Revision Modes.
			 *	TAN should be alpha numeric and length exactly equal to 10 digits.
			 *	No leading and trailing spaces should be specified.
			 *	Should follow check digit validation of TAN  
			 */
			boolean isTANInvalid = false;
			if (tdTAN.equals(TBAF_FIELD_NULL) || tdTAN.equals(TBAF_FIELD_SEPERATOR))
			{
				//errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[7] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3001);
				errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[7] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3067);
				isTANInvalid = true;
			}
			else if (objRecVal.isFieldNull(tdTAN))
			{
				//errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[7] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3002);
				errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[7] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3067);
				isTANInvalid = true;
			}
			else if (tdTAN.trim().length() != tdTAN.length())
			{
				//errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[7] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3048);
				errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[7] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3067);
				isTANInvalid = true;
			}
			else if (tdTAN.trim().length() != 10 || tdTAN.length() != 10)
			{
				//errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[7] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3014);
				errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[7] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3067);
				isTANInvalid = true;
			}
			else if (tdTAN.trim().length() == 10 || tdTAN.length() == 10)
			{
				if ((objRecVal.isAlphaNum(tdTAN) && ! objRecVal.checkTanAgainstConstants(tdTAN)) || (objRecVal.checkTan(tdTAN) && ! objRecVal.checkTanAgainstConstants(tdTAN)))
				{
					//errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[7] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3002);
					errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[7] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3067);
					isTANInvalid = true;
				}
				// Added by faizan for FVU 1.4
				else if(!tdRevMode.equals(TBAF_REVISION_MODE_DEL) && (tdTAN.equals("TANINVALID") || tdTAN.equals("TANAPPLIED") || tdTAN.equals("TANNOTAVBL") || isTANInvalid) )
				{
					errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[7] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3067);
				}
				else
				{
					try
					{
						if(! objRecVal.checkTanAgainstConstants(tdTAN))
						{
							//	Add TAN to HashSet.
							objReadFVAL3.hashSetTDTAN.add(tdTAN);
							objReadFVAL3.countValidTan.add(tdTAN);
						}
						else
						{
							if(tdTAN.equals("TANINVALID"))
							{
								cBeanTD.setCountTANINVALID(cBeanTD.getCountTANINVALID()+1);
							}
							else if(tdTAN.equals("TANNOTAVBL"))
							{
								cBeanTD.setCountTANNOTAVBL(cBeanTD.getCountTANNOTAVBL()+1);
							}
							else if(tdTAN.equals("TANAPPLIED"))
							{
								cBeanTD.setCountTANAPPLIED(cBeanTD.getCountTANAPPLIED()+1);
							}
						}
					}
					catch (Exception e)
					{
						/*NO ERROR LOGGED*/
					}
				}
			}	//	End of DDO TAN Validation

			if(objReadFVAL3.stmtType.equals(TBAF_TYPE_OF_STMT_CORR) && objReadFVAL3.transType.equals(TBAF_TRANSACTION_TYPE_M) && tdRevMode.equals(TBAF_REVISION_MODE_UPDATE))
			{
				if(isTANInvalid==true && isLastTANInvalid==false)
				{
					errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[7] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3064);
				}
			}



			/**
			 *	Validation of DDO NAME(Field No. 8 of DDO Transaction Detail Record) 
			 * 	
			 *	For C2 and C3 Correction DDO Name should be NULL if Revision Mode is "DEL".
			 * 	For Regular and Correction Statement if Revision Mode is "ADD" this field is MANDATORY.
			 *	Name should be of length less than or equal to 75 characters.
			 *	No tab spaces should be specified.
			 */

			if (objReadFVAL3.stmtType.equals(TBAF_TYPE_OF_STMT_CORR) && tdRevMode.equals(TBAF_REVISION_MODE_DEL))
			{
				if (!tdDDOName.equals(TBAF_FIELD_NULL) && !tdDDOName.equals(TBAF_FIELD_SEPERATOR))
				{
					errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[8] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3015);
				}
			}
			else if (objReadFVAL3.stmtType.equals(TBAF_TYPE_OF_STMT_ORIG) 
					|| (objReadFVAL3.stmtType.equals(TBAF_TYPE_OF_STMT_CORR) 
							&& (tdRevMode.equals(TBAF_REVISION_MODE_ADD) ||tdRevMode.equals(TBAF_REVISION_MODE_UPDATE)) ))
			{ 
				if (tdDDOName.equals(TBAF_FIELD_NULL) || tdDDOName.equals(TBAF_FIELD_SEPERATOR))
				{
					errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[8] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3001);  // New Error added in jan16 changed by puja
				}
				else if (objRecVal.isFieldNull(tdDDOName))
				{ 
					errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[8] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3002); // New Error added in jan16 changed by puja
				}
				else if (tdDDOName.length() > 75)
				{
					errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[8] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3005);
				}
				else if (! objRecVal.checkValidAOName(tdDDOName))
				{
					errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[8] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3002); // New Error added in jan16 changed by puja
				}
			}	//	End of DDO NAME Validation

			/**	
			 * Validation of ADDRESS FIELDS IN DDO Transaction Detail Record
			 * 
			 * Validation for Regular Statement, C4 Correction and C2, C3 Correction if Revision Mode is "ADD". 
			 * Address 1 is MANDATORY.
			 * Length should be less than or equal to 25 characters.
			 * Value should not be specified with TAB spaces.
			 * This field is not applicable from FY 2026-27, CR 89435 FVU 1.9
			 */
			if (Integer.parseInt(objReadFVAL3.finYear) < 2026 && (objReadFVAL3.stmtType.equals(TBAF_TYPE_OF_STMT_ORIG) //Gauri added FY condition for CR 89435, FVU 1.9
					|| (objReadFVAL3.stmtType.equals(TBAF_TYPE_OF_STMT_CORR) 
							&& (tdRevMode.equals(TBAF_REVISION_MODE_ADD) ||tdRevMode.equals(TBAF_REVISION_MODE_UPDATE)))))
			{
				//	Validation of ADDRESS 1(Field No. 9 Of DDO Transaction Detail Record)
				if (tdDDOAddress1.equals(TBAF_FIELD_NULL) || tdDDOAddress1.equals(TBAF_FIELD_SEPERATOR))
				{
					errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[9] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3001);  // New Error added in jan16 changed by puja
				}
				else if (objRecVal.isFieldNull(tdDDOAddress1))
				{
					errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[9] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3002); // New Error added in jan16 changed by puja
				}
				else if (tdDDOAddress1.length() > 25)
				{
					errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[9] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3005);
				}
				else if (! objRecVal.checkValidAOAddress(tdDDOAddress1))
				{
					errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[9] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3002); // New Error added in jan16 changed by puja
				}

				/**
				 *	DDO Address 2, DDO Address 3, DDO Address 4 are OPTIONAL fields.
				 * Following validations are done when the user DOES NOT SPECIFY ANY VALUE in this field:
				 *	
				 *	(1)	Check for only TAB spaces are specified.
				 *	(2)	Check if greater than 25 blank spaces are specified.
				 *
				 *	Following validations are done when the user SPECIFIES ANY VALUE in this field:
				 *	
				 *	(1)	Check if the specified value is of length less than 25 characters.
				 *	(2)	Check if the specified value is not having TAB spaces.
				 */

				//	Validation of ADDRESS 2(Field No. 10 Of DDO Transaction Detail Record)		
				if (tdDDOAddress2.equals(TBAF_FIELD_NULL) || tdDDOAddress2.equals(TBAF_FIELD_SEPERATOR))
				{
					// Optional Field, No Error Checking.
				}
				else if (objRecVal.isFieldNull(tdDDOAddress2))
				{
					if(objRecVal.checkTabSpaces(tdDDOAddress2))
					{
						errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[10] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3002); // New error added in jan16 changed by puja
						
					}
					else if (tdDDOAddress2.length() > 25)
					{
						errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[10] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3005);
					}
				}
				else
				{ 
					if (tdDDOAddress2.length() > 25)
					{
						errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[10] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3005);
					}
					else if (! objRecVal.checkValidAOAddress(tdDDOAddress2))
					{
						errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[10] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3002); // New error added in jan16 changed by puja
					}
				}	

				//	Validation of ADDRESS 3(Field No. 11 Of DDO Transaction Detail Record)
				if (tdDDOAddress3.equals(TBAF_FIELD_NULL) || tdDDOAddress3.equals(TBAF_FIELD_SEPERATOR))
				{
					// Optional Field, No Error Checking.
				}
				else if (objRecVal.isFieldNull(tdDDOAddress3))
				{
					if(objRecVal.checkTabSpaces(tdDDOAddress3))
					{
						errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[11] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3002);  // New Error code added in jan16 changed by puja
					}
					else if (tdDDOAddress3.length() > 25)
					{
						errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[11] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3005);
					}
				}
				else
				{ 
					if (tdDDOAddress3.length() > 25)
					{
						errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[11] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3005);
					}
					else if (! objRecVal.checkValidAOAddress(tdDDOAddress3))
					{
						errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[11] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3002);  // New Error code added in jan16 changed by puja
					}
				}	

				//	Validation of ADDRESS 4(Field No. 12 Of DDO Transaction Detail Record)
				if (tdDDOAddress4.equals(TBAF_FIELD_NULL) || tdDDOAddress4.equals(TBAF_FIELD_SEPERATOR))
				{
					// Optional Field, No Error Checking.
				}
				else if (objRecVal.isFieldNull(tdDDOAddress4))
				{
					if(objRecVal.checkTabSpaces(tdDDOAddress4))
					{
						errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[12] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3002); // New Error code added in jan16 changed by puja
					}
					else if (tdDDOAddress4.length() > 25)
					{
						errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[12] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3005);
					}
				}
				else
				{ 
					if (tdDDOAddress4.length() > 25)
					{
						errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[12] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3005);
					}
					else if (! objRecVal.checkValidAOAddress(tdDDOAddress4))
					{
						errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[12] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3002); // New Error code added in jan16 changed by puja
					}
				}	
			}

			//	For C2 and C3 Correction all the 4 DDO Address Fields should be NULL if Revision Mode is "DEL"
			else if (Integer.parseInt(objReadFVAL3.finYear) < 2026 && (objReadFVAL3.stmtType.equals(TBAF_TYPE_OF_STMT_CORR) && tdRevMode.equals(TBAF_REVISION_MODE_DEL)))//Gauri added FY condition for CR 89435, FVU 1.9
			{
				//	Validation of ADDRESS 1(Field No. 9 Of DDO Transaction Detail Record)
				if (!tdDDOAddress1.equals(TBAF_FIELD_NULL) && !tdDDOAddress1.equals(TBAF_FIELD_SEPERATOR))
				{
					errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[9] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3015);
				}
				//	Validation of ADDRESS 2(Field No. 10 Of DDO Transaction Detail Record)	
				if (!tdDDOAddress2.equals(TBAF_FIELD_NULL) && !tdDDOAddress2.equals(TBAF_FIELD_SEPERATOR))
				{
					errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[10] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3015);
				}
				//	Validation of ADDRESS 3(Field No. 11 Of DDO Transaction Detail Record)	
				if (!tdDDOAddress3.equals(TBAF_FIELD_NULL) && !tdDDOAddress3.equals(TBAF_FIELD_SEPERATOR))
				{
					errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[11] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3015);
				}
				//	Validation of ADDRESS 4(Field No. 12 Of DDO Transaction Detail Record)	
				if (!tdDDOAddress4.equals(TBAF_FIELD_NULL) && !tdDDOAddress4.equals(TBAF_FIELD_SEPERATOR))
				{
					errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[12] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3015);
				}
			}
			//Gauri added else if condition for CR 89435, FVU 1.9
			else if(Integer.parseInt(objReadFVAL3.finYear) >= 2026) {
				if(!tdDDOAddress2.equals(TBAF_FIELD_NULL) && !tdDDOAddress2.equals(TBAF_FIELD_SEPERATOR)) 
				{
					errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[12] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_5058);
				}
				
			}
			//	End of ADDRESS FIELDS Validation

			/**
			 * Validation of DDO CITY(Field No. 13 Of DDO Transaction Detail Record)
			 * 
			 *  DDO City Name is MANDATORY for Regular.
			 *	Length should be less than 25 characters.
			 *	City name validations added on "21-July-2006".
			 *This field is not applicable from FY 2026-27, CR 89435, FVU 1.9
			 */
			if (Integer.parseInt(objReadFVAL3.finYear) < 2026 && (objReadFVAL3.stmtType.equals(TBAF_TYPE_OF_STMT_ORIG) || (objReadFVAL3.stmtType.equals(TBAF_TYPE_OF_STMT_CORR) //Gauri added FY condition for CR 89435, FVU 1.9
					&& (tdRevMode.equals(TBAF_REVISION_MODE_ADD) ||tdRevMode.equals(TBAF_REVISION_MODE_UPDATE)))))
			{
				if (tdDDOCity.equals(TBAF_FIELD_NULL) || tdDDOCity.equals(TBAF_FIELD_SEPERATOR))
				{
					errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[13] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3001);  // New Erro code added in jan16 changed by puja
				}
				else if (objRecVal.isFieldNull(tdDDOCity))
				{
					errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[13] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3002);  // New Erro code added in jan16 changed by puja
				}
				else if (tdDDOCity.length() > 25)
				{
					errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[13] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3005);
				}
				else if(tdDDOCity.length() != tdDDOCity.trim().length())   //Added By Subhankar as per new Client requirements
				{
					errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[13] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3052);
				}
				else if (objRecVal.isValidCityName(tdDDOCity))
				{
					errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[13] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3002);  // New Erro code added in jan16 changed by puja
				}
			}

			/**
			 *	For C4 Correction and C2,C3 Correction if Revision Mode is "ADD" DDO City Name is Optional	
			 *	Following validations are done when the user DOES NOT SPECIFY ANY VALUE in this field:
			 *	
			 *	(1)	Check for only TAB spaces are specified.
			 *	(2)	Check if greater than 25 blank spaces are specified.
			 *
			 *	Following validations are done when the user SPECIFIES ANY VALUE in this field:
			 *	
			 *	(1)	Check if the specified value is of length less than 25 characters.
			 *	(2)	Check if the specified value is not having TAB spaces.
			 */ 

			//Commented on 31st Oct as No Revision Mode other than A and D

			/*	else if (objReadFVAL3.stmtType.equals(TBAF_TYPE_OF_STMT_CORR))
			{
				if(objReadFVAL3.transType.equals(TBAF_TRANSACTION_TYPE_C4) 
				   || (objReadFVAL3.transType.equals(TBAF_TRANSACTION_TYPE_C2) 
				   	   || objReadFVAL3.transType.equals(TBAF_TRANSACTION_TYPE_C3) 
				   	   && tdRevMode.equals(TBAF_REVISION_MODE_ADD)))	
				{	   
					if (tdDDOCity.equals(TBAF_FIELD_NULL) || tdDDOCity.equals(TBAF_FIELD_SEPERATOR))
					{
						// Optional Field, No Error Checking.
					}
					else if (objRecVal.isFieldNull(tdDDOCity))
					{
						if(objRecVal.checkTabSpaces(tdDDOCity))
						{
							errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[13] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3002);
						}
						else if (tdDDOCity.length() > 25)
						{
							errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[13] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3005);
						}
					}
					else
					{
						if (tdDDOCity.length() > 25)
						{
							errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[13] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3005);
						}
						else if (objRecVal.isValidCityName(tdDDOCity))
						{
							errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[13] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3002);
						}
					}	
				}
			}	 */
			//	For C2 and C3 Correction DDO City should be NULL if Revision Mode is "DEL" 
			else if (Integer.parseInt(objReadFVAL3.finYear) < 2026 && (objReadFVAL3.stmtType.equals(TBAF_TYPE_OF_STMT_CORR) && tdRevMode.equals(TBAF_REVISION_MODE_DEL))) 	//Gauri added FY condition for CR 89435, FVU 1.9
			{
				if (!tdDDOCity.equals(TBAF_FIELD_NULL) && !tdDDOCity.equals(TBAF_FIELD_SEPERATOR))
				{
					errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[13] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3015);
				}
			}	
			//Gauri added else if block for CR 89435, FVU 1.9
			else if (Integer.parseInt(objReadFVAL3.finYear) >= 2026) {
				if(!tdDDOCity.equals(TBAF_FIELD_NULL) && !tdDDOCity.equals(TBAF_FIELD_SEPERATOR)) {
					errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[13] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_5058);
				}
				
			}
			//	End of DDO CITY Validation

			/**
			 *	Validation of DDO STATE CODE(Field No. 14 Of DDO Transaction Detail Record)
			 * 
			 *	This field is MANDATORY for Regular, C4 Correction, and C2, C3 Correction when Revision Mode is "ADD".  
			 *  State Code should have only 2-digits.
			 *	State code should have a value between 01 and 35.  
			 *  This field is not applicable from FY 2026-27, CR 89435 FVU 1.9
			 */
			if (Integer.parseInt(objReadFVAL3.finYear) < 2026 && (objReadFVAL3.stmtType.equals(TBAF_TYPE_OF_STMT_ORIG)//Gauri added FY condition for CR 89435, FVU 1.9
					|| (objReadFVAL3.stmtType.equals(TBAF_TYPE_OF_STMT_CORR) 
							&& (tdRevMode.equals(TBAF_REVISION_MODE_ADD) ||tdRevMode.equals(TBAF_REVISION_MODE_UPDATE)))))
				//	|| objReadFVAL3.transType.equals(TBAF_TRANSACTION_TYPE_C4)))
			{
				if (tdDDOStateCode.equals(TBAF_FIELD_NULL) || tdDDOStateCode.equals(TBAF_FIELD_SEPERATOR))
				{
					errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[14] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3001);
				}
				else if (objRecVal.isFieldNull(tdDDOStateCode))
				{
					errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[14] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3002);
				}
				else if (tdDDOStateCode.length() > 2)
				{
					errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[14] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3016);
				}
				else if(tdDDOStateCode.length() != tdDDOStateCode.trim().length())
				{
					errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[14] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3048);
				}
				else if (objRecVal.isInt(tdDDOStateCode))
				{
					errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[14] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3002);
				}
				// Code for validation between 01 and 35.
				//changed by amit//Changes added for TBAF FVU 1.6 version by puja //24GFVU 1.7 Changes for state code 08
				else if (Integer.parseInt(tdDDOStateCode.trim()) > 37 || Integer.parseInt(tdDDOStateCode.trim()) < 1 || Integer.parseInt(tdDDOStateCode.trim()) == 8) 
				{
					errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[14] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3017);
				}
			}
			//	For C2 and C3 Correction DDO State Code should be NULL if Revision Mode is "DEL" 
			else if (Integer.parseInt(objReadFVAL3.finYear) < 2026 && (objReadFVAL3.stmtType.equals(TBAF_TYPE_OF_STMT_CORR) && tdRevMode.equals(TBAF_REVISION_MODE_DEL))) //Gauri added FY condition for CR 89435, FVU 1.9
			{
				if (!tdDDOStateCode.equals(TBAF_FIELD_NULL) && !tdDDOStateCode.equals(TBAF_FIELD_SEPERATOR))
				{
					errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[14] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3015);
				}
			}	
			//Gauri added the else if condition for CR 89435, FVU 1.9
			else if (Integer.parseInt(objReadFVAL3.finYear) >= 2026) {
				if(!tdDDOStateCode.equals(TBAF_FIELD_NULL) && !tdDDOStateCode.equals(TBAF_FIELD_SEPERATOR)) {
					errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[14] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_5058);
				}
				
			}
			
			//	End of DDO STATE CODE Validation

			/**
			 *	Validation of DDO PIN CODE(Field No. 15 Of DDO Transaction Detail Record) 
			 *	
			 *	This field is MANDATORY for Regular, C4 Correction, and C2, C3 Correction when Revision Mode is "ADD".
			 *	Validation for Correction Statement if Revision Mode is "ADD".
			 * 	PIN Code should be of length exactly equal to 6 digits.
			 *	PIN Code should be greater than 110001.
			 *  This field is not applicable from FY 2026-27, CR 89435, FVU 1.9
			 */
			if (Integer.parseInt(objReadFVAL3.finYear) < 2026 && (objReadFVAL3.stmtType.equals(TBAF_TYPE_OF_STMT_ORIG)	//Gauri added the FY condition for CR 89435, FVU 1.9
					|| (objReadFVAL3.stmtType.equals(TBAF_TYPE_OF_STMT_CORR) 
							&& (tdRevMode.equals(TBAF_REVISION_MODE_ADD) ||tdRevMode.equals(TBAF_REVISION_MODE_UPDATE)))))
				//	|| objReadFVAL3.transType.equals(TBAF_TRANSACTION_TYPE_C4)))
			{
				if (tdDDOPinCode.equals(TBAF_FIELD_NULL) || tdDDOPinCode.equals(TBAF_FIELD_SEPERATOR))
				{
					errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[15] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3001);  // New Error code  added in Jan16 changed by puja
				}
				else if (objRecVal.isFieldNull(tdDDOPinCode))
				{
					errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[15] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3002); // New Error code  added in Jan16  changed by puja
				}
				else if (tdDDOPinCode.trim().length() != 6 || tdDDOPinCode.length() != 6)
				{
					errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[15] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3018);
				}
				else if (objRecVal.isInt(tdDDOPinCode))
				{
					errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[15] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3002); // New Error code  added in Jan16  changed by puja
				}
				else if(Integer.parseInt(tdDDOPinCode) == 999999)
				{
					errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[15] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3043);
				}

				else if (Integer.parseInt(tdDDOPinCode.trim()) < 110001)
				{
					errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[15] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3018);
				}
			}
			//	For C2 and C3 Correction DDO Pin Code should be NULL if Revision Mode is "DEL" 
			//Gauri added FY condition for CR 89435, FVU 1.9
			else if (Integer.parseInt(objReadFVAL3.finYear) < 2026 && (objReadFVAL3.stmtType.equals(TBAF_TYPE_OF_STMT_CORR) && tdRevMode.equals(TBAF_REVISION_MODE_DEL)))
			{
				if (!tdDDOPinCode.equals(TBAF_FIELD_NULL) && !tdDDOPinCode.equals(TBAF_FIELD_SEPERATOR))
				{
					errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[15] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3015);
				}
			}	
			//Gauri added else if condition for CR 89435, FVU 1.9
			else if (Integer.parseInt(objReadFVAL3.finYear) >= 2026) {
				if(!tdDDOPinCode.equals(TBAF_FIELD_NULL) && !tdDDOPinCode.equals(TBAF_FIELD_SEPERATOR)) {
					errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[15] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_5058);
				}
				
			}
			
			//	End of DDO PIN CODE Validation

			/**
			 *	Validation of TAX AMOUNT(Field No. 16 Of DDO Transaction Detail Record)
			 * 
			 *	This field is MANDATORY irrespective of Statement Types and revision Modes.
			 *	Tax Amount should be a decimal number of length less than or equal to 15 digits.
			 *	Tax Amount should have "00" in the decimal part.
			 *	Tax amount should not be Negative.
			 */

			if (objReadFVAL3.stmtType.equals(TBAF_TYPE_OF_STMT_ORIG)
					|| (objReadFVAL3.stmtType.equals(TBAF_TYPE_OF_STMT_CORR) 
							&& (tdRevMode.equals(TBAF_REVISION_MODE_UPDATE) || tdRevMode.equals(TBAF_REVISION_MODE_ADD)))) 
			{

				if( tdDDOMapping.trim().equals("D")  && ! taxAmt.equals("0.00"))   //Added By Subhankar(As When DDO Mapping/Update Flag is 'D' then 0.00 should be allowed in TDS/TCS Deducted Amt Field)  
				{

					errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[16] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3038);
				}

				else if (! (tdDDOMapping.trim().equals("D")))
				{
					if (taxAmt.equals(TBAF_FIELD_NULL) || taxAmt.equals(TBAF_FIELD_SEPERATOR))
					{
						objReadFVAL3.invalidTaxAmt = true;
						errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[16] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3001);  // New Error added in Jan16 changed by puja
					}
					else if (objRecVal.isFieldNull(taxAmt))
					{
						objReadFVAL3.invalidTaxAmt = true;
						errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[16] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3002);
					}
					else if (taxAmt.trim().length() > 15 || taxAmt.length() > 15)
					{
						objReadFVAL3.invalidTaxAmt = true;
						errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[16] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3005);
					}
					else if (taxAmt.trim().length() != taxAmt.length())  //Added by Subhankar to detect leading and trailing spaces.
					{
						objReadFVAL3.invalidTaxAmt = true;
						errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[16] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3002); // New Error added in Jan16 changed by puja
					}
					else if (objReadFVAL3.stmtType.equals(TBAF_TYPE_OF_STMT_ORIG)
							|| (objReadFVAL3.stmtType.equals(TBAF_TYPE_OF_STMT_CORR) 
									&& objReadFVAL3.transType.equals(TBAF_TRANSACTION_TYPE_C4)) || (objReadFVAL3.stmtType.equals(TBAF_TYPE_OF_STMT_CORR) && tdRevMode.equals(TBAF_REVISION_MODE_ADD)) 
									|| (objReadFVAL3.stmtType.equals(TBAF_TYPE_OF_STMT_CORR) && objReadFVAL3.transType.equals(TBAF_TRANSACTION_TYPE_M) && tdRevMode.equals(TBAF_REVISION_MODE_UPDATE)))
					{
						if (objRecVal.isDecimalNumber(taxAmt) || !taxAmt.endsWith("00"))
						{
							if(objRecVal.isInt(taxAmt))
							{
								objReadFVAL3.invalidTaxAmt = true;
								errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[16] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3002); // New Error added in Jan16  changed by puja
							}
							else
							{
								objReadFVAL3.invalidTaxAmt = true;
								errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[16] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3022);
							}
						}
						else if (Double.parseDouble(taxAmt.trim()) < 0.00)
						{
							objReadFVAL3.invalidTaxAmt = true;
							errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[16] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3002);  // New Error added in Jan16 changed by puja
						}
						else
						{
							//	Add the individual tax amounts in each TD Record.
							
						   if(tdRevMode.equals(TBAF_REVISION_MODE_UPDATE))
						   {
							   if(!tdLastTaxAmt.equals("^"))
							   {
								   objReadFVAL3.totalTaxAdded = objReadFVAL3.totalTaxAdded + Double.parseDouble(taxAmt.trim())-Double.parseDouble(tdLastTaxAmt.trim());
							   }
							   
						   }
						   else
						   {
							   objReadFVAL3.totalTaxAdded = objReadFVAL3.totalTaxAdded + Double.parseDouble(taxAmt.trim());
						   }
							
						}
					}

					/**
					 *	Validation of Last Total TDS/TCS Remitted (Field No. 25 Of DDO Transaction Detail Record)
					 */

					if(objReadFVAL3.stmtType.equals(TBAF_TYPE_OF_STMT_CORR) && objReadFVAL3.transType.equals(TBAF_TRANSACTION_TYPE_M) && tdRevMode.equals(TBAF_REVISION_MODE_UPDATE))
					{



						if( (tdDDOMapping.trim().equals("D")) && ! tdLastRemittedAmt.equals("0.00"))    
						{
							errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[25] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3039);
						}

						else if(! (tdDDOMapping.trim().equals("D")))
						{


							if (tdLastRemittedAmt.equals(TBAF_FIELD_NULL) || tdLastRemittedAmt.equals(TBAF_FIELD_SEPERATOR))
							{
								objReadFVAL3.invalidLastRemittedAmt = true;
								errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[25] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3001);  // New Error added in Jan16 changed by puja
							}
							else if (objRecVal.isFieldNull(tdLastRemittedAmt))
							{
								objReadFVAL3.invalidLastRemittedAmt = true;
								errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[25] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3002);  // New Error added in Jan16  changed by puja
							}
							else if (tdLastRemittedAmt.trim().length() > 15 || tdLastRemittedAmt.length() > 15)
							{
								objReadFVAL3.invalidLastRemittedAmt = true;
								errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[25] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3005);
							}
							else if(tdLastRemittedAmt.trim().length() != tdLastRemittedAmt.length())
							{
								objReadFVAL3.invalidLastRemittedAmt = true;
								errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[25] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3002);

							}
							else if (objReadFVAL3.invalidLastRemittedAmt == false 
									&& objReadFVAL3.stmtType.equals(TBAF_TYPE_OF_STMT_CORR) 
									&& objReadFVAL3.transType.equals(TBAF_TRANSACTION_TYPE_M) )
							{
								if (objRecVal.isDecimalNumber(tdLastRemittedAmt) || !tdLastRemittedAmt.endsWith("00"))
								{
									if(objRecVal.isInt(tdLastRemittedAmt))
									{
										objReadFVAL3.invalidLastRemittedAmt = true;
										errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[25] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3002); // New Error added in Jan16  changed by puja
									}
									else
									{
										objReadFVAL3.invalidLastRemittedAmt = true;
										errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[25] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3053);
									}
								}
								else if (Double.parseDouble(tdLastRemittedAmt.trim()) < 0.00)
								{
									objReadFVAL3.invalidLastRemittedAmt = true;
									errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[25] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3002 );  // New Error added in Jan16  changed by puja
								} 
							}
						}
						
					}
					else if (!tdLastRemittedAmt.equals(TBAF_FIELD_NULL) && !tdLastRemittedAmt.equals(TBAF_FIELD_SEPERATOR))
					{
						errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[25] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3012);
					}


					//End of Nature Of DDO Last Total Remitted Amount Validation

					//Commented On 3rd Nov as for Correction Type M TD is allowed and for Revision Type A taxAmt is allowed in TD


					/*else if (objReadFVAL3.invalidTaxAmt == false 
							&& objReadFVAL3.stmtType.equals(TBAF_TYPE_OF_STMT_CORR) 
							&& !objReadFVAL3.transType.equals(TBAF_TRANSACTION_TYPE_C4)) */


					// Hashed on 07 November as Total Remitted amount has been considered as Verification Key not Total Tax amount

					/*	else if (objReadFVAL3.invalidTaxAmt == false 
							&& objReadFVAL3.stmtType.equals(TBAF_TYPE_OF_STMT_CORR) 
							&& objReadFVAL3.transType.equals(TBAF_TRANSACTION_TYPE_M) && tdRevMode.equals(TBAF_REVISION_MODE_ADD) )


					{



						if (tdRevMode.equals(TBAF_REVISION_MODE_ADD))
						{
							//	Add the individual tax amounts seperately for "ADD" Mode. 
							objReadFVAL3.totalTaxAdded = objReadFVAL3.totalTaxAdded + Double.parseDouble(taxAmt.trim());
						}
						else if (tdRevMode.equals(TBAF_REVISION_MODE_DEL))
						{
							//	Add the individual tax amounts seperately for "DEL" Mode.
							objReadFVAL3.totalTaxDeleted = objReadFVAL3.totalTaxDeleted + Double.parseDouble(taxAmt.trim());
						}
					} */
				}
			}
			else if(objReadFVAL3.stmtType.equals(TBAF_TYPE_OF_STMT_CORR) && tdRevMode.equals(TBAF_REVISION_MODE_DEL))
			{
				if (taxAmt.equals(TBAF_FIELD_NULL) || taxAmt.equals(TBAF_FIELD_SEPERATOR))
				{
					objReadFVAL3.invalidRecord = true;
					objReadFVAL3.invalidTaxAmt = true;
					errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[16] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3001);
				}
				else if (objRecVal.isFieldNull(taxAmt))
				{
					objReadFVAL3.invalidRecord = true;
					objReadFVAL3.invalidTaxAmt = true;
					errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[16] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3002);
				}
				else if (! "0.00".equals(taxAmt))
				{
					objReadFVAL3.invalidRecord = true;
					objReadFVAL3.invalidTaxAmt = true;
					errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[16] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3057);
				}
			}





			//	End of TAX AMOUNT Validation

			/**
			 *	Validation of MONTH & YEAR OF PAYMENT(Field No. 17 Of DDO Transaction Detail Record)
			 *
			 * 	Value should be numeric of length exactly equal to 6 digits.
			 * 	No leading amd trailing spaces should be specified. 
			 *	Month specified should  exactly come under the Quarter specified in the Batch Header. 
			 *
			 * 
			 * Now According to the new Validation (as Added by Subhankar) the Month and Year of Statement is now going to be a filler field.....

				Gauri added Form Type field for CR 89435, FVU 1.9
				
			 */

			// Start:: Gauri added validations of Form type for CR 89435, FVU 1.9
			
			
						if (Integer.parseInt(objReadFVAL3.finYear) >= 2026 && (objReadFVAL3.stmtType.equals(TBAF_TYPE_OF_STMT_ORIG) || objReadFVAL3.stmtType.equals(TBAF_TYPE_OF_STMT_CORR)))
						{
							if((tdDDOMapping.trim().equals("D")) && (! formType.equals(TBAF_FIELD_NULL) && ! formType.equals(TBAF_FIELD_SEPERATOR)))   
							{
								errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[17] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_5059);
							}

							else if(! (tdDDOMapping.trim().equals("D")))
							{

								if (formType.equals(TBAF_FIELD_NULL) || formType.equals(TBAF_FIELD_SEPERATOR))
								{
									errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[17] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3001);
								}
								else if (objRecVal.isFieldNull(formType))
								{
									errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[17] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3002);
								}
								else if (formType.trim().length() > TBAF_DDO_FORM_TYPE_LEN || formType.length() > TBAF_DDO_FORM_TYPE_LEN)  //Gauri added changes for Form Type for CR 89435.FVU 1.9
								{
									errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[17] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3005);
								}
								else if(formType.length() != formType.trim().length())
								{
									errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[17] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3048);
								}
								else if(! objRecVal.isValidFormType(formType))  //Gauri added changes for Form Type for CR 89435.FVU 1.9
								{
									errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[17] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_5065);
								}
								else
								{
									
									//For Correction Files with Transaction Type M whether combination of Mode,TAN & DDODeductionNature is unique or not


									
									if(objReadFVAL3.stmtType.equals(TBAF_TYPE_OF_STMT_CORR))
									{
										//If the TAN is a structurally valid one and not in one of the Three defined constants then the combination of TAN,MODE & NATURE OF DEDUCTION should be unique
										if(! objRecVal.checkTanAgainstConstants(tdTAN))
										{
											String modeTANNatOfDed = tdRevMode.trim() + tdTAN.trim() + formType ;
											if(! objReadFVAL3.corrTDConflict.add(modeTANNatOfDed))
											{
												errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[17] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3056);
											}

											if(! tdDDORegNo.equals(TBAF_FIELD_NULL) && ! tdDDORegNo.equals(TBAF_FIELD_SEPERATOR))
											{
												String modeDDORegNoDeductNat = tdRevMode.trim() + tdDDORegNo.trim() + formType;
												if (! objReadFVAL3.corrTDConflict.add(modeDDORegNoDeductNat))
												{
													errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[17] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_5060);
												}

											}
											if(! tdDDOCode.equals(TBAF_FIELD_NULL) && ! tdDDOCode.equals(TBAF_FIELD_SEPERATOR))
											{
												String modeDDOCodeDeductNat = tdRevMode.trim() + tdDDOCode.trim() + formType;
												if (! objReadFVAL3.corrTDConflict.add(modeDDOCodeDeductNat))
												{
													errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[17] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_5061);
												}

											}

										}
										
										else
										{
											if(! tdDDORegNo.equals(TBAF_FIELD_NULL) && ! tdDDORegNo.equals(TBAF_FIELD_SEPERATOR))
											{
												String modeDDORegNoDeductNat = tdRevMode.trim() + tdDDORegNo.trim() + formType;
												if (! objReadFVAL3.corrTDConflict.add(modeDDORegNoDeductNat))
												{
													errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[17] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_5060);
												}

											}
											if(! tdDDOCode.equals(TBAF_FIELD_NULL) && ! tdDDOCode.equals(TBAF_FIELD_SEPERATOR))
											{
												String modeDDOCodeDeductNat = tdRevMode.trim() + tdDDOCode.trim() + formType;
												if (! objReadFVAL3.corrTDConflict.add(modeDDOCodeDeductNat))
												{
													errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[17] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_5061);
												}

											}
										}
									}
									else if(objReadFVAL3.stmtType.equals(TBAF_TYPE_OF_STMT_ORIG)	)
									{
										//For Checking whether the tan or DDO Registration Number or DDO Code with the same form already exists more than one time in the file or not.
										if(! objRecVal.checkTanAgainstConstants(tdTAN))
										{
											String tanDeductNat = tdTAN.trim() + formType ;
											if (objReadFVAL3.hashSetMonthYear.contains(tanDeductNat))
											{
												errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[17] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_5062);
											}
											else
											{
												//	Add TAN & Deduction Nature or DDO Registration Number Deduction Nature or DDO Code and Deduction Nature into HashSet.
												objReadFVAL3.hashSetMonthYear.add(tanDeductNat);
											}
											if(! tdDDORegNo.equals(TBAF_FIELD_NULL) && ! tdDDORegNo.equals(TBAF_FIELD_SEPERATOR))
											{
												String ddoRegNoDeductNat = tdDDORegNo.trim() + formType ;
												if (objReadFVAL3.hashSetMonthYear.contains(ddoRegNoDeductNat))
												{
													errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[17] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_5063);
												}
												else
												{
													//	Add TAN & Deduction Nature or DDO Registration Number Deduction Nature or DDO Code and Deduction Nature into HashSet.
													objReadFVAL3.hashSetMonthYear.add(ddoRegNoDeductNat);
												}
											}
											if(! tdDDOCode.equals(TBAF_FIELD_NULL) && ! tdDDOCode.equals(TBAF_FIELD_SEPERATOR))
											{
												String ddoCodeDeductNat = tdDDOCode.trim() + formType;
												if (objReadFVAL3.hashSetMonthYear.contains(ddoCodeDeductNat))
												{
													errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[17] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_5064);
												}
												else
												{
													//	Add TAN & Deduction Nature or DDO Registration Number Deduction Nature or DDO Code and Deduction Nature into HashSet.
													objReadFVAL3.hashSetMonthYear.add(ddoCodeDeductNat);
												}
											}
										} 



										if(objRecVal.checkTanAgainstConstants(tdTAN))
										{
											if(! tdDDORegNo.equals(TBAF_FIELD_NULL) && ! tdDDORegNo.equals(TBAF_FIELD_SEPERATOR))
											{
												String ddoRegNoDeductNat = tdDDORegNo.trim() + formType ;
												if (objReadFVAL3.hashSetMonthYear.contains(ddoRegNoDeductNat))
												{
													errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[17] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_5063);
												}
												else
												{
													//	Add TAN & Deduction Nature or DDO Registration Number Deduction Nature or DDO Code and Deduction Nature into HashSet.
													objReadFVAL3.hashSetMonthYear.add(ddoRegNoDeductNat);
												}
											}
											if(! tdDDOCode.equals(TBAF_FIELD_NULL) && ! tdDDOCode.equals(TBAF_FIELD_SEPERATOR))
											{
												String ddoCodeDeductNat = tdDDOCode.trim() + formType;
												if (objReadFVAL3.hashSetMonthYear.contains(ddoCodeDeductNat))
												{
													errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[17] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_5064);
												}
												else
												{
													//	Add TAN & Deduction Nature or DDO Registration Number Deduction Nature or DDO Code and Deduction Nature into HashSet.
													objReadFVAL3.hashSetMonthYear.add(ddoCodeDeductNat);
												}
											}
										}
										/*	else
						    	{
						    		String tanDeductNat = tdTAN.trim() + tdDDODeductionNature ;
						    		if (objReadFVAL3.hashSetMonthYear.contains(tanDeductNat))
									 {
										errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[22] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3034);
									 }
									else
									 {
										//	Add TAN & Deduction Nature or DDO Registration Number Deduction Nature or DDO Code and Deduction Nature into HashSet.
										objReadFVAL3.hashSetMonthYear.add(tanDeductNat);
									 }
						    	}*/


									}
										
										
										double tAmt = 0.00;
										double tLastAmt=0.00;
										double remAmt = 0.00;
										double lastRemAmt=0.00;
										try{
											
											tAmt = Double.parseDouble(taxAmt);
										
											remAmt = Double.parseDouble(remittedAmt);
											if(tdRevMode.equals(TBAF_REVISION_MODE_UPDATE))
											{
											lastRemAmt=Double.parseDouble(tdLastRemittedAmt);
											tLastAmt=Double.parseDouble(tdLastTaxAmt);
											}
										}
										catch(Exception e)
										{
											Log.tbaf_log.error("Exception:",e);
											tAmt = 0.00;
											remAmt = 0.00;
										}
										
										
										//Gauri changed form type for F138 to keep track of Amounts(Total TAX and Remitted) for added and deleted TD
										if(FORM_TYPE[0].equals(formType))
										{
												if(objReadFVAL3.stmtType.equals(TBAF_TYPE_OF_STMT_CORR))
											{
												if(tdRevMode.equals(TBAF_REVISION_MODE_ADD))
												{
													cBeanTD.setTotalTDAddedIn24Q(cBeanTD.getTotalTDAddedIn24Q()+1);
													cBeanTD.setTotalTaxAddedTD24Q(cBeanTD.getTotalTaxAddedTD24Q()+tAmt);
													cBeanTD.setRemittedAmtAddedTD24Q(cBeanTD.getRemittedAmtAddedTD24Q()+remAmt);
												}
												else if(tdRevMode.equals(TBAF_REVISION_MODE_DEL))
												{
													cBeanTD.setTotalTDDeletedIn24Q(cBeanTD.getTotalTDDeletedIn24Q()+1);
													cBeanTD.setTotalTaxDeletedTD24Q(cBeanTD.getTotalTaxDeletedTD24Q()+tAmt);
													cBeanTD.setRemittedAmtDeletedTD24Q(cBeanTD.getRemittedAmtDeletedTD24Q()+remAmt);
												}
												else if(tdRevMode.equals(TBAF_REVISION_MODE_UPDATE))
												{
													cBeanTD.setTotalTDUpdatedIn24Q(cBeanTD.getTotalTDUpdatedIn24Q()+1);
													if(formType.equals(tdLastDDODeductionNature))
													{
														cBeanTD.setTotalTaxUpdatedTD24Q(cBeanTD.getTotalTaxUpdatedTD24Q()+(tAmt-tLastAmt));
														cBeanTD.setRemittedAmtUpdatedTD24Q(cBeanTD.getRemittedAmtUpdatedTD24Q()+(remAmt-lastRemAmt));
													}
													else
													{
														cBeanTD.setTotalTaxUpdatedTD24Q(cBeanTD.getTotalTaxUpdatedTD24Q()+tAmt);
														cBeanTD.setRemittedAmtUpdatedTD24Q(cBeanTD.getRemittedAmtUpdatedTD24Q()+remAmt);	
													}
												}
											}
											else
											{
												
												cBeanTD.setTotalTaxAddedTD24Q(cBeanTD.getTotalTaxAddedTD24Q()+tAmt);
												cBeanTD.setRemittedAmtAddedTD24Q(cBeanTD.getRemittedAmtAddedTD24Q()+remAmt);
											}
										}
										
										//Gauri changed form type for F140 to keep track of Amounts(Total TAX and Remitted) for added and deleted TD
										if(FORM_TYPE[1].equals(formType))
										{
											if(objReadFVAL3.stmtType.equals(TBAF_TYPE_OF_STMT_CORR))
											{
										
												if(tdRevMode.equals(TBAF_REVISION_MODE_ADD))
												{
													cBeanTD.setTotalTDAddedIn26Q(cBeanTD.getTotalTDAddedIn26Q()+1);
													cBeanTD.setTotalTaxAddedTD26Q(cBeanTD.getTotalTaxAddedTD26Q()+tAmt);
													cBeanTD.setRemittedAmtAddedTD26Q(cBeanTD.getRemittedAmtAddedTD26Q()+remAmt);
												}
												else if(tdRevMode.equals(TBAF_REVISION_MODE_DEL))
												{
													cBeanTD.setTotalTDDeletedIn26Q(cBeanTD.getTotalTDDeletedIn26Q()+1);
													cBeanTD.setTotalTaxDeletedTD26Q(cBeanTD.getTotalTaxDeletedTD26Q()+tAmt);
													cBeanTD.setRemittedAmtDeletedTD26Q(cBeanTD.getRemittedAmtDeletedTD26Q()+remAmt);
												}
												else if(tdRevMode.equals(TBAF_REVISION_MODE_UPDATE))
												{
													cBeanTD.setTotalTDUpdatedIn26Q(cBeanTD.getTotalTDUpdatedIn26Q()+1);
													if(formType.equals(tdLastDDODeductionNature))
													{
														cBeanTD.setTotalTaxUpdatedTD26Q(cBeanTD.getTotalTaxUpdatedTD26Q()+(tAmt-tLastAmt));
														cBeanTD.setRemittedAmtUpdatedTD26Q(cBeanTD.getRemittedAmtUpdatedTD26Q()+(remAmt-lastRemAmt));
													}
													else
													{
														cBeanTD.setTotalTaxUpdatedTD26Q(cBeanTD.getTotalTaxUpdatedTD26Q()+tAmt);
														cBeanTD.setRemittedAmtUpdatedTD26Q(cBeanTD.getRemittedAmtUpdatedTD26Q()+remAmt);
													}
												}
											}
											else
											{
												cBeanTD.setTotalTaxAddedTD26Q(cBeanTD.getTotalTaxAddedTD26Q()+tAmt);
												cBeanTD.setRemittedAmtAddedTD26Q(cBeanTD.getRemittedAmtAddedTD26Q()+remAmt);
											}
										}
													
									
										
										//Gauri changed form type for F144 27Q to keep track of Amounts(Total TAX and Remitted) for added and deleted TD
									if(FORM_TYPE[2].equals(formType))
									{
										if(objReadFVAL3.stmtType.equals(TBAF_TYPE_OF_STMT_CORR))
										{
											if(tdRevMode.equals(TBAF_REVISION_MODE_ADD))
											{
												cBeanTD.setTotalTDAddedIn27Q(cBeanTD.getTotalTDAddedIn27Q()+1);
												cBeanTD.setTotalTaxAddedTD27Q(cBeanTD.getTotalTaxAddedTD27Q()+tAmt);
												cBeanTD.setRemittedAmtAddedTD27Q(cBeanTD.getRemittedAmtAddedTD27Q()+remAmt);
											}
											else if(tdRevMode.equals(TBAF_REVISION_MODE_DEL))
											{
												cBeanTD.setTotalTDDeletedIn27Q(cBeanTD.getTotalTDDeletedIn27Q()+1);
												cBeanTD.setTotalTaxDeletedTD27Q(cBeanTD.getTotalTaxDeletedTD27Q()+tAmt);
												cBeanTD.setRemittedAmtDeletedTD27Q(cBeanTD.getRemittedAmtDeletedTD27Q()+remAmt);
											}
											else if(tdRevMode.equals(TBAF_REVISION_MODE_UPDATE))
											{
												cBeanTD.setTotalTDUpdatedIn27Q(cBeanTD.getTotalTDUpdatedIn27Q()+1);
												if(formType.equals(tdLastDDODeductionNature))
												{
												cBeanTD.setTotalTaxUpdatedTD27Q(cBeanTD.getTotalTaxUpdatedTD27Q()+(tAmt-tLastAmt));
												cBeanTD.setRemittedAmtUpdatedTD27Q(cBeanTD.getRemittedAmtUpdatedTD27Q()+(remAmt-lastRemAmt));
												}
												else
												{
													cBeanTD.setTotalTaxUpdatedTD27Q(cBeanTD.getTotalTaxUpdatedTD27Q()+tAmt);
													cBeanTD.setRemittedAmtUpdatedTD27Q(cBeanTD.getRemittedAmtUpdatedTD27Q()+remAmt);
												}
											}
										}
									else
									{
										cBeanTD.setTotalTaxAddedTD27Q(cBeanTD.getTotalTaxAddedTD27Q()+tAmt);
										cBeanTD.setRemittedAmtAddedTD27Q(cBeanTD.getRemittedAmtAddedTD27Q()+remAmt);
									}
								}
										
										
										//Gauri changed form type for F143 to keep track of Amounts(Total TAX and Remitted) for added and deleted TD
									if(FORM_TYPE[3].equals(formType))
									{
										if(objReadFVAL3.stmtType.equals(TBAF_TYPE_OF_STMT_CORR))
										{
											if(tdRevMode.equals(TBAF_REVISION_MODE_ADD))
											{
												cBeanTD.setTotalTDAddedIn27EQ(cBeanTD.getTotalTDAddedIn27EQ()+1);
												cBeanTD.setTotalTaxAddedTD27EQ(cBeanTD.getTotalTaxAddedTD27EQ()+tAmt);
												cBeanTD.setRemittedAmtAddedTD27EQ(cBeanTD.getRemittedAmtAddedTD27EQ()+remAmt);
											}
											else if(tdRevMode.equals(TBAF_REVISION_MODE_DEL))
											{
												cBeanTD.setTotalTDDeletedIn27EQ(cBeanTD.getTotalTDDeletedIn27EQ()+1);
												cBeanTD.setTotalTaxDeletedTD27EQ(cBeanTD.getTotalTaxDeletedTD27EQ()+tAmt);
												cBeanTD.setRemittedAmtDeletedTD27EQ(cBeanTD.getRemittedAmtDeletedTD27EQ()+remAmt);
											}
											else if(tdRevMode.equals(TBAF_REVISION_MODE_UPDATE))
											{
												cBeanTD.setTotalTDUpdatedIn27EQ(cBeanTD.getTotalTDUpdatedIn27EQ()+1);
												if(formType.equals(tdLastDDODeductionNature))
												{
												cBeanTD.setTotalTaxUpdatedTD27EQ(cBeanTD.getTotalTaxUpdatedTD27EQ()+(tAmt-tLastAmt));
												cBeanTD.setRemittedAmtUpdatedTD27EQ(cBeanTD.getRemittedAmtUpdatedTD27EQ()+(remAmt-lastRemAmt));
												}
												else
												{
													cBeanTD.setTotalTaxUpdatedTD27EQ(cBeanTD.getTotalTaxUpdatedTD27EQ()+tAmt);
													cBeanTD.setRemittedAmtUpdatedTD27EQ(cBeanTD.getRemittedAmtUpdatedTD27EQ()+remAmt);
												}
											}
										}
										else
										{
											cBeanTD.setTotalTaxAddedTD27EQ(cBeanTD.getTotalTaxAddedTD27EQ()+tAmt);
											cBeanTD.setRemittedAmtAddedTD27EQ(cBeanTD.getRemittedAmtAddedTD27EQ()+remAmt);
										}
									}
									




									// End of For Checking whether the tan with the same form already exists more than one time in the file or not.
									if(objReadFVAL3.stmtType.equals(TBAF_TYPE_OF_STMT_ORIG) || objReadFVAL3.stmtType.equals(TBAF_TYPE_OF_STMT_CORR))
									{
										if(formType.equals("F138"))  //Gauri changed form type for CR 89435, FVU 1.9
										{

											cBeanTD.setCountNatOfDed24Q(cBeanTD.getCountNatOfDed24Q() + 1);
											
											if(taxAmt.equals(TBAF_FIELD_NULL) || taxAmt.equals(TBAF_FIELD_SEPERATOR))
											{
												cBeanTD.setTotalTaxTD24Q(cBeanTD.getTotalTaxTD24Q() + 0.00);
											}
											else
											{
												double tax24Amt = 0.00;
												try
												{
													tax24Amt = Double.parseDouble(taxAmt);
												}
												catch(Exception e)
												{
													Log.tbaf_log.error("Exception", e);
													tax24Amt = 0.00 ;
												}
												cBeanTD.setTotalTaxTD24Q(cBeanTD.getTotalTaxTD24Q() + tax24Amt);
											}
											if(remittedAmt.equals(TBAF_FIELD_NULL) || remittedAmt.equals(TBAF_FIELD_SEPERATOR))
											{
												cBeanTD.setTotalRemittanceTD24Q(cBeanTD.getTotalRemittanceTD24Q() + 0.00);
											}
											else
											{
												double remit24Amt = 0.00;
												try
												{
													remit24Amt = Double.parseDouble(remittedAmt);
													
												}
												catch(Exception e)
												{
													Log.tbaf_log.error("Exception", e);
													remit24Amt = 0.00 ;
												}
												if(objReadFVAL3.stmtType.equals(TBAF_TYPE_OF_STMT_CORR))
												{
													
													
												if((tdRevMode.equals(TBAF_REVISION_MODE_UPDATE)))
												{
													
													double lastRemit24Amt=0.00;
													if(!tdLastRemittedAmt.equals("^"))
													{
														lastRemit24Amt=Double.parseDouble(tdLastRemittedAmt);
													}
													cBeanTD.setTotalRemittanceTD24Q(cBeanTD.getTotalRemittanceTD24Q() + (remit24Amt-lastRemit24Amt));
												}
												else if((tdRevMode.equals(TBAF_REVISION_MODE_ADD)))
												{
													cBeanTD.setTotalRemittanceTD24Q(cBeanTD.getTotalRemittanceTD24Q() + remit24Amt);
												}
												else if((tdRevMode.equals(TBAF_REVISION_MODE_DEL)))
												{
													cBeanTD.setTotalRemittanceTD24Q(cBeanTD.getTotalRemittanceTD24Q() - remit24Amt);
												}
												}
												else
												{
													cBeanTD.setTotalRemittanceTD24Q(cBeanTD.getTotalRemittanceTD24Q() + remit24Amt);
												}
											}


											/*	if((! taxAmt.equals(TBAF_FIELD_NULL) && ! taxAmt.equals(TBAF_FIELD_SEPERATOR)) && (! remittedAmt.equals(TBAF_FIELD_NULL) && ! remittedAmt.equals(TBAF_FIELD_SEPERATOR)))
						    		{
					    		    cBeanTD.setTotalTaxTD24Q(cBeanTD.getTotalTaxTD24Q() + Double.parseDouble(taxAmt));
						    		    cBeanTD.setTotalRemittanceTD24Q(cBeanTD.getTotalRemittanceTD24Q() + Double.parseDouble(remittedAmt));

						    		}*/


											//objReadFVAL3.statReportBuffer.append(tdDDODeductionNature.trim() + TBAF_FIELD_SEPERATOR);
										}
										else if(formType.equals("F140"))	//Gauri changed form type for CR 89435, FVU 1.9
										{
											
											cBeanTD.setCountNatOfDed26Q(cBeanTD.getCountNatOfDed26Q() + 1);
											if(taxAmt.equals(TBAF_FIELD_NULL) || taxAmt.equals(TBAF_FIELD_SEPERATOR))
											{
												cBeanTD.setTotalTaxTD26Q(cBeanTD.getTotalTaxTD26Q() + 0.00);
											}
											else
											{
												double tax26Amt = 0.00;
												try
												{
													tax26Amt = Double.parseDouble(taxAmt);
												}
												catch(Exception e)
												{
													Log.tbaf_log.error("Exception", e);
													tax26Amt = 0.00 ;
												}
												cBeanTD.setTotalTaxTD26Q(cBeanTD.getTotalTaxTD26Q() + tax26Amt);
											}
											if(remittedAmt.equals(TBAF_FIELD_NULL) || remittedAmt.equals(TBAF_FIELD_SEPERATOR))
											{
												cBeanTD.setTotalRemittanceTD26Q(cBeanTD.getTotalRemittanceTD26Q() + 0.00);
											}
											else
											{
												double remit26Amt = 0.00;
												try
												{
													remit26Amt = Double.parseDouble(remittedAmt);
												}
												catch(Exception e)
												{
													Log.tbaf_log.error("Exception", e);
													remit26Amt = 0.00 ;
												}
												if(objReadFVAL3.stmtType.equals(TBAF_TYPE_OF_STMT_CORR))
												{
													if(tdLastDDODeductionNature.equals("F140"))
													{
														cBeanTD.setCountLastNatOfDed26Q(cBeanTD.getCountLastNatOfDed26Q() + 1);
													}
												if((tdRevMode.equals(TBAF_REVISION_MODE_UPDATE)))
												{
													double lastRemit26Amt=0.00;
													if(!tdLastRemittedAmt.equals("^"))
													{
														lastRemit26Amt=Double.parseDouble(tdLastRemittedAmt);
													}
													cBeanTD.setTotalRemittanceTD26Q(cBeanTD.getTotalRemittanceTD26Q() + (remit26Amt-lastRemit26Amt));
												}
												else if((tdRevMode.equals(TBAF_REVISION_MODE_ADD)))
												{
													cBeanTD.setTotalRemittanceTD26Q(cBeanTD.getTotalRemittanceTD26Q() + remit26Amt);
												}
												else if((tdRevMode.equals(TBAF_REVISION_MODE_DEL)))
												{
													cBeanTD.setTotalRemittanceTD26Q(cBeanTD.getTotalRemittanceTD26Q() - remit26Amt);
												}
												}
												else
												{
													cBeanTD.setTotalRemittanceTD26Q(cBeanTD.getTotalRemittanceTD26Q() + remit26Amt);
												}
												
											}


											/*	if((! taxAmt.equals(TBAF_FIELD_NULL) && ! taxAmt.equals(TBAF_FIELD_SEPERATOR)) && (! remittedAmt.equals(TBAF_FIELD_NULL) && ! remittedAmt.equals(TBAF_FIELD_SEPERATOR)))
						    		{
						    		  cBeanTD.setTotalTaxTD26Q(cBeanTD.getTotalTaxTD26Q() + Double.parseDouble(taxAmt));
						    		  cBeanTD.setTotalRemittanceTD26Q(cBeanTD.getTotalRemittanceTD26Q() + Double.parseDouble(remittedAmt));

						    		}*/


											//objReadFVAL3.statReportBuffer.append(tdDDODeductionNature.trim() + TBAF_FIELD_SEPERATOR);
										}
										else if(formType.equals("F144"))		//Gauri changed form type for CR 89435, FVU 1.9
										{
											cBeanTD.setCountNatOfDed27Q(cBeanTD.getCountNatOfDed27Q() + 1);
											if(taxAmt.equals(TBAF_FIELD_NULL) || taxAmt.equals(TBAF_FIELD_SEPERATOR))
											{
												cBeanTD.setTotalTaxTD27Q(cBeanTD.getTotalTaxTD27Q() + 0.00);
											}
											else
											{
												double tax27Amt = 0.00;
												try
												{
													tax27Amt = Double.parseDouble(taxAmt);
												}
												catch(Exception e)
												{
													Log.tbaf_log.error("Exception", e);
													tax27Amt = 0.00 ;
												}
												cBeanTD.setTotalTaxTD27Q(cBeanTD.getTotalTaxTD27Q() + tax27Amt);
											}
											if(remittedAmt.equals(TBAF_FIELD_NULL) || remittedAmt.equals(TBAF_FIELD_SEPERATOR))
											{
												cBeanTD.setTotalRemittanceTD27Q(cBeanTD.getTotalRemittanceTD27Q() + 0.00);
											}
											else
											{
												double remit27Amt = 0.00;
												try
												{
													remit27Amt = Double.parseDouble(remittedAmt);
												}
												catch(Exception e)
												{
													Log.tbaf_log.error("Exception", e);
													remit27Amt = 0.00 ;
												}
												if(objReadFVAL3.stmtType.equals(TBAF_TYPE_OF_STMT_CORR))
												{
													if(tdLastDDODeductionNature.equals("F144"))
													{
														cBeanTD.setCountLastNatOfDed27Q(cBeanTD.getCountLastNatOfDed27Q() + 1);
													}
												if((tdRevMode.equals(TBAF_REVISION_MODE_UPDATE)))
												{
													double lastRemit27Amt=0.00;
													if(!tdLastRemittedAmt.equals("^"))
													{
														lastRemit27Amt=Double.parseDouble(tdLastRemittedAmt);
													}
													cBeanTD.setTotalRemittanceTD27Q(cBeanTD.getTotalRemittanceTD27Q() + (remit27Amt-lastRemit27Amt));
												}
												else if((tdRevMode.equals(TBAF_REVISION_MODE_ADD)))
												{
													cBeanTD.setTotalRemittanceTD27Q(cBeanTD.getTotalRemittanceTD27Q() + remit27Amt);
												}
												else if((tdRevMode.equals(TBAF_REVISION_MODE_DEL)))
												{
													cBeanTD.setTotalRemittanceTD27Q(cBeanTD.getTotalRemittanceTD27Q() - remit27Amt);
												}
												}
												else
												{
													cBeanTD.setTotalRemittanceTD27Q(cBeanTD.getTotalRemittanceTD27Q() + remit27Amt);
												}
											}

											/*if((! taxAmt.equals(TBAF_FIELD_NULL) && ! taxAmt.equals(TBAF_FIELD_SEPERATOR)) && (! remittedAmt.equals(TBAF_FIELD_NULL) && ! remittedAmt.equals(TBAF_FIELD_SEPERATOR)))
						    		{
						    		   cBeanTD.setTotalTaxTD27Q(cBeanTD.getTotalTaxTD27Q() + Double.parseDouble(taxAmt));
						    		   cBeanTD.setTotalRemittanceTD27Q(cBeanTD.getTotalRemittanceTD27Q() + Double.parseDouble(remittedAmt));
						    		}*/

											//objReadFVAL3.statReportBuffer.append(tdDDODeductionNature.trim() + TBAF_FIELD_SEPERATOR);
										}
										else if(formType.equals("F143"))	//Gauri changed form type for CR 89435, FVU 1.9
										{
											cBeanTD.setCountNatOfDed27EQ(cBeanTD.getCountNatOfDed27EQ() + 1);
											if(taxAmt.equals(TBAF_FIELD_NULL) || taxAmt.equals(TBAF_FIELD_SEPERATOR))
											{
												cBeanTD.setTotalTaxTD27EQ(cBeanTD.getTotalTaxTD27EQ() + 0.00); 
											}
											else
											{

												double tax27EQAmt = 0.00;
												try
												{
													tax27EQAmt = Double.parseDouble(taxAmt);
												}
												catch(Exception e)
												{
													Log.tbaf_log.error("Exception", e);
													tax27EQAmt = 0.00 ;
												}
												cBeanTD.setTotalTaxTD27EQ(cBeanTD.getTotalTaxTD27EQ() + tax27EQAmt);
											}
											if(remittedAmt.equals(TBAF_FIELD_NULL) || remittedAmt.equals(TBAF_FIELD_SEPERATOR))
											{
												cBeanTD.setTotalRemittanceTD27EQ(cBeanTD.getTotalRemittanceTD27EQ() + 0.00);
											}
											else
											{
												double remit27EQAmt = 0.00;
												try
												{
													remit27EQAmt = Double.parseDouble(remittedAmt);
												}
												catch(Exception e)
												{
													Log.tbaf_log.error("Exception", e);
													remit27EQAmt = 0.00 ;
												}
												if(objReadFVAL3.stmtType.equals(TBAF_TYPE_OF_STMT_CORR))
												{
													if(tdLastDDODeductionNature.equals("F143")) //Gauri changed form type
													{
														cBeanTD.setCountLastNatOfDed27EQ(cBeanTD.getCountLastNatOfDed24Q() + 1);
													}
												if((tdRevMode.equals(TBAF_REVISION_MODE_UPDATE)))
												{
													double lastRemit27EQAmt=0.00;
													if(!tdLastRemittedAmt.equals("^"))
													{
														lastRemit27EQAmt=Double.parseDouble(tdLastRemittedAmt);
													}
													cBeanTD.setTotalRemittanceTD27EQ(cBeanTD.getTotalRemittanceTD27EQ() + (remit27EQAmt-lastRemit27EQAmt));
												}
												else if((tdRevMode.equals(TBAF_REVISION_MODE_ADD)))
												{
													cBeanTD.setTotalRemittanceTD27EQ(cBeanTD.getTotalRemittanceTD27EQ() + remit27EQAmt);
												}
												else if((tdRevMode.equals(TBAF_REVISION_MODE_DEL)))
												{
													cBeanTD.setTotalRemittanceTD27EQ(cBeanTD.getTotalRemittanceTD27EQ() - remit27EQAmt);
												}
												}
												else
												{
													cBeanTD.setTotalRemittanceTD27EQ(cBeanTD.getTotalRemittanceTD27EQ() + remit27EQAmt);
												}
											}


											/*	if((! taxAmt.equals(TBAF_FIELD_NULL) && ! taxAmt.equals(TBAF_FIELD_SEPERATOR)) && (! remittedAmt.equals(TBAF_FIELD_NULL) && ! remittedAmt.equals(TBAF_FIELD_SEPERATOR)))
						    		{
						    		cBeanTD.setTotalTaxTD27EQ(cBeanTD.getTotalTaxTD27EQ() + Double.parseDouble(taxAmt));
						    		cBeanTD.setTotalRemittanceTD27EQ(cBeanTD.getTotalRemittanceTD27EQ() + Double.parseDouble(remittedAmt));
						    		}*/

											//objReadFVAL3.statReportBuffer.append(tdDDODeductionNature.trim() + TBAF_FIELD_SEPERATOR);
										}
										else
										{
											//objReadFVAL3.statReportBuffer.append("-" + TBAF_FIELD_SEPERATOR);
										}
									}
									
									//Gauri made form type changes
									if(objReadFVAL3.stmtType.equals(TBAF_TYPE_OF_STMT_CORR) && tdRevMode.equals(TBAF_REVISION_MODE_UPDATE))
									{
										if(tdLastDDODeductionNature.equals("F138"))
										{
											cBeanTD.setCountLastNatOfDed24Q(cBeanTD.getCountLastNatOfDed24Q() + 1);
										}
										if(tdLastDDODeductionNature.equals("F140"))
										{
											cBeanTD.setCountLastNatOfDed26Q(cBeanTD.getCountLastNatOfDed26Q() + 1);
										}
										if(tdLastDDODeductionNature.equals("F144"))
										{
											cBeanTD.setCountLastNatOfDed27Q(cBeanTD.getCountLastNatOfDed27Q() + 1);
										}
										if(tdLastDDODeductionNature.equals("F143"))
										{
											cBeanTD.setCountLastNatOfDed27EQ(cBeanTD.getCountLastNatOfDed27EQ() + 1);
										}
									}
									//objReadFVAL3.statReportBuffer.append(tdDDODeductionNature + TBAF_FIELD_SEPERATOR);
								}

							}
						}
						
						

						//Gauri added this change for Form Type changes for CR 89435, FVU 1.9
						else if (Integer.parseInt(objReadFVAL3.finYear) < 2026 && (objReadFVAL3.stmtType.equals(TBAF_TYPE_OF_STMT_ORIG) || objReadFVAL3.stmtType.equals(TBAF_TYPE_OF_STMT_CORR)))
						{
							if (!formType.equals(TBAF_FIELD_NULL) && !formType.equals(TBAF_FIELD_SEPERATOR))
							{
								errStrBuff.append(TBAF_TDREC + lineNo + "^" + "Filler 7(17)" + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3012);
							}
							else
							{
								//objReadFVAL3.statReportBuffer.append("-" + TBAF_FIELD_SEPERATOR);
							}
						}



						/*  if( (tdDDOMapping.trim().equals("D")) && (! monthYear.equals(TBAF_FIELD_NULL) && ! monthYear.equals(TBAF_FIELD_SEPERATOR)))   //Added By Subhankar(As When DDO Mapping/Update Flag is 'D' then no value should be allowed in Month and year of Statement Field)  
					    {
					        	errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[17] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3044);
					    }
				      else if(! (tdDDOMapping.trim().equals("D")))
				      {
						if (monthYear.equals(TBAF_FIELD_NULL) || monthYear.equals(TBAF_FIELD_SEPERATOR))
						{
							errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[17] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3001);
						}
						else if (objRecVal.isFieldNull(monthYear))
						{
							errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[17] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3002);
						}
						else if(monthYear.trim().length() != monthYear.length())
						{
							errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[17] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3048);
						}
						else if (monthYear.trim().length() != 6 || monthYear.length() != 6)
						{
							errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[17] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3018);
						}
						else if(objRecVal.isInt(monthYear.trim()))
						{
							errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[17] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3002);
						}
						else if( (Integer.parseInt(monthYear)) != (Integer.parseInt(objReadFVAL3.batchMonthYear)))
						{
							errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[17] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3033);
						}
						else
						{
							//objReadFVAL3.statReportBuffer.append(monthYear.trim() + TBAF_FIELD_SEPERATOR);
						}
				      }  */






						/*else if (objRecVal.checkMonthAndYearOfPayment(objReadFVAL3.quarter, objReadFVAL3.finYear, monthYear))
						{
							errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[17] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3020);
						}*/
						/*	else if (!isTANInvalid)
						{
							if (objReadFVAL3.stmtType.equals(TBAF_TYPE_OF_STMT_ORIG) 
								|| objReadFVAL3.transType.equals(TBAF_TRANSACTION_TYPE_C4))
							{
								String tanMonthYear = tdTAN.trim() + monthYear.trim(); */
						/*
						 * To check whether the same TAN with same Month & Year is already
						 * present in the HashSet.
						 */
						/*	if (objReadFVAL3.hashSetMonthYear.contains(tanMonthYear))
								{
									errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[17] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3021);
								}
								else
								{*/
						//	Add TAN & Month Year into HashSet.
						//objReadFVAL3.hashSetMonthYear.add(tanMonthYear);
						/*		}
							}
							else
							{
								String tanMonthYearRevMode = tdTAN.trim() + monthYear.trim() + tdRevMode.trim();
								if (tdRevMode.equals(TBAF_REVISION_MODE_ADD) && tdRevMode.equals(TBAF_REVISION_MODE_DEL))
								{*/
						/*
						 *	If the Revision Mode is different, same TAN with 
						 *  same Month & Year can be allowed.
						 */
						/*		if (objReadFVAL3.hashSetMonthYearRevMode.contains(tanMonthYearRevMode))
									{
										// No error check
									}
									else
									{
										objReadFVAL3.hashSetMonthYearRevMode.add(tanMonthYearRevMode);
									}
								}
								else if (tdRevMode.equals(TBAF_REVISION_MODE_ADD))
								{*/
						/*
						 * Check whether the same TAN & MonthYear repeats again with 
						 * the same Revision Mode.
						 */
						/*		if (objReadFVAL3.hashSetMonthYearRevMode.contains(tanMonthYearRevMode))
									{
										errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[17] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3021);
									}
									else
									{
										objReadFVAL3.hashSetMonthYearRevMode.add(tanMonthYearRevMode);
									}
								}
								else if (tdRevMode.equals(TBAF_REVISION_MODE_DEL))
								{*/
						/*
						 * Check whether the same TAN & MonthYear repeats again with 
						 * the same Revision Mode.
						 */
						/*	if (objReadFVAL3.hashSetMonthYearRevMode.contains(tanMonthYearRevMode))
									{
										errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[17] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3021);
									}
									else
									{
										objReadFVAL3.hashSetMonthYearRevMode.add(tanMonthYearRevMode);
									}
								}
							}
						} */



						// End:: Gauri added validations of Form type for CR 89435, FVU 1.9

						
						


			//Added By Subhankar


			/**
			 *	Validation of DDO Registration Number(Field No. 18 Of DDO Transaction Detail Record)
			 *  If TAN is Invalid i.e. within the 3 constants then either DDO Registration Number or DDO Code
			 *  should be present for both Revision mode.
			 *  This field is not applicable from FY 2026-27, CR 89435 FVU 1.9
			 *
			 */
			
			if (Integer.parseInt(objReadFVAL3.finYear) < 2026 && (objReadFVAL3.stmtType.equals(TBAF_TYPE_OF_STMT_ORIG) || objReadFVAL3.stmtType.equals(TBAF_TYPE_OF_STMT_CORR))) //Gauri added the FY condition for CR 89435 FVU 1.9

			{
				if(objRecVal.checkTanAgainstConstants(tdTAN) || isTANInvalid)
				{
					if(! tdDDORegNo.equals(TBAF_FIELD_NULL) && ! tdDDORegNo.equals(TBAF_FIELD_SEPERATOR))
					{
						if(tdDDORegNo.trim().length() != TBAF_DDO_REG_NO_LEN || tdDDORegNo.length() != TBAF_DDO_REG_NO_LEN)
						{
							errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[18] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3014);
						}
						else if(tdDDORegNo.length() != tdDDORegNo.trim().length())
						{
							errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[18] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3048);
						}
						else if((objRecVal.isAlphaNum(tdDDORegNo)))
						{
							errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[18] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3030);
						}
						else if(objReadFVAL3.deductCatgry.equals("A"))
						{
							if(! (tdDDORegNo.substring(0,3).equals("CGV")) || (objRecVal.isInt(tdDDORegNo.substring(4,9))) || !(objRecVal.isIn_DDO_REG_NO_MOD(tdDDORegNo.substring(4,9),tdDDORegNo.substring(9,10))))
							{
								errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[18] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3026);
							}
						}
						else if(objReadFVAL3.deductCatgry.equals("S")) 
						{
							if(! (tdDDORegNo.substring(0,3).equals("SGV")) || (objRecVal.isInt(tdDDORegNo.substring(4,9))) || !(objRecVal.isIn_DDO_REG_NO_MOD(tdDDORegNo.substring(4,9),tdDDORegNo.substring(9,10))))
							{
								errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[18] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3027);
							}
						}
						else
						{
							//objReadFVAL3.statReportBuffer.append(tdDDORegNo + TBAF_FIELD_SEPERATOR);
						}


					}
					else
					{
						//objReadFVAL3.statReportBuffer.append("-" + TBAF_FIELD_SEPERATOR);
					}
				}  

				else
				{
					if(! tdDDORegNo.equals(TBAF_FIELD_NULL) && ! tdDDORegNo.equals(TBAF_FIELD_SEPERATOR))
					{
						if(tdDDORegNo.trim().length() != TBAF_DDO_REG_NO_LEN || tdDDORegNo.length() != TBAF_DDO_REG_NO_LEN)
						{
							errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[18] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3014);
						}
						else if(tdDDORegNo.length() != tdDDORegNo.trim().length())
						{
							errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[18] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3048);
						}
						else if((objRecVal.isAlphaNum(tdDDORegNo)))
						{
							errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[18] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3030);
						}
						else if(objReadFVAL3.deductCatgry.equals("A"))
						{
							if(! (tdDDORegNo.substring(0,3).equals("CGV")) || (objRecVal.isInt(tdDDORegNo.substring(4,9))) || !(objRecVal.isIn_DDO_REG_NO_MOD(tdDDORegNo.substring(4,9),tdDDORegNo.substring(9,10))))
							{
								errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[18] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3026);
							}
						}
						else if(objReadFVAL3.deductCatgry.equals("S")) 
						{
							if(! (tdDDORegNo.substring(0,3).equals("SGV")) || (objRecVal.isInt(tdDDORegNo.substring(4,9))) || !(objRecVal.isIn_DDO_REG_NO_MOD(tdDDORegNo.substring(4,9),tdDDORegNo.substring(9,10))))
							{
								errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[18] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3027);
							}
						}
						else
						{
							//objReadFVAL3.statReportBuffer.append(tdDDORegNo + TBAF_FIELD_SEPERATOR);
						}
					}
					else
					{
						//objReadFVAL3.statReportBuffer.append("-" + TBAF_FIELD_SEPERATOR);
					}

				}
			}

			/*	else if(objReadFVAL3.stmtType.equals(TBAF_TYPE_OF_STMT_CORR) && tdRevMode.equals(TBAF_REVISION_MODE_DEL))
			{
				if (!tdDDORegNo.equals(TBAF_FIELD_NULL) && !tdDDORegNo.equals(TBAF_FIELD_SEPERATOR))
				{
					errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[18] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3015);
				}
			} */
			
			//Gauri added else if condition for CR 89435, FVU 1.9
			else if(Integer.parseInt(objReadFVAL3.finYear) >= 2026){
				if(! tdDDORegNo.equals(TBAF_FIELD_NULL) && ! tdDDORegNo.equals(TBAF_FIELD_SEPERATOR)) {
					errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[18] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_5058);
				}
				
			}

			//End of DDO Registration Number Validation




			/**
			 *	Validation of DDO Code(Field No. 19 Of DDO Transaction Detail Record)
			 *  If TAN is Invalid i.e. within the 3 constants then either DDO Registration Number or DDO Code
			 *  should be present for both Revision mode.
			 *  This field is not applicable from FY 2026-27, CR 89435 FVU 1.9
			 *
			 */


			if (Integer.parseInt(objReadFVAL3.finYear) < 2026 && (objReadFVAL3.stmtType.equals(TBAF_TYPE_OF_STMT_ORIG) || objReadFVAL3.stmtType.equals(TBAF_TYPE_OF_STMT_CORR))) //Gauri added FY condition for CR 89435, FVU 1.9

			{

				if(isTANInvalid || objRecVal.checkTanAgainstConstants(tdTAN))
				{
					if( (tdDDORegNo.equals(TBAF_FIELD_NULL) || tdDDORegNo.equals(TBAF_FIELD_SEPERATOR)) && (! tdDDOCode.equals(TBAF_FIELD_NULL) && ! tdDDOCode.equals(TBAF_FIELD_SEPERATOR)))
					{
						if(tdDDOCode.trim().length() > TBAF_DDO_CODE_LEN || tdDDOCode.length() > TBAF_DDO_CODE_LEN)
						{
							errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[19] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3031);
						}
						else if(tdDDOCode.length() != tdDDOCode.trim().length())
						{
							errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[19] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3048);
						}
						else if((objRecVal.isAlphaNum(tdDDOCode)))
						{
							errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[19] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3030);
						}
						else if(objRecVal.CheckZeros(tdDDOCode.trim()))
						{
							errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[19] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3035);
						}
						else
						{
							//objReadFVAL3.statReportBuffer.append(tdDDOCode.trim() + TBAF_FIELD_SEPERATOR);
						}
					}
					else if( (! tdDDORegNo.equals(TBAF_FIELD_NULL) && ! tdDDORegNo.equals(TBAF_FIELD_SEPERATOR)) && (! tdDDOCode.equals(TBAF_FIELD_NULL) && ! tdDDOCode.equals(TBAF_FIELD_SEPERATOR)))
					{
						if(tdDDOCode.trim().length() > TBAF_DDO_CODE_LEN || tdDDOCode.length() > TBAF_DDO_CODE_LEN)
						{
							errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[19] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3031);
						}
						else if(tdDDOCode.length() != tdDDOCode.trim().length())
						{
							errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[19] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3048);
						}
						else if((objRecVal.isAlphaNum(tdDDOCode)))
						{
							errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[19] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3030);
						}
						else if(objRecVal.CheckZeros(tdDDOCode.trim()))
						{
							errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[19] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3035);
						}
						else
						{
							// objReadFVAL3.statReportBuffer.append(tdDDOCode.trim() + TBAF_FIELD_SEPERATOR);
						}
					}
					/*else if( (tdDDORegNo.equals(TBAF_FIELD_NULL)  || tdDDORegNo.equals(TBAF_FIELD_SEPERATOR)) && (tdDDOCode.equals(TBAF_FIELD_NULL)  || tdDDOCode.equals(TBAF_FIELD_SEPERATOR)))
					{
						errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[18] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3037);
						//objReadFVAL3.statReportBuffer.append("-" + TBAF_FIELD_SEPERATOR);
					}*/
					else
					{
						//objReadFVAL3.statReportBuffer.append("-" + TBAF_FIELD_SEPERATOR);
					}
				}
				else
				{
					if((! tdDDOCode.equals(TBAF_FIELD_NULL) && ! tdDDOCode.equals(TBAF_FIELD_SEPERATOR)))
					{
						if(tdDDOCode.trim().length() > TBAF_DDO_CODE_LEN || tdDDOCode.length() > TBAF_DDO_CODE_LEN)
						{
							errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[19] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3031);
						}
						else if(tdDDOCode.length() != tdDDOCode.trim().length())
						{
							errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[19] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3048);
						}
						else if((objRecVal.isAlphaNum(tdDDOCode)))
						{
							errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[19] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3030);
						}
						else if(objRecVal.CheckZeros(tdDDOCode.trim()))
						{
							errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[19] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3035);
						}
						else
						{
							// objReadFVAL3.statReportBuffer.append(tdDDOCode.trim() + TBAF_FIELD_SEPERATOR);
						}
					}
					else
					{
						//objReadFVAL3.statReportBuffer.append("-" + TBAF_FIELD_SEPERATOR);
					}
				}
			}
			/* else if(objReadFVAL3.stmtType.equals(TBAF_TYPE_OF_STMT_CORR) && tdRevMode.equals(TBAF_REVISION_MODE_DEL))
			{
				if (!tdDDOCode.equals(TBAF_FIELD_NULL) && !tdDDOCode.equals(TBAF_FIELD_SEPERATOR))
				{
					errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[19] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3015);
				}
			} */
			
			//Gauri added else if condition for CR 89435, FVU 1.9
			else if(Integer.parseInt(objReadFVAL3.finYear) >= 2026) {
				if((! tdDDOCode.equals(TBAF_FIELD_NULL) && ! tdDDOCode.equals(TBAF_FIELD_SEPERATOR))) {
					errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[19] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_5058);
				}
				
			}

			//End of DDO Code Validation



			/**
			 *	Validation of DDO Email Id(Field No. 20 Of DDO Transaction Detail Record)
			 *	This field is not applicable from FY 2026-27 CR 89435, FVU 1.9
			 */

			if (Integer.parseInt(objReadFVAL3.finYear) < 2026 && (objReadFVAL3.stmtType.equals(TBAF_TYPE_OF_STMT_ORIG) || (objReadFVAL3.stmtType.equals(TBAF_TYPE_OF_STMT_CORR) //Gauri added FY condition for CR 89435, FVU 1.9
					&& tdRevMode.equals(TBAF_REVISION_MODE_ADD))))
			{
				if (! tdDDOEmailID.equals(TBAF_FIELD_NULL) && ! tdDDOEmailID.equals(TBAF_FIELD_SEPERATOR))
				{
					if (tdDDOEmailID.trim().length() > TBAF_DDO_EMAIL_LEN || tdDDOEmailID.length() > TBAF_DDO_EMAIL_LEN)
					{
						errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[20] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3005);
					}
					else if(tdDDOEmailID.length() != tdDDOEmailID.trim().length())
					{
						errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[20] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3048);
					}
					else if(objRecVal.isValidEmail(tdDDOEmailID))
					{ 
						errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[20] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3032);
					}
					else
					{
						//objReadFVAL3.statReportBuffer.append(tdDDOEmailID.trim() + TBAF_FIELD_SEPERATOR);
					}
				}
				else
				{
					//objReadFVAL3.statReportBuffer.append("-" + TBAF_FIELD_SEPERATOR);
				}
			}	
			else if(Integer.parseInt(objReadFVAL3.finYear) < 2026 && (objReadFVAL3.stmtType.equals(TBAF_TYPE_OF_STMT_CORR) && tdRevMode.equals(TBAF_REVISION_MODE_DEL))) //Gauri added FY condition for CR 89435, FVU 1.9
			{
				if (!tdDDOEmailID.equals(TBAF_FIELD_NULL) && !tdDDOEmailID.equals(TBAF_FIELD_SEPERATOR))
				{
					errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[20] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3015);
				}
			}
			
			//Gauri added else if condition for CR 89435, FVU 1.9
			else if(Integer.parseInt(objReadFVAL3.finYear) >= 2026) {
				if(!tdDDOEmailID.equals(TBAF_FIELD_NULL) && !tdDDOEmailID.equals(TBAF_FIELD_SEPERATOR)) {
					errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[20] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_5058);
				}
				
			}

			//End of DDO Email Id Validation



			/**
			 *	Validation of Total TDS/TCS Remitted(Field No. 21 Of DDO Transaction Detail Record)
			 */

			if (objReadFVAL3.stmtType.equals(TBAF_TYPE_OF_STMT_ORIG) || (objReadFVAL3.stmtType.equals(TBAF_TYPE_OF_STMT_CORR) 
					&& objReadFVAL3.transType.equals(TBAF_TRANSACTION_TYPE_M)))
			{


				if( (tdDDOMapping.trim().equals("D")) && ! remittedAmt.equals("0.00"))   //Added By Subhankar(As When DDO Mapping/Update Flag is 'D' then no value should be allowed in TDS/TCS Remitted Amt Field)  
				{
					errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[21] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3039);
				}

				else if(! (tdDDOMapping.trim().equals("D")))
				{


					if (remittedAmt.equals(TBAF_FIELD_NULL) || remittedAmt.equals(TBAF_FIELD_SEPERATOR))
					{
						objReadFVAL3.invalidRemittedAmt = true;
						errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[21] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3001);
					}
					else if (objRecVal.isFieldNull(remittedAmt))
					{
						objReadFVAL3.invalidRemittedAmt = true;
						errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[21] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3002);
					}
					else if (remittedAmt.trim().length() > 15 || remittedAmt.length() > 15)
					{
						objReadFVAL3.invalidRemittedAmt = true;
						errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[21] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3005);
					}
					else if(remittedAmt.trim().length() != remittedAmt.length())
					{
						objReadFVAL3.invalidRemittedAmt = true;
						errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[21] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3002);

					}
					else if (objReadFVAL3.stmtType.equals(TBAF_TYPE_OF_STMT_ORIG)
							|| (objReadFVAL3.stmtType.equals(TBAF_TYPE_OF_STMT_CORR) 
									&& objReadFVAL3.transType.equals(TBAF_TRANSACTION_TYPE_C4)) )
					{
						if (objRecVal.isDecimalNumber(remittedAmt) || !remittedAmt.endsWith("00"))
						{
							if(objRecVal.isInt(remittedAmt))
							{
								objReadFVAL3.invalidRemittedAmt = true;
								errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[21] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3002);
							}
							else
							{
								objReadFVAL3.invalidRemittedAmt = true;
								errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[21] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3053);
							}
						}
						else if (Double.parseDouble(remittedAmt.trim()) < 0.00)
						{
							objReadFVAL3.invalidRemittedAmt = true;
							errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[21] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3002);
						}
						else
						{
							//	Add the individual Remitted amounts in each TD Record.
							objReadFVAL3.totalRemittedTaxAdded = objReadFVAL3.totalRemittedTaxAdded + Double.parseDouble(remittedAmt.trim());
						}
					}



					//On 7rd Nov as for Correction Type M , Remitted amount has been taken as Verification Key so separate accounts are required to be maintained for records in Added Mode and Delete mode
					
					/*else if (objReadFVAL3.invalidRemittedAmt == false 
					&& objReadFVAL3.stmtType.equals(TBAF_TYPE_OF_STMT_CORR) 
					&& !objReadFVAL3.transType.equals(TBAF_TRANSACTION_TYPE_C4)) */



					else if (objReadFVAL3.invalidRemittedAmt == false 
							&& objReadFVAL3.stmtType.equals(TBAF_TYPE_OF_STMT_CORR) 
							&& objReadFVAL3.transType.equals(TBAF_TRANSACTION_TYPE_M) )




					{


						if (objRecVal.isDecimalNumber(remittedAmt) || !remittedAmt.endsWith("00"))
						{
							if(objRecVal.isInt(remittedAmt))
							{
								objReadFVAL3.invalidRemittedAmt = true;
								errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[21] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3002);
							}
							else
							{
								objReadFVAL3.invalidRemittedAmt = true;
								errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[21] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3053);
							}
						}
						else if (Double.parseDouble(remittedAmt.trim()) < 0.00)
						{
							objReadFVAL3.invalidRemittedAmt = true;
							errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[21] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3002);
						}
						else
						{

							if (tdRevMode.equals(TBAF_REVISION_MODE_ADD))
							{
								//	Add the individual Remitted amounts seperately for "ADD" Mode. 
								objReadFVAL3.totalRemittedTaxAdded = objReadFVAL3.totalRemittedTaxAdded + Double.parseDouble(remittedAmt.trim());
							}
							else if (tdRevMode.equals(TBAF_REVISION_MODE_DEL))
							{
								//	Add the individual Remitted amounts seperately for "DEL" Mode.
								objReadFVAL3.totalRemittedTaxDeleted = objReadFVAL3.totalRemittedTaxDeleted + Double.parseDouble(remittedAmt.trim());
							}
							else if (tdRevMode.equals(TBAF_REVISION_MODE_UPDATE))
							{
//								Add the individual Remitted amounts seperately for "Update" Mode.
								objReadFVAL3.totalRemittedTaxUpdated = objReadFVAL3.totalRemittedTaxUpdated + Double.parseDouble(remittedAmt.trim());
								if(tdLastRemittedAmt!=null)
								{
								objReadFVAL3.totalLastRemittedTaxUpdated = objReadFVAL3.totalLastRemittedTaxUpdated + Double.parseDouble(tdLastRemittedAmt.trim());
								}
							}
						}
					}
				}
			}



			//End of Total TDS/TCS Remitted Validation


			/**
			 *	Validation of Nature Of Deduction(Field No. 22 Of DDO Transaction Detail Record)
			 *	This field is not applicable from FY 2026-27
			 */
			if (Integer.parseInt(objReadFVAL3.finYear) < 2026 && (objReadFVAL3.stmtType.equals(TBAF_TYPE_OF_STMT_ORIG) || objReadFVAL3.stmtType.equals(TBAF_TYPE_OF_STMT_CORR)))
			{
				if((tdDDOMapping.trim().equals("D")) && (! tdDDODeductionNature.equals(TBAF_FIELD_NULL) && ! tdDDODeductionNature.equals(TBAF_FIELD_SEPERATOR)))   //Added By Subhankar(As When DDO Mapping/Update Flag is 'D' then no value should be allowed in Nature of Deduction Field)  
				{
					errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[22] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3040);
				}

				else if(! (tdDDOMapping.trim().equals("D")))
				{

					if (tdDDODeductionNature.equals(TBAF_FIELD_NULL) || tdDDODeductionNature.equals(TBAF_FIELD_SEPERATOR))
					{
						errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[22] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3001);
					}
					else if (objRecVal.isFieldNull(tdDDODeductionNature))
					{
						errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[22] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3002);
					}
					else if (tdDDODeductionNature.trim().length() > TBAF_DDO_NAT_OF_DED_LEN || tdDDODeductionNature.length() > TBAF_DDO_NAT_OF_DED_LEN)
					{
						errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[22] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3005);
					}
					else if(tdDDODeductionNature.length() != tdDDODeductionNature.trim().length())
					{
						errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[22] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3048);
					}
					else if(! objRecVal.isValidNatureOfDeduction(tdDDODeductionNature))
					{
						errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[22] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3023);
					}
					else
					{
						
						//For Correction Files with Transaction Type M whether combination of Mode,TAN & DDODeductionNature is unique or not


						
						if(objReadFVAL3.stmtType.equals(TBAF_TYPE_OF_STMT_CORR))
						{
							//If the TAN is a structurally valid one and not in one of the Three defined constants then the combination of TAN,MODE & NATURE OF DEDUCTION should be unique
							if(! objRecVal.checkTanAgainstConstants(tdTAN))
							{
								String modeTANNatOfDed = tdRevMode.trim() + tdTAN.trim() + tdDDODeductionNature ;
								if(! objReadFVAL3.corrTDConflict.add(modeTANNatOfDed))
								{
									errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[22] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3056);
								}

								if(! tdDDORegNo.equals(TBAF_FIELD_NULL) && ! tdDDORegNo.equals(TBAF_FIELD_SEPERATOR))
								{
									String modeDDORegNoDeductNat = tdRevMode.trim() + tdDDORegNo.trim() + tdDDODeductionNature;
									if (! objReadFVAL3.corrTDConflict.add(modeDDORegNoDeductNat))
									{
										errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[22] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3059);
									}

								}
								if(! tdDDOCode.equals(TBAF_FIELD_NULL) && ! tdDDOCode.equals(TBAF_FIELD_SEPERATOR))
								{
									String modeDDOCodeDeductNat = tdRevMode.trim() + tdDDOCode.trim() + tdDDODeductionNature;
									if (! objReadFVAL3.corrTDConflict.add(modeDDOCodeDeductNat))
									{
										errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[22] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3060);
									}

								}

							}
							
							else
							{
								if(! tdDDORegNo.equals(TBAF_FIELD_NULL) && ! tdDDORegNo.equals(TBAF_FIELD_SEPERATOR))
								{
									String modeDDORegNoDeductNat = tdRevMode.trim() + tdDDORegNo.trim() + tdDDODeductionNature;
									if (! objReadFVAL3.corrTDConflict.add(modeDDORegNoDeductNat))
									{
										errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[22] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3059);
									}

								}
								if(! tdDDOCode.equals(TBAF_FIELD_NULL) && ! tdDDOCode.equals(TBAF_FIELD_SEPERATOR))
								{
									String modeDDOCodeDeductNat = tdRevMode.trim() + tdDDOCode.trim() + tdDDODeductionNature;
									if (! objReadFVAL3.corrTDConflict.add(modeDDOCodeDeductNat))
									{
										errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[22] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3060);
									}

								}
							}
						}
						else if(objReadFVAL3.stmtType.equals(TBAF_TYPE_OF_STMT_ORIG)	)
						{
							//For Checking whether the tan or DDO Registration Number or DDO Code with the same form already exists more than one time in the file or not.
							if(! objRecVal.checkTanAgainstConstants(tdTAN))
							{
								String tanDeductNat = tdTAN.trim() + tdDDODeductionNature ;
								if (objReadFVAL3.hashSetMonthYear.contains(tanDeductNat))
								{
									errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[22] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3034);
								}
								else
								{
									//	Add TAN & Deduction Nature or DDO Registration Number Deduction Nature or DDO Code and Deduction Nature into HashSet.
									objReadFVAL3.hashSetMonthYear.add(tanDeductNat);
								}
								if(! tdDDORegNo.equals(TBAF_FIELD_NULL) && ! tdDDORegNo.equals(TBAF_FIELD_SEPERATOR))
								{
									String ddoRegNoDeductNat = tdDDORegNo.trim() + tdDDODeductionNature ;
									if (objReadFVAL3.hashSetMonthYear.contains(ddoRegNoDeductNat))
									{
										errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[22] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3041);
									}
									else
									{
										//	Add TAN & Deduction Nature or DDO Registration Number Deduction Nature or DDO Code and Deduction Nature into HashSet.
										objReadFVAL3.hashSetMonthYear.add(ddoRegNoDeductNat);
									}
								}
								if(! tdDDOCode.equals(TBAF_FIELD_NULL) && ! tdDDOCode.equals(TBAF_FIELD_SEPERATOR))
								{
									String ddoCodeDeductNat = tdDDOCode.trim() + tdDDODeductionNature;
									if (objReadFVAL3.hashSetMonthYear.contains(ddoCodeDeductNat))
									{
										errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[22] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3042);
									}
									else
									{
										//	Add TAN & Deduction Nature or DDO Registration Number Deduction Nature or DDO Code and Deduction Nature into HashSet.
										objReadFVAL3.hashSetMonthYear.add(ddoCodeDeductNat);
									}
								}
							} 



							if(objRecVal.checkTanAgainstConstants(tdTAN))
							{
								if(! tdDDORegNo.equals(TBAF_FIELD_NULL) && ! tdDDORegNo.equals(TBAF_FIELD_SEPERATOR))
								{
									String ddoRegNoDeductNat = tdDDORegNo.trim() + tdDDODeductionNature ;
									if (objReadFVAL3.hashSetMonthYear.contains(ddoRegNoDeductNat))
									{
										errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[22] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3041);
									}
									else
									{
										//	Add TAN & Deduction Nature or DDO Registration Number Deduction Nature or DDO Code and Deduction Nature into HashSet.
										objReadFVAL3.hashSetMonthYear.add(ddoRegNoDeductNat);
									}
								}
								if(! tdDDOCode.equals(TBAF_FIELD_NULL) && ! tdDDOCode.equals(TBAF_FIELD_SEPERATOR))
								{
									String ddoCodeDeductNat = tdDDOCode.trim() + tdDDODeductionNature;
									if (objReadFVAL3.hashSetMonthYear.contains(ddoCodeDeductNat))
									{
										errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[22] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3042);
									}
									else
									{
										//	Add TAN & Deduction Nature or DDO Registration Number Deduction Nature or DDO Code and Deduction Nature into HashSet.
										objReadFVAL3.hashSetMonthYear.add(ddoCodeDeductNat);
									}
								}
							}
							/*	else
			    	{
			    		String tanDeductNat = tdTAN.trim() + tdDDODeductionNature ;
			    		if (objReadFVAL3.hashSetMonthYear.contains(tanDeductNat))
						 {
							errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[22] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3034);
						 }
						else
						 {
							//	Add TAN & Deduction Nature or DDO Registration Number Deduction Nature or DDO Code and Deduction Nature into HashSet.
							objReadFVAL3.hashSetMonthYear.add(tanDeductNat);
						 }
			    	}*/


						}
							
							
							double tAmt = 0.00;
							double tLastAmt=0.00;
							double remAmt = 0.00;
							double lastRemAmt=0.00;
							try{
								
								tAmt = Double.parseDouble(taxAmt);
							
								remAmt = Double.parseDouble(remittedAmt);
								if(tdRevMode.equals(TBAF_REVISION_MODE_UPDATE))
								{
								lastRemAmt=Double.parseDouble(tdLastRemittedAmt);
								tLastAmt=Double.parseDouble(tdLastTaxAmt);
								}
							}
							catch(Exception e)
							{
								Log.tbaf_log.error("Exception:",e);
								tAmt = 0.00;
								remAmt = 0.00;
							}
							
							
							//For 24Q to keep track of Amounts(Total TAX and Remitted) for added and deleted TD
							if(NATURE_OF_DEDUCTION[0].equals(tdDDODeductionNature))
							{
									if(objReadFVAL3.stmtType.equals(TBAF_TYPE_OF_STMT_CORR))
								{
									if(tdRevMode.equals(TBAF_REVISION_MODE_ADD))
									{
										cBeanTD.setTotalTDAddedIn24Q(cBeanTD.getTotalTDAddedIn24Q()+1);
										cBeanTD.setTotalTaxAddedTD24Q(cBeanTD.getTotalTaxAddedTD24Q()+tAmt);
										cBeanTD.setRemittedAmtAddedTD24Q(cBeanTD.getRemittedAmtAddedTD24Q()+remAmt);
									}
									else if(tdRevMode.equals(TBAF_REVISION_MODE_DEL))
									{
										cBeanTD.setTotalTDDeletedIn24Q(cBeanTD.getTotalTDDeletedIn24Q()+1);
										cBeanTD.setTotalTaxDeletedTD24Q(cBeanTD.getTotalTaxDeletedTD24Q()+tAmt);
										cBeanTD.setRemittedAmtDeletedTD24Q(cBeanTD.getRemittedAmtDeletedTD24Q()+remAmt);
									}
									else if(tdRevMode.equals(TBAF_REVISION_MODE_UPDATE))
									{
										cBeanTD.setTotalTDUpdatedIn24Q(cBeanTD.getTotalTDUpdatedIn24Q()+1);
										if(tdDDODeductionNature.equals(tdLastDDODeductionNature))
										{
											cBeanTD.setTotalTaxUpdatedTD24Q(cBeanTD.getTotalTaxUpdatedTD24Q()+(tAmt-tLastAmt));
											cBeanTD.setRemittedAmtUpdatedTD24Q(cBeanTD.getRemittedAmtUpdatedTD24Q()+(remAmt-lastRemAmt));
										}
										else
										{
											cBeanTD.setTotalTaxUpdatedTD24Q(cBeanTD.getTotalTaxUpdatedTD24Q()+tAmt);
											cBeanTD.setRemittedAmtUpdatedTD24Q(cBeanTD.getRemittedAmtUpdatedTD24Q()+remAmt);	
										}
									}
								}
								else
								{
									
									cBeanTD.setTotalTaxAddedTD24Q(cBeanTD.getTotalTaxAddedTD24Q()+tAmt);
									cBeanTD.setRemittedAmtAddedTD24Q(cBeanTD.getRemittedAmtAddedTD24Q()+remAmt);
								}
							}
							
							//For 26Q to keep track of Amounts(Total TAX and Remitted) for added and deleted TD
							if(NATURE_OF_DEDUCTION[1].equals(tdDDODeductionNature))
							{
								if(objReadFVAL3.stmtType.equals(TBAF_TYPE_OF_STMT_CORR))
								{
							
									if(tdRevMode.equals(TBAF_REVISION_MODE_ADD))
									{
										cBeanTD.setTotalTDAddedIn26Q(cBeanTD.getTotalTDAddedIn26Q()+1);
										cBeanTD.setTotalTaxAddedTD26Q(cBeanTD.getTotalTaxAddedTD26Q()+tAmt);
										cBeanTD.setRemittedAmtAddedTD26Q(cBeanTD.getRemittedAmtAddedTD26Q()+remAmt);
									}
									else if(tdRevMode.equals(TBAF_REVISION_MODE_DEL))
									{
										cBeanTD.setTotalTDDeletedIn26Q(cBeanTD.getTotalTDDeletedIn26Q()+1);
										cBeanTD.setTotalTaxDeletedTD26Q(cBeanTD.getTotalTaxDeletedTD26Q()+tAmt);
										cBeanTD.setRemittedAmtDeletedTD26Q(cBeanTD.getRemittedAmtDeletedTD26Q()+remAmt);
									}
									else if(tdRevMode.equals(TBAF_REVISION_MODE_UPDATE))
									{
										cBeanTD.setTotalTDUpdatedIn26Q(cBeanTD.getTotalTDUpdatedIn26Q()+1);
										if(tdDDODeductionNature.equals(tdLastDDODeductionNature))
										{
											cBeanTD.setTotalTaxUpdatedTD26Q(cBeanTD.getTotalTaxUpdatedTD26Q()+(tAmt-tLastAmt));
											cBeanTD.setRemittedAmtUpdatedTD26Q(cBeanTD.getRemittedAmtUpdatedTD26Q()+(remAmt-lastRemAmt));
										}
										else
										{
											cBeanTD.setTotalTaxUpdatedTD26Q(cBeanTD.getTotalTaxUpdatedTD26Q()+tAmt);
											cBeanTD.setRemittedAmtUpdatedTD26Q(cBeanTD.getRemittedAmtUpdatedTD26Q()+remAmt);
										}
									}
								}
								else
								{
									cBeanTD.setTotalTaxAddedTD26Q(cBeanTD.getTotalTaxAddedTD26Q()+tAmt);
									cBeanTD.setRemittedAmtAddedTD26Q(cBeanTD.getRemittedAmtAddedTD26Q()+remAmt);
								}
							}
										
						
							
							//For 27Q to keep track of Amounts(Total TAX and Remitted) for added and deleted TD
						if(NATURE_OF_DEDUCTION[2].equals(tdDDODeductionNature))
						{
							if(objReadFVAL3.stmtType.equals(TBAF_TYPE_OF_STMT_CORR))
							{
								if(tdRevMode.equals(TBAF_REVISION_MODE_ADD))
								{
									cBeanTD.setTotalTDAddedIn27Q(cBeanTD.getTotalTDAddedIn27Q()+1);
									cBeanTD.setTotalTaxAddedTD27Q(cBeanTD.getTotalTaxAddedTD27Q()+tAmt);
									cBeanTD.setRemittedAmtAddedTD27Q(cBeanTD.getRemittedAmtAddedTD27Q()+remAmt);
								}
								else if(tdRevMode.equals(TBAF_REVISION_MODE_DEL))
								{
									cBeanTD.setTotalTDDeletedIn27Q(cBeanTD.getTotalTDDeletedIn27Q()+1);
									cBeanTD.setTotalTaxDeletedTD27Q(cBeanTD.getTotalTaxDeletedTD27Q()+tAmt);
									cBeanTD.setRemittedAmtDeletedTD27Q(cBeanTD.getRemittedAmtDeletedTD27Q()+remAmt);
								}
								else if(tdRevMode.equals(TBAF_REVISION_MODE_UPDATE))
								{
									cBeanTD.setTotalTDUpdatedIn27Q(cBeanTD.getTotalTDUpdatedIn27Q()+1);
									if(tdDDODeductionNature.equals(tdLastDDODeductionNature))
									{
									cBeanTD.setTotalTaxUpdatedTD27Q(cBeanTD.getTotalTaxUpdatedTD27Q()+(tAmt-tLastAmt));
									cBeanTD.setRemittedAmtUpdatedTD27Q(cBeanTD.getRemittedAmtUpdatedTD27Q()+(remAmt-lastRemAmt));
									}
									else
									{
										cBeanTD.setTotalTaxUpdatedTD27Q(cBeanTD.getTotalTaxUpdatedTD27Q()+tAmt);
										cBeanTD.setRemittedAmtUpdatedTD27Q(cBeanTD.getRemittedAmtUpdatedTD27Q()+remAmt);
									}
								}
							}
						else
						{
							cBeanTD.setTotalTaxAddedTD27Q(cBeanTD.getTotalTaxAddedTD27Q()+tAmt);
							cBeanTD.setRemittedAmtAddedTD27Q(cBeanTD.getRemittedAmtAddedTD27Q()+remAmt);
						}
					}
							
							
							//For 27EQ to keep track of Amounts(Total TAX and Remitted) for added and deleted TD
						if(NATURE_OF_DEDUCTION[3].equals(tdDDODeductionNature))
						{
							if(objReadFVAL3.stmtType.equals(TBAF_TYPE_OF_STMT_CORR))
							{
								if(tdRevMode.equals(TBAF_REVISION_MODE_ADD))
								{
									cBeanTD.setTotalTDAddedIn27EQ(cBeanTD.getTotalTDAddedIn27EQ()+1);
									cBeanTD.setTotalTaxAddedTD27EQ(cBeanTD.getTotalTaxAddedTD27EQ()+tAmt);
									cBeanTD.setRemittedAmtAddedTD27EQ(cBeanTD.getRemittedAmtAddedTD27EQ()+remAmt);
								}
								else if(tdRevMode.equals(TBAF_REVISION_MODE_DEL))
								{
									cBeanTD.setTotalTDDeletedIn27EQ(cBeanTD.getTotalTDDeletedIn27EQ()+1);
									cBeanTD.setTotalTaxDeletedTD27EQ(cBeanTD.getTotalTaxDeletedTD27EQ()+tAmt);
									cBeanTD.setRemittedAmtDeletedTD27EQ(cBeanTD.getRemittedAmtDeletedTD27EQ()+remAmt);
								}
								else if(tdRevMode.equals(TBAF_REVISION_MODE_UPDATE))
								{
									cBeanTD.setTotalTDUpdatedIn27EQ(cBeanTD.getTotalTDUpdatedIn27EQ()+1);
									if(tdDDODeductionNature.equals(tdLastDDODeductionNature))
									{
									cBeanTD.setTotalTaxUpdatedTD27EQ(cBeanTD.getTotalTaxUpdatedTD27EQ()+(tAmt-tLastAmt));
									cBeanTD.setRemittedAmtUpdatedTD27EQ(cBeanTD.getRemittedAmtUpdatedTD27EQ()+(remAmt-lastRemAmt));
									}
									else
									{
										cBeanTD.setTotalTaxUpdatedTD27EQ(cBeanTD.getTotalTaxUpdatedTD27EQ()+tAmt);
										cBeanTD.setRemittedAmtUpdatedTD27EQ(cBeanTD.getRemittedAmtUpdatedTD27EQ()+remAmt);
									}
								}
							}
							else
							{
								cBeanTD.setTotalTaxAddedTD27EQ(cBeanTD.getTotalTaxAddedTD27EQ()+tAmt);
								cBeanTD.setRemittedAmtAddedTD27EQ(cBeanTD.getRemittedAmtAddedTD27EQ()+remAmt);
							}
						}
						




						// End of For Checking whether the tan with the same form already exists more than one time in the file or not.
						if(objReadFVAL3.stmtType.equals(TBAF_TYPE_OF_STMT_ORIG) || objReadFVAL3.stmtType.equals(TBAF_TYPE_OF_STMT_CORR))
						{
							if(tdDDODeductionNature.equals("24Q"))
							{

								cBeanTD.setCountNatOfDed24Q(cBeanTD.getCountNatOfDed24Q() + 1);
								
								if(taxAmt.equals(TBAF_FIELD_NULL) || taxAmt.equals(TBAF_FIELD_SEPERATOR))
								{
									cBeanTD.setTotalTaxTD24Q(cBeanTD.getTotalTaxTD24Q() + 0.00);
								}
								else
								{
									double tax24Amt = 0.00;
									try
									{
										tax24Amt = Double.parseDouble(taxAmt);
									}
									catch(Exception e)
									{
										Log.tbaf_log.error("Exception", e);
										tax24Amt = 0.00 ;
									}
									cBeanTD.setTotalTaxTD24Q(cBeanTD.getTotalTaxTD24Q() + tax24Amt);
								}
								if(remittedAmt.equals(TBAF_FIELD_NULL) || remittedAmt.equals(TBAF_FIELD_SEPERATOR))
								{
									cBeanTD.setTotalRemittanceTD24Q(cBeanTD.getTotalRemittanceTD24Q() + 0.00);
								}
								else
								{
									double remit24Amt = 0.00;
									try
									{
										remit24Amt = Double.parseDouble(remittedAmt);
										
									}
									catch(Exception e)
									{
										Log.tbaf_log.error("Exception", e);
										remit24Amt = 0.00 ;
									}
									if(objReadFVAL3.stmtType.equals(TBAF_TYPE_OF_STMT_CORR))
									{
										
										
									if((tdRevMode.equals(TBAF_REVISION_MODE_UPDATE)))
									{
										
										double lastRemit24Amt=0.00;
										if(!tdLastRemittedAmt.equals("^"))
										{
											lastRemit24Amt=Double.parseDouble(tdLastRemittedAmt);
										}
										cBeanTD.setTotalRemittanceTD24Q(cBeanTD.getTotalRemittanceTD24Q() + (remit24Amt-lastRemit24Amt));
									}
									else if((tdRevMode.equals(TBAF_REVISION_MODE_ADD)))
									{
										cBeanTD.setTotalRemittanceTD24Q(cBeanTD.getTotalRemittanceTD24Q() + remit24Amt);
									}
									else if((tdRevMode.equals(TBAF_REVISION_MODE_DEL)))
									{
										cBeanTD.setTotalRemittanceTD24Q(cBeanTD.getTotalRemittanceTD24Q() - remit24Amt);
									}
									}
									else
									{
										cBeanTD.setTotalRemittanceTD24Q(cBeanTD.getTotalRemittanceTD24Q() + remit24Amt);
									}
								}


								/*	if((! taxAmt.equals(TBAF_FIELD_NULL) && ! taxAmt.equals(TBAF_FIELD_SEPERATOR)) && (! remittedAmt.equals(TBAF_FIELD_NULL) && ! remittedAmt.equals(TBAF_FIELD_SEPERATOR)))
			    		{
		    		    cBeanTD.setTotalTaxTD24Q(cBeanTD.getTotalTaxTD24Q() + Double.parseDouble(taxAmt));
			    		    cBeanTD.setTotalRemittanceTD24Q(cBeanTD.getTotalRemittanceTD24Q() + Double.parseDouble(remittedAmt));

			    		}*/


								//objReadFVAL3.statReportBuffer.append(tdDDODeductionNature.trim() + TBAF_FIELD_SEPERATOR);
							}
							else if(tdDDODeductionNature.equals("26Q"))
							{
								
								cBeanTD.setCountNatOfDed26Q(cBeanTD.getCountNatOfDed26Q() + 1);
								if(taxAmt.equals(TBAF_FIELD_NULL) || taxAmt.equals(TBAF_FIELD_SEPERATOR))
								{
									cBeanTD.setTotalTaxTD26Q(cBeanTD.getTotalTaxTD26Q() + 0.00);
								}
								else
								{
									double tax26Amt = 0.00;
									try
									{
										tax26Amt = Double.parseDouble(taxAmt);
									}
									catch(Exception e)
									{
										Log.tbaf_log.error("Exception", e);
										tax26Amt = 0.00 ;
									}
									cBeanTD.setTotalTaxTD26Q(cBeanTD.getTotalTaxTD26Q() + tax26Amt);
								}
								if(remittedAmt.equals(TBAF_FIELD_NULL) || remittedAmt.equals(TBAF_FIELD_SEPERATOR))
								{
									cBeanTD.setTotalRemittanceTD26Q(cBeanTD.getTotalRemittanceTD26Q() + 0.00);
								}
								else
								{
									double remit26Amt = 0.00;
									try
									{
										remit26Amt = Double.parseDouble(remittedAmt);
									}
									catch(Exception e)
									{
										Log.tbaf_log.error("Exception", e);
										remit26Amt = 0.00 ;
									}
									if(objReadFVAL3.stmtType.equals(TBAF_TYPE_OF_STMT_CORR))
									{
										if(tdLastDDODeductionNature.equals("26Q"))
										{
											cBeanTD.setCountLastNatOfDed26Q(cBeanTD.getCountLastNatOfDed26Q() + 1);
										}
									if((tdRevMode.equals(TBAF_REVISION_MODE_UPDATE)))
									{
										double lastRemit26Amt=0.00;
										if(!tdLastRemittedAmt.equals("^"))
										{
											lastRemit26Amt=Double.parseDouble(tdLastRemittedAmt);
										}
										cBeanTD.setTotalRemittanceTD26Q(cBeanTD.getTotalRemittanceTD26Q() + (remit26Amt-lastRemit26Amt));
									}
									else if((tdRevMode.equals(TBAF_REVISION_MODE_ADD)))
									{
										cBeanTD.setTotalRemittanceTD26Q(cBeanTD.getTotalRemittanceTD26Q() + remit26Amt);
									}
									else if((tdRevMode.equals(TBAF_REVISION_MODE_DEL)))
									{
										cBeanTD.setTotalRemittanceTD26Q(cBeanTD.getTotalRemittanceTD26Q() - remit26Amt);
									}
									}
									else
									{
										cBeanTD.setTotalRemittanceTD26Q(cBeanTD.getTotalRemittanceTD26Q() + remit26Amt);
									}
									
								}


								/*	if((! taxAmt.equals(TBAF_FIELD_NULL) && ! taxAmt.equals(TBAF_FIELD_SEPERATOR)) && (! remittedAmt.equals(TBAF_FIELD_NULL) && ! remittedAmt.equals(TBAF_FIELD_SEPERATOR)))
			    		{
			    		  cBeanTD.setTotalTaxTD26Q(cBeanTD.getTotalTaxTD26Q() + Double.parseDouble(taxAmt));
			    		  cBeanTD.setTotalRemittanceTD26Q(cBeanTD.getTotalRemittanceTD26Q() + Double.parseDouble(remittedAmt));

			    		}*/


								//objReadFVAL3.statReportBuffer.append(tdDDODeductionNature.trim() + TBAF_FIELD_SEPERATOR);
							}
							else if(tdDDODeductionNature.equals("27Q"))
							{
								cBeanTD.setCountNatOfDed27Q(cBeanTD.getCountNatOfDed27Q() + 1);
								if(taxAmt.equals(TBAF_FIELD_NULL) || taxAmt.equals(TBAF_FIELD_SEPERATOR))
								{
									cBeanTD.setTotalTaxTD27Q(cBeanTD.getTotalTaxTD27Q() + 0.00);
								}
								else
								{
									double tax27Amt = 0.00;
									try
									{
										tax27Amt = Double.parseDouble(taxAmt);
									}
									catch(Exception e)
									{
										Log.tbaf_log.error("Exception", e);
										tax27Amt = 0.00 ;
									}
									cBeanTD.setTotalTaxTD27Q(cBeanTD.getTotalTaxTD27Q() + tax27Amt);
								}
								if(remittedAmt.equals(TBAF_FIELD_NULL) || remittedAmt.equals(TBAF_FIELD_SEPERATOR))
								{
									cBeanTD.setTotalRemittanceTD27Q(cBeanTD.getTotalRemittanceTD27Q() + 0.00);
								}
								else
								{
									double remit27Amt = 0.00;
									try
									{
										remit27Amt = Double.parseDouble(remittedAmt);
									}
									catch(Exception e)
									{
										Log.tbaf_log.error("Exception", e);
										remit27Amt = 0.00 ;
									}
									if(objReadFVAL3.stmtType.equals(TBAF_TYPE_OF_STMT_CORR))
									{
										if(tdLastDDODeductionNature.equals("27Q"))
										{
											cBeanTD.setCountLastNatOfDed27Q(cBeanTD.getCountLastNatOfDed27Q() + 1);
										}
									if((tdRevMode.equals(TBAF_REVISION_MODE_UPDATE)))
									{
										double lastRemit27Amt=0.00;
										if(!tdLastRemittedAmt.equals("^"))
										{
											lastRemit27Amt=Double.parseDouble(tdLastRemittedAmt);
										}
										cBeanTD.setTotalRemittanceTD27Q(cBeanTD.getTotalRemittanceTD27Q() + (remit27Amt-lastRemit27Amt));
									}
									else if((tdRevMode.equals(TBAF_REVISION_MODE_ADD)))
									{
										cBeanTD.setTotalRemittanceTD27Q(cBeanTD.getTotalRemittanceTD27Q() + remit27Amt);
									}
									else if((tdRevMode.equals(TBAF_REVISION_MODE_DEL)))
									{
										cBeanTD.setTotalRemittanceTD27Q(cBeanTD.getTotalRemittanceTD27Q() - remit27Amt);
									}
									}
									else
									{
										cBeanTD.setTotalRemittanceTD27Q(cBeanTD.getTotalRemittanceTD27Q() + remit27Amt);
									}
								}

								/*if((! taxAmt.equals(TBAF_FIELD_NULL) && ! taxAmt.equals(TBAF_FIELD_SEPERATOR)) && (! remittedAmt.equals(TBAF_FIELD_NULL) && ! remittedAmt.equals(TBAF_FIELD_SEPERATOR)))
			    		{
			    		   cBeanTD.setTotalTaxTD27Q(cBeanTD.getTotalTaxTD27Q() + Double.parseDouble(taxAmt));
			    		   cBeanTD.setTotalRemittanceTD27Q(cBeanTD.getTotalRemittanceTD27Q() + Double.parseDouble(remittedAmt));
			    		}*/

								//objReadFVAL3.statReportBuffer.append(tdDDODeductionNature.trim() + TBAF_FIELD_SEPERATOR);
							}
							else if(tdDDODeductionNature.equals("27EQ"))
							{
								cBeanTD.setCountNatOfDed27EQ(cBeanTD.getCountNatOfDed27EQ() + 1);
								if(taxAmt.equals(TBAF_FIELD_NULL) || taxAmt.equals(TBAF_FIELD_SEPERATOR))
								{
									cBeanTD.setTotalTaxTD27EQ(cBeanTD.getTotalTaxTD27EQ() + 0.00); 
								}
								else
								{

									double tax27EQAmt = 0.00;
									try
									{
										tax27EQAmt = Double.parseDouble(taxAmt);
									}
									catch(Exception e)
									{
										Log.tbaf_log.error("Exception", e);
										tax27EQAmt = 0.00 ;
									}
									cBeanTD.setTotalTaxTD27EQ(cBeanTD.getTotalTaxTD27EQ() + tax27EQAmt);
								}
								if(remittedAmt.equals(TBAF_FIELD_NULL) || remittedAmt.equals(TBAF_FIELD_SEPERATOR))
								{
									cBeanTD.setTotalRemittanceTD27EQ(cBeanTD.getTotalRemittanceTD27EQ() + 0.00);
								}
								else
								{
									double remit27EQAmt = 0.00;
									try
									{
										remit27EQAmt = Double.parseDouble(remittedAmt);
									}
									catch(Exception e)
									{
										Log.tbaf_log.error("Exception", e);
										remit27EQAmt = 0.00 ;
									}
									if(objReadFVAL3.stmtType.equals(TBAF_TYPE_OF_STMT_CORR))
									{
										if(tdLastDDODeductionNature.equals("27EQ"))
										{
											cBeanTD.setCountLastNatOfDed27EQ(cBeanTD.getCountLastNatOfDed24Q() + 1);
										}
									if((tdRevMode.equals(TBAF_REVISION_MODE_UPDATE)))
									{
										double lastRemit27EQAmt=0.00;
										if(!tdLastRemittedAmt.equals("^"))
										{
											lastRemit27EQAmt=Double.parseDouble(tdLastRemittedAmt);
										}
										cBeanTD.setTotalRemittanceTD27EQ(cBeanTD.getTotalRemittanceTD27EQ() + (remit27EQAmt-lastRemit27EQAmt));
									}
									else if((tdRevMode.equals(TBAF_REVISION_MODE_ADD)))
									{
										cBeanTD.setTotalRemittanceTD27EQ(cBeanTD.getTotalRemittanceTD27EQ() + remit27EQAmt);
									}
									else if((tdRevMode.equals(TBAF_REVISION_MODE_DEL)))
									{
										cBeanTD.setTotalRemittanceTD27EQ(cBeanTD.getTotalRemittanceTD27EQ() - remit27EQAmt);
									}
									}
									else
									{
										cBeanTD.setTotalRemittanceTD27EQ(cBeanTD.getTotalRemittanceTD27EQ() + remit27EQAmt);
									}
								}


								/*	if((! taxAmt.equals(TBAF_FIELD_NULL) && ! taxAmt.equals(TBAF_FIELD_SEPERATOR)) && (! remittedAmt.equals(TBAF_FIELD_NULL) && ! remittedAmt.equals(TBAF_FIELD_SEPERATOR)))
			    		{
			    		cBeanTD.setTotalTaxTD27EQ(cBeanTD.getTotalTaxTD27EQ() + Double.parseDouble(taxAmt));
			    		cBeanTD.setTotalRemittanceTD27EQ(cBeanTD.getTotalRemittanceTD27EQ() + Double.parseDouble(remittedAmt));
			    		}*/

								//objReadFVAL3.statReportBuffer.append(tdDDODeductionNature.trim() + TBAF_FIELD_SEPERATOR);
							}
							else
							{
								//objReadFVAL3.statReportBuffer.append("-" + TBAF_FIELD_SEPERATOR);
							}
						}
						
						if(objReadFVAL3.stmtType.equals(TBAF_TYPE_OF_STMT_CORR) && tdRevMode.equals(TBAF_REVISION_MODE_UPDATE))
						{
							if(tdLastDDODeductionNature.equals("24Q"))
							{
								cBeanTD.setCountLastNatOfDed24Q(cBeanTD.getCountLastNatOfDed24Q() + 1);
							}
							if(tdLastDDODeductionNature.equals("26Q"))
							{
								cBeanTD.setCountLastNatOfDed26Q(cBeanTD.getCountLastNatOfDed26Q() + 1);
							}
							if(tdLastDDODeductionNature.equals("27Q"))
							{
								cBeanTD.setCountLastNatOfDed27Q(cBeanTD.getCountLastNatOfDed27Q() + 1);
							}
							if(tdLastDDODeductionNature.equals("27EQ"))
							{
								cBeanTD.setCountLastNatOfDed27EQ(cBeanTD.getCountLastNatOfDed27EQ() + 1);
							}
						}
						//objReadFVAL3.statReportBuffer.append(tdDDODeductionNature + TBAF_FIELD_SEPERATOR);
					}

				}
			}
			
			//Gauri added else if condition for CR 89435, FVU 1.9
			else if(Integer.parseInt(objReadFVAL3.finYear) >= 2026) {
				if(! tdDDODeductionNature.equals(TBAF_FIELD_NULL) && ! tdDDODeductionNature.equals(TBAF_FIELD_SEPERATOR)) {
					errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[22] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_5058);
				}
				
			}

			//End of Nature Of Deduction Validation

			/**
			 *	Validation of DDO Mapping/Update(Field No. 23 Of DDO Transaction Detail Record)
			 *	This field is not applicable from FY 2026-27, CR 89435 FVU 1.9
			 */

			if (Integer.parseInt(objReadFVAL3.finYear) < 2026 && (objReadFVAL3.stmtType.equals(TBAF_TYPE_OF_STMT_ORIG) || (objReadFVAL3.stmtType.equals(TBAF_TYPE_OF_STMT_CORR) //Gauri added FY condition for CR 89425, FVU 1.9
					&& (tdRevMode.equals(TBAF_REVISION_MODE_ADD) || tdRevMode.equals(TBAF_REVISION_MODE_UPDATE)))))

			{
				if (! tdDDOMapping.equals(TBAF_FIELD_NULL) && ! tdDDOMapping.equals(TBAF_FIELD_SEPERATOR))
				{

					if(tdDDOMapping.trim().length() > TBAF_DDO_MAPPING_LEN || tdDDOMapping.length() > TBAF_DDO_MAPPING_LEN)
					{
						errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[23] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3005);
					}
					else if(tdDDOMapping.trim().length() != tdDDOMapping.length())
					{
						errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[23] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3048);
					}
					else if(!objRecVal.isValidDDOMappingFlag(tdDDOMapping))
					{
						errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[23] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3025);
					}
					else
					{

						if(tdDDOMapping.equals("A"))
						{
							cBeanTD.setCountDDOTDAdd(cBeanTD.getCountDDOTDAdd() + 1); //To determine the count of  records with Mode 'A'
							if(taxAmt.trim().equals("0.00")) //Transaction Detail Records with TDS/TCS transferred amount (0.00) (excluding records with mode D): [For Statistics File]  
							{
								cBeanTD.setTdRecordZeroTaxExD(cBeanTD.getTdRecordZeroTaxExD() + 1); 

							}

							if(! objRecVal.checkTanAgainstConstants(tdTAN))
							{



								String tanMap = (String)cBeanTD.tanDDOMappingHM.put(tdTAN,tdDDOMapping) ;
								if(tanMap != null)
								{


									if(! tanMap.equals(tdDDOMapping))           //Check to see whether a valid TAN is associated with more than one Mapping
									{



										errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[23] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3045);
									}
								}

							}
						}
						else if(tdDDOMapping.equals("U"))
						{
							cBeanTD.setCountDDOTDUpdated(cBeanTD.getCountDDOTDUpdated() + 1);//To determine the count of  records with Mode 'U'

							if(taxAmt.trim().equals("0.00")) //Transaction Detail Records with TDS/TCS transferred amount (0.00) (excluding records with mode D):  [For Statistics File] 
							{
								cBeanTD.setTdRecordZeroTaxExD(cBeanTD.getTdRecordZeroTaxExD() + 1); 

							}


							if(! objRecVal.checkTanAgainstConstants(tdTAN))
							{
								String tanMap = (String)cBeanTD.tanDDOMappingHM.put(tdTAN,tdDDOMapping) ;
								if(tanMap != null)
								{
									if(! tanMap.equals(tdDDOMapping))           //Check to see whether a valid TAN is associated with more than one Mapping
									{
										errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[23] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3045);
									}
								}
							}
						}
						else if(tdDDOMapping.equals("D"))
						{
							cBeanTD.setCountDDOTDDeleted(cBeanTD.getCountDDOTDDeleted() + 1);//To determine the count of  records with Mode 'D'
							if(! objRecVal.checkTanAgainstConstants(tdTAN))
							{
								if(cBeanTD.tanDDOMapNull.contains(tdTAN))
								{
									errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[23] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3049);
								}

								else
								{
									String tanMap = (String)cBeanTD.tanDDOMappingHM.put(tdTAN,tdDDOMapping) ;
									if(tanMap != null)
									{

										errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[23] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3046);

									}
								}
							}
						}
						// objReadFVAL3.statReportBuffer.append(tdDDOMapping.trim() + TBAF_FIELD_SEPERATOR);
					}
				}
				else
				{
					if(taxAmt.trim().equals("0.00")) //Transaction Detail Records with TDS/TCS transferred amount (0.00) (excluding records with mode D): [For Statistics File]  
					{
						cBeanTD.setTdRecordZeroTaxExD(cBeanTD.getTdRecordZeroTaxExD() + 1); 

					}
					if(cBeanTD.tanDDOMappingHM.containsKey(tdTAN))
					{
						if(cBeanTD.tanDDOMappingHM.get(tdTAN).equals("D"))
						{
							errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[23] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3050);
						}
						else
						{

						}
					}
					else
					{

						cBeanTD.tanDDOMapNull.add(tdTAN);  //Added By Subhankar to check that in case Tan with null Mapping Flag if that Tan again comes With Flag 'D' Then it should be rejected. 
						//objReadFVAL3.statReportBuffer.append("-" + TBAF_FIELD_SEPERATOR);
					}
				}
			}	
			//Gauri added FY condition for CR 89435, FVU 1.9
			else if(Integer.parseInt(objReadFVAL3.finYear) < 2026 && (objReadFVAL3.stmtType.equals(TBAF_TYPE_OF_STMT_CORR) && tdRevMode.equals(TBAF_REVISION_MODE_DEL)))
			{
				if (!tdDDOMapping.equals(TBAF_FIELD_NULL) && !tdDDOMapping.equals(TBAF_FIELD_SEPERATOR))
				{
					objReadFVAL3.invalidRecord = true;
					errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[23] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3015);
				}
			}
			//Gauri added else if condition for CR 89435, FVU 1.9
			else if(Integer.parseInt(objReadFVAL3.finYear) >= 2026) {
				if(! tdDDOMapping.equals(TBAF_FIELD_NULL) && ! tdDDOMapping.equals(TBAF_FIELD_SEPERATOR)) {
					errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[23] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_5058);
				}
				
			}

			//End of Nature Of DDO Mapping/Update Validation


			/**
			 *	Validation of DDO Serial No.(Field No. 24 Of DDO Transaction Detail Record)
			 */
			if (objReadFVAL3.stmtType.equals(TBAF_TYPE_OF_STMT_ORIG) || objReadFVAL3.stmtType.equals(TBAF_TYPE_OF_STMT_CORR))
			{
				if (!tdDDOSerialNo.equals(TBAF_FIELD_NULL) && !tdDDOSerialNo.equals(TBAF_FIELD_SEPERATOR))
				{
					errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[24] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3012);
				}
				else
				{
					//objReadFVAL3.statReportBuffer.append("-" + TBAF_FIELD_SEPERATOR);
				}
			}

			//End of Nature Of DDO Serial No. Validation



			

			/**
			 *	Validation of Last DDO Registraton Number(Field No. 26 Of DDO Transaction Detail Record)
			 */
			
			if(objReadFVAL3.stmtType.equals(TBAF_TYPE_OF_STMT_CORR) && objReadFVAL3.transType.equals(TBAF_TRANSACTION_TYPE_M) && tdRevMode.equals(TBAF_REVISION_MODE_UPDATE)) 

			{
				if(objRecVal.checkTanAgainstConstants(tdLastTAN) || isLastTANInvalid)
				{
					if(! tdLastDDORegNo.equals(TBAF_FIELD_NULL) && ! tdLastDDORegNo.equals(TBAF_FIELD_SEPERATOR))
					{
						if(tdLastDDORegNo.trim().length() != TBAF_DDO_REG_NO_LEN || tdLastDDORegNo.length() != TBAF_DDO_REG_NO_LEN)
						{
							errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[26] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3014);
						}
						else if(tdLastDDORegNo.length() != tdLastDDORegNo.trim().length())
						{
							errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[26] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3048);
						}
						else if((objRecVal.isAlphaNum(tdLastDDORegNo)))
						{
							errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[26] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3030);
						}
						else if(objReadFVAL3.deductCatgry.equals("A"))
						{
							if(! (tdLastDDORegNo.substring(0,3).equals("CGV")) || (objRecVal.isInt(tdLastDDORegNo.substring(4,9))) || !(objRecVal.isIn_DDO_REG_NO_MOD(tdDDORegNo.substring(4,9),tdDDORegNo.substring(9,10))))
							{
								errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[26] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3026);
							}
						}
						else if(objReadFVAL3.deductCatgry.equals("S")) 
						{
							if(! (tdLastDDORegNo.substring(0,3).equals("SGV")) || (objRecVal.isInt(tdLastDDORegNo.substring(4,9))) || !(objRecVal.isIn_DDO_REG_NO_MOD(tdDDORegNo.substring(4,9),tdDDORegNo.substring(9,10))))
							{
								errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[26] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3027);
							}
						}
						else
						{
							//objReadFVAL3.statReportBuffer.append(tdDDORegNo + TBAF_FIELD_SEPERATOR);
						}


					}
					else
					{
						//objReadFVAL3.statReportBuffer.append("-" + TBAF_FIELD_SEPERATOR);
					}
				}  
			}
			else if (!tdLastDDORegNo.equals(TBAF_FIELD_NULL) && !tdLastDDORegNo.equals(TBAF_FIELD_SEPERATOR))
			{
				errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[26] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3012);
			}
			
			
			/**
			 *	Validation of Last DDO Code(Field No. 27 Of DDO Transaction Detail Record)
			 */

			/////////////////////////////////////////////////////////////////////////////////////////////
			if (objReadFVAL3.stmtType.equals(TBAF_TYPE_OF_STMT_CORR) && objReadFVAL3.transType.equals(TBAF_TRANSACTION_TYPE_M) && tdRevMode.equals(TBAF_REVISION_MODE_UPDATE)) 
			{
				if(isLastTANInvalid || objRecVal.checkTanAgainstConstants(tdLastTAN))
				{
					if( (tdLastDDORegNo.equals(TBAF_FIELD_NULL) || tdLastDDORegNo.equals(TBAF_FIELD_SEPERATOR)) && (! tdLastDDOCode.equals(TBAF_FIELD_NULL) && ! tdLastDDOCode.equals(TBAF_FIELD_SEPERATOR)))
					{
						if(tdLastDDOCode.trim().length() > TBAF_DDO_CODE_LEN || tdLastDDOCode.length() > TBAF_DDO_CODE_LEN)
						{
							errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[27] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3031);
						}
						else if(tdLastDDOCode.length() != tdLastDDOCode.trim().length())
						{
							errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[27] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3048);
						}
						else if((objRecVal.isAlphaNum(tdLastDDOCode)))
						{
							errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[27] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3030);
						}
						else if(objRecVal.CheckZeros(tdLastDDOCode.trim()))
						{
							errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[27] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3035);
						}
						else
						{
							//objReadFVAL3.statReportBuffer.append(tdDDOCode.trim() + TBAF_FIELD_SEPERATOR);
						}
					}
					else if( (! tdLastDDORegNo.equals(TBAF_FIELD_NULL) && ! tdLastDDORegNo.equals(TBAF_FIELD_SEPERATOR)) && (! tdLastDDOCode.equals(TBAF_FIELD_NULL) && ! tdLastDDOCode.equals(TBAF_FIELD_SEPERATOR)))
					{
						if(tdLastDDOCode.trim().length() > TBAF_DDO_CODE_LEN || tdLastDDOCode.length() > TBAF_DDO_CODE_LEN)
						{
							errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[27] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3031);
						}
						else if(tdLastDDOCode.length() != tdLastDDOCode.trim().length())
						{
							errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[27] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3048);
						}
						else if((objRecVal.isAlphaNum(tdLastDDOCode)))
						{
							errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[27] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3030);
						}
						else if(objRecVal.CheckZeros(tdLastDDOCode.trim()))
						{
							errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[27] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3035);
						}
						else
						{
							// objReadFVAL3.statReportBuffer.append(tdDDOCode.trim() + TBAF_FIELD_SEPERATOR);
						}
					}
					else if( (tdLastDDORegNo.equals(TBAF_FIELD_NULL)  || tdLastDDORegNo.equals(TBAF_FIELD_SEPERATOR)) && (tdLastDDOCode.equals(TBAF_FIELD_NULL)  || tdLastDDOCode.equals(TBAF_FIELD_SEPERATOR)))
					{
						errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[26] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3062);
						//objReadFVAL3.statReportBuffer.append("-" + TBAF_FIELD_SEPERATOR);
					}
					else
					{
						//objReadFVAL3.statReportBuffer.append("-" + TBAF_FIELD_SEPERATOR);
					}
				}
				else
				{
					if((! tdLastDDOCode.equals(TBAF_FIELD_NULL) && ! tdLastDDOCode.equals(TBAF_FIELD_SEPERATOR)))
					{
						if(tdLastDDOCode.trim().length() > TBAF_DDO_CODE_LEN || tdLastDDOCode.length() > TBAF_DDO_CODE_LEN)
						{
							errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[27] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3031);
						}
						else if(tdLastDDOCode.length() != tdLastDDOCode.trim().length())
						{
							errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[27] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3048);
						}
						else if((objRecVal.isAlphaNum(tdLastDDOCode)))
						{
							errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[27] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3030);
						}
						else if(objRecVal.CheckZeros(tdLastDDOCode.trim()))
						{
							errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[27] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3035);
						}
						else
						{
							// objReadFVAL3.statReportBuffer.append(tdDDOCode.trim() + TBAF_FIELD_SEPERATOR);
						}
					}
					else
					{
						//objReadFVAL3.statReportBuffer.append("-" + TBAF_FIELD_SEPERATOR);
					}
				}
			}
			else if (!tdLastDDOCode.equals(TBAF_FIELD_NULL) && !tdLastDDOCode.equals(TBAF_FIELD_SEPERATOR))
			{
				errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[26] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3012);
			}
			
			/////////////////////////////////////////////////////////////////////////////////////////////



			
			//End of Nature Of DDO Filler 7 Validation


			/**
			 *	Validation of DDO Filler 9(Field No. 28 Of DDO Transaction Detail Record)
			 */

			if (objReadFVAL3.stmtType.equals(TBAF_TYPE_OF_STMT_CORR) && objReadFVAL3.transType.equals(TBAF_TRANSACTION_TYPE_M) && tdRevMode.equals(TBAF_REVISION_MODE_UPDATE)) 
			{
				if (! (tdDDOMapping.trim().equals("D")))
				{
					if (tdLastTaxAmt.equals(TBAF_FIELD_NULL) || tdLastTaxAmt.equals(TBAF_FIELD_SEPERATOR))
					{
						errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[28] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3001);
					}
					else if (objRecVal.isFieldNull(tdLastTaxAmt))
					{
						errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[28] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3002);
					}
					else if (tdLastTaxAmt.trim().length() > 15 || tdLastTaxAmt.length() > 15)
					{
						errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[28] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3005);
					}
					else if (tdLastTaxAmt.trim().length() != tdLastTaxAmt.length())  
					{
						errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[28] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3002);
					}
					else if (objReadFVAL3.stmtType.equals(TBAF_TYPE_OF_STMT_ORIG)
							|| (objReadFVAL3.stmtType.equals(TBAF_TYPE_OF_STMT_CORR) 
									&& (objReadFVAL3.transType.equals(TBAF_TRANSACTION_TYPE_C4) || tdRevMode.equals(TBAF_REVISION_MODE_ADD) || tdRevMode.equals(TBAF_REVISION_MODE_UPDATE)) ))
					{
						if (objRecVal.isDecimalNumber(tdLastTaxAmt) || !tdLastTaxAmt.endsWith("00"))
						{
							if(objRecVal.isInt(tdLastTaxAmt))
							{
								errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[28] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3002);
							}
							else
							{
								errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[28] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3022);
							}
						}
						else if (Double.parseDouble(tdLastTaxAmt.trim()) < 0.00)
						{
							errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[28] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3002);
						}
						
					}
				}
			}

			else if (!tdLastTaxAmt.equals(TBAF_FIELD_NULL) && !tdLastTaxAmt.equals(TBAF_FIELD_SEPERATOR))
			{
				errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[28] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3012);
			}



			//End of Nature Of DDO Filler 9 Validation


			/**
			 *	Validation of DDO Last Nature of Deduction(Field No. 29 Of DDO Transaction Detail Record)
			 */


			if (objReadFVAL3.stmtType.equals(TBAF_TYPE_OF_STMT_CORR) && objReadFVAL3.transType.equals(TBAF_TRANSACTION_TYPE_M) && tdRevMode.equals(TBAF_REVISION_MODE_UPDATE))
			{
				if((tdDDOMapping.trim().equals("D")) && (! tdLastDDODeductionNature.equals(TBAF_FIELD_NULL) && ! tdLastDDODeductionNature.equals(TBAF_FIELD_SEPERATOR)))  
				{
					errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[29] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3040);
				}
				else if(! (tdDDOMapping.trim().equals("D")))
				{

					if (tdLastDDODeductionNature.equals(TBAF_FIELD_NULL) || tdLastDDODeductionNature.equals(TBAF_FIELD_SEPERATOR))
					{
						errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[22] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3001);
					}
					else if (objRecVal.isFieldNull(tdLastDDODeductionNature))
					{
						errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[22] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3002);
					}
					else if (tdLastDDODeductionNature.trim().length() > TBAF_DDO_NAT_OF_DED_LEN || tdLastDDODeductionNature.length() > TBAF_DDO_NAT_OF_DED_LEN)
					{
						errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[22] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3005);
					}
					else if(tdLastDDODeductionNature.length() != tdLastDDODeductionNature.trim().length())
					{
						errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[22] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3048);
					}
					
					//Gauri added this changes for Form Type for CR 89435, FVU 1.9
					else if(Integer.parseInt(objReadFVAL3.finYear) < 2026) {						
						if(! objRecVal.isValidNatureOfDeduction(tdLastDDODeductionNature)) {
							errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[22] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3023);
						}			
					}
					else if(Integer.parseInt(objReadFVAL3.finYear) >= 2026) {						
						if(! objRecVal.isValidFormType(tdLastDDODeductionNature)) {
							errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[22] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_5065);
						}			
					}
					
					
					
					double tAmt = 0.00;
					double tLastAmt=0.00;
					double remAmt = 0.00;
					double lastRemAmt=0.00;
					try{
						
						tAmt = Double.parseDouble(taxAmt);
					
						remAmt = Double.parseDouble(remittedAmt);
						if(tdRevMode.equals(TBAF_REVISION_MODE_UPDATE))
						{
						lastRemAmt=Double.parseDouble(tdLastRemittedAmt);
						tLastAmt=Double.parseDouble(tdLastTaxAmt);
						}
					}
					catch(Exception e)
					{
						Log.tbaf_log.error("Exception:",e);
						tAmt = 0.00;
						remAmt = 0.00;
					}
					
					
					if(!tdDDODeductionNature.equals(tdLastDDODeductionNature))
					{
						if(NATURE_OF_DEDUCTION[0].equals(tdLastDDODeductionNature))
						{
							if(tdRevMode.equals(TBAF_REVISION_MODE_UPDATE))
							{
								cBeanTD.setTotalTaxUpdatedTD24Q(cBeanTD.getTotalTaxUpdatedTD24Q()-tLastAmt);
								cBeanTD.setRemittedAmtUpdatedTD24Q(cBeanTD.getRemittedAmtUpdatedTD24Q()-lastRemAmt);
							}
							
						}
					
					
						//For 26Q to keep track of Amounts(Total TAX and Remitted) for added and deleted TD
						if(NATURE_OF_DEDUCTION[1].equals(tdLastDDODeductionNature))
						{
							
							if(tdRevMode.equals(TBAF_REVISION_MODE_UPDATE))
							{
								cBeanTD.setTotalTaxUpdatedTD26Q(cBeanTD.getTotalTaxUpdatedTD26Q()-tLastAmt);
								cBeanTD.setRemittedAmtUpdatedTD26Q(cBeanTD.getRemittedAmtUpdatedTD26Q()-lastRemAmt);
							}
						}
					
					
						//For 27Q to keep track of Amounts(Total TAX and Remitted) for added and deleted TD
						if(NATURE_OF_DEDUCTION[2].equals(tdLastDDODeductionNature))
						{
							if(tdRevMode.equals(TBAF_REVISION_MODE_UPDATE))
							{
								cBeanTD.setTotalTaxUpdatedTD27Q(cBeanTD.getTotalTaxUpdatedTD27Q()-tLastAmt);
								cBeanTD.setRemittedAmtUpdatedTD27Q(cBeanTD.getRemittedAmtUpdatedTD27Q()-lastRemAmt);
							}
						}
					
					
						//For 27EQ to keep track of Amounts(Total TAX and Remitted) for added and deleted TD
						if(NATURE_OF_DEDUCTION[3].equals(tdLastDDODeductionNature))
						{
							if(tdRevMode.equals(TBAF_REVISION_MODE_UPDATE))
							{
								cBeanTD.setTotalTaxUpdatedTD27EQ(cBeanTD.getTotalTaxUpdatedTD27EQ()-tLastAmt);
								cBeanTD.setRemittedAmtUpdatedTD27EQ(cBeanTD.getRemittedAmtUpdatedTD27EQ()-lastRemAmt);
							}
						}
					}
				}
			}
			else if (!tdLastDDODeductionNature.equals(TBAF_FIELD_NULL) && !tdLastDDODeductionNature.equals(TBAF_FIELD_SEPERATOR))
			{
				errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[29] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3012);
			}


			//End of Nature Of DDO Filler 10 Validation


			/**
			 *	Validation of DDO Filler 11(Field No. 30 Of DDO Transaction Detail Record)
			 */

			if (objReadFVAL3.stmtType.equals(TBAF_TYPE_OF_STMT_CORR) && objReadFVAL3.transType.equals(TBAF_TRANSACTION_TYPE_M) && tdRevMode.equals(TBAF_REVISION_MODE_UPDATE)) 
			{
				if (!tdFiller_11.equals(TBAF_FIELD_NULL) && !tdFiller_11.equals(TBAF_FIELD_SEPERATOR))
				{
					errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[30] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3012);
				}
				else
				{
					//objReadFVAL3.statReportBuffer.append("-" + TBAF_FIELD_SEPERATOR);
				}
			}



			//End of Nature Of DDO Filler 11 Validation


			/**
			 *	Validation of DDO Filler 12(Field No. 31 Of DDO Transaction Detail Record)
			 */



			if (objReadFVAL3.stmtType.equals(TBAF_TYPE_OF_STMT_ORIG) || objReadFVAL3.stmtType.equals(TBAF_TYPE_OF_STMT_CORR))
			{
				if (!tdFiller_12.equals(TBAF_FIELD_NULL) && !tdFiller_12.equals(TBAF_FIELD_SEPERATOR))
				{
					errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[31] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3012);
				}
				else
				{
					//objReadFVAL3.statReportBuffer.append("-" + TBAF_FIELD_SEPERATOR);
				}
			}

			//End of Nature Of DDO Filler 12 Validation


			/**
			 *	Validation of DDO Filler 13(Field No. 32 Of DDO Transaction Detail Record)
			 */

			if (objReadFVAL3.stmtType.equals(TBAF_TYPE_OF_STMT_ORIG) || objReadFVAL3.stmtType.equals(TBAF_TYPE_OF_STMT_CORR))
			{
				if (!tdFiller_13.equals(TBAF_FIELD_NULL) && !tdFiller_13.equals(TBAF_FIELD_SEPERATOR))
				{
					errStrBuff.append(TBAF_TDREC + lineNo + "^" + TBAF_TD_FIELD[32] + "^" + tdSerialNo + TBAF_ERR_SEP + TBAF_FV_3012);
				}
				else
				{
					//objReadFVAL3.statReportBuffer.append("-" + TBAF_FIELD_SEPERATOR);
				}
			}



			//End of Nature Of DDO Filler 13 Validation





			/**.
			 * Counting the Number of DDO Records with Rs.(0.00) as Tax Amount
			 */
			if (objReadFVAL3.stmtType.equals(TBAF_TYPE_OF_STMT_ORIG) 
					|| objReadFVAL3.stmtType.equals(TBAF_TYPE_OF_STMT_CORR))
			{
				if (objReadFVAL3.invalidTaxAmt == false)
				{
					if (taxAmt == null || taxAmt == "" || taxAmt.trim().equals("0.00"))
					{
						objReadFVAL3.zeroTDTaxAmtCounter++;
					}
				}
			} 

			/**
			 *	Check for Deleted Tax Amount is Greater than Added Tax Amount. 
			 */
			if (objReadFVAL3.stmtType.equals(TBAF_TYPE_OF_STMT_CORR) 
					&& !objReadFVAL3.transType.equals(TBAF_TRANSACTION_TYPE_C4))
			{
				if (objReadFVAL3.totalTaxDeleted > objReadFVAL3.totalTaxAdded)
				{
					objReadFVAL3.taxDeletedIsGreater = true;
				}
				else
				{
					objReadFVAL3.taxDeletedIsGreater = false;
				}
			}






		} // end of try block            //End of Added By Subhankar
		/*********************************DDO Transaction Detail Record Validation Ends******************************/
		catch (Exception e)
		{
			Log.tbaf_log.error("Exception", e);
			e.printStackTrace();
		}
			}
}	//	End of tdFieldValidator method	