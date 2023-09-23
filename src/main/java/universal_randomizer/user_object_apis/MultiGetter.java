package universal_randomizer.user_object_apis;

public interface MultiGetter <T, R> 
{
	public R get(T toGetFrom, int count);
}
