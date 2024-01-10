package localizationHints;

import java.util.ArrayList;
import java.util.HashMap;

import utility.ContentLoader;
import utility.ContentWriter;
import utility.MiscUtility;

public class LocHintsAnalysis {

	public static void main(String[] args) {
		String project_no="1";
		// TODO Auto-generated method stub
		String corpus="Apache";
		String project="HIVE";
		String version="1_2_1";
		String base="C:\\Users\\mukta\\OneDrive\\Documents\\Journals\\JSS-2023-2024\\Datasets\\Repo\\"+corpus+"-"+project+"\\"+version+"\\"+version+"\\";
	
		//String base="C:\\Users\\mukta\\OneDrive\\Documents\\PhD\\VisualBugProject\\Experiment\\DataSet\\Project"+project_no+"\\Corpus";
		String resultPathActual=base+"data\\Results\\FinalResultMethodDec14Plain.txt";
		String groundTruth=base+"\\data\\goldset"+corpus+project+version+".txt";
		String idPath=base+"\\data\\BugIDs.txt";
		LocHintsAnalysis obj=new LocHintsAnalysis();
		obj.Performace_Analysis_Lucene_Main(base,resultPathActual, groundTruth, idPath);
		String[] list_0f_methods= {"AmaLgam", "BLIA","BLUiR", "BRTracer", "BugLocator", "Locus"};
		System.out.println("Result for: "+corpus+" "+project+" "+version);
		for(int i=0;i<6;i++)
		{
			obj.Find_WH_NH_Bench4BL(base, base+"\\Bench4BLresults\\exp"+corpus+project+"_"+version+"\\"+corpus+"\\"+project+"\\"+list_0f_methods[i]+"_"+project+"_"+project+"_"+version+"_output.txt", groundTruth, idPath, list_0f_methods[i]);
	
		}
	}
      
	public void Performace_Analysis_Lucene_Main(String base, String resultPathActual, String groundTruthPath, String idPath)
	{
		//HashMap<String, ArrayList<String>> hmActualResult=new HashMap<String, ArrayList<String>>();
		ArrayList<String> bugIdsList=getIssueIDlist(idPath); 
		System.out.println("No. of bug extracted from XML file: "+bugIdsList.size());
		//Collect Actual Results
		HashMap<String, ArrayList<String>> hmActualResult=loadActualResults(resultPathActual, bugIdsList);
		//Collect Ground Truth
		HashMap<String, ArrayList<String>> hmGroundTruth=loadgroundTruth(groundTruthPath, bugIdsList);
		System.out.println("No. of bug for Lucene-based results: "+hmActualResult.size());
		System.out.println("No. of bug found in GroundTruth: "+hmGroundTruth.size());
		System.out.println("Need to discard "+(hmActualResult.size()-hmGroundTruth.size())+" bug");
		//Compare Results
		Performace_Analysis_Lucene(10, hmActualResult,hmGroundTruth,base);
	}
	
