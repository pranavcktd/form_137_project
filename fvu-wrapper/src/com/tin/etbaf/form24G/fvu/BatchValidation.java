/**
 *	Class: BatchValidation.java
 */
package com.tin.etbaf.form24G.fvu;
import java.util.*;

import com.tin.etbaf.form24G.bean.BHTDCompBean;
import com.tin.etbaf.form24G.bean.RawFileBean;
import com.tin.etbaf.form24G.bean.TBAFFileStatistics;
import com.tin.etbaf.form24G.util.Log;
import com.tin.etbaf.form24G.util.Parameters;
/**
 *	This class is for validating the format of the fields of  
 *	Batch Header Record	for Regular and Correction Statements as per 	
 *	ETBAF File Format Version 1.3. This class implements TBAFInterface 
 *	
 *	@author TCS
 *	@version 12
 */
public class BatchValidation extends RecordValidation implements TBAFInterface
{
	private String lineNo = null; // Line Number
	private String recType = null; // Record Type
	private String batchNo = null; // Batch number
	private String tbafAIN = null; // AIN
	private String tbafLastAIN = null; // Last AIN
	private String aoName = null; // AO Name
	private String aoAddr1 = null; // AO Address 1
	private String aoAddr2 = null; // AO Address 2
	private String aoAddr3 = null; // AO Address 3
	private String aoAddr4 = null; // AO Address 4
	private String aoCity = null; // AO City
	private String tbafAOStateCode = null; // AO State Code
	private String tbafAOPinCode = null; // AO Pin Code
	private String tbafAOStdCode = null; // AO STD Code
	private String tbafAOPhoneNo = null; // AO Phone Number 
	private String aoEmailID = null; // AO e-Mail ID
	private String personName = null; // Responsible Person Name	
	private String personDesig = null; // Responsible Person Designation
	private String batchUpdtIndicator = null;//Added by subhankar
	private String bhFiller_5 = null; //Gauri added this field for CR 89435, FVU 1.9                         aoTitle
	private String bhFiller_2 = null; //Gauri added this field for CR 89435, FVU 1.9 //Batch Filler 2        aoFirstName               //Added by subhankar
	private String bhFiller_3 = null; //Gauri added this field for CR 89435, FVU 1.9 //Batch Filler 3        aoMiddleName               //Added by subhankar
	private String bhFiller_4 = null; //Gauri added this field for CR 89435, FVU 1.9 //Batch Filler 4        aoLastName               //Added by subhankar
	private String originalRRR = null; // Original RRR Number
	private String previousRRR = null; // Previous RRR Number
	private String prnNumber = null; // Provisinal Receipt Numer
	private String prnDate = null; // Provisional Receipt Date
	//private String transferVoucherMonth = null; //Month of Transfer Voucher   //Added by subhankar
	private String rPersonAddr1 = null; //Responsible person Address1        //Added by subhankar
	private String rPersonAddr2 = null; //Responsible person Address2        //Added by subhankar
	private String rPersonAddr3 = null; //Responsible person Address3        //Added by subhankar
	private String rPersonAddr4 = null; //Responsible person Address4        //Added by subhankar
	private String rPersonCity = null; //Responsible person City             //Added by subhankar
	private String rPersonState = null; //Responsible person State           //Added by subhankar 
	private String rPersonPin = null; //Responsible person PinCode           //Added by subhankar
	private String rPersonStdCode = null; //Responsible person STD Code      //Added by subhankar 
	private String rPersonPhoneNo = null; //Responsible person Phone No.     //Added by subhankar
	private String rPersonEmailID = null; //Responsible person Email Id      //Added by subhankar
	private String rPersonMobileNo = null; //Responsible person Mobile No.   //Added by subhankar
	private String countryCode = null; //Gauri added countryCode for CR 89435, FVU 1.9 //Has the statement been filed Earlier      //Added by subhankar
	private String stateName = null; //State Name                                              //Added by subhankar
	private String ministryName = null; //Ministry/Department Name                             //Added by subhankar
	private String subMinistryName = null; //Sub Ministry Name                                 //Added by subhankar
	private String subMinistryName_O = null; //Sub Ministry Name(Others)                       //Added by subhankar
	//private String countTD24Q = null; //count of 24Q Transactions
	//private String totalTax24Q = null; //Control total of tax deducted/collected (Sum of BAS_TAX, SUR, EDU_CESS) for 24Q
	//private String totalRemittedAmt24Q = null; //Total TDS/TCS remitted to Government account (AG/Pr CCA) for 24Q
	//private String countTD26Q = null; //count of 26Q Transactions
	//private String totalTax26Q = null; //Control total of tax deducted/collected (Sum of BAS_TAX, SUR, EDU_CESS) for 26Q
	//private String totalRemittedAmt26Q = null; //Total TDS/TCS remitted to Government account (AG/Pr CCA) for 26Q
	//private String countTD27Q = null; //count of 27Q Transactions
	//private String totalTax27Q = null; //Control total of tax deducted/collected (Sum of BAS_TAX, SUR, EDU_CESS) for 27Q
	//private String totalRemittedAmt27Q = null; //Total TDS/TCS remitted to Government account (AG/Pr CCA) for 27Q
	//private String countTD27EQ = null; //count of 27EQ Transactions
	//private String totalTax27EQ = null; //Control total of tax deducted/collected (Sum of BAS_TAX, SUR, EDU_CESS) for 27EQ
	//private String totalRemittedAmt27EQ = null; //Total TDS/TCS remitted to Government account (AG/Pr CCA) for 27EQ
	private String paoRegistrationNo = null; //PAO/DTO/CDDO registration no.                       //Added by subhankar
	//private String distinctDDOCount = null; //Count of Distinct DDO                                //Added by subhankar
	//private String totalRemittedAmt = null; //Total TDS/TCS remitted to Government account (AG/Pr CCA)        //Added by subhankar   
	//private String countDDOAdd = null; //Count of DDO Added                                                  //Added by subhankar
	//private String countDDOUpdated = null; //Count of DDO Updated                                             //Added by subhankar 
	//private String countDDODeleted = null; //Count of DDO Deleted                                            //Added by subhankar
	private String receiptNo = null; //Receipt Number                                                        //Added by subhankar 
	private String bhFiller_6 = null; //Gauri added Mobile number field for CR 89435, FVU 1.9					mobileNoOfAO
	private String TANofAO = null; //Gauri added TAN number of AO field for CR 89435, FVU 1.9
	private String specialTAN = null; //Gauri added Special TAN for AO field for CR 89435, FVU 1.9
	private String stateAGcode = null; //Gauri added State AG for AO field for CR 89435, FVU 1.9
	private String bhFiller_7 = null; //Gauri added Title field for Responsible person details for CR 89435, FVU 1.9		rTitle 
	private String rFirstName = null; //Gauri added First name field for Responsible person details for CR 89435, FVU 1.9
	private String rMiddleName = null; //Gauri added Middle Name for Responsible person details for CR 89435, FVU 1.9
	private String rLastName = null; //Gauri added Last Name for Responsible person details for CR 89435, FVU 1.9
	private String rCountryCode = null; //Gauri added Country code for Responsible person details for CR 89435, FVU 1.9

	private String bhRecordHash = null; // Batch Header Record Hash
	RecordValidation objRecVal = new RecordValidation();
	
