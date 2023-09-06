package universal_randomizer.wrappers;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.junit.jupiter.api.Test;

import Support.SimpleObject;
import Support.UncomparableObject;

class ReflectionObjectTests {

	// TODO: Null tests
	
	@Test
	void create() 
	{
		SimpleObject so = new SimpleObject("test obj", 3);
		ReflectionObject<SimpleObject> ro = ReflectionObject.create(so);
		assertEquals(so, ro.getObject());
		
		assertNull(ReflectionObject.create(null));
	}
	
	@Test
	void forceNonNull() 
	{
		assertNull(ReflectionObject.create(null));
		
		SimpleObject so = new SimpleObject("test obj", 3);
		ReflectionObject<SimpleObject> ro = ReflectionObject.create(so);
		assertFalse(ro.setObject(null));
		assertEquals(so, ro.getObject()); // Ensure not changed

		SimpleObject so2 = new SimpleObject("test obj", 3);
		assertTrue(ro.setObject(so2));
		assertEquals(so2, ro.getObject()); // Ensure changed
	}
	
	@Test
	void getField() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NoSuchFieldException 
	{
		int expected = 3;
		SimpleObject so = new SimpleObject("test obj", 3);
		so.list.add(1);
		so.list.add(2);
		so.list.add(3);
		ReflectionObject<SimpleObject> ro = ReflectionObject.create(so);
		
		// primative
		assertEquals(expected, ro.getField("intField"));
		assertEquals(expected, ro.getField("getIntField()"));
		
		// List
		assertEquals(so.list, ro.getField("list"));
		assertEquals(so.list, ro.getField("getList()"));
		
		assertNull(ro.getField("uncomparableObj"));
	}
	
	@Test
	void getField_nested() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NoSuchFieldException 
	{
		SimpleObject so = new SimpleObject("test obj", 3);
		so.uncomparableObj = new UncomparableObject(7);
		so.uncomparableObj.recurse = so;

		int expectedSo = 3;
		int expectedUo = 7;
		ReflectionObject<SimpleObject> ro = ReflectionObject.create(so);
		assertEquals(expectedUo, ro.getField("uncomparableObj.val"));
		assertEquals(expectedSo, ro.getField("uncomparableObj.recurse.getUncomparableObject.recurse.intField"));
		assertEquals(expectedSo, ro.getField("getUncomparableObject.recurse.uncomparableObj.recurse.getIntField()"));
	}
	
	@Test
	void getField_badInputs() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NoSuchFieldException 
	{
		ReflectionObject<SimpleObject> ro = ReflectionObject.create(new SimpleObject("test obj", 3));

		assertThrows(NoSuchFieldException.class, () ->
			ro.getField(""));
		assertThrows(NoSuchFieldException.class, () ->
			ro.getField("unfound"));
		assertThrows(NoSuchMethodException.class, () ->
			ro.getField("unfound()"));
		assertThrows(NoSuchMethodException.class, () ->
			ro.getField("uncomparableObj()"));
		assertThrows(NoSuchFieldException.class, () ->
			ro.getField("getUncomparableObject"));
		
		assertThrows(NullPointerException.class, () ->
			ro.getField("uncomparableObj.unfound.unfound2"));
		
		ro.getObject().uncomparableObj = new UncomparableObject(0);
		ro.getObject().uncomparableObj.recurse = ro.getObject();

		assertThrows(NoSuchFieldException.class, () ->
			ro.getField("uncomparableObj.unfound.unfound2"));
		assertThrows(NoSuchMethodException.class, () ->
			ro.getField("uncomparableObj.unfound()"));
		
		assertThrows(IllegalArgumentException.class, () ->
			ro.getField("getUncomparableObject().recurse.intIsEqualTo()"));
	}
	
