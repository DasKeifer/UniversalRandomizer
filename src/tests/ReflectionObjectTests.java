package tests;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import tests.support.CollectionsObject;
import tests.support.TestData;
import universal_randomizer.wrappers.ReflectionObject;

class ReflectionObjectTests {
	
	@Test
	@SuppressWarnings("unchecked")
	void ReflObj_GetField() 
	{
		ReflectionObject<CollectionsObject> testObj = TestData.coList.get(0);
				
		assertEquals(1, testObj.getField("intVal"));
		assertEquals("co1", testObj.getField("name"));

		assertArrayEquals(new Double[] {1.0, 1.5, 2.0}, (Double[]) testObj.getField("doubleWrapperArray"));
		assertArrayEquals(new char[] {'A', 'B', 'C'}, (char[]) testObj.getField("charRawArray"));
		assertIterableEquals(Arrays.asList('z', 'y', 'x'), (Collection<Character>) testObj.getField("charCollection"));
		
		Map<Integer, Float> expected = new HashMap<>();
		expected.put(-2, 42.0f);
		expected.put(-1, 43.0f);
		expected.put(0, 44.0f);
		assertIterableEquals(expected.entrySet(), ((Map<Integer, Float>) testObj.getField("floatMap")).entrySet());
		
		assertNull(testObj.getField("not a valid field"));
	}

	@Test
	void ReflObj_GetFieldStream() 
	{
		ReflectionObject<CollectionsObject> testObj = TestData.coList.get(0);
		// TODO:
	}
}
