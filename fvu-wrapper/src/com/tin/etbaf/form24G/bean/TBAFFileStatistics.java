/**	
 *	Class: TBAFFileStatistics.java 
 */
package com.tin.etbaf.form24G.bean;
import java.io.Serializable;
/**
 *	This class contains all the variable daclarations and getter and setter 
 *	methods which will be passed as a string to the TBAFFileGenerator class. 	
 * 
 *	@author TCS
 *	@version 5 
 */
public class TBAFFileStatistics implements Serializable
{
	private String receiptNumber = "-"; // Provisional Receipt Number
	private String nameOfAO = "-"; // AO Name
	private String date = "-"; // Date Of Receipt
	private String AIN = "-"; //	AIN in Batch Record 
	private String natureOfDed = "-"; // Nature of Deduction
	private String deductorCat = "-"; // Deductor Category
	private String typeOfStatement = "-"; // Statement Type
	private String quarter = "-"; // Quarter
	private String monthOfTransaction = "-"; //Month of Transfer Voucher     //Added by Subhankar
	private String transactionType = "-"; // Transaction type
	private String financialYear = "-"; // Financial Year
	private String countOfTD = "-"; // Count of DDO Records                  
	private String countOfDistinctTD = "-"; // Count of DDO Records with distinct TAN    //Added by Subhankar
	private String totalTax = "-"; // Total Tax Amount
	private String oiginalRecptNo = "-"; // Original Receipt Number
	private String noOfLines = "-"; // Number of Lines in the '.txt' file
	private String ainTFCId = "-"; // AIN/Organization/TFC id
	private String zeroTDTaxAmtCounter = "-"; // Count of Number of DDO records with Tax Amount as '0.00'
	private String lastAIN = "-"; // Previous AIN
	private String lastNatureOfDed = "-"; // Previous Nature of Deduction
	private String lastDeductorCat = "-"; // Previous Deductor Category
	private String lastQuarter = "-"; // Previous Quarter
	private String lastFinancialYear = "-"; // Previous Financial Year
	private String aoAdd1 = "-"; //	AO Address 1
	private String aoAdd2 = "-"; //	AO Address 2
	private String aoAdd3 = "-"; //	AO Address 3
	private String aoAdd4 = "-"; //	AO Address 4
	private String aoCity = "-"; //	AO City
	private String aoState = "-"; //  AO State  
	private String aoPIN = "-"; // AO Pin Code
	private String fvuVersion = "-"; // FVU version
	private String fvuHash = "-"; // FVU hash
	private String samVersion = "-"; // SAM Version
	private String samHash = "-"; // SAM Hash
	private String uploadFee = "-"; // Upload Fee
	private String responsiblePersonname = "-"; // Responsible Person Name
	// Error File Fields
	private String tbafRecType = "-"; // Record Type
	private String lineNo = "-"; // Line Number
	private String batchNo = "-"; // Batch Number
	private String transactionNo = "-"; // Transaction Detail Number
	private String errorCode = "-"; // Error Code
	private String errorDescription = "-"; // Error Description
	/*
	 * Added By Subhankar
	 */
		
	private String ddoAdd = "-";
	private String ddoUpdate = "-";
	private String ddoDelete = "-";
	private String ministryName = "-";
	private String subMinistryName = "-";
	private String subMinistryName_O = "-";
	private String stateName = "-";
	private String count24Q = "-";
	private String totalTDSTCS24Q = "-";
	private String totalTDSTCSTransferred24Q = "-";
	private String count26Q = "-";
	private String totalTDSTCS26Q = "-";
	private String totalTDSTCSTransferred26Q = "-";
	private String count27Q = "-";
	private String totalTDSTCS27Q = "-";
	private String totalTDSTCSTransferred27Q = "-";
	private String count27EQ = "-";
	private String totalTDSTCS27EQ = "-";
	private String totalTDSTCSTransferred27EQ = "-";
	private String totalTDSTCSTransferred = "-";
	private String countOfDDOAdded = "-";
	private String countOfDDOUpdated = "-";
	private String countOfDDODeleted = "-";
	private String countOfValidTAN = "-";
	private String countOfTANAPPLIED = "-";
	private String countOfTANNOTABVL = "-";
	private String countOfTANINVALID = "-";
	private String countOfTdZeroTaxExD = "-";
	
