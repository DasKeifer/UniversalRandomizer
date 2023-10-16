package universal_randomizer.pool;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import org.junit.jupiter.api.Test;

import Support.SimpleObject;
import universal_randomizer.user_object_apis.Getter;
import universal_randomizer.user_object_apis.MultiGetter;

@SuppressWarnings("serial")
class MultiPoolTests {

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
	
	public <T> void assertPoolEquals(Map<T, Integer> expected, PeekPool<T> found)
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
		PeekPool<Integer> p1 = PeekPool.create(false, NON_DUPLICATE_VALS);
		PeekPool<Integer> p2 = PeekPool.create(false, DUPLICATE_VALS);
		Map<Integer, RandomizerPool<Integer>> poolMap = new HashMap<>();
		poolMap.put(1, p1);
		poolMap.put(2, p2);
		
		MultiGetter<SimpleObject, Integer> soInt = (so, cnt) -> so.getIntField();
		assertNotNull(MultiPool.create(poolMap, soInt));
		assertNull(MultiPool.create(null, soInt));
		assertNull(MultiPool.create(poolMap, null));
	}
	

	@Test
	void setPool() 
	{
		SimpleObject so = new SimpleObject("test", 1);
		PeekPool<Integer> p1 = PeekPool.create(false, NON_DUPLICATE_VALS);
		PeekPool<Integer> p2 = PeekPool.create(false, DUPLICATE_VALS);
		Map<Integer, RandomizerPool<Integer>> poolMap = new HashMap<>();
		poolMap.put(1, p1);
		poolMap.put(2, p2);
		
		MultiGetter<SimpleObject, Integer> soInt = (o, cnt) -> {
			return cnt == 0 ? null : cnt;
		};
		MultiPool<SimpleObject, Integer, Integer> mp = MultiPool.create(poolMap, soInt);
		
		assertNull(mp.peek(null));
		assertNull(mp.selectPeeked());
		mp.reset();
		mp.resetPeeked();
		assertFalse(mp.useNextPool());
		
		// bad key
		assertFalse(mp.setPool(so, 0));
		// key not in map
		assertFalse(mp.setPool(so, 5));
		
		assertTrue(mp.setPool(so, 1));
	}
