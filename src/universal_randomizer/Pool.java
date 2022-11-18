package universal_randomizer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.SortedSet;
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
	
	List<T> values;
	
	protected Pool(Collection<T> valCollection)
	{
		values = new ArrayList<>();
		for (T val : valCollection)
		{
			values.add(Utils.deepCopy(val));
		}
	}
	
	public static <V> Pool<V> createEmpty()
	{
		return new Pool<>(new ArrayList<>());
	}
	
	public static <V> Pool<V> createCopy(Pool<V> toCopy)
	{
		return new Pool<>(toCopy.values);
	}
	
	// TODO: Create weighted from array
	
	@SafeVarargs
	public static <V> Pool<V> createUniformFromArray(boolean removeDuplicates, V... values)
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
	
	public int getRandomIndex(Random rand)
	{
		return rand.nextInt(values.size());
	}
	
	public int getRandomIndex(Random rand, SortedSet<Integer> excluded)
	{
		if (excluded != null)
		{
			int adjustedSize = values.size() - excluded.size();
			if (adjustedSize <= 0)
			{
				return -1;
			}
			int tempIndex = rand.nextInt(adjustedSize);
			for (Integer exclIndex : excluded)
			{
				if (tempIndex < exclIndex)
				{
					break;
				}
				tempIndex++;
			}
			return tempIndex;
		}
		return getRandomIndex(rand);
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
		if (!values.isEmpty())
		{
			if (remove)
			{
				return values.remove(rand.nextInt(values.size()));
			}
			return values.get(rand.nextInt(values.size()));
		}
		
		return null;
	}
	
	public T pop(int i)
	{
		return get(i, true);
	}
	
	public T get(int i)
	{
		return get(i, false);
	}
	
	public T get(int i, boolean remove)
	{
		if (i < values.size())
		{
			if (remove)
			{
				return values.remove(i);
			}
			return values.get(i);
		}
		return null;
	}
	
	public void remove(int i)
	{
		pop(i);
	}
	
	public int size()
	{
		return values.size();
	}
	
	public boolean isEmpty()
	{
		return values.isEmpty();
	}
}