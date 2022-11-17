package universal_randomizer.randomize;

import universal_randomizer.condition.CompoundCondition;

public class OnFailAlternateAction<T> extends OnFailAction 
{
	//TODO: Implement
	
	public OnFailAlternateAction()
	{
		super(OnFail.ALTERNATE);
	}
	
	public CompoundCondition<T> getCondition()
	{
		return null;
	}

	public boolean applied() {
		// TODO Auto-generated method stub
		return false;
	}
}
