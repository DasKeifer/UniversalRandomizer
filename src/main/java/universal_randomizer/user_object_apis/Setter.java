package universal_randomizer.user_object_apis;

public interface Setter <T, V>
{
	public boolean setReturn(T toSet, V val);
	
	public default void asSetterNoReturn(T t, V v)
	{
		setReturn(t, v);
	}

	public default boolean asMultiSetter(T t, V v, int c)
	{
		return setReturn(t, v);
	}

	public default void asMultiSetterNoReturn(T t, V v, int c)
	{
		setReturn(t, v);
	}
	
	public static <T2, V2> Setter<T2, V2> cast(SetterNoReturn<T2, V2> setter)
	{
		return setter::asSetter;
	}

	public static <T2, V2> Setter<T2, V2> cast(MultiSetter<T2, V2> setter)
	{
		return setter::asSetter;
	}
	
	public static <T2, V2> Setter<T2, V2> cast(MultiSetterNoReturn<T2, V2> setter)
	{
		return setter::asSetter;
	}
}
