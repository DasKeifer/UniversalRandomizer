package universal_randomizer.pool;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class PeekPool<T> implements RandomizerBasicPool<T>
{	
	// TODO: Return using function?
	// TODO: Create new class for dependent Pools - i.e. if passed x, use set y
	// TODO: Allow creation by reference, shallow, deep and reflection copy?
	// TODO: Make into more specific subclasses, make Pool an interface/abstract class?
	
	private ArrayList<T> unpeeked;
	private LinkedList<T> peekedBatch;
	private LinkedList<T> skipped;
	private LinkedList<T> removed;
	private boolean selectPeekedRemoves;
	
	protected PeekPool(boolean selectPeekedRemoves, Collection<T> valCollection, boolean removeDuplicates)
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
		peekedBatch = new LinkedList<>();
		skipped = new LinkedList<>();
		removed = new LinkedList<>();
		this.selectPeekedRemoves = selectPeekedRemoves;
	}

	protected PeekPool(PeekPool<T> toCopy)
	{
		peekedBatch = new LinkedList<>(toCopy.peekedBatch);
		unpeeked = new ArrayList<>(toCopy.unpeeked);
		skipped = new LinkedList<>(toCopy.skipped);
		removed = new LinkedList<>(toCopy.removed);
	}
	
	public static <V> PeekPool<V> createEmpty()
	{
		return new PeekPool<>(false, new ArrayList<>(), false);
	}
	
	public static <V> PeekPool<V> create(boolean selectPeekedRemoves, Collection<V> valCollection)
	{
		if (valCollection == null)
		{
			return null;
		}
		return new PeekPool<>(selectPeekedRemoves, valCollection, false);
	}
	
	public static <V> PeekPool<V> createNoDups(boolean selectPeekedRemoves, Collection<V> valCollection)
	{
		if (valCollection == null)
		{
			return null;
		}
		return new PeekPool<>(selectPeekedRemoves, valCollection, true);
	}
	
	@SafeVarargs
	public static <V> PeekPool<V> create(boolean selectPeekedRemoves, V... values)
	{
		return create(selectPeekedRemoves, List.of(values));
	}

	@SafeVarargs
	public static <V> PeekPool<V> createNoDups(boolean selectPeekedRemoves, V... values)
	{
		return createNoDups(selectPeekedRemoves, List.of(values));
	}
	
	public PeekPool<T> copy()
	{
		return new PeekPool<>(this);
	}

	@Override
	public void reset()
	{
		resetPeeked();
		unpeeked.addAll(removed);
		removed.clear();
	}

	@Override
	public void resetPeeked()
	{
		// Add all the peeked back into the unpeeked and clear it
		unpeeked.addAll(peekedBatch);
		unpeeked.addAll(skipped);
		peekedBatch.clear();
		skipped.clear();
	}

	@Override
	public T peek(Random rand)
	{
		if (!peekedBatch.isEmpty())
		{
			peekNewBatch();
		}
		return peekBatch(rand);
	}

	// TODO: For non pop/remove don't move from unpeeked for batch?
	// Then would probably need a function to remove from batch pool
	@Override
	public T peekBatch(Random rand)
	{
		if (unpeeked.isEmpty() || rand == null)
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
		peekedBatch.addFirst(obj);
		if (index != unpeeked.size() - 1)
		{
			unpeeked.set(index, unpeeked.get(unpeeked.size() - 1));
		}
		unpeeked.remove(unpeeked.size() - 1);
		
		// Return the object
		return obj;
	}

	@Override
	public void peekNewBatch()
	{
		skipped.addAll(peekedBatch);
		peekedBatch.clear();
	}
	
	public void popPeeked()
	{
		selectPeeked(true);
	}

	@Override
	public void selectPeeked()
	{
		selectPeeked(selectPeekedRemoves);
	}
	
	public void selectPeeked(boolean remove)
	{
		if (!peekedBatch.isEmpty())
		{
			// if we are removing it, move it to the removed list
			// before resetting the other lists
			if (remove)
			{
				removed.addAll(peekedBatch);
				peekedBatch.clear();
			}
			
			resetPeeked();
		}
	}
	
	public int unpeekedSize()
	{
		return unpeeked.size();
	}
	
	public int size()
	{
		return unpeeked.size() + peekedBatch.size() + skipped.size();
	}
	
	public int instancesOf(T obj)
	{
		return (int) (peekedBatch.stream().filter(s -> s == obj).count() +
				skipped.stream().filter(s -> s == obj).count() +
				unpeeked.stream().filter(s -> s == obj).count());
	}
	
	public int instancesOfUnpeeked(T obj)
	{
		return (int) unpeeked.stream().filter(s -> s == obj).count();
	}

	protected ArrayList<T> getUnpeeked() {
		return unpeeked;
	}

	protected LinkedList<T> getPeekedBatch() {
		return peekedBatch;
	}

	protected LinkedList<T> getSkipped() {
		return skipped;
	}

	protected LinkedList<T> getRemoved() {
		return removed;
	}
	
	public boolean doesSelectPeekedRemove() {
		return selectPeekedRemoves;
	}

	public void setSelectPeekedRemoves(boolean selectPeekedRemoves) {
		this.selectPeekedRemoves = selectPeekedRemoves;
	}

	@Override
	public boolean useNextPool() 
	{
		return false;
	}
}