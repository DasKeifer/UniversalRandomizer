package universal_randomizer.condition;

import universal_randomizer.wrappers.ReflectionObject;

public interface Condition <T> 
{
	public abstract Condition<T> copy();
	public abstract boolean evaluate(ReflectionObject<T> obj);
}
