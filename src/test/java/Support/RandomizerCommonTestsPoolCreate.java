package Support;


import universal_randomizer.randomize.EnforceParams;
import universal_randomizer.randomize.BasicRandomizer;
import universal_randomizer.user_object_apis.Setter;
import universal_randomizer.user_object_apis.SetterNoReturn;

public interface RandomizerCommonTestsPoolCreate<T, P>
{
	BasicRandomizer<T, P> create(Setter<T, P> setter, EnforceParams<T> enforce);
	
	default BasicRandomizer<T, P> create(SetterNoReturn<T, P> setter, EnforceParams<T> enforce)
	{
		return create((Setter<T, P>)setter, enforce);
	}
}
