package universal_randomizer.user_object_apis;

public interface SetterNoReturn <T, V> extends Setter<T, V>
{
	public void set(T toSet, V val);
	
	public default boolean setReturn(T toSet, V val)
	{
		set(toSet, val);
		return true;
	}
}
