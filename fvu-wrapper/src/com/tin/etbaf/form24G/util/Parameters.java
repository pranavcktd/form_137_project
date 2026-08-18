/**	
 *	Class: Parameters.java 
 */
package com.tin.etbaf.form24G.util;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Properties;
import java.util.StringTokenizer;
/**
 * 	This class contains all the static values like FVU version and values 
 * 	which will be used in TBAFHashing.properties file when uploaded to STM
 *
 *	@author TCS
 *	@version 14   
 */
public class Parameters
{
	public static boolean ignoreHashing = false;
	public static boolean ignoreVersioning = false;
	public static boolean ignoreRecordLevelHashing = false;
	public static String tbaffvuVersion[] = { "FVU 1.0","FVU 1.1","FVU 1.2","FVU 1.3","FVU 1.4","FVU 1.5","FVU 1.6","FVU 1.7","FVU 1.8","FVU 1.9"}; //Gauri changed version to 1.9 for CR 89435
	public static String tbafsamVersion[] = { "SAM 3.2","SAM 3.3","SAM 3.6","SAM 3.7","SAM 3.8","SAM 3.9","SAM 4.0" };
	public static String tbafscmVersion[] = { "SCM 2.9","SCM 3.0","SCM 3.1","SCM 3.2" };
	//	public static final int maxVersionLength = 10;
	public static int maxHashCodeLength = 20;
	public static String tbafThreshholdYear ;
	public static String tbafThreshholdMonth ;
	
	public static HashMap GovtMap = new HashMap();
	
	static
	{
		GovtMap.put("A", "Central Government");
		GovtMap.put("S", "State Government");
	}

	static {
		try
		{
			File file = new File("tin_config/TBAFHashing.properties");
			if (file.exists() == true)
			{
				Properties p = new Properties();
				p.load(new FileInputStream(file));

				//				if (p.getProperty("IgnoreHashing").equalsIgnoreCase("true"))
				//					ignoreHashing = true;
				//
				//				if (p.getProperty("IgnoreVersioning").equalsIgnoreCase("true"))
				//					ignoreVersioning = true;
				//
				//				if (p.getProperty("IgnoreRecordLevelHashing").equalsIgnoreCase("true"))
				//					ignoreRecordLevelHashing = true;

				ignoreHashing = Boolean.valueOf(p.getProperty("IgnoreHashing")).booleanValue();
				ignoreVersioning = Boolean.valueOf(p.getProperty("IgnoreVersioning")).booleanValue();
				ignoreRecordLevelHashing = Boolean.valueOf(p.getProperty("IgnoreRecordLevelHashing")).booleanValue();

				String sVersion;

				sVersion = p.getProperty("FVUVersion");
				if (sVersion != null)
				{
					StringTokenizer st = new StringTokenizer(sVersion, ",");
					int count = st.countTokens();
					tbaffvuVersion = new String[count];
					for (int i = 0; i < count; i++)
					{
						tbaffvuVersion[i] = st.nextToken().trim();
					}
				}

				sVersion = p.getProperty("SAMVersion");
				if (sVersion != null)
				{
					StringTokenizer st = new StringTokenizer(sVersion, ",");
					int count = st.countTokens();
					tbafsamVersion = new String[count];
					for (int i = 0; i < count; i++)
					{
						tbafsamVersion[i] = st.nextToken().trim();
					}
				}

				sVersion = p.getProperty("SCMVersion");
				if (sVersion != null)
				{
					StringTokenizer st = new StringTokenizer(sVersion, ",");
					int count = st.countTokens();
					tbafscmVersion = new String[count];
					for (int i = 0; i < count; i++)
					{
						tbafscmVersion[i] = st.nextToken().trim();
					}
				}

				//				maxVersionLength = Integer.parseInt(p.getProperty("MAXVersionLength"));
				maxHashCodeLength = Integer.parseInt(p.getProperty("MaxHashCodeLength"));
			}
			
			InputStream is =    ClassLoader.getSystemClassLoader().getResourceAsStream("tin_config/24GFVU.properties");
//			File file2 = new File("tin_config/24GFVU.properties");
//			if (file2.exists() == true)
			
			if(is != null)
			{
				Properties p1 = new Properties();
				p1.load(is);
				tbafThreshholdYear = p1.getProperty("year");
				tbafThreshholdMonth = p1.getProperty("month");
			}
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
			Log.tbaf_log.error("Exception", e);
		}
	}
}