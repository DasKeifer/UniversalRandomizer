package universal_randomizer;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;

import Support.SimpleObject;
import universal_randomizer.user_object_apis.Getter;

class StreamUtilsTests {

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
	void group() 
	{
		List<SimpleObject> list = createSoList(3,1,2,3,2,3);
		Getter<SimpleObject, Integer> getter = o -> o.getIntField();
		
		Map<Integer, List<SimpleObject>> result = StreamUtils.group(list.stream(), getter);
		for (Entry<Integer, List<SimpleObject>> entry : result.entrySet())
		{
			assertEquals(entry.getKey(), entry.getValue().size());
			for (SimpleObject so : entry.getValue())
			{
				assertEquals(entry.getKey(), so.getIntField());
			}
		}

		assertNull(StreamUtils.group(null, getter));
		assertNull(StreamUtils.group(list.stream(), null));
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
		
		Stream<Integer> rss = StreamUtils.shuffle(list.stream(), rand);
		assertIterableEquals(expected, rss.toList());
		
		try (MockedConstruction<Random> mocked = mockConstruction(Random.class,
				(mock, context) -> {
					assertEquals(0, context.arguments().size());
					when(mock.nextLong()).thenReturn(3L, 2L, 1L, 0L);
				}))
		{
			rss = StreamUtils.shuffle(list.stream());
			assertIterableEquals(expected, rss.toList());
			
			rss = StreamUtils.shuffle(list.stream(), null);
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
			rss = StreamUtils.shuffle(list.stream(), 42);
			assertIterableEquals(expected, rss.toList());
			assertEquals(1, mocked.constructed().size());
		}
	}

	@Test
	void field()
	{
		List<SimpleObject> list = new LinkedList<>();
		list.add(new SimpleObject("obj0", 0));
		list.add(new SimpleObject("obj1", 1));
		list.add(new SimpleObject("obj2", 2));
		list.add(new SimpleObject("obj3", 3));
		list.add(new SimpleObject("obj4", 4));
		
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

		Stream<Integer> resultInts = StreamUtils.field(list.stream(), o -> o.getIntField());
		assertIterableEquals(expectedIntField, resultInts.toList());
		
		resultInts = StreamUtils.fieldCollection(list.stream(), o -> o.list);
		assertIterableEquals(expectedIntMultiple, resultInts.toList());

		resultInts = StreamUtils.fieldArray(list.stream(), o -> o.wrappedArray);
		assertIterableEquals(expectedIntMultiple, resultInts.toList());
		
		// Primitive arrays have to be converted to something else first (i.e. a stream)
		resultInts = StreamUtils.fieldStream(list.stream(), o -> ConversionUtils.convertPrimitiveArrayToStream(o.array));
		assertIterableEquals(expectedIntMultiple, resultInts.toList());
		
		resultInts = StreamUtils.fieldStream(list.stream(), o -> o.list.stream());
		assertIterableEquals(expectedIntMultiple, resultInts.toList());

		resultInts = StreamUtils.fieldMapKeys(list.stream(), o -> o.map);
		assertIterableEquals(expectedIntMultiple, resultInts.toList());
		
		Stream<String> resultString = StreamUtils.fieldMapValues(list.stream(), o -> o.map);
		assertIterableEquals(expectedStringMultiple, resultString.toList());

		assertNull(StreamUtils.field(null, o -> o));
		assertNull(StreamUtils.fieldCollection(null, o -> List.of(o)));
		assertNull(StreamUtils.fieldArray(null, o -> new Object[] {o}));
		assertNull(StreamUtils.fieldStream(null, o -> Arrays.stream(new Object[] {o})));
		assertNull(StreamUtils.fieldStream(null, o -> Arrays.stream(new Object[] {o})));
		assertNull(StreamUtils.fieldMapKeys(null, o -> new HashMap<Object, Object>()));
		assertNull(StreamUtils.fieldMapValues(null, o -> new HashMap<Object, Object>()));

		assertNull(StreamUtils.field(list.stream(), null));
		assertNull(StreamUtils.fieldCollection(list.stream(), null));
		assertNull(StreamUtils.fieldArray(list.stream(), null));
		assertNull(StreamUtils.fieldStream(list.stream(), null));
		assertNull(StreamUtils.fieldStream(list.stream(), null));
		assertNull(StreamUtils.fieldMapKeys(list.stream(), null));
		assertNull(StreamUtils.fieldMapValues(list.stream(), null));
	}
}
