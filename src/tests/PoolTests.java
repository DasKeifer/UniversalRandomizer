package tests;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

import org.junit.jupiter.api.Test;
import universal_randomizer.Pool;

@SuppressWarnings("serial")
class PoolTests {

	final List<Integer> NON_DUPLICATE_VALS = List.of(1, -4, 5, 99);
	final List<Integer> DUPLICATE_VALS = List.of(1, -4, 5, 1, 99, 1, 5);
	final Integer NON_EXISTING_VAL = 7;
	
	public <T> void assertPoolEquals(Map<T, Integer> expected, Pool<T> found)
	{
		for (Entry<T, Integer> pair : expected.entrySet())
		{
			int foundCount = found.instancesOf(pair.getKey());
			assertTrue(foundCount == pair.getValue(), "Found " + foundCount + " instances of " + pair.getKey() + " in pool but expected to find " + pair.getValue());
		}
	}
	
	@Test
	void create_FromArray() 
	{
		final Integer[] NON_DUPLICATE_ARRAY = (Integer[]) NON_DUPLICATE_VALS.toArray(new Integer[0]);
		final Integer[] DUPLICATE_ARRAY = (Integer[]) DUPLICATE_VALS.toArray(new Integer[0]);
		
		final Map<Integer, Integer> EXPECTED_NON_DUPLICATE = Collections.unmodifiableMap(new HashMap<Integer, Integer>() {
		    {
		        put(-4, 1);
		        put(1, 1);
		        put(5, 1);
		        put(99, 1);
		    }
		});
		final Map<Integer, Integer> EXPECTED_DUPLICATE = Collections.unmodifiableMap(new HashMap<Integer, Integer>() {
		    {
		        put(-4, 1);
		        put(1, 3);
		        put(5, 2);
		        put(99, 1);
		    }
		});
		
		Pool<Integer> nonDup = Pool.createFromArray(NON_DUPLICATE_ARRAY);
		assertPoolEquals(EXPECTED_NON_DUPLICATE, nonDup);
		
		Pool<Integer> dup = Pool.createFromArray(DUPLICATE_ARRAY);
		assertPoolEquals(EXPECTED_DUPLICATE, dup);
		
		Pool<Integer> nonDupFromDup = Pool.createUniformFromArray(DUPLICATE_ARRAY);
		assertPoolEquals(EXPECTED_NON_DUPLICATE, nonDupFromDup);
		
//		
//		printPool(intArrayPool);
//		
//		Pool<Float> floatPool = Pool.createRange(-3.14f, 1.88f, 0.7152f, Float::sum);
//		printPool(floatPool);
//
//		SimpleObject so1 = new SimpleObject("1", 2);
//		SimpleObject so2 = new SimpleObject("2", 12);
//		SimpleObject soStep = new SimpleObject("3", 2);
//		Pool<SimpleObject> soPool = Pool.createRange(so1, so2, soStep, Test::sumSO);
//		printSoPool(soPool);
//		
//		Pool<Integer> fromSo = Pool.createFromStream("intVal", soList.stream());
//		printPool(fromSo);
//
//		// From Stream with complex types
//		Pool<Integer> fromCoIv = Pool.createFromStream("intVal", soList.stream());
//		printPool(fromCoIv);
//		Pool<Integer> fromCoDwa = Pool.createFromStream("doubleWrapperArray", coList.stream());
//		printPool(fromCoDwa);
//		Pool<Integer> fromCoCra = Pool.createFromStream("charRawArray", coList.stream());
//		printPool(fromCoCra);
//		Pool<Integer> fromCoCc = Pool.createFromStream("charCollection", coList.stream());
//		printPool(fromCoCc);
//		Pool<Integer> fromCoFm = Pool.createFromStream("floatMap", coList.stream());
//		printPool(fromCoFm);
//		Pool<Integer> fromCoFmv = Pool.createFromMapValuesStream("floatMap", coList.stream());
//		printPool(fromCoFmv);
//		Pool<Integer> fromCoFmk = Pool.createFromMapKeysStream("floatMap", coList.stream());
//		printPool(fromCoFmk);
	}
	
	// TODO: more creates
	
	@Test
	void isEmpty() 
	{
		Pool<Integer> empty = Pool.createEmpty();
		assertTrue(empty.isEmpty(), "isEmpty returned true for non-empty pool");
		
		Pool<Integer> pool = Pool.createFromList(NON_DUPLICATE_VALS);
		assertFalse(pool.isEmpty(), "isEmpty returned true for non-empty pool");
	}
	
	@Test
	void size() 
	{
		Pool<Integer> empty = Pool.createEmpty();
		assertEquals(empty.size(), 0, "size returned non zero for empty pool");
		
		Pool<Integer> single = Pool.createFromArray(1);
		assertEquals(single.size(), 1, "size returned wrong size for single item pool");

		Pool<Integer> pool = Pool.createFromList(NON_DUPLICATE_VALS);
		assertEquals(NON_DUPLICATE_VALS.size(), pool.size(), "size returned wrong size for item pool");
	}
	
	@Test
	void isValidKey() 
	{
		Pool<Integer> pool = Pool.createFromList(NON_DUPLICATE_VALS);
		assertTrue(pool.isValidKey(0), "isValidKey returned false for key 0");
		assertFalse(pool.isValidKey(-1), "isValidKey returned true for bad value key -1");
		assertFalse(pool.isValidKey(100), "isValidKey returned true for out of range key 100");
		
		Integer key = pool.keyOf(1);
		pool.remove(key);
		assertFalse(pool.isValidKey(key), "isValidKey returned true for removed key " + key);
	}
	
