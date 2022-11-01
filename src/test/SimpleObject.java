package test;

public class SimpleObject implements Comparable<SimpleObject>
{
	public String name;
	public int intVal;
	
	public SimpleObject(String name, int intVal)
	{
		this.name = name;
		this.intVal = intVal;
	}
    
	public static int reverseSort(SimpleObject lhs, SimpleObject rhs)
    {
        return Integer.compare(rhs.intVal, lhs.intVal);
    }

	@Override
	public int compareTo(SimpleObject o) 
	{
		return Integer.compare(this.intVal, o.intVal);
	}

}
