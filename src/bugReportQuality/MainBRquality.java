package bugReportQuality;

public class MainBRquality {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		String corpus="Apache";
		String project="HBASE";
		String version="0_20_5";
		String base="C:\\Users\\mukta\\OneDrive\\Documents\\Journals\\JSS-2023-2024\\Datasets\\Repo\\"+corpus+"-"+project+"\\"+version+"\\"+version+"\\";
		MainBRquality obj = new MainBRquality();
		obj.mainManager(corpus, project, base);
	}

	public void mainManager(String corpus, String project, String base)
	{
		//For later
		//Create a list. Retrieve the version information in the list
		//For each version, run the following code
		//for loop for versions
		String version="0_20_5";
		String XMLfolderPath=base+"\\bugXML\\"+project+"_"+version+".xml";
		//Extract bug report contents from XML file and save the gold set
		new BugExtractorForBLRepo().extractBugReports(XMLfolderPath, base, base+"\\data\\goldset"+corpus+project+version+".txt");
		
		//Bug Report preprocessing
		String bugFolder = base + "\\data\\BugDataExtracted\\"; 
		String bugPPFolder = base + "\\data\\BugDataPreprocessed\\";
		new BugReportCorpusBuilder().createPreprocessedRepo(bugFolder, bugPPFolder);
		
		
		//source code preprocessing
		new SourceCodeCorpusBuilder( base).createPreprocessedRepo(corpus);
	}
}
