
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.*;

public class FactoryGUI extends JFrame{
	//menu stuff
	JMenuBar mainMenu;
	JMenuItem openFolder;
	
	//parser
	FileParser fp;
	boolean filesLoaded = false;
	
	//task board
	JPanel taskBoard;
	JPanel taskBoardText;
	JLabel taskBoardLabels[];
	int taskBarPointer;
	RCPObject allRecipes[];
	JScrollPane jsp;
	
	//materials
	int wood,metal,plastic;
	int screwDriver,hammer, paintBrush, pliers, scissors;
	int curScrewDriver, curHammer, curPaintBrush, curPliers, curScissors;
	int workers;
	Map<String,Integer> materialsMap;
	
	//money
	int curMoney;
	
	//main panel
	MainPanel mp;
	
	//machine statuses
	String anvils[];
	String workBenches[];
	String furnaces[];
	String tableSaws[];
	String paintingStations[];
	String press[];
	
	boolean anvilsStatus[];
	boolean workBenchesStatus[];
	boolean furnacesStatus[];
	boolean tableSawsStatus[];
	boolean paintingStationsStatus[];
	boolean pressStatus[];
	
	Map<String, String[]> machineMap;
	Map<String, boolean[]> machineStatusMap;
	
	//all machines and items
	Map<Integer, String> coordinateMap;
	
	//locks and semaphores
	Lock lock = new ReentrantLock();
	double startTime;	
	
	Map<String, Semaphore> semaphoreMap;
	Semaphore screwdriverSemahpore;
	Semaphore hammerSemahpore;
	Semaphore paintbrushSemahpore;
	Semaphore pliersSemahpore;
	Semaphore scissorsSemahpore;
	
	//simulation state
	boolean finishedSimulation;
	boolean restart;
	
	ServerThread st;
	public FactoryGUI(){
		//setup menu
		restart();
		
		//setup material values
		mp = new MainPanel(this, fp);
		fp = new FileParser("assignment_5_test");
		fp.parseFactory();
		materialsMap = new HashMap<String, Integer>();
		machineMap = new HashMap<String, String[]>();
		machineStatusMap = new HashMap<String, boolean[]>();
		coordinateMap = new HashMap<Integer, String>();
		
		//add all components to JFrame
		addAllComponents();	
		
		st = new ServerThread(this);
		st.start();
		
		setupMaterials();
		setupTaskBoard();
		loadMaterials();
	}	
	
	public void restart(){
		restart = false;
		finishedSimulation = false;
	}
	
	public void addAllComponents(){
		add(mp, BorderLayout.CENTER);
	}
	
	public void readdMap(){
		materialsMap.put("Wood", wood);
		materialsMap.put("Metal", metal);
		materialsMap.put("Plastic", plastic);
		materialsMap.put("Screwdriver", curScrewDriver);
		materialsMap.put("Hammer", curHammer);
		materialsMap.put("Paintbrush", curPaintBrush);
		materialsMap.put("Plier", curPliers);
		materialsMap.put("Scissor", curScissors);
		
		screwDriver =curScrewDriver;
		hammer = curHammer; 
		paintBrush = curPaintBrush; 
		pliers = curPliers; 
		scissors= curScissors;
		
		machineMap.put("Anvil", anvils);
		machineMap.put("Workbench", workBenches);
		machineMap.put("Furnace", furnaces );
		machineMap.put("Saw", tableSaws);
		machineMap.put("Painting Station", paintingStations);
		machineMap.put("Press", press);
		
		machineStatusMap.put("Anvil", anvilsStatus);
		machineStatusMap.put("Workbench", workBenchesStatus);
		machineStatusMap.put("Furnace", furnacesStatus );
		machineStatusMap.put("Saw", tableSawsStatus);
		machineStatusMap.put("Painting Station", paintingStationsStatus);
		machineStatusMap.put("Press", pressStatus);
		
		screwdriverSemahpore = new Semaphore(screwDriver);
		hammerSemahpore = new Semaphore(hammer);
		paintbrushSemahpore = new Semaphore(paintBrush);
		pliersSemahpore = new Semaphore(pliers);
		scissorsSemahpore = new Semaphore(scissors);
		
		semaphoreMap = new HashMap<String,Semaphore>();
		semaphoreMap.put("Screwdriver", screwdriverSemahpore);
		semaphoreMap.put("Hammer", hammerSemahpore);
		semaphoreMap.put("Paintbrush", paintbrushSemahpore);
		semaphoreMap.put("Plier", pliersSemahpore);
		semaphoreMap.put("Scissor", scissorsSemahpore);
	}
	
