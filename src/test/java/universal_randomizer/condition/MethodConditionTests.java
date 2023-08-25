package universal_randomizer.condition;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import Support.SimpleObject;
import universal_randomizer.wrappers.ReflectionObject;

class MethodConditionTests 
{	
	@Test
	void create() 
	{
		MethodCondition<SimpleObject> mc1 = MethodCondition.create("intBetween2And5Excl");
		assertEquals("intBetween2And5Excl", mc1.getMethod());
		assertEquals(Negate.NO, mc1.getNegate());
		
		MethodCondition<SimpleObject> mc2 = MethodCondition.create("intBetween2And5Excl", Negate.YES);
		assertEquals("intBetween2And5Excl", mc2.getMethod());
		assertEquals(Negate.YES, mc2.getNegate());
	}
	
	@Test
	void evaluate() 
	{
		ReflectionObject<SimpleObject> testObj3 = ReflectionObject.create(new SimpleObject("test obj", 3));
		ReflectionObject<SimpleObject> testObj5 = ReflectionObject.create(new SimpleObject("test obj", 5));

		Condition<SimpleObject> between2and5 = MethodCondition.create("intBetween2And5Excl");
		Condition<SimpleObject> notBetween2and5 = MethodCondition.create("intBetween2And5Excl", Negate.YES);
		
		assertTrue(between2and5.evaluate(testObj3), "Method compare between2and5 failed - 3 should be but was not (returned false)");
		assertFalse(notBetween2and5.evaluate(testObj3), "Method compare notBetween2and5 failed - 3 should be but was not (returned true)");
		assertFalse(between2and5.evaluate(testObj5), "Method compare between2and5 failed - 5 should not be but was (returned true)");
		assertTrue(notBetween2and5.evaluate(testObj5), "Method compare notBetween2and5 failed - 5 should not be but was (returned false)");
	}
	
	@Test
	void evaluate_bad_method() 
	{
		ReflectionObject<SimpleObject> testObj = ReflectionObject.create(new SimpleObject("test obj", 3));

		Condition<SimpleObject> nonExistent = MethodCondition.create("nonExistent");
		Condition<SimpleObject> nonBoolean = MethodCondition.create("getIntField");
		Condition<SimpleObject> wrongArgs = MethodCondition.create("intIsEqualTo");
		
		assertFalse(nonExistent.evaluate(testObj), "Method compare non existent function should return false");
		assertFalse(nonBoolean.evaluate(testObj), "Method compare non boolean function should return false");
		assertFalse(wrongArgs.evaluate(testObj), "Method compare boolean function with args should return false");
	}
}
