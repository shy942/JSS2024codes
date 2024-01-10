package bugReportQuality;

import java.io.File;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

//import com.sun.swing.internal.plaf.metal.resources.metal;

import utility.ContentWriter;
import utility.MiscUtility;

public class BugExtractorForBLRepo {

	public BugExtractorForBLRepo() {

	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		String corpus = "Apache";
		String project = "HBASE";
		String version = "1_2_4";
		String base = "C:\\Users\\mukta\\OneDrive\\Documents\\Journals\\JSS-2023-2024\\Datasets\\Repo\\" + corpus + "-"
				+ project + "\\" + version + "\\" + version + "";
		// String base="E:\\PhD\\Repo\\"+corpus+"\\"+project+"\\"+version;
		String XMLfolderPath = base + "\\bugXML\\" + project + "_" + version + ".xml";

		new BugExtractorForBLRepo().extractBugReports(XMLfolderPath, base,
				base + "\\data\\goldset" + corpus + project + version + ".txt");
		// new BugExtractorForBLRepo().createGodSets(XMLfolderPath, outputBaseGoldSet);

	}

	public void WriteContent(HashMap<String, String> bugInfoHM, HashMap<String, ArrayList<String>> bugFixInfoHM,
			String bugFolder, String gitFileadddress) {

		ArrayList<String> list = new ArrayList<>();
		for (String bugID : bugInfoHM.keySet()) {
			String outFile = bugFolder + "\\data\\BugDataExtracted\\" + bugID + ".txt";
			ContentWriter.writeContent(outFile, bugInfoHM.get(bugID));
		}

		String gitContent = "";
		for (String bugID : bugFixInfoHM.keySet()) {
			gitContent = gitContent + bugID + " " + bugFixInfoHM.get(bugID).size() + "\n";
			ArrayList<String> listofFiles = bugFixInfoHM.get(bugID);
			for (int i = 0; i < listofFiles.size(); i++) {
				gitContent = gitContent + listofFiles.get(i) + "\n";
			}
		}
		ContentWriter.writeContent(gitFileadddress, gitContent);
	}

	protected void extractBugReports(String XMLfolderPath, String bugFolder, String gitFileadddress) {
		try {
			HashMap<String, Long> dateInforMillis = new HashMap<>();
			HashMap<String, String> bugInfoHM = new HashMap<>();
			HashMap<String, ArrayList<String>> bugFixInfoHM = new HashMap<>();

			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(new File(XMLfolderPath));

			// normalize text representation
			doc.getDocumentElement().normalize();
			ArrayList<String> allBugIDs = new ArrayList<>();
			NodeList listOfBugs = doc.getElementsByTagName("bug");
			int totalBug = listOfBugs.getLength();
			System.out.println("Total Bug: " + totalBug);

			for (int i = 0; i < listOfBugs.getLength(); i++) {

				Node nNode = listOfBugs.item(i);
				// System.out.println(nNode.getNodeName());
				String bugID = "";
				String bugContent = "";
				ArrayList<String> listOfFiles = new ArrayList<>();
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;
					// System.out.println(eElement.getAttribute("id"));
					bugID = eElement.getAttribute("id");
					allBugIDs.add(bugID);
					String opendate = eElement.getAttribute("opendate");

					int bugInfo = eElement.getElementsByTagName("buginformation").getLength();

					if (bugInfo > 0) {
						String summary = eElement.getElementsByTagName("summary").item(0).getTextContent();
						// System.out.println(summary);
						bugContent = bugContent + summary + "\n";
						String description = eElement.getElementsByTagName("description").item(0).getTextContent();
						// System.out.println(description);
						bugContent = bugContent + description;

						// System.out.println(bugID+"\n"+bugContent);
						bugInfoHM.put(bugID, bugContent);
					}

					NodeList fixfiles = eElement.getElementsByTagName("fixedFiles");
					NodeList fixfilesChildren = fixfiles.item(0).getChildNodes();

					for (int k = 0; k < fixfilesChildren.getLength(); k++) {
						Node file = fixfilesChildren.item(k);
						// Only want stuff from ELEMENT nodes
						if (file.getNodeType() == Node.ELEMENT_NODE) {
							// System.out.println(file.getNodeName()+": "+file.getTextContent());
							listOfFiles.add(file.getTextContent());
						}
					}

					bugFixInfoHM.put(bugID, listOfFiles);
				}
			}
			MiscUtility.showResult(10, bugInfoHM);
			MiscUtility.showResult(90, bugFixInfoHM);

			WriteContent(bugInfoHM, bugFixInfoHM, bugFolder, gitFileadddress);
			ContentWriter.writeContent(bugFolder + "/data/BugIDs.txt", allBugIDs);
		} catch (Exception exc) {
			exc.printStackTrace();
		}
	}

}
