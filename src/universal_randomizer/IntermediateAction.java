package universal_randomizer;

import java.util.stream.Stream;

public abstract class IntermediateAction<T extends Object> implements StreamAction<T>
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
	
	protected boolean continueActions(Stream<T> objStream)
	{
		return nextAction.perform(objStream);
	}
}
