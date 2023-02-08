package universal_randomizer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import universal_randomizer.user_object_apis.Sum;
import universal_randomizer.user_object_apis.Sumable;
import universal_randomizer.wrappers.ComparableAsComparator;
import universal_randomizer.wrappers.ReflectionObject;
import universal_randomizer.wrappers.SumableAsSum;

public class Pool<T>
{	
	// TODO: Return using function?
	// TODO: Create new class for dependent Pools - i.e. if passed x, use set y
	// TODO: Allow creation by reference, shallow, deep and reflection copy?
	// TODO: Make into more specific subclasses, make Pool an interface/abstract class?
	
	protected HashMap<Integer, T> data;
	protected List<Integer> activeKeys;
	protected boolean keysInSync;
	
	protected Pool(Collection<T> valCollection)
	{
		data = new HashMap<>();
		int key = 0;
		for (T val : valCollection)
		{
			data.put(key++, Utils.deepCopy(val));
		}
		refreshKeyTracking();
	}

	protected Pool(Map<Integer, T> map)
	{
		data.putAll(map);
		refreshKeyTracking();
	}
	
	protected void refreshKeyTracking()
	{
		activeKeys = new ArrayList<>(data.keySet());
		keysInSync = true;
	}
	
	protected void refreshKeyTrackingIfNeeded()
	{
		if (!keysInSync)
		{
			refreshKeyTracking();
		}
	}
	
	protected void invalidateKeyTracking()
	{
		keysInSync = false;
	}
	
	public static <V> Pool<V> createEmpty()
	{
		return new Pool<>(new ArrayList<>());
	}
	
	public static <V> Pool<V> createCopy(Pool<V> toCopy)
	{
		return new Pool<>(toCopy.data);
	}
	
	// TODO: Create weighted from array
	
	@SafeVarargs
	public static <V> Pool<V> createUniformFromArray(V... values)
	{
		return createFromArray(true, values);
	}
	
	@SafeVarargs
	public static <V> Pool<V> createFromArray(V... values)
	{
		return createFromArray(false, values);
	}
	
	@SafeVarargs
	public static <V> Pool<V> createFromArray(boolean removeDuplicates, V... values)
	{
		if (removeDuplicates)
		{
			Set<V> asSet = new HashSet<>();
			Collections.addAll(asSet, values);
			return new Pool<>(asSet);
		}
		else
		{
			return new Pool<>(Arrays.asList(values));
		}
	}
	
	public static <V> Pool<V> createUniformFromList(List<V> values)
	{
		return createFromList(true, values);
	}
	
	public static <V> Pool<V> createFromList(List<V> values)
	{
		return createFromList(false, values);
	}
	
	public static <V> Pool<V> createFromList(boolean removeDuplicates, List<V> values)
	{
		if (removeDuplicates)
		{
			return new Pool<>(new HashSet<>(values));
		}
		else
		{
			return new Pool<>(values);
		}
	}
	
	public static <S, V> Pool<S> createUniformFromStream(String pathToField, Stream<ReflectionObject<V>> objStream)
	{
		return createFromStream(true, pathToField, objStream);
	}
	
	public static <S, V> Pool<S> createFromStream(String pathToField, Stream<ReflectionObject<V>> objStream)
	{
		return createFromStream(false, pathToField, objStream);
	}
	
	public static <S, V> Pool<S> createFromStream(boolean removeDuplicates, String pathToField, Stream<ReflectionObject<V>> objStream)
	{
		Stream<S> narrowed = objStream.flatMap(obj -> obj.getFieldStream(pathToField));
		if (removeDuplicates)
		{
			return new Pool<>(narrowed.collect(Collectors.toSet()));
		}
		return new Pool<>(narrowed.collect(Collectors.toList()));
	}

	public static <S, V> Pool<S> createUniformFromMapValuesStream(String pathToField, Stream<ReflectionObject<V>> objStream)
	{
		return createFromMapValuesStream(true, pathToField, objStream);
	}

	public static <S, V> Pool<S> createFromMapValuesStream(String pathToField, Stream<ReflectionObject<V>> objStream)
	{
		return createFromMapValuesStream(false, pathToField, objStream);
	}
	
	public static <S, V> Pool<S> createFromMapValuesStream(boolean removeDuplicates, String pathToField, Stream<ReflectionObject<V>> objStream)
	{
		return createFromMapStream(removeDuplicates, pathToField, objStream, true);
	}

	public static <S, V> Pool<S> createFromMapKeysStream(String pathToField, Stream<ReflectionObject<V>> objStream)
	{
		// Keys are already a set by definition
		return createFromMapStream(false, pathToField, objStream, false);
	}
	