	public void Find_WH_NH_Bench4BL(String base, String resultPathBench4BL, String groundTruthPath, String idPath, String method)
	{
		System.out.print(method +" ");
		//HashMap<String, ArrayList<String>> hmActualResult=new HashMap<String, ArrayList<String>>();
		ArrayList<String> bugIdsList=getIssueIDlist(idPath); 
		//System.out.println(bugIdsList);
		//Collect Actual Results
		HashMap<String, ArrayList<String>> hmResultBench4BL=loadActualResultsBL(resultPathBench4BL, bugIdsList);
	   // System.out.println(hmResultBench4BL);
	    
	    HashMap<String, ArrayList<String>> hmBench4B_WH=new HashMap();
	    HashMap<String, ArrayList<String>> hmBench4B_NH=new HashMap();
		//System.out.println(hmResultBench4BL);
		//Collect Ground Truth
	    HashMap<String, ArrayList<String>> hmGroundTruth=loadgroundTruth(groundTruthPath, bugIdsList);
		
	    //Load WithHint and NoHint list
		ArrayList<String> WithHint=new ArrayList();
		ArrayList<String> NoHint=new ArrayList();
		
		//Retrieve WithHint and NoHint bug ids
		WithHint=ContentLoader.getAllLinesList(base+"\\data\\WithHint.txt");
		NoHint=ContentLoader.getAllLinesList(base+"\\data\\NoHint.txt");

		int count=0;
		int count_WithHint=0;
		int count_NoHint=0;
		for(String issuID:hmResultBench4BL.keySet()) 
		{
			if(hmGroundTruth.containsKey(issuID))
			{
				count++;
				ArrayList<String> rankList=new ArrayList<>();
				rankList=hmResultBench4BL.get(issuID);
				int foundHQ=0;
				for(String rank:rankList)
				{
					int r=Integer.valueOf(rank);
					if(r<=9) 
					{
						foundHQ=1;
					    if(WithHint.contains(issuID)) 
					    	{
					    	    count_WithHint++;
					    		//rHQList stores rank 0 to 9
					    		ArrayList<String> rWithHintList=new ArrayList<>();
					    		if(hmBench4B_WH.containsKey(issuID))
					    		{
					    			rWithHintList=hmBench4B_WH.get(issuID);
					    		}
					    		rWithHintList.add(String.valueOf(r));
					    		hmBench4B_WH.put(issuID, rWithHintList);
					    	}
					    else if(NoHint.contains(issuID)) 
					    	{
					    		count_NoHint++;
					    		//rLQList stores rank 0 to 9
					    		ArrayList<String> rLQList=new ArrayList<>();
					    		if(hmBench4B_NH.containsKey(issuID))
					    		{
					    			rLQList=hmBench4B_NH.get(issuID);
					    		}
					    		rLQList.add(String.valueOf(r));
					    		hmBench4B_NH.put(issuID, rLQList);
					    	}
					 
					}
				}
			}
		}
		
		//System.out.println(hmBench4B_HQ);
		//System.out.println(hmBench4B_LQ);
		System.out.println("Actual WH and NH: ");
		System.out.println(WithHint.size()+" "+NoHint.size());
		System.out.println("For "+method);
		System.out.println( hmBench4B_WH.size()+" "+hmBench4B_NH.size());
		System.out.println("Top1         Top5        Top10         MRR");
		computeAvgPerformanceB4BL(hmBench4B_WH, WithHint.size());
		
		computeAvgPerformanceB4BL(hmBench4B_NH, NoHint.size());
		//System.out.println("count: "+count);
		
		//double HQ_percent=Double.valueOf(count_HQlucene)/Double.valueOf(HQlucene.size())*100;
		//double LQ_percent=Double.valueOf(count_LQlucene)/Double.valueOf(LQlucene.size())*100;
		//System.out.print("HQ: "+HQ+"("+HQ_percent+") LQ: "+LQ+"("+LQ_percent+")"+"\n");
		//System.out.println("HQ retrieved: "+count_HQlucene+"("+HQ_percent+") LQ retrieved: "+count_LQlucene+"("+LQ_percent+")");
	    
	   // System.out.print(" Not retrieved: "+not_retrieved+"\n");
	}


		
	
