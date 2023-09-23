package universal_randomizer.user_object_apis;

public interface MultiSetterNoReturn <T, V> extends MultiSetter<T, V>
{
	public void set(T toSet, V val, int count);
	
	public default boolean setReturn(T toSet, V val, int count)
	{
		set(toSet, val, count);
		return true;
	}
}
