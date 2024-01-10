package localizationHints;

import java.io.File;
import java.util.ArrayList;

import bugReportQuality.BugReportClassifier;
import bugReportQuality.MainBRquality;
import utility.ContentLoader;
import utility.ContentWriter;

public class MainLocHints {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String corpus="Apache";
		String project="HIVE";
		String version="1_2_1";
		String base="C:\\Users\\mukta\\OneDrive\\Documents\\Journals\\JSS-2023-2024\\Datasets\\Repo\\"+corpus+"-"+project+"\\"+version+"\\"+version+"\\";
		String bugFolder = base + "\\data\\BugDataExtracted\\"; 
		MainLocHints obj = new MainLocHints();
		obj.FindLocHints(base,bugFolder);
	}

	
	public void FindLocHints(String base, String bugFolder)
	{
		File[] files=new File(bugFolder).listFiles();
		//String allInOne="";
		ArrayList <String> list=new ArrayList<String>();
		int noOfBugReports=files.length;
		ArrayList<String> NoHint=new ArrayList();
		ArrayList<String> WithHint=new ArrayList();
		
		int count=0;
		for(File f:files){
			
			String fileName=f.getName();
			String content=ContentLoader.readContentSimple(f.getAbsolutePath());
			BugReportClassifier BRC=new BugReportClassifier(content);
			String type=BRC.determineReportClass();

			String bugID=fileName.substring(0, fileName.length()-4);
			//System.out.println(fileName+ " "+bugID+"\n"+type);
			System.out.println(++count+" "+bugID);
			if(type.equalsIgnoreCase("PE")||type.equalsIgnoreCase("ST")) WithHint.add(bugID);
			else if(type.equalsIgnoreCase("NL")) NoHint.add(bugID);
			
		}
		System.out.println("With Hint: "+WithHint.size());
		System.out.println("No Hint: "+NoHint.size());
		
		
		ContentWriter.writeContent(base+"\\data\\WithHint.txt", WithHint);
		ContentWriter.writeContent(base+"\\data\\NoHint.txt", NoHint);
	}
}
