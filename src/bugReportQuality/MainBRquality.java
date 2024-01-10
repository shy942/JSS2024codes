package bugReportQuality;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.store.LockObtainFailedException;

import localizationHints.LocHintsAnalysis;
import utility.ContentLoader;
import utility.ContentWriter;

public class MainBRquality {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		
		String corpus="Apache";
		String project="HIVE";
		String version="1_2_1";
		String base="C:\\Users\\mukta\\OneDrive\\Documents\\Journals\\JSS-2023-2024\\Datasets\\Repo\\"+corpus+"-"+project+"\\"+version+"\\"+version+"\\";
		MainBRquality obj = new MainBRquality();
		obj.mainManager(corpus, project, version, base);
	}

	public void mainManager(String corpus, String project, String version, String base) throws IOException, IOException, IOException
	{
		//For later
		//Create a list. Retrieve the version information in the list
		//For each version, run the following code
		//for loop for versions
		//String version="0_20_5";
		//Step #1
		String XMLfolderPath=base+"\\bugXML\\"+project+"_"+version+".xml";
		//Extract bug report contents from XML file and save the gold set
		//new BugExtractorForBLRepo().extractBugReports(XMLfolderPath, base, base+"\\data\\goldset"+corpus+project+version+".txt");
		
		//Step #2
		//Bug Report preprocessing
		String bugFolder = base + "\\data\\BugDataExtracted\\"; 
		String bugPPFolder = base + "\\data\\BugDataPreprocessed\\";
		//new BugReportCorpusBuilder().createPreprocessedRepo(bugFolder, bugPPFolder);
		
		//Step #3
		//source code preprocessing
		//String outFolder=base+"\\ProcessedSourceCorpusPlain\\";
		//new SourceCodeCorpusBuilderPlain(base, corpus).nameChangedandContent(outFolder);
		SourceCodeCorpusBuilder obSCB= new SourceCodeCorpusBuilder( base);
		//obSCB.createPreprocessedRepo(corpus);
		int no_of_source_codes=obSCB.getNoOfsourcecode();
		
		
		//Step #4
		//Lucene-based bug localization
		//Indexing
		String indexFolder=base+"\\Index_"+corpus+"Plain\\";
	    String docFolder=base+"\\"+"ProcessedSourceCorpusPlain\\";
		new LuceneIndexer(docFolder, indexFolder, corpus, base).createIndex();
		//Bug Localization
		HashMap<Integer, HashMap<String, Double>>finalResultHM=new BugLocatorLuceneBased(indexFolder,bugPPFolder,no_of_source_codes).getLuceneBasedScore();
		//System.out.println(finalResultHM.size());
		String resultPathName="FinalResultJan10.txt";
		ContentWriter.writeContentFinalResult(base+"data\\Results\\"+resultPathName, finalResultHM);
		System.out.println("no_of_source_codes: "+no_of_source_codes);
		
		//Step #5
		//HQ and LQ analysis
		System.out.println("HQ and LQ Resylts==============================================================================");
		String resultPathActual=base+"data\\Results\\"+resultPathName;
		String groundTruth=base+"\\data\\goldset"+corpus+project+version+".txt";
		String idPath=base+"\\data\\BugIDs.txt";
		HQandLQanalysis objHLquality=new HQandLQanalysis();
		objHLquality.Performace_Analysis_Lucene_Main(base,resultPathActual, groundTruth, idPath);
		String[] list_0f_methods= {"AmaLgam", "BLIA","BLUiR", "BRTracer", "BugLocator", "Locus"};
		System.out.println("Result for: "+corpus+" "+project+" "+version);
		for(int i=0;i<6;i++)
		{
			objHLquality.Find_HQ_LQ_Bench4BL(base, base+"\\Bench4BLresults\\exp"+corpus+project+"_"+version+"\\"+corpus+"\\"+project+"\\"+list_0f_methods[i]+"_"+project+"_"+project+"_"+version+"_output.txt", groundTruth, idPath, list_0f_methods[i]);
			
		}
		
		//Hinat and No-hint Analysis
		System.out.println("Hint and No-Hint Resylts==============================================================================");
		LocHintsAnalysis objHNhint=new LocHintsAnalysis();
		objHNhint.Performace_Analysis_Lucene_Main(base,resultPathActual, groundTruth, idPath);
		for(int i=0;i<6;i++)
		{
			objHNhint.Find_WH_NH_Bench4BL(base, base+"\\Bench4BLresults\\exp"+corpus+project+"_"+version+"\\"+corpus+"\\"+project+"\\"+list_0f_methods[i]+"_"+project+"_"+project+"_"+version+"_output.txt", groundTruth, idPath, list_0f_methods[i]);
	
		}
	}
	
	}
