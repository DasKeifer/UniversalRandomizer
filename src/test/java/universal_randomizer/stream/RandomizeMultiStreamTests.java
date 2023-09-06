package universal_randomizer.stream;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;

import Support.SimpleObject;
import Support.SumableComparableObject;
import universal_randomizer.Utils;

class RandomizeMultiStreamTests {

	private List<SimpleObject> createSoList(int... vals)
	{
		List<SimpleObject> list = new LinkedList<>();
		for (int val : vals)
		{
			list.add(new SimpleObject("obj" + val, val));
		}
		return list;
	}
	
	private List<SimpleObject> createSoList(List<Integer> intVals, List<String> stringVals)
	{
		List<SimpleObject> list = new LinkedList<>();
		for (int i = 0; i < intVals.size(); i++)
		{
			list.add(new SimpleObject(stringVals.get(i), intVals.get(i)));
		}
		return list;
	}
	
	private List<SumableComparableObject> createScoList(int[] intVals, int[] secondVals)
	{
		List<SumableComparableObject> list = new LinkedList<>();
		for (int i = 0; i < intVals.length; i++)
		{
			SumableComparableObject sco = new SumableComparableObject(intVals[i]);
			sco.compareSecNullReturn = secondVals[i];
			list.add(sco);
		}
		return list;
	}
	
	private <T> void assertIdxIterableEquals(List<T> expected, List<Integer> expectedIdx, RandomizeMultiStream<T> result)
	{
		List<T> resultList = result.toStream().toList();
		for (int i = 0; i < expectedIdx.size(); i++)
		{
			assertEquals(expected.get(expectedIdx.get(i)), resultList.get(i));
		}
	}
	
	@Test
	void select() 
	{
		List<SimpleObject> list = createSoList(3,1,2,3,2,3,4,5);
		List<Integer> expected = Arrays.asList(2,2,3,3,3);
		
		RandomizeMultiStream<SimpleObject> rms = RandomizeSingleStream.create(list).group(o -> o.getIntField());
		assertNull(rms.select(null));
		
		RandomizeMultiStream<SimpleObject> result = rms.select(o -> o.intField == 2 || o.intField == 3);
		List<SimpleObject> resultList = result.toStream().toList();
		for (int i = 0; i < expected.size(); i++)
		{
			assertEquals(expected.get(i), resultList.get(i).getIntField());
		}
	}
	
	@Test
	void group() 
	{
		List<SimpleObject> list = createSoList(List.of( 3,  1,  2,  3,  2,  3,  4,  5),
											   List.of("1","1","2","2","1","1","1","1"));
		List<Integer> expectedIdx = List.of(1,4,2,0,5,3,6,7);
		
		RandomizeMultiStream<SimpleObject> rms = RandomizeSingleStream.create(list).group(o -> o.getIntField());
		assertNull(rms.group(null));
		
		RandomizeMultiStream<SimpleObject> result = rms.group(o -> o.getStringField());
		assertIdxIterableEquals(list, expectedIdx, result);
	}

	@Test
	void shuffle() 
	{
		List<SimpleObject> list = createSoList(List.of( 3,  1,  2,  3,  2,  3),
						                       List.of("0","1","2","3","4","5"));
		List<Integer> expectedIdx = List.of(1,4,2,5,3,0);
		
		Random rand = mock(Random.class);
		when(rand.nextLong()).thenReturn(5L, 4L, 3L, 2L, 1L, 0L);

		RandomizeMultiStream<SimpleObject> rms = RandomizeSingleStream.create(list).group(o -> o.getIntField());
		RandomizeMultiStream<SimpleObject> result = rms.shuffle(rand);
		assertIdxIterableEquals(list, expectedIdx, result);
		
		try (MockedConstruction<Random> mocked = mockConstruction(Random.class,
				(mock, context) -> {
					assertEquals(0, context.arguments().size());
					when(mock.nextLong()).thenReturn(5L, 4L, 3L, 2L, 1L, 0L);
				}))
		{
			rms = RandomizeSingleStream.create(list).group(o -> o.getIntField());
			result = rms.shuffle();
			assertIdxIterableEquals(list, expectedIdx, result);
			
			rms = RandomizeSingleStream.create(list).group(o -> o.getIntField());
			result = rms.shuffle(null);
			assertIdxIterableEquals(list, expectedIdx, result);
			
			assertEquals(6, mocked.constructed().size());
		}
		
		try (MockedConstruction<Random> mocked = mockConstruction(Random.class,
				(mock, context) -> {
					assertEquals(1, context.arguments().size());
					assertEquals(42L, context.arguments().get(0));
					when(mock.nextLong()).thenReturn(5L, 4L, 3L, 2L, 1L, 0L);
				}))
		{
			rms = RandomizeSingleStream.create(list).group(o -> o.getIntField());
			result = rms.shuffle(42);
			assertIdxIterableEquals(list, expectedIdx, result);
			
			assertEquals(1, mocked.constructed().size());
		}
	}
	
