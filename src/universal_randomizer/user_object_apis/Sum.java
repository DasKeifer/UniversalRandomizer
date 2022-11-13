package universal_randomizer.user_object_apis;

@FunctionalInterface
public interface Sum<T> 
{
	public T sum(T lhs, T rhs);
}
