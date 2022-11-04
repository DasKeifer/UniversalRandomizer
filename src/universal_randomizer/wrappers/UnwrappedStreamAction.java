package universal_randomizer.wrappers;

public interface UnwrappedStreamAction<T> 
{
	// TODO enhance we pre & post actions?
	
	public boolean perform(T obj);
}
