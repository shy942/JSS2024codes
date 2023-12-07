package bugReportQuality;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;

import utility.ContentLoader;
import utility.ContentWriter;
import utility.MiscUtility;

public class BugLocatorLuceneBased {

	
	static String indexDir;
	String bugReportFolder;
	int no_of_source_code;

	public BugLocatorLuceneBased(String indexDir, String bugReportFolder) {
		this.indexDir = indexDir;
		this.bugReportFolder = bugReportFolder;
	}
	public BugLocatorLuceneBased(String indexDir, String bugReportFolder, int no_of_source_code) {
		this.indexDir = indexDir;
		this.bugReportFolder = bugReportFolder;
		this.no_of_source_code=no_of_source_code;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String corpus="Apache";
		String project="HBASE";
		String version="0_20_5";
		String base="C:\\Users\\mukta\\OneDrive\\Documents\\Journals\\JSS-2023-2024\\Datasets\\Repo\\"+corpus+"-"+project+"\\"+version+"\\"+version+"\\";
	
		
		String indexFolder=base+"\\Index_"+corpus+"\\";
		String bugReportFolder = base + "\\data\\BugDataPreprocessed\\";
	    BugLocatorLuceneBased obj=new BugLocatorLuceneBased(indexFolder,bugReportFolder,418);
		HashMap<Integer, HashMap<String, Double>>finalResultHM=obj.getLuceneBasedScore();
		System.out.println(finalResultHM.size());
		//obj.writeFinalResult(finalResultHM, "C:/Users/Shamima/Documents/"+"E\\PhD\\Repo\\"+project+"\\"+subproject+"\\"+version+"\\DataForFeatureExtraction\\RankInfo\\VSM100.txt");
		ContentWriter.writeContentFinalResult(base+"data\\Results\\FinalResultMethodDec07.txt", finalResultHM);
	}
    public HashMap<Integer, HashMap<String, Double>> getLuceneBasedScore()
    {
        HashMap<Integer, HashMap<String, Double>>finalResult=new HashMap<>();
        HashMap<String, ArrayList<String>> docMap=getBugReportContentMap(this.bugReportFolder);
        
        for(String bugID: docMap.keySet())
        {
            int queryID=Integer.valueOf(bugID.substring(0,bugID.length()-4));
            ArrayList<String> content=docMap.get(bugID);
            System.out.println(bugID);
            String queryContent=MiscUtility.list2Str(content);
            //System.out.println(queryContent);
            HashMap<String, Double> resultPerQuery=searchIndex(queryContent, 1.0);
            //System.out.println(resultPerQuery.size());
            //MiscUtility.showResult(90, resultPerQuery);
            finalResult.put(queryID, resultPerQuery);
        }
        
        
        return finalResult;
    }
	public HashMap<Integer, HashMap<String, Double>> getLuceneBasedScore(Double ALPHA)
	{
		HashMap<Integer, HashMap<String, Double>>finalResult=new HashMap<>();
		HashMap<String, ArrayList<String>> docMap=getBugReportContentMap(this.bugReportFolder);
		
		for(String bugID: docMap.keySet())
		{
			int queryID=Integer.valueOf(bugID.substring(0,bugID.length()-4));
			ArrayList<String> content=docMap.get(bugID);
			//System.out.println(bugID);
			String queryContent=MiscUtility.list2Str(content);
			//System.out.println(queryContent);
			HashMap<String, Double> resultPerQuery=searchIndex(queryContent, ALPHA);
			//System.out.println(resultPerQuery.size());
			//MiscUtility.showResult(90, resultPerQuery);
			finalResult.put(queryID, resultPerQuery);
		}
		
		
		return finalResult;
	}
	
	protected static void writeFinalResult(HashMap<Integer, HashMap<String, Double>>finalResultHM, String outFile)
	{
		
		HashMap<String, Double> hm=new HashMap<>();
		ArrayList<String> list=new ArrayList<>();
 		int count=0;
		for(int key:finalResultHM.keySet())
		{
			
			hm=finalResultHM.get(key);
			count=0;
			for(String sid:hm.keySet())
			{
				count++;
				if(count>10)break; 
				list.add(key+","+sid+","+hm.get(sid));
			}
		}
		
		ContentWriter.writeContent(outFile, list);
		
	}
	protected static HashMap<String, ArrayList<String>> getBugReportContentMap(
			String bugReportFolder) {
		File[] files = new File(bugReportFolder).listFiles();
		HashMap<String, ArrayList<String>> docMap = new HashMap<>();
		for (File file : files) {
			ArrayList<String> docTokens = ContentLoader.getDocTokensAll(file
					.getAbsolutePath());

			docMap.put(file.getName(), docTokens);
		}
		return docMap;
	}
	
	public HashMap<String, Double> searchIndex(String searchString, double ALPHA) {

		HashMap<String, Double> resultMap=new HashMap<>();

		try {
			Analyzer analyzer = new StandardAnalyzer();
			FSDirectory dir = FSDirectory.open(new File(this.indexDir).toPath());
            System.out.println(searchString+" "+indexDir.length());
			IndexReader indexReader;
			indexReader=DirectoryReader.open(dir);
			IndexSearcher searcher = new IndexSearcher(indexReader);

			int hitsPerPage = this.no_of_source_code;
			System.out.println(hitsPerPage);
			//int hitsPerPage = 100;
			QueryParser qp = new QueryParser("contents",
					analyzer);
			Query query = qp.parse(searchString); // parse the query and construct the Query object
			TopDocs docs = searcher.search(query, hitsPerPage);
			ScoreDoc[] hits = docs.scoreDocs;// run the query
			//for(int i=0;i<hits.length;++i) {
			for(int i=0;i<10;++i) {
			{
				ScoreDoc item = hits[i];
	        	Document doc = searcher.doc(item.doc);
	        	double score=item.score;
	        	System.out.println((i + 1) + ". " + doc.get("path") + "\t"+score);
	        	resultMap.put(doc.get("path"), score);
			}
		
			
           }
		indexReader.close(); 
		} catch (Exception e) {
			e.printStackTrace();
	}
		
		HashMap<String, Double> resultMapNormalized=normalizeMe(resultMap, ALPHA);
		HashMap<String, Double> resultSorted=MiscUtility.sortByValues(resultMapNormalized);
		return resultSorted;
}
	
	
	protected static HashMap<String, Double> normalizeMe(
			HashMap<String, Double> tempMap, double ALPHA) {
		double max = 0;
		for (String key : tempMap.keySet()) {
			double score = tempMap.get(key);
			if (score > max) {
				max = score;
			}
		}
		for (String key : tempMap.keySet()) {
			if (max > 0) {
				double oldScore = tempMap.get(key);
				tempMap.put(key, (oldScore / max)*ALPHA);
			}
		}
		return tempMap;
	}
}
