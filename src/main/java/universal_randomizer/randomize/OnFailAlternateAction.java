package universal_randomizer.randomize;

import universal_randomizer.condition.Condition;

public class OnFailAlternateAction<T> extends OnFailAction 
{
	//TODO: Implement
	
	public OnFailAlternateAction()
	{
		super(OnFail.OR_ENFORCE);
	}
	
	public Condition<T> getCondition()
	{
		return null;
	}

	public boolean applied() {
		// TODO Auto-generated method stub
		return false;
	}
}
