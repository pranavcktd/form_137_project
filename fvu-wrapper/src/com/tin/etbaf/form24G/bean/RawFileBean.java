package com.tin.etbaf.form24G.bean;

public class RawFileBean {
	
	private static RawFileBean rfb = null;
	private String[] rawFileBuff ;
	
	public static final int TOTAL_NUM_FLDS = 65;
	
	public static final int TYPE_OF_STMT_FLDNUM  = 1;
	public static final int FILE_CRTN_DT_FLDNUM  = 2;
	public static final int NO_OF_BTCH_FLDNUM  = 3;
	public static final int AIN_FLDNUM  = 4;
	public static final int AO_NAME_FLDNUM  = 5;
	public static final int AO_ADDR1_FLDNUM  = 6;
	public static final int AO_ADDR2_FLDNUM  = 7;
	public static final int AO_ADDR3_FLDNUM  = 8;
	public static final int AO_ADDR4_FLDNUM  = 9;
	public static final int AO_STATE_FLDNUM  = 10;
	public static final int AO_PIN_FLDNUM  = 11;
	public static final int RESP_PERS_NAME_FLDNUM  = 12;
	public static final int RESP_PERS_DESG_FLDNUM  = 13;
	public static final int RESP_PERS_ADDR1_FLDNUM  = 14;
	public static final int RESP_PERS_ADDR2_FLDNUM  = 15;
	public static final int RESP_PERS_ADDR3_FLDNUM  = 16;
	public static final int RESP_PERS_ADDR4_FLDNUM  = 17;
	public static final int RESP_PERS_STATE_FLDNUM  = 18;
	public static final int RESP_PERS_PIN_FLDNUM  = 19;
	public static final int FIN_YEAR_FLDNUM  = 20;
	public static final int DED_CATEGORY_FLDNUM  = 21;
	public static final int MIN_STATE_NAME_FLDNUM  = 22;
	public static final int SUBMIN_NAME_FLDNUM  = 23;
	public static final int MONTH_FLDNUM  = 24;
	public static final int NAT_OF_DED_24Q_FLDNUM  = 25;
	public static final int COUNT_OF_DDO_24Q_FLDNUM  = 26;
	public static final int TOTAL_TDS_AMT_24Q_FLDNUM  = 27;
	public static final int NAT_OF_DED_26Q_FLDNUM  = 28;
	public static final int COUNT_OF_DDO_26Q_FLDNUM  = 29;
	public static final int TOTAL_TDS_AMT_26Q_FLDNUM  = 30;
	public static final int NAT_OF_DED_27Q_FLDNUM  = 31;
	public static final int COUNT_OF_DDO_27Q_FLDNUM  = 32;
	public static final int TOTAL_TDS_AMT_27Q_FLDNUM  = 33;
	public static final int NAT_OF_DED_27EQ_FLDNUM  = 34;
	public static final int COUNT_OF_DDO_27EQ_FLDNUM  = 35;
	public static final int TOTAL_TDS_AMT_27EQ_FLDNUM  = 36;
	public static final int TOTAL_DISTINCT_DDO_FLDNUM  = 37;
	public static final int COUNT_OF_DDO_ADDED_FLDNUM  = 38;
	public static final int COUNT_OF_DDO_UPDATED_FLDNUM  = 39;
	public static final int COUNT_OF_DDO_DELETED_FLDNUM  = 40;
	public static final int TOTAL_NO_OF_RECORDS_FLDNUM  = 41;
	public static final int COUNT_OF_VALID_TAN_FLDNUM  = 42;
	public static final int COUNT_OF_TANAPPLIED_FLDNUM  = 43;
	public static final int COUNT_OF_TANNOTAVBL_FLDNUM  = 44;
	public static final int COUNT_OF_TANINVALID_FLDNUM  = 45;
	public static final int COUNT_OF_DDO_RECORDS_FLDNUM = 46;
	

	//New fields added for Form24G correction
	public static final int COUNT_OF_DDO_RECORDS_ADDED_24Q = 47;
	public static final int COUNT_OF_DDO_RECORDS_DELETED_24Q = 48;
	public static final int Count_OF_DDO_RECORDS_UPDATED_24Q=49;
	public static final int TOTAL_AMOUNT_OF_ADDED_TAX_REMITTED_TO_GOVT_24Q = 50;
		
		
	public static final int COUNT_OF_DDO_RECORDS_ADDED_26Q = 51;
	public static final int COUNT_OF_DDO_RECORDS_DELETED_26Q = 52;
	public static final int Count_OF_DDO_RECORDS_UPDATED_26Q=53;
	public static final int TOTAL_AMOUNT_OF_ADDED_TAX_REMITTED_TO_GOVT_26Q = 54;
	
		
	public static final int COUNT_OF_DDO_RECORDS_ADDED_27Q = 55;
	public static final int COUNT_OF_DDO_RECORDS_DELETED_27Q = 56;
	public static final int Count_OF_DDO_RECORDS_UPDATED_27Q=57;
	public static final int TOTAL_AMOUNT_OF_ADDED_TAX_REMITTED_TO_GOVT_27Q = 58;	
	
		
	public static final int COUNT_OF_DDO_RECORDS_ADDED_27EQ= 59;
	public static final int COUNT_OF_DDO_RECORDS_DELETED_27EQ = 60;
	public static final int Count_OF_DDO_RECORDS_UPDATED_27EQ=61;
	public static final int TOTAL_AMOUNT_OF_ADDED_TAX_REMITTED_TO_GOVT_27EQ = 62;	
		
	
	public static final int TOTAL_AMOUNT_OF_TAX_REMITTED_TO_GOVT = 63;
	public static final int ORIGINAL_RRR_NUM = 64;
	public static final int PREVIOUS_RRR_NUM = 65;
	
	
	public static final String[] NAT_OF_DED = {"24Q","26Q","27Q","27EQ"};
	public static final String[] NEW_FORM_TYPE = {"F138","F140","F144","F143"};//Gauri added form name changes for CR 89435, FVU 1.9


	
	private RawFileBean()
	{}
	
	public static RawFileBean getInstance()
	{
		if(rfb == null)
		{
			rfb = new RawFileBean();
			rfb.rawFileBuff = new String[TOTAL_NUM_FLDS+1];		
		}
		
		return rfb;
		
	}
	
	
	public void setValue(String str,int position)
	{
		if(str != null)
			str = str.trim();
		if(str == null || str.equals("") || str.equals(null) || str.equals("^"))
			str = "-";
		
		rawFileBuff[position] = str;
	}
	
	public void setValues(String str,int position,int flag)		
	{
		if(str.equals("0"))
			setValue("-",position);
		else
		{
			setValue(NAT_OF_DED[flag],position);
		}
			
	}
	
	//Gauri added new method for new form type for CR 89435, FVU 1.9
	public void setValues_2(String str,int position,int flag)	
	{
		if(str.equals("0"))
			setValue("-",position);
		else
		{
			setValue(NEW_FORM_TYPE[flag],position);
		}
			
	}
	

	public String getValue(int position)
	{
		return rawFileBuff[position];
	}

}
