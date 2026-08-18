/**	
 *	Class Name: TBAFInterface.java 
 */
package com.tin.etbaf.form24G.fvu;
/**	This class is an interface for all the static constants, error codes
 *	and error descriptions. 
 *
 *	@author TCS
 *	@version 13  
 */ 
public interface TBAFInterface
{
	public static final String TBAFFVUVersion = "1.9";  //Gauri changed version to 1.9 for CR 89435
	public static final String TBAF_FIELD_SEPERATOR = "^";
	public static final String TBAF_FIELD_NULL = "";
	public static final String TBAF_FH_REC = "FH";
	public static final String TBAF_BH_REC = "BH";
	public static final String TBAF_TD_REC = "TD";
	public static final String TBAF_FILE_TYPE = "24G";
	public static final String TBAF_NEW_FILE_TYPE = "F137";  //Gauri added new form type for CR 89435, FVU 1.9
	public static final String TBAF_UPLOADED_BY_TFC = "T";
	public static final String TBAF_UPLOADED_BY_AO = "D";
	public static final String TBAF_TYPE_OF_STMT_ORIG = "O";
	public static final String TBAF_TYPE_OF_STMT_CORR = "C";
	public static final String TBAF_REVISION_MODE_ADD = "N";
	public static final String TBAF_REVISION_MODE_DEL = "D";
	public static final String TBAF_REVISION_MODE_UPDATE="U";
	public static final String TBAF_TRANSACTION_TYPE_C1 = "C1";
	public static final String TBAF_TRANSACTION_TYPE_C2 = "C2";
	public static final String TBAF_TRANSACTION_TYPE_C3 = "C3";
	public static final String TBAF_TRANSACTION_TYPE_C4 = "C4";
	public static final String TBAF_FORM_24Q = "24Q";
	public static final String TBAF_FORM_26Q = "26Q";
	public static final String TBAF_FORM_27Q = "27Q";
	public static final String TBAF_FORM_27EQ = "27EQ";
	public static final String TBAF_NEW_FORM_24Q = "F138";	
	public static final String TBAF_NEW_FORM_26Q = "F140";
	public static final String TBAF_NEW_FORM_27Q = "F144";
	public static final String TBAF_NEW_FORM_27EQ = "F143";
	public static final String TBAF_QUARTER1 = "Q1";
	public static final String TBAF_QUARTER2 = "Q2";
	public static final String TBAF_QUARTER3 = "Q3";
	public static final String TBAF_QUARTER4 = "Q4";      //Commented By Subhankar as Quarter Is not Valid Now
	public static final String TBAF_FH_ERR_SEP = "^^^";
	public static final String TBAF_BH_ERR_SEP = "^1^^";
	public static final String TBAF_ERR_SEP = "^";
	public static final String TBAF_TD_ERR_SEP = "^1^";
	public static final String TBAF_HSHREC = "Hashing Error^";
	public static final String TBAF_FHREC = "File Header Record^";
	public static final String TBAF_BHREC = "Batch Record^";
	public static final String TBAF_TDREC = "Transaction Detail Record^";
	public static final int TBAF_DDO_REG_NO_LEN = 10;
	public static final int TBAF_DDO_CODE_LEN = 20;
	public static final int TBAF_DDO_EMAIL_LEN = 75;
	public static final int TBAF_DDO_NAT_OF_DED_LEN = 4;
	public static final int TBAF_DDO_MAPPING_LEN = 1;
	public static final int TBAF_BH_TRANSFER_VOUCHER_MONTH_LEN = 2;
	public static final int TBAF_RESPONSIBLE_PERSON_ADDRESS_LEN = 25;
    public static final int TBAF_RESPONSIBLE_PERSON_CITY_LEN = 25;
    public static final int TBAF_RESPONSIBLE_PERSON_STATE_LOW_RANGE = 1;
    public static final int TBAF_RESPONSIBLE_PERSON_STATE_HIGH_RANGE = 37; //changed By amit//Changes added for TBAF FVU 1.6 version by puja
    public static final int TBAF_RESPONSIBLE_PERSON_STATE_LEN = 2;
    public static final int TBAF_RESPONSIBLE_PERSON_PIN_LEN = 6;
    public static final int TBAF_RESPONSIBLE_PERSON_STD_LEN = 5;
    public static final int TBAF_RESPONSIBLE_PERSON_PHONE_LEN = 10;
    public static final int TBAF_RESPONSIBLE_PERSON_EMAIL_ID_LEN = 75;
    public static final int TBAF_RESPONSIBLE_PERSON_MOBILE_NO_LEN = 10;
    public static final int TBAF_MINISTRY_NAME_LEN = 2;
    public static final int TBAF_MINISTRY_NAME_HIGH_RANGE = 5;
    public static final int TBAF_MINISTRY_NAME_LOW_RANGE = 1;
    public static final int TBAF_STATE_LEN = 2;
    public static final int TBAF_SUB_MINISTRY_NAME_LEN = 2;
    public static final int TBAF_SUB_MINISTRY_NAME_HIGH_RANGE = 53;
    public static final int TBAF_SUB_MINISTRY_NAME_LOW_RANGE = 1;
    public static final int TBAF_SUB_MINISTRY_NAME_O_LEN = 150;
    public static final int TBAF_TOTAL_COUNT_24Q_26Q_27Q_27EQ_LEN = 9;
    public static final int TBAF_TOTAL_TAX_LEN = 15;
    public static final int TBAF_TOTAL_REMITTANCE_LEN = 15;
    public static final int TBAF_PAO_DTO_REG_NO_LEN = 7;
    public static final int TBAF_DISTINCT_DDO_COUNT_LEN = 9;
    public static final int TBAF_COUNT_DDO_ADD_UPDT_DEL_LEN = 9;
    public static final int TBAF_SUB_MINISTRY_OTHERS_CODE = 99;
    public static final int TBAF_FILE_CREATION_DATE_LEN = 8;
    public static final int TBAF_COUNTRY_CODE_LOW_RANGE = 1;
    public static final int TBAF_COUNTRY_CODE_HIGH_RANGE = 3;
    public static final int TBAF_AO_MOBILE_NO_LEN = 10;
    public static final int TBAF_DDO_FORM_TYPE_LEN = 4;	//Gauri added this for form type change for CR 89435
    
    
    //Addition on account of Correction in Form24G
    public static final String TBAF_TRANSACTION_TYPE_X = "X";
    public static final String TBAF_TRANSACTION_TYPE_M = "M";
    
    
    
	public static final String TBAF_FH_FIELD[] =
		{
			"",
			"Line Number(1)",
			"Record Type(2)",
			"File Type(3)",
			"File Creation Date(4)",
			"Type of Statement(5)",
			"Uploader Type(6)",
			"AIN/Organization/TFC ID(7)",
			"Number of Batches(8)",
			"FH Record Hash(9)",
			"FVU Version(10)",
			"FVU File Level Hash(11)",
			"SAM Version(12)",
			"SAM File Level Hash(13)",
			"SCM Version(14)",
			"SCM File Level Hash(15)" };

	public static final String TBAF_BH_FIELD[] =
		{
			"",
			"Line Number(1)",
			"Record Type(2)",
			"Batch Number(3)",
			"Type of Correction(4)",
			"AIN(5)",
			"Last AIN(6)",
			"AO Name(7)",
			"AO Address1(8)",
			"AO Address2(9)",
			"AO Address3(10)",
			"AO Address4(11)",
			"AO City(12)",
			"AO State(13)",
			"AO Pin Code(14)",
			"AO STD Code(15)",
			"Phone No.(16)",
			"Email ID(17)",
			"Responsible Person Name(18)",
			"Responsible Person Designation(19)",
			"Financial Year(20)",
			"Last Financial Year(21)",
			"Deductor Category(22)",
			"Last Deductor Category(23)",
			"Batch Updation Indicator(24)",
			"Filler 2(25)",
			"Filler 3(26)",
			"Filler 4(27)",
			"Count of DDO Records(28)",
			"Total TDS/TCS transferred (Rs.)(29)",
			"Original Receipt No. (PRN)(30)",
			"Previous Receipt No. (PRN)(31)",
			"Provisional Receipt Number(32)",
			"PRN Date(33)",
			"Month Of Transfer Voucher(34)",
			"Responsible Person Address1(35)",
			"Responsible Person Address2(36)",
			"Responsible Person Address3(37)",
			"Responsible Person Address4(38)",
			"Responsible Person City(39)",
			"Responsible Person State(40)",
			"Responsible Person Pin Code(41)",
			"Responsible Person STD Code(42)",
			"Responsible Person Phone No.(43)",
			"Responsible Person Email ID(44)",
			"Responsible Person Mobile(45)",
			"Statement Filed Earlier(46)",
			"State Name(47)",
			"Ministry Name(48)",
			"Sub Ministry Name(49)",
			"Sub Ministry Name(Others)(50)",
			"Count of 24Q Transactions(51)",
			"Control Total Tax for 24Q(52)",
			"Total TCS/TDS Remitted for 24Q(53)",
			"Count of 26Q Transactions(54)",
			"Control Total Tax for 26Q(55)",
			"Total TCS/TDS Remitted for 26Q(56)",
			"Count of 27Q Transactions(57)",
			"Control Total Tax for 27Q(58)",
			"Total TCS/TDS Remitted for 27Q(59)",
			"Count of 27EQ Transactions(60)",
			"Control Total Tax for 27EQ(61)",
			"Total TCS/TDS Remitted for 27EQ(62)",
			"Account Office Registration Number(63)",
			"Count of Distinct DDO's(64)",
			"Total TDS/TCS Remitted (Rs.)(65)",
			"Count of DDO Record Added(66)",
			"Count of DDO Record Updated(67)",
			"Count of DDO Record Deleted (68)",
			"Receipt Number(69)",
			"Filler 5(70)",
			"Filler 6(71)",
			"TAN of the Accounts Office(72)",
			"Special TAN(73)",
			"State AG Code(74)",
			"Filler 7(75)",
			"Responsible Person First Name(76)",
			"Responsible Person Middle Name(77)",
			"Responsible Person Last Name(78)",
			"Responsible Person Country Code(79)",
			"Batch Header Record Hash(80)",
			};

