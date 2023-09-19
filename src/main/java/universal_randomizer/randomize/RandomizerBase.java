package universal_randomizer.randomize;

import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

public abstract class RandomizerBase<T, P> 
{	
	private P pool;
	private Random rand;
	private EnforceParams<T> enforceActions;
	
	protected RandomizerBase(EnforceParams<T> enforce)
	{
		pool = null;
		rand = null;
		
		if (enforce == null)
		{
			this.enforceActions = EnforceParams.createNoEnforce();
		}
		else
		{
			this.enforceActions = enforce;
		}
	}
	
	public boolean perform(Stream<T> objStream, P pool) 
	{
		return perform(objStream, pool, null);
	}
	
	public boolean perform(Stream<T> objStream, P pool, Random rand) 
	{
		this.pool = pool;
		if (rand == null && this.rand == null)
		{
			this.rand = new Random();
		}
		
		// in order to "reuse" the stream, we need to convert it out of a stream
		// and create new ones. We need to save off the list if we need to create
        // a source pool or if there is a RESET on fail action
		List<T> streamAsList = objStream.toList();
		return attemptRandomization(streamAsList);
	}
	
	// Handles RESET
	protected boolean attemptRandomization(List<T> streamAsList)
	{
		// Attempt to assign randomized values for each item in the stream
		List<T> failed = randomize(streamAsList.stream());
		
		// While we have failed and have resets left
		for (int reset = 0; reset < enforceActions.getMaxResets(); reset++)
		{
			if (failed.isEmpty())
			{
				break;
			}
			resetPool();
			failed = randomize(streamAsList.stream());
		}
		
		return failed.isEmpty();
	}

	protected List<T> randomize(Stream<T> objStream)
	{
		return objStream.filter(this::assignValueNegated).toList();
	}
	
	protected boolean assignValueNegated(T obj)
	{
		return !assignValue(obj);
	}

	protected abstract void resetPool();
	protected abstract boolean assignValue(T obj);

	protected Random getRandom() 
	{
		return rand;
	}

	protected P getPool() 
	{
		return pool;
	}

	protected EnforceParams<T> getEnforceActions() 
	{
		return enforceActions;
	}
}