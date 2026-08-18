package com.tin.etbaf.form24G.bean;                                        //Formed by Subhankar

import java.util.*;
public class BHTDCompBean {           
	private String countTD24Q ; //Count of number of 24Q Transactions in Batch
	private String countTD26Q ; //Count of number of 26Q Transactions in Batch
	private String countTD27Q ; //Count of number of 27Q Transactions in Batch
	private String countTD27EQ ; //Count of number of 27EQ Transactions in Batch
	private int countNatOfDed24Q = 0; //Count of number of 24Q Transactions in TD
	private int countNatOfDed26Q = 0; //Count of number of 26Q Transactions in TD
	private int countNatOfDed27Q = 0; //Count of number of 27Q Transactions in TD
	private int countNatOfDed27EQ = 0; //Count of number of 27EQ Transactions in TD
	private int countLastNatOfDed24Q = 0; //Count of number of Last 24Q Transactions in TD
	private int countLastNatOfDed26Q = 0; //Count of number of Last 26Q Transactions in TD
	private int countLastNatOfDed27Q = 0; //Count of number of Last 27Q Transactions in TD
	private int countLastNatOfDed27EQ = 0; //Count of number of Last 27EQ Transactions in TD
	private double totalTaxTD24Q = 0.00; //Total Amount of Tax for Form 24Q present in TD
	private double totalTaxTD26Q = 0.00; //Total Amount of Tax for Form 26Q present in TD
	private double totalTaxTD27Q = 0.00; //Total Amount of Tax for Form 27Q present in TD
	private double totalTaxTD27EQ = 0.00; //Total Amount of Tax for Form 27EQ present in TD
	private String totalTax24Q; //Total amount of Tax as present for 24Q in Batch
	private String totalTax26Q; //Total amount of Tax as present for 26Q in Batch
	private String totalTax27Q; //Total amount of Tax as present for 27Q in Batch
	private String totalTax27EQ; //Total amount of Tax as present for 27EQ in Batch
	private double totalRemittanceTD24Q = 0.00; //Total Amount of Tax for Form 24Q present in TD
	private double totalRemittanceTD26Q = 0.00; //Total Amount of Tax for Form 26Q present in TD
	private double totalRemittanceTD27Q = 0.00; //Total Amount of Tax for Form 27Q present in TD
	private double totalRemittanceTD27EQ = 0.00;//Total Amount of Tax for Form 27EQ present in TD
	
	
	private int    totalTDAddedIn24Q = 0;    //sum of all Added  TD's in 24Q 
	private int    totalTDDeletedIn24Q = 0;    //sum of all deleted TD's in 24Q
	private int    totalTDUpdatedIn24Q=0;    //sum of all updated TD's in 24Q
	private double totalTaxAddedTD24Q = 0.00; //Sum of Total tax in TD for 24Q for Add Mode
	private double remittedAmtAddedTD24Q = 0.00;  //Sum of Remitted Amount in TD for 24Q for Add Mode
	private double totalTaxDeletedTD24Q = 0.00; //Sum of Total tax in TD for 24Q for Delete Mode
	private double remittedAmtDeletedTD24Q = 0.00;  //Sum of Remitted Amount in TD for 24Q for Delete Mode
	private double totalTaxUpdatedTD24Q = 0.00; //Sum of Total tax in TD for 24Q for Update Mode
	private double totalLastTaxUpdatedTD24Q = 0.00; //Sum of Total tax in TD for 24Q for Update Mode
	private double remittedAmtUpdatedTD24Q = 0.00;  //Sum of Remitted Amount in TD for 24Q for Update Mode
	
