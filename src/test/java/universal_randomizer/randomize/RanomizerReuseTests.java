package universal_randomizer.randomize;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.junit.jupiter.api.Test;
import org.mockito.AdditionalAnswers;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import Support.SimpleObject;
import Support.SimpleObjectUtils;
import universal_randomizer.Pool;
import universal_randomizer.condition.Compare;
import universal_randomizer.condition.Condition;
import universal_randomizer.condition.Negate;
import universal_randomizer.condition.SimpleCondition;
import universal_randomizer.wrappers.ReflectionObject;

class RanomizerReuseTests {

	final List<Integer> NON_DUPLICATE_VALS = List.of(1, -4, 5, 99);
	final List<Integer> DUPLICATE_VALS = List.of(1, -4, 5, 1, 99, 1, 5);
	final Integer NON_EXISTING_VAL = 7;
	
	private List<ReflectionObject<SimpleObject>> createSimpleObjects(int number)
	{
		List<ReflectionObject<SimpleObject>> list = new LinkedList<>();
		for (int i = 0; i < number; i++)
		{
			list.add(new ReflectionObject<>(new SimpleObject("name" + i, i * 100)));
		}
		return list;
	}
	
	@Test
	void create() 
	{
		@SuppressWarnings("unchecked")
		Pool<Integer> pool = mock(Pool.class);
		when(pool.copy()).thenReturn(pool);
		
		Random rand = mock(Random.class);
		
		@SuppressWarnings("unchecked")
		EnforceActions<SimpleObject> enforceAction = mock(EnforceActions.class);
		when(enforceAction.copy()).thenReturn(enforceAction);
    	
		
    	try (MockedConstruction<Random> mocked = mockConstruction(Random.class)) 
    	{
    		RandomizerResuse.create("test", pool, rand, enforceAction);
    		assertEquals(0, mocked.constructed().size());
    	}
    	verify(pool, times(1)).copy();
    	verify(enforceAction, times(1)).copy();
    	

    	try (MockedConstruction<Random> mocked = mockConstruction(Random.class)) 
    	{
    		RandomizerResuse.createWithPoolAndEnforce("test", pool, rand);
    		assertEquals(0, mocked.constructed().size());
    	}
    	verify(pool, times(2)).copy();
    	verify(enforceAction, times(1)).copy();


    	try (MockedConstruction<Random> mocked = mockConstruction(Random.class)) 
    	{
    		RandomizerResuse.createPoolFromStream("test", rand, enforceAction);
    		assertEquals(0, mocked.constructed().size());
    	}
    	verify(pool, times(2)).copy();
    	verify(enforceAction, times(2)).copy();
    	
    	
    	try (MockedConstruction<Random> mocked = mockConstruction(Random.class)) 
    	{
    		RandomizerResuse.createPoolFromStreamNoEnforce("test", rand);
    		assertEquals(0, mocked.constructed().size());
    	}
    	verify(pool, times(2)).copy();
    	verify(enforceAction, times(2)).copy();
	}
	
	@Test
	void seed() 
	{
		Randomizer<SimpleObject, Integer> test = RandomizerResuse.create("intField", null, null, null);
		test.seed(0);
		test.seed(new Random());
	}

	@Test
	void perform_noEnforce_basic() 
	{
		final int POOL_VAL = 5;
		final int LIST_SIZE = 10;
		
		// Set expectations
		List<Integer> expected = new LinkedList<>();
		for (int i = 0; i < LIST_SIZE; i++)
		{
			expected.add(POOL_VAL);
		}
		
		// Setup mocks
		Random rand = mock(Random.class);
		when(rand.nextInt(anyInt())).thenReturn(0);
		
		@SuppressWarnings("unchecked")
		Pool<Integer> pool = mock(Pool.class);
		when(pool.peek(any())).thenReturn(POOL_VAL);
		when(pool.copy()).thenReturn(pool);

		// Create test data and object
		List<ReflectionObject<SimpleObject>> list = createSimpleObjects(LIST_SIZE);
		Randomizer<SimpleObject, Integer> test = RandomizerResuse.create("intField", pool, rand, null);

		// Perform test and check results
		assertTrue(test.perform(list.stream()));
		List<Integer> results = SimpleObjectUtils.toIntFieldList(list);
		assertIterableEquals(expected, results);
	}

