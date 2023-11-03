package universal_randomizer.randomize;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.junit.jupiter.api.Test;

import support.SimpleObject;
import universal_randomizer.pool.PeekPool;
import universal_randomizer.user_object_apis.Condition;
import universal_randomizer.user_object_apis.Getter;
import universal_randomizer.user_object_apis.MultiSetter;
import universal_randomizer.user_object_apis.Setter;

// Tests the Randomizer Reuse class and by extension the Randomizer class since the
// reuse class is the most simple of the classes
class SetRandomizerTests {

	final List<Integer> NON_DUPLICATE_VALS = List.of(1, -4, 5, 99);
	final List<Integer> DUPLICATE_VALS = List.of(1, -4, 5, 1, 99, 1, 5);
	final Integer NON_EXISTING_VAL = 7;
	
	@Test
	void create() 
	{
		EnforceParams<Collection<SimpleObject>> enforceAction = EnforceParams.createNoEnforce();
    	MultiSetter<SimpleObject, Integer> ms = (o, v, cnt) -> { o.intField = v; return true; };
    	Setter<SimpleObject, Integer> setter = (o, v) -> { o.intField = v; return true; };
    	Getter<Collection<SimpleObject>, Integer> count2Getter = o -> 2;
    	
    	//create(MultiSetter<O2, S2> setter, Getter<T2, Integer> countGetter, EnforceParams<T2> enforce)
    	Randomizer<Collection<SimpleObject>, SimpleObject, Collection<Integer>, Integer> rr = 
    			SetRandomizer.create(ms, count2Getter, enforceAction);
    	assertEquals(ms, rr.getSetter());
    	assertEquals(count2Getter, rr.getCountGetter());
    	assertEquals(2, rr.getCountGetter().get(null));
    	assertEquals(enforceAction, rr.getEnforceActions());
    	assertNull(rr.getPool());
    	assertNull(rr.getMultiPool());
    	assertNull(rr.getRandom());
    	assertNotNull(SetRandomizer.create(ms, count2Getter, null));
    	
    	//create(MultiSetter<O2, S2> setter, int count, EnforceParams<T2> enforce)
    	rr = SetRandomizer.create(ms, 1, enforceAction);
    	assertEquals(ms, rr.getSetter());
    	assertEquals(1, rr.getCountGetter().get(null));
    	assertEquals(enforceAction, rr.getEnforceActions());
    	assertNull(rr.getPool());
    	assertNull(rr.getMultiPool());
    	assertNull(rr.getRandom());
    	assertNotNull(SetRandomizer.create(ms, 1, null));

    	//create(Setter<O2, S2> setter, EnforceParams<T2> enforce)
    	SimpleObject test = new SimpleObject("test", 0);
    	rr = SetRandomizer.create(setter, enforceAction);
    	assertTrue(setter.setReturn(test, 1)); 	// Setter gets wrapped 
    	assertEquals(1, test.intField); 		// Setter gets wrapped 
    	assertEquals(1, rr.getCountGetter().get(null));
    	assertEquals(enforceAction, rr.getEnforceActions());
    	assertNull(rr.getPool());
    	assertNull(rr.getMultiPool());
    	assertNull(rr.getRandom());
    	assertNotNull(SetRandomizer.create(setter, null));
    	assertNotNull(SetRandomizer.create(ms, enforceAction));
    	assertNotNull(SetRandomizer.create(ms, null));
	}
	
	@Test
	void createNoEnforce() 
	{
    	MultiSetter<SimpleObject, Integer> ms = (o, v, cnt) -> { o.intField = v; return true; };
    	Setter<SimpleObject, Integer> setter = (o, v) -> { o.intField = v; return true; };
    	Getter<Collection<SimpleObject>, Integer> count2Getter = o -> 2;
    	
    	// createNoEnforce(MultiSetter<O2, S2> setter, Getter<T2, Integer> countGetter)
    	Randomizer<Collection<SimpleObject>, SimpleObject, Collection<Integer>, Integer> rr = 
    			SetRandomizer.createNoEnforce(ms, count2Getter);
    	assertEquals(ms, rr.getSetter());
    	assertEquals(count2Getter, rr.getCountGetter());
    	assertEquals(2, rr.getCountGetter().get(null));
    	assertTrue(rr.getEnforceActions().isNoEnforce());
    	assertNull(rr.getPool());
    	assertNull(rr.getMultiPool());
    	assertNull(rr.getRandom());
    	
    	// createNoEnforce(MultiSetter<O2, S2> setter, int count)
    	rr = SetRandomizer.createNoEnforce(ms, 1);
    	assertEquals(ms, rr.getSetter());
    	assertEquals(1, rr.getCountGetter().get(null));
    	assertTrue(rr.getEnforceActions().isNoEnforce());
    	assertNull(rr.getPool());
    	assertNull(rr.getMultiPool());
    	assertNull(rr.getRandom());
    	
    	// createNoEnforce(Setter<O2, S2> setter)
    	SimpleObject test = new SimpleObject("test", 0);
    	rr = SetRandomizer.createNoEnforce(setter);
    	assertTrue(setter.setReturn(test, 1)); 	// Setter gets wrapped 
    	assertEquals(1, test.intField); 		// Setter gets wrapped 
    	assertEquals(1, rr.getCountGetter().get(null));
    	assertTrue(rr.getEnforceActions().isNoEnforce());
    	assertNull(rr.getPool());
    	assertNull(rr.getMultiPool());
    	assertNull(rr.getRandom());
    	assertNotNull(SetRandomizer.createNoEnforce(ms));
	}
	
