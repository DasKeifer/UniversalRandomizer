package universal_randomizer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import universal_randomizer.user_object_apis.Sum;
import universal_randomizer.user_object_apis.Sumable;
import universal_randomizer.wrappers.ComparableAsComparator;
import universal_randomizer.wrappers.ReflectionObject;
import universal_randomizer.wrappers.SumableAsSum;

public class Pool<T>
{	
	List<T> values;
	
	protected Pool(Collection<T> valCollection)
	{
		values = new ArrayList<>();
		for (T val : valCollection)
		{
			values.add(Utils.deepCopy(val));
		}
	}
	
	@SafeVarargs
	public static <V> Pool<V> createFromArray(V... values)
	{
		return new Pool<>(Arrays.asList(values));
	}
	
	public static <S, V> Pool<S> createFromStream(String pathToField, Stream<ReflectionObject<V>> objStream)
	{
		Stream<S> narrowed = objStream.flatMap(obj -> obj.getFieldStream(pathToField));
		return new Pool<>(narrowed.collect(Collectors.toList()));
	}

	public static <S, V> Pool<S> createFromMapValuesStream(String pathToField, Stream<ReflectionObject<V>> objStream)
	{
		return createFromMapStream(pathToField, objStream, true);
	}

	public static <S, V> Pool<S> createFromMapKeysStream(String pathToField, Stream<ReflectionObject<V>> objStream)
	{
		return createFromMapStream(pathToField, objStream, false);
	}
	
	public static <S, V> Pool<S> createFromMapStream(String pathToField, Stream<ReflectionObject<V>> objStream, boolean valuesNotKeys)
	{
		Stream<S> narrowed = objStream.flatMap(obj -> obj.getMapFieldStream(pathToField, valuesNotKeys));
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
	
	public int size()
	{
		return values.size();
	}
	
	public boolean isEmpty()
	{
		return values.isEmpty();
	}
}
