package universal_randomizer.action;

import java.util.stream.Stream;

import universal_randomizer.wrappers.ReflectionObject;

public abstract class IntermediateAction<T> implements ReflObjStreamAction<T>
{
	ReflObjStreamAction<T> nextAction;
	
	protected IntermediateAction(ReflObjStreamAction<T> nextAction)
	{
		if (nextAction == null)
		{
			System.out.println("Select does not have a next action");
			//TODO Throw error
		}
		
		this.nextAction = nextAction;
	}
	
	protected boolean continueActions(Stream<ReflectionObject<T>> objStream)
	{
		
		return nextAction.perform(objStream);
	}
}