	private int    totalTDAddedIn26Q = 0;    //sum of all Added  TD's in 26Q 
	private int    totalTDDeletedIn26Q = 0;    //sum of all deleted TD's in 26Q
	private int    totalTDUpdatedIn26Q=0;    //sum of all updated TD's in 26Q
	private double totalTaxAddedTD26Q = 0.00; //Sum of Total tax in TD for 26Q for Add Mode
	private double remittedAmtAddedTD26Q = 0.00;  //Sum of Remitted Amount in TD for 26Q for Add Mode
	private double totalTaxDeletedTD26Q = 0.00; //Sum of Total tax in TD for 26Q for Delete Mode
	private double remittedAmtDeletedTD26Q = 0.00;  //Sum of Remitted Amount in TD for 26Q for Delete Mode
	private double totalTaxUpdatedTD26Q = 0.00; //Sum of Total tax in TD for 26Q for Update Mode
	private double totalLastTaxUpdatedTD26Q = 0.00; //Sum of Total tax in TD for 26Q for Update Mode
	private double remittedAmtUpdatedTD26Q = 0.00;  //Sum of Remitted Amount in TD for 26Q for Update Mode
	
	
	private int    totalTDAddedIn27Q = 0;    //sum of all Added  TD's in 27Q 
	private int    totalTDDeletedIn27Q = 0;    //sum of all deleted TD's in 27Q
	private int    totalTDUpdatedIn27Q=0;    //sum of all updated TD's in 27Q
	private double totalTaxAddedTD27Q = 0.00; //Sum of Total tax in TD for 27Q for Add Mode
	private double remittedAmtAddedTD27Q = 0.00;  //Sum of Remitted Amount in TD for 27Q for Add Mode
	private double totalTaxDeletedTD27Q = 0.00; //Sum of Total tax in TD for 27Q for Delete Mode
	private double remittedAmtDeletedTD27Q = 0.00;  //Sum of Remitted Amount in TD for 27Q for Delete Mode
	private double totalTaxUpdatedTD27Q = 0.00; //Sum of Total tax in TD for 27Q for Update Mode
	private double totalLastTaxUpdatedTD27Q = 0.00; //Sum of Total tax in TD for 27Q for Update Mode
	private double remittedAmtUpdatedTD27Q = 0.00;  //Sum of Remitted Amount in TD for 27Q for Update Mode
	
	
	private int    totalTDAddedIn27EQ = 0;    //sum of all Added  TD's in 27EQ 
	private int    totalTDDeletedIn27EQ = 0;    //sum of all deleted TD's in 27EQ
	private int    totalTDUpdatedIn27EQ=0;    //sum of all updated TD's in 27EQ
	private double totalTaxAddedTD27EQ = 0.00; //Sum of Total tax in TD for 27EQ for Add Mode
	private double remittedAmtAddedTD27EQ = 0.00;  //Sum of Remitted Amount in TD for 27EQ for Add Mode
	private double totalTaxDeletedTD27EQ = 0.00; //Sum of Total tax in TD for 27EQ for Delete Mode
	private double remittedAmtDeletedTD27EQ = 0.00;  //Sum of Remitted Amount in TD for 27EQ for Delete Mode
	private double totalTaxUpdatedTD27EQ = 0.00; //Sum of Total tax in TD for 27EQ for Update Mode
	private double totalLastTaxUpdatedTD27EQ = 0.00; //Sum of Total tax in TD for 24Q for Update Mode
	private double remittedAmtUpdatedTD27EQ = 0.00;  //Sum of Remitted Amount in TD for 27EQ for Update Mode
	
	
	
