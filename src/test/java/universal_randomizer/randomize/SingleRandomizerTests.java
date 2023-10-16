package universal_randomizer.randomize;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import java.util.List;

import org.junit.jupiter.api.Test;

import Support.SimpleObject;
import universal_randomizer.user_object_apis.Getter;
import universal_randomizer.user_object_apis.MultiSetterNoReturn;
import universal_randomizer.user_object_apis.SetterNoReturn;

// Tests the Randomizer Reuse class and by extension the Randomizer class since the
// reuse class is the most simple of the classes
class SingleRandomizerTests {

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
    	
    	//create(MultiSetter<T2, P2> setter, Getter<T2, Integer> countGetter, EnforceParams<T2> enforce)
		Randomizer<SimpleObject, SimpleObject, Integer, Integer> rr = SingleRandomizer.create(ms, count2Getter, enforceAction);
    	assertEquals(ms, rr.getSetter());
    	assertEquals(count2Getter, rr.getCountGetter());
    	assertEquals(2, rr.getCountGetter().get(null));
    	assertEquals(enforceAction, rr.getEnforceActions());
    	assertNull(rr.getPool());
    	assertNull(rr.getMultiPool());
    	assertNull(rr.getRandom());
    	assertNotNull(SingleRandomizer.create(ms, count2Getter, null));
    	
    	//create(MultiSetter<T2, P2> setter, int count, EnforceParams<T2> enforce)
    	rr = SingleRandomizer.create(ms, 1, enforceAction);
    	assertEquals(ms, rr.getSetter());
    	assertEquals(1, rr.getCountGetter().get(null));
    	assertEquals(enforceAction, rr.getEnforceActions());
    	assertNull(rr.getPool());
    	assertNull(rr.getMultiPool());
    	assertNull(rr.getRandom());
    	assertNotNull(SingleRandomizer.create(ms, 1, null));

    	// create(Setter<T2, P2> setter, EnforceParams<T2> enforce)
    	rr = SingleRandomizer.create(setter, enforceAction);
    	assertEquals(setter, rr.getSetter());
    	assertEquals(1, rr.getCountGetter().get(null));
    	assertEquals(enforceAction, rr.getEnforceActions());
    	assertNull(rr.getPool());
    	assertNull(rr.getMultiPool());
    	assertNull(rr.getRandom());
    	assertNotNull(SingleRandomizer.create(setter, null));
	}
	
	@Test
	void createNoEnforce() 
	{
    	MultiSetterNoReturn<SimpleObject, Integer> ms = (o, v, cnt) -> o.intField = v;
    	SetterNoReturn<SimpleObject, Integer> setter = (o, v) -> o.intField = v;
    	Getter<SimpleObject, Integer> count2Getter = o -> 2;
    	
    	// createNoEnforce(MultiSetter<T2, P2> setter, Getter<T2, Integer> countGetter)
    	Randomizer<SimpleObject, SimpleObject, Integer, Integer> rr = SingleRandomizer.createNoEnforce(ms, count2Getter);
    	assertEquals(ms, rr.getSetter());
    	assertEquals(count2Getter, rr.getCountGetter());
    	assertEquals(2, rr.getCountGetter().get(null));
    	assertTrue(rr.getEnforceActions().isNoEnforce());
    	assertNull(rr.getPool());
    	assertNull(rr.getMultiPool());
    	assertNull(rr.getRandom());
    	
    	// createNoEnforce(MultiSetter<T2, P2> setter, int count)
    	rr = SingleRandomizer.createNoEnforce(ms, 1);
    	assertEquals(ms, rr.getSetter());
    	assertEquals(1, rr.getCountGetter().get(null));
    	assertTrue(rr.getEnforceActions().isNoEnforce());
    	assertNull(rr.getPool());
    	assertNull(rr.getMultiPool());
    	assertNull(rr.getRandom());
    	
    	// createNoEnforce(Setter<T2, P2> setter)
    	rr = SingleRandomizer.createNoEnforce(setter);
    	assertEquals(setter, rr.getSetter());
    	assertEquals(1, rr.getCountGetter().get(null));
    	assertTrue(rr.getEnforceActions().isNoEnforce());
    	assertNull(rr.getPool());
    	assertNull(rr.getMultiPool());
    	assertNull(rr.getRandom());
	}
	
	@Test
	void create_badInput() 
	{
		@SuppressWarnings("unchecked")
		EnforceParams<SimpleObject> enforceAction = mock(EnforceParams.class);

    	MultiSetterNoReturn<SimpleObject, Integer> ms = (o, v, cnt) -> o.intField = v;
    	MultiSetterNoReturn<SimpleObject, Integer> msNull = null;
    	SetterNoReturn<SimpleObject, Integer> setterNull = null;
    	Getter<SimpleObject, Integer> count2Getter = o -> 2;
    	Getter<SimpleObject, Integer> countGetterNull = null;
    	
    	assertNull(SingleRandomizer.create(msNull, count2Getter, enforceAction));
    	assertNull(SingleRandomizer.create(ms, countGetterNull, enforceAction));
    	assertNull(SingleRandomizer.create(msNull, 1, enforceAction));
    	assertNull(SingleRandomizer.create(setterNull, enforceAction));
    	
    	assertNull(SingleRandomizer.createNoEnforce(msNull, count2Getter));
    	assertNull(SingleRandomizer.createNoEnforce(ms, countGetterNull));
    	assertNull(SingleRandomizer.createNoEnforce(msNull, 1));
    	assertNull(SingleRandomizer.createNoEnforce(setterNull));
	}
	
	@Test
	void assignAndCheckEnforce() 
	{
		// TODO
//		@Override	
//		protected boolean assignAndCheckEnforce(T obj, P poolValue, int count)
//		{
//			return getSetter().setReturn(obj, poolValue, count) && getEnforceActions().evaluateEnforce(obj);
//		}
		
	}
}