	@Test
	void getFieldStream() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NoSuchFieldException 
	{
		SimpleObject so = new SimpleObject("test obj", 3);
		ReflectionObject<SimpleObject> ro = ReflectionObject.create(so);
		so.list.add(1);
		so.list.add(2);
		so.list.add(3);
		so.map.put(4, "14");
		so.map.put(5, "15");

		// get a single field
		List<?> fieldStreamArray = ro.getFieldStream("intField").toList();
		assertEquals(1, fieldStreamArray.size());
		assertEquals(3, fieldStreamArray.get(0));
		
		// collections/maps
		assertIterableEquals(so.list, ro.getFieldStream("list").toList());
		assertIterableEquals(so.map.values(), ro.getFieldStream("map").toList());
		assertIterableEquals(so.map.keySet(), ro.getFieldStream("map.keySet()").toList());
	}
	
	@Test
	void getFieldStream_badInputs() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NoSuchFieldException 
	{
		SimpleObject so = new SimpleObject("test obj", 3);
		ReflectionObject<SimpleObject> ro = ReflectionObject.create(so);
		so.list.add(1);
		so.list.add(2);
		so.list.add(3);

		assertEquals(0, ro.getFieldStream("").count());
		assertEquals(0, ro.getFieldStream("unused").count());
		assertEquals(0, ro.getFieldStream("unused.unused2").count());
	}
	
	@Test
	void getMapFieldStream() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NoSuchFieldException 
	{
		SimpleObject so = new SimpleObject("test obj", 3);
		ReflectionObject<SimpleObject> ro = ReflectionObject.create(so);
		so.list.add(1);
		so.list.add(2);
		so.list.add(3);
		so.map.put(4, "14");
		so.map.put(5, "15");

		assertIterableEquals(so.map.keySet(), ro.getMapFieldKeysStream("map").toList());
		assertIterableEquals(so.map.values(), ro.getMapFieldValuesStream("map").toList());
	}
	
