package Support;


import universal_randomizer.Pool;
import universal_randomizer.randomize.EnforceParams;
import universal_randomizer.randomize.Randomizer;
import universal_randomizer.user_object_apis.Setter;
import universal_randomizer.user_object_apis.SetterNoReturn;

public interface RandomizerCommonTestsPoolCreate<T, P>
{
	Randomizer<T, P> create(Setter<T, P> setter, Pool<P> pool, EnforceParams<T> enforce);
	
	default Randomizer<T, P> create(SetterNoReturn<T, P> setter, Pool<P> pool, EnforceParams<T> enforce)
	{
		return create((Setter<T, P>)setter, pool, enforce);
	}
}