	public static <S, V> Pool<S> createFromMapStream(
			boolean removeDuplicates, String pathToField, Stream<ReflectionObject<V>> objStream, boolean valuesNotKeys)
	{
		Stream<S> narrowed = objStream.flatMap(obj -> obj.getMapFieldStream(pathToField, valuesNotKeys));
		if (removeDuplicates)
		{
			return new Pool<>(narrowed.collect(Collectors.toSet()));
		}
		return new Pool<>(narrowed.collect(Collectors.toList()));
	}
	
	public static <N extends Comparable<N> & Sumable<N>> Pool<N> createRange(N min, N max, N stepSize)
	{
		return createRange(min, max, stepSize, new SumableAsSum<>());
	}
	
	public static <N extends Comparable<N>> Pool<N> createRange(N min, N max, N stepSize, Sum<N> sumFn)
	{
		return createRange(min, max, stepSize, new ComparableAsComparator<>(), sumFn);
	}
	
	public static <N extends Sumable<N>> Pool<N> createRange(N min, N max, N stepSize, Comparator<N> comparator)
	{
		return createRange(min, max, stepSize, comparator, new SumableAsSum<>());
	}
	
	public static <N> Pool<N> createRange(N min, N max, N stepSize, Comparator<N> comparator, Sum<N> sumtor)
	{
		List<N> vals = new LinkedList<>();
		N nextVal = min;
		while (comparator.compare(nextVal, max) <= 0)
		{
			vals.add(nextVal);
			nextVal = sumtor.sum(nextVal, stepSize);
		}
		return new Pool<>(vals);
	}
	
	public int getRandomKey(Random rand)
	{
		return getRandomKey(rand, null);
	}
	
	public int getRandomKey(Random rand, Set<Integer> excludedKeys)
	{
		if (isEmpty())
		{
			return -1;
		}
		else if (excludedKeys != null && !excludedKeys.isEmpty())
		{
			// Determine how many valid keys are left
			if (activeKeys.size() - excludedKeys.size() <= 0)
			{
				return -1;
			}

			// Make a temp list of list of 
			int tempIndex = 0;
			boolean found = false;
			List<Integer> workingKeys = new ArrayList<>(data.keySet());
			
			while (!found)
			{
				//try a new index
				tempIndex = rand.nextInt(workingKeys.size());
				if (!excludedKeys.contains(workingKeys.get(tempIndex)))
				{
					found = true;
				}
				else
				{
					workingKeys.remove(tempIndex);
				}
			} 
			return workingKeys.get(tempIndex);
		}
		else
		{
			refreshKeyTrackingIfNeeded();
			return activeKeys.get(rand.nextInt(activeKeys.size()));
		}
	}

	public T popRandom(Random rand)
	{
		return getRandom(rand, true);
	}
	
	
	public T getRandom(Random rand)
	{
		return getRandom(rand, false);
	}
	
	
	public T getRandom(Random rand, boolean remove)
	{
		if (!data.isEmpty())
		{
			if (remove)
			{
				invalidateKeyTracking();
				return data.remove(rand.nextInt(data.size()));
			}
			return data.get(rand.nextInt(data.size()));
		}
		
		return null;
	}
	
	
	public T pop(int key)
	{
		return get(key, true);
	}
	
	public T get(int key)
	{
		return get(key, false);
	}
	
	public T get(int key, boolean remove)
	{
		if (data.containsKey(key))
		{
			if (remove)
			{
				invalidateKeyTracking();
				return data.remove(key);
			}
			return data.get(key);
		}
		return null;
	}
	
	public void remove(int key)
	{
		pop(key);
	}
	
	private Stream<Entry<Integer, T>> baseKeyOfStream(T val)
	{
		return data.entrySet().stream().filter(obj -> obj.getValue().equals(val));
	}
	
	public int keyOf(T val)
	{
		Optional<Entry<Integer, T>> entry = baseKeyOfStream(val).findFirst();
		if (entry.isEmpty())
		{
			return -1;
		}
		return entry.get().getKey();
	}
	
	public Set<Integer> allKeysOf(T val)
	{
		return baseKeyOfStream(val).map(obj -> obj.getKey()).collect(Collectors.toSet());
	}
	
	public int instancesOf(T val)
	{
		return (int) baseKeyOfStream(val).count();
	}
	
	
	public int size()
	{
		return data.size();
	}
	
	
	public boolean isEmpty()
	{
		return data.isEmpty();
	}


	public boolean isValidKey(int key)
	{
		return data.containsKey(key);
	}
}