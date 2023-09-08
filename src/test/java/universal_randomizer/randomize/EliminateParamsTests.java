package universal_randomizer.randomize;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class EliminateParamsTests {

	@Test
	void constructor() 
	{
		final int MAX_DEPTH = 2;
		EliminateParams test = EliminateParams.create(MAX_DEPTH);
		assertEquals(MAX_DEPTH, test.getMaxDepth());
		
		assertNull(EliminateParams.create(0));
	}
	
	@Test
	void noAdditionalPools() 
	{
		EliminateParams test = EliminateParams.createNoAdditionalPools();
		assertEquals(1, test.getMaxDepth());
	}
}
