package universal_randomizer.user_object_apis;

public interface MultiSetterNoReturn <T, V>
{
	public void set(T toSet, V val, int count);

	public default boolean asSetter(T t, V v)
	{
		set(t, v, 1);
		return true;
	}

	public default void asSetterNoReturn(T t, V v)
	{
		set(t, v, 1);
	}

	public default boolean asMultiSetter(T t, V v, int c)
	{
		set(t, v, c);
		return true;
	}
	
	public static <T2, V2> MultiSetterNoReturn<T2, V2> cast(Setter<T2, V2> setter)
	{
		return setter::asMultiSetterNoReturn;
	}
	
	public static <T2, V2> MultiSetterNoReturn<T2, V2> cast(SetterNoReturn<T2, V2> setter)
	{
		return setter::asMultiSetterNoReturn;
	}

	public static <T2, V2> MultiSetterNoReturn<T2, V2> cast(MultiSetter<T2, V2> setter)
	{
		return setter::asMultiSetterNoReturn;
	}
}
