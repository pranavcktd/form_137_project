/**
 * Class: RecordValidation.java
 */ 
package com.tin.etbaf.form24G.fvu;
import java.text.SimpleDateFormat;
import java.util.*;
/**
 *	This class contains all the methods which are called in other programs 
 *	to perform common validations on different fields of the records. 
 * 
 *	@author TCS
 *	@version 8 
 */
public class RecordValidation implements TBAFInterface
{
	//	VALIDATE STRING AS INTEGER   
	/**
	 *	This method performs validations on a field to check if the
	 *	field is Integer or not.
	 *	
	 *	@param field-> String value passed by calling this method on the data to be validated.		  
	 *	@return boolean
	 */	        
	boolean isInt(String field)
	{
		if (field.trim() == "" || field.trim().length() == 0)
		{
			return true;
		}
		for (int i = 0; i < field.length(); i++)
		{
			if (field.charAt(i) < 32 || field.charAt(i) > 127)
			{
				return true;
			}
		}
		try
		{
			Long.parseLong(field.trim());
		}
		catch (Exception e)
		{
			return true;
		}
		if (Long.parseLong(field.trim()) < 0)
		{
			return true;
		}
		return (false);
	}
	
	boolean isValidValue(String data)
	{
		boolean value=false;
		if (data.trim() == "" || data.trim().length() == 0)
		{
			value=true;
			return value;
		}
		try
		{
			if(Double.parseDouble(data)<0 || Double.parseDouble(data)>0)
			{
				return value;
			}
		}
		catch(Exception e)
		{
			value=true;
			return value;
		}
		return value;
	}
	
	
	/**
	 *	This method performs validations on a field to check if the
	 *	field is Integer or not.
	 *	
	 *	@param field-> String value passed by calling this method on the data to be validated.		  
	 *	@return boolean
	 */	        
	boolean isIntForRemAmt(String field)
	{
		if (field.trim() == "" || field.trim().length() == 0)
		{
			return true;
		}
		for (int i = 0; i < field.length(); i++)
		{
			if (field.charAt(i) < 32 || field.charAt(i) > 127)
			{
				return true;
			}
		}
		try
		{
			Long.parseLong(field.trim());
		}
		catch (Exception e)
		{
			return true;
		}
		/*if (Long.parseLong(field.trim()) < 0)
		{
			return true;
		} */
		return (false);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	//	VALIDATE YEAR AS A LEAP YEAR 
	private boolean isLeap(int year)
	{
		return ((year % 4 == 0) && (year % 100 != 0 || year % 400 == 0));
	} 
	protected boolean checkValidation(int iYear, int iMonth, int iDay)
	{
		if ((iYear < 1800) || (iYear > 2099))
			return false;
		if ((iDay < 1) || (iDay > 31) || (iMonth < 1) || (iMonth > 12))
			return false;
		if (!isLeap(iYear) && (iMonth == 2) && (iDay > 28))
			return false;
		if (isLeap(iYear) && (iMonth == 2) && (iDay > 29))
			return false;
		if (((iMonth == 4) || (iMonth == 6) || (iMonth == 9) || (iMonth == 11)) && (iDay > 30))
			return false;
		return true;
	}
	
	//	VALIDATE STRING AS DECIMAL
	boolean isDecimal(String data, int Precision)
	{
		String field = data.trim();
		boolean error = false;
		if (field.trim() == "" || field.trim().length() == 0)
		{
			return true;
		}
		for (int i = 0; i < data.length(); i++)
		{
			if ((data.charAt(i) < 32 || data.charAt(i) > 127))
			{
				return true;
			}
		}
		try
		{
			Double.parseDouble(field);
		}
		catch (Exception e)
		{
			error = true;
			return error;
		}
		int pointCounter = 0;
		int decimalPosition = 0;
		int length = field.length();
		for (int j = 0; j < length; j++)
		{
			if (!(Character.isDigit(field.charAt(j))))
			{
				if (field.charAt(j) == '.')
				{
					pointCounter++;
					decimalPosition = j;
				}
				else
					error = true;
			}
		}
		if (field.charAt(0) == '.')
		{
			return true;
		}
		if (pointCounter == 0)
			error = true;
		else
		{
			if (Precision == (length - 1) - decimalPosition)
				error = false;
			else
				error = true;
		}
		if (pointCounter > 1)
			error = true;
		return error;
	}
	
	// VALIDATE STRING AS NULL 	
	/** 
	 *	This method checks if a field is having any value or not. 
	 *
	 *	@param text-> String value passed by calling this method on the data to be validated.
	 *	@return boolean  	 
	 */
	boolean isFieldNull(String text)
	{
		if(text.length() > text.trim().length())
		{
			return false;
		}
		if (text.trim() == "" || text.trim().length() == 0)
		{
			return true;
		}
		if (text.length() == 0)
		{
			return (true);
		}
		else
		{
			return (false);
		}
	}
	
	// VALIDATE DATE 	   
	boolean isDate(String date1) //date in ddmmyy form
	{
		if (date1.trim() == "" || date1.trim().length() == 0 || date1.trim().length() != 8 || date1.length() != 8)
		{
			return true;
		}
		boolean errorDate;
		String dd = date1.substring(0, 2);
		String mm = date1.substring(2, 4);
		String yy = date1.substring(4, 8);
		try
		{
			if (checkValidation(Integer.parseInt(yy), Integer.parseInt(mm), Integer.parseInt(dd)))
			{
				errorDate = false;
			}
			else
			{
				errorDate = true;
			}
		}
		catch (Exception e)
		{
			errorDate = true;
		}
		return errorDate;
	} 
	
	//	COMPARE TWO VALID DATES         
	public int compareDate(String date1, String date2)
	{
		if (date1.trim().length() != 8 || date1.length() != 8 || date2.trim().length() != 8 || date2.length() != 8)
		{
			return 5;
		}
		if (isNumber(date1) && isNumber(date2))
		{
			Calendar cal1 = new GregorianCalendar(Integer.parseInt(date1.substring(4, 8)), Integer.parseInt(date1.substring(2, 4)) - 1, Integer.parseInt(date1.substring(0, 2)));
			Calendar cal2 = new GregorianCalendar(Integer.parseInt(date2.substring(4, 8)), Integer.parseInt(date2.substring(2, 4)) - 1, Integer.parseInt(date2.substring(0, 2)));
			if (cal1.after(cal2))
				return 1;
			else
				return 2;
		}
		else
			return 4;
	}
	
	//	VALIDATE STRING AS ALPHANUMERIC         	   
	boolean isAlphaNum(String data)
	{
		String field = data.trim();
		if (field.trim() == "" || field.trim().length() == 0)
		{
			return true;
		}
		for (int j = 0; j < field.length(); j++)
		{
			if (!Character.isLetterOrDigit(field.charAt(j)))
			{
				return true;
			}
		}
		return false;
	}
	
	
	//	VALIDATE STRING AS VALID CHARACTER STRING 		
	boolean isValidCharString(String dataString)
	{
		if (dataString.trim() == "" || dataString.trim().length() == 0)
		{
			return true;
		}
		for (int i = 0; i < dataString.length(); i++)
		{
			if ((dataString.charAt(i) < 32 || dataString.charAt(i) > 127))
			{
				return true;
			}
		}
		return false;
	}
	
	
	
	
	//To Check the the Validity of AO NAME
	
	 boolean checkValidAOName(String str)
	{
		
		if (str.trim() == "" || str.trim().length() == 0)
		{
			return false;
		}
		
		
		for (int i=0;i<str.length();i++)
		{
			if( ((str.charAt(i)> 64 && str.charAt(i)< 91) || (str.charAt(i)> 96 && str.charAt(i)< 123)) )
			{
				return true;
			}
		}
		return false;
	}
	
	
	
	//eND OF VALIDATION
	 
		//Gauri added o Check the the alphabets only for CR 89435, FVU 1.9::START
		
	 boolean checkAlphabets(String str)
	{
		if(str == null || str.isEmpty()) {
			return false;
		}
		if(str.startsWith(" ") || str.endsWith(" ")) {
			return false;
		}
		
		if(!str.matches("[A-Za-z ]+")) {
			return false;
		}
		return true;
	}
	
	
	//eND OF VALIDATION
	 
	 //Gauri added to check state ag code::start
	 
	 boolean isAlphanumeric(String value) {
		 if(value == null || value.isEmpty()) {
			 return false;
		 }
		 return value.matches("^[a-zA-Z0-9]+$");
				 
	 }
	 
	//Gauri added to check state ag code::end
	
	
	
	 //To Check the the Validity of AO Addresses
	 
	 boolean checkValidAOAddress(String str)
		{
			
			if (str.trim() == "" || str.trim().length() == 0)
			{
				return false;
			}
			
			
			for (int i=0;i<str.length();i++)
			{
				if(  (str.charAt(i)> 47 && str.charAt(i)< 58)  || ((str.charAt(i)> 64 && str.charAt(i)< 91) || (str.charAt(i)> 96 && str.charAt(i)< 123)) )
				{
					return true;
				}
			}
			return false;
		}
	 
	 
	 //End of Validation
	 
	 
	 
	
	///////////////////////////////////////////////////////////////////////////////////	
	//	  boolean isAlphaSpace(String field)
	//	  {
	//		 if (field.trim() == "" || field.trim().length() == 0)
	//		 {
	//			return true;
	//		 }
	//		 for (int i = 0; i < field.length(); i++)
	//		 {
	//			if (!(Character.isLetter(field.charAt(i)) || field.charAt(i) == ' '))
	//			{
	//			   return true;
	//			}
	//		 }
	//		 return false;
	//	  }
	///////////////////////////////////////////////////////////////////////////////////
	//	  boolean isAlphaNumSpace(String data)
	//	  {
	//		 if (data.trim() == "" || data.trim().length() == 0)
	//		 {
	//			return true;
	//		 }
	//		 String field = data.trim();
	//		 for (int j = 0; j < field.length(); j++)
	//		 {
	//			if (!(Character.isLetterOrDigit(field.charAt(j)) || field.charAt(j) == ' '))
	//			{
	//			   return true;
	//			}
	//		 }
	//		 return false;
	//	  } //end of AlNum
	///////////////////////////////////////////////////////////////////////////////////////
	
	// VALIDATE RRR NUMBER
	boolean isValidRrrNumber(String RRR_No)
	{
		try
		{
			Long.parseLong(RRR_No.trim());
		}
		catch (Exception e)
		{
			return true;
		}
		if (Long.parseLong(RRR_No.trim()) == 0)
		{
			return true;
		}
		long longRRR_No;
		long longRRR_No1;
		int intRRR_No1, intRRR_No2;
		longRRR_No = Long.parseLong(RRR_No);
		longRRR_No1 = longRRR_No / 10;
		intRRR_No2 = (int) (longRRR_No % 10);
		intRRR_No1 = (int) (longRRR_No1 % 7);
		if (intRRR_No1 == intRRR_No2)
		{
			return false;
		}
		else
		{
			return true;
		}
	}
	//	VALIDATE STRING AS NULL 	
	boolean nullFieldCheck(String text)
	{
		if (text.trim() == "" || text.trim().length() == 0)
		{
			return true;
		}
		if (text.length() == 0)
		{
			return (true);
		} 
		else
		{
			return (false);
		} 
	} 			
	//	VALIDATE NEGATIVE DECIMAL FIGURE
	boolean isNegativeDecimalNumber(String data)
	{
		String field = data.trim();
		if (field.trim() == "" || field.trim().length() < 4)
		{
			return true;
		}
		for (int i = 0; i < data.length(); i++)
		{
			if ((data.charAt(i) < 32 || data.charAt(i) > 127))
			{
				return true;
			}
		}
		try
		{
			Double.parseDouble(field);
		}
		catch (Exception e)
		{
			return true;
		}
		if (field.charAt(field.length() - 3) != '.')
		{
			return true;
		}
		for (int localI = 0; localI < field.length(); localI++)
		{
			if (!(field.charAt(localI) > 47 && field.charAt(localI) < 58) && !(field.charAt(localI) == 46) && !(field.charAt(0) == 45))
			{
				return true;
			}
			if ((field.charAt(localI) == 46) && !((field.length() - localI) == 3))
			{
				return true;
			}
			if ((field.charAt(localI) == 46) && (localI == 0))
			{
				return true;
			}
			if (Double.parseDouble(field) > 0)
			{
				return true;
			}
		}
		return false;
	}
	//	VALIDATE DECIMAL NUMBER
	/*
	 *  To check if the Number is a VALID POSITIVE DECIMAL with 2 Decimal digits.
	 */
	boolean isDecimalNumber(String data)
	{
		String Field = data.trim();
		if (Field.trim() == "" || Field.trim().length() < 4 || Field.length() < 4)
		{
			return true;
		}
		for (int i = 0; i < data.length(); i++)
		{
			if ((data.charAt(i) < 32 || data.charAt(i) > 127))
			{
				return true;
			}
		}
		try
		{
			Double.parseDouble(Field);
		}
		catch (Exception e)
		{
			return true;
		}
		if (Field.charAt(Field.length() - 3) != '.')
		{
			return true;
		}
		for (int localI = 0; localI < Field.length(); localI++)
		{
			if(localI==0 && Field.charAt(localI)==45)
			{
					continue;
			}
			if (!(Field.charAt(localI) > 47 && Field.charAt(localI) < 58) && !(Field.charAt(localI) == 46))
			{
				return true;
			}
			if ((Field.charAt(localI) == 46) && (localI == 0))
			{
				return true;
			}
			if ((Field.charAt(localI) == 46) && !((Field.length() - localI) == 3))
			{
				return true;
			}
		}
		return false;
	}
	
	
	
	
	
//	VALIDATE DECIMAL NUMBER
	/*
	 *  To check if the Number is a VALID  DECIMAL with 2 Decimal digits MAY BE POSITIVE OR NEGATIVE.
	 */
	boolean isDecimalNumberForRemAmt(String data)
	{
		String Field = data.trim();
		if (Field.trim() == "" || Field.trim().length() < 4 || Field.length() < 4)
		{
			return true;
		}
		for (int i = 0; i < data.length(); i++)
		{
			if ((data.charAt(i) < 32 || data.charAt(i) > 127))
			{
				return true;
			}
		}
		try
		{
			Double.parseDouble(Field);
		}
		catch (Exception e)
		{
			return true;
		}
		if (Field.charAt(Field.length() - 3) != '.')
		{
			return true;
		}
		for (int localI = 0; localI < Field.length(); localI++)
		{
			if (!(Field.charAt(localI) > 47 && Field.charAt(localI) < 58) && !(Field.charAt(localI) == 46)  && !(Field.charAt(localI) == 45))
			{
				return true;
			}
			if ((Field.charAt(localI) == 46) && (localI == 0))
			{
				return true;
			}
			if ((Field.charAt(localI) == 46) && !((Field.length() - localI) == 3))
			{
				return true;
			}
		}
		return false;
	}
	
	
	
	
	
	
	
	
	
	
	//		//////////////////////////    Annual  Integer  //////////////////////////////////	
	//	public int checkNumeric(String data, int length)
	//	{
	//		if (data == null)
	//		{
	//			return 3;
	//		}
	//		String testNumber = data.trim();
	//		if (testNumber.trim() == "" || testNumber.trim().length() == 0)
	//		{
	//			return 3;
	//		}
	//		if (testNumber == null)
	//		{
	//			return 2;
	//		}
	//		if (testNumber.length() > length)
	//		{
	//			return 2;
	//		}
	//		for (int i = 0; i < testNumber.length(); i++)
	//		{
	//			if (!(testNumber.charAt(i) > 47 && testNumber.charAt(i) < 58))
	//			{
	//				return 3;
	//			}
	//		}
	//		return 4;
	//	}
	
	//	VALIDATE FLOAT FIGURE
	boolean isFloatNumber(String data)
	{
		String Field = data.trim();
		if (data.trim() == "" || data.trim().length() == 0 || data.trim().length() < 4 || data.length() < 4)
		{
			return true;
		}
		for (int i = 0; i < data.length(); i++)
		{
			if ((data.charAt(i) < 32 || data.charAt(i) > 127))
			{
				return true;
			}
		}
		try
		{
			Double.parseDouble(Field);
		}
		catch (Exception e)
		{
			return true;
		}
		if (Field.charAt(Field.length() - 3) != '.')
		{
			return true;
		}
		for (int localI = 0; localI < Field.length(); localI++)
		{
			if (!(Field.charAt(localI) > 47 && Field.charAt(localI) < 58) && !(Field.charAt(localI) == 46) && !(Field.charAt(0) == 45))
			{
				return true;
			}
			if ((Field.charAt(localI) == 46) && !((Field.length() - localI) == 3))
			{
				return true;
			}
			if ((Field.charAt(localI) == 46) && (localI == 0))
			{
				return true;
			}
		}
		return false;
	}
	//	VALIDATING E-MAIL
	 boolean isValidEmail(String data)
	{
		String field = data.trim();
		if (isValidCharString(data))
		{
			return true;
		}
		if (field.trim() == "" || field.trim().length() == 0)
		{
			return true;
		}
		for (int j = 0; j < field.length(); j++)
		{
			if (field.charAt(j) == ' ')
			{
				return true;
			}
		}
		for (int j = 0; j < field.length(); j++) //added By Subhankar
		{
			if (field.charAt(j) == '^')
			{
				return true;
			}
		}
		if(field.indexOf('@') == (field.indexOf('.') -1) || field.indexOf('.') == (field.indexOf('@') -1)) //added By Subhankar
		{
			return true;
		}
		if (field.charAt(0) == '@' || field.charAt(0) == '.')
		{
			return true;
		}
		if (field.charAt(field.length() - 1) == '@' || field.charAt(field.length() - 1) == '.')
		{
			return true;
		}
		int localCount = 0;
		int dotPos = 0;
		int ratePos = 0;
		for (int j = 0; j < field.length(); j++)
		{
			if (field.charAt(j) == '@')
			{
				ratePos = j;
				if (field.charAt(j + 1) == '.')
				{
					return true;
				}
				localCount++;
				if (localCount > 1)
				{
					return true;
				}
			}
		}
		if (localCount == 0)
		{
			return true;
		}
		for (int x = 0; x < field.length(); x++)
		{
			if (field.charAt(x) == '.')
			{
				dotPos = x;
				if ((x + 1) != field.length())
				{
					if (field.charAt(x + 1) == '.')
					{
						return true;
					}
				}
			}
		}
		if (dotPos < ratePos)
		{
			return true;
		}
		return false;
	}
	
	
	//	Annual CHECK NUMERIC
	boolean isNumber(String data)
	{
		String field = data.trim();
		if (field.trim() == "" || field.trim().length() == 0)
		{
			return false;
		}
		for (int i = 0; i < data.length(); i++)
		{
			if ((data.charAt(i) < 32 || data.charAt(i) > 127))
			{
				return true;
			}
		}
		try
		{
			Integer.parseInt(field);
		}
		catch (Exception e)
		{
			return false;
		}
		if (Integer.parseInt(field) < 0)
		{
			return false;
		}
		return true;
	}
	//////////////      Validate PAN   //////////////////////
	//	  boolean checkPan(String pan)
	//	  {
	//
	//		 if (pan.trim().length() != 10 || pan.length() != 10)
	//		 {
	//			return true;
	//		 }
	//		 for (int i = 0; i < 3; i++)
	//		 {
	//			if (!(pan.charAt(i) > 64 && pan.charAt(i) < 91))
	//			{
	//			   return true;
	//			}
	//		 }
	//
	//		 for (int i = 5; i < 9; i++)
	//		 {
	//			if (!(pan.charAt(i) > 47 && pan.charAt(i) < 58))
	//			{
	//			   return true;
	//			}
	//		 }
	//
	//		 if (pan.charAt(3) != 'P'
	//			&& pan.charAt(3) != 'H'
	//			&& pan.charAt(3) != 'C'
	//			&& pan.charAt(3) != 'J'
	//			&& pan.charAt(3) != 'F'
	//			&& pan.charAt(3) != 'A'
	//			&& pan.charAt(3) != 'T'
	//			&& pan.charAt(3) != 'B'
	//			&& pan.charAt(3) != 'L'
	//			&& pan.charAt(3) != 'G')
	//		 {
	//			return true;
	//		 }
	//
	//		 if (!(pan.charAt(4) > 64 && pan.charAt(4) < 91))
	//		 {
	//			return true;
	//		 }
	//
	//		 if (!(pan.charAt(9) > 64 && pan.charAt(9) < 91))
	//		 {
	//			return true;
	//		 }
	//
	//		 return false;
	//	  }
	
	//	VALIDATE TAN
	/** 
	 * 	A valid TAN should have  the first 4 characters as alphabets, 
	 *	the next 5 characters as numbers and the last character is the check digit alphabet of the
	 *	previous 5 numbers. The check-digit is mod 7 of the 5 digits present.
	 *
	 * 	@parama tan -> TAN value from TD is passed as the parameter to this method.
	 * 	@return boolean 
	 */
	boolean checkTan(String tan)
	{
		int checkBit;
		if (tan.trim().length() != 10)
		{
			return true;
		}
		String subtan = tan.substring(0, 3);
		String midSubtan = tan.substring(4, 9);
		String endSubtan = tan.substring(9, 10);
		int rccFlag = 0;
		for (int localR = 0; localR < TBAF_TAN_RCC.length; localR++)
		{
			if (subtan.equals(TBAF_TAN_RCC[localR]))
			{
				rccFlag = 1;
				break;
			}
		}
		if (rccFlag == 0)
		{
			return true;
		}
		for (int i = 0; i < 3; i++)
		{
			if (!(subtan.charAt(i) > 64 && subtan.charAt(i) < 91))
			{
				return true;
			}
		}
		if (!Character.isLetterOrDigit(tan.charAt(3)))
		{
			return true;
		}
		if (Character.isLetter(tan.charAt(3)))
		{
			if (!(tan.charAt(3) > 64 && tan.charAt(3) < 91))
			{
				return true;
			}
		}
		for (int i = 0; i < 5; i++)
		{
			if (!(midSubtan.charAt(i) > 47 && midSubtan.charAt(i) < 58))
			{
				return true;
			}
		}
		checkBit = Integer.parseInt(midSubtan) % 7;
		//Changes added for TBAF FVU 1.6 version by puja
		
		if (checkBit == 0 && !(endSubtan.charAt(0) == 'A' || endSubtan.charAt(0) == 'H'))
		{
			return true;
		}
		else if (checkBit == 1 && !(endSubtan.charAt(0) == 'B'|| endSubtan.charAt(0) == 'I'))
		{
			return true;
		}
		else if (checkBit == 2 && !(endSubtan.charAt(0) == 'C'|| endSubtan.charAt(0) == 'J'))
		{
			return true;
		}
		else if (checkBit == 3 && !(endSubtan.charAt(0) == 'D'|| endSubtan.charAt(0) == 'K'))
		{
			return true;
		}
		else if (checkBit == 4 && !(endSubtan.charAt(0) == 'E'|| endSubtan.charAt(0) == 'L'))
		{
			return true;
		}
		else if (checkBit == 5 && !(endSubtan.charAt(0) == 'F'|| endSubtan.charAt(0) == 'M'))
		{
			return true;
		}
		else if (checkBit == 6 && !(endSubtan.charAt(0) == 'G'|| endSubtan.charAt(0) == 'N'))
		{
			return true;
		}
		return false;
	}
	//	VALIDATE FINANCIAL YEAR	
	boolean checkFinYear(String field)
	{
		if (field.trim() == "" || field.trim().length() == 0)
		{
			return true;
		}
		if (isNumber(field))
		{
			int financialyr = Integer.parseInt(field);
			int first_f = financialyr / 10000;
			int second_f = financialyr % 100;
			Calendar cal = new GregorianCalendar();
			int year = cal.get(Calendar.YEAR);
			if (year < ((first_f * 100) + second_f))
				return true;
		}
		else
			return true;
		return false;
	}
	/////////////  Check Name   ///////////
	//	  boolean testName(String data)
	//	  {
	//		 if (data.trim() == "" || data.trim().length() == 0)
	//		 {
	//			return true;
	//		 }
	//		 String nameField = data.trim();
	//		 int countalph = 0;
	//		 int counterSpace = 0;
	//		 int countApostrophe = 0;
	//
	//		 for (int i = 1; i < nameField.length(); i++)
	//		 {
	//			if (!(nameField.charAt(i) > 64 && nameField.charAt(i) < 91)
	//			   && !(nameField.charAt(i) > 96 && nameField.charAt(i) < 123)
	//			   && !(nameField.charAt(i) == ' ')
	//			   && !(nameField.charAt(i) == 39))
	//			{
	//			   return true;
	//			}
	//		 }
	//
	//		 for (int j = 1; j < nameField.length(); j++)
	//		 {
	//			if ((nameField.charAt(j) > 64 && nameField.charAt(j) < 91) || (nameField.charAt(j) > 96 && nameField.charAt(j) < 123))
	//			{
	//			   countalph = 1;
	//			   break;
	//			}
	//		 }
	//
	//		 for (int j = 1; j < nameField.length(); j++)
	//		 {
	//			if (nameField.charAt(j) == ' ')
	//			{
	//			   if (nameField.charAt(j + 1) == 32)
	//			   {
	//				  counterSpace = 1;
	//				  break;
	//			   }
	//			}
	//		 }
	//
	//		 for (int j = 1; j < nameField.length(); j++)
	//		 {
	//			if (nameField.charAt(j) == 39)
	//			{
	//			   if (nameField.charAt(j + 1) == 39 || nameField.charAt(j + 1) == ' ')
	//			   {
	//				  countApostrophe = 1;
	//			   }
	//			}
	//		 }
	//
	//		 if (countalph == 0 || counterSpace != 0 || countApostrophe != 0)
	//		 {
	//			return true;
	//		 }
	//
	//		 return false;
	//	  }
	
	//	VALIDATE DATE IS A FUTURE DATE OR NOT
	boolean isFutureDate(String date)
	{
		if (date.trim().length() != 8 || date.length() != 8)
		{
			return true;
		}
		Calendar cal1 = Calendar.getInstance();
		Calendar cal2 = new GregorianCalendar(Integer.parseInt(date.substring(4, 8)), Integer.parseInt(date.substring(2, 4)) - 1, Integer.parseInt(date.substring(0, 2)));
		if (cal1.before(cal2))
		{
			return true;
		}
		return false;
	}
	//////  Check if Date is Greater then RRRDatePresentDate /////
	//	  boolean isDateGreaterThenRRRDatePresentDate(String date, Calendar RRRDatePresentDate)
	//	  {
	//		 if (date.trim().length() != 8 || date.length() != 8)
	//		 {
	//			return true;
	//		 }
	//
	//		 Calendar cal2 = new GregorianCalendar(Integer.parseInt(date.substring(4, 8)), Integer.parseInt(date.substring(2, 4)) - 1, Integer.parseInt(date.substring(0, 2)));
	//
	//		 if (cal2.after(RRRDatePresentDate))
	//		 {
	//			return true;
	//		 }
	//		 return false;
	//	  }
	
	//	Check Date IF Before Financial Year
	boolean isDateAfterFinYear(String date1, String finyear)
	{
		if (isNumber(date1) && isNumber(finyear))
		{
			if (date1.trim().length() != 8 || date1.length() != 8 || finyear.trim().length() != 6 || finyear.length() != 6)
			{
				return true;
			}
			Calendar cal1 = new GregorianCalendar(Integer.parseInt(date1.substring(4, 8)), Integer.parseInt(date1.substring(2, 4)) - 1, Integer.parseInt(date1.substring(0, 2)));
			Calendar cal2 = new GregorianCalendar(Integer.parseInt(finyear) / 100, 3, 1);
			if (cal1.before(cal2))
			{
				return true;
			}
			return false;
		}
		else
			return true;
	}
	
	//	Check Date IF in Financial Year
	public boolean isDateInFinancialYr(String date, String FinancialYear)
	{
		if (isNumber(date) && isNumber(FinancialYear))
		{
			String secondYr = null;
			if (date.trim().length() != 8 || FinancialYear.trim().length() != 6)
			{
				return true;
			}
			Calendar cal1 = new GregorianCalendar(Integer.parseInt(date.substring(4, 8)), Integer.parseInt(date.substring(2, 4)) - 1, Integer.parseInt(date.substring(0, 2)));
			Calendar cal2 = new GregorianCalendar(Integer.parseInt(FinancialYear) / 100, 3, 1);
			if (FinancialYear.substring(4, 6).equals("00"))
			{
				secondYr = Integer.toString((Integer.parseInt(FinancialYear) / 100) + 1);
			}
			else
				secondYr = FinancialYear.substring(0, 2) + FinancialYear.substring(4, 6);
			Calendar cal3 = new GregorianCalendar(Integer.parseInt(secondYr), 3, 31);
			if (cal1.before(cal2) || cal1.after(cal3))
			{
				return true;
			}
			else
				return false;
		}
		else
			return true;
	}
	///////////  Employment date within quater of Financial Year /////////
	//	  public boolean employmentDateWithinFinQuat(String empDate, String finYear, String quater)
	//	  {
	//		 if (isNumber(empDate) && isNumber(finYear))
	//		 {
	//			String firstYr = null;
	//			String secondYr = null;
	//			if (empDate.trim().length() != 8 || finYear.trim().length() != 6)
	//			{
	//			   return true;
	//			}
	//
	//			Calendar cal1 = new GregorianCalendar(Integer.parseInt(empDate.substring(4, 8)), Integer.parseInt(empDate.substring(2, 4)) - 1, Integer.parseInt(empDate.substring(0, 2)));
	//			if (quater.equals("Q1"))
	//			{
	//			   Calendar cal2 = new GregorianCalendar(Integer.parseInt(finYear) / 100, 3, 1);
	//			   Calendar cal3 = new GregorianCalendar(Integer.parseInt(finYear) / 100, 5, 30);
	//
	//			   if (cal1.before(cal2))
	//				  return true;
	//
	//			   if (cal1.after(cal3))
	//				  return true;
	//
	//			}
	//			else if (quater.equals("Q2"))
	//			{
	//			   Calendar cal2 = new GregorianCalendar(Integer.parseInt(finYear) / 100, 6, 1);
	//			   Calendar cal3 = new GregorianCalendar(Integer.parseInt(finYear) / 100, 8, 30);
	//
	//			   if (cal1.before(cal2))
	//				  return true;
	//
	//			   if (cal1.after(cal3))
	//				  return true;
	//
	//			}
	//			else if (quater.equals("Q3"))
	//			{
	//			   Calendar cal2 = new GregorianCalendar(Integer.parseInt(finYear) / 100, 9, 1);
	//			   Calendar cal3 = new GregorianCalendar(Integer.parseInt(finYear) / 100, 11, 31);
	//
	//			   if (cal1.before(cal2))
	//				  return true;
	//
	//			   if (cal1.after(cal3))
	//				  return true;
	//
	//			}
	//			else if (quater.equals("Q4"))
	//			{
	//			   firstYr = finYear.substring(0, 4);
	//			   if (finYear.substring(4, 6).equals("00"))
	//			   {
	//				  secondYr = Integer.toString((Integer.parseInt(finYear) / 100) + 1);
	//			   }
	//			   else
	//				  secondYr = finYear.substring(0, 2) + finYear.substring(4, 6);
	//
	//			   Calendar cal2 = new GregorianCalendar(Integer.parseInt(secondYr), 0, 1);
	//			   Calendar cal3 = new GregorianCalendar(Integer.parseInt(secondYr), 2, 31);
	//
	//			   if (cal1.before(cal2))
	//				  return true;
	//
	//			   if (cal1.after(cal3))
	//				  return true;
	//
	//			}
	//
	//			return false;
	//		 }
	//		 else
	//			return true;
	//
	//	  }
	//	/////////  Employment From-Date prior to quater/////////
	//	  public boolean employmentFromDateBeforeQuatEnd(String empDate, String finYear, String quater)
	//	  {
	//		 if (isNumber(empDate) && isNumber(finYear))
	//		 {
	//			String firstYr = null;
	//			String secondYr = null;
	//			if (empDate.trim().length() != 8 || finYear.trim().length() != 6)
	//			{
	//			   return true;
	//			}
	//
	//			Calendar cal1 = new GregorianCalendar(Integer.parseInt(empDate.substring(4, 8)), Integer.parseInt(empDate.substring(2, 4)) - 1, Integer.parseInt(empDate.substring(0, 2)));
	//
	//			if (quater.equals("Q1"))
	//			{
	//			   Calendar cal3 = new GregorianCalendar(Integer.parseInt(finYear) / 100, 5, 30);
	//
	//			   if (cal1.after(cal3))
	//				  return true;
	//
	//			}
	//			else if (quater.equals("Q2"))
	//			{
	//			   Calendar cal3 = new GregorianCalendar(Integer.parseInt(finYear) / 100, 8, 30);
	//
	//			   if (cal1.after(cal3))
	//				  return true;
	//
	//			}
	//			else if (quater.equals("Q3"))
	//			{
	//			   Calendar cal3 = new GregorianCalendar(Integer.parseInt(finYear) / 100, 11, 31);
	//
	//			   if (cal1.after(cal3))
	//				  return true;
	//
	//			}
	//			else if (quater.equals("Q4"))
	//			{
	//			   firstYr = finYear.substring(0, 4);
	//			   if (finYear.substring(4, 6).equals("00"))
	//			   {
	//				  secondYr = Integer.toString((Integer.parseInt(finYear) / 100) + 1);
	//			   }
	//			   else
	//				  secondYr = finYear.substring(0, 2) + finYear.substring(4, 6);
	//
	//			   Calendar cal3 = new GregorianCalendar(Integer.parseInt(secondYr), 2, 31);
	//
	//			   if (cal1.after(cal3))
	//				  return true;
	//
	//			}
	//
	//			return false;
	//		 }
	//		 else
	//			return true;
	//
	//	  }
	
	//	VALIDATE DECIMAL NUMBER IS POSITIVE OR NEGATIVE DECIMAL
	boolean isPositiveNegativeDecimalNumber(String data)
	{
		String field = data.trim();
		if (field.trim() == "" || field.trim().length() < 4)
		{
			return true;
		}
		try
		{
			Double.parseDouble(field);
		}
		catch (Exception e)
		{
			return true;
		}
		if (field.charAt(field.length() - 3) != '.')
		{
			return true;
		}
		for (int localI = 0; localI < field.length(); localI++)
		{
			if (!(field.charAt(localI) > 47 && field.charAt(localI) < 58) && !(field.charAt(localI) == 46) && !(field.charAt(0) == 45))
			{
				return true;
			}
			if ((field.charAt(localI) == 46) && !((field.length() - localI) == 3))
			{
				return true;
			}
			if ((field.charAt(localI) == 46) && (localI == 0))
			{
				return true;
			}
		}
		return false;
	}
	/**
	 * Method to convert number into 2 decimal point string,
	 * if we print double it will show it in exponential form, so we multiply it with 100 and convert 
	 * into long than from long convert it into string and add . before last two character
	 */
	public String changeNumberFormat(double number)
	{
		if (number == 0)
		{
			return "0.00";
		}
		else
		{
			// d4 = Math.round((double)(d4 * 100)) /100.0;
			//	  long l = (long) (number * 100); // converting Amount into Paisa and converting to long
			long l = Math.round((double) (number * 100));
			String amount = Long.toString(l); // converting amount to String
			int length = amount.length();
			if (length == 1)
			{
				return "0.0" + l; // appending . between Rs and Paisa part
			}
			else if (length == 2)
			{
				return "0." + l;
			}
			String rupees = amount.substring(0, (length - 2)); // separating Rs part and Paisa part
			String paisa = amount.substring((length - 2), length);
			return rupees + "." + paisa; // appending . between Rs and Paisa part
		}
	}
	
	//	VALIDATE MONTH AND YEAR OF PAYMENT
	public boolean checkMonthAndYearOfPayment(String quarter, String finYear, String monthYear)
	{
		if (quarter.equals(TBAF_QUARTER1))
		{
			if ((!monthYear.substring(0, 2).equals("04") && !monthYear.substring(0, 2).equals("05") 
				&& !monthYear.substring(0, 2).equals("06")) || !monthYear.substring(2, 6).equals(finYear))
				return true;
		}
		if (quarter.equals(TBAF_QUARTER2))
		{
			if ((!monthYear.substring(0, 2).equals("07") && !monthYear.substring(0, 2).equals("08") 
				&& !monthYear.substring(0, 2).equals("09")) || !monthYear.substring(2, 6).equals(finYear))
				return true;
		}
		if (quarter.equals(TBAF_QUARTER3))
		{
			if ((!monthYear.substring(0, 2).equals("10") && !monthYear.substring(0, 2).equals("11") 
				&& !monthYear.substring(0, 2).equals("12")) || !monthYear.substring(2, 6).equals(finYear))
				return true;
		}
		if (quarter.equals(TBAF_QUARTER4))
		{
			if ((!monthYear.substring(0, 2).equals("01") && !monthYear.substring(0, 2).equals("02") 
				&& !monthYear.substring(0, 2).equals("03")) 
				|| !monthYear.substring(2, 6).equals(Integer.toString(Integer.parseInt(finYear.trim()) + 1)))
				return true;
		}
		return false;
	}  
	
	//	VALIDATE CHECK DIGIT OF AIN
	/**
	 *	This method validates the AIN. The 7-digit AIN is passed to this method.
	 *	The first 6 digits of the AIN and the 7th digit is taken by using the substring method
	 *	and stored seperately in two String variables. Both these strings are converted to Integer
	 *	by using Integer.parseInt() method. Then, mod 7 of the first 6 dgits is taken and compared
	 *	with the lastdigit and the mod value.
	 *	
	 *	@param id-> String value of the AIN
	 *	@return boolean
	 */
	public boolean checkID(String id)
	{
		String firstId = id.substring(0, 6);
		String lastId = id.substring(6, 7);
		int ain = Integer.parseInt(firstId);
		int lastDigitOfAin = Integer.parseInt(lastId);
		int modOfAin = ain % 7;
		if (lastDigitOfAin != modOfAin)
		{
			return true;
		}
		return false;
	}
	
	//	VALIDATE CITY NAME
	public boolean isValidCityName(String cityName)
	{
		String cName = cityName.trim();
		if (cName.trim() == "" || cName.trim().length() > 25)
		{
			return true;
		}
		for (int localN = 0; localN < cName.length(); localN++)
		{
			if (!(cName.charAt(localN) > 64 && cName.charAt(localN) < 91) // Check for Uppercase 
				&& !(cName.charAt(localN) > 96 && cName.charAt(localN) < 123) // Check for Lowercase
				&& !(cName.charAt(localN) > 31 && cName.charAt(localN) < 33) // Check for space
				&& !(cName.charAt(localN) > 44 && cName.charAt(localN) < 47) // Check for dot(.) and hyphen(-)
				&& !(cName.charAt(localN) > 94 && cName.charAt(localN) < 96) // Check for underscore(_) 
				&& !(cName.charAt(localN) > 39 && cName.charAt(localN) < 42)) // Check for brackets()
			{
				return true;
			}
		}
		return false;
	}
	//	CHECK TAB SPACES IN BETWEEN THE STRINGS
	public boolean checkTabSpaces(String field)
	{
		for (int k = 0; k < field.length(); k++)
		{
			if (field.charAt(k) == 9)
			{
				return true;
			}
		}
		return false;
	}
	
	//	VALIDATE ORGANIZATION ID
	public boolean isValidOrgID(String field)
	{
		int alphabetCount = 0;
		int specialCharacterCount = 0;
		int numberCount = 0;
		if (field.trim().length() == 0)
		{
			return true;
		}
		for (int i = 0; i < field.length(); i++)
		{
			if (!Character.isDigit(field.charAt(i)))
			{
				if (!Character.isLetter(field.charAt(i)))
				{
					specialCharacterCount++;
				}
				else
				{
					alphabetCount++;
				}
			}
			else
			{
				numberCount++;
			}
		}
		if(alphabetCount == 6)
		{
			return true;
		}	
		else if (numberCount == 6)
		{
			return true;
		}
		else if (specialCharacterCount != 0)
		{
			return true;
		}
		return false;
	}
	
	//	CHECK FOR BLANKSPACES IN BETWEEN THE FIELDS
	public String trimInnerSpaces(String str)
	{
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < str.length(); i++)
		{
			if (str.charAt(i) != ' ')
			{
				sb.append(str.charAt(i));
			}
		}
		return sb.toString();
	}
	
