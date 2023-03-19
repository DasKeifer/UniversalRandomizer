package universal_randomizer.randomize;


public class PoolEnforceActions
{
	private int maxDepth;

	public PoolEnforceActions(int maxDepth)
	{
		this.maxDepth = maxDepth;
	}
	
	public PoolEnforceActions(PoolEnforceActions toCopy)
	{
		this.maxDepth = toCopy.maxDepth;
	}
	
	public static PoolEnforceActions createNone() 
	{
		return new PoolEnforceActions(0);
	}

	public PoolEnforceActions copy() 
	{
		return new PoolEnforceActions(this);
	}

	public int getMaxDepth() 
	{
		return maxDepth;
	}
}
