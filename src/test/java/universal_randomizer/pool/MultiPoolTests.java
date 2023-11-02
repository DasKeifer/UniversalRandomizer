package universal_randomizer.pool;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.junit.jupiter.api.Test;

import support.SimpleObject;
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
	
	@Test
	void reset() 
	{
		SimpleObject so = new SimpleObject("test", 1);
		PeekPool<Integer> p1 = PeekPool.create(true, NON_DUPLICATE_VALS);
		PeekPool<Integer> p2 = PeekPool.create(true, DUPLICATE_VALS);
		Map<Integer, RandomizerPool<Integer>> poolMap = new HashMap<>();
		poolMap.put(1, p1);
		poolMap.put(2, p2);
		
		MultiGetter<SimpleObject, Integer> soInt = (so2, cnt) -> so2.getIntField();
		MultiPool<SimpleObject, Integer, Integer> mp = MultiPool.create(poolMap, soInt);
		
		Random rand = mock(Random.class);
		when(rand.nextInt(anyInt())).thenReturn(0);
		
		mp.reset();
		
		mp.setPool(so, 0);
		
		mp.peek(rand);
		mp.selectPeeked();
		mp.peek(rand);
		mp.selectPeeked();
		mp.peek(rand);
		
		assertEquals(NON_DUPLICATE_VALS.size() - 2, ((PeekPool<Integer>)poolMap.get(1)).size(), "size returned wrong size for item pool");
		assertEquals(NON_DUPLICATE_VALS.size() - 3, p1.unpeekedSize(), "unpeekedSize returned wrong size for item pool");

		so.setIntField(2);
		mp.setPool(so, 0);
		
		mp.peek(rand);
		mp.selectPeeked();
		mp.peek(rand);
		
		assertEquals(DUPLICATE_VALS.size() - 1, p2.size(), "size returned wrong size for item pool");
		assertEquals(DUPLICATE_VALS.size() - 2, p2.unpeekedSize(), "unpeekedSize returned wrong size for item pool");
		
		mp.reset();
		assertEquals(NON_DUPLICATE_VALS.size(), p1.size(), "size returned wrong size for item pool after reset");
		assertEquals(NON_DUPLICATE_VALS.size(), p1.unpeekedSize(), "unpeekedSize returned wrong size for item pool after reset");
		assertEquals(DUPLICATE_VALS.size(), p2.size(), "size returned wrong size for item pool after reset");
		assertEquals(DUPLICATE_VALS.size(), p2.unpeekedSize(), "unpeekedSize returned wrong size for item pool after reset");
	}
	
	@Test
	void peek() 
	{
		SimpleObject so = new SimpleObject("test", 1);
		PeekPool<Integer> p1 = PeekPool.create(true, NON_DUPLICATE_VALS);
		PeekPool<Integer> p2 = PeekPool.create(true, DUPLICATE_VALS);
		Map<Integer, RandomizerPool<Integer>> poolMap = new HashMap<>();
		poolMap.put(1, p1);
		poolMap.put(2, p2);
		
		MultiGetter<SimpleObject, Integer> soInt = (so2, cnt) -> so2.getIntField();
		MultiPool<SimpleObject, Integer, Integer> mp = MultiPool.create(poolMap, soInt);
		mp.setPool(so, 0);
		
		Random rand = mock(Random.class);
		when(rand.nextInt(anyInt())).thenReturn(0);
		
		int found = mp.peek(rand);
		
		assertEquals(NON_DUPLICATE_VALS.get(0), found, "peek did not return value based on passed Random");

		int found2 = mp.peek(rand);
		assertNotEquals(found, found2, "peek did not remove item from pool");
	}
	
	@Test
	void peek_lastItem() 
	{
		// Test the case of the last item in the pool
		SimpleObject so = new SimpleObject("test", 1);
		PeekPool<Integer> p1 = PeekPool.create(true, NON_DUPLICATE_VALS);
		PeekPool<Integer> p2 = PeekPool.create(true, DUPLICATE_VALS);
		Map<Integer, RandomizerPool<Integer>> poolMap = new HashMap<>();
		poolMap.put(1, p1);
		poolMap.put(2, p2);
		
		MultiGetter<SimpleObject, Integer> soInt = (so2, cnt) -> so2.getIntField();
		MultiPool<SimpleObject, Integer, Integer> mp = MultiPool.create(poolMap, soInt);
		mp.setPool(so, 0);
		
		Random rand = mock(Random.class);
		when(rand.nextInt(anyInt())).thenReturn(NON_DUPLICATE_VALS.size() - 1).thenReturn(NON_DUPLICATE_VALS.size() - 2);
		
		int found = mp.peek(rand);
		
		assertEquals(NON_DUPLICATE_VALS.get(NON_DUPLICATE_VALS.size() - 1), found, "peek did not return value based on passed Random");

		int found2 = mp.peek(rand);
		assertNotEquals(found, found2, "peek did not remove item from pool");
	}
	
	@Test
	void resetPeeked() 
	{
		SimpleObject so = new SimpleObject("test", 1);
		PeekPool<Integer> p1 = PeekPool.create(true, NON_DUPLICATE_VALS);
		PeekPool<Integer> p2 = PeekPool.create(true, DUPLICATE_VALS);
		Map<Integer, RandomizerPool<Integer>> poolMap = new HashMap<>();
		poolMap.put(1, p1);
		poolMap.put(2, p2);
		
		MultiGetter<SimpleObject, Integer> soInt = (so2, cnt) -> so2.getIntField();
		MultiPool<SimpleObject, Integer, Integer> mp = MultiPool.create(poolMap, soInt);
		mp.setPool(so, 0);
		
		Random rand = mock(Random.class);
		when(rand.nextInt(anyInt())).thenReturn(0);
		
		mp.peek(rand);
		mp.resetPeeked();
		assertEquals(NON_DUPLICATE_VALS.size(), p1.size(), "size did not return the full pool size");
		assertEquals(NON_DUPLICATE_VALS.size(), p1.unpeekedSize(), "unpeekedSize did not reflect peeked value");
		
		mp.peek(rand);
		mp.selectPeeked();
		mp.peek(rand);
		mp.resetPeeked();
		assertEquals(NON_DUPLICATE_VALS.size() - 1, p1.size(), "size did not return the full pool size");
		assertEquals(NON_DUPLICATE_VALS.size() - 1, p1.unpeekedSize(), "unpeekedSize did not reflect peeked value");
	}
	
	@Test
	void peek_badCases() 
	{
		SimpleObject so = new SimpleObject("test", 1);
		PeekPool<Integer> p1 = PeekPool.create(true, NON_DUPLICATE_VALS);
		PeekPool<Integer> p2 = PeekPool.create(true, DUPLICATE_VALS);
		Map<Integer, RandomizerPool<Integer>> poolMap = new HashMap<>();
		poolMap.put(1, p1);
		poolMap.put(2, p2);
		
		MultiGetter<SimpleObject, Integer> soInt = (so2, cnt) -> so2.getIntField();
		MultiPool<SimpleObject, Integer, Integer> mp = MultiPool.create(poolMap, soInt);

		Random rand = mock(Random.class);
		when(rand.nextInt(anyInt())).thenReturn(0);

		assertNull(mp.peek(null));
		assertNull(mp.peek(rand));
		
		mp.setPool(so, 0);

		assertNull(mp.peek(null));
		
		// Exhaust the pool
		for (int i = 0; i < NON_DUPLICATE_VALS.size(); i++)
		{
			assertNotNull(mp.peek(rand), "Peek returned null when it should have items still");
		}
		
		// then do one more peek
		assertNull(mp.peek(rand), "Peek did not return null when it was empty");
	}
	
	@Test
	void selectPeeked() 
	{
		SimpleObject so = new SimpleObject("test", 1);
		PeekPool<Integer> p1 = PeekPool.create(true, NON_DUPLICATE_VALS);
		PeekPool<Integer> p2 = PeekPool.create(true, DUPLICATE_VALS);
		Map<Integer, RandomizerPool<Integer>> poolMap = new HashMap<>();
		poolMap.put(1, p1);
		poolMap.put(2, p2);
		
		MultiGetter<SimpleObject, Integer> soInt = (so2, cnt) -> so2.getIntField();
		MultiPool<SimpleObject, Integer, Integer> mp = MultiPool.create(poolMap, soInt);
		mp.setPool(so, 0);

		Random rand = mock(Random.class);
		when(rand.nextInt(anyInt())).thenReturn(0);
		
		int foundPeek = mp.peek(rand);
		int found = mp.selectPeeked();

		assertEquals(NON_DUPLICATE_VALS.get(0), foundPeek, "peek did not function as expected");
		assertEquals(foundPeek, found, "SelectPeeked did not return the same value as peek");
		assertEquals(NON_DUPLICATE_VALS.size() - 1, p1.size(), "size changed when it should not have been removed");
		assertEquals(NON_DUPLICATE_VALS.size() - 1, p1.unpeekedSize(), "unpeekedSize was not reset after selectPeeked");
		
		// Test a longer peek list
		mp.peek(rand);
		mp.peek(rand);
		mp.peek(rand);
		mp.selectPeeked();
		assertEquals(NON_DUPLICATE_VALS.size() - 2, p1.size(), "size changed when it should not have been removed");
		assertEquals(NON_DUPLICATE_VALS.size() - 2, p1.unpeekedSize(), "unpeekedSize was not reset after selectPeeked");
	}	
	
	@Test
	void selectPeeked_Unpeeked() 
	{
		SimpleObject so = new SimpleObject("test", 1);
		PeekPool<Integer> p1 = PeekPool.create(true, NON_DUPLICATE_VALS);
		PeekPool<Integer> p2 = PeekPool.create(true, DUPLICATE_VALS);
		Map<Integer, RandomizerPool<Integer>> poolMap = new HashMap<>();
		poolMap.put(1, p1);
		poolMap.put(2, p2);
		
		MultiGetter<SimpleObject, Integer> soInt = (so2, cnt) -> so2.getIntField();
		MultiPool<SimpleObject, Integer, Integer> mp = MultiPool.create(poolMap, soInt);
		mp.setPool(so, 0);

		assertNull(mp.selectPeeked(), "selectPeeked did not return null when pool was unpeeked");
	}
	
	@Test
	void peek_selectPeeked_empty() 
	{
		SimpleObject so = new SimpleObject("test", 1);
		PeekPool<Integer> p1 = PeekPool.createEmpty();
		PeekPool<Integer> p2 = PeekPool.create(true, DUPLICATE_VALS);
		Map<Integer, RandomizerPool<Integer>> poolMap = new HashMap<>();
		poolMap.put(1, p1);
		poolMap.put(2, p2);
		
		MultiGetter<SimpleObject, Integer> soInt = (so2, cnt) -> so2.getIntField();
		MultiPool<SimpleObject, Integer, Integer> mp = MultiPool.create(poolMap, soInt);
		mp.setPool(so, 0);

		assertNull(mp.peek(new Random()), "peek did not return null when pool was empty");
		assertNull(mp.selectPeeked(), "selectPeeked did not return null when pool was empty");
	}
	
	@Test
	void useNextPool() 
	{
		SimpleObject so = new SimpleObject("test", 1);
		PeekPool<Integer> base1 = PeekPool.create(true, NON_DUPLICATE_ARRAY);
		PeekPool<Integer> base2 = PeekPool.create(true, DUPLICATE_VALS);
		EliminatePoolSet<Integer> p1 = EliminatePoolSet.create(base1, 3);
		EliminatePoolSet<Integer> p2 = EliminatePoolSet.create(base2, 1);
		Map<Integer, RandomizerPool<Integer>> poolMap = new HashMap<>();
		poolMap.put(1, p1);
		poolMap.put(2, p2);
		
		MultiGetter<SimpleObject, Integer> soInt = (so2, cnt) -> so2.getIntField();
		MultiPool<SimpleObject, Integer, Integer> mp = MultiPool.create(poolMap, soInt);
		mp.setPool(so, 0);

		Random rand = mock(Random.class);
		when(rand.nextInt(anyInt())).thenReturn(0);
		
		assertTrue(mp.useNextPool());
		assertTrue(mp.useNextPool());
		assertFalse(mp.useNextPool());

		mp.reset();
		
		mp.peek(rand);
		mp.selectPeeked();
		
		assertTrue(mp.useNextPool());	
		
		mp.peek(rand);
		assertEquals(NON_DUPLICATE_VALS.size() - 1, p1.getWorkingPools().get(0).size(), "size did not return the full pool size");
		assertEquals(NON_DUPLICATE_VALS.size() - 1, p1.getWorkingPools().get(0).unpeekedSize(), "unpeekedSize did not reflect peeked value");
		assertEquals(NON_DUPLICATE_VALS.size(), p1.getWorkingPools().get(1).size(), "size did not return the full pool size");
		assertEquals(NON_DUPLICATE_VALS.size() - 1, p1.getWorkingPools().get(1).unpeekedSize(), "unpeekedSize did not reflect peeked value");
		
		// Finish out using next pools
		assertTrue(mp.useNextPool());
		assertFalse(mp.useNextPool());

		so.setIntField(2);
		mp.setPool(so, 0);
		assertFalse(mp.useNextPool());
	}
}
