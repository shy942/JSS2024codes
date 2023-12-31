package utility;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;

public class ContentWriter {
	public static boolean writeContent(String outFile, ArrayList<String> items) {
		// writing content to output
		boolean written = false;
		try {
			FileWriter fwriter = new FileWriter(new File(outFile));
			for (String item : items) {
				fwriter.write(item+"\n");
			}
			fwriter.close();
			written = true;

		} catch (Exception exc) {
			exc.printStackTrace();
		}
		return written;
	}
	
	public static boolean writeFormulatedQueriesContent(String outFile, ArrayList<String> items) {
		// writing content to output
		boolean written = false;
		try {
			FileWriter fwriter = new FileWriter(new File(outFile));
			for (String item : items) {
				fwriter.write(item+"\r\n");
			}
			fwriter.close();
			written = true;

		} catch (Exception exc) {
			exc.printStackTrace();
		}
		return written;
	}

	public static boolean appendContent(String outFile, ArrayList<String> items) {
		// writing content to output
		boolean written = false;
		try {
			FileWriter fwriter = new FileWriter(new File(outFile), true);
			for (String item : items) {
				fwriter.write(item + "\n");
			}
			fwriter.close();
			written = true;

		} catch (Exception exc) {
			exc.printStackTrace();
		}
		return written;
	}

	public static boolean writeContentInt(String outFile,
			ArrayList<Integer> items) {
		// writing content to output
		boolean written = false;
		try {
			FileWriter fwriter = new FileWriter(new File(outFile));
			for (Integer item : items) {
				fwriter.write(item + "\n");
			}
			fwriter.close();
			written = true;

		} catch (Exception exc) {
			exc.printStackTrace();
		}
		return written;
	}

	public static boolean appendContentInt(String outFile,
			ArrayList<Integer> items) {
		// writing content to output
		boolean written = false;
		try {
			FileWriter fwriter = new FileWriter(new File(outFile), true);
			for (Integer item : items) {
				fwriter.write(item + "\n");
			}
			fwriter.close();
			written = true;

		} catch (Exception exc) {
			exc.printStackTrace();
		}
		return written;
	}

	public static void writeContent(String outFile, String content) {
		try {
			FileWriter fwriter = new FileWriter(new File(outFile));
			fwriter.write(content);
			fwriter.close();
		} catch (Exception exc) {
			exc.printStackTrace();
		}
	}

	public static void appendContent(String outFile, String content) {
		try {
			FileWriter fwriter = new FileWriter(new File(outFile), true);
			fwriter.write(content+"\n");
			fwriter.close();
		} catch (Exception exc) {
			exc.printStackTrace();
		}
	}

	 public static boolean writeContentFinalResult(String outFile, HashMap<Integer, HashMap<String, Double>> resultContainer) {
        // TODO Auto-generated method stub
        //System.out.println(resultContainer);
        boolean written = false;
        try {
            FileWriter fwriter = new FileWriter(new File(outFile), true);
            for (Integer key:resultContainer.keySet()) {
                fwriter.write(key+":"+resultContainer.get(key).size()+ "\n");
                HashMap<String, Double> queryContent=resultContainer.get(key);
                for(String key2:queryContent.keySet())
                {
                    fwriter.write(key2+"\n");
                }
            }
            fwriter.close();
            written = true;

        } catch (Exception exc) {
            exc.printStackTrace();
        }
        return written;
    }

}