	public static final String TBAF_TD_FIELD[] =
		{
			"",
			"Line Number(1)",
			"Record Type(2)",
			"Batch Number(3)",
			"Revision Mode(4)",
			"Serial No.(5)",
			"Last TAN(6)",
			"TAN(7)",
			"Name(8)",
			"Address1(9)",
			"Address2(10)",
			"Address3(11)",
			"Address4(12)",
			"Address City(13)",
			"Address State(14)",
			"Address PIN(15)",
			"Tax Amount(16)",
			"Form Type(17)",
			"DDO Registration no.(18)",
			"DDO Code(19)",
			"Email ID(20)",
			"Remitted Amt(21)",
			"Nature of Deduction(22)",
			"DDO Mapping/Update(23)",
			"DDO Serial no.(24)",
			"Last Remitted Amt(25)",
			"Last DDO Registration Number(26)",
			"Last DDO Code(27)",
			"Last Tax Amount(28)",
			"Last DDO Deduction Nature(29)",
			"Filler_11(30)",
			"Filler_12(31)",
			"Filler_13(32)",
			"Transaction Detail Record Hash(33)" };

	public static final String TBAF_STATE_NAME[] =
		{
			"NO STATE",
			"ANDAMAN AND NICOBAR ISLANDS",
			"ANDHRA PRADESH",
			"ARUNACHAL PRADESH",
			"ASSAM",
			"BIHAR",
			"CHANDIGARH",
			"DADRA NAGAR HAVELI & DAMAN DIU",//24GFVU 1.7 Changes for state code
			"NO STATE",//24GFVU 1.7 bug fix
			"DELHI",
			"GOA",
			"GUJARAT",
			"HARYANA",
			"HIMACHAL PRADESH",
			"JAMMU & KASHMIR",
			"KARNATAKA",
			"KERALA",
			"LAKSHWADEEP",
			"MADHYA PRADESH",
			"MAHARASHTRA",
			"MANIPUR",
			"MEGHALAYA",
			"MIZORAM",
			"NAGALAND",
			"ORISSA",
			"PONDICHERRY",
			"PUNJAB",
			"RAJASTHAN",
			"SIKKIM",
			"TAMILNADU",
			"TRIPURA",
			"UTTAR PRADESH",
			"WEST BENGAL",
			"CHHATISHGARH",
			"UTTARANCHAL",
			"JHARKHAND",
			"TELANGANA","LADAKH"};    //Added By amit
	/*public static final String TBAF_STATE_GOVT_NAME[] =
		{
			"Central Government",
			"State Government - ANDAMAN AND NICOBAR ISLANDS",
			"State Government - ANDHRA PRADESH",
			"State Government - ARUNACHAL PRADESH",
			"State Government - ASSAM",
			"State Government - BIHAR",
			"State Government - CHANDIGARH",
			"State Government - DADRA & NAGAR HAVELI",
			"State Government - DAMAN & DIU",
			"State Government - DELHI",
			"State Government - GOA",
			"State Government - GUJARAT",
			"State Government - HARYANA",
			"State Government - HIMACHAL PRADESH",
			"State Government - JAMMU & KASHMIR",
			"State Government - KARNATAKA",
			"State Government - KERALA",
			"State Government - LAKSHWADEEP",
			"State Government - MADHYA PRADESH",
			"State Government - MAHARASHTRA",
			"State Government - MANIPUR",
			"State Government - MEGHALAYA",
			"State Government - MIZORAM",
			"State Government - NAGALAND",
			"State Government - ORISSA",
			"State Government - PONDICHERRY",
			"State Government - PUNJAB",
			"State Government - RAJASTHAN",
			"State Government - SIKKIM",
			"State Government - TAMILNADU",
			"State Government - TRIPURA",
			"State Government - UTTAR PRADESH",
			"State Government - WEST BENGAL",
			"State Government - CHHATISHGARH",
			"State Government - UTTARANCHAL",
			"State Government - JHARKHAND" };
*/
	

	public static final String[] TBAF_MINISTRY_NAME=
	{
		"NO MINISTRY",
		"Civil",
		"Railway",
		"Defence",
		"Telecommunication",
		"Post"
	};
	
	public static final String[] TBAF_SUB_MINISTRY_NAME=               //Added By Subhankar for change in Statistic file Data Format
	{
		"NO SUB MINISTRY",
		"Agriculture",
		"Atomic Energy",
		"Fertilizers",
		"Chemicals & Petrochemicals",
		"Civil Aviation & Tourism",
		"Coal",
		"Consumer Affairs, Food & Public Distribution",
		"Commerce & Textiles",
		"Environment & Forest and Ministry of Earth Science",
		"External affairs and Overseas Indian affairs",
		"Finance",
		"Central Board of Direct Taxes",
		"Central Board of Excise and Customs",
		"Controller of Aid Accounts and Audit",
		"Central Pension Accounting Office",
		"Food Processing Industries",
		"Health and Family Welfare",
		"Home Affairs and development of North Eastern Region",
		"Human Resource Development",
		"Industry",
		"Information and Broadcasting",
		"Telecommunication and Information Technology",
		"Labour",
		"Law and Justice and Company Affairs",
		"Personnel, Public Grievances and Pensions", 
		"Petroleum and Natural Gas",
		"Planning, Statistics and Programme Implementation",
		"Power",
		"New and Renewable Energy",
		"Rural Development and Panchayati Raj",
		"Science and Technology",
		"Space",
		"Steel",
		"Mines",
		"Social Justice and Empowerment",
		"Tribal Affairs",
		"D/o of Commerce (Supply Division)",
		"Shipping and Road Transport and Highways",
		"Urban Development, Urban Employment and Povery Alleviation",
		"Water Resources",
		"President's Secretariat",
		"Lok Sabha Secretariat",
		"Rajya Sabha Secretariat",
		"Election Commision",
		"Andaman and Nicobar Islands Administration",
		"Chandigarh Administration",
		"Dadra and Nagar Haveli",
		"Goa , Daman and Diu",
		"Lakshwadeep",
		"Pondicherry Administration",
		"Pay and Account Officers (Audit)",
		"Non-conventional energy sources",
		"Government of NCT of Delhi",


	};
	
	public static final String[] TBAF_TAN_RCC =
		{
			"AGR",
			"AHM",
			"ALD",
			"AMR",
			"BBN",
			"BLR",
			"BPL",
			"BRD",
			"CAL",
			"CHE",
			"CHN",
			"CMB",
			"DEL",
			"HYD",
			"JBP",
			"JDH",
			"JLD",
			"JPR",
			"KLP",
			"KNP",
			"LKN",
			"MRI",
			"MRT",
			"MUM",
			"NGP",
			"NSK",
			"PNE",
			"PTL",
			"PTN",
			"RCH",
			"RKT",
			"RTK",
			"SHL",
			"SRT",
			"TVD",
			"VPN" };
	
	
	public static final String[] NATURE_OF_DEDUCTION = 
	{
		"24Q",
		"26Q",
		"27Q",
		"27EQ" 
	};
	
	public static final String[] FORM_TYPE = //Gauri added this for form type changes for CR 89435, FVU 1.9
	{
		"F138",
		"F140",
		"F144",
		"F143"
	};
	
	public static final String[] DDO_MAPPING = 
	{
		"A",
		"D",
		"U"
	};
	
	public static final String[] DDO_REG_NO_MOD = 
	{
		"A",
		"B",
		"C",
		"D",
		"E",
		"F",
		"G"
	};
	
	public static final String[] DDO_TAN_INVALID = 
	{
		"TANAPPLIED",
		"TANINVALID",
		"TANNOTAVBL"
	};
	
	public static final String[] MONTH =
	{
		"NO MONTH",
		"January",
		"February",
		"March",
		"April",
		"May",
		"June",
		"July",
		"August",
		"September",
		"October",
		"November",
		"December"
	};
	//added by faizan for  FVU 1.4
	public static final String[] MONTH1 =
	{
		"00",
		"01",
		"02",
		"03",
		"04",
		"05",
		"06",
		"07",
		"08",
		"09",
		"10",
		"11",
		"12"
	};
	//ended by faizan

