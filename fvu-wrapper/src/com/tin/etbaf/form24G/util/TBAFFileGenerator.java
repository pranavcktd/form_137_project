/**
 * Class: TBAFFileGenerator.java
 */
package com.tin.etbaf.form24G.util;
import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.StringTokenizer;


import net.sourceforge.barbecue.Barcode;
import net.sourceforge.barbecue.BarcodeFactory;
import net.sourceforge.barbecue.BarcodeImageHandler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zefer.pd4ml.PD4Constants;
import org.zefer.pd4ml.PD4ML;

import com.tin.etbaf.form24G.bean.RawFileBean;
import com.tin.etbaf.form24G.bean.TBAFFileStatistics;
import com.tin.etbaf.form24G.fvu.BatchValidation;
import com.tin.etbaf.form24G.fvu.TBAFFormatValidator;
import com.tin.etbaf.form24G.fvu.TBAFInterface;

//Added for barcode generation FVU 4.1
import net.sourceforge.barbecue.Barcode;
import net.sourceforge.barbecue.BarcodeException;
import net.sourceforge.barbecue.BarcodeFactory;
import net.sourceforge.barbecue.BarcodeImageHandler;
import net.sourceforge.barbecue.output.OutputException;
/**
 *	This class is used for generating the e-TBAF Statement Statistics Report, TDS/TCS Book Adjustment Form
 *	at standalone FVU level and Provisional Receipt at STM level. This class gets the value of each fields
 *	to be displayed in the reports through StringBuffer.
 *
 *	@author TCS
 *	@version 15
 */
public class TBAFFileGenerator implements TBAFInterface
{
	static Logger log1 = LogManager.getLogger("TBAFLogging");
	Calendar cal = new GregorianCalendar();
	String tab = "^";
	RawFileBean rawFileBean = RawFileBean.getInstance();
	TBAFFileStatistics fStats=null;
	public void writeToFile(String ERROR_FILE, String text, int errRespFile) throws IOException
	{
		
		Log.tbaf_log.debug("writeToFile() Method Called");
		Log.tbaf_log.debug("File Name : " + ERROR_FILE + "\t Data Length : " + text.length());
		try
		{
			cal = new GregorianCalendar();
			if (text.trim().length() != 0)
			{
				PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(ERROR_FILE, false)));
				out.println(text);
				out.close();
				cal = new GregorianCalendar();
			}



			String imgPath=ERROR_FILE.substring(0,ERROR_FILE.lastIndexOf('\\')+1);
			
		    //InputStream is = ClassLoader.getSystemClassLoader().getResourceAsStream("etbaf/form24G/rp.PNG");
			
			//added by faizan
			InputStream is = ClassLoader.getSystemClassLoader().getResourceAsStream("images/rp.PNG");
			