	@Test
	void perform_noEnforce_someFailed() 
	{
		final int LIST_SIZE = 10;
		final List<Integer> POOL_VALS =     Arrays.asList(0, 1, 2, null, 4, null, 6, 7, 8, null);
		final List<Integer> EXPECTED_VALS = Arrays.asList(0, 1, 2, 300,  4, 500,  6, 7, 8, 900);
		
		// Set mocks
		Random rand = mock(Random.class);
		when(rand.nextInt(anyInt())).thenReturn(0);
		
		@SuppressWarnings("unchecked")
		Pool<Integer> pool = mock(Pool.class);
		when(pool.peek(any())).thenAnswer(AdditionalAnswers.returnsElementsOf(POOL_VALS));
		when(pool.copy()).thenReturn(pool);
		
		// Create test data and object
		List<ReflectionObject<SimpleObject>> list = createSimpleObjects(LIST_SIZE);
		Randomizer<SimpleObject, Integer> test = RandomizerResuse.create("intField", pool, rand, null);

		// Perform test and check results
		assertFalse(test.perform(list.stream()));
		List<Integer> results = SimpleObjectUtils.toIntFieldList(list);
		assertIterableEquals(EXPECTED_VALS, results);
	}
	
	@Test
	void perform_enforce_null() 
	{
		final int EXCLUDED_VAL = 5;
		final int LIST_SIZE = 10;
		final List<Integer> POOL_VALS =     Arrays.asList(0, 1, null, 3, 4, null, 6, 7, 8, 9);
		// 5 will be excluded by the enforce
		final List<Integer> EXPECTED_VALS = Arrays.asList(0, 1, 200,  3, 4, 500,  6, 7, 8, 9);
		
		// Setup mocks
		Random rand = mock(Random.class);
		when(rand.nextInt(anyInt())).thenReturn(0);
		
		@SuppressWarnings("unchecked")
		Pool<Integer> pool = mock(Pool.class);
		when(pool.peek(any())).thenAnswer(AdditionalAnswers.returnsElementsOf(POOL_VALS));
		when(pool.copy()).thenReturn(pool);

		// Create test data and object
		Condition<SimpleObject> neq5 = new SimpleCondition<SimpleObject, Integer>("intField", Negate.YES, Compare.EQUAL, EXCLUDED_VAL);
		EnforceActions<SimpleObject> enforce = new EnforceActions<>(neq5, 2, 0);
		
		List<ReflectionObject<SimpleObject>> list = createSimpleObjects(LIST_SIZE);
		Randomizer<SimpleObject, Integer> test = RandomizerResuse.create("intField", pool, rand, enforce);

		// Perform test and check results
		assertFalse(test.perform(list.stream()));
		List<Integer> results = SimpleObjectUtils.toIntFieldList(list);
		assertIterableEquals(EXPECTED_VALS, results);
	}
	
	@Test
	void perform_enforce_retries() 
	{
		final int EXCLUDED_VAL = 5;
		final int LIST_SIZE = 10;
		final List<Integer> POOL_VALS =     Arrays.asList(0, EXCLUDED_VAL, 1, EXCLUDED_VAL, 2, 
				EXCLUDED_VAL, 3, EXCLUDED_VAL, 4, EXCLUDED_VAL, 6, 7, 8, 9, 10);
		// 5 will be excluded by the enforce
		final List<Integer> EXPECTED_VALS = Arrays.asList(0,               1,               2, 
				              3,               4,               6, 7, 8, 9, 10);
		
		// Setup mocks
		Random rand = mock(Random.class);
		when(rand.nextInt(anyInt())).thenReturn(0);
		
		@SuppressWarnings("unchecked")
		Pool<Integer> pool = mock(Pool.class);
		when(pool.peek(any())).thenAnswer(AdditionalAnswers.returnsElementsOf(POOL_VALS));
		when(pool.copy()).thenReturn(pool);

		// Create test data and object
		Condition<SimpleObject> neq5 = new SimpleCondition<SimpleObject, Integer>("intField", Negate.YES, Compare.EQUAL, EXCLUDED_VAL);
		EnforceActions<SimpleObject> enforce = new EnforceActions<>(neq5, 2, 2);
		
		List<ReflectionObject<SimpleObject>> list = createSimpleObjects(LIST_SIZE);
		Randomizer<SimpleObject, Integer> test = RandomizerResuse.create("intField", pool, rand, enforce);

		// Perform test and check results
		assertTrue(test.perform(list.stream()));
		List<Integer> results = SimpleObjectUtils.toIntFieldList(list);
		assertIterableEquals(EXPECTED_VALS, results);
	}
	
