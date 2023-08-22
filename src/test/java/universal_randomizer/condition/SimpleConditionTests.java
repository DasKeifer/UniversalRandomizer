package universal_randomizer.condition;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;

import Support.SimpleObject;
import Support.UncomparableObject;
import universal_randomizer.wrappers.ReflectionObject;

class SimpleConditionTests 
{	
	// TODO Consturctors
	
	@Test
	void evaluate_eq_neq() 
	{
		ReflectionObject<SimpleObject> testObj3 = new ReflectionObject<>(new SimpleObject("test obj", 3));
		ReflectionObject<SimpleObject> testObj5 = new ReflectionObject<>(new SimpleObject("test obj", 5));

		Condition<SimpleObject> eq5 = SimpleCondition.create("intField", Negate.NO, Comparison.EQUAL, 5);
		Condition<SimpleObject> neq5 = SimpleCondition.create("intField", Negate.YES, Comparison.EQUAL, 5);
		
		assertTrue(eq5.evaluate(testObj5), "Simple compare eq5 failed - 5 should be equal but wasn't (returned false)");
		assertFalse(neq5.evaluate(testObj5), "Simple compare neq5 failed - 5 should be equal but wasn't (returned true)");
		assertFalse(eq5.evaluate(testObj3), "Simple compare eq5 failed - 3 should NOT be equal but was (returned true)");
		assertTrue(neq5.evaluate(testObj3), "Simple compare neq5 failed - 3 should NOT be equal but was (returned false)");
	}
	
	@Test
	void evaluate_lt_lte_nlt_nlte() 
	{
		ReflectionObject<SimpleObject> testObj3 = new ReflectionObject<>(new SimpleObject("test obj", 3));
		ReflectionObject<SimpleObject> testObj5 = new ReflectionObject<>(new SimpleObject("test obj", 5));
		ReflectionObject<SimpleObject> testObj7 = new ReflectionObject<>(new SimpleObject("test obj", 7));

		Condition<SimpleObject> lt5 = SimpleCondition.create("intField", Negate.NO, Comparison.LESS_THAN, 5);
		Condition<SimpleObject> nlt5 = SimpleCondition.create("intField", Negate.YES, Comparison.LESS_THAN, 5);
		Condition<SimpleObject> lte5 = SimpleCondition.create("intField", Negate.NO, Comparison.LESS_THAN_OR_EQUAL, 5);
		Condition<SimpleObject> nlte5 = SimpleCondition.create("intField", Negate.YES, Comparison.LESS_THAN_OR_EQUAL, 5);
		
		assertTrue(lt5.evaluate(testObj3), "Simple compare lt5 failed - 3 should be less than 5 but wasn't (returned false)");
		assertFalse(nlt5.evaluate(testObj3), "Simple compare nlt5 failed - 3 should be less than 5 but wasn't (returned true)");
		assertTrue(lte5.evaluate(testObj3), "Simple compare lte5 failed - 3 should be less than or equal to 5 but wasn't (returned false)");
		assertFalse(nlte5.evaluate(testObj3), "Simple compare nlte5 failed - 3 should be less than or equal to 5 but wasn't (returned true)");
		
		assertFalse(lt5.evaluate(testObj5), "Simple compare lt5 failed - 5 should NOT be less than 5 but was (returned true)");
		assertTrue(nlt5.evaluate(testObj5), "Simple compare nlt5 failed - 5 should NOT be less than 5 but was (returned false)");
		assertTrue(lte5.evaluate(testObj5), "Simple compare lte5 failed - 5 should be less than or equal to 5 but wasn't (returned false)");
		assertFalse(nlte5.evaluate(testObj5), "Simple compare nlte5 failed - 5 should be less than or equal to 5 but wasn't (returned true)");
		
		assertFalse(lt5.evaluate(testObj7), "Simple compare lt5 failed - 7 should NOT be less than 5 but was (returned true)");
		assertTrue(nlt5.evaluate(testObj7), "Simple compare nlt5 failed - 7 should NOT be less than 5 but was (returned false)");
		assertFalse(lte5.evaluate(testObj7), "Simple compare lte5 failed - 7 should NOT be less than or equal to 5 but was (returned false)");
		assertTrue(nlte5.evaluate(testObj7), "Simple compare nlte5 failed - 7 should NOT be less than or equal to 5 but was (returned true)");
	}
	
