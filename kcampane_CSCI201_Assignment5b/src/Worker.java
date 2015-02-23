
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class Worker extends JPanel implements Runnable  
{
	int posx;
	int posy;
	int pixelx;
	int pixely;
	int destinationx;
	int destinationy;
	FactoryGUI fg;
	BufferedImage imgWorker = null;
	
	static int ROW_NUM = 11;
	static int COL_NUM = 11;
	int prevMove;
	
	boolean bounds[];
	boolean needNextInstruction;
	boolean atDestination;
	boolean grabbedTask;
	boolean gettingMaterials;
	boolean gettingTools;
	boolean usingMachine;
	boolean puttingToolsBack;
	boolean waitingForMachine;
	boolean finishedTask;
	boolean done;
	int previousTask;
	boolean workerVisible;
	
	boolean threadTerminated;
	
	RCPObject rcp;
	
	Instructions curInstruction;
	int instructionPointer=0;
	
	int rcpWood;
	int rcpMetal;
	int rcpPlastic;
	Map<String, Integer> materialMap;
	
	int toolPointer;
	int machinePointer;
	Tool curTool;
	
	int id;
	
	public Worker(FactoryGUI fg, int startx, int starty, int id) 
	{
		this.fg = fg;
		this.posx = startx;
		this.posy = starty;
		this.id = id;
		
		init();
	}
	public void init()
	{
		pixelx = 0;
		pixely = 0;
		prevMove = 1;
		
		needNextInstruction =true;
		bounds = new boolean[4];
		grabbedTask = false;
		gettingMaterials = true;
		atDestination = false;		
		gettingTools = false;
		usingMachine = false;
		puttingToolsBack = false;
		finishedTask=false;
		done=false;
		workerVisible = true;
		
		threadTerminated = false;
		
		materialMap = new HashMap<String, Integer>();
		
		setLayout(null);
		setSize(550,550);
		setOpaque(false);
		
		try {
		    imgWorker = ImageIO.read(new File("assignment5images/worker.png"));
		} catch (IOException e) {}

		goBackToTaskBoard();
	}
	
	public void setWorkerDestination(int i, int j)
	{
		destinationx =i;
		destinationy =j;
	}
	public void setWorkerDestination(int i)
	{
		destinationx = (i%COL_NUM);
		destinationy = (i/COL_NUM);
	}
	public void goBackToTaskBoard()
	{
		setWorkerDestination(10,0);
	}
	public void run() 
	{
		while(true)
		{
			if(threadTerminated)
			{
				break;
			}
			else
			{
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if(done)
				{
					//do nothing
					workerVisible = false;
					boolean allTasksComplete = true;
					for(int i=0;i<fg.taskBoardLabels.length;i++)
					{
						if(fg.taskBoardLabels[i].getText().equals(fg.allRecipes[i].name+"...In Process"))
						{
							allTasksComplete =false;
						}
					}
					if(allTasksComplete && !fg.finishedSimulation)
					{
						fg.setSimulationDone();
						double endTime = System.currentTimeMillis();
						fg.setEndTime(endTime);
					}
					threadTerminated = true;
				}
				else
				{
					if(!grabbedTask)
					{
						if(atDestination){
							goBackToTaskBoard();
							atDestination = false;
						}	
						if(posx!=destinationx || posy!=destinationy)
						{
							moveWorker();		
						}
						else
						{
							grabbedTask =true;
							atDestination = true;
							gettingMaterials = true;
							synchronized(fg)//changing shared data
							{
								if(finishedTask)
								{
									fg.taskBoardText.remove(fg.taskBoardLabels[previousTask]);
									
									Iterator<UpdateRequestsThreadPanel> it= fg.st.allRequests.iterator();
									while(it.hasNext()){
										UpdateRequestsThreadPanel urtp = it.next();
										if(urtp.nameOfItem.equals(fg.allRecipes[previousTask].name)){
											urtp.completedTask = true;
											fg.curMoney += urtp.price;
										}
									}
								}
							}
							if(fg.taskBarPointer<fg.allRecipes.length)
							{
								synchronized(fg)//changing shared data
								{
									previousTask = fg.taskBarPointer;
									fg.taskBoardLabels[fg.taskBarPointer].setText(fg.allRecipes[fg.taskBarPointer].name+"...In Process");
									rcp = fg.allRecipes[fg.taskBarPointer];
									fg.incrementTask();
								}
							}
							else{
								if(fg.taskBarPointer == fg.taskBoardLabels.length)
								{
									done=true;
								}
							}
							//update data based on rcp
							rcpWood = rcp.wood;
							rcpMetal = rcp.metal;
							rcpPlastic = rcp.plastic;
							materialMap.put("Wood", rcpWood);
							materialMap.put("Metal", rcpMetal);
							materialMap.put("Plastic", rcpPlastic);
						}
					}
					else//has task
					{
						if(gettingMaterials)
						{
							if(atDestination)//setDestination
							{
								if(materialMap.get("Wood")!=0)
								{
									int tempDest = retrieveDestination("Wood", false)+1;
									setWorkerDestination(tempDest);
									atDestination = false;
								}
								else if(materialMap.get("Metal")!=0)
								{
									int tempDest = retrieveDestination("Metal", false)+1;
									setWorkerDestination(tempDest);
									atDestination = false;
								}
								else if(materialMap.get("Plastic")!=0)
								{
									int tempDest = retrieveDestination("Plastic", false)+1;
									setWorkerDestination(tempDest);
									atDestination = false;
								}
								else
								{
									gettingMaterials = false;
									needNextInstruction = true;
									instructionPointer = 0;
								}
							}
							if(posx!=destinationx || posy!=destinationy)
							{
								//set position
								moveWorker();
							}
							else//at destination
							{
								synchronized(fg)//changing shared data
								{
									String materials = fg.coordinateMap.get((posy*11+posx)-1);
									fg.getMaterial(materialMap.get(materials), materials);
									materialMap.put(materials, 0);
								}		
								atDestination = true;
							}
						}
						else 
						{
							//start to make recipe
							if(needNextInstruction)
							{
								toolPointer =0;
								curInstruction = rcp.instructions.get(instructionPointer++);
								needNextInstruction=false;
								gettingTools = true;
							}
							
							//getting tools
							if(gettingTools)
							{
								if(atDestination)
								{
									if(toolPointer<curInstruction.tools.size())
									{
										curTool = curInstruction.tools.get(toolPointer++);
										System.out.println(curTool.tool);
										setWorkerDestination(retrieveDestination(curTool.tool, false)+1);
										atDestination =false;
									}
									else
									{
										//got all tools
										gettingTools = false;
										usingMachine = true;
									}
								}
								
								if(posx!=destinationx || posy!=destinationy)
								{
									//set position
									moveWorker();
								}
								else
								{
									//at destination
									if(gettingTools)
									{
										atDestination = true;
										synchronized(fg)
										{
											fg.takeTool(curTool.tool, curTool.num);
										}
									}
								}
							}
							else if(usingMachine)
							{
								if(atDestination)
								{	
									int destination = retrieveDestination(curInstruction.station, true)+ROW_NUM;
									
										System.out.println(curInstruction.station+": "+destination);
										setWorkerDestination(destination);
										atDestination =false;
									
								}
								
								if(posx!=destinationx || posy!=destinationy)
								{
									//set position
									moveWorker();
								}
								else
								{
									//at destination
									atDestination = true;
									puttingToolsBack = true;
									toolPointer = 0;
									waitingForMachine = true;
									moveWorker();
									usingMachine = false;
									synchronized(fg)
									{
										String machines[] = fg.machineMap.get(curInstruction.station);
										machines[machinePointer] = curInstruction.time+"s";
										fg.machineMap.put(curInstruction.station, machines);	
										//
									}
								}
							}
							else if(puttingToolsBack)
							{
								if(waitingForMachine)
								{
									waitForXSeconds(curInstruction.time);
									synchronized(fg)
									{
										fg.freeUpMachine(curInstruction.station, machinePointer);
										String machines[] = fg.machineMap.get(curInstruction.station);
										machines[machinePointer] = "Open";
										fg.machineMap.put(curInstruction.station, machines);
									}	
									waitingForMachine = false;
								}
								if(atDestination)
								{
									if(toolPointer<curInstruction.tools.size())
									{
										curTool = curInstruction.tools.get(toolPointer++);
										setWorkerDestination(retrieveDestination(curTool.tool, false)+1);
										atDestination =false;
									}
									else
									{
										//put all back
										puttingToolsBack = false;
										if(instructionPointer < rcp.instructions.size())
										{
											needNextInstruction=true;
										}
										else
										{
											grabbedTask =false;
											finishedTask = true;
											atDestination = true;
										}
									}
								}
								
								if(posx!=destinationx || posy!=destinationy)
								{
									//set position
									moveWorker();
								}
								else
								{
									//at destination
									if(puttingToolsBack)
									{
										atDestination = true;
										synchronized(fg)
										{
											fg.giveToolBack(curTool.tool,curTool.num);
										}
									}
								}
							}	
						}	
					}
				}
			}
		}
	}
	public void waitForXSeconds(int sec)
	{
		try {
			Thread.sleep(sec*1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	public int findFirstMachineAvailable(String machineWord)
	{
		synchronized(fg)
		{
			int numMachine = fg.findNextAvailableMachine(machineWord);
			return numMachine;
		}
	}
	public int retrieveDestination(String dest, boolean machine)
	{
		int intDest = fg.retrieveDestination(dest);
		
		if(machine)
		{
			int whatMachineIndex = findFirstMachineAvailable(dest);
			System.out.println("FIRST AVAILABLE MACHINE: "+whatMachineIndex);
			machinePointer = whatMachineIndex;
			return intDest+(2*whatMachineIndex);
		}
		else
		{
			return intDest;
		}
	}
	public void moveWorker()
	{
		//////0//////
		/////////////
		//3///W///1//
		/////////////
		//////2//////
		
		////CHECK OUT OF BOUNDS/////////////////CHECK FOR OBSTICLES////////////////////////
		//check left
		if((posx-1 < 0) || fg.coordinateMap.containsKey((posx-1)+(posy*ROW_NUM))){bounds[3] =false;}
		else{bounds[3] =true;}
		//check right
		if((posx+1 > (COL_NUM-1)) || fg.coordinateMap.containsKey((posx+1)+(posy*ROW_NUM))){bounds[1] =false;}
		else{bounds[1] =true;}
		//check up
		if((posy-1 < 0) || fg.coordinateMap.containsKey((posx)+((posy-1)*ROW_NUM))){bounds[0] =false;}
		else{bounds[0] =true;}
		//check down
		if((posy+1 > (ROW_NUM-1)) || fg.coordinateMap.containsKey((posx)+((posy+1)*ROW_NUM))){bounds[2] =false;}
		else{bounds[2] =true;}
		////////////////////////////////////////////////////////////////////////////////////
		
		
		if(usingMachine && atDestination)
		{
			pixely-=50;
			posy--;
		}
		else
		{
			if(destinationx < posx && bounds[3]  && prevMove!=1)//move left
			{
				posx--;
				prevMove = 3;
			}
			else if(destinationy < posy && bounds[0] && prevMove!=2)//move up
			{
				posy--;
				prevMove = 0;
			}
			else if(destinationx > posx && bounds[1] && prevMove!=3)//move right
			{
				posx++;
				prevMove = 1;
			}
			else if(destinationy > posy && bounds[2] && prevMove!=0)//move down
			{
				posy++;
				prevMove = 2;
			}
			else if(destinationx==posx && !bounds[0])
			{
				if(bounds[3] ){posx--;prevMove = 3;}
				else if(bounds[1] ){posx++;prevMove = 1;}
			}
			else if(destinationx==posx && !bounds[2])
			{
				if(bounds[3]  ){posx--;prevMove = 3;}
				else if(bounds[1] ){posx++;prevMove = 1;}
			}
			else if(destinationy==posy && !bounds[3])
			{
				if(bounds[0] ){posy--;prevMove = 0;}
				else if(bounds[2] ){posy++;prevMove = 2;}
			}
			else if(destinationy==posy && !bounds[1])
			{
				if(bounds[0] ){posy--;prevMove = 0;}
				else if(bounds[2] ){posy++;prevMove = 2;}
			}
			else
			{
				//move random direction
				int rand = (int)(Math.random()*4);
				if(rand==0 && bounds[0])
				{
					posy--;
					prevMove = 0;
				}
				else if(rand==1 && bounds[1])
				{
					posx++;
					prevMove = 1;
				}
				else if(rand==2 && bounds[2])
				{
					posy++;
					prevMove = 2;
				}
				else if(rand==3 && bounds[3])
				{
					posx--;
					prevMove = 3;
				}
			}
		}
		repaintAnimate();
		//repaint();
	}
	public void repaintAnimate()
	{
		if(pixely>posy*50)//up
		{
			while(pixely!=posy*50)
			{
				try {
					Thread.sleep(2);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				pixely -=1;
				repaint();
			}
		}
		else if(pixelx<posx*50)//right
		{
			while(pixelx!=posx*50)
			{
				try {
					Thread.sleep(2);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				pixelx +=1;
				repaint();
			}
		}
		else if(pixely<posy*50)//down
		{
			while(pixely!=posy*50)
			{
				try {
					Thread.sleep(2);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				pixely +=1;
				repaint();
			}
		}
		else if(pixelx>posx*50)//left
		{
			while(pixelx!=posx*50)
			{
				try {
					Thread.sleep(2);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				pixelx -=1;
				repaint();
			}
		}
		else{repaint();}
	}
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		if(workerVisible)
		{
			g.drawImage(imgWorker,pixelx,pixely,null);
		}
	}
}
