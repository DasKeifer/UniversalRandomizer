package universal_randomizer.user_object_apis;

public interface SetterNoReturn <T, V> extends Setter<T, V>
{
	// TODO: Does this work with lambda functions? Probably not in which case convert to wrapper
	public void set(T toSet, V val);
	
	public default boolean setReturn(T toSet, V val)
	{
		set(toSet, val);
		return true;
	}
}
