package universal_randomizer.wrappers;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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
		assertEquals(0, ro.getSortingValue());
		assertEquals(true, ro.getTryUnboxWrappersOfPrimitives());
		
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
	void getBooleanMethod() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException 
	{
		ReflectionObject<SimpleObject> ro = ReflectionObject.create(new SimpleObject("test obj", 3));
		Method found = ro.getBooleanMethod("intBetween2And5Excl");
		assertNotNull(found);
		assertTrue((boolean) found.invoke(ro.getObject()));
		
		int expected = 3;
		Integer expectedBoxed = 3;
		found = ro.getBooleanMethod("intIsEqualTo", int.class);
		assertNotNull(found);
		assertTrue((boolean) found.invoke(ro.getObject(), expected));
		assertTrue((boolean) found.invoke(ro.getObject(), expectedBoxed));
		
		found = ro.getBooleanMethod("returnTrue");
		assertNotNull(found);
		assertTrue((boolean) found.invoke(ro.getObject()));
	}
	
	@Test
	void getBooleanMethod_badInputs() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException 
	{
		ReflectionObject<SimpleObject> ro = ReflectionObject.create(new SimpleObject("test obj", 3));
		assertNull(ro.getBooleanMethod(""));
		assertNull(ro.getBooleanMethod("unfound"));
		assertNull(ro.getBooleanMethod("unfound.unfound2"));
		assertNull(ro.getBooleanMethod("uncomparableObj.unfound"));
		assertNull(ro.getBooleanMethod("getStringField")); // Return non bool
		assertNull(ro.getBooleanMethod("intIsEqualTo")); // not correct params		
		Class<?>[] nullArgs = null;
		assertNull(ro.getBooleanMethod("intIsEqualTo", nullArgs)); // null list
		assertNull(ro.getBooleanMethod("intIsEqualTo", Integer.class, null)); // list with null
		assertNull(ro.getBooleanMethod("intIsEqualTo", Integer.class)); // still not correct params (int not INteger)
		assertNull(ro.getBooleanMethod("intIsEqualTo", int.class, int.class)); // too many params
	}
	
	@Test
	void getField() 
	{
		int expected = 3;
		SimpleObject so = new SimpleObject("test obj", 3);
		so.list.add(1);
		so.list.add(2);
		so.list.add(3);
		ReflectionObject<SimpleObject> ro = ReflectionObject.create(so);
		
		// primative
		assertEquals(expected, ro.getField("intField"));
		assertEquals(expected, ro.getField("getIntField"));
		
		// List
		assertEquals(so.list, ro.getField("list"));
		assertEquals(so.list, ro.getField("getList"));

		// Typesafe
		assertEquals("test obj", ro.getField("stringField", String.class));
		assertEquals("test obj", ro.getField("getStringField", String.class));		
		assertEquals(3, ro.getField("intField", int.class));
		assertEquals(so.list, ro.getField("list", List.class));
	}
	
	@Test
	void getField_nested() 
	{
		SimpleObject so = new SimpleObject("test obj", 3);
		so.uncomparableObj = new UncomparableObject(7);
		so.uncomparableObj.recurse = so;

		int expectedSo = 3;
		int expectedUo = 7;
		ReflectionObject<SimpleObject> ro = ReflectionObject.create(so);
		assertEquals(expectedUo, ro.getField("uncomparableObj.val"));
		assertEquals(expectedSo, ro.getField("uncomparableObj.recurse.getUncomparableObject.recurse.intField"));
		assertEquals(expectedSo, ro.getField("getUncomparableObject.recurse.uncomparableObj.recurse.getIntField"));
	}
	
	@Test
	void getField_badInputs() 
	{
		ReflectionObject<SimpleObject> ro = ReflectionObject.create(new SimpleObject("test obj", 3));
		assertNull(ro.getField(""));
		assertNull(ro.getField("unfound"));
		assertNull(ro.getField("intIsEqualTo")); //has args
		assertNull(ro.getField("uncomparableObj.unfound"));
		assertNull(ro.getField("uncomparableObj.unfound.unfound2"));
		assertNull(ro.getField("intField.unfound"));
		assertNull(ro.getField("intField.unfound.unfound2"));
		assertNull(ro.getField("uncomparableObj.recurse.intIsEqualTo"));

		assertNull(ro.getField("", Object.class));
		assertNull(ro.getField("unfound", String.class));
		assertNull(ro.getField("intIsEqualTo", boolean.class)); //has args
		assertNull(ro.getField("uncomparableObj.unfound", String.class));
		assertNull(ro.getField("uncomparableObj.unfound.unfound2", String.class));
		assertNull(ro.getField("intField.unfound", String.class));
		assertNull(ro.getField("intField.unfound.unfound2", String.class));
		assertNull(ro.getField("uncomparableObj.recurse.intIsEqualTo", boolean.class));

		assertNull(ro.getField("intField", null));
		assertNull(ro.getField("getStringField", Integer.class));
		assertNull(ro.getField("getStringField", int.class));
		assertNull(ro.getField("intField", Integer.class));
		assertNull(ro.getField("intField", String.class));
	}
	
	@Test
	void getFieldStream() 
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

		List<Integer> fieldStreamSafeArray = ro.getFieldStream("intField", int.class).toList();
		assertEquals(1, fieldStreamSafeArray.size());
		assertEquals(3, fieldStreamSafeArray.get(0));
		
		// collections/maps
		assertIterableEquals(so.list, ro.getFieldStream("list").toList());
		assertIterableEquals(so.map.values(), ro.getFieldStream("map").toList());
		assertIterableEquals(so.map.keySet(), ro.getFieldStream("map.keySet").toList());
		
		assertIterableEquals(so.list, ro.getFieldStream("list", Integer.class).toList());
		assertIterableEquals(so.map.values(), ro.getFieldStream("map", String.class).toList());
		assertIterableEquals(so.map.keySet(), ro.getFieldStream("map.keySet", Integer.class).toList());
	}
	
	@Test
	void getFieldStream_badInputs() 
	{
		SimpleObject so = new SimpleObject("test obj", 3);
		ReflectionObject<SimpleObject> ro = ReflectionObject.create(so);
		so.list.add(1);
		so.list.add(2);
		so.list.add(3);

		assertEquals(0, ro.getFieldStream("").count());
		assertEquals(0, ro.getFieldStream("unused").count());
		assertEquals(0, ro.getFieldStream("unused.unused2").count());

		assertEquals(0, ro.getFieldStream("", Object.class).count());
		assertEquals(0, ro.getFieldStream("list", null).count());
		assertEquals(0, ro.getFieldStream("list", String.class).count()); // Wrong type
		assertEquals(0, ro.getFieldStream("unused", String.class).count());
		assertEquals(0, ro.getFieldStream("unused.unused2", String.class).count());
	}
	
	@Test
	void getMapFieldStream() 
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
		
		assertIterableEquals(so.map.keySet(), ro.getMapFieldKeysStream("map", Integer.class).toList());
		assertIterableEquals(so.map.values(), ro.getMapFieldValuesStream("map", String.class).toList());
	}
	
	@Test
	void getMapFieldStream_badInputs() 
	{
		SimpleObject so = new SimpleObject("test obj", 3);
		ReflectionObject<SimpleObject> ro = ReflectionObject.create(so);
		so.list.add(1);
		so.list.add(2);
		so.list.add(3);
		so.map.put(4, "14");
		so.map.put(5, "15");
		
		// Non map
		assertEquals(0, ro.getMapFieldKeysStream("list").count());
		assertEquals(0, ro.getMapFieldKeysStream("intField").count());
		assertEquals(0, ro.getMapFieldKeysStream("unused").count());
		
		assertEquals(0, ro.getMapFieldKeysStream("list", Integer.class).count());
		assertEquals(0, ro.getMapFieldKeysStream("intField", Integer.class).count());
		assertEquals(0, ro.getMapFieldKeysStream("unused", Integer.class).count());
	}
	
	@Test
	void setField() 
	{
		SimpleObject so = new SimpleObject("test obj", 3);
		ReflectionObject<SimpleObject> ro = ReflectionObject.create(so);
		
		// objects
		assertTrue(ro.setField("stringField", "1", String.class));
		assertEquals("1", so.stringField);
		assertTrue(ro.setField("setStringField", "2", String.class));
		assertEquals("2", so.stringField);
		
		// object null value
		assertTrue(ro.setField("stringField", null, String.class));
		assertNull(so.stringField);
		assertTrue(ro.setField("setStringField", null, String.class));
		assertNull(so.stringField);
		
		// primitives
		assertTrue(ro.setField("intField", 2, int.class));
		assertEquals(2, so.intField);
		assertTrue(ro.setField("setIntField", 1, int.class));
		assertEquals(1, so.intField);
		
		// Test return vals
		assertTrue(ro.setField("setIntFieldReturn", 7, int.class));
		assertEquals(7, so.intField);
		assertFalse(ro.setField("setIntFieldReturn", -4, int.class));
		assertEquals(-4, so.intField); // sets anyway
		
		assertTrue(ro.setField("setIntFieldReturnBoxed", 6, int.class));
		assertEquals(6, so.intField);
		assertFalse(ro.setField("setIntFieldReturnBoxed", 0, int.class)); // returns null
		assertEquals(0, so.intField); // sets anyway
		assertFalse(ro.setField("setIntFieldReturnBoxed", -2, int.class));
		assertEquals(-2, so.intField); // sets anyway
	}
	
	@Test
	void setField_nested() 
	{
		SimpleObject so = new SimpleObject("test obj", 3);
		so.uncomparableObj = new UncomparableObject(1);
		so.uncomparableObj.recurse = so;
		
		ReflectionObject<SimpleObject> ro = ReflectionObject.create(so);
		
		// objects
		assertTrue(ro.setField("uncomparableObj.recurse.stringField", "1", String.class));
		assertEquals("1", so.stringField);
		assertTrue(ro.setField("uncomparableObj.recurse.setStringField", "2", String.class));
		assertEquals("2", so.stringField);
		
		// primitives
		assertTrue(ro.setField("uncomparableObj.recurse.intField", 2, int.class));
		assertEquals(2, so.intField);
		assertTrue(ro.setField("uncomparableObj.recurse.setIntField", 1, int.class));
		assertEquals(1, so.intField);
	}
	
	@Test
	void setField_autoUnbox() 
	{
		SimpleObject so = new SimpleObject("test obj", 3);
		so.uncomparableObj = new UncomparableObject(1);
		so.uncomparableObj.recurse = so;
		
		ReflectionObject<SimpleObject> ro = ReflectionObject.create(so);
		
		// Autounboxing of primitives
		assertTrue(ro.setField("uncomparableObj.recurse.intField", -4, Integer.class));
		assertEquals(-4, so.intField);
		assertTrue(ro.setField("uncomparableObj.recurse.setIntField", -5, Integer.class));
		assertEquals(-5, so.intField);

		// Test return vals
		assertTrue(ro.setField("setIntFieldReturn", 7, Integer.class));
		assertEquals(7, so.intField);
		assertFalse(ro.setField("setIntFieldReturn", -4, Integer.class));
		assertEquals(-4, so.intField); // sets anyway
		
		assertTrue(ro.setField("setIntFieldReturnBoxed", 6, Integer.class));
		assertEquals(6, so.intField);
		assertFalse(ro.setField("setIntFieldReturnBoxed", 0, Integer.class)); // returns null
		assertEquals(0, so.intField); // sets anyway
		assertFalse(ro.setField("setIntFieldReturnBoxed", -2, Integer.class));
		assertEquals(-2, so.intField); // sets anyway
	}
	
	@Test
	void setField_badInput() 
	{
		SimpleObject so = new SimpleObject("test obj", 3);
		so.uncomparableObj = new UncomparableObject(1);
		so.uncomparableObj.recurse = so;
		
		ReflectionObject<SimpleObject> ro = ReflectionObject.create(so);
		
		assertFalse(ro.setField("unused", "2", String.class));
		assertFalse(ro.setField("uncomparableObj.unused", "2", String.class));
		assertFalse(ro.setField("uncomparableObj.recurse.unused", "2", String.class));
		assertFalse(ro.setField("uncomparableObj.unused.unused2", "2", String.class));
		
		assertFalse(ro.setField("intField", "2", String.class)); // wrong type
		assertFalse(ro.setField("getIntField", "2", String.class)); // wrong type
		
		assertFalse(ro.setField("stringField", "2", null));
		assertFalse(ro.setField("stringField", null, int.class)); //null val for primitive
		
		assertFalse(ro.setField("intField", null, Integer.class)); // wrong type (boxed vs primitive with null arg)
		assertFalse(ro.setField("getIntField", null, Integer.class)); // wrong type (boxed vs primitive with null arg)
		
		// Set to not unbox primitive wrappers
		ro.setTryUnboxWrappersOfPrimitives(false);
		assertFalse(ro.setField("intField", 2, Integer.class)); // wrong type (boxed vs primitive without unboxing)
		assertFalse(ro.setField("getIntField", 2, Integer.class)); // wrong type (boxed vs primitive without unboxing)
		
	}
	
	@Test
	void sortingValue() 
	{
		ReflectionObject<SimpleObject> ro1 = ReflectionObject.create(new SimpleObject("test obj", 3));
		ReflectionObject<SimpleObject> ro2 = ReflectionObject.create(new SimpleObject("test obj", -8));
		
		ro1.setSortingValue(1);
		assertEquals(1, ro1.getSortingValue());
		ro2.setSortingValue(6);
		assertEquals(6, ro2.getSortingValue());
		
		assertEquals(ro1, ro1.setSortingValueReturnSelf(-1));
		assertEquals(-1, ro1.getSortingValue());
		
		assertTrue(ReflectionObject.sortBySortingValue(ro1, ro2) < 0);
		assertTrue(ReflectionObject.sortBySortingValue(ro2, ro1) > 0);
		assertEquals(0, ReflectionObject.sortBySortingValue(ro1, ro1));
	}
}
