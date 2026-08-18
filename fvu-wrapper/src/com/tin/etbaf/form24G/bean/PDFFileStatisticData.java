// added by Faizan for FVU 1.4

package com.tin.etbaf.form24G.bean;
import java.text.DecimalFormat;

public class PDFFileStatisticData {
	
	private static DecimalFormat df  = new DecimalFormat("#.00");
	private String transactionType="-";
	private int noOfDeductee =0;
	private String amtPaid="-";
	
	private String taxDeducted="-";
	private String taxDeposited="-";
	
	
	public String getTransactionType() {
		return transactionType;
	}
	public void setTransactionType(String transactionType) {
		if(transactionType!=null && !transactionType.equals(""))
		{
			this.transactionType = transactionType;
		}
	}
	
	public int getNoOfDeductee() {
		return noOfDeductee;
	}
	public void addnoOfDeductee(String noOfDeductee) {
		
		if(noOfDeductee!=null && !noOfDeductee.equals(""))
		{
			if(this.noOfDeductee==0)
			{
				this.noOfDeductee = Integer.parseInt(noOfDeductee);
			}
			else
			{
				this.noOfDeductee = this.noOfDeductee+Integer.parseInt(noOfDeductee);
			}
		}
	}
	
	public String getAmtPaid() {
		return amtPaid;
	}
	public void addamtPaid(String amtPaid) {
		
		if(amtPaid!=null && !amtPaid.equals(""))
		{
			if(this.amtPaid.equals("-"))
			{
				this.amtPaid = df.format(Double.parseDouble(amtPaid));
			}
			else
			{
				this.amtPaid = df.format(Double.parseDouble(this.amtPaid)+Double.parseDouble(amtPaid));
			}
		}
	}
	
	public String getTaxDeducted() {
		return taxDeducted;
	}
	public void addtaxDeducted(String taxDeducted) {
		
		if(taxDeducted!=null && !taxDeducted.equals(""))
		{
			if(this.taxDeducted.equals("-"))
			{
				this.taxDeducted = df.format(Double.parseDouble(taxDeducted));
			}
			else
			{
				this.taxDeducted = df.format(Double.parseDouble(this.taxDeducted)+Double.parseDouble(taxDeducted));
			}
		}
	}
	
	public String getTaxDeposited() {
		return taxDeposited;
	}
	public void addtaxDeposited(String taxDeposited) {
		
		if(taxDeposited!=null && !taxDeposited.equals(""))
		{
			if(this.taxDeposited.equals("-"))
			{
				this.taxDeposited = df.format(Double.parseDouble(taxDeposited));
			}
			else
			{
				this.taxDeposited = df.format(Double.parseDouble(this.taxDeposited)+Double.parseDouble(taxDeposited));
			}
		}
	}



}
