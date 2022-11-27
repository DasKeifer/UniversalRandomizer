package universal_randomizer.randomize;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import universal_randomizer.condition.Condition;
import universal_randomizer.wrappers.ReflectionObject;
import universal_randomizer.Pool;

public abstract class Randomizer<T, P> 
{
	protected static final int FAILED_INDEX = -1;
	protected static final int RETRY_INDEX = -2;
	
	String pathToField;
	Random rand;
	Pool<P> sourcePool;
	Condition<P> sourceEnforce;
	List<OnFailAction> sourceOnFailActions;
	
	
	List<Condition<P>> workingConditions;
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
		
		sourceEnforce = null;
		sourceOnFailActions = new LinkedList<>();
		workingConditions = new LinkedList<>();
		workingOnFailActions = null;
		currentOnFailActionIndex = 0;
		currentOnFailAction = null;
	}
	
	public void setOnFailActions(List<OnFailAction> actions)
	{
		this.sourceOnFailActions = actions;
	}
	
	public void setEnforce(Condition<P> enforce)
	{
		sourceEnforce = enforce;
	}

	public boolean perform(Stream<ReflectionObject<T>> objStream) 
	{
    	// Create the working actions. If they are empty, use the default settings
		workingOnFailActions = new LinkedList<>(sourceOnFailActions);
        if (workingOnFailActions.isEmpty())
        {
        	workingOnFailActions.add(OnFailActionAttempts.createRetryUntilExhaustedAction());
        }
        
        // Now go ahead an initialize our current location in our list of onFailActions
        // TODO: Come up with a better name
        resetActionListLocation();
        
		// in order to "reuse" the stream, we need to convert it out of a stream
		// and create new ones. We need to save off the list if we need to create
        // a source pool or if there is a RESET on fail action
		List<ReflectionObject<T>> streamAsList = null;
		if (hasActionRequiringRestart() || sourcePool == null)
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
		boolean success = attemptRandomization(objStream, streamAsList);
		
		// TODO: Randomize Each/All/Set
		
		// TODO: Handle multi-value sets
		
		return success;
	}
	
	protected List<ReflectionObject<T>> randomize(Stream<ReflectionObject<T>> objStream)
	{
		return objStream.filter(this::attemptAssignValue).collect(Collectors.toList());
	}
	
	// Handles RESET
	protected boolean attemptRandomization(Stream<ReflectionObject<T>> objStream, List<ReflectionObject<T>> streamAsList)
	{
		// Attempt to assign randomized values for each item in the stream
		List<ReflectionObject<T>> failed = randomize(objStream);
		
		if (!failed.isEmpty())
		{
			failed = failedRandomizationLoop(streamAsList);
		}
		
		return failed == null || !failed.isEmpty();
	}
	
	protected List<ReflectionObject<T>> failedRandomizationLoop(List<ReflectionObject<T>> streamAsList)
	{
		List<ReflectionObject<T>> failed;
		do
		{
			// TODO log error info
			
			failed = failedRandomization(streamAsList);
		} while (failed != null && !failed.isEmpty());
		return failed;
	}
	
	protected List<ReflectionObject<T>> failedRandomization(List<ReflectionObject<T>> streamAsList)
	{
		switch (getCurrentActionType())
		{
			case RESET:
				OnFailActionAttempts resetAction = (OnFailActionAttempts) currentOnFailAction;
				if (resetAction.anyAttemptsLeft())
				{
					 return randomize(streamAsList.stream());
				}
				else
				{
					// Reset our retry action, move to the next one, and retry
					resetAction.resetAttempts();
					moveToNextAction();
					return failedRandomization(streamAsList);
				}
			case OR_ENFORCE:
				applyOrEnforce();
				return failedRandomization(streamAsList);
			case ENFORCE:
				// TODO: new pass
				break;
			case RETRY:
			case IGNORE:
				// Handled at lower levels
			case ABORT:
				// Nothing to do - will abort naturally
			case NEW_POOL:
				// Not handled by this class
			case INVALID:
			default:
				break;
		}
		
		return null;
	}
	
	protected boolean attemptAssignValueNegated(ReflectionObject<T> obj)
	{
		return !attemptAssignValue(obj);
	}
	
	protected boolean attemptAssignValue(ReflectionObject<T> obj)
	{
		int index = attemptGetNextIndex();
		if (index >= 0)
		{
			return assignValue(obj, getAtIndex(index));
		}
		System.err.println("Failed to assign value for obj " + obj);
		return false;
	}

	protected abstract int getNextIndex(SortedSet<Integer> excludedIndexes);
	protected abstract P peekAtIndex(int index);
	protected abstract P getAtIndex(int index);
	
	protected boolean assignValue(ReflectionObject<T> obj, P value)
	{
		return obj.setVariableValue(pathToField, value);
	}
		
	protected int getNextIndex()
	{
		return getNextIndex(null);
	}
	
	protected int attemptGetNextIndex()
	{		
		// Get a random index
		int randIndex = getNextIndex();
		if (randIndex >= 0 && !passesEnforce(randIndex))
		{
			randIndex = failedEnforceLoop(randIndex);
		}

		// Failed entry checking
		if (randIndex < 0)
		{
			randIndex = failedEntryLoop();
		}
		return randIndex;
	}
	
	protected boolean passesEnforce(int index)
	{
		if (sourceEnforce != null)
		{
			return sourceEnforce.evaluate(peekAtIndex(index));
		}
		return false;
	}
	
	protected int failedEnforceLoop()
	{
		return failedEnforceLoop(-1);
	}
	
	protected int failedEnforceLoop(int failedIndex)
	{
		boolean success = false;
		SortedSet<Integer> failedIndexes = new TreeSet<>();
		do 
		{
			failedIndexes.add(failedIndex);
			failedIndex = failedEnforce(failedIndexes);
			if (failedIndex >= 0 && passesEnforce(failedIndex))
			{
				success = true;
			}
		} while (failedIndex == RETRY_INDEX || failedIndex >= 0  && !success);
		
		if (!success)
		{
			failedIndex = FAILED_INDEX;
		}
		return failedIndex;
	}
	
	protected int failedEnforce(SortedSet<Integer> excludedIndexes)
	{
		// If our fail action is RETRY, keep trying while we
		// have a valid index, have attempts left, and still fail
		// the condition
		switch (getCurrentActionType())
		{
			case RETRY:
				OnFailActionAttempts retryAction = (OnFailActionAttempts) currentOnFailAction;
				if (retryAction.anyAttemptsLeft())
				{
					int index = getNextIndex(excludedIndexes);
					if (index < 0)
					{
						return FAILED_INDEX;
					}
					return index;
				}
				else
				{
					// Reset our retry action, move to the next one, and retry
					retryAction.resetAttempts();
					moveToNextAction();
					return failedEnforce(excludedIndexes);
				}
			case OR_ENFORCE:
				applyOrEnforce();
				return RETRY_INDEX;
			case IGNORE:
				// What to do? New const for ingore?
				break;
			case ENFORCE:
			case ABORT:
			case RESET:
				// Handled at higher level
			case NEW_POOL:
				// Not handled in this class
			case INVALID:
			default:
				break;
		
		}
		return FAILED_INDEX;
	}
	
	protected int failedEntryLoop() 
	{
		int index;
		do
		{
			index = failedEntry();
		} while (index == RETRY_INDEX);
		
		if (index < 0)
		{
			index = FAILED_INDEX;
		}
		
		return index;
	}
	
	protected int failedEntry()
	{
		switch (getCurrentActionType())
		{
			case OR_ENFORCE:
				applyOrEnforce();
				return RETRY_INDEX;
			case IGNORE:
				// What to do? New const for ingore?
				break;
			case RETRY:
			case ABORT:
			case ENFORCE:
			case RESET:
				// Handled at higher level
			case NEW_POOL:
				// Not handled in this class
			case INVALID:
			default:
				break;
		
		}
		return FAILED_INDEX;
	}
	
	protected boolean failedRandomize()
	{
		return false;
	}
	
	protected boolean applyOrEnforce()
	{
		if (isCurrentActionOfType(OnFail.OR_ENFORCE))
		{
			@SuppressWarnings("unchecked")
			OnFailAlternateAction<P> altAction = (OnFailAlternateAction<P>) currentOnFailAction;
			
			// If it hasn't already been applied, apply it then step back to
			// the previous step
			if (!altAction.applied())
			{
				workingConditions.add(altAction.getCondition());
			}
			
			// Otherwise, move past this if its already applied - 
			// nothing to do but move on and try the next thing
			moveToNextAction();
			return true;
		}
		return false;
	}
	
	protected boolean hasActionRequiringRestart()
	{
		for (OnFailAction action : workingOnFailActions)
		{
			if (action.actionType == OnFail.RESET)
			{
				return true;
			}
		}
		return false;
	}
	
	protected OnFail getCurrentActionType()
	{
		if (currentOnFailAction != null)
		{
			return currentOnFailAction.actionType;
		}
		return OnFail.INVALID;
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