	@Test
	void create_badInput() 
	{
		EnforceParams<Collection<SimpleObject>> enforceAction = EnforceParams.createNoEnforce();
    	MultiSetter<SimpleObject, Integer> ms = (o, v, cnt) -> { o.intField = v; return true; };
    	MultiSetter<SimpleObject, Integer> msNull = null;
    	Setter<SimpleObject, Integer> setterNull = null;
    	Getter<Collection<SimpleObject>, Integer> count2Getter = o -> 2;
    	Getter<Collection<SimpleObject>, Integer> countGetterNull = null;
    	
    	assertNull(SetRandomizer.create(msNull, count2Getter, enforceAction));
    	assertNull(SetRandomizer.create(ms, countGetterNull, enforceAction));
    	assertNull(SetRandomizer.create(msNull, 1, enforceAction));
    	assertNull(SetRandomizer.create(msNull, enforceAction));
    	assertNull(SetRandomizer.create(setterNull, enforceAction));
    	
    	assertNull(SetRandomizer.createNoEnforce(msNull, count2Getter));
    	assertNull(SetRandomizer.createNoEnforce(ms, countGetterNull));
    	assertNull(SetRandomizer.createNoEnforce(msNull, 1));
    	assertNull(SetRandomizer.createNoEnforce(msNull));
    	assertNull(SetRandomizer.createNoEnforce(setterNull));
	}
	
	@Test
	void setRandomizer() 
	{
		final int LIST_SIZE = 5;
		final int INNER_LIST_SIZE = 3;
		final String EXLCUDED_VALUE = "2";
		
		// Use mock randomizer to force the excluded value to be selected
		Random rand = mock(Random.class);
		when(rand.nextInt(anyInt())).thenReturn(0);

		List<List<SimpleObject>> soListList = new LinkedList<>();
		Map<String, List<String>> vals = new HashMap<>();
		for (int i = 0; i < LIST_SIZE + 2; i++)
		{
			List<SimpleObject> innerList = new LinkedList<>();
			List<String> innerVals = new LinkedList<>();
			for (int inner = 0; inner < INNER_LIST_SIZE; inner++)
			{
				if (i < LIST_SIZE)
				{
					innerList.add(new SimpleObject("test" + (i * 100 + inner), i * 100 + inner));
				}
				innerVals.add("" + (inner + i * INNER_LIST_SIZE));
			}
			if (i < LIST_SIZE)
			{
				soListList.add(innerList);
			}
			vals.put(innerVals.get(0), innerVals);
		}
		
		PeekPool<List<String>> pool = PeekPool.create(true, vals.values());
		
		Condition<List<SimpleObject>> no2Cond = soList -> {
			for (SimpleObject so : soList)
			{
				if (so.getMap().get(0).equals(EXLCUDED_VALUE))
				{
					return false;
				}
			}
			return true;
		};		

		MultiSetter<SimpleObject, String> setMapEntryButNotVal11 = (so, val, cnt) -> {
			if (val.equals("11"))
			{
				return false;
			}
			so.setMapEntry(val, cnt);
			return true;
		};
		
		SetRandomizer<List<SimpleObject>, SimpleObject, List<String>, String> test = 
				SetRandomizer.create(setMapEntryButNotVal11, EnforceParams.create(no2Cond, 3, 0));

		// Perform test and check results
		vals.remove("0");
		vals.remove("9");
		assertTrue(test.perform(soListList.stream(), pool, rand));
		for (List<SimpleObject> soList : soListList)
		{
			List<String> expectedVals = vals.remove(soList.get(0).getMap().get(0));
			assertNotNull(expectedVals, "Failed to find key for so in expected vals: " + soList.get(0).getMap().get(0));
			for (int i = 0; i < soList.size(); i++)
			{
				assertEquals(expectedVals.get(i), soList.get(i).getMap().get(0), 
						soList.get(i).getMap().get(0) + " value " + i + " not found in set");
			}
		}
		assertTrue(vals.isEmpty());
	}
}
