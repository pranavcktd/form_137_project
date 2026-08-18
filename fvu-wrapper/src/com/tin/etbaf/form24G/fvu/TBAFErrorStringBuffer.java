/*
 * Created on Jul 25, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.tin.etbaf.form24G.fvu;

import java.io.File;

//import org.apache.log4j.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.tin.etbaf.form24G.util.Log;
import com.tin.etbaf.form24G.util.TBAFFileGenerator;
/**
 * @author Prashant
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class TBAFErrorStringBuffer implements TBAFInterface
{
	static Logger log1 = LogManager.getLogger("TBAFLogging");
	private int errorCount;
	private int maxErrorCount = 2000;

	public StringBuffer errorBufferString = null;

	public boolean fileOpened = false;
	public boolean appedHtmlErrorFileHeader = true;
	public boolean appedHtmlErrorFileFooter = false;

	String fileName;

	TBAFFileGenerator objFileGenerator = new TBAFFileGenerator();
	String errorFileName;

	TBAFErrorStringBuffer()
	{
	}

	TBAFErrorStringBuffer(String ERROR_FILE)
	{
		errorCount = 0;
		errorFileName = ERROR_FILE;
		errorBufferString = new StringBuffer();

		String inputFileName = ERROR_FILE.substring(0, (ERROR_FILE.length() - 4)) + ".txt";
		File flName = new File(inputFileName);
		fileName = flName.getName();
	}

	/**  
	 *	Method to append ERROR Message String to buffer
	 *  Every Time this method is called, errorCount is incremented by one and errorString is appended to
	 *  String Buffer. When the errorCount reaches maxErrorCount, HTML Error File and Text File is Printed.
	 *  The value of this varaible is configurable.
	 */
	public void append(String errorString)
	{
		if (errorCount > maxErrorCount)
		{
			try
			{
				
				//	Write txt ERROR File
				StringBuffer htmlErrorFileStringBuffer =
				objFileGenerator.generateHtmlErrorFile(errorBufferString.toString(), appedHtmlErrorFileHeader, appedHtmlErrorFileFooter, fileName);


				//	Write Html ERROR File
				String htmlErrorFileName = errorFileName.substring(0, (errorFileName.length() - 4)) + "err.html";
				objFileGenerator.writeToFile(htmlErrorFileName, htmlErrorFileStringBuffer.toString(), 0, fileOpened);
				fileOpened = true;
				appedHtmlErrorFileHeader = false;

				errorBufferString = new StringBuffer();
				errorBufferString.append(errorString);
				errorCount = 1;
			}
			catch (Exception e)
			{
				Log.tbaf_log.error("Exception", e);
				e.printStackTrace();
			}
			return;
		}
		else
		{
			errorBufferString.append(errorString);
			errorCount++;
		}
	}
	/**
	 * @return errorcount
	 */
	public int getErrorCount()
	{
		return errorCount;
	}

}
