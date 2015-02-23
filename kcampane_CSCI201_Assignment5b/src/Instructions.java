

import java.util.ArrayList;

public class Instructions {
	String station;
	ArrayList<Tool> tools;
	int time;
	public Instructions() 
	{
		tools = new ArrayList<Tool>();
	}
	public void addTool(Tool t)
	{
		tools.add(t);
	}
	public void setTime(int t)
	{
		time = t;
	}
	public void setStation(String s)
	{
		station = s;
	}
}
