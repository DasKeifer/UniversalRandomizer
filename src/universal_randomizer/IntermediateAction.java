package universal_randomizer;

import java.util.stream.Stream;

import universal_randomizer.wrappers.ReflectionObject;
import universal_randomizer.wrappers.ReflectionObjectStreamAction;

public abstract class IntermediateAction<T> implements ReflectionObjectStreamAction<T>
{
	ReflectionObjectStreamAction<T> nextAction;
	
	protected IntermediateAction(ReflectionObjectStreamAction<T> nextAction)
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
