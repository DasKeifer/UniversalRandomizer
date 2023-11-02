package universal_randomizer.randomize;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.mockito.AdditionalAnswers;

import support.SimpleObject;
import support.SimpleObjectUtils;
import universal_randomizer.condition.Comparison;
import universal_randomizer.condition.Negate;
import universal_randomizer.condition.SimpleCondition;
import universal_randomizer.pool.EliminatePoolSet;
import universal_randomizer.pool.MultiPool;
import universal_randomizer.pool.PeekPool;
import universal_randomizer.pool.RandomizerPool;
import universal_randomizer.user_object_apis.Condition;
import universal_randomizer.user_object_apis.Getter;
import universal_randomizer.user_object_apis.MultiGetter;
import universal_randomizer.user_object_apis.Setter;
import universal_randomizer.user_object_apis.SetterNoReturn;

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
	
	@Test
	void elimatePoolRandomizer() 
	{
		Random rand = mock(Random.class);
		when(rand.nextInt(anyInt())).thenReturn(0);
		
		final List<Integer> TEST_VALUES = List.of(1, 5, -4);
		final List<Integer> EXPECTED_SEQ = List.of(1, -4);
		
		List<SimpleObject> objs = new LinkedList<>();
		for (int i = 0; i < 6; i++)
		{
			objs.add(new SimpleObject("name" + i, i * 100));
		}

		EliminatePoolSet<Integer> pool = EliminatePoolSet.create(
				PeekPool.create(true, TEST_VALUES), 3);
		EnforceParams<SimpleObject> enforce = EnforceParams.create(
				SimpleCondition.create(SimpleObject::getIntField, Negate.YES, Comparison.EQUAL, 5), 2, 0);

		SingleRandomizer<SimpleObject, Integer> test = SingleRandomizer.create(
				SetterNoReturn.asMultiSetter(SimpleObject::setField), enforce);
		
		assertTrue(test.perform(objs.stream(), pool, rand));
		for (int i = 0; i < objs.size(); i++)
		{
			assertEquals(EXPECTED_SEQ.get(i % EXPECTED_SEQ.size()), objs.get(i).getIntField(), " Failed at index " + 1);
		}
	}
	
	@Test
	void elimatePoolRandomizer_exhaust() 
	{
		Random rand = mock(Random.class);
		when(rand.nextInt(anyInt())).thenReturn(0);
		
		final List<Integer> TEST_VALUES = List.of(1, 5, -4);
		
		List<SimpleObject> objs = new LinkedList<>();
		for (int i = 0; i < 7; i++)
		{
			objs.add(new SimpleObject("name" + i, i * 100));
		}

		EliminatePoolSet<Integer> pool = EliminatePoolSet.create(
				PeekPool.create(true, TEST_VALUES), 3);
		EnforceParams<SimpleObject> enforce = EnforceParams.create(
				SimpleCondition.create(SimpleObject::getIntField, Negate.YES, Comparison.EQUAL, 5), 2, 0);

		SingleRandomizer<SimpleObject, Integer> test = SingleRandomizer.create(
				SetterNoReturn.asMultiSetter(SimpleObject::setField), enforce);
		
		assertFalse(test.perform(objs.stream(), pool, rand));
	}
	
	@Test
	void mutlipoolRandomizer() 
	{
		Random rand = mock(Random.class);
		when(rand.nextInt(anyInt())).thenReturn(0);
		
		Set<Integer> expected1 = new HashSet<Integer>();
		expected1.add(1);
		expected1.add(-4);
		expected1.add(9);
		Set<Integer> expected2 = new HashSet<Integer>();
		expected2.add(3);
		expected2.add(-1);
		expected2.add(0);

		Map<String, RandomizerPool<Integer>> poolMap = new HashMap<>();
		poolMap.put("name1", PeekPool.create(true, List.of(1, 5, -4, 9)));
		poolMap.put("name2", PeekPool.create(true, List.of(3, -1, 5, 0)));
		
		List<SimpleObject> objs = new LinkedList<>();
		for (int i = 0; i < 6; i++)
		{
			objs.add(new SimpleObject("name" + (1 + (i % 2)), i * 100));
		}
		
		MultiGetter<SimpleObject, String> soString = (so2, cnt) -> so2.getStringField();
		MultiPool<SimpleObject, String, Integer> pool = MultiPool.create(poolMap, soString);
		
		EnforceParams<SimpleObject> enforce = EnforceParams.create(
				SimpleCondition.create(SimpleObject::getIntField, Negate.YES, Comparison.EQUAL, 5), 2, 0);

		SingleRandomizer<SimpleObject, Integer> test = SingleRandomizer.create(
				SetterNoReturn.asMultiSetter(SimpleObject::setField), enforce);
		
		assertTrue(test.perform(objs.stream(), pool, rand));
		assertEquals(objs.size(), expected1.size() + expected2.size(), "Bad test setup!");
		for (SimpleObject so : objs)
		{
			if (so.getStringField().equals("name1"))
			{
				assertTrue(expected1.remove(so.getIntField()), so.getIntField() + " not found in set 1");
			} 
			else if (so.getStringField().equals("name2"))
			{
				assertTrue(expected2.remove(so.getIntField()), so.getIntField() + " not found in set 2");
			}
			else
			{
				assertTrue(false, "Bad test setup!");
			}
		}
	}
	
	@Test
	void mutlipoolRandomizer_exhaust() 
	{
		Random rand = mock(Random.class);
		when(rand.nextInt(anyInt())).thenReturn(0);

		Map<String, RandomizerPool<Integer>> poolMap = new HashMap<>();
		poolMap.put("name1", PeekPool.create(true, List.of(1, 5, -4, 9)));
		poolMap.put("name2", PeekPool.create(true, List.of(3, -1, 5, 0)));
		
		List<SimpleObject> objs = new LinkedList<>();
		for (int i = 0; i < 7; i++)
		{
			objs.add(new SimpleObject("name" + (1 + (i % 2)), i * 100));
		}
		
		MultiGetter<SimpleObject, String> soString = (so2, cnt) -> so2.getStringField();
		MultiPool<SimpleObject, String, Integer> pool = MultiPool.create(poolMap, soString);
		
		EnforceParams<SimpleObject> enforce = EnforceParams.create(
				SimpleCondition.create(SimpleObject::getIntField, Negate.YES, Comparison.EQUAL, 5), 2, 0);

		SingleRandomizer<SimpleObject, Integer> test = SingleRandomizer.create(
				SetterNoReturn.asMultiSetter(SimpleObject::setField), enforce);
		
		assertFalse(test.perform(objs.stream(), pool, rand));
	}
}
