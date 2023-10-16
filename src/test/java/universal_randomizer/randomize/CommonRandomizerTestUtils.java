package universal_randomizer.randomize;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.mockito.AdditionalAnswers;

import Support.RandomizerCommonTestsPoolCreate;
import Support.SimpleObject;
import Support.SimpleObjectUtils;
import universal_randomizer.condition.Comparison;
import universal_randomizer.condition.Negate;
import universal_randomizer.condition.SimpleCondition;
import universal_randomizer.pool.PeekPool;
import universal_randomizer.user_object_apis.Condition;
import universal_randomizer.user_object_apis.Getter;
import universal_randomizer.user_object_apis.Setter;

// Tests the Randomizer Reuse class and by extension the Randomizer class since the
// reuse class is the most simple of the classes
class CommonRandomizerTestUtils {
	




//	@SuppressWarnings("unchecked")
//	public static void perform_noPool(RandomizerCommonTestsGetterCreate<SimpleObject, Integer> createFn)
//	{
//		final int EXCLUDED_VAL = 5;
//		final int LIST_SIZE = 10;
//		final List<Integer> POOL_VALS =     Arrays.asList(0, 1, 2, 3, 4, EXCLUDED_VAL, 6, 7, 8, 9, 10);
//		// 5 will be excluded by the enforce until it gives up and then ignores the condition
//		final List<Integer> EXPECTED_VALS = Arrays.asList(0, 1, 2, 3, 4,               6, 7, 8, 9, 10);
//
//		// Create test data and object
//		Condition<SimpleObject> neq5 = SimpleCondition.create(getterInt, Negate.YES, Comparison.EQUAL, EXCLUDED_VAL);
//		EnforceParams<SimpleObject> enforce = EnforceParams.create(neq5, 2, 0);
//		
//		List<SimpleObject> list = createSimpleObjects(LIST_SIZE);
//		Randomizer<SimpleObject, Integer> test = null;
//		
//		Random rand = mock(Random.class);
//		when(rand.nextInt(anyInt())).thenReturn(0);
//		
//	    try (@SuppressWarnings("rawtypes")
//		MockedStatic<PeekPool> intPool = Mockito.mockStatic(PeekPool.class)) 
//	    {
//			// Setup static function
//			PeekPool<Integer> pool = mock(PeekPool.class);
//			when(pool.peek(any())).thenAnswer(AdditionalAnswers.returnsElementsOf(POOL_VALS));
//			when(pool.copy()).thenReturn(pool);
//
//	    	intPool.when(() -> PeekPool.create(anyBoolean(), any(Stream.class)))
//	          .thenReturn(pool);
//	    	
//	    	test = createFn.createPoolFromStream(setterInt, getterInt, enforce);
//    		
//			// Perform test and check results
//			assertTrue(test.perform(list.stream(), pool, rand));
//	    }
//	    
//		List<Integer> results = SimpleObjectUtils.toIntFieldList(list);
//		assertIterableEquals(EXPECTED_VALS, results);
//	}
}
