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
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;

import Support.SimpleObject;
import universal_randomizer.Utils;

class RandomizeSingleStreamTests {

	private List<SimpleObject> createSoList(int... vals)
	{
		List<SimpleObject> list = new LinkedList<>();
		for (int val : vals)
		{
			list.add(new SimpleObject("obj" + val, val));
		}
		return list;
	}
	
	@Test
	void create() 
	{
		List<Integer> list = Arrays.asList(1,2,3,4,5);
		
		RandomizeSingleStream<Integer> rss = RandomizeSingleStream.create(list);
		assertIterableEquals(list, rss.toStream().toList());
		RandomizeStream<Integer> rs = RandomizeStream.create(list);
		assertIterableEquals(list, rs.toStream().toList());

		rss = RandomizeSingleStream.create(list.stream());
		assertIterableEquals(list, rss.toStream().toList());
		rs = RandomizeStream.create(list.stream());
		assertIterableEquals(list, rs.toStream().toList());
		
		Integer[] array = new Integer[5];
		rss = RandomizeSingleStream.create(list.toArray(array));
		assertIterableEquals(list, rss.toStream().toList());
		rs = RandomizeStream.create(list.toArray(array));
		assertIterableEquals(list, rs.toStream().toList());
	}
	
	@Test
	void forceNonNull() 
	{
		List<Integer> nullList = null;
		assertNull(RandomizeSingleStream.create(nullList));
		
		Stream<Integer> nullStream = null;
		assertNull(RandomizeSingleStream.create(nullStream));
		
		SimpleObject[] nullArray = null;
		assertNull(RandomizeSingleStream.create(nullArray));

		List<Integer> list = Arrays.asList(1,2,3,4,5);
		RandomizeSingleStream<Integer> rss1 = RandomizeSingleStream.create(list);
		rss1.setStream(null);
		assertIterableEquals(list, rss1.toStream().toList());
		
		List<Integer> list2 = Arrays.asList(3,6,7);
		rss1.setStream(list2.stream());
		assertIterableEquals(list2, rss1.toStream().toList());
	}
	
	@Test
	void duplicate() 
	{
		List<Integer> list = Arrays.asList(1,2,3,4,5);
		
		RandomizeSingleStream<Integer> rss = RandomizeSingleStream.create(list);
		RandomizeSingleStream<Integer> rssCopy = rss.duplicate().select(o -> o == 2 || o == 3);
		rss = rss.select(o -> o == 3 || o == 4);
		
		assertIterableEquals(Arrays.asList(3,4), rss.toStream().toList());
		assertIterableEquals(Arrays.asList(2,3), rssCopy.toStream().toList());

		rss = RandomizeSingleStream.create(list).select(o -> o == 2 || o == 3);
		rssCopy = rss.duplicate();
		
		assertIterableEquals(Arrays.asList(2,3), rss.toStream().toList());
		assertIterableEquals(Arrays.asList(2,3), rssCopy.toStream().toList());
		
		// Multi duplicates
		rss = RandomizeSingleStream.create(list).select(o -> o != 1);
		List<RandomizeStream<Integer>> rssCopies = rss.duplicate(2);
		rssCopies.get(0).select(o -> o == 2 || o == 3);
		assertIterableEquals(Arrays.asList(2,3,4,5), rss.toStream().toList());
		assertIterableEquals(Arrays.asList(2,3), rssCopies.get(0).toStream().toList());
		assertIterableEquals(Arrays.asList(2,3,4,5), rssCopies.get(1).toStream().toList());
	}
	
	@Test
	void select() 
	{
		List<Integer> list = Arrays.asList(0,1,2,3,4,5,6,7,8,9,10);
		List<Integer> expected = Arrays.asList(3,4);
		
		RandomizeSingleStream<Integer> rss = RandomizeSingleStream.create(list).select(o -> o > 2 && o < 5);
		assertIterableEquals(expected, rss.toStream().toList());

		assertNull(RandomizeSingleStream.create(list).select(null));
	}
	
	@Test
	void group() 
	{
		List<SimpleObject> list = createSoList(3,1,2,3,2,3);
		List<SimpleObject> expected = createSoList(1,2,2,3,3,3);
		
		RandomizeMultiStream<SimpleObject> result = RandomizeSingleStream.create(list).group(o -> o.getIntField());
		List<SimpleObject> resultList = result.toStream().toList();
		for (int i = 0; i < expected.size(); i++)
		{
			assertEquals(expected.get(i).getIntField(), resultList.get(i).getIntField());
		}

		assertNull(RandomizeSingleStream.create(list).group(null));
	}

