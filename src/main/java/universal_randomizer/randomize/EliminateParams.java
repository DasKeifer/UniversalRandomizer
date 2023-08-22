package universal_randomizer.randomize;


public class EliminateParams
{
	private int maxDepth;

	// TODO refactor to factory
	
	public EliminateParams(int maxDepth)
	{
		// TODO: Warn if <= 0
		this.maxDepth = maxDepth;
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
