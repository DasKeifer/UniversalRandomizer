package universal_randomizer.randomize;

public class OnFailActionAttempts extends OnFailAction
{
	int maxAttempts;
	int attemptsLeft;
	
	private OnFailActionAttempts(OnFail type, int maxAttempts)
	{
		super(type);
		
		this.maxAttempts = maxAttempts;
		resetAttempts();
	}
	
	public static OnFailActionAttempts createRetryUntilExhaustedAction()
	{
		return new OnFailActionAttempts(OnFail.RETRY, -1);
	}
	
	public static OnFailActionAttempts createRetryAction(int maxRetires)
	{
		return new OnFailActionAttempts(OnFail.RETRY, maxRetires);
	}
	
	public static OnFailActionAttempts createResetAction(int maxResets)
	{
		if (maxResets < 0)
		{
			return null;
		}
		return new OnFailActionAttempts(OnFail.RESET, maxResets);
	}
	
	boolean anyAttemptsLeft()
	{
		return attemptsLeft > 0;
	}
	
	void decrementAttemptsLeft()
	{
		if (attemptsLeft > 0)
		{
			attemptsLeft--;
		}
	}
	
	boolean attempt()
	{
		boolean anyLeft = anyAttemptsLeft();
		decrementAttemptsLeft();
		return anyLeft;
	}
	
	void resetAttempts()
	{
		attemptsLeft = maxAttempts;
	}
}
