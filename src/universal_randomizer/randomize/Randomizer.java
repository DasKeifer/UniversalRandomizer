package universal_randomizer.randomize;

import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import universal_randomizer.condition.CompoundCondition;
import universal_randomizer.wrappers.ReflectionObject;
import universal_randomizer.Pool;

public abstract class Randomizer<T, P> 
{
	String pathToField;
	Random rand;
	Pool<P> sourcePool;
	CompoundCondition<P> sourceEnforce;
	List<OnFailAction> sourceOnFailActions;
	
	
	List<CompoundCondition<P>> workingConditions;
	List<OnFailAction> workingOnFailActions;
	int currentOnFailActionIndex;
	OnFailAction currentOnFailAction;
	
	// Make this a set? ALTERNATE repeats previous set if no specific is given]
	// Define specific order for fail actions?
	// Set order: Retry, alternate then retry again, repeat
	
	// RETRY, RESET, [NEW_POOL, ALTERNATE], <IGNORE/ABORT>
	
	protected Randomizer(String pathToField, Pool<P> pool, Random rand)
	{
		this.pathToField = pathToField;

		if (pool == null)
		{
			this.sourcePool = null;
		}
		else
		{
			this.sourcePool = Pool.createCopy(pool);
		}
		
		if (rand == null)
		{
			rand = new Random();
		}
		else
		{
			this.rand = rand;
		}
	}
	
	public void setOnFailActions(List<OnFailAction> actions)
	{
		this.sourceOnFailActions = actions;
	}

	public boolean perform(Stream<ReflectionObject<T>> objStream) 
	{
		// Set our working actions
        if (workingOnFailActions.isEmpty())
        {
        	if (sourceOnFailActions.isEmpty())
        	{
            	workingOnFailActions.add(OnFailActionAttempts.createRetryUntilExhaustedAction());
        	}
        	else
        	{
        		workingOnFailActions.addAll(sourceOnFailActions);
        	}
        }
        
        // Now go ahead an initialize our current location in our list of onFailActions
        // TODO: Come up with a better name
        resetActionListLocation();
        
		// in order to "reuse" the stream, we need to convert it out of a stream
		// and create new ones. We need to save off the list if we need to create
        // a source pool or if there is a RESET on fail action
		List<ReflectionObject<T>> streamAsList = null;
		if (setNextResetAction() || sourcePool == null)
		{
			//TODO: have a flatten option (Which is how it behaves now) vs a "pairwise" option which would
			//treat arrays/collections as a single entry
			streamAsList = objStream.collect(Collectors.toList());
			objStream = streamAsList.stream();
			
			if (sourcePool == null)
			{
				sourcePool = Pool.createFromStream(pathToField, streamAsList.stream());
			}
		}

		// If we failed randomization and have other actions left
		if (!attemptRandomization(objStream, streamAsList))
		{
			
		}
		
		// TODO: Randomize Each/All/Set
		
		// TODO: handle enforce
		
		// TODO: Handle multi-value sets
		
		return true;
	}
	
	protected boolean attemptRandomization(Stream<ReflectionObject<T>> objStream, List<ReflectionObject<T>> streamAsList)
	{
		// Attempt to assign randomized values for each item in the stream
		List<ReflectionObject<T>> failed = objStream.filter(this::attemptAssignValue).collect(Collectors.toList());

		// TODO: Need to reset pool for exclude type
		
		// if we failed, reset as many times as we are allowed 
		while (!failed.isEmpty() && isCurrentActionOfType(OnFail.RESET) && 
				((OnFailActionAttempts) currentOnFailAction).attempt())
		{
			// TODO log error info
			
			// Reset our list location and start over
			resetActionListLocation();
			failed = streamAsList.stream().filter(this::attemptAssignValue).collect(Collectors.toList());
		}

		// If we had a reset action, now that we finished, reset it
		if (isCurrentActionOfType(OnFail.RESET))
		{
			((OnFailActionAttempts) currentOnFailAction).resetAttempts();
		}
		
		// If we still didn't find any after resetting, move to the next action
		if (!failed.isEmpty())
		{
			moveToNextAction();
			
			// Attempt to apply an alternate enforce and reset the retry
			if (attemptApplyCurrentAlternateEnforce())
			{
				moveToNextAction();
			}
			
			return false;
		}
		return true;
	}
	
	protected abstract boolean attemptAssignValue(ReflectionObject<T> obj);
		
