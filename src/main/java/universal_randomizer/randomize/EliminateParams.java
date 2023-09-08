package universal_randomizer.randomize;


public class EliminateParams
{
	private int maxDepth;
	
	private EliminateParams(int maxDepth)
	{
		this.maxDepth = maxDepth;
	}
	
	public static EliminateParams create(int maxDepth) 
	{
		if (maxDepth <= 0)
		{
			return null;
		}
		return new EliminateParams(maxDepth);
	}
	
	public static EliminateParams createNoAdditionalPools() 
	{
		return new EliminateParams(1);
	}

	public int getMaxDepth() 
	{
		return maxDepth;
	}
}
