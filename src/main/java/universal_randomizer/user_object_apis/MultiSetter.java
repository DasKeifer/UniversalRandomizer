package universal_randomizer.user_object_apis;

public interface MultiSetter <T, V>
{
	public boolean setReturn(T toSet, V val, int counter);
	

	public static <T, V> MultiSetter<T, V> cast(Setter<T, V> setter)
	{
		return (t,v,c) -> setter.setReturn(t, v);
	}

	public static <T, V> MultiSetter<T, V> cast(MultiSetterNoReturn<T, V> setter)
	{
		return (t,v,c) -> {
			setter.set(t, v, c);
			return true;
		};
	}
	
	public static <T, V> MultiSetter<T, V> cast(SetterNoReturn<T, V> setter)
	{
		return (t,v,c) -> {
			setter.set(t, v);
			return true;
		};
	}
}
