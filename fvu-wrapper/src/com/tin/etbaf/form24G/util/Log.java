/*
 * Created on March 15, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.tin.etbaf.form24G.util;
/*import org.apache.log4j.*;*/
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author tcs
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class Log
 {
	public static Logger tbaf_log = null;
		static
		{
			tbaf_log =LogManager.getLogger("TBAFLogging");
		}
 }
