package universal_randomizer;

import java.util.Random;
import java.util.stream.Stream;

import universal_randomizer.pool.Pool;
import universal_randomizer.wrappers.ReflectionObject;

public class Randomize<T> implements StreamAction<T>
{
	String pathToField;
	Random rand;
	Pool<?> pool;
	
	private Randomize(String pathToField, Pool<?> pool, Random rand)
	{
		this.pathToField = pathToField;
		this.pool = pool;
		if (rand == null)
		{
			rand = new Random();
		}
		else
		{
			this.rand = rand;
		}
	}
	
	public static <T> Randomize<T> createRandom(String pathToField, Pool<?> pool)
	{
		return new Randomize<>(pathToField, pool, null);
	}
			
	public static <T> Randomize<T> createSeeded(String pathToField, Pool<?> pool, long seed)
	{
		return new Randomize<>(pathToField, pool, new Random(seed));
	}

	@Override
	public boolean perform(Stream<ReflectionObject<T>> objStream) 
	{
		objStream.forEach(obj -> obj.setVariableValue(pathToField, pool.getRandomValue(rand)));
		return true;
	}
}