	public void computeAvgPerformance( HashMap<String, ArrayList<String>> hmBench4B_Top10, int no_of_bugs)
	{
		
		//Compute Hit@1
		Double avgTop1=computTopK(1, hmBench4B_Top10, no_of_bugs);
		System.out.print(avgTop1+" ");
		//Compute Hit@5
		Double avgTop5 =computTopK(5, hmBench4B_Top10, no_of_bugs);
		System.out.print(avgTop5+" ");
		//Compute Hit@10
		Double avgTop10 =computTopK(10, hmBench4B_Top10, no_of_bugs);
		System.out.println(avgTop10+" ");
		//Compute MRR
		//create a new method for MRR
		//ComputeMRR (hmBench4B_Top10,no_of_bugs);
		//create a new method for MAP
		//ComputeMAP(hmBench4B_Top10, no_of_bugs);
	}
	
	
	public void computeAvgPerformanceB4BL( HashMap<String, ArrayList<String>> hmBench4B_Top10, int no_of_bugs)
	{
		
		//Compute Hit@1
		Double avgTop1=computTopK(1, hmBench4B_Top10, no_of_bugs);
		System.out.print(avgTop1+" ");
		//Compute Hit@5
		Double avgTop5 =computTopK(5, hmBench4B_Top10, no_of_bugs);
		System.out.print(avgTop5+" ");
		//Compute Hit@10
		Double avgTop10 =computTopK(10, hmBench4B_Top10, no_of_bugs);
		System.out.print(avgTop10+" ");
		//Compute MRR
		ComputeMRR (hmBench4B_Top10,no_of_bugs);
		//Compute MAP
		ComputeMAP(hmBench4B_Top10, no_of_bugs);
	}
	
	
	public static double ComputeMAP(HashMap<String, ArrayList<String>> finalRankedResult, int no_of_bug)
    {
        double averagePrecision=0.0;
        for(String issueID: finalRankedResult.keySet())
        {
            ArrayList<String> rankList=finalRankedResult.get(issueID);
            averagePrecision+=getAvgPrecisionEachQuery(rankList, issueID);
            //System.out.println(rankList);
            //System.out.println(getAvgPrecisionEachQuery(rankList));
        }
       /// int totalQuery=obj.resultsMap.size();
        //System.out.println("averagePrecision: "+averagePrecision);
        double MAP=averagePrecision/Double.valueOf(no_of_bug);
        //System.out.println("Total Query: "+totalQuery+" MAP: "+MAP);
        System.out.println(MAP);
        return MAP;
    }
    
    public static double getAvgPrecisionEachQuery(ArrayList<String> rankList, String issueID)
    {
        double Precision=0.0;
        int count =0;
        for(String rankStr:rankList)
        {
            count++;
            int rank=Integer.valueOf(rankStr)+1;
            Precision+=Double.valueOf(count)/Double.valueOf(rank);
        }
        int length=rankList.size();
        double AvgPrecision=Precision/Double.valueOf(count);
        //double AvgPrecision=Precision/Double.valueOf(length);
        return AvgPrecision;
        
    }
	
	public static double ComputeMRR(HashMap<String, ArrayList<String>> finalRankedResult, int no_of_bugs)
    {
        double averageRecall=0.0;
        for(String queryID: finalRankedResult.keySet())
        {
            ArrayList<String> rankList=finalRankedResult.get(queryID);
            averageRecall+=get1stRecall(rankList);
            //System.out.println(rankList);
            //System.out.println(get1stRecall(rankList,TOP_K));
        }
        
        double MRR=Double.valueOf(averageRecall)/Double.valueOf(no_of_bugs);
       // System.out.println("averageRecall: "+averageRecall);
        // System.out.println("No of bugs: "+no_of_bugs);
        System.out.print(MRR+"   ");
        return MRR;
    }
    
