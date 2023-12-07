package bugReportQuality;

import java.util.ArrayList;
import java.util.HashMap;

import utility.ContentLoader;
import utility.MiscUtility;

public class PerformanceCalculator {

	public static void main(String[] args) {
		String project_no="1";
		// TODO Auto-generated method stub
		String corpus="Apache";
		String project="HBASE";
		String version="0_20_5";
		String base="C:\\Users\\mukta\\OneDrive\\Documents\\Journals\\JSS-2023-2024\\Datasets\\Repo\\"+corpus+"-"+project+"\\"+version+"\\"+version+"\\";
	
		//String base="C:\\Users\\mukta\\OneDrive\\Documents\\PhD\\VisualBugProject\\Experiment\\DataSet\\Project"+project_no+"\\Corpus";
		String resultPathActual=base+"data\\Results\\FinalResultMethodDec07.txt";
		String groundTruth=base+"\\data\\goldset"+corpus+project+version+".txt";
		String idPath=base+"\\data\\BugIDs.txt";
		PerformanceCalculator obj=new PerformanceCalculator();
		obj.Calculator(resultPathActual, groundTruth, idPath);
	}

	
	public void Calculator(String resultPathActual, String groundTruthPath, String idPath)
	{
		//HashMap<String, ArrayList<String>> hmActualResult=new HashMap<String, ArrayList<String>>();
		ArrayList<String> bugIdsList=getIssueIDlist(idPath); 
		System.out.println(bugIdsList);
		//Collect Actual Results
		HashMap<String, ArrayList<String>> hmActualResult=loadActualResults(resultPathActual, bugIdsList);
		//Collect Ground Truth
		HashMap<String, ArrayList<String>> hmGroundTruth=loadgroundTruth(groundTruthPath, bugIdsList);
		System.out.println("Actual Total: "+hmActualResult.size()+" and GroundTruth: "+hmGroundTruth.size());
		//Compare Results
		comparePerformance(10, hmActualResult,hmGroundTruth);
	}
	
	public ArrayList<String> getIssueIDlist(String idPath)
	{
		ArrayList<String> bugIdsList=new ArrayList<String>();
		
		ArrayList<String> content=ContentLoader.getAllLinesList(idPath);
		for(int i=3;i<content.size();i++)
		{
			bugIdsList.add(content.get(i));
		}
		
		return bugIdsList;
	}
	
	public void comparePerformance(int topK, HashMap<String, ArrayList<String>> hmActualResult, HashMap<String, ArrayList<String>> hmGroundTruth)
	{
		int no_of_bug_found=0;
		int count=0;
		topK=topK-1;
		int i=0;
		for(String issuID:hmActualResult.keySet()) 
		{
			//System.out.println(issuID);
			
			if(hmGroundTruth.containsKey(issuID))
			{
				count=0;
				ArrayList<String> actualResult=hmActualResult.get(issuID);
				ArrayList<String> groundTruth=hmGroundTruth.get(issuID);
			//	System.out.println(groundTruth);
				int found=0;
				for(String file:actualResult)
				{
					if(count>topK)break;
					count++;
					if(groundTruth.contains(file))
					{
						found=1;
						no_of_bug_found++;
						System.out.print(++i+" "+issuID+" "+count+" ");
						System.out.println(file+" ");
						
						break;
					}
				}
			}
		}
		System.out.println("No. of Bug found: "+no_of_bug_found);
	}
	
	public HashMap<String, ArrayList<String>> loadActualResults(String resultPathActual,ArrayList<String> bugIdsList)
	{
		HashMap<String, ArrayList<String>> hmActualResult=new HashMap<String, ArrayList<String>>();
		
		ArrayList<String> content=ContentLoader.getAllLinesList(resultPathActual);
		int count=0;
		String issueID="";
		ArrayList<String> resultsPerIssue=new ArrayList<String>();
		int x=0;
		for(String line:content)
		{
			if(count==0) 
			{
				if(bugIdsList.contains(issueID))hmActualResult.put(issueID, resultsPerIssue);
				resultsPerIssue=new ArrayList<String>();
				String[] spilter=line.split(":");
				issueID=spilter[0];
				int no_of_fixed_files=Integer.valueOf(spilter[1]);
				if(no_of_fixed_files>0)count++;
				System.out.println(++x+"------------"+issueID);
			}
			else
			{
				resultsPerIssue.add(line);
				count++;
				if(count==11)count=0;
			}
		}
		//last one
		if(bugIdsList.contains(issueID))hmActualResult.put(issueID, resultsPerIssue);
	 //   MiscUtility.showResult(10, hmActualResult);
		System.out.println(bugIdsList.size());
		return hmActualResult;
	}
	
	public HashMap<String, ArrayList<String>> loadgroundTruth(String groundTruthPath, ArrayList<String> bugIdsList)
	{
		HashMap<String, ArrayList<String>> hmGroundTruth=new HashMap<String, ArrayList<String>>();
		
		ArrayList<String> content=ContentLoader.getAllLinesList(groundTruthPath);
		int count=0;
		String issueID="";
		ArrayList<String> resultsPerIssue=new ArrayList<String>();
		int no_of_changed_file=0;
		for(String line:content)
		{
			if(count==0)
			{
				count++;
				String[] spilter=line.split(" ");
				if(bugIdsList.contains(issueID))hmGroundTruth.put(issueID, resultsPerIssue);
			//	System.out.println(issueID+"---------------------------------------------------------");
				issueID=spilter[0];
				no_of_changed_file=Integer.valueOf(spilter[1]);
				if(no_of_changed_file==0) count=0;
				resultsPerIssue=new ArrayList<String>();
			}
			else
			{
				resultsPerIssue.add(line);
				count++;
				if(count>no_of_changed_file) count=0;
			}
			
		}
		
	  //  MiscUtility.showResult(10, hmGroundTruth);
		return hmGroundTruth;
	}
}
