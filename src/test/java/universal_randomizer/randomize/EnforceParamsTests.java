package universal_randomizer.randomize;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;

import Support.SimpleObject;
import universal_randomizer.user_object_apis.Condition;

class EnforceParamsTests {

	@Test
	void constructor_nonNull() 
	{
		final int MAX_RETRIES = 2;
		final int MAX_RESETS = 3;
		
		@SuppressWarnings("unchecked")
		Condition<SimpleObject> testCond = mock(Condition.class);
		
		EnforceParams<SimpleObject> test = new EnforceParams<>(testCond, MAX_RETRIES, MAX_RESETS);
		
		assertEquals(MAX_RETRIES, test.getMaxRetries());
		assertEquals(MAX_RESETS, test.getMaxResets());
	}
	
	@Test
	void constructor_null() 
	{
		final int MAX_RETRIES = 2;
		final int MAX_RESETS = 3;
		
		EnforceParams<SimpleObject> test = new EnforceParams<>(null, MAX_RETRIES, MAX_RESETS);
		
		assertEquals(0, test.getMaxRetries());
		assertEquals(0, test.getMaxResets());
	}
	
	@Test
	void noEnforce() 
	{
		EnforceParams<SimpleObject> testNone = EnforceParams.createNoEnforce();
		assertEquals(0, testNone.getMaxRetries());
		assertEquals(0, testNone.getMaxResets());
	}
	
	@Test
	void evaluateEnforce() 
	{
		final int MAX_RETRIES = 2;
		final int MAX_RESETS = 3;
		
		@SuppressWarnings("unchecked")
		Condition<SimpleObject> testCond = mock(Condition.class);
		when(testCond.evaluate(any())).thenReturn(true).thenReturn(false);
		
		EnforceParams<SimpleObject> test = new EnforceParams<>(testCond, MAX_RETRIES, MAX_RESETS);
		
		assertTrue(test.evaluateEnforce(null));	
		assertFalse(test.evaluateEnforce(null));
		
		EnforceParams<SimpleObject> testNone = EnforceParams.createNoEnforce();
		assertTrue(testNone.evaluateEnforce(null));
	}

	@Test
	void copy_noEnforce() 
	{
		final int MAX_RETRIES = 2;
		final int MAX_RESETS = 3;
		EnforceParams<SimpleObject> test = new EnforceParams<>(null, MAX_RETRIES, MAX_RESETS);
		
		assertTrue(test.evaluateEnforce(null));
	}
}
