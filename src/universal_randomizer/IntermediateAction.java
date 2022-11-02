package universal_randomizer;

import java.util.stream.Stream;

import universal_randomizer.wrappers.ReflectionObject;

public abstract class IntermediateAction<T> implements StreamAction<T>
{
	StreamAction<T> nextAction;
	
	protected IntermediateAction(StreamAction<T> nextAction)
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