			FileOutputStream fout = new FileOutputStream(imgPath+"rp.png");
			while(is.available()!=0)
			{
				fout.write(is.read());
			}
			fout.flush();
			fout.close();
			is.close();

		}
		catch (Exception e)
		{
			e.printStackTrace();
			Log.tbaf_log.error("Exception in writeToFile(String ERROR_FILE, String text, int errRespFile) method : " + e.toString());
		}
	}

	/**
	 *  Added by Bharath for Raw File Generation
	 *
	 *
	 *	This class is used for generating the 24G Raw File at STM level. This class returns the contents which is
	 *  to be written in the raw file
	 *
	 *	@author TCS
	 *	@version 15
	 */

	public String prepareRawFile()
	{
		StringBuffer str=new StringBuffer();
		for(int i=1; i<=RawFileBean.TOTAL_NUM_FLDS;i++)
		{
			if(i==RawFileBean.TOTAL_NUM_FLDS)
			{
				str.append(rawFileBean.getValue(i));
			}
			else
			{
				str.append(rawFileBean.getValue(i)+"^");
			}
		}
		return str.toString();

	}

	public void writeToFile(String fileName, String text, int errRespFile, boolean fileOpened) throws IOException
	{
		try
		{
			cal = new GregorianCalendar();
			if (text.trim().length() != 0)
			{
				PrintWriter out = null;
				if (fileOpened)
				{
					out = new PrintWriter(new BufferedWriter(new FileWriter(fileName, true)));
				}
				else
				{
					out = new PrintWriter(new BufferedWriter(new FileWriter(fileName, false)));
				}
				out.println(text);
				out.close();
				cal = new GregorianCalendar();
			}
		}
		catch (Exception e)
		{
			Log.tbaf_log.error("Exception in writeError/RSPToFile method : " + e.toString());
		}
	}
	
	// added by faizan for FVU 1.4
	public void writePdfFile(String temppdfFileName,String fileName, StringBuffer textBufferString) throws IOException
	{
		try
		{
			cal = new GregorianCalendar();
			String text = textBufferString.toString();
			textBufferString = new StringBuffer();
			String sImagePath=temppdfFileName.substring(0,temppdfFileName.lastIndexOf('\\')+1);
			//Added by faizan for FVU 1.4 
			//String sImagePath = "etbaf/form24G/rp.PNG";
			if (text.trim().length() != 0)
			{
				System.out.println(" Entering writePdfFile() Method " + cal.getTime());
				PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(temppdfFileName,false)));
				out.println(text);
				System.out.println(" Exiting writePdfFile() Method " + cal.getTime());
				out.close();
				out.flush();
			}
			File f;
			if(temppdfFileName.contains(".html"))
			{
				f = new File(sImagePath+fileName+".pdf");
			}
			else
			{
				f = new File(fileName);
			}
			File barcode = new File(sImagePath+"barcode.jpeg");
			java.io.FileOutputStream fos = new java.io.FileOutputStream(f);
			PD4ML pd4ml = new PD4ML();
			File fz = new File(temppdfFileName);
			java.io.FileInputStream fis = new java.io.FileInputStream(fz);
			InputStreamReader isr = new InputStreamReader( fis, "UTF-8" ); 
			URL base = new URL( "file:"+sImagePath );
			pd4ml.adjustHtmlWidth();
			pd4ml.fitPageVertically();
			pd4ml.setHtmlWidth(750);
			//Dimension d = pd4ml.changePageOrientation(PD4Constants.A4);
			//pd4ml.setPageSize(d);
			pd4ml.render( isr, fos,base );
			fz.delete();
			if(barcode.exists())
				barcode.delete();
		
		
		
		
		}
		catch (Exception e)
		{
			System.err.println("Exception in writePdfFile() method : ");e.printStackTrace();
		}
	}
	
	/**
	 *	Method for generating e-TBAF Statement Statistics Report.
	 *  This method is called from TBAFFVU.java. This method gets the value of all the fields which are appended
	 * 	in a StringBuffer Statistics Report which is stored in an ArrayList with '^' as a seperator.
	 *	The ArrayList is again converted to an array of string in which the data from the text file is stored.
	 * 	@param obj_FrmValidator-> object of TBAFFormatValidator class
	 * 	@param statFileName -> Name of the Statistics Report file to be generated including file path
	 *	@param fileName -> Name of the input file including file patjh
	 *	@param FVUVersion -> Version Number of the File Validation Utility
	 */
	public void generateStatisticFile(TBAFFormatValidator obj_FrmValidator, String statFileName, String fileName, String FVUVersion,String filehash)throws Exception
	{
		try
		{

			Log.tbaf_log.info("Inside generateStatisticFile Method ");
			ArrayList statisticDataList = parseString(obj_FrmValidator.getStatReportBuffer().toString(), "\n");
			Object statisticDataArray[] = statisticDataList.toArray();
			obj_FrmValidator.setStatReportBuffer(new StringBuffer());
			TBAFFileStatistics fStatistics = new TBAFFileStatistics();
			fStatistics = getStatisticFileDetail((String) statisticDataArray[0], "^", fStatistics);
			fStats=fStatistics;
			createStatisticFile(obj_FrmValidator,fStatistics, statFileName, fileName, FVUVersion, filehash);
		}
		catch (Exception e)
		{
			Log.tbaf_log.error("generateStatisticFile Method : " + e.toString());
		}
	}
	private boolean isLeap(int year)
	{
		return ((year % 4 == 0) && (year % 100 != 0 || year % 400 == 0));
	}

	/**
	 *	Method for generating TDS/TCS Book Adjustment Form.
	 *	This method is called from TBAFFVU.java. Data from FH,BH and TD are stored in seperate String Buffers.
	 *	Each record is stored as a String and the values are seperated using StringTokenizer with "^" as seperator.
	 *	All the data are appended to the string buffer and placed between the HTML tags.
	 *	@param fileName -> Name of the input file name with the path
	 *	@param seperator -> Caret "^" is passed as a string seperator
	 * 	@param transferVoucherFileName -> File name of the TBAF with the path
	 */
	public void generateTbafForm(String fileName, String seperator, String transferVoucherFileName)throws Exception
	{
		try
		{
			//InputStream is = ClassLoader.getSystemClassLoader().getResourceAsStream("etbaf/form24G/rp.PNG");
			//added by faizan for fvu 1.4
		/*	InputStream is = ClassLoader.getSystemClassLoader().getResourceAsStream("images/rp.PNG");
			String imagePath = fileName.substring(0,(fileName.lastIndexOf('\\')) + 1);
			System.out.println("The imagePath in generateTBAFForm is:"+imagePath);
			FileOutputStream fout =new FileOutputStream(imagePath+"rp.png");
			while(is.available()!= 0)
			{
				fout.write(is.read());

			}
			fout.flush();
			fout.close();
			is.close();
*/

			Log.tbaf_log.info("Inside generateTbafForm Method ");
			BufferedReader br = null;
			BufferedWriter bw = null;
			br = new BufferedReader(new FileReader(fileName));
			bw = new BufferedWriter(new FileWriter(transferVoucherFileName));
			String recstr = "";
			// String Buffers to store the values coming from the input file
			StringBuffer sb1 = new StringBuffer();
			StringBuffer sb2 = new StringBuffer();
			StringBuffer sb3 = new StringBuffer();
			StringBuffer sb4 = new StringBuffer();
			String fieldsBuffer = null; // String for storing the values coming from the StringBuffer
			String statementType = null; // Statement Type
			String transactionType = null; // Transaction Type
			String AIN = null; // AIN
			String aoName = null; // AO Name
			String aoAddress1 = null; // AO Address 1
			String aoAddress2 = null; // AO Address 2
			String aoAddress3 = null; // AO Address 3
			String aoAddress4 = null; // AO Address 4
			String aoCity = null; // AO City
			String aoState = null; // AO State
			String aoPinCode = null; // AO PIN Code
			String aoStdCode = null; // STD Code
			String aoPhoneNo = null; // Phone Nuber
			String personName = null; // Responsible Person Name
			String personDesig = null; // Responsible Person Designation
			//	String quarter = null; // Quarter  //Commented by Bharath
			String financialYear = null; // Financial Year
			String deductorCategoryCode = null; // Deductor Category Code
			String deductorCategoryAsInMap = null; // Deductor Category as specified in TBAFInterface
			String natureOfDeduction = null; // Nature Of Deduction
			String countOfDDORecords = null; // Count of DDO Records

			String totalTax = null; // Total Tax Amount
					String revisionMode = null; // Revision Mode  //Commented by Bharath
			String serialNo = null; // Serial Number
			//		String oldSerialNo = null; // Old Serial Number   //Commented by Bharath
			String ddoRegistrationNo = null; // DDO Registration Number  //Added by Bharath
			String ddoCode = null; // DDO Code  //Added by Bharath
			String ddoTAN = null; // DDO TAN
			String ddoName = null; // DDO Name
			String ddoAddress1 = null; // DDO Address 1
			String ddoAddress2 = null; // DDO Address 2
			String ddoAddress3 = null; // DDO Address 3
			String ddoAddress4 = null; // DDO Address 4
			String ddoCity = null; // DDO City
			String ddoPinCode = null; // DDO PIN Code
			String ddoEmailId = null; //DDO Email Id  //Added by Bharath
			String taxAmount = null; // Tax Amount of each TD
			String lastTaxAmount=null; //Last Tax Amount of each TD
			String amountRemitted = null; // Total TDS/TCS Remitted of each TD //Added by Bharath
			String lastAmtRemitted=null;//Total last TDS/TCS Remitted of each TD//Added by Aditya
			String totalRemitted = null; // Total TDS/TCS Remitted  //Added by Bharath
			String monthOfPayment = null; // Month of Payment
			String ddoStateName = null; // DDO State Name
			String stateName=null;  //Added by Bharath
			String ministryName=null;  //Added by Bharath
			String subMinistryName=null;  //Added by Bharath
			String subMinistryNameOthers=null;  //Added by Bharath
			String dayOfEnding= null; //Added by Bharath
			

			//Gauri newly added this fields ofr CR 89435
            
            String countryCode = null;
            String TANofAO = null;
            String specialTAN = null;
            String stateAGcode = null;
            String rFirstName = null;
            String rMiddleName = null;
            String rLastName = null;
            String rCountryCode = null;
			
			/*
			 * Creating HTML report for TDS/TCS Book Adjustment Form
			 */




			
			sb1.append("<HTML>\n");
			sb1.append("<HEAD>\n");
			sb1.append("<TITLE>Form 24G</TITLE>\n");			
			sb1.append("</HEAD>\n");
			sb1.append("<style type=\"text/css\">\n");
			sb1.append("td{font-size:12;font-family:\"Times New Roman\", Times, serif;}");
			sb1.append("textarea{font-size:12;font-family:\"Times New Roman\", Times, serif;}");
			sb1.append("input{font-size:12;font-family:\"Times New Roman\", Times, serif;}");
			sb1.append("</style>\n");

         
			sb1.append("<BODY> <div align=\"center\">\n");

			sb1.append("<TABLE  WIDTH=\"100%\" FRAME=\"BORDER\" RULES=\"NONE\" CELLSPACING=0 CELLPADDING=0 BORDERCOLOR=black style='border-collapse: collapse;'>\n");
			sb1.append("<TR>\n");
			sb1.append("<TD WIDTH=\"100%\" colspan=\"2\" STYLE=\"BORDER-BOTTOM:NONE\" >");
			if(Integer.parseInt(fStats.getFinancialYear()) < 2026) { 		//Gauri changes form name in HTML
				sb1.append("<CENTER><H2>Form 24G</H2></CENTER>");
			}
			else {
				sb1.append("<CENTER><H2>Form 137</H2></CENTER>");
			}
			String finYr="-";
			String nextYr="-";
			String financialYr="-";
			if(!(fStats.getFinancialYear().equals("-")||fStats.getFinancialYear().equals("^")))
			{
			finYr=fStats.getFinancialYear();
			nextYr=fStats.getFinancialYear().substring(2);
			nextYr=String.valueOf((Integer.parseInt(nextYr)+1));
				if((Integer.parseInt(nextYr))<10)
				{
					financialYr=finYr+"-0"+nextYr;
				}
				else
				{
					financialYr=finYr+"-"+nextYr;
				}
			}
			sb1.append("<B><H4>Details of Transfer Voucher for Financial Year "+financialYr+" and Month "+MONTH[Integer.parseInt(fStats.getMonthOfTransaction())]);
			sb1.append("</B></H4>");
			sb1.append("</TD>");
			sb1.append("</TR>");

			sb1.append("<TR>");
			
			sb1.append("<TD WIDTH=\"35%\">");
			sb1.append("<B>1. Type of Statement</B>");
			sb1.append("</TD>");
			sb1.append("<TD>");

			int count = 1;
			recstr = br.readLine(); // Reading File Header Record
			NewStringTokenizer str = new NewStringTokenizer(recstr, "^");
			while ((fieldsBuffer = str.nextToken()) != null)
			{
				if (count == 5)
				{

					statementType = fieldsBuffer;
					if (statementType.trim().equals(TBAF_TYPE_OF_STMT_ORIG))
					{
						sb1.append("<INPUT TYPE=TEXT style=\"font-size:12;border-style:solid;border-width:1\" READONLY VALUE= \"" + "Original" + "\">");
					}
					else if (statementType.trim().equals(TBAF_TYPE_OF_STMT_CORR))
					{
						sb1.append("<INPUT TYPE=TEXT style=\"font-size:12;border-style:solid;border-width:1\" READONLY VALUE= \"" + "Correction" + "\">");
					}


				}
				count++;
				if (count > 5)
				{
					break;
				}
			}
			sb1.append("</TD>");		
			sb1.append("</TR>");


			//Transaction Type will be Displayed for Correction Type Only

			if (statementType.trim().equals(TBAF_TYPE_OF_STMT_CORR))
			{
				sb1.append("<TR>");
				sb1.append("<TD WIDTH=\"45%\">");
				sb1.append("&nbsp;&nbsp;&nbsp;Type of Transaction");
				sb1.append("</TD>");
				sb1.append("<TD>");
				if(fStats.getTransactionType().equals("M"))
				{
				sb1.append("<INPUT TYPE=TEXT style=\"font-size:12;border-style:solid;border-width:1\" READONLY VALUE= \"" + "Modification-M" + "\">");
				}
				else if(fStats.getTransactionType().equals("X"))
				{
					sb1.append("<INPUT TYPE=TEXT style=\"font-size:12;border-style:solid;border-width:1\" READONLY VALUE= \"" + "Cancellation-X" + "\">");
				}
				sb1.append("</TD>");
				sb1.append("</TR>");
			}

			//End

			sb1.append("<TR>");
			sb1.append("<TD colspan=\"2\">");
			sb1.append("<B>2. Accounts Office Details</B>");
			sb1.append("</TD>");
			sb1.append("</TR>");
			sb1.append("<TR>");
			sb1.append("<TD WIDTH=\"45%\">");
			sb1.append("&nbsp;&nbsp;&nbsp;Accounts Office Identification Number(AIN)");
			sb1.append("</TD>");
			sb1.append("<TD VALIGN=BOTTOM >");
			// Reading Batch Header Record
			recstr = br.readLine();
			NewStringTokenizer strBH = new NewStringTokenizer(recstr, "^");
			count = 1;
			while ((fieldsBuffer = strBH.nextToken()) != null)
			{
				if (count == 4)
				{
					transactionType = fieldsBuffer;
				}
				if (count == 5)
				{
					AIN = fieldsBuffer;
				}
				if (count == 7)
				{
					aoName = fieldsBuffer;
				}
				if (count == 8)
				{
					aoAddress1 = fieldsBuffer;
				}
				if (count == 9)
				{
					aoAddress2 = fieldsBuffer;
				}
				if (count == 10)
				{
					aoAddress3 = fieldsBuffer;
				}
				if (count == 11)
				{
					aoAddress4 = fieldsBuffer;
				}
				if (count == 12)
				{
					aoCity = fieldsBuffer;
				}


				if (count == 13)
				{
					if (transactionType.trim().equals(TBAF_TRANSACTION_TYPE_C3))
					{
						aoState = fieldsBuffer;
					}
					else
					{
						aoState = TBAF_STATE_NAME[Integer.parseInt(fieldsBuffer.trim())];
					}
				}
				if (count == 14)
				{
					aoPinCode = fieldsBuffer;
				}
				if (count == 15)
				{
					aoStdCode = fieldsBuffer;
				}
				if (count == 16)
				{
					aoPhoneNo = fieldsBuffer;
				}
				if (count == 18)
				{
					personName = fieldsBuffer;
				}
				if (count == 19)
				{
					personDesig = fieldsBuffer;
				}
				if (count == 20)
				{
					financialYear = fieldsBuffer;
				}
				if (count == 22)
				{

					deductorCategoryCode = fieldsBuffer;
					if (deductorCategoryCode.trim().equals("A"))  //Added By SUBHANKAR as for central govt. deductor catagory is 'A' and for state govt. deductor catagory is 'S'
					{
						deductorCategoryAsInMap = (String)Parameters.GovtMap.get(fieldsBuffer.trim());

						//deductorCategory = deductorCategoryAsInArray;

					}
					else
					{
						deductorCategoryAsInMap = (String)Parameters.GovtMap.get(fieldsBuffer.trim());

						//deductorCategory = deductorCategoryAsInArray.substring(19, deductorCategoryAsInArray.length());

					}

				}


				/*			if (count == 24)
											{
												quarter = fieldsBuffer;

											}*/ //Commented by Bharath
				if (count == 26)
				{
					natureOfDeduction = fieldsBuffer;

				}
				if (count == 28)
				{
					countOfDDORecords = fieldsBuffer;

				}
				if (count == 29)
				{
					totalTax = fieldsBuffer;

				}
				if(count == 34)
				{
					monthOfPayment = fieldsBuffer;
				}
				if(count == 47)
				{
					stateName = fieldsBuffer;

				}  //Added by Bharath
				if(count == 48)
				{
					ministryName = fieldsBuffer;

				}  //Added by Bharath
				if(count == 49)
				{
					subMinistryName = fieldsBuffer;

				}  //Added by Bharath
				if(count == 50)
				{
					subMinistryNameOthers = fieldsBuffer;

				} //Added by Bharath
				if(count == 65)
				{
					totalRemitted =fieldsBuffer;
				} //Added by Bharath
				
				//Gauri added
//				
//				if (count == 25)
//				{
//					aoFirstName = fieldsBuffer;
//				}
//				if (count == 26)
//				{
//					aoMiddleName = fieldsBuffer;
//				}
//				if (count == 27)
//				{
//					aoLastName = fieldsBuffer;
//				}
				if (count == 46)
				{					
					countryCode = fieldsBuffer;
				}
//				if (count == 70)
//				{
//					aoTitle = fieldsBuffer;
//				}
//				if (count == 71)
//				{
//					mobileNoOfAO = fieldsBuffer;
//				}
				if (count == 72)
				{
					TANofAO = fieldsBuffer;
				}
				if (count == 73)
				{
					specialTAN = fieldsBuffer;
				}
				if (count == 74)
				{
					stateAGcode = fieldsBuffer;
				}
//				if (count == 75)
//				{
//					rTitle = fieldsBuffer;
//				}
				if (count == 76)
				{
					rFirstName = fieldsBuffer;
				}
				if (count == 77)
				{
					rMiddleName = fieldsBuffer;
				}
				if (count == 78)
				{
					rLastName = fieldsBuffer;
				}
				if (count == 79)
				{
					rCountryCode = fieldsBuffer;
				}


				count++;

				if (count > 80) //the count was 66
				{


					break;
				}
			}


			int financialYear1 = Integer.parseInt(financialYear.trim());
			financialYear1 = financialYear1 + 1;




			

			/*	if (quarter.trim().equals(TBAF_QUARTER1))
										{
											sb1.append("&nbsp;&nbsp;&nbsp;&nbsp;" + lastMonthOfQ1 + " " + financialYear);
										}
										else if (quarter.trim().equals(TBAF_QUARTER2))
										{
											sb1.append("&nbsp;&nbsp;&nbsp;&nbsp;" + lastMonthOfQ2 + " " + financialYear);
										}
										else if (quarter.trim().equals(TBAF_QUARTER3))
										{
											sb1.append("&nbsp;&nbsp;&nbsp;&nbsp;" + lastMonthOfQ3 + " " + financialYear);
										}
										else if (quarter.trim().equals(TBAF_QUARTER4))
										{
											sb1.append("&nbsp;&nbsp;&nbsp;&nbsp;" + lastMonthOfQ4 + " " + financialYr);
										}

			 */



			/*		sb1.append("</B>");
										sb1.append("</H3>");
										sb1.append("</TD>");
										sb1.append("</TR>"); 
										sb1.append("</TABLE>"); */
			bw.write(sb1.toString());
			Log.tbaf_log.info("Status of the details of File Header Record: Completed");

			
			Log.tbaf_log.debug("*****************  THE AIN  ******************"+AIN);
			
			bw.write(sb2.toString());
			sb2.delete(0, sb2.length());

			
			sb2.append("<INPUT TYPE=TEXT SIZE=1 style=\"border-style:solid;border-width:1;text-align:center\"  READONLY VALUE = \"" + AIN.substring(0, 1) + "\">");
			sb2.append("<INPUT TYPE=TEXT SIZE=1 style=\"border-style:solid;border-width:1;text-align:center\"  READONLY VALUE = \"" + AIN.substring(1, 2) + "\">");
			sb2.append("<INPUT TYPE=TEXT SIZE=1 style=\"border-style:solid;border-width:1;text-align:center\" READONLY VALUE = \"" + AIN.substring(2, 3) + "\">");
			sb2.append("<INPUT TYPE=TEXT SIZE=1 style=\"border-style:solid;border-width:1;text-align:center\" READONLY VALUE = \"" + AIN.substring(3, 4) + "\">");
			sb2.append("<INPUT TYPE=TEXT SIZE=1 style=\"border-style:solid;border-width:1;text-align:center\" READONLY VALUE = \"" + AIN.substring(4, 5) + "\">");
			sb2.append("<INPUT TYPE=TEXT SIZE=1 style=\"border-style:solid;border-width:1;text-align:center\" READONLY VALUE = \"" + AIN.substring(5, 6) + "\">");
			sb2.append("<INPUT TYPE=TEXT SIZE=1 style=\"border-style:solid;border-width:1;text-align:center\" READONLY VALUE = \"" + AIN.substring(6, 7) + "\">");
			sb2.append("</TD>");
			sb2.append("</TR>");

			sb2.append("<TR>");
			sb2.append("<TD WIDTH=\"35%\">");
			sb2.append("&nbsp;&nbsp;&nbsp;Accounts Office Name");
			sb2.append("</TD>");
			sb2.append("<TD>");
			sb2.append("<textarea rows=2 cols=75 readonly style=\"border-style:solid;border-width:1;font-size:12;overflow:hidden;\">");
			sb2.append(aoName.trim());
			sb2.append("</textarea></TD>");
			sb2.append("</TR>");


			if (transactionType.equals(TBAF_TRANSACTION_TYPE_C3))
			{
				sb2.append("<TR>");
				sb2.append("<TD colspan=\"2\">");
				sb2.append("<B>&nbsp;&nbsp;&nbsp;Accounts Office Address</B>");
				sb2.append("</TD>");
				sb2.append("</TR>");

				sb2.append("<TR>");
				sb2.append("<TD width=\"35%\">");
				sb2.append("&nbsp;&nbsp;&nbsp;Address Line 1");
				sb2.append("</TD>");
				sb2.append("<TD>");
				sb2.append("<INPUT TYPE=TEXT SIZE=70 style=\"font-size:12;border-style:solid;border-width:1\" READONLY VALUE = \"" + "" + "\">");
				sb2.append("</TD>");
				sb2.append("</TR>");

				sb2.append("<TR>");
				sb2.append("<TD width=\"35%\">");
				sb2.append("&nbsp;&nbsp;&nbsp;Address Line 2");
				sb2.append("</TD>");
				sb2.append("<TD>");
				sb2.append("<INPUT TYPE=TEXT SIZE=70 style=\"font-size:12;border-style:solid;border-width:1\" READONLY VALUE = \"" + "" + "\">");
				sb2.append("</TD>");
				sb2.append("</TR>");

				sb2.append("<TR>");
				sb2.append("<TD width=\"35%\">");
				sb2.append("&nbsp;&nbsp;&nbsp;Address Line 3");
				sb2.append("</TD>");
				sb2.append("<TD>");
				sb2.append("<INPUT TYPE=TEXT SIZE=70 style=\"font-size:12;border-style:solid;border-width:1\" READONLY VALUE = \"" + "" + "\">");
				sb2.append("</TD>");
				sb2.append("</TR>");

				sb2.append("<TR>");
				sb2.append("<TD width=\"35%\">");
				sb2.append("&nbsp;&nbsp;&nbsp;Address Line 4");
				sb2.append("</TD>");
				sb2.append("<TD>");
				sb2.append("<INPUT TYPE=TEXT SIZE=70 style=\"font-size:12;border-style:solid;border-width:1\" READONLY VALUE = \"" + "" + "\">");
				sb2.append("</TD>");
				sb2.append("</TR>");

				sb2.append("<TR>");
				sb2.append("<TD width=\"35%\">");
				sb2.append("&nbsp;&nbsp;&nbsp;City");
				sb2.append("</TD>");
				sb2.append("<TD>");
				sb2.append("<INPUT TYPE=TEXT SIZE=70 style=\"font-size:12;border-style:solid;border-width:1\" READONLY VALUE = \"" + "" + "\">");
				sb2.append("</TD>");
				sb2.append("</TR>");

				sb2.append("<TR>");
				sb2.append("<TD width=\"35%\">");
				sb2.append("&nbsp;&nbsp;&nbsp;State");
				sb2.append("</TD>");
				sb2.append("<TD>");
				sb2.append("<INPUT TYPE=TEXT SIZE=70 style=\"font-size:12;border-style:solid;border-width:1\" READONLY VALUE = \"" + "" + "\">");
				sb2.append("</TD>");
				sb2.append("</TR>");

				sb2.append("<TR>");
				sb2.append("<TD width=\"35%\">");
				sb2.append("&nbsp;&nbsp;&nbsp;PIN");
				sb2.append("</TD>");
				sb2.append("<TD>");
				sb2.append("<INPUT TYPE=TEXT SIZE=15 style=\"font-size:12;border-style:solid;border-width:1;text-align:left\" READONLY VALUE = \"" + "" + "\">");
				sb2.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;STD Code&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
				sb2.append("<INPUT TYPE=TEXT SIZE=15 style=\"font-size:12;border-style:solid;border-width:1;text-align:left\" READONLY VALUE = \"" + "" + "\">");
				sb2.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Phone Number&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
				sb2.append("<INPUT TYPE=TEXT SIZE=15 style=\"font-size:12;border-style:solid;border-width:1;text-align:left\" READONLY VALUE = \"" + "" + "\">");
				sb2.append("</TD>");
				sb2.append("</TR>");

			}
			else
			{
				sb2.append("<TR>");
				sb2.append("<TD colspan=\"2\">");
				sb2.append("<B>&nbsp;&nbsp;&nbsp;Accounts Office Address</B>");
				sb2.append("</TD>");
				sb2.append("</TR>");

				sb2.append("<TR>");
				sb2.append("<TD width=\"35%\">");
				sb2.append("&nbsp;&nbsp;&nbsp;Address Line 1");
				sb2.append("</TD>");
				sb2.append("<TD>");
				sb2.append("<INPUT TYPE=TEXT SIZE=70 style=\"font-size:12;border-style:solid;border-width:1\" READONLY VALUE = \"" + aoAddress1.trim() + "\">");
				sb2.append("</TD>");
				sb2.append("</TR>");

				sb2.append("<TR>");
				sb2.append("<TD width=\"35%\">");
				sb2.append("&nbsp;&nbsp;&nbsp;Address Line 2");
				sb2.append("</TD>");
				sb2.append("<TD>");
				sb2.append("<INPUT TYPE=TEXT SIZE=70 style=\"font-size:12;border-style:solid;border-width:1\" READONLY VALUE = \"" + aoAddress2.trim() + "\">");
				sb2.append("</TD>");
				sb2.append("</TR>");

				sb2.append("<TR>");
				sb2.append("<TD width=\"35%\">");
				sb2.append("&nbsp;&nbsp;&nbsp;Address Line 3");
				sb2.append("</TD>");
				sb2.append("<TD>");
				sb2.append("<INPUT TYPE=TEXT SIZE=70 style=\"font-size:12;border-style:solid;border-width:1\" READONLY VALUE = \"" + aoAddress3.trim() + "\">");
				sb2.append("</TD>");
				sb2.append("</TR>");

				sb2.append("<TR>");
				sb2.append("<TD width=\"35%\">");
				sb2.append("&nbsp;&nbsp;&nbsp;Address Line 4");
				sb2.append("</TD>");
				sb2.append("<TD>");
				sb2.append("<INPUT TYPE=TEXT SIZE=70 style=\"font-size:12;border-style:solid;border-width:1\" READONLY VALUE = \"" + aoAddress4.trim() + "\">");
				sb2.append("</TD>");
				sb2.append("</TR>");

				sb2.append("<TR>");
				sb2.append("<TD width=\"35%\">");
				sb2.append("&nbsp;&nbsp;&nbsp;City");
				sb2.append("</TD>");
				sb2.append("<TD>");
				sb2.append("<INPUT TYPE=TEXT SIZE=70 style=\"font-size:12;border-style:solid;border-width:1\" READONLY VALUE = \"" + aoCity.trim() + "\">");
				sb2.append("</TD>");
				sb2.append("</TR>");

				sb2.append("<TR>");
				sb2.append("<TD width=\"35%\">");
				sb2.append("&nbsp;&nbsp;&nbsp;State");
				sb2.append("</TD>");
				sb2.append("<TD>");
				sb2.append("<INPUT TYPE=TEXT SIZE=70 style=\"font-size:12;border-style:solid;border-width:1\" READONLY VALUE = \"" + aoState.trim() + "\">");
				sb2.append("</TD>");
				sb2.append("</TR>");

				sb2.append("<TR>");
				sb2.append("<TD width=\"35%\">");
				sb2.append("&nbsp;&nbsp;&nbsp;PIN");
				sb2.append("</TD>");
				sb2.append("<TD>");
				sb2.append("<INPUT TYPE=TEXT SIZE=15 style=\"font-size:12;border-style:solid;border-width:1;text-align:left\" READONLY VALUE = \"" + aoPinCode.trim() + "\">");
				sb2.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;STD Code&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
				sb2.append("<INPUT TYPE=TEXT SIZE=15 style=\"font-size:12;border-style:solid;border-width:1;text-align:left\" READONLY VALUE = \"" + aoStdCode.trim() + "\">");
				sb2.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Phone Number&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
				sb2.append("<INPUT TYPE=TEXT SIZE=15 style=\"font-size:12;border-style:solid;border-width:1;text-align:left\" READONLY VALUE = \"" + aoPhoneNo.trim() + "\">");
				sb2.append("</TD>");
				sb2.append("</TR>");
			}

			sb2.append("<TR>");
			sb2.append("</TR>");
			sb2.append("<TD>");
			sb2.append("&nbsp;");
			sb2.append("</TD>");
			sb2.append("<TD>");
			sb2.append("&nbsp;");
			sb2.append("</TD>");
			sb2.append("<TR>");
			sb2.append("</TR>");

			sb2.append("<TR>");
			sb2.append("</TR>");
			sb2.append("<TD>");
			sb2.append("&nbsp;");
			sb2.append("</TD>");
			sb2.append("<TD>");
			sb2.append("&nbsp;");
			sb2.append("</TD>");
			sb2.append("<TR>");
			sb2.append("</TR>");

			sb2.append("<TR>");
			sb2.append("<TD width=\"35%\">");
			sb2.append("&nbsp;&nbsp;&nbsp;Category of Deductor");
			sb2.append("</TD>");
			sb2.append("<TD>");
			if (deductorCategoryCode.equals("A"))
			{
				sb2.append("<INPUT TYPE=TEXT SIZE=25 style=\"font-size:12;border-style:solid;border-width:1\" READONLY VALUE = \"Central Government\">&nbsp;&nbsp;&nbsp;");
			}
			else
			{
				sb2.append("<INPUT TYPE=TEXT SIZE=25 style=\"font-size:12;border-style:solid;border-width:1\" READONLY VALUE = \"State Government\">&nbsp;&nbsp;&nbsp;");
			}
			sb2.append("</TD>");
			sb2.append("</TR>");

			sb2.append("<TR>");
			sb2.append("<TD width=\"35%\">");
			sb2.append("&nbsp;&nbsp;&nbsp;Ministry Name");
			sb2.append("</TD>");
			sb2.append("<TD>");
			if(ministryName.equals(""))
				sb2.append("<INPUT TYPE=TEXT SIZE=25 style=\"font-size:12;border-style:solid;border-width:1\"  READONLY VALUE = \"" + "" + "\">");
			else
				sb2.append("<INPUT TYPE=TEXT SIZE=25 style=\"font-size:12;border-style:solid;border-width:1\"  READONLY VALUE = \"" + TBAF_MINISTRY_NAME[Integer.parseInt(ministryName.trim())] + "\">");

			sb2.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;State Name&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");

			if(stateName.equals(""))
				sb2.append("<INPUT TYPE=TEXT SIZE=44 style=\"font-size:12;border-style:solid;border-width:1\" READONLY VALUE = \"" + "" + "\">");
			else
				sb2.append("<INPUT TYPE=TEXT SIZE=44 style=\"font-size:12;border-style:solid;border-width:1\"  READONLY VALUE = \"" + TBAF_STATE_NAME[Integer.parseInt(stateName.trim())] + "\">");
			sb2.append("</TD>");
			sb2.append("</TR>");

			sb2.append("<TR>");
			sb2.append("<TD width=\"35%\">");
			sb2.append("&nbsp;&nbsp;&nbsp;Sub Ministry Name");
			sb2.append("</TD>");
			sb2.append("<TD>");
			if(subMinistryName.equals(""))
				sb2.append("<INPUT TYPE=TEXT SIZE=100 style=\"font-size:12;border-style:solid;border-width:1\"  READONLY VALUE = \"" + "" + "\">");
			else
			{
				if(subMinistryName.equals("99"))
					sb2.append("<INPUT TYPE=TEXT SIZE=100 style=\"font-size:12;border-style:solid;border-width:1\"  READONLY VALUE = \"" + "OTHERS" + "\">");
				else
					sb2.append("<INPUT TYPE=TEXT SIZE=100 style=\"font-size:12;border-style:solid;border-width:1\"  READONLY VALUE = \"" + TBAF_SUB_MINISTRY_NAME[Integer.parseInt(subMinistryName.trim())] + "\">");
			}
			sb2.append("</TD>");
			sb2.append("</TR>");

			sb2.append("<TR>");
			sb2.append("<TD width=\"35%\">");
			sb2.append("&nbsp;&nbsp;&nbsp;Sub Ministry Name (Other)");
			sb2.append("</TD>");
			sb2.append("<TD>");
			if(subMinistryNameOthers.equals(""))
				sb2.append("<textarea rows=2 cols=75 readonly style=\"border-style:solid;border-width:1;font-size:12;overflow:hidden;\">"+""+"</textarea>");
			else
				sb2.append("<textarea rows=2 cols=75 readonly style=\"border-style:solid;border-width:1;font-size:12;overflow:hidden;\">"+ subMinistryNameOthers.trim() +"</textarea>");
			sb2.append("</TD>");
			sb2.append("</TR>");

			sb2.append("<TR>");
			sb2.append("<TD>");
			sb2.append("&nbsp;");
			sb2.append("</TD>");
			sb2.append("<TD>");
			sb2.append("&nbsp;");
			sb2.append("</TD>");
			sb2.append("</TR>");

			sb2.append("<TR>");
			sb2.append("<TD>");
			sb2.append("&nbsp;");
			sb2.append("</TD>");
			sb2.append("<TD>");
			sb2.append("&nbsp;");
			sb2.append("</TD>");
			sb2.append("</TR>");


			if (transactionType.equals(TBAF_TRANSACTION_TYPE_C3))
			{
				sb2.append("<TR>");
				sb2.append("<TD colspan=\"2\">");
				sb2.append("<B>&nbsp;&nbsp;&nbsp;Responsible Person Details</B><BR>");
				sb2.append("</TD>");
				sb2.append("</TR>");

				sb2.append("<TR>");
				sb2.append("<TD width=\"35%\">");
				sb2.append("&nbsp;&nbsp;&nbsp;Responsible Person Name");
				sb2.append("</TD>");
				sb2.append("<TD>");
				sb2.append("<textarea rows=2 cols=75 readonly style=\"border-style:solid;border-width:1;font-size:12;overflow:hidden;\">"+""+"</textarea>");
				sb2.append("</TD>");
				sb2.append("</TR>");

				sb2.append("<TR>");
				sb2.append("<TD width=\"35%\">");
				sb2.append("&nbsp;&nbsp;&nbsp;Responsible Person Designation");
				sb2.append("</TD>");
				sb2.append("<TD>");
				sb2.append("<textarea rows=2 cols=75 readonly style=\"border-style:solid;border-width:1;font-size:12;overflow:hidden;\">"+""+"</textarea>");
				sb2.append("</TD>");
				sb2.append("</TR>");

			}
			else
			{
				sb2.append("<TR>");
				sb2.append("<TD colspan=\"2\">");
				sb2.append("<B>&nbsp;&nbsp;&nbsp;Responsible Person Details</B><BR>");
				sb2.append("</TD>");
				sb2.append("</TR>");

				sb2.append("<TR>");
				sb2.append("<TD width=\"35%\">");
				sb2.append("&nbsp;&nbsp;&nbsp;Responsible Person Name");
				sb2.append("</TD>");
				sb2.append("<TD>");
				//Gauri added a Responsible person name for CR 89435, FVU 1.9::START
				if(Integer.parseInt(fStats.getFinancialYear()) < 2026) {
				sb2.append("<textarea rows=2 cols=75 readonly style=\"border-style:solid;border-width:1;font-size:12;overflow:hidden;\">"+ personName.trim() +"</textarea>");
				}
				else {
					sb2.append("<textarea rows=2 cols=75 readonly style=\"border-style:solid;border-width:1;font-size:12;overflow:hidden;\">"+ rFirstName.trim() +" " + rMiddleName.trim() + " " + rLastName.trim() +"</textarea>");	
				}
				//Gauri added a Responsible person name for CR 89435, FVU 1.9::END
				sb2.append("</TD>");
				sb2.append("</TR>");

				sb2.append("<TR>");
				sb2.append("<TD width=\"35%\">");
				sb2.append("&nbsp;&nbsp;&nbsp;Responsible Person Designation");
				sb2.append("</TD>");
				sb2.append("<TD>");
				sb2.append("<textarea rows=2 cols=75 readonly style=\"border-style:solid;border-width:1;font-size:12;overflow:hidden;\">"+ personDesig.trim() +"</textarea>");
				sb2.append("</TD>");
				sb2.append("</TR>");
			}
			sb2.append("<TR>");
			sb2.append("<TD>");
			sb2.append("&nbsp;");
			sb2.append("</TD>");
			sb2.append("<TD>");
			sb2.append("&nbsp;");
			sb2.append("</TD>");
			sb2.append("</TR>");

			sb2.append("<TR>");
			sb2.append("<TD>");
			sb2.append("&nbsp;");
			sb2.append("</TD>");
			sb2.append("<TD>");
			sb2.append("&nbsp;");
			sb2.append("</TD>");
			sb2.append("</TR>");

			bw.write(sb2.toString());
            sb2.delete(0, sb2.length());
            
			//This Portion is not required to be displayed for X correction Type
			if( (statementType.equals(TBAF_TYPE_OF_STMT_ORIG)) || (statementType.equals(TBAF_TYPE_OF_STMT_CORR) && ! transactionType.equals(TBAF_TRANSACTION_TYPE_X))  )
				{
					sb3.append("<TR>");
					sb3.append("<TD WIDTH=\"35%\" width=\"2\">");
					sb3.append("<B>3. Return Summary</B><BR>");
					sb3.append("</TD>");
					sb3.append("</TR>");
	
					sb3.append("<TR>");
					sb3.append("<TD WIDTH=\"35%\">");
					sb3.append("&nbsp;&nbsp;&nbsp;Month ending (dd/mm/yyyy)");
					sb3.append("</TD>");
					sb3.append("<TD>");
					int mon=Integer.parseInt(monthOfPayment);
					switch(mon)
					{
					case 1:
					case 2:
					case 3:
						int value1=(Integer.parseInt(financialYear))+1;
						financialYear=String.valueOf(value1);
						break;
					}
					switch(Integer.parseInt(monthOfPayment))
					{
					case 1:
					case 3:
					case 5:
					case 7:
					case 8:
					case 10:
					case 12:
						dayOfEnding="31";
						break;
					case 4:
					case 6:
					case 9:
					case 11:
						dayOfEnding="30";
						break;
					case 2:
					{
						if(isLeap(Integer.parseInt(financialYear)))
							dayOfEnding="29";
						else
							dayOfEnding="28";
					}
					}
					
					sb3.append("<INPUT TYPE=TEXT SIZE=25 style=\"font-size:12;border-style:solid;border-width:1;text-align:right\" READONLY VALUE = \"" +dayOfEnding+"/" + monthOfPayment+"/"+financialYear+ "\">");
					sb3.append("</TD>");
					sb3.append("</TR>");
					if (transactionType.equals(TBAF_TRANSACTION_TYPE_C1))
					{
						sb3.append("<TR>");
						sb3.append("<TD WIDTH=\"35%\">");
						sb3.append("&nbsp;&nbsp;&nbsp;No. of entries in item 4");
						sb3.append("</TD>");
						sb3.append("<TD>");
						sb3.append("<INPUT TYPE=TEXT SIZE=25 style=\"font-size:12;border-style:solid;border-width:1;text-align:right\" READONLY VALUE = \"" + " " + "\">");
						sb3.append("</TD>");
						sb3.append("</TR>");
	
						sb3.append("<TR>");
						sb3.append("<TD WIDTH=\"35%\">");
						sb3.append("&nbsp;&nbsp;&nbsp;Total TDS/TCS Amount Transferred (Note 1) ( <img src =\"rp.png\" > ) </img>");
						sb3.append("</TD>");
						sb3.append("<TD>");
						sb3.append("<INPUT TYPE=TEXT SIZE=25 style=\"font-size:12;border-style:solid;border-width:1;text-align:right\" READONLY VALUE = \"" + " " + "\">");
						sb3.append("</TD>");
						sb3.append("</TR>");
	
						sb3.append("<TR>");
						sb3.append("<TD WIDTH=\"35%\">");
						sb3.append("&nbsp;&nbsp;&nbsp;Total TDS/TCS Amount Remitted (Note 1) ( <img src =\"rp.png\" > ) </img>");
						sb3.append("</TD>");
						sb3.append("<TD>");
						sb3.append("<INPUT TYPE=TEXT SIZE=25 style=\"font-size:12;border-style:solid;border-width:1;text-align:right\" READONLY VALUE = \"" + " " + "\">");
						sb3.append("</TD>");
						sb3.append("</TR>");
	
					}
					else
					{
						sb3.append("<TR>");
						sb3.append("<TD WIDTH=\"35%\">");
						sb3.append("&nbsp;&nbsp;&nbsp;No. of entries in item 4");
						sb3.append("</TD>");
						sb3.append("<TD>");
						sb3.append("<INPUT TYPE=TEXT SIZE=25 style=\"font-size:12;border-style:solid;border-width:1;text-align:right\" READONLY VALUE = \"" + countOfDDORecords.trim() + "\">");
						sb3.append("</TD>");
						sb3.append("</TR>");
	
						sb3.append("<TR>");
						sb3.append("<TD WIDTH=\"35%\">");
						sb3.append("&nbsp;&nbsp;&nbsp;Total TDS/TCS Amount Transferred (Note 1)(<img src =\"rp.png\" > ) </img>");
						sb3.append("</TD>");
						sb3.append("<TD>");
						sb3.append("<INPUT TYPE=TEXT SIZE=25 style=\"font-size:12;border-style:solid;border-width:1;text-align:right\" READONLY VALUE = \"" + totalTax.trim() + "\">");
						sb3.append("</TD>");
						sb3.append("</TR>");
	
						sb3.append("<TR>");
						sb3.append("<TD WIDTH=\"35%\">");
						sb3.append("&nbsp;&nbsp;&nbsp;Total TDS/TCS Amount Remitted (Note 1) ( <img src =\"rp.png\" > ) </img>");
						sb3.append("</TD>");
						sb3.append("<TD>");
						sb3.append("<INPUT TYPE=TEXT SIZE=25 style=\"font-size:12;border-style:solid;border-width:1;text-align:right\" READONLY VALUE = \"" + totalRemitted.trim() + "\">");
						sb3.append("<BR>");
						sb3.append("</TD>");
						sb3.append("</TR>");
	
					}
					sb3.append("<TR>");
					sb2.append("<TD>");
					sb2.append("&nbsp;");
					sb2.append("</TD>");
					sb2.append("<TD>");
					sb2.append("&nbsp;");
					sb2.append("</TD>");
					sb3.append("</TR>");
	
					sb3.append("<TR>");
					sb2.append("<TD>");
					sb2.append("&nbsp;");
					sb2.append("</TD>");
					sb2.append("<TD>");
					sb2.append("&nbsp;");
					sb2.append("</TD>");
					sb3.append("</TR>");
	
				   // sb2.append("</TABLE>");
					
	
					/*		sb2.append("<TR>");
											sb2.append("<TD>");
											sb2.append("&nbsp;&nbsp;&nbsp;Nature of Deduction/ Collection (Note 2)");
											sb2.append("</TD>");
											sb2.append("<TD>");
	
											if (natureOfDeduction.trim().equals(TBAF_FORM_24Q))
											{
												sb2.append("<INPUT TYPE=TEXT SIZE=38 READONLY VALUE = \"" + "TDS-SAL" + "(" + natureOfDeduction + ")" + "\">");
											}
											else if (natureOfDeduction.trim().equals(TBAF_FORM_26Q))
											{
												sb2.append("<INPUT TYPE=TEXT SIZE=38 READONLY VALUE = \"" + "TDS-NON-SAL" + "(" + natureOfDeduction + ")" + "\">");
											}
											else if (natureOfDeduction.trim().equals(TBAF_FORM_27Q))
											{
												sb2.append("<INPUT TYPE=TEXT SIZE=38 READONLY VALUE = \"" + "TDS-NR" + "(" + natureOfDeduction + ")" + "\">");
											}
											else if (natureOfDeduction.trim().equals(TBAF_FORM_27EQ))
											{
												sb2.append("<INPUT TYPE=TEXT SIZE=38 READONLY VALUE = \"" + "TCS" + "(" + natureOfDeduction + ")" + "\">");
											}    //Commented by Subhankar as Nature of deduction is moved from  BATCH to TRANSACTION Details
	
	
											sb2.append("</TD>");
											sb2.append("</TR>");
	
											sb2.append("</TABLE>");*/ //Commented by Bharath
					bw.write(sb3.toString());
	
	
					Log.tbaf_log.info("Status of the details of Batch Header Record: Completed");
	
					int tdDDOCount = 0;
					//if (!transactionType.equals(TBAF_TRANSACTION_TYPE_C1))
					//{
						sb4.append("<TR>");
						sb4.append("<TD STYLE=\"BORDER-TOP:NONE;BORDER-BOTTOM:NONE\" colspan=\"2\">");
						sb4.append("<B>4. DDO wise Details of Transfer Vouchers</B>");
						sb4.append("</TD>");
						sb4.append("</TR>");
	
						sb4.append("<TR>");
						sb4.append("<TD colspan=\"2\">");
						sb4.append("<TABLE BORDER=1 WIDTH=\"100%\"  CELLSPACING=0 CELLPADDING=0 BORDERCOLOR=black style='border-collapse: collapse; ' >");
						sb4.append("<TR>");
						sb4.append("<TD  width=2% style=\"WORD-BREAK:BREAK-ALL\"><CENTER><B>S No.*</B></CENTER></TD>");
						sb4.append("<TD  width=8% style=\"WORD-BREAK:BREAK-ALL\"><CENTER><B>DDO Registration No.</B></CENTER></TD>");
						sb4.append("<TD  width=10% style=\"WORD-BREAK:BREAK-ALL\"><CENTER><B>DDO Code</B></CENTER></TD>");
						sb4.append("<TD  width=8% style=\"WORD-BREAK:BREAK-ALL\"><CENTER><B>TAN of DDO*</B></CENTER></TD>");
						sb4.append("<TD  width=10% style=\"WORD-BREAK:BREAK-ALL\"><CENTER><B>Name of DDO*</B></CENTER></TD>");
						sb4.append("<TD  width=4% style=\"WORD-BREAK:BREAK-ALL\"><CENTER><B>Address 1*</B></CENTER></TD>");
						sb4.append("<TD  width=4% style=\"WORD-BREAK:BREAK-ALL\"><CENTER><B>Address 2</B></CENTER></TD>");
						sb4.append("<TD  width=4% style=\"WORD-BREAK:BREAK-ALL\"><CENTER><B>Address 3</B></CENTER></TD>");
						sb4.append("<TD  width=4% style=\"WORD-BREAK:BREAK-ALL\"><CENTER><B>Address 4</B></CENTER></TD>");
						sb4.append("<TD  width=4% style=\"WORD-BREAK:BREAK-ALL\"><CENTER><B>City*</B></CENTER></TD>");
						sb4.append("<TD  width=4% style=\"WORD-BREAK:BREAK-ALL\"><CENTER><B>State*</B></CENTER></TD>");
						sb4.append("<TD  width=5% style=\"WORD-BREAK:BREAK-ALL\"><CENTER><B>PIN Code*</B></CENTER></TD>");
						sb4.append("<TD  width=10% style=\"WORD-BREAK:BREAK-ALL\"><CENTER><B>Email id</B></CENTER></TD>");
						sb4.append("<TD  width=8% style=\"WORD-BREAK:BREAK-ALL\"><CENTER><B>Tax Deducted/ Collected (Sum of BAS_TAX, SUR, EDU_CESS)*</B><BR>( <img src =\"rp.png\" > ) </img></CENTER></TD>");
						sb4.append("<TD  width=9% style=\"WORD-BREAK:BREAK-ALL\"><CENTER><B>Total TDS/TCS remitted to Government Account (AG/Pr CCA)*</B><BR>( <img src =\"rp.png\" > ) </img></CENTER></TD>");
						
						//This would be present only if the transaction type is M 
						
						if(transactionType.equals(TBAF_TRANSACTION_TYPE_M))
						{
							sb4.append("<TD  width=6% style=\"WORD-BREAK:BREAK-ALL\"><CENTER><B>ADDED/<BR>DELETED</B></CENTER></TD>");
						}
						
						//End of Validation
						
						sb4.append("</TR>");
						sb4.append("<TR>");
						sb4.append("<TD style=\"WORD-BREAK:BREAK-ALL\"><CENTER>(151)</CENTER></TD>");
						sb4.append("<TD style=\"WORD-BREAK:BREAK-ALL\"><CENTER>(152)</CENTER></TD>");
						sb4.append("<TD style=\"WORD-BREAK:BREAK-ALL\"><CENTER>(153)</CENTER></TD>");
						sb4.append("<TD style=\"WORD-BREAK:BREAK-ALL\"><CENTER>(154)</CENTER></TD>");
						sb4.append("<TD style=\"WORD-BREAK:BREAK-ALL\"><CENTER>(155)</CENTER></TD>");
						sb4.append("<TD style=\"WORD-BREAK:BREAK-ALL\"><CENTER>(156)</CENTER></TD>");
						sb4.append("<TD style=\"WORD-BREAK:BREAK-ALL\"><CENTER>(157)</CENTER></TD>");
						sb4.append("<TD style=\"WORD-BREAK:BREAK-ALL\"><CENTER>(158)</CENTER></TD>");
						sb4.append("<TD style=\"WORD-BREAK:BREAK-ALL\"><CENTER>(159)</CENTER></TD>");
						sb4.append("<TD style=\"WORD-BREAK:BREAK-ALL\"><CENTER>(160)</CENTER></TD>");
						sb4.append("<TD style=\"WORD-BREAK:BREAK-ALL\"><CENTER>(161)</CENTER></TD>");
						sb4.append("<TD style=\"WORD-BREAK:BREAK-ALL\"><CENTER>(162)</CENTER></TD>");
						sb4.append("<TD style=\"WORD-BREAK:BREAK-ALL\"><CENTER>(163)</CENTER></TD>");
						sb4.append("<TD style=\"WORD-BREAK:BREAK-ALL\"><CENTER>(164)</CENTER></TD>");
						sb4.append("<TD style=\"WORD-BREAK:BREAK-ALL\"><CENTER>(165)</CENTER></TD>");
						
						
						//This would be present only if the transaction type is M 
						if(transactionType.equals(TBAF_TRANSACTION_TYPE_M))
						{
							sb4.append("<TD style=\"WORD-BREAK:BREAK-ALL\"><CENTER>(166)</CENTER></TD>");
						}
						
						
						//End of validation
						
						sb4.append("</TR>");
	
						//Added by Bharath
						NewStringTokenizer strTD = null;
						double ddoWisetotalTax1=0;
						double ddoWisetotalRemitted1=0;
						// Reading DDO Transaction Detail Records
						while ((recstr = br.readLine()) != null)
						{
							count = 1;
							sb4.append("<TR>");
							strTD = new NewStringTokenizer(recstr, "^");
							TDLoop : while ((fieldsBuffer = strTD.nextToken()) != null)
							{
								switch (count)
								{
								
								case 4:
								{
									revisionMode = fieldsBuffer;
									break;
								}
								
								/*	case 4 :
																{
																	revisionMode = fieldsBuffer;
																	if (transactionType.trim().equals(TBAF_TRANSACTION_TYPE_C2) || transactionType.trim().equals(TBAF_TRANSACTION_TYPE_C3))
																	{
																		temp = "<TD>" + "<CENTER>" +  revisionMode.trim() + "</CENTER>" + "</TD>";
																	}
																	else
																	{
																		temp = "<TD>" + "<CENTER>" + "-" + "</CENTER>" + "</TD>";
																	}
																}
																break; */ // Commented by Bharath
								case 5 :
								{
									/*sb3.append("<TD>");
																	sb3.append("&nbsp;" + fieldsBuffer + "&nbsp;");
																	sb3.append("</TD>");*/
									serialNo=fieldsBuffer;
								}
								break;
								case 18 :
								{
									/*	sb3.append("<TD>");
																	sb3.append("<CENTER>");
																	sb3.append("&nbsp;" + fieldsBuffer + "&nbsp;");
																	sb3.append("</CENTER>");
																	sb3.append("</TD>"); */
									ddoRegistrationNo=fieldsBuffer;
								}
								break;
								case 19 :
								{
									/*	sb3.append("<TD>");
																	sb3.append("<CENTER>");
																	sb3.append("&nbsp;" + fieldsBuffer + "&nbsp;");
																	sb3.append("</CENTER>");
																	sb3.append("</TD>"); */
									ddoCode=fieldsBuffer;
								}
								break;
								/*			case 6 :
																{
																	oldSerialNo = fieldsBuffer;
																	if (oldSerialNo.length() != 0)
																	{
																		temp = temp + "<TD>" + "&nbsp;" + "<CENTER>" + oldSerialNo.trim() + "</CENTER>" + "&nbsp;" + "</TD>";
																	}
																	else
																	{
																		temp = temp + "<TD>" + "&nbsp;" + "<CENTER>" + "-" + "</CENTER>" + "&nbsp;" + "</TD>";
																	}
																}
																break; */ //Commented by Bharath
								case 7 :
									ddoTAN=fieldsBuffer;
									break;
								case 8 :
									ddoName=fieldsBuffer;
									break;
								case 9 :
									ddoAddress1=fieldsBuffer;
									break;
								case 10 :
									ddoAddress2=fieldsBuffer;
									break;
								case 11 :
									ddoAddress3=fieldsBuffer;
									break;
								case 12 :
									ddoAddress4=fieldsBuffer;
									break;
								case 13 :
									/*	{
																	sb3.append("<TD>");
																	sb3.append("<CENTER>");
																	sb3.append("&nbsp;" + fieldsBuffer + "&nbsp;");
																	sb3.append("</CENTER>");
																	sb3.append("</TD>");
																}*/
									ddoCity=fieldsBuffer;
									break;
								case 14 :
								{
									if (fieldsBuffer.length() != 0)
									{
										ddoStateName = TBAF_STATE_NAME[Integer.parseInt(fieldsBuffer.trim())];
										/*		sb3.append("<TD>");
																		sb3.append("<CENTER>");
																		sb3.append("&nbsp;" + ddoStateName + "&nbsp;");
																		sb3.append("</CENTER>");
																		sb3.append("</TD>");
																	} */ //Commented by Bharath
									}
									else
									{
										/*			sb3.append("<TD>");
																		sb3.append("<CENTER>");
																		sb3.append("&nbsp;" + fieldsBuffer + "&nbsp;");
																		sb3.append("</CENTER>");
																		sb3.append("</TD>"); */
										ddoStateName=fieldsBuffer;
									}
								}
								break;
								case 15 :
									/*		sb3.append("<TD>");
																sb3.append("<CENTER>");
																sb3.append("&nbsp;" + fieldsBuffer + "&nbsp;");
																sb3.append("</CENTER>");
																sb3.append("</TD>"); */ //Commented by Bharath
									ddoPinCode=fieldsBuffer;
									break;
								case 20 :
									/*			{
																	sb3.append("<TD>");
																	sb3.append("<CENTER>");
																	sb3.append("&nbsp;" + fieldsBuffer + "&nbsp;");
																	sb3.append("</CENTER>");
																	sb3.append("</TD>");
																} */ //Commented by Bharath
									ddoEmailId=fieldsBuffer;
									break;
								case 16 :
									/*				{
																sb3.append("<TD ALIGN=LEFT WRAP>");
																sb3.append("&nbsp;" + fieldsBuffer);
																sb3.append("</TD>");
																break;
															} */ //Commented by Bharath
									taxAmount=fieldsBuffer;
									ddoWisetotalTax1=ddoWisetotalTax1+Double.parseDouble(taxAmount);
									break;
								case 21 :
									/*	{
																sb3.append("<TD>");
																sb3.append("<CENTER>");
																sb3.append("&nbsp;" + fieldsBuffer + "&nbsp;");
																sb3.append("</CENTER>");
																sb3.append("</TD>");
																break;
															} */  //Commented by Bharath
									amountRemitted=fieldsBuffer;
									ddoWisetotalRemitted1=ddoWisetotalRemitted1+Double.parseDouble(amountRemitted);
									break;
								case 25 :
									lastAmtRemitted=fieldsBuffer;
									if(!(lastAmtRemitted.equals("^")||lastAmtRemitted.equals("")))
									{
									ddoWisetotalRemitted1=ddoWisetotalRemitted1-Double.parseDouble(lastAmtRemitted);
									}
									break;
								case 28:
									lastTaxAmount=fieldsBuffer;
										if(!(lastTaxAmount.equals("^") || lastTaxAmount.equals("")))
									{
									ddoWisetotalTax1=ddoWisetotalTax1-Double.parseDouble(lastTaxAmount);
									}
									break;
									/*			case 17 :
																if(fieldsBuffer.length()!=0)
																{
																	sb3.append("<TD NOWRAP>");
																	String monthAndYear = displayMonthYear(fieldsBuffer);
																	sb3.append("&nbsp;" + monthAndYear.trim() + "&nbsp;");
																	sb3.append("</TD>");
																}
																else
																{
																	sb3.append("<TD>");
																	sb3.append("-");
																	sb3.append("</TD>");
																}
																break; */ //Commented by Bharath
								}
	
								if (fieldsBuffer.length() == 0 || fieldsBuffer.length() != 0)
								{
									count++;
								}
								if (count > 28)
									break TDLoop;
							}
							sb4.append("<TD style=\"WORD-BREAK:BREAK-ALL\">");
							sb4.append("&nbsp;" + serialNo + "&nbsp;");
							sb4.append("</TD>");
							sb4.append("<TD style=\"WORD-BREAK:BREAK-ALL\">");
							sb4.append("&nbsp;" + ddoRegistrationNo + "&nbsp;");
							sb4.append("</TD>");
							sb4.append("<TD style=\"WORD-BREAK:BREAK-ALL\">");
							sb4.append("&nbsp;" + ddoCode + "&nbsp;");
							sb4.append("</TD>");
							sb4.append("<TD style=\"WORD-BREAK:BREAK-ALL\">");
							sb4.append("&nbsp;" + ddoTAN + "&nbsp;");
							sb4.append("</TD>");
							sb4.append("<TD style=\"WORD-BREAK:BREAK-ALL\">");
							sb4.append("&nbsp;" + ddoName + "&nbsp;");
							sb4.append("</TD>");
							sb4.append("<TD style=\"WORD-BREAK:BREAK-ALL\">");
							sb4.append("&nbsp;" + ddoAddress1 + "&nbsp;");
							sb4.append("</TD>");
							sb4.append("<TD style=\"WORD-BREAK:BREAK-ALL\">");
							sb4.append("&nbsp;" + ddoAddress2 + "&nbsp;");
							sb4.append("</TD>");
							sb4.append("<TD style=\"WORD-BREAK:BREAK-ALL\">");
							sb4.append("&nbsp;" + ddoAddress3 + "&nbsp;");
							sb4.append("</TD>");
							sb4.append("<TD style=\"WORD-BREAK:BREAK-ALL\">");
							sb4.append("&nbsp;" + ddoAddress4 + "&nbsp;");
							sb4.append("</TD>");
							sb4.append("<TD style=\"WORD-BREAK:BREAK-ALL\">");
							sb4.append("&nbsp;" + ddoCity + "&nbsp;");
							sb4.append("</TD>");
							sb4.append("<TD style=\"WORD-BREAK:BREAK-ALL\">");
							sb4.append("&nbsp;" + ddoStateName + "&nbsp;");
							sb4.append("</TD>");
							sb4.append("<TD style=\"WORD-BREAK:BREAK-ALL\">");
							sb4.append("&nbsp;" + ddoPinCode + "&nbsp;");
							sb4.append("</TD>");
							sb4.append("<TD style=\"WORD-BREAK:BREAK-ALL\">");
							sb4.append("&nbsp;" + ddoEmailId + "&nbsp;");
							sb4.append("</TD>");
							
							
							
							
							if(transactionType.equals(TBAF_TRANSACTION_TYPE_M))
							{
								if(revisionMode.equals(TBAF_REVISION_MODE_UPDATE) && !lastTaxAmount.equals("^") && !taxAmount.equals("^"))
								{
									sb4.append("<TD ALIGN=RIGHT style=\"WORD-BREAK:BREAK-ALL\">");
									sb4.append("&nbsp;" + (Double.parseDouble(taxAmount)-Double.parseDouble(lastTaxAmount)) + "&nbsp;");
									sb4.append("</TD>");
								}
								else
								{
									sb4.append("<TD ALIGN=RIGHT style=\"WORD-BREAK:BREAK-ALL\">");
									sb4.append("&nbsp;" + taxAmount + "&nbsp;");
									sb4.append("</TD>");
								}
								if(revisionMode.equals(TBAF_REVISION_MODE_UPDATE) && !lastAmtRemitted.equals("^") && !amountRemitted.equals("^"))
								{
									sb4.append("<TD ALIGN=RIGHT style=\"WORD-BREAK:BREAK-ALL\">");
									sb4.append("&nbsp;" + (Double.parseDouble(amountRemitted)-Double.parseDouble(lastAmtRemitted)) + "&nbsp;");
									sb4.append("</TD>");
								}
								else
								{
									sb4.append("<TD ALIGN=RIGHT style=\"WORD-BREAK:BREAK-ALL\">");
									sb4.append("&nbsp;" + amountRemitted + "&nbsp;");
									sb4.append("</TD>");
								}
								sb4.append("<TD ALIGN=RIGHT style=\"WORD-BREAK:BREAK-ALL\">");
								sb4.append("&nbsp;" + revisionMode + "&nbsp;");
								sb4.append("</TD>");
							}
							else
							{
								sb4.append("<TD ALIGN=RIGHT style=\"WORD-BREAK:BREAK-ALL\">");
								sb4.append("&nbsp;" + taxAmount + "&nbsp;");
								sb4.append("</TD>");
								sb4.append("<TD ALIGN=RIGHT style=\"WORD-BREAK:BREAK-ALL\">");
								sb4.append("&nbsp;" + amountRemitted + "&nbsp;");
								sb4.append("</TD>");
							}
							//	sb3.append(temp);
							sb3.append("</TR>");
							tdDDOCount++;
							/**
							 *	Added om 5-Jan-2007
							 *	To handle java.lang.OutOfMemory Exception the folowing if condition is coded.
							 *	Since each field of TD record is added to the string buffer memory gets occupied
							 *	and there will not be enough space to write into the file. So a counter is incremneted
							 *	after every TD record is read and if the counter exceeds 1000 it is writte immediately
							 *	into the file and the counter is re-initialized to '0' and string buffer object is made
							 *	null and again re-constructed to store next set of TD records.
							 */
							if(tdDDOCount > 1000)
							{
								bw.write(sb3.toString());
								sb3 = null;
								sb3 = new StringBuffer();
								tdDDOCount = 0;
							}
						}
						//	sb3.append("</TR>");
						sb4.append("<TR>");
						sb4.append("<TD><B>Total</B></TD>");
						sb4.append("<TD>&nbsp;</TD>");
						sb4.append("<TD>&nbsp;</TD>");
						sb4.append("<TD>&nbsp;</TD>");
						sb4.append("<TD>&nbsp;</TD>");
						sb4.append("<TD>&nbsp;</TD>");
						sb4.append("<TD>&nbsp;</TD>");
						sb4.append("<TD>&nbsp;</TD>");
						sb4.append("<TD>&nbsp;</TD>");
						sb4.append("<TD>&nbsp;</TD>");
						sb4.append("<TD>&nbsp;</TD>");
						sb4.append("<TD>&nbsp;</TD>");
						sb4.append("<TD>&nbsp;</TD>");
						sb4.append("<TD ALIGN=RIGHT>");
						sb4.append("<B>"+new DecimalFormat("#0.00").format(ddoWisetotalTax1)+"</B>");
						sb4.append("</TD>");
						sb4.append("<TD ALIGN=RIGHT>");
						sb4.append("<B>"+new DecimalFormat("#0.00").format(Double.parseDouble(totalRemitted.trim()))+"</B>");
						sb4.append("</TD>");
						sb4.append("</TR>");
					    
						
						Log.tbaf_log.info("Status of the details of DDO Transaction Detail Records: Completed");
				//	}
					sb4.append("</TABLE></TD>");
					
					sb4.append("</TR>");
				}
				
			
			
		
			sb4.append("<TR>");
			sb4.append("<TD>&nbsp;</TD>");
			sb4.append("<TD>&nbsp;</TD>");
			sb4.append("</TR>");
			
			sb4.append("<TR>");
			sb4.append("<TD>&nbsp;</TD>");
			sb4.append("<TD>&nbsp;</TD>");
			sb4.append("</TR>");
			
			
			
			
			
			
			sb4.append("<TR>");
			
			
			sb4.append("<TD WIDTH=\"100%\" ALIGN=RIGHT colspan=\"2\"  STYLE=\"BORDER-TOP:NONE;BORDER-BOTTOM:NONE\">");
			sb4.append("<B><font style=\"margin-right:40px\">Signature of the AO</font></B>");
			sb4.append("</TD>");
			
			sb4.append("</TR>");
			
			
			
			

			sb4.append("<TR>");
		
			sb4.append("<TD WIDTH=\"100%\" ALIGN=LEFT colspan=\"2\" STYLE=\"BORDER-TOP:NONE;BORDER-BOTTOM:NONE\">");
			sb4.append("<B>Notes:</B>");
			sb4.append("</TD>");
			
			sb4.append("</TR>");

			
			
			sb4.append("<TR>");
		
			sb4.append("<TD WIDTH=\"100%\" colspan=\"2\" STYLE=\"BORDER-TOP:NONE;BORDER-BOTTOM:NONE\">");
			sb4.append("&nbsp;&nbsp;&nbsp;&nbsp;1. Responsible person is the person made responsible in the office of Pay and Accounts Officer (PAO) or Treasure Officer (TO) or Cheque Drawing and Disbursing Officer (CDDO) for filing of this &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;form.");
			sb4.append("</TD>");
			
			sb4.append("</TR>");

			
			sb4.append("<TR>");
			
			sb4.append("<TD WIDTH=\"100%\" colspan=\"2\" STYLE=\"BORDER-TOP:NONE;BORDER-BOTTOM:NONE\">");
			sb4.append("&nbsp;&nbsp;&nbsp;&nbsp;2. Payments pertaining to all the nature of payment TDS-Salary (24Q)/TDS Non-Salary (26Q)/TDS-Non-Resident (27Q)/TCS (27EQ) to be furnished in same form.");
			sb4.append("</TD>");
			;
			sb4.append("</TR>");

			sb4.append("<TR>");
			
			sb4.append("<TD WIDTH=\"100%\" colspan=\"2\" STYLE=\"BORDER-TOP:NONE;BORDER-BOTTOM:NONE\">");
			sb4.append("&nbsp;&nbsp;&nbsp;&nbsp;3. Furnishing of either DDO registration No. or DDO code is mandatory.");
			sb4.append("</TD>");
		
			sb4.append("</TR>");

			sb4.append("<TR>");
			
			sb4.append("<TD WIDTH=\"100%\" colspan=\"2\" STYLE=\"BORDER-TOP:NONE;BORDER-BOTTOM:NONE\">");
			sb4.append("&nbsp;&nbsp;&nbsp;&nbsp;4. There can be maximum four entries (Nature of deduction wise) per DDO in every month.");
			sb4.append("</TD>");
			
			sb4.append("</TR>");

			sb4.append("<TR>");
			
			sb4.append("<TD WIDTH=\"100%\" colspan=\"2\" STYLE=\"BORDER-TOP:NONE;BORDER-BOTTOM:NONE\">");
			sb4.append("&nbsp;&nbsp;&nbsp;&nbsp;5. This form shall be applicable only in respect of tax deducted/collected on or after 1st April, 2010.");
			sb4.append("</TD>");
			
			sb4.append("</TR>");

			sb4.append("<TR>");
			
			sb4.append("<TD WIDTH=\"100%\" colspan=\"2\" STYLE=\"BORDER-TOP:NONE;BORDER-BOTTOM:NONE\">");
			sb4.append("&nbsp;&nbsp;&nbsp;&nbsp;6.The fields marked as * are mandatory.");
			sb4.append("</TD>");
		
			sb4.append("</TR>");

			
			sb4.append("<TR>");
			sb4.append("<TD WIDTH=\"100%\" colspan=\"2\" ALIGN=LEFT  STYLE=\"BORDER-TOP:NONE;BORDER-BOTTOM:NONE\">");
			sb4.append("<TABLE BORDER=1 style=\"MARGIN-LEFT:40;\"BORDER=1 CELLSPACING=0 CELLPADDING=0 BORDERCOLOR=black  style='border-collapse: collapse; '>");
			sb4.append("<TR>");
			sb4.append("<TH><B>Sr.No.</B></TH>");
			sb4.append("<TH><B>Ministry</B></TH>");
			sb4.append("</TR>");
			sb4.append("<TR><TD>1</TD><TD ALIGN = LEFT>Civil</TD></TR>");
			sb4.append("<TR><TD>2</TD><TD ALIGN = LEFT>Railway</TD></TR>");
			sb4.append("<TR><TD>3</TD><TD ALIGN = LEFT>Defence</TD></TR>");
			sb4.append("<TR><TD>4</TD><TD ALIGN = LEFT>Telecommunication</TD></TR>");
			sb4.append("<TR><TD>5</TD><TD ALIGN = LEFT>Post</TD></TR>");
			sb4.append("</TABLE>");
			sb4.append("</TD>");
			sb4.append("</TR>");
			
			
			
	



			/*	sb3.append("<TR>");
										sb3.append("<TD>");
										sb3.append("</TD>");
										sb3.append("<TD>");
										sb3.append("&nbsp;&nbsp;&nbsp;6. Each line item in the above table for a particular DDO should give the sum of all payments made for that particular type of payment (TDS Sal, ");
										sb3.append("<BR>");
										sb3.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;TDS - NonSal, TDS NR, TCS) by that DDO for that month. (This means there will be maximum of three entries per DDO per form in every quarter");
										sb3.append("</TD>");
										sb3.append("</TR>");
										sb3.append("<TR>");
										sb3.append("<TD>");
										sb3.append("</TD>");
										sb3.append("<TD>");
										sb3.append("&nbsp;&nbsp;&nbsp;7. Month of payment in column 162 is the month of issue of cheque. E.g. if the cheque is dated 31/7/2005, month of issue will be July, 2005");
										sb3.append("</TD>");
										sb3.append("</TR>");
										sb3.append("</TABLE>");
										sb3.append("<CENTER>");
										sb3.append("<TABLE BORDER=0 ALIGN=LEFT CELLSPACING=0 CELLPADDING=0 BORDERCOLOR=000000 style='border-collapse: collapse; border: none; mso-border-alt: solid windowtext .5pt; mso-padding-alt: 0in 5.4pt 0in 5.4pt'>");
										sb3.append("<TR>");
										sb3.append("<TD ALIGN=RIGHT WIDTH=63>");
										sb3.append("8. ");
										sb3.append("</TD>");
										sb3.append("</TR>");
										sb3.append("</TABLE>");
										sb3.append("<TABLE BORDER=0 ALIGN=CENTER CELLSPACING=0 CELLPADDING=0 BORDERCOLOR=000000 style='border-collapse: collapse; border: none; mso-border-alt: solid windowtext .5pt; mso-padding-alt: 0in 5.4pt 0in 5.4pt'>");
										sb3.append("<TR>");
										sb3.append("<TD>");
							 			sb3.append("<TABLE BORDER=1 ALIGN=LEFT CELLSPACING=0 CELLPADDING=0 BORDERCOLOR=000000 style='border-collapse: collapse; border: none; mso-border-alt: solid windowtext .5pt; mso-padding-alt: 0in 5.4pt 0in 5.4pt'>");
										sb3.append("<B><TH>State</TH></B>");
										sb3.append("<B><TH>Code</TH></B>");
										sb3.append("<TR><TD>Central Government</TD><TD ALIGN = RIGHT>00</TD></TR>");
										sb3.append("<TR><TD>ANDAMAN AND NICOBAR ISLANDS</TD><TD ALIGN = RIGHT>01</TD></TR>");
										sb3.append("<TR><TD>ANDHRA PRADESH	</TD><TD ALIGN = RIGHT>02</TD></TR>");
										sb3.append("<TR><TD>ARUNACHAL PRADESH</TD><TD ALIGN = RIGHT>03</TD></TR>");
										sb3.append("<TR><TD>ASSAM</TD><TD ALIGN = RIGHT>04</TD></TR>");
										sb3.append("<TR><TD>BIHAR</TD><TD ALIGN = RIGHT>05</TD></TR>");
										sb3.append("<TR><TD>CHANDIGARH</TD><TD ALIGN = RIGHT>06</TD></TR>");
										sb3.append("<TR><TD>DADRA & NAGAR HAVELI</TD><TD ALIGN = RIGHT>07</TD></TR>");
										sb3.append("<TR><TD>DAMAN & DIU</TD><TD ALIGN = RIGHT>08</TD></TR>");
										sb3.append("<TR><TD>DELHI</TD><TD ALIGN = RIGHT>09</TD></TR>");
										sb3.append("<TR><TD>GOA</TD><TD ALIGN = RIGHT>10</TD></TR>");
										sb3.append("<TR><TD>GUJARAT</TD><TD ALIGN = RIGHT>11</TD></TR>");
										sb3.append("<TR><TD>HARYANA</TD><TD ALIGN = RIGHT>12</TD></TR>");
										sb3.append("<TR><TD>HIMACHAL PRADESH</TD><TD ALIGN = RIGHT>13</TD></TR>");
										sb3.append("<TR><TD>JAMMU & KASHMIR</TD><TD ALIGN = RIGHT>14</TD></TR>");
										sb3.append("<TR><TD>KARNATAKA</TD><TD ALIGN = RIGHT>15</TD></TR>");
										sb3.append("<TR><TD>KERALA</TD><TD ALIGN = RIGHT>16</TD></TR>");
										sb3.append("<TR><TD>LAKSHWADEEP</TD><TD ALIGN = RIGHT>17</TD></TR>");
										sb3.append("</TD>");
										sb3.append("</TABLE>");
										sb3.append("<TD>");
										sb3.append("<TABLE BORDER=0>");
										sb3.append("<TR><TD WIDTH=20></TD></TR>");
										sb3.append("<TR><TD WIDTH=20></TD></TR>");
										sb3.append("<TR><TD WIDTH=20></TD></TR>");
										sb3.append("<TR><TD WIDTH=20></TD></TR>");
										sb3.append("<TR><TD WIDTH=20></TD></TR>");
										sb3.append("<TR><TD WIDTH=20></TD></TR>");
										sb3.append("<TR><TD WIDTH=20></TD></TR>");
										sb3.append("<TR><TD WIDTH=20></TD></TR>");
										sb3.append("<TR><TD WIDTH=20></TD></TR>");
										sb3.append("<TR><TD WIDTH=20></TD></TR>");
										sb3.append("<TR><TD WIDTH=20></TD></TR>");
										sb3.append("<TR><TD WIDTH=20></TD></TR>");
										sb3.append("<TR><TD WIDTH=20></TD></TR>");
										sb3.append("<TR><TD WIDTH=20></TD></TR>");
										sb3.append("<TR><TD WIDTH=20></TD></TR>");
										sb3.append("<TR><TD WIDTH=20></TD></TR>");
										sb3.append("<TR><TD WIDTH=20></TD></TR>");
										sb3.append("<TR><TD WIDTH=20></TD></TR>");
										sb3.append("<TR><TD WIDTH=20></TD></TR>");
										sb3.append("</TABLE>");
										sb3.append("</TD>");
										sb3.append("<TD>");
										sb3.append("<TABLE BORDER=1 ALIGN=RIGHT CELLSPACING=0 CELLPADDING=0 BORDERCOLOR=000000 style='border-collapse: collapse; border: none; mso-border-alt: solid windowtext .5pt; mso-padding-alt: 0in 5.4pt 0in 5.4pt'>");
										sb3.append("<B><TH>State</TH></B>");
										sb3.append("<B><TH>Code</TH></B>");
										sb3.append("<TR><TD WIDTH=250>MADHYA PRADESH</TD><TD  ALIGN = RIGHT>18</TD></TR>");
										sb3.append("<TR><TD>MAHARASHTRA</TD><TD ALIGN = RIGHT>19</TD></TR>");
										sb3.append("<TR><TD>MANIPUR</TD><TD ALIGN = RIGHT>20</TD></TR>");
										sb3.append("<TR><TD>MEGHALAYA</TD><TD ALIGN = RIGHT>21</TD></TR>");
										sb3.append("<TR><TD>MIZORAM</TD><TD ALIGN = RIGHT>22</TD></TR>");
										sb3.append("<TR><TD>NAGALAND</TD><TD ALIGN = RIGHT>23</TD></TR>");
										sb3.append("<TR><TD>ORISSA</TD><TD ALIGN = RIGHT>24</TD></TR>");
										sb3.append("<TR><TD>PONDICHERRY</TD><TD ALIGN = RIGHT>25</TD></TR>");
										sb3.append("<TR><TD>PUNJAB</TD><TD ALIGN = RIGHT>26</TD></TR>");
										sb3.append("<TR><TD>RAJASTHAN</TD><TD ALIGN = RIGHT>27</TD></TR>");
										sb3.append("<TR><TD>SIKKIM</TD><TD ALIGN = RIGHT>28</TD></TR>");
										sb3.append("<TR><TD>TAMILNADU</TD><TD ALIGN = RIGHT>29</TD></TR>");
										sb3.append("<TR><TD>TRIPURA</TD><TD ALIGN = RIGHT>30</TD></TR>");
										sb3.append("<TR><TD>UTTAR PRADESH</TD><TD ALIGN = RIGHT>31</TD></TR>");
										sb3.append("<TR><TD>WEST BENGAL</TD><TD ALIGN = RIGHT>32</TD></TR>");
										sb3.append("<TR><TD>CHHATISHGARH</TD><TD ALIGN = RIGHT>33</TD></TR>");
										sb3.append("<TR><TD>UTTARANCHAL</TD><TD ALIGN = RIGHT>34</TD></TR>");
										sb3.append("<TR><TD>JHARKHAND</TD><TD ALIGN = RIGHT>35</TD></TR>");
										sb3.append("</TD>");
										sb3.append("</TR>");
										sb3.append("</TABLE>");
										sb3.append("</TD>");
										sb3.append("</TR>");
										sb3.append("</TABLE>");
										sb3.append("<BR>");
										sb3.append("</TD>");
										sb3.append("</TR>"); */ //Commented by Bharath
			
			
			
			sb4.append("</TABLE>");
			sb4.append("</div></BODY>");
			sb4.append("</HTML>");

			bw.write(sb4.toString());

			if (bw != null)
				bw.close();
		}
		catch (Exception e)
		{
			Log.tbaf_log.debug("Exception in generating Form 24G  "  +  e.toString());
			e.printStackTrace();
			return;
		}
	}




	public StringBuffer generateHtmlErrorFile(String errorDataString)
	{
		try
		{
			ArrayList errorDataList = parseString(errorDataString, "\n");
			Object errorDataArray[] = errorDataList.toArray();
			StringBuffer htmlErrorFileStringBuffer = new StringBuffer();
			htmlErrorFileStringBuffer = createHtmlErrorFileHeader(htmlErrorFileStringBuffer);
			for (int i = 0; i < errorDataArray.length; i++)
			{
				TBAFFileStatistics fStatistics = parseDelemitedErrorFile((String) errorDataArray[i], "^");
				htmlErrorFileStringBuffer = createErrorFile(fStatistics, htmlErrorFileStringBuffer);
			}
			htmlErrorFileStringBuffer = createHtmlErrorFileFooter(htmlErrorFileStringBuffer);
			return htmlErrorFileStringBuffer;
		}
		catch (Exception e)
		{
			Log.tbaf_log.error(" Exception in FileGenerator.java generateHtmlErrorFile Method : " + e.toString());
			StringBuffer htmlBuffer = new StringBuffer();
			return htmlBuffer;
		}
	}
	public StringBuffer generateHtmlErrorFile(String errorDataString, boolean appedHtmlErrorFileHeader, boolean appedHtmlErrorFileFooter, String inputFileName)
	{
		try
		{
			ArrayList errorDataList = parseString(errorDataString, "\n");
			Object errorDataArray[] = errorDataList.toArray();
			StringBuffer htmlErrorFileStringBuffer = new StringBuffer();
			if (appedHtmlErrorFileHeader)
			{
				htmlErrorFileStringBuffer = createHtmlErrorFileHeader(htmlErrorFileStringBuffer);
			}
			for (int i = 0; i < errorDataArray.length; i++)
			{
				TBAFFileStatistics fStatistics = parseDelemitedErrorFile((String) errorDataArray[i], "^");
				htmlErrorFileStringBuffer = createErrorFile(fStatistics, htmlErrorFileStringBuffer);
			}
			if (appedHtmlErrorFileFooter)
			{
				htmlErrorFileStringBuffer = createHtmlErrorFileFooter(htmlErrorFileStringBuffer, inputFileName);
			}
			return htmlErrorFileStringBuffer;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			Log.tbaf_log.error(" Exception in FileGenerator.java generateHtmlErrorFile Method : " + e.toString());
			StringBuffer htmlBuffer = new StringBuffer();
			return htmlBuffer;
		}
	}
	public ArrayList parseString(String toParse, String Seperator)
	{
		ArrayList aList = new ArrayList();
		StringTokenizer parsed = new StringTokenizer(toParse, Seperator);
		while (parsed.hasMoreTokens())
		{
			aList.add(parsed.nextElement());
		}
		return aList;
	}
	/**
	 * Method to Create HTML Error File
	 */
	private StringBuffer createErrorFile(TBAFFileStatistics fStatistics, StringBuffer toReturn)
	{
		try
		{
			toReturn.append("<TR>");
			toReturn.append("<TD ALIGN=RIGHT WIDTH=70> " + fStatistics.getLineNo() + "</TD>");
			toReturn.append("<TD ALIGN=LEFT WIDTH=85> " + fStatistics.getTbafRecType() + " </TD>");
			toReturn.append("<TD ALIGN=LEFT WIDTH=50> " + fStatistics.getBatchNo() + " </TD>");
			toReturn.append("<TD ALIGN=LEFT WIDTH=85> " + fStatistics.getTransactionNo() + " </TD>");
			toReturn.append("<TD ALIGN=LEFT WIDTH=130> " + fStatistics.getErrorCode() + " </TD>");
			toReturn.append("<TD ALIGN=LEFT WIDTH=585> " + fStatistics.getErrorDescription() + " </TD>");
			toReturn.append("</TR>");
			return toReturn;
		}
		catch (Exception e)
		{
			Log.tbaf_log.error("Exception in FileGenerator.java createErrorFileString() method : " + e.toString());
			return null;
		}
	}
	private StringBuffer createHtmlErrorFileHeader(StringBuffer toReturn)
	{
		// FORMAT OF HTML Error File - Header
		toReturn.append("<HTML>");
		//toReturn.append("<HEAD> <TITLE> Form 24G ERROR FILE </TITLE></HEAD>");
		toReturn.append("<BODY>");
		//Gauri added a change here for CR 89435
		toReturn.append("<H3><CENTER> Form 24G / Form 137 - ERROR FILE </CENTER></H3>");
		toReturn.append(
		"<TABLE BORDER=1 CELLSPACING=0 CELLPADDING=0 BORDERCOLOR=000000 style='border-collapse:collapse; border:none;mso-border-alt:solid windowtext .5pt;mso-padding-alt:0in 5.4pt 0in 5.4pt'>");
		// First Table
		toReturn.append("<TR>");
		toReturn.append("<TD WIDTH=70 VALIGN=top style='text-align:right' ><B> " + "  Line No " + "</B></CENTER></TD>");
		toReturn.append("<TD WIDTH=85 VALIGN=top><B> " + " Record Type " + "</B></CENTER></TD>");
		toReturn.append("<TD WIDTH=85 VALIGN=top><B> " + " Field Name & No.* " + "</B></CENTER></TD>");
		toReturn.append("<TD WIDTH=85 VALIGN=top><B> " + " Transaction Detail No " + "</B></CENTER></TD>");
		toReturn.append("<TD WIDTH=150 VALIGN=top><B> " + " Error Code " + "</B></CENTER></TD>"); //130 was before
		toReturn.append("<TD WIDTH=585 VALIGN=top><B> " + " Error Description " + "</B></CENTER></TD>");
		toReturn.append("</TR>");
		return toReturn;
	}
	/*private void createStatisticFile(TBAFFormatValidator obj_FrmValidator,TBAFFileStatistics fStatistics, String statisticFileName, String fileName, String FVUVersion)
	{
		try
		{
			

			StringBuffer toReturn = new StringBuffer();
			Log.tbaf_log.info("FileGenerator.java createStatisticFileString() method is called");
			toReturn.append("<HTML>");
			toReturn.append("<HEAD>");
			//toReturn.append("<TITLE>FVU Form 24G STATEMENT STATISTICS REPORT</TITLE>");  //Commented by Subhankar as the title is not needed as per client confirmation
			toReturn.append("<TITLE></TITLE>");
			toReturn.append("<style TYPE = \"text/css\">  TD { FONT-SIZE:12; } </style>");
			toReturn.append("</HEAD>");
			toReturn.append("<BODY>");
			toReturn.append("<TABLE BORDER=1 WIDTH=100% BORDERCOLOR=\"000000\" CELLSPACING=1 CELLPADDING=0 "
					+ "style='border-collapse: collapse; '>");
			toReturn.append("<TR>");
			toReturn.append("<TD>");
			toReturn.append("<TABLE BORDER=1 WIDTH=100% BORDERCOLOR=\"000000\" CELLSPACING=1 CELLPADDING=0 "
					+ "style='border-collapse: collapse; '>");
			toReturn.append("<TR>");
			toReturn.append("<TD>");
			toReturn.append("<FONT SIZE = 4>");
			toReturn.append("<CENTER><B>Form 24G Statement Statistic Report</B></CENTER>");
			toReturn.append("</FONT>");
			toReturn.append("</TD>");
			toReturn.append("</TR>");
			toReturn.append("<TR>");
			toReturn.append("<TD>");

			toReturn.append("<B><CENTER>To be submitted with Form 24G</B></CENTER>");
			//toReturn.append("</FONT>");
			toReturn.append("<BR>");
			toReturn.append("</TD>");
			toReturn.append("</TR>");
			toReturn.append("<TR><TD>The details shown in the report are as per the statement prepared by you. In case any discrepancy in the details shown is observed,"
					+ " the statement should be corrected accordingly. After such changes statement should be validated again through the FVU.<BR> "
					+ "Kindly ensure AIN details are as communicated by Directorate of Income Tax (Systems).<BR></TD> </TR> ");
			toReturn.append("</TABLE>");
			toReturn.append("<BR>");
			toReturn.append("<TABLE BORDER=1 WIDTH=100% CELLSPACING=1 CELLPADDING=0 BORDERCOLOR=000000 style='border-collapse: collapse; '> ");
			toReturn.append("<TR>");
			toReturn.append("<TD WIDTH=80% VALIGN=TOP><B> Name of Accounts Office </B> </CENTER> </TD> ");
			toReturn.append("<TD WIDTH=20% VALIGN=TOP><B>  AIN </B>	</CENTER> </TD>");
			toReturn.append("</TR>");
			toReturn.append("<TR>");
			toReturn.append("<TD ALIGN=LEFT>" + fStatistics.getNameOfAO() + "</TD>");
			toReturn.append("<TD ALIGN=LEFT>" + fStatistics.getAIN() + "</TD>");
			toReturn.append("</TR>");
			toReturn.append("</TABLE>");
			toReturn.append("<P>");
			toReturn.append("<P>");
			if (!fStatistics.getTransactionType().equals(TBAF_TRANSACTION_TYPE_C3))
			{
				toReturn.append("<TABLE BORDER=1 WIDTH=100% CELLSPACING=1 CELLPADDING=0 BORDERCOLOR=000000 style='border-collapse: collapse; '> ");
				toReturn.append("<TR>");
				toReturn.append("<TD VALIGN=TOP><B> Address of Accounts Office </B> </CENTER> </TD> ");
				toReturn.append("</TR>");
				toReturn.append("<TR>");
				toReturn.append("<TD ALIGN=LEFT VALIGN=TOP WIDTH=100% style=\"WORD-BREAK:BREAK-ALL\">");
				toReturn.append(fStatistics.getAoAdd1());
				toReturn.append(", ");
				if (!fStatistics.getAoAdd2().equals(""))
				{
					toReturn.append(fStatistics.getAoAdd2());
					toReturn.append(", ");
				}
				if (!fStatistics.getAoAdd3().equals(""))
				{
					toReturn.append(fStatistics.getAoAdd3());
					toReturn.append(", ");
				}
				if (!fStatistics.getAoAdd4().equals(""))
				{
					toReturn.append(fStatistics.getAoAdd4());
					toReturn.append(", ");
				}
				toReturn.append(fStatistics.getAoCity());
				toReturn.append(", ");
				toReturn.append(TBAF_STATE_NAME[Integer.parseInt(fStatistics.getAoState().trim())]);
				toReturn.append(", ");
				toReturn.append(fStatistics.getAoPIN());
				toReturn.append("</TD>");
				toReturn.append("</TR>");
				toReturn.append("</TABLE>");
			}
			toReturn.append("<P>");

			//ADDED BY SUBHANKAR


			//Statistic Report Generation for X correction

			if(fStatistics.getTypeOfStatement().equals(TBAF_TYPE_OF_STMT_CORR) && fStatistics.getTransactionType().equals(TBAF_TRANSACTION_TYPE_X))
			{
				toReturn.append("<TABLE BORDER=1 WIDTH=100% CELLSPACING=1 CELLPADDING=0 BORDERCOLOR=000000 "
						+ "style='border-collapse: collapse; '>");
				toReturn.append("<TR>");
				toReturn.append("<TD VALIGN=TOP WIDTH = 35%><B> Financial Year </B></TD>");
				int financialYear = Integer.parseInt(fStatistics.getFinancialYear().substring(2, 4));
				if (financialYear >= 9 && financialYear < 99)
				{
					financialYear = financialYear + 1;
					String finYear = String.valueOf(financialYear);
					toReturn.append("<TD ALIGN=LEFT WIDTH = 15%>" + fStatistics.getFinancialYear() + "-" + finYear + "</TD>");
				}
				else if (financialYear == 99)
				{
					toReturn.append("<TD ALIGN=LEFT WIDTH = 15%>" + fStatistics.getFinancialYear() + "-" + "00" + "</TD>");
				}
				else
				{
					financialYear = financialYear + 1;
					String finYear = String.valueOf(financialYear);
					toReturn.append("<TD ALIGN=LEFT WIDTH = 15%>" + fStatistics.getFinancialYear() + "-" + "0" + finYear + "</TD>");
				}

				toReturn.append("<TD VALIGN=TOP WIDTH = 32%><B> Type of Statement </B></TD>");
				toReturn.append("<TD  ALIGN=RIGHT WIDTH = 18%> Correction </TD>");
				toReturn.append("</TR>");
				toReturn.append("<TR>");
				toReturn.append("<TD VALIGN=TOP WIDTH = 35%><B> Month </B></TD>");
				toReturn.append("<TD ALIGN=LEFT WIDTH = 15%>" + MONTH[Integer.parseInt(fStatistics.getMonthOfTransaction())] + "</TD>");
				toReturn.append("<TD VALIGN=TOP WIDTH = 32%><B> Type of Transaction </B></TD>");
				toReturn.append("<TD  ALIGN=RIGHT WIDTH = 18%> Cancellation(X) </TD>");
				toReturn.append("</TR>");

				toReturn.append("<TR>");
				toReturn.append("<TD VALIGN=TOP WIDTH = 40%><B> Category of AO: </B></TD>");
				toReturn.append("<TD ALIGN=LEFT WIDTH = 60% colspan =3>" + Parameters.GovtMap.get(fStatistics.getDeductorCat().trim()) + "</TD>");
				toReturn.append("</TR>");
				toReturn.append("<TR>");
				toReturn.append("<TD VALIGN=TOP WIDTH = 40%><B> Name of Ministry/State </B></TD>");
				if(fStatistics.getDeductorCat().trim().equals("A"))
				{

					toReturn.append("<TD ALIGN=LEFT WIDTH = 60% colspan =3>" +TBAF_MINISTRY_NAME[Integer.parseInt(fStatistics.getMinistryName().trim())]+ "</TD>");
				}
				else
				{
					toReturn.append("<TD ALIGN=LEFT WIDTH = 60% colspan =3>" +TBAF_STATE_NAME[Integer.parseInt(fStatistics.getStateName().trim())]+ "</TD>");
				}
				toReturn.append("</TR>");
				toReturn.append("<TR>");
				toReturn.append("<TD VALIGN=TOP WIDTH = 40%><B> Name of Sub Ministry </B></TD>");
				if(fStatistics.getDeductorCat().trim().equals("A"))
				{
					if(Integer.parseInt(fStatistics.getMinistryName().trim()) == 1 && Integer.parseInt(fStatistics.getSubMinistryName().trim()) != 99)
					{
						toReturn.append("<TD style = \" WORD-BREAK:BREAK-ALL \" ALIGN=LEFT WIDTH = 60% colspan =3>" +TBAF_SUB_MINISTRY_NAME[Integer.parseInt(fStatistics.getSubMinistryName().trim())]+ "</TD>");
					}
					else if(! fStatistics.getSubMinistryName().trim().equals("-") && Integer.parseInt(fStatistics.getSubMinistryName().trim()) == 99)
					{
						toReturn.append("<TD style = \" WORD-BREAK:BREAK-ALL \" ALIGN=LEFT WIDTH = 60% colspan =3>" +fStatistics.getSubMinistryName_O()+ "</TD>");
					}
					else
					{
						toReturn.append("<TD style = \" WORD-BREAK:BREAK-ALL \"  ALIGN=LEFT WIDTH = 60% colspan =3>" +"-"+ "</TD>");
					}
				}
				else
				{
					toReturn.append("<TD style = \" WORD-BREAK:BREAK-ALL \" ALIGN=LEFT WIDTH = 60% colspan =3>" +"-"+ "</TD>");
				}
				toReturn.append("</TR>");
				toReturn.append("</TABLE>");
				toReturn.append("<BR></BR>");
				toReturn.append("<BR></BR>");
				toReturn.append("<B>"+ "TO BE FILLED IN BY THE AO: "+"</B>");

				toReturn.append("<CENTER><B><U>VERIFICATION</B></U></CENTER>");
				toReturn.append("<BR></BR>");
				toReturn.append("</B></FONT>");
				toReturn.append("&nbsp;I,&nbsp;" +"________________________________________________"+"&nbsp;&nbsp;" + "hereby certify that all the" +
				" particulars furnished above are correct and complete.");
				toReturn.append("<BR>");
				toReturn.append("<TABLE>");
				toReturn.append("<TR>");
				toReturn.append("<TD>" + "Place: " + "</TD>");
				toReturn.append("</TR>");
				toReturn.append("<TR>");
				toReturn.append("<TD>" + "Date: " + "</TD>");
				toReturn.append("<TD ALIGN=RIGHT WIDTH = 950>" + "Signature of person responsible for furnishing Form 24G" + "</TD>");
				toReturn.append("</TR>");
				toReturn.append("</TABLE>");
				toReturn.append("<BR>");
				//toReturn.append("&nbsp;&nbsp;" + "*Name");
				toReturn.append("&nbsp;&nbsp;" + "*Name & Designation ________________________________________________________________________________" );
				toReturn.append("<BR></BR>" + "&nbsp;&nbsp;" + "Signature __________________________________________________________________________________________");
				toReturn.append("<TABLE>");
				toReturn.append("<TR>");
				toReturn.append("<TD>");
				toReturn.append("&nbsp;" + "<B>Notes :</B>");
				toReturn.append("<BR>" + "*To be counter signed by the person who is his immediate superior in case "
						+ "the PAO is also a DDO whose details are being given in the statement.");
				toReturn.append("</TD>");
				toReturn.append("</TR>");
				//toReturn.append("<BR>");
				toReturn.append("<TABLE>");
				toReturn.append("&nbsp;FVU Version : " + FVUVersion + " &nbsp;&nbsp;&nbsp; Input File Name : " + fileName + " <BR>");
				toReturn.append("</TD>");
				toReturn.append("</TR>");
				toReturn.append("</TABLE>");
				toReturn.append("</TABLE>");
				toReturn.append("</BODY>");
				toReturn.append("</HTML>");
			}
			//End of statisticReport Generation for X correction
			else
			{
				toReturn.append("<TABLE BORDER=1 WIDTH=100% CELLSPACING=1 CELLPADDING=0 BORDERCOLOR=000000 "
						+ "style='border-collapse: collapse; '>");


				toReturn.append("<TR>");
				toReturn.append("<TD VALIGN=TOP WIDTH = 35%><B> Financial Year </B></TD>");
				int financialYear = Integer.parseInt(fStatistics.getFinancialYear().substring(2, 4));
				if (financialYear >= 9 && financialYear < 99)
				{
					financialYear = financialYear + 1;
					String finYear = String.valueOf(financialYear);
					toReturn.append("<TD ALIGN=LEFT WIDTH = 15%>" + fStatistics.getFinancialYear() + "-" + finYear + "</TD>");
				}
				else if (financialYear == 99)
				{
					toReturn.append("<TD ALIGN=LEFT WIDTH = 15%>" + fStatistics.getFinancialYear() + "-" + "00" + "</TD>");
				}
				else
				{
					financialYear = financialYear + 1;
					String finYear = String.valueOf(financialYear);
					toReturn.append("<TD ALIGN=LEFT WIDTH = 15%>" + fStatistics.getFinancialYear() + "-" + "0" + finYear + "</TD>");
				}



				toReturn.append("<TD VALIGN=TOP WIDTH = 32%><B> Count of Distinct DDOs (valid TAN) </B></TD>");
				toReturn.append("<TD  ALIGN=RIGHT WIDTH = 18%><B>"+fStatistics.getCountOfDistinctTD()+"</B></TD>");

				toReturn.append("</TR>");

				toReturn.append("<TR>");
				
				toReturn.append("<TD VALIGN=TOP WIDTH = 35%><B> Month </B></TD>");
				toReturn.append("<TD ALIGN=LEFT WIDTH = 15%>" + MONTH[Integer.parseInt(fStatistics.getMonthOfTransaction())] + "</TD>");


				toReturn.append("<TD VALIGN=TOP WIDTH = 32%><B> Count of  DDO records with valid TAN </B></TD>");
				toReturn.append("<TD ALIGN=RIGHT WIDTH = 18%>" + fStatistics.getCountOfValidTAN() + "</TD>");

				toReturn.append("</TR>");


				toReturn.append("<TR>");

					if (fStatistics.getTypeOfStatement().equals(TBAF_TYPE_OF_STMT_ORIG))
			{
				toReturn.append("<TD VALIGN=TOP WIDTH=195><B> Type of Statement </B></TD>");
			}
			else
			{
				toReturn.append("<TD VALIGN=TOP><B> Type of Correction </B></TD>");
				//toReturn.append("<TD VALIGN=TOP><B> Type of Statement </B></TD>");
			}

			toReturn.append("<TD ALIGN=LEFT WIDTH=120>" + fStatistics.getMonthOfTransaction() + "</TD>");

				 

				toReturn.append("<TD VALIGN=TOP WIDTH = 35%><B> Type of Statement </B></TD>");

				if (fStatistics.getTypeOfStatement().equals(TBAF_TYPE_OF_STMT_ORIG))
				{
					toReturn.append("<TD ALIGN=LEFT WIDTH=15%> Original </TD>");
				}
				else
				{
					toReturn.append("<TD VALIGN=TOP WIDTH = 15%> Correction </TD>");
					//toReturn.append("<TD VALIGN=TOP><B> Type of Statement </B></TD>");
				}


				toReturn.append("<TD VALIGN=TOP WIDTH = 32%><B> Count of  DDO records with invalid TAN </B></TD>");
				int cd=0;
				int vTan=0;
				if(!(fStatistics.getCountOfTD()).equals("-"))
				{
					cd=Integer.parseInt(fStatistics.getCountOfTD());
				}
				if(!(fStatistics.getCountOfValidTAN()).equals("-"))
				{
					vTan=Integer.parseInt(fStatistics.getCountOfValidTAN());
				}
				int invTan=cd-vTan;
				toReturn.append("<TD ALIGN=RIGHT WIDTH = 18%>" + invTan + "</TD>");

				toReturn.append("</TR>");




				if (fStatistics.getTypeOfStatement().equals(TBAF_TYPE_OF_STMT_CORR))
				{
					toReturn.append("<TR>");
					toReturn.append("<TD ALIGN=LEFT WIDTH=40%><B> Type of Transaction </B></TD>");


					if (fStatistics.getTransactionType().equals(TBAF_TRANSACTION_TYPE_C1))
					{
						toReturn.append("<TD ALIGN=LEFT colspan=3 WIDTH=60%> C1 - Correction in Accounts Officer details </TD>");
					}
					else if (fStatistics.getTransactionType().equals(TBAF_TRANSACTION_TYPE_C2))
					{
						toReturn.append("<TD ALIGN=LEFT colspan=3 WIDTH=60%> C2 - Correction in DDO and/or Accounts Officer details </TD>");
					}
					else if (fStatistics.getTransactionType().equals(TBAF_TRANSACTION_TYPE_C3))
					{
						toReturn.append("<TD ALIGN=LEFT colspan=3 WIDTH=60%> C3 - Correction in DDO details  </TD>");
					}
					else if(fStatistics.getTransactionType().equals(TBAF_TRANSACTION_TYPE_M))
					{
						toReturn.append("<TD ALIGN=LEFT colspan=3 WIDTH=60%> M  </TD>");
					}
					else if(fStatistics.getTransactionType().equals(TBAF_TRANSACTION_TYPE_X))
					{
						toReturn.append("<TD ALIGN=LEFT colspan=3 WIDTH=60%> X </TD>");
					}
					else
					{
						toReturn.append("<TD ALIGN=LEFT colspan=3 WIDTH=60%> C4 - Correction in AIN and /or statement details </TD>");
					}
					toReturn.append("</TR>");
				}
				else
			{
				toReturn.append("<TD ALIGN=LEFT WIDTH = 15%> Original </TD>");
			} 

			toReturn.append("<TD VALIGN=TOP WIDTH = 35%><B> Count of  DDO records with invalid TAN </B></TD>");
			toReturn.append("<TD ALIGN=RIGHT WIDTH = 15%>" + (Integer.parseInt(fStatistics.getCountOfTD()) - Integer.parseInt(fStatistics.getCountOfValidTAN())) + "</TD>");


			toReturn.append("</TR>");

				 

				toReturn.append("<TR>");


				toReturn.append("<TD VALIGN=TOP WIDTH = 40%><B> Category of AO: </B></TD>");
				toReturn.append("<TD ALIGN=LEFT WIDTH = 60% colspan =3>" + Parameters.GovtMap.get(fStatistics.getDeductorCat().trim()) + "</TD>");

				toReturn.append("</TR>");

				toReturn.append("<TR>");

				toReturn.append("<TD VALIGN=TOP WIDTH = 40%><B> Name of Ministry/State </B></TD>");
				if(fStatistics.getDeductorCat().trim().equals("A"))
				{

					toReturn.append("<TD ALIGN=LEFT WIDTH = 60% colspan =3>" +TBAF_MINISTRY_NAME[Integer.parseInt(fStatistics.getMinistryName().trim())]+ "</TD>");
				}
				else
				{
					toReturn.append("<TD ALIGN=LEFT WIDTH = 60% colspan =3>" +TBAF_STATE_NAME[Integer.parseInt(fStatistics.getStateName().trim())]+ "</TD>");
				}
				toReturn.append("</TR>");


				toReturn.append("<TR>");
				toReturn.append("<TD VALIGN=TOP WIDTH = 40%><B> Name of Sub Ministry </B></TD>");

				if(fStatistics.getDeductorCat().trim().equals("A"))
				{
					if(Integer.parseInt(fStatistics.getMinistryName().trim()) == 1 && Integer.parseInt(fStatistics.getSubMinistryName().trim()) != 99)
					{
						toReturn.append("<TD style = \" WORD-BREAK:BREAK-ALL \" ALIGN=LEFT WIDTH = 60% colspan =3>" +TBAF_SUB_MINISTRY_NAME[Integer.parseInt(fStatistics.getSubMinistryName().trim())]+ "</TD>");
					}
					else if(! fStatistics.getSubMinistryName().trim().equals("-") && Integer.parseInt(fStatistics.getSubMinistryName().trim()) == 99)
					{
						toReturn.append("<TD style = \" WORD-BREAK:BREAK-ALL \" ALIGN=LEFT WIDTH = 60% colspan =3>" +fStatistics.getSubMinistryName_O()+ "</TD>");
					}
					else
					{
						toReturn.append("<TD style = \" WORD-BREAK:BREAK-ALL \"  ALIGN=LEFT WIDTH = 60% colspan =3>" +"-"+ "</TD>");
					}
				}
				else
				{
					toReturn.append("<TD style = \" WORD-BREAK:BREAK-ALL \" ALIGN=LEFT WIDTH = 60% colspan =3>" +"-"+ "</TD>");
				}




				toReturn.append("</TR>");
				toReturn.append("</TABLE>");


					if (fStatistics.getTypeOfStatement().equals(TBAF_TYPE_OF_STMT_CORR))
			{
				toReturn.append("<TD VALIGN=TOP><B> Type of Correction </B></TD>");
			} 


				if (fStatistics.getTypeOfStatement().equals(TBAF_TYPE_OF_STMT_ORIG))
			{
				toReturn.append("<TD VALIGN=TOP WIDTH=205><B> Nature of Deduction </B></TD>");
			}
			else
			{
				toReturn.append("<TD VALIGN=TOP><B> Nature of Deduction </B></TD>");
			} 
				if (fStatistics.getTypeOfStatement().equals(TBAF_TYPE_OF_STMT_CORR))
			{
				toReturn.append("<TD VALIGN=TOP WIDTH=216><B> Category of Deductor</B></TD>");
			}
			else
			{
				toReturn.append("<TD VALIGN=TOP WIDTH=305><B> Category of Deductor</B></TD>");
			}

						toReturn.append("<TD VALIGN=TOP><B> Catagory Of Deductor </B></TD>");
			toReturn.append("<TD VALIGN=TOP><B> Name of Ministry/State </B></TD>");  

						toReturn.append("</TR>");
			toReturn.append("<TR>");
			int financialYear = Integer.parseInt(fStatistics.getFinancialYear().substring(2, 4));
			if (financialYear >= 9 && financialYear < 99)
			{
				financialYear = financialYear + 1;
				String finYear = String.valueOf(financialYear);
				toReturn.append("<TD ALIGN=CENTER WIDTH=150>" + fStatistics.getFinancialYear() + "-" + finYear + "</TD>");
			}
			else if (financialYear == 99)
			{
				toReturn.append("<TD ALIGN=LEFT WIDTH=150>" + fStatistics.getFinancialYear() + "-" + "00" + "</TD>");
			}
			else
			{
				financialYear = financialYear + 1;
				String finYear = String.valueOf(financialYear);
				toReturn.append("<TD ALIGN=LEFT WIDTH=150>" + fStatistics.getFinancialYear() + "-" + "0" + finYear + "</TD>");
			}
			toReturn.append("<TD ALIGN=LEFT WIDTH=120>" + fStatistics.getMonthOfTransaction() + "</TD>");
			if (fStatistics.getTypeOfStatement().equals(TBAF_TYPE_OF_STMT_CORR))
			{
				toReturn.append("<TD ALIGN=LEFT WIDTH=120> Correction </TD>");
				if (fStatistics.getTransactionType().equals(TBAF_TRANSACTION_TYPE_C1))
				{
					toReturn.append("<TD ALIGN=LEFT WIDTH=250> C1 - Correction in Accounts Officer details </TD>");
				}
				else if (fStatistics.getTransactionType().equals(TBAF_TRANSACTION_TYPE_C2))
				{
					toReturn.append("<TD ALIGN=LEFT WIDTH=250> C2 - Correction in DDO and/or Accounts Officer details </TD>");
				}
				else if (fStatistics.getTransactionType().equals(TBAF_TRANSACTION_TYPE_C3))
				{
					toReturn.append("<TD ALIGN=LEFT WIDTH=250> C3 - Correction in DDO details  </TD>");
				}
				else
				{
					toReturn.append("<TD ALIGN=LEFT WIDTH=250> C4 - Correction in AIN and /or statement details </TD>");
				}
			}
			else
			{
				toReturn.append("<TD ALIGN=LEFT WIDTH=120> Original </TD>");
			}  

				 if (fStatistics.getNatureOfDed().equals(TBAF_FORM_24Q))
			{
				toReturn.append("<TD ALIGN=LEFT WIDTH=120>" + "TDS-SAL " + "(" + fStatistics.getNatureOfDed() + ")" + "</TD>");
			}
			else if (fStatistics.getNatureOfDed().equals(TBAF_FORM_26Q))
			{
				toReturn.append("<TD ALIGN=LEFT WIDTH=120>" + "TDS-NON-SAL " + "(" + fStatistics.getNatureOfDed() + ")" + "</TD>");
			}
			else if (fStatistics.getNatureOfDed().equals(TBAF_FORM_27Q))
			{
				toReturn.append("<TD ALIGN=LEFT WIDTH=120>" + "TDS-NR " + "(" + fStatistics.getNatureOfDed() + ")" + "</TD>");
			}
			else if (fStatistics.getNatureOfDed().equals(TBAF_FORM_27EQ))
			{
				toReturn.append("<TD ALIGN=LEFT WIDTH=120>" + "TCS " + "(" + fStatistics.getNatureOfDed() + ")" + "</TD>");
			} 

						toReturn.append("<TD ALIGN=LEFT WIDTH = 166>" + Parameters.GovtMap.get(fStatistics.getDeductorCat().trim()) + "</TD>");

			if(fStatistics.getDeductorCat().trim().equals("A"))
			{

				toReturn.append("<TD ALIGN=LEFT WIDTH = 166>" +TBAF_MINISTRY_NAME[Integer.parseInt(fStatistics.getMinistryName().trim())]+ "</TD>");
			}
			else
			{
			toReturn.append("<TD ALIGN=LEFT WIDTH = 166>" +TBAF_STATE_NAME[Integer.parseInt(fStatistics.getStateName().trim())]+ "</TD>");
			}
			toReturn.append("</TR>");
			toReturn.append("</TABLE>");
			toReturn.append("<BR>");   





				toReturn.append("<BR>");
				toReturn.append("<BR>");
				if ( (fStatistics.getTransactionType().equals(TBAF_TRANSACTION_TYPE_C1)) || (fStatistics.getTransactionType().equals(TBAF_TRANSACTION_TYPE_X)) )
				{
					
					 * Incase of C1 or X (calcellation) CORRECTION, Count of transactions, Total value of all Transactions(Rs.) and Count of Transacting Parties (Distinct TANs)
					 * is not shown in Statistics Report
					 * 
					 * As in Cancellation i.e. X correction in particular there are no TD records Possible
					 * 
					 
				}

				else if((fStatistics.getTransactionType().equals(TBAF_TRANSACTION_TYPE_M)))

				{
					toReturn.append("<TABLE BORDER=1 WIDTH=100% CELLSPACING=1 CELLPADDING=0 BORDERCOLOR=000000 "
							+ "style='border-collapse: collapse; '>");
					toReturn.append("<TR>");
					toReturn.append("<TD VALIGN=TOP WIDTH=10%><B>Nature of Deduction</B></TD>");
					toReturn.append("<TD VALIGN=TOP WIDTH=15%><B>Count of DDO Records Added(A)</B></TD>");
					toReturn.append("<TD VALIGN=TOP WIDTH=15%><B>TDS/TCS remitted to Govt. Account </B> &nbsp; (<img src =\"rp.png\" />)(B)</TD>  </TD>");
					toReturn.append("<TD VALIGN=TOP WIDTH=15%><B>Count of DDO Records Updated(C)</B></TD>");
					toReturn.append("<TD VALIGN=TOP WIDTH=15%><B>TDS/TCS remitted to Govt. Account </B> &nbsp; (<img src =\"rp.png\" />)(D)</TD>  </TD>");
					toReturn.append("<TD VALIGN=TOP WIDTH=10%><B>Count of DDO Records Deleted(E)</B></TD>");
					toReturn.append("<TD VALIGN=TOP WIDTH=10%><B>TDS/TCS remitted to Govt. Account </B> &nbsp; (<img src =\"rp.png\" />)(F)</TD>  </TD>");
					toReturn.append("<TD VALIGN=TOP WIDTH=10%><B>Total TDS/TCS remitted to Govt. Account </B> &nbsp; (<img src =\"rp.png\" />)(G=B+D-F)</TD>  </TD>");
					
					toReturn.append("</TR>");
					toReturn.append("<TR>");
					toReturn.append("<TD ALIGN=LEFT>" + "TDS Salary-24Q" + "</TD>");
					toReturn.append("<TD ALIGN=RIGHT >" + obj_FrmValidator.cBean.getTotalTDAddedIn24Q() + "</TD>");
					toReturn.append("<TD ALIGN=RIGHT >" + obj_FrmValidator.cBean.getRemittedAmtAddedTD24Q() + "</TD>");
					toReturn.append("<TD ALIGN=RIGHT >" + obj_FrmValidator.cBean.getTotalTDUpdatedIn24Q() + "</TD>");
					toReturn.append("<TD ALIGN=RIGHT >" + obj_FrmValidator.cBean.getRemittedAmtUpdatedTD24Q() + "</TD>");
					toReturn.append("<TD ALIGN=RIGHT >" + obj_FrmValidator.cBean.getTotalTDDeletedIn24Q() + "</TD>");
					toReturn.append("<TD ALIGN=RIGHT >" + obj_FrmValidator.cBean.getRemittedAmtDeletedTD24Q() + "</TD>");
					toReturn.append("<TD ALIGN=RIGHT >" + (obj_FrmValidator.cBean.getRemittedAmtAddedTD24Q()+obj_FrmValidator.cBean.getRemittedAmtUpdatedTD24Q()-obj_FrmValidator.cBean.getRemittedAmtDeletedTD24Q())+ "</TD>");
					toReturn.append("</TR>");




					toReturn.append("<TR>");
					toReturn.append("<TD ALIGN=LEFT>" + "TDS Non Salary-26Q" + "</TD>");
					toReturn.append("<TD ALIGN=RIGHT >" + obj_FrmValidator.cBean.getTotalTDAddedIn26Q() + "</TD>");
					toReturn.append("<TD ALIGN=RIGHT >" + obj_FrmValidator.cBean.getRemittedAmtAddedTD26Q() + "</TD>");
					toReturn.append("<TD ALIGN=RIGHT >" + obj_FrmValidator.cBean.getTotalTDUpdatedIn26Q() + "</TD>");
					toReturn.append("<TD ALIGN=RIGHT >" + obj_FrmValidator.cBean.getRemittedAmtUpdatedTD26Q() + "</TD>");
					toReturn.append("<TD ALIGN=RIGHT >" + obj_FrmValidator.cBean.getTotalTDDeletedIn26Q() + "</TD>");
					toReturn.append("<TD ALIGN=RIGHT >" + obj_FrmValidator.cBean.getRemittedAmtDeletedTD26Q() + "</TD>");
					toReturn.append("<TD ALIGN=RIGHT >" + (obj_FrmValidator.cBean.getRemittedAmtAddedTD26Q()+obj_FrmValidator.cBean.getRemittedAmtUpdatedTD26Q()-obj_FrmValidator.cBean.getRemittedAmtDeletedTD26Q()) + "</TD>");
					toReturn.append("</TR>");





					toReturn.append("<TR>");
					toReturn.append("<TD ALIGN=LEFT>" + "DS Non Salary Non Resident - 27Q" + "</TD>");
					toReturn.append("<TD ALIGN=RIGHT >" + obj_FrmValidator.cBean.getTotalTDAddedIn27Q() + "</TD>");
					toReturn.append("<TD ALIGN=RIGHT >" + obj_FrmValidator.cBean.getRemittedAmtAddedTD27Q() + "</TD>");
					toReturn.append("<TD ALIGN=RIGHT >" + obj_FrmValidator.cBean.getTotalTDUpdatedIn27Q() + "</TD>");
					toReturn.append("<TD ALIGN=RIGHT >" + obj_FrmValidator.cBean.getRemittedAmtUpdatedTD27Q() + "</TD>");
					toReturn.append("<TD ALIGN=RIGHT >" + obj_FrmValidator.cBean.getTotalTDDeletedIn27Q() + "</TD>");
					toReturn.append("<TD ALIGN=RIGHT >" + obj_FrmValidator.cBean.getRemittedAmtDeletedTD27Q() + "</TD>");
					toReturn.append("<TD ALIGN=RIGHT >" + (obj_FrmValidator.cBean.getRemittedAmtAddedTD27Q()+obj_FrmValidator.cBean.getRemittedAmtUpdatedTD27Q()-obj_FrmValidator.cBean.getRemittedAmtDeletedTD27Q()) + "</TD>");
					toReturn.append("</TR>");




					toReturn.append("<TR>");
					toReturn.append("<TD ALIGN=LEFT>" + "TCS - 27EQ" + "</TD>");
					toReturn.append("<TD ALIGN=RIGHT >" + obj_FrmValidator.cBean.getTotalTDAddedIn27EQ() + "</TD>");
					toReturn.append("<TD ALIGN=RIGHT >" + obj_FrmValidator.cBean.getRemittedAmtAddedTD27EQ() + "</TD>");
					toReturn.append("<TD ALIGN=RIGHT >" + obj_FrmValidator.cBean.getTotalTDUpdatedIn27EQ() + "</TD>");
					toReturn.append("<TD ALIGN=RIGHT >" + obj_FrmValidator.cBean.getRemittedAmtUpdatedTD27EQ() + "</TD>");
					toReturn.append("<TD ALIGN=RIGHT >" + obj_FrmValidator.cBean.getTotalTDDeletedIn27EQ() + "</TD>");
					toReturn.append("<TD ALIGN=RIGHT >" + obj_FrmValidator.cBean.getRemittedAmtDeletedTD27EQ() + "</TD>");
					toReturn.append("<TD ALIGN=RIGHT >" + (obj_FrmValidator.cBean.getRemittedAmtAddedTD27EQ()+obj_FrmValidator.cBean.getRemittedAmtUpdatedTD27EQ()-obj_FrmValidator.cBean.getRemittedAmtDeletedTD27EQ()) + "</TD>");
					toReturn.append("</TR>");


					toReturn.append("<TR>");
					toReturn.append("<TD ALIGN=LEFT >" + "<B>Grand Total</B>" + "</TD>");
					toReturn.append("<TD ALIGN=RIGHT >" +(obj_FrmValidator.cBean.getTotalTDAddedIn24Q()+obj_FrmValidator.cBean.getTotalTDAddedIn26Q()+obj_FrmValidator.cBean.getTotalTDAddedIn27Q()+obj_FrmValidator.cBean.getTotalTDAddedIn27EQ())  + "</TD>");
					toReturn.append("<TD ALIGN=RIGHT >" + (obj_FrmValidator.cBean.getRemittedAmtAddedTD24Q()+obj_FrmValidator.cBean.getRemittedAmtAddedTD26Q()+obj_FrmValidator.cBean.getRemittedAmtAddedTD27Q()+obj_FrmValidator.cBean.getRemittedAmtAddedTD27EQ()) + "</TD>");
					toReturn.append("<TD ALIGN=RIGHT >" + (obj_FrmValidator.cBean.getTotalTDUpdatedIn24Q()+obj_FrmValidator.cBean.getTotalTDUpdatedIn26Q()+obj_FrmValidator.cBean.getTotalTDUpdatedIn27Q()+obj_FrmValidator.cBean.getTotalTDUpdatedIn27EQ()) + "</TD>");
					toReturn.append("<TD ALIGN=RIGHT >" + (obj_FrmValidator.cBean.getRemittedAmtUpdatedTD24Q()+obj_FrmValidator.cBean.getRemittedAmtUpdatedTD26Q()+obj_FrmValidator.cBean.getRemittedAmtUpdatedTD27Q()+obj_FrmValidator.cBean.getRemittedAmtUpdatedTD27EQ()) + "</TD>");
					toReturn.append("<TD ALIGN=RIGHT >" + (obj_FrmValidator.cBean.getTotalTDDeletedIn24Q()+obj_FrmValidator.cBean.getTotalTDDeletedIn26Q()+obj_FrmValidator.cBean.getTotalTDDeletedIn27Q()+obj_FrmValidator.cBean.getTotalTDDeletedIn27EQ()) + "</TD>");
					toReturn.append("<TD ALIGN=RIGHT >" + (obj_FrmValidator.cBean.getRemittedAmtDeletedTD24Q()+obj_FrmValidator.cBean.getRemittedAmtDeletedTD26Q()+obj_FrmValidator.cBean.getRemittedAmtDeletedTD27Q()+obj_FrmValidator.cBean.getRemittedAmtDeletedTD27EQ()) + "</TD>");
					toReturn.append("<TD ALIGN=RIGHT >" + (obj_FrmValidator.cBean.getTotalRemittanceTD24Q()+obj_FrmValidator.cBean.getTotalRemittanceTD26Q()+obj_FrmValidator.cBean.getTotalRemittanceTD27Q()+obj_FrmValidator.cBean.getTotalRemittanceTD27EQ()) + "</TD>");
					toReturn.append("</TR>");



					toReturn.append("</TABLE>");
				}

				else
				{
					toReturn.append("<TABLE BORDER=1 WIDTH=100% CELLSPACING=1 CELLPADDING=0 BORDERCOLOR=000000 "
							+ "style='border-collapse: collapse; '>");
					toReturn.append("<TR>");
					toReturn.append("<TD VALIGN=TOP WIDTH=50%><B>Nature of Deduction</B></TD>");
					toReturn.append("<TD VALIGN=TOP WIDTH=10%><B>Count of DDO Records</B></TD>");
					toReturn.append("<TD VALIGN=TOP WIDTH=20%><B>Total TDS/TCS </B> &nbsp; (<img src =\"rp.png\" />)</TD>");
					toReturn.append("<TD VALIGN=TOP WIDTH=20%><B>Total TDS/TCS remitted to Govt. Account </B> &nbsp; (<img src =\"rp.png\" />)</TD>  </TD>");


					//toReturn.append("<img src = \"rupee.png \" /> ");
					//toReturn.append("<TD VALIGN=TOP WIDTH=290><B>Count of Distinct DDOs (TANs)</B></TD>");
					toReturn.append("</TR>");
					toReturn.append("<TR>");
					toReturn.append("<TD ALIGN=LEFT>" + "TDS Salary - 24Q" + "</TD>");
					toReturn.append("<TD ALIGN=RIGHT >" + fStatistics.getCount24Q() + "</TD>");
					toReturn.append("<TD ALIGN=RIGHT >" + fStatistics.getTotalTDSTCS24Q() + "</TD>");
					toReturn.append("<TD ALIGN=RIGHT >" + fStatistics.getTotalTDSTCSTransferred24Q() + "</TD>");
					//toReturn.append("<TD rowspan=4 VALIGN = CENTER  ALIGN=LEFT WIDTH = 166>" + fStatistics.getCountOfDistinctTD()+ "</TD>");
					//toReturn.append("<TD ALIGN=RIGHT>" + fStatistics.getCountOfTD() + "</TD>");
					//toReturn.append("<TD ALIGN=RIGHT>" + fStatistics.getTotalTax() + "</TD>");
					toReturn.append("</TR>");
					toReturn.append("<TR>");
					toReturn.append("<TD ALIGN=LEFT >" + "TDS Non Salary - 26Q" + "</TD>");
					toReturn.append("<TD ALIGN=RIGHT >" + fStatistics.getCount26Q() + "</TD>");
					toReturn.append("<TD ALIGN=RIGHT >" + fStatistics.getTotalTDSTCS26Q() + "</TD>");
					toReturn.append("<TD ALIGN=RIGHT >" + fStatistics.getTotalTDSTCSTransferred26Q() + "</TD>");

					//toReturn.append("<TD ALIGN=RIGHT>" + fStatistics.getCountOfTD() + "</TD>");
					//toReturn.append("<TD ALIGN=RIGHT>" + fStatistics.getTotalTax() + "</TD>");
					toReturn.append("</TR>");
					toReturn.append("<TR>");
					toReturn.append("<TD ALIGN=LEFT >" + "TDS Non Salary Non Resident - 27Q" + "</TD>");
					toReturn.append("<TD ALIGN=RIGHT >" + fStatistics.getCount27Q() + "</TD>");
					toReturn.append("<TD ALIGN=RIGHT >" + fStatistics.getTotalTDSTCS27Q() + "</TD>");
					toReturn.append("<TD ALIGN=RIGHT >" + fStatistics.getTotalTDSTCSTransferred27Q() + "</TD>");

					//toReturn.append("<TD ALIGN=RIGHT>" + fStatistics.getCountOfTD() + "</TD>");
					//toReturn.append("<TD ALIGN=RIGHT>" + fStatistics.getTotalTax() + "</TD>");
					toReturn.append("</TR>");
					toReturn.append("<TR>");
					toReturn.append("<TD ALIGN=LEFT >" + "TCS -27EQ" + "</TD>");
					toReturn.append("<TD ALIGN=RIGHT >" + fStatistics.getCount27EQ() + "</TD>");
					toReturn.append("<TD ALIGN=RIGHT >" + fStatistics.getTotalTDSTCS27EQ() + "</TD>");
					toReturn.append("<TD ALIGN=RIGHT >" + fStatistics.getTotalTDSTCSTransferred27EQ() + "</TD>");

					//toReturn.append("<TD ALIGN=RIGHT>" + fStatistics.getCountOfTD() + "</TD>");
					//toReturn.append("<TD ALIGN=RIGHT>" + fStatistics.getTotalTax() + "</TD>");

					toReturn.append("</TR>");

					toReturn.append("<TR>");
					toReturn.append("<TD ALIGN=LEFT >" + "<B>Grand Total</B>" + "</TD>");
					int c24=0;
					int c26=0;
					int c27=0;
					int c27EQ=0;
					if(!fStatistics.getCount24Q().equals("-") && !fStatistics.getCount24Q().equals("0.00"))
					{
						c24=Integer.parseInt(fStatistics.getCount24Q());
					}
					if(!fStatistics.getCount26Q().equals("-") && !fStatistics.getCount26Q().equals("0.00"))
					{
						c26=Integer.parseInt(fStatistics.getCount26Q());
					}
					if(!fStatistics.getCount27Q().equals("-") && !fStatistics.getCount27Q().equals("0.00"))
					{
						c27=Integer.parseInt(fStatistics.getCount27Q());
					}
					if(!fStatistics.getCount27EQ().equals("-") && !fStatistics.getCount27Q().equals("0.00"))
					{
						c27EQ=Integer.parseInt(fStatistics.getCount27EQ());
					}
					toReturn.append("<TD ALIGN=RIGHT ><B>" + (c24+c26+c27+c27EQ)+ "</B></TD>");
					toReturn.append("<TD ALIGN=RIGHT ><B>" + fStatistics.getTotalTax()+ "</B></TD>");
					toReturn.append("<TD ALIGN=RIGHT ><B>" + fStatistics.getTotalTDSTCSTransferred()+ "</B></TD>");
					//toReturn.append("<TD ALIGN=RIGHT>" + fStatistics.getCountOfTD() + "</TD>");
					//toReturn.append("<TD ALIGN=RIGHT>" + fStatistics.getTotalTax() + "</TD>");

					toReturn.append("</TR>");

					toReturn.append("</TABLE>");
				}
					toReturn.append("<BR>");
			toReturn.append("<B>"+ "DDO TAN DETAILS "+"</B>");
			toReturn.append("<TABLE BORDER=1 CELLSPACING=1 CELLPADDING=0 BORDERCOLOR=000000 "
					+ "style='border-collapse: collapse; border: none; mso-border-alt: solid windowtext .5pt; mso-padding-alt: 0in 5.4pt 0in 5.4pt'>");
			toReturn.append("<TR>");
			toReturn.append("<TD VALIGN=TOP WIDTH=359><B>No. Of Valid TAN</B></TD>");
			toReturn.append("<TD VALIGN=TOP WIDTH=330><B>No. Of TAN Applied(TANAPPLIED)</B></TD>");
			toReturn.append("<TD VALIGN=TOP WIDTH=359><B>No. Of TAN Not Available(TANNOTAVBL)</B></TD>");
			toReturn.append("<TD VALIGN=TOP WIDTH=290><B>No. Of Structurally Invalid TAN (TANINVALID)</B></TD>");
			toReturn.append("</TR>");
			toReturn.append("<TR>");

			toReturn.append("<TD VALIGN=TOP WIDTH=359><B>"+ fStatistics.getCountOfValidTAN() +"</TD>");
			toReturn.append("<TD VALIGN=TOP WIDTH=330><B>"+ fStatistics.getCountOfTANAPPLIED() +"</B></TD>");
			toReturn.append("<TD VALIGN=TOP WIDTH=359><B>"+ fStatistics.getCountOfTANNOTABVL() +"</B></TD>");
			toReturn.append("<TD VALIGN=TOP WIDTH=290><B>"+ fStatistics.getCountOfTANINVALID() +"</B></TD>");


			toReturn.append("</TR>");

			toReturn.append("</TABLE>");



			toReturn.append("<BR>");   


				toReturn.append("<P>");
				toReturn.append("<B>Transaction Detail Records with TDS/TCS transferred amount (0.00) (excluding records with mode D): </B>"+fStatistics.getCountOfTdZeroTaxExD());  //Value to be inserted
				toReturn.append("<BR>");
				int cTD=0;
				if(!fStatistics.getCountOfTD().equals("-"))
				{
					cTD=Integer.parseInt(fStatistics.getCountOfTD());
				}
				toReturn.append("<B>Number of records to be charged: </B>"+(cTD+2));
				toReturn.append("<BR>");
				//toReturn.append("Total Count of DDO:"+fStatistics.getCountOfTD());


				toReturn.append("<div style=border-style:ridge ; border-width:2 ; border-color:balck; > </div>");
				//toReturn.append("<B>"+"______________________________________________________________________________________________________________________________________________________"+"</B>");


				toReturn.append("<B>"+ "TO BE FILLED IN BY THE AO: "+"</B>");

				toReturn.append("<BR><BR>");
				toReturn.append("<TABLE BORDER=1 WIDTH 1=100% CELLSPACING=1 CELLPADDING=0 BORDERCOLOR=000000 "
						+ "style='border-collapse: collapse; '>");
				toReturn.append("<TR>");
				toReturn.append("<TD VALIGN=TOP WIDTH=25%><B>Count of DDO Added</B></TD>");
				toReturn.append("<TD VALIGN=TOP WIDTH=25%><B>Count of DDO Updated</B></TD>");
				toReturn.append("<TD VALIGN=TOP WIDTH=25%><B>Count of DDO Deleted</B></TD>");
				toReturn.append("<TD VALIGN=TOP WIDTH=25%><B>Count of Total DDOs associated with AO</B></TD>");

				toReturn.append("</TR>");
				toReturn.append("<TR>");

				toReturn.append("<TD VALIGN=TOP WIDTH=359 HEIGHT=25><B></TD>");
				toReturn.append("<TD VALIGN=TOP WIDTH=330><B></B></TD>");
				toReturn.append("<TD VALIGN=TOP WIDTH=359><B></B></TD>");
				toReturn.append("<TD VALIGN=TOP WIDTH=359><B></B></TD>");



				toReturn.append("</TR>");

				toReturn.append("</TABLE>");


				//END OF ADDED BY SUBHANKAR


					toReturn.append("<TABLE>");
			toReturn.append("<TR>");
			toReturn.append("<TD>");
			toReturn.append("<B>Change in count of DDOs since last Quarter (Y/N): _______</B>");
			toReturn.append("</TD>");
			toReturn.append("<TR>");
			toReturn.append("<TD ALIGN=LEFT>");
			toReturn.append("Count of DDOs added: ________");
			toReturn.append("</TD>");
			toReturn.append("<TD ALIGN=RIGHT>");
			toReturn.append("Count of DDOs deleted: ________");
			toReturn.append("</TD>");
			toReturn.append("</TR>");
			toReturn.append("</TABLE>");
			toReturn.append("<FONT SIZE =4><B>");  



				//toReturn.append("<BR></BR>");
				toReturn.append("<CENTER><B><U>VERIFICATION</B></U></CENTER>");
				toReturn.append("<BR></BR>");
				toReturn.append("</B></FONT>");
				toReturn.append("&nbsp;I,&nbsp;" +"________________________________________________"+"&nbsp;&nbsp;" + "hereby certify that all the" +
				" particulars furnished above are correct and complete.");
				toReturn.append("<BR>");
				toReturn.append("<TABLE>");
				toReturn.append("<TR>");
				toReturn.append("<TD>" + "Place: " + "</TD>");
				toReturn.append("</TR>");
				toReturn.append("<TR>");
				toReturn.append("<TD>" + "Date: " + "</TD>");
				toReturn.append("<TD ALIGN=RIGHT WIDTH = 950>" + "Signature of person responsible for furnishing Form 24G" + "</TD>");
				toReturn.append("</TR>");
				toReturn.append("</TABLE>");
				toReturn.append("<BR>");
				//toReturn.append("&nbsp;&nbsp;" + "*Name");
				toReturn.append("&nbsp;&nbsp;" + "*Name & Designation ________________________________________________________________________________" );
				toReturn.append("<BR></BR>" + "&nbsp;&nbsp;" + "Signature __________________________________________________________________________________________");
				toReturn.append("<TABLE>");
				toReturn.append("<TR>");
				toReturn.append("<TD>");
				toReturn.append("&nbsp;" + "<B>Notes :</B>");
				toReturn.append("<BR>" + "*To be counter signed by the person who is his immediate superior in case "
						+ "the PAO is also a DDO whose details are being given in the statement.");
				toReturn.append("</TD>");
				toReturn.append("</TR>");
				//toReturn.append("<BR>");
				toReturn.append("<TABLE>");
				toReturn.append("&nbsp;FVU Version : " + FVUVersion + " &nbsp;&nbsp;&nbsp; Input File Name : " + fileName + " <BR>");
				toReturn.append("</TD>");
				toReturn.append("</TR>");
				toReturn.append("</TABLE>");
				toReturn.append("</BODY>");
				toReturn.append("</HTML>");
			}
			writeToFile(statisticFileName, toReturn.toString(), 0);
			// added by faizan for FVU 1.4
			TBAFFileGenerator flgObj = null;
			flgObj =new TBAFFileGenerator();
			String temppdfFileName = "Faizan1.pdf";
			String pdfFileName="Faizan2.pdf";
			flgObj.writePdfFile(temppdfFileName,pdfFileName, toReturn);
			toReturn = new StringBuffer();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			Log.tbaf_log.error("EXCEPTION in FileGenerator.java createStatisticFileString() : ",  e);
		}
	}*/
	
	//Added by faizan for FVU 1.4
	
	private void createStatisticFile(TBAFFormatValidator obj_FrmValidator,TBAFFileStatistics fStatistics, String statisticFileName, String fileName, String FVUVersion, String filehash)
	{
		try
		{

			StringBuffer toReturn = new StringBuffer();
			//added by faizan for fvu 1.4
			InputStream is = ClassLoader.getSystemClassLoader().getResourceAsStream("images/rp.PNG");
			String imagePath = statisticFileName.substring(0,(statisticFileName.lastIndexOf('\\')) + 1);
			System.out.println("The imagePath in createStatisticFile is:"+imagePath);
			FileOutputStream fout =new FileOutputStream(imagePath+"rp.png");
			while(is.available()!= 0)
			{
				fout.write(is.read());

			}
			fout.flush();
			fout.close();
			is.close();
			if(fileName.contains("\\"))
					fileName = fileName.substring(fileName.lastIndexOf('\\')+1, fileName.length());
			Log.tbaf_log.info("FileGenerator.java createStatisticFileString() method is called");
			toReturn.append("<HTML>");
			toReturn.append("<HEAD>");
			toReturn.append("<TITLE></TITLE>");
			toReturn.append("<style TYPE = \"text/css\">  TD { FONT-SIZE:12 } </style>");
			toReturn.append("<style type=text/css> ");
			toReturn.append("table.tab1 {");
			toReturn.append(" align: center");
			toReturn.append("	}");
			toReturn.append("table.tab2 {");
			toReturn.append(" align: center");
			toReturn.append("	}");
			toReturn.append("table.tab3 {");
			toReturn.append(" align: center");
			toReturn.append("	}");
			toReturn.append("</style>");
			toReturn.append("</HEAD>");
			toReturn.append("<BODY >");
			// added by faizan for FVU 1.4
			toReturn.append("<CENTER >");
			toReturn.append("<TABLE BORDER=0 WIDTH=100% align = center CELLSPACING=1 CELLPADDING=0 "
					+ "style='margin-right:15px;border-collapse: collapse; '>");
			toReturn.append("<TR>");
			
			toReturn.append("		<td width=10% align=left>File Hash  </td>");
			toReturn.append("		<td width=50% align=left>"+filehash+"</td>");
			toReturn.append("		<td width=20% align=left></td>");
			toReturn.append("		<td width=20% align=right style = 'padding-bottom:20px'><img src='barcode.jpeg' height = '25px' /></td>");
			toReturn.append("	</tr>");
			toReturn.append("</TABLE>");
			toReturn.append("</CENTER>");
			toReturn.append("<CENTER>");
			toReturn.append("<TABLE BORDER=1 WIDTH=100% BORDERCOLOR=black  class=tab1  CELLSPACING=0 CELLPADDING=2 "
					+ "style='margin-right:15px;border-collapse: collapse; '>");
			
			
			toReturn.append("<TR>");
			toReturn.append("<TD>");
			toReturn.append("<TABLE BORDER=1 WIDTH=100% BORDERCOLOR=black align = center class=tab3  CELLSPACING=0 CELLPADDING=2 "
					+ "style='border-collapse: collapse; '>");
			toReturn.append("<TR>");
			toReturn.append("<TD>");
			toReturn.append("<FONT SIZE = 4>");
			//Gauri changed form type for CR 89435, FVU 1.9
			if(Integer.parseInt(fStatistics.getFinancialYear().substring(2, 4)) < 26) {
				toReturn.append("<CENTER><B>Form 24G Statement Statistic Report</B></CENTER>");
			}
			else {
				toReturn.append("<CENTER><B>Form 137 Statement Statistic Report</B></CENTER>");				
				}
			//toReturn.append("<CENTER><B>Form 24G Statement Statistic Report</B></CENTER>");
			toReturn.append("</FONT>");
			toReturn.append("</TD>");
			toReturn.append("</TR>");
			toReturn.append("<TR>");
			toReturn.append("<TD >");		
			//Gauri changed form type for CR 89435, FVU 1.9
			if(Integer.parseInt(fStatistics.getFinancialYear().substring(2, 4)) < 26) {
				toReturn.append("<B><CENTER>To be submitted with Form 24G</B></CENTER>");
			}
			else {
				toReturn.append("<B><CENTER>To be submitted with Form 137</B></CENTER>");				
				}
			//toReturn.append("<B><CENTER>To be submitted with Form 24G</B></CENTER>");
			//toReturn.append("</FONT>");
			toReturn.append("<BR>");
			toReturn.append("</TD>");
			toReturn.append("</TR>");
			//added by faizan for FVU 1.4
			toReturn.append("<TR><TD style='text-align: justify;text-justify: inter-word;'>The details shown in the report are as per the statement prepared by you. In case any discrepancy in the details shown is observed,"
					+ " the statement should be corrected accordingly. After such changes statement should be validated again through the File Validation Utility(FVU)."
					+ " Kindly ensure AIN details are as communicated by Directorate of Income Tax (Systems).<BR></TD> </TR> ");
			//ended by faizan
			toReturn.append("</TABLE>");
			toReturn.append("<BR>");
			toReturn.append("<TABLE BORDER=1 WIDTH=100% CELLSPACING=0 CELLPADDING=0 BORDERCOLOR=black  class=tab3 style='border-collapse: collapse; '> ");
			toReturn.append("<TR>");
			toReturn.append("<TD WIDTH=80% VALIGN=TOP><B> Name of Accounts Office </B> </CENTER> </TD> ");
			toReturn.append("<TD WIDTH=20% VALIGN=TOP><CENTER><B>  AIN </B>	</CENTER> </TD>");
			toReturn.append("</TR>");
			toReturn.append("<TR>");
			/*//Gauri changed AO name for CR 89435, FVU 1.9
			if(Integer.parseInt(fStatistics.getFinancialYear().substring(2, 4)) < 25) {
				toReturn.append("<TD ALIGN=LEFT>" + fStatistics.getNameOfAO() + "</TD>");
			}
			else {
				if(fStatistics.getaoFirstName() == null && fStatistics.getaoMiddleName() == null) {
					toReturn.append("<TD ALIGN=LEFT>" + fStatistics.getaoLastName() + "</TD>");
				}
				else {
					toReturn.append("<TD ALIGN=LEFT>" + (fStatistics.getaoFirstName() != null? fStatistics.getaoFirstName() + " " : "")+
							(fStatistics.getaoMiddleName() != null? fStatistics.getaoMiddleName() + " " : "")+
							(fStatistics.getaoLastName() != null? fStatistics.getaoLastName() : " ") + "</TD>");
				}
			}//END
*/			
			toReturn.append("<TD ALIGN=LEFT>" + fStatistics.getNameOfAO() + "</TD>");
			toReturn.append("<TD ALIGN=LEFT>" +"<CENTER>"+ fStatistics.getAIN()+"</CENTER>" + "</TD>");
			toReturn.append("</TR>");
			toReturn.append("</TABLE>");
			toReturn.append("<P>");
			toReturn.append("<P>");
			if (!fStatistics.getTransactionType().equals(TBAF_TRANSACTION_TYPE_C3))
			{
				toReturn.append("<TABLE BORDER=1 WIDTH=100% CELLSPACING=0 CELLPADDING=0 BORDERCOLOR=black  class=tab1 style='border-collapse: collapse; '> ");
				toReturn.append("<TR>");
				toReturn.append("<TD VALIGN=TOP><B> Address of Accounts Office </B> </CENTER> </TD> ");
				toReturn.append("</TR>");
				toReturn.append("<TR>");
				toReturn.append("<TD ALIGN=LEFT VALIGN=TOP WIDTH=100% style=\"WORD-BREAK:BREAK-ALL\">");
				toReturn.append(fStatistics.getAoAdd1());
				toReturn.append(", ");
				if (!fStatistics.getAoAdd2().equals(""))
				{
					toReturn.append(fStatistics.getAoAdd2());
					toReturn.append(", ");
				}
				if (!fStatistics.getAoAdd3().equals(""))
				{
					toReturn.append(fStatistics.getAoAdd3());
					toReturn.append(", ");
				}
				if (!fStatistics.getAoAdd4().equals(""))
				{
					toReturn.append(fStatistics.getAoAdd4());
					toReturn.append(", ");
				}
				toReturn.append(fStatistics.getAoCity());
				toReturn.append(", ");
				toReturn.append(TBAF_STATE_NAME[Integer.parseInt(fStatistics.getAoState().trim())]);
				toReturn.append(", ");
				toReturn.append(fStatistics.getAoPIN());
				toReturn.append("</TD>");
				toReturn.append("</TR>");
				toReturn.append("</TABLE>");
			}
			toReturn.append("<P>");

			//ADDED BY SUBHANKAR


			//Statistic Report Generation for X correction

			if(fStatistics.getTypeOfStatement().equals(TBAF_TYPE_OF_STMT_CORR) && fStatistics.getTransactionType().equals(TBAF_TRANSACTION_TYPE_X))
			{
				toReturn.append("<TABLE   class=tab3 BORDER=1  WIDTH=100% CELLSPACING=0 CELLPADDING=0 BORDERCOLOR=black "
						+ "style='border-collapse: collapse; '>");
				toReturn.append("<TR>");
				toReturn.append("<TD VALIGN=TOP WIDTH = 35%><B> Financial Year </B></TD>");
				int financialYear = Integer.parseInt(fStatistics.getFinancialYear().substring(2, 4));
				if (financialYear >= 9 && financialYear < 99)
				{
					financialYear = financialYear + 1;
					String finYear = String.valueOf(financialYear);
					toReturn.append("<TD ALIGN=LEFT WIDTH = 15%><CENTER>" + fStatistics.getFinancialYear() + "-" + finYear + "</CENTER></TD>");
				}
				else if (financialYear == 99)
				{
					toReturn.append("<TD ALIGN=LEFT WIDTH = 15%><CENTER>" + fStatistics.getFinancialYear() + "-" + "00" + "</CENTER></TD>");
				}
				else
				{
					financialYear = financialYear + 1;
					String finYear = String.valueOf(financialYear);
					toReturn.append("<TD ALIGN=LEFT WIDTH = 15%><CENTER>" + fStatistics.getFinancialYear() + "-" + "0" + finYear + "</CENTER></TD>");
				}

				toReturn.append("<TD  WIDTH = 32%><B> Type of Statement </B></TD>");
				toReturn.append("<TD  ALIGN=RIGHT WIDTH = 18%><CENTER> Correction </CENTER></TD>");
				toReturn.append("</TR>");
				toReturn.append("<TR>");
				toReturn.append("<TD  WIDTH = 35%><B> Month </B></TD>");
				toReturn.append("<TD ALIGN=LEFT WIDTH = 15%><CENTER>" + MONTH[Integer.parseInt(fStatistics.getMonthOfTransaction())] + "</CENTER></TD>");
				toReturn.append("<TD VALIGN=TOP WIDTH = 32%><B> Type of Transaction </B></TD>");
				toReturn.append("<TD  ALIGN=RIGHT WIDTH = 18%> Cancellation(X) </TD>");
				toReturn.append("</TR>");

				toReturn.append("<TR>");
				toReturn.append("<TD VALIGN=TOP WIDTH = 40%><B> Category of AO: </B></TD>");
				toReturn.append("<TD ALIGN=LEFT WIDTH = 60% colspan =3>" + Parameters.GovtMap.get(fStatistics.getDeductorCat().trim()) + "</TD>");
				toReturn.append("</TR>");
				toReturn.append("<TR>");
				toReturn.append("<TD VALIGN=TOP WIDTH = 40%><B> Name of Ministry/State </B></TD>");
				if(fStatistics.getDeductorCat().trim().equals("A"))
				{

					toReturn.append("<TD ALIGN=LEFT WIDTH = 60% colspan =3>" +TBAF_MINISTRY_NAME[Integer.parseInt(fStatistics.getMinistryName().trim())]+ "</TD>");
				}
				else
				{
					toReturn.append("<TD ALIGN=LEFT WIDTH = 60% colspan =3>" +TBAF_STATE_NAME[Integer.parseInt(fStatistics.getStateName().trim())]+ "</TD>");
				}
				toReturn.append("</TR>");
				toReturn.append("<TR>");
				toReturn.append("<TD VALIGN=TOP WIDTH = 40%><B> Name of Sub Ministry </B></TD>");
				if(fStatistics.getDeductorCat().trim().equals("A"))
				{
					if(Integer.parseInt(fStatistics.getMinistryName().trim()) == 1 && Integer.parseInt(fStatistics.getSubMinistryName().trim()) != 99)
					{
						toReturn.append("<TD style = \" WORD-BREAK:BREAK-ALL \" ALIGN=LEFT WIDTH = 60% colspan =3>" +TBAF_SUB_MINISTRY_NAME[Integer.parseInt(fStatistics.getSubMinistryName().trim())]+ "</TD>");
					}
					else if(! fStatistics.getSubMinistryName().trim().equals("-") && Integer.parseInt(fStatistics.getSubMinistryName().trim()) == 99)
					{
						toReturn.append("<TD style = \" WORD-BREAK:BREAK-ALL \" ALIGN=LEFT WIDTH = 60% colspan =3>" +fStatistics.getSubMinistryName_O()+ "</TD>");
					}
					else
					{
						// ADDED by faizan for FVU 1.4
						toReturn.append("<TD style = \" WORD-BREAK:BREAK-ALL \"  ALIGN=LEFT WIDTH = 60% colspan =3>" +"NA"+ "</TD>");
						// ended by faizan
						//toReturn.append("<TD style = \" WORD-BREAK:BREAK-ALL \"  ALIGN=LEFT WIDTH = 60% colspan =3>" +"-"+ "</TD>");
					}
				}
				else
				{
					// ADDED by faizan for FVU 1.4
					toReturn.append("<TD style = \" WORD-BREAK:BREAK-ALL \" ALIGN=LEFT WIDTH = 60% colspan =3>" +"NA"+ "</TD>");
					// ended by faizan
					//toReturn.append("<TD style = \" WORD-BREAK:BREAK-ALL \" ALIGN=LEFT WIDTH = 60% colspan =3>" +"-"+ "</TD>");
				}
				toReturn.append("</TR>");
				toReturn.append("</TABLE>");
				toReturn.append("<BR></BR>");
				toReturn.append("<BR></BR>");
			/*	toReturn.append("<B>"+ "TO BE FILLED IN BY THE AO: "+"</B>");
*/
				toReturn.append("<CENTER><B><U>VERIFICATION</U></B></CENTER>");
				toReturn.append("<BR></BR>");
				toReturn.append("</B>");
				//toReturn.append("&nbsp;I,&nbsp;" +"________________________________________________"+"&nbsp;&nbsp;" + "hereby certify that all the" +
				toReturn.append("&nbsp;&nbsp;&nbsp;I,&nbsp;" +"<u>"+ fStatistics.getResponsiblePersonName()+"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"+"</u>"+"&nbsp;&nbsp;" + "hereby certify that all the" +
				" particulars furnished above are correct and complete.");
				toReturn.append("<BR><BR>");
				toReturn.append("<TABLE>");
				toReturn.append("<TR>");
				//toReturn.append("<TD>" + "Place: " + "</TD>");
				//added by faizan for FVU 1.4
				toReturn.append("<TD COLSPAN = 2>" + "&nbsp;&nbsp;" + "<B>Place: </B>"+fStatistics.getAoCity()+ "</TD>");
				//ended by faizan
				toReturn.append("</TR>");
				toReturn.append("<TR>");
				//toReturn.append("<TD>" + "Date: " + "</TD>");
				//added by faizan for FVU 1.4
				toReturn.append("<TD WIDTH = 300>" + "&nbsp;&nbsp;" + "<B>Date: </B>" +obj_FrmValidator.fileCreationDate.substring(0,2)+"/"+obj_FrmValidator.fileCreationDate.substring(2,4)+"/"+obj_FrmValidator.fileCreationDate.substring(4,8)+"</TD>");
				//ended by faizan
				toReturn.append("<TD ALIGN=RIGHT WIDTH = 800><B>" + "Signature of person responsible for furnishing Form 24G" + "</B></TD>");
				toReturn.append("</TR>");
				toReturn.append("</TABLE>");
				toReturn.append("<BR>");
				//toReturn.append("&nbsp;&nbsp;" + "*Name");
				toReturn.append( "<B>*Name & Designation </B>________________________________________________________________________________" );
				toReturn.append("<BR></BR>" + "&nbsp;&nbsp;" + "<B>Signature</B> __________________________________________________________________________________________");
				toReturn.append("<BR><TABLE>");
				toReturn.append("<TR>");
				toReturn.append("<TD>");
				toReturn.append("&nbsp;" + "<B>Notes :</B>");
				toReturn.append("<BR>" + "*To be counter signed by the person who is his immediate superior in case "
						+ "the PAO is also a DDO whose details are being given in the statement.");
				toReturn.append("</TD>");
				toReturn.append("</TR>");
				toReturn.append("</TABLE>");
				toReturn.append("<TABLE>");
				toReturn.append("<TR>");
				toReturn.append("<TD >");
				toReturn.append("<B>&nbsp;FVU Version : " + FVUVersion+"</B>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
				toReturn.append("</TD>");
			    toReturn.append("<TD style='text-align:right; width:80%;'><B> Input File Name : " + fileName + " </B><BR></TD>");
//			    toReturn.append("&nbsp;<B>FVU Version : " + FVUVersion +"</B>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
//				toReturn.append("</TD>");
//				toReturn.append("<TD ALIGN=RIGHT>");
//				toReturn.append("<B>Input File Name : " + fileName + " </B><BR>");
//				toReturn.append("</TD>");
				toReturn.append("</TR>");
				toReturn.append("</TABLE>");
				toReturn.append("</TABLE>");
				toReturn.append("</CENTER>");
				toReturn.append("</BODY>");
				toReturn.append("</HTML>");
			}
			//End of statisticReport Generation for X correction
			else
			{
				toReturn.append("<CENTER>");
				toReturn.append("<TABLE   class=tab3 BORDER=1 WIDTH=100% CELLSPACING=0 CELLPADDING=0 BORDERCOLOR=black "
						+ "style='border-collapse: collapse; '>");


				toReturn.append("<TR>");
				toReturn.append("<TD VALIGN=TOP WIDTH = 35%><B> Financial Year </B></TD>");
				int financialYear = Integer.parseInt(fStatistics.getFinancialYear().substring(2, 4));
				//Log.tbaf_log.info("Gauri financialYear:- " + financialYear);
				if (financialYear >= 9 && financialYear < 99)
				{
					financialYear = financialYear + 1;
					String finYear = String.valueOf(financialYear);
					toReturn.append("<TD ALIGN=LEFT WIDTH = 15%><CENTER>" + fStatistics.getFinancialYear() + "-" + finYear + "</CENTER></TD>");
				}
				else if (financialYear == 99)
				{
					toReturn.append("<TD ALIGN=LEFT WIDTH = 15%><CENTER>" + fStatistics.getFinancialYear() + "-" + "00" + "</CENTER></TD>");
				}
				else
				{
					financialYear = financialYear + 1;
					String finYear = String.valueOf(financialYear);
					toReturn.append("<TD ALIGN=LEFT WIDTH = 15%><CENTER>" + fStatistics.getFinancialYear() + "-" + "0" + finYear + "</CENTER></TD>");
				}



				toReturn.append("<TD VALIGN=TOP WIDTH = 32%><B> Count of Distinct DDOs (valid TAN) </B></TD>");
				toReturn.append("<TD  ALIGN=RIGHT WIDTH = 18%><B><CENTER>"+fStatistics.getCountOfDistinctTD()+"</CENTER></B></TD>");

				toReturn.append("</TR>");

				toReturn.append("<TR>");
				
				toReturn.append("<TD  WIDTH = 35%><B> Month </B></TD>");
				toReturn.append("<TD ALIGN=LEFT WIDTH = 15%><CENTER>" + MONTH[Integer.parseInt(fStatistics.getMonthOfTransaction())] + "</CENTER></TD>");
				Log.tbaf_log.debug(fStatistics.getMonthOfTransaction());


				toReturn.append("<TD VALIGN=TOP WIDTH = 32%><B> Count of  DDO records with valid TAN </B></TD>");
				toReturn.append("<TD ALIGN=RIGHT WIDTH = 18%><CENTER>" + fStatistics.getCountOfValidTAN() + "</CENTER></TD>");

				toReturn.append("</TR>");


				toReturn.append("<TR>");

				toReturn.append("<TD WIDTH = 35%><B> Type of Statement </B></TD>");

				if (fStatistics.getTypeOfStatement().equals(TBAF_TYPE_OF_STMT_ORIG))
				{
					toReturn.append("<TD ALIGN=LEFT WIDTH=15%><CENTER> Original </CENTER></TD>");
				}
				else
				{
					toReturn.append("<TD  WIDTH = 15%><CENTER> Correction </CENTER></TD>");
					//toReturn.append("<TD VALIGN=TOP><B> Type of Statement </B></TD>");
				}


				toReturn.append("<TD VALIGN=TOP WIDTH = 32%><B> Count of  DDO records with invalid TAN </B></TD>");
				int cd=0;
				int vTan=0;
				if(!(fStatistics.getCountOfTD()).equals("-"))
				{
					cd=Integer.parseInt(fStatistics.getCountOfTD());
				}
				if(!(fStatistics.getCountOfValidTAN()).equals("-"))
				{
					vTan=Integer.parseInt(fStatistics.getCountOfValidTAN());
				}
				int invTan=cd-vTan;
				toReturn.append("<TD ALIGN=RIGHT WIDTH = 18%><CENTER>" + invTan + "</CENTER></TD>");

				toReturn.append("</TR>");




				if (fStatistics.getTypeOfStatement().equals(TBAF_TYPE_OF_STMT_CORR))
				{
					toReturn.append("<TR>");
					toReturn.append("<TD ALIGN=LEFT WIDTH=40%><B> Type of Transaction </B></TD>");


					if (fStatistics.getTransactionType().equals(TBAF_TRANSACTION_TYPE_C1))
					{
						toReturn.append("<TD ALIGN=LEFT colspan=3 WIDTH=60%> C1 - Correction in Accounts Officer details </TD>");
					}
					else if (fStatistics.getTransactionType().equals(TBAF_TRANSACTION_TYPE_C2))
					{
						toReturn.append("<TD ALIGN=LEFT colspan=3 WIDTH=60%> C2 - Correction in DDO and/or Accounts Officer details </TD>");
					}
					else if (fStatistics.getTransactionType().equals(TBAF_TRANSACTION_TYPE_C3))
					{
						toReturn.append("<TD ALIGN=LEFT colspan=3 WIDTH=60%> C3 - Correction in DDO details  </TD>");
					}
					else if(fStatistics.getTransactionType().equals(TBAF_TRANSACTION_TYPE_M))
					{
						toReturn.append("<TD ALIGN=LEFT colspan=3 WIDTH=60%> M  </TD>");
					}
					else if(fStatistics.getTransactionType().equals(TBAF_TRANSACTION_TYPE_X))
					{
						toReturn.append("<TD ALIGN=LEFT colspan=3 WIDTH=60%> X </TD>");
					}
					else
					{
						toReturn.append("<TD ALIGN=LEFT colspan=3 WIDTH=60%> C4 - Correction in AIN and /or statement details </TD>");
					}
					toReturn.append("</TR>");
				}
				toReturn.append("<TR>");


				toReturn.append("<TD VALIGN=TOP WIDTH = 40%><B> Category of AO: </B></TD>");
				toReturn.append("<TD ALIGN=LEFT WIDTH = 60% colspan =3>" + Parameters.GovtMap.get(fStatistics.getDeductorCat().trim()) + "</TD>");

				toReturn.append("</TR>");

				toReturn.append("<TR>");

				toReturn.append("<TD VALIGN=TOP WIDTH = 40%><B> Name of Ministry/State </B></TD>");
				if(fStatistics.getDeductorCat().trim().equals("A"))
				{

					toReturn.append("<TD ALIGN=LEFT WIDTH = 60% colspan =3>" +TBAF_MINISTRY_NAME[Integer.parseInt(fStatistics.getMinistryName().trim())]+ "</TD>");
				}
				else
				{
					toReturn.append("<TD ALIGN=LEFT WIDTH = 60% colspan =3>" +TBAF_STATE_NAME[Integer.parseInt(fStatistics.getStateName().trim())]+ "</TD>");
				}
				toReturn.append("</TR>");


				toReturn.append("<TR>");
				toReturn.append("<TD VALIGN=TOP WIDTH = 40%><B> Name of Sub Ministry </B></TD>");

				if(fStatistics.getDeductorCat().trim().equals("A"))
				{
					if(Integer.parseInt(fStatistics.getMinistryName().trim()) == 1 && Integer.parseInt(fStatistics.getSubMinistryName().trim()) != 99)
					{
						toReturn.append("<TD style = \" WORD-BREAK:BREAK-ALL \" ALIGN=LEFT WIDTH = 60% colspan =3>" +TBAF_SUB_MINISTRY_NAME[Integer.parseInt(fStatistics.getSubMinistryName().trim())]+ "</TD>");
					}
					else if(! fStatistics.getSubMinistryName().trim().equals("-") && Integer.parseInt(fStatistics.getSubMinistryName().trim()) == 99)
					{
						toReturn.append("<TD style = \" WORD-BREAK:BREAK-ALL \" ALIGN=LEFT WIDTH = 60% colspan =3>" +fStatistics.getSubMinistryName_O()+ "</TD>");
					}
					else
					{
						// ADDED by faizan for FVU 1.4
						toReturn.append("<TD style = \" WORD-BREAK:BREAK-ALL \"  ALIGN=LEFT WIDTH = 60% colspan =3>" +"NA"+ "</TD>");
						//ended by faizan
						//toReturn.append("<TD style = \" WORD-BREAK:BREAK-ALL \"  ALIGN=LEFT WIDTH = 60% colspan =3>" +"-"+ "</TD>");
					}
				}
				else
				{
					// ADDED by faizan for FVU 1.4
					toReturn.append("<TD style = \" WORD-BREAK:BREAK-ALL \" ALIGN=LEFT WIDTH = 60% colspan =3>" +"NA"+ "</TD>");
					
					//ended by faizan
					//toReturn.append("<TD style = \" WORD-BREAK:BREAK-ALL \" ALIGN=LEFT WIDTH = 60% colspan =3>" +"-"+ "</TD>");
				}




				toReturn.append("</TR>");
				toReturn.append("</TABLE>");
				toReturn.append("</CENTER>");
				toReturn.append("<BR>");
				toReturn.append("<BR>");
				if ( (fStatistics.getTransactionType().equals(TBAF_TRANSACTION_TYPE_C1)) || (fStatistics.getTransactionType().equals(TBAF_TRANSACTION_TYPE_X)) )
				{
					/*
					 * Incase of C1 or X (calcellation) CORRECTION, Count of transactions, Total value of all Transactions(Rs.) and Count of Transacting Parties (Distinct TANs)
					 * is not shown in Statistics Report
					 * 
					 * As in Cancellation i.e. X correction in particular there are no TD records Possible
					 * 
					 */
				}

				else if((fStatistics.getTransactionType().equals(TBAF_TRANSACTION_TYPE_M)))

				{
					DecimalFormat df = new DecimalFormat("0.00");
					df.setMaximumFractionDigits(2);
					toReturn.append("<CENTER>");
					toReturn.append("<TABLE  class=tab3 BORDER=1 WIDTH=100% CELLSPACING=0 CELLPADDING=0 BORDERCOLOR=black "
							+ "style='border-collapse: collapse; '>");
					toReturn.append("<TR>");
					toReturn.append("<TD ALIGN=CENTER WIDTH=13%><B>Nature of Deduction</B></TD>");
					toReturn.append("<TD ALIGN=CENTER WIDTH=11%><B>Count of DDO Records Added(A)</B></TD>");
					toReturn.append("<TD ALIGN=CENTER WIDTH=13%><B>TDS/TCS remitted to Govt. Account </B><BR> (<img src ='rp.png' />)<BR>(B)</TD> ");
					toReturn.append("<TD ALIGN=CENTER WIDTH=12%><B>Count of DDO Records Updated(C)</B></TD>");
					toReturn.append("<TD ALIGN=CENTER WIDTH=13%><B>TDS/TCS remitted to Govt. Account </B> <BR> (<img src ='rp.png' />)<BR>(D)</TD> ");
					toReturn.append("<TD ALIGN=CENTER WIDTH=11%><B>Count of DDO Records Deleted(E)</B></TD>");
					toReturn.append("<TD ALIGN=CENTER WIDTH=13%><B>TDS/TCS remitted to Govt. Account </B> <BR> (<img src ='rp.png' />)<BR>(F)</TD> ");
					toReturn.append("<TD ALIGN=CENTER WIDTH=14%><B>Total TDS/TCS remitted to Govt. Account </B> <BR> (<img src ='rp.png' />)<BR>(G=B+D-F)</TD> ");
					
					toReturn.append("</TR>");
					toReturn.append("<TR>");
					//Gauri changed form type in for cBean
					if(financialYear < 26) {
						//Log.tbaf_log.info("Gauri financialYear:- " + financialYear);
						toReturn.append("<TD ALIGN=LEFT >" + "TDS Salary-24Q" + "</TD>");
					}
					else {
						//Log.tbaf_log.info("Gauri financialYear:- " + financialYear);
						toReturn.append("<TD ALIGN=LEFT >" + "TDS Salary-FN138" + "</TD>");				
						}
					//toReturn.append("<TD ALIGN=LEFT>" + "TDS Salary-24Q" + "</TD>");
					toReturn.append("<TD ALIGN=RIGHT >" + obj_FrmValidator.cBean.getTotalTDAddedIn24Q() + "</TD>");
					toReturn.append("<TD ALIGN=RIGHT >" + df.format(obj_FrmValidator.cBean.getRemittedAmtAddedTD24Q()) + "</TD>");
					toReturn.append("<TD ALIGN=RIGHT >" + obj_FrmValidator.cBean.getTotalTDUpdatedIn24Q() + "</TD>");
					toReturn.append("<TD ALIGN=RIGHT >" + df.format(obj_FrmValidator.cBean.getRemittedAmtUpdatedTD24Q()) + "</TD>");
					toReturn.append("<TD ALIGN=RIGHT >" + obj_FrmValidator.cBean.getTotalTDDeletedIn24Q() + "</TD>");
					toReturn.append("<TD ALIGN=RIGHT >" + df.format(obj_FrmValidator.cBean.getRemittedAmtDeletedTD24Q()) + "</TD>");
					toReturn.append("<TD ALIGN=RIGHT >" + df.format(obj_FrmValidator.cBean.getRemittedAmtAddedTD24Q()+obj_FrmValidator.cBean.getRemittedAmtUpdatedTD24Q()-obj_FrmValidator.cBean.getRemittedAmtDeletedTD24Q())+ "</TD>");
					toReturn.append("</TR>");




					toReturn.append("<TR>");
					////Gauri changed form type in for cBean
					if(financialYear < 26) {
						//Log.tbaf_log.info("Gauri financialYear:- " + financialYear);
						toReturn.append("<TD ALIGN=LEFT >" + "TDS Non Salary-26Q" + "</TD>");
					}
					else {
						//Log.tbaf_log.info("Gauri financialYear:- " + financialYear);
						toReturn.append("<TD ALIGN=LEFT >" + "TDS Salary-FN140" + "</TD>");				
						}
					//toReturn.append("<TD ALIGN=LEFT>" + "TDS Non Salary-26Q" + "</TD>");
					toReturn.append("<TD ALIGN=RIGHT >" + obj_FrmValidator.cBean.getTotalTDAddedIn26Q() + "</TD>");
					toReturn.append("<TD ALIGN=RIGHT >" + df.format(obj_FrmValidator.cBean.getRemittedAmtAddedTD26Q()) + "</TD>");
					toReturn.append("<TD ALIGN=RIGHT >" + obj_FrmValidator.cBean.getTotalTDUpdatedIn26Q() + "</TD>");
					toReturn.append("<TD ALIGN=RIGHT >" + df.format(obj_FrmValidator.cBean.getRemittedAmtUpdatedTD26Q()) + "</TD>");
					toReturn.append("<TD ALIGN=RIGHT >" + obj_FrmValidator.cBean.getTotalTDDeletedIn26Q() + "</TD>");
					toReturn.append("<TD ALIGN=RIGHT >" + df.format(obj_FrmValidator.cBean.getRemittedAmtDeletedTD26Q()) + "</TD>");
					toReturn.append("<TD ALIGN=RIGHT >" + df.format(obj_FrmValidator.cBean.getRemittedAmtAddedTD26Q()+obj_FrmValidator.cBean.getRemittedAmtUpdatedTD26Q()-obj_FrmValidator.cBean.getRemittedAmtDeletedTD26Q()) + "</TD>");
					toReturn.append("</TR>");





					toReturn.append("<TR>");
					//Gauri changed form type in for cBean
					if(financialYear < 26) {
						//Log.tbaf_log.info("Gauri financialYear:- " + financialYear);
						toReturn.append("<TD ALIGN=LEFT >" + "TDS Non Salary Non Resident - 27Q" + "</TD>");
					}
					else {
						//Log.tbaf_log.info("Gauri financialYear:- " + financialYear);
						toReturn.append("<TD ALIGN=LEFT >" + "TDS Non Salary Non Resident - FN144" + "</TD>");				
						}
					//toReturn.append("<TD ALIGN=LEFT>" + "TDS Non Salary Non Resident - 27Q" + "</TD>");
					toReturn.append("<TD ALIGN=RIGHT >" + obj_FrmValidator.cBean.getTotalTDAddedIn27Q() + "</TD>");
					toReturn.append("<TD ALIGN=RIGHT >" + df.format(obj_FrmValidator.cBean.getRemittedAmtAddedTD27Q()) + "</TD>");
					toReturn.append("<TD ALIGN=RIGHT >" + obj_FrmValidator.cBean.getTotalTDUpdatedIn27Q() + "</TD>");
					toReturn.append("<TD ALIGN=RIGHT >" + df.format(obj_FrmValidator.cBean.getRemittedAmtUpdatedTD27Q()) + "</TD>");
					toReturn.append("<TD ALIGN=RIGHT >" + obj_FrmValidator.cBean.getTotalTDDeletedIn27Q() + "</TD>");
					toReturn.append("<TD ALIGN=RIGHT >" + df.format(obj_FrmValidator.cBean.getRemittedAmtDeletedTD27Q()) + "</TD>");
					toReturn.append("<TD ALIGN=RIGHT >" + df.format(obj_FrmValidator.cBean.getRemittedAmtAddedTD27Q()+obj_FrmValidator.cBean.getRemittedAmtUpdatedTD27Q()-obj_FrmValidator.cBean.getRemittedAmtDeletedTD27Q()) + "</TD>");
					toReturn.append("</TR>");




					toReturn.append("<TR>");
					//Gauri changed form type in for cBean
					if(financialYear < 26) {
						//Log.tbaf_log.info("Gauri financialYear:- " + financialYear);
						toReturn.append("<TD ALIGN=LEFT >" + "TCS - 27EQ" + "</TD>");
					}
					else {
						//Log.tbaf_log.info("Gauri financialYear:- " + financialYear);
						toReturn.append("<TD ALIGN=LEFT >" + "TCS - FN143" + "</TD>");				
						}
					//toReturn.append("<TD ALIGN=LEFT>" + "TCS - 27EQ" + "</TD>");
					toReturn.append("<TD ALIGN=RIGHT >" + obj_FrmValidator.cBean.getTotalTDAddedIn27EQ() + "</TD>");
					toReturn.append("<TD ALIGN=RIGHT >" + df.format(obj_FrmValidator.cBean.getRemittedAmtAddedTD27EQ()) + "</TD>");
					toReturn.append("<TD ALIGN=RIGHT >" + obj_FrmValidator.cBean.getTotalTDUpdatedIn27EQ() + "</TD>");
					toReturn.append("<TD ALIGN=RIGHT >" + df.format(obj_FrmValidator.cBean.getRemittedAmtUpdatedTD27EQ()) + "</TD>");
					toReturn.append("<TD ALIGN=RIGHT >" + obj_FrmValidator.cBean.getTotalTDDeletedIn27EQ() + "</TD>");
					toReturn.append("<TD ALIGN=RIGHT >" + df.format(obj_FrmValidator.cBean.getRemittedAmtDeletedTD27EQ()) + "</TD>");
					toReturn.append("<TD ALIGN=RIGHT >" + df.format(obj_FrmValidator.cBean.getRemittedAmtAddedTD27EQ()+obj_FrmValidator.cBean.getRemittedAmtUpdatedTD27EQ()-obj_FrmValidator.cBean.getRemittedAmtDeletedTD27EQ()) + "</TD>");
					toReturn.append("</TR>");


					toReturn.append("<TR>");
					toReturn.append("<TD ALIGN=LEFT >" + "<B>Grand Total</B>" + "</TD>");
					toReturn.append("<TD ALIGN=RIGHT >" +(obj_FrmValidator.cBean.getTotalTDAddedIn24Q()+obj_FrmValidator.cBean.getTotalTDAddedIn26Q()+obj_FrmValidator.cBean.getTotalTDAddedIn27Q()+obj_FrmValidator.cBean.getTotalTDAddedIn27EQ())  + "</TD>");
					toReturn.append("<TD ALIGN=RIGHT >" + df.format(obj_FrmValidator.cBean.getRemittedAmtAddedTD24Q()+obj_FrmValidator.cBean.getRemittedAmtAddedTD26Q()+obj_FrmValidator.cBean.getRemittedAmtAddedTD27Q()+obj_FrmValidator.cBean.getRemittedAmtAddedTD27EQ()) + "</TD>");
					toReturn.append("<TD ALIGN=RIGHT >" + (obj_FrmValidator.cBean.getTotalTDUpdatedIn24Q()+obj_FrmValidator.cBean.getTotalTDUpdatedIn26Q()+obj_FrmValidator.cBean.getTotalTDUpdatedIn27Q()+obj_FrmValidator.cBean.getTotalTDUpdatedIn27EQ()) + "</TD>");
					toReturn.append("<TD ALIGN=RIGHT >" + df.format(obj_FrmValidator.cBean.getRemittedAmtUpdatedTD24Q()+obj_FrmValidator.cBean.getRemittedAmtUpdatedTD26Q()+obj_FrmValidator.cBean.getRemittedAmtUpdatedTD27Q()+obj_FrmValidator.cBean.getRemittedAmtUpdatedTD27EQ()) + "</TD>");
					toReturn.append("<TD ALIGN=RIGHT >" + (obj_FrmValidator.cBean.getTotalTDDeletedIn24Q()+obj_FrmValidator.cBean.getTotalTDDeletedIn26Q()+obj_FrmValidator.cBean.getTotalTDDeletedIn27Q()+obj_FrmValidator.cBean.getTotalTDDeletedIn27EQ()) + "</TD>");
					toReturn.append("<TD ALIGN=RIGHT >" + df.format(obj_FrmValidator.cBean.getRemittedAmtDeletedTD24Q()+obj_FrmValidator.cBean.getRemittedAmtDeletedTD26Q()+obj_FrmValidator.cBean.getRemittedAmtDeletedTD27Q()+obj_FrmValidator.cBean.getRemittedAmtDeletedTD27EQ()) + "</TD>");
					toReturn.append("<TD ALIGN=RIGHT >" + df.format(obj_FrmValidator.cBean.getTotalRemittanceTD24Q()+obj_FrmValidator.cBean.getTotalRemittanceTD26Q()+obj_FrmValidator.cBean.getTotalRemittanceTD27Q()+obj_FrmValidator.cBean.getTotalRemittanceTD27EQ()) + "</TD>");
					toReturn.append("</TR>");



					toReturn.append("</TABLE>");
					toReturn.append("</CENTER>");
				}

				else
				{
					toReturn.append("<CENTER>");
					toReturn.append("<TABLE  class=tab3 BORDER=1 WIDTH=100% CELLSPACING=0 CELLPADDING=0 BORDERCOLOR=black "
							+ "style='border-collapse: collapse; '>");
					toReturn.append("<TR>");
					toReturn.append("<TD VALIGN=TOP WIDTH=50%><B>Nature of Deduction</B></TD>");
					toReturn.append("<TD ALIGN=CENTER WIDTH=10%><B>Count of DDO Records</B></TD>");
					toReturn.append("<TD ALIGN=CENTER WIDTH=20%><B>Total TDS/TCS </B> <BR> (<img src =\"rp.png\" />)</TD>");
					toReturn.append("<TD ALIGN=CENTER WIDTH=20%><B>Total TDS/TCS remitted to Govt. Account </B> <BR> (<img src =\"rp.png\" />)  </TD>");


					//toReturn.append("<img src = \"rupee.png \" /> ");
					//toReturn.append("<TD VALIGN=TOP WIDTH=290><B>Count of Distinct DDOs (TANs)</B></TD>");
					
					//Gauri changes the form type by FY condition for CR 89435, FVU 1.9::START
					toReturn.append("</TR>");
					toReturn.append("<TR>");
					if(financialYear < 27) {
						//Log.tbaf_log.info("Gauri financialYear:- " + financialYear);
						toReturn.append("<TD ALIGN=LEFT >" + "TDS Salary - 24Q" + "</TD>");
					}
					else {
						//Log.tbaf_log.info("Gauri financialYear:- " + financialYear);
						toReturn.append("<TD ALIGN=LEFT >" + "TDS Salary - FN138" + "</TD>");				
						}
					toReturn.append("<TD ALIGN=RIGHT >" + fStatistics.getCount24Q() + "</TD>");
					toReturn.append("<TD ALIGN=RIGHT >" + fStatistics.getTotalTDSTCS24Q() + "</TD>");
					toReturn.append("<TD ALIGN=RIGHT >" + fStatistics.getTotalTDSTCSTransferred24Q() + "</TD>");
					
					
					
//					toReturn.append("</TR>");
//					toReturn.append("<TR>");
//					toReturn.append("<TD ALIGN=LEFT>" + "TDS Salary - 24Q" + "</TD>");
//					toReturn.append("<TD ALIGN=RIGHT >" + fStatistics.getCount24Q() + "</TD>");
//					toReturn.append("<TD ALIGN=RIGHT >" + fStatistics.getTotalTDSTCS24Q() + "</TD>");
//					toReturn.append("<TD ALIGN=RIGHT >" + fStatistics.getTotalTDSTCSTransferred24Q() + "</TD>");										
											
					//toReturn.append("<TD rowspan=4 VALIGN = CENTER  ALIGN=LEFT WIDTH = 166>" + fStatistics.getCountOfDistinctTD()+ "</TD>");
					//toReturn.append("<TD ALIGN=RIGHT>" + fStatistics.getCountOfTD() + "</TD>");
					//toReturn.append("<TD ALIGN=RIGHT>" + fStatistics.getTotalTax() + "</TD>");
					toReturn.append("</TR>");
					toReturn.append("<TR>");
					if(financialYear < 27 ){
						toReturn.append("<TD ALIGN=LEFT >" + "TDS Non Salary - 26Q" + "</TD>");
					}
					else {
						toReturn.append("<TD ALIGN=LEFT >" + "TDS Non Salary - FN140" + "</TD>");
					}
					toReturn.append("<TD ALIGN=RIGHT >" + fStatistics.getCount26Q() + "</TD>");
					toReturn.append("<TD ALIGN=RIGHT >" + fStatistics.getTotalTDSTCS26Q() + "</TD>");
					toReturn.append("<TD ALIGN=RIGHT >" + fStatistics.getTotalTDSTCSTransferred26Q() + "</TD>");

					//toReturn.append("<TD ALIGN=RIGHT>" + fStatistics.getCountOfTD() + "</TD>");
					//toReturn.append("<TD ALIGN=RIGHT>" + fStatistics.getTotalTax() + "</TD>");
					toReturn.append("</TR>");
					toReturn.append("<TR>");
					if(financialYear < 27 ){
						toReturn.append("<TD ALIGN=LEFT >" + "TDS Non Salary Non Resident - 27Q" + "</TD>");
					}
					else {
						toReturn.append("<TD ALIGN=LEFT >" + "TDS Non Salary Non Resident - FN144" + "</TD>");
					}					
					toReturn.append("<TD ALIGN=RIGHT >" + fStatistics.getCount27Q() + "</TD>");
					toReturn.append("<TD ALIGN=RIGHT >" + fStatistics.getTotalTDSTCS27Q() + "</TD>");
					toReturn.append("<TD ALIGN=RIGHT >" + fStatistics.getTotalTDSTCSTransferred27Q() + "</TD>");

					//toReturn.append("<TD ALIGN=RIGHT>" + fStatistics.getCountOfTD() + "</TD>");
					//toReturn.append("<TD ALIGN=RIGHT>" + fStatistics.getTotalTax() + "</TD>");
					toReturn.append("</TR>");
					toReturn.append("<TR>");
					if(financialYear < 27 ){
						toReturn.append("<TD ALIGN=LEFT >" + "TCS - 27EQ" + "</TD>");
					}
					else {
						toReturn.append("<TD ALIGN=LEFT >" + "TCS - FN143" + "</TD>");
					}					
					toReturn.append("<TD ALIGN=RIGHT >" + fStatistics.getCount27EQ() + "</TD>");
					toReturn.append("<TD ALIGN=RIGHT >" + fStatistics.getTotalTDSTCS27EQ() + "</TD>");
					toReturn.append("<TD ALIGN=RIGHT >" + fStatistics.getTotalTDSTCSTransferred27EQ() + "</TD>");

					//toReturn.append("<TD ALIGN=RIGHT>" + fStatistics.getCountOfTD() + "</TD>");
					//toReturn.append("<TD ALIGN=RIGHT>" + fStatistics.getTotalTax() + "</TD>");

					toReturn.append("</TR>");
					
					//Gauri changes the form type by FY condition for CR 89435, FVU 1.9::END

					toReturn.append("<TR>");
					toReturn.append("<TD ALIGN=LEFT >" + "<B>Grand Total</B>" + "</TD>");
					int c24=0;
					int c26=0;
					int c27=0;
					int c27EQ=0;
					if(!fStatistics.getCount24Q().equals("-") && !fStatistics.getCount24Q().equals("0.00"))
					{
						c24=Integer.parseInt(fStatistics.getCount24Q());
					}
					if(!fStatistics.getCount26Q().equals("-") && !fStatistics.getCount26Q().equals("0.00"))
					{
						c26=Integer.parseInt(fStatistics.getCount26Q());
					}
					if(!fStatistics.getCount27Q().equals("-") && !fStatistics.getCount27Q().equals("0.00"))
					{
						c27=Integer.parseInt(fStatistics.getCount27Q());
					}
					if(!fStatistics.getCount27EQ().equals("-") && !fStatistics.getCount27Q().equals("0.00"))
					{
						c27EQ=Integer.parseInt(fStatistics.getCount27EQ());
					}
					toReturn.append("<TD ALIGN=RIGHT ><B>" + (c24+c26+c27+c27EQ)+ "</B></TD>");
					toReturn.append("<TD ALIGN=RIGHT ><B>" + fStatistics.getTotalTax()+ "</B></TD>");
					toReturn.append("<TD ALIGN=RIGHT ><B>" + fStatistics.getTotalTDSTCSTransferred()+ "</B></TD>");
					//toReturn.append("<TD ALIGN=RIGHT>" + fStatistics.getCountOfTD() + "</TD>");
					//toReturn.append("<TD ALIGN=RIGHT>" + fStatistics.getTotalTax() + "</TD>");

					toReturn.append("</TR>");

					toReturn.append("</TABLE>");
					toReturn.append("</CENTER>");
				}
				toReturn.append("<P>");
				toReturn.append("<B>Transaction Detail Records with TDS/TCS transferred amount (0.00) (excluding records with mode D): </B>"+fStatistics.getCountOfTdZeroTaxExD());  //Value to be inserted
				toReturn.append("<BR>");
				int cTD=0;
				if(!fStatistics.getCountOfTD().equals("-"))
				{
					cTD=Integer.parseInt(fStatistics.getCountOfTD());
				}
				toReturn.append("<B>Number of records to be charged: </B>"+(cTD));
				toReturn.append("<BR>");
				//toReturn.append("Total Count of DDO:"+fStatistics.getCountOfTD());


				//toReturn.append("<div style='border-style:ridge ; border-width:2 ; border-color:balck;' > </div>");
				//toReturn.append("<B>"+"______________________________________________________________________________________________________________________________________________________"+"</B>");


		/*		toReturn.append("<B>"+ "TO BE FILLED IN BY THE AO: "+"</B>");
*/
				toReturn.append("<BR><BR>");
//				toReturn.append("<TABLE BORDER=1 WIDTH =100% CELLSPACING=1 CELLPADDING=0 BORDERCOLOR=000000 "
//						+ "style='border-collapse: collapse; '>");
				toReturn.append("<CENTER>");
/*				toReturn.append("<TABLE  class=tab3 BORDER=1 WIDTH =100% CELLSPACING=0 CELLPADDING=0 BORDERCOLOR=black "					+ "style='border-collapse: collapse; '>");
				toReturn.append("<TR>");
				toReturn.append("<TD VALIGN=TOP WIDTH=25%><B><CENTER>Count of DDO Added</CENTER></B></TD>");
				toReturn.append("<TD VALIGN=TOP WIDTH=25%><B><CENTER>Count of DDO Updated</CENTER></B></TD>");
				toReturn.append("<TD VALIGN=TOP WIDTH=25%><B><CENTER>Count of DDO Deleted</CENTER></B></TD>");
				toReturn.append("<TD VALIGN=TOP WIDTH=25% style='border: 1.0px;'><B><CENTER>Count of Total DDOs associated with AO</CENTER></B></TD>");

				toReturn.append("</TR>");
				toReturn.append("<TR>");

				toReturn.append("<TD VALIGN=TOP WIDTH=359 HEIGHT=25><B>&nbsp;&nbsp;</B></TD>");
				toReturn.append("<TD VALIGN=TOP WIDTH=330><B>&nbsp;&nbsp;</B></TD>");
				toReturn.append("<TD VALIGN=TOP WIDTH=359><B>&nbsp;&nbsp;</B></TD>");
				toReturn.append("<TD VALIGN=TOP WIDTH=359><B>&nbsp;&nbsp;</B></TD>");



				toReturn.append("</TR>");

				toReturn.append("</TABLE><BR>");

*/
	


				
				toReturn.append("<CENTER><B><U>VERIFICATION</U></B></CENTER>");
				toReturn.append("<BR></BR>");
				toReturn.append("</B>");
                //added by faizan for FVU 1.4
				
				toReturn.append("<p>&nbsp;&nbsp;&nbsp;I,&nbsp;" +"<u>"+ fStatistics.getResponsiblePersonName()+"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"+"__________________"+"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"+"</u>"+"&nbsp;&nbsp;" + "hereby certify that all the" +
				" particulars furnished above are correct and complete.</p>" );
			//	toReturn.append("<BR><BR>");
				toReturn.append("<TABLE>");
				toReturn.append("<TR>");
				//toReturn.append("<TD>" + "Place: " + "</TD>");
				//added by faizan for FVU 1.4
				toReturn.append("<TD COLSPAN = 2>" + "&nbsp;&nbsp;" + "<B>Place: </B>"+fStatistics.getAoCity()+ "</TD>");
				toReturn.append("</TR>");
				toReturn.append("<TR>");
				//toReturn.append("<TD>" + "Date: " + "</TD>");
				//added by Faizan for FVU 1.4
				toReturn.append("<TD WIDTH = 300>" + "&nbsp;&nbsp;" + "<B>Date: </B>" +obj_FrmValidator.fileCreationDate.substring(0,2)+"/"+obj_FrmValidator.fileCreationDate.substring(2,4)+"/"+obj_FrmValidator.fileCreationDate.substring(4,8)+"</TD>");
				//Gauri changed a form name in PDF for CR 89435, FVU 1.9
				if(Integer.parseInt(fStatistics.getFinancialYear()) < 2026) {
					toReturn.append("<TD ALIGN=RIGHT WIDTH = 800><B>" + "Signature of person responsible for furnishing Form 24G" + "</B></TD>");
				}
				else {
					toReturn.append("<TD ALIGN=RIGHT WIDTH = 800><B>" + "Signature of person responsible for furnishing Form 137" + "</B></TD>");
				}
				toReturn.append("</TR>");
				toReturn.append("</TABLE>");
				toReturn.append("<BR></BR>");
				//toReturn.append("&nbsp;&nbsp;" + "*Name");
				toReturn.append( "&nbsp;&nbsp;" +"<B>*Name & Designation</B> _______________________________________________________________________________" +"<BR></BR>");
				toReturn.append("<BR></BR>" + "&nbsp;&nbsp;" + "<B>Signature</B> __________________________________________________________________________________________<BR></BR>");
				toReturn.append("<BR><TABLE>");
				toReturn.append("<TR>");
				toReturn.append("<TD>");
				toReturn.append("&nbsp;&nbsp;" + "<B>Notes :</B>");
				toReturn.append("<BR>" + "&nbsp;*To be counter signed by the person who is his immediate superior in case "
						+ "the PAO is also a DDO whose details are being &nbsp;given in the statement.");
				toReturn.append("</TD>");
				toReturn.append("</TR>");
				toReturn.append("</TABLE>");
				toReturn.append("<TABLE>");
				toReturn.append("<TR>");
//				toReturn.append("<TD>");
//				toReturn.append("<B>&nbsp;FVU Version : " + FVUVersion+"</B>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<BR>");
//				toReturn.append("</TD>");
//				toReturn.append("<TD style='text-align:right;width:70%;'><B> Input File Name : " + fileName + " </B><BR></TD>");
				//addded by faizan for FVU 1.4
				toReturn.append("<TD >");
				toReturn.append("<B>&nbsp;FVU Version : " + FVUVersion+"</B>&nbsp;&nbsp;&nbsp;");
				toReturn.append("</TD>");
			    toReturn.append("<TD style='text-align:right; width:85%;'><B> Input File Name : " + fileName + " </B><BR></TD>");
			    //ended by faizan
				//toReturn.append("Input File Name : " + fileName + " <BR>");
				//toReturn.append("</TD>");
				toReturn.append("</TR>");
				toReturn.append("</TABLE>");
				toReturn.append("</TABLE>");
				toReturn.append("</CENTER>");
				toReturn.append("</BODY>");
				toReturn.append("</HTML>");
			}
		
			TBAFFileGenerator flgObj = null;
			flgObj =new TBAFFileGenerator();
			String sImagePath=statisticFileName.substring(0,statisticFileName.lastIndexOf('\\')+1);
			//Added for barcode generation FVU 4.1
			int financialYear = Integer.parseInt(fStatistics.getFinancialYear().substring(2, 4));
			String finYr = null;
			if (financialYear >= 9 && financialYear < 99)
			{
				financialYear = financialYear + 1;
				String finYear = String.valueOf(financialYear);
				finYr= fStatistics.getFinancialYear() + finYear;
			}
			else if (financialYear == 99)
			{
				finYr= fStatistics.getFinancialYear() + "00";
			}
			else
			{
				financialYear = financialYear + 1;
				String finYear = String.valueOf(financialYear);
				finYr = fStatistics.getFinancialYear() + "0" + finYear ;
			}
	        //String ackNum=fStatistics.getAIN()+finYr+MONTH[Integer.parseInt(fStatistics.getMonthOfTransaction())];
	        //added by faizan for FVU 1.4
			String ackNum=fStatistics.getAIN()+finYr+MONTH1[Integer.parseInt(fStatistics.getMonthOfTransaction())];
	        File ostrm=new File(sImagePath+"barcode.jpeg");
	        Barcode barcode = null;
	        barcode=BarcodeFactory.createCode128(ackNum);
	        
			barcode.setBarWidth(1);
			barcode.setBarHeight(100);
			barcode.setDrawingText(false);
			BarcodeImageHandler.saveJPEG(barcode,ostrm);
			if(statisticFileName.contains(".html"))
			{
				String filenaam = statisticFileName.substring(statisticFileName.lastIndexOf('\\')+1,statisticFileName.lastIndexOf('.'));
				flgObj.writePdfFile(statisticFileName,filenaam, toReturn);
			}
			else
			{
				flgObj.writePdfFile(statisticFileName,statisticFileName+".pdf", toReturn);
			}
			
			toReturn = new StringBuffer();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			Log.tbaf_log.error("EXCEPTION in FileGenerator.java createStatisticFileString() : ",  e);
		}
	}
	
	
	//end method
	
	/**
	 * Method to parse the HTML Error file Data from the Error String Buffer
	 */
	private TBAFFileStatistics parseDelemitedErrorFile(String StringToParse, String Seperator)
	{
		TBAFFileStatistics objFileStatistics = new TBAFFileStatistics();
		int counter = 1;
		boolean FieldFound = false;
		boolean Caret = true;
		int localCaretCounter = 0;
		StringTokenizer Obj_st = new StringTokenizer(StringToParse, Seperator, true);
		while (Obj_st.hasMoreTokens())
		{
			String token = Obj_st.nextToken();
			FieldFound = false;
			if ((token.equals(TBAF_FIELD_SEPERATOR) && Caret) || token.trim().length() == 0)
			{
				FieldFound = true;
			}
			if (token.equals(TBAF_FIELD_SEPERATOR))
			{
				Caret = true;
				token = "-";
				if ((counter == 1) && (localCaretCounter == 0))
				{
					counter++;
					objFileStatistics.setLineNo(token);
				}
				localCaretCounter++;
			}
			else
			{
				Caret = false;
				FieldFound = true;
			}
			if (counter > 5)
			{
				break;
			}
			if (FieldFound)
			{
				if (counter == 1) // Record Type
				{
					objFileStatistics.setTbafRecType(token);
				}
				else if (counter == 2) // Line No
				{
					objFileStatistics.setLineNo(token);
				}
				else if (counter == 3) // Field Name
				{
					objFileStatistics.setBatchNo(token);
				}
				else if (counter == 4) //Transaction Detail No
				{
					objFileStatistics.setTransactionNo(token);
				}
				else if (counter == 5) //Error Code and Error Meaasge
				{
					int firstIndex = token.indexOf(" ");
					objFileStatistics.setErrorCode(token.substring(0, firstIndex));
					objFileStatistics.setErrorDescription(token.substring(firstIndex, token.length()));
				}
				counter++;
			}
		} //WHILE
		//	Log.tds_log.info("Exiting parseDelemitedErrorFile Method ");
		return objFileStatistics;
	}
	/*
	 * Method to parse String to get Data for Return Statistics Report
	 */
	public TBAFFileStatistics getStatisticFileDetail(String StringToParse, String Seperator, TBAFFileStatistics fStatistics)
	{
		Log.tbaf_log.info("FileGenerator.java getStatisticaFileDetail() method Called");
		System.out.println("Divya" + StringToParse);
		StringTokenizer batch = new StringTokenizer(StringToParse, Seperator);
		int counter = 0;
		while (batch.hasMoreTokens())
		{

			String token = batch.nextToken();

			if (counter == 0) // Type of Statement
			{
				fStatistics.setTypeOfStatement(token.trim());
				System.out.println(counter + " setTypeOfStatement " + (token.trim()));
			}
			else if (counter == 1) //AIN TFC ID
			{
				fStatistics.setAinTFCId(token.trim());
				System.out.println(counter + " setTypeOfStatement " + (token.trim()));
			}
			else if (counter == 2) //FVU Version
			{
				fStatistics.setFvuVersion(token.trim());
				System.out.println(counter + " setFvuVersion " + (token.trim()));
			}
			else if (counter == 3) //FVU File Level Hash
			{
				fStatistics.setFvuHash(token.trim());
				System.out.println(counter + " setFvuHash " + (token.trim()));
			}
			else if (counter == 4) //SAM Version
			{
				fStatistics.setSamVersion(token.trim());
				System.out.println(counter + " setSamVersion " + (token.trim()));
			}
			else if (counter == 5) //SAM Hash
			{
				fStatistics.setSamHash(token.trim());
				System.out.println(counter + " setSamHash " + (token.trim()));
			}
			else if (counter == 6) //Transaction Type
			{
				fStatistics.setTransactionType(token.trim());
				System.out.println(counter + " setTransactionType " + (token.trim()));
			}
			else if (counter == 7) //AIN
			{
				fStatistics.setAIN(token.trim());
				System.out.println(counter + " setAIN " + (token.trim()));
			}
			else if (counter == 8) //Last AIN
			{
				fStatistics.setLastAIN(token.trim());
				System.out.println(counter + " setLastAIN " + (token.trim()));
			}
			else if (counter == 9) //AO Name
			{
				fStatistics.setNameOfAO(token.trim());
				System.out.println(counter + " setNameOfAO " + (token.trim()));
			}
			else if (counter == 10) //AO Address 1
			{
				fStatistics.setAoAdd1(token.trim());
				System.out.println(counter + " setAoAdd1 " + (token.trim()));
			}
			else if (counter == 11) //AO Address 2
			{
				fStatistics.setAoAdd2(token.trim());
				System.out.println(counter + " setAoAdd2 " + (token.trim()));
			}
			else if (counter == 12) //AO Address 3
			{
				fStatistics.setAoAdd3(token.trim());
				System.out.println(counter + " setAoAdd3 " + (token.trim()));
			}
			else if (counter == 13) // AO Address 4
			{
				fStatistics.setAoAdd4(token.trim());
				System.out.println(counter + " setAoAdd4 " + (token.trim()));
			}
			else if (counter == 14) // AO City
			{
				fStatistics.setAoCity(token.trim());
				System.out.println(counter + " setAoCity " + (token.trim()));
			}
			else if (counter == 15) // AO State
			{
				fStatistics.setAoState(token.trim());
				System.out.println(counter + " setAoState " + (token.trim()));
			}
			else if (counter == 16) // AO PIN
			{
				fStatistics.setAoPIN(token.trim());
				System.out.println(counter + " setAoPIN " + (token.trim()));
			}
			else if (counter == 17) // Responsible Person Name
			{
				fStatistics.setResponsiblePersonName(token);
				System.out.println(counter + " setResponsiblePersonName " + (token.trim()));
			}
			else if (counter == 18) // Financial Year
			{
				fStatistics.setFinancialYear(token);
				System.out.println(counter + " setFinancialYear " + (token.trim()));
			}
			else if (counter == 19) // Last Financial Year
			{
				fStatistics.setLastFinancialYear(token);
				System.out.println(counter + " setLastFinancialYear " + (token.trim()));
			}
			else if (counter == 20) // Deductor Category
			{
				fStatistics.setDeductorCat(token.toString().trim());
				System.out.println(counter + " setDeductorCat " + (token.trim()));
			}
			else if (counter == 21) // Last Deductor Category
			{
				fStatistics.setLastDeductorCat(token);
				System.out.println(counter + " setLastDeductorCat " + (token.trim()));
			}
			else if (counter == 22) // Quarter
			{
				fStatistics.setQuarter(token);
				System.out.println(counter + " setQuarter " + (token.trim()));
			}
			else if (counter == 23) // Last Quarter
			{
				fStatistics.setLastQuarter(token);
				System.out.println(counter + " setLastQuarter " + (token.trim()));
			}
			/*	else if (counter == 24) // Nature OF Deduction
			{
				fStatistics.setNatureOfDed(token);
			}*/
			else if (counter == 25) // Last Nature OF Deduction
			{
				fStatistics.setLastNatureOfDed(token);
				System.out.println(counter + " setLastNatureOfDed " + (token.trim()));
			}
			else if (counter == 26) // Count of DDO TD
			{
				fStatistics.setCountOfTD(token);
				System.out.println(counter + " setCountOfTD " + (token.trim()));
			}
			else if (counter == 27) // Total Tax Amount
			{
				fStatistics.setTotalTax(token);
				System.out.println(counter + " setTotalTax " + (token.trim()));
			}
			else if (counter == 28) // Original RRR
			{
				fStatistics.setOiginalRecptNo(token);
				System.out.println(counter + " setOiginalRecptNo " + (token.trim()));
			}
			else if (counter == 29) // PRN number (Only For TFC Upload)
			{
				fStatistics.setReceiptNumber(token);
				System.out.println(counter + " setReceiptNumber " + (token.trim()));
			}
			else if (counter == 30) // PRN date (Only For TFC Upload)
			{
				fStatistics.setDate(token);
				System.out.println(counter + " setDate " + (token.trim()));
			}
			else if (counter == 31) // set Month of transfer Voucher  //Added by Subhankar
			{
				fStatistics.setMonthOfTransaction(token);
				System.out.println(counter + " setMonthOfTransaction " + (token.trim()));
			}
//			else if (counter == 32) // Count TD with Tax Amt 0.00
//			{
//				fStatistics.zeroTDTaxAmtCounter(token);
//			}
			/*else if (counter == 33) // No of Lines
			{
				fStatistics.setNoOfLines(token);
			}*/
			else if (counter == 44)     //State Name                  //Added by Subhankar
			{
				fStatistics.setStateName(token);
				System.out.println(counter + " setStateName " + (token.trim()));
			}
			else if (counter == 45) //Ministry Name                   //Added by Subhankar
			{
				fStatistics.setMinistryName(token);
				System.out.println(counter + " setMinistryName " + (token.trim()));
			}
			else if (counter == 46) //SUB Ministry Name                   //Added by Subhankar
			{
				fStatistics.setSubMinistryName(token);
				System.out.println(counter + " setSubMinistryName " + (token.trim()));
			}
			else if (counter == 47) //SUB Ministry Name                   //Added by Subhankar
			{
				fStatistics.setSubMinistryName_O(token);
				System.out.println(counter + " setSubMinistryName_O " + (token.trim()));
			}

			else if (counter == 48) //Count of 24Q TD                  //Added by Subhankar
			{
				fStatistics.setCount24Q(token);
				System.out.println(counter + " setCount24Q " + (token.trim()));
			}
			else if (counter == 49) //Total Tax for 24Q TD                  //Added by Subhankar
			{
				fStatistics.setTotalTDSTCS24Q(token);
				System.out.println(counter + " setTotalTDSTCS24Q " + (token.trim()));
			}
			else if (counter == 50) //Total  Remitted Amt For 24Q      //Added by Subhankar
			{
				fStatistics.setTotalTDSTCSTransferred24Q(token);
				System.out.println(counter + " setTotalTDSTCSTransferred24Q " + (token.trim()));
			}

			else if (counter == 51)  //Count of 26Q TD                 //Added by Subhankar
			{
				fStatistics.setCount26Q(token);
				System.out.println(counter + " setCount26Q " + (token.trim()));
			}
			else if (counter == 52) //Total Tax for 26Q TD                       //Added by Subhankar
			{
				fStatistics.setTotalTDSTCS26Q(token);
				System.out.println(counter + " setTotalTDSTCS26Q " + (token.trim()));
			}
			else if (counter == 53) //Total  Remitted Amt For 26Q      //Added by Subhankar
			{
				fStatistics.setTotalTDSTCSTransferred26Q(token);
				System.out.println(counter + " setTotalTDSTCSTransferred26Q " + (token.trim()));
			}

			else if (counter == 54) //Count of 27Q TD                  //Added by Subhankar
			{
				fStatistics.setCount27Q(token);
				System.out.println(counter + " setCount27Q " + (token.trim()));
			}
			else if (counter == 55) //Total Tax for 27Q TD                   //Added by Subhankar
			{
				fStatistics.setTotalTDSTCS27Q(token);
				System.out.println(counter + " setTotalTDSTCS27Q " + (token.trim()));
			}
			else if (counter == 56) //Total  Remitted Amt For 27Q      //Added by Subhankar
			{
				fStatistics.setTotalTDSTCSTransferred27Q(token);
				System.out.println(counter + " setTotalTDSTCSTransferred27Q " + (token.trim()));
			}

			else if (counter == 57) //Count of 27EQ TD                 //Added by Subhankar
			{
				fStatistics.setCount27EQ(token);
				System.out.println(counter + " setCount27EQ " + (token.trim()));
			}
			else if (counter == 58) //Total Tax for 27EQ TD                    //Added by Subhankar
			{
				fStatistics.setTotalTDSTCS27EQ(token);
				System.out.println(counter + " setTotalTDSTCS27EQ " + (token.trim()));
			}
			else if (counter == 59) //Total  Remitted Amt For 27EQ     //Added by Subhankar
			{
				fStatistics.setTotalTDSTCSTransferred27EQ(token);
				System.out.println(counter + " setTotalTDSTCSTransferred27EQ " + (token.trim()));
			}

			else if (counter == 61) //Count Of Distinct DDO           //Added by Subhankar
			{
				fStatistics.setCountOfDistinctTD(token);
				System.out.println(counter + " setCountOfDistinctTD " + (token.trim()));
			}
			else if (counter == 62) //Total TDS/TCS Transferred to government Acct.           //Added by Subhankar
			{
				fStatistics.setTotalTDSTCSTransferred(token);
				System.out.println(counter + " setTotalTDSTCSTransferred " + (token.trim()));
			}
			else if (counter == 63) //DDO in 'A' mode                 //Added by Subhankar
			{
				fStatistics.setCountOfDDOAdded(token);
				System.out.println(counter + " setCountOfDDOAdded " + (token.trim()));
			}
			else if (counter == 64) //DDO in 'U' mode                 //Added by Subhankar
			{
				fStatistics.setCountOfDDOUpdated(token);
				System.out.println(counter + " setCountOfDDOUpdated " + (token.trim()));
			}

			else if (counter == 65) //DDO in 'D' mode                //Added by Subhankar
			{
				fStatistics.setCountOfDDODeleted(token);
				System.out.println(counter + " setCountOfDDODeleted " + (token.trim()));
			}

			//Gauri added new counter for newly added field for CR 89435::START
			
			/*else if(counter == 67)  
			{
				fStatistics.setmobileNoOfAO(token);
			}*/
			/*else if(counter == 69)  
			{
				fStatistics.setTANofAO(token);
				System.out.println(counter + " setTANofAO " + (token.trim()));
			}
			else if(counter == 70)        
			{
				fStatistics.setspecialTAN(token);
				System.out.println(counter + " setspecialTAN " + (token.trim()));
			}
			else if(counter == 71) 
			{
				fStatistics.setstateAGcode(token);
				System.out.println(counter + " setstateAGcode " + (token.trim()));
			}
			else if(counter == 71)  
			{
				fStatistics.setrTitle(token);
			}
			else if(counter == 73)  
			{
				fStatistics.setrFirstName(token);
				System.out.println(counter + " setrFirstName " + (token.trim()));
			}
			else if(counter == 74) 
			{
				fStatistics.setrMiddleName(token);
				System.out.println(counter + " setrMiddleName " + (token.trim()));
			}
			else if(counter == 75)  
			{
				fStatistics.setrLastName(token);
				System.out.println(counter + " setrLastName " + (token.trim()));
			}
			else if(counter == 76)  
			{
				fStatistics.setrCountryCode(token);
				System.out.println(counter + " setrCountryCode " + (token.trim()));
			}*/
			
			
			
			
			else if (counter == 69) //Count Of VALID TAN            //Added by Subhankar
			{
				fStatistics.setCountOfValidTAN(token);
				System.out.println(counter + " setCountOfValidTAN " + (token.trim()));
			}

			else if (counter == 70) //Count Of TANAPPLIED           //Added by Subhankar
			{
				fStatistics.setCountOfTANAPPLIED(token);
				System.out.println(counter + " setCountOfTANAPPLIED " + (token.trim()));
			}
			else if(counter == 71)  //Count Of TANNOTABVL          //Added by Subhankar
			{
				fStatistics.setCountOfTANNOTABVL(token);
				System.out.println(counter + " setCountOfTANNOTABVL " + (token.trim()));
			}

			else if(counter == 72)  //Count Of TANINVALID          //Added by Subhankar
			{
				fStatistics.setCountOfTANINVALID(token);
				System.out.println(counter + " setCountOfTANINVALID " + (token.trim()));
			}

			else if(counter == 73)  //Transaction Detail Records with TDS/TCS transferred amount (0.00) (excluding records with mode D)         //Added by Subhankar
			{
				fStatistics.setCountOfTdZeroTaxExD(token);
				System.out.println(counter + " setCountOfTdZeroTaxExD " + (token.trim()));
			}
			

			counter++;
		} // while () inner While
		Log.tbaf_log.info("FileGenerator.java getStatisticaFileDetail() method is returning ");
		return fStatistics;
	}
	/*
	 * Method to create the HTML Footer of HTML Error File
	 */
	private StringBuffer createHtmlErrorFileFooter(StringBuffer toReturn)
	{
		//		FORMAT OF HTML Error File - Footer
		toReturn.append("</TABLE><BR>");
		toReturn.append("<BR><BR><BR>");
		toReturn.append("</BODY>");
		toReturn.append("</HTML>");
		return toReturn;
	}
	/*
	 * Method to create the HTML Footer of HTML Error File
	 */
	private StringBuffer createHtmlErrorFileFooter(StringBuffer toReturn, String inputFileName)
	{
		//		FORMAT OF HTML Error File - Footer
		toReturn.append("</TABLE><BR>");
		toReturn.append("<TR>");
		toReturn.append("<TD>* Field Name & No. is as per the file format</TD>");
		//toReturn.append("<BR><BR> FVU Version : " + TBAFFVUVersion + " &nbsp;&nbsp; Input File Name : " + inputFileName);
		//Gauri added a note for CR 89435, FVU 1.9::START
		toReturn.append("<BR><BR> FVU Version : " + TBAFFVUVersion );
		toReturn.append("<BR><BR>Input File Name : " + inputFileName);		
		toReturn.append("<BR><BR> ___________________________________________________________________________________________________________________________________");
		toReturn.append("<BR><BR>");
		toReturn.append("<B> " + "• Form 24G: " + "</B><TD>For statements pertaining prior to FY 2026-27.</TD>");
		toReturn.append("<BR><BR>");
		toReturn.append("<B> " + "• Form No. 137: " + "</B><TD>For statements pertaining to FY 2026-27 and onwards.</TD>");
		toReturn.append("<BR><BR> ___________________________________________________________________________________________________________________________________");
		//Gauri added a note for CR 89435, FVU 1.9::END
		toReturn.append("<BR><BR><BR>");
		toReturn.append("</BODY>");
		toReturn.append("</HTML>");
		return toReturn;
	}
	//Gauri changed the form name according to FY for CR 89435, FVU 1.9
	public StringBuffer createProvisionalReceipt(TBAFFileStatistics pReceipt, StringBuffer toReturn)
	{
		try
		{
			toReturn.append("<HTML>");
			toReturn.append("<HEAD>");
			//newly added
			if(Integer.parseInt(pReceipt.getFinancialYear().substring(2, 4)) < 26)
			{
			toReturn.append("<TITLE>Form 24G Provisional Receipt</TITLE>");
			}
			else {
				toReturn.append("<TITLE>Form 137 Provisional Receipt</TITLE>");	
			}
			toReturn.append("</HEAD>");
			toReturn.append("<BODY>");
			// TABLE NO. 1 STARTS
			toReturn.append("<TABLE BORDER=1 BORDERCOLOR=black CELLSPACING=0 CELLPADDING=0 "
					+ "style='border-collapse: collapse; border: none; mso-border-alt: solid windowtext .5pt; mso-padding-alt: 0in 5.4pt 0in 5.4pt'>");
			toReturn.append("<BR>");
			toReturn.append("<TR>");
			toReturn.append("<TD>");
			toReturn.append("<BR>");
			//newly added
			if(Integer.parseInt(pReceipt.getFinancialYear().substring(2, 4)) < 26)
			{
			toReturn.append("<H3><CENTER>Form 24G - Provisional Receipt</CENTER></H3>");
			}
			else {
				toReturn.append("<H3><CENTER>Form 137 - Provisional Receipt</CENTER></H3>");	
			}
			toReturn.append("<BR>");
			// TABLE NO.2 STARTS
			if (pReceipt.getTypeOfStatement().equals(TBAF_TYPE_OF_STMT_ORIG))
			{
				toReturn.append("<TABLE BORDER=1 BORDERCOLOR=black CELLSPACING=0 CELLPADDING=0 "
						+ "style='border-collapse: collapse; border: none; mso-border-alt: solid windowtext .5pt; mso-padding-alt: 0in 5.4pt 0in 5.4pt'>");
				toReturn.append("<TR>");
				toReturn.append("<TD WIDTH=350 ALIGN=LEFT VALIGN=TOP TEXTCOLOR=\"#F4CE85\"><B>Receipt Number</B></TD>");
				toReturn.append("<TD WIDTH=252 ALIGN=LEFT VALIGN=TOP><B>Date of Receipt</B></TD>");
				toReturn.append("<TD WIDTH=200 ALIGN=LEFT VALIGN=TOP><B>AIN</B></TD>");
				toReturn.append("</TR>");
				toReturn.append("<TR>");
				toReturn.append("<TD ALIGN=LEFT WIDTH=350 VALIGN=TOP><FONT COLOR=\"RED\"> <B> " + pReceipt.getReceiptNumber() + "</B></FONT></TD>");
				toReturn.append("<TD ALIGN=LEFT WIDTH=250 VALIGN=TOP>" + setDateFormat(pReceipt.getDate().toString()) + "</TD>");
				toReturn.append("<TD ALIGN=LEFT WIDTH=200 VALIGN=TOP><B> " + pReceipt.getAIN() + " </B></TD>");
				toReturn.append("</TR>");
				toReturn.append("</TABLE>");
				// TABLE NO.2 ENDS
				toReturn.append("<BR>");
				// TABLE NO.3 STARTS
				toReturn.append("<TABLE BORDER=1 BORDERCOLOR=black CELLSPACING=0 CELLPADDING=0 "
						+ "style='border-collapse: collapse; border: none; mso-border-alt: solid windowtext .5pt; mso-padding-alt: 0in 5.4pt 0in 5.4pt'>");
				toReturn.append("<TR>");
				toReturn.append("<TD WIDTH=400 ALIGN=LEFT VALIGN=TOP><B>Name of Accounts Office</B></TD>");
				toReturn.append("<TD WIDTH=402 ALIGN=LEFT VALIGN=TOP><B>Address of Accounts Office</B></TD>");
				toReturn.append("</TR>");
				toReturn.append("<TR>");
				/*//Gauri changed AO name for CR 89435, FVU 1.9
				if(Integer.parseInt(pReceipt.getFinancialYear().substring(2, 4)) < 25) {
					toReturn.append("<TD ALIGN=LEFT WIDTH=400 VALIGN=TOP>\" + pReceipt.getNameOfAO() + \"</TD>");
				}
				else {
					if(pReceipt.getaoFirstName() == null && pReceipt.getaoMiddleName() == null) {
						toReturn.append("<TD ALIGN=LEFT WIDTH=400 VALIGN=TOP>\" + pReceipt.getaoLastName() + \"</TD>");
					}
					else {
						toReturn.append("<TD ALIGN=LEFT WIDTH=400 VALIGN=TOP>" + (pReceipt.getaoFirstName() != null? pReceipt.getaoFirstName() + " " : " ")+
								(pReceipt.getaoMiddleName() != null? pReceipt.getaoMiddleName()  + " " : " ")+
								(pReceipt.getaoLastName()  != null? pReceipt.getaoLastName() : " ") + "</TD>");
					}
				}//END
*/				
				toReturn.append("<TD ALIGN=LEFT WIDTH = 400 VALIGN=TOP>" + pReceipt.getNameOfAO() + "</TD>");
				toReturn.append("<TD ALIGN=LEFT WIDTH = 400 VALIGN=TOP>");
				toReturn.append(pReceipt.getAoAdd1());
				toReturn.append(", ");
				if (!pReceipt.getAoAdd2().equals(""))
				{
					toReturn.append(pReceipt.getAoAdd2());
					toReturn.append(", ");
				}
				if (!pReceipt.getAoAdd3().equals(""))
				{
					toReturn.append(pReceipt.getAoAdd3());
					toReturn.append(", ");
				}
				if (!pReceipt.getAoAdd4().equals(""))
				{
					toReturn.append(pReceipt.getAoAdd4());
					toReturn.append(", ");
				}
				toReturn.append(pReceipt.getAoCity());
				toReturn.append(", ");
				toReturn.append(TBAF_STATE_NAME[Integer.parseInt(pReceipt.getAoState().trim())]);
				toReturn.append(", ");
				toReturn.append(pReceipt.getAoPIN());
				toReturn.append("</TD>");
				toReturn.append("</TR>");
				toReturn.append("</TABLE>");
				// TABLE NO.3 ENDS
				toReturn.append("<BR>");
				toReturn.append(
						"<TABLE BORDER=1 BORDERCOLOR=black CELLSPACING=0 CELLPADDING=0 "
						+ "style='border-collapse: collapse; border: none; mso-border-alt: solid windowtext .5pt; mso-padding-alt: 0in 5.4pt 0in 5.4pt'>");
				toReturn.append("<TR>");
				toReturn.append("<TD WIDTH=150 ALIGN=LEFT VALIGN=TOP><B> Financial Year</B></TD>");
				toReturn.append("<TD WIDTH=100 ALIGN=LEFT VALIGN=TOP><B> Quarter</B></TD>");
				toReturn.append("<TD WIDTH=150 ALIGN=LEFT VALIGN=TOP><B> Type of Statement</B></TD>");
				//newly added
				if(Integer.parseInt(pReceipt.getFinancialYear().substring(2, 4)) < 26) {
				toReturn.append("<TD WIDTH=200 ALIGN=LEFT VALIGN=TOP><B> Nature of Deduction</B></TD>");
				}
				else {
					toReturn.append("<TD WIDTH=200 ALIGN=LEFT VALIGN=TOP><B> Form Type</B></TD>");	
				}
				toReturn.append("<TD WIDTH=200 ALIGN=LEFT VALIGN=TOP><B> Category of Deductor</B></TD>");
				toReturn.append("</TR>");
				toReturn.append("<TR>");
				int financialYearForReceipt = Integer.parseInt(pReceipt.getFinancialYear().substring(2, 4));
				if (financialYearForReceipt >= 9 && financialYearForReceipt < 99)
				{
					financialYearForReceipt = financialYearForReceipt + 1;
					String finYear = String.valueOf(financialYearForReceipt);
					toReturn.append("<TD ALIGN=LEFT WIDTH=150>" + pReceipt.getFinancialYear() + "-" + finYear + "</TD>");
				}
				else if (financialYearForReceipt == 99)
				{
					toReturn.append("<TD ALIGN=LEFT WIDTH=150>" + pReceipt.getFinancialYear() + "-" + "00" + "</TD>");
				}
				else
				{
					financialYearForReceipt = financialYearForReceipt + 1;
					String finYear = String.valueOf(financialYearForReceipt);
					toReturn.append("<TD ALIGN=LEFT WIDTH=150>" + pReceipt.getFinancialYear() + "-" + "0" + finYear + "</TD>");
				}
				toReturn.append("<TD ALIGN=LEFT WIDTH=100 VALIGN=TOP>" + pReceipt.getQuarter() + "</TD>");
				toReturn.append("<TD ALIGN=LEFT WIDTH=150 VALIGN=TOP> Original </TD>");
				
				if(financialYearForReceipt < 26 && pReceipt.getNatureOfDed().equals(TBAF_FORM_24Q)) {
					toReturn.append("<TD ALIGN=LEFT WIDTH=200>" + "TDS-SAL " + "(" + pReceipt.getNatureOfDed() + ")" + "</TD>");
				}
				else if(financialYearForReceipt >= 26 && pReceipt.getNatureOfDed().equals(TBAF_NEW_FORM_24Q)){
					toReturn.append("<TD ALIGN=LEFT WIDTH=200>" + "TDS-SAL " + "(" + pReceipt.getNatureOfDed() + ")" + "</TD>");				
					}
				
//				if (pReceipt.getNatureOfDed().equals(TBAF_FORM_24Q))
//				{
//					toReturn.append("<TD ALIGN=LEFT WIDTH=200>" + "TDS-SAL " + "(" + pReceipt.getNatureOfDed() + ")" + "</TD>");
//				}
				
				else if (financialYearForReceipt < 26 && pReceipt.getNatureOfDed().equals(TBAF_FORM_26Q))
				{
					toReturn.append("<TD ALIGN=LEFT WIDTH=200>" + "TDS-NON-SAL " + "(" + pReceipt.getNatureOfDed() + ")" + "</TD>");
				}
				else if (financialYearForReceipt >= 26 && pReceipt.getNatureOfDed().equals(TBAF_NEW_FORM_26Q))
				{
					toReturn.append("<TD ALIGN=LEFT WIDTH=200>" + "TDS-NON-SAL " + "(" + pReceipt.getNatureOfDed() + ")" + "</TD>");
				}
				
				else if (financialYearForReceipt < 26 && pReceipt.getNatureOfDed().equals(TBAF_FORM_27Q))
				{
					toReturn.append("<TD ALIGN=LEFT WIDTH=200>" + "TDS-NR " + "(" + pReceipt.getNatureOfDed() + ")" + "</TD>");
				}
				else if (financialYearForReceipt >= 26 && pReceipt.getNatureOfDed().equals(TBAF_NEW_FORM_27Q))
				{
					toReturn.append("<TD ALIGN=LEFT WIDTH=200>" + "TDS-NR " + "(" + pReceipt.getNatureOfDed() + ")" + "</TD>");
				}
				
				else if (financialYearForReceipt < 26 && pReceipt.getNatureOfDed().equals(TBAF_FORM_27EQ))
				{
					toReturn.append("<TD ALIGN=LEFT WIDTH=200>" + "TCS " + "(" + pReceipt.getNatureOfDed() + ")" + "</TD>");
				}
				else if (financialYearForReceipt >= 26 && pReceipt.getNatureOfDed().equals(TBAF_NEW_FORM_27EQ))
				{
					toReturn.append("<TD ALIGN=LEFT WIDTH=200>" + "TCS " + "(" + pReceipt.getNatureOfDed() + ")" + "</TD>");
				}
				
				toReturn.append("<TD ALIGN=LEFT WIDTH=200 VALIGN=TOP>" + Parameters.GovtMap.get(pReceipt.getDeductorCat().trim()) + "</TD>");
				toReturn.append("</TR>");
				toReturn.append("</TABLE>");
			}
			else if (pReceipt.getTypeOfStatement().equals(TBAF_TYPE_OF_STMT_CORR))
			{
				toReturn.append(
						"<TABLE BORDER=1 BORDERCOLOR=black CELLSPACING=0 CELLPADDING=0 "
						+ "style='border-collapse: collapse; border: none; mso-border-alt: solid windowtext .5pt; mso-padding-alt: 0in 5.4pt 0in 5.4pt'>");
				toReturn.append("<TR>");
				toReturn.append("<TD WIDTH=400 ALIGN=LEFT VALIGN=TOP TEXTCOLOR=\"#F4CE85\"><B>Receipt Number</B></TD>");
				toReturn.append("<TD WIDTH=200 ALIGN=LEFT VALIGN=TOP><B>Date of Receipt</B></TD>");
				toReturn.append("<TD WIDTH=200 ALIGN=LEFT VALIGN=TOP><B>AIN</B></TD>");
				toReturn.append("</TR>");
				toReturn.append("<TR>");
				toReturn.append("<TD ALIGN=LEFT WIDTH=400 VALIGN=TOP><FONT COLOR=\"RED\"> <B> " + pReceipt.getReceiptNumber() + "</B></FONT></TD>");
				toReturn.append("<TD ALIGN=LEFT WIDTH=200 VALIGN=TOP>" + setDateFormat(pReceipt.getDate().toString()) + "</TD>");
				toReturn.append("<TD ALIGN=LEFT WIDTH=200 VALIGN=TOP><B> " + pReceipt.getAIN() + " </B></TD>");
				toReturn.append("</TR>");
				toReturn.append("</TABLE>");
				toReturn.append("<BR>");
				if (pReceipt.getTransactionType().equals(TBAF_TRANSACTION_TYPE_C3))
				{
					toReturn.append(
							"<TABLE BORDER=1 BORDERCOLOR=black CELLSPACING=0 CELLPADDING=0 "
							+ "style='border-collapse: collapse; border: none; mso-border-alt: solid windowtext .5pt; mso-padding-alt: 0in 5.4pt 0in 5.4pt'>");
					toReturn.append("<TR>");
					toReturn.append("<TD WIDTH=400 ALIGN=LEFT VALIGN=TOP><B>Name of Accounts Office</B></TD>");
					toReturn.append("<TD WIDTH=401 ALIGN=LEFT VALIGN=TOP><B> Original Receipt Number </B></TD>");
					toReturn.append("</TR>");
					toReturn.append("<TR>");
					/*//Gauri changed AO name for CR 89435, FVU 1.9-PENDING
					if(Integer.parseInt(pReceipt.getFinancialYear().substring(2, 4)) < 25) {
						toReturn.append("<TD ALIGN=LEFT WIDTH=400 VALIGN=TOP>\" + pReceipt.getNameOfAO() + \"</TD>");
					}
					else {
						if(pReceipt.getaoFirstName() == null && pReceipt.getaoMiddleName() == null) {
							toReturn.append("<TD ALIGN=LEFT WIDTH=400 VALIGN=TOP>\" + pReceipt.getaoLastName() + \"</TD>");
						}
						else {
							toReturn.append("<TD ALIGN=LEFT WIDTH=400 VALIGN=TOP>" + (pReceipt.getaoFirstName() != null? pReceipt.getaoFirstName() + " " : " ")+
									(pReceipt.getaoMiddleName() != null? pReceipt.getaoMiddleName() + " " : " ")+
									(pReceipt.getaoLastName() != null? pReceipt.getaoLastName() : " ") + "</TD>");
						}
					}//END
*/					toReturn.append("<TD ALIGN=LEFT WIDTH=400 VALIGN=TOP>" + pReceipt.getNameOfAO() + "</TD>");
					toReturn.append("<TD ALIGN=LEFT WIDTH=400 VALIGN=TOP>" + pReceipt.getOiginalRecptNo() + "</TD>");
					toReturn.append("</TR>");
					toReturn.append("</TABLE>");
				}
				else
				{
					toReturn.append(
							"<TABLE BORDER=1 BORDERCOLOR=black CELLSPACING=0 CELLPADDING=0 "
							+ "style='border-collapse: collapse; border: none; mso-border-alt: solid windowtext .5pt; mso-padding-alt: 0in 5.4pt 0in 5.4pt'>");
					toReturn.append("<TR>");
					toReturn.append("<TD WIDTH=300 ALIGN=LEFT VALIGN=TOP><B>Name of Accounts Office</B></TD>");
					toReturn.append("<TD WIDTH=301 ALIGN=LEFT VALIGN=TOP><B>Address of Accounts Office</B></TD>");
					toReturn.append("<TD WIDTH=200 ALIGN=LEFT VALIGN=TOP><B> Original Receipt Number </B></TD>");
					toReturn.append("</TR>");
					toReturn.append("<TR>");
					/*//Gauri changed AO name for CR 89435, FVU 1.9-PENDING
					if(Integer.parseInt(pReceipt.getFinancialYear().substring(2, 4)) < 25) {
						toReturn.append("<TD ALIGN=LEFT WIDTH=300 VALIGN=TOP>\" + pReceipt.getNameOfAO() + \"</TD>");
					}
					else {
						if(pReceipt.getaoFirstName() == null && pReceipt.getaoMiddleName() == null) {
							toReturn.append("<TD ALIGN=LEFT WIDTH=300 VALIGN=TOP>\" + pReceipt.getaoLastName() + \"</TD>");
						}
						else {
							toReturn.append("<TD ALIGN=LEFT WIDTH=300 VALIGN=TOP>" + (pReceipt.getaoFirstName() != null? pReceipt.getaoFirstName() + " " : " ")+
									(pReceipt.getaoMiddleName() != null? pReceipt.getaoMiddleName() + " " : " ")+
									(pReceipt.getaoLastName() != null? pReceipt.getaoLastName() : " ") + "</TD>");
						}
					}//END
*/					
					toReturn.append("<TD ALIGN=LEFT WIDTH=300 VALIGN=TOP>" + pReceipt.getNameOfAO() + "</TD>");
					toReturn.append("<TD ALIGN=LEFT WIDTH=300 VALIGN=TOP>");
					toReturn.append(pReceipt.getAoAdd1());
					toReturn.append(", ");
					if (!pReceipt.getAoAdd2().equals(""))
					{
						toReturn.append(pReceipt.getAoAdd2());
						toReturn.append(", ");
					}
					if (!pReceipt.getAoAdd3().equals(""))
					{
						toReturn.append(pReceipt.getAoAdd3());
						toReturn.append(", ");
					}
					if (!pReceipt.getAoAdd4().equals(""))
					{
						toReturn.append(pReceipt.getAoAdd4());
						toReturn.append(", ");
					}
					toReturn.append(pReceipt.getAoCity());
					toReturn.append(", ");
					toReturn.append(TBAF_STATE_NAME[Integer.parseInt(pReceipt.getAoState().trim())]);
					toReturn.append(", ");
					toReturn.append(pReceipt.getAoPIN());
					toReturn.append("<TD ALIGN=LEFT WIDTH=200 VALIGN=TOP>" + pReceipt.getOiginalRecptNo() + "</TD>");
					toReturn.append("</TR>");
					toReturn.append("</TABLE>");
				}
				toReturn.append("<BR>");
				// TABLE NO.4 STARTS
				toReturn.append(
						"<TABLE BORDER=1 BORDERCOLOR=black CELLSPACING=0 CELLPADDING=0 "
						+ "style='border-collapse: collapse; border: none; mso-border-alt: solid windowtext .5pt; mso-padding-alt: 0in 5.4pt 0in 5.4pt'>");
				toReturn.append("<TR>");
				toReturn.append("<TD WIDTH=100 ALIGN=LEFT VALIGN=TOP><B> Financial Year</B></TD>");
				toReturn.append("<TD WIDTH=100 ALIGN=LEFT VALIGN=TOP><B> Quarter</B></TD>");
				toReturn.append("<TD WIDTH=100 ALIGN=LEFT VALIGN=TOP><B> Type of Statement</B></TD>");
				toReturn.append("<TD WIDTH=100 ALIGN=LEFT VALIGN=TOP><B> Type of Correction</B></TD>");
				toReturn.append("<TD WIDTH=200 ALIGN=LEFT VALIGN=TOP><B> Nature of Deduction</B></TD>");
				toReturn.append("<TD WIDTH=200 ALIGN=LEFT VALIGN=TOP><B> Category of Deductor</B></TD>");
				toReturn.append("</TR>");
				toReturn.append("<TR>");
				int financialYearForReceipt = Integer.parseInt(pReceipt.getFinancialYear().substring(2, 4));
				if (financialYearForReceipt >= 9 && financialYearForReceipt < 99)
				{
					financialYearForReceipt = financialYearForReceipt + 1;
					String finYear = String.valueOf(financialYearForReceipt);
					toReturn.append("<TD ALIGN=LEFT WIDTH=100>" + pReceipt.getFinancialYear() + "-" + finYear + "</TD>");
				}
				else if (financialYearForReceipt == 99)
				{
					toReturn.append("<TD ALIGN=LEFT WIDTH=100>" + pReceipt.getFinancialYear() + "-" + "00" + "</TD>");
				}
				else
				{
					financialYearForReceipt = financialYearForReceipt + 1;
					String finYear = String.valueOf(financialYearForReceipt);
					toReturn.append("<TD ALIGN=LEFT WIDTH=100>" + pReceipt.getFinancialYear() + "-" + "0" + finYear + "</TD>");
				}
				toReturn.append("<TD ALIGN=LEFT WIDTH=100 VALIGN=TOP>" + pReceipt.getQuarter() + "</TD>");
				toReturn.append("<TD ALIGN=LEFT WIDTH=100 VALIGN=TOP> Correction </TD>");
				if (pReceipt.getTransactionType().equals(TBAF_TRANSACTION_TYPE_C1))
				{
					toReturn.append("<TD ALIGN=LEFT WIDTH=100 VALIGN=TOP> C1 - Correction </TD>");
				}
				else if (pReceipt.getTransactionType().equals(TBAF_TRANSACTION_TYPE_C2))
				{
					toReturn.append("<TD ALIGN=LEFT WIDTH=100 VALIGN=TOP> C2 - Correction </TD>");
				}
				else if (pReceipt.getTransactionType().equals(TBAF_TRANSACTION_TYPE_C3))
				{
					toReturn.append("<TD ALIGN=LEFT WIDTH=100 VALIGN=TOP> C3 - Correction </TD>");
				}
				else
				{
					toReturn.append("<TD ALIGN=LEFT WIDTH=100 VALIGN=TOP> C4 - Correction </TD>");
				}
				//newly added
				if(Integer.parseInt(pReceipt.getFinancialYear().substring(2, 4)) < 26) {
					Log.tbaf_log.debug("Gauri get Nature of DED:- " + pReceipt.getNatureOfDed());
					if (pReceipt.getNatureOfDed().equals(TBAF_FORM_24Q))
					{
						toReturn.append("<TD ALIGN=LEFT WIDTH=200>" + "TDS-SAL " + "(" + pReceipt.getNatureOfDed() + ")" + "</TD>");
					}
					else if (pReceipt.getNatureOfDed().equals(TBAF_FORM_26Q))
					{
						toReturn.append("<TD ALIGN=LEFT WIDTH=200>" + "TDS-NON-SAL " + "(" + pReceipt.getNatureOfDed() + ")" + "</TD>");
					}
					else if (pReceipt.getNatureOfDed().equals(TBAF_FORM_27Q))
					{
						toReturn.append("<TD ALIGN=LEFT WIDTH=200>" + "TDS-NR " + "(" + pReceipt.getNatureOfDed() + ")" + "</TD>");
					}
					else if (pReceipt.getNatureOfDed().equals(TBAF_FORM_27EQ))
					{
						toReturn.append("<TD ALIGN=LEFT WIDTH=200>" + "TCS " + "(" + pReceipt.getNatureOfDed() + ")" + "</TD>");
					}
				}
				else {
					Log.tbaf_log.debug("Gauri get Nature of DED:- " + pReceipt.getNatureOfDed());
					if (pReceipt.getNatureOfDed().equals(TBAF_NEW_FORM_24Q))
					{
						toReturn.append("<TD ALIGN=LEFT WIDTH=200>" + "TDS-SAL " + "(" + pReceipt.getNatureOfDed() + ")" + "</TD>");
					}
					else if (pReceipt.getNatureOfDed().equals(TBAF_NEW_FORM_26Q))
					{
						toReturn.append("<TD ALIGN=LEFT WIDTH=200>" + "TDS-NON-SAL " + "(" + pReceipt.getNatureOfDed() + ")" + "</TD>");
					}
					else if (pReceipt.getNatureOfDed().equals(TBAF_NEW_FORM_27Q))
					{
						toReturn.append("<TD ALIGN=LEFT WIDTH=200>" + "TDS-NR " + "(" + pReceipt.getNatureOfDed() + ")" + "</TD>");
					}
					else if (pReceipt.getNatureOfDed().equals(TBAF_NEW_FORM_27EQ))
					{
						toReturn.append("<TD ALIGN=LEFT WIDTH=200>" + "TCS " + "(" + pReceipt.getNatureOfDed() + ")" + "</TD>");
					}
				}
				toReturn.append("<TD ALIGN=LEFT WIDTH=200 VALIGN=TOP>" + Parameters.GovtMap.get(pReceipt.getDeductorCat().trim()) + "</TD>");
				toReturn.append("</TR>");
				toReturn.append("</TABLE>");
				// TABLE NO.4 ENDS*/
			}
			toReturn.append("<BR>");
			// TABLE NO.5 STARTS
			toReturn.append(
					"<TABLE BORDER=1 BORDERCOLOR=black CELLSPACING=0 CELLPADDING=0 "
					+ "style='border-collapse: collapse; border: none; mso-border-alt: solid windowtext .5pt; mso-padding-alt: 0in 5.4pt 0in 5.4pt'>");
			if (!pReceipt.getTransactionType().equals(TBAF_TRANSACTION_TYPE_C1))
			{
				toReturn.append("<TR>");
				toReturn.append("<TD WIDTH=200 ALIGN=RIGHT VALIGN=TOP><B> Count of Transactions</B></TD>");
				toReturn.append("<TD WIDTH=250 ALIGN=RIGHT VALIGN=TOP><B> Total value of all Transactions (Rs.)</B></TD>");
				toReturn.append("<TD WIDTH=201 ALIGN=RIGHT VALIGN=TOP><B> Count of Distinct DDOs (TANs)</B></TD>");
				toReturn.append("<TD WIDTH=150 ALIGN=RIGHT VALIGN=TOP><B> Upload Fee (Rs.)</B></TD>");
				toReturn.append("</TR>");
				toReturn.append("<TR>");
				toReturn.append("<TD ALIGN=RIGHT WIDTH=200 VALIGN=TOP>" + pReceipt.getCountOfTD() + "</TD>");
				toReturn.append("<TD ALIGN=RIGHT WIDTH=250 VALIGN=TOP>" + pReceipt.getTotalTax() + "</TD>");
				toReturn.append("<TD ALIGN=RIGHT WIDTH=200 VALIGN=TOP>" + pReceipt.getCountOfDistinctTD() + "</TD>");
				toReturn.append("<TD ALIGN=RIGHT WIDTH=150 VALIGN=TOP>" + pReceipt.getUploadFee() + "</TD>");
				toReturn.append("</TR>");
			}
			toReturn.append("</TABLE>");
			// TABLE NO.5 ENDS
			toReturn.append("<BR>");
			toReturn.append("No. of records to be charged : " + pReceipt.getNoOfLines());
			toReturn.append("<BR>");
			toReturn.append("Service Tax Registration No.:<B> M-IV/ST/BFN/104/03 </B><BR>");
			toReturn.append("<B>National Securities Depository Limited (e-TBAF intermediary)</B><BR>");
			toReturn.append("<B><I>This is a computer generated Provisional Receipt, hence signature not required.</I></B><BR>");
			toReturn.append("<BR>");
			toReturn.append("</TD>");
			toReturn.append("</TR>");
			toReturn.append("</TABLE>");
			// TABLE NO.1 ENDS
			toReturn.append("<BR><BR><BR><BR>");
			toReturn.append("</BODY>");
			toReturn.append("</HTML>");
		}
		catch (Exception e)
		{
			e.printStackTrace();
			Log.tbaf_log.error("EXCEPTON in FileGenerator.java createProvisionalReceipt() : " + e.getMessage());
		}
		return toReturn;
	}

	/**
	 *	Method to get Date in date Month Year format Eg. 4 April 2004
	 */
	private String setDateFormat(String dateString)
	{
		String year = dateString.substring(0, 4);
		String month = dateString.substring(6, 7);
		String date = dateString.substring(8, 10);
		switch (Integer.parseInt(month.trim()))
		{
		case 1 :
			month = "January";
			break;
		case 2 :
			month = "February";
			break;
		case 3 :
			month = "March";
			break;
		case 4 :
			month = "April";
			break;
		case 5 :
			month = "May";
			break;
		case 6 :
			month = "June";
			break;
		case 7 :
			month = "July";
			break;
		case 8 :
			month = "August";
			break;
		case 9 :
			month = "September";
			break;
		case 10 :
			month = "October";
			break;
		case 11 :
			month = "November";
			break;
		case 12 :
			month = "December";
			break;
		}
		dateString = date + " " + month + " " + year;
		return dateString;
	}

	private String displayMonthYear(String monthAndYear)
	{
		String month = monthAndYear.substring(0,2);
		String year = monthAndYear.substring(2,6);
		switch (Integer.parseInt(month.trim()))
		{
		case 1 :
			month = "Jan";
			break;
		case 2 :
			month = "Feb";
			break;
		case 3 :
			month = "Mar";
			break;
		case 4 :
			month = "Apr";
			break;
		case 5 :
			month = "May";
			break;
		case 6 :
			month = "Jun";
			break;
		case 7 :
			month = "Jul";
			break;
		case 8 :
			month = "Aug";
			break;
		case 9 :
			month = "Sep";
			break;
		case 10 :
			month = "Oct";
			break;
		case 11 :
			month = "Nov";
			break;
		case 12 :
			month = "Dec";
			break;
		}
		monthAndYear = month+" "+year;
		return monthAndYear;
	}
}	// END OF CLASS TBAFFileGenerator.java
