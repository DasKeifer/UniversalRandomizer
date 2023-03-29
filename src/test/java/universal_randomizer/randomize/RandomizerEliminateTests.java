package universal_randomizer.randomize;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.junit.jupiter.api.Test;
import org.mockito.AdditionalAnswers;
import org.mockito.MockedConstruction;

import Support.ExposeRandomizerEliminate;
import Support.RandomizerCommonTestsCreate;
import Support.SimpleObject;
import Support.SimpleObjectUtils;
import universal_randomizer.Pool;
import universal_randomizer.condition.Compare;
import universal_randomizer.condition.Condition;
import universal_randomizer.condition.Negate;
import universal_randomizer.condition.SimpleCondition;
import universal_randomizer.wrappers.ReflectionObject;

class RandomizerEliminateTests {

	//TODO: Update to actually be for elimate version
	
	final List<Integer> NON_DUPLICATE_VALS = List.of(1, -4, 5, 99);
	final List<Integer> DUPLICATE_VALS = List.of(1, -4, 5, 1, 99, 1, 5);
	final Integer NON_EXISTING_VAL = 7;

	private static RandomizerCommonTestsCreate<SimpleObject, Integer> randElimCreateFn = (p1, p2, p3, p4) -> { return RandomizerEliminate.create(p1, p2, p3, p4, null);};
	
	@Test
	void create() 
	{
		@SuppressWarnings("unchecked")
		Pool<Integer> pool = mock(Pool.class);
		when(pool.copy()).thenReturn(pool);
		
		Random rand = mock(Random.class);
		
		@SuppressWarnings("unchecked")
		EnforceParams<SimpleObject> enforceAction = mock(EnforceParams.class);
		when(enforceAction.copy()).thenReturn(enforceAction);

		EliminateParams poolAction = mock(EliminateParams.class);
		when(poolAction.copy()).thenReturn(poolAction);
    	
		
    	try (MockedConstruction<Random> mocked = mockConstruction(Random.class)) 
    	{
    		RandomizerEliminate.create("test", pool, rand, enforceAction, poolAction);
    		assertEquals(0, mocked.constructed().size());
    	}
    	verify(pool, times(1)).copy();
    	verify(enforceAction, times(1)).copy();
    	verify(poolAction, times(1)).copy();
    	

    	try (MockedConstruction<Random> mocked = mockConstruction(Random.class)) 
    	{
    		RandomizerEliminate.createWithPoolNoEnforce("test", pool, rand);
    		assertEquals(0, mocked.constructed().size());
    	}
    	verify(pool, times(2)).copy();
    	verify(enforceAction, times(1)).copy();
    	verify(poolAction, times(1)).copy();


    	try (MockedConstruction<Random> mocked = mockConstruction(Random.class)) 
    	{
    		RandomizerEliminate.createPoolFromStream("test", rand, enforceAction, poolAction);
    		assertEquals(0, mocked.constructed().size());
    	}
    	verify(pool, times(2)).copy();
    	verify(enforceAction, times(2)).copy();
    	verify(poolAction, times(2)).copy();
    	
    	
    	try (MockedConstruction<Random> mocked = mockConstruction(Random.class)) 
    	{
    		RandomizerEliminate.createPoolFromStreamNoEnforce("test", rand);
    		assertEquals(0, mocked.constructed().size());
    	}
    	verify(pool, times(2)).copy();
    	verify(enforceAction, times(2)).copy();
    	verify(poolAction, times(2)).copy();
	}

	@Test
	void perform_noEnforce_basic() 
	{
		CommonRandomizerTests.perform_noEnforce_basic(randElimCreateFn);
	}

	@Test
	void perform_noEnforce_someFailed() 
	{
		CommonRandomizerTests.perform_noEnforce_someFailed(randElimCreateFn);
	}
	
	@Test
	void perform_enforce_null() 
	{
		CommonRandomizerTests.perform_enforce_null(randElimCreateFn);
	}
	
	@Test
	void perform_enforce_retries() 
	{
		CommonRandomizerTests.perform_enforce_retries(randElimCreateFn);
	}
	
	@Test
	void perform_enforce_exhaustRetries_noResets() 
	{
		CommonRandomizerTests.perform_enforce_exhaustRetries_noResets(randElimCreateFn);
	}
	
	@Test
	void perform_enforce_exhaustRetries_resets() 
	{
		CommonRandomizerTests.perform_enforce_exhaustRetries_resets(randElimCreateFn);
	}
	
	@Test
	void perform_noPool() 
	{
		CommonRandomizerTests.perform_noPool(randElimCreateFn);
	}
	