	//Gauri added new fields for CR 89435 for FVU 1.9::START
	
	/*private String aoTitle = "-";
	private String aoFirstName = "-";
	private String aoMiddleName = "-";
	private String aoLastName = "-";
	private String mobileNoOfAO = "-";*/
	private String countryCode = "-";
	private String TANofAO = "-";
	private String specialTAN = "-";
	private String stateAGcode = "-";
	//private String rTitle = "-";
	private String rFirstName = "-";
	private String rMiddleName = "-";
	private String rLastName = "-";
	private String rCountryCode = "-";

//	//Gauri added new fields for CR 89435 for FVU 1.9::END
	
	//End of Added By subhankar
	
	public String getMinistryName() {
		return ministryName;
	}

	public void setMinistryName(String ministryName) {
		this.ministryName = ministryName;
	}
	
	

	public String getSubMinistryName() {
		return subMinistryName;
	}

	public void setSubMinistryName(String subMinistryName) {
		this.subMinistryName = subMinistryName;
	}

	
	
	public String getSubMinistryName_O() {
		return subMinistryName_O;
	}

	public void setSubMinistryName_O(String subMinistryNameO) {
		subMinistryName_O = subMinistryNameO;
	}

	public String getCount24Q() {
		return count24Q;
	}

	public void setCount24Q(String count24q) {
		count24Q = count24q;
	}
	

	public String getTotalTDSTCSTransferred24Q() {
		return totalTDSTCSTransferred24Q;
	}

	public void setTotalTDSTCSTransferred24Q(String totalTDSTCSTransferred24Q) {
		this.totalTDSTCSTransferred24Q = totalTDSTCSTransferred24Q;
	}

	
	public String getCount26Q() {
		return count26Q;
	}

	public void setCount26Q(String count26q) {
		count26Q = count26q;
	}

	public String getTotalTDSTCSTransferred26Q() {
		return totalTDSTCSTransferred26Q;
	}

	public void setTotalTDSTCSTransferred26Q(String totalTDSTCSTransferred26Q) {
		this.totalTDSTCSTransferred26Q = totalTDSTCSTransferred26Q;
	}

	public String getCount27Q() {
		return count27Q;
	}

	public void setCount27Q(String count27q) {
		count27Q = count27q;
	}

	public String getTotalTDSTCSTransferred27Q() {
		return totalTDSTCSTransferred27Q;
	}

	public void setTotalTDSTCSTransferred27Q(String totalTDSTCSTransferred27Q) {
		this.totalTDSTCSTransferred27Q = totalTDSTCSTransferred27Q;
	}

	public String getCount27EQ() {
		return count27EQ;
	}

	public void setCount27EQ(String count27eq) {
		count27EQ = count27eq;
	}

	public String getTotalTDSTCSTransferred27EQ() {
		return totalTDSTCSTransferred27EQ;
	}

	public void setTotalTDSTCSTransferred27EQ(String totalTDSTCSTransferred27EQ) {
		this.totalTDSTCSTransferred27EQ = totalTDSTCSTransferred27EQ;
	}
	
	
	

	public String getTotalTDSTCS24Q() {
		return totalTDSTCS24Q;
	}

	public void setTotalTDSTCS24Q(String totalTDSTCS24Q) {
		this.totalTDSTCS24Q = totalTDSTCS24Q;
	}

	public String getTotalTDSTCS26Q() {
		return totalTDSTCS26Q;
	}

	public void setTotalTDSTCS26Q(String totalTDSTCS26Q) {
		this.totalTDSTCS26Q = totalTDSTCS26Q;
	}

