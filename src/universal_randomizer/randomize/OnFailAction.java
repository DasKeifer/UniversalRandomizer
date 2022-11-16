package universal_randomizer.randomize;

public class OnFailAction 
{
	OnFail actionType;
	
	public OnFailAction(OnFail type)
	{
		actionType = type;
	}
	
	public OnFail getActionType()
	{
		return actionType;
	}
}
