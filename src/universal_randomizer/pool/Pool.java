package universal_randomizer.pool;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import universal_randomizer.Sumable;
import universal_randomizer.Utils;

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
		List<N> vals = new LinkedList<>();
		N nextVal = min;
		while (nextVal.compareTo(max) <= 0)
		{
			vals.add(nextVal);
			nextVal = nextVal.sum(min, stepSize);
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