//
//	@Override
//	public void reset() 
//	{
//		activePool.reset();
//	}
//
//	@Override
//	public T peek(Random rand) 
//	{
//		return activePool.peek(rand);
//	}
//
//	@Override
//	public T selectPeeked() 
//	{
//		return activePool.selectPeeked();
//	}
//	
//	@Override
//	public boolean useNextPool()
//	{
//		return activePool.useNextPool();
//	}
//
//	@Override
//	public void resetPeeked() 
//	{
//		activePool.resetPeeked();
//	}
	
	@Test
	void reset() 
	{
//		SimpleObject so = new SimpleObject("test", 1);
//		PeekPool<Integer> p1 = PeekPool.create(false, NON_DUPLICATE_VALS);
//		PeekPool<Integer> p2 = PeekPool.create(false, DUPLICATE_VALS);
//		Map<Integer, RandomizerPool<Integer>> poolMap = new HashMap<>();
//		poolMap.put(1, p1);
//		poolMap.put(2, p2);
//		
//		MultiGetter<SimpleObject, Integer> soInt = (so, cnt) -> so.getIntField();
//		MultiPool<SimpleObject, Integer, Integer> mp = MultiPool.create(poolMap, soInt);
//		Random rand = mock(Random.class);
//		when(rand.nextInt(anyInt())).thenReturn(0);
//		
//		mp.reset();
//		
//		mp.setPool(so, 0);
//		
//		mp.peek(rand);
//		mp.selectPeeked();
//		mp.peek(rand);
//		mp.selectPeeked();
//		mp.peek(rand);
//		assertEquals(NON_DUPLICATE_VALS.size() - 2, mp.size(), "size returned wrong size for item pool");
//		assertEquals(NON_DUPLICATE_VALS.size() - 3, mp.unpeekedSize(), "unpeekedSize returned wrong size for item pool");
//		
//		mp.reset();
//		assertEquals(NON_DUPLICATE_VALS.size(), pool.size(), "size returned wrong size for item pool after reset");
//		assertEquals(NON_DUPLICATE_VALS.size(), pool.unpeekedSize(), "unpeekedSize returned wrong size for item pool after reset");
//
//		assertPoolEquals(EXPECTED_NON_DUPLICATE, pool);
	}
	
	@Test
	void peek() 
	{
		Random rand = mock(Random.class);
		when(rand.nextInt(anyInt())).thenReturn(0);

		PeekPool<Integer> base = PeekPool.create(false, NON_DUPLICATE_VALS);
		EliminatePoolSet<Integer> pool = EliminatePoolSet.create(base, 1);
		
		int found = pool.peek(rand);
		
		assertEquals(NON_DUPLICATE_VALS.get(0), found, "peek did not return value based on passed Random");

		int found2 = pool.peek(rand);
		assertNotEquals(found, found2, "peek did not remove item from pool");
	}
	
	@Test
	void peek_lastItem() 
	{
		// Test the case of the last item in the pool
		Random rand = mock(Random.class);
		when(rand.nextInt(anyInt())).thenReturn(NON_DUPLICATE_VALS.size() - 1).thenReturn(NON_DUPLICATE_VALS.size() - 2);

		PeekPool<Integer> base = PeekPool.create(false, NON_DUPLICATE_VALS);
		EliminatePoolSet<Integer> pool = EliminatePoolSet.create(base, 1);
		
		int found = pool.peek(rand);
		
		assertEquals(NON_DUPLICATE_VALS.get(NON_DUPLICATE_VALS.size() - 1), found, "peek did not return value based on passed Random");

		int found2 = pool.peek(rand);
		assertNotEquals(found, found2, "peek did not remove item from pool");
	}
	
	@Test
	void peek_badCases() 
	{
		Random rand = mock(Random.class);
		when(rand.nextInt(anyInt())).thenReturn(0);

		PeekPool<Integer> base = PeekPool.create(false, NON_DUPLICATE_VALS);
		EliminatePoolSet<Integer> pool = EliminatePoolSet.create(base, 1);
		
		assertNull(pool.peek(null));
		
		// Exhaust the pool
		for (int i = 0; i < NON_DUPLICATE_VALS.size(); i++)
		{
			assertNotNull(pool.peek(rand), "Peek returned null when it should have items still");
		}
		
		// then do one more peek
		assertNull(pool.peek(rand), "Peek did not return null when it was empty");
	}
	
	@Test
	void selectPeeked_Unpeeked() 
	{
		PeekPool<Integer> base = PeekPool.create(false, NON_DUPLICATE_VALS);
		EliminatePoolSet<Integer> pool = EliminatePoolSet.create(base, 1);
		
		assertNull(pool.selectPeeked(), "selectPeeked did not return null when pool was unpeeked");
	}
	
	@Test
	void peek_selectPeeked_empty() 
	{
		PeekPool<Integer> base = PeekPool.createEmpty();
		EliminatePoolSet<Integer> pool = EliminatePoolSet.create(base, 1);

		assertNull(pool.peek(new Random()), "peek did not return null when pool was empty");
		assertNull(pool.selectPeeked(), "selectPeeked did not return null when pool was empty");
	}
	
	@Test
	void useNextPool() 
	{
		PeekPool<Integer> base = PeekPool.create(false, NON_DUPLICATE_ARRAY);
		EliminatePoolSet<Integer> pool = EliminatePoolSet.create(base, 3);
		assertTrue(pool.useNextPool());
		assertTrue(pool.useNextPool());
		assertFalse(pool.useNextPool());
		// ensure it didn't walk past the end
		assertNotNull(pool.peek(new Random()));

		pool.reset();
		
		assertTrue(pool.useNextPool());
		assertTrue(pool.useNextPool());
		assertFalse(pool.useNextPool());

		EliminatePoolSet<Integer> pool2 = EliminatePoolSet.create(base, 1);
		assertFalse(pool2.useNextPool());
		// ensure it didn't walk past the end
		assertNotNull(pool2.peek(new Random()));
	}
}
