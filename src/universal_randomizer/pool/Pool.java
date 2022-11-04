package universal_randomizer.pool;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import universal_randomizer.Utils;
import universal_randomizer.interfaces.Sum;
import universal_randomizer.interfaces.Sumable;
import universal_randomizer.wrappers.ComparableAsComparator;
import universal_randomizer.wrappers.SumableAsSum;

public class Pool<T>
{	
	Map<Integer, T> values;
	
	protected Pool(Collection<T> valCollection)
	{
		values = new HashMap<>();
		int index = 0;
		for (T val : valCollection)
		{
			values.put(index++, Utils.deepCopy(val));
		}
	}
	
	// TODO: Look into this annotation
	@SafeVarargs
	public static <M> Pool<M> createFromArray(M... values)
	{
		return new Pool<>(Arrays.asList(values));
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
	
	public T getRandomValue(Random rand)
	{
		if (!values.isEmpty())
		{
			return values.get(rand.nextInt(values.size()));
		}
		
		return null;
	}
}