	protected int getNextIndex(ReflectionObject<T> obj, Pool<P> pool)
	{
		// Get a random index
		int randIndex = pool.getRandomIndex(rand);
		
		// If we got a valid index, but the condition failed
		if (randIndex >= 0 /*&& enforce condition fails on index*/)
		{
			// If our fail action is RETRY, keep trying while we
			// have a valid index, have attempts left, and still fail
			// the condition
			if (isCurrentActionOfType(OnFail.RETRY) && 
				((OnFailActionAttempts) currentOnFailAction).anyAttemptsLeft())
			{
				OnFailActionAttempts retryAction = (OnFailActionAttempts) currentOnFailAction;
				
				// Make the list of indexes to exclude
				SortedSet<Integer> exlcudedIndexes = new TreeSet<>();
				
				while (retryAction.attempt())
				{
					// Add the index that failed the check
					exlcudedIndexes.add(randIndex);
					
					// get the next index
					randIndex = pool.getRandomIndex(rand, exlcudedIndexes);
					
					// if we got a bad index or the condition passes, we
					// found a good value
					if (randIndex < 0 &&
							sourceEnforce != null /*&& enforce condition passes on index*/)
					{
						break;
					}
				}

				// If we ran out of attempts, set the index to invalid
				// and move to the next fail action
				if (!retryAction.anyAttemptsLeft())
				{
					randIndex = -1;
					moveToNextAction();
				}
				
				// Reset our retry action before we go
				retryAction.resetAttempts();
			}
			// If we don't have a RETRY action, set the index to -1
			else
			{
				randIndex = -1;
			}
		}
		
		// TODO: handle "chained" retries for things like new pool or replace
		
		// Attempt to apply an alternate enforce and reset the retry
		if (attemptApplyCurrentAlternateRepeat())
		{
			resetActionListLocation();
			return getNextIndex(obj, pool);
		}
		
		return randIndex;
	}
	
	protected boolean isCurrentActionOfType(OnFail toCheck)
	{
		return currentOnFailAction != null && currentOnFailAction.actionType == toCheck;
	}
	
	protected boolean moveToNextAction()
	{
		if (currentOnFailActionIndex + 1 < workingOnFailActions.size())
		{
			currentOnFailAction = workingOnFailActions.get(++currentOnFailActionIndex);
		}
		else
		{
			currentOnFailAction = null;
		}
		return currentOnFailAction != null;
	}
	
	protected boolean moveToPreviousAction()
	{
		if (currentOnFailActionIndex - 1 > 0)
		{
			currentOnFailAction = workingOnFailActions.get(--currentOnFailActionIndex);
		}
		else
		{
			currentOnFailAction = null;
		}
		return currentOnFailAction != null;
	}
		
	@SuppressWarnings("unchecked")
	protected boolean attemptApplyCurrentAlternateRepeat()
	{
		if (isCurrentActionOfType(OnFail.ALTERNATE_REPEAT))
		{
			OnFailAlternateAction<P> altAction = (OnFailAlternateAction<P>) currentOnFailAction;
			
			// If it hasn't already been applied, apply it then step back to
			// the previous step
			if (!altAction.applied())
			{
				workingConditions.add(altAction.getCondition());
				moveToPreviousAction();
				return true;
			}
			
			// Otherwise, move past this if its already applied
			moveToNextAction();
		}
		return false;
	}

	// TODO: Do it by position? Need a way to exclude the ALTERNATE_REPEAT. Maybe just check the next action?
	// Maybe alts are always kept after applied initially? Probably easiest approach. No. In below, I would want
	// the ALTERNATE reset as well.
	// RETRY 5 ALTERNATE X RETRY 5 RESET 3 ALTERNATE Y RESET 3
	// Maybe have alternates be "temporary" and only apply to previous
	// Maybe alternates imply a RETRY?
	// ALT only works after RETRY
	// RETRY and ALT cant follow RESET? That or it implies a RESET/new attempt? That probalby makes more sense
	// RESET is effectively a "terminal" and after require a new sequence with implied enforce X
	//
	// ENFORCE X RETRY 5 OR_ENFORCE Y RETRY 5 RESET 3 
	// [ENFORCE X] OR_ENFORCE Z RETRY 10 RESET 3
	// [ENFORCE X] RETRY 10 NEW_POOL 3 (RETRY 10 OR_ENFORCE Z)
	// retry item asignment 5 times. Then try using an alt enforce and retry 5 times. Then Reset up to 3 times the
	// whole assignment to try and get it to work. IF that fails, try
	protected void resetActionListLocation()
	{
		currentOnFailActionIndex = 0;
		
		if (!workingOnFailActions.isEmpty())
		{
			currentOnFailAction = workingOnFailActions.get(currentOnFailActionIndex);
		}
		else
		{
			currentOnFailAction = null;
		}
		
		workingConditions.clear();
		workingConditions.add(sourceEnforce);
	}
}