	public String getTotalTDSTCS27Q() {
		return totalTDSTCS27Q;
	}

	public void setTotalTDSTCS27Q(String totalTDSTCS27Q) {
		this.totalTDSTCS27Q = totalTDSTCS27Q;
	}

	public String getTotalTDSTCS27EQ() {
		return totalTDSTCS27EQ;
	}

	public void setTotalTDSTCS27EQ(String totalTDSTCS27EQ) {
		this.totalTDSTCS27EQ = totalTDSTCS27EQ;
	}
	
	

	public String getTotalTDSTCSTransferred() {
		return totalTDSTCSTransferred;
	}

	public void setTotalTDSTCSTransferred(String totalTDSTCSTransferred) {
		this.totalTDSTCSTransferred = totalTDSTCSTransferred;
	}

	public String getCountOfDDOAdded() {
		return countOfDDOAdded;
	}

	public void setCountOfDDOAdded(String countOfDDOAdded) {
		this.countOfDDOAdded = countOfDDOAdded;
	}

	public String getCountOfDDOUpdated() {
		return countOfDDOUpdated;
	}

	public void setCountOfDDOUpdated(String countOfDDOUpdated) {
		this.countOfDDOUpdated = countOfDDOUpdated;
	}

	public String getCountOfDDODeleted() {
		return countOfDDODeleted;
	}

	public void setCountOfDDODeleted(String countOfDDODeleted) {
		this.countOfDDODeleted = countOfDDODeleted;
	}

	
	
	public String getCountOfTdZeroTaxExD() {
		return countOfTdZeroTaxExD;
	}

	public void setCountOfTdZeroTaxExD(String countOfTdZeroTaxExD) {
		this.countOfTdZeroTaxExD = countOfTdZeroTaxExD;
	}

	public String getCountOfTANAPPLIED() {
		return countOfTANAPPLIED;
	}

	public void setCountOfTANAPPLIED(String countOfTANAPPLIED) {
		this.countOfTANAPPLIED = countOfTANAPPLIED;
	}

	
	
	public String getCountOfTANNOTABVL() {
		return countOfTANNOTABVL;
	}

	public void setCountOfTANNOTABVL(String countOfTANNOTABVL) {
		this.countOfTANNOTABVL = countOfTANNOTABVL;
	}

	public String getCountOfTANINVALID() {
		return countOfTANINVALID;
	}

	public void setCountOfTANINVALID(String countOfTANINVALID) {
		this.countOfTANINVALID = countOfTANINVALID;
	}

	
	
	public String getCountOfValidTAN() {
		return countOfValidTAN;
	}

	public void setCountOfValidTAN(String countOfValidTAN) {
		this.countOfValidTAN = countOfValidTAN;
	}

	public String getAIN()
	{
		return AIN;
	}
	
	public String getStateName() {
		return stateName;
	}

	public void setStateName(String stateName) {
		this.stateName = stateName;
	}

	public String getDdoAdd() {
		return ddoAdd;
	}

	
	public void setDdoAdd(String ddoAdd) {
		this.ddoAdd = ddoAdd;
	}

	public String getDdoUpdate() {
		return ddoUpdate;
	}

	public void setDdoUpdate(String ddoUpdate) {
		this.ddoUpdate = ddoUpdate;
	}

	public String getDdoDelete() {
		return ddoDelete;
	}

	public void setDdoDelete(String ddoDelete) {
		this.ddoDelete = ddoDelete;
	}

