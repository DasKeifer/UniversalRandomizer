package universal_randomizer.wrappers;

public class ReflectionObject <T> {
	T obj;
	int randValue;

	// TODO: Refactor to factory instead of constructor
	
	public ReflectionObject(T obj)
	{
		this.obj = obj;
	}

	// TODO: Needed?
//	public Class<?> getVariableType(String name)
//	{
//		try 
//		{
//			return obj.getClass().getField(name).getType();
//		} 
//		catch (NoSuchFieldException | SecurityException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return null;
//	}
	
	// TODO: Extract to function that can be used for vars or functions? At least part probably can be
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
	
	public T getObject()
	{
		return obj;
	}
	
	public int getRandomValue()
	{
		return randValue;
	}
	
	public void setRandomValue(int val)
	{
		randValue = val;
	}
}
