package universal_randomizer.randomize;



class RandomizerEliminateTests {

//	//TODO: Update to actually be for elimate version
//	
//	final List<Integer> NON_DUPLICATE_VALS = List.of(1, -4, 5, 99);
//	final List<Integer> DUPLICATE_VALS = List.of(1, -4, 5, 1, 99, 1, 5);
//	final Integer NON_EXISTING_VAL = 7;
//
//	private static RandomizerCommonTestsPoolCreate<SimpleObject, Integer> randElimCreateFn = 
//			(p1, p2, p3) -> { return RandomizerEliminate.create(p1, p2, p3, null);};
//	private static RandomizerCommonTestsGetterCreate<SimpleObject, Integer> randElimGetterCreateFn = 
//			(p1, p2, p3) -> { return RandomizerEliminate.createPoolFromStream(p1, p2, p3, null);};
//	
//	@Test
//	void create() 
//	{
//		@SuppressWarnings("unchecked")
//		PeekPool<Integer> pool = mock(PeekPool.class);
//		when(pool.copy()).thenReturn(pool);
//		
//		@SuppressWarnings("unchecked")
//		EnforceParams<SimpleObject> enforceAction = mock(EnforceParams.class);
//		EliminateParams poolAction = mock(EliminateParams.class);
//		
//    	EnforceParams<?> defaultEA = EnforceParams.createNoEnforce();
//
//    	SetterNoReturn<SimpleObject, Integer> setter = (o, v) -> o.intField = v;
//    	Getter<SimpleObject, Integer> getter = o -> o.intField;
//    	
//		
//		RandomizerEliminate<SimpleObject, Integer> re = RandomizerEliminate.create(setter, pool, enforceAction, poolAction);
//    	verify(pool, times(1)).copy();
//    	assertEquals(setter, re.getSetter());
//    	assertEquals(pool, re.getPool());
//    	assertEquals(enforceAction, re.getEnforceActions());
//    	
//		re = RandomizerEliminate.createWithPoolNoEnforce(setter, pool);
//    	verify(pool, times(2)).copy();
//    	assertEquals(setter, re.getSetter());
//    	assertEquals(pool, re.getPool());
//    	assertEquals(defaultEA.getMaxResets(), re.getEnforceActions().getMaxResets());
//    	assertEquals(defaultEA.getMaxRetries(), re.getEnforceActions().getMaxRetries());
//
//    	re = RandomizerEliminate.createPoolFromStream(setter, getter, enforceAction, poolAction);
//    	verify(pool, times(2)).copy();
//    	assertEquals(setter, re.getSetter());
//    	assertNull(re.getPool());
//    	assertEquals(enforceAction, re.getEnforceActions());
//    	
//    	re = RandomizerEliminate.createPoolFromStreamNoEnforce(setter, getter);
//    	verify(pool, times(2)).copy();
//    	assertEquals(setter, re.getSetter());
//    	assertNull(re.getPool());
//    	assertEquals(defaultEA.getMaxResets(), re.getEnforceActions().getMaxResets());
//    	assertEquals(defaultEA.getMaxRetries(), re.getEnforceActions().getMaxRetries());
//	}
//	
//	@Test
//	void create_badInput() 
//	{
//		@SuppressWarnings("unchecked")
//		PeekPool<Integer> pool = mock(PeekPool.class);
//		when(pool.copy()).thenReturn(pool);
//		
//		@SuppressWarnings("unchecked")
//		EnforceParams<SimpleObject> enforceAction = mock(EnforceParams.class);
//		EliminateParams poolAction = mock(EliminateParams.class);
//
//    	SetterNoReturn<SimpleObject, Integer> setter = (o, v) -> o.intField = v;
//    	Getter<SimpleObject, Integer> getter = o -> o.intField;
//    	
//    	assertNull(RandomizerEliminate.create(null, pool, enforceAction, poolAction));
//    	assertNull(RandomizerEliminate.create(setter, null, enforceAction, poolAction));
//    	assertNull(RandomizerEliminate.createWithPoolNoEnforce(null, pool));
//    	assertNull(RandomizerEliminate.createWithPoolNoEnforce(setter, null));
//    	assertNull(RandomizerEliminate.createPoolFromStream(null, getter, enforceAction, poolAction));
//    	assertNull(RandomizerEliminate.createPoolFromStream(setter, null, enforceAction, poolAction));
//    	assertNull(RandomizerEliminate.createPoolFromStreamNoEnforce(null, getter));
//    	assertNull(RandomizerEliminate.createPoolFromStreamNoEnforce(setter, null));
//	}
//	
//	@Test
//	void seed() 
//	{
//		RandomizerEliminate<SimpleObject, Integer> test = RandomizerEliminate.createPoolFromStreamNoEnforce(
//				(o, v) -> o.intField = v,
//				 o -> o.intField);
//		
//		Random rand0 = new Random(0);
//		test.setRandom(0);
//		assertEquals(rand0.nextLong(), test.getRandom().nextLong());
//				
//		Random randObj = new Random(42);
//		test.setRandom(new Random(42));
//		assertEquals(randObj.nextLong(), test.getRandom().nextLong());
//
//    	try (MockedConstruction<Random> mocked = mockConstruction(Random.class)) 
//    	{
//    		Random rand = new Random();
//    		when(rand.nextLong()).thenReturn(0L);
//    		
//			test.unseedRandom();
//			assertNotEquals(randObj.nextLong(), test.getRandom().nextLong());
//    	}
//	}
//
//	@Test
//	void perform_noEnforce_basic() 
//	{
//		CommonRandomizerTestUtils.perform_noEnforce_basic(randElimCreateFn);
//	}
//
//	@Test
//	void perform_noEnforce_someFailed() 
//	{
//		CommonRandomizerTestUtils.perform_noEnforce_someFailed(randElimCreateFn);
//	}
//	
//	@Test
//	void perform_enforce_null() 
//	{
//		CommonRandomizerTestUtils.perform_enforce_null(randElimCreateFn);
//	}
//	
//	@Test
//	void perform_enforce_retries() 
//	{
//		CommonRandomizerTestUtils.perform_enforce_retries(randElimCreateFn);
//	}
//	
//	@Test
//	void perform_enforce_exhaustRetries_noResets() 
//	{
//		CommonRandomizerTestUtils.perform_enforce_exhaustRetries_noResets(randElimCreateFn);
//	}
//	
//	@Test
//	void perform_enforce_exhaustRetries_resets() 
//	{
//		CommonRandomizerTestUtils.perform_enforce_exhaustRetries_resets(randElimCreateFn);
//	}
//	
//	@Test
//	void perform_noPool() 
//	{
//		CommonRandomizerTestUtils.perform_noPool(randElimGetterCreateFn);
//	}
//	
//	@Test
//	void perform_pools_noEnforce() 
//	{
//		final int LIST_SIZE = 10;
//		final List<Integer> POOL_1_VALS =     Arrays.asList(0, 1, 2, 3, 4, null);
//		final List<Integer> POOL_2_VALS =     Arrays.asList(			   5, 6, 7, 8, 9, null);
//		
//		// 5 will be excluded by the enforce
//		final List<Integer> EXPECTED_VALS = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
//		
//		// Setup mocks
//		Random rand = mock(Random.class);
//		when(rand.nextInt(anyInt())).thenReturn(0);
//		
//		@SuppressWarnings("unchecked")
//		PeekPool<Integer> poolBase = mock(PeekPool.class);
//		@SuppressWarnings("unchecked")
//		PeekPool<Integer> pool1 = mock(PeekPool.class);
//		@SuppressWarnings("unchecked")
//		PeekPool<Integer> pool2 = mock(PeekPool.class);
//		
//		when(poolBase.copy()).thenReturn(pool1);
//		when(pool1.peek(any())).thenAnswer(AdditionalAnswers.returnsElementsOf(POOL_1_VALS));
//		when(pool1.copy()).thenReturn(pool2);
//		when(pool2.peek(any())).thenAnswer(AdditionalAnswers.returnsElementsOf(POOL_2_VALS));
//		when(pool2.copy()).thenReturn(null);
//
//		// Create test data and object
//		EliminateParams elimParams = EliminateParams.create(2);
//		
//		Setter<SimpleObject, Integer> setterInt = (o, v) -> {
//			if (v == null)
//			{
//				return false;
//			}
//			o.intField = v;
//			return true;
//		};
//		
//		List<SimpleObject> list = CommonRandomizerTestUtils.createSimpleObjects(LIST_SIZE);
//		Randomizer<SimpleObject, Integer> test = RandomizerEliminate.create(setterInt, poolBase, null, elimParams);
//		test.setRandom(rand);
//
//		// Perform test and check results
//		assertTrue(test.perform(list.stream()));
//		List<Integer> results = SimpleObjectUtils.toIntFieldList(list);
//		assertIterableEquals(EXPECTED_VALS, results);
//	}
//	
//	@Test
//	void perform_pools_enforce() 
//	{
//		final int EXCLUDED_VAL = 5;
//		final int LIST_SIZE = 10;
//		final List<Integer> POOL_1_VALS =     Arrays.asList(0, 1, 2, 3, 4, EXCLUDED_VAL,   7, 8, EXCLUDED_VAL,    10);
//		final List<Integer> POOL_2_VALS =     Arrays.asList(							6,                     9);
//		
//		// 5 will be excluded by the enforce
//		final List<Integer> EXPECTED_VALS = Arrays.asList(0, 1, 2, 3, 4,                6, 7, 8,               9, 10);
//		
//		// Setup mocks
//		Random rand = mock(Random.class);
//		when(rand.nextInt(anyInt())).thenReturn(0);
//		
//		@SuppressWarnings("unchecked")
//		PeekPool<Integer> poolBase = mock(PeekPool.class);
//		@SuppressWarnings("unchecked")
//		PeekPool<Integer> pool1 = mock(PeekPool.class);
//		@SuppressWarnings("unchecked")
//		PeekPool<Integer> pool2 = mock(PeekPool.class);
//		
//		when(poolBase.copy()).thenReturn(pool1);
//		when(pool1.peek(any())).thenAnswer(AdditionalAnswers.returnsElementsOf(POOL_1_VALS));
//		when(pool1.copy()).thenReturn(pool2);
//		when(pool2.peek(any())).thenAnswer(AdditionalAnswers.returnsElementsOf(POOL_2_VALS));
//		when(pool2.copy()).thenReturn(null);
//
//		// Create test data and object
//		Condition<SimpleObject> neq5 = SimpleCondition.create(o -> o.intField, Negate.YES, Comparison.EQUAL, EXCLUDED_VAL);
//		EnforceParams<SimpleObject> enforce = EnforceParams.create(neq5, 0, 0);
//		EliminateParams elimParams = EliminateParams.create(2);
//		
//		List<SimpleObject> list = CommonRandomizerTestUtils.createSimpleObjects(LIST_SIZE);
//		Randomizer<SimpleObject, Integer> test = RandomizerEliminate.create((o, v) -> o.intField = v, poolBase, enforce, elimParams);
//		test.setRandom(rand);
//		
//		// Perform test and check results
//		assertTrue(test.perform(list.stream()));
//		List<Integer> results = SimpleObjectUtils.toIntFieldList(list);
//		assertIterableEquals(EXPECTED_VALS, results);
//	}
//	
//	@Test
//	void perform_poolsExhaust_enforce() 
//	{
//		final int EXCLUDED_VAL = 5;
//		final int LIST_SIZE = 10;
//		final List<Integer> POOL_1_VALS =     Arrays.asList(0, 1, 2, 3, 4, EXCLUDED_VAL,    7, 8, EXCLUDED_VAL,    10);
//		final List<Integer> POOL_2_VALS =     Arrays.asList(			      EXCLUDED_VAL,                     9);
//		
//		// 5 will appear because we exhausted the pools
//		final List<Integer> EXPECTED_VALS = Arrays.asList(0, 1, 2, 3, 4,      EXCLUDED_VAL, 7, 8,               9, 10);
//		
//		// Setup mocks
//		Random rand = mock(Random.class);
//		when(rand.nextInt(anyInt())).thenReturn(0);
//		
//		@SuppressWarnings("unchecked")
//		PeekPool<Integer> poolBase = mock(PeekPool.class);
//		@SuppressWarnings("unchecked")
//		PeekPool<Integer> pool1 = mock(PeekPool.class);
//		@SuppressWarnings("unchecked")
//		PeekPool<Integer> pool2 = mock(PeekPool.class);
//		
//		when(poolBase.copy()).thenReturn(pool1);
//		when(pool1.peek(any())).thenAnswer(AdditionalAnswers.returnsElementsOf(POOL_1_VALS));
//		when(pool1.copy()).thenReturn(pool2);
//		when(pool2.peek(any())).thenAnswer(AdditionalAnswers.returnsElementsOf(POOL_2_VALS));
//		when(pool2.copy()).thenReturn(null);
//
//		// Create test data and object
//		Condition<SimpleObject> neq5 = SimpleCondition.create(o -> o.intField, Negate.YES, Comparison.EQUAL, EXCLUDED_VAL);
//		EnforceParams<SimpleObject> enforce = EnforceParams.create(neq5, 0, 0);
//		EliminateParams elimParams = EliminateParams.create(2);
//		
//		List<SimpleObject> list = CommonRandomizerTestUtils.createSimpleObjects(LIST_SIZE);
//		Randomizer<SimpleObject, Integer> test = RandomizerEliminate.create((o, v) -> o.intField = v, poolBase, enforce, elimParams);
//		test.setRandom(rand);
//
//		// Perform test and check results
//		assertFalse(test.perform(list.stream()));
//		List<Integer> results = SimpleObjectUtils.toIntFieldList(list);
//		assertIterableEquals(EXPECTED_VALS, results);
//	}
//	
//	@Test
//	void perform_poolsExhaust_enforce_resets() 
//	{
//		final int EXCLUDED_VAL = 5;
//		final int LIST_SIZE = 10;
//		final List<Integer> POOL_1_VALS =     Arrays.asList(0, 1, 2, 3, 4, EXCLUDED_VAL,    6, 7, 8, 9,
//				10, 11, 12, 13, EXCLUDED_VAL, 15, 16, 17, EXCLUDED_VAL, 19);
//		final List<Integer> POOL_2_VALS =     Arrays.asList(			   EXCLUDED_VAL                ,
//				                14,                       18);
//		final List<Integer> EXPECTED_VALS = Arrays.asList(
//				10, 11, 12, 13, 14,           15, 16, 17, 18,           19);
//		
//		// Setup mocks
//		Random rand = mock(Random.class);
//		when(rand.nextInt(anyInt())).thenReturn(0);
//		
//		@SuppressWarnings("unchecked")
//		PeekPool<Integer> poolBase = mock(PeekPool.class);
//		@SuppressWarnings("unchecked")
//		PeekPool<Integer> pool1 = mock(PeekPool.class);
//		@SuppressWarnings("unchecked")
//		PeekPool<Integer> pool2 = mock(PeekPool.class);
//		
//		when(poolBase.copy()).thenReturn(pool1);
//		when(pool1.peek(any())).thenAnswer(AdditionalAnswers.returnsElementsOf(POOL_1_VALS));
//		when(pool1.copy()).thenReturn(pool2);
//		when(pool2.peek(any())).thenAnswer(AdditionalAnswers.returnsElementsOf(POOL_2_VALS));
//		when(pool2.copy()).thenReturn(null);
//
//		// Create test data and object
//		Condition<SimpleObject> neq5 = SimpleCondition.create(o -> o.intField, Negate.YES, Comparison.EQUAL, EXCLUDED_VAL);
//		EnforceParams<SimpleObject> enforce = EnforceParams.create(neq5, 0, 1);
//		EliminateParams elimParams = EliminateParams.create(2);
//		
//		List<SimpleObject> list = CommonRandomizerTestUtils.createSimpleObjects(LIST_SIZE);
//		Randomizer<SimpleObject, Integer> test = RandomizerEliminate.create((o, v) -> o.intField = v, poolBase, enforce, elimParams);
//		test.setRandom(rand);
//
//		// Perform test and check results
//		assertTrue(test.perform(list.stream()));
//		List<Integer> results = SimpleObjectUtils.toIntFieldList(list);
//		assertIterableEquals(EXPECTED_VALS, results);
//	}
//	
//	@Test
//	void perform_poolLocation_edges() 
//	{
//		RandomizerEliminate<SimpleObject, Integer> nullPool = RandomizerEliminate.createPoolFromStreamNoEnforce(
//				(o, v) -> o.intField = v,
//				 o -> o.intField);
//
//		// Make sure it doesn't explode
//		nullPool.selectPeeked();
//		assertNull(nullPool.peekNext(null));
//		
//		// Move past the valid pool (there is only one)
//		assertFalse(nullPool.nextPool());
//		
//
//		@SuppressWarnings("unchecked")
//		PeekPool<Integer> pool = mock(PeekPool.class);
//		when(pool.peek(any())).thenReturn(5);
//		when(pool.copy()).thenReturn(pool);
//
//		RandomizerEliminate<SimpleObject, Integer> nonNullPool = RandomizerEliminate.createWithPoolNoEnforce((o, v) -> o.intField = v, pool);
//
//		// Make sure it doesn't explode
//		nonNullPool.selectPeeked();
//		assertNull(nonNullPool.peekNext(null));
//		
//		// Move past the valid pool (there is only one)
//		assertTrue(nonNullPool.nextPool());
//		assertFalse(nonNullPool.nextPool());
//		
//		// Now try again
//		nonNullPool.selectPeeked();
//		assertNull(nonNullPool.peekNext(null));
//	}
}