	public void setupMaterials(){				
		//setup panels
		curMoney = 0;
		workers = 0;
		
		wood = 0;
		metal = 0;
		plastic = 0;
		screwDriver =0;
		hammer =0; 
		paintBrush =0; 
		pliers =0; 
		scissors=0;
		curScrewDriver=0;
		curHammer=0; 
		curPaintBrush=0; 
		curPliers=0; 
		curScissors=0;
		
		anvils = new String[2];
		for(int i =0;i<2;i++){anvils[i]="Open";}
		workBenches= new String[3];
		for(int i =0;i<3;i++){workBenches[i]="Open";}
		furnaces= new String[2];
		for(int i =0;i<2;i++){furnaces[i]="Open";}
		tableSaws= new String[3];
		for(int i =0;i<3;i++){tableSaws[i]="Open";}
		paintingStations= new String[4];
		for(int i =0;i<4;i++){paintingStations[i]="Open";}
		press= new String[1];
		press[0]="Open";
		
		anvilsStatus = new boolean[2];
		for(int i =0;i<2;i++){anvilsStatus[i]=true;}
		workBenchesStatus= new boolean[3];
		for(int i =0;i<3;i++){workBenchesStatus[i]=true;}
		furnacesStatus= new boolean[2];
		for(int i =0;i<2;i++){furnacesStatus[i]=true;}
		tableSawsStatus= new boolean[3];
		for(int i =0;i<3;i++){tableSawsStatus[i]=true;}
		paintingStationsStatus= new boolean[4];
		for(int i =0;i<4;i++){paintingStationsStatus[i]=true;}
		pressStatus= new boolean[1];
		pressStatus[0]=true;
		
		//init coordinate map 11x11
		coordinateMap.put(11, "Screwdriver");
		coordinateMap.put(14, "Wood");
		coordinateMap.put(16, "Metal");
		coordinateMap.put(18, "Plastic");
		coordinateMap.put(33, "Hammer");
		coordinateMap.put(35, "Anvil");
		coordinateMap.put(37, "Anvil");
		coordinateMap.put(39, "Workbench");
		coordinateMap.put(41, "Workbench");
		coordinateMap.put(43, "Workbench");
		coordinateMap.put(55, "Paintbrush");
		coordinateMap.put(68, "Furnace");
		coordinateMap.put(70, "Furnace");
		coordinateMap.put(72, "Saw");
		coordinateMap.put(74, "Saw");
		coordinateMap.put(76, "Saw");
		coordinateMap.put(77, "Plier");
		coordinateMap.put(99, "Scissor");
		coordinateMap.put(101, "Painting Station");
		coordinateMap.put(103, "Painting Station");
		coordinateMap.put(105, "Painting Station");
		coordinateMap.put(107, "Painting Station");
		coordinateMap.put(109, "Press");
		
		readdMap();
	}
	
	public void setupTaskBoard(){
		taskBoard = new JPanel();
		taskBoard.setVisible(true);
		taskBoard.setLayout(new BoxLayout(taskBoard, BoxLayout.Y_AXIS));
		JLabel taskBoardLabel = new JLabel("TASK BOARD");
		taskBoard.add(taskBoardLabel);
		taskBoardText = new JPanel();
		taskBoardText.setLayout(new BoxLayout(taskBoardText, BoxLayout.Y_AXIS));
	}
	
	public void populateTaskBoard(){
		taskBarPointer = 0;
		taskBoardLabels = new JLabel[fp.totalItems];
		allRecipes = new RCPObject[fp.totalItems];
		int labelCount =0;		
		Iterator<RCPObject> it = fp.rcpFiles.iterator();
		while(it.hasNext()){
			RCPObject tempRCP = it.next();
			for(int i =0; i < 1;i++){//num is now price
				//add strings to task board
				taskBoardLabels[labelCount] = new JLabel(tempRCP.name+"...Not Built");
				taskBoardLabels[labelCount].setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
				taskBoardText.add(taskBoardLabels[labelCount]);
				allRecipes[labelCount] = new RCPObject(tempRCP.name, tempRCP.num);
				allRecipes[labelCount].setMaterials(tempRCP.wood, tempRCP.metal, tempRCP.plastic);
				Iterator<Instructions> it2 = tempRCP.instructions.iterator();
				while(it2.hasNext()){
					allRecipes[labelCount].addInstruction(it2.next());
				}
				labelCount++;
			}
		}		
		jsp = new JScrollPane(taskBoardText);
		jsp.setPreferredSize(new Dimension(200, 600));
		taskBoard.add(jsp);
		add(taskBoard, BorderLayout.EAST);
		repaint();
		revalidate();
	}
	