	/**
	 *	START - Error Codes and Error Descriptions For File Header Record
	 */
	static final String TBAF_FV_1001 = "F137/F24G-FV-1001 " + " Value not specified. " + "\n";
	static final String TBAF_FV_1000 = "F137/F24G-FV-1000 Invalid File Header Record length. \n";
	static final String TBAF_FV_1002 = "F137/F24G-FV-1002 " + " Invalid value. " + "\n";
	static final String TBAF_FV_1003 = "F137/F24G-FV-1003 " + " Leading and Trailing Spaces are not allowed. " + "\n";
	static final String TBAF_FV_1004 = "F137/F24G-FV-1004 " + " Value should only be 1. " + "\n";
	static final String TBAF_FV_1005 = "F137/F24G-FV-1005 " + " Length is greater than allowed limit. " + "\n";
	static final String TBAF_FV_1006 = "F137/F24G-FV-1006 " + " Value should be 'FH' (in Capital letters). " + "\n";
	static final String TBAF_FV_1007 = "F137/F24G-FV-1007 " + " Value should be '24G'. " + "\n";
//	static final String TBAF_FV_1008 = "F24G-FV-1008 " + " Value should be 'O' for Original (Regular) statement.Value should be 'C' for Correction statement. " + "\n";
	static final String TBAF_FV_1008 = "F137/F24G-FV-1008 " + " Value should be 'O' for Original (Regular) statement & 'C' for Correction Statement" + "\n";
	static final String TBAF_FV_1009 = "F137/F24G-FV-1009 " + " Invalid Upload type, value should be D. " + "\n";
	static final String TBAF_FV_1010 = "F137/F24G-FV-1010 " + " Length should be 5 digits. " + "\n";
	static final String TBAF_FV_1011 = "F137/F24G-FV-1011 " + " Length should be 7 digits. " + "\n";
	static final String TBAF_FV_1012 = "F137/F24G-FV-1012 " + " Value must be equal to 1. " + "\n";
	static final String TBAF_FV_1022 = "F137/F24G-FV-1022 " + " File Creation date is not a valid date " + "\n";
	static final String TBAF_FV_1023 = "F137/F24G-FV-1023 " + " File Creation date cannot be a future date " + "\n";

	public static final String TBAF_FV_1013 = "F137/F24G-FV-1013 File does not exist or Empty File\n";
	public static final String TBAF_FV_1014 = "F137/F24G-FV-1014 FVU Version is either Incorrect or NULL\n";
	public static final String TBAF_FV_1015 = "F137/F24G-FV-1015 Errors Found during Hash Validation.\n";
	public static final String TBAF_FV_1016 = "F137/F24G-FV-1016 Invalid File Type. Paper-based returns not allowed\n";
	public static final String TBAF_FV_1017 = "F137/F24G-FV-1017 SAM Version is either Incorrect or NULL.\n";
	public static final String TBAF_FV_1018 = "F137/F24G-FV-1018 SCM Version is either Incorrect or NULL.\n";
	public static final String TBAF_FV_1019 = "F137/F24G-FV-1019 Mismatch of FVU File Level HashCode.\n";
	public static final String TBAF_FV_1020 = "F137/F24G-FV-1020 Mismatch of SAM File Level HashCode.\n";
	public static final String TBAF_FV_1021 = "F137/F24G-FV-1021 Mismatch of SCM File Level HashCode.\n";
	public static final String TBAF_FV_1025 = "F137/F24G-FV-1025 " + " File Creation Date should be of 8 digits" + "\n";
	
	
	// Start ::  New Error Added in jan16
	
	
	//static final String TBAF_FV_1026 = "F24G-FV-1026 " + " Value not specified. The value should be '1'. " + "\n"; //For LINE NUMBER(Field No.1)
	//static final String TBAF_FV_1027 = "F24G-FV-1027 " + " Length is greater than allowed limit. The value should be '1'. " + "\n"; //For LINE NUMBER(Field No.1)
	
	/*static final String TBAF_FV_1028 = "F24G-FV-1028 " + " Value should be 'FH' (in Capital letters). " + "\n"; //For RECORD TYPE(Field No.2)
	
	static final String TBAF_FV_1029 = "F24G-FV-1029 " + " Value should be '24G'. " + "\n"; //For FILE TYPE(Field No.3)
	
	static final String TBAF_FV_1033 = "F24G-FV-1033 " + "Mention File Creation Date in DDMMYYYY format. " + "\n"; //For FILE CREATION DATE(Field No.4)
	
	static final String TBAF_FV_1030 = "F24G-FV-1030 " + " Value should be 'O' for Original (Regular) statement.Value should be 'C' for Correction statement.. " + "\n"; //For STATEMENT TYPE(Field No.5)
	
	static final String TBAF_FV_1031 = "F24G-FV-1031 " + " Value should be 'D'. " + "\n"; //For UPLOADER TYPE(Field No.6)
	
	static final String TBAF_FV_1032 = "F24G-FV-1032 " + "Mention 7 digit AIN allotted to the Accounts Office. " + "\n"; //For AIN/ORGANIZATION/TFC-ID(Field No.7)
	
	static final String TBAF_FV_1034 = "F24G-FV-1034 " + "Value must always be 1. " + "\n"; //For  NUMBER OF BATCHES(Field No.8)
*/	
	//End :: 
	/**
	 *	END - Error Codes and Error Descriptions For File Header Record
	 */

