package universal_randomizer.utils;

public class AttemptCounter
{
	int maxAttempts;
	int attemptCount;
	
	private AttemptCounter(int maxAttempts)
	{
		this.maxAttempts = maxAttempts;
		resetAttempts();
	}

	public static AttemptCounter createNoAttempts()
	{
		return new AttemptCounter(0);
	}
	
	public static AttemptCounter createNoRetries()
	{
		return new AttemptCounter(1);
	}
	
	public static AttemptCounter create(int maxAttempts)
	{
		return new AttemptCounter(maxAttempts);
	}	
	
	public static AttemptCounter createUnlimited()
	{
		return new AttemptCounter(-1);
	}
	
	public boolean anyAttemptsLeft()
	{
		return attemptCount < maxAttempts || maxAttempts < 0;
	}
	
	/// Attempts to marks another attempt having been made
	/// and returns whether or not there are any attempts left
	/// I.e. if the another attempt was marked
	public boolean attemptIfAnyRemaining()
	{
		boolean anyLeft = anyAttemptsLeft();
		if (anyLeft)
		{
			attemptCount++;
		}
		return anyLeft;
	}
	
	/// Returns if there are any more after accounting for
	/// the attempt. It is the responsibility of the caller
	/// to ensure attempts are left prior to calling this
	public boolean accountForAttempt()
	{
		if (anyAttemptsLeft())
		{
			attemptCount++;
			return anyAttemptsLeft();
		}
		return false;
	}
	
	public void resetAttempts()
	{
		attemptCount = 0;
	}
}
