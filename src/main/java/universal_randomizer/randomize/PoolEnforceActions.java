package universal_randomizer.randomize;


public class PoolEnforceActions
{
	private int maxDepth;

	public PoolEnforceActions(int maxDepth)
	{
		this.maxDepth = maxDepth;
	}
	
	public static PoolEnforceActions createNone() 
	{
		return new PoolEnforceActions(0);
	}

	public int getMaxDepth() 
	{
		return maxDepth;
	}
}