	public String getBatchNo()
	{
		return batchNo;
	}
	public String getMonthOfTransaction() {
		return monthOfTransaction;
	}
	public void setMonthOfTransaction(String monthOfTransaction) {
		this.monthOfTransaction = monthOfTransaction;
	}
	public String getCountOfDistinctTD()
	{
		return countOfDistinctTD;
	}
	public String getCountOfTD()
	{
		return countOfTD;
	}
	public String getDate()
	{
		return date;
	}
	public String getDeductorCat()
	{
		return deductorCat;
	}
	public String getErrorCode()
	{
		return errorCode;
	}
	public String getErrorDescription()
	{
		return errorDescription;
	}
	public String getFinancialYear()
	{
		return financialYear;
	}
	public String getLineNo()
	{
		return lineNo;
	}
	public String getNameOfAO()
	{
		return nameOfAO;
	}
	public String getNatureOfDed()
	{
		return natureOfDed;
	}
	public String getQuarter()
	{
		return quarter;
	}
	public String getReceiptNumber()
	{
		return receiptNumber;
	}
	

	public String getTbafRecType()
	{
		return tbafRecType;
	}
	public String getTransactionNo()
	{
		return transactionNo;
	}
	public String getTransactionType()
	{
		return transactionType;
	}
	public String getTypeOfStatement()
	{
		return typeOfStatement;
	}
	public String getUploadFee()
	{
		return uploadFee;
	}
	public String getResponsiblePersonName()
	{
		return responsiblePersonname;
	}
	public void setAIN(String string)
	{
		AIN = string;
	}
	public void setBatchNo(String string)
	{
		batchNo = string;
	}
	public void setCountOfDistinctTD(String string)
	{
		countOfDistinctTD = string;
	}
	public void setCountOfTD(String string)
	{
		countOfTD = string;
	}
	public void setDate(String string)
	{
		date = string;
	}
	public void setDeductorCat(String string)
	{
		deductorCat = string;
	}
	public void setErrorCode(String string)
	{
		errorCode = string;
	}
	public void setErrorDescription(String string)
	{
		errorDescription = string;
	}
	public void setFinancialYear(String string)
	{
		financialYear = string;
	}
	public void setLineNo(String string)
	{
		lineNo = string;
	}
	public void setNameOfAO(String string)
	{
		nameOfAO = string;
	}
	public void setNatureOfDed(String string)
	{
		natureOfDed = string;
	}
	public void setQuarter(String string)
	{
		quarter = string;
	}
	public void setReceiptNumber(String string)
	{
		receiptNumber = string;
	}
	public void setTbafRecType(String string)
	{
		tbafRecType = string;
	}
	public void setTransactionNo(String string)
	{
		transactionNo = string;
	}
	public void setTransactionType(String string)
	{
		transactionType = string;
	}
	public void setTypeOfStatement(String string)
	{
		typeOfStatement = string;
	}
	public void setUploadFee(String string)
	{
		uploadFee = string;
	}
	public String getTotalTax()
	{
		return totalTax;
	}
	public void setTotalTax(String string)
	{
		totalTax = string;
	}
	public String getOiginalRecptNo()
	{
		return oiginalRecptNo;
	}
	public void setOiginalRecptNo(String string)
	{
		oiginalRecptNo = string;
	}
	public String getAoAdd1()
	{
		return aoAdd1;
	}
	public String getAoAdd2()
	{
		return aoAdd2;
	}
	public String getAoAdd3()
	{
		return aoAdd3;
	}
	public String getAoAdd4()
	{
		return aoAdd4;
	}
	public String getAoCity()
	{
		return aoCity;
	}
	public void setAoAdd1(String string)
	{
		aoAdd1 = string;
	}
	public void setAoAdd2(String string)
	{
		aoAdd2 = string;
	}
	public void setAoAdd3(String string)
	{
		aoAdd3 = string;
	}
	public void setAoAdd4(String string)
	{
		aoAdd4 = string;
	}
	public void setAoCity(String string)
	{
		aoCity = string;
	}
	public String getNoOfLines()
	{
		return noOfLines;
	}
	public void setNoOfLines(String string)
	{
		noOfLines = string;
	}
	public String getAinTFCId()
	{
		return ainTFCId;
	}
	public void setAinTFCId(String string)
	{
		ainTFCId = string;
	}
	public String getAoPIN()
	{
		return aoPIN;
	}
	public String getAoState()
	{
		return aoState;
	}
	public void setAoPIN(String string)
	{
		aoPIN = string;
	}
	public void setAoState(String string)
	{
		aoState = string;
	}
	public String getFvuHash()
	{
		return fvuHash;
	}
	public String getFvuVersion()
	{
		return fvuVersion;
	}
	public String getSamHash()
	{
		return samHash;
	}
	public String getSamVersion()
	{
		return samVersion;
	}
	public void setFvuHash(String string)
	{
		fvuHash = string;
	}
	public void setFvuVersion(String string)
	{
		fvuVersion = string;
	}
	public void setSamHash(String string)
	{
		samHash = string;
	}
	public void setSamVersion(String string)
	{
		samVersion = string;
	}
	public String getLastAIN()
	{
		return lastAIN;
	}
	public String getLastDeductorCat()
	{
		return lastDeductorCat;
	}
	public String getLastFinancialYear()
	{
		return lastFinancialYear;
	}
	public String getLastNatureOfDed()
	{
		return lastNatureOfDed;
	}
	public String getLastQuarter()
	{
		return lastQuarter;
	}
	public void setLastAIN(String string)
	{
		lastAIN = string;
	}
	public void setLastDeductorCat(String string)
	{
		lastDeductorCat = string;
	}
	public void setLastFinancialYear(String string)
	{
		lastFinancialYear = string;
	}
	public void setLastNatureOfDed(String string)
	{
		lastNatureOfDed = string;
	}
	public void setLastQuarter(String string)
	{
		lastQuarter = string;
	}
	public String getzeroTDTaxAmtCounter()
	{
		return zeroTDTaxAmtCounter;
	}
	public void zeroTDTaxAmtCounter(String string)
	{
		zeroTDTaxAmtCounter = string;
	}
	public void setResponsiblePersonName(String string)
	{
		responsiblePersonname = string;
	}
	