	/**
	 *	START - Error Codes and Error Descriptions For Batch Header Record
	 */
	static final String TBAF_FV_2000 = "F137/F24G-FV-2000 Invalid Batch Header Record length \n";
	static final String TBAF_FV_2001 = "F137/F24G-FV-2001 " + " Value not specified. " + "\n";
	static final String TBAF_FV_2002 = "F137/F24G-FV-2002 " + " Spaces are not allowed. " + "\n";
	static final String TBAF_FV_2003 = "F137/F24G-FV-2003 " + " Invalid value." + "\n";
	static final String TBAF_FV_2004 = "F137/F24G-FV-2004 " + " Not In Sequence." + "\n";
	static final String TBAF_FV_2005 = "F137/F24G-FV-2005 " + " Length is greater than allowed limit. " + "\n";
	static final String TBAF_FV_2006 = "F137/F24G-FV-2006 " + " Value not specified." + "\n";  // new error added in jan16 changed by puja
	static final String TBAF_FV_2007 = "F137/F24G-FV-2007 " + " Value should be 'BH' (in Capital letters). " + "\n";
	static final String TBAF_FV_2008 = "F137/F24G-FV-2008 " + " Value is not specified. " + "\n";  // new error added in jan16 changed by puja
	static final String TBAF_FV_2009 = "F137/F24G-FV-2009 " + " Invalid value. " + "\n";
	static final String TBAF_FV_2010 = "F137/F24G-FV-2010 " + " Invalid Batch No, Batch no is not same as batch in which DDO record is present. " + "\n";
	static final String TBAF_FV_2011 = "F137/F24G-FV-2011 " + " Length is greater than allowed limit. " + "\n";
	static final String TBAF_FV_2012 = "F137/F24G-FV-2012 " + " Value not specified." + "\n";    // new error added in jan16 changed by puja
	static final String TBAF_FV_2013 = "F137/F24G-FV-2013 " + " For Correction Either TransactionType M or X should be provided." + "\n";  // new error added in jan16 changed by puja
	static final String TBAF_FV_2014 = "F137/F24G-FV-2014 " + " Not applicable for Regular (Original) Files. " + "\n";
	static final String TBAF_FV_2015 = "F137/F24G-FV-2015 " + " Value not specified. " + "\n";
	static final String TBAF_FV_2016 = "F137/F24G-FV-2016 " + " Invalid value. " + "\n";
	static final String TBAF_FV_2017 = "F137/F24G-FV-2017 " + " Leading and Trailing Spaces are not allowed. " + "\n";
	static final String TBAF_FV_2018 = "F137/F24G-FV-2018 " + " Length should be 7 digits. " + "\n";
	static final String TBAF_FV_2019 = "F137/F24G-FV-2019 " + " Value at File Header (FH) and Batch Header (BH) needs to be same." + "\n";
	static final String TBAF_FV_2020 = "F137/F24G-FV-2020 " + " Not applicable for Regular (Original) Files and  Correction (M,X) files " + "\n";
	static final String TBAF_FV_2021 = "F137/F24G-FV-2021 " + " Value should be same as specified in AIN at Batch Header Record. " + "\n";
	static final String TBAF_FV_2022 = "F137/F24G-FV-2022 " + " Value not specified. " + "\n";
	static final String TBAF_FV_2023 = "F137/F24G-FV-2023 " + " Invalid value. " + "\n";
	static final String TBAF_FV_2024 = "F137/F24G-FV-2024 " + " Length is greater than allowed limit. " + "\n";
	static final String TBAF_FV_2025 = "F137/F24G-FV-2025 " + " Not Applicable for X Correction Files. " + "\n";
	static final String TBAF_FV_2026 = "F137/F24G-FV-2026 " + " Value not specified. " + "\n";
	static final String TBAF_FV_2027 = "F137/F24G-FV-2027 " + " Invalid value. " + "\n";
	static final String TBAF_FV_2028 = "F137/F24G-FV-2028 " + " Length should be less than or equal to 2 digits. " + "\n";
	static final String TBAF_FV_2029 = "F137/F24G-FV-2029 " + " Value should be between 01 to 37 excluding 08. " + "\n";
	static final String TBAF_FV_2030 = "F137/F24G-FV-2030 " + " Value not specified." + "\n";
	static final String TBAF_FV_2031 = "F137/F24G-FV-2031 " + " Invalid value. " + "\n";
	static final String TBAF_FV_2032 = "F137/F24G-FV-2032 " + " Length should be equal to 6 digits. " + "\n";
	static final String TBAF_FV_2033 = "F137/F24G-FV-2033 " + " Value must be >= 110001. and it should not be 999999" + "\n";
	static final String TBAF_FV_2034 = "F137/F24G-FV-2034 " + " Value as '0' (all Zeros) is not allowed." + "\n";
	static final String TBAF_FV_2035 = "F137/F24G-FV-2035 " + " Length should be equal to 4 digits. " + "\n";
	static final String TBAF_FV_2036 = "F137/F24G-FV-2036 " + " Value should not be less than 2005. " + "\n";
	static final String TBAF_FV_2037 = "F137/F24G-FV-2037 " + " Not applicable for Regular (Original) Files and Correction files " + "\n";
	static final String TBAF_FV_2038 = "F137/F24G-FV-2038 " + " Value should be same as specifed in Financial Year.  " + "\n";
	static final String TBAF_FV_2039 = "F137/F24G-FV-2039 " + " Value should be between 00 to 35. " + "\n";
	static final String TBAF_FV_2040 = "F137/F24G-FV-2040 " + " Value should be same as specifed in Deductor Category. " + "\n";
//  static final String TBAF_FV_2041 = "F24G-FV-2041 " + " Valid values are 'Q1' ,'Q2' , 'Q3' ,'Q4'. " + "\n";
//  static final String TBAF_FV_2042 = "F24G-FV-2042 " + " Value should be same as specifed in Quarter. " + "\n";
	static final String TBAF_FV_2043 = "F137/F24G-FV-2043 " + " Valid values are '24Q' , '26Q' , '27Q' and '27EQ'. " + "\n";
  //static final String TBAF_FV_2044 = "F24G-FV-2044 " + " Value should be same as specifed in Nature Of Deduction. " + "\n";
	static final String TBAF_FV_2045 = "F137/F24G-FV-2045 " + " Not Applicable for X Correction Files. " + "\n";
	static final String TBAF_FV_2046 = "F137/F24G-FV-2046 " + " Value must be in decimals e.g 1000.00  " + "\n";
	static final String TBAF_FV_2047 = "F137/F24G-FV-2047 " + " There should be atleast 1 TD (DDO) Record for Original Statement. " + "\n";
	static final String TBAF_FV_2048 = "F137/F24G-FV-2048 " + " Length should be equal to 15 digits. " + "\n";
	static final String TBAF_FV_2049 = "F137/F24G-FV-2049 " + " No value should be specified. " + "\n";
	static final String TBAF_FV_2050 = "F137/F24G-FV-2050 " + " Invalid value. First five digits of the PRN Number must be same as 5-digit TFC ID in FH Record. " + "\n";
	static final String TBAF_FV_2051 = "F137/F24G-FV-2051 " + " Length should be equal to 8 digits. " + "\n";
	static final String TBAF_FV_2052 = "F137/F24G-FV-2052 " + " Number of Transaction detail Records in Batch Header is not matching with Number of Records present in Transaction Details \n";
	static final String TBAF_FV_2053 = "F137/F24G-FV-2053 " + " Number of Transaction detail Records in Batch Header is not matching with Number of Records present in Transaction Details \n";
	static final String TBAF_FV_2054 = "F137/F24G-FV-2054 " + " Total Remitted amount in Batch Record is not equal to sum of individual Remitted amounts in TD record For Correction of M type\n";
	static final String TBAF_FV_2055 = "F137/F24G-FV-2055 " + " For X Correction, Transaction Detail Records must not be specified. \n";
	static final String TBAF_FV_2056 = "F137/F24G-FV-2056 " + " For C4 Correction, AIN/Last AIN, Financial Year/Last Financial Year, Quarter/Last Quarter, Deductor Category/Last Deductor Category and Nature Of Deduction/Last Nature Of Deduction,its mandatory that atleast one of the fields should be different \n";
	static final String TBAF_FV_2057 = "F137/F24G-FV-2057 " + " Length should be less than or equal to 2 " + "\n";
	static final String TBAF_FV_2058 = "F137/F24G-FV-2058 " + " Month should be less than or equal to current month " + "\n";
	static final String TBAF_FV_2059 = "F137/F24G-FV-2059 " + " STD Code must be 91 " + "\n";
	static final String TBAF_FV_2060 = "F137/F24G-FV-2060 " + " Valid Ministry Name should be Provided. " + "\n";
	static final String TBAF_FV_2061 = "F137/F24G-FV-2061 " + " Length should be equal to 1 digit. " + "\n";
	static final String TBAF_FV_2062 = "F137/F24G-FV-2062 " + " Value should be either A or S ('A' for Central Govt. and 'S' for State Govt.) . " + "\n";
	static final String TBAF_FV_2063 = "F137/F24G-FV-2063 " + " Length should be equal to 3 digits. " + "\n";
	static final String TBAF_FV_2064 = "F137/F24G-FV-2064 " + " Valid Sub Ministry Name should be Provided. " + "\n";
	static final String TBAF_FV_2065 = "F137/F24G-FV-2065 " + " Value must be greater than 0.00 . " + "\n";
	static final String TBAF_FV_2066 = "F137/F24G-FV-2066 " + " Value must be equal to 0.00 if count of number of transaction is  0. " + "\n";
	static final String TBAF_FV_2067 = "F137/F24G-FV-2067 " + " Mobile number should be 10 digits. " + "\n";
	static final String TBAF_FV_2068 = "F137/F24G-FV-2068 " + " 7 digit valid PAO Registration Number should be Provided. " + "\n";
	static final String TBAF_FV_2069 = "F137/F24G-FV-2069 " + " Distint DDO Count should always be greater than or equal to 1 " + "\n";
	static final String TBAF_FV_2070 = "F137/F24G-FV-2070 " + " Value should be zero or greater than zero" + "\n";
	static final String TBAF_FV_2071 = "F137/F24G-FV-2071 " + " Financial Year less than 2005 is not allowed. " + "\n";
//	static final String TBAF_FV_2072 = "F24G-FV-2072 " + " As the year is 2010 so the month should be 04 or more" + "\n";
	static final String TBAF_FV_2073 = "F137/F24G-FV-2073 " + " As Phone no. is provided STD code is mandatory " + "\n";
//	static final String TBAF_FV_2074 = "F24G-FV-2074 " + " Both Phone number and mobile no cannot be blank"+ "\n";
	static final String TBAF_FV_2075 = "F137/F24G-FV-2075 " + " Both Phone number and mobile no cannot be blank"+ "\n";
	static final String TBAF_FV_2076 = "F137/F24G-FV-2076 " + " When Phone number is not present std should not be present"+ "\n";
	static final String TBAF_FV_2077 = "F137/F24G-FV-2077 " + " Only spaces are not allowed"+ "\n";
	static final String TBAF_FV_2078 = "F137/F24G-FV-2078 " + " Only Zeros' and all 9's as Pin code is not allowed"+ "\n";
	static final String TBAF_FV_2079 = "F137/F24G-FV-2079 " + " Financial year cannot be all zeros or all 9's"+ "\n";
	static final String TBAF_FV_2080 = "F137/F24G-FV-2080 " + " Mobile no should not have a leading zero."+ "\n";
	static final String TBAF_FV_2081 = "F137/F24G-FV-2081 " + " Mention proper STD Code.All 0's and all 9's are not allowed in STD code"+ "\n";
	static final String TBAF_FV_2082 = "F137/F24G-FV-2082 " + " Either month of transfer voucher or year is not valid"+ "\n";
	static final String TBAF_FV_2083 = "F137/F24G-FV-2083 " + " Valid PAO City should be provided"+ "\n";
	static final String TBAF_FV_2084 = "F137/F24G-FV-2084 " + " For Transaction Type X no AO Name should not be provided"+ "\n";
	static final String TBAF_FV_2085 = "F137/F24G_FV_2085 " + " For Correction Type X value should be 0 (zero)."+ "\n";
	static final String TBAF_FV_2086 = "F137/F24G_FV_2086 " + " For Correction Type X, amount of Tax should be 0.00 (zero)."+ "\n";
	static final String TBAF_FV_2087 = "F137/F24G_FV_2087 " + " For Correction Type X Last AIN should not be there"+ "\n";
	static final String TBAF_FV_2088 = "F137/F24G_FV_2088 " + " For Correction of Type M with Zero reords in batch there should be no TD records "+ "\n";
	static final String TBAF_FV_2089 = "F137/F24G_FV_2089 " + " Not applicable for regular and correction files "+ "\n";
	static final String TBAF_FV_2090 = "F137/F24G_FV_2090 " + " Batch Updation Indicator should be either Zero or One "+ "\n";
	static final String TBAF_FV_2091 = "F137/F24G-FV-2091 " + " Month of Transfer Voucher Length should be equal to 2 digits. " + "\n";
	static final String TBAF_FV_2092 = "F137/F24G-FV-2092 " + " Not applicable for Regular (Original) Files and X Correction files " + "\n";
	
	
	// Start ::  New Error added in jan 2016
     