	@Test
	void perform_pools_noEnforce() 
	{
		final int LIST_SIZE = 10;
		final List<Integer> POOL_1_VALS =     Arrays.asList(0, 1, 2, 3, 4, null);
		final List<Integer> POOL_2_VALS =     Arrays.asList(			   5, 6, 7, 8, 9, null);
		
		// 5 will be excluded by the enforce
		final List<Integer> EXPECTED_VALS = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
		
		// Setup mocks
		Random rand = mock(Random.class);
		when(rand.nextInt(anyInt())).thenReturn(0);
		
		@SuppressWarnings("unchecked")
		Pool<Integer> poolBase = mock(Pool.class);
		@SuppressWarnings("unchecked")
		Pool<Integer> pool1 = mock(Pool.class);
		@SuppressWarnings("unchecked")
		Pool<Integer> pool2 = mock(Pool.class);
		
		when(poolBase.copy()).thenReturn(pool1);
		when(pool1.peek(any())).thenAnswer(AdditionalAnswers.returnsElementsOf(POOL_1_VALS));
		when(pool1.copy()).thenReturn(pool2);
		when(pool2.peek(any())).thenAnswer(AdditionalAnswers.returnsElementsOf(POOL_2_VALS));
		when(pool2.copy()).thenReturn(null);

		// Create test data and object
		EliminateParams elimParams = new EliminateParams(2);
		
		List<ReflectionObject<SimpleObject>> list = CommonRandomizerTests.createSimpleObjects(LIST_SIZE);
		Randomizer<SimpleObject, Integer> test = RandomizerEliminate.create("intField", poolBase, rand, null, elimParams);

		// Perform test and check results
		assertTrue(test.perform(list.stream()));
		List<Integer> results = SimpleObjectUtils.toIntFieldList(list);
		assertIterableEquals(EXPECTED_VALS, results);
	}
	
	@Test
	void perform_pools_enforce() 
	{
		final int EXCLUDED_VAL = 5;
		final int LIST_SIZE = 10;
		final List<Integer> POOL_1_VALS =     Arrays.asList(0, 1, 2, 3, 4, EXCLUDED_VAL,   7, 8, EXCLUDED_VAL,    10);
		final List<Integer> POOL_2_VALS =     Arrays.asList(							6,                     9);
		
		// 5 will be excluded by the enforce
		final List<Integer> EXPECTED_VALS = Arrays.asList(0, 1, 2, 3, 4,                6, 7, 8,               9, 10);
		
		// Setup mocks
		Random rand = mock(Random.class);
		when(rand.nextInt(anyInt())).thenReturn(0);
		
		@SuppressWarnings("unchecked")
		Pool<Integer> poolBase = mock(Pool.class);
		@SuppressWarnings("unchecked")
		Pool<Integer> pool1 = mock(Pool.class);
		@SuppressWarnings("unchecked")
		Pool<Integer> pool2 = mock(Pool.class);
		
		when(poolBase.copy()).thenReturn(pool1);
		when(pool1.peek(any())).thenAnswer(AdditionalAnswers.returnsElementsOf(POOL_1_VALS));
		when(pool1.copy()).thenReturn(pool2);
		when(pool2.peek(any())).thenAnswer(AdditionalAnswers.returnsElementsOf(POOL_2_VALS));
		when(pool2.copy()).thenReturn(null);

		// Create test data and object
		Condition<SimpleObject> neq5 = new SimpleCondition<SimpleObject, Integer>("intField", Negate.YES, Compare.EQUAL, EXCLUDED_VAL);
		EnforceParams<SimpleObject> enforce = new EnforceParams<>(neq5, 0, 0);
		EliminateParams elimParams = new EliminateParams(2);
		
		List<ReflectionObject<SimpleObject>> list = CommonRandomizerTests.createSimpleObjects(LIST_SIZE);
		Randomizer<SimpleObject, Integer> test = RandomizerEliminate.create("intField", poolBase, rand, enforce, elimParams);

		// Perform test and check results
		assertTrue(test.perform(list.stream()));
		List<Integer> results = SimpleObjectUtils.toIntFieldList(list);
		assertIterableEquals(EXPECTED_VALS, results);
	}
	
