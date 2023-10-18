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
class SetRandomizerTests {

	final List<Integer> NON_DUPLICATE_VALS = List.of(1, -4, 5, 99);
	final List<Integer> DUPLICATE_VALS = List.of(1, -4, 5, 1, 99, 1, 5);
	final Integer NON_EXISTING_VAL = 7;
	
	@Test
	void create() 
	{
		EnforceParams<Collection<SimpleObject>> enforceAction = EnforceParams.createNoEnforce();
    	MultiSetterNoReturn<SimpleObject, Integer> ms = (o, v, cnt) -> o.intField = v;
    	SetterNoReturn<SimpleObject, Integer> setter = (o, v) -> o.intField = v;
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

    	//create(MultiSetter<O2, S2> setter, EnforceParams<T2> enforce)
    	rr = SetRandomizer.create(setter, enforceAction);
    	assertEquals(setter, rr.getSetter());
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
    	MultiSetterNoReturn<SimpleObject, Integer> ms = (o, v, cnt) -> o.intField = v;
    	SetterNoReturn<SimpleObject, Integer> setter = (o, v) -> o.intField = v;
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
    	
    	// createNoEnforce(MultiSetter<O2, S2> setter)
    	rr = SetRandomizer.createNoEnforce(setter);
    	assertEquals(setter, rr.getSetter());
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
    	MultiSetterNoReturn<SimpleObject, Integer> ms = (o, v, cnt) -> o.intField = v;
    	MultiSetterNoReturn<SimpleObject, Integer> msNull = null;
    	SetterNoReturn<SimpleObject, Integer> setterNull = null;
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
	void assignAndCheckEnforce() 
	{
		// TODO
	}
}
