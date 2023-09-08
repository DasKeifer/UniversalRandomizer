package universal_randomizer;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

import Support.SimpleObject;
import Support.SumableComparableObject;


class UtilsTests {

	void assertScoCollectionEquals(Collection<Integer> expected, Collection<SumableComparableObject> found)
	{
		assertEquals(expected.size(), found.size());
		Iterator<Integer> expItr = expected.iterator();
		Iterator<SumableComparableObject> foundItr = found.iterator();
		while (expItr.hasNext())
		{
			assertEquals(expItr.next(), foundItr.next().intVal);
		}
	}
	
	@Test
	void createRange() 
	{
		Collection<Integer> expected = List.of(0,3,6,9,12);
		
		Collection<SumableComparableObject> rangeSco = Utils.createRange(
				new SumableComparableObject(0), new SumableComparableObject(12), new SumableComparableObject(3));
		assertScoCollectionEquals(expected, rangeSco);

		rangeSco = Utils.createRange(
				new SumableComparableObject(0), new SumableComparableObject(13), new SumableComparableObject(3),
				(Comparator<SumableComparableObject>)(lhs,rhs) -> lhs.compareTo(rhs));
		assertScoCollectionEquals(expected, rangeSco);

		Collection<Integer> range = Utils.createRange(0, 12, 3, (i1,i2) -> i1 + i2);
		assertIterableEquals(expected, range);

		range = Utils.createRange(0, 13, 3, (lhs,rhs)->lhs.compareTo(rhs), (i1,i2) -> i1 + i2);
		assertIterableEquals(expected, range);
	}
	
	@Test
	void convertToWrapperClass() 
	{
		assertEquals(Byte.class, Utils.convertToWrapperClass(byte.class));
		assertEquals(Short.class, Utils.convertToWrapperClass(short.class));
		assertEquals(Integer.class, Utils.convertToWrapperClass(int.class));
		assertEquals(Long.class, Utils.convertToWrapperClass(long.class));
		assertEquals(Float.class, Utils.convertToWrapperClass(float.class));
		assertEquals(Double.class, Utils.convertToWrapperClass(double.class));
		assertEquals(Boolean.class, Utils.convertToWrapperClass(boolean.class));
		assertEquals(Character.class, Utils.convertToWrapperClass(char.class));
		assertEquals(Void.class, Utils.convertToWrapperClass(void.class));
		
		assertNull(Utils.convertToWrapperClass(Object.class));
		assertNull(Utils.convertToWrapperClass(null));
	}
	
	@Test
	void convertToPrimitiveClass() 
	{
		assertEquals(byte.class, Utils.convertToPrimitiveClass(Byte.class));
		assertEquals(short.class, Utils.convertToPrimitiveClass(Short.class));
		assertEquals(int.class, Utils.convertToPrimitiveClass(Integer.class));
		assertEquals(long.class, Utils.convertToPrimitiveClass(Long.class));
		assertEquals(float.class, Utils.convertToPrimitiveClass(Float.class));
		assertEquals(double.class, Utils.convertToPrimitiveClass(Double.class));
		assertEquals(boolean.class, Utils.convertToPrimitiveClass(Boolean.class));
		assertEquals(char.class, Utils.convertToPrimitiveClass(Character.class));
		assertEquals(void.class, Utils.convertToPrimitiveClass(Void.class));
		
		assertNull(Utils.convertToPrimitiveClass(Object.class));
		assertNull(Utils.convertToPrimitiveClass(null));
	}