	static final String TBAF_FV_2100 = "F137/F24G-FV-2100 " + " Mention 7 digit AIN allotted to the Accounts Office. " + "\n"; // For AIN(Field No. 5)
	
	static final String TBAF_FV_2101 = "F137/F24G-FV-2101 " + " Value not specified. Mention AO Name. " + "\n"; // For AO NAME(Field No. 7)
	
	static final String TBAF_FV_2102 = "F137/F24G-FV-2102 " + "Value Not mentioned. Mention AO Address. " + "\n"; // For ADDRESS 1(Field No. 8)
	
	static final String TBAF_FV_2103 = "F137/F24G-FV-2103 " + "Value Not Specified. Mention City/District of AO. " + "\n";  // For AO CITY(Field No. 12)
	
	static final String TBAF_FV_2104 = "F137/F24G-FV-2104 " + "Value not specified. Mention numeric code for State/UT of AO office. For list of state code refer Annexure 1. " + "\n";  // For AO STATE(Field No. 13)
	
	static final String TBAF_FV_2105 = "F137/F24G-FV-2105 " + "Mention proper PIN Code. " + "\n";  // For AO PIN CODE(Field No. 14)
	
	static final String TBAF_FV_2106 = "F137/F24G-FV-2106 " + "Mention proper STD Code of AO. " + "\n";  // For STD CODE(Field No. 15)
	
	static final String TBAF_FV_2107 = "F137/F24G-FV-2107 " + "Mention Phone Number. Only Numeric values allowed. " + "\n";  // For PHONE NUMBER(Field No. 16)
	static final String TBAF_FV_2108 = "F137/F24G-FV-2108 " + "Mention proper Phone Number. Only Numeric values allowed. " + "\n";  // For PHONE NUMBER(Field No. 16)
	
	static final String TBAF_FV_2109 = "F137/F24G-FV-2109 " + "Mention proper Email ID. " + "\n";  // For E-MAIL ID(Field No. 17)
	
	static final String TBAF_FV_2110 = "F137/F24G-FV-2110 " + "Mention name of person responsible for furnishing 24G statement.. " + "\n";  // For RESPONSIBLE PERSON NAME(Field No. 18)
	
	static final String TBAF_FV_2111 = "F137/F24G-FV-2111 " + "Value not specified. Mention Responsible person Designation. " + "\n";  // For RESPONSIBLE PERSON DESIGNATION(Field No. 19)
	
	
	static final String TBAF_FV_2112 = "F137/F24G-FV-2112 " + "Mention financial year for which Form 24G is submitted. " + "\n"; // For FINANCIAL YEAR(Field No.20)
	static final String TBAF_FV_2113 = "F137/F24G-FV-2113 " + "Mention valid value for financial year for which Form 24G is submitted. " + "\n"; // For FINANCIAL YEAR(Field No.20)
	
	static final String TBAF_FV_2114 = "F137/F24G-FV-2114 " + "Value not specified. Value should be either A or S ('A' for Central Govt. and 'S' for State Govt.) " + "\n"; // For AO Category(Field No.22)
	static final String TBAF_FV_2115 = "F137/F24G-FV-2115 " + "Value should be either A or S ('A' for Central Govt. and 'S' for State Govt.) " + "\n"; // For AO Category (Field No.22)
	
	static final String TBAF_FV_2116 = "F137/F24G-FV-2116 " + "Mention Number of transactions (should be equal to total no. of DDO records) pesent in the statement. Count should be >= 1 " + "\n"; // For No. of Transactions(Field No.28)
	
	static final String TBAF_FV_2117 = "F137/F24G-FV-2117 " + "For Correction, mention proper Original PRN. " + "\n"; // For Original RRR No. (Field No.30)
	static final String TBAF_FV_2118 = "F137/F24G-FV-2118 " + "For Correction, Original PRN should be provided. " + "\n"; // For Original RRR No. (Field No.30)
	
	static final String TBAF_FV_2120 = "F137/F24G-FV-2120 " + "For correcttion statement, for first correction value in this field should be original PRN, in case of subsequent correction PRN of last accepted correction should be provided . " + "\n";  // For Previous RRR No. (Field No.31)
	
	static final String TBAF_FV_2121 = "F137/F24G-FV-2121 " + "Mention the Month for which Form 24G to be filed. " + "\n";  // For Month of Transfer voucher (Field No.34)
	
	static final String TBAF_FV_2141 = "F137/F24G-FV-2141 " + "Mention address of Responsible person. " + "\n";  // For Responsible person Address  (Field No.35)
	static final String TBAF_FV_2123 = "F137/F24G-FV-2123 " + "Mention proper address of Responsible person. " + "\n";  // For Responsible person Address Line 3 (Field No.37)
	
	static final String TBAF_FV_2124 = "F137/F24G-FV-2124 " + "Mention City/District of AO. " + "\n";  // For Responsible person_City (Field No.39)
	static final String TBAF_FV_2125 = "F137/F24G-FV-2125 " + "Mention proper City/District of AO. " + "\n";  // For Responsible person_City (Field No.39)
	
	static final String TBAF_FV_2126 = "F137/F24G-FV-2126 " + "Mention Numeric code for State/UT of AO office. For list of state code refer Annexure 1." + "\n";  // For Responsible person_State (Field No.40)
	
	static final String TBAF_FV_2127 = "F137/F24G-FV-2127 " + "Mention PIN Code of Responsible person adress." + "\n";  // For Responsible person_PIN (Field No.41)
	
	static final String TBAF_FV_2128 = "F137/F24G-FV-2128 " + "Mention proper STD Code. For mobile phone STD code should be 91." + "\n";  // For Responsible person STD Code (Field No.42)

	static final String TBAF_FV_2129 = "F137/F24G-FV-2129 " + "Mention Phone Number." + "\n";  // For Responsible person Phone No. (Field No.43)
	static final String TBAF_FV_2130 = "F137/F24G-FV-2130 " + "Mention Proper value for Phone Number." + "\n";  // For Responsible person Phone No. (Field No.43)
	
	static final String TBAF_FV_2131 = "F137/F24G-FV-2131 " + "Mention Email ID. " + "\n";  // For Responsible person E-mail id(Field No. 44)
	static final String TBAF_FV_2132 = "F137/F24G-FV-2132 " + "Mention proper Email ID. " + "\n";  // For Responsible person E-mail id(Field No. 44)
	
	static final String TBAF_FV_2133 = "F137/F24G-FV-2133 " + "10 digit mobile number of responsible person to be mentioned. " + "\n";  // For Mobile no. of Responsible person(Field No. 45)
	
	static final String TBAF_FV_2134 = "F137/F24G-FV-2134 " + "Numeric code of state should be provided for state government, refer Annexure 1. " + "\n";  // For State name(Field No. 47)
	//static final String TBAF_FV_2135 = "F24G-FV-2135 " + "Numeric code of state should be provided for state government, Value should be between 01 to 36. refer Annexure 1 " + "\n";  // For State name(Field No. 47)
	
	static final String TBAF_FV_2135 = "F137/F24G-FV-2135 " + "Numeric code for Ministry name should be provided. Refer Annexure 3 of file format for list of Ministry name codes Mandatory for AO category central government " + "\n";  // For Ministry/ Department name(Field No. 48)
	
	static final String TBAF_FV_2136 = "F137/F24G-FV-2136 " + "Numeric code for sub ministry name should be provided. " + "\n";  // For Sub Ministry name(Field No. 49)
	static final String TBAF_FV_2137 = "F137/F24G-FV-2137 " + "Proper Numeric code for sub ministry name should be provided. " + "\n";  // For Sub Ministry name(Field No. 49)
	
	static final String TBAF_FV_2138 = "F137/F24G-FV-2138 " + "If numeric code '99' (i.e. Other) is provided in Ministry Name field then value in sub ministry name 'Other' field should be provided. " + "\n";  // For Sub Ministry name (Others)(Field No. 50)
	
	static final String TBAF_FV_2139 = "F137/F24G-FV-2139 " + "Mention Total number of DDO transactions for Form 24Q. " + "\n";  // For Count of 24Q transaction(Field No.51)
	
