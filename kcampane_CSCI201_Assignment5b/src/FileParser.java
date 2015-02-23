
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;

public class FileParser 
{
	String directPath;
	static final String F_PATH = ".factory";
	static final String RCP_PATH = ".rcp";
	
	//supplies
	ArrayList<RCPObject> rcpFiles = new ArrayList<RCPObject>();
	int wood, metal, plastic;
	int screwDriver,hammer, paintBrush, pliers, scissors;
	int workers, money, totalTime;
	
	String nameOfItem;
	String workStations[] = {"Anvil","Workbench","Furnace","Saw","Painting","Press"};

	int totalItems =1;
	int price = 0;
	public FileParser(String directPath) 
	{
		this.directPath = directPath;
		wood=0;
		metal=0;
		plastic=0;
		screwDriver=0;
		hammer=0;
		paintBrush=0;
		pliers=0;
		scissors=0;
		workers=0;
		money = 0;
		totalTime=0;
	}
	public void parseFactory()
	{
		FileNameFilter filter = new FileNameFilter(F_PATH);
		File dir = new File(directPath);
		String[] list = dir.list(filter);
		
		for (String file : list) 
		{
			String tempFile = file.toString();
			try
			{
				FileReader fr = new FileReader(directPath+"/"+tempFile);
				BufferedReader br = new BufferedReader(fr);
				int num;
				String line = br.readLine();
				while(line != null)
				{
					StringTokenizer nextTokenizer = new StringTokenizer(line, "[]: ");
					String tag = (String) nextTokenizer.nextToken();
					if(tag.equals("Hammers")){
						num = Integer.parseInt((String) nextTokenizer.nextToken());
						hammer += num;
					}
					else if(tag.equals("Screwdrivers")){
						num = Integer.parseInt((String) nextTokenizer.nextToken());
						screwDriver += num;
					}
					else if(tag.equals("Pliers")){
						num = Integer.parseInt((String) nextTokenizer.nextToken());
						pliers += num;
					}
					else if(tag.equals("Scissors")){
						num = Integer.parseInt((String) nextTokenizer.nextToken());
						scissors += num;
					}
					else if(tag.equals("Paintbrushes")){
						num = Integer.parseInt((String) nextTokenizer.nextToken());
						paintBrush += num;
					}
					else if(tag.equals("Money")){
						num = Integer.parseInt((String) nextTokenizer.nextToken());
						money += num;
					}
					else {//workers
						num = Integer.parseInt((String) nextTokenizer.nextToken());
						workers += num;
					}
					line = br.readLine();
				}
				
				fr.close();
				br.close();
			}
			catch(FileNotFoundException fnfe){
				System.out.println(fnfe.getMessage());
			}
			catch(IOException ioe){
				System.out.println(ioe.getMessage());
			}
		}
	}
	public void parseRCP()
	{
		FileNameFilter filter = new FileNameFilter(RCP_PATH);
		File dir = new File(directPath);
		String[] list = dir.list(filter);
		
		
		for (String file : list) {
			String tempFile = file.toString();
		
			try
			{
				FileReader fr = new FileReader(directPath+"/"+tempFile);
				BufferedReader br = new BufferedReader(fr);
				String line = br.readLine();
				
				StringTokenizer tokenizer = new StringTokenizer(line, "[]x$: ");
				String name = (String) tokenizer.nextToken();
				int numOfItem = Integer.parseInt((String) tokenizer.nextToken());
				System.out.println(numOfItem);
				int num;
				
				RCPObject rcpObject = new RCPObject(name,numOfItem);
				int rcpWood =0;
				int rcpMetal =0;
				int rcpPlastic =0;
				price +=numOfItem;
				rcpFiles.add(rcpObject);
				nameOfItem = name;
				
				//wood metal plastic use
				line = br.readLine();
				while(line != null)
				{
					StringTokenizer nextTokenizer = new StringTokenizer(line, "[]:x ");
					String tag = (String) nextTokenizer.nextToken();
					if(tag.equals("Wood"))
					{
						num = Integer.parseInt((String) nextTokenizer.nextToken());
						wood += num;
						rcpWood = num;
					}
					else if(tag.equals("Metal"))
					{
						num = Integer.parseInt((String) nextTokenizer.nextToken());
						metal += num;
						rcpMetal = num;
					}
					else if(tag.equals("Plastic"))
					{
						num = Integer.parseInt((String) nextTokenizer.nextToken());
						plastic += num;
						rcpPlastic = num;
					}
					else if(tag.equals("Use"))
					{
						Instructions instruction = new Instructions();
						boolean isNext=true;
						ArrayList<Tool> tools= new ArrayList<Tool>();
						while(isNext)
						{
							String element = nextTokenizer.nextToken();
							boolean isTool = true;
							for(int i=0;i<workStations.length;i++)
							{
								if(element.equals(workStations[i]))
								{
									isTool=false;
									break;
								}
							}
							if(isTool)
							{
								int numOfTool = Integer.parseInt(element);
								
								element = nextTokenizer.nextToken();
								String nameOfTool = element;
								if(nameOfTool.equals("Screwdrivers"))
								{
									nameOfTool = nameOfTool.substring(0, 11);
								}
								if(nameOfTool.equals("Hammers"))
								{
									nameOfTool = nameOfTool.substring(0, 6);
								}
								if(nameOfTool.equals("Paintbrushes"))
								{
									nameOfTool = nameOfTool.substring(0, 10);
								}
								if(nameOfTool.equals("Pliers"))
								{
									nameOfTool = nameOfTool.substring(0, 5);
								}
								if(nameOfTool.equals("Scissors"))
								{
									nameOfTool = nameOfTool.substring(0, 7);
								}
								Tool t = new Tool(nameOfTool, numOfTool);
								tools.add(t);
								
								element = nextTokenizer.nextToken();//either and or at is thrown away
								//isNext is still true
							}
							else
							{
								//make the instruction to add
								if(element.equals("Painting"))
								{
									element = element+" "+nextTokenizer.nextToken();
								}
								instruction.setStation(element);
								element = nextTokenizer.nextToken();//the for
								element = nextTokenizer.nextToken();
								StringTokenizer timeTokenizer = new StringTokenizer(element, "s");//should produce time
								int time = Integer.parseInt(timeTokenizer.nextToken());
								totalTime += time;
								instruction.setTime(time);
								isNext = false;
							}
						}
						Iterator<Tool> it = tools.iterator();
						while(it.hasNext())
						{
							instruction.addTool(it.next());
						}
						rcpObject.addInstruction(instruction);
					}
					line = br.readLine();
				}
				rcpObject.setMaterials(rcpWood,rcpMetal, rcpPlastic);
				
				fr.close();
				br.close();
			}
			catch(FileNotFoundException fnfe)
			{
				System.out.println(fnfe.getMessage());
			}
			catch(IOException ioe)
			{
				System.out.println(ioe.getMessage());
			}
		}
	}
}

class FileNameFilter implements FilenameFilter
{
	private String ext;
	public FileNameFilter(String ext) 
	{
		this.ext = ext;
	}
	public boolean accept(File dir, String name) 
	{
		return (name.endsWith(ext));
	}
}