	@Test
	void getMapFieldStream_badInputs() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NoSuchFieldException 
	{
		SimpleObject so = new SimpleObject("test obj", 3);
		ReflectionObject<SimpleObject> ro = ReflectionObject.create(so);
		so.list.add(1);
		so.list.add(2);
		so.list.add(3);
		so.map.put(4, "14");
		so.map.put(5, "15");
		
		// Non map
		assertThrows(ClassCastException.class, () ->
			ro.getMapFieldKeysStream("list"));
		assertThrows(ClassCastException.class, () ->
			ro.getMapFieldKeysStream("intField"));
		assertThrows(NoSuchFieldException.class, () ->
			ro.getMapFieldKeysStream("unused"));
		assertThrows(NoSuchMethodException.class, () ->
			ro.getMapFieldKeysStream("list()"));
		assertThrows(NullPointerException.class, () ->
			ro.getMapFieldKeysStream("uncomparableObj.unused"));
	}
	
//	@Test
//	void setField() 
//	{
//		SimpleObject so = new SimpleObject("test obj", 3);
//		ReflectionObject<SimpleObject> ro = ReflectionObject.create(so);
//		
//		// objects
//		assertTrue(ro.setField("stringField", "1", String.class));
//		assertEquals("1", so.stringField);
//		assertTrue(ro.setField("setStringField", "2", String.class));
//		assertEquals("2", so.stringField);
//		
//		// object null value
//		assertTrue(ro.setField("stringField", null, String.class));
//		assertNull(so.stringField);
//		assertTrue(ro.setField("setStringField", null, String.class));
//		assertNull(so.stringField);
//		
//		// primitives
//		assertTrue(ro.setField("intField", 2, int.class));
//		assertEquals(2, so.intField);
//		assertTrue(ro.setField("setIntField", 1, int.class));
//		assertEquals(1, so.intField);
//		
//		// Test return vals
//		assertTrue(ro.setField("setIntFieldReturn", 7, int.class));
//		assertEquals(7, so.intField);
//		assertFalse(ro.setField("setIntFieldReturn", -4, int.class));
//		assertEquals(-4, so.intField); // sets anyway
//		
//		assertTrue(ro.setField("setIntFieldReturnBoxed", 6, int.class));
//		assertEquals(6, so.intField);
//		assertFalse(ro.setField("setIntFieldReturnBoxed", 0, int.class)); // returns null
//		assertEquals(0, so.intField); // sets anyway
//		assertFalse(ro.setField("setIntFieldReturnBoxed", -2, int.class));
//		assertEquals(-2, so.intField); // sets anyway
//	}
//	
//	@Test
//	void setField_nested() 
//	{
//		SimpleObject so = new SimpleObject("test obj", 3);
//		so.uncomparableObj = new UncomparableObject(1);
//		so.uncomparableObj.recurse = so;
//		
//		ReflectionObject<SimpleObject> ro = ReflectionObject.create(so);
//		
//		// objects
//		assertTrue(ro.setField("uncomparableObj.recurse.stringField", "1", String.class));
//		assertEquals("1", so.stringField);
//		assertTrue(ro.setField("uncomparableObj.recurse.setStringField", "2", String.class));
//		assertEquals("2", so.stringField);
//		
//		// primitives
//		assertTrue(ro.setField("uncomparableObj.recurse.intField", 2, int.class));
//		assertEquals(2, so.intField);
//		assertTrue(ro.setField("uncomparableObj.recurse.setIntField", 1, int.class));
//		assertEquals(1, so.intField);
//	}
//	
//	@Test
//	void setField_autoUnbox() 
//	{
//		SimpleObject so = new SimpleObject("test obj", 3);
//		so.uncomparableObj = new UncomparableObject(1);
//		so.uncomparableObj.recurse = so;
//		
//		ReflectionObject<SimpleObject> ro = ReflectionObject.create(so);
//		
//		// Autounboxing of primitives
//		assertTrue(ro.setField("uncomparableObj.recurse.intField", -4, Integer.class));
//		assertEquals(-4, so.intField);
//		assertTrue(ro.setField("uncomparableObj.recurse.setIntField", -5, Integer.class));
//		assertEquals(-5, so.intField);
//
//		// Test return vals
//		assertTrue(ro.setField("setIntFieldReturn", 7, Integer.class));
//		assertEquals(7, so.intField);
//		assertFalse(ro.setField("setIntFieldReturn", -4, Integer.class));
//		assertEquals(-4, so.intField); // sets anyway
//		
//		assertTrue(ro.setField("setIntFieldReturnBoxed", 6, Integer.class));
//		assertEquals(6, so.intField);
//		assertFalse(ro.setField("setIntFieldReturnBoxed", 0, Integer.class)); // returns null
//		assertEquals(0, so.intField); // sets anyway
//		assertFalse(ro.setField("setIntFieldReturnBoxed", -2, Integer.class));
//		assertEquals(-2, so.intField); // sets anyway
//	}
//	
//	@Test
//	void setField_badInput() 
//	{
//		SimpleObject so = new SimpleObject("test obj", 3);
//		so.uncomparableObj = new UncomparableObject(1);
//		so.uncomparableObj.recurse = so;
//		
//		ReflectionObject<SimpleObject> ro = ReflectionObject.create(so);
//		
//		assertFalse(ro.setField("unused", "2", String.class));
//		assertFalse(ro.setField("uncomparableObj.unused", "2", String.class));
//		assertFalse(ro.setField("uncomparableObj.recurse.unused", "2", String.class));
//		assertFalse(ro.setField("uncomparableObj.unused.unused2", "2", String.class));
//		
//		assertFalse(ro.setField("intField", "2", String.class)); // wrong type
//		assertFalse(ro.setField("getIntField", "2", String.class)); // wrong type
//		
//		assertFalse(ro.setField("stringField", "2", null));
//		assertFalse(ro.setField("stringField", null, int.class)); //null val for primitive
//		
//		assertFalse(ro.setField("intField", null, Integer.class)); // wrong type (boxed vs primitive with null arg)
//		assertFalse(ro.setField("getIntField", null, Integer.class)); // wrong type (boxed vs primitive with null arg)
//		
//		// Set to not unbox primitive wrappers
//		ro.setTryUnboxWrappersOfPrimitives(false);
//		assertFalse(ro.setField("intField", 2, Integer.class)); // wrong type (boxed vs primitive without unboxing)
//		assertFalse(ro.setField("getIntField", 2, Integer.class)); // wrong type (boxed vs primitive without unboxing)
//		
//	}
}