	@Test
	void perform_enforce_exhaustRetries_noResets() 
	{
		final int EXCLUDED_VAL = 5;
		final int LIST_SIZE = 10;
		final List<Integer> POOL_VALS =     Arrays.asList(0, 1, 2, 3, 4, EXCLUDED_VAL, EXCLUDED_VAL, EXCLUDED_VAL, 6, 7, 8, 9);
		// 5 will be excluded by the enforce until it gives up and then ignores the condition
		final List<Integer> EXPECTED_VALS = Arrays.asList(0, 1, 2, 3, 4, EXCLUDED_VAL,                             6, 7, 8, 9);
		
		// Setup mocks
		Random rand = mock(Random.class);
		when(rand.nextInt(anyInt())).thenReturn(0);
		
		@SuppressWarnings("unchecked")
		Pool<Integer> pool = mock(Pool.class);
		when(pool.peek(any())).thenAnswer(AdditionalAnswers.returnsElementsOf(POOL_VALS));
		when(pool.copy()).thenReturn(pool);

		// Create test data and object
		Condition<SimpleObject> neq5 = new SimpleCondition<SimpleObject, Integer>("intField", Negate.YES, Compare.EQUAL, EXCLUDED_VAL);
		EnforceActions<SimpleObject> enforce = new EnforceActions<>(neq5, 2, 0);
		
		List<ReflectionObject<SimpleObject>> list = createSimpleObjects(LIST_SIZE);
		Randomizer<SimpleObject, Integer> test = RandomizerResuse.create("intField", pool, rand, enforce);

		// Perform test and check results
		assertFalse(test.perform(list.stream()));
		List<Integer> results = SimpleObjectUtils.toIntFieldList(list);
		assertIterableEquals(EXPECTED_VALS, results);
	}
	
	@Test
	void perform_enforce_exhaustRetries_resets() 
	{
		final int EXCLUDED_VAL = 5;
		final int LIST_SIZE = 10;
		final List<Integer> POOL_VALS =     Arrays.asList(0, 1, 2, 3, 4, EXCLUDED_VAL, EXCLUDED_VAL, EXCLUDED_VAL, 6, 7, 8, 9, // reset at this point 
														  10, 11, 12, EXCLUDED_VAL, 13, 14, 15, 16, 17, 18, 19);
		// Reset will complete out the first attempt before resetting
		// 5 will be excluded by the enforce
		final List<Integer> EXPECTED_VALS = Arrays.asList(10, 11, 12,               13, 14, 15, 16, 17, 18, 19);
		
		// Setup mocks
		Random rand = mock(Random.class);
		when(rand.nextInt(anyInt())).thenReturn(0);
		
		@SuppressWarnings("unchecked")
		Pool<Integer> pool = mock(Pool.class);
		when(pool.peek(any())).thenAnswer(AdditionalAnswers.returnsElementsOf(POOL_VALS));
		when(pool.copy()).thenReturn(pool);

		// Create test data and object
		Condition<SimpleObject> neq5 = new SimpleCondition<SimpleObject, Integer>("intField", Negate.YES, Compare.EQUAL, EXCLUDED_VAL);
		EnforceActions<SimpleObject> enforce = new EnforceActions<>(neq5, 2, 2);
		
		List<ReflectionObject<SimpleObject>> list = createSimpleObjects(LIST_SIZE);
		Randomizer<SimpleObject, Integer> test = RandomizerResuse.create("intField", pool, rand, enforce);

		// Perform test and check results
		assertTrue(test.perform(list.stream()));
		List<Integer> results = SimpleObjectUtils.toIntFieldList(list);
		assertIterableEquals(EXPECTED_VALS, results);
	}
	
	@Test
	void perform_noPool() 
	{
		final int EXCLUDED_VAL = 5;
		final int LIST_SIZE = 10;
		final List<Integer> POOL_VALS =     Arrays.asList(0, 1, 2, 3, 4, EXCLUDED_VAL, 6, 7, 8, 9, 10);
		// 5 will be excluded by the enforce until it gives up and then ignores the condition
		final List<Integer> EXPECTED_VALS = Arrays.asList(0, 1, 2, 3, 4,               6, 7, 8, 9, 10);

		// Create test data and object
		Condition<SimpleObject> neq5 = new SimpleCondition<SimpleObject, Integer>("intField", Negate.YES, Compare.EQUAL, EXCLUDED_VAL);
		EnforceActions<SimpleObject> enforce = new EnforceActions<>(neq5, 2, 0);
		
		List<ReflectionObject<SimpleObject>> list = createSimpleObjects(LIST_SIZE);
		Randomizer<SimpleObject, Integer> test = null;
		
	    try (@SuppressWarnings("rawtypes")
		MockedStatic<Pool> intPool = Mockito.mockStatic(Pool.class)) 
	    {
			// Setup static function
			@SuppressWarnings("unchecked")
			Pool<Integer> pool = mock(Pool.class);
			when(pool.peek(any())).thenAnswer(AdditionalAnswers.returnsElementsOf(POOL_VALS));
			when(pool.copy()).thenReturn(pool);
			
	    	intPool.when(() -> Pool.createFromStream(any(), any()))
	          .thenReturn(pool);
	    	
	    	try (MockedConstruction<Random> mocked = mockConstruction(Random.class)) 
	    	{
	    		Random rand = new Random();
	    		when(rand.nextInt(anyInt())).thenReturn(0);
	    	
	    		test = RandomizerResuse.create("intField", null, null, enforce);
   		 	}

			// Perform test and check results
			assertTrue(test.perform(list.stream()));
	    }
	    
		List<Integer> results = SimpleObjectUtils.toIntFieldList(list);
		assertIterableEquals(EXPECTED_VALS, results);
	}
}