	@Test
	void sort() 
	{
		int[] secondVals = {1,2,2,3,3,3};
		int[] intVals =    {1,1,2,2,1,3};
		List<SumableComparableObject> list = createScoList(intVals, secondVals);
		
		List<Integer> expectedIdx =        List.of(0,1,2,4,3,5);
		List<Integer> expectedIdxReverse = List.of(0,2,1,5,3,4);
		
		RandomizeMultiStream<SumableComparableObject> rms = RandomizeSingleStream.create(list).group(o -> o.compareSecNullReturn);
		RandomizeMultiStream<SumableComparableObject> result = rms.sort();
		assertIdxIterableEquals(list, expectedIdx, result);

		rms = RandomizeSingleStream.create(list).group(o -> o.compareSecNullReturn);
		result = rms.sort(null);
		assertIdxIterableEquals(list, expectedIdx, result);

		rms = RandomizeSingleStream.create(list).group(o -> o.compareSecNullReturn);
		result = rms.sort((lhs, rhs) -> rhs.compareTo(lhs));
		assertIdxIterableEquals(list, expectedIdxReverse, result);
	}

	@Test
	void convertToField()
	{
		List<SimpleObject> list = createSoList(0,0,1,2,2);
		for (int i = 0; i < list.size(); i++)
		{
			list.get(i).array = new int[i];
			list.get(i).wrappedArray = new Integer[i];
			for (int j = 0; j < i; j++)
			{
				list.get(i).array[j] = j;
				list.get(i).wrappedArray[j] = j;
				list.get(i).list.add(j);
				list.get(i).map.put(j, "" + j*10);
			}
		}
		List<Integer> expectedIntField = Arrays.asList(0,0,1,2,2);
		List<Integer> expectedIntMultiple = Arrays.asList(0,0,1,0,1,2,0,1,2,3);
		List<String> expectedStringMultiple = Arrays.asList("0","0","10","0","10","20","0","10","20","30");

		RandomizeMultiStream<SimpleObject> rms = RandomizeSingleStream.create(list).group(o -> o.getIntField());
		RandomizeMultiStream<Integer> resultInts = rms.convertToField(o -> o.getIntField());
		assertIterableEquals(expectedIntField, resultInts.toStream().toList());
		
		rms = RandomizeSingleStream.create(list).group(o -> o.getIntField());
		resultInts = rms.convertToFieldCollection(o -> o.list);
		assertIterableEquals(expectedIntMultiple, resultInts.toStream().toList());

		rms = RandomizeSingleStream.create(list).group(o -> o.getIntField());
		resultInts = rms.convertToFieldArray(o -> o.wrappedArray);
		assertIterableEquals(expectedIntMultiple, resultInts.toStream().toList());
		
		// Primitive arrays have to be converted to something else first (i.e. a stream)
		rms = RandomizeSingleStream.create(list).group(o -> o.getIntField());
		resultInts = rms.convertToFieldStream(o -> Utils.convertPrimitiveArrayToStream(o.array));
		assertIterableEquals(expectedIntMultiple, resultInts.toStream().toList());
		
		rms = RandomizeSingleStream.create(list).group(o -> o.getIntField());
		resultInts = rms.convertToFieldStream(o -> o.list.stream());
		assertIterableEquals(expectedIntMultiple, resultInts.toStream().toList());

		rms = RandomizeSingleStream.create(list).group(o -> o.getIntField());
		resultInts = rms.convertToFieldMapKeys(o -> o.map);
		assertIterableEquals(expectedIntMultiple, resultInts.toStream().toList());
		
		rms = RandomizeSingleStream.create(list).group(o -> o.getIntField());
		RandomizeMultiStream<String> resultString = rms.convertToFieldMapValues(o -> o.map);
		assertIterableEquals(expectedStringMultiple, resultString.toStream().toList());

		assertNull(RandomizeSingleStream.create(list).group(o -> o.getIntField()).convertToField(null));
		assertNull(RandomizeSingleStream.create(list).group(o -> o.getIntField()).convertToFieldArray(null));
		assertNull(RandomizeSingleStream.create(list).group(o -> o.getIntField()).convertToFieldCollection(null));
		assertNull(RandomizeSingleStream.create(list).group(o -> o.getIntField()).convertToFieldStream(null));
		assertNull(RandomizeSingleStream.create(list).group(o -> o.getIntField()).convertToFieldMapKeys(null));
		assertNull(RandomizeSingleStream.create(list).group(o -> o.getIntField()).convertToFieldMapValues(null));
	}
}
