package FileTransfer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;

public class WriteInTxt {

	/**
	 * Write in a txt-data on Desktop.
	 * @param list
	 */
	public void write(LinkedList<String> list){
		BufferedWriter writer = null;
		
		try{
			writer = new BufferedWriter(new FileWriter(System.getProperty("user.home")+"/Desktop/test2.txt"));
			
			for(int i = 0; i<list.size();i++){
				writer.write(list.get(i) + ". \n");
			}
		}
		catch(IOException e){}
		
		finally{
			try{
				if(writer != null)
					writer.close();
			}
			catch(IOException e){};
		}
	}
}
