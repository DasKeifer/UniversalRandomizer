package universal_randomizer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

public class Pool<T>
{	
	// TODO: Return using function?
	// TODO: Create new class for dependent Pools - i.e. if passed x, use set y
	// TODO: Allow creation by reference, shallow, deep and reflection copy?
	// TODO: Make into more specific subclasses, make Pool an interface/abstract class?
	
	private ArrayList<T> items;
	
	protected Pool(boolean removeDuplicates, Collection<T> valCollection)
	{
		if (removeDuplicates)
		{
			// Convert to a set first to remove duplicates
			items = new ArrayList<>(new HashSet<>(valCollection));
		}
		else
		{
			items = new ArrayList<>(valCollection);
		}
	}
	
	protected Pool(Pool<T> toCopy)
	{
		items = new ArrayList<>(toCopy.items);
	}
	
	public static <V> Pool<V> createEmpty()
	{
		return new Pool<>(false, new ArrayList<>());
	}
	
	public static <V> Pool<V> create(boolean removeDuplicates, Collection<V> valCollection)
	{
		return new Pool<>(removeDuplicates, valCollection);
	}

	@SafeVarargs
	public static <V> Pool<V> create(boolean removeDuplicates, V... values)
	{
		return new Pool<>(removeDuplicates, Arrays.asList(values));
	}
	
	public Pool<T> copy()
	{
		return new Pool<>(this);
	}

	public T get(Random rand)
	{
		if (items.isEmpty())
		{
			return null;
		}
		
		// get a random index and the object at that index
		int index = rand.nextInt(items.size());
		return items.get(index);
	}
	
	public int size()
	{
		return items.size();
	}
	
	public int instancesOf(T obj)
	{
		return (int) (items.stream().filter(s -> s == obj).count());
	}

	protected List<T> getItems() {
		return items;
	}
}