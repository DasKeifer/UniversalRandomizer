package tests.support;


public class SimpleObject implements Comparable<SimpleObject>
{
	public String name;
	public int intVal;
	
	public SimpleObject(String name, int intVal)
	{
		this.name = name;
		this.intVal = intVal;
	}
    
	public SimpleObject(SimpleObject toCopy) 
	{
		this.name = toCopy.name;
		this.intVal = toCopy.intVal;
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

	public Boolean valBetween2And5Excl()
	{
		return intVal > 2 && intVal < 5;
	}
}
