package test;

public class SimpleObject
{
	public String name;
	public int intVal;
	
	public SimpleObject(String name, int intVal)
	{
		this.name = name;
		this.intVal = intVal;
	}
	
    static public int reverseSortAsObj(Object lhs, Object rhs)
    {
    	return Integer.compare(((SimpleObject)rhs).intVal, ((SimpleObject)lhs).intVal);
    }
	
    static public int reverseSort(SimpleObject lhs, SimpleObject rhs)
    {
    	return Integer.compare(rhs.intVal, lhs.intVal);
    }
}
