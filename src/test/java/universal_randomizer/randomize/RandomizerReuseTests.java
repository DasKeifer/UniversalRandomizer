package universal_randomizer.randomize;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Random;

import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;

import Support.RandomizerCommonTestsCreate;
import Support.SimpleObject;
import universal_randomizer.Pool;

// Tests the Randomizer Reuse class and by extension the Randomizer class since the
// reuse class is the most simple of the classes
class RandomizerReuseTests {

	final List<Integer> NON_DUPLICATE_VALS = List.of(1, -4, 5, 99);
	final List<Integer> DUPLICATE_VALS = List.of(1, -4, 5, 1, 99, 1, 5);
	final Integer NON_EXISTING_VAL = 7;
	
	private static RandomizerCommonTestsCreate<SimpleObject, Integer> randReuseCreateFn = (p1, p2, p3, p4) -> { return RandomizerReuse.create(p1, p2, p3, p4);};
	
	@Test
	void create() 
	{
		@SuppressWarnings("unchecked")
		Pool<Integer> pool = mock(Pool.class);
		when(pool.copy()).thenReturn(pool);
		
		Random rand = mock(Random.class);
		
		@SuppressWarnings("unchecked")
		EnforceParams<SimpleObject> enforceAction = mock(EnforceParams.class);
		when(enforceAction.copy()).thenReturn(enforceAction);
    	
		
    	try (MockedConstruction<Random> mocked = mockConstruction(Random.class)) 
    	{
    		RandomizerReuse.create("test", pool, rand, enforceAction);
    		assertEquals(0, mocked.constructed().size());
    	}
    	verify(pool, times(1)).copy();
    	verify(enforceAction, times(1)).copy();
    	

    	try (MockedConstruction<Random> mocked = mockConstruction(Random.class)) 
    	{
    		RandomizerReuse.createWithPoolNoEnforce("test", pool, rand);
    		assertEquals(0, mocked.constructed().size());
    	}
    	verify(pool, times(2)).copy();
    	verify(enforceAction, times(1)).copy();


    	try (MockedConstruction<Random> mocked = mockConstruction(Random.class)) 
    	{
    		RandomizerReuse.createPoolFromStream("test", rand, enforceAction);
    		assertEquals(0, mocked.constructed().size());
    	}
    	verify(pool, times(2)).copy();
    	verify(enforceAction, times(2)).copy();
    	
    	
    	try (MockedConstruction<Random> mocked = mockConstruction(Random.class)) 
    	{
    		RandomizerReuse.createPoolFromStreamNoEnforce("test", rand);
    		assertEquals(0, mocked.constructed().size());
    	}
    	verify(pool, times(2)).copy();
    	verify(enforceAction, times(2)).copy();
	}
	
	@Test
	void seed() 
	{
		Randomizer<SimpleObject, Integer> test = RandomizerReuse.create("intField", null, null, null);
		test.seed(0);
		test.seed(new Random());
	}

	@Test
	void perform_noEnforce_basic() 
	{
		CommonRandomizerTests.perform_noEnforce_basic(randReuseCreateFn);
	}

	@Test
	void perform_noEnforce_someFailed() 
	{
		CommonRandomizerTests.perform_noEnforce_someFailed(randReuseCreateFn);
	}
	
	@Test
	void perform_enforce_null() 
	{
		CommonRandomizerTests.perform_enforce_null(randReuseCreateFn);
	}
	
	@Test
	void perform_enforce_retries() 
	{
		CommonRandomizerTests.perform_enforce_retries(randReuseCreateFn);
	}
	
	@Test
	void perform_enforce_exhaustRetries_noResets() 
	{
		CommonRandomizerTests.perform_enforce_exhaustRetries_noResets(randReuseCreateFn);
	}
	
	@Test
	void perform_enforce_exhaustRetries_resets() 
	{
		CommonRandomizerTests.perform_enforce_exhaustRetries_resets(randReuseCreateFn);
	}
	
	@Test
	void perform_noPool() 
	{
		CommonRandomizerTests.perform_noPool(randReuseCreateFn);
	}
}
