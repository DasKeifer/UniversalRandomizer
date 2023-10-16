package universal_randomizer;


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
import universal_randomizer.RandomizeStream;
import universal_randomizer.Utils;

class RandomizeStreamTests {

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
		
		RandomizeStream<Integer> rss = RandomizeStream.create(list.stream());
		assertIterableEquals(list, rss.toList());
		RandomizeStream<Integer> rs = RandomizeStream.create(list.stream());
		assertIterableEquals(list, rs.toList());

		// bad case
		assertNull(RandomizeStream.create(null));
	}
	
	@Test
	void duplicate() 
	{
		List<Integer> list = Arrays.asList(1,2,3,4,5);
		
		RandomizeStream<Integer> rss = RandomizeStream.create(list.stream());
		RandomizeStream<Integer> rssCopy = rss.duplicate().select(o -> o == 2 || o == 3);
		rss = rss.select(o -> o == 3 || o == 4);
		
		assertIterableEquals(Arrays.asList(3,4), rss.toList());
		assertIterableEquals(Arrays.asList(2,3), rssCopy.toList());

		rss = RandomizeStream.create(list).select(o -> o == 2 || o == 3);
		rssCopy = rss.duplicate();
		
		assertIterableEquals(Arrays.asList(2,3), rss.toList());
		assertIterableEquals(Arrays.asList(2,3), rssCopy.toList());
		
		// Multi duplicates
		rss = RandomizeStream.create(list).select(o -> o != 1);
		List<RandomizeStream<Integer>> rssCopies = rss.duplicate(2);
		rssCopies.get(0).select(o -> o == 2 || o == 3);
		assertIterableEquals(Arrays.asList(2,3,4,5), rss.toList());
		assertIterableEquals(Arrays.asList(2,3), rssCopies.get(0).toList());
		assertIterableEquals(Arrays.asList(2,3,4,5), rssCopies.get(1).toList());
	}
	
	@Test
	void select() 
	{
		List<Integer> list = Arrays.asList(0,1,2,3,4,5,6,7,8,9,10);
		List<Integer> expected = Arrays.asList(3,4);
		
		RandomizeStream<Integer> rss = RandomizeStream.create(list).select(o -> o > 2 && o < 5);
		assertIterableEquals(expected, rss.toList());

		assertNull(RandomizeStream.create(list).select(null));
	}
	
	@Test
	void group() 
	{
		List<SimpleObject> list = createSoList(3,1,2,3,2,3);
		List<SimpleObject> expected = createSoList(1,2,2,3,3,3);
		
		RandomizeMultiStream<SimpleObject> result = RandomizeStream.create(list).group(o -> o.getIntField());
		List<SimpleObject> resultList = result.toList();
		for (int i = 0; i < expected.size(); i++)
		{
			assertEquals(expected.get(i).getIntField(), resultList.get(i).getIntField());
		}

		assertNull(RandomizeStream.create(list).group(null));
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
		
		RandomizeStream<Integer> rss = RandomizeStream.create(list).shuffle(rand);
		assertIterableEquals(expected, rss.toList());
		
		try (MockedConstruction<Random> mocked = mockConstruction(Random.class,
				(mock, context) -> {
					assertEquals(0, context.arguments().size());
					when(mock.nextLong()).thenReturn(3L, 2L, 1L, 0L);
				}))
		{
			rss = RandomizeStream.create(list).shuffle();
			assertIterableEquals(expected, rss.toList());
			
			rss = RandomizeStream.create(list).shuffle(null);
			assertIterableEquals(expected, rss.toList());
			
			assertEquals(2, mocked.constructed().size());
		}
		
		try (MockedConstruction<Random> mocked = mockConstruction(Random.class,
				(mock, context) -> {
					assertEquals(1, context.arguments().size());
					assertEquals(42L, context.arguments().get(0));
					when(mock.nextLong()).thenReturn(3L, 2L, 1L, 0L);
				}))
		{
			rss = RandomizeStream.create(list).shuffle(42);
			assertIterableEquals(expected, rss.toList());
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
		
		RandomizeStream<Integer> rss = RandomizeStream.create(list).sort();
		assertIterableEquals(expected, rss.toList());
		rss = RandomizeStream.create(list).sort(null);
		assertIterableEquals(expected, rss.toList());
		
		// custom inverse sort
		RandomizeStream<Integer> rss2 = RandomizeStream.create(list).sort((lhs, rhs) -> rhs.compareTo(lhs));
		assertIterableEquals(expectedReverse, rss2.toList());
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

		RandomizeStream<Integer> resultInts = RandomizeStream.create(list).convertToField(o -> o.getIntField());
		assertIterableEquals(expectedIntField, resultInts.toList());
		
		resultInts = RandomizeStream.create(list).convertToFieldCollection(o -> o.list);
		assertIterableEquals(expectedIntMultiple, resultInts.toList());

		resultInts = RandomizeStream.create(list).convertToFieldArray(o -> o.wrappedArray);
		assertIterableEquals(expectedIntMultiple, resultInts.toList());
		
		// Primitive arrays have to be converted to something else first (i.e. a stream)
		resultInts = RandomizeStream.create(list).convertToFieldStream(o -> Utils.convertPrimitiveArrayToStream(o.array));
		assertIterableEquals(expectedIntMultiple, resultInts.toList());
		
		resultInts = RandomizeStream.create(list).convertToFieldStream(o -> o.list.stream());
		assertIterableEquals(expectedIntMultiple, resultInts.toList());

		resultInts = RandomizeStream.create(list).convertToFieldMapKeys(o -> o.map);
		assertIterableEquals(expectedIntMultiple, resultInts.toList());
		
		RandomizeStream<String> resultString = RandomizeStream.create(list).convertToFieldMapValues(o -> o.map);
		assertIterableEquals(expectedStringMultiple, resultString.toList());

		assertNull(RandomizeStream.create(list).convertToField(null));
		assertNull(RandomizeStream.create(list).convertToFieldArray(null));
		assertNull(RandomizeStream.create(list).convertToFieldCollection(null));
		assertNull(RandomizeStream.create(list).convertToFieldStream(null));
		assertNull(RandomizeStream.create(list).convertToFieldMapKeys(null));
		assertNull(RandomizeStream.create(list).convertToFieldMapValues(null));
	}
}
