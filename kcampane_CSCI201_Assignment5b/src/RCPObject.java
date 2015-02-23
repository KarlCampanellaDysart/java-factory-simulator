

import java.util.ArrayList;

class RCPObject
{
	String name;
	int num;
	int wood;
	int metal;
	int plastic;
	ArrayList<Instructions> instructions;
	public RCPObject(String name, int num)
	{
		this.name = name;
		this.num = num;
		instructions = new ArrayList<Instructions>();
	}
	public void addInstruction(Instructions i)
	{
		instructions.add(i);
	}
	public void setMaterials(int w,int m,int p)
	{
		wood = w;
		metal = m;
		plastic = p;
	}
}
