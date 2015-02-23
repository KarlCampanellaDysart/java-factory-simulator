
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

public class MainPanel extends JPanel
{
	FactoryGUI fg;
	Vector<Worker> workers = new Vector<Worker>();
	boolean storeVisable = false;
	boolean ordersVisible = false;
	
	//store buttons
	JButton store;
	JButton buys[];
	JButton sells[];
	JButton hire;
	JButton fire;
	
	//orders
	JButton orders;
	
	int buyPrices[] = {10,12,5,11,9,1,3,2};
	int sellPrices[] = {7,9,3,9,7,1,2,1};
	int hireAndFirePrice = 15;
	
	//images
	BufferedImage imgWood = null;
	BufferedImage imgMetal = null;
	BufferedImage imgPlastic = null;
	BufferedImage imgScrewDriver = null;
	BufferedImage imgHammer = null;
	BufferedImage imgPaintbrush = null;
	BufferedImage imgPliers = null;
	BufferedImage imgScissors = null;
	BufferedImage imgAnvil = null;
	BufferedImage imgWorkBench = null;
	BufferedImage imgFurnace = null;
	BufferedImage imgTableSaw = null;
	BufferedImage imgPaintingStation = null;
	BufferedImage imgPress = null;
	BufferedImage imgWorker = null;
	
	//orders
	JPanel ordersPanel;
	
	public MainPanel(FactoryGUI fg, FileParser fp)
	{
		setLayout(null);
		this.fg = fg;
		loadImages();
		setUpButtons();
		setupOrdersPanel();
	}
	public void setupOrdersPanel(){
		ordersPanel = new JPanel();
		ordersPanel.setVisible(false);
		ordersPanel.setBounds(0,20, 600,600);
		ordersPanel.setLayout(new BoxLayout(ordersPanel, BoxLayout.Y_AXIS));
		add(ordersPanel);
	}
	public void setUpButtons(){
		ButtonListener b = new ButtonListener();
		store = new JButton("Store");
		store.setBounds(90, 3, 50, 20);
		store.setVisible(true);
		store.addActionListener(b);
		add(store);
		
		buys = new JButton[8];
		sells = new JButton[8];
		hire = new JButton("hire");
		hire.setBounds(400, 450, 50, 20);
		hire.setVisible(true);
		hire.addActionListener(b);
		add(hire);
		fire = new JButton("fire");
		fire.setBounds(400, 485, 50, 20);
		fire.setVisible(true);
		fire.addActionListener(b);
		add(fire);
		for(int i =0;i<8;i++){
			buys[i] = new JButton("Buy");
			buys[i].setBounds(((i/5)*300)+100, ((i%5)*100)+50, 50, 30);
			buys[i].setVisible(true);
			buys[i].addActionListener(b);
			add(buys[i]);
			
			sells[i] = new JButton("Sell");
			sells[i].setBounds(((i/5)*300)+100, ((i%5)*100)+85, 50, 30);
			sells[i].setVisible(true);
			sells[i].addActionListener(b);
			add(sells[i]);
		}
		
		orders = new JButton("Orders");
		orders.setBounds(500, 3, 50, 20);
		orders.setVisible(true);
		orders.addActionListener(b);
		add(orders);
	}
	public void loadImages(){
		try {
		    imgWood = ImageIO.read(new File("assignment5images/wood.png"));
		    imgMetal = ImageIO.read(new File("assignment5images/metal.png"));
		    imgPlastic = ImageIO.read(new File("assignment5images/plastic.png"));
		} catch (IOException e) {}
		try {
		    imgScrewDriver = ImageIO.read(new File("assignment5images/screwdriver.png"));
		    imgHammer = ImageIO.read(new File("assignment5images/hammer.png"));
		    imgPaintbrush = ImageIO.read(new File("assignment5images/paintbrush.png"));
		    imgPliers = ImageIO.read(new File("assignment5images/pliers.png"));
		    imgScissors = ImageIO.read(new File("assignment5images/scissors.png"));
		} catch (IOException e) {}
		try {
		    imgAnvil = ImageIO.read(new File("assignment5images/anvil.png"));
		    imgWorkBench = ImageIO.read(new File("assignment5images/workbench.png"));
		    imgFurnace = ImageIO.read(new File("assignment5images/furnace.png"));
		    imgTableSaw = ImageIO.read(new File("assignment5images/tablesaw.png"));
		    imgPaintingStation = ImageIO.read(new File("assignment5images/paintingstation.png"));
		    imgPress = ImageIO.read(new File("assignment5images/press.png"));
		} catch (IOException e) {}
		try {
		    imgWorker = ImageIO.read(new File("assignment5images/worker.png"));
		} catch (IOException e) {}
	}
	public void addWorkers(int numWorkers)
	{
		int i =0;	
		while(i<numWorkers && i<fg.taskBoardLabels.length)
		{
			Worker w = new Worker(fg, 0, 0, i);
			w.setVisible(true);
			w.setLocation(5, 5);
			add(w);
			Thread t = new Thread(w);
			t.start();
			i++;
		}
	}
	
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		int startx = 0;
		int starty = 0;
		