	//Gauri added this for CR 89435, FVU 1.9 to get AO name in SSR
	public static String firstNameAO = null;
	public static String middleNameAO = null;
	public static String lastNameAO = null;
	
	
	/*****************************************BATCH HEADER VALIDATION STARTS*****************************************/
	/**
	 *	bhFieldValidator method is called from TBAFFormatValidator class 
	 *	to validate the fields present in the Batch Header Record.
	 *	 		
	 *	@param objReadFVAL2 (Object of TBAFFormatValidator class)
	 *	@param lineCountP (Logical line number for each line in the file)
	 *	@param bhRecord (The Batch Header Record is taken as a String, line no.2 of the file)  	
	 *	@param errStrBuff (Object of TBAFErrorStringBuffer class. A string buffer in which the errors are appended)
	 *	
	 *	@return void 
	 *	@throws Exception
	 */
	void bhFieldValidator(TBAFFormatValidator objReadFVAL2,BHTDCompBean cBeanBH, int lineCountP, 
						  String bhRecord, TBAFErrorStringBuffer errStrBuff) throws Exception
	{
		try
		{
			boolean fieldFoundBol = false;
			boolean carretBol = true;
			lineNo = "";
			recType = "";
			/**
			 *	Tokenizing the Batch Header Record. Seperate the fields and '^' in the record.
			 *	'^' is the field seperator
			 */
			RawFileBean rawFileBean = RawFileBean.getInstance();
			StringTokenizer StrTokenizerBH = new StringTokenizer(bhRecord, TBAF_FIELD_SEPERATOR, true);
			int caretCounter = 0;
			int localFieldCountBH = 1;
			while (StrTokenizerBH.hasMoreTokens())
			{
				String val = StrTokenizerBH.nextToken();
				fieldFoundBol = false;
				if ((val.equals(TBAF_FIELD_SEPERATOR) && carretBol) || val.trim().length() == 0)
				{
					fieldFoundBol = true;
				}
				if (val.equals(TBAF_FIELD_SEPERATOR))
				{
					carretBol = true;
					if (caretCounter == 0 && localFieldCountBH == 1)
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
				//	If the number of fields found in the Batch Header Record is greater than 71, reject the file
				//If fields are added we have to add the number here!
				if (localFieldCountBH > 80)   //Added by subhankar
				{
					break;
				}
				if (fieldFoundBol)
				{
					switch (localFieldCountBH)
					{
						case 1 :
							lineNo = val;
							break;
						case 2 :
							recType = val;
							break;
						case 3 :
							batchNo = val;
							break;
						case 4 :
							objReadFVAL2.transType = val;
							break;
						case 5 :
							tbafAIN = val;
							break;
						case 6 :
							tbafLastAIN = val;
							break;
						case 7 :
							aoName = val;
							break;
						case 8 :
							aoAddr1 = val;
							break;
						case 9 :
							aoAddr2 = val;
							break;
						case 10 :
							aoAddr3 = val;
							break;
						case 11 :
							aoAddr4 = val;
							break;
						case 12 :
							aoCity = val;
							break;
						case 13 :
							tbafAOStateCode = val;
							break;
						case 14 :
							tbafAOPinCode = val;
							break;
						case 15 :
							tbafAOStdCode = val;
							break;
						case 16 :
							tbafAOPhoneNo = val;
							break;
						case 17 :
							aoEmailID = val;
							break;
						case 18 :
							personName = val;
							break;
						case 19 :
							personDesig = val;
							break;
						case 20 :
							System.out.println("CASE:- "+val);
							objReadFVAL2.finYear = val;	
							break;
						case 21 :
							objReadFVAL2.lastFinYear = val;
							break;
						case 22 :
							objReadFVAL2.deductCatgry = val;
							break;
						case 23 :
							objReadFVAL2.lastDeductCatgry = val;
							break;
						case 24 :
							batchUpdtIndicator = val;               //Modified by subhankar							
							//objReadFVAL2.quarter = val;
							break;
						case 25 :
							bhFiller_2 = val;
							//aoFirstName = val;	//Gauri added this field for CR 89435, FVU 1.9
							//objReadFVAL2.lastQuarter = val;
							//setaoFirstName(aoFirstName);
							break;
						case 26 :
							bhFiller_3 = val;
							//aoMiddleName = val;	//Gauri added this field for CR 89435, FVU 1.9
							//natureOfDeduction = val;
							//setaoMiddleName(aoMiddleName);
							break;
						case 27 :
							bhFiller_4 = val;
							//aoLastName = val;	//Gauri added this field for CR 89435, FVU 1.9
							//lastNatureOfDeduction = val;   //End of Modified by subhankar
							//setaoLastName(aoLastName);
							break;
						case 28 :
							objReadFVAL2.countTD = val;
							break;
						case 29 :
							objReadFVAL2.totalTax = val;
							break;
						case 30 :
							originalRRR = val;
							break;
						case 31 :
							previousRRR = val;
							break;
						case 32 :
							prnNumber = val;
							break;
						case 33 :
							prnDate = val;
							break;
							
						//Added By Subhankar For TBAF new File Format 24G	
						case 34 :
							objReadFVAL2.transferVoucherMonth = val;
							break;
						case 35 :
							rPersonAddr1 = val;
							break;
						case 36 :
							rPersonAddr2 = val;
							break;
						case 37 :
							rPersonAddr3 = val;
							break;
						case 38 :
							rPersonAddr4 = val;
							break;
						case 39 :
							rPersonCity = val;
							break;
						case 40 :
							rPersonState = val;
							break;
						case 41 :
							rPersonPin = val;
							break;
						case 42 :
							rPersonStdCode = val;
							break;
						case 43 :
							rPersonPhoneNo = val;
							break;
						case 44 :
							rPersonEmailID = val;
							break;
						case 45 :
							rPersonMobileNo = val;
							break;
						case 46 :
							//isStatementFiledEarlier = val;           //Gauri added Country field for CR 89435, FVU 1.9
							countryCode= val;
							break;
						case 47 :
							stateName = val;
							break;
						case 48 :
							ministryName = val;
							break;
						case 49 :
							subMinistryName = val;
							break;
						case 50 :
							subMinistryName_O = val;
							break;
						case 51 :
							cBeanBH.setCountTD24Q(val);
							break;
						case 52 :
							cBeanBH.setTotalTax24Q(val);
							break;
						case 53 :
							cBeanBH.setTotalRemittedAmt24Q(val);
							break;
						case 54 :
							cBeanBH.setCountTD26Q(val);
							break;
						case 55 :
							cBeanBH.setTotalTax26Q(val);
							break;
						case 56 :
							cBeanBH.setTotalRemittedAmt26Q(val);
							break;
						case 57 :
							cBeanBH.setCountTD27Q(val);
							break;
						case 58 :
							cBeanBH.setTotalTax27Q(val);
							break;
						case 59 :
							cBeanBH.setTotalRemittedAmt27Q(val);
							break;
						case 60 :
							cBeanBH.setCountTD27EQ(val);
							break;
						case 61 :
							cBeanBH.setTotalTax27EQ(val);
							break;
						case 62 :
							cBeanBH.setTotalRemittedAmt27EQ(val);
							break;
						case 63 :
							paoRegistrationNo = val;
							break;
						case 64 :
							cBeanBH.setDistinctDDOCount(val);
							break;
						case 65 :
							cBeanBH.setTotalRemittedAmt(val);
							break;
						case 66 :
							cBeanBH.setCountDDOAdd(val);
							break;
						case 67 :
							cBeanBH.setCountDDOUpdated(val);
							break;
						case 68 :
							cBeanBH.setCountDDODeleted(val);
							break;
						case 69 :
							receiptNo = val;
							break;
						case 70 :
							bhFiller_5 = val;
							//aoTitle = val;      //Gauri added this field for CR 89435, FVU 1.9
							break;							
						case 71 :
							bhFiller_6 = val;
							//mobileNoOfAO = val;     //Gauri added this new field for CR 89435, FVU 1.9
							break;
						case 72 :
							TANofAO = val;     //Gauri added this new field for CR 89435, FVU 1.9
							break;
						case 73 :
							specialTAN = val;     //Gauri added this new field for CR 89435, FVU 1.9
							break;
						case 74 :
							stateAGcode = val;     //Gauri added this new field for CR 89435, FVU 1.9
							break;
						case 75 :
							bhFiller_7 = val;
							//rTitle = val;     //Gauri added this new field for CR 89435, FVU 1.9
							break;
						case 76 :
							rFirstName = val;     //Gauri added this new field for CR 89435, FVU 1.9
							break;
						case 77 :
							rMiddleName = val;     //Gauri added this new field for CR 89435, FVU 1.9
							break;
						case 78 :
							rLastName = val;     //Gauri added this new field for CR 89435, FVU 1.9
							break;
						case 79 :
							rCountryCode = val;     //Gauri added this new field for CR 89435, FVU 1.9
							break;
						                      //End of  TBAF new File Format 24G	
						case 80 :
							bhRecordHash = val;
							break;
							
							
					} // end of switch case
					localFieldCountBH++;
				} // end of If
			} // end of inner while loop
			
			//	Number of carets in the Batch Header Record must be exactly equal to 33.
			//If fields are added we have to add the number here!
			// if (caretCounter != 33)
			if (caretCounter != 79) //Added By Subhankar
			{
				objReadFVAL2.inValidCaretCount = true;
				errStrBuff.append(TBAF_BHREC + "2" + "^" + "-" + "^^" + TBAF_FV_2000);
				return;
			}
			 
			/**
			 *	Validation of LINE NUMBER(Field No.1 of Batch Header Record)
			 *	
			 *	Line Number should not be NULL.
			 * 	Line Number should be of length less than or equal to 9 digits. 
			 *	Line Number should not have leading and trailing spaces.
			 *	Line Number should not have spaces in between the number.
			 *	Line Number should always be in sequence.  	
			 *
			 */
			
			if (lineNo.equals(TBAF_FIELD_NULL) || lineNo.equals(TBAF_FIELD_SEPERATOR))
			{
				errStrBuff.append(TBAF_BHREC + "-" + "^" + TBAF_BH_FIELD[1] + "^^" + TBAF_FV_2001);  
				lineNo = "-";
			}
			else if (objRecVal.isFieldNull(lineNo))
			{
				errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[1] + "^^" + TBAF_FV_2003); 
			}
			else if (lineNo.trim().length() > 9 || lineNo.length() > 9)
			{
				errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[1] + "^^" + TBAF_FV_2005); 
			}
			else if (lineNo.length() <= 9)
			{
				String bhLineNum = objRecVal.trimInnerSpaces(lineNo);
				if (objRecVal.isInt(bhLineNum))
				{
					errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[1] + "^^" + TBAF_FV_2003); 
				}
				else if (!bhLineNum.equals(lineNo))
				{
					errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[1] + "^^" + TBAF_FV_2003); 
				}
				else if (Integer.parseInt(lineNo.trim()) != lineCountP)
				{
					errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[1] + "^^" + TBAF_FV_2004);  
				} 
			}
			else if (lineNo.trim().length() != lineNo.length())
			{
				errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[1] + "^^" + TBAF_FV_2002);  
			}	//	End of LINE NUMBER Validation
			
			/**	
			 *	Validation of RECORD TYPE(Field No.2 of Batch Header Record) 	
			 * 
			 * 	Record Type should not be NULL.
			 *	Record Type should be "BH" for Batch Header Record. 
			 * 	Values other than "BH" are invalid.
			 */
			if (recType.equals(TBAF_FIELD_NULL) || recType.equals(TBAF_FIELD_SEPERATOR))
			{
				errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[2] + "^^" + TBAF_FV_2006);
			}
			else if (!recType.equals(TBAF_BH_REC))
			{
				errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[2] + "^^" + TBAF_FV_2007);
			}	//	End of RECORD TYPE Validation
			
			
			
			/**
			 *	Validation of BATCH NUMBER(Field No.3 of Batch Header Record)	
			 *
			 *	Batch Number should not be NULL.
			 * 	Batch Number should be of length less than or equal to 9 digits. 
			 *	Batch Number should not have leading and trailing spaces.
			 *	Batch Number should not have spaces in between the number.
			 *	Batch Number should always have the value as '1'.
			 */
			if (batchNo.equals(TBAF_FIELD_NULL) || batchNo.equals(TBAF_FIELD_SEPERATOR))
			{
				errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[3] + "^^" + TBAF_FV_2008); // new error code added in jan16
			}
			else if (objRecVal.isFieldNull(batchNo))
			{
				errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[3] + "^^" + TBAF_FV_2009);  // new error code added in jan16
			}
			else if (batchNo.trim().length() > 9 || batchNo.length() > 9)
			{
				errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[3] + "^^" + TBAF_FV_2011);  // new error code added in jan16
			}
			else if (batchNo.length() <= 9)
			{
				String batchNumber = objRecVal.trimInnerSpaces(batchNo);
				if (objRecVal.isInt(batchNumber))
				{
					errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[3] + "^^" + TBAF_FV_2003); // new error code added in jan16
				}
				else if (!batchNumber.equals(batchNo))
				{
					errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[3] + "^^" + TBAF_FV_2002);  // new error code added in jan16
				}
				else if (Integer.parseInt(batchNo.trim()) != 1) 
				{  
					errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[3] + "^^" + TBAF_FV_2010);  // new error code added in jan16
				}
			}
			else if (batchNo.trim().length() != batchNo.length())
			{
				errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[3] + "^^" + TBAF_FV_2002);  // new error code added in jan16
			}	//	End of BATCH NUMBER Validation
			
			/** 
			 * 	Validation of TRANSACTION TYPE(Field No. 4 Of Batch Header Record)
			 * 
			 * 	For Correction Statement this field should not be NULL.
			 *	Valid Transaction Types are C1, C2, C3 and C4.
			 *	C1 - AO Level Correction (No DDO Records will be present).
			 *	C2 - AO Level + DDO Level Correction.
			 *	C3 - DDO Level Correction.
			 *	C4 - AIN Level Correction + DDO.
			 *	Values other than C1,C2,C3 and C4 should be rejected. 	
			 */
			if (objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_CORR))
			{
				
				if (objReadFVAL2.transType.equals(TBAF_FIELD_NULL) 
					|| objReadFVAL2.transType.equals(TBAF_FIELD_SEPERATOR))
				{
					errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[4] + "^^" + TBAF_FV_2012);
					objReadFVAL2.errorFoundInBH = true;
				}
				else if (objRecVal.isFieldNull(objReadFVAL2.transType))
				{
					errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[4] + "^^" + TBAF_FV_2013);
					objReadFVAL2.errorFoundInBH = true;
				}
				else if (!objReadFVAL2.transType.equals(TBAF_TRANSACTION_TYPE_X)
						&& !objReadFVAL2.transType.equals(TBAF_TRANSACTION_TYPE_M) )
					/*	&& !objReadFVAL2.transType.equals(TBAF_TRANSACTION_TYPE_C3)
						&& !objReadFVAL2.transType.equals(TBAF_TRANSACTION_TYPE_C4)) */
				{
					errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[4] + "^^" + TBAF_FV_2013);
					objReadFVAL2.errorFoundInBH = true;
				}
				else
				{
					objReadFVAL2.statReportBuffer.append(objReadFVAL2.transType + TBAF_FIELD_SEPERATOR);
				}
			}
			/**
			 * 	For Regular Statement this field must be NULL.
			 *	If any value is specified for Regular Statement, file should be rejected. 
			 */
			else if (objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_ORIG))
			{
				
				if (!objReadFVAL2.transType.equals(TBAF_FIELD_NULL) 
					&& !objReadFVAL2.transType.equals(TBAF_FIELD_SEPERATOR))
				{
					errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[4] + "^^" + TBAF_FV_2014);
					objReadFVAL2.errorFoundInBH = true;
				}
				else
				{
					objReadFVAL2.statReportBuffer.append("-" + TBAF_FIELD_SEPERATOR);
				}
			}
			
			else
			{
				objReadFVAL2.errorFoundInBH = true;
			}
			//	End of TRANSACTION TYPE Validation
			
			
			/**	
			 * 	Validation of AIN(Field No. 5 Of Batch Header Record)
			 * 
			 * 	This is a MANDATORY field irrespective of the statement type.
			 *	AIN should be an integer value with length exactly equal to 7 digits.
			 *	First digit should not be "0" (Zero).
			 *	AIN should follow the check digit validation. 
			 */
			if (tbafAIN.equals(TBAF_FIELD_NULL) || tbafAIN.equals(TBAF_FIELD_SEPERATOR))
			{
				errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[5] + "^^" + TBAF_FV_2100);  // new error code added in jan16
			}
			else if (objRecVal.isFieldNull(tbafAIN))
			{
				errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[5] + "^^" + TBAF_FV_2100); // new error code added in jan16
			}
			else if (tbafAIN.trim().length() != tbafAIN.length())
			{
				errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[5] + "^^" + TBAF_FV_2100);  // new error code added in jan16
			}
			else if (tbafAIN.trim().length() != 7 || tbafAIN.length() != 7)
			{
				errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[5] + "^^" + TBAF_FV_2100);  // new error code added in jan16
			}
			else if (objRecVal.isInt(tbafAIN))
			{
				errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[5] + "^^" + TBAF_FV_2100);  // new error code added in jan16
			}
			else if (tbafAIN.startsWith("0"))
			{
				errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[5] + "^^" + TBAF_FV_2100);  // new error code added in jan16
			}
			else if (objRecVal.checkID(tbafAIN))
			{
				errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[5] + "^^" + TBAF_FV_2100); // new error code added in jan16
			}
			
			/**
			 * 
			 * 
			 * For D type If a 7 digit Valid AIN is present then the same AIN should be present in FH also
			 * Added on 08 Dec 
			 */
			else if(objReadFVAL2.uploadBy.equals(TBAF_UPLOADED_BY_AO))
			{
				if(! tbafAIN.equals(objReadFVAL2.id))
				{
					errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[5] + "^^" + TBAF_FV_2019);
				}
				else
				{
					rawFileBean.setValue(tbafAIN,RawFileBean.AIN_FLDNUM); //Added by Bharath for Raw File Generation
					objReadFVAL2.statReportBuffer.append(tbafAIN + TBAF_FIELD_SEPERATOR);
				}
			}
			
			/** 
			 *	If the length of the AIN/Organization/TFC ID is 6 digits(Organization ID) or 5 digits(TFC ID)
			 *	append the AIN directly to statistics report string buffer.
			 */
			else if ((objReadFVAL2.id.trim().length() == 5 && objReadFVAL2.id.length() == 5) 
					  || (objReadFVAL2.id.trim().length() == 6 && objReadFVAL2.id.length() == 6))
			{
				rawFileBean.setValue(tbafAIN,RawFileBean.AIN_FLDNUM); //Added by Bharath for Raw File Generation
				objReadFVAL2.statReportBuffer.append(tbafAIN + TBAF_FIELD_SEPERATOR);
			}
			//	If the length of the AIN in FH is 7 digits, AIN in FH and BH should be equal.
			else if (objReadFVAL2.id.trim().length() == 7 
					 && objReadFVAL2.id.length() == 7 
					 && objReadFVAL2.invalidStatementType == false)
			{
				if (!tbafAIN.equals(objReadFVAL2.id))
				{
					errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[5] + "^^" + TBAF_FV_2019);
				}
				else
				{
					rawFileBean.setValue(tbafAIN,RawFileBean.AIN_FLDNUM); //Added by Bharath for Raw File Generation
					objReadFVAL2.statReportBuffer.append(tbafAIN + TBAF_FIELD_SEPERATOR);
				}
			}	// End of AIN Validation 
			
			/**	
			 *	Validation of LAST AIN(Field No. 6 Of Batch Header Record)	
			 * 
			 *	For Regular Statement this field must be NULL.
			 *	If any value is specified error should be displayed. 
			 */
			if (objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_ORIG) || objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_CORR) && ( objReadFVAL2.transType.equals(TBAF_TRANSACTION_TYPE_X) || objReadFVAL2.transType.equals(TBAF_TRANSACTION_TYPE_M) ) )
			{
				if (!tbafLastAIN.equals(TBAF_FIELD_NULL) && !tbafLastAIN.equals(TBAF_FIELD_SEPERATOR))
				{
					errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[6] + "^^" + TBAF_FV_2020);
				}
				else
				{
					objReadFVAL2.statReportBuffer.append("-" + TBAF_FIELD_SEPERATOR);
				}
			}
			//	For Correction Statement this field must NOT be NULL.	 	
		/*	else if (objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_CORR))
			{
			
				if (tbafLastAIN.equals(TBAF_FIELD_NULL) || tbafLastAIN.equals(TBAF_FIELD_SEPERATOR))
				{
					errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[6] + "^^" + TBAF_FV_2015);
				}
				else if (objRecVal.isFieldNull(tbafLastAIN))
				{
					errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[6] + "^^" + TBAF_FV_2016);
				}
				else if (tbafLastAIN.trim().length() != tbafLastAIN.length())
				{
					errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[6] + "^^" + TBAF_FV_2017);
				} */
			    /**	
			     *	For Transaction Type C4, it is NOT mandatory that AIN/Last AIN should be equal, so all the 
			     *	validations are to be  done as in AIN validation.
			     */
			/*	else if (objReadFVAL2.transType.equals(TBAF_TRANSACTION_TYPE_C4))
				{
					if (tbafLastAIN.trim().length() != 7 || tbafLastAIN.length() != 7)
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[6] + "^^" + TBAF_FV_2018);
					}
					else if (objRecVal.isInt(tbafLastAIN))
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[6] + "^^" + TBAF_FV_2016);
					}
					else if (tbafLastAIN.startsWith("0"))
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[6] + "^^" + TBAF_FV_2016);
					}
					else if (objRecVal.checkID(tbafLastAIN))
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[6] + "^^" + TBAF_FV_2016);
					}
					else
					{
						objReadFVAL2.statReportBuffer.append(tbafLastAIN + TBAF_FIELD_SEPERATOR);
					}
				}
				//	For Transaction Types C1,C2 and C3 it is MANDATORY that AIN/Last AIN should be wqual.
				else if (objReadFVAL2.transType.equals(TBAF_TRANSACTION_TYPE_C1)
						|| objReadFVAL2.transType.equals(TBAF_TRANSACTION_TYPE_C2)
						|| objReadFVAL2.transType.equals(TBAF_TRANSACTION_TYPE_C3))
				{
					if (!tbafLastAIN.equals(tbafAIN))
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[6] + "^^" + TBAF_FV_2021);
					}
					else
					{
						objReadFVAL2.statReportBuffer.append(tbafLastAIN + TBAF_FIELD_SEPERATOR);
					}
				}
				
			} 	*/
			
			// End of LAST AIN Validation
			
			/**
			 *	Validation of AO NAME(Field No. 7 Of Batch Header Record)	
			 *
			 *	This field is MANDATORY irrespective of Statement Type.
			 *  
			 *  For Statement Type Correction and Transaction Type X No AO NAME should be present
			 *  
			 *  Validations for Title as "Non-Individual" added for CR 89435, FVU 1.9
			 *  
			 */
			
			//Gauri added the year condition for FY CR 89435, FVU 1.9 in below exsisting code:: START
			//Removed the Non-Indivudual validation
			
		if(objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_ORIG) || (objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_CORR)) ) 
		{
			
			if (aoName.equals(TBAF_FIELD_NULL) || aoName.equals(TBAF_FIELD_SEPERATOR))
			{
				errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[7] + "^^" + TBAF_FV_2101);  // new error added in jan16  
			}
			else if (objRecVal.isFieldNull(aoName))
			{
				errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[7] + "^^" + TBAF_FV_2023);  
			}
			else if (aoName.length() > 75)
			{
				errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[7] + "^^" + TBAF_FV_2024);  
			}
			else if (! objRecVal.checkValidAOName(aoName))
			{
				errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[7] + "^^" + TBAF_FV_2023);  
			}
			else
			{
				rawFileBean.setValue(aoName,RawFileBean.AO_NAME_FLDNUM); //Added by Bharath for Raw File Generation
				objReadFVAL2.statReportBuffer.append(aoName + TBAF_FIELD_SEPERATOR);
			}
			
			
		}
		/*//CORR-REG CHANGE
		else if(Integer.parseInt(objReadFVAL2.finYear) >= 2025 && objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_ORIG) || (objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_CORR)))
		{
			if(aoTitle.equals("04")) {
				if (aoName.equals(TBAF_FIELD_NULL) || aoName.equals(TBAF_FIELD_SEPERATOR))
				{
					errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[7] + "^^" + TBAF_FV_5050);  
				}
				else if (objRecVal.isFieldNull(aoName))
				{
					errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[7] + "^^" + TBAF_FV_5050);  
				}
				else if (aoName.length() > 75)
				{
					errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[7] + "^^" + TBAF_FV_5050);  
				}
				else if (! objRecVal.checkAlphabets(aoName))
				{
					errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[7] + "^^" + TBAF_FV_5050);  
				}
				else
				{
					rawFileBean.setValue(aoName,RawFileBean.AO_NAME_FLDNUM); 
					objReadFVAL2.statReportBuffer.append('-' + TBAF_FIELD_SEPERATOR);
				}
			}
			else if(aoTitle.equals("01") || aoTitle.equals("02") || aoTitle.equals("03")){
				if(!(aoName.equals(TBAF_FIELD_NULL) || aoName.equals(TBAF_FIELD_SEPERATOR))) {
				errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[7] + "^^" + TBAF_FV_5042);
				}
				else
				{
					rawFileBean.setValue(aoName,RawFileBean.AO_NAME_FLDNUM); 
					objReadFVAL2.statReportBuffer.append('-' + TBAF_FIELD_SEPERATOR);
				}
			}
			
		}
		
		//Gauri added the year condition for FY CR 89435, FVU 1.9 in below exsisting code:: END
*/		
	/*	else if(objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_CORR))
		{
			if(objReadFVAL2.transType.equals(TBAF_TRANSACTION_TYPE_X))
			{
				if (!aoName.equals(TBAF_FIELD_NULL) 
						&& !aoName.equals(TBAF_FIELD_SEPERATOR))
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[7] + "^^" + TBAF_FV_2084);
						
					}
					else
					{
						objReadFVAL2.statReportBuffer.append("-" + TBAF_FIELD_SEPERATOR);
					}
			}
			else if(objReadFVAL2.transType.equals(TBAF_TRANSACTION_TYPE_M))
			{
				//Validation Type to be determined
				
			}
			
		} */
			
			
			
			
			
		    // End of AO NAME Validation
			
			
			/**	
			 * Validation of ADDRESS FIELDS IN Batch Header Record
			 * 
			 * Validation for Regular Statement & Correction Types X AND M.
			 * Address 1 is MANDATORY for Regular & Coreection Types M.
			 * Length should be less than or equal to 25 characters.
			 * Value should not be specified with TAB spaces.
			 */
			if (objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_ORIG)
					|| (objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_CORR)))
			{
				//	Validation of ADDRESS 1(Field No. 8 Of Batch Header Record)
				if (aoAddr1.equals(TBAF_FIELD_NULL) || aoAddr1.equals(TBAF_FIELD_SEPERATOR))
				{
					errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[8] + "^^" + TBAF_FV_2102);   // new error code added in jan16
				}
				else if (objRecVal.isFieldNull(aoAddr1))
				{
					errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[8] + "^^" + TBAF_FV_2023);  
				}
				else if (aoAddr1.length() > 25)
				{
					errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[8] + "^^" + TBAF_FV_2024);  
				}
				else if (! objRecVal.checkValidAOAddress(aoAddr1))
				{
					errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[8] + "^^" + TBAF_FV_2023);  
				}
				else
				{
					rawFileBean.setValue(aoAddr1,RawFileBean.AO_ADDR1_FLDNUM); //Added by Bharath for Raw File Generation
					objReadFVAL2.statReportBuffer.append(aoAddr1 + TBAF_FIELD_SEPERATOR);
				}
				/**
				 *	AO Address 2, AO Address 3, AO Address 4 are OPTIONAL fields.
				 *  Following validations are done when the user DOES NOT SPECIFY ANY VALUE in this field:
				 *	
				 *	(1)	Check for only TAB spaces are specified.
				 *	(2)	Check if greater than 25 blank spaces are specified.
				 *
				 *	Following validations are done when the user SPECIFIES ANY VALUE in this field:
				 *	
				 *	(1)	Check if the specified value is of length less than 25 characters.
				 *	(2)	Check if the specified value is not having TAB spaces.
				 */
				//	Validation of ADDRESS 2(Field No. 9 Of Batch Header Record)
				if (aoAddr2.equals(TBAF_FIELD_NULL) || aoAddr2.equals(TBAF_FIELD_SEPERATOR))
				{
					// Optional Field, No Error Checking
					
					rawFileBean.setValue(aoAddr2,RawFileBean.AO_ADDR2_FLDNUM); //Added by Bharath for Raw File Generation
					objReadFVAL2.statReportBuffer.append(" " + TBAF_FIELD_SEPERATOR);
				}
				else if (objRecVal.isFieldNull(aoAddr2))
				{
					if (objRecVal.checkTabSpaces(aoAddr2))
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[9] + "^^" + TBAF_FV_2023);  
					}
					else if (aoAddr2.length() > 25)
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[9] + "^^" + TBAF_FV_2024);  
					}
					else
					{
						
						rawFileBean.setValue(aoAddr2,RawFileBean.AO_ADDR2_FLDNUM); //Added by Bharath for Raw File Generation
						objReadFVAL2.statReportBuffer.append(" " + TBAF_FIELD_SEPERATOR);
					}
				}
				else
				{
					if (aoAddr2.length() > 25)
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[9] + "^^" + TBAF_FV_2024);  
					}
					else if (! objRecVal.checkValidAOAddress(aoAddr2))
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[9] + "^^" + TBAF_FV_2023);  
					}
					else
					{
						
						rawFileBean.setValue(aoAddr2,RawFileBean.AO_ADDR2_FLDNUM); //Added by Bharath for Raw File Generation
						objReadFVAL2.statReportBuffer.append(aoAddr2 + TBAF_FIELD_SEPERATOR);
					}
				}
				//	Validation of ADDRESS 3(Field No. 10 Of Batch Header Record)
				if (aoAddr3.equals(TBAF_FIELD_NULL) || aoAddr3.equals(TBAF_FIELD_SEPERATOR))
				{
					//	Optional Field, No Error Checking
					rawFileBean.setValue(aoAddr3,RawFileBean.AO_ADDR3_FLDNUM); //Added by Bharath for Raw File Generation
					objReadFVAL2.statReportBuffer.append(" " + TBAF_FIELD_SEPERATOR);
				}
				else if (objRecVal.isFieldNull(aoAddr3))
				{
					if (objRecVal.checkTabSpaces(aoAddr3))
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[10] + "^^" + TBAF_FV_2023);  
						
					}
					else if (aoAddr3.length() > 25)
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[10] + "^^" + TBAF_FV_2024);  
					}
					else
					{
						rawFileBean.setValue(aoAddr3,RawFileBean.AO_ADDR3_FLDNUM); //Added by Bharath for Raw File Generation
						objReadFVAL2.statReportBuffer.append(" " + TBAF_FIELD_SEPERATOR);
					}
				}
				else
				{
					if (aoAddr3.length() > 25)
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[10] + "^^" + TBAF_FV_2024);  
					}
					else if (! objRecVal.checkValidAOAddress(aoAddr3))
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[10] + "^^" + TBAF_FV_2023);  
					}
					else
					{
						rawFileBean.setValue(aoAddr3,RawFileBean.AO_ADDR3_FLDNUM); //Added by Bharath for Raw File Generation
						objReadFVAL2.statReportBuffer.append(aoAddr3 + TBAF_FIELD_SEPERATOR);
					}
				}
				//	Validation of ADDRESS 4(Field No. 11 Of Batch Header Record)
				if (aoAddr4.equals(TBAF_FIELD_NULL) || aoAddr4.equals(TBAF_FIELD_SEPERATOR))
				{
					//	Optional Field, No Error Checking
					rawFileBean.setValue(aoAddr4,RawFileBean.AO_ADDR4_FLDNUM); //Added by Bharath for Raw File Generation
					objReadFVAL2.statReportBuffer.append(" " + TBAF_FIELD_SEPERATOR);
				}
				else if (objRecVal.isFieldNull(aoAddr4))
				{
					if (objRecVal.checkTabSpaces(aoAddr4))
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[11] + "^^" + TBAF_FV_2023); 
					}
					else if (aoAddr4.length() > 25)
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[11] + "^^" + TBAF_FV_2024); 
					}
					else
					{
						rawFileBean.setValue(aoAddr4,RawFileBean.AO_ADDR4_FLDNUM); //Added by Bharath for Raw File Generation
						objReadFVAL2.statReportBuffer.append(" " + TBAF_FIELD_SEPERATOR);
					}
				}
				else
				{
					if (aoAddr4.length() > 25)
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[11] + "^^" + TBAF_FV_2024);
					}
					else if (! objRecVal.checkValidAOAddress(aoAddr4))
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[11] + "^^" + TBAF_FV_2023); 
					}
					else
					{
						rawFileBean.setValue(aoAddr4,RawFileBean.AO_ADDR4_FLDNUM); //Added by Bharath for Raw File Generation
						objReadFVAL2.statReportBuffer.append(aoAddr4 + TBAF_FIELD_SEPERATOR);
					}
				}
			}
			//	For X Correction, all the 4 Address Fields should be NULL. 
		/*	else if ((objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_CORR)) && (objReadFVAL2.transType.equals(TBAF_TRANSACTION_TYPE_X)) )
			{
				//	Validation of ADDRESS 1.
				if (!aoAddr1.equals(TBAF_FIELD_NULL) && !aoAddr1.equals(TBAF_FIELD_SEPERATOR))
				{
					errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[8] + "^^" + TBAF_FV_2025);
				}
				else
				{
					
					objReadFVAL2.statReportBuffer.append(" " + TBAF_FIELD_SEPERATOR);
				}
				//	Validation of ADDRESS 2.
				if (!aoAddr2.equals(TBAF_FIELD_NULL) && !aoAddr2.equals(TBAF_FIELD_SEPERATOR))
				{
					errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[9] + "^^" + TBAF_FV_2025);
				}
				else
				{
					
					objReadFVAL2.statReportBuffer.append(" " + TBAF_FIELD_SEPERATOR);
				}
				//	Validation of ADDRESS 3.
				if (!aoAddr3.equals(TBAF_FIELD_NULL) && !aoAddr3.equals(TBAF_FIELD_SEPERATOR))
				{
					errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[10] + "^^" + TBAF_FV_2025);
				}
				else
				{
					
					objReadFVAL2.statReportBuffer.append(" " + TBAF_FIELD_SEPERATOR);
				}
				//	Validation of ADDRESS 4.
				if (!aoAddr4.equals(TBAF_FIELD_NULL) && !aoAddr4.equals(TBAF_FIELD_SEPERATOR))
				{
					errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[11] + "^^" + TBAF_FV_2025);
				}
				else
				{
					
					objReadFVAL2.statReportBuffer.append(" " + TBAF_FIELD_SEPERATOR);
				}
			}	*/ // End of ADDRESS FIELDS Validation
			
			/**	
			 *	Validation of AO CITY(Field No. 12 Of Batch Header Record)
			 *
			 *	Validations for Regular Statement & Correction Type M.
			 *  AO City Name is MANDATORY for Regular and M correction.
			 *	Length should be less than 25 characters.
			 *	City name validations added on "21-July-2006". 
			 */
			if (objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_ORIG) 
				|| (objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_CORR)) )
			{
				if (aoCity.equals(TBAF_FIELD_NULL) || aoCity.equals(TBAF_FIELD_SEPERATOR))
				{
					errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[12] + "^^" + TBAF_FV_2103);   // new Error added in jan16
				}
				else if (objRecVal.isFieldNull(aoCity))
				{
					errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[12] + "^^" + TBAF_FV_2023);  
				}
				else if (aoCity.length() > 25)
				{
					errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[12] + "^^" + TBAF_FV_2024);  
				}
				else if(aoCity.trim().length() != aoCity.length())  //Added by Subhankar as per new Validation
				{
					errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[12] + "^^" + TBAF_FV_2083);
				}
				else if (objRecVal.isValidCityName(aoCity))
				{
					errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[12] + "^^" + TBAF_FV_2023); 
				}
				else
				{
					
					objReadFVAL2.statReportBuffer.append(aoCity + TBAF_FIELD_SEPERATOR);
				}
			}
			/**
			 *	Validations for Correction Types C1,C2
			 *
			 *	This field is OPTIONAL for Correction Types C1,C2.
			 *  Following validations are done when the user DOES NOT SPECIFY ANY VALUE in this field:
			 *	
			 *	(1)	Check for only TAB spaces are specified.
			 *	(2)	Check if greater than 25 blank spaces are specified.
			 *
			 *	Following validations are done when the user SPECIFIES ANY VALUE in this field:
			 *	
			 *	(1)	Check if the specified value is of length less than 25 characters.
			 *	(2)	Check if the specified value is not having TAB spaces.
			 */
			else if (objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_CORR) 
					 && !objReadFVAL2.transType.equals(TBAF_TRANSACTION_TYPE_C3))
			{
				if (aoCity.equals(TBAF_FIELD_NULL) || aoCity.equals(TBAF_FIELD_SEPERATOR))
				{
					//	Optional Field, No error checking.
					objReadFVAL2.statReportBuffer.append(" " + TBAF_FIELD_SEPERATOR);
				}
				else if (objRecVal.isFieldNull(aoCity))
				{
					if (objRecVal.checkTabSpaces(aoCity))
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[12] + "^^" + TBAF_FV_2023);
					}
					else if (aoCity.length() > 25)
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[12] + "^^" + TBAF_FV_2024);
					}
					else
					{
						objReadFVAL2.statReportBuffer.append(" " + TBAF_FIELD_SEPERATOR);
					}
				}
				else
				{
					if (aoCity.length() > 25)
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[12] + "^^" + TBAF_FV_2024);
					}
					else if (objRecVal.isValidCityName(aoCity))
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[12] + "^^" + TBAF_FV_2023);
					}
					else
					{
						objReadFVAL2.statReportBuffer.append(aoCity + TBAF_FIELD_SEPERATOR);
					}
				}
			}
			//	For X Correction AO City Name should be NULL. 
		/*	else if (objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_CORR) 
					 && objReadFVAL2.transType.equals(TBAF_TRANSACTION_TYPE_X))
			{
				if (!aoCity.equals(TBAF_FIELD_NULL) && !aoCity.equals(TBAF_FIELD_SEPERATOR))
				{
					errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[12] + "^^" + TBAF_FV_2025);
				}
				else
				{
					objReadFVAL2.statReportBuffer.append("-" + TBAF_FIELD_SEPERATOR);
				}	
			}	*/
			// End of AO CITY Validation
			
			/**
			 *	Validation of AO STATE(Field No. 13 Of Batch Header Record)	
			 *
			 *	Validation for Regular Statement & Correction Types M
			 *	This field is MANDATORY for Regular Statement and M Correction Types.
			 *	State Code should have only 2-digits.
			 *	State code should have a value between 01 and 35.  
			 */
			if (objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_ORIG)
				|| (objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_CORR)) )
			{
				if (tbafAOStateCode.equals(TBAF_FIELD_NULL) || tbafAOStateCode.equals(TBAF_FIELD_SEPERATOR))
				{
					errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[13] + "^^" + TBAF_FV_2104);   // new error code added in jan16
				}
				else if (objRecVal.isFieldNull(tbafAOStateCode))
				{
					errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[13] + "^^" + TBAF_FV_2027);  
				}
				else if (tbafAOStateCode.length() > 2)
				{
					errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[13] + "^^" + TBAF_FV_2028);  
				}
				else if(tbafAOStateCode.trim().length() != tbafAOStateCode.length())
				{
					errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[13] + "^^" + TBAF_FV_2017);   
				}
				else if (objRecVal.isInt(tbafAOStateCode))
				{
					errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[13] + "^^" + TBAF_FV_2027);  
				}
				//changed By amit//Changes added for TBAF FVU 1.6 version by puja //24GFVU 1.7 Changes for state code 08
				else if (Integer.parseInt(tbafAOStateCode.trim()) > 37 
					|| Integer.parseInt(tbafAOStateCode.trim()) < 1 || Integer.parseInt(tbafAOStateCode.trim()) == 8)
				{
					errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[13] + "^^" + TBAF_FV_2029);  
				}
				else
				{
					
					rawFileBean.setValue(tbafAOStateCode,RawFileBean.AO_STATE_FLDNUM); //Added by Bharath for Raw File Generation
					objReadFVAL2.statReportBuffer.append(tbafAOStateCode + TBAF_FIELD_SEPERATOR);
				}
			}
			//	For X Correction AO State should be NULL.
		/*	else if ((objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_CORR)) && (objReadFVAL2.transType.equals(TBAF_TRANSACTION_TYPE_X)) )
			{
				if (!tbafAOStateCode.equals(TBAF_FIELD_NULL) && !tbafAOStateCode.equals(TBAF_FIELD_SEPERATOR))
				{
					errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[13] + "^^" + TBAF_FV_2025);
				}
				else
				{
					
					objReadFVAL2.statReportBuffer.append("-" + TBAF_FIELD_SEPERATOR);
				}
			}	*/
			
			// End of AO STATE Validation
			
			/**
			 *	Validation of AO PIN CODE(Field No. 14 Of Batch Header Record)
			 *
			 *	Validation for Regular Statement & Correction Types C1,C2 and C4.
			 *	This field is MANDATORY for Regular Statement and C1, C2 C4 Correction Types.
			 *	Pin Code should be of length exactly equal to 6 digits.
			 *	Pin Code should be greater than 110001.
			 */
			if (objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_ORIG)
				|| (objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_CORR)) )
			{
				if (tbafAOPinCode.equals(TBAF_FIELD_NULL) || tbafAOPinCode.equals(TBAF_FIELD_SEPERATOR))
				{
					errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[14] + "^^" + TBAF_FV_2105); // new error code added in jan16
				}
				else if (objRecVal.isFieldNull(tbafAOPinCode))
				{
					errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[14] + "^^" + TBAF_FV_2105);  // new error code added in jan16
				}
				else if (tbafAOPinCode.trim().length() != 6 || tbafAOPinCode.length() != 6)
				{
					errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[14] + "^^" + TBAF_FV_2105);  // new error code added in jan16
				}
				else if (objRecVal.isInt(tbafAOPinCode))
				{
					errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[14] + "^^" + TBAF_FV_2105);  // new error code added in jan16
				}
				else if (Integer.parseInt(tbafAOPinCode.trim()) < 110001 || Integer.parseInt(tbafAOPinCode.trim()) == 999999)
				{
					errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[14] + "^^" + TBAF_FV_2033);  
				}
				else
				{
					rawFileBean.setValue(tbafAOPinCode,RawFileBean.AO_PIN_FLDNUM); //Added by Bharath for Raw File Generation
					objReadFVAL2.statReportBuffer.append(tbafAOPinCode + TBAF_FIELD_SEPERATOR);
				}
			}
			//	For X Correction AO Pin Code should be NULL.
		/*	else if ((objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_CORR)) && (objReadFVAL2.transType.equals(TBAF_TRANSACTION_TYPE_X)) )
			{
				if (!tbafAOPinCode.equals(TBAF_FIELD_NULL) && !tbafAOPinCode.equals(TBAF_FIELD_SEPERATOR))
				{
					errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[14] + "^^" + TBAF_FV_2025);
				}
				else
				{
					
					objReadFVAL2.statReportBuffer.append("-" + TBAF_FIELD_SEPERATOR);
				}
			}	*/
			
			// End of PIN CODE Validation
			
			/**
			 *	Validation of STD CODE(Field No. 15 Of Batch Header Record)
			 *
			 *	Validation for Regular Statement & Correction Types C1,C2 and C4.
			 *	This field is MANDATORY for Regular Statement and C1, C2 C4 Correction Types.
			 *	STD Code should have value less than or equal to 5-digits.
			 *	STD Code should not have value as "0" (Zero).
			 */
			if (objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_ORIG)
				|| (objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_CORR) 
				&& objReadFVAL2.transType.equals(TBAF_TRANSACTION_TYPE_M)))
			{
				if (tbafAOStdCode.equals(TBAF_FIELD_NULL) || tbafAOStdCode.equals(TBAF_FIELD_SEPERATOR))
				{
					errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[15] + "^^" + TBAF_FV_2106); // new error code added in jan16
				}
				else if (objRecVal.isFieldNull(tbafAOStdCode))
				{
					errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[15] + "^^" + TBAF_FV_2106); // new error code added in jan16
				}
				else if (tbafAOStdCode.length() > 5 || tbafAOStdCode.trim().length() > 5)
				{
					errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[15] + "^^" + TBAF_FV_2106); // new error code added in jan16
				}
				else if(tbafAOStdCode.length() != tbafAOStdCode.trim().length())
				{
					errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[15] + "^^" + TBAF_FV_2106);
				}
				else if (objRecVal.isInt(tbafAOStdCode))
				{
					errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[15] + "^^" + TBAF_FV_2106);  // new error code added in jan16
				}
				else if (Integer.parseInt(tbafAOStdCode.trim()) == 0 || Integer.parseInt(tbafAOStdCode.trim()) == 99999)
				{
					errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[15] + "^^" + TBAF_FV_2081);
				}
			}
			//	For X Correction AO STD Code should be NULL.
			else if ((objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_CORR)) && (objReadFVAL2.transType.equals(TBAF_TRANSACTION_TYPE_X)) )
			{
				if (!tbafAOStdCode.equals(TBAF_FIELD_NULL) && !tbafAOStdCode.equals(TBAF_FIELD_SEPERATOR))
				{
					errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[15] + "^^" + TBAF_FV_2025);
				}
			}	// End of STD CODE Validation
			
			/**
			 *	Validation of PHONE NUMBER(Field No. 16 Of Batch Header Record)
			 *
			 *	Validation for Regular Statement & Correction Types C1,C2 and C4.
			 *	This field is MANDATORY for Regular Statement and C1, C2 C4 Correction Types.
			 *	Phone Number should have value length less than or equal to 10 digits.
			 *	Phone Number should not have value as "0" (Zero). 
			 */
			if (objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_ORIG)
				|| (objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_CORR) 
				&& objReadFVAL2.transType.equals(TBAF_TRANSACTION_TYPE_M)))
			{
				if (tbafAOPhoneNo.equals(TBAF_FIELD_NULL) || tbafAOPhoneNo.equals(TBAF_FIELD_SEPERATOR))
				{
					errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[16] + "^^" + TBAF_FV_2107); // new error code added in jan16
				}
				else if (objRecVal.isFieldNull(tbafAOPhoneNo))
				{
					errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[16] + "^^" + TBAF_FV_2107);   // New Error code added in Jan16
				} 
				else if (tbafAOPhoneNo.length() > 10)
				{
					errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[16] + "^^" + TBAF_FV_2024); 
				}
				else if(tbafAOPhoneNo.length() != tbafAOPhoneNo.trim().length())
				{
					errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[16] + "^^" + TBAF_FV_2017);
				}
				else if (objRecVal.isInt(tbafAOPhoneNo))
				{
					errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[16] + "^^" + TBAF_FV_2107);  // new error code added in jan16
				}
				else if (Long.parseLong(tbafAOPhoneNo.trim()) == 0 || tbafAOPhoneNo.trim().equals("9999999999"))
				{
					errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[16] + "^^" + TBAF_FV_2108); // new error code added in jan16
				}
			}
			//	For X Correction AO Phone Number should be NULL. 
			else if ((objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_CORR)) && (objReadFVAL2.transType.equals(TBAF_TRANSACTION_TYPE_X)) )
			{
				if (!tbafAOPhoneNo.equals(TBAF_FIELD_NULL) && !tbafAOPhoneNo.equals(TBAF_FIELD_SEPERATOR))
				{
					errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[16] + "^^" + TBAF_FV_2025);
				}
			}	// End of PHONE NUMBER Validation
			
			/**	
			 *	Validation of E-MAIL ID(Field No. 17 Of Batch Header Record) 
			 * 
			 *	Validation for Regular Statement & Correction Types C1,C2 and C4.
			 *	This field is OPTIONAL for Regular Statement and C1, C2 C4 Correction Types.
			 *	e-Mail ID should be less than or equal to 75 characters.
			 *	e-Mail should follow the e-Mail ID validations.
			 */
			if (objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_ORIG)
				|| (objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_CORR) 
				&& objReadFVAL2.transType.equals(TBAF_TRANSACTION_TYPE_M)))
			{
				if (aoEmailID.equals(TBAF_FIELD_NULL) || aoEmailID.equals(TBAF_FIELD_SEPERATOR))
				{
					errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[17] + "^^" + TBAF_FV_2109);  // New Error code Added in Jan16
				}
				else if (objRecVal.isFieldNull(aoEmailID))
				{
					errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[17] + "^^" + TBAF_FV_2109); // New Error code Added in Jan16
				}
				else if (aoEmailID.length() > 75)
				{
					errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[17] + "^^" + TBAF_FV_2024); 
				}
				else if(aoEmailID.trim().length() != aoEmailID.length())
				{
					errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[17] + "^^" + TBAF_FV_2109); // New Error code Added in Jan16   //Added By Subhankar
				}
				else if (objRecVal.isValidEmail(aoEmailID))
				{
					errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[17] + "^^" + TBAF_FV_2109);   // New Error code Added in Jan16
				}
			}
			//	For X Correction e-Mail ID should be NULL 
			else if ((objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_CORR)) && (objReadFVAL2.transType.equals(TBAF_TRANSACTION_TYPE_X)) )
			{
				if (!aoEmailID.equals(TBAF_FIELD_NULL) && !aoEmailID.equals(TBAF_FIELD_SEPERATOR))
				{
					errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[17] + "^^" + TBAF_FV_2025);
				}
			}	// End of e-MAIL ID Validation
			
			/**	
			 *	Validation of RESPONSIBLE PERSON NAME(Field No. 18 Of Batch Header Record) 
			 * 
			 *	Validation for Regular Statement & Correction Types C1,C2 and C4.	
			 *	This field is MANDATORY for Regular Statement and C1, C2 C4 Correction Types.
			 *	Name should have length less than or equal to 75 character.
			 *	Name should not have TAB spaces.
			 * From FY 26-27 this field will take name if rtitle selected as Non-Individual
			 */
			
			//Gauri added the year condition for FY CR 89435, FVU 1.9 in below exsisting code:: START (if was already there)
			
			if (Integer.parseInt(objReadFVAL2.finYear) < 2026 && (objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_ORIG)
				|| (objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_CORR) 
				&& (objReadFVAL2.transType.equals(TBAF_TRANSACTION_TYPE_M) || objReadFVAL2.transType.equals(TBAF_TRANSACTION_TYPE_X)))))
			{
				if (personName.equals(TBAF_FIELD_NULL) || personName.equals(TBAF_FIELD_SEPERATOR))
				{
					errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[18] + "^^" + TBAF_FV_2110);  // new error added in jan16
				}
				else if (objRecVal.isFieldNull(personName))
				{
					errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[18] + "^^" + TBAF_FV_2031);  
				}
				else if (personName.length() > 75)
				{
					errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[18] + "^^" + TBAF_FV_2024); 
				}
				else if (! objRecVal.checkValidAOName(personName))
				{
					errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[18] + "^^" + TBAF_FV_2031); 
				}
				else
				{
					rawFileBean.setValue(personName,RawFileBean.RESP_PERS_NAME_FLDNUM); //Added by Bharath for Raw File Generation
					objReadFVAL2.statReportBuffer.append(personName + TBAF_FIELD_SEPERATOR);
				}
			}
			
			//Gauri added the FY condition after FY 26 this field is not applicable
			else if(Integer.parseInt(objReadFVAL2.finYear) >= 2026) {
				if(!personName.equals(TBAF_FIELD_NULL) && !personName.equals(TBAF_FIELD_SEPERATOR)){
					errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[18] + "^^" + TBAF_FV_5058); 
				}
				else {
					objReadFVAL2.statReportBuffer.append('-' + TBAF_FIELD_SEPERATOR);
				}
			}
			
			/*// added else-if block for CR 89435
			
			else if(Integer.parseInt(objReadFVAL2.finYear) >= 2025 && objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_ORIG) || (objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_CORR)))
			{
				if(rTitle.equals("04")) {
					if (personName.equals(TBAF_FIELD_NULL) || personName.equals(TBAF_FIELD_SEPERATOR))
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[18] + "^^" + TBAF_FV_5056);  
					}
					else if (objRecVal.isFieldNull(personName))
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[18] + "^^" + TBAF_FV_5056);  
					}
					else if (personName.length() > 75)
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[18] + "^^" + TBAF_FV_5056);  
					}
					else if (! objRecVal.checkAlphabets(personName))
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[18] + "^^" + TBAF_FV_5056);  
					}
					else
					{
						rawFileBean.setValue(personName,RawFileBean.RESP_PERS_NAME_FLDNUM); //Added by Bharath for Raw File Generation
						objReadFVAL2.statReportBuffer.append('-' + TBAF_FIELD_SEPERATOR);
					}
				}
				else if(rTitle.equals("01") || rTitle.equals("02") || rTitle.equals("03")){
					if(!(personName.equals(TBAF_FIELD_NULL) || personName.equals(TBAF_FIELD_SEPERATOR))) {
					errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[18] + "^^" + TBAF_FV_5042);
					}
					else
					{
						rawFileBean.setValue(personName,RawFileBean.RESP_PERS_NAME_FLDNUM); //Added by Bharath for Raw File Generation
						objReadFVAL2.statReportBuffer.append('-' + TBAF_FIELD_SEPERATOR);
					}
				}
			}
			
			//Gauri added the year condition for FY CR 89435, FVU 1.9 in below exsisting code:: END
*/			
			//	For X Correction Responsible Person Name should be NULL. 
			/*else if ((objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_CORR)) && (objReadFVAL2.transType.equals(TBAF_TRANSACTION_TYPE_X)) )
			{
//				if (!personName.equals(TBAF_FIELD_NULL) && !personName.equals(TBAF_FIELD_SEPERATOR))
//				{
//				//errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[18] + "^^" + TBAF_FV_2025);
//				}
//				else
//				{
					
					objReadFVAL2.statReportBuffer.append("-" + TBAF_FIELD_SEPERATOR);
				
			}*/	// End of RESPONSIBLE PERSON NAME Validation
			
			/**	
			 *	Validation of RESPONSIBLE PERSON DESIGNATION(Field No. 19 Of Batch Header Record) 
			 * 
			 *	Validation for Regular Statement & Correction Types C1,C2 and C4.
			 *	This field is MANDATORY for Regular Statement and C1, C2 C4 Correction Types.
			 *	Designation should have length less than or equal to 25 characters.
			 *	Designation should not have TAB spaces.
			 */
			if (objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_ORIG)
				|| (objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_CORR) 
				&& objReadFVAL2.transType.equals(TBAF_TRANSACTION_TYPE_M)))
			{
				if (personDesig.equals(TBAF_FIELD_NULL) || personDesig.equals(TBAF_FIELD_SEPERATOR))
				{
					errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[19] + "^^" + TBAF_FV_2111); // new error added in jan16
				}
				else if (objRecVal.isFieldNull(personDesig))
				{
					errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[19] + "^^" + TBAF_FV_2031);
				}
				else if (personDesig.length() > 25)
				{
					errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[19] + "^^" + TBAF_FV_2024);
				}
				else if (! objRecVal.checkValidAOName(personDesig))
				{
					errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[19] + "^^" + TBAF_FV_2031);
				}
				else
				{
					rawFileBean.setValue(personDesig,RawFileBean.RESP_PERS_DESG_FLDNUM); //Added by Bharath for Raw File Generation
				}
			}
			//	For X Correction Responsible Person Designation should be NULL. 
			else if ((objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_CORR)) && (objReadFVAL2.transType.equals(TBAF_TRANSACTION_TYPE_X)) )
			{
				if (!personDesig.equals(TBAF_FIELD_NULL) && !personDesig.equals(TBAF_FIELD_SEPERATOR))
				{
					errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[19] + "^^" + TBAF_FV_2025);
				}
				
			}	// End of RESPONSIBLE PERSON DESIGNATION Validation
					
			/**	
			 *	Validation of FINANCIAL YEAR(Field No.20 Of Batch Header Record) 
			 * 	
			 * 	This field is MANDATORY irrespective of the Statement Type.
			 *	Financial Year should be an integer of length exactly equal to 4 digits.
			 *	Value specified should be the current financial year.
			 */
			if (objReadFVAL2.finYear.equals(TBAF_FIELD_NULL) || objReadFVAL2.finYear.equals(TBAF_FIELD_SEPERATOR))
			{
				errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[20] + "^^" + TBAF_FV_2112);  // new Error added in jan16
				objReadFVAL2.errorFoundInBH = true;
				objReadFVAL2.isValid24GFile = false;
			}
			else if (objRecVal.isFieldNull(objReadFVAL2.finYear))
			{
				errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[20] + "^^" + TBAF_FV_2113); // new Error added in jan16
				objReadFVAL2.errorFoundInBH = true;
				objReadFVAL2.isValid24GFile = false;
			}
			else if ((objReadFVAL2.finYear.trim().length()) != 4 || objReadFVAL2.finYear.length() != 4)
			{
				errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[20] + "^^" + TBAF_FV_2113);  // new Error added in jan16
				objReadFVAL2.errorFoundInBH = true;
				objReadFVAL2.isValid24GFile = false;
			}
			else if(objReadFVAL2.finYear.trim().length() != objReadFVAL2.finYear.length())
			{
				errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[20] + "^^" + TBAF_FV_2113);  // new Error added in jan16
				objReadFVAL2.errorFoundInBH = true; 
				objReadFVAL2.isValid24GFile = false;
			}
			else if (objRecVal.isInt(objReadFVAL2.finYear))
			{
				errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[20] + "^^" + TBAF_FV_2113);  // new Error added in jan16
				objReadFVAL2.errorFoundInBH = true;
				objReadFVAL2.isValid24GFile = false;
			}
			
			else if(Integer.parseInt(objReadFVAL2.finYear) < Integer.parseInt(Parameters.tbafThreshholdYear))   //Added by Subhankar (This check is to determine whether the fin year is less than the value specified in the properties file)
			{
				errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[20] + "^^" + TBAF_FV_2071);
				objReadFVAL2.errorFoundInBH = true;
				objReadFVAL2.isValid24GFile = false;
			} 
			
			else if( (Integer.parseInt(objReadFVAL2.finYear.trim()) == 0) || (Integer.parseInt(objReadFVAL2.finYear.trim()) == 9999) )
			{
				errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[20] + "^^" + TBAF_FV_2079);
				objReadFVAL2.errorFoundInBH = true;
				objReadFVAL2.isValid24GFile = false;
			}
			else if(objRecVal.checkFutureFinancialYear(objReadFVAL2.finYear))   //Future Year return cannot be Submitted
			{
				errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[20] + "^^" + TBAF_FV_2050);  // new Error added in jan16
				objReadFVAL2.errorFoundInBH = true;
				objReadFVAL2.isValid24GFile = false;
			}
			else
			{
				rawFileBean.setValue(objReadFVAL2.finYear,RawFileBean.FIN_YEAR_FLDNUM); //Added by Bharath for Raw File Generation
				objReadFVAL2.statReportBuffer.append(objReadFVAL2.finYear + TBAF_FIELD_SEPERATOR);
			    objReadFVAL2.isValid24GFile = true;
			}	
			// End of FINANCIAL YEAR Validation
			
	
					
			
		    /**	
			 *	Validation of LAST FINANCIAL YEAR(Field No. 21 Of Batch Header Record) 
			 *
			 * 	For Regular Statement this field should be NULL.
			 *	If any value is specified, the file should be rejected.
			 */
			if (objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_ORIG) || objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_CORR))
			{
				if (!objReadFVAL2.lastFinYear.equals(TBAF_FIELD_NULL) 
					&& !objReadFVAL2.lastFinYear.equals(TBAF_FIELD_SEPERATOR))
				{
					errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[21] + "^^" + TBAF_FV_2037);
					objReadFVAL2.errorFoundInBH = true;
				}
				else
				{
					objReadFVAL2.statReportBuffer.append("-" + TBAF_FIELD_SEPERATOR);
				}
			}
			/**	
			 * 	For Correction Statement, this field is MANDATORY.
			 *	For Transaction Types C1,C2 and C3 the Financial Year & Last Financial Year must be EQUAL.
			 *	For Transaction Type C4 it is NOT mandatory that Financial Year & Last Financial Year must be equal.
			 */
			
			//Commented On 31st Oct as For Original Or Correction LAST FINANCIAL YEAR should always be null (as handled in the previous block).
			
		/*	else if (objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_CORR))
			{
				if (objReadFVAL2.lastFinYear.equals(TBAF_FIELD_NULL) 
					|| objReadFVAL2.lastFinYear.equals(TBAF_FIELD_SEPERATOR))
				{
					errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[21] + "^^" + TBAF_FV_2030);
					objReadFVAL2.errorFoundInBH = true;
				}
				else if (objRecVal.isFieldNull(objReadFVAL2.lastFinYear))
				{
					errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[21] + "^^" + TBAF_FV_2031);
					objReadFVAL2.errorFoundInBH = true;
				}
				else if ((objReadFVAL2.lastFinYear.trim().length()) != 4 || objReadFVAL2.lastFinYear.length() != 4)
				{
					errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[21] + "^^" + TBAF_FV_2035);
					objReadFVAL2.errorFoundInBH = true;
				}
				else if (objRecVal.isInt(objReadFVAL2.lastFinYear))
				{
					errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[21] + "^^" + TBAF_FV_2031);
					objReadFVAL2.errorFoundInBH = true;
				}
				else if(Integer.parseInt(objReadFVAL2.lastFinYear) < 2006)
				{
					errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[20] + "^^" + TBAF_FV_2031);
					objReadFVAL2.errorFoundInBH = true;
				}
				else if(objRecVal.checkFutureFinancialYear(objReadFVAL2.lastFinYear))
				{
					errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[20] + "^^" + TBAF_FV_2031);
					objReadFVAL2.errorFoundInBH = true;
				}
				else if (objReadFVAL2.transType.equals(TBAF_TRANSACTION_TYPE_C4))
				{
					objReadFVAL2.statReportBuffer.append(objReadFVAL2.lastFinYear + TBAF_FIELD_SEPERATOR);
				}
				else if (objReadFVAL2.transType.equals(TBAF_TRANSACTION_TYPE_C1)
						|| objReadFVAL2.transType.equals(TBAF_TRANSACTION_TYPE_C2)
						|| objReadFVAL2.transType.equals(TBAF_TRANSACTION_TYPE_C3))
				{
					if (!objReadFVAL2.lastFinYear.equals(objReadFVAL2.finYear))
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[21] + "^^" + TBAF_FV_2038);
						objReadFVAL2.errorFoundInBH = true;
					}
					else
					{
						objReadFVAL2.statReportBuffer.append(objReadFVAL2.lastFinYear + TBAF_FIELD_SEPERATOR);
					}
				}
			}	*/
			
			// End of LAST FINANCIAL YEAR Validation
			
			/**	
			 *	Validation of DEDUCTOR CATEGORY(Field No. 22 Of Batch Header Record) 
			 * 
			 *  This field is MANDATORY irrespective of the Statement Type.
			 *	Deductor category must be an integer value of length exactly equal to 2 digits. 
			 *	Value specified should be between 00 and 35.
			 */
		/*	if (objReadFVAL2.deductCatgry.equals(TBAF_FIELD_NULL) 
				|| objReadFVAL2.deductCatgry.equals(TBAF_FIELD_SEPERATOR))
			{
				errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[22] + "^^" + TBAF_FV_2030);
				objReadFVAL2.errorFoundInBH = true;
			}
			else if (objRecVal.isFieldNull(objReadFVAL2.deductCatgry))
			{
				errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[22] + "^^" + TBAF_FV_2031);
				objReadFVAL2.errorFoundInBH = true;
			}
			else if (objReadFVAL2.deductCatgry.length() != 2)
			{
				errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[22] + "^^" + TBAF_FV_2028);
				objReadFVAL2.errorFoundInBH = true;
			}
			else if (objRecVal.isInt(objReadFVAL2.deductCatgry))
			{
				errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[22] + "^^" + TBAF_FV_2031);
				objReadFVAL2.errorFoundInBH = true;
			}
			else if (objReadFVAL2.deductCatgry.startsWith(" "))
			{
				errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[22] + "^^" + TBAF_FV_2027);
			}
			else if (Integer.parseInt(objReadFVAL2.deductCatgry.trim()) > 35 
					 || Integer.parseInt(objReadFVAL2.deductCatgry.trim()) < 0)
			{
				errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[22] + "^^" + TBAF_FV_2039);
				objReadFVAL2.errorFoundInBH = true;
			}
			else
			{
				objReadFVAL2.statReportBuffer.append(objReadFVAL2.deductCatgry + TBAF_FIELD_SEPERATOR);
			}	*/  // End of DEDUCTOR CATEGORY Validation
			
			/**	
			 *	Validation of DEDUCTOR CATEGORY(Field No. 22 Of Batch Header Record) 
			 * 
			 *  This field is MANDATORY irrespective of the Statement Type.
			 *  Deductor catagory value must be either 'A' or 'S'
			*/
			
			if (objReadFVAL2.deductCatgry.equals(TBAF_FIELD_NULL) 
					|| objReadFVAL2.deductCatgry.equals(TBAF_FIELD_SEPERATOR))
				{
					errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[22] + "^^" + TBAF_FV_2114); // new error code added in jan16
					objReadFVAL2.errorFoundInBH = true;
				}
				else if (objRecVal.isFieldNull(objReadFVAL2.deductCatgry))
				{
					errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[22] + "^^" + TBAF_FV_2115); // new error code added in jan16
					objReadFVAL2.errorFoundInBH = true;
				}
				else if (objReadFVAL2.deductCatgry.length() != 1)
				{
					errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[22] + "^^" + TBAF_FV_2061); 
					objReadFVAL2.errorFoundInBH = true;
				}
				else if (objReadFVAL2.deductCatgry.trim().length() != objReadFVAL2.deductCatgry.length())
				{
					errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[22] + "^^" + TBAF_FV_2115);  // new error code added in jan16
					objReadFVAL2.errorFoundInBH = true;
				}
				else if((! (objReadFVAL2.deductCatgry.equals("A"))) && (! (objReadFVAL2.deductCatgry.equals("S"))))
				{
					errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[22] + "^^" + TBAF_FV_2062);
					objReadFVAL2.errorFoundInBH = true;
				}
				else
				{
					rawFileBean.setValue(objReadFVAL2.deductCatgry,RawFileBean.DED_CATEGORY_FLDNUM); //Added by Bharath for Raw File Generation
					objReadFVAL2.statReportBuffer.append(objReadFVAL2.deductCatgry + TBAF_FIELD_SEPERATOR);
				}
			
			
			
			// End of DEDUCTOR CATEGORY Validation
			
				
			/**	
			 * 	Validation of LAST DEDUCTOR CATEGORY(Field No. 23 Of Batch Header Record)
			 * 
			 *	For Regular and Correction Statement this field should be NULL.
			 *	If any value is specified, the file should be rejected.
			 */
			if (objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_ORIG) || objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_CORR))
			{
				if (!objReadFVAL2.lastDeductCatgry.equals(TBAF_FIELD_NULL) 
					&& !objReadFVAL2.lastDeductCatgry.equals(TBAF_FIELD_SEPERATOR))
				{
					errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[23] + "^^" + TBAF_FV_2089);
					objReadFVAL2.errorFoundInBH = true;
				}
				else
				{
					objReadFVAL2.statReportBuffer.append("-" + TBAF_FIELD_SEPERATOR);
				}
			}  
			/**	 
			 *	This field is " No value should be specified ". For future use  for the Correction Statement.
			 *	For Transaction Types C1,C2 and C3 Deductor Category & Last Deductor Category should be EQUAL.
			 *	For Transaction Type C4 it is NOT mandatory that Deductor Category & Last Deductor Category
			 *	must be equal.
			 */
			
			
			//Commented On 31st Oct as For Original Or Correction type LAST DEDUCTOR CATEGORY should always be null (as handled in the previous block).
			
		/*	else if (objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_CORR))
			{
				
				if(objReadFVAL2.transType.equals(TBAF_TRANSACTION_TYPE_X))
				{
					if (!objReadFVAL2.lastDeductCatgry.equals(TBAF_FIELD_NULL) 
							&& !objReadFVAL2.lastDeductCatgry.equals(TBAF_FIELD_SEPERATOR))
						{
							errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[23] + "^^" + TBAF_FV_2037);
							objReadFVAL2.errorFoundInBH = true;
						}
						else
						{
							objReadFVAL2.statReportBuffer.append("-" + TBAF_FIELD_SEPERATOR);
						}
				} 
				
				
			  	if (objReadFVAL2.lastDeductCatgry.equals(TBAF_FIELD_NULL) 
					|| objReadFVAL2.lastDeductCatgry.equals(TBAF_FIELD_SEPERATOR))
				{
					errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[23] + "^^" + TBAF_FV_2030);
					objReadFVAL2.errorFoundInBH = true;
				}
				else if (objRecVal.isFieldNull(objReadFVAL2.lastDeductCatgry))
				{
					errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[23] + "^^" + TBAF_FV_2031);
					objReadFVAL2.errorFoundInBH = true;
				}
				else if (objReadFVAL2.lastDeductCatgry.length() != 2)
				{
					errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[23] + "^^" + TBAF_FV_2028);
					objReadFVAL2.errorFoundInBH = true;
				}
				else if (objRecVal.isInt(objReadFVAL2.lastDeductCatgry))
				{
					errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[23] + "^^" + TBAF_FV_2031);
					objReadFVAL2.errorFoundInBH = true;
				}
				else if (Integer.parseInt(objReadFVAL2.lastDeductCatgry.trim()) > 35 
						 || Integer.parseInt(objReadFVAL2.lastDeductCatgry.trim()) < 0)
				{
					errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[23] + "^^" + TBAF_FV_2039);
					objReadFVAL2.errorFoundInBH = true;
				}
				else if (objReadFVAL2.transType.equals(TBAF_TRANSACTION_TYPE_C4))
				{
					objReadFVAL2.statReportBuffer.append(objReadFVAL2.lastDeductCatgry + TBAF_FIELD_SEPERATOR);
				}
				else if (objReadFVAL2.transType.equals(TBAF_TRANSACTION_TYPE_C1)
						|| objReadFVAL2.transType.equals(TBAF_TRANSACTION_TYPE_C2)
						|| objReadFVAL2.transType.equals(TBAF_TRANSACTION_TYPE_C3))
				{
					if (!objReadFVAL2.lastDeductCatgry.equals(objReadFVAL2.deductCatgry))
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[23] + "^^" + TBAF_FV_2040);
						objReadFVAL2.errorFoundInBH = true;
					}
					else
					{
						objReadFVAL2.statReportBuffer.append(objReadFVAL2.lastDeductCatgry + TBAF_FIELD_SEPERATOR);
					}
				} 
			}	*/
		
		
		// End of LAST DEDUCTOR CATEGORY Validation
			
			/**	
			 *	Validation of QUARTER(Field No. 24 Of Batch Header Record) 
			 * 
			 *	This field is MANDATORY irrespective of the Statement Type 
			 *	Valid values aee Q1, Q2 Q3 and Q4
			 *	If any other value is specified the file should be rejected 		
			 */
		/*	if (objReadFVAL2.quarter.equals(TBAF_FIELD_NULL) || objReadFVAL2.quarter.equals(TBAF_FIELD_SEPERATOR))
			{
				errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[24] + "^^" + TBAF_FV_2030);
				objReadFVAL2.errorFoundInBH = true;
			}
			else if (objRecVal.isFieldNull(objReadFVAL2.quarter))
			{
				errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[24] + "^^" + TBAF_FV_2031);
				objReadFVAL2.errorFoundInBH = true;
			}
			else if ((!objReadFVAL2.quarter.equals(TBAF_QUARTER1))
					&& (!objReadFVAL2.quarter.equals(TBAF_QUARTER2))
					&& (!objReadFVAL2.quarter.equals(TBAF_QUARTER3))
					&& (!objReadFVAL2.quarter.equals(TBAF_QUARTER4)))
			{
				errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[24] + "^^" + TBAF_FV_2041);
				objReadFVAL2.errorFoundInBH = true;
			}
			else
			{
				objReadFVAL2.statReportBuffer.append(objReadFVAL2.quarter + TBAF_FIELD_SEPERATOR);
			} */	// End of QUARTER Validation
			
			
			/**
			 *	Validation of Batch Updation Indicator 1(Field No. 24  of Batch Header Record)
			 */
			
			 if (objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_ORIG) || (objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_CORR) && objReadFVAL2.transType.equals(TBAF_TRANSACTION_TYPE_X)) )
			{
				if (!batchUpdtIndicator.equals(TBAF_FIELD_NULL) && !batchUpdtIndicator.equals(TBAF_FIELD_SEPERATOR))
				{
					errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[24] + "^^" + TBAF_FV_2092);
				}
			/*	else if(! objRecVal.isFieldNull(bhFiller_1))     
				{
					errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[24] + "^^" + TBAF_FV_2049);
				} */
				else
				{
					objReadFVAL2.statReportBuffer.append("-" + TBAF_FIELD_SEPERATOR);
				}
			}
			else if(objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_CORR) && objReadFVAL2.transType.equals(TBAF_TRANSACTION_TYPE_M))
			
				{
					if (batchUpdtIndicator.equals(TBAF_FIELD_NULL) 
							|| batchUpdtIndicator.equals(TBAF_FIELD_SEPERATOR))
						{
							errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[24] + "^^" + TBAF_FV_2030);
							
						}
						else if (objRecVal.isFieldNull(batchUpdtIndicator))
						{
							errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[24] + "^^" + TBAF_FV_2031);
							
						}
						else if (batchUpdtIndicator.length() != 1)
						{
							errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[24] + "^^" + TBAF_FV_2061);
							
						}
						else if (batchUpdtIndicator.trim().length() != batchUpdtIndicator.length())
						{
							errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[24] + "^^" + TBAF_FV_2031);
							
						}
						else if ( (Integer.parseInt(batchUpdtIndicator) != 1)  && (Integer.parseInt(batchUpdtIndicator) != 0) )
						{
							errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[24] + "^^" + TBAF_FV_2090);
						}
						else
						{
							objReadFVAL2.statReportBuffer.append(batchUpdtIndicator + TBAF_FIELD_SEPERATOR);
						}
					
				}
			
			//End of Batch Updation Indicator Validation
			
				
			/**	
			 *	Validation of LAST QUARTER(Field No. 25 Of Batch Header Record) 
			 *	For Regular Statement this field should be NULL.
			 *  If any value is specified, the file should be rejected.
			 */
		/*	if (objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_ORIG))
			{
				if (!objReadFVAL2.lastQuarter.equals(TBAF_FIELD_NULL) 
					&& !objReadFVAL2.lastQuarter.equals(TBAF_FIELD_SEPERATOR))
				{
					errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[25] + "^^" + TBAF_FV_2037);
				}
				else
				{
					objReadFVAL2.statReportBuffer.append("-" + TBAF_FIELD_SEPERATOR);
				}
			}*/
			
			/**	 
			 *	This field is MANDATORY for the Correction Statement.
			 *	For Transaction Types C1,C2 and C3 Quarter & Last Quarter should be EQUAL.
			 *	For Transaction Type C4 it is NOT mandatory that Quarter & Last Quarter must be equal. 
			 */
		/*	else if (objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_CORR))
			{
				if (objReadFVAL2.lastQuarter.equals(TBAF_FIELD_NULL) 
					|| objReadFVAL2.lastQuarter.equals(TBAF_FIELD_SEPERATOR))
				{
					errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[25] + "^^" + TBAF_FV_2030);
					objReadFVAL2.errorFoundInBH = true;
				}
				else if (objRecVal.isFieldNull(objReadFVAL2.lastQuarter))
				{
					errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[25] + "^^" + TBAF_FV_2031);
					objReadFVAL2.errorFoundInBH = true;
				}
				else if ((!objReadFVAL2.lastQuarter.equals(TBAF_QUARTER1))
						&& (!objReadFVAL2.lastQuarter.equals(TBAF_QUARTER2))
						&& (!objReadFVAL2.lastQuarter.equals(TBAF_QUARTER3))
						&& (!objReadFVAL2.lastQuarter.equals(TBAF_QUARTER4)))
				{
					errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[25] + "^^" + TBAF_FV_2041);
					objReadFVAL2.errorFoundInBH = true;
				}
				else if (objReadFVAL2.transType.equals(TBAF_TRANSACTION_TYPE_C4))
				{
					objReadFVAL2.statReportBuffer.append(objReadFVAL2.lastQuarter + TBAF_FIELD_SEPERATOR);
				}
				else if (objReadFVAL2.transType.equals(TBAF_TRANSACTION_TYPE_C1)
						|| objReadFVAL2.transType.equals(TBAF_TRANSACTION_TYPE_C2)
						|| objReadFVAL2.transType.equals(TBAF_TRANSACTION_TYPE_C3))
				{
					if (!objReadFVAL2.lastQuarter.equals(objReadFVAL2.quarter))
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[25] + "^^" + TBAF_FV_2042);
						objReadFVAL2.errorFoundInBH = true;
					}
					else
					{
						objReadFVAL2.statReportBuffer.append(objReadFVAL2.lastQuarter + TBAF_FIELD_SEPERATOR);
					}
				}
			}	*/    // End of LAST QUARTER Validation
			
			
			
			/**
			 *	Validation of Batch Filler 2(Field No. 25 Filler2 of Batch Header Record)
			 *Validation for Filler 2 added for CR 89435, FVU 1.9
			 */
			
			 //Gauri added Filler 2 validation for CR 89435, FVU 1.9:: START
			
			if (objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_ORIG) || objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_CORR))
			{
				if (!bhFiller_2.equals(TBAF_FIELD_NULL) && !bhFiller_2.equals(TBAF_FIELD_SEPERATOR))
				{
					errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[25] + "^^" + TBAF_FV_2049);
				}
				/*else if(! (objRecVal.isFieldNull(bhFiller_2)))     
				{
					errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[25] + "^^" + TBAF_FV_2049);
				}*/
				else
				{
					objReadFVAL2.statReportBuffer.append("-" + TBAF_FIELD_SEPERATOR);
				}
			}
			
			//Gauri added Filler 2 validation for CR 89435, FVU 1.9:: END
			
