package Support;

public class BadObject 
{
	public String stringField;
	public int intField;
	
	public BadObject(String stringField, int intField)
	{
		this.setStringField(stringField);
		this.setIntField(intField);
	}
	
    public Boolean intBetween2And5Excl()
    {
        return intField > 2 && intField < 5;
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
}
