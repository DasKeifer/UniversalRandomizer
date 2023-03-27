package Support;

import java.util.Random;

import universal_randomizer.Pool;
import universal_randomizer.randomize.EnforceParams;
import universal_randomizer.randomize.Randomizer;

public interface RandomizerCommonTestsCreate<T, P>
{
	Randomizer<T, P> create(String pathToField, Pool<P> pool, Random rand, EnforceParams<T> enforce);
}
