package universal_randomizer;

import java.util.Random;
import java.util.stream.Stream;

import universal_randomizer.action.ReflObjStreamAction;
import universal_randomizer.wrappers.ReflectionObject;

public class Randomize<T, P> implements ReflObjStreamAction<T>
{
	String pathToField;
	Random rand;
	Pool<P> pool;
	
	private Randomize(String pathToField, Pool<P> pool, Random rand)
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
	
	public static <V, S> Randomize<V, S> createRandom(String pathToField, Pool<S> pool)
	{
		return new Randomize<>(pathToField, pool, null);
	}
			
	public static <V, S> Randomize<V, S> createSeeded(String pathToField, Pool<S> pool, long seed)
	{
		return new Randomize<>(pathToField, pool, new Random(seed));
	}

	@Override
	public boolean perform(Stream<ReflectionObject<T>> objStream) 
	{
		if (pool == null)
		{
			pool = Pool.createFromStream(pathToField, objStream);
		}
		objStream.forEach(obj -> obj.setVariableValue(pathToField, pool.getRandom(rand)));
		return true;
	}
}