	@Test
	void convertPrimitiveArrayToStream() 
	{
		byte[] inputByte = {1,2,3,4,5};
		short[] inputShort = {1,2,3,4,5};
		int[] inputInt = {1,2,3,4,5};
		long[] inputLong = {1,2,3,4,5};
		float[] inputFloat = {1,2,3,4,5};
		double[] inputDouble = {1,2,3,4,5};
		boolean[] inputBool = {true, false, true, true, false};
		char[] inputChar = {1,2,3,4,5};
		
		Collection<Byte> expectedByte = new LinkedList<>();
		Collection<Short> expectedShort = new LinkedList<>();
		Collection<Integer> expectedInt = new LinkedList<>();
		Collection<Long> expectedLong = new LinkedList<>();
		Collection<Float> expectedFloat = new LinkedList<>();
		Collection<Double> expectedDouble = new LinkedList<>();
		Collection<Boolean> expectedBool = new LinkedList<>();
		Collection<Character> expectedChar = new LinkedList<>();
		
		for (int i = 0; i < inputByte.length; i++)
		{
			expectedByte.add(inputByte[i]);
			expectedShort.add(inputShort[i]);
			expectedInt.add(inputInt[i]);
			expectedLong.add(inputLong[i]);
			expectedFloat.add(inputFloat[i]);
			expectedDouble.add(inputDouble[i]);
			expectedBool.add(inputBool[i]);
			expectedChar.add(inputChar[i]);
		}
		
		assertIterableEquals(expectedByte, Utils.convertPrimitiveArrayToStream(inputByte).toList());
		assertIterableEquals(expectedByte, Utils.convertPrimitiveArrayToStream((Object)inputByte).toList());
		assertIterableEquals(expectedByte, Utils.convertArrayToStream(inputByte).toList());

		assertIterableEquals(expectedShort, Utils.convertPrimitiveArrayToStream(inputShort).toList());
		assertIterableEquals(expectedShort, Utils.convertPrimitiveArrayToStream((Object)inputShort).toList());
		assertIterableEquals(expectedShort, Utils.convertArrayToStream(inputShort).toList());
		
		assertIterableEquals(expectedInt, Utils.convertPrimitiveArrayToStream(inputInt).toList());
		assertIterableEquals(expectedInt, Utils.convertPrimitiveArrayToStream((Object)inputInt).toList());
		assertIterableEquals(expectedInt, Utils.convertArrayToStream(inputInt).toList());
		
		assertIterableEquals(expectedLong, Utils.convertPrimitiveArrayToStream(inputLong).toList());
		assertIterableEquals(expectedLong, Utils.convertPrimitiveArrayToStream((Object)inputLong).toList());
		assertIterableEquals(expectedLong, Utils.convertArrayToStream(inputLong).toList());
		
		assertIterableEquals(expectedFloat, Utils.convertPrimitiveArrayToStream(inputFloat).toList());
		assertIterableEquals(expectedFloat, Utils.convertPrimitiveArrayToStream((Object)inputFloat).toList());
		assertIterableEquals(expectedFloat, Utils.convertArrayToStream(inputFloat).toList());
		
		assertIterableEquals(expectedDouble, Utils.convertPrimitiveArrayToStream(inputDouble).toList());
		assertIterableEquals(expectedDouble, Utils.convertPrimitiveArrayToStream((Object)inputDouble).toList());
		assertIterableEquals(expectedDouble, Utils.convertArrayToStream(inputDouble).toList());
		
		assertIterableEquals(expectedBool, Utils.convertPrimitiveArrayToStream(inputBool).toList());
		assertIterableEquals(expectedBool, Utils.convertPrimitiveArrayToStream((Object)inputBool).toList());
		assertIterableEquals(expectedBool, Utils.convertArrayToStream(inputBool).toList());
		
		assertIterableEquals(expectedChar, Utils.convertPrimitiveArrayToStream(inputChar).toList());
		assertIterableEquals(expectedChar, Utils.convertPrimitiveArrayToStream((Object)inputChar).toList());
		assertIterableEquals(expectedChar, Utils.convertArrayToStream(inputChar).toList());
	}

	@Test
	void convertToStream() 
	{
		String[] inputStr = {"t", "h", "r", "o", "w"};
		
		Collection<String> expectedStr = new LinkedList<>();
		for (int i = 0; i < inputStr.length; i++)
		{			
			expectedStr.add(inputStr[i]);
		}
		
		assertThrows(IllegalArgumentException.class, () ->
			Utils.convertPrimitiveArrayToStream(inputStr));
		
		assertIterableEquals(expectedStr, Utils.convertArrayToStream(inputStr).toList());
		assertIterableEquals(expectedStr, Utils.convertArrayToStream((Object)inputStr).toList());
		
		assertIterableEquals(expectedStr, Utils.convertToStream(inputStr).toList());
		assertIterableEquals(expectedStr, Utils.convertToStream(Arrays.asList(inputStr)).toList());
		
		Map<Integer, String> inputMap = new HashMap<>();
		for (int i = 0; i < inputStr.length; i++)
		{
			inputMap.put(i, inputStr[i]);
		}
		assertIterableEquals(expectedStr, Utils.convertToStream(inputMap).toList());
		
		assertEquals("t", Utils.convertToStream("t").findFirst().get());
	}
	