	private String totalRemittedAmt24Q; //Total amount of Tax as present for 24Q in Batch
	private String totalRemittedAmt26Q; //Total amount of Tax as present for 26Q in Batch
	private String totalRemittedAmt27Q; //Total amount of Tax as present for 27Q in Batch
	private String totalRemittedAmt27EQ; //Total amount of Tax as present for 27EQ in Batch
	private String countDDOAdd;          //Total no. of TD in 'A' mode in Batch
	private String countDDOUpdated;      //Total no. of TD in 'U' mode in Batch
	private String countDDODeleted;      //Total no. of TD in 'D' mode in Batch
	private int countDDOTDAdd = 0;       //Total no. of TD in mode 'A' in TD
	private int countDDOTDUpdated = 0;   //Total no. of TD in mode 'U' in TD
	private int countDDOTDDeleted = 0;   //Total no. of TD in mode 'D' in TD
    private String distinctDDOCount;     //Count of Distinct DDO's in Batch
    private String totalRemittedAmt;    //Total  TDS/TCS remitted to Government account (AG/Pr CCA) in Batch
    private int countLastTANINVALID = 0;    //Total no of Last TANINVALID in TD
    private int countLastTANNOTAVBL = 0;     //Total no of Last TANNOTAVBL in TD
    private int countLastTANAPPLIED = 0;    //Total no of Last TANAPPLIED in TD
    private int countTANINVALID = 0;    //Total no of TANINVALID in TD
    private int countTANNOTAVBL = 0;     //Total no of TANNOTAVBL in TD
    private int countTANAPPLIED = 0;    //Total no of TANAPPLIED in TD
    public HashMap tanDDOMappingHM = new HashMap();
    public HashSet tanDDOMapNull = new HashSet();  //For storing tan with null DDO Mapping/Update flag
    private int tdRecordZeroTaxExD;  //Transaction Detail Records with TDS/TCS transferred amount (0.00) (excluding records with mode D)[For Statistics File]   
    
    //public double totalRemittedTDAmt = 0.00 ; //Total  TDS/TCS remitted to Government account (AG/Pr CCA) in TD (is used for comparing in Format Validator  
        
    public String getCountTD24Q() {
		return countTD24Q;
	}
	public void setCountTD24Q(String countTD24Q) {
		this.countTD24Q = countTD24Q;
	}
	public String getCountTD26Q() {
		return countTD26Q;
	}
	public void setCountTD26Q(String countTD26Q) {
		this.countTD26Q = countTD26Q;
	}
	public String getCountTD27Q() {
		return countTD27Q;
	}
	public void setCountTD27Q(String countTD27Q) {
		this.countTD27Q = countTD27Q;
	}
	public String getCountTD27EQ() {
		return countTD27EQ;
	}
	public void setCountTD27EQ(String countTD27EQ) {
		this.countTD27EQ = countTD27EQ;
	}
	
	
	
	
	
