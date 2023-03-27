package universal_randomizer.randomize;


public class EliminateParams
{
	private int maxDepth;

	public EliminateParams(int maxDepth)
	{
		// TODO: Warn if <= 0
		this.maxDepth = maxDepth;
	}
	
	public EliminateParams(EliminateParams toCopy)
	{
		this.maxDepth = toCopy.maxDepth;
	}
	
	public static EliminateParams createNoAdditionalPools() 
	{
		return new EliminateParams(1);
	}

	public EliminateParams copy() 
	{
		return new EliminateParams(this);
	}

	public int getMaxDepth() 
	{
		return maxDepth;
	}
}
