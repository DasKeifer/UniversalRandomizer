package universal_randomizer;

import java.util.stream.Stream;

public abstract class IntermediateAction implements StreamAction
{
	StreamAction nextAction;
	
	protected IntermediateAction(StreamAction nextAction)
	{
		if (nextAction == null)
		{
			System.out.println("Select does not have a next action");
			//TODO Throw error
		}
		
		this.nextAction = nextAction;
	}
	
	protected boolean continueActions(Stream<ReflectionObject> objStream)
	{
		return nextAction.perform(objStream);
	}
}
