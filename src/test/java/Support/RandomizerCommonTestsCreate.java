package Support;


import universal_randomizer.Pool;
import universal_randomizer.randomize.EnforceParams;
import universal_randomizer.randomize.Randomizer;

public interface RandomizerCommonTestsCreate<T, P>
{
	Randomizer<T, P> create(String pathToField, Pool<P> pool, EnforceParams<T> enforce);
}
