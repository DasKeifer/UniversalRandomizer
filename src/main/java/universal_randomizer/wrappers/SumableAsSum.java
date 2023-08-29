package universal_randomizer.wrappers;

import universal_randomizer.user_object_apis.Sum;
import universal_randomizer.user_object_apis.Sumable;

public class SumableAsSum<T extends Sumable<T>> implements Sum<T>
{
	private boolean eitherNullThrow;
	private boolean bothNullThrow;
	
	public SumableAsSum()
	{
		eitherNullThrow = true;
		bothNullThrow = false;
	}
	
	@Override
	public T sum(T o1, T o2) 
	{
		if (o1 == null)
		{
			if (eitherNullThrow)
			{
				throw new NullPointerException();
			}
			if (o2 == null)
			{
				if (bothNullThrow)
				{
					throw new NullPointerException();
				}
				return null;
			}
			return o2.sum(null);
		}
		else if (o2 == null && eitherNullThrow)
		{
			throw new NullPointerException();
		}
		return o1.sum(o2);
	}

	protected boolean isBothNullThrow() 
	{
		return bothNullThrow;
	}

	protected void setBothNullThrow(boolean bothNullThrow) 
	{
		this.bothNullThrow = bothNullThrow;
	}

	protected boolean isEitherNullThrow() 
	{
		return eitherNullThrow;
	}

	protected void setEitherNullThrow(boolean eitherNullThrow) 
	{
		this.eitherNullThrow = eitherNullThrow;
	}
}