	public int getCountLastNatOfDed24Q() {
		return countLastNatOfDed24Q;
	}
	public void setCountLastNatOfDed24Q(int countLastNatOfDed24Q) {
		this.countLastNatOfDed24Q = countLastNatOfDed24Q;
	}
	public int getCountLastNatOfDed26Q() {
		return countLastNatOfDed26Q;
	}
	public void setCountLastNatOfDed26Q(int countLastNatOfDed26Q) {
		this.countLastNatOfDed26Q = countLastNatOfDed26Q;
	}
	public int getCountLastNatOfDed27Q() {
		return countLastNatOfDed27Q;
	}
	public void setCountLastNatOfDed27Q(int countLastNatOfDed27Q) {
		this.countLastNatOfDed27Q = countLastNatOfDed27Q;
	}
	public int getCountLastNatOfDed27EQ() {
		return countLastNatOfDed27EQ;
	}
	public void setCountLastNatOfDed27EQ(int countLastNatOfDed27EQ) {
		this.countLastNatOfDed27EQ = countLastNatOfDed27EQ;
	}
	public int getCountNatOfDed24Q() {
		return countNatOfDed24Q;
	}
	public void setCountNatOfDed24Q(int countNatOfDed24Q) {
		this.countNatOfDed24Q = countNatOfDed24Q;
	}
	public int getCountNatOfDed26Q() {
		return countNatOfDed26Q;
	}
	public void setCountNatOfDed26Q(int countNatOfDed26Q) {
		this.countNatOfDed26Q = countNatOfDed26Q;
	}
	public int getCountNatOfDed27Q() {
		return countNatOfDed27Q;
	}
	public void setCountNatOfDed27Q(int countNatOfDed27Q) {
		this.countNatOfDed27Q = countNatOfDed27Q;
	}
	public int getCountNatOfDed27EQ() {
		return countNatOfDed27EQ;
	}
	public void setCountNatOfDed27EQ(int countNatOfDed27EQ) {
		this.countNatOfDed27EQ = countNatOfDed27EQ;
	}
	public double getTotalTaxTD24Q() {
		return totalTaxTD24Q;
	}
	public void setTotalTaxTD24Q(double totalTaxTD24Q) {
		this.totalTaxTD24Q = totalTaxTD24Q;
	}
	public double getTotalTaxTD26Q() {
		return totalTaxTD26Q;
	}
	public void setTotalTaxTD26Q(double totalTaxTD26Q) {
		this.totalTaxTD26Q = totalTaxTD26Q;
	}
	public double getTotalTaxTD27Q() {
		return totalTaxTD27Q;
	}
	public void setTotalTaxTD27Q(double totalTaxTD27Q) {
		this.totalTaxTD27Q = totalTaxTD27Q;
	}
	public double getTotalTaxTD27EQ() {
		return totalTaxTD27EQ;
	}
	public void setTotalTaxTD27EQ(double totalTaxTD27EQ) {
		this.totalTaxTD27EQ = totalTaxTD27EQ;
	}
	public String getTotalTax24Q() {
		return totalTax24Q;
	}
	
	
	
	
	public double getTotalLastTaxUpdatedTD24Q() {
		return totalLastTaxUpdatedTD24Q;
	}
	public void setTotalLastTaxUpdatedTD24Q(double totalLastTaxUpdatedTD24Q) {
		this.totalLastTaxUpdatedTD24Q = totalLastTaxUpdatedTD24Q;
	}
	public double getTotalLastTaxUpdatedTD26Q() {
		return totalLastTaxUpdatedTD26Q;
	}
	public void setTotalLastTaxUpdatedTD26Q(double totalLastTaxUpdatedTD26Q) {
		this.totalLastTaxUpdatedTD26Q = totalLastTaxUpdatedTD26Q;
	}
	public double getTotalLastTaxUpdatedTD27Q() {
		return totalLastTaxUpdatedTD27Q;
	}
	public void setTotalLastTaxUpdatedTD27Q(double totalLastTaxUpdatedTD27Q) {
		this.totalLastTaxUpdatedTD27Q = totalLastTaxUpdatedTD27Q;
	}
	public double getTotalLastTaxUpdatedTD27EQ() {
		return totalLastTaxUpdatedTD27EQ;
	}
	public void setTotalLastTaxUpdatedTD27EQ(double totalLastTaxUpdatedTD27EQ) {
		this.totalLastTaxUpdatedTD27EQ = totalLastTaxUpdatedTD27EQ;
	}
	public void setTotalTax24Q(String totalTax24Q) {
		this.totalTax24Q = totalTax24Q;
	}
	public String getTotalTax26Q() {
		return totalTax26Q;
	}
	public void setTotalTax26Q(String totalTax26Q) {
		this.totalTax26Q = totalTax26Q;
	}
	public String getTotalTax27Q() {
		return totalTax27Q;
	}
	public void setTotalTax27Q(String totalTax27Q) {
		this.totalTax27Q = totalTax27Q;
	}
	public String getTotalTax27EQ() {
		return totalTax27EQ;
	}
	public void setTotalTax27EQ(String totalTax27EQ) {
		this.totalTax27EQ = totalTax27EQ;
	}
	public double getTotalRemittanceTD24Q() {
		return totalRemittanceTD24Q;
	}
	public void setTotalRemittanceTD24Q(double totalRemittanceTD24Q) {
		this.totalRemittanceTD24Q = totalRemittanceTD24Q;
	}
	public double getTotalRemittanceTD26Q() {
		return totalRemittanceTD26Q;
	}
	public void setTotalRemittanceTD26Q(double totalRemittanceTD26Q) {
		this.totalRemittanceTD26Q = totalRemittanceTD26Q;
	}
	public double getTotalRemittanceTD27Q() {
		return totalRemittanceTD27Q;
	}
	public void setTotalRemittanceTD27Q(double totalRemittanceTD27Q) {
		this.totalRemittanceTD27Q = totalRemittanceTD27Q;
	}
	public double getTotalRemittanceTD27EQ() {
		return totalRemittanceTD27EQ;
	}
	public void setTotalRemittanceTD27EQ(double totalRemittanceTD27EQ) {
		this.totalRemittanceTD27EQ = totalRemittanceTD27EQ;
	}
	public String getTotalRemittedAmt24Q() {
		return totalRemittedAmt24Q;
	}
	public void setTotalRemittedAmt24Q(String totalRemittedAmt24Q) {
		this.totalRemittedAmt24Q = totalRemittedAmt24Q;
	}
	public String getTotalRemittedAmt26Q() {
		return totalRemittedAmt26Q;
	}
	public void setTotalRemittedAmt26Q(String totalRemittedAmt26Q) {
		this.totalRemittedAmt26Q = totalRemittedAmt26Q;
	}
	public String getTotalRemittedAmt27Q() {
		return totalRemittedAmt27Q;
	}
	public void setTotalRemittedAmt27Q(String totalRemittedAmt27Q) {
		this.totalRemittedAmt27Q = totalRemittedAmt27Q;
	}
	public String getTotalRemittedAmt27EQ() {
		return totalRemittedAmt27EQ;
	}
	public void setTotalRemittedAmt27EQ(String totalRemittedAmt27EQ) {
		this.totalRemittedAmt27EQ = totalRemittedAmt27EQ;
	}
	public String getCountDDOAdd() {
		return countDDOAdd;
	}
	public void setCountDDOAdd(String countDDOAdd) {
		this.countDDOAdd = countDDOAdd;
	}
	public String getCountDDOUpdated() {
		return countDDOUpdated;
	}
	public void setCountDDOUpdated(String countDDOUpdated) {
		this.countDDOUpdated = countDDOUpdated;
	}
	public String getCountDDODeleted() {
		return countDDODeleted;
	}
	public void setCountDDODeleted(String countDDODeleted) {
		this.countDDODeleted = countDDODeleted;
	}
	public int getCountDDOTDAdd() {
		return countDDOTDAdd;
	}
	public void setCountDDOTDAdd(int countDDOTDAdd) {
		this.countDDOTDAdd = countDDOTDAdd;
	}
	public int getCountDDOTDUpdated() {
		return countDDOTDUpdated;
	}
	public void setCountDDOTDUpdated(int countDDOTDUpdated) {
		this.countDDOTDUpdated = countDDOTDUpdated;
	}
	public int getCountDDOTDDeleted() {
		return countDDOTDDeleted;
	}
	public void setCountDDOTDDeleted(int countDDOTDDeleted) {
		this.countDDOTDDeleted = countDDOTDDeleted;
	}
	public String getDistinctDDOCount() {
		return distinctDDOCount;
	}
	public void setDistinctDDOCount(String distinctDDOCount) {
		this.distinctDDOCount = distinctDDOCount;
	}
	public String getTotalRemittedAmt() {
		return totalRemittedAmt;
	}
	public void setTotalRemittedAmt(String totalRemittedAmt) {
		this.totalRemittedAmt = totalRemittedAmt;
	}
	
	
	public int getCountLastTANINVALID() {
		return countLastTANINVALID;
	}
	public void setCountLastTANINVALID(int countLastTANINVALID) {
		this.countLastTANINVALID = countLastTANINVALID;
	}
	public int getCountLastTANNOTAVBL() {
		return countLastTANNOTAVBL;
	}
	public void setCountLastTANNOTAVBL(int countLastTANNOTAVBL) {
		this.countLastTANNOTAVBL = countLastTANNOTAVBL;
	}
	public int getCountLastTANAPPLIED() {
		return countLastTANAPPLIED;
	}
	public void setCountLastTANAPPLIED(int countLastTANAPPLIED) {
		this.countLastTANAPPLIED = countLastTANAPPLIED;
	}
	public int getCountTANINVALID() {
		return countTANINVALID;
	}
	public void setCountTANINVALID(int countTANINVALID) {
		this.countTANINVALID = countTANINVALID;
	}
	public int getCountTANNOTAVBL() {
		return countTANNOTAVBL;
	}
	public void setCountTANNOTAVBL(int countTANNOTAVBL) {
		this.countTANNOTAVBL = countTANNOTAVBL;
	}
	public int getCountTANAPPLIED() {
		return countTANAPPLIED;
	}
	public void setCountTANAPPLIED(int countTANAPPLIED) {
		this.countTANAPPLIED = countTANAPPLIED;
	}
	