		if(storeVisable){
			fg.taskBoard.setVisible(false);
			orders.setVisible(false);
			ordersPanel.setVisible(false);
			for(int i =0;i<8;i++){
				buys[i].setVisible(true);
				sells[i].setVisible(true);
				g.drawString("$"+buyPrices[i],((i/5)*300)+80, ((i%5)*100)+70 );
				g.drawString("$"+sellPrices[i],((i/5)*300)+80, ((i%5)*100)+105 );
			}
			
			fire.setVisible(true);
			hire.setVisible(true);
			g.drawString("$"+hireAndFirePrice, 365, 465);
			g.drawString("$"+hireAndFirePrice, 365, 500);
			
			startx = 315;
			starty = 65;
			
			g.drawString("wood", startx, starty -20);
			g.drawString("metal", startx, starty +100-20);
			g.drawString("plastic", startx , starty +200-20);
		
			g.drawImage(imgWood, startx-10, starty-10,null);
			g.drawImage(imgMetal, startx-10, starty+100-10,null);		
			g.drawImage(imgPlastic, startx-10, starty+200-10,null);
									
			g.setColor(Color.black);
			g.drawString(""+fg.materialsMap.get("Wood"), startx+10, starty+20);
			g.drawString(""+fg.materialsMap.get("Metal"), startx+10, starty+100+20);
			g.drawString(""+fg.materialsMap.get("Plastic"), startx+10, starty+200+20);	
			
			g.drawImage(imgWorker, startx-10, starty+400-10,null);
			g.drawString(fg.workers+"", startx+10, starty+400+50);	

			startx = 15;
			starty = 65;
			
			g.drawString("Screwdriver", startx, starty-20);
			g.drawString("Hammer", startx, starty + 100 -20);
			g.drawString("Paintbrush", startx, starty + 200 -20);
			g.drawString("Pliers", startx, starty + 300 - 20);
			g.drawString("Scissors", startx, starty + 400 -20);
			
			g.drawImage(imgScrewDriver, startx-10, starty-10,null);
			g.drawImage(imgHammer, startx-10, starty+100-10,null);		
			g.drawImage(imgPaintbrush, startx-10, starty+200-10,null);
			g.drawImage(imgPliers, startx-10, starty+300-10,null);		
			g.drawImage(imgScissors, startx-10, starty+400-10,null);
			
			g.drawString(fg.materialsMap.get("Screwdriver")+"", startx+10, starty+20);
			g.drawString(fg.materialsMap.get("Hammer")+"", startx+10, starty+100+20);
			g.drawString(fg.materialsMap.get("Paintbrush")+"", startx+10, starty+200+20);
			g.drawString(fg.materialsMap.get("Plier")+"", startx+10, starty+300+20);
			g.drawString(fg.materialsMap.get("Scissor")+"", startx+10, starty+400+20);
			
			//money
			g.setColor(Color.black);
			g.drawString("Money: $", 5,18);
			g.drawString(fg.curMoney+"", 62, 18);
		}
		else{
			if(ordersVisible){
				fg.taskBoard.setVisible(false);
				orders.setVisible(false);
				//store.setVisible(false);
				for(int i =0;i<8;i++){
					buys[i].setVisible(false);
					sells[i].setVisible(false);
					fire.setVisible(false);
					hire.setVisible(false);
				}
				ordersPanel.setVisible(true);
				
				//money
				g.setColor(Color.black);
				g.drawString("Money: $", 5,18);
				g.drawString(fg.curMoney+"", 62, 18);
			}
			else{
				fg.taskBoard.setVisible(true);
				orders.setVisible(true);
				ordersPanel.setVisible(false);
				//store.setVisible(true);
				for(int i =0;i<8;i++){
					buys[i].setVisible(false);
					sells[i].setVisible(false);
					fire.setVisible(false);
					hire.setVisible(false);
				}
				
				startx = 165;
				starty = 65;
				
				g.drawString("wood", startx, starty - 20);
				g.drawString("metal", startx+100, starty - 20);
				g.drawString("plastic", startx+200, starty - 20);
			
				g.drawImage(imgWood, startx-10, starty-10,null);
				g.drawImage(imgMetal, startx+100-10, starty-10,null);		
				g.drawImage(imgPlastic, startx+200-10, starty-10,null);
										
				//numbers
				g.setColor(Color.black);
				g.drawString(""+fg.materialsMap.get("Wood"), startx+10, starty+20);
				g.drawString(""+fg.materialsMap.get("Metal"), startx+100+10, starty+20);
				g.drawString(""+fg.materialsMap.get("Plastic"), startx+200+10, starty+20);
				
				startx = 15;
				starty = 65;
				
				g.drawString("Screwdriver", startx, starty-20);
				g.drawString("Hammer", startx, starty + 100 -20);
				g.drawString("Paintbrush", startx, starty + 200 -20);
				g.drawString("Pliers", startx, starty + 300 - 20);
				g.drawString("Scissors", startx, starty + 400 -20);
				
				g.drawImage(imgScrewDriver, startx-10, starty-10,null);
				g.drawImage(imgHammer, startx-10, starty+100-10,null);		
				g.drawImage(imgPaintbrush, startx-10, starty+200-10,null);
				g.drawImage(imgPliers, startx-10, starty+300-10,null);		
				g.drawImage(imgScissors, startx-10, starty+400-10,null);
				
				g.drawString(fg.materialsMap.get("Screwdriver")+"/"+fg.screwDriver, startx+4, starty+20);
				g.drawString(fg.materialsMap.get("Hammer")+"/"+fg.hammer, startx+4, starty+100+20);
				g.drawString(fg.materialsMap.get("Paintbrush")+"/"+fg.paintBrush, startx+4, starty+200+20);
				g.drawString(fg.materialsMap.get("Plier")+"/"+fg.pliers, startx+4, starty+300+20);
				g.drawString(fg.materialsMap.get("Scissor")+"/"+fg.scissors, startx+4, starty+400+20);
				
				startx = 115;
				starty = 155;
				
				g.setColor(Color.black);
				g.drawImage(imgAnvil, startx-10, starty,null);
				g.drawImage(imgAnvil, startx-10 + 100, starty,null);
				g.drawString("Anvils", startx+45, starty+70);
				
				for(int i =0;i<2;i++){
					if(fg.machineMap.get("Anvil")[i].equals("Open")){g.setColor(Color.green);}
					else{g.setColor(Color.red);}
					g.drawString(fg.machineMap.get("Anvil")[i], (startx+i*100), starty-5);
				}
				
				g.setColor(Color.black);
				g.drawImage(imgWorkBench, startx-10 +200, starty,null);	
				g.drawImage(imgWorkBench, startx-10 +300, starty,null);		
				g.drawImage(imgWorkBench, startx-10 +400, starty,null);	
				g.drawString("Workbenches", startx+270, starty+70);
				
				for(int i =0;i<3;i++){
					if(fg.machineMap.get("Workbench")[i].equals("Open")){g.setColor(Color.green);}
					else{g.setColor(Color.red);}
					g.drawString(fg.machineMap.get("Workbench")[i], 200+(startx+i*100), starty-5);
				}
				
				starty = 305;
				
				g.setColor(Color.black);
				g.drawImage(imgFurnace, startx-10, starty,null);
				g.drawImage(imgFurnace, startx+100-10, starty,null);
				g.drawString("Furnace", startx+45, starty+70);
				
				for(int i =0;i<2;i++){
					if(fg.machineMap.get("Furnace")[i].equals("Open")){g.setColor(Color.green);}
					else{g.setColor(Color.red);}
					g.drawString(fg.machineMap.get("Furnace")[i], (startx+i*100), starty-5);
				}
				
				g.setColor(Color.black);
				g.drawImage(imgTableSaw, startx+200-10, starty,null);
				g.drawImage(imgTableSaw, startx+300-10, starty,null);
				g.drawImage(imgTableSaw, startx+400-10, starty,null);
				g.drawString("Table Saws", startx+270, starty+70);
				
				for(int i =0;i<3;i++){
					if(fg.machineMap.get("Saw")[i].equals("Open")){g.setColor(Color.green);}
					else{g.setColor(Color.red);}
					g.drawString(fg.machineMap.get("Saw")[i], 200+(startx+i*100), starty-5);
				}
				
				starty = 455;
				
				g.setColor(Color.black);
				g.drawImage(imgPaintingStation, startx-10, starty,null);
				g.drawImage(imgPaintingStation, startx+100-10, starty,null);
				g.drawImage(imgPaintingStation, startx+200-10, starty,null);
				g.drawImage(imgPaintingStation, startx+300-10, starty,null);
				g.drawString("Painting Stations", startx+120, starty+70);
				
				for(int i =0;i<4;i++){
					if(fg.machineMap.get("Painting Station")[i].equals("Open")){g.setColor(Color.green);}
					else{g.setColor(Color.red);}
					g.drawString(fg.machineMap.get("Painting Station")[i], (startx+i*100), starty-5);
				}
			
				g.setColor(Color.black);
				g.drawImage(imgPress, startx+400-10, starty,null);
				g.drawString("Press", startx+395, starty+70);
				if(fg.machineMap.get("Press")[0].equals("Open")){g.setColor(Color.green);}
				else{g.setColor(Color.red);}
				g.drawString(fg.machineMap.get("Press")[0], 400+(startx), starty-5);
			
				//money
				g.setColor(Color.black);
				g.drawString("Money: $", 5,18);
				g.drawString(fg.curMoney+"", 62, 18);
			}
			
		}
	}
	public class ButtonListener implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			if(e.getSource() == store){
				if(storeVisable){
					storeVisable = false;
					store.setText("Store");
				}
				else if(ordersVisible)
				{
					ordersVisible = false;
					store.setText("Store");
				}
				else{
					storeVisable = true;
					store.setText("Back");
				}
				repaint();
				revalidate();
			}
			else if(e.getSource() == orders)
			{		
				ordersVisible = true;
				store.setText("Back");
				repaint();
				revalidate();
			}
			else{
				for(int i =0;i<8;i++){
					if(e.getSource() == buys[i]){
						if(fg.curMoney-buyPrices[i]>=0){
							if(i == 0){fg.curScrewDriver++;fg.curMoney -= buyPrices[i];}
							else if(i == 1){fg.curHammer++;fg.curMoney -= buyPrices[i];}
							else if(i == 2){fg.curPaintBrush++;fg.curMoney -= buyPrices[i];}
							else if(i == 3){fg.curPliers++;fg.curMoney -= buyPrices[i];}
							else if(i == 4){fg.curScissors++;fg.curMoney -= buyPrices[i];}
							else if(i == 5){fg.wood++;fg.curMoney -= buyPrices[i];}
							else if(i == 6){fg.metal++;fg.curMoney -= buyPrices[i];}
							else if(i == 7){fg.plastic++;fg.curMoney -= buyPrices[i];}
						}
					}
					if(e.getSource() == sells[i]){						
						if(i == 0 && fg.curScrewDriver>0){fg.curScrewDriver++;fg.curMoney += sellPrices[i];}
						else if(i == 1 && fg.curHammer>0){fg.curHammer--;fg.curMoney += sellPrices[i];}
						else if(i == 2 && fg.curPaintBrush>0){fg.curPaintBrush--;fg.curMoney += sellPrices[i];}
						else if(i == 3 && fg.curPliers>0){fg.curPliers--;fg.curMoney += sellPrices[i];}
						else if(i == 4 && fg.curScissors>0){fg.curScissors--;fg.curMoney += sellPrices[i];}
						else if(i == 5 && fg.wood>0){fg.wood--;fg.curMoney += sellPrices[i];}
						else if(i == 6 && fg.metal>0){fg.metal--;fg.curMoney += sellPrices[i];}
						else if(i == 7 && fg.plastic>0){fg.plastic--;fg.curMoney += sellPrices[i];}
					}
				}
				if(e.getSource() == hire){
					if(fg.curMoney-hireAndFirePrice>=0){
						fg.curMoney -= hireAndFirePrice;
						fg.workers++;
					}	
				}
				if(e.getSource() == fire){
					if(fg.workers>0){
						fg.curMoney += hireAndFirePrice;
						fg.workers--;
					}
				}
				fg.readdMap();
			}
			repaint();
			revalidate();
		}
		
	}
}
