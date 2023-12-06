package bugReportQuality;

import java.io.File;
import java.util.ArrayList;


import utility.ContentLoader;
import utility.ContentWriter;
import utility.MiscUtility;


public class BugReportCorpusBuilder {


	
	protected void createPreprocessedRepo(String bugFolder, String bugPPFolder)
	{
		File[] files=new File(bugFolder).listFiles();
		//String allInOne="";
		ArrayList <String> list=new ArrayList<String>();
		int noOfBugReports=files.length;
		
		for(File f:files){
			
			String fileName=f.getName();
			String content=ContentLoader.readContentSimple(f.getAbsolutePath());
			BugReportPreprocessor bpp=new BugReportPreprocessor(content);
			String preprocessed=bpp.performNLPforAllContent();
		
			//preprocessed=fileName+": "+preprocessed.trim()+"\n";
			String outFile=bugPPFolder+"/"+fileName;
			ContentWriter.writeContent(outFile, preprocessed);
			//allInOne+=allInOne+preprocessed+"\n";
			System.out.println("Preprocessed:"+fileName);
			list.add(preprocessed);
			
		}

	}
		
	
	
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
	
		String bugFolder = ""; 
		String bugPPFolder = "";
		new BugReportCorpusBuilder().createPreprocessedRepo(bugFolder, bugPPFolder);
	}

}