	public int getTdRecordZeroTaxExD() {
		return tdRecordZeroTaxExD;
	}
	public void setTdRecordZeroTaxExD(int tdRecordZeroTaxExD) {
		this.tdRecordZeroTaxExD = tdRecordZeroTaxExD;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public int getTotalTDUpdatedIn24Q() {
		return totalTDUpdatedIn24Q;
	}
	public void setTotalTDUpdatedIn24Q(int totalTDUpdatedIn24Q) {
		this.totalTDUpdatedIn24Q = totalTDUpdatedIn24Q;
	}
	public double getTotalTaxUpdatedTD24Q() {
		return totalTaxUpdatedTD24Q;
	}
	public void setTotalTaxUpdatedTD24Q(double totalTaxUpdatedTD24Q) {
		this.totalTaxUpdatedTD24Q = totalTaxUpdatedTD24Q;
	}
	public double getRemittedAmtUpdatedTD24Q() {
		return remittedAmtUpdatedTD24Q;
	}
	public void setRemittedAmtUpdatedTD24Q(double remittedAmtUpdatedTD24Q) {
		this.remittedAmtUpdatedTD24Q = remittedAmtUpdatedTD24Q;
	}
	public int getTotalTDUpdatedIn26Q() {
		return totalTDUpdatedIn26Q;
	}
	public void setTotalTDUpdatedIn26Q(int totalTDUpdatedIn26Q) {
		this.totalTDUpdatedIn26Q = totalTDUpdatedIn26Q;
	}
	public double getTotalTaxUpdatedTD26Q() {
		return totalTaxUpdatedTD26Q;
	}
	public void setTotalTaxUpdatedTD26Q(double totalTaxUpdatedTD26Q) {
		this.totalTaxUpdatedTD26Q = totalTaxUpdatedTD26Q;
	}
	public double getRemittedAmtUpdatedTD26Q() {
		return remittedAmtUpdatedTD26Q;
	}
	public void setRemittedAmtUpdatedTD26Q(double remittedAmtUpdatedTD26Q) {
		this.remittedAmtUpdatedTD26Q = remittedAmtUpdatedTD26Q;
	}
	public int getTotalTDUpdatedIn27Q() {
		return totalTDUpdatedIn27Q;
	}
	public void setTotalTDUpdatedIn27Q(int totalTDUpdatedIn27Q) {
		this.totalTDUpdatedIn27Q = totalTDUpdatedIn27Q;
	}
	public double getTotalTaxUpdatedTD27Q() {
		return totalTaxUpdatedTD27Q;
	}
	public void setTotalTaxUpdatedTD27Q(double totalTaxUpdatedTD27Q) {
		this.totalTaxUpdatedTD27Q = totalTaxUpdatedTD27Q;
	}
	public double getRemittedAmtUpdatedTD27Q() {
		return remittedAmtUpdatedTD27Q;
	}
	public void setRemittedAmtUpdatedTD27Q(double remittedAmtUpdatedTD27Q) {
		this.remittedAmtUpdatedTD27Q = remittedAmtUpdatedTD27Q;
	}
	public int getTotalTDUpdatedIn27EQ() {
		return totalTDUpdatedIn27EQ;
	}
	public void setTotalTDUpdatedIn27EQ(int totalTDUpdatedIn27EQ) {
		this.totalTDUpdatedIn27EQ = totalTDUpdatedIn27EQ;
	}
	public double getTotalTaxUpdatedTD27EQ() {
		return totalTaxUpdatedTD27EQ;
	}
	public void setTotalTaxUpdatedTD27EQ(double totalTaxUpdatedTD27EQ) {
		this.totalTaxUpdatedTD27EQ = totalTaxUpdatedTD27EQ;
	}
	public double getRemittedAmtUpdatedTD27EQ() {
		return remittedAmtUpdatedTD27EQ;
	}
	public void setRemittedAmtUpdatedTD27EQ(double remittedAmtUpdatedTD27EQ) {
		this.remittedAmtUpdatedTD27EQ = remittedAmtUpdatedTD27EQ;
	}
	public int getTotalTDAddedIn24Q() {
		return totalTDAddedIn24Q;
	}
	public void setTotalTDAddedIn24Q(int totalTDAddedIn24Q) {
		this.totalTDAddedIn24Q = totalTDAddedIn24Q;
	}
	public int getTotalTDDeletedIn24Q() {
		return totalTDDeletedIn24Q;
	}
	public void setTotalTDDeletedIn24Q(int totalTDDeletedIn24Q) {
		this.totalTDDeletedIn24Q = totalTDDeletedIn24Q;
	}
	public int getTotalTDAddedIn26Q() {
		return totalTDAddedIn26Q;
	}
	public void setTotalTDAddedIn26Q(int totalTDAddedIn26Q) {
		this.totalTDAddedIn26Q = totalTDAddedIn26Q;
	}
	public int getTotalTDDeletedIn26Q() {
		return totalTDDeletedIn26Q;
	}
	public void setTotalTDDeletedIn26Q(int totalTDDeletedIn26Q) {
		this.totalTDDeletedIn26Q = totalTDDeletedIn26Q;
	}
	public int getTotalTDAddedIn27Q() {
		return totalTDAddedIn27Q;
	}
	public void setTotalTDAddedIn27Q(int totalTDAddedIn27Q) {
		this.totalTDAddedIn27Q = totalTDAddedIn27Q;
	}
	public int getTotalTDDeletedIn27Q() {
		return totalTDDeletedIn27Q;
	}
	public void setTotalTDDeletedIn27Q(int totalTDDeletedIn27Q) {
		this.totalTDDeletedIn27Q = totalTDDeletedIn27Q;
	}
	public int getTotalTDAddedIn27EQ() {
		return totalTDAddedIn27EQ;
	}
	public void setTotalTDAddedIn27EQ(int totalTDAddedIn27EQ) {
		this.totalTDAddedIn27EQ = totalTDAddedIn27EQ;
	}
	public int getTotalTDDeletedIn27EQ() {
		return totalTDDeletedIn27EQ;
	}
	public void setTotalTDDeletedIn27EQ(int totalTDDeletedIn27EQ) {
		this.totalTDDeletedIn27EQ = totalTDDeletedIn27EQ;
	}
	public double getTotalTaxAddedTD24Q() {
		return totalTaxAddedTD24Q;
	}
	public void setTotalTaxAddedTD24Q(double totalTaxAddedTD24Q) {
		this.totalTaxAddedTD24Q = totalTaxAddedTD24Q;
	}
	public double getRemittedAmtAddedTD24Q() {
		return remittedAmtAddedTD24Q;
	}
	public void setRemittedAmtAddedTD24Q(double remittedAmtAddedTD24Q) {
		this.remittedAmtAddedTD24Q = remittedAmtAddedTD24Q;
	}
	public double getTotalTaxDeletedTD24Q() {
		return totalTaxDeletedTD24Q;
	}
	public void setTotalTaxDeletedTD24Q(double totalTaxDeletedTD24Q) {
		this.totalTaxDeletedTD24Q = totalTaxDeletedTD24Q;
	}
	public double getRemittedAmtDeletedTD24Q() {
		return remittedAmtDeletedTD24Q;
	}
	public void setRemittedAmtDeletedTD24Q(double remittedAmtDeletedTD24Q) {
		this.remittedAmtDeletedTD24Q = remittedAmtDeletedTD24Q;
	}
	public double getTotalTaxAddedTD26Q() {
		return totalTaxAddedTD26Q;
	}
	public void setTotalTaxAddedTD26Q(double totalTaxAddedTD26Q) {
		this.totalTaxAddedTD26Q = totalTaxAddedTD26Q;
	}
	public double getRemittedAmtAddedTD26Q() {
		return remittedAmtAddedTD26Q;
	}
	public void setRemittedAmtAddedTD26Q(double remittedAmtAddedTD26Q) {
		this.remittedAmtAddedTD26Q = remittedAmtAddedTD26Q;
	}
	public double getTotalTaxDeletedTD26Q() {
		return totalTaxDeletedTD26Q;
	}
	public void setTotalTaxDeletedTD26Q(double totalTaxDeletedTD26Q) {
		this.totalTaxDeletedTD26Q = totalTaxDeletedTD26Q;
	}
	public double getRemittedAmtDeletedTD26Q() {
		return remittedAmtDeletedTD26Q;
	}
	public void setRemittedAmtDeletedTD26Q(double remittedAmtDeletedTD26Q) {
		this.remittedAmtDeletedTD26Q = remittedAmtDeletedTD26Q;
	}
	public double getTotalTaxAddedTD27Q() {
		return totalTaxAddedTD27Q;
	}
	public void setTotalTaxAddedTD27Q(double totalTaxAddedTD27Q) {
		this.totalTaxAddedTD27Q = totalTaxAddedTD27Q;
	}
	public double getRemittedAmtAddedTD27Q() {
		return remittedAmtAddedTD27Q;
	}
	public void setRemittedAmtAddedTD27Q(double remittedAmtAddedTD27Q) {
		this.remittedAmtAddedTD27Q = remittedAmtAddedTD27Q;
	}
	public double getTotalTaxDeletedTD27Q() {
		return totalTaxDeletedTD27Q;
	}
	public void setTotalTaxDeletedTD27Q(double totalTaxDeletedTD27Q) {
		this.totalTaxDeletedTD27Q = totalTaxDeletedTD27Q;
	}
	public double getRemittedAmtDeletedTD27Q() {
		return remittedAmtDeletedTD27Q;
	}
	public void setRemittedAmtDeletedTD27Q(double remittedAmtDeletedTD27Q) {
		this.remittedAmtDeletedTD27Q = remittedAmtDeletedTD27Q;
	}
	public double getTotalTaxAddedTD27EQ() {
		return totalTaxAddedTD27EQ;
	}
	public void setTotalTaxAddedTD27EQ(double totalTaxAddedTD27EQ) {
		this.totalTaxAddedTD27EQ = totalTaxAddedTD27EQ;
	}
	public double getRemittedAmtAddedTD27EQ() {
		return remittedAmtAddedTD27EQ;
	}
	public void setRemittedAmtAddedTD27EQ(double remittedAmtAddedTD27EQ) {
		this.remittedAmtAddedTD27EQ = remittedAmtAddedTD27EQ;
	}
	public double getTotalTaxDeletedTD27EQ() {
		return totalTaxDeletedTD27EQ;
	}
	public void setTotalTaxDeletedTD27EQ(double totalTaxDeletedTD27EQ) {
		this.totalTaxDeletedTD27EQ = totalTaxDeletedTD27EQ;
	}
	public double getRemittedAmtDeletedTD27EQ() {
		return remittedAmtDeletedTD27EQ;
	}
	public void setRemittedAmtDeletedTD27EQ(double remittedAmtDeletedTD27EQ) {
		this.remittedAmtDeletedTD27EQ = remittedAmtDeletedTD27EQ;
	}
	public BHTDCompBean()
    {
    	
    }
   /* public String getFinYear()
   {
	   Properties props = new Properties();

       //try retrieve data from file
          try {

          props.load(new FileInputStream("24GFVU.properties"));
           }

          //catch exception in case properties file does not exist

          catch(Exception e)
          {
          e.printStackTrace();
          }
          
          return (props.getProperty("year"));
   }   */
  
   
   
   /* public String getTransactionMonth()
   {
	   Properties props = new Properties();

       //try retrieve data from file
          try {

          props.load(new FileInputStream("24GFVU.properties"));
           }

          //catch exception in case properties file does not exist

          catch(Exception e)
          {
          e.printStackTrace();
          }
          return ( props.getProperty("month"));
   }  */

}
