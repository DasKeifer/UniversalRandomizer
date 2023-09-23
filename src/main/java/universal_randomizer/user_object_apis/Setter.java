package universal_randomizer.user_object_apis;

public interface Setter <T, V> extends MultiSetter<T, V>
{
	public boolean setReturn(T toSet, V val);
	
	public default boolean setReturn(T toSet, V val, int counter)
	{
		return setReturn(toSet, val);
	}
}
