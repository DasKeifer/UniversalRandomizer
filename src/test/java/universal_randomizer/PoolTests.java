package universal_randomizer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Arrays;
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
	
	public <T> void assertPoolEquals(Map<T, Integer> expected, Pool<T> found)
	{
		for (Entry<T, Integer> pair : expected.entrySet())
		{
			int foundCount = found.instancesOf(pair.getKey());
			assertEquals(pair.getValue(), foundCount, "Found " + foundCount + " instances of " + 
					pair.getKey() + " in pool but expected to find " + pair.getValue());
		}
	}
	
	@Test
	void create() 
	{
		//Array
		Pool<Integer> nonDup = Pool.create(false, NON_DUPLICATE_ARRAY);
		assertPoolEquals(EXPECTED_NON_DUPLICATE, nonDup);
		assertEquals(EXPECTED_NON_DUPLICATE.size(), nonDup.getUnpeeked().size());
		assertTrue(nonDup.getPeeked().isEmpty());
		assertTrue(nonDup.getRemoved().isEmpty());
		
		Pool<Integer> dup = Pool.create(false, DUPLICATE_ARRAY);
		assertPoolEquals(EXPECTED_DUPLICATE, dup);
		assertEquals(DUPLICATE_ARRAY.length, dup.getUnpeeked().size());
		assertTrue(dup.getPeeked().isEmpty());
		assertTrue(dup.getRemoved().isEmpty());
		
		Pool<Integer> nonDupFromDup = Pool.create(true, DUPLICATE_ARRAY);
		assertPoolEquals(EXPECTED_NON_DUPLICATE, nonDupFromDup);
		assertEquals(EXPECTED_NON_DUPLICATE.size(), nonDup.getUnpeeked().size());
		assertTrue(nonDupFromDup.getPeeked().isEmpty());
		assertTrue(nonDupFromDup.getRemoved().isEmpty());

		//Collection
		nonDup = Pool.create(false, Arrays.asList(NON_DUPLICATE_ARRAY));
		assertPoolEquals(EXPECTED_NON_DUPLICATE, nonDup);
		
		dup = Pool.create(false, Arrays.asList(DUPLICATE_ARRAY));
		assertPoolEquals(EXPECTED_DUPLICATE, dup);
		
		nonDupFromDup = Pool.create(true, Arrays.asList(DUPLICATE_ARRAY));
		assertPoolEquals(EXPECTED_NON_DUPLICATE, nonDupFromDup);

		//Stream
		nonDup = Pool.create(false, Arrays.stream(NON_DUPLICATE_ARRAY));
		assertPoolEquals(EXPECTED_NON_DUPLICATE, nonDup);
		
		dup = Pool.create(false, Arrays.stream(DUPLICATE_ARRAY));
		assertPoolEquals(EXPECTED_DUPLICATE, dup);
		
		nonDupFromDup = Pool.create(true, Arrays.stream(DUPLICATE_ARRAY));
		assertPoolEquals(EXPECTED_NON_DUPLICATE, nonDupFromDup);
	}

	@Test
	void copy() 
	{
		Random rand = mock(Random.class);
		when(rand.nextInt(anyInt())).thenReturn(0);
		
		Pool<Integer> pool = Pool.create(false, NON_DUPLICATE_VALS);
		
		pool.peek(rand);
		pool.popPeeked();
		pool.peek(rand);
		pool.popPeeked();
		pool.peek(rand);
		
		ArrayList<Integer> poolUnpeeked = new ArrayList<>(pool.getUnpeeked());
		ArrayList<Integer> poolPeeked = new ArrayList<>(pool.getPeeked());
		ArrayList<Integer> poolRemoved = new ArrayList<>(pool.getRemoved());
		
		Pool<Integer> copy = pool.copy();
		assertIterableEquals(poolUnpeeked, copy.getUnpeeked());
		assertIterableEquals(poolPeeked, copy.getPeeked());
		assertIterableEquals(poolRemoved, copy.getRemoved());
		
		copy.reset();
		assertIterableEquals(poolUnpeeked, pool.getUnpeeked());
		assertIterableEquals(poolPeeked, pool.getPeeked());
		assertIterableEquals(poolRemoved, pool.getRemoved());
		
		assertEquals(NON_DUPLICATE_VALS.size(), copy.getUnpeeked().size());
		assertTrue(copy.getPeeked().isEmpty());
		assertTrue(copy.getRemoved().isEmpty());
	}
	
	@Test
	void size_unpeekedSize_onCreation() 
	{
		Pool<Integer> empty = Pool.createEmpty();
		assertEquals(0, empty.size(), "size returned non zero for empty pool");
		assertEquals(0, empty.unpeekedSize(), "unpeekedSize returned non zero for empty pool");
		
		Pool<Integer> single = Pool.create(false, 1);
		assertEquals(1, single.size(), "size returned wrong size for single item pool");
		assertEquals(1, single.unpeekedSize(), "unpeekedSize returned wrong size for single item pool");

		Pool<Integer> pool = Pool.create(false, NON_DUPLICATE_VALS);
		assertEquals(NON_DUPLICATE_VALS.size(), pool.size(), "size returned wrong size for item pool");
		assertEquals(NON_DUPLICATE_VALS.size(), pool.unpeekedSize(), "unpeekedSize returned wrong size for item pool");
	}
	
	@Test
	void reset() 
	{
		Random rand = mock(Random.class);
		when(rand.nextInt(anyInt())).thenReturn(0);
		
		Pool<Integer> pool = Pool.create(false, NON_DUPLICATE_VALS);
		
		pool.peek(rand);
		pool.popPeeked();
		pool.peek(rand);
		pool.popPeeked();
		pool.peek(rand);
		assertEquals(NON_DUPLICATE_VALS.size() - 2, pool.size(), "size returned wrong size for item pool");
		assertEquals(NON_DUPLICATE_VALS.size() - 3, pool.unpeekedSize(), "unpeekedSize returned wrong size for item pool");
		
		pool.reset();
		assertEquals(NON_DUPLICATE_VALS.size(), pool.size(), "size returned wrong size for item pool after reset");
		assertEquals(NON_DUPLICATE_VALS.size(), pool.unpeekedSize(), "unpeekedSize returned wrong size for item pool after reset");

		assertPoolEquals(EXPECTED_NON_DUPLICATE, pool);
	}
	
	@Test
	void peek() 
	{
		Random rand = mock(Random.class);
		when(rand.nextInt(anyInt())).thenReturn(0);
		
		Pool<Integer> pool = Pool.create(false, NON_DUPLICATE_VALS);
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
		
		Pool<Integer> pool = Pool.create(false, NON_DUPLICATE_VALS);
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
		
		Pool<Integer> pool = Pool.create(false, NON_DUPLICATE_VALS);
		
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
		
		Pool<Integer> pool = Pool.create(false, NON_DUPLICATE_VALS);
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
		
		Pool<Integer> pool = Pool.create(false, NON_DUPLICATE_VALS);
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
	void peeked_popPeeked_size() 
	{
		Random rand = mock(Random.class);
		when(rand.nextInt(anyInt())).thenReturn(0);

		Pool<Integer> pool = Pool.create(false, NON_DUPLICATE_VALS);
		
		pool.peek(rand);
		assertEquals(NON_DUPLICATE_VALS.size(), pool.size(), "size returned wrong size for item pool");
		assertEquals(NON_DUPLICATE_VALS.size() - 1, pool.unpeekedSize(), "unpeekedSize returned wrong size for item pool");
		
		pool.peek(rand);
		pool.peek(rand);
		assertEquals(NON_DUPLICATE_VALS.size(), pool.size(), "size returned wrong size for item pool");
		assertEquals(NON_DUPLICATE_VALS.size() - 3, pool.unpeekedSize(), "unpeekedSize returned wrong size for item pool");
		
		pool.popPeeked();
		assertEquals(NON_DUPLICATE_VALS.size() - 1, pool.size(), "size returned wrong size for item pool");
		assertEquals(NON_DUPLICATE_VALS.size() - 1, pool.unpeekedSize(), "unpeekedSize returned wrong size for item pool");
		
		pool.peek(rand);
		assertEquals(NON_DUPLICATE_VALS.size() - 1, pool.size(), "size returned wrong size for item pool");
		assertEquals(NON_DUPLICATE_VALS.size() - 2, pool.unpeekedSize(), "unpeekedSize returned wrong size for item pool");

		pool.popPeeked();
		pool.peek(rand);
		pool.popPeeked();
		assertEquals(NON_DUPLICATE_VALS.size() - 3, pool.size(), "size returned wrong size for item pool");
		assertEquals(NON_DUPLICATE_VALS.size() - 3, pool.unpeekedSize(), "unpeekedSize returned wrong size for item pool");
	}
	
	@Test
	void selectPeeked_Unpeeked() 
	{
		Pool<Integer> pool = Pool.create(false, NON_DUPLICATE_VALS);
		assertNull(pool.selectPeeked(false), "selectPeeked did not return null when pool was unpeeked");
	}
	
	@Test
	void peek_selectPeeked_empty() 
	{
		Pool<Integer> pool = Pool.createEmpty();

		assertNull(pool.peek(new Random()), "peek did not return null when pool was empty");
		assertNull(pool.selectPeeked(false), "selectPeeked did not return null when pool was empty");
		assertEquals(0, pool.size(), "size changed when it should not have been removed");
		assertEquals(0, pool.unpeekedSize(), "unpeekedSize was not reset after selectPeeked");
	}

	@Test
	void instancesOf_instancesOfUnpeeked_unpeeked() 
	{
		Random rand = mock(Random.class);
		when(rand.nextInt(anyInt())).thenReturn(0);
		Pool<Integer> pool = Pool.create(false, DUPLICATE_VALS);

		// 1, -4, 5, 1, 99, 1, 5
		final int NOT_IN_POOL = -100;
		assertEquals(3, pool.instancesOf(1));
		assertEquals(1, pool.instancesOf(-4));
		assertEquals(2, pool.instancesOf(5));
		assertEquals(1, pool.instancesOf(99));
		assertEquals(0, pool.instancesOf(NOT_IN_POOL));

		assertEquals(3, pool.instancesOfUnpeeked(1));
		assertEquals(1, pool.instancesOfUnpeeked(-4));
		assertEquals(2, pool.instancesOfUnpeeked(5));
		assertEquals(1, pool.instancesOfUnpeeked(99));
		assertEquals(0, pool.instancesOfUnpeeked(NOT_IN_POOL));
	}
	
	@Test
	void instancesOf_instancesOfUnpeeked_peeked_popped() 
	{
		Random rand = mock(Random.class);
		when(rand.nextInt(anyInt())).thenReturn(0);
		Pool<Integer> pool = Pool.create(false, DUPLICATE_VALS);
		
		assertEquals(DUPLICATE_VALS.get(0), pool.peek(rand), "peek did not return value based on passed Random");

		assertEquals(3, pool.instancesOf(1));
		assertEquals(1, pool.instancesOf(-4));
		assertEquals(2, pool.instancesOf(5));
		assertEquals(1, pool.instancesOf(99));

		assertEquals(2, pool.instancesOfUnpeeked(1));
		assertEquals(1, pool.instancesOfUnpeeked(-4));
		assertEquals(2, pool.instancesOfUnpeeked(5));
		assertEquals(1, pool.instancesOfUnpeeked(99));
		
		pool.popPeeked();

		assertEquals(2, pool.instancesOfUnpeeked(1));
		assertEquals(1, pool.instancesOfUnpeeked(-4));
		assertEquals(2, pool.instancesOfUnpeeked(5));
		assertEquals(1, pool.instancesOfUnpeeked(99));
		
		assertEquals(2, pool.instancesOfUnpeeked(1));
		assertEquals(1, pool.instancesOfUnpeeked(-4));
		assertEquals(2, pool.instancesOfUnpeeked(5));
		assertEquals(1, pool.instancesOfUnpeeked(99));
	}

	@Test
	void instancesOf_instancesOfUnpeeked_peeked_selected() 
	{
		Random rand = mock(Random.class);
		when(rand.nextInt(anyInt())).thenReturn(0);
		Pool<Integer> pool = Pool.create(false, DUPLICATE_VALS);
		
		assertEquals(DUPLICATE_VALS.get(0), pool.peek(rand), "peek did not return value based on passed Random");
		pool.selectPeeked();

		assertEquals(3, pool.instancesOf(1));
		assertEquals(1, pool.instancesOf(-4));
		assertEquals(2, pool.instancesOf(5));
		assertEquals(1, pool.instancesOf(99));

		assertEquals(3, pool.instancesOfUnpeeked(1));
		assertEquals(1, pool.instancesOfUnpeeked(-4));
		assertEquals(2, pool.instancesOfUnpeeked(5));
		assertEquals(1, pool.instancesOfUnpeeked(99));
	}
	
	// copy/deep copy tests with pop?
}