    public static double get1stRecall(ArrayList<String> rankList)
    {
        double recall1st=0.0;
        int count =0;
        int length=rankList.size();
        
        recall1st=1/Double.valueOf(rankList.get(0)+1);
        
        return recall1st;
        
    }
	public Double computTopK(int topK,  HashMap<String, ArrayList<String>> hmBench4B_Top10, int no_of_bugs)
	{
		topK=topK-1;
		int count=0;
		for(String issuID:hmBench4B_Top10.keySet()) 
		{
			ArrayList<String> rankList=new ArrayList<>();
			rankList=hmBench4B_Top10.get(issuID);
			for(String rank:rankList)
			{
				int r=Integer.valueOf(rank);
				if(r<=topK)
				{
					count++;
					break;
				}
			}
		}
		Double AverageTopK=Double.valueOf(count)/Double.valueOf(no_of_bugs)*100;
		
		return AverageTopK;
	}	
	private HashMap<String, ArrayList<String>> loadActualResultsBL(String resultPathBench4BL,
			ArrayList<String> bugIdsList) {
		// TODO Auto-generated method stub
		HashMap<String, ArrayList<String>> hm=new HashMap<>();
        ArrayList <String> list =new ArrayList<String>();
        
        //list=ContentLoader.readContent(resultPath);
        list=ContentLoader.readContent(resultPathBench4BL);
        
        for(String line: list)
        {
           // System.out.println(line);
            String [] spilter=line.split("\\s");
            String bugID=spilter[0];
            String rankInfo=spilter[2];
            int rank=Integer.valueOf(rankInfo);
            ArrayList<String> rankList=new ArrayList<>();
            if(hm.containsKey(bugID))
            {
                rankList=hm.get(bugID);
                rankList.add(rankInfo);
            }
            else
            {
                rankList.add(rankInfo);
            }
            hm.put(bugID, rankList);
        }
        return hm;
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
	
	public void Performace_Analysis_Lucene(int topK, HashMap<String, ArrayList<String>> hmActualResult, HashMap<String, ArrayList<String>> hmGroundTruth, String base)
	{
		int no_of_bug_found=0;
		int count=0;
		topK=topK-1;
		int i=0;
		
		
		HashMap<String, ArrayList<String>> hmLucene_Top10=new HashMap();
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
						
						//save this rank,
						String rank=String.valueOf(count-1);
						ArrayList<String> rankList=new ArrayList<>();
						if(hmLucene_Top10.containsKey(issuID))
						{
							rankList=hmLucene_Top10.get(issuID);
						}
						rankList.add(rank);
						hmLucene_Top10.put(issuID, rankList);
						//break;
					}
				}
			
			}
		}
		//System.out.println("No. of Bug found: "+no_of_bug_found);
		
		//Performance for WithHint and NoHint bug reports
		lucene_WiH_NoH_analysis(hmLucene_Top10, base);
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
				//    System.out.println(++x+"------------"+issueID);
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
	public void lucene_WiH_NoH_analysis(HashMap<String, ArrayList<String>> hmLucene_Top10,String base)
	{
		//Load WithHint and NoHint list
		ArrayList<String> WithHint=new ArrayList();
		ArrayList<String> NoHint=new ArrayList();
				
		//Retrieve WithHint and NoHint bug ids
		WithHint=ContentLoader.getAllLinesList(base+"\\data\\WithHint.txt");
		NoHint=ContentLoader.getAllLinesList(base+"\\data\\NoHint.txt");
		
		HashMap<String, ArrayList<String>> hmLuicene_WiH=new HashMap();
		HashMap<String, ArrayList<String>> hmLucene_NoH=new HashMap();

		int count=0;
		int count_WithHint=0;
		int count_NoHint=0;
		for(String issuID:hmLucene_Top10.keySet()) 
		{
				count++;
				ArrayList<String> rankList=new ArrayList<>();
				rankList=hmLucene_Top10.get(issuID);
				if(WithHint.contains(issuID))hmLuicene_WiH.put(issuID, rankList);
				else if(NoHint.contains(issuID))hmLucene_NoH.put(issuID, rankList);
				
		}
				
		System.out.println("WH in Lucene: "+hmLuicene_WiH.size()+" and NH in Lucene: "+hmLucene_NoH.size());		
		System.out.println("Performance for With Hints");
		computeAvgPerformance(hmLuicene_WiH, WithHint.size());
		System.out.println("Performance for No Hints");	
		computeAvgPerformance(hmLucene_NoH, NoHint.size());
				
			   
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
