package com.tin.etbaf.form24G.util;
import java.util.StringTokenizer;

public class NewStringTokenizer
{
	boolean bCaret;
	boolean bFieldFound;
	boolean bNullField;

	StringTokenizer st = null;
	String sep;

	public NewStringTokenizer(String inpStr, String sep)
	{
		this.sep = sep;
		st = new StringTokenizer(inpStr, sep, true);
		bCaret = true;
		bFieldFound = false;
		bNullField = false;
	}

	/*	public boolean hasMoreTokens()
		{
		}*/

	public String nextToken()
	{
		final String FIELD_NULL = "";
		String token = null;

		while (st.hasMoreTokens() && token == null)
		{
			bNullField = false;
			bFieldFound = false;
			String sValue = st.nextToken();
			if ((sValue.equals(sep) && bCaret))// || sValue.trim().length() == 0)
			{
				bNullField = true;
				bFieldFound = true;
			}
			if (sValue.equals(sep))
			{
				bCaret = true;
			}
			else
			{
				bCaret = false;
				bFieldFound = true;
			}
			if (bFieldFound)
			{
				if (bNullField)
				{
					sValue = FIELD_NULL;
				}
				
				token = sValue;
			}
		}

		return token;
	}
}
