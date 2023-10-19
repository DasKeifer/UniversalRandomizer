package universal_randomizer.user_object_apis;

public interface SetterNoReturn <T, V>
{
	public void set(T toSet, V val);
	
	public default boolean asSetter(T t, V v)
	{
		set(t, v);
		return true;
	}

	public default boolean asMultiSetter(T t, V v, int c)
	{
		set(t, v);
		return true;
	}

	public default void asMultiSetterNoReturn(T t, V v, int c)
	{
		set(t, v);
	}
	
	public static <T2, V2> SetterNoReturn<T2, V2> cast(Setter<T2, V2> setter)
	{
		return setter::asSetterNoReturn;
	}

	public static <T2, V2> SetterNoReturn<T2, V2> cast(MultiSetter<T2, V2> setter)
	{
		return setter::asSetterNoReturn;
	}
	
	public static <T2, V2> SetterNoReturn<T2, V2> cast(MultiSetterNoReturn<T2, V2> setter)
	{
		return setter::asSetterNoReturn;
	}
}
