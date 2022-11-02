package universal_randomizer.wrappers;

public interface UnwrappedStreamAction<T extends Object> 
{
	// TODO enhance we pre & post actions?
	
	public boolean perform(T obj);
}
