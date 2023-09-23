package Support;


import universal_randomizer.randomize.EnforceParams;
import universal_randomizer.randomize.SingleRandomizer;
import universal_randomizer.user_object_apis.Getter;
import universal_randomizer.user_object_apis.Setter;
import universal_randomizer.user_object_apis.SetterNoReturn;

public interface RandomizerCommonTestsGetterCreate<T, P>
{
	SingleRandomizer<T, P> createPoolFromStream(Setter<T, P> setter, Getter<T, P> poolGetter, EnforceParams<T> enforce);
	
	default SingleRandomizer<T, P> create(SetterNoReturn<T, P> setter, Getter<T, P> poolGetter, EnforceParams<T> enforce)
	{
		return createPoolFromStream((Setter<T, P>)setter, poolGetter, enforce);
	}
}
