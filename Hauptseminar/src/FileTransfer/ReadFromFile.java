package FileTransfer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;

import Model.Element;
import Model.Graph;

public class ReadFromFile {

	private LinkedList<String> finalList = new LinkedList<String>();
	
	/**
	 * Method to get Text from data on Desktop
	 * Path for windows:
	 * System.getProperty("user.home")+"/Desktop"
	 * @param fileName name of file to read from
	 * @return
	 * @throws IOException
	 */
	public LinkedList<String> readIn() throws IOException{
		BufferedReader reader = new BufferedReader(new FileReader((System.getProperty("user.home")+"/Desktop/test.txt")));
		
		LinkedList<String> tempList = new LinkedList<String>();
		String temp = "";
		
		try{
			while((temp = reader.readLine()) != null)
				tempList.add(temp);
		}
		catch(IOException e){}

		splitList(tempList);
		
		return finalList;
	}
	
	/***
	 * Splits a List of Strings at every "."
	 * @param tempList list to split
	 */
	public void splitList(LinkedList<String>tempList){
		for(int i = 0; i<tempList.size();i++){
			String [] tempArray = tempList.get(i).split("\\.");
			for(int j = 0; j<tempArray.length;j++){
				String tempString = "";
				if(tempArray[j].substring(0, 1).equals(" "))
					tempString = tempArray[j].substring(1,tempArray[j].length());
				else
					tempString = tempArray[j];
				finalList.add(tempString);
			}
		}
	}
	
	/*public static void main(String [] args) throws IOException{
		ReadFromFile r = new ReadFromFile();
		LinkedList<String> t = r.readIn();
		
		System.out.println(r.finalList.size());
		
		WriteInTxt w = new WriteInTxt();
		w.write(r.finalList);
		
		Graph g = new Graph();
		
		Element[] e = new Element[t.size()];
		
		for(int i = 0; i<e.length;i++){
			e[i] = new Element(t.get(i), i+1);
		}
		
		g.setHead(e[0]);
		
		g.getHead().addNeighbour(e[1]);
		g.getHead().addNeighbour(e[2]);
		
		g.getHead().getChildren().get(0).addNeighbour(e[3]);
		g.getHead().getChildren().get(1).addNeighbour(e[4]);
		
		g.getHead().getChildren().get(0).getChildren().get(0).addNeighbour(g.getHead());
		
		System.out.println(g.searchNodeByString("Mensch").getNounPhrase());
		
		System.out.println(g.searchNodeByElement(g.getHead().getChildren().get(0)).getNounPhrase());
	}*/
	
	public static void main (String [] args){
		Element e1 = new Element("1", 1);
		Element e2 = new Element("2", 2);
		Element e3 = new Element("3", 3);
		Element e4 = new Element("4", 4);
		Element e5 = new Element("5", 5);
		Element e6 = new Element("6", 6);
		
		Graph g = new Graph();
		
		g.setHead(e1);
	
		g.linkElements(e1, e2);
		g.linkElements(e1, e3);
		
		g.linkElements(e2, e3);
		g.linkElements(e2, e4);
		g.linkElements(e2, e6);
		
		g.linkElements(e3, e4);
		g.linkElements(e3, e5);
		g.linkElements(e3, e6);
		
		g.linkElements(e4, e5);
		g.linkElements(e4, e6);
	}
}
