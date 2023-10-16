package universal_randomizer.randomize;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.junit.jupiter.api.Test;
import org.mockito.AdditionalAnswers;

import Support.RandomizerCommonTestsPoolCreate;
import Support.SimpleObject;
import Support.SimpleObjectUtils;
import universal_randomizer.condition.Comparison;
import universal_randomizer.condition.Negate;
import universal_randomizer.condition.SimpleCondition;
import universal_randomizer.pool.PeekPool;
import universal_randomizer.user_object_apis.Condition;
import universal_randomizer.user_object_apis.Getter;
import universal_randomizer.user_object_apis.Setter;

// Tests the Randomizer Reuse class and by extension the Randomizer class since the
// reuse class is the most simple of the classes
class RandomizerTests {

	final List<Integer> NON_DUPLICATE_VALS = List.of(1, -4, 5, 99);
	final List<Integer> DUPLICATE_VALS = List.of(1, -4, 5, 1, 99, 1, 5);
	final Integer NON_EXISTING_VAL = 7;
	
	final static Setter<SimpleObject, Integer> setterInt = (o, v) -> {
		if (v == null)
		{
			return false;
		}
		o.intField = v;
		return true;
	};
	final static Getter<SimpleObject, Integer> getterInt = o -> o.intField;
	
	public static List<SimpleObject> createSimpleObjects(int number)
	{
		List<SimpleObject> list = new LinkedList<>();
		for (int i = 0; i < number; i++)
		{
			list.add(new SimpleObject("name" + i, i * 100));
		}
		return list;
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
		PeekPool<Integer> pool = mock(PeekPool.class);
		when(pool.peek(any())).thenReturn(POOL_VAL);
		when(pool.copy()).thenReturn(pool);

		// Create test data and object
		List<SimpleObject> list = createSimpleObjects(LIST_SIZE);
		SingleRandomizer<SimpleObject, Integer> test = SingleRandomizer.createNoEnforce(setterInt);

		// Perform test and check results
		assertTrue(test.perform(list.stream(), pool, rand));
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
		PeekPool<Integer> pool = mock(PeekPool.class);
		when(pool.peek(any())).thenAnswer(AdditionalAnswers.returnsElementsOf(POOL_VALS));
		when(pool.copy()).thenReturn(pool);
		
		// Create test data and object
		List<SimpleObject> list = createSimpleObjects(LIST_SIZE);
		SingleRandomizer<SimpleObject, Integer> test = SingleRandomizer.createNoEnforce(setterInt);

		// Perform test and check results
		assertFalse(test.perform(list.stream(), pool, rand));
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
		PeekPool<Integer> pool = mock(PeekPool.class);
		when(pool.peek(any())).thenAnswer(AdditionalAnswers.returnsElementsOf(POOL_VALS));
		when(pool.copy()).thenReturn(pool);

		// Create test data and object
		Condition<SimpleObject> neq5 = SimpleCondition.create(getterInt, Negate.YES, Comparison.EQUAL, EXCLUDED_VAL);
		EnforceParams<SimpleObject> enforce = EnforceParams.create(neq5, 2, 0);
		
		List<SimpleObject> list = createSimpleObjects(LIST_SIZE);
		SingleRandomizer<SimpleObject, Integer> test = SingleRandomizer.create(setterInt, enforce);

		// Perform test and check results
		assertFalse(test.perform(list.stream(), pool, rand));
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
		PeekPool<Integer> pool = mock(PeekPool.class);
		when(pool.peek(any())).thenAnswer(AdditionalAnswers.returnsElementsOf(POOL_VALS));
		when(pool.copy()).thenReturn(pool);

		// Create test data and object
		Condition<SimpleObject> neq5 = SimpleCondition.create(getterInt, Negate.YES, Comparison.EQUAL, EXCLUDED_VAL);
		EnforceParams<SimpleObject> enforce = EnforceParams.create(neq5, 2, 2);
		
		List<SimpleObject> list = createSimpleObjects(LIST_SIZE);
		SingleRandomizer<SimpleObject, Integer> test = SingleRandomizer.create(setterInt, enforce);

		// Perform test and check results
		assertTrue(test.perform(list.stream(), pool, rand));
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
		PeekPool<Integer> pool = mock(PeekPool.class);
		when(pool.peek(any())).thenAnswer(AdditionalAnswers.returnsElementsOf(POOL_VALS));
		when(pool.copy()).thenReturn(pool);

		// Create test data and object
		Condition<SimpleObject> neq5 = SimpleCondition.create(getterInt, Negate.YES, Comparison.EQUAL, EXCLUDED_VAL);
		EnforceParams<SimpleObject> enforce = EnforceParams.create(neq5, 2, 0);
		
		List<SimpleObject> list = createSimpleObjects(LIST_SIZE);
		SingleRandomizer<SimpleObject, Integer> test = SingleRandomizer.create(setterInt, enforce);

		// Perform test and check results
		assertFalse(test.perform(list.stream(), pool, rand));
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
		PeekPool<Integer> pool = mock(PeekPool.class);
		when(pool.peek(any())).thenAnswer(AdditionalAnswers.returnsElementsOf(POOL_VALS));
		when(pool.copy()).thenReturn(pool);

		// Create test data and object
		Condition<SimpleObject> neq5 = SimpleCondition.create(getterInt, Negate.YES, Comparison.EQUAL, EXCLUDED_VAL);
		EnforceParams<SimpleObject> enforce = EnforceParams.create(neq5, 2, 2);
		
		List<SimpleObject> list = createSimpleObjects(LIST_SIZE);
		SingleRandomizer<SimpleObject, Integer> test = SingleRandomizer.create(setterInt, enforce);

		// Perform test and check results
		assertTrue(test.perform(list.stream(), pool, rand));
		List<Integer> results = SimpleObjectUtils.toIntFieldList(list);
		assertIterableEquals(EXPECTED_VALS, results);
	}
}