	static final String TBAF_FV_2140 = "F137/F24G-FV-2140 " + "Mention Total number of DDO transactions for Form type. " + "\n";  // For Count of 24Q transaction(Field No.51)
	
	
	// end :: New Error added in jan 2016
	/**
	 *	END - Error Codes and Error Descriptions For Batch Header Record
	 */

	/**
	 *	START - Error Codes and Error Descriptions For DDO Transaction Detail Record
	 */
	static final String TBAF_FV_3000 = "F137/F24G-FV-3000 Invalid Transaction Detail Record length.\n";
	static final String TBAF_FV_3001 = "F137/F24G-FV-3001 " + " Value not Specified. " + "\n";
	static final String TBAF_FV_3002 = "F137/F24G-FV-3002 " + " Invalid value. " + "\n";
	static final String TBAF_FV_3003 = "F137/F24G-FV-3003 " + " Spaces are not allowed. " + "\n";
	static final String TBAF_FV_3004 = "F137/F24G-FV-3004 " + " Line no. not in sequence. " + "\n";
	static final String TBAF_FV_3005 = "F137/F24G-FV-3005 " + " Length is greater than allowed limit. " + "\n";
	static final String TBAF_FV_3006 = "F137/F24G-FV-3006 " + " Value should be 'TD' (in Capital letters). " + "\n";
	static final String TBAF_FV_3007 = "F137/F24G-FV-3007 " + " Value must be equal to 1. " + "\n";
	static final String TBAF_FV_3008 = "F137/F24G-FV-3008 " + " For Regular (Original) statement no value should be provided. " + "\n";
	static final String TBAF_FV_3009 = "F137/F24G-FV-3009 " + " Value should be 'N' For Addition And 'D' For Deletion of record And 'U' For Updation of record. " + "\n";
	static final String TBAF_FV_3010 = "F137/F24G-FV-3010 " + " DDO Serial Number not in Sequence. " + "\n";
	static final String TBAF_FV_3011 = "F137/F24G-FV-3011 " + " Value must be greater than 1 for Correction Files. " + "\n";	
	static final String TBAF_FV_3012 = "F137/F24G-FV-3012 " + " No value should be specified. " + "\n";
	static final String TBAF_FV_3013 = "F137/F24G-FV-3013 " + " Old Serial Number and Serial Number should not be equal. " + "\n";
	static final String TBAF_FV_3014 = "F137/F24G-FV-3014 " + " Length should be equal to 10 digits. " + "\n";
	static final String TBAF_FV_3015 = "F137/F24G-FV-3015 " + " Value must not be specified if Revision Mode is 'D'. " + "\n";
	static final String TBAF_FV_3016 = "F137/F24G-FV-3016 " + " Length should not be greater than 2 digits. " + "\n";
	static final String TBAF_FV_3017 = "F137/F24G-FV-3017 " + " Value should be between 01 to 37 excluding 08. " + "\n";
	static final String TBAF_FV_3018 = "F137/F24G-FV-3018 " + " 6 Digit Valid DDO Address PIN Code should be provided. " + "\n";
	static final String TBAF_FV_3019 = "F137/F24G-FV-3019 " + " Value must be >= 110001. " + "\n";
//  static final String TBAF_FV_3020 = "F24G-FV-3020 " + " Month & Year of Payment is not within the Financial Year (Field no. 20 of BH). " + "\n";
//  static final String TBAF_FV_3021 = "F24G-FV-3021 " + " Month & Year of Payment should not appear more than once for the same TAN. " + "\n";
	static final String TBAF_FV_3022 = "F137/F24G-FV-3022 " + " Valid  TDS/TCS deducted amount should be provided (ie. 1000.00)  " + "\n";
	static final String TBAF_FV_3023 = "F137/F24G-FV-3023 " + " Invalid Value.Allowed Values are 24Q , 26Q , 27Q , 27EQ. " + "\n";
	static final String TBAF_FV_3024 = "F137/F24G-FV-3024 " + " No value should be specified. " + "\n";
	static final String TBAF_FV_3025 = "F137/F24G-FV-3025 " + " Invalid Value.Allowed Values are A , D , U " + "\n";
	static final String TBAF_FV_3026 = "F137/F24G-FV-3026 " + " Invalid DDO Registration No. for Deductor catagory Central Government " + "\n";
	static final String TBAF_FV_3027 = "F137/F24G-FV-3027 " + " Invalid DDO Registration No. for Deductor catagory State Government " + "\n";
//	static final String TBAF_FV_3028 = "F24G-FV-3028 " + " 5th to 9th character must be numeric " + "\n";
//	static final String TBAF_FV_3029 = "F24G-FV-3029 " + " Last Character should be between 'A' to 'G' AS PER MOD 7 of 5th to 9th digit " + "\n";
	static final String TBAF_FV_3030 = "F137/F24G-FV-3030 " + " Special characters are not allowed." + "\n";
	static final String TBAF_FV_3031 = "F137/F24G-FV-3031 " + " Length should not be greater than 20 digits. " + "\n";
	static final String TBAF_FV_3032 = "F137/F24G-FV-3032 " + " Email ID of DDO is not valid. " + "\n";
	static final String TBAF_FV_3033 = "F137/F24G-FV-3033 " + " DDO Month and year should be same as that of Batch " + "\n";
	static final String TBAF_FV_3034 = "F137/F24G-FV-3034 " + " DDO with the deduction nature (FOR TAN) already exists..... " + "\n";
	static final String TBAF_FV_3035 = "F137/F24G-FV-3035 " + " Only Zero is not allowed in the DDO Code. " + "\n";
	static final String TBAF_FV_3036 = "F137/F24G-FV-3036 " + " For AIN -DDO flag 'D' no value should be provided. " + "\n";
	static final String TBAF_FV_3037 = "F137/F24G-FV-3037 " + " If TAN is invalid then either DDO Reg no or DDo code should be provided. " + "\n";
	static final String TBAF_FV_3038 = "F137/F24G-FV-3038 " + " As the DDO Mapping/Update flag is D, value in field 'Tax Amount' should be 0.00  " + "\n";
	static final String TBAF_FV_3039 = "F137/F24G-FV-3039 " + " As the DDO Mapping/Update flag is D, value in field 'Remitted Amount' should be 0.00 " + "\n";
	static final String TBAF_FV_3040 = "F137/F24G-FV-3040 " + " As the DDO Mapping/Update flag is D no value should be specified in field 'Nature of Deduction'. " + "\n";
	static final String TBAF_FV_3041 = "F137/F24G-FV-3041 " + " DDO with the deduction nature (FOR DDO REG NUMBER) already exists..... " + "\n";
	static final String TBAF_FV_3042 = "F137/F24G-FV-3042 " + " DDO with the deduction nature (FOR DDO CODE) already exists..... " + "\n";
	static final String TBAF_FV_3043 = "F137/F24G-FV-3043 " + " All 9's is not allowed in the PIN CODE " + "\n";
	static final String TBAF_FV_3044 = "F137/F24G-FV-3044 " + " As the DDO Mapping/Update flag is D so no value should be specified in Month and year of the statement " + "\n";
	static final String TBAF_FV_3045 = "F137/F24G-FV-3045 " + " Mapping for this TAN is different from that of Mapping specified in one of the previous records of this file " + "\n";
	static final String TBAF_FV_3046 = "F137/F24G-FV-3046 " + " The TAN with mapping 'D' already exists in this file" + "\n";
//  static final String TBAF_FV_3047 = "TBAF-FV-3047 " + " The Tan with mapping  'D' cannot be applied or a TAN with 'D' can only be once " + "\n";
	static final String TBAF_FV_3048 = "F137/F24G-FV-3048 " + " Leading and Trailing spaces are not allowed " + "\n";
	static final String TBAF_FV_3049 = "F137/F24G-FV-3049 " + " Tan with null value already exists so DDO Mapping 'D' cannot be applied to it " + "\n";
	static final String TBAF_FV_3050 = "F137/F24G-FV-3050 " + " Tan with DDO Mapping 'D'  already exists so null  cannot be applied to it " + "\n";
	static final String TBAF_FV_3051 = "F137/F24G-FV-3051 " + " TD serial no. not in sequence " + "\n";
	static final String TBAF_FV_3052 = "F137/F24G-FV-3052 " + " Valid DDO City should be provided " + "\n";
	static final String TBAF_FV_3053 = "F137/F24G-FV-3053 " + " Valid  TDS/TCS Remitted amount should be provided (ie. 1000.00)  " + "\n";
	static final String TBAF_FV_3054 = "F137/F24G-FV-3054 " + " TD with the given Serial Number already exists with a Revision mode A" + "\n"; 
	static final String TBAF_FV_3055 = "F137/F24G-FV-3055 " + " TD with the given Serial Number already exists with a Revision mode D" + "\n"; 
	static final String TBAF_FV_3056 = "F137/F24G-FV-3056 " + "TD with the TAN, Nature Of Deduction and Revision Mode already exists" +"\n";
	static final String TBAF_FV_3057 = "F137/F24G-FV-3057 " + "For DDO(TD) with Revision mode D the TAX Amount should be 0.00 " +"\n";
	static final String TBAF_FV_3058 = "F137/F24G-FV-3058 " + "For DDO(TD) with Revision mode D the Remitted Amount should be 0.00 " +"\n";
	static final String TBAF_FV_3059 = "F137/F24G-FV-3059 " + "TD with the DDO Reg Number, Nature Of Deduction and Revision Mode already exists " +"\n";
	static final String TBAF_FV_3060 = "F137/F24G-FV-3060 " + "TD with the DDO CODE, Nature Of Deduction and  Revision Mode already exists " +"\n";
	static final String TBAF_FV_3061 = "F137/F24G-FV-3061 " + "For DDO(TD) with Revision mode U either Last DDO Regn Nuber or Last DDO Code is mandatory " +"\n";
	static final String TBAF_FV_3062 = "F137/F24G-FV-3062 " + " If TAN is invalid then either Last DDO Reg no or Last DDo code should be provided. " + "\n";
	static final String TBAF_FV_3063 = "F137/F24G-FV-3063 " + " Valid Last TDS/TCS deducted amount should be provided (ie. 1000.00)  " + "\n";
	static final String TBAF_FV_3064 = "F137/F24G-FV-3064 " + " Valid TAN can not be updated to Invalid TAN  " + "\n";
	static final String TBAF_FV_3065 = "F137/F24G-FV-3065 " + " Updation of invalid TAN of DDO to valid TAN as well as DDO Code is not allowed  " + "\n";
	static final String TBAF_FV_3066 = "F137/F24G-FV-3066 " + " Updation of invalid TAN of DDO to valid TAN as well as DDO Registration is not allowed  " + "\n";
	
