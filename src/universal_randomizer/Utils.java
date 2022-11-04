package universal_randomizer;

public class Utils 
{
	@SuppressWarnings("unchecked")
	public static <M> M safeCast(Object obj)
	{
		try
		{
			return (M) obj;
		}
		catch (ClassCastException cce)
		{
			return null;
		}
	}
	
	// TODO: implement
	public static <T> T deepCopy(T obj)
	{
		return obj;
	}
}
