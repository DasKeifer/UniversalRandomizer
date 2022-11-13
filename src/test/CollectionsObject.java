package test;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class CollectionsObject extends SimpleObject
{
	public Double[] doubleWrapperArray;
	public char[] charRawArray;
	public Collection<Character> charCollection;
	public Map<Integer, Float> floatMap;
	
	public CollectionsObject(String name, int intVal)
	{
		super(name, intVal);

		doubleWrapperArray = new Double[0];
		charRawArray = new char[0];
		charCollection = new LinkedList<>();
		floatMap = new HashMap<>();
	}
}