	// CHECK FUTURE FINANCIAL YEAR 
	public boolean checkFutureFinancialYear(String financialYear)
	{
		Date dt = new Date();
		final SimpleDateFormat systemMth = new SimpleDateFormat("MM");
		final SimpleDateFormat systemYear = new SimpleDateFormat("yyyy");
		String systemMonth = systemMth.format(dt);
		String systemYr = systemYear.format(dt); 
		if(Integer.parseInt(financialYear) == Integer.parseInt(systemYr))		 
		{		
			if(systemMonth.equals("01") || systemMonth.equals("02") || systemMonth.equals("03"))
			{
				return true;
			}
			else
			{
				return false;
			}
		}
		else if(Integer.parseInt(financialYear) > Integer.parseInt(systemYr))
		{
			return true;
		}
		return false;
	}
	
	
	
	//Added by subhankar
	
	public boolean isValidNatureOfDeduction(String Nat_of_Deduction)
	{
		if(Nat_of_Deduction == null || Nat_of_Deduction.trim().length() == 0)
		{
			return false;
		}
		for(int i=0 ; i < NATURE_OF_DEDUCTION.length ; i++)
		{
			if(NATURE_OF_DEDUCTION[i].equals(Nat_of_Deduction))
			{
				return true;
			}
		}
		return false;
	}
	
	
	//Gauri added this for form type changes for CR 89435, FVU 1.9::START
	