	@Test
	void keyOf() 
	{
		Pool<Integer> pool = Pool.createFromList(DUPLICATE_VALS);
		assertEquals(pool.keyOf(1), 0, "keyOf returned unexpected value");

		Integer found = pool.keyOf(NON_EXISTING_VAL);
		assertTrue(found < 0, "keyOf returned value " + found + " for value " + NON_EXISTING_VAL);
	}
	
	@Test
	void allKeysOf() 
	{
		final List<Integer> EXPECTED_FOR_1 = List.of(0,3,5);
		final List<Integer> EXPECTED_FOR_NEG_4 = List.of(1);

		Pool<Integer> pool = Pool.createFromList(DUPLICATE_VALS);
		
		Set<Integer> found = pool.allKeysOf(1);
		assertIterableEquals(EXPECTED_FOR_1, found, "allKeysOf did not find the expected keys");
		
		found = pool.allKeysOf(-4);
		assertIterableEquals(EXPECTED_FOR_NEG_4, found, "allKeysOf did not find the expected keys");
		
		found = pool.allKeysOf(NON_EXISTING_VAL);
		assertTrue(found.isEmpty(), "allKeysOf returned non empty for non existent value " + NON_EXISTING_VAL);
	}
	
	@Test
	void instancesOf() 
	{
		final int EXPECTED_OF_1 = 3;
		final int EXPECTED_OF_NEG_4 = 1;
		
		Pool<Integer> pool = Pool.createFromList(DUPLICATE_VALS);
		
		int found = pool.instancesOf(1);
		assertEquals(EXPECTED_OF_1, found, "instancesOf found unexpected value for value 1");

		found = pool.instancesOf(-4);
		assertEquals(EXPECTED_OF_NEG_4, found, "instancesOf found unexpected value for value -4");
		
		found = pool.instancesOf(NON_EXISTING_VAL);
		assertEquals(0, found, "instancesOf returned non empty for non existent value " + NON_EXISTING_VAL);
	}
		
	@Test
	void getRandomKey() 
	{
		Random rand = new Random(0);
		
		Pool<Integer> pool = Pool.createFromList(NON_DUPLICATE_VALS);
		Integer found = pool.getRandomKey(rand);
		assertTrue(pool.isValidKey(found), "getRandomKey returned invalid key: " + found);

		Pool<Integer> emptyPool = Pool.createEmpty();
		found = emptyPool.getRandomKey(rand);
		assertTrue(found < 0, "getRandomKey returned a key on an empty pool: " + found);
	}
		
	@Test
	void getRandomKeyWithExclusions() 
	{
		Random rand = new Random(0);
		
		Pool<Integer> pool = Pool.createFromList(NON_DUPLICATE_VALS);
		Set<Integer> excluded = new HashSet<>();
		excluded.add(pool.keyOf(1));
		excluded.add(pool.keyOf(-4));
		excluded.add(pool.keyOf(5));
		
		Integer expectedKey = pool.keyOf(99);
		
		Integer found = pool.getRandomKey(rand, excluded);
		assertEquals(expectedKey, found, "getRandomKey with exclusions returned key " + found + " but only key " + expectedKey + " should be allowed");
		
		// Force it down another path
		rand = new Random(0);
		excluded = new HashSet<>();
		excluded.add(pool.keyOf(1));
		excluded.add(pool.keyOf(-4));
		excluded.add(pool.keyOf(99));
		
		expectedKey = pool.keyOf(5);
		
		found = pool.getRandomKey(rand, excluded);
		assertEquals(expectedKey, found, "getRandomKey with exclusions returned key " + found + " but only key " + expectedKey + " should be allowed");
		
	}

	@Test
	void getRandomKeyWithExclusions_edges() 
	{
		Random rand = new Random(0);
		
		Pool<Integer> pool = Pool.createFromList(NON_DUPLICATE_VALS);
		Integer found = pool.getRandomKey(rand, null);
		assertTrue(pool.isValidKey(found), "getRandomKey returned invalid key when passed null exclusion list: " + found);
		
		Set<Integer> empty = new HashSet<>();
		found = pool.getRandomKey(rand, empty);
		assertTrue(pool.isValidKey(found), "getRandomKey returned invalid key when passed empty exclusion list: " + found);
		
		Set<Integer> full = new HashSet<>();
		full.addAll(NON_DUPLICATE_VALS);
		found = pool.getRandomKey(rand, full);
		assertTrue(found < 0, "getRandomKey returned a key when all were excluded: " + found);
	}

	@Test
	void getRandom_popRandom() 
	{
		Random rand = new Random(0);
		
		Pool<Integer> pool = Pool.createFromList(NON_DUPLICATE_VALS);
		Integer found = pool.getRandom(rand);
		assertTrue(NON_DUPLICATE_VALS.contains(found), "getRandom returned value not in pool: " + found);
		
		found = pool.popRandom(rand);
		assertTrue(NON_DUPLICATE_VALS.contains(found), "popRandom returned value not in pool: " + found);

		Pool<Integer> emptyPool = Pool.createEmpty();
		found = emptyPool.getRandom(rand);
		assertNull(found, "getRandom returned a value for an empty pool: " + found);
		
		found = emptyPool.popRandom(rand);
		assertNull(found, "popRandom returned a value for an empty pool: " + found);
	}
	
	// pop random Exhaustion test
	
	// get_pop
	
	// pop Exhaustion test
	
	// copy/deep copy tests with pop?
}
