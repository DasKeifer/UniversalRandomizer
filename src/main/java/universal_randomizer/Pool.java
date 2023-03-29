package universal_randomizer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Pool<T>
{	
	// TODO: Return using function?
	// TODO: Create new class for dependent Pools - i.e. if passed x, use set y
	// TODO: Allow creation by reference, shallow, deep and reflection copy?
	// TODO: Make into more specific subclasses, make Pool an interface/abstract class?
	
	protected ArrayList<T> unpeeked;
	protected LinkedList<T> peeked;
	protected LinkedList<T> removed;
	
	protected Pool(boolean removeDuplicates, Collection<T> valCollection)
	{
		if (removeDuplicates)
		{
			// Convert to a set first to remove duplicates
			unpeeked = new ArrayList<>(new HashSet<>(valCollection));
		}
		else
		{
			unpeeked = new ArrayList<>(valCollection);
		}
		peeked = new LinkedList<>();
		removed = new LinkedList<>();
	}
	
	// TODO: Copy constructor vs function vs static
	
	protected Pool(Pool<T> toCopy)
	{
		unpeeked = new ArrayList<>(toCopy.unpeeked);
		peeked = new LinkedList<>(toCopy.peeked);
		removed = new LinkedList<>(toCopy.removed);
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
	
	public static <V> Pool<V> create(boolean removeDuplicates, Stream<V> values)
	{
		return new Pool<>(removeDuplicates, values.collect(Collectors.toList()));
	}
	
	public Pool<T> copy()
	{
		return new Pool<>(this);
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
	
	public int instancesOfUnpeeked(T obj)
	{
		return (int) unpeeked.stream().filter(s -> s == obj).count();
	}
}