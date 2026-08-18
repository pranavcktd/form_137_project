//added by Faizan for FVU 1.4
package com.tin.etbaf.form24G.util;

import java.io.BufferedReader;
import java.io.FileReader;

import com.tin.etbaf.form24G.bean.PDFFileStatisticData;
import com.tin.etbaf.form24G.bean.TBAFFileStatistics;
import com.tin.etbaf.form24G.fvu.TBAFInterface;

//import net.sourceforge.barbecue.BarcodeException;
//import net.sourceforge.barbecue.output.OutputException;

public class PDFGenerator {
	
	
	public static void generatePDFFile(String inputFile,String pdfFileName,String temppdfFileName,String consFile) throws Exception {
		
		int lineCount = 0;
		int noOfBatches=0;
		PDFFileStatisticData [] statisticData = null;
		TBAFFileGenerator fg =new TBAFFileGenerator();
		TBAFFileStatistics fstatistics=new TBAFFileStatistics();
		
		try {
			BufferedReader fileReader = new BufferedReader (new FileReader(inputFile));
			
			String lineData =null;
			int currentBatchInProcessing = 0;
			String transactionType=null;
			
			while((lineData=fileReader.readLine())!=null){
				
				lineCount++;
				String [] recordData= (lineData+"-").split("\\^");
				if (recordData[1].equals(TBAFInterface.TBAF_FH_REC)){
					
					
				}
				
			}
	
		}catch (Exception e){
			
			e.printStackTrace();
		}
		
		
		
		
	}

}
