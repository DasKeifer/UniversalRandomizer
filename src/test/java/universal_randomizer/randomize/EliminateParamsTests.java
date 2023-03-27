package universal_randomizer.randomize;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class EliminateParamsTests {

	@Test
	void constructor() 
	{
		final int MAX_DEPTH = 2;
		EliminateParams test = new EliminateParams(MAX_DEPTH);
		assertEquals(MAX_DEPTH, test.getMaxDepth());
	}
	
	@Test
	void noAdditionalPools() 
	{
		EliminateParams test = EliminateParams.createNoAdditionalPools();
		assertEquals(1, test.getMaxDepth());
	}
	
	@Test
	void copy() 
	{
		final int MAX_DEPTH = 2;
		EliminateParams test = new EliminateParams(MAX_DEPTH);
		EliminateParams copy = test.copy();
		assertEquals(MAX_DEPTH, copy.getMaxDepth());
	}
}
