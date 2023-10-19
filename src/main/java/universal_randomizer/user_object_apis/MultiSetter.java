package universal_randomizer.user_object_apis;

public interface MultiSetter <T, V>
{
	public boolean setReturn(T toSet, V val, int counter);

	public default boolean asSetter(T t, V v)
	{
		return setReturn(t, v, 1);
	}

	public default void asSetterNoReturn(T t, V v)
	{
		setReturn(t, v, 1);
	}

	public default void asMultiSetterNoReturn(T t, V v, int c)
	{
		setReturn(t, v, c);
	}
	
	public static <T2, V2> MultiSetter<T2, V2> cast(Setter<T2, V2> setter)
	{
		return setter::asMultiSetter;
	}
	
	public static <T2, V2> MultiSetter<T2, V2> cast(SetterNoReturn<T2, V2> setter)
	{
		return setter::asMultiSetter;
	}

	public static <T2, V2> MultiSetter<T2, V2> cast(MultiSetterNoReturn<T2, V2> setter)
	{
		return setter::asMultiSetter;
	}
}
