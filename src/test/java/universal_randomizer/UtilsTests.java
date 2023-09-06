package universal_randomizer;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

class UtilsTests {
	
	@Test
	void convertArrayToStream() 
	{
		Integer[] wrappedArray = {0,1,2,3,4};
		Stream<Integer> wrappedResult = Arrays.stream(wrappedArray);
		assertIterableEquals(Arrays.asList(wrappedArray), wrappedResult.toList());
		
		byte[] byteArray = {0,1,2,3,4};
		List<Byte> byteResult = Utils.convertPrimitiveArrayToStream(byteArray).toList();
		for (int i = 0; i < byteArray.length; i++)
		{
			assertEquals(byteArray[i], byteResult.get(i));
		}
		assertNull(Utils.convertPrimitiveArrayToStream((byte[])null));

		short[] shortArray = {0,1,2,3,4};
		List<Short> shortResult = Utils.convertPrimitiveArrayToStream(shortArray).toList();
		for (int i = 0; i < byteArray.length; i++)
		{
			assertEquals(shortArray[i], shortResult.get(i));
		}
		assertNull(Utils.convertPrimitiveArrayToStream((short[])null));

		int[] intArray = {0,1,2,3,4};
		List<Integer> intResult = Utils.convertPrimitiveArrayToStream(intArray).toList();
		for (int i = 0; i < byteArray.length; i++)
		{
			assertEquals(intArray[i], intResult.get(i));
		}
		assertNull(Utils.convertPrimitiveArrayToStream((float[])null));

		float[] floatArray = {0,1,2,3,4};
		List<Float> floatResult = Utils.convertPrimitiveArrayToStream(floatArray).toList();
		for (int i = 0; i < byteArray.length; i++)
		{
			assertEquals(floatArray[i], floatResult.get(i));
		}
		assertNull(Utils.convertPrimitiveArrayToStream((float[])null));
		
		double[] doubleArray = {0,1,2,3,4};
		List<Double> doubleResult = Utils.convertPrimitiveArrayToStream(doubleArray).toList();
		for (int i = 0; i < byteArray.length; i++)
		{
			assertEquals(doubleArray[i], doubleResult.get(i));
		}
		assertNull(Utils.convertPrimitiveArrayToStream((double[])null));
		
		boolean[] booleanArray = {true, false, true, true, false};
		List<Boolean> boolResult = Utils.convertPrimitiveArrayToStream(booleanArray).toList();
		for (int i = 0; i < byteArray.length; i++)
		{
			assertEquals(booleanArray[i], boolResult.get(i));
		}
		assertNull(Utils.convertPrimitiveArrayToStream((boolean[])null));
		
		char[] charArray = {0,1,2,3,4};
		List<Character> charResult = Utils.convertPrimitiveArrayToStream(charArray).toList();
		for (int i = 0; i < byteArray.length; i++)
		{
			assertEquals(charArray[i], charResult.get(i));
		}
		assertNull(Utils.convertPrimitiveArrayToStream((char[])null));
	}
}
