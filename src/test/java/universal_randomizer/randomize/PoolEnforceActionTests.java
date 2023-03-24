package universal_randomizer.randomize;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class PoolEnforceActionTests {

	@Test
	void constructor() 
	{
		final int MAX_DEPTH = 2;
		PoolEnforceActions test = new PoolEnforceActions(MAX_DEPTH);
		assertEquals(MAX_DEPTH, test.getMaxDepth());
	}
	
	@Test
	void none() 
	{
		PoolEnforceActions test = PoolEnforceActions.createNone();
		assertEquals(0, test.getMaxDepth());
	}
	
	@Test
	void copy() 
	{
		final int MAX_DEPTH = 2;
		PoolEnforceActions test = new PoolEnforceActions(MAX_DEPTH);
		PoolEnforceActions copy = test.copy();
		assertEquals(MAX_DEPTH, copy.getMaxDepth());
	}
}
