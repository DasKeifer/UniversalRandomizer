package universal_randomizer.randomize;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collection;
import java.util.List;

import org.junit.jupiter.api.Test;

import Support.SimpleObject;
import universal_randomizer.user_object_apis.Getter;
import universal_randomizer.user_object_apis.MultiSetterNoReturn;
import universal_randomizer.user_object_apis.SetterNoReturn;

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
    	MultiSetterNoReturn<SimpleObject, Integer> ms = (o, v, cnt) -> o.intField = v;
    	SetterNoReturn<SimpleObject, Integer> setter = (o, v) -> o.intField = v;
    	Getter<SimpleObject, Integer> count2Getter = o -> 2;
    	
    	// create(MultiSetter<T2, S2> setter, Getter<T2, Integer> countGetter, EnforceParams<T2> enforce)
		Randomizer<SimpleObject, SimpleObject, Collection<Integer>, Integer> rr = MultiRandomizer.create(ms, count2Getter, enforceAction);
    	assertEquals(ms, rr.getSetter());
    	assertEquals(count2Getter, rr.getCountGetter());
    	assertEquals(2, rr.getCountGetter().get(null));
    	assertEquals(enforceAction, rr.getEnforceActions());
    	assertNull(rr.getPool());
    	assertNull(rr.getMultiPool());
    	assertNull(rr.getRandom());
    	assertNotNull(MultiRandomizer.create(ms, count2Getter, null));
    	
    	// create(MultiSetter<T2, S2> setter, int count, EnforceParams<T2> enforce)
    	rr = MultiRandomizer.create(ms, 1, enforceAction);
    	assertEquals(ms, rr.getSetter());
    	assertEquals(1, rr.getCountGetter().get(null));
    	assertEquals(enforceAction, rr.getEnforceActions());
    	assertNull(rr.getPool());
    	assertNull(rr.getMultiPool());
    	assertNull(rr.getRandom());
    	assertNotNull(MultiRandomizer.create(ms, 1, null));

    	// create(MultiSetter<T2, S2> setter, EnforceParams<T2> enforce)
    	rr = MultiRandomizer.create(setter, enforceAction);
    	assertEquals(setter, rr.getSetter());
    	assertEquals(1, rr.getCountGetter().get(null));
    	assertEquals(enforceAction, rr.getEnforceActions());
    	assertNull(rr.getPool());
    	assertNull(rr.getMultiPool());
    	assertNull(rr.getRandom());
    	assertNotNull(MultiRandomizer.create(setter, null));
    	assertNotNull(MultiRandomizer.create(ms, enforceAction));
    	assertNotNull(MultiRandomizer.create(ms, null));
	}
	
	@Test
	void createNoEnforce() 
	{
    	MultiSetterNoReturn<SimpleObject, Integer> ms = (o, v, cnt) -> o.intField = v;
    	SetterNoReturn<SimpleObject, Integer> setter = (o, v) -> o.intField = v;
    	Getter<SimpleObject, Integer> count2Getter = o -> 2;
    	
    	// createNoEnforce(MultiSetter<T2, S2> setter, Getter<T2, Integer> countGetter)
    	Randomizer<SimpleObject, SimpleObject, Collection<Integer>, Integer> rr = MultiRandomizer.createNoEnforce(ms, count2Getter);
    	assertEquals(ms, rr.getSetter());
    	assertEquals(count2Getter, rr.getCountGetter());
    	assertEquals(2, rr.getCountGetter().get(null));
    	assertTrue(rr.getEnforceActions().isNoEnforce());
    	assertNull(rr.getPool());
    	assertNull(rr.getMultiPool());
    	assertNull(rr.getRandom());
    	
    	// createNoEnforce(MultiSetter<T2, S2> setter, int count)
    	rr = MultiRandomizer.createNoEnforce(ms, 1);
    	assertEquals(ms, rr.getSetter());
    	assertEquals(1, rr.getCountGetter().get(null));
    	assertTrue(rr.getEnforceActions().isNoEnforce());
    	assertNull(rr.getPool());
    	assertNull(rr.getMultiPool());
    	assertNull(rr.getRandom());
    	
    	// createNoEnforce(MultiSetter<T2, S2> setter)
    	rr = MultiRandomizer.createNoEnforce(setter);
    	assertEquals(setter, rr.getSetter());
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
    	MultiSetterNoReturn<SimpleObject, Integer> ms = (o, v, cnt) -> o.intField = v;
    	MultiSetterNoReturn<SimpleObject, Integer> msNull = null;
    	SetterNoReturn<SimpleObject, Integer> setterNull = null;
    	Getter<SimpleObject, Integer> count2Getter = o -> 2;
    	Getter<SimpleObject, Integer> countGetterNull = null;
    	
    	assertNull(MultiRandomizer.create(msNull, count2Getter, enforceAction));
    	assertNull(MultiRandomizer.create(ms, countGetterNull, enforceAction));
    	assertNull(MultiRandomizer.create(msNull, 1, enforceAction));
    	assertNull(MultiRandomizer.create(msNull, enforceAction));
    	assertNull(MultiRandomizer.create(setterNull, enforceAction));
    	
    	assertNull(MultiRandomizer.createNoEnforce(msNull, count2Getter));
    	assertNull(MultiRandomizer.createNoEnforce(ms, countGetterNull));
    	assertNull(MultiRandomizer.createNoEnforce(msNull, 1));
    	assertNull(MultiRandomizer.createNoEnforce(msNull));
    	assertNull(MultiRandomizer.createNoEnforce(setterNull));
	}
	
	@Test
	void assignAndCheckEnforce() 
	{
		// TODO
	}
}
