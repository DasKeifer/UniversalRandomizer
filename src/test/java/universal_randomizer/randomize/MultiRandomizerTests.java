package universal_randomizer.randomize;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.junit.jupiter.api.Test;

import support.SimpleObject;
import universal_randomizer.pool.PeekPool;
import universal_randomizer.user_object_apis.Condition;
import universal_randomizer.user_object_apis.MultiSetter;

// Tests the Randomizer Reuse class and by extension the Randomizer class since the
// reuse class is the most simple of the classes
class MultiRandomizerTests {

	final List<Integer> NON_DUPLICATE_VALS = List.of(1, -4, 5, 99);
	final List<Integer> DUPLICATE_VALS = List.of(1, -4, 5, 1, 99, 1, 5);
	final Integer NON_EXISTING_VAL = 7;
	
	@Test
	void create() 
	{
		EnforceParams<SimpleObject> enforceAction = EnforceParams.createNoEnforce();
    	MultiSetter<SimpleObject, Integer> ms = (o, v, cnt) -> { o.intField = v; return true; };
    	
    	// create(MultiSetter<T2, S2> setter, EnforceParams<T2> enforce)
    	Randomizer<SimpleObject, SimpleObject, List<Integer>, Integer> rr = 
    			MultiRandomizer.create(ms, enforceAction);
    	assertEquals(ms, rr.getSetter());
    	assertEquals(1, rr.getCountGetter().get(null));
    	assertEquals(enforceAction, rr.getEnforceActions());
    	assertNull(rr.getPool());
    	assertNull(rr.getMultiPool());
    	assertNull(rr.getRandom());
    	assertNotNull(MultiRandomizer.create(ms, null));
	}
	
	@Test
	void createNoEnforce() 
	{
    	MultiSetter<SimpleObject, Integer> ms = (o, v, cnt) -> { o.intField = v; return true; };

    	// createNoEnforce(MultiSetter<T2, S2> setter)
    	Randomizer<SimpleObject, SimpleObject, List<Integer>, Integer> rr = 
    			MultiRandomizer.createNoEnforce(ms);
    	assertEquals(ms, rr.getSetter());
    	assertEquals(1, rr.getCountGetter().get(null));
    	assertTrue(rr.getEnforceActions().isNoEnforce());
    	assertNull(rr.getPool());
    	assertNull(rr.getMultiPool());
    	assertNull(rr.getRandom());
    	assertNotNull(MultiRandomizer.createNoEnforce(ms));
	}
	
	@Test
	void create_badInput() 
	{
		EnforceParams<SimpleObject> enforceAction = EnforceParams.createNoEnforce();
    	MultiSetter<SimpleObject, Integer> msNull = null;
    	
    	assertNull(MultiRandomizer.create(msNull, enforceAction));
    	assertNull(MultiRandomizer.createNoEnforce(msNull));
	}
	
	@Test
	void multiRandomizer() 
	{
		final int LIST_SIZE = 5;
		final String EXCLUDED_VAL = "3";
		
		// Use mock randomizer to force the excluded value to be selected
		Random rand = mock(Random.class);
		when(rand.nextInt(anyInt())).thenReturn(0);
		
		// Set expectations
		Map<Integer, List<String>> valsMap = new HashMap<>();
		List<SimpleObject> list = new LinkedList<>();
		for (int i = 0; i < LIST_SIZE + 2; i++)
		{
			// add another to the list and account for the 
			// excluded value
			valsMap.put(i, List.of("" + i, "" + (i + LIST_SIZE + 2)));
			if (i < LIST_SIZE)
			{
				list.add(new SimpleObject("test" + i, i));
			}
		}
		
		PeekPool<List<String>> pool = PeekPool.create(true, valsMap.values());
		Condition<SimpleObject> no3Cond = so -> {
			for (String val : so.getMap().values())
			{
				if (val.equals(EXCLUDED_VAL))
				{
					return false;
				}
			}
			return true;
		};		

		MultiSetter<SimpleObject, String> setMapEntryButNotVal5 = (so, val, cnt) -> {
			if (val.equals("5"))
			{
				return false;
			}
			so.setMapEntry(val, cnt);
			return true;
		};
		MultiRandomizer<SimpleObject, List<String>, String> test = MultiRandomizer.create(
				setMapEntryButNotVal5, 
				EnforceParams.create(no3Cond, 3, 0));

		// Perform test and check results
		valsMap.remove(3);
		valsMap.remove(5);
		assertTrue(test.perform(list.stream(), pool, rand));
		for (SimpleObject so : list)
		{
			assertEquals(2, so.getMap().size());
			int key = Integer.parseInt(so.getMap().get(0));
			List<String> expectedVals = valsMap.remove(key);
			assertNotNull(expectedVals, "Failed to find key for so in expected vals: " + key);
			assertEquals(expectedVals.get(0), so.getMap().get(0), so.getMap().get(1) + " value 1 not found in set");
			assertEquals(expectedVals.get(1), so.getMap().get(1), so.getMap().get(2) + " value 2 not found in set");
		}
		assertTrue(valsMap.isEmpty());
	}
}