	public boolean isValidFormType(String form_type)
	{
		if(form_type == null || form_type.trim().length() == 0)
		{
			return false;
		}
		for(int i=0 ; i < FORM_TYPE.length ; i++)
		{
			if(FORM_TYPE[i].equals(form_type))
			{
				return true;
			}
		}
		return false;
	}
	
	
	
	//Gauri added this for form type changes for CR 89435, FVU 1.9::END
	
	
	public boolean isValidDDOMappingFlag(String validMapping)
	{
		if(validMapping == null || validMapping.trim().length() == 0)
		{
			return false;
		}
		for(int i = 0 ; i < DDO_MAPPING.length ; i++)
		{
			if(DDO_MAPPING[i].equals(validMapping))
			{
				return true;
			}
		}
		return false;
	}
	public boolean isIn_DDO_REG_NO_MOD(String midDigit,String lastDigit)
	{
		if(lastDigit == null || lastDigit.trim().length() == 0)
		{
			return false;
		}
		int modSeven = Integer.parseInt(midDigit) % 7;
		for(int i = 0 ; i < DDO_REG_NO_MOD.length; i++)
		{
			if(DDO_REG_NO_MOD[i].equals(lastDigit))
			{
				if(i == modSeven)
				{
					return true;
				}
			}
		}
		return false;
	}
	