	//Gauri added for CR 89435

	
	/*public String getaoFirstName() {
		return aoFirstName;
	}
	public void setaoFirstName(String string)
	{
		aoFirstName = string;
	}
	
	public String getaoMiddleName() {
		return aoMiddleName;
	}
	public void setaoMiddleName(String string)
	{
		aoMiddleName = string;
	}
	
	public String getaoLastName() {
		return aoLastName;
	}
	public void setaoLastName(String string)
	{
		aoLastName = string;
	}
	
	public String getmobileNoOfAO() {
		return mobileNoOfAO;
	}
	public void setmobileNoOfAO(String string)
	{
		mobileNoOfAO = string;
	}*/
	
	public String getTANofAO() {
		return TANofAO;
	}
	public void setTANofAO(String string)
	{
		TANofAO = string;
	}
	
	public String getspecialTAN() {
		return specialTAN;
	}
	public void setspecialTAN(String string)
	{
		specialTAN = string;
	}
	
	public String getstateAGcode() {
		return stateAGcode;
	}
	public void setstateAGcode(String string)
	{
		stateAGcode = string;
	}
	
	/*public String getrTitle() {
		return rTitle;
	}
	public void setrTitle(String string)
	{
		rTitle = string;
	}*/
	
	
	public String getrFirstName() {
		return rFirstName;
	}
	public void setrFirstName(String string)
	{
		rFirstName = string;
	}
	
	public String getrMiddleName() {
		return rMiddleName;
	}
	public void setrMiddleName(String string)
	{
		rMiddleName = string;
	}
	
	public String getrLastName() {
		return rLastName;
	}
	public void setrLastName(String string)
	{
		rLastName = string;
	}
	
	public String getrCountryCode() {
		return rCountryCode;
	}
	public void setrCountryCode(String string)
	{
		rCountryCode = string;
	}
	
/*	public String getaoTitle() {
		return aoTitle;
	}
	public void setaoTitle(String string)
	{
		aoTitle = string;
	}*/
	
	public String getcountryCode() {
		return countryCode;
	}
	public void setcountryCode(String string)
	{
		countryCode = string;
	}
}
