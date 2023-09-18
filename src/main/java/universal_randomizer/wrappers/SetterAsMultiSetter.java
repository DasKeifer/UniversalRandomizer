package universal_randomizer.wrappers;

import universal_randomizer.user_object_apis.Setter;
import universal_randomizer.user_object_apis.MultiSetter;

public class SetterAsMultiSetter<T, V> implements MultiSetter<T, V>
{
	Setter<T, V> setter;
	
	public SetterAsMultiSetter(Setter<T, V> setter)
	{
		this.setter = setter;
	}

	@Override
	public boolean setReturn(T toSet, V val, int counter) 
	{
		return setter.setReturn(toSet, val);
	}
}
