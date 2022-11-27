package tests.support;

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

	static double dwv = 1;
	static char crv = 'A';
	static char ccv = 'z';
	static int fmk = -2;
	static float fmv = 42;
	
	public CollectionsObject(String name, int intVal, int colLengths)
	{
		super(name, intVal);

		doubleWrapperArray = new Double[colLengths];
		charRawArray = new char[colLengths];
		charCollection = new LinkedList<>();
		floatMap = new HashMap<>();
		
		for (int i = 0; i < colLengths; i++)
		{
			doubleWrapperArray[i] = dwv;
			dwv = dwv + 0.5;
			charRawArray[i] = crv++;
			charCollection.add(ccv--);
			floatMap.put(fmk++, fmv);
			fmv = fmv + 1;
		}
	}
}