	@Test
	void convertToField()
	{		
		List<SimpleObject> list = new LinkedList<>();
		list.add(new SimpleObject("obj0", 0));
		list.add(new SimpleObject("obj1", 1));
		list.add(new SimpleObject("obj2", 2));
		list.add(new SimpleObject("obj3", 3));
		list.add(new SimpleObject("obj4", 4));
		
		for (int i = 0; i < list.size(); i++)
		{
			list.get(i).array = new int[i];
			list.get(i).wrappedArray = new Integer[i];
			for (int j = 0; j < i; j++)
			{
				list.get(i).array[j] = j;
				list.get(i).wrappedArray[j] = j;
				list.get(i).list.add(j);
				list.get(i).map.put(j, "" + j*10);
			}
		}
		List<Integer> expectedIntField = Arrays.asList(0,1,2,3,4);
		List<Integer> expectedIntMultiple = Arrays.asList(0,0,1,0,1,2,0,1,2,3);
		List<String> expectedStringMultiple = Arrays.asList("0","0","10","0","10","20","0","10","20","30");

		Stream<Integer> resultInts = Utils.convertToField(list.stream(), o -> o.getIntField());
		assertIterableEquals(expectedIntField, resultInts.toList());
		
		resultInts = Utils.convertToFieldCollection(list.stream(), o -> o.list);
		assertIterableEquals(expectedIntMultiple, resultInts.toList());

		resultInts = Utils.convertToFieldArray(list.stream(), o -> o.wrappedArray);
		assertIterableEquals(expectedIntMultiple, resultInts.toList());
		
		// Primitive arrays have to be converted to something else first (i.e. a stream)
		resultInts = Utils.convertToFieldStream(list.stream(), o -> Utils.convertPrimitiveArrayToStream(o.array));
		assertIterableEquals(expectedIntMultiple, resultInts.toList());
		
		resultInts = Utils.convertToFieldStream(list.stream(), o -> o.list.stream());
		assertIterableEquals(expectedIntMultiple, resultInts.toList());

		resultInts = Utils.convertToFieldMapKeys(list.stream(), o -> o.map);
		assertIterableEquals(expectedIntMultiple, resultInts.toList());
		
		Stream<String> resultString = Utils.convertToFieldMapValues(list.stream(), o -> o.map);
		assertIterableEquals(expectedStringMultiple, resultString.toList());

		
		assertNull(Utils.convertToField(null, o -> o));
		assertNull(Utils.convertToFieldCollection(null, o -> List.of(o)));
		assertNull(Utils.convertToFieldArray(null, o -> new Object[] {o}));
		assertNull(Utils.convertToFieldStream(null, o -> Arrays.stream(new Object[] {o})));
		assertNull(Utils.convertToFieldStream(null, o -> Arrays.stream(new Object[] {o})));
		assertNull(Utils.convertToFieldMapKeys(null, o -> new HashMap<Object, Object>()));
		assertNull(Utils.convertToFieldMapValues(null, o -> new HashMap<Object, Object>()));

		assertNull(Utils.convertToField(list.stream(), null));
		assertNull(Utils.convertToFieldCollection(list.stream(), null));
		assertNull(Utils.convertToFieldArray(list.stream(), null));
		assertNull(Utils.convertToFieldStream(list.stream(), null));
		assertNull(Utils.convertToFieldStream(list.stream(), null));
		assertNull(Utils.convertToFieldMapKeys(list.stream(), null));
		assertNull(Utils.convertToFieldMapValues(list.stream(), null));
	}

	@Test
	void castStream() 
	{
		Integer[] inputInt = {1,2,3,4,5};
		Stream<Integer> stream = Arrays.stream(inputInt);
		Stream<Object> objStream = Utils.castStream(stream);
		List<Object> asObj = objStream.toList();
		assertIterableEquals(Arrays.asList(inputInt), asObj);
		
		stream = Utils.castStream(asObj.stream());
		List<Integer> asInt = stream.toList();
		assertIterableEquals(Arrays.asList(inputInt), asInt);
	}
}
