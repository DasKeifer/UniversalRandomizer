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
        if (name.contains("."))
        {
            String[] paths = name.split("\\.");
            try 
            {
                Object nextObj = obj.getClass().getField(paths[0]).get(obj);
                for (int pathIndex = 1; pathIndex < paths.length; pathIndex++)
                {
                    nextObj = nextObj.getClass().getField(paths[pathIndex]).get(nextObj);
                }
                return nextObj;
            } 
            catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) 
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

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
