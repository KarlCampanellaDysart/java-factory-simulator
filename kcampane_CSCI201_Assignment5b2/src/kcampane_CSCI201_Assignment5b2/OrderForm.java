package kcampane_CSCI201_Assignment5b2;
import java.awt.BorderLayout;
import java.awt.Image;

import org.json.*;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.Border;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class OrderForm extends JFrame{
	String tools[]={"Screwdrivers","Hammers","Paintbrushes","Pliers","Scissors"};
	String machines[]={"Anvil","Workbench","Furnace","Saw","Painting Station","Press"};
	String feedBackString = "";
	String imageURL="";
	int indexImage = 0;

	JPanel mainPanel;
	JPanel descriptionPanel;
	
	JLabel waiting = new JLabel("Waiting for response...");
	JLabel acc = new JLabel("Accepted!");
	JLabel dec = new JLabel("Declined!");
	JLabel picLabel;
	
	JButton addInstruction = new JButton("+");
	JButton minusInstruction = new JButton("-");
	JButton sendRequest = new JButton("Send Request");
	JButton back = new JButton("Back");
	JButton done = new JButton("Done");
	
	JTextField item = new JTextField();
	JTextField cost = new JTextField();
	JTextField wood = new JTextField();
	JTextField plastic = new JTextField();
	JTextField metal = new JTextField();
	
	NetworkRequestThread nrt = new NetworkRequestThread(this);
	ArrayList<InstructionPanel> instructions = new ArrayList<InstructionPanel>();
	public OrderForm() {
		mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		descriptionPanel = new JPanel();
		//JScrollPane jsp = new JScrollPane();
		
		//jsp.add(mainPanel);
		ButtonListener b = new ButtonListener();
		addInstruction.addActionListener(b);
		minusInstruction.addActionListener(b);
		sendRequest.addActionListener(b);
		back.addActionListener(b);
		done.addActionListener(b);
		
		setupDescriptionPanel();
		
		add(mainPanel, BorderLayout.CENTER);
		add(descriptionPanel, BorderLayout.NORTH);
		add(sendRequest, BorderLayout.SOUTH);
		mainPanel.setVisible(true);
		descriptionPanel.setVisible(true);;
		sendRequest.setVisible(true);
	}
	
	private void setupDescriptionPanel() {
		descriptionPanel.setLayout(new BoxLayout(descriptionPanel, BoxLayout.X_AXIS));
		
		JPanel itemCost = new JPanel();
		itemCost.setLayout(new BoxLayout(itemCost, BoxLayout.Y_AXIS));
		JPanel itemPanel = new JPanel();
		itemPanel.setLayout(new BoxLayout(itemPanel, BoxLayout.X_AXIS));
		itemPanel.add(new JLabel("Item:"));
		itemPanel.add(item);
		JPanel costPanel = new JPanel();
		costPanel.setLayout(new BoxLayout(costPanel, BoxLayout.X_AXIS));
		costPanel.add(new JLabel("Cost:"));
		costPanel.add(cost);
		
		itemCost.add(itemPanel);
		itemCost.add(costPanel);
		descriptionPanel.add(itemCost);
		
		JPanel materials = new JPanel();
		materials.setLayout(new BoxLayout(materials, BoxLayout.Y_AXIS));
		JPanel woodPanel = new JPanel();
		woodPanel.setLayout(new BoxLayout(woodPanel, BoxLayout.X_AXIS));
		woodPanel.add(new JLabel("Wood:"));
		woodPanel.add(wood);
		JPanel plasticPanel = new JPanel();
		plasticPanel.setLayout(new BoxLayout(plasticPanel, BoxLayout.X_AXIS));
		plasticPanel.add(new JLabel("Plastic:"));
		plasticPanel.add(plastic);
		JPanel metalPanel = new JPanel();
		metalPanel.setLayout(new BoxLayout(metalPanel, BoxLayout.X_AXIS));
		metalPanel.add(new JLabel("Metal:"));
		metalPanel.add(metal);
		
		materials.add(new JLabel("Materials"));
		materials.add(woodPanel);
		materials.add(plasticPanel);
		materials.add(metalPanel);
		descriptionPanel.add(materials);
		
		JPanel buttons = new JPanel();
		buttons.setLayout(new BoxLayout(buttons, BoxLayout.Y_AXIS));
		buttons.add(addInstruction);
		buttons.add(minusInstruction);
		descriptionPanel.add(buttons);
	}
	
	public void accepted(){
		remove(waiting);
		add(acc, BorderLayout.NORTH);
		repaint();
		revalidate();
	}
	
	public void declined(){
		remove(waiting);
		add(dec, BorderLayout.NORTH);
		add(back, BorderLayout.SOUTH);
		repaint();
		revalidate();
	}
	
	public void makeNewInstruction(){
		InstructionPanel ip = new InstructionPanel(this);
		instructions.add(ip);
		mainPanel.add(ip);
		repaint();
		revalidate();
	}
	
	public void deleteInstruction(){
		mainPanel.remove(instructions.get(instructions.size()-1));
		instructions.remove(instructions.size()-1);
		repaint();
		revalidate();
	}
	
	public void sendRequest(){
		remove(mainPanel);
		remove(descriptionPanel);
		remove(sendRequest);
		add(waiting, BorderLayout.NORTH);
		repaint();
		revalidate();
		nrt.start();
	}
	
	public void checkRequest(){
		if(feedBackString.equals("accept")){
			accepted();		
		}
		else if(feedBackString.equals("decline")){
			declined();
		}
		else{
			System.out.println("feedback:" + feedBackString);
		}
	}
	public void backPressed(){
		back.setVisible(false);
		acc.setVisible(false);
		dec.setVisible(false);
		remove(back);
		remove(acc);
		remove(dec);
		add(mainPanel, BorderLayout.CENTER);
		add(descriptionPanel, BorderLayout.NORTH);
		add(sendRequest, BorderLayout.SOUTH);
	}
	
	public void donePressed(){
		remove(picLabel);
		remove(done);
		add(mainPanel, BorderLayout.CENTER);
		add(descriptionPanel, BorderLayout.NORTH);
		add(sendRequest, BorderLayout.SOUTH);
	}
	
	public void showCompletedImage(){
		String jsonStream="";
		URL url = null;
		Scanner s = null;
		try {
			url = new URL(imageURL);
			s = new Scanner(url.openStream());
			while(s.hasNext()){
				jsonStream += s.next()+ " ";
			}
			JSONObject jobject = new JSONObject(jsonStream);
			JSONObject data = (JSONObject) jobject.get("responseData");
			JSONArray results = (JSONArray) data.get("results");
			JSONObject result = (JSONObject) results.getJSONObject(indexImage);
			String imageLink = (String) result.get("url");
			System.out.println(imageLink);
			BufferedImage image = null;
			URL urlImage = new URL(imageLink);
			image = ImageIO.read(urlImage.openStream());
			Image newimg = image.getScaledInstance(200, 200,  java.awt.Image.SCALE_SMOOTH);
			ImageIcon tempImage = new ImageIcon(newimg);
			picLabel = new JLabel(tempImage);
			
			remove(acc);
			add(picLabel, BorderLayout.CENTER);
			add(done, BorderLayout.SOUTH);
			
			repaint();
			revalidate();
		} 
		catch (MalformedURLException e) {} 
		catch (IOException e) {
			System.out.println("IO ERROR: "+e.getMessage());
			indexImage++;
			showCompletedImage();
		}
		catch (JSONException e) {
			System.out.print("JSON ERROR");
		}
	}
	
	public static void main(String []args){
		OrderForm of = new OrderForm();
		of.setVisible(true);
		of.setDefaultCloseOperation(EXIT_ON_CLOSE);
		of.setSize(500,400);
	}
	public class ButtonListener implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			if(e.getSource() == addInstruction){
				makeNewInstruction();
			}
			if(e.getSource() == minusInstruction){
				deleteInstruction();
			}
			if(e.getSource() == sendRequest){
				sendRequest();
			}
			if(e.getSource() == back){
				backPressed();
			}
			if(e.getSource() == done){
				donePressed();
			}
		}	
	}
}
