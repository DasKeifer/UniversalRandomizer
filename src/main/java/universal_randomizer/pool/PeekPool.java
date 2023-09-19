package universal_randomizer.pool;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

public class PeekPool<T> implements RandomizerPool<T>
{	
	// TODO: Return using function?
	// TODO: Create new class for dependent Pools - i.e. if passed x, use set y
	// TODO: Allow creation by reference, shallow, deep and reflection copy?
	// TODO: Make into more specific subclasses, make Pool an interface/abstract class?
	
	private ArrayList<T> unpeeked;
	private LinkedList<T> peeked;
	private LinkedList<T> removed;
	
	protected PeekPool(boolean removeDuplicates, Collection<T> valCollection)
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
	
	protected PeekPool(PeekPool<T> toCopy)
	{
		unpeeked = new ArrayList<>(toCopy.unpeeked);
		peeked = new LinkedList<>(toCopy.peeked);
		removed = new LinkedList<>(toCopy.removed);
	}
	
	public static <V> PeekPool<V> createEmpty()
	{
		return new PeekPool<>(false, new ArrayList<>());
	}
	
	public static <V> PeekPool<V> create(boolean removeDuplicates, Collection<V> valCollection)
	{
		return new PeekPool<>(removeDuplicates, valCollection);
	}

	@SafeVarargs
	public static <V> PeekPool<V> create(boolean removeDuplicates, V... values)
	{
		return create(removeDuplicates, List.of(values));
	}
	
	public static <V> PeekPool<V> create(boolean removeDuplicates, Stream<V> values)
	{
		return create(removeDuplicates, values.toList());
	}
	
	public PeekPool<T> copy()
	{
		return new PeekPool<>(this);
	}

	@Override
	public void reset()
	{
		unpeeked.addAll(peeked);
		unpeeked.addAll(removed);
		peeked.clear();
		removed.clear();
	}

	@Override
	public void resetPeeked()
	{
		// Add all the peeked back into the unpeeked and clear it
		unpeeked.addAll(peeked);
		peeked.clear();
	}

	@Override
	public T peek(Random rand)
	{
		if (unpeeked.isEmpty())
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

	@Override
	public T selectPeeked()
	{
		return selectPeeked(false);
	}
	
	public T selectPeeked(boolean remove)
	{
		if (peeked.isEmpty())
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

	protected ArrayList<T> getUnpeeked() {
		return unpeeked;
	}

	protected LinkedList<T> getPeeked() {
		return peeked;
	}

	protected LinkedList<T> getRemoved() {
		return removed;
	}

	@Override
	public boolean useNextPool() 
	{
		return false;
	}
}