	//added by faizan for FVU 1.4
	static final String TBAF_FV_3067 = "F137/F24G-FV-3067 " + " 10 digit Valid TAN should be provided  " + "\n";
	
	
	//Start ::  New Error code in jan16
	
	/*static final String TBAF_FV_3070 = "F24G-FV-3070 " + " Line no. not in sequence.  " + "\n";  // For Line Number(Field No.1)
	
	static final String TBAF_FV_3071 = "F24G-FV-3071 " + " Value should be 'TD' signifying the Transaction Detail (DDO) Record.  " + "\n";  // For Record Type(Field No.2)
	
	
	static final String TBAF_FV_3072 = "F24G-FV-3072 " +  "For Correction statement below values should be mentione 'N' - for addition of new DDO record 'D' - for deletion of DDO record 'U'- for updation of Ddo record"  + "\n";  // For Revision Mode(Field No.4)
	
	
	static final String TBAF_FV_3073 = "F24G-FV-3073 " + " Mention Correct TD Sr No" + "\n";  // For Serial No.(Field No.5)
	static final String TBAF_FV_3074 = "F24G-FV-3074 " + " Mention TD Sr No" + "\n";  // For Serial No.(Field No.5)
	
	static final String TBAF_FV_3075 = "F24G-FV-3075 " + " Mention Last DDO TAN case of U mode,for other modes value should be NULL" + "\n";  // For LAST DDO TAN.(Field No.6)
	static final String TBAF_FV_3076 = "F24G-FV-3076 " + "Mention proper Last DDO TAN case of U mode,for other modes value should be NULL" + "\n";  // For LAST DDO TAN.(Field No.6)
	
	static final String TBAF_FV_3077 = "F24G-FV-3077 " + " Mention Correct Name of DDO" + "\n";  // For Name of the DDO.(Field No.8)
	static final String TBAF_FV_3078 = "F24G-FV-3078 " + "Mention Name of DDO" + "\n";  // For Name of the DDO.(Field No.8)
	
	static final String TBAF_FV_3079 = "F24G-FV-3079 " + "Mention Address of DDO" + "\n";  // For DDO_Address Line 1.(Field No.9)
	static final String TBAF_FV_3080 = "F24G-FV-3080 " + "Mention Correct Address of DDO" + "\n";  // For DDO_Address Line 1.(Field No.9)
	
	static final String TBAF_FV_3081 = "F24G-FV-3081 " + "Mention Correct Value for Address of DDO" + "\n";  // For DDO_Address Line 2.(Field No.10)
	
	static final String TBAF_FV_3082 = "F24G-FV-3082 " + "Mention City/District of DDO." + "\n";  // For DDO_City(Field No.13)
	static final String TBAF_FV_3083 = "F24G-FV-3083 " + "Mention Correct Value for Address of DDO." + "\n";  // For DDO_City(Field No.13)
	
	static final String TBAF_FV_3084 = "F24G-FV-3084 " + "Mention correct Value for PIN Code of DDO." + "\n";  // For DDO_Address PIN(Field No.15)
	
	static final String TBAF_FV_3085 = "F24G-FV-3085 " + "Valid  TDS/TCS deducted amount should be provided (ie. 1000.00) ." + "\n";  // For TDS/TCS deducted Amount(Field No.16)
	
	static final String TBAF_FV_3086 = "F24G-FV-3086 " + "Mention the value Tax Remitted for for U mode(Mandatory),for rest all other modes value should be null." + "\n";  // For LAST Total TDS/TCS remitted to Government account (AG/Pr CCA)(Field No.25)
	static final String TBAF_FV_3087 = "F24G-FV-3087 " + "Valid  TDS/TCS Remitted amount should be provided (ie. 1000.00)." + "\n";  // For LAST Total TDS/TCS remitted to Government account (AG/Pr CCA)(Field No.25)
	

	*/
	
	//static final String TBAF_FV_3082 = "F24G-FV-3082 " + "Mention Correct Value for Address of DDO" + "\n";  // For DDO_Address Line 2.(Field No.10)
	
	
	
	
	
	
	//static final String TBAF_FV_3079 = "F24G-FV-3079 " + " Mention Correct Name of DDO" + "\n";  // For Name of the DDO.(Field No.8)
	
	
	
	
	//static final String TBAF_FV_3077 = "F24G-FV-3077 " + " Value not Specified. Mention ten digit valid TAN of DDO. Should be in Capital..  " + "\n";  //  DDO TAN(Field No. 7 )
	//static final String TBAF_FV_3078 = "F24G-FV-3078 " + " Invalid value. Mention ten digit valid TAN of DDO. Should be in Capital.  " + "\n";  // DDO TAN(Field No. 7 )
	//static final String TBAF_FV_3079 = "F24G-FV-3079 " + " Spaces are not allowed. Mention ten digit valid TAN of DDO. Should be in Capital.  " + "\n";  // DDO TAN(Field No. 7 )
	//static final String TBAF_FV_3080 = "F24G-FV-3080 " + " Length should be equal to 10 digits. Mention ten digit valid TAN of DDO. Should be in Capital.  " + "\n";  // DDO TAN(Field No. 7 )
	
	// End :: New Error code added in jan16
	/**
	 *	END - Error Codes and Error Descriptions For DDO Transaction Detail Record
	 */
	
