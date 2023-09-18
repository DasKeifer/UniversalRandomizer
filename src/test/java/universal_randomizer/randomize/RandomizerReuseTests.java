package universal_randomizer.randomize;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Random;

import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;

import Support.RandomizerCommonTestsGetterCreate;
import Support.RandomizerCommonTestsPoolCreate;
import Support.SimpleObject;
import universal_randomizer.PeekPool;
import universal_randomizer.user_object_apis.Getter;
import universal_randomizer.user_object_apis.SetterNoReturn;

// Tests the Randomizer Reuse class and by extension the Randomizer class since the
// reuse class is the most simple of the classes
class RandomizerReuseTests {

	final List<Integer> NON_DUPLICATE_VALS = List.of(1, -4, 5, 99);
	final List<Integer> DUPLICATE_VALS = List.of(1, -4, 5, 1, 99, 1, 5);
	final Integer NON_EXISTING_VAL = 7;
	
	private static RandomizerCommonTestsPoolCreate<SimpleObject, Integer> randReuseCreateFn = 
			(p1, p2, p3) -> { return RandomizerReuse.create(p1, p2, p3);};
	private static RandomizerCommonTestsGetterCreate<SimpleObject, Integer> randReuseGetterCreateFn = 
			(p1, p2, p3) -> { return RandomizerReuse.createPoolFromStream(p1, p2, p3);};
	
	@Test
	void create() 
	{
		@SuppressWarnings("unchecked")
		PeekPool<Integer> pool = mock(PeekPool.class);
		when(pool.copy()).thenReturn(pool);
		
		@SuppressWarnings("unchecked")
		EnforceParams<SimpleObject> enforceAction = mock(EnforceParams.class);

    	EnforceParams<?> defaultEA = EnforceParams.createNoEnforce();
    	
    	SetterNoReturn<SimpleObject, Integer> setter = (o, v) -> o.intField = v;
    	Getter<SimpleObject, Integer> getter = o -> o.intField;
    	
		RandomizerReuse<SimpleObject, Integer> rr = RandomizerReuse.create(setter, pool, enforceAction);
    	verify(pool, times(1)).copy();
    	assertEquals(setter, rr.getSetter());
    	assertEquals(pool, rr.getPool());
    	assertEquals(enforceAction, rr.getEnforceActions());
    	
    	rr = RandomizerReuse.createWithPoolNoEnforce(setter, pool);
    	verify(pool, times(2)).copy();
    	assertEquals(setter, rr.getSetter());
    	assertEquals(pool, rr.getPool());
    	assertEquals(defaultEA.getMaxResets(), rr.getEnforceActions().getMaxResets());
    	assertEquals(defaultEA.getMaxRetries(), rr.getEnforceActions().getMaxRetries());

    	rr = RandomizerReuse.createPoolFromStream(setter, getter, enforceAction);
    	verify(pool, times(2)).copy();
    	assertEquals(setter, rr.getSetter());
    	assertNull(rr.getPool());
    	assertEquals(enforceAction, rr.getEnforceActions());
    	
    	rr = RandomizerReuse.createPoolFromStreamNoEnforce(setter, getter);
    	verify(pool, times(2)).copy();
    	assertEquals(setter, rr.getSetter());
    	assertNull(rr.getPool());
    	assertEquals(defaultEA.getMaxResets(), rr.getEnforceActions().getMaxResets());
    	assertEquals(defaultEA.getMaxRetries(), rr.getEnforceActions().getMaxRetries());
	}
	
	@Test
	void create_badInput() 
	{
		@SuppressWarnings("unchecked")
		PeekPool<Integer> pool = mock(PeekPool.class);
		when(pool.copy()).thenReturn(pool);
		
		@SuppressWarnings("unchecked")
		EnforceParams<SimpleObject> enforceAction = mock(EnforceParams.class);
		
    	SetterNoReturn<SimpleObject, Integer> setter = (o, v) -> o.intField = v;
    	Getter<SimpleObject, Integer> getter = o -> o.intField;
    	
    	assertNull(RandomizerReuse.create(null, pool, enforceAction));
    	assertNull(RandomizerReuse.create(setter, null, enforceAction));
    	assertNull(RandomizerReuse.createWithPoolNoEnforce(null, pool));
    	assertNull(RandomizerReuse.createWithPoolNoEnforce(setter, null));
    	assertNull(RandomizerReuse.createPoolFromStream(null, getter, enforceAction));
    	assertNull(RandomizerReuse.createPoolFromStream(setter, null, enforceAction));
    	assertNull(RandomizerReuse.createPoolFromStreamNoEnforce(null, getter));
    	assertNull(RandomizerReuse.createPoolFromStreamNoEnforce(setter, null));
	}
	
	@Test
	void seed() 
	{
    	SetterNoReturn<SimpleObject, Integer> setter = (o, v) -> o.intField = v;
    	Getter<SimpleObject, Integer> getter = o -> o.intField;
		RandomizerReuse<SimpleObject, Integer> test = RandomizerReuse.createPoolFromStreamNoEnforce(setter, getter);
		
		Random rand0 = new Random(0);
		test.setRandom(0);
		assertEquals(rand0.nextLong(), test.getRandom().nextLong());
				
		Random randObj = new Random(42);
		test.setRandom(new Random(42));
		assertEquals(randObj.nextLong(), test.getRandom().nextLong());

    	try (MockedConstruction<Random> mocked = mockConstruction(Random.class)) 
    	{
    		Random rand = new Random();
    		when(rand.nextLong()).thenReturn(0L);
    		
			test.unseedRandom();
			assertNotEquals(randObj.nextLong(), test.getRandom().nextLong());
    	}
	}

	@Test
	void perform_noEnforce_basic() 
	{
		CommonRandomizerTestUtils.perform_noEnforce_basic(randReuseCreateFn);
	}

	@Test
	void perform_noEnforce_someFailed() 
	{
		CommonRandomizerTestUtils.perform_noEnforce_someFailed(randReuseCreateFn);
	}
	
	@Test
	void perform_enforce_null() 
	{
		CommonRandomizerTestUtils.perform_enforce_null(randReuseCreateFn);
	}
	
	@Test
	void perform_enforce_retries() 
	{
		CommonRandomizerTestUtils.perform_enforce_retries(randReuseCreateFn);
	}
	
	@Test
	void perform_enforce_exhaustRetries_noResets() 
	{
		CommonRandomizerTestUtils.perform_enforce_exhaustRetries_noResets(randReuseCreateFn);
	}
	
	@Test
	void perform_enforce_exhaustRetries_resets() 
	{
		CommonRandomizerTestUtils.perform_enforce_exhaustRetries_resets(randReuseCreateFn);
	}
	
	@Test
	void perform_noPool() 
	{
		CommonRandomizerTestUtils.perform_noPool(randReuseGetterCreateFn);
	}
}
