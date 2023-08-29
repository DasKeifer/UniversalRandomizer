package Support;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class SimpleObject 
{
	public UncomparableObject uncomparableObj;
	public String stringField;
	public int intField;
	public List<Integer> list;
	public Map<Integer, String> map;
	
	public SimpleObject(String stringField, int intField)
	{
		this.setStringField(stringField);
		this.setIntField(intField);
		list = new LinkedList<>();
		map = new LinkedHashMap<>();
	}
	
    public boolean intBetween2And5Excl()
    {
        return intField > 2 && intField < 5;
    }
    
    public boolean intIsEqualTo(int val)
    {
        return intField == val;
    }
    
    public Boolean returnTrue()
    {
        return true;
    }
    
    public Boolean returnNull()
    {
        return null;
    }


	public String getStringField() {
		return stringField;
	}

	public void setStringField(String stringField) {
		this.stringField = stringField;
	}

	public int getIntField() {
		return intField;
	}

	public void setIntField(int intField) {
		this.intField = intField;
	}
	
	public boolean setIntFieldReturn(int intField) {
		this.intField = intField;
		if (intField > 0)
		{
			return true;
		}
		return false;
	}
	
	public Boolean setIntFieldReturnBoxed(int intField) {
		this.intField = intField;
		if (intField > 0)
		{
			return true;
		}
		else if (intField == 0)
		{
			return null;
		}
		return false;
	}
	
	public UncomparableObject getUncomparableObject()
	{
		return uncomparableObj;
	}
	
	public List<Integer> getList()
	{
		return list;
	}
	
	public Map<Integer, String> getMap()
	{
		return map;
	}
}
