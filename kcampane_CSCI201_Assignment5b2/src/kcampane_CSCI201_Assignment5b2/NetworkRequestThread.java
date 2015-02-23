package kcampane_CSCI201_Assignment5b2;

import java.awt.BorderLayout;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.JLabel;

public class NetworkRequestThread extends Thread{
	OrderForm of;
	public NetworkRequestThread(OrderForm of){
		this.of = of;
	}
	public void  run(){
		makeRPC();
	}
	public void makeRPC(){
		try{
			File f = new File("example.rcp");
			String rcp ="";
			FileWriter fw = new FileWriter(f);
			PrintWriter pw = new PrintWriter(fw);
			if(!of.item.getText().equals("") && !of.cost.getText().equals("")){
				rcp += "["+of.item.getText()+":$"+of.cost.getText()+"]\n";
				pw.println("["+of.item.getText()+":$"+of.cost.getText()+"]");
			}
			if(!of.wood.getText().equals("")){
				rcp += "[Wood:"+of.wood.getText()+"]\n";
				pw.println("[Wood:"+of.wood.getText()+"]");
			}
			if(!of.plastic.getText().equals("")){
				rcp += "[Plastic:"+of.plastic.getText()+"]\n";
				pw.println("[Plastic:"+of.plastic.getText()+"]");
			}
			if(!of.metal.getText().equals("")){
				rcp += "[Metal:"+of.metal.getText()+"]\n";
				pw.println("[Metal:"+of.metal.getText()+"]");
			}			
			for(InstructionPanel ip: of.instructions){
				rcp += "[Use ";
				pw.print("[Use ");
				if(!ip.numUse[0].getText().equals("") && !ip.numUse[1].getText().equals("")){
					rcp += ip.numUse[0].getText()+"x "+of.tools[ip.toolboxes[0].getSelectedIndex()]+" and ";
					rcp += ip.numUse[1].getText()+"x "+of.tools[ip.toolboxes[1].getSelectedIndex()]+ " at ";
					pw.print(ip.numUse[0].getText()+"x "+of.tools[ip.toolboxes[0].getSelectedIndex()]+" and ");
					pw.print(ip.numUse[1].getText()+"x "+of.tools[ip.toolboxes[1].getSelectedIndex()]+ " at ");
				}
				else if(!ip.numUse[0].getText().equals("")){
					rcp += ip.numUse[0].getText()+"x "+of.tools[ip.toolboxes[0].getSelectedIndex()]+ " at ";
					pw.print(ip.numUse[0].getText()+"x "+of.tools[ip.toolboxes[0].getSelectedIndex()]+ " at ");
				}
				else if(!ip.numUse[1].getText().equals("")){
					rcp += ip.numUse[1].getText()+"x "+of.tools[ip.toolboxes[1].getSelectedIndex()]+ " at ";
					pw.print(ip.numUse[1].getText()+"x "+of.tools[ip.toolboxes[1].getSelectedIndex()]+ " at ");
				}		
				rcp += of.machines[ip.machineBoxes.getSelectedIndex()]+" for "+ip.seconds.getText()+"s]\n";
				pw.println(of.machines[ip.machineBoxes.getSelectedIndex()]+" for "+ip.seconds.getText()+"s]");
			}
			fw.close();
			pw.close();
			
			//connect to server and write file
			String hostname = "127.0.0.1";
			int port = 5001;
			Socket s = new Socket(hostname, port);
			InputStream is = s.getInputStream();
			OutputStream os = s.getOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(os);
			oos.writeObject(rcp);
			oos.flush();
			
				
			//wait for response
			ObjectInputStream ois = new ObjectInputStream(is);
			String response = (String) ois.readObject();
			of.feedBackString = response;
			of.checkRequest();
			if(response.equals("accept")){
				//keep listening
				of.imageURL = (String) ois.readObject();
				of.showCompletedImage();
			}
			else{}//do nothing
			System.out.println("closing streams");
			ois.close();
			oos.close();
		}
		catch(FileNotFoundException fnfe){}
		catch(IOException ioe){} 
		catch (ClassNotFoundException e) {}
	}
}
