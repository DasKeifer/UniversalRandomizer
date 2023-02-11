package universal_randomizer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import org.junit.jupiter.api.Test;

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
	void size_onCreation() 
	{
		Pool<Integer> empty = Pool.createEmpty();
		assertEquals(0, empty.size(), "size returned non zero for empty pool");
		assertEquals(0, empty.unpeekedSize(), "unpeekedSize returned non zero for empty pool");
		
		Pool<Integer> single = Pool.createFromArray(1);
		assertEquals(1, single.size(), "size returned wrong size for single item pool");
		assertEquals(1, single.unpeekedSize(), "unpeekedSize returned wrong size for single item pool");

		Pool<Integer> pool = Pool.createFromList(NON_DUPLICATE_VALS);
		assertEquals(NON_DUPLICATE_VALS.size(), pool.size(), "size returned wrong size for item pool");
		assertEquals(NON_DUPLICATE_VALS.size(), pool.unpeekedSize(), "unpeekedSize returned wrong size for item pool");
	}
	
	@Test
	void peek() 
	{
		Random rand = mock(Random.class);
		when(rand.nextInt(anyInt())).thenReturn(0);
		
		Pool<Integer> pool = Pool.createFromList(NON_DUPLICATE_VALS);
		int found = pool.peek(rand);
		
		assertEquals(NON_DUPLICATE_VALS.get(0), found, "peek did not return value based on passed Random");
		assertEquals(NON_DUPLICATE_VALS.size(), pool.size(), "size did not return the full pool size");
		assertEquals(NON_DUPLICATE_VALS.size() - 1, pool.unpeekedSize(), "unpeekedSize did not reflect peeked value");

		int found2 = pool.peek(rand);
		assertNotEquals(found, found2, "peek did not remove item from pool");
		assertEquals(NON_DUPLICATE_VALS.size(), pool.size(), "size did not return the full pool size");
		assertEquals(NON_DUPLICATE_VALS.size() - 2, pool.unpeekedSize(), "unpeekedSize did not reflect peeked value");
	}
	
	@Test
	void peek_lastItem() 
	{
		// Test the case of the last item in the pool
		Random rand = mock(Random.class);
		when(rand.nextInt(anyInt())).thenReturn(NON_DUPLICATE_VALS.size() - 1).thenReturn(NON_DUPLICATE_VALS.size() - 2);
		
		Pool<Integer> pool = Pool.createFromList(NON_DUPLICATE_VALS);
		int found = pool.peek(rand);
		
		assertEquals(NON_DUPLICATE_VALS.get(NON_DUPLICATE_VALS.size() - 1), found, "peek did not return value based on passed Random");
		assertEquals(NON_DUPLICATE_VALS.size(), pool.size(), "size did not return the full pool size");
		assertEquals(NON_DUPLICATE_VALS.size() - 1, pool.unpeekedSize(), "unpeekedSize did not reflect peeked value");

		int found2 = pool.peek(rand);
		
		assertNotEquals(found, found2, "peek did not remove item from pool");
		assertEquals(NON_DUPLICATE_VALS.size(), pool.size(), "size did not return the full pool size");
		assertEquals(NON_DUPLICATE_VALS.size() - 2, pool.unpeekedSize(), "unpeekedSize did not reflect peeked value");
	}
	
	@Test
	void peek_exhaust() 
	{
		Random rand = mock(Random.class);
		when(rand.nextInt(anyInt())).thenReturn(0);
		
		Pool<Integer> pool = Pool.createFromList(NON_DUPLICATE_VALS);
		
		for (int i = 0; i < NON_DUPLICATE_VALS.size(); i++)
		{
			assertNotNull(pool.peek(rand), "Peek returned null when it should have items still");
		}
		assertEquals(0, pool.unpeekedSize(), "size did not return the full pool size");
		
		// then do one more peek
		assertNull(pool.peek(rand), "Peek did not return null when it was empty");
	}

	@Test
	void selectPeeked_DoNotRemove() 
	{
		Random rand = mock(Random.class);
		when(rand.nextInt(anyInt())).thenReturn(0);
		
		Pool<Integer> pool = Pool.createFromList(NON_DUPLICATE_VALS);
		int foundPeek = pool.peek(rand);
		int found = pool.selectPeeked();

		assertEquals(NON_DUPLICATE_VALS.get(0), foundPeek, "peek did not function as expected");
		assertEquals(foundPeek, found, "SelectPeeked did not return the same value as peek");
		assertEquals(NON_DUPLICATE_VALS.size(), pool.size(), "size changed when it should not have been removed");
		assertEquals(NON_DUPLICATE_VALS.size(), pool.unpeekedSize(), "unpeekedSize was not reset after selectPeeked");
		
		// Test a longer peek list
		pool.peek(rand);
		pool.peek(rand);
		pool.peek(rand);
		pool.selectPeeked();
		assertEquals(NON_DUPLICATE_VALS.size(), pool.size(), "size changed when it should not have been removed");
		assertEquals(NON_DUPLICATE_VALS.size(), pool.unpeekedSize(), "unpeekedSize was not reset after selectPeeked");
	}	
	
	@Test
	void popPeeked() 
	{
		Random rand = mock(Random.class);
		when(rand.nextInt(anyInt())).thenReturn(0);
		
		Pool<Integer> pool = Pool.createFromList(NON_DUPLICATE_VALS);
		int foundPeek = pool.peek(rand);
		int found = pool.popPeeked();

		assertEquals(NON_DUPLICATE_VALS.get(0), foundPeek, "peek did not function as expected");
		assertEquals(foundPeek, found, "SelectPeeked did not return the same value as peek");
		assertEquals(NON_DUPLICATE_VALS.size() - 1, pool.size(), "Item was not removed by selectPeeked");
		assertEquals(NON_DUPLICATE_VALS.size() - 1, pool.unpeekedSize(), "Item was not removed by selectPeeked");

		// Test a longer peek list
		pool.peek(rand);
		pool.peek(rand);
		pool.peek(rand);
		pool.popPeeked();
		assertEquals(NON_DUPLICATE_VALS.size() - 2, pool.size(), "Item was not removed by selectPeeked");
		assertEquals(NON_DUPLICATE_VALS.size() - 2, pool.unpeekedSize(), "Item was not removed by selectPeeked");
	}
	
	@Test
	void selectPeeked_Unpeeked() 
	{
		Pool<Integer> pool = Pool.createFromList(NON_DUPLICATE_VALS);
		assertNull(pool.selectPeeked(false), "selectPeeked did not return null when pool was unpeeked");
	}
	
	@Test
	void peek_selectPeeked_empty() 
	{
		Pool<Integer> pool = Pool.createEmpty();

		assertNull(pool.peek(new Random()), "peek did not return null when pool was empty");
		assertNull(pool.selectPeeked(false), "selectPeeked did not return null when pool was empty");
	}
	
	// copy/deep copy tests with pop?
}