/*			//Gauri added AO First Name validation for CR 89435, FVU 1.9:: START
			
			if(Integer.parseInt(objReadFVAL2.finYear) >= 2025 && objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_ORIG) || objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_CORR)) 
			{
				if(aoTitle.equals("01") || aoTitle.equals("02") || aoTitle.equals("03"))
				{
					if (!(aoFirstName.equals(TBAF_FIELD_NULL) || aoFirstName.equals(TBAF_FIELD_SEPERATOR)))
					{
						//errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[25] + "^^" + TBAF_FV_5047);  
						 if (objRecVal.isFieldNull(aoFirstName))
						{
							errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[25] + "^^" + TBAF_FV_5047);  
						}
						else if (aoFirstName.length() > 25)
						{
							errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[25] + "^^" + TBAF_FV_5047);  
						}
						else if (! objRecVal.checkAlphabets(aoFirstName))
						{
							errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[25] + "^^" + TBAF_FV_5047);  
						}
						else //gauri added this
						{
							objReadFVAL2.statReportBuffer.append('-' + TBAF_FIELD_SEPERATOR);
						}
//						 else //pdf issue
//							{
//								rawFileBean.setValue(aoFirstName,RawFileBean.AO_FIRST_NAME); 
//								objReadFVAL2.statReportBuffer.append('-' + TBAF_FIELD_SEPERATOR);
//							}
					}
					
					
				}
				else if(aoTitle.equals("04")) {
					if(!(aoFirstName.equals(TBAF_FIELD_NULL) || aoFirstName.equals(TBAF_FIELD_SEPERATOR))) {
					errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[25] + "^^" + TBAF_FV_5042);
					}
					else //gauri added this
					{
					objReadFVAL2.statReportBuffer.append('-' + TBAF_FIELD_SEPERATOR);
					}
//					else //pdf issue
//					{
//						rawFileBean.setValue(aoFirstName,RawFileBean.AO_FIRST_NAME); 
//						objReadFVAL2.statReportBuffer.append('-' + TBAF_FIELD_SEPERATOR);
//					}
				}
			}
			
			else //gauri added this
			{
				objReadFVAL2.statReportBuffer.append('-' + TBAF_FIELD_SEPERATOR);
			}
			//Gauri added AO First Name validation for CR 89435, FVU 1.9:: END
*/			
			 
			
			/**	
			 *	Validation of NATURE OF DEDUCTION(Field No. 26 Of Batch Header Record)
			 *	
			 * 	This field is MANDATORY irrespective of the Statement Type.
			 *	Valid values are 24Q(TDS-SAL),26Q(TDS-NONSAL),27Q(TDS-NR),27EQ(TCS).
			 *	All other values specified are invalid.
			 */
		/*	if (natureOfDeduction.equals(TBAF_FIELD_NULL) || natureOfDeduction.equals(TBAF_FIELD_SEPERATOR))
			{
				errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[26] + "^^" + TBAF_FV_2030);
			}
			else if (objRecVal.isFieldNull(natureOfDeduction))
			{
				errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[26] + "^^" + TBAF_FV_2031);
			}
			else if (!natureOfDeduction.equals(TBAF_FORM_24Q)
					&& !natureOfDeduction.equals(TBAF_FORM_26Q)
					&& !natureOfDeduction.equals(TBAF_FORM_27Q)
					&& !natureOfDeduction.equals(TBAF_FORM_27EQ))
			{
				errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[26] + "^^" + TBAF_FV_2043);
			}
			else
			{
				objReadFVAL2.statReportBuffer.append(natureOfDeduction + TBAF_FIELD_SEPERATOR);
			}*/	  
			
			// End of NATURE OF DEDUCTION Validation									
			
			
			
			
			/**
			 *	Validation of Batch Filler 3(Field No. 26 Filler3 of Batch Header Record)
			 *Validation for Filler 3 added for CR 89435, FVU 1.9
			 */
			
			//Gauri added Filler 3 validation::START
			
			if (objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_ORIG) || objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_CORR))
			{
				if (!bhFiller_3.equals(TBAF_FIELD_NULL) && !bhFiller_3.equals(TBAF_FIELD_SEPERATOR))
				{
					errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[26] + "^^" + TBAF_FV_2049);
				}
				/* else if(! (objRecVal.isFieldNull(bhFiller_3)))     
				{
					errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[26] + "^^" + TBAF_FV_2049);
				} */
				else
				{
					objReadFVAL2.statReportBuffer.append("-" + TBAF_FIELD_SEPERATOR);
				}
			}		
			
			//Gauri added Filler 3 validation::END
			
			
			
			/*//Gauri added AO Middle Name validation for CR 89435, FVU 1.9:: START
			
			if(Integer.parseInt(objReadFVAL2.finYear) >= 2025 && objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_ORIG) || objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_CORR)) 
			{
				if(aoTitle.equals("01") || aoTitle.equals("02") || aoTitle.equals("03"))
				{
					if (!(aoMiddleName.equals(TBAF_FIELD_NULL) || aoMiddleName.equals(TBAF_FIELD_SEPERATOR)))
					{
						//errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[26] + "^^" + TBAF_FV_5048);  
						 if (objRecVal.isFieldNull(aoMiddleName))
						{
							errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[26] + "^^" + TBAF_FV_5048);  
						}
						else if (aoMiddleName.length() > 25)
						{
							errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[26] + "^^" + TBAF_FV_5048);  
						}
						else if (! objRecVal.checkAlphabets(aoMiddleName))
						{
							errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[26] + "^^" + TBAF_FV_5048);  
						}
						else //gauri added this
						{
						objReadFVAL2.statReportBuffer.append('-' + TBAF_FIELD_SEPERATOR);
						}
//						else //pdf issue
//						{
//							rawFileBean.setValue(aoMiddleName,RawFileBean.AO_MIDDLE_NAME); 
//							objReadFVAL2.statReportBuffer.append('-' + TBAF_FIELD_SEPERATOR);
//						}
					}
					
				}
				else if(aoTitle.equals("04")) {
					if(!(aoMiddleName.equals(TBAF_FIELD_NULL) || aoMiddleName.equals(TBAF_FIELD_SEPERATOR))) {
					errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[26] + "^^" + TBAF_FV_5042);
					}
					else //gauri added this
					{
					objReadFVAL2.statReportBuffer.append('-' + TBAF_FIELD_SEPERATOR);
					}
//					else //pdf issue
//					{
//						rawFileBean.setValue(aoMiddleName,RawFileBean.AO_MIDDLE_NAME); 
//						objReadFVAL2.statReportBuffer.append('-' + TBAF_FIELD_SEPERATOR);
//					}
				}
			}
			else //gauri added this
			{
				objReadFVAL2.statReportBuffer.append('-' + TBAF_FIELD_SEPERATOR);
			}
			
			//Gauri added AO Middle Name validation for CR 89435, FVU 1.9:: END
*/			
		
			/**
			 *	Validation of LAST NATURE OF DEDUCTION(Field No. 27 Of Batch Header Record)
			 * 	
			 * 	For Regular Statement this field should be NULL.
			 *	If any value is specified, the file should be rejected.
			 */
		/*	if (objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_ORIG))
			{
				if (!lastNatureOfDeduction.equals(TBAF_FIELD_NULL) && !lastNatureOfDeduction.equals(TBAF_FIELD_SEPERATOR))
				{
					errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[27] + "^^" + TBAF_FV_2037);
				}
				else
				{
					objReadFVAL2.statReportBuffer.append("-" + TBAF_FIELD_SEPERATOR);
				}
			}  */
			/**	
			 *	This field is MANDATORY for the Correction Statement. 
			 *	For Transaction Types C1,C2 and C3 Nature Of Deduction & Last Nature Of Deduction should be EQUAL.
			 *	For Transaction Type C4 it is NOT mandatory that Nature Of Deduction & Last Nature Of Deduction
			 *	must be equal.
			 */
		/*	else if (objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_CORR))
			{
				if (lastNatureOfDeduction.equals(TBAF_FIELD_NULL) 
					|| lastNatureOfDeduction.equals(TBAF_FIELD_SEPERATOR))
				{
					errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[27] + "^^" + TBAF_FV_2030);
				}
				else if (objRecVal.isFieldNull(lastNatureOfDeduction))
				{
					errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[27] + "^^" + TBAF_FV_2031);
				}
				else if (!lastNatureOfDeduction.equals(TBAF_FORM_24Q)
						&& !lastNatureOfDeduction.equals(TBAF_FORM_26Q)
						&& !lastNatureOfDeduction.equals(TBAF_FORM_27Q)
						&& !lastNatureOfDeduction.equals(TBAF_FORM_27EQ))
				{
					errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[27] + "^^" + TBAF_FV_2043);
				}
				else if (objReadFVAL2.transType.equals(TBAF_TRANSACTION_TYPE_C4))
				{
					objReadFVAL2.statReportBuffer.append(lastNatureOfDeduction + TBAF_FIELD_SEPERATOR);
				}
				else if (objReadFVAL2.transType.equals(TBAF_TRANSACTION_TYPE_C1)
						|| objReadFVAL2.transType.equals(TBAF_TRANSACTION_TYPE_C2)
						|| objReadFVAL2.transType.equals(TBAF_TRANSACTION_TYPE_C3))
				{
					if (!lastNatureOfDeduction.equals(natureOfDeduction))
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[27] + "^^" + TBAF_FV_2044);
					}
					else
					{
						objReadFVAL2.statReportBuffer.append(lastNatureOfDeduction + TBAF_FIELD_SEPERATOR);
					}
				}
			}	*/ // End of LAST NATURE OF DEDUCTION Validation
			
			/*********************Validation of Primary Fields For C4 Correction********************/
			/**
			* For C4 Correction, AIN/Last AIN, Financial Year/Last Financial Year, Quarter/Last Quarter, 
			* Deductor Category/Last Deductor Category and Nature Of Deduction/Last Nature Of Deduction,
			* its MANDATORY that atleast one of the fields should be different. If these fields are equal
			* the file should be rejected.
			**/
		/*	if (objReadFVAL2.transType.equals(TBAF_TRANSACTION_TYPE_C4))
			{
				if (tbafAIN.equals(tbafLastAIN)
					&& objReadFVAL2.finYear.equals(objReadFVAL2.lastFinYear)
					&& objReadFVAL2.quarter.equals(objReadFVAL2.lastQuarter)
					&& objReadFVAL2.deductCatgry.equals(objReadFVAL2.lastDeductCatgry)
					&& natureOfDeduction.equals(lastNatureOfDeduction))
				{
					errStrBuff.append(TBAF_BHREC + lineNo + "^" + "-" + "^^" + TBAF_FV_2056);
					objReadFVAL2.errorFoundInBH = true;
				}
			}  */
			
			
			/**
			 *	Validation of Batch Filler 4(Field No. 27 Filler4 of Batch Header Record)
			 *Validation for Filler 4 added for CR 89435, FVU 1.9
			 */
			
			//Gauri added Filler 4 validation::START
			
			if (objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_ORIG) || objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_CORR))
			{
				if (!bhFiller_4.equals(TBAF_FIELD_NULL) && !bhFiller_4.equals(TBAF_FIELD_SEPERATOR))
				{
					errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[27] + "^^" + TBAF_FV_2049);
				}
				/* else if(! (objRecVal.isFieldNull(bhFiller_4)))     
				{
					errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[27] + "^^" + TBAF_FV_2049);
				} */
				else
				{
					objReadFVAL2.statReportBuffer.append("-" + TBAF_FIELD_SEPERATOR);
				}
			}
			//Gauri added Filler 4 validation::END
			
			/*//Gauri added AO Last Name validation for CR 89435, FVU 1.9:: START
			
			if(Integer.parseInt(objReadFVAL2.finYear) >= 2025 && objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_ORIG) || objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_CORR)) 
			{
				if(aoTitle.equals("01") || aoTitle.equals("02") || aoTitle.equals("03"))
				{
					if (!(aoLastName.equals(TBAF_FIELD_NULL) || aoLastName.equals(TBAF_FIELD_SEPERATOR)))
					{
						//errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[27] + "^^" + TBAF_FV_5049);  
						 if (objRecVal.isFieldNull(aoLastName))
						{
							errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[27] + "^^" + TBAF_FV_5049);  
						}
						else if (aoLastName.length() > 25)
						{
							errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[27] + "^^" + TBAF_FV_5049);  
						}
						else if (! objRecVal.checkAlphabets(aoLastName))
						{
							errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[27] + "^^" + TBAF_FV_5049);  
						}
						else //gauri added this
						{
						objReadFVAL2.statReportBuffer.append('-' + TBAF_FIELD_SEPERATOR);
						}
//						else //pdf issue
//						{
//							rawFileBean.setValue(aoLastName,RawFileBean.AO_LAST_NAME); 
//							objReadFVAL2.statReportBuffer.append('-' + TBAF_FIELD_SEPERATOR);
//						}
					}
					
					else if ((aoLastName.equals(TBAF_FIELD_NULL) || aoLastName.equals(TBAF_FIELD_SEPERATOR))){
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[27] + "^^" + TBAF_FV_5049);
					}
					
				}
				else if(aoTitle.equals("04")) {
					if(!(aoLastName.equals(TBAF_FIELD_NULL) || aoLastName.equals(TBAF_FIELD_SEPERATOR))) {
					errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[27] + "^^" + TBAF_FV_5042);
					}
					else //gauri added this
					{
					objReadFVAL2.statReportBuffer.append('-' + TBAF_FIELD_SEPERATOR);
					}
//					else //pdf issue
//					{
//						rawFileBean.setValue(aoLastName,RawFileBean.AO_LAST_NAME); 
//						objReadFVAL2.statReportBuffer.append('-' + TBAF_FIELD_SEPERATOR);
//					}
				}
			}
			else //gauri added this
			{
				objReadFVAL2.statReportBuffer.append('-' + TBAF_FIELD_SEPERATOR);
			}
			
			//Gauri added AO Last Name validation for CR 89435, FVU 1.9:: END
*/			
						
			/**
			 * Validation of COUNT OF DDO TRANSACTION DETAILS(Field No. 28 Of Batch Header Record)
			 * 
			 * For X Correction, count of DDO Transaction details should be NULL.
			 */
			if (!objReadFVAL2.errorFoundInBH)
			{
				
			
			
				if ((objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_CORR)) && (objReadFVAL2.transType.equals(TBAF_TRANSACTION_TYPE_X)) )
				{
					
					
					
					
					if (objReadFVAL2.countTD.equals(TBAF_FIELD_NULL) 
							|| objReadFVAL2.countTD.equals(TBAF_FIELD_SEPERATOR))
						{
							objReadFVAL2.errorFoundInBH = true;
							errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[28] + "^^" + TBAF_FV_2116);  // New Error added in Jan16
						}
						else if (objRecVal.isFieldNull(objReadFVAL2.countTD))
						{
							objReadFVAL2.errorFoundInBH = true;
							errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[28] + "^^" + TBAF_FV_2116);  // New Error added in Jan16
						}
						else if(! "0".equals(objReadFVAL2.countTD))
						{
							objReadFVAL2.errorFoundInBH = true;
							errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[28] + "^^" + TBAF_FV_2085);
						}
						else
						{
							objReadFVAL2.statReportBuffer.append("0" + TBAF_FIELD_SEPERATOR);
						}
				}
			
			
				/**	
				 * 	This field is MANDATORY for Regular statement & Transaction Types M.
				 *	Count should be an integer value with length less than or equal to 9 digits.
				 *	Count should not be "0" (Zero).
				 */
				else
				{
					if (objReadFVAL2.countTD.equals(TBAF_FIELD_NULL) 
						|| objReadFVAL2.countTD.equals(TBAF_FIELD_SEPERATOR))
					{
						objReadFVAL2.errorFoundInBH = true;
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[28] + "^^" + TBAF_FV_2116);  // new error code added in jan16
					}
					else if (objRecVal.isFieldNull(objReadFVAL2.countTD))
					{
						objReadFVAL2.errorFoundInBH = true;
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[28] + "^^" + TBAF_FV_2116);  // new error code added in jan16
					}
					else if (objReadFVAL2.countTD.length() > 9)
					{
						objReadFVAL2.errorFoundInBH = true;
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[28] + "^^" + TBAF_FV_2024);
					}
					else if(objReadFVAL2.countTD.length() != objReadFVAL2.countTD.trim().length())
					{
						objReadFVAL2.errorFoundInBH = true;
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[28] + "^^" + TBAF_FV_2017);
					}
					else if (objRecVal.isInt(objReadFVAL2.countTD))
					{
						objReadFVAL2.errorFoundInBH = true;
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[28] + "^^" + TBAF_FV_2116); // new error code added in jan16
					}
					/*
					 * 
					 * For Regular statement Zero TD is not allowed
					 * The Same is applicable for Correction
					 *  
					 */
					
					else if (objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_ORIG) && Integer.parseInt(objReadFVAL2.countTD.trim()) == 0)
					{
						objReadFVAL2.errorFoundInBH = true;
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[28] + "^^" + TBAF_FV_2047);
					}
					else
					{
						objReadFVAL2.statReportBuffer.append(objReadFVAL2.countTD + TBAF_FIELD_SEPERATOR);
					}
				}
			}	// End of COUNT OF DDO RECORDS Validation
			
			/**
			 *	Validation of TOTAL TAX AMOUNT(Field No. 29 Of Batch Header Record) 
			 *	For C1 Correction, Total Tax Anmount should be NULL.
			 */
		/*	if (invalidTransactionType == false)
			{
				if (objReadFVAL2.transType.equals(TBAF_TRANSACTION_TYPE_C1))
				{
					if (!objReadFVAL2.totalTax.equals(TBAF_FIELD_NULL) 
						&& !objReadFVAL2.totalTax.equals(TBAF_FIELD_SEPERATOR))
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[29] + "^^" + TBAF_FV_2045);
						objReadFVAL2.errorFoundInBH = true;
					}
					else
					{
						objReadFVAL2.statReportBuffer.append("-" + TBAF_FIELD_SEPERATOR);
					}
				}*/
				/**		
				 *	This field is MANDATORY for Regular statement & Transaction Types C2, C3 and C4. 
				 *  Amount must have length less than or equal to 15 digits.
				 *	Amount should be a positive decimal value for Regular statement and C4 Correction.  
				 *	Amount should have "00" in the decimal part(For ex., 200.00 is valid but 200.35 is invalid). 
				 */
				/*else
				{
					if (objReadFVAL2.totalTax.equals(TBAF_FIELD_NULL) 
						|| objReadFVAL2.totalTax.equals(TBAF_FIELD_SEPERATOR))
					{
						objReadFVAL2.invalidTotalTax = true;
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[29] + "^^" + TBAF_FV_2030);
						objReadFVAL2.errorFoundInBH = true;
					}
					else if (objRecVal.isFieldNull(objReadFVAL2.totalTax))
					{
						objReadFVAL2.invalidTotalTax = true;
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[29] + "^^" + TBAF_FV_2031);
						objReadFVAL2.errorFoundInBH = true;
					}
					else if (objReadFVAL2.totalTax.length() > 15)
					{
						objReadFVAL2.invalidTotalTax = true;
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[29] + "^^" + TBAF_FV_2024);
						objReadFVAL2.errorFoundInBH = true;
					}
					else if (Double.parseDouble(objReadFVAL2.totalTax.trim()) <= 0.00)
					{
						objReadFVAL2.invalidTotalTax = true;
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[29] + "^^" + TBAF_FV_2031);
						objReadFVAL2.errorFoundInBH = true;
					}*/
					/**
					 *	Blank spaces should not be specified between the numbers.
					 *	No leading and trailing spaces should be specified.
					 *	No tab spaces should be specified.
					 *	Amount specified should be greater than or equal to 0.00 for Regular and C4 Correction.
					 */
				/*	else if (objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_ORIG)
							 || (objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_CORR)
							 && !objReadFVAL2.transType.equals(TBAF_TRANSACTION_TYPE_C1) 
							 && objReadFVAL2.transType.equals(TBAF_TRANSACTION_TYPE_C4)))
					{
						totalTaxAmount = objRecVal.trimInnerSpaces(objReadFVAL2.totalTax);
						if (objRecVal.isDecimalNumber(totalTaxAmount) || !totalTaxAmount.endsWith("00"))
						{
							if (objRecVal.isInt(totalTaxAmount))
							{
								objReadFVAL2.invalidTotalTax = true;
								errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[29] + "^^" + TBAF_FV_2031);
								objReadFVAL2.errorFoundInBH = true;
							}
							else
							{
								objReadFVAL2.invalidTotalTax = true;
								errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[29] + "^^" + TBAF_FV_2046);
								objReadFVAL2.errorFoundInBH = true;
							}
						}
						else if (!totalTaxAmount.equals(objReadFVAL2.totalTax))
						{
							objReadFVAL2.invalidTotalTax = true;
							errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[29] + "^^" + TBAF_FV_2017);
							objReadFVAL2.errorFoundInBH = true;
						}
						else if (objReadFVAL2.totalTax.trim().length() != objReadFVAL2.totalTax.length())
						{
							objReadFVAL2.invalidTotalTax = true;
							errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[29] + "^^" + TBAF_FV_2017);
							objReadFVAL2.errorFoundInBH = true;
						}
						else
						{
							objReadFVAL2.statReportBuffer.append(objReadFVAL2.totalTax + TBAF_FIELD_SEPERATOR);
						}
					}
				}
			}*/	
			if (objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_ORIG) || (objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_CORR) && objReadFVAL2.transType.equals(TBAF_TRANSACTION_TYPE_M)) )
			 {
				
				
				   if (objReadFVAL2.totalTax.equals(TBAF_FIELD_NULL) || objReadFVAL2.totalTax.equals(TBAF_FIELD_SEPERATOR))
					{
					   objReadFVAL2.errorFoundInBH = true;
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[29] + "^^" + TBAF_FV_2030);
					}
					else if (objRecVal.isFieldNull(objReadFVAL2.totalTax))
					{
						
						objReadFVAL2.errorFoundInBH = true;
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[29] + "^^" + TBAF_FV_2031);
					}
					else if (objReadFVAL2.totalTax.length() > TBAF_TOTAL_TAX_LEN || objReadFVAL2.totalTax.trim().length() > TBAF_TOTAL_TAX_LEN)
					{
						
						objReadFVAL2.errorFoundInBH = true;
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[29] + "^^" + TBAF_FV_2024);
					}
					else if(objReadFVAL2.totalTax.trim().length() != objReadFVAL2.totalTax.length())
					{
						
						objReadFVAL2.errorFoundInBH = true;
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[29] + "^^" + TBAF_FV_2031);
					}
					else if(objRecVal.isDecimalNumber(objReadFVAL2.totalTax) || ! objReadFVAL2.totalTax.endsWith("00"))
					{
					   if(objRecVal.isValidValue(objReadFVAL2.totalTax))
					   {
						 
						   objReadFVAL2.errorFoundInBH = true;
						   errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[29] + "^^" + TBAF_FV_2031);
					   }
					   else
					   {
						   objReadFVAL2.errorFoundInBH = true;
						   errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[29] + "^^" + TBAF_FV_2046);
					   }
						
					}
					else
					{
						objReadFVAL2.statReportBuffer.append(objReadFVAL2.totalTax.trim() + TBAF_FIELD_SEPERATOR);
					}
			     }
				 
			else if((objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_CORR)) && (objReadFVAL2.transType.equals(TBAF_TRANSACTION_TYPE_X)) )
			{
				
				
				
				if (objReadFVAL2.totalTax.equals(TBAF_FIELD_NULL) || objReadFVAL2.totalTax.equals(TBAF_FIELD_SEPERATOR))
				{
					objReadFVAL2.errorFoundInBH = true;
					errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[29] + "^^" + TBAF_FV_2030);
				}
				else if (objRecVal.isFieldNull(objReadFVAL2.totalTax))
				{
					objReadFVAL2.errorFoundInBH = true;
					errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[29] + "^^" + TBAF_FV_2031);
				}
				else if(! "0.00".equals(objReadFVAL2.totalTax))
				{
					objReadFVAL2.errorFoundInBH = true;
					errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[29] + "^^" + TBAF_FV_2086);
				}
				else
				{
					 objReadFVAL2.statReportBuffer.append("0.00" + TBAF_FIELD_SEPERATOR);
				}
			}
			
			
			
			// End of TOTAL TAX AMOUNT Validation
			
		   /**	
			*	Validation of ORIGINAL RRR NUMBER(Field No. 30 Of Batch Header Record)
			*	For Regular Statement this field should be NULL.
			*/
			if (objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_ORIG))
			{
				if (!originalRRR.equals(TBAF_FIELD_NULL) && !originalRRR.equals(TBAF_FIELD_SEPERATOR))
				{
					errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[30] + "^^" + TBAF_FV_2037);
				}
				else
				{
					rawFileBean.setValue("-",RawFileBean.ORIGINAL_RRR_NUM);
					objReadFVAL2.statReportBuffer.append("-" + TBAF_FIELD_SEPERATOR);
				}
			}
			/**	
			 * 	This field is MANDATORY for the Correction Statement.
			 *	Original RRR Number should be an inetger of length exactly equal to 15 digits.
			 *	Original RRR Number should follow the RRR Number Validation.
			 */
			else if (objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_CORR))
			{
				if (originalRRR.equals(TBAF_FIELD_NULL) || originalRRR.equals(TBAF_FIELD_SEPERATOR))
				{
					errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[30] + "^^" + TBAF_FV_2118); // new Error added in jan16
				}
				else if (originalRRR.length() != 15 || originalRRR.trim().length() != 15)
				{
					errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[30] + "^^" + TBAF_FV_2048);
				}
				else
				{
					if (objRecVal.isValidRrrNumber(originalRRR))
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[30] + "^^" + TBAF_FV_2117); // new Error added in jan16
					}
					else
					{
						rawFileBean.setValue(originalRRR.trim(),RawFileBean.ORIGINAL_RRR_NUM);
						objReadFVAL2.statReportBuffer.append(originalRRR.trim() + TBAF_FIELD_SEPERATOR);
					}
				}
			}	// End of ORIGINAL RRR NUMBER Validation			
				
			
		   /**	
			*	Validation of PREVIOUS RRR NUMBER(Field No. 31 Of Batch Header Record)
			*	For Regular Statement this field should be NULL.
			*/
			if (objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_ORIG))
			{
				if (!previousRRR.equals(TBAF_FIELD_NULL) && !previousRRR.equals(TBAF_FIELD_SEPERATOR))
				{
					errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[31] + "^^" + TBAF_FV_2037);
				}
				
				else
				{
					rawFileBean.setValue("-",RawFileBean.PREVIOUS_RRR_NUM);
				}
			}
			/**	
			 *	This field is MANDATORY for the Correction Statement.	
			 *	Previous RRR Number should be an inetger of length exactly equal to 15 digits.
			 *	Previous RRR Number should follow the RRR Number Validation.
			 */
			else if (objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_CORR) )
			{
				if (previousRRR.equals(TBAF_FIELD_NULL) || previousRRR.equals(TBAF_FIELD_SEPERATOR))
				{
					errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[31] + "^^" + TBAF_FV_2120);  // new error added in jan16
				}
				else if (previousRRR.length() != 15 || previousRRR.trim().length() != 15)
				{
					errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[31] + "^^" + TBAF_FV_2048);
				}
				else if (objRecVal.isValidRrrNumber(previousRRR))
				{
					errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[31] + "^^" + TBAF_FV_2120); // new error added in jan16
				}
				else
				{
					rawFileBean.setValue(previousRRR.trim(),RawFileBean.PREVIOUS_RRR_NUM);
				}
			}	// End of PREVIOUS RRR NUMBER Validation
			
			
			/**
			 *	Validation of PROVISIONAL RECEIPT NUMBER(PRN)(Field No. 32 Of Batch Header Record)
			 *	This field must be NULL when the Uploader Type is "D" (AO/Organization Upload).
			 */
			if (objReadFVAL2.uploadBy.equals(TBAF_UPLOADED_BY_AO))
			{
				if (!prnNumber.equals(TBAF_FIELD_NULL) && !prnNumber.equals(TBAF_FIELD_SEPERATOR))
				{
					errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[32] + "^^" + TBAF_FV_2049);
				}
				else
				{
					objReadFVAL2.statReportBuffer.append("-" + TBAF_FIELD_SEPERATOR);
				}
			}
			/**
			 *	This field is MANDATORY when the Uploader Type is "T" (TFC Upload) 
			 * 
			 *	PRN should be an integer value with length exactly equal to 15 digits.
			 *	PRN should follow the RRR Number Validation.
			 *	First 5-digits of PRN should be equal to TFC-ID. 
			 */
			else if (objReadFVAL2.uploadBy.equals(TBAF_UPLOADED_BY_TFC))
			{
				if (prnNumber.equals(TBAF_FIELD_NULL) || prnNumber.equals(TBAF_FIELD_SEPERATOR))
				{
					errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[32] + "^^" + TBAF_FV_2030);
				}
				else if (objRecVal.isFieldNull(prnNumber))
				{
					errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[32] + "^^" + TBAF_FV_2031);
				}
				else if (prnNumber.trim().length() != 15 || prnNumber.length() != 15)
				{
					errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[32] + "^^" + TBAF_FV_2048);
				}
				else
				{
					if (!prnNumber.substring(0, 5).equals(objReadFVAL2.id))
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[32] + "^^" + TBAF_FV_2050);
					}
					else if (objRecVal.isValidRrrNumber(prnNumber))
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[32] + "^^" + TBAF_FV_2031);
					}
					else
					{
						objReadFVAL2.statReportBuffer.append(prnNumber + TBAF_FIELD_SEPERATOR);
					}
				}
			}	// End of PROVISIONAL RECEIPT NUMBER(PRN) Validation
			
			/**
			 *	Validation of PRN DATE(Field No. 33 Of Batch Header Record)	
			 *	This field must be NULL when the Uploader Type is "D" (AO/Organization Upload).
			 */
			if (objReadFVAL2.uploadBy.equals(TBAF_UPLOADED_BY_AO))
			{
				if (!prnDate.equals(TBAF_FIELD_NULL) && !prnDate.equals(TBAF_FIELD_SEPERATOR))
				{
					errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[33] + "^^" + TBAF_FV_2049);
				}
				else
				{
					objReadFVAL2.statReportBuffer.append("-" + TBAF_FIELD_SEPERATOR);
				}
			}
			/**
			 *	This field is MANDATORY when the Uploader Type is "T" (TFC Upload) 
			 * 
			 *	PRN Date should be an integer value with length exactly equal to 8 digits. 	
			 *	PRN date Should be in the format DD-MM-YYYY.
			 *	PRN date should not be a future date.
			 */
			else if (objReadFVAL2.uploadBy.equals(TBAF_UPLOADED_BY_TFC))
			{
				if (prnDate.equals(TBAF_FIELD_NULL) || prnDate.equals(TBAF_FIELD_SEPERATOR))
				{
					errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[33] + "^^" + TBAF_FV_2030);
				}
				else if (objRecVal.isFieldNull(prnDate))
				{
					errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[33] + "^^" + TBAF_FV_2031);
				}
				else if (prnDate.trim().length() != 8 || prnDate.length() != 8)
				{
					errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[33] + "^^" + TBAF_FV_2051);
				}
				else if (prnDate.trim().length() == 8 && prnDate.length() == 8)
				{
					if (objRecVal.isDate(prnDate) && objRecVal.isFutureDate(prnDate))
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[33] + "^^" + TBAF_FV_2031);
					}
					else
					{
						objReadFVAL2.statReportBuffer.append(prnDate + TBAF_FIELD_SEPERATOR);
					}
				}
			}
			
			//	End of PRN DATE Validation
			
			
			//Added By Subhankar
			/**
			 *	Validation of Month Of Transfer Voucher(Field No. 34 Of Batch Header Record)	
			 *	This field is a Mandatory Field for both Regular and Correction.
			 */
			
				
				if (objReadFVAL2.transferVoucherMonth.equals(TBAF_FIELD_NULL) || objReadFVAL2.transferVoucherMonth.equals(TBAF_FIELD_SEPERATOR))
				{
					errStrBuff.append(TBAF_BHREC + "-" + "^" + TBAF_BH_FIELD[34] + "^^" + TBAF_FV_2121);
					objReadFVAL2.isValid24GFile = false;
				}
				else if (objRecVal.isFieldNull(objReadFVAL2.transferVoucherMonth))
				{
					errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[34] + "^^" + TBAF_FV_2121);
					objReadFVAL2.isValid24GFile = false;
				}
				
				//Month of Transfer voucher is made 2 digit manadatory
				else if(objReadFVAL2.transferVoucherMonth.trim().length() != TBAF_BH_TRANSFER_VOUCHER_MONTH_LEN || objReadFVAL2.transferVoucherMonth.length() != TBAF_BH_TRANSFER_VOUCHER_MONTH_LEN)
				{
					errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[34] + "^^" + TBAF_FV_2091);
					objReadFVAL2.isValid24GFile = false;
				}
				else if(objReadFVAL2.transferVoucherMonth.trim().length() != objReadFVAL2.transferVoucherMonth.length())
				{
					errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[34] + "^^" + TBAF_FV_2121);
					objReadFVAL2.isValid24GFile = false;
				}
				else if (objRecVal.isInt(objReadFVAL2.transferVoucherMonth) || objRecVal.isInt(objReadFVAL2.finYear) || (objReadFVAL2.finYear.trim().length() != objReadFVAL2.finYear.length()))
				{
					errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[34] + "^^" + TBAF_FV_2082);
					objReadFVAL2.isValid24GFile = false;
				}
				else if(! (objRecVal.isValidMonthOfTransferVoucher(objReadFVAL2.transferVoucherMonth,objReadFVAL2.finYear)))
				{
					errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[34] + "^^" + TBAF_FV_2058);
					objReadFVAL2.isValid24GFile = false;
				}
				else if(Integer.parseInt(objReadFVAL2.finYear) == Integer.parseInt(Parameters.tbafThreshholdYear))
				{
				    if((Integer.parseInt(objReadFVAL2.transferVoucherMonth) < Integer.parseInt(Parameters.tbafThreshholdMonth)) || ! (objRecVal.isValidMonthOfTransferVoucher(objReadFVAL2.transferVoucherMonth,objReadFVAL2.finYear)))   //Added by Subhankar (This check is to determine whether the fin year is less than the value specified in the properties file)
				    {
					  errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[34] + "^^" + TBAF_FV_2121);
					  objReadFVAL2.isValid24GFile = false;
				    }
				    else
				    {
				    	rawFileBean.setValue(objReadFVAL2.transferVoucherMonth,RawFileBean.MONTH_FLDNUM); //Added by Bharath for Raw File Generation
				    	objReadFVAL2.statReportBuffer.append(objReadFVAL2.transferVoucherMonth + TBAF_FIELD_SEPERATOR);
				    }
				} 
				
				else
				{
					rawFileBean.setValue(objReadFVAL2.transferVoucherMonth,RawFileBean.MONTH_FLDNUM); //Added by Bharath for Raw File Generation
					objReadFVAL2.statReportBuffer.append(objReadFVAL2.transferVoucherMonth + TBAF_FIELD_SEPERATOR);
				}
			
			  
			
			//End of Month Of Transfer Voucher Validation
			
			/**
			 *	Validation of Responsible Person Address 1(Field No. 35 Of Batch Header Record)	
			 *	This field is a Mandatory Field.
			 */
			
			if (objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_ORIG) || (objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_CORR) && objReadFVAL2.transType.equals(TBAF_TRANSACTION_TYPE_M)) )
					
				{
					//	Validation of Responsible Preson ADDRESS 1(Field No. 36 Of Batch Header Record)
					if (rPersonAddr1.equals(TBAF_FIELD_NULL) || rPersonAddr1.equals(TBAF_FIELD_SEPERATOR))
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[35] + "^^" + TBAF_FV_2141);
					}
					else if (objRecVal.isFieldNull(rPersonAddr1))
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[35] + "^^" + TBAF_FV_2023);
					}
					else if (rPersonAddr1.length() > TBAF_RESPONSIBLE_PERSON_ADDRESS_LEN)
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[35] + "^^" + TBAF_FV_2024);
					}
					else if (! objRecVal.checkValidAOAddress(rPersonAddr1))
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[35] + "^^" + TBAF_FV_2023);
					}
					else
					{
						rawFileBean.setValue(rPersonAddr1,RawFileBean.RESP_PERS_ADDR1_FLDNUM); //Added by Bharath for Raw File Generation
						objReadFVAL2.statReportBuffer.append(rPersonAddr1 + TBAF_FIELD_SEPERATOR);
					}
					
					
					//End of Responsible Person Address 1 Validation
					
					
					/**
					 *	AO Address 2, AO Address 3, AO Address 4 are OPTIONAL fields.
					 *  Following validations are done when the user DOES NOT SPECIFY ANY VALUE in this field:
					 *	
					 *	(1)	Check for only TAB spaces are specified.
					 *	(2)	Check if greater than 25 blank spaces are specified.
					 *
					 *	Following validations are done when the user SPECIFIES ANY VALUE in this field:
					 *	
					 *	(1)	Check if the specified value is of length less than 25 characters.
					 *	(2)	Check if the specified value is not having TAB spaces.
					 */
					//	Validation of Responsible Person ADDRESS 2(Field No. 36 Of Batch Header Record)
					if (rPersonAddr2.equals(TBAF_FIELD_NULL) || rPersonAddr2.equals(TBAF_FIELD_SEPERATOR))
					{
						// Optional Field, No Error Checking
						rawFileBean.setValue(rPersonAddr2,RawFileBean.RESP_PERS_ADDR2_FLDNUM); //Added by Bharath for Raw File Generation
						objReadFVAL2.statReportBuffer.append(" " + TBAF_FIELD_SEPERATOR);
					}
					else if (objRecVal.isFieldNull(rPersonAddr2))
					{
						if (objRecVal.checkTabSpaces(rPersonAddr2))
						{
							errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[36] + "^^" + TBAF_FV_2023);
						}
						else if (rPersonAddr2.length() > TBAF_RESPONSIBLE_PERSON_ADDRESS_LEN)
						{
							errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[36] + "^^" + TBAF_FV_2024);
						}
						else
						{
							rawFileBean.setValue(rPersonAddr2,RawFileBean.RESP_PERS_ADDR2_FLDNUM); //Added by Bharath for Raw File Generation
							objReadFVAL2.statReportBuffer.append(" " + TBAF_FIELD_SEPERATOR);
						}
					}
					else
					{
						if (rPersonAddr2.length() > TBAF_RESPONSIBLE_PERSON_ADDRESS_LEN)
						{
							errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[36] + "^^" + TBAF_FV_2024);
						}
						else if (! objRecVal.checkValidAOAddress(rPersonAddr2))
						{
							errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[36] + "^^" + TBAF_FV_2023);
						}
						else
						{
							rawFileBean.setValue(rPersonAddr2,RawFileBean.RESP_PERS_ADDR2_FLDNUM); //Added by Bharath for Raw File Generation
							objReadFVAL2.statReportBuffer.append(rPersonAddr2 + TBAF_FIELD_SEPERATOR);
						}
					}
					
					//End of Responsible Person Address 2 Validation
					
					
					//	Validation of Responsible Person ADDRESS 3(Field No. 37 Of Batch Header Record)
					if (rPersonAddr3.equals(TBAF_FIELD_NULL) || rPersonAddr3.equals(TBAF_FIELD_SEPERATOR))
					{
						//	Optional Field, No Error Checking
						rawFileBean.setValue(rPersonAddr3,RawFileBean.RESP_PERS_ADDR3_FLDNUM); //Added by Bharath for Raw File Generation
						objReadFVAL2.statReportBuffer.append(" " + TBAF_FIELD_SEPERATOR);
					}
					else if (objRecVal.isFieldNull(rPersonAddr3))
					{
						if (objRecVal.checkTabSpaces(rPersonAddr3))
						{
							errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[37] + "^^" + TBAF_FV_2123);  // new Error code added in jan16
						}
						else if (rPersonAddr3.length() > TBAF_RESPONSIBLE_PERSON_ADDRESS_LEN)
						{
							errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[37] + "^^" + TBAF_FV_2024);
						}
						else
						{
							rawFileBean.setValue(rPersonAddr3,RawFileBean.RESP_PERS_ADDR3_FLDNUM); //Added by Bharath for Raw File Generation
							objReadFVAL2.statReportBuffer.append(" " + TBAF_FIELD_SEPERATOR);
						}
					}
					else
					{
						if (rPersonAddr3.length() > TBAF_RESPONSIBLE_PERSON_ADDRESS_LEN)
						{
							errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[37] + "^^" + TBAF_FV_2024);
						}
						else if (! objRecVal.checkValidAOAddress(rPersonAddr3))
						{
							errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[37] + "^^" + TBAF_FV_2123);  // new Error code added in jan16
						}
						else
						{
							rawFileBean.setValue(rPersonAddr3,RawFileBean.RESP_PERS_ADDR3_FLDNUM); //Added by Bharath for Raw File Generation
							objReadFVAL2.statReportBuffer.append(rPersonAddr3 + TBAF_FIELD_SEPERATOR);
						}
					}
					
					
					//End of Responsible Person Address 3 Validation
					
					
					
					//	Validation of Responsible Person ADDRESS 4(Field No. 38 Of Batch Header Record)
					if (rPersonAddr4.equals(TBAF_FIELD_NULL) || rPersonAddr4.equals(TBAF_FIELD_SEPERATOR))
					{
						//	Optional Field, No Error Checking
						rawFileBean.setValue(rPersonAddr4,RawFileBean.RESP_PERS_ADDR4_FLDNUM); //Added by Bharath for Raw File Generation
						objReadFVAL2.statReportBuffer.append(" " + TBAF_FIELD_SEPERATOR);
					}
					else if (objRecVal.isFieldNull(rPersonAddr4))
					{
						if (objRecVal.checkTabSpaces(rPersonAddr4))
						{
							errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[38] + "^^" + TBAF_FV_2123);   // new Error code added in jan16
						}
						else if (rPersonAddr4.length() > TBAF_RESPONSIBLE_PERSON_ADDRESS_LEN)
						{
							errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[38] + "^^" + TBAF_FV_2024);
						}
						else
						{
							rawFileBean.setValue(rPersonAddr4,RawFileBean.RESP_PERS_ADDR4_FLDNUM); //Added by Bharath for Raw File Generation
							objReadFVAL2.statReportBuffer.append(" " + TBAF_FIELD_SEPERATOR);
						}
					}
					else
					{
						if (rPersonAddr4.length() > TBAF_RESPONSIBLE_PERSON_ADDRESS_LEN)
						{
							errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[38] + "^^" + TBAF_FV_2024);
						}
						else if (! objRecVal.checkValidAOAddress(rPersonAddr4))
						{
							errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[38] + "^^" + TBAF_FV_2123); // new Error code added in jan16
						}
						else
						{
							rawFileBean.setValue(rPersonAddr4,RawFileBean.RESP_PERS_ADDR4_FLDNUM); //Added by Bharath for Raw File Generation
							objReadFVAL2.statReportBuffer.append(rPersonAddr4 + TBAF_FIELD_SEPERATOR);
						}
					}
				}
			
			else if(objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_CORR) && (objReadFVAL2.transType.equals(TBAF_TRANSACTION_TYPE_X)))
			{
//				Validation of ADDRESS 1.
				if (!rPersonAddr1.equals(TBAF_FIELD_NULL) && !rPersonAddr1.equals(TBAF_FIELD_SEPERATOR))
				{
					errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[35] + "^^" + TBAF_FV_2025);
				}
				else
				{
					
					objReadFVAL2.statReportBuffer.append(" " + TBAF_FIELD_SEPERATOR);
				}
				//	Validation of ADDRESS 2.
				if (!rPersonAddr2.equals(TBAF_FIELD_NULL) && !rPersonAddr2.equals(TBAF_FIELD_SEPERATOR))
				{
					errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[36] + "^^" + TBAF_FV_2025);
				}
				else
				{
					
					objReadFVAL2.statReportBuffer.append(" " + TBAF_FIELD_SEPERATOR);
				}
				//	Validation of ADDRESS 3.
				if (!rPersonAddr3.equals(TBAF_FIELD_NULL) && !rPersonAddr3.equals(TBAF_FIELD_SEPERATOR))
				{
					errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[37] + "^^" + TBAF_FV_2025);
				}
				else
				{
					
					objReadFVAL2.statReportBuffer.append(" " + TBAF_FIELD_SEPERATOR);
				}
				//	Validation of ADDRESS 4.
				if (!rPersonAddr4.equals(TBAF_FIELD_NULL) && !rPersonAddr4.equals(TBAF_FIELD_SEPERATOR))
				{
					errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[38] + "^^" + TBAF_FV_2025);
				}
				else
				{
					
					objReadFVAL2.statReportBuffer.append(" " + TBAF_FIELD_SEPERATOR);
				}
			}
			
			
			//End of Responsible Person Address 4 Validation
			
			
			
			/**
			 *	Validation of Responsible Person City(Field No. 39 Of Batch Header Record)	
			 *	This field is a Mandatory Field.
			 */
			if (objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_ORIG) || (objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_CORR) && objReadFVAL2.transType.equals(TBAF_TRANSACTION_TYPE_M)) )
				{
					if (rPersonCity.equals(TBAF_FIELD_NULL) || rPersonCity.equals(TBAF_FIELD_SEPERATOR))
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[39] + "^^" + TBAF_FV_2124); // new Erro code adeed in jan16
					}
					else if (objRecVal.isFieldNull(rPersonCity))
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[39] + "^^" + TBAF_FV_2125);  // new Erro code adeed in jan16
					}
					else if (rPersonCity.length() > TBAF_RESPONSIBLE_PERSON_CITY_LEN ||rPersonCity.trim().length() > TBAF_RESPONSIBLE_PERSON_CITY_LEN)
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[39] + "^^" + TBAF_FV_2024);
					}
					else if(rPersonCity.trim().length() != rPersonCity.length())
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[39] + "^^" + TBAF_FV_2125); // new Erro code adeed in jan16
					}
					else if (objRecVal.isValidCityName(rPersonCity))
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[39] + "^^" + TBAF_FV_2125); // new Erro code adeed in jan16
					}
					else
					{
						objReadFVAL2.statReportBuffer.append(rPersonCity + TBAF_FIELD_SEPERATOR);
					}
				}
			else if(objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_CORR) && (objReadFVAL2.transType.equals(TBAF_TRANSACTION_TYPE_X)))
			{
				if(!rPersonCity.equals(TBAF_FIELD_NULL) && !rPersonCity.equals(TBAF_FIELD_SEPERATOR))
				{
					errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[39] + "^^" + TBAF_FV_2125); // new Erro code adeed in jan16
				}
				else
				{
					objReadFVAL2.statReportBuffer.append("-" + TBAF_FIELD_SEPERATOR);
				}
			}
			
			
			//End of Responsible Person City Validation
			
			
			/**
			 *	Validation of Responsible Person State(Field No. 40 Of Batch Header Record)	
			 *	This field is a Mandatory Field.
			 */
			
			if (objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_ORIG) || (objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_CORR) && objReadFVAL2.transType.equals(TBAF_TRANSACTION_TYPE_M)) )
					
				{
					if (rPersonState.equals(TBAF_FIELD_NULL) || rPersonState.equals(TBAF_FIELD_SEPERATOR))
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[40] + "^^" + TBAF_FV_2126);  // new error code added in Jan16
					}
					else if (objRecVal.isFieldNull(rPersonState))
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[40] + "^^" + TBAF_FV_2126); // new error code added in Jan16
					}
					else if (rPersonState.length() > TBAF_RESPONSIBLE_PERSON_STATE_LEN || rPersonState.trim().length() > TBAF_RESPONSIBLE_PERSON_STATE_LEN )
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[40] + "^^" + TBAF_FV_2028);
					}
					else if (objRecVal.isInt(rPersonState))
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[40] + "^^" + TBAF_FV_2126); // new error code added in Jan16
					}
					else if(rPersonState.trim().length() != rPersonState.length())
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[40] + "^^" + TBAF_FV_2126);  // new error code added in Jan16
					}
					else if (Integer.parseInt(rPersonState.trim()) > TBAF_RESPONSIBLE_PERSON_STATE_HIGH_RANGE 
							 || Integer.parseInt(rPersonState.trim()) < TBAF_RESPONSIBLE_PERSON_STATE_LOW_RANGE
							 || Integer.parseInt(rPersonState.trim()) == 8) //24GFVU 1.7 Changes for state code 08
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[40] + "^^" + TBAF_FV_2029);
					}
					else
					{
					    rawFileBean.setValue(rPersonState,RawFileBean.RESP_PERS_STATE_FLDNUM); //Added by Bharath for Raw File Generation
						objReadFVAL2.statReportBuffer.append(rPersonState + TBAF_FIELD_SEPERATOR);
					}
				}
			
			else if(objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_CORR) && (objReadFVAL2.transType.equals(TBAF_TRANSACTION_TYPE_X)))
			{
				if(!rPersonState.equals(TBAF_FIELD_NULL) && !rPersonState.equals(TBAF_FIELD_SEPERATOR))
				{
					errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[40] + "^^" + TBAF_FV_2025);
				}
				else
				{
					objReadFVAL2.statReportBuffer.append("-" + TBAF_FIELD_SEPERATOR);
				}
			}

			
			//End of Responsible Person State Validation
			
			/**
			 *	Validation of Responsible Person PIN(Field No. 41 Of Batch Header Record)	
			 *	This field is a Mandatory Field.
			 */
			
			
			if (objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_ORIG) || (objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_CORR) && objReadFVAL2.transType.equals(TBAF_TRANSACTION_TYPE_M)) )
				{
					if (rPersonPin.equals(TBAF_FIELD_NULL) || rPersonPin.equals(TBAF_FIELD_SEPERATOR))
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[41] + "^^" + TBAF_FV_2127); // new Error code added in jan16
					}
					else if (objRecVal.isFieldNull(rPersonPin))
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[41] + "^^" + TBAF_FV_2127); // new Error code added in jan16
					}
					else if (rPersonPin.trim().length() != TBAF_RESPONSIBLE_PERSON_PIN_LEN || rPersonPin.length() != TBAF_RESPONSIBLE_PERSON_PIN_LEN)
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[41] + "^^" + TBAF_FV_2032);
					}
					else if(rPersonPin.trim().length() != rPersonPin.length())
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[41] + "^^" + TBAF_FV_2127); // new Error code added in jan16
					}
					else if (objRecVal.isInt(rPersonPin))
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[41] + "^^" + TBAF_FV_2127); // new Error code added in jan16
					}
					else if( Integer.parseInt(rPersonPin.trim()) == 0 || Integer.parseInt(rPersonPin.trim()) == 999999)
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[41] + "^^" + TBAF_FV_2078);
					}
					else if (Integer.parseInt(rPersonPin.trim()) < 110001)
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[41] + "^^" + TBAF_FV_2033);
					}
					
					else
					{
						rawFileBean.setValue(rPersonPin,RawFileBean.RESP_PERS_PIN_FLDNUM); //Added by Bharath for Raw File Generation
						objReadFVAL2.statReportBuffer.append(rPersonPin + TBAF_FIELD_SEPERATOR);
					}
				}
			
			else if(objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_CORR) && (objReadFVAL2.transType.equals(TBAF_TRANSACTION_TYPE_X)))
			{
				if(!rPersonPin.equals(TBAF_FIELD_NULL) && !rPersonPin.equals(TBAF_FIELD_SEPERATOR))
				{
					errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[41] + "^^" + TBAF_FV_2025);
				}
				else
				{
					objReadFVAL2.statReportBuffer.append("-" + TBAF_FIELD_SEPERATOR);
				}
			}

			
			
			//End of Responsible Person PIN Validation
			
			/**
			 *	Validation of Responsible Person STD Code(Field No. 42 Of Batch Header Record)	
			 *	This field is a Mandatory Field.
			 */
			   
			if (objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_ORIG) || (objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_CORR) && objReadFVAL2.transType.equals(TBAF_TRANSACTION_TYPE_M)) )
				{
					if (! rPersonStdCode.equals(TBAF_FIELD_NULL) && ! rPersonStdCode.equals(TBAF_FIELD_SEPERATOR))
					{
					/*	errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[42] + "^^" + TBAF_FV_2030);
					}
					else if (objRecVal.isFieldNull(rPersonStdCode))
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[42] + "^^" + TBAF_FV_2031);
					} */
					   if (rPersonStdCode.length() > TBAF_RESPONSIBLE_PERSON_STD_LEN || rPersonStdCode.trim().length() > TBAF_RESPONSIBLE_PERSON_STD_LEN)
					    {
						   errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[42] + "^^" + TBAF_FV_2024);
					    }
					   else if(rPersonStdCode.trim().length() != rPersonStdCode.length())
					    {
						   errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[42] + "^^" + TBAF_FV_2128); // new Error code added in jan16
					    }
					   else if (objRecVal.isInt(rPersonStdCode))
					    {
						   errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[42] + "^^" + TBAF_FV_2128); // new Error code added in jan16
					    }
					   else if (Integer.parseInt(rPersonStdCode.trim()) == 0 ||Integer.parseInt(rPersonStdCode.trim()) == 99999)
					    {
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[42] + "^^" + TBAF_FV_2081);
					    }
					   else
					    {
						objReadFVAL2.statReportBuffer.append(rPersonStdCode + TBAF_FIELD_SEPERATOR);
					    }
				    }
					else
					{
						objReadFVAL2.statReportBuffer.append("-" + TBAF_FIELD_SEPERATOR);
					}
				}
			else if(objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_CORR) && (objReadFVAL2.transType.equals(TBAF_TRANSACTION_TYPE_X)))
			{
				if(!rPersonStdCode.equals(TBAF_FIELD_NULL) && !rPersonStdCode.equals(TBAF_FIELD_SEPERATOR))
				{
					errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[42] + "^^" + TBAF_FV_2025);
				}
				else
				{
					objReadFVAL2.statReportBuffer.append("-" + TBAF_FIELD_SEPERATOR);
				}
			}
			
			//End of Responsible Person STD Code Validation
			
			
			/**
			 *	Validation of Responsible Person Phone No.(Field No. 43 Of Batch Header Record)	
			 *	This field is a Mandatory Field.
			 */
			
			 if(objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_ORIG) || (objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_CORR) && objReadFVAL2.transType.equals(TBAF_TRANSACTION_TYPE_M)) )
				{
					if (! rPersonPhoneNo.equals(TBAF_FIELD_NULL) && ! rPersonPhoneNo.equals(TBAF_FIELD_SEPERATOR))
					{
						/*errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[43] + "^^" + TBAF_FV_2030);
					}
					else if (objRecVal.isFieldNull(rPersonPhoneNo))
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[43] + "^^" + TBAF_FV_2031);
					}*/
				        if (rPersonPhoneNo.length() > TBAF_RESPONSIBLE_PERSON_PHONE_LEN ||rPersonPhoneNo.trim().length() > TBAF_RESPONSIBLE_PERSON_PHONE_LEN)
					     {
						    errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[43] + "^^" + TBAF_FV_2024);
					     }
					   else if(rPersonPhoneNo.trim().length() != rPersonPhoneNo.length())
					     {
						    errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[43] + "^^" + TBAF_FV_2130); // new Error code added in jan16
					     }
					  else if (objRecVal.isInt(rPersonPhoneNo))
					     {
						    errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[43] + "^^" + TBAF_FV_2130);  // new Error code added in jan16
					     }
					  else if (Long.parseLong(rPersonPhoneNo.trim()) == 0)
					     {
						   errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[43] + "^^" + TBAF_FV_2034);
					     }
					  else
					     {
						    if (rPersonStdCode.equals(TBAF_FIELD_NULL) || rPersonStdCode.equals(TBAF_FIELD_SEPERATOR))
						     {
							   errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[43] + "^^" + TBAF_FV_2073);
						     }
						    else
						     {
						       objReadFVAL2.statReportBuffer.append(rPersonPhoneNo + TBAF_FIELD_SEPERATOR);
						     }
					      }
				      }
					else if(rPersonPhoneNo.equals(TBAF_FIELD_NULL) ||  rPersonPhoneNo.equals(TBAF_FIELD_SEPERATOR))
					{
						if(! rPersonStdCode.equals(TBAF_FIELD_NULL) && ! rPersonStdCode.equals(TBAF_FIELD_SEPERATOR))
						{
							errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[43] + "^^" + TBAF_FV_2076);
						}
						else
						{
							objReadFVAL2.statReportBuffer.append("-" + TBAF_FIELD_SEPERATOR);
						}
					}
					
					else
					{
						objReadFVAL2.statReportBuffer.append("-" + TBAF_FIELD_SEPERATOR);
					}
				}
				else if(objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_CORR) && (objReadFVAL2.transType.equals(TBAF_TRANSACTION_TYPE_X)))
				{
					if(!rPersonPhoneNo.equals(TBAF_FIELD_NULL) && !rPersonPhoneNo.equals(TBAF_FIELD_SEPERATOR))
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[43] + "^^" + TBAF_FV_2025);
					}
					else
					{
						objReadFVAL2.statReportBuffer.append("-" + TBAF_FIELD_SEPERATOR);
					}
				}
			
			//End of Responsible Person Phone No. Validation
			 
			 /**
				 *	Validation of Responsible Person Email ID(Field No. 44 Of Batch Header Record)	
				 *	This field is a Mandatory Field.
				 */	 
			 
			 
			 if (objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_ORIG) || (objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_CORR) && objReadFVAL2.transType.equals(TBAF_TRANSACTION_TYPE_M)) )
					{
						if (rPersonEmailID.equals(TBAF_FIELD_NULL) || rPersonEmailID.equals(TBAF_FIELD_SEPERATOR))
						{
							errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[44] + "^^" + TBAF_FV_2131);  // new Error code  added in jan16
						}
						else if (objRecVal.isFieldNull(rPersonEmailID))
						{
							errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[44] + "^^" + TBAF_FV_2132); // new Error code  added in jan16
						}
						else if (rPersonEmailID.length() > TBAF_RESPONSIBLE_PERSON_EMAIL_ID_LEN || rPersonEmailID.trim().length() > TBAF_RESPONSIBLE_PERSON_EMAIL_ID_LEN)
						{
							errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[44] + "^^" + TBAF_FV_2024);
						}
						else if(rPersonEmailID.trim().length() != rPersonEmailID.length())
						{
							errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[44] + "^^" + TBAF_FV_2132); //  new Error code  added in jan16
						} 
						else if (objRecVal.isValidEmail(rPersonEmailID))
						{
							errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[44] + "^^" + TBAF_FV_2132); // new Error code  added in jan16
						}
						else
						{
							objReadFVAL2.statReportBuffer.append(rPersonEmailID + TBAF_FIELD_SEPERATOR);
						}
						
					}
			 else if(objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_CORR) && (objReadFVAL2.transType.equals(TBAF_TRANSACTION_TYPE_X)))
				{
					if(!rPersonEmailID.equals(TBAF_FIELD_NULL) && !rPersonEmailID.equals(TBAF_FIELD_SEPERATOR))
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[44] + "^^" + TBAF_FV_2025);
					}
					else
					{
						objReadFVAL2.statReportBuffer.append("-" + TBAF_FIELD_SEPERATOR);
					}
				}
			 
			 
			//End of Responsible Person Email ID Validation
			 
			 
			 /**
				 *	Validation of Responsible Person Mobile No.(Field No. 45 Of Batch Header Record)	
				 *	This field is an Optional Field.
				 */	 	
			 
			 if (objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_ORIG) || (objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_CORR) && objReadFVAL2.transType.equals(TBAF_TRANSACTION_TYPE_M)) )
			 {
				 if (! rPersonMobileNo.equals(TBAF_FIELD_NULL) && ! rPersonMobileNo.equals(TBAF_FIELD_SEPERATOR))
				 {
					/* if(! (rPersonStdCode.equals("91")))
					 {
						 errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[45] + "^^" + TBAF_FV_2059);
					 }*/
					 if(rPersonMobileNo.trim().length() != rPersonMobileNo.length())
					 {
						 errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[45] + "^^" + TBAF_FV_2133); // New Error code added in jan16
					 }
					 else if(rPersonMobileNo.trim().length() != TBAF_RESPONSIBLE_PERSON_MOBILE_NO_LEN || rPersonMobileNo.length() != TBAF_RESPONSIBLE_PERSON_MOBILE_NO_LEN)
					 {
						 errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[45] + "^^" + TBAF_FV_2067);
					 }
					 else if(objRecVal.isInt(rPersonMobileNo))
					 {
						 errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[45] + "^^" + TBAF_FV_2133); // New Error code added in jan16
					 }
					 else if (Long.parseLong(rPersonMobileNo.trim()) == 0)
					 {
							errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[45] + "^^" + TBAF_FV_2034);
					 }
					 else if(Integer.parseInt(new Character (rPersonMobileNo.charAt(0)).toString()) == 0)
					 {
						 errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[45] + "^^" + TBAF_FV_2080);
					 }
					 else
					 {
						 objReadFVAL2.statReportBuffer.append(rPersonMobileNo + TBAF_FIELD_SEPERATOR);
					 }
				 }
				 else if(rPersonMobileNo.equals(TBAF_FIELD_NULL) || rPersonMobileNo.equals(TBAF_FIELD_SEPERATOR))
				 {
					 if(rPersonPhoneNo.equals(TBAF_FIELD_NULL) || rPersonPhoneNo.equals(TBAF_FIELD_SEPERATOR))
					 {
						 errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[45] + "^^" + TBAF_FV_2075);
					 }
					 else
					 {
						 objReadFVAL2.statReportBuffer.append("-" + TBAF_FIELD_SEPERATOR);
					 }
				 }
				 else
				 {
					 objReadFVAL2.statReportBuffer.append("-" + TBAF_FIELD_SEPERATOR);
				 }
			 }
			 else if(objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_CORR) && (objReadFVAL2.transType.equals(TBAF_TRANSACTION_TYPE_X)))
				{
					if(!rPersonMobileNo.equals(TBAF_FIELD_NULL) && !rPersonMobileNo.equals(TBAF_FIELD_SEPERATOR))
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[45] + "^^" + TBAF_FV_2025);
					}
					else
					{
						objReadFVAL2.statReportBuffer.append("-" + TBAF_FIELD_SEPERATOR);
					}
				}
			//End of Responsible Person Mobile No. Validation
		
			 /**
				 *	Validation of Whether the statement is filed earlier for the same month(Field No. 46 Of Batch Header Record)	
				 *	This field is an Optional Field.
				 */	 				 
			 
			 // Gauri added the validation for country code for CR 89435, FVU 1.9 :- START
			 
				if (Integer.parseInt(objReadFVAL2.finYear) >= 2026 && (objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_ORIG) || objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_CORR)))
				 {				
					 if(!countryCode.equals(TBAF_FIELD_NULL) && ! countryCode.equals(TBAF_FIELD_SEPERATOR))
					 {
						 	 if (objRecVal.isFieldNull(countryCode))
							{
						 		errStrBuff.append(TBAF_BHREC + lineNo + "^" + "Country(46)" + "^^" + TBAF_FV_5040);
							}
							else if (countryCode.length() > TBAF_COUNTRY_CODE_HIGH_RANGE || countryCode.trim().length() < TBAF_COUNTRY_CODE_LOW_RANGE)
							{
								errStrBuff.append(TBAF_BHREC + lineNo + "^" + "Country(46)" + "^^" + TBAF_FV_5040);
							}
							else if (objRecVal.isInt(countryCode))
							{
								errStrBuff.append(TBAF_BHREC + lineNo + "^" + "Country(46)" + "^^" + TBAF_FV_5040);
							}
							else if(countryCode.trim().length() != countryCode.length())
							{
								errStrBuff.append(TBAF_BHREC + lineNo + "^" + "Country(46)" + "^^" + TBAF_FV_5040);
							}
						  	
							else if( (Integer.parseInt(countryCode.trim()) > 286) 
									|| (Integer.parseInt(countryCode.trim()) < 01))								
							{
								errStrBuff.append(TBAF_BHREC + lineNo + "^" + "Country(46)" + "^^" + TBAF_FV_5040);
							}
							else
						 	{
								objReadFVAL2.statReportBuffer.append("-" + TBAF_FIELD_SEPERATOR);
							}
					 }				 
					 else{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + "Country(46)" + "^^" + TBAF_FV_5040);						 				
					 	}
				 	}
				
				//below code was already written for isStatementFiledEarlier
				else
					{
						if (!countryCode.equals(TBAF_FIELD_NULL) && !countryCode.equals(TBAF_FIELD_SEPERATOR))
						{
							errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[46] + "^^" + TBAF_FV_2049);
						}
					/*	else if(! (objRecVal.isFieldNull(isStatementFiledEarlier)))     
						{
							errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[46] + "^^" + TBAF_FV_2049);
						} */
						else
						{
							objReadFVAL2.statReportBuffer.append("-" + TBAF_FIELD_SEPERATOR);
						}
					}
				 
				//End of  Whether the statement is filed earlier for the same month Validation
			 
			 //Gauri added the validation for country code for CR 89435, FVU 1.9 :- END
			 
			 
			 /**
				 *	Validation of State name(Field No. 47 Of Batch Header Record)	
				 *	This field is an Optional Field for both Original and Correction.
				 */	  
			 if (objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_ORIG) || objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_CORR))
			 {
				 if(objReadFVAL2.deductCatgry.equals("S"))
				 {
					 if (stateName.equals(TBAF_FIELD_NULL) || stateName.equals(TBAF_FIELD_SEPERATOR))
						{
							errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[47] + "^^" + TBAF_FV_2134); // New Error code added in jan16
						}
						else if (objRecVal.isFieldNull(stateName))
						{
							errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[47] + "^^" + TBAF_FV_2031);
						}
						else if (stateName.length() > TBAF_STATE_LEN || stateName.trim().length() > TBAF_STATE_LEN)
						{
							errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[47] + "^^" + TBAF_FV_2057);
						}
						else if (objRecVal.isInt(stateName))
						{
							errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[47] + "^^" + TBAF_FV_2134); // New Error code added in jan16
						}
						else if(stateName.trim().length() != stateName.length())
						{
							errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[47] + "^^" + TBAF_FV_2031);
						}
						else if( (Integer.parseInt(stateName.trim()) > TBAF_RESPONSIBLE_PERSON_STATE_HIGH_RANGE) 
								|| (Integer.parseInt(stateName.trim()) < TBAF_RESPONSIBLE_PERSON_STATE_LOW_RANGE)
								|| Integer.parseInt(stateName.trim()) == 8)//24GFVU 1.7 Changes for state code 08
						{
							errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[47] + "^^" + TBAF_FV_2134); // New Error code added in jan16
						}
					 
						else
						{
							rawFileBean.setValue(stateName,RawFileBean.MIN_STATE_NAME_FLDNUM); //Added by Bharath for Raw File Generation
							objReadFVAL2.statReportBuffer.append(stateName + TBAF_FIELD_SEPERATOR);
						}
				 }
				 else
				 {
					 if (!stateName.equals(TBAF_FIELD_NULL) && !stateName.equals(TBAF_FIELD_SEPERATOR))
						{
							errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[47] + "^^" + TBAF_FV_2049);
						}
					/*	else if(! (objRecVal.isFieldNull(stateName)))     
						{
							errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[47] + "^^" + TBAF_FV_2049);
						} */
						else
						{
							
							objReadFVAL2.statReportBuffer.append("-" + TBAF_FIELD_SEPERATOR);
						}
				 }
			 }
			 
			 
			 
			 
			 
			//End of State name Validation
			 
			 
			 /**
				 *	Validation of Ministry/Department name(Field No. 48 Of Batch Header Record)	
				 *	This field is an Optional Field.
				 */	  
			         
			 if (objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_ORIG) || objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_CORR))
			 {
				if(objReadFVAL2.deductCatgry.equals("A"))
				 {
					if (ministryName.equals(TBAF_FIELD_NULL) || ministryName.equals(TBAF_FIELD_SEPERATOR))
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[48] + "^^" + TBAF_FV_2135);  // new Error code added in jan16
					}
					else if (objRecVal.isFieldNull(ministryName))
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[48] + "^^" + TBAF_FV_2135);  // new Error code added in jan16
					}
					else if (ministryName.length() > TBAF_MINISTRY_NAME_LEN || ministryName.trim().length() > TBAF_MINISTRY_NAME_LEN)
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[48] + "^^" + TBAF_FV_2005);
					}
					else if(objRecVal.isInt(ministryName))
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[48] + "^^" + TBAF_FV_2135);  // new Error code added in jan16
					}
					else if(ministryName.trim().length() != ministryName.length())
					{ 
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[48] + "^^" + TBAF_FV_2135); // new Error code added in jan16
					}
					else if(objRecVal.CheckZeros(ministryName))
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[48] + "^^" + TBAF_FV_2034);
					}
					else if((Integer.parseInt(ministryName.trim()) > TBAF_MINISTRY_NAME_HIGH_RANGE) || (Integer.parseInt(ministryName.trim()) < TBAF_MINISTRY_NAME_LOW_RANGE))
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[48] + "^^" + TBAF_FV_2060);						
					}
					else
					{
						rawFileBean.setValue(TBAF_MINISTRY_NAME[Integer.parseInt(ministryName)],RawFileBean.MIN_STATE_NAME_FLDNUM); //Added by Bharath for Raw File Generation
						objReadFVAL2.statReportBuffer.append(ministryName + TBAF_FIELD_SEPERATOR);
					}
									 
				 }
			   /* else
				{
					if(! (objRecVal.isFieldNull(ministryName)))
					{
					    if (ministryName.length() > TBAF_MINISTRY_NAME_LEN || ministryName.trim().length() > TBAF_MINISTRY_NAME_LEN)
						{
							errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[48] + "^^" + TBAF_FV_2005);
						}
						else if(objRecVal.isInt(ministryName))
						{
							errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[48] + "^^" + TBAF_FV_2031);
						}
						else if(ministryName.trim().length() != ministryName.length())
						{
							errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[48] + "^^" + TBAF_FV_2031);
						}
						else if((Integer.parseInt(ministryName.trim()) > TBAF_MINISTRY_NAME_HIGH_RANGE) || (Integer.parseInt(ministryName.trim()) < TBAF_MINISTRY_NAME_LOW_RANGE))
						{
							errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[48] + "^^" + TBAF_FV_2060);						
						}
						else
						{
							objReadFVAL2.statReportBuffer.append(ministryName + TBAF_FIELD_SEPERATOR);
						}
								
					}
				}*/
				else
				 {
					 if (! ministryName.equals(TBAF_FIELD_NULL) && ! ministryName.equals(TBAF_FIELD_SEPERATOR))
						{
							errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[48] + "^^" + TBAF_FV_2049);
						}
					/*	else if(! (objRecVal.isFieldNull(stateName)))     
						{
							errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[47] + "^^" + TBAF_FV_2049);
						} */
						else
						{
							
							objReadFVAL2.statReportBuffer.append("-" + TBAF_FIELD_SEPERATOR);
						}
				 }
				
			 }
			 
			//End of Ministry/Department Validation
			 
			 /**
				 *	Validation of Sub Ministry name(Field No. 49 Of Batch Header Record)	
				 *	This field is an Optional Field.
				 */	  
			 
			 if (objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_ORIG) || objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_CORR))
			 {
				 if(ministryName.trim().equals("01") || ministryName.trim().equals("1"))
				 {
					 if (subMinistryName.equals(TBAF_FIELD_NULL) || subMinistryName.equals(TBAF_FIELD_SEPERATOR))
						{
							errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[49] + "^^" + TBAF_FV_2136);  // New Error code added in jan16
						}
						else if (objRecVal.isFieldNull(subMinistryName))
						{
							errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[49] + "^^" + TBAF_FV_2137);  // New Error code added in jan16
						}
						else if (subMinistryName.length() > TBAF_SUB_MINISTRY_NAME_LEN || subMinistryName.trim().length() > TBAF_SUB_MINISTRY_NAME_LEN)
						{
							errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[49] + "^^" + TBAF_FV_2005);
						}
						
						else if(subMinistryName.trim().length() != subMinistryName.length())
						{
							errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[49] + "^^" + TBAF_FV_2137);  // New Error code added in jan16
						}
						else if(objRecVal.isInt(subMinistryName))
						{
							errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[49] + "^^" + TBAF_FV_2137);  // New Error code added in jan16
						}
						else if(((Integer.parseInt(subMinistryName.trim()) > TBAF_SUB_MINISTRY_NAME_HIGH_RANGE) || (Integer.parseInt(subMinistryName.trim()) < TBAF_SUB_MINISTRY_NAME_LOW_RANGE)) && (Integer.parseInt(subMinistryName.trim()) != TBAF_SUB_MINISTRY_OTHERS_CODE))
						{
							errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[49] + "^^" + TBAF_FV_2064);						
						}
						
						else
						{
							rawFileBean.setValue(subMinistryName,RawFileBean.SUBMIN_NAME_FLDNUM); //Added by Bharath for Raw File Generation
							objReadFVAL2.statReportBuffer.append(subMinistryName + TBAF_FIELD_SEPERATOR);
						}
										 
					 }
					else
					{
						if (! subMinistryName.equals(TBAF_FIELD_NULL) && ! subMinistryName.equals(TBAF_FIELD_SEPERATOR))
						{
							errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[49] + "^^" + TBAF_FV_2049);
						}
						/* else if(! (objRecVal.isFieldNull(subMinistryName)))     
						{
							errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[49] + "^^" + TBAF_FV_2049);
						} */
						else
						{
							rawFileBean.setValue(subMinistryName,RawFileBean.SUBMIN_NAME_FLDNUM); //Added by Bharath for Raw File Generation
							objReadFVAL2.statReportBuffer.append("-" + TBAF_FIELD_SEPERATOR);
						}
					}
				 }
			//End of Sub Ministry Validation
			 
			     /**
				 *	Validation of Sub Ministry name Others(Field No. 50 Of Batch Header Record)	
				 *	This field is an Optional Field.
				 */	  
			 
			            
			 if (objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_ORIG) || objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_CORR))
			 {
				 
				 if(subMinistryName.trim().equals("99"))
				 {
					 
					 if (subMinistryName_O.equals(TBAF_FIELD_NULL) || subMinistryName_O.equals(TBAF_FIELD_SEPERATOR))
						{
							errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[50] + "^^" + TBAF_FV_2138); // new Error added in jan16
						}
					 else if (objRecVal.isFieldNull(subMinistryName_O))
						{
							errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[50] + "^^" + TBAF_FV_2138); // new Error added in jan16
						}
					 else if (subMinistryName_O.length() > TBAF_SUB_MINISTRY_NAME_O_LEN || subMinistryName_O.trim().length() > TBAF_SUB_MINISTRY_NAME_O_LEN)
						{
							errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[50] + "^^" + TBAF_FV_2005);
						}
					 else if(subMinistryName_O.trim().length() == 0)
					 {
						 errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[50] + "^^" + TBAF_FV_2077);
					 }
					 else if(subMinistryName_O.trim().length() != subMinistryName_O.length())
						{
							errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[50] + "^^" + TBAF_FV_2138); // new Error added in jan16
						}
					 else if(! (objRecVal.checkStringWithCharForSubMinistry_O(subMinistryName_O)))
						{
							errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[50] + "^^" + TBAF_FV_2138); // new Error added in jan16
						}
					  else
						{
							objReadFVAL2.statReportBuffer.append(subMinistryName_O + TBAF_FIELD_SEPERATOR);
						}
					 
				  }
				 else
				 {
					 if (! (subMinistryName_O.equals(TBAF_FIELD_NULL)) && ! (subMinistryName_O.equals(TBAF_FIELD_SEPERATOR)))
						{
							errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[50] + "^^" + TBAF_FV_2049);
						}
						/*else if(! (objRecVal.isFieldNull(subMinistryName_O)))     
						{
							errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[50] + "^^" + TBAF_FV_2049);
						}*/
						else
						{
							objReadFVAL2.statReportBuffer.append("-" + TBAF_FIELD_SEPERATOR);
						}
				 }
			 }
			 
			 
			//End of Sub Ministry Others Validation
			 
			 /**
				 *	Validation of Count Of 24Q Transactions(Field No. 51 Of Batch Header Record)	
				 *	This field is a Mandatory Field.
				 *	Form Type changes for CR 89435
				 */	  
			 
			 if (Integer.parseInt(objReadFVAL2.finYear) < 2026 && objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_ORIG) || (objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_CORR) && objReadFVAL2.transType.equals(TBAF_TRANSACTION_TYPE_M) ) )
			 {
				 if (cBeanBH.getCountTD24Q().equals(TBAF_FIELD_NULL) || cBeanBH.getCountTD24Q().equals(TBAF_FIELD_SEPERATOR))
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[51] + "^^" + TBAF_FV_2140);  // new Error code added in jan16
					}
					else if (objRecVal.isFieldNull(cBeanBH.getCountTD24Q()))
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[51] + "^^" + TBAF_FV_2139); // new Error code added in jan16
					} 
					else if (cBeanBH.getCountTD24Q().length() > TBAF_TOTAL_COUNT_24Q_26Q_27Q_27EQ_LEN || cBeanBH.getCountTD24Q().trim().length() > TBAF_TOTAL_COUNT_24Q_26Q_27Q_27EQ_LEN)
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[51] + "^^" + TBAF_FV_2024);
					}
					else if(cBeanBH.getCountTD24Q().trim().length() != cBeanBH.getCountTD24Q().length())
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[51] + "^^" + TBAF_FV_2139); // new Error code added in jan16
					}
					else if(objRecVal.isInt(cBeanBH.getCountTD24Q()))
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[51] + "^^" + TBAF_FV_2139); // new Error code added in jan16
					}
					else
					{
						
						rawFileBean.setValue(cBeanBH.getCountTD24Q(),RawFileBean.COUNT_OF_DDO_24Q_FLDNUM); //Added by Bharath for Raw File Generation
						rawFileBean.setValues(cBeanBH.getCountTD24Q()+"raw",RawFileBean.NAT_OF_DED_24Q_FLDNUM,0); //Added by Bharath for Raw File Generation
						objReadFVAL2.statReportBuffer.append(cBeanBH.getCountTD24Q().trim() + TBAF_FIELD_SEPERATOR);
					}
			 }
			 
			//Gauri add one else on year condition change NAT_OF_DED_24Q_FLDNUM add new variable from newly created method::start
			 else if (Integer.parseInt(objReadFVAL2.finYear) >= 2026 && (objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_ORIG) || (objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_CORR) && objReadFVAL2.transType.equals(TBAF_TRANSACTION_TYPE_M))))
			 {
				 if (cBeanBH.getCountTD24Q().equals(TBAF_FIELD_NULL) || cBeanBH.getCountTD24Q().equals(TBAF_FIELD_SEPERATOR))
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[51] + "^^" + TBAF_FV_2140);  
					}
					else if (objRecVal.isFieldNull(cBeanBH.getCountTD24Q()))
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[51] + "^^" + TBAF_FV_5066); 
					} 
					else if (cBeanBH.getCountTD24Q().length() > TBAF_TOTAL_COUNT_24Q_26Q_27Q_27EQ_LEN || cBeanBH.getCountTD24Q().trim().length() > TBAF_TOTAL_COUNT_24Q_26Q_27Q_27EQ_LEN)
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[51] + "^^" + TBAF_FV_2024);
					}
					else if(cBeanBH.getCountTD24Q().trim().length() != cBeanBH.getCountTD24Q().length())
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[51] + "^^" + TBAF_FV_5066); 
					}
					else if(objRecVal.isInt(cBeanBH.getCountTD24Q()))
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[51] + "^^" + TBAF_FV_5066); // new Error code added in jan16
					}
					else
					{
						//Gauri add one else on year condition change NAT_OF_DED_24Q_FLDNUM add new variable from newly created method
						rawFileBean.setValue(cBeanBH.getCountTD24Q(),RawFileBean.COUNT_OF_DDO_24Q_FLDNUM);
						rawFileBean.setValues_2(cBeanBH.getCountTD24Q()+"raw",RawFileBean.NAT_OF_DED_24Q_FLDNUM,0); 
						objReadFVAL2.statReportBuffer.append(cBeanBH.getCountTD24Q().trim() + TBAF_FIELD_SEPERATOR);
					}
			 }
			 
			//Gauri add one else on year condition change NAT_OF_DED_24Q_FLDNUM add new variable from newly created method::end
			 
			 else if(objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_CORR) && objReadFVAL2.transType.equals(TBAF_TRANSACTION_TYPE_X))
			 {
				 if (cBeanBH.getCountTD24Q().equals(TBAF_FIELD_NULL) || cBeanBH.getCountTD24Q().equals(TBAF_FIELD_SEPERATOR))
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[51] + "^^" + TBAF_FV_2140); // new Error code added in jan16
					}
				 else if(! "0".equals(cBeanBH.getCountTD24Q()))
				 {
					 errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[51] + "^^" + TBAF_FV_2085);
				 }
				 else
				 {
					 objReadFVAL2.statReportBuffer.append("0" + TBAF_FIELD_SEPERATOR);
				 }
				 
			 }
			 
			//End of Count Of 24Q Transactions  Validation
			
			 
			 
			 /**
				 *	Validation of Control Total of Tax Deducted or Collected for 24Q(Field No. 52 Of Batch Header Record)	
				 *	This field is a Mandatory Field.
				 */	   
			 
			 if (objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_ORIG) || (objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_CORR) && objReadFVAL2.transType.equals(TBAF_TRANSACTION_TYPE_M) ))
			 {
				 
				 
				   if (cBeanBH.getTotalTax24Q().equals(TBAF_FIELD_NULL) || cBeanBH.getTotalTax24Q().equals(TBAF_FIELD_SEPERATOR))
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[52] + "^^" + TBAF_FV_2030);
					}
					else if (objRecVal.isFieldNull(cBeanBH.getTotalTax24Q()))
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[52] + "^^" + TBAF_FV_2031);
					}
					else if (cBeanBH.getTotalTax24Q().length() > TBAF_TOTAL_TAX_LEN || cBeanBH.getTotalTax24Q().trim().length() > TBAF_TOTAL_TAX_LEN)
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[52] + "^^" + TBAF_FV_2024);
					}
					else if(cBeanBH.getTotalTax24Q().trim().length() != cBeanBH.getTotalTax24Q().length())
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[52] + "^^" + TBAF_FV_2031);
					}
					else if(objRecVal.isDecimalNumber(cBeanBH.getTotalTax24Q()) || ! cBeanBH.getTotalTax24Q().endsWith("00"))
					{
					   if(objRecVal.isInt(cBeanBH.getTotalTax24Q()))
					   {
						   errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[52] + "^^" + TBAF_FV_2031);
					   }
					   else
					   {
						   errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[52] + "^^" + TBAF_FV_2046);
					   }
						
					}
					else if((objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_ORIG) && (Double.parseDouble(cBeanBH.getTotalTax24Q()) < 0.00)))  //Aditya
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[52] + "^^" + TBAF_FV_2065);
						
					}
					else 
					{
						objReadFVAL2.statReportBuffer.append(cBeanBH.getTotalTax24Q().trim() + TBAF_FIELD_SEPERATOR);
					}
			     }
			 
			 else if(objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_CORR) && objReadFVAL2.transType.equals(TBAF_TRANSACTION_TYPE_X))
			 {
				 if (cBeanBH.getTotalTax24Q().equals(TBAF_FIELD_NULL) || cBeanBH.getTotalTax24Q().equals(TBAF_FIELD_SEPERATOR))
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[52] + "^^" + TBAF_FV_2030);
					}
				 else if(! "0.00".equals(cBeanBH.getTotalTax24Q()))
				 {
					 errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[52] + "^^" + TBAF_FV_2086);
				 }
				 else
				 {
					 objReadFVAL2.statReportBuffer.append("0.00" + TBAF_FIELD_SEPERATOR);
				 }
				 
			 }
				 
			 //End of Control Total of Tax Deducted or Collected for 24Q validation
			 
			 
			 /**
				 *	Validation of Total TDS/TCS Remitted to govt. for 24Q(Field No. 53 Of Batch Header Record)	
				 *	This field is a Mandatory Field.
				 */	   
			  
			 
			 if (objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_ORIG) || (objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_CORR) && objReadFVAL2.transType.equals(TBAF_TRANSACTION_TYPE_M) ))
			 {
				   if (cBeanBH.getTotalRemittedAmt24Q().equals(TBAF_FIELD_NULL) || cBeanBH.getTotalRemittedAmt24Q().equals(TBAF_FIELD_SEPERATOR))
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[53] + "^^" + TBAF_FV_2030);
					}
					else if (objRecVal.isFieldNull(cBeanBH.getTotalRemittedAmt24Q()))
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[53] + "^^" + TBAF_FV_2031);
					}
					else if (cBeanBH.getTotalRemittedAmt24Q().length() > TBAF_TOTAL_REMITTANCE_LEN || cBeanBH.getTotalRemittedAmt24Q().trim().length() > TBAF_TOTAL_REMITTANCE_LEN)
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[53] + "^^" + TBAF_FV_2024);
					}
					else if(cBeanBH.getTotalRemittedAmt24Q().trim().length() != cBeanBH.getTotalRemittedAmt24Q().length())
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[53] + "^^" + TBAF_FV_2031);
					}
					else if((objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_ORIG)) && (Double.parseDouble(cBeanBH.getTotalRemittedAmt24Q()) < 0.00))
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[53] + "^^" + TBAF_FV_2065);
					}
					else if(objRecVal.isDecimalNumberForRemAmt(cBeanBH.getTotalRemittedAmt24Q()) || ! cBeanBH.getTotalRemittedAmt24Q().endsWith("00"))
					{
					   if(objRecVal.isIntForRemAmt(cBeanBH.getTotalRemittedAmt24Q()))
					   {
						   errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[53] + "^^" + TBAF_FV_2031);
					   }
					   else
					   {
						   errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[53] + "^^" + TBAF_FV_2046);
					   }
						
					}
					
					else
					{
						
						rawFileBean.setValue(cBeanBH.getTotalRemittedAmt24Q().trim(),RawFileBean.TOTAL_TDS_AMT_24Q_FLDNUM); //Added by Bharath for Raw File Generation
						objReadFVAL2.statReportBuffer.append(cBeanBH.getTotalRemittedAmt24Q().trim() + TBAF_FIELD_SEPERATOR);
					}
			     }
			 
			 else if(objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_CORR) && objReadFVAL2.transType.equals(TBAF_TRANSACTION_TYPE_X))
			 {
				 if (cBeanBH.getTotalRemittedAmt24Q().equals(TBAF_FIELD_NULL) || cBeanBH.getTotalRemittedAmt24Q().equals(TBAF_FIELD_SEPERATOR))
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[53] + "^^" + TBAF_FV_2030);
					}
				 else if(! "0.00".equals(cBeanBH.getTotalRemittedAmt24Q()))
				 {
					 errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[53] + "^^" + TBAF_FV_2086);
				 }
				 else
				 {
					 objReadFVAL2.statReportBuffer.append("0.00" + TBAF_FIELD_SEPERATOR);
				 }
				 
			 }
			 
			 //End of Total TDS/TCS Remitted to govt. for 24Q validation
	       
			
			 
			 /**
				 *	Validation of Count Of 26Q Transactions(Field No. 54 Of Batch Header Record)	
				 *	This field is a Mandatory Field.
				 */	  
			 
			 if (Integer.parseInt(objReadFVAL2.finYear) < 2026 && objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_ORIG) || (objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_CORR) && objReadFVAL2.transType.equals(TBAF_TRANSACTION_TYPE_M) ))
			 {
				 if (cBeanBH.getCountTD26Q().equals(TBAF_FIELD_NULL) || cBeanBH.getCountTD26Q().equals(TBAF_FIELD_SEPERATOR))
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[54] + "^^" + TBAF_FV_2030);
					}
					else if (objRecVal.isFieldNull(cBeanBH.getCountTD26Q()))
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[54] + "^^" + TBAF_FV_2031);
					}
					else if (cBeanBH.getCountTD26Q().length() > TBAF_TOTAL_COUNT_24Q_26Q_27Q_27EQ_LEN || cBeanBH.getCountTD26Q().trim().length() > TBAF_TOTAL_COUNT_24Q_26Q_27Q_27EQ_LEN)
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[54] + "^^" + TBAF_FV_2024);
					}
					else if(cBeanBH.getCountTD26Q().trim().length() != cBeanBH.getCountTD26Q().length())
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[54] + "^^" + TBAF_FV_2031);
					}
					else if(objRecVal.isInt(cBeanBH.getCountTD26Q()))
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[54] + "^^" + TBAF_FV_2031);
					}
					else
					{
						rawFileBean.setValue(cBeanBH.getCountTD26Q().trim(),RawFileBean.COUNT_OF_DDO_26Q_FLDNUM); //Added by Bharath for Raw File Generation
						rawFileBean.setValues(cBeanBH.getCountTD26Q()+"raw",RawFileBean.NAT_OF_DED_26Q_FLDNUM,1); //Added by Bharath for Raw File Generation
						objReadFVAL2.statReportBuffer.append(cBeanBH.getCountTD26Q().trim() + TBAF_FIELD_SEPERATOR);
					}
			 }
			 
			//Gauri changes form type for CR 89435, FVU 1.9::START
			 else if (Integer.parseInt(objReadFVAL2.finYear) >= 2026 && (objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_ORIG) || (objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_CORR) && objReadFVAL2.transType.equals(TBAF_TRANSACTION_TYPE_M))))
			 {
				 if (cBeanBH.getCountTD26Q().equals(TBAF_FIELD_NULL) || cBeanBH.getCountTD26Q().equals(TBAF_FIELD_SEPERATOR))
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[54] + "^^" + TBAF_FV_2030);
					}
					else if (objRecVal.isFieldNull(cBeanBH.getCountTD26Q()))
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[54] + "^^" + TBAF_FV_2031);
					}
					else if (cBeanBH.getCountTD26Q().length() > TBAF_TOTAL_COUNT_24Q_26Q_27Q_27EQ_LEN || cBeanBH.getCountTD26Q().trim().length() > TBAF_TOTAL_COUNT_24Q_26Q_27Q_27EQ_LEN)
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[54] + "^^" + TBAF_FV_2024);
					}
					else if(cBeanBH.getCountTD26Q().trim().length() != cBeanBH.getCountTD26Q().length())
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[54] + "^^" + TBAF_FV_2031);
					}
					else if(objRecVal.isInt(cBeanBH.getCountTD26Q()))
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[54] + "^^" + TBAF_FV_2031);
					}
					else
					{
						rawFileBean.setValue(cBeanBH.getCountTD26Q().trim(),RawFileBean.COUNT_OF_DDO_26Q_FLDNUM); 
						rawFileBean.setValues_2(cBeanBH.getCountTD26Q()+"raw",RawFileBean.NAT_OF_DED_26Q_FLDNUM,1); 
						objReadFVAL2.statReportBuffer.append(cBeanBH.getCountTD26Q().trim() + TBAF_FIELD_SEPERATOR);
					}
			 }
			 
			//Gauri changes form type for CR 89435, FVU 1.9::END
			 
			 else if(objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_CORR) && objReadFVAL2.transType.equals(TBAF_TRANSACTION_TYPE_X))
			 {
				 if (cBeanBH.getCountTD26Q().equals(TBAF_FIELD_NULL) || cBeanBH.getCountTD26Q().equals(TBAF_FIELD_SEPERATOR))
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[54] + "^^" + TBAF_FV_2030);
					}
				 else if(! "0".equals(cBeanBH.getCountTD26Q()))
				 {
					 errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[54] + "^^" + TBAF_FV_2085);
				 }
				 else
				 {
					 objReadFVAL2.statReportBuffer.append("0" + TBAF_FIELD_SEPERATOR);
				 }
				 
			 }
			 
			//End of Count Of 26Q Transactions  Validation
			
			 
			 
			 /**
				 *	Validation of Control Total of Tax Deducted or Collected for 26Q(Field No. 55 Of Batch Header Record)	
				 *	This field is a Mandatory Field.
				 */	   
			 if (objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_ORIG) || (objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_CORR) && objReadFVAL2.transType.equals(TBAF_TRANSACTION_TYPE_M) ))
			 {
				   if (cBeanBH.getTotalTax26Q().equals(TBAF_FIELD_NULL) || cBeanBH.getTotalTax26Q().equals(TBAF_FIELD_SEPERATOR))
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[55] + "^^" + TBAF_FV_2030);
					}
					else if (objRecVal.isFieldNull(cBeanBH.getTotalTax26Q()))
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[55] + "^^" + TBAF_FV_2031);
					}
					else if (cBeanBH.getTotalTax26Q().length() > TBAF_TOTAL_TAX_LEN || cBeanBH.getTotalTax26Q().trim().length() > TBAF_TOTAL_TAX_LEN)
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[55] + "^^" + TBAF_FV_2024);
					}
					else if(cBeanBH.getTotalTax26Q().trim().length() != cBeanBH.getTotalTax26Q().length())
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[55] + "^^" + TBAF_FV_2031);
					}
					else if(objRecVal.isDecimalNumber(cBeanBH.getTotalTax26Q()) || ! cBeanBH.getTotalTax26Q().endsWith("00"))
					{
					   if(objRecVal.isInt(cBeanBH.getTotalTax26Q()))
					   {
						   errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[55] + "^^" + TBAF_FV_2031);
					   }
					   else
					   {
						   errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[55] + "^^" + TBAF_FV_2046);
					   }
						
					}
					else if((objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_ORIG) && (Double.parseDouble(cBeanBH.getTotalTax26Q()) < 0.00)))
					{
						
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[55] + "^^" + TBAF_FV_2065);
						
					}
					else 
					{
						objReadFVAL2.statReportBuffer.append(cBeanBH.getTotalTax26Q().trim() + TBAF_FIELD_SEPERATOR);
					}
			     }
			 else if(objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_CORR) && objReadFVAL2.transType.equals(TBAF_TRANSACTION_TYPE_X))
			 {
				 if (cBeanBH.getTotalTax26Q().equals(TBAF_FIELD_NULL) || cBeanBH.getTotalTax26Q().equals(TBAF_FIELD_SEPERATOR))
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[55] + "^^" + TBAF_FV_2030);
					}
				 else if(! "0.00".equals(cBeanBH.getTotalTax26Q()))
				 {
					 errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[55] + "^^" + TBAF_FV_2086);
				 }
				 else
				 {
					 objReadFVAL2.statReportBuffer.append("0.00" + TBAF_FIELD_SEPERATOR);
				 }
				 
			 }
			 
			 //End of Control Total of Tax Deducted or Collected for 26Q validation
			 
			 
			 /**
				 *	Validation of Total TDS/TCS Remitted to govt. for 26Q(Field No. 56 Of Batch Header Record)	
				 *	This field is a Mandatory Field.
				 */	   
			  
			 
			 if (objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_ORIG) || (objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_CORR) && objReadFVAL2.transType.equals(TBAF_TRANSACTION_TYPE_M) ))
			 {
				   if (cBeanBH.getTotalRemittedAmt26Q().equals(TBAF_FIELD_NULL) || cBeanBH.getTotalRemittedAmt26Q().equals(TBAF_FIELD_SEPERATOR))
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[56] + "^^" + TBAF_FV_2030);
					}
					else if (objRecVal.isFieldNull(cBeanBH.getTotalRemittedAmt26Q()))
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[56] + "^^" + TBAF_FV_2031);
					}
					else if (cBeanBH.getTotalRemittedAmt26Q().length() > TBAF_TOTAL_REMITTANCE_LEN || cBeanBH.getTotalRemittedAmt26Q().trim().length() > TBAF_TOTAL_REMITTANCE_LEN)
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[56] + "^^" + TBAF_FV_2024);
					}
					else if(cBeanBH.getTotalRemittedAmt26Q().trim().length() != cBeanBH.getTotalRemittedAmt26Q().length())
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[56] + "^^" + TBAF_FV_2031);
					}
					else if((objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_ORIG)) && (Double.parseDouble(cBeanBH.getTotalRemittedAmt26Q()) < 0.00))
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[56] + "^^" + TBAF_FV_2065);
					}
					else if(objRecVal.isDecimalNumberForRemAmt(cBeanBH.getTotalRemittedAmt26Q()) || ! cBeanBH.getTotalRemittedAmt26Q().endsWith("00"))
					{
					   if(objRecVal.isIntForRemAmt(cBeanBH.getTotalRemittedAmt26Q()))
					   {
						   errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[56] + "^^" + TBAF_FV_2031);
					   }
					   else
					   {
						   errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[56] + "^^" + TBAF_FV_2046);
					   }
						
					}
					
					else
					{
						
						
						rawFileBean.setValue(cBeanBH.getTotalRemittedAmt26Q().trim(),RawFileBean.TOTAL_TDS_AMT_26Q_FLDNUM); //Added by Bharath for Raw File Generation
						objReadFVAL2.statReportBuffer.append(cBeanBH.getTotalRemittedAmt26Q().trim() + TBAF_FIELD_SEPERATOR);
					}
			     }
			 else if(objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_CORR) && objReadFVAL2.transType.equals(TBAF_TRANSACTION_TYPE_X))
			 {
				 if (cBeanBH.getTotalRemittedAmt26Q().equals(TBAF_FIELD_NULL) || cBeanBH.getTotalRemittedAmt26Q().equals(TBAF_FIELD_SEPERATOR))
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[56] + "^^" + TBAF_FV_2030);
					}
				 else if(! "0.00".equals(cBeanBH.getTotalRemittedAmt26Q()))
				 {
					 errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[56] + "^^" + TBAF_FV_2086);
				 }
				 else
				 {
					 objReadFVAL2.statReportBuffer.append("0.00" + TBAF_FIELD_SEPERATOR);
				 }
				 
			 }
			 //End of Total TDS/TCS Remitted to govt. for 26Q validation
	       
			 
			 
			 /**
				 *	Validation of Count Of 27Q Transactions(Field No. 57 Of Batch Header Record)	
				 *	This field is a Mandatory Field.
				 */	  
			 
			 if (Integer.parseInt(objReadFVAL2.finYear) < 2026 && objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_ORIG) || (objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_CORR) && objReadFVAL2.transType.equals(TBAF_TRANSACTION_TYPE_M) ))
			 {
				 if (cBeanBH.getCountTD27Q().equals(TBAF_FIELD_NULL) || cBeanBH.getCountTD27Q().equals(TBAF_FIELD_SEPERATOR))
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[57] + "^^" + TBAF_FV_2030);
					}
					else if (objRecVal.isFieldNull(cBeanBH.getCountTD27Q()))
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[57] + "^^" + TBAF_FV_2031);
					}
					else if (cBeanBH.getCountTD27Q().length() > TBAF_TOTAL_COUNT_24Q_26Q_27Q_27EQ_LEN || cBeanBH.getCountTD27Q().trim().length() > TBAF_TOTAL_COUNT_24Q_26Q_27Q_27EQ_LEN)
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[57] + "^^" + TBAF_FV_2024);
					}
					else if(cBeanBH.getCountTD27Q().trim().length() != cBeanBH.getCountTD27Q().length())
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[57] + "^^" + TBAF_FV_2031);
					}
					else if(objRecVal.isInt(cBeanBH.getCountTD27Q()))
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[57] + "^^" + TBAF_FV_2031);
					}
					else
					{
						rawFileBean.setValue(cBeanBH.getCountTD27Q().trim(),RawFileBean.COUNT_OF_DDO_27Q_FLDNUM); //Added by Bharath for Raw File Generation
						rawFileBean.setValues(cBeanBH.getCountTD27Q()+"raw",RawFileBean.NAT_OF_DED_27Q_FLDNUM,2); //Added by Bharath for Raw File Generation
						objReadFVAL2.statReportBuffer.append(cBeanBH.getCountTD27Q().trim() + TBAF_FIELD_SEPERATOR);
					}
			 }
			 
			 //Gauri changes form type for CR 89435, FVU 1.9::START
			 else  if (Integer.parseInt(objReadFVAL2.finYear) >= 2026 && (objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_ORIG) || (objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_CORR) && objReadFVAL2.transType.equals(TBAF_TRANSACTION_TYPE_M))))
			 {
				 if (cBeanBH.getCountTD27Q().equals(TBAF_FIELD_NULL) || cBeanBH.getCountTD27Q().equals(TBAF_FIELD_SEPERATOR))
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[57] + "^^" + TBAF_FV_2030);
					}
					else if (objRecVal.isFieldNull(cBeanBH.getCountTD27Q()))
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[57] + "^^" + TBAF_FV_2031);
					}
					else if (cBeanBH.getCountTD27Q().length() > TBAF_TOTAL_COUNT_24Q_26Q_27Q_27EQ_LEN || cBeanBH.getCountTD27Q().trim().length() > TBAF_TOTAL_COUNT_24Q_26Q_27Q_27EQ_LEN)
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[57] + "^^" + TBAF_FV_2024);
					}
					else if(cBeanBH.getCountTD27Q().trim().length() != cBeanBH.getCountTD27Q().length())
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[57] + "^^" + TBAF_FV_2031);
					}
					else if(objRecVal.isInt(cBeanBH.getCountTD27Q()))
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[57] + "^^" + TBAF_FV_2031);
					}
					else
					{
						rawFileBean.setValue(cBeanBH.getCountTD27Q().trim(),RawFileBean.COUNT_OF_DDO_27Q_FLDNUM); 
						rawFileBean.setValues_2(cBeanBH.getCountTD27Q()+"raw",RawFileBean.NAT_OF_DED_27Q_FLDNUM,2); 
						objReadFVAL2.statReportBuffer.append(cBeanBH.getCountTD27Q().trim() + TBAF_FIELD_SEPERATOR);
					}
			 }
			//Gauri changes form type for CR 89435, FVU 1.9::END
			 
			 else if(objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_CORR) && objReadFVAL2.transType.equals(TBAF_TRANSACTION_TYPE_X))
			 {
				 if (cBeanBH.getCountTD27Q().equals(TBAF_FIELD_NULL) || cBeanBH.getCountTD27Q().equals(TBAF_FIELD_SEPERATOR))
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[57] + "^^" + TBAF_FV_2030);
					}
				 else if(! "0".equals(cBeanBH.getCountTD27Q()))
				 {
					 errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[57] + "^^" + TBAF_FV_2085);
				 }
				 else
				 {
					 objReadFVAL2.statReportBuffer.append("0" + TBAF_FIELD_SEPERATOR);
				 }
				 
			 }
			 
			//End of Count Of 27Q Transactions  Validation
			
			 
			 
			 /**
				 *	Validation of Control Total of Tax Deducted or Collected for 27Q(Field No. 58 Of Batch Header Record)	
				 *	This field is a Mandatory Field.
				 */	   
			 if (objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_ORIG) || (objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_CORR) && objReadFVAL2.transType.equals(TBAF_TRANSACTION_TYPE_M) ))
			 {
				   if (cBeanBH.getTotalTax27Q().equals(TBAF_FIELD_NULL) || cBeanBH.getTotalTax27Q().equals(TBAF_FIELD_SEPERATOR))
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[58] + "^^" + TBAF_FV_2030);
					}
					else if (objRecVal.isFieldNull(cBeanBH.getTotalTax27Q()))
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[58] + "^^" + TBAF_FV_2031);
					}
					else if (cBeanBH.getTotalTax27Q().length() > TBAF_TOTAL_TAX_LEN || cBeanBH.getTotalTax27Q().trim().length() > TBAF_TOTAL_TAX_LEN)
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[58] + "^^" + TBAF_FV_2024);
					}
					else if(cBeanBH.getTotalTax27Q().trim().length() != cBeanBH.getTotalTax27Q().length())
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[58] + "^^" + TBAF_FV_2031);
					}
					else if(objRecVal.isDecimalNumber(cBeanBH.getTotalTax27Q()) || ! cBeanBH.getTotalTax27Q().endsWith("00"))
					{
					   if(objRecVal.isInt(cBeanBH.getTotalTax27Q()))
					   {
						   errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[58] + "^^" + TBAF_FV_2031);
					   }
					   else
					   {
						   errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[58] + "^^" + TBAF_FV_2046);
					   }
						
					}
					else if((objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_ORIG) && (Double.parseDouble(cBeanBH.getTotalTax27Q()) < 0.00)))
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[58] + "^^" + TBAF_FV_2065);
						
					}
					else
					{
						objReadFVAL2.statReportBuffer.append(cBeanBH.getTotalTax27Q().trim() + TBAF_FIELD_SEPERATOR);
					}
			     }
			 else if(objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_CORR) && objReadFVAL2.transType.equals(TBAF_TRANSACTION_TYPE_X))
			 {
				 if (cBeanBH.getTotalTax27Q().equals(TBAF_FIELD_NULL) || cBeanBH.getTotalTax27Q().equals(TBAF_FIELD_SEPERATOR))
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[58] + "^^" + TBAF_FV_2030);
					}
				 else if(! "0.00".equals(cBeanBH.getTotalTax27Q()))
				 {
					 errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[58] + "^^" + TBAF_FV_2086);
				 }
				 else
				 {
					 objReadFVAL2.statReportBuffer.append("0.00" + TBAF_FIELD_SEPERATOR);
				 }
				 
			 }
			 
			 //End of Control Total of Tax Deducted or Collected for 27Q validation
			 
			 
			 /**
				 *	Validation of Total TDS/TCS Remitted to govt. for 27Q(Field No. 59 Of Batch Header Record)	
				 *	This field is a Mandatory Field.
				 */	   
			  
			 
			 if (objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_ORIG) || (objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_CORR) && objReadFVAL2.transType.equals(TBAF_TRANSACTION_TYPE_M) ))
			 {
				   if (cBeanBH.getTotalRemittedAmt27Q().equals(TBAF_FIELD_NULL) || cBeanBH.getTotalRemittedAmt27Q().equals(TBAF_FIELD_SEPERATOR))
					{
					    objReadFVAL2.invalidTotalRemAmt = true;
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[59] + "^^" + TBAF_FV_2030);
					}
					else if (objRecVal.isFieldNull(cBeanBH.getTotalRemittedAmt27Q()))
					{
						objReadFVAL2.invalidTotalRemAmt = true;
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[59] + "^^" + TBAF_FV_2031);
					}
					else if (cBeanBH.getTotalRemittedAmt27Q().length() > TBAF_TOTAL_REMITTANCE_LEN || cBeanBH.getTotalRemittedAmt27Q().trim().length() > TBAF_TOTAL_REMITTANCE_LEN)
					{
						objReadFVAL2.invalidTotalRemAmt = true;
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[59] + "^^" + TBAF_FV_2024);
					}
					else if(cBeanBH.getTotalRemittedAmt27Q().trim().length() != cBeanBH.getTotalRemittedAmt27Q().length())
					{
						objReadFVAL2.invalidTotalRemAmt = true;
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[59] + "^^" + TBAF_FV_2031);
					}
					else if((objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_ORIG)) && (Double.parseDouble(cBeanBH.getTotalRemittedAmt27Q()) < 0.00))
					{
						objReadFVAL2.invalidTotalRemAmt = true;
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[59] + "^^" + TBAF_FV_2065);
					}
					else if(objRecVal.isDecimalNumberForRemAmt(cBeanBH.getTotalRemittedAmt27Q()) || ! cBeanBH.getTotalRemittedAmt27Q().endsWith("00"))
					{
					   if(objRecVal.isIntForRemAmt(cBeanBH.getTotalRemittedAmt27Q()))
					   {
						   objReadFVAL2.invalidTotalRemAmt = true;
						   errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[59] + "^^" + TBAF_FV_2031);
					   }
					   else
					   {
						   objReadFVAL2.invalidTotalRemAmt = true;
						   errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[59] + "^^" + TBAF_FV_2046);
					   }
						
					}
					
					else
					{
						rawFileBean.setValue(cBeanBH.getTotalRemittedAmt27Q().trim(),RawFileBean.TOTAL_TDS_AMT_27Q_FLDNUM); //Added by Bharath for Raw File Generation
						objReadFVAL2.statReportBuffer.append(cBeanBH.getTotalRemittedAmt27Q().trim() + TBAF_FIELD_SEPERATOR);
					}
			     }
			 else if(objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_CORR) && objReadFVAL2.transType.equals(TBAF_TRANSACTION_TYPE_X))
			 {
				 if (cBeanBH.getTotalRemittedAmt27Q().equals(TBAF_FIELD_NULL) || cBeanBH.getTotalRemittedAmt27Q().equals(TBAF_FIELD_SEPERATOR))
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[59] + "^^" + TBAF_FV_2030);
					}
				 else if(! "0.00".equals(cBeanBH.getTotalRemittedAmt27Q()))
				 {
					 errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[59] + "^^" + TBAF_FV_2086);
				 }
				 else
				 {
					 objReadFVAL2.statReportBuffer.append("0.00" + TBAF_FIELD_SEPERATOR);
				 }
				 
			 }
			 //End of Total TDS/TCS Remitted to govt. for 27Q validation
	       
			 
			 
			 /**
				 *	Validation of Count Of 27EQ Transactions(Field No. 60 Of Batch Header Record)	
				 *	This field is a Mandatory Field.
				 */	  
			 
			 if (Integer.parseInt(objReadFVAL2.finYear) < 2026 && objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_ORIG) || (objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_CORR) && objReadFVAL2.transType.equals(TBAF_TRANSACTION_TYPE_M) ))
			 {
				 if (cBeanBH.getCountTD27EQ().equals(TBAF_FIELD_NULL) || cBeanBH.getCountTD27EQ().equals(TBAF_FIELD_SEPERATOR))
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[60] + "^^" + TBAF_FV_2030);
					}
					else if (objRecVal.isFieldNull(cBeanBH.getCountTD27EQ()))
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[60] + "^^" + TBAF_FV_2031);
					}
					else if (cBeanBH.getCountTD27EQ().length() > TBAF_TOTAL_COUNT_24Q_26Q_27Q_27EQ_LEN || cBeanBH.getCountTD27EQ().trim().length() > TBAF_TOTAL_COUNT_24Q_26Q_27Q_27EQ_LEN)
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[60] + "^^" + TBAF_FV_2024);
					}
					else if(cBeanBH.getCountTD27EQ().trim().length() != cBeanBH.getCountTD27EQ().length())
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[60] + "^^" + TBAF_FV_2031);
					}
					else if(objRecVal.isInt(cBeanBH.getCountTD27EQ()))
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[60] + "^^" + TBAF_FV_2031);
					}
					else
					{
						rawFileBean.setValue(cBeanBH.getCountTD27EQ().trim(),RawFileBean.COUNT_OF_DDO_27EQ_FLDNUM); //Added by Bharath for Raw File Generation
						rawFileBean.setValues(cBeanBH.getCountTD27EQ()+"raw",RawFileBean.NAT_OF_DED_27EQ_FLDNUM,3); //Added by Bharath for Raw File Generation
						objReadFVAL2.statReportBuffer.append(cBeanBH.getCountTD27EQ().trim() + TBAF_FIELD_SEPERATOR);
					}
			 }
			 //Gauri changed form type for CR 89435, FVU 1.9::START
			 else  if (Integer.parseInt(objReadFVAL2.finYear) >= 2026 && (objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_ORIG) || (objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_CORR) && objReadFVAL2.transType.equals(TBAF_TRANSACTION_TYPE_M))))
			 {
				 if (cBeanBH.getCountTD27EQ().equals(TBAF_FIELD_NULL) || cBeanBH.getCountTD27EQ().equals(TBAF_FIELD_SEPERATOR))
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[60] + "^^" + TBAF_FV_2030);
					}
					else if (objRecVal.isFieldNull(cBeanBH.getCountTD27EQ()))
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[60] + "^^" + TBAF_FV_2031);
					}
					else if (cBeanBH.getCountTD27EQ().length() > TBAF_TOTAL_COUNT_24Q_26Q_27Q_27EQ_LEN || cBeanBH.getCountTD27EQ().trim().length() > TBAF_TOTAL_COUNT_24Q_26Q_27Q_27EQ_LEN)
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[60] + "^^" + TBAF_FV_2024);
					}
					else if(cBeanBH.getCountTD27EQ().trim().length() != cBeanBH.getCountTD27EQ().length())
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[60] + "^^" + TBAF_FV_2031);
					}
					else if(objRecVal.isInt(cBeanBH.getCountTD27EQ()))
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[60] + "^^" + TBAF_FV_2031);
					}
					else
					{
						rawFileBean.setValue(cBeanBH.getCountTD27EQ().trim(),RawFileBean.COUNT_OF_DDO_27EQ_FLDNUM); 
						rawFileBean.setValues_2(cBeanBH.getCountTD27EQ()+"raw",RawFileBean.NAT_OF_DED_27EQ_FLDNUM,3); 
						objReadFVAL2.statReportBuffer.append(cBeanBH.getCountTD27EQ().trim() + TBAF_FIELD_SEPERATOR);
					}
			 }
			 //Gauri changed form type for CR 89435, FVU 1.9::END
			 else if(objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_CORR) && objReadFVAL2.transType.equals(TBAF_TRANSACTION_TYPE_X))
			 {
				 if (cBeanBH.getCountTD27EQ().equals(TBAF_FIELD_NULL) || cBeanBH.getCountTD27EQ().equals(TBAF_FIELD_SEPERATOR))
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[60] + "^^" + TBAF_FV_2030);
					}
				 else if(! "0".equals(cBeanBH.getCountTD27EQ()))
				 {
					 errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[60] + "^^" + TBAF_FV_2085);
				 }
				 else
				 {
					 objReadFVAL2.statReportBuffer.append("0" + TBAF_FIELD_SEPERATOR);
				 }
				 
			 }
			 
			//End of Count Of 27EQ Transactions  Validation
			
			 
			 
			 /**
				 *	Validation of Control Total of Tax Deducted or Collected for 27EQ(Field No. 61 Of Batch Header Record)	
				 *	This field is a Mandatory Field.
				 */	   
			 if (objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_ORIG) || (objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_CORR) && objReadFVAL2.transType.equals(TBAF_TRANSACTION_TYPE_M) ))
			 {
				   if (cBeanBH.getTotalTax27EQ().equals(TBAF_FIELD_NULL) || cBeanBH.getTotalTax27EQ().equals(TBAF_FIELD_SEPERATOR))
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[61] + "^^" + TBAF_FV_2030);
					}
					else if (objRecVal.isFieldNull(cBeanBH.getTotalTax27EQ()))
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[61] + "^^" + TBAF_FV_2031);
					}
					else if (cBeanBH.getTotalTax27EQ().length() > TBAF_TOTAL_TAX_LEN || cBeanBH.getTotalTax27EQ().trim().length() > TBAF_TOTAL_TAX_LEN)
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[61] + "^^" + TBAF_FV_2024);
					}
					else if(cBeanBH.getTotalTax27EQ().trim().length() != cBeanBH.getTotalTax27EQ().length())
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[61] + "^^" + TBAF_FV_2031);
					}
					else if(objRecVal.isDecimalNumber(cBeanBH.getTotalTax27EQ()) || ! cBeanBH.getTotalTax27EQ().endsWith("00"))
					{
					   if(objRecVal.isInt(cBeanBH.getTotalTax27EQ()))
					   {
						   errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[61] + "^^" + TBAF_FV_2031);
					   }
					   else
					   {
						   errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[61] + "^^" + TBAF_FV_2046);
					   }
						
					}
					else if((objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_ORIG) && (Double.parseDouble(cBeanBH.getTotalTax27EQ()) < 0.00)))
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[61] + "^^" + TBAF_FV_2065);
					
					}
					else 
					{
						objReadFVAL2.statReportBuffer.append(cBeanBH.getTotalTax27EQ().trim() + TBAF_FIELD_SEPERATOR);
					}
			     }
			 else if(objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_CORR) && objReadFVAL2.transType.equals(TBAF_TRANSACTION_TYPE_X))
			 {
				 if (cBeanBH.getTotalTax27EQ().equals(TBAF_FIELD_NULL) || cBeanBH.getTotalTax27EQ().equals(TBAF_FIELD_SEPERATOR))
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[61] + "^^" + TBAF_FV_2030);
					}
				 else if(! "0.00".equals(cBeanBH.getTotalTax27EQ()))
				 {
					 errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[61] + "^^" + TBAF_FV_2086);
				 }
				 else
				 {
					 objReadFVAL2.statReportBuffer.append("0.00" + TBAF_FIELD_SEPERATOR);
				 }
				 
			 }
			 
			 //End of Control Total of Tax Deducted or Collected for 27EQ validation
			 
			 
			 /**
				 *	Validation of Total TDS/TCS Remitted to govt. for 27EQ(Field No. 62 Of Batch Header Record)	
				 *	This field is a Mandatory Field.
				 */	   
			  
			 
			 if (objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_ORIG) || (objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_CORR) && objReadFVAL2.transType.equals(TBAF_TRANSACTION_TYPE_M) ))
			 {
				   if (cBeanBH.getTotalRemittedAmt27EQ().equals(TBAF_FIELD_NULL) || cBeanBH.getTotalRemittedAmt27EQ().equals(TBAF_FIELD_SEPERATOR))
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[62] + "^^" + TBAF_FV_2030);
					}
					else if (objRecVal.isFieldNull(cBeanBH.getTotalRemittedAmt27EQ()))
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[62] + "^^" + TBAF_FV_2031);
					}
					else if (cBeanBH.getTotalRemittedAmt27EQ().length() > TBAF_TOTAL_REMITTANCE_LEN || cBeanBH.getTotalRemittedAmt27EQ().trim().length() > TBAF_TOTAL_REMITTANCE_LEN)
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[62] + "^^" + TBAF_FV_2024);
					}
					else if(cBeanBH.getTotalRemittedAmt27EQ().trim().length() != cBeanBH.getTotalRemittedAmt27EQ().length())
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[62] + "^^" + TBAF_FV_2031);
					}
					else if((objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_ORIG)) && (Double.parseDouble(cBeanBH.getTotalRemittedAmt27EQ()) < 0.00))
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[62] + "^^" + TBAF_FV_2065);
					}
					else if(objRecVal.isDecimalNumberForRemAmt(cBeanBH.getTotalRemittedAmt27EQ()) || ! cBeanBH.getTotalRemittedAmt27EQ().endsWith("00"))
					{
					   if(objRecVal.isIntForRemAmt(cBeanBH.getTotalRemittedAmt27EQ()))
					   {
						   errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[62] + "^^" + TBAF_FV_2031);
					   }
					   else
					   {
						   errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[62] + "^^" + TBAF_FV_2046);
					   }
						
					}
					
					else
					{
						rawFileBean.setValue(cBeanBH.getTotalRemittedAmt27EQ().trim(),RawFileBean.TOTAL_TDS_AMT_27EQ_FLDNUM); //Added by Bharath for Raw File Generation
						objReadFVAL2.statReportBuffer.append(cBeanBH.getTotalRemittedAmt27EQ().trim() + TBAF_FIELD_SEPERATOR);
					}
			     }
			 else if(objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_CORR) && objReadFVAL2.transType.equals(TBAF_TRANSACTION_TYPE_X))
			 {
				 if (cBeanBH.getTotalRemittedAmt27EQ().equals(TBAF_FIELD_NULL) || cBeanBH.getTotalRemittedAmt27EQ().equals(TBAF_FIELD_SEPERATOR))
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[62] + "^^" + TBAF_FV_2030);
					}
				 else if(! "0.00".equals(cBeanBH.getTotalRemittedAmt27EQ()))
				 {
					 errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[62] + "^^" + TBAF_FV_2086);
				 }
				 else
				 {
					 objReadFVAL2.statReportBuffer.append("0.00" + TBAF_FIELD_SEPERATOR);
				 }
				 
			 }
			 //End of Total TDS/TCS Remitted to govt. for 27EQ validation
	       
			 
			 
			 
			 
			 /**
				 *	Validation of PAO/DTO/CDDO registration no.(Field No. 63 Of Batch Header Record)	
				 *	This field is a Optional Field for both Original and Correction Statement type.
				 *  From FY 2026-27 this field will be frezzed
				 */	 
			
			 //Gauri added the else-if  block to add the validation of FY for CR 89435, FVU 1.9
			 
			 if (Integer.parseInt(objReadFVAL2.finYear) < 2026 && (objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_ORIG) || objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_CORR)))
			 {
				 if (! paoRegistrationNo.equals(TBAF_FIELD_NULL) && ! paoRegistrationNo.equals(TBAF_FIELD_SEPERATOR))
				 {
				     if(paoRegistrationNo.trim().length() == 0)
				     {
				    	 errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[63] + "^^" + TBAF_FV_2002);
				     }
					 else if(paoRegistrationNo.trim().length() != TBAF_PAO_DTO_REG_NO_LEN || paoRegistrationNo.length() != TBAF_PAO_DTO_REG_NO_LEN)
					 {
						 errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[63] + "^^" + TBAF_FV_2068);
					 }
					  else if(paoRegistrationNo.trim().length() != paoRegistrationNo.length())
					 {
						 errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[63] + "^^" + TBAF_FV_2068);
					 }
					 else if(objRecVal.isInt(paoRegistrationNo))
					 {
						 errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[63] + "^^" + TBAF_FV_2068);
					 }
					 else if (objRecVal.isValidRrrNumber(paoRegistrationNo))
					 {
							errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[63] + "^^" + TBAF_FV_2068);
					 }
					 else
					 {
						 objReadFVAL2.statReportBuffer.append(paoRegistrationNo + TBAF_FIELD_SEPERATOR);
					 }
				 }
				 
				 else
				 {
					 objReadFVAL2.statReportBuffer.append("-" + TBAF_FIELD_SEPERATOR);
				 }
			 }
			 
			 else if(Integer.parseInt(objReadFVAL2.finYear) >= 2026)
			 {
				 if (!paoRegistrationNo.equals(TBAF_FIELD_NULL) && ! paoRegistrationNo.equals(TBAF_FIELD_SEPERATOR)) {
					 errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[63] + "^^" + TBAF_FV_5042);
				 }
				 else //gauri added this
				 {
					 objReadFVAL2.statReportBuffer.append("-" + TBAF_FIELD_SEPERATOR);
				 }
			 }
			 	 
			//End PAO/DTO/CDDO registration no. validation
			 
			 
			 /**
				 *	Validation of Count of Distinct DDO(Field No. 64 Of Batch Header Record)	
				 *	This field is a Mandatory Field.
				 */	  
			 
			 if (objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_ORIG) || (objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_CORR) && objReadFVAL2.transType.equals(TBAF_TRANSACTION_TYPE_M)) )
				{
					if (cBeanBH.getDistinctDDOCount().equals(TBAF_FIELD_NULL) || cBeanBH.getDistinctDDOCount().equals(TBAF_FIELD_SEPERATOR))
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[64] + "^^" + TBAF_FV_2022);
					}
					else if (objRecVal.isFieldNull(cBeanBH.getDistinctDDOCount()))
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[64] + "^^" + TBAF_FV_2023);
					}
					else if(cBeanBH.getDistinctDDOCount().length() > TBAF_DISTINCT_DDO_COUNT_LEN || cBeanBH.getDistinctDDOCount().trim().length() > TBAF_DISTINCT_DDO_COUNT_LEN)
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[64] + "^^" + TBAF_FV_2005);
					}
					else if(cBeanBH.getDistinctDDOCount().length() != cBeanBH.getDistinctDDOCount().trim().length())
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[64] + "^^" + TBAF_FV_2023);
					}
					else if(objRecVal.isInt(cBeanBH.getDistinctDDOCount()))
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[64] + "^^" + TBAF_FV_2023);
					}
					else if(Integer.parseInt(cBeanBH.getDistinctDDOCount()) < 0)
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[64] + "^^" + TBAF_FV_2069);
					}
					else
					{
						rawFileBean.setValue(cBeanBH.getDistinctDDOCount(),RawFileBean.TOTAL_DISTINCT_DDO_FLDNUM); //Added by Bharath for Raw File Generation
						objReadFVAL2.statReportBuffer.append(cBeanBH.getDistinctDDOCount().trim() + TBAF_FIELD_SEPERATOR);
					}
				}
			 
			 else if(objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_CORR) && objReadFVAL2.transType.equals(TBAF_TRANSACTION_TYPE_X))
			 {
				 if (cBeanBH.getDistinctDDOCount().equals(TBAF_FIELD_NULL) || cBeanBH.getDistinctDDOCount().equals(TBAF_FIELD_SEPERATOR))
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[64] + "^^" + TBAF_FV_2030);
					}
				 else if(! "0".equals(cBeanBH.getDistinctDDOCount()))
				 {
					 errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[64] + "^^" + TBAF_FV_2085);
				 }
				 else
				 {
					 objReadFVAL2.statReportBuffer.append("0" + TBAF_FIELD_SEPERATOR);
				 }
				 
			 }
			//End Count of Distinct DDO validation 
			
			 
			 /**
				 *	Validation of Total TDS/TCS remitted to Government account (AG/Pr CCA)(Field No. 65 Of Batch Header Record)	
				 *	This field is a Mandatory Field.
				 */	 
			
			 
			 
			 if (objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_ORIG) || (objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_CORR) && objReadFVAL2.transType.equals(TBAF_TRANSACTION_TYPE_M)) )
			 {
				   if (cBeanBH.getTotalRemittedAmt().equals(TBAF_FIELD_NULL) || cBeanBH.getTotalRemittedAmt().equals(TBAF_FIELD_SEPERATOR))
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[65] + "^^" + TBAF_FV_2030);
					}
					else if (objRecVal.isFieldNull(cBeanBH.getTotalRemittedAmt()))
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[65] + "^^" + TBAF_FV_2031);
					}
					else if (cBeanBH.getTotalRemittedAmt().length() > TBAF_TOTAL_REMITTANCE_LEN || cBeanBH.getTotalRemittedAmt().trim().length() > TBAF_TOTAL_REMITTANCE_LEN)
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[65] + "^^" + TBAF_FV_2024);
					}
					else if(cBeanBH.getTotalRemittedAmt().trim().length() != cBeanBH.getTotalRemittedAmt().length())
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[65] + "^^" + TBAF_FV_2031);
					}
					else if( (objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_ORIG)) && (Double.parseDouble(cBeanBH.getTotalRemittedAmt()) < 0.00) )
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[65] + "^^" + TBAF_FV_2065);
					}
				   
					else if(objRecVal.isDecimalNumberForRemAmt(cBeanBH.getTotalRemittedAmt()) || ! cBeanBH.getTotalRemittedAmt().endsWith("00"))
					{
					   if(objRecVal.isIntForRemAmt(cBeanBH.getTotalRemittedAmt()))
					   {
						   errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[65] + "^^" + TBAF_FV_2031);
					   }
					   else
					   {
						   errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[65] + "^^" + TBAF_FV_2046);
					   }
						
					}
					
					else
					{
						rawFileBean.setValue(cBeanBH.getTotalRemittedAmt().trim(),RawFileBean.TOTAL_AMOUNT_OF_TAX_REMITTED_TO_GOVT);
						objReadFVAL2.statReportBuffer.append(cBeanBH.getTotalRemittedAmt().trim() + TBAF_FIELD_SEPERATOR);
					}
			     }
			 
			 else if(objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_CORR) && objReadFVAL2.transType.equals(TBAF_TRANSACTION_TYPE_X))
			 {
				 if (cBeanBH.getTotalRemittedAmt().equals(TBAF_FIELD_NULL) || cBeanBH.getTotalRemittedAmt().equals(TBAF_FIELD_SEPERATOR))
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[65] + "^^" + TBAF_FV_2030);
					}
				 else if(! "0.00".equals(cBeanBH.getTotalRemittedAmt()))
				 {
					 errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[65] + "^^" + TBAF_FV_2086);
				 }
				 else
				 {
					 rawFileBean.setValue(cBeanBH.getTotalRemittedAmt().trim(),RawFileBean.TOTAL_AMOUNT_OF_TAX_REMITTED_TO_GOVT);
					 objReadFVAL2.statReportBuffer.append("0.00" + TBAF_FIELD_SEPERATOR);
				 }
				 
			 }
			 
			 
			 
			 //End of Total TDS/TCS remitted to Government account (AG/Pr CCA) Validation
			
			 
			 
			 
			 /**
				 *	Validation of Count of DDO added(Field No. 66 Of Batch Header Record)	
				 *	This field is a Mandatory Field.
				 */	 
			 if (objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_ORIG) || (objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_CORR) && objReadFVAL2.transType.equals(TBAF_TRANSACTION_TYPE_M)) )
				{
					if (cBeanBH.getCountDDOAdd().equals(TBAF_FIELD_NULL) || cBeanBH.getCountDDOAdd().equals(TBAF_FIELD_SEPERATOR))
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[66] + "^^" + TBAF_FV_2022);
					}
					else if (objRecVal.isFieldNull(cBeanBH.getCountDDOAdd()))
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[66] + "^^" + TBAF_FV_2023);
					}
					else if(cBeanBH.getCountDDOAdd().length() > TBAF_COUNT_DDO_ADD_UPDT_DEL_LEN || cBeanBH.getCountDDOAdd().trim().length() > TBAF_COUNT_DDO_ADD_UPDT_DEL_LEN)
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[66] + "^^" + TBAF_FV_2005);
					}
					else if(cBeanBH.getCountDDOAdd().length() != cBeanBH.getCountDDOAdd().trim().length())
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[66] + "^^" + TBAF_FV_2023);
					}
					else if(objRecVal.isInt(cBeanBH.getCountDDOAdd()))
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[66] + "^^" + TBAF_FV_2023);
					}
					else if(Integer.parseInt(cBeanBH.getCountDDOAdd().trim()) < 0)
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[66] + "^^" + TBAF_FV_2070);
					}
					else
					{
						rawFileBean.setValue(cBeanBH.getCountDDOAdd(),RawFileBean.COUNT_OF_DDO_ADDED_FLDNUM); //Added by Bharath for Raw File Generation
						objReadFVAL2.statReportBuffer.append(cBeanBH.getCountDDOAdd().trim() + TBAF_FIELD_SEPERATOR);
					}
				}
			 else if(objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_CORR) && objReadFVAL2.transType.equals(TBAF_TRANSACTION_TYPE_X))
			 {
				 if (cBeanBH.getCountDDOAdd().equals(TBAF_FIELD_NULL) || cBeanBH.getCountDDOAdd().equals(TBAF_FIELD_SEPERATOR))
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[66] + "^^" + TBAF_FV_2030);
					}
				 else if(! "0".equals(cBeanBH.getCountDDOAdd()))
				 {
					 errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[66] + "^^" + TBAF_FV_2085);
				 }
				 else
				 {
					 objReadFVAL2.statReportBuffer.append("0" + TBAF_FIELD_SEPERATOR);
				 }
				 
			 }
			 //End of Count of DDO added validation
			 
			 /**
				 *	Validation of Count of DDO updated(Field No. 67 Of Batch Header Record)	
				 *	This field is a Mandatory Field.
				 */	 
			 if (objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_ORIG) || (objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_CORR) && objReadFVAL2.transType.equals(TBAF_TRANSACTION_TYPE_M)) )
				{
					if (cBeanBH.getCountDDOUpdated().equals(TBAF_FIELD_NULL) || cBeanBH.getCountDDOUpdated().equals(TBAF_FIELD_SEPERATOR))
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[67] + "^^" + TBAF_FV_2022);
					}
					else if (objRecVal.isFieldNull(cBeanBH.getCountDDOUpdated()))
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[67] + "^^" + TBAF_FV_2023);
					}
					else if(cBeanBH.getCountDDOUpdated().length() > TBAF_COUNT_DDO_ADD_UPDT_DEL_LEN || cBeanBH.getCountDDOUpdated().trim().length() > TBAF_COUNT_DDO_ADD_UPDT_DEL_LEN)
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[67] + "^^" + TBAF_FV_2005);
					}
					else if(cBeanBH.getCountDDOUpdated().length() != cBeanBH.getCountDDOUpdated().trim().length())
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[67] + "^^" + TBAF_FV_2023);
					}
					else if(objRecVal.isInt(cBeanBH.getCountDDOUpdated()))
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[67] + "^^" + TBAF_FV_2023);
					}
					else if(Integer.parseInt(cBeanBH.getCountDDOUpdated().trim()) < 0)
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[67] + "^^" + TBAF_FV_2070);
					}
					else
					{
						rawFileBean.setValue(cBeanBH.getCountDDOUpdated(),RawFileBean.COUNT_OF_DDO_UPDATED_FLDNUM); //Added by Bharath for Raw File Generation
						objReadFVAL2.statReportBuffer.append(cBeanBH.getCountDDOUpdated().trim() + TBAF_FIELD_SEPERATOR);
					}
				}
			 
			 else if(objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_CORR) && objReadFVAL2.transType.equals(TBAF_TRANSACTION_TYPE_X))
			 {
				 if (cBeanBH.getCountDDOUpdated().equals(TBAF_FIELD_NULL) || cBeanBH.getCountDDOUpdated().equals(TBAF_FIELD_SEPERATOR))
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[67] + "^^" + TBAF_FV_2030);
					}
				 else if(! "0".equals(cBeanBH.getCountDDOUpdated()))
				 {
					 errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[67] + "^^" + TBAF_FV_2085);
				 }
				 else
				 {
					 objReadFVAL2.statReportBuffer.append("0" + TBAF_FIELD_SEPERATOR);
				 }
				 
			 }
			 //End of Count of DDO updated validation
			 
			 /**
				 *	Validation of Count of DDO deleted(Field No. 68 Of Batch Header Record)	
				 *	This field is a Mandatory Field.
				 */	  if (objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_ORIG) || (objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_CORR) && objReadFVAL2.transType.equals(TBAF_TRANSACTION_TYPE_M)) )
					{
						if (cBeanBH.getCountDDODeleted().equals(TBAF_FIELD_NULL) || cBeanBH.getCountDDODeleted().equals(TBAF_FIELD_SEPERATOR))
						{
							errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[68] + "^^" + TBAF_FV_2022);
						}
						else if (objRecVal.isFieldNull(cBeanBH.getCountDDODeleted()))
						{
							errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[68] + "^^" + TBAF_FV_2023);
						}
						else if(cBeanBH.getCountDDODeleted().length() > TBAF_COUNT_DDO_ADD_UPDT_DEL_LEN || cBeanBH.getCountDDODeleted().trim().length() > TBAF_COUNT_DDO_ADD_UPDT_DEL_LEN)
						{
							errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[68] + "^^" + TBAF_FV_2005);
						}
						else if(cBeanBH.getCountDDODeleted().length() != cBeanBH.getCountDDODeleted().trim().length())
						{
							errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[68] + "^^" + TBAF_FV_2023);
						}
						else if(objRecVal.isInt(cBeanBH.getCountDDODeleted()))
						{
							errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[68] + "^^" + TBAF_FV_2023);
						}
						else if(Integer.parseInt(cBeanBH.getCountDDODeleted().trim()) < 0)
						{
							errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[68] + "^^" + TBAF_FV_2070);
						}
						else
						{
							rawFileBean.setValue(cBeanBH.getCountDDODeleted(),RawFileBean.COUNT_OF_DDO_DELETED_FLDNUM); //Added by Bharath for Raw File Generation
							objReadFVAL2.statReportBuffer.append(cBeanBH.getCountDDODeleted().trim() + TBAF_FIELD_SEPERATOR);
						}
					}
			 
				 else if(objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_CORR) && objReadFVAL2.transType.equals(TBAF_TRANSACTION_TYPE_X))
				 {
					 if (cBeanBH.getCountDDODeleted().equals(TBAF_FIELD_NULL) || cBeanBH.getCountDDODeleted().equals(TBAF_FIELD_SEPERATOR))
						{
							errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[68] + "^^" + TBAF_FV_2030);
						}
					 else if(! "0".equals(cBeanBH.getCountDDODeleted()))
					 {
						 errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[68] + "^^" + TBAF_FV_2085);
					 }
					 else
					 {
						 objReadFVAL2.statReportBuffer.append("0" + TBAF_FIELD_SEPERATOR);
					 }
					 
				 }
			 
			 
			 //End of Count of DDO deleted validation
			 
			 
			 /**
				 *	Validation of Receipt Number(Field No. 69 Of Batch Header Record)	
				 *	No value should be specified
				 */	   
			 
			 if (objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_ORIG) || objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_CORR))
				{
					if (!receiptNo.equals(TBAF_FIELD_NULL) && !receiptNo.equals(TBAF_FIELD_SEPERATOR))
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[69] + "^^" + TBAF_FV_2049);
					}
					/*  else if(! (objRecVal.isFieldNull(receiptNo)))     
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[69] + "^^" + TBAF_FV_2049);
					}*/
					else
					{
						objReadFVAL2.statReportBuffer.append("-" + TBAF_FIELD_SEPERATOR);
					}
				}
			 
			//End of Receipt Number validation
			
			 /**
				 *	Validation of Filler 5(Field No. 70 Of Batch Header Record)	
				 *	
				 */	
			 
			 
			//Gauri added Filler 5 validation::START
				
				if (objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_ORIG) || objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_CORR))
				{
					if (!bhFiller_5.equals(TBAF_FIELD_NULL) && !bhFiller_5.equals(TBAF_FIELD_SEPERATOR))
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[70] + "^^" + TBAF_FV_2049);
					}
					/* else if(! (objRecVal.isFieldNull(bhFiller_5)))     
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[26] + "^^" + TBAF_FV_2049);
					} */
					else
					{
						objReadFVAL2.statReportBuffer.append("-" + TBAF_FIELD_SEPERATOR);
					}
				}		
				
				//Gauri added Filler 5 validation::END
			 
			 			 
				/*//Gauri added AO Title validation for CR 89435, FVU 1.9:: START
				
				if(Integer.parseInt(objReadFVAL2.finYear) >= 2025 && (objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_ORIG) || objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_CORR)))
				{				
//					if (aoTitle.equals(TBAF_FIELD_NULL) || aoTitle.equals(TBAF_FIELD_SEPERATOR))
//					{
//						errStrBuff.append(TBAF_BHREC + lineNo + "^" + "AO Title(24)" + "^^" + TBAF_FV_5046);  
//					}
//					else if (!(aoTitle.equals("Mr.") || aoTitle.equals("Mrs.") || aoTitle.equals("Ms.") || aoTitle.equals("Non-Individual"))) 
//					{
//						errStrBuff.append(TBAF_BHREC + lineNo + "^" + "AO Title(24)" + "^^" + TBAF_FV_5046);
//					}
					
					//Code 01 to 04 added for aoTitle
					
					if (!aoTitle.equals(TBAF_FIELD_NULL) && ! aoTitle.equals(TBAF_FIELD_SEPERATOR)) {
						if (objRecVal.isFieldNull(aoTitle))
						{
					 		errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[70] + "^^" + TBAF_FV_5046);
						}
						else if (aoTitle.length() > 2 || aoTitle.trim().length() < 2)
						{
							errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[70] + "^^" + TBAF_FV_5046);
						}
						else if (objRecVal.isInt(aoTitle))
						{
							errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[70] + "^^" + TBAF_FV_5046);
						}
						else if(aoTitle.trim().length() != aoTitle.length())
						{
							errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[70] + "^^" + TBAF_FV_5046);
						}
					  	
						else if( (Integer.parseInt(aoTitle.trim()) > 04) 
								|| (Integer.parseInt(aoTitle.trim()) < 01))								
						{
							errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[70] + "^^" + TBAF_FV_5046);
						}
					}
					
					else if (aoTitle.equals(TBAF_FIELD_NULL) || aoTitle.equals(TBAF_FIELD_SEPERATOR)){
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[70] + "^^" + TBAF_FV_5068);
					}
					
				}
				else //gauri added this
				{
					if(!aoTitle.equals(TBAF_FIELD_NULL) && ! aoTitle.equals(TBAF_FIELD_SEPERATOR)) {
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[70] + "^^" + TBAF_FV_5042);
					}
					else {
					objReadFVAL2.statReportBuffer.append("-" + TBAF_FIELD_SEPERATOR);
					}
				}
				//Gauri added AO Title validation for CR 89435, FVU 1.9:: END
*/				
			 
			 /**
				 *	Validation of Filler 6(Field No. 71 Of Batch Header Record)	
				 *	
				 */	
			 
				//Gauri added Filler 6 validation::START
				
				if (objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_ORIG) || objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_CORR))
				{
					if (!bhFiller_6.equals(TBAF_FIELD_NULL) && !bhFiller_6.equals(TBAF_FIELD_SEPERATOR))
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[71] + "^^" + TBAF_FV_2049);
					}
					/* else if(! (objRecVal.isFieldNull(bhFiller_6)))     
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[71] + "^^" + TBAF_FV_2049);
					} */
					else
					{
						objReadFVAL2.statReportBuffer.append("-" + TBAF_FIELD_SEPERATOR);
					}
				}		
				
				//Gauri added Filler 6 validation::END
				
				
			 /*//Gauri added the validations for AO mobile number for CR 89435, FVU 1.9: START
			 
			 if (Integer.parseInt(objReadFVAL2.finYear) >= 2025 && (objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_ORIG) || objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_CORR)))
			 {
				 if (! mobileNoOfAO.equals(TBAF_FIELD_NULL) && ! mobileNoOfAO.equals(TBAF_FIELD_SEPERATOR))
				 {
					 if(mobileNoOfAO.trim().length() != mobileNoOfAO.length())
					 {
						 errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[71] + "^^" + TBAF_FV_5041); 
					 }
					 else if(mobileNoOfAO.trim().length() != TBAF_AO_MOBILE_NO_LEN || mobileNoOfAO.length() != TBAF_AO_MOBILE_NO_LEN)
					 {
						 errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[71] + "^^" + TBAF_FV_5041);
					 }
					 else if(objRecVal.isInt(mobileNoOfAO))
					 {
						 errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[71] + "^^" + TBAF_FV_5041);
					 }
					 else if (Long.parseLong(mobileNoOfAO.trim()) == 0 || mobileNoOfAO.trim().equals("9999999999"))
					 {
							errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[71] + "^^" + TBAF_FV_5041);
					 }
				 }				 
				 else {
					 errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[71] + "^^" + TBAF_FV_5041);
				 }
			 }
			 else {
				 if(!mobileNoOfAO.equals(TBAF_FIELD_NULL) && ! mobileNoOfAO.equals(TBAF_FIELD_SEPERATOR)) {
					 errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[71] + "^^" + TBAF_FV_5042);
				 }
				 else {
					 objReadFVAL2.statReportBuffer.append("-" + TBAF_FIELD_SEPERATOR);
				 }
			 }
			 
			//Gauri added the validations for AO mobile number for CR 89435, FVU 1.9: END	
*/			 
			 /**
				 *	Validation of TAN of AO(Field No. 72 Of Batch Header Record)	
				 *	10 digit value to be provided, mandatory field
				 */	
			 
			 //Gauri added the validations for TAN of AO for CR 89435, FVU 1.9: START
			 
			  if (Integer.parseInt(objReadFVAL2.finYear) >= 2026 && (objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_ORIG) || objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_CORR)))
			 {
				 if (! TANofAO.equals(TBAF_FIELD_NULL) && ! TANofAO.equals(TBAF_FIELD_SEPERATOR))
				 {					 
					  if (objRecVal.isFieldNull(TANofAO))
						{
						 errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[72] + "^^" + TBAF_FV_5051);							
						}
					  else if (TANofAO.trim().length() != TANofAO.length())
						{
						 errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[72] + "^^" + TBAF_FV_5051);
						}
					  else if (TANofAO.trim().length() != 10 || TANofAO.length() != 10)
						{					
						 errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[72] + "^^" + TBAF_FV_5051);							
						}
					  else if((objRecVal.isAlphaNum(TANofAO) && ! objRecVal.checkTanAgainstConstants(TANofAO)) || (objRecVal.checkTan(TANofAO) && ! objRecVal.checkTanAgainstConstants(TANofAO)))
					  {
						 errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[72] + "^^" + TBAF_FV_5051);
					  }
				 }				 
				 else
				 {
					 errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[72] + "^^" + TBAF_FV_5051);
				 }				 
			 }			  
			 
			  else {
				  if(!TANofAO.equals(TBAF_FIELD_NULL) && ! TANofAO.equals(TBAF_FIELD_SEPERATOR)){
					  errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[72] + "^^" + TBAF_FV_5042);
				  }
				  /*else {
					  objReadFVAL2.statReportBuffer.append("-" + TBAF_FIELD_SEPERATOR);
				  }*/
			  }
			 
			//Gauri added the validations for TAN of AO for CR 89435, FVU 1.9: END
			 			 
			 /**
				 *	Validation of Special TAN for AO (Field No. 73 Of Batch Header Record)	
				 *	10 digit numeric value to be provided, optional field
				 */	
			//Gauri added the validations for AO Special TAN for CR 89435, FVU 1.9: START
			 
			 if (Integer.parseInt(objReadFVAL2.finYear) >= 2026 && (objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_ORIG) || objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_CORR)))
			 {
				 if(objReadFVAL2.deductCatgry.equals("S"))
				 {
					 if (!specialTAN.equals(TBAF_FIELD_NULL) && !specialTAN.equals(TBAF_FIELD_SEPERATOR))
					 {
					   if (objRecVal.isFieldNull(specialTAN))
						{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[73] + "^^" + TBAF_FV_5043);								
						}
					   else if (specialTAN.trim().length() != specialTAN.length())
						{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[73] + "^^" + TBAF_FV_5043);
						}
					   else if (specialTAN.trim().length() != 10 || specialTAN.length() != 10)
						{					
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[73] + "^^" + TBAF_FV_5043);
						}
					   else if((objRecVal.isAlphaNum(specialTAN) && ! objRecVal.checkTanAgainstConstants(specialTAN)) || (objRecVal.checkTan(specialTAN) && ! objRecVal.checkTanAgainstConstants(specialTAN)))
						{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[73] + "^^" + TBAF_FV_5043);
						}
					   /*else {
						   objReadFVAL2.statReportBuffer.append("-" + TBAF_FIELD_SEPERATOR);
						 }*/
				    }
			    }
		
				 else if(!objReadFVAL2.deductCatgry.equals("S")) {
					 if(!specialTAN.equals(TBAF_FIELD_NULL) && !specialTAN.equals(TBAF_FIELD_SEPERATOR)) {
						 errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[73] + "^^" + TBAF_FV_5044);
					 }
					 //1111 removed
					 /*else {
						 objReadFVAL2.statReportBuffer.append("-" + TBAF_FIELD_SEPERATOR);
					 }*/
				 }				
				 
			 }
		
			 else {
				 if(!specialTAN.equals(TBAF_FIELD_NULL) && ! specialTAN.equals(TBAF_FIELD_SEPERATOR)) {
					 errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[73] + "^^" + TBAF_FV_5042);
				 }
				 /*else {
					 objReadFVAL2.statReportBuffer.append("-" + TBAF_FIELD_SEPERATOR);
				 }*/
			 }
			 
			//Gauri added the validations for AO Special TAN for CR 89435, FVU 1.9: END
			 
			 /**
				 *	Validation of Name of Responsible person Title (Field No. 74 Of Batch Header Record)	
				 *	Optional field
				 */	
			 
				//Gauri added the validations for State AG Name for AO for CR 89435, 1.9 :- START
				
				if (Integer.parseInt(objReadFVAL2.finYear) >= 2026 && (objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_ORIG) || objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_CORR)))					
					{
					 	if(objReadFVAL2.deductCatgry.equals("S")) {
					 		if (!stateAGcode.equals(TBAF_FIELD_NULL) && !stateAGcode.equals(TBAF_FIELD_SEPERATOR))
							{
								if (objRecVal.isFieldNull(stateAGcode))
								{
									errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[74] + "^^" + TBAF_FV_5057);  
								}
								else if (stateAGcode.length() > 20)
								{
									errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[74] + "^^" + TBAF_FV_5057);  
								}
								else if(stateAGcode.trim().length() != stateAGcode.length())
								{
									errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[74] + "^^" + TBAF_FV_5057);   
								}
								else if (!objRecVal.isAlphanumeric(stateAGcode))
								{
									errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[74] + "^^" + TBAF_FV_5057);  
								}
																	  
							}
					 		/*else { 
					 			objReadFVAL2.statReportBuffer.append("-" + TBAF_FIELD_SEPERATOR);
							 }*/
					 	}
					 	
					 	else if(!objReadFVAL2.deductCatgry.equals("S")) {
					 		if (!stateAGcode.equals(TBAF_FIELD_NULL) && !stateAGcode.equals(TBAF_FIELD_SEPERATOR))
							 {
							 errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[74] + "^^" + TBAF_FV_5042);
							 }
					 		/*else {
					 			objReadFVAL2.statReportBuffer.append("-" + TBAF_FIELD_SEPERATOR);
					 		}*/
					 	}
					}
				
				else {
					 if(!stateAGcode.equals(TBAF_FIELD_NULL) && !stateAGcode.equals(TBAF_FIELD_SEPERATOR)) {
						 errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[74] + "^^" + TBAF_FV_5042);
					 }
					 /*else {
						 objReadFVAL2.statReportBuffer.append("-" + TBAF_FIELD_SEPERATOR);
					 }*/
				 }
				
				//Gauri added the validations for State AG Name for AO for CR 89435, 1.9 :- END		 
			 
			 /**
				 *	Validation of Filler 7(Field No. 75 Of Batch Header Record)	
				 *	
				 */	
				
				//Gauri added Filler 7 validation::START
				
				if (objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_ORIG) || objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_CORR))
				{
					if (!bhFiller_7.equals(TBAF_FIELD_NULL) && !bhFiller_7.equals(TBAF_FIELD_SEPERATOR))
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[75] + "^^" + TBAF_FV_2049);
					}
					/* else if(! (objRecVal.isFieldNull(bhFiller_6)))     
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[71] + "^^" + TBAF_FV_2049);
					} */
					/*else
					{
						objReadFVAL2.statReportBuffer.append("-" + TBAF_FIELD_SEPERATOR);
					}*/
				}		
				
				//Gauri added Filler 7 validation::END
				
				
				
				
			/*//Gauri added the validations for Name of Responsible person Title for CR 89435, FVU 1.9: START
			 
				if(Integer.parseInt(objReadFVAL2.finYear) >= 2025 && (objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_ORIG) || objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_CORR)))
				{				
//					if (rTitle.equals(TBAF_FIELD_NULL) || rTitle.equals(TBAF_FIELD_SEPERATOR))
//					{
//						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[74] + "^^" + TBAF_FV_5052);  
//					}
//					else if (!(rTitle.equals("Mr.") || rTitle.equals("Mrs.") || rTitle.equals("Ms.") || rTitle.equals("Non-Individual"))) 
//					{
//						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[74] + "^^" + TBAF_FV_5052);
//					}
					
					//Codes 01 to 04 are valid for Title
					
					if (!rTitle.equals(TBAF_FIELD_NULL) && ! rTitle.equals(TBAF_FIELD_SEPERATOR)) {
						if (objRecVal.isFieldNull(rTitle))
						{
					 		errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[75] + "^^" + TBAF_FV_5052);
						}
						else if (rTitle.length() > 2 || rTitle.trim().length() < 2)
						{
							errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[75] + "^^" + TBAF_FV_5052);
						}
						else if (objRecVal.isInt(rTitle))
						{
							errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[75] + "^^" + TBAF_FV_5052);
						}
						else if(rTitle.trim().length() != rTitle.length())
						{
							errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[75] + "^^" + TBAF_FV_5052);
						}
					  	
						else if( (Integer.parseInt(rTitle.trim()) > 04) 
								|| (Integer.parseInt(rTitle.trim()) < 01))								
						{
							errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[75] + "^^" + TBAF_FV_5052);
						}
					}
					else if (rTitle.equals(TBAF_FIELD_NULL) || rTitle.equals(TBAF_FIELD_SEPERATOR)) {
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[75] + "^^" + TBAF_FV_5069);
					}
					
				}
				
				else if (Integer.parseInt(objReadFVAL2.finYear) < 2025) {
					if(!(rTitle.equals(TBAF_FIELD_NULL) || rTitle.equals(TBAF_FIELD_SEPERATOR))) {
					errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[75] + "^^" + TBAF_FV_5042);
					}
					else {
						objReadFVAL2.statReportBuffer.append("-" + TBAF_FIELD_SEPERATOR);
					}
				}
			 
			//Gauri added the validations for Name of Responsible person Title for CR 89435, FVU 1.9: END
*/				
				 /**
				 *	Validation of Name of Responsible person First Name (Field No. 76 Of Batch Header Record)	
				 *	Optional field
				 */	
				
				//Gauri added  the validations for Name of Responsible person First Name for CR 89435, FVU 1.9:: START
				
				if(Integer.parseInt(objReadFVAL2.finYear) >= 2026 && (objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_ORIG) || objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_CORR))) 
				{					
						if (!(rFirstName.equals(TBAF_FIELD_NULL) || rFirstName.equals(TBAF_FIELD_SEPERATOR)))
						{
							//errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[75] + "^^" + TBAF_FV_5047);  
							 if (objRecVal.isFieldNull(rFirstName))
							{
								errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[76] + "^^" + TBAF_FV_5053);  
							}
							else if (rFirstName.length() > 25)
							{
								errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[76] + "^^" + TBAF_FV_5053);  
							}
							else if (! objRecVal.checkAlphabets(rFirstName))
							{
								errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[76] + "^^" + TBAF_FV_5053);  
							}
						}
						/*else {
							objReadFVAL2.statReportBuffer.append("-" + TBAF_FIELD_SEPERATOR);
						}*/											
				}

				
				else if(Integer.parseInt(objReadFVAL2.finYear) < 2026){
					if(!(rFirstName.equals(TBAF_FIELD_NULL) || rFirstName.equals(TBAF_FIELD_SEPERATOR))) {
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[76] + "^^" + TBAF_FV_5042);
						}
						/*else {
							objReadFVAL2.statReportBuffer.append("-" + TBAF_FIELD_SEPERATOR);
						}*/
				}
								
				//Gauri added  the validations for Name of Responsible person First Name for CR 89435, FVU 1.9:: END
			 
				/**
				 *	Validation of Name of Responsible person Middle Name (Field No. 77 Of Batch Header Record)	
				 *	Optional field
				 */	
			 
				//Gauri added  the validations for Name of Responsible person Middle Name for CR 89435, FVU 1.9:: START
				
				if(Integer.parseInt(objReadFVAL2.finYear) >= 2026 && (objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_ORIG) || objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_CORR))) 
				{
						if (!(rMiddleName.equals(TBAF_FIELD_NULL) || rMiddleName.equals(TBAF_FIELD_SEPERATOR)))
						{
							//errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[76] + "^^" + TBAF_FV_5048);  
							 if (objRecVal.isFieldNull(rMiddleName))
							{
								errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[77] + "^^" + TBAF_FV_5054);  
							}
							else if (rMiddleName.length() > 25)
							{
								errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[77] + "^^" + TBAF_FV_5054);  
							}
							else if (! objRecVal.checkAlphabets(rMiddleName))
							{
								errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[77] + "^^" + TBAF_FV_5054);  
							}
						}
						/*else {
							objReadFVAL2.statReportBuffer.append("-" + TBAF_FIELD_SEPERATOR);
						}*/
				}

				
				else if(Integer.parseInt(objReadFVAL2.finYear) < 2026){
					if(!(rMiddleName.equals(TBAF_FIELD_NULL) || rMiddleName.equals(TBAF_FIELD_SEPERATOR))) {
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[77] + "^^" + TBAF_FV_5042);
						}
						/*else {
							objReadFVAL2.statReportBuffer.append("-" + TBAF_FIELD_SEPERATOR);
						}*/
				}
				
				//Gauri added  the validations for Name of Responsible person Middle Name for CR 89435, FVU 1.9:: END
				
				/**
				 *	Validation of Name of Responsible person Last Name (Field No. 78 Of Batch Header Record)	
				 *	Optional field
				 */	
			 
				//Gauri added  the validations for Name of Responsible person Last Name for CR 89435, FVU 1.9:: START
				
				if(Integer.parseInt(objReadFVAL2.finYear) >= 2026 && (objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_ORIG) || objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_CORR))) 
				{					
						if (!(rLastName.equals(TBAF_FIELD_NULL) || rLastName.equals(TBAF_FIELD_SEPERATOR)))
						{
							//errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[77] + "^^" + TBAF_FV_5049);  
							 if (objRecVal.isFieldNull(rLastName))
							{
								errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[78] + "^^" + TBAF_FV_5055);  
							}
							else if (rLastName.length() > 25)
							{
								errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[78] + "^^" + TBAF_FV_5055);  
							}
							else if (! objRecVal.checkAlphabets(rLastName))
							{
								errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[78] + "^^" + TBAF_FV_5055);  
							}
						}
						
						else if ((rLastName.equals(TBAF_FIELD_NULL) || rLastName.equals(TBAF_FIELD_SEPERATOR))) {
							errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[78] + "^^" + TBAF_FV_5055);
						}						
				}
				
				
				else if(Integer.parseInt(objReadFVAL2.finYear) < 2026){
					if(!(rLastName.equals(TBAF_FIELD_NULL) || rLastName.equals(TBAF_FIELD_SEPERATOR))) {
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[78] + "^^" + TBAF_FV_5042);
						}
						/*else {
							objReadFVAL2.statReportBuffer.append("-" + TBAF_FIELD_SEPERATOR);
						}*/
				}
				//Gauri added  the validations for Name of Responsible person Last Name for CR 89435, FVU 1.9:: END
				
			 			 
			 /**
				 *	Validation of Country Code for Responsible person details (Field No. 79 Of Batch Header Record)	
				 *	mandatory field
				 */
			 
			 // Gauri added the validation for responsible person country code for  CR 89435, FVU 1.9 :- START
			 
				if (Integer.parseInt(objReadFVAL2.finYear) >= 2026 && (objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_ORIG) || objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_CORR)))
				 {				
					 if(!rCountryCode.equals(TBAF_FIELD_NULL) && ! rCountryCode.equals(TBAF_FIELD_SEPERATOR))
					 {
						 	 if (objRecVal.isFieldNull(rCountryCode))
							{
						 		errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[79] + "^^" + TBAF_FV_5045);
							}
							else if (rCountryCode.length() > TBAF_COUNTRY_CODE_HIGH_RANGE || rCountryCode.trim().length() < TBAF_COUNTRY_CODE_LOW_RANGE)
							{
								errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[79] + "^^" + TBAF_FV_5045);
							}
							else if (objRecVal.isInt(rCountryCode))
							{
								errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[79] + "^^" + TBAF_FV_5045);
							}
							else if(rCountryCode.trim().length() != rCountryCode.length())
							{
								errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[79] + "^^" + TBAF_FV_5045);
							}
						  	
							else if( (Integer.parseInt(rCountryCode.trim()) > 286) 
									|| (Integer.parseInt(rCountryCode.trim()) < 01))								
							{
								errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[79] + "^^" + TBAF_FV_5045);
							}
					 }				 
					 else 
					 	{
						 errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[79] + "^^" + TBAF_FV_5045);
						}
				 	}

				else {
					 if(!rCountryCode.equals(TBAF_FIELD_NULL) && ! rCountryCode.equals(TBAF_FIELD_SEPERATOR)) {
						 errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[79] + "^^" + TBAF_FV_5042);
					 }
					 /*else {
						 objReadFVAL2.statReportBuffer.append("-" + TBAF_FIELD_SEPERATOR);
					 }*/
				 }
			 
				// Gauri added the validation for responsible person country code for  CR 89435, FVU 1.9 :- END
			 
		 
			 /**
				 *	Validation of Batch Header Record Hash(Field No. 80 Of Batch Header Record)	
				 *	TNo value should be specified
				 */	   
			 
		/*	 if (objReadFVAL2.stmtType.equals(TBAF_TYPE_OF_STMT_ORIG))
				{
					if (! bhRecordHash.equals(TBAF_FIELD_NULL) && ! bhRecordHash.equals(TBAF_FIELD_SEPERATOR))
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[70] + "^^" + TBAF_FV_2049);
					} */
					/*  else if(! (objRecVal.isFieldNull(receiptNo)))     
					{
						errStrBuff.append(TBAF_BHREC + lineNo + "^" + TBAF_BH_FIELD[70] + "^^" + TBAF_FV_2049);
					}*/
				/*	else
					{
						objReadFVAL2.statReportBuffer.append(bhRecordHash + TBAF_FIELD_SEPERATOR);
					}
				} */
			
			 
			 //End of Batch Header Record Hash validation
			 
			 //End of Added by subhankar
     }// end of try block
		catch (Exception e)
		{
			Log.tbaf_log.error("Exception", e);
			e.printStackTrace();
		}
		/***************************************BATCH HEADER VALIDATION ENDS***************************************/
		
		
		
		String s = FileHeaderValidation.getFileType();
		
		if (s.equals(TBAF_FIELD_NULL) || s.equals(TBAF_FIELD_SEPERATOR))
		{
			errStrBuff.append(TBAF_FHREC + lineNo + "^" + TBAF_FH_FIELD[3] + "^^" + TBAF_FV_1001); 
		}

		//Gauri added year condition for Form Type for CR 89435, FVU 1.9::START
		else if(Integer.parseInt(objReadFVAL2.finYear) < 2026) {
			if(!s.equals(TBAF_FILE_TYPE)) {
				errStrBuff.append(TBAF_FHREC + lineNo + "^" + TBAF_FH_FIELD[3] + "^^" + TBAF_FV_1007);
			}
		}
		
		else if(Integer.parseInt(objReadFVAL2.finYear) >= 2026) {
			if(!s.equals(TBAF_NEW_FILE_TYPE)) {
				errStrBuff.append(TBAF_FHREC + lineNo + "^" + TBAF_FH_FIELD[3] + "^^" + TBAF_FV_5067);
			}
		}
		//Gauri added year condition for Form Type for CR 89435, FVU 1.9::END
		
	}
	
	//Gauri added this method to get AO names in SSR for CR 89435, FVU 1.9:: START
//	public static void setaoFirstName(String aoFirstName) {
//		firstNameAO = aoFirstName;
//		Log.tbaf_log.debug(firstNameAO);
//	}
//
//	public String getaoFirstName() {
//		return firstNameAO;
//	}
//	
//	public static void setaoMiddleName(String aoMiddleName) {
//		middleNameAO = aoMiddleName;
//		Log.tbaf_log.debug(middleNameAO);
//	}
//
//	public String getaoMiddleName() {
//		return middleNameAO;
//	}
//	
//	public static void setaoLastName(String aoLastName) {
//		lastNameAO = aoLastName;
//		Log.tbaf_log.debug(lastNameAO);
//	}
//
//	public String getaoLastName() {
//		return lastNameAO;
//	}
	
	//Gauri added this method to get AO names in SSR for CR 89435, FVU 1.9:: END
	

 }	// End of bhFieldValidator method