	@Test
	void perform_poolsExhaust_enforce() 
	{
		final int EXCLUDED_VAL = 5;
		final int LIST_SIZE = 10;
		final List<Integer> POOL_1_VALS =     Arrays.asList(0, 1, 2, 3, 4, EXCLUDED_VAL,    7, 8, EXCLUDED_VAL,    10);
		final List<Integer> POOL_2_VALS =     Arrays.asList(			      EXCLUDED_VAL,                     9);
		
		// 5 will appear because we exhausted the pools
		final List<Integer> EXPECTED_VALS = Arrays.asList(0, 1, 2, 3, 4,      EXCLUDED_VAL, 7, 8,               9, 10);
		
		// Setup mocks
		Random rand = mock(Random.class);
		when(rand.nextInt(anyInt())).thenReturn(0);
		
		@SuppressWarnings("unchecked")
		Pool<Integer> poolBase = mock(Pool.class);
		@SuppressWarnings("unchecked")
		Pool<Integer> pool1 = mock(Pool.class);
		@SuppressWarnings("unchecked")
		Pool<Integer> pool2 = mock(Pool.class);
		
		when(poolBase.copy()).thenReturn(pool1);
		when(pool1.peek(any())).thenAnswer(AdditionalAnswers.returnsElementsOf(POOL_1_VALS));
		when(pool1.copy()).thenReturn(pool2);
		when(pool2.peek(any())).thenAnswer(AdditionalAnswers.returnsElementsOf(POOL_2_VALS));
		when(pool2.copy()).thenReturn(null);

		// Create test data and object
		Condition<SimpleObject> neq5 = new SimpleCondition<SimpleObject, Integer>("intField", Negate.YES, Compare.EQUAL, EXCLUDED_VAL);
		EnforceParams<SimpleObject> enforce = new EnforceParams<>(neq5, 0, 0);
		EliminateParams elimParams = new EliminateParams(2);
		
		List<ReflectionObject<SimpleObject>> list = CommonRandomizerTests.createSimpleObjects(LIST_SIZE);
		Randomizer<SimpleObject, Integer> test = RandomizerEliminate.create("intField", poolBase, rand, enforce, elimParams);

		// Perform test and check results
		assertFalse(test.perform(list.stream()));
		List<Integer> results = SimpleObjectUtils.toIntFieldList(list);
		assertIterableEquals(EXPECTED_VALS, results);
	}
	
	@Test
	void perform_poolsExhaust_enforce_resets() 
	{
		final int EXCLUDED_VAL = 5;
		final int LIST_SIZE = 10;
		final List<Integer> POOL_1_VALS =     Arrays.asList(0, 1, 2, 3, 4, EXCLUDED_VAL,    6, 7, 8, 9,
				10, 11, 12, 13, EXCLUDED_VAL, 15, 16, 17, EXCLUDED_VAL, 19);
		final List<Integer> POOL_2_VALS =     Arrays.asList(			   EXCLUDED_VAL                ,
				                14,                       18);
		final List<Integer> EXPECTED_VALS = Arrays.asList(
				10, 11, 12, 13, 14,           15, 16, 17, 18,           19);
		
		// Setup mocks
		Random rand = mock(Random.class);
		when(rand.nextInt(anyInt())).thenReturn(0);
		
		@SuppressWarnings("unchecked")
		Pool<Integer> poolBase = mock(Pool.class);
		@SuppressWarnings("unchecked")
		Pool<Integer> pool1 = mock(Pool.class);
		@SuppressWarnings("unchecked")
		Pool<Integer> pool2 = mock(Pool.class);
		
		when(poolBase.copy()).thenReturn(pool1);
		when(pool1.peek(any())).thenAnswer(AdditionalAnswers.returnsElementsOf(POOL_1_VALS));
		when(pool1.copy()).thenReturn(pool2);
		when(pool2.peek(any())).thenAnswer(AdditionalAnswers.returnsElementsOf(POOL_2_VALS));
		when(pool2.copy()).thenReturn(null);

		// Create test data and object
		Condition<SimpleObject> neq5 = new SimpleCondition<SimpleObject, Integer>("intField", Negate.YES, Compare.EQUAL, EXCLUDED_VAL);
		EnforceParams<SimpleObject> enforce = new EnforceParams<>(neq5, 0, 1);
		EliminateParams elimParams = new EliminateParams(2);
		
		List<ReflectionObject<SimpleObject>> list = CommonRandomizerTests.createSimpleObjects(LIST_SIZE);
		Randomizer<SimpleObject, Integer> test = RandomizerEliminate.create("intField", poolBase, rand, enforce, elimParams);

		// Perform test and check results
		assertTrue(test.perform(list.stream()));
		List<Integer> results = SimpleObjectUtils.toIntFieldList(list);
		assertIterableEquals(EXPECTED_VALS, results);
	}
	
	@Test
	void perform_poolLocation_edges() 
	{
		ExposeRandomizerEliminate nullPool = new ExposeRandomizerEliminate("intField", null, null, null, null);

		// Make sure it doesn't explode
		nullPool.exposedSelectPeeked();
		assertNull(nullPool.exposedPeekNext(null));
		
		// Move past the valid pool (there is only one)
		assertFalse(nullPool.exposedNextPool());
		

		@SuppressWarnings("unchecked")
		Pool<Integer> pool = mock(Pool.class);
		when(pool.peek(any())).thenReturn(5);
		when(pool.copy()).thenReturn(pool);
		
		ExposeRandomizerEliminate nonNullPool = new ExposeRandomizerEliminate("intField", pool, null, null, null);

		// Make sure it doesn't explode
		nonNullPool.exposedSelectPeeked();
		assertNull(nonNullPool.exposedPeekNext(null));
		
		// Move past the valid pool (there is only one)
		assertTrue(nonNullPool.exposedNextPool());
		assertFalse(nonNullPool.exposedNextPool());
		
		// Now try again
		nonNullPool.exposedSelectPeeked();
		assertNull(nonNullPool.exposedPeekNext(null));
	}
}