	@Test
	void shuffle() 
	{
		Random rand = mock(Random.class);
		when(rand.nextLong()).thenReturn(3L, 2L, 1L, 0L);
		
		// pass rand, null, and seed
		List<Integer> list = Arrays.asList(7,2,-3, 5);
		List<Integer> expected = new LinkedList<>(list);
		Collections.reverse(expected);
		
		RandomizeSingleStream<Integer> rss = RandomizeSingleStream.create(list).shuffle(rand);
		assertIterableEquals(expected, rss.toStream().toList());
		
		try (MockedConstruction<Random> mocked = mockConstruction(Random.class,
				(mock, context) -> {
					assertEquals(0, context.arguments().size());
					when(mock.nextLong()).thenReturn(3L, 2L, 1L, 0L);
				}))
		{
			rss = RandomizeSingleStream.create(list).shuffle();
			assertIterableEquals(expected, rss.toStream().toList());
			
			rss = RandomizeSingleStream.create(list).shuffle(null);
			assertIterableEquals(expected, rss.toStream().toList());
			
			assertEquals(2, mocked.constructed().size());
		}
		
		try (MockedConstruction<Random> mocked = mockConstruction(Random.class,
				(mock, context) -> {
					assertEquals(1, context.arguments().size());
					assertEquals(42L, context.arguments().get(0));
					when(mock.nextLong()).thenReturn(3L, 2L, 1L, 0L);
				}))
		{
			rss = RandomizeSingleStream.create(list).shuffle(42);
			assertIterableEquals(expected, rss.toStream().toList());
			assertEquals(1, mocked.constructed().size());
		}
	}
	
	@Test
	void sort() 
	{
		List<Integer> list = Arrays.asList(7,2,-3, 5);
		List<Integer> expected = Arrays.asList(-3,2,5,7);
		List<Integer> expectedReverse = new LinkedList<>(expected);
		Collections.reverse(expectedReverse);
		
		RandomizeSingleStream<Integer> rss = RandomizeSingleStream.create(list).sort();
		assertIterableEquals(expected, rss.toStream().toList());
		rss = RandomizeSingleStream.create(list).sort(null);
		assertIterableEquals(expected, rss.toStream().toList());
		
		// custom inverse sort
		RandomizeSingleStream<Integer> rss2 = RandomizeSingleStream.create(list).sort((lhs, rhs) -> rhs.compareTo(lhs));
		assertIterableEquals(expectedReverse, rss2.toStream().toList());
	}

	@Test
	void forEach()
	{
		
	}

	@Test
	void map()
	{
		
	}

	@Test
	void convertToField()
	{
		List<SimpleObject> list = createSoList(0,1,2,3,4);
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
		List<Integer> expectedIntField = Arrays.asList(0,1,2,3,4);
		List<Integer> expectedIntMultiple = Arrays.asList(0,0,1,0,1,2,0,1,2,3);
		List<String> expectedStringMultiple = Arrays.asList("0","0","10","0","10","20","0","10","20","30");

		RandomizeSingleStream<Integer> resultInts = RandomizeSingleStream.create(list).convertToField(o -> o.getIntField());
		assertIterableEquals(expectedIntField, resultInts.toStream().toList());
		
		resultInts = RandomizeSingleStream.create(list).convertToFieldCollection(o -> o.list);
		assertIterableEquals(expectedIntMultiple, resultInts.toStream().toList());

		resultInts = RandomizeSingleStream.create(list).convertToFieldArray(o -> o.wrappedArray);
		assertIterableEquals(expectedIntMultiple, resultInts.toStream().toList());
		
		// Primitive arrays have to be converted to something else first (i.e. a stream)
		resultInts = RandomizeSingleStream.create(list).convertToFieldStream(o -> Utils.convertPrimitiveArrayToStream(o.array));
		assertIterableEquals(expectedIntMultiple, resultInts.toStream().toList());
		
		resultInts = RandomizeSingleStream.create(list).convertToFieldStream(o -> o.list.stream());
		assertIterableEquals(expectedIntMultiple, resultInts.toStream().toList());

		resultInts = RandomizeSingleStream.create(list).convertToFieldMapKeys(o -> o.map);
		assertIterableEquals(expectedIntMultiple, resultInts.toStream().toList());
		
		RandomizeSingleStream<String> resultString = RandomizeSingleStream.create(list).convertToFieldMapValues(o -> o.map);
		assertIterableEquals(expectedStringMultiple, resultString.toStream().toList());

		assertNull(RandomizeSingleStream.create(list).convertToField(null));
		assertNull(RandomizeSingleStream.create(list).convertToFieldArray(null));
		assertNull(RandomizeSingleStream.create(list).convertToFieldCollection(null));
		assertNull(RandomizeSingleStream.create(list).convertToFieldStream(null));
		assertNull(RandomizeSingleStream.create(list).convertToFieldMapKeys(null));
		assertNull(RandomizeSingleStream.create(list).convertToFieldMapValues(null));
	}
}