	public boolean checkTanAgainstConstants(String tan)
	{
		if(tan == null || tan.trim().length() == 0)
		{
			return false;
		}

		for(int i = 0; i < DDO_TAN_INVALID.length; i++)
		{
			if(DDO_TAN_INVALID[i].equals(tan))
			{
				return true;
			}
			
		}
		return false;
	}
	
	
	
	public static boolean isValidMonthOfTransferVoucher(String month,String year)
	{
		try
		{
			if(month == null || month.trim().length() == 0)
			{
				return false;
			}
		    Calendar c =Calendar.getInstance();
		    int currMon = c.get(Calendar.MONTH)+1;
		    int currYear = c.get(Calendar.YEAR);
		    
		    int batchMon = Integer.parseInt(month);
		    int batchYear = Integer.parseInt(year); 
		    
	        if((batchMon >= 1 && batchMon <= 12) && (batchMon > currMon) && batchYear < currYear)
	        {
	        	return true;
	        }
	        else if((batchMon >= 1 && batchMon <= 12) && (batchMon <= currMon))
	        	{
	        	return true;
	        	}
	        else
	        	{
	      	     return false;
	            }
		}
	        catch (Exception e) {
				return false;
			}
	        
	}
	
	
	public boolean checkStringWithCharForSubMinistry_O(String data)
	{
		String field = data.trim();
		if (field.trim() == "" || field.trim().length() == 0 || data == null)
		{
			return false;
		}
		
		for (int i = 0; i < data.length(); i++)
		{
			if (((data.charAt(i) >= 65 && data.charAt(i) <= 90)) ||((data.charAt(i) >= 97 && data.charAt(i) <= 122)))
			{
				return true;
			}
		}
	return false;
	}
	
	
	public boolean CheckZeros(String data)
	{
		boolean flag = true;
		for(int i=0;i< data.length();i++)
		{
			int bit = data.charAt(i);
			if(bit != '0')
			{
				flag = false;
			}
		}
		return flag;
	}
	
	
	// End of Added by subhankar
} 
