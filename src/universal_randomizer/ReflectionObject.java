package universal_randomizer;

public class ReflectionObject 
{
	Object obj;
	
	public ReflectionObject(Object obj)
	{
		this.obj = obj;
	}
	
	public Class<?> getVariableType(String name)
	{
		try 
		{
			return obj.getClass().getField(name).getType();
		} 
		catch (NoSuchFieldException | SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public Object getVariableValue(String name)
	{
		try 
		{
			return obj.getClass().getField(name).get(obj);
		} 
		catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	public Object getObject()
	{
		return obj;
	}
}
