package universal_randomizer.user_object_apis;

public interface SetterNoReturn <T, V>
{
	public void set(T toSet, V val);

	public static <T, V> Setter<T, V> cast(SetterNoReturn<T, V> setter)
	{
		return (t,v) -> {
			setter.set(t, v);
			return true;
		};
	}
	
	public static <T, V> Setter<T, V> cast(MultiSetter<T, V> setter)
	{
		return (t,v) -> setter.setReturn(t, v, 1);
	}
	
	public static <T, V> Setter<T, V> cast(MultiSetterNoReturn<T, V> setter)
	{
		return (t,v) -> {
			setter.set(t, v, 1);
			return true;
		};
	}
}
