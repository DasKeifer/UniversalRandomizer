package Support;

import java.util.Random;

import universal_randomizer.Pool;
import universal_randomizer.randomize.EliminateParams;
import universal_randomizer.randomize.EnforceParams;
import universal_randomizer.randomize.RandomizerEliminate;

public class ExposeRandomizerEliminate extends RandomizerEliminate<SimpleObject, Integer>{

	public ExposeRandomizerEliminate(String pathToField, Pool<Integer> pool, Random rand,
			EnforceParams<SimpleObject> enforce, EliminateParams poolEnforce) 
	{
		super(pathToField, pool, rand, enforce, poolEnforce);
	}

	public Integer exposedPeekNext(Random rand) 
	{
		return peekNext(rand);
	}
	
	public void exposedSelectPeeked() 
	{
		selectPeeked();
	}
	
	public boolean exposedNextPool()
	{
		return nextPool();
	}
}
