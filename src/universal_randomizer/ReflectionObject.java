package universal_randomizer;

public class ReflectionObject <T> {
	public T obj;
	public int randValue;
	
	public ReflectionObject(T obj)
	{
		this.obj = obj;
	}

	public Object getVariableValue(String variable) {
		return ReflectionUtils.getVariableValue(obj, variable);
	}
}