	@Test
	void evaluate_gt_gte_ngt_ngte() 
	{
		ReflectionObject<SimpleObject> testObj3 = new ReflectionObject<>(new SimpleObject("test obj", 3));
		ReflectionObject<SimpleObject> testObj5 = new ReflectionObject<>(new SimpleObject("test obj", 5));
		ReflectionObject<SimpleObject> testObj7 = new ReflectionObject<>(new SimpleObject("test obj", 7));

		Condition<SimpleObject> gt5 = SimpleCondition.create("intField", Negate.NO, Comparison.GREATER_THAN, 5);
		Condition<SimpleObject> ngt5 = SimpleCondition.create("intField", Negate.YES, Comparison.GREATER_THAN, 5);
		Condition<SimpleObject> gte5 = SimpleCondition.create("intField", Negate.NO, Comparison.GREATER_THAN_OR_EQUAL, 5);
		Condition<SimpleObject> ngte5 = SimpleCondition.create("intField", Negate.YES, Comparison.GREATER_THAN_OR_EQUAL, 5);

		assertFalse(gt5.evaluate(testObj3), "Simple compare gt5 failed - 3 should NOT be greater than 5 but was (returned true)");
		assertTrue(ngt5.evaluate(testObj3), "Simple compare ngt5 failed - 3 should NOT be greater than 5 but was (returned false)");
		assertFalse(gte5.evaluate(testObj3), "Simple compare gte5 failed - 3 should NOT be greater than or equal to 5 but was (returned false)");
		assertTrue(ngte5.evaluate(testObj3), "Simple compare ngte5 failed - 3 should NOT be greater than or equal to 5 but was (returned true)");
		
		assertFalse(gt5.evaluate(testObj5), "Simple compare gt5 failed - 5 should NOT be greater than 5 but was (returned true)");
		assertTrue(ngt5.evaluate(testObj5), "Simple compare ngt5 failed - 5 should NOT be greater than 5 but was (returned false)");
		assertTrue(gte5.evaluate(testObj5), "Simple compare gte5 failed - 5 should be greater than or equal to 5 but wasn't (returned false)");
		assertFalse(ngte5.evaluate(testObj5), "Simple compare ngte5 failed - 5 should be greater than or equal to 5 but wasn't (returned true)");

		assertTrue(gt5.evaluate(testObj7), "Simple compare gt5 failed - 7 should be greater than 5 but wasn't (returned false)");
		assertFalse(ngt5.evaluate(testObj7), "Simple compare ngt5 failed - 7 should be greater than 5 but wasn't (returned true)");
		assertTrue(gte5.evaluate(testObj7), "Simple compare gte5 failed - 7 should be greater than or equal to 5 but wasn't (returned false)");
		assertFalse(ngte5.evaluate(testObj7), "Simple compare ngte5 failed - 7 should be greater than or equal to 5 but wasn't (returned true)");
	}
	
	@Test
	void evaluate_null() 
	{
		@SuppressWarnings("unchecked")
		ReflectionObject<SimpleObject> ro = mock(ReflectionObject.class);
		when(ro.getField(anyString())).thenReturn(null);
		
		Condition<SimpleObject> eq5 = SimpleCondition.create("intField", Negate.NO, Comparison.EQUAL, 5);
		assertFalse(eq5.evaluate(ro));
	}
	
	@Test
	void evaluate_wrong_object() 
	{
		ReflectionObject<SimpleObject> testObj = new ReflectionObject<>(new SimpleObject("test obj", 0));
		testObj.getObject().uncomparableObj = new UncomparableObject(5);
		Condition<SimpleObject> eq5 = SimpleCondition.create("uncomparableObj", Negate.NO, Comparison.EQUAL, 5);
		assertFalse(eq5.evaluate(testObj)); // todo should error somehow?
	}
}