	/**
	 *	START - Error Codes in TBAFFormatValidator class
	 */
	static final String TBAF_FV_5001 = "F137/F24G-FV-5001 More than one File Header Record is specified. " + "\n";
	static final String TBAF_FV_5002 = "F137/F24G-FV-5002 More than one Batch Header Record is specified. " + "\n";
	static final String TBAF_FV_5003 = "F137/F24G-FV-5003 Invalid Record Type. \n";
	static final String TBAF_FV_5004 = "F137/F24G-FV-5004 Invalid File. \n";
	static final String TBAF_FV_5005 = "F137/F24G-FV-5005 "+" Either Year or Month of the statement is not valid  \n";
	static final String TBAF_FV_5006 = "F137/F24G-FV-5006 "+"Valid Count of 24Q transaction should be provided.  \n";
	static final String TBAF_FV_5007 = "F137/F24G-FV-5007 "+"When count of 24Q is zero then its total tax and total remittance amounts should be zero   \n";
	static final String TBAF_FV_5008 = "F137/F24G-FV-5008 "+"Tax Deducted/collected Amount at batch header and summation of Tax Deducted/collected Amount for Form 24Q is not matching \n";
	static final String TBAF_FV_5009 = "F137/F24G-FV-5009 "+"Total tax remitted amount at batch header for Form 24Q and summation of Tax remitted amount at transaction detail for Form 24Q is not matching \n";
	static final String TBAF_FV_5010 = "F137/F24G-FV-5010 "+"Valid Count of 26Q transaction should be provided.  \n";
	static final String TBAF_FV_5011 = "F137/F24G-FV-5011 "+"When count of 26Q is zero then its total tax and total remittance amounts should be zero  \n";
	static final String TBAF_FV_5012 = "F137/F24G-FV-5012 "+"Tax Deducted/collected Amount at batch header and summation of Tax Deducted/collected Amount for Form 26Q is not matching\n";
	static final String TBAF_FV_5013 = "F137/F24G-FV-5013 "+"Total tax remitted amount at batch header for Form 26Q and summation of Tax remitted amount at transaction detail for Form 26Q is not matching  \n";
	static final String TBAF_FV_5014 = "F137/F24G-FV-5014 "+"Valid Count of 27Q transaction should be provided..  \n";
	static final String TBAF_FV_5015 = "F137/F24G-FV-5015 "+"When count of 27Q is zero then its total tax and total remittance amounts should be zero  \n";
	static final String TBAF_FV_5016 = "F137/F24G-FV-5016 "+"Tax Deducted/collected Amount at batch header and summation of Tax Deducted/collected Amount for Form 27Q is not matching \n";
	static final String TBAF_FV_5017 = "F137/F24G-FV-5017 "+"Total tax remitted amount at batch header for Form 27Q and summation of Tax remitted amount at transaction detail for Form 27Q is not matching \n";
	static final String TBAF_FV_5018 = "F137/F24G-FV-5018 "+"Valid Count of 27EQ transaction should be provided..  \n";
	static final String TBAF_FV_5019 = "F137/F24G-FV-5019 "+"When count of 27EQ is zero then its total tax and total remittance amounts should be zero  \n";
	static final String TBAF_FV_5020 = "F137/F24G-FV-5020 "+"Tax Deducted/collected Amount at batch header and summation of Tax Deducted/collected Amount for Form 27EQ is not matching \n";
	static final String TBAF_FV_5021 = "F137/F24G-FV-5021 "+"Total tax remitted amount at batch header for Form 27EQ and summation of Tax remitted amount at transaction detail for Form 27EQ is not matching \n";
	static final String TBAF_FV_5022 = "F137/F24G-FV-5022 "+"Number of Distinct DDO's are not same \n";
	static final String TBAF_FV_5023 = "F137/F24G-FV-5023 "+"Total Remittance amount is not same \n";
	static final String TBAF_FV_5024 = "F137/F24G-FV-5024 "+"Total no of DDO record added is not same \n";
	static final String TBAF_FV_5025 = "F137/F24G-FV-5025 "+"Total no of DDO record updated is not same \n";
	static final String TBAF_FV_5026 = "F137/F24G-FV-5026 "+"Total no of DDO record deleted is not same \n";
	static final String TBAF_FV_5027 = "F137/F24G-FV-5027 "+"Total Count of Added DDO Record in Batch Header is not matching with Number Added DDO Record in DDO Transcation Detail \n";
	static final String TBAF_FV_5028 = "F137/F24G-FV-5028 "+"Total Count of Updated DDO Record in Batch Header is not matching with Number Updated DDO Record in DDO Transcation Detail \n";
	static final String TBAF_FV_5029 = "F137/F24G-FV-5029 "+"Total Count of Deleted DDO Record in Batch Header is not matching with Number Deleted DDO Record in DDO Transcation Detail \n";
	static final String TBAF_FV_5030 = "F137/F24G-FV-5030 "+"File Creation Date cannot less than Batch Date";
	static final String TBAF_FV_5031 = "F137/F24G-FV-5031 "+"Total Tax amount is not same \n";
	
	static final String TBAF_FV_5032 = "F137/F24G-FV-5032 "+"Tax Deducted/collected Amount at batch header and summation of Tax Deducted/collected Amount for Form 24Q is not matching for 'M' Correction \n";
	static final String TBAF_FV_5033 = "F137/F24G-FV-5033 "+"Total tax remitted amount at batch header for Form 24Q and summation of Tax remitted amount at transaction detail for Form 24Q is not matching for 'M' Correction\n";
	static final String TBAF_FV_5034 = "F137/F24G-FV-5034 "+"Tax Deducted/collected Amount at batch header and summation of Tax Deducted/collected Amount for Form 26Q is not matching for 'M' Correction \n";
	static final String TBAF_FV_5035 = "F137/F24G-FV-5035 "+"Total tax remitted amount at batch header for Form 26Q and summation of Tax remitted amount at transaction detail for Form 26Q is not matching for 'M' Correction \n";
	static final String TBAF_FV_5036 = "F137/F24G-FV-5036 "+"Tax Deducted/collected Amount at batch header and summation of Tax Deducted/collected Amount for Form 27Q is not matching for 'M' Correction \n";
	static final String TBAF_FV_5037 = "F137/F24G-FV-5037 "+"Total tax remitted amount at batch header for Form 27Q and summation of Tax remitted amount at transaction detail for Form 27Q is not matching for 'M' Correction \n";
	static final String TBAF_FV_5038 = "F137/F24G-FV-5038 "+"Tax Deducted/collected Amount at batch header and summation of Tax Deducted/collected Amount for Form 27EQ is not matching for 'M' Correction \n";
	static final String TBAF_FV_5039 = "F137/F24G-FV-5039 "+"Total tax remitted amount at batch header for Form 27EQ and summation of Tax remitted amount at transaction detail for Form 27EQ is not matching for 'M' Correction \n";
	
	//Gauri added error code for CR 89435, FVU 1.9
	static final String TBAF_FV_5040 = "F137/F24G-FV-5040 "+"Invaild value for Country code of Account Office \n";
	static final String TBAF_FV_5041 = "F137/F24G-FV-5041 " + "10 digit mobile number of Accounts Office to be mentioned \n";
	static final String TBAF_FV_5042 = "F137/F24G-FV-5042 " + "No value should be specified \n";
	static final String TBAF_FV_5043 = "F137/F24G-FV-5043 " + "10 digit Valid TAN should be provided for Special TAN of the Accounts Office \n";
	static final String TBAF_FV_5044 = "F137/F24G-FV-5044 " + "This field is applicable only when 'Account Officer Category' is selected as 'State Govt.'" +"\n";
	static final String TBAF_FV_5045 = "F137/F24G-FV-5045 "+"Invaild value for Country code of Responsible Person \n";
	static final String TBAF_FV_5046 = "F137/F24G-FV-5046 "+"Invaild value for Title of AO \n";
	static final String TBAF_FV_5047 = "F137/F24G-FV-5047 "+"Invaild value for First Name of AO \n";
	static final String TBAF_FV_5048 = "F137/F24G-FV-5048 "+"Invaild value for Middle Name of AO \n";
	static final String TBAF_FV_5049 = "F137/F24G-FV-5049 "+"Invaild value for Last Name of AO \n";
	static final String TBAF_FV_5050 = "F137/F24G-FV-5050 "+"Invaild value for Name of AO \n";
	static final String TBAF_FV_5051 = "F137/F24G-FV-5051 "+"10 digit valid TAN should be provided for TAN of Accounts Office \n";
	static final String TBAF_FV_5052 = "F137/F24G-FV-5052 "+"Invaild value for Title of Name of Responsible Person \n";
	static final String TBAF_FV_5053 = "F137/F24G-FV-5053 "+"Invaild value for First Name of Responsible Person \n";
	static final String TBAF_FV_5054 = "F137/F24G-FV-5054 "+"Invaild value for Middle Name of Responsible Person \n";
	static final String TBAF_FV_5055 = "F137/F24G-FV-5055 "+"Invaild value for Last Name of Responsible Person \n";
	static final String TBAF_FV_5056 = "F137/F24G-FV-5056 "+"Invaild value for Name of Responsible Person \n";
	static final String TBAF_FV_5057 = "F137/F24G-FV-5057 "+"Invaild value for State AG Code of Accounts Office \n";
	static final String TBAF_FV_5058 = "F137/F24G-FV-5058 "+"This field is not applicable from Financial Year 2026-27 onwards \n";
	static final String TBAF_FV_5059 = "F137/F24G-FV-5059 " + " As the DDO Mapping/Update flag is D no value should be specified in this field. " + "\n";
	static final String TBAF_FV_5060 = "F137/F24G-FV-5060 " + "TD with the DDO Reg Number, Form Type and Revision Mode already exists " +"\n";
	static final String TBAF_FV_5061 = "F137/F24G-FV-5061 " + "TD with the DDO CODE,Form Type and  Revision Mode already exists " +"\n";
	static final String TBAF_FV_5062 = "F137/F24G-FV-5062 " + " DDO with the Form Type (FOR TAN) already exists " + "\n";
	static final String TBAF_FV_5063 = "F137/F24G-FV-5063 " + " DDO with the Form Type (FOR DDO REG NUMBER) already exists " + "\n";
	static final String TBAF_FV_5064 = "F137/F24G-FV-5064 " + " DDO with the Form Type (FOR DDO CODE) already exists " + "\n";
	static final String TBAF_FV_5065 = "F137/F24G-FV-5065 " + " Invalid Value.Allowed Values are F138, F140 , F144 , F143 " + "\n";
	static final String TBAF_FV_5066 = "F137/F24G-FV-5066 " + "Mention Total number of DDO transactions for Form F138. " + "\n";
	static final String TBAF_FV_5067 = "F137/F24G-FV-5067 " + " Value should be 'F137' " + "\n";
	static final String TBAF_FV_5068 = "F137/F24G-FV-5068 " + " AO Title is a Mandatory Field " + "\n";
	static final String TBAF_FV_5069 = "F137/F24G-FV-5069 " + " Responsible Person Title is a Mandatory Field " + "\n";
	
	
	/**
	 *	END - Error Codes in TBAFFormatValidator class
	 */

	

	
}

