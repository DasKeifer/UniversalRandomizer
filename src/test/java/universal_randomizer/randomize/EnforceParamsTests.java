package universal_randomizer.randomize;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;

import Support.SimpleObject;
import universal_randomizer.condition.Condition;

class EnforceParamsTests {

	@Test
	void constructor_nonNull() 
	{
		final int MAX_RETRIES = 2;
		final int MAX_RESETS = 3;
		
		@SuppressWarnings("unchecked")
		Condition<SimpleObject> testCond = mock(Condition.class);
		when(testCond.evaluate(any())).thenReturn(true);
		
		@SuppressWarnings("unchecked")
		Condition<SimpleObject> testCondCopy = mock(Condition.class);
		when(testCondCopy.evaluate(any())).thenReturn(false);
		
		// Set up chain to test dependencies
		when(testCond.copy()).thenReturn(testCondCopy);
		
		EnforceParams<SimpleObject> test = new EnforceParams<>(testCond, MAX_RETRIES, MAX_RESETS);
		
		assertEquals(MAX_RETRIES, test.getMaxRetries());
		assertEquals(MAX_RESETS, test.getMaxResets());
		
		// Ensure the copied one is used it the copy
		assertFalse(test.evaluateEnforce(null));
		verify(testCond, times(1)).copy();
		verify(testCondCopy, times(0)).copy();
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
		when(testCond.copy()).thenReturn(testCond);
		
		EnforceParams<SimpleObject> test = new EnforceParams<>(testCond, MAX_RETRIES, MAX_RESETS);
		verify(testCond, times(1)).copy();
		
		assertTrue(test.evaluateEnforce(null));	
		assertFalse(test.evaluateEnforce(null));
		
		EnforceParams<SimpleObject> testNone = EnforceParams.createNoEnforce();
		assertTrue(testNone.evaluateEnforce(null));
	}

	@Test
	void copy() 
	{
		final int MAX_RETRIES = 2;
		final int MAX_RESETS = 3;
		
		@SuppressWarnings("unchecked")
		Condition<SimpleObject> testCondOrig = mock(Condition.class);
		when(testCondOrig.evaluate(any())).thenReturn(true);

		@SuppressWarnings("unchecked")
		Condition<SimpleObject> testCondTestObj = mock(Condition.class);
		when(testCondTestObj.evaluate(any())).thenReturn(true);
		
		@SuppressWarnings("unchecked")
		Condition<SimpleObject> testCondTestObjCopy = mock(Condition.class);
		when(testCondTestObjCopy.evaluate(any())).thenReturn(false);

		// Setup to return the copy
		when(testCondOrig.copy()).thenReturn(testCondTestObj);
		when(testCondTestObj.copy()).thenReturn(testCondTestObjCopy);
		
		EnforceParams<SimpleObject> test = new EnforceParams<>(testCondOrig, MAX_RETRIES, MAX_RESETS);
		EnforceParams<SimpleObject> copy = test.copy();
		verify(testCondOrig, times(1)).copy();
		verify(testCondTestObj, times(1)).copy();
		verify(testCondTestObjCopy, times(0)).copy();
		
		assertEquals(test.getMaxRetries(), copy.getMaxRetries());
		assertEquals(test.getMaxResets(), copy.getMaxResets());
		assertFalse(copy.evaluateEnforce(null));
	}

	@Test
	void copy_noEnforce() 
	{
		final int MAX_RETRIES = 2;
		final int MAX_RESETS = 3;
		EnforceParams<SimpleObject> test = new EnforceParams<>(null, MAX_RETRIES, MAX_RESETS);
		EnforceParams<SimpleObject> copy = test.copy();
		
		assertEquals(0, copy.getMaxRetries());
		assertEquals(0, copy.getMaxResets());
		assertTrue(copy.evaluateEnforce(null));
	}
}
