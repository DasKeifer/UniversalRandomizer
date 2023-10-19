package universal_randomizer.user_object_apis;

public interface MultiSetterNoReturn <T, V>
{
	public void set(T toSet, V val, int count);

	public static <T, V> MultiSetterNoReturn<T, V> cast(Setter<T, V> setter)
	{
		return (t,v,c) -> setter.setReturn(t, v);
	}
	
	public static <T, V> MultiSetterNoReturn<T, V> cast(SetterNoReturn<T, V> setter)
	{
		return (t,v,c) -> setter.set(t, v);
	}
	
	public static <T, V> MultiSetterNoReturn<T, V> cast(MultiSetter<T, V> setter)
	{
		return setter::setReturn;
	}
}
