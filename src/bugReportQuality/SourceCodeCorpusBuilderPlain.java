package bugReportQuality;



import java.io.File;
import java.util.ArrayList;

import utility.ContentLoader;
import utility.ContentWriter;


public class SourceCodeCorpusBuilderPlain {

    File sourceCodeFolder;
	String sourceCodePPFolder;
	ArrayList<String> javaFilePaths;
	ArrayList<String> javaFilePathsLastName;
	int noOfFile=0;
	String base;
	String corpus;
	int count;
	public SourceCodeCorpusBuilderPlain(String base, String corpus)
	{
		this.count=0;
		this.base=base;
		this.corpus=corpus;
		this.sourceCodeFolder=new File(base+"\\Source\\");
		//this.sourceCodePPFolder=base+"\\ProcessedSourceCorpusJuly2019\\";
		this.javaFilePaths=new ArrayList<String>();
		this.javaFilePathsLastName=new ArrayList<String>();
		this.noOfFile=0;
		this.loadCPPFilesOnly(sourceCodeFolder);
	}
	
	/*protected void createPreprocessedRepo(String corpus)
	{
		int file_track=0;
		ArrayList<String> listofFiles=new ArrayList<>();
		int i=0;
		for (String s : javaFilePaths)
	    {
		    i++;
		   // if(i>3) break;
		    System.out.println();
	        String fileName=javaFilePathsLastName.get(file_track++);
	        String sourceCodeContent = ContentLoader.readContentSimple(fileName);
	        System.out.println(sourceCodeContent);
	    	//Remove initial copyright comment
			//CommentFilterer cf=new CommentFilterer(s,fileName);
			//cf.discardClassHeaderComment();
			System.out.println(i+" "+fileName);
			//System.out.println(file_track+" Preprocessed:"+this.sourceCodePPFolder+filePart);
			//ContentWriter.writeContent(this.sourceCodePPFolder+filePart, preprocessed);
		}
		System.out.println("Total no. of files: "+file_track);
	
	}*/
	
	public void loadCPPFilesOnly(final File folder) {
		for (final File fileEntry : folder.listFiles()) {
			if (fileEntry.isDirectory()) {
				loadCPPFilesOnly(fileEntry);
			} else {
				// System.out.println(fileEntry.getAbsolutePath());
				if (fileEntry.getName().endsWith(".java")||fileEntry.getName().endsWith(".h")) {
					System.out.println(++count+": "+fileEntry.getAbsolutePath());
					this.javaFilePaths.add(fileEntry.getAbsolutePath());
					this.javaFilePathsLastName.add(noOfFile++,
							fileEntry.getName());
				}
			}
		}
	}
	
	public void nameChangedandContent(String outFolder)
	{
		System.out.println("From nameChangedandContent "+"----------------------------------------------");
		System.out.println(javaFilePaths.size());
		for (String filepath : javaFilePaths)
	    { 
			
			System.out.println(filepath);
			//Find the output file name
			String[] spilter=filepath.split("\\\\");
			//System.out.println(spilter[spilter.length-1]);
			if(filepath.contains("org")) {
			int index=filepath.indexOf("org");
			System.out.println("Index of org: "+index);
			System.out.println("Sub String after org: "+filepath.substring(index));
			String subStringAfterimugi=filepath.substring(index);
			String [] subSpilter=subStringAfterimugi.split("\\\\");
			String fileToSave="";
			for(int i=0;i<subSpilter.length-1;i++) fileToSave+=subSpilter[i]+".";
			fileToSave+=subSpilter[subSpilter.length-1];
			System.out.println(fileToSave);
			//String fileToSave=spilter[spilter.length-1];
			
			//Get the code content
			String modifiedFilePath="";
			for(int i=0;i<spilter.length-1;i++)
			{
				modifiedFilePath+=spilter[i]+"/";
			}
			modifiedFilePath+=spilter[spilter.length-1];
			System.out.println(modifiedFilePath);
			String sourceCodeContent = ContentLoader.readContentSimple(modifiedFilePath);
				
			//Save to a new folder all together
		    ContentWriter.writeContent(outFolder+fileToSave, sourceCodeContent);
			}
			
		}
	}
	
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		String corpus="Apache";
		String project="HIVE";
		String version="2_1_0";
		String base="C:\\Users\\mukta\\OneDrive\\Documents\\Journals\\JSS-2023-2024\\Datasets\\Repo\\"+corpus+"-"+project+"\\"+version+"\\"+version+"\\";
	
		String outFolder=base+"\\ProcessedSourceCorpusPlain\\";
	    //String base="E:\\PhD\\Repo\\"+corpus+"\\";
		//new SourceCodeCorpusBuilder( base,corpus).createPreprocessedRepo(corpus);
		new SourceCodeCorpusBuilderPlain(base, corpus).nameChangedandContent(outFolder);
		//This is a simple change.
	}

}

