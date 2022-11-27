package tests.support;

public class NestedObject
{
	public String name;
	public int intVal;
	public SimpleObject so;
	
	public NestedObject(String name, int intVal, SimpleObject so)
	{
		this.name = name;
		this.intVal = intVal;
		this.so = new SimpleObject(so.name, so.intVal);
	}
}
