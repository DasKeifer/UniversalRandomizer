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
	
	protected ArrayList<T> unpeeked;
	protected LinkedList<T> peeked;
	protected LinkedList<T> removed;
	
	protected Pool(Collection<T> valCollection)
	{
		unpeeked = new ArrayList<>(valCollection);
		peeked = new LinkedList<>();
		removed = new LinkedList<>();
	}
	
	protected Pool(Pool<T> toCopy)
	{
		unpeeked = new ArrayList<>(toCopy.unpeeked);
		peeked = new LinkedList<>(toCopy.peeked);
		removed = new LinkedList<>(toCopy.removed);
	}
	
	public static <V> Pool<V> createEmpty()
	{
		return new Pool<>(new ArrayList<>());
	}
	
	public static <V> Pool<V> createCopy(Pool<V> toCopy)
	{
		return new Pool<>(toCopy);
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

	public void reset()
	{
		unpeeked.addAll(peeked);
		unpeeked.addAll(removed);
		peeked.clear();
		removed.clear();
	}

	public void resetPeeked()
	{
		// Add all the peeked back into the unpeeked and clear it
		unpeeked.addAll(peeked);
		peeked.clear();
	}

	public T peek(Random rand)
	{
		if (unpeeked.size() <= 0)
		{
			return null;
		}
		
		// get a random index and the object at that index
		int index = rand.nextInt(unpeeked.size());
		T obj = unpeeked.get(index);
		
		// Add the object to the peeked list and remove it
		// To do this a bit more efficiently, we replace the
		// found index with the last item, then we remove the
		// last item
		peeked.addFirst(obj);
		if (index != unpeeked.size() - 1)
		{
			unpeeked.set(index, unpeeked.get(unpeeked.size() - 1));
		}
		unpeeked.remove(unpeeked.size() - 1);
		
		// Return the object
		return obj;
	}
	
	public T popPeeked()
	{
		return selectPeeked(true);
	}
	
	public T selectPeeked()
	{
		return selectPeeked(false);
	}
	
	public T selectPeeked(boolean remove)
	{
		if (peeked.size() < 1)
		{
			return null;
		}
		
		// Get the last peeked object
		T obj = peeked.getFirst();
		
		// if we are removing it, move it to the removed list
		// before resetting the other lists
		if (remove)
		{
			removed.addFirst(obj);
			peeked.pop();
		}
		
		resetPeeked();
		
		// return the object
		return obj;
	}
	
	public int unpeekedSize()
	{
		return unpeeked.size();
	}
	
	public int size()
	{
		return unpeeked.size() + peeked.size();
	}
	
	public int instancesOf(T obj)
	{
		return (int) (peeked.stream().filter(s -> s == obj).count() +
				unpeeked.stream().filter(s -> s == obj).count());
	}
}