	public void loadMaterials()
	{
		wood = fp.wood;
		metal = fp.metal;
		plastic = fp.plastic;
		curMoney = fp.money;
		workers = fp.workers;
		
		screwDriver = fp.screwDriver;
		hammer = fp.hammer; 
		paintBrush = fp.paintBrush; 
		pliers = fp.pliers; 
		scissors = fp.scissors;
		curScrewDriver = fp.screwDriver;
		curHammer = fp.hammer; 
		curPaintBrush = fp.paintBrush; 
		curPliers = fp.pliers; 
		curScissors = fp.scissors;
		
		materialsMap.put("Wood", wood);
		materialsMap.put("Metal", metal);
		materialsMap.put("Plastic", plastic);
		materialsMap.put("Screwdriver", curScrewDriver);
		materialsMap.put("Hammer", curHammer);
		materialsMap.put("Paintbrush", curPaintBrush);
		materialsMap.put("Plier", curPliers);
		materialsMap.put("Scissor", curScissors);
		materialsMap.put("Money", curMoney);
		
		//money
		curMoney = fp.money;
	}
	
	public void incrementTask(){
		lock.lock();
		taskBarPointer++;
		lock.unlock();
	}
	
	public void updateMachineStatus(String machine, boolean[] statuses){
		lock.lock();
		machineStatusMap.put(machine, statuses);
		lock.unlock();
	}
	
	public int findNextAvailableMachine(String machine){
		lock.lock();	
		int i =0;
		boolean machines[] = machineStatusMap.get(machine);
		int size = machines.length;
		while(i<size){
			if(machines[i]){//found free machine
				machines[i] = false;
				updateMachineStatus(machine, machines);
				lock.unlock();
				return i;
			}	
			i++;
		}	
		
		try {
			lock.unlock();
			wait();
		} catch (InterruptedException e) {}
	
		return findNextAvailableMachine(machine);
	}
	
	public void freeUpMachine(String machine, int index){
		lock.lock();
		boolean machinesStatus[] = machineStatusMap.get(machine);
		machinesStatus[index] = true;
		machineStatusMap.put(machine, machinesStatus);
		notifyAll();
		lock.unlock();
	}
	
	public int retrieveDestination(String dest){
		int i = 0;
		while(i<11*11){
			if(coordinateMap.containsKey(i)){
				if(coordinateMap.get(i).equals(dest)){break;}
			}
			i++;
		}
		return i;
	}
	
	public void takeTool(String tool, int num){
		try {
			semaphoreMap.get(tool).acquire(num);
			int curNumOfTool = materialsMap.get(tool);
			if((curNumOfTool-num) >= 0){
				materialsMap.put(tool, curNumOfTool-num);
			}
		} 
		catch (InterruptedException e1) {}
	}
	
	public void giveToolBack(String tool, int num){
		semaphoreMap.get(tool).release(num);
		int curNumOfTool = materialsMap.get(tool);
		materialsMap.put(tool, curNumOfTool+num);
	}
	
	public void getMaterial(int numMaterials, String materials){
		lock.lock();
		int tempMaterial = materialsMap.get(materials);
		materialsMap.put(materials, tempMaterial-numMaterials);
		lock.unlock();
	}
	
	public void setStartTime(double time){
		startTime = time;
	}
	
	public void setEndTime(double time){	
		int answer = JOptionPane.showConfirmDialog(this, "The simulation took "+((time-startTime)/1000)+" seconds.\nWould you like to run another simulation?");
		
		if(answer == JOptionPane.OK_OPTION){
			restart = true;
			
			setupMaterials();
			taskBoard.remove(1);
			restart();
			
			int labelCount =0;
			Iterator<RCPObject> it = fp.rcpFiles.iterator();
			while(it.hasNext()){
				RCPObject tempRCP = it.next();
				for(int i =0; i < tempRCP.num;i++){
					taskBoardLabels[labelCount].setText("");
					taskBoardText.remove(taskBoardLabels[labelCount]);
					labelCount++;
				}
			}
			taskBoard.repaint();
			taskBoard.revalidate();
		}
		else{//close
			System.exit(0);
		}	
	}

	public void setSimulationDone(){
		finishedSimulation = true;
	}
	
	public void openFileAndStart(){
		//fp.parseFactory();		
		//loadMaterials();
		populateTaskBoard();
		filesLoaded = true;
		setStartTime(System.currentTimeMillis());
		mp.addWorkers(workers);
	}
	
	public static void main(String[] args) {
		FactoryGUI fgui = new FactoryGUI();
		fgui.setSize(800,600);
		fgui.setVisible(true);
		fgui.setDefaultCloseOperation(EXIT_ON_CLOSE);
		fgui.setTitle("Factory");
		while(true){
			if(fgui.restart){
				System.out.println("restarting");
				fgui = new FactoryGUI();
				fgui.setSize(800,600);
				fgui.setVisible(true);
				fgui.setDefaultCloseOperation(EXIT_ON_CLOSE);
				fgui.setTitle("Factory");
			}
		}
	}
}
