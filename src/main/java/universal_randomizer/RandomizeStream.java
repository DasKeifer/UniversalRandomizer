package universal_randomizer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Spliterator;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import universal_randomizer.user_object_apis.Getter;
import universal_randomizer.wrappers.RandomOrdering;

public class RandomizeStream<T> implements Stream<T>
{
	private Stream<T> stream;

	protected RandomizeStream(Stream<T> source)
	{
		this.stream = source;
	}
	
	public static <T2> RandomizeStream<T2> create(Stream<T2> source)
	{
		return new RandomizeStream<>(source);
	}

	public <R> Map<R, List<T>> group(Getter<T, R> groupingFn)
	{
		if (groupingFn == null)
		{
			return new HashMap<>();
		}
		return stream.collect(Collectors.groupingBy(groupingFn::get));
	}

	public RandomizeStream<T> shuffle()
	{
		return shuffle(null);
	}

	public RandomizeStream<T> shuffle(long seed)
	{
		return shuffle(new Random(seed));
	}
	
	public RandomizeStream<T> shuffle(Random rand) 
	{
		final Random nonNull = rand != null ? rand : new Random();
		stream = stream
				.map(o -> RandomOrdering.create(o, nonNull.nextLong()))
				.sorted(RandomOrdering::sortBySortingValue)
				.map(RandomOrdering::getObject);
		return this;
	}

	public RandomizeStream<T> duplicate()
	{
		List<T> list = stream.toList();
		stream = list.stream();
		return RandomizeStream.create(list.stream());
	}

	public List<RandomizeStream<T>> duplicate(int numCopies)
	{
		List<T> list = stream.toList();
		stream = list.stream();
		List<RandomizeStream<T>> ret = new ArrayList<>(numCopies);
		for (int i = 0; i < numCopies; i++)
		{
			ret.add(RandomizeStream.create(list.stream()));
		}
		return ret;
	}
	
	public <R> RandomizeStream<R> convertToField(Getter<T, R> getter)
	{
		return getter != null ? RandomizeStream.create(stream.map(getter::get)) : null;
	}

	public <R> RandomizeStream<R> convertToFieldArray(Getter<T, R[]> getter)
	{
		return getter != null ? RandomizeStream.create(stream.flatMap(o -> Arrays.stream(getter.get(o)))) : null;
	}

	public <C extends Collection<R>, R> RandomizeStream<R> convertToFieldCollection(Getter<T, C> getter)
	{
		return getter != null ? RandomizeStream.create(stream.flatMap(obj -> getter.get(obj).stream())) : null;
	}

	public <S extends Stream<R>, R> RandomizeStream<R> convertToFieldStream(Getter<T, S> getter) 
	{
		return getter != null ? RandomizeStream.create(stream.flatMap(getter::get)) : null;
	}

	public <M extends Map<R, ?>, R> RandomizeStream<R> convertToFieldMapKeys(Getter<T, M> getter)
	{
		return getter != null ? RandomizeStream.create(stream.flatMap(obj -> getter.get(obj).keySet().stream())) : null;
	}

	public <M extends Map<?, R>, R> RandomizeStream<R> convertToFieldMapValues(Getter<T, M> getter)
	{
		return getter != null ? RandomizeStream.create(stream.flatMap(obj -> getter.get(obj).values().stream())) : null;
	}

	@Override
	public Iterator<T> iterator() {
		return stream.iterator();
	}

	@Override
	public Spliterator<T> spliterator() {
		return stream.spliterator();
	}

	@Override
	public boolean isParallel() {
		return stream.isParallel();
	}

	@Override
	public RandomizeStream<T> sequential() {
		stream = stream.sequential();
		return this;
	}

	@Override
	public RandomizeStream<T> parallel() {
		stream = stream.parallel();
		return this;
	}

	@Override
	public RandomizeStream<T> unordered() {
		stream = stream.unordered();
		return this;
	}

	@Override
	public RandomizeStream<T> onClose(Runnable closeHandler) {
		stream = stream.onClose(closeHandler);
		return this;
	}

	@Override
	public void close() {
		stream.close();
	}

	@Override
	public RandomizeStream<T> filter(Predicate<? super T> predicate) {
		stream = stream.filter(predicate);
		return this;
	}

	@Override
	public <R> RandomizeStream<R> map(Function<? super T, ? extends R> mapper) {
		return RandomizeStream.create(stream.map(mapper));
	}

	@Override
	public IntStream mapToInt(ToIntFunction<? super T> mapper) {
		return stream.mapToInt(mapper);
	}

	@Override
	public LongStream mapToLong(ToLongFunction<? super T> mapper) {
		return stream.mapToLong(mapper);
	}

	@Override
	public DoubleStream mapToDouble(ToDoubleFunction<? super T> mapper) {
		return stream.mapToDouble(mapper);
	}

	@Override
	public <R> RandomizeStream<R> flatMap(Function<? super T, ? extends Stream<? extends R>> mapper) {
		return RandomizeStream.create(stream.flatMap(mapper));
	}

	@Override
	public IntStream flatMapToInt(Function<? super T, ? extends IntStream> mapper) {
		return stream.flatMapToInt(mapper);
	}

	@Override
	public LongStream flatMapToLong(Function<? super T, ? extends LongStream> mapper) {
		return stream.flatMapToLong(mapper);
	}

	@Override
	public DoubleStream flatMapToDouble(Function<? super T, ? extends DoubleStream> mapper) {
		return stream.flatMapToDouble(mapper);
	}

	@Override
	public RandomizeStream<T> distinct() {
		stream = stream.distinct();
		return this;
	}

	@Override
	public RandomizeStream<T> sorted() {
		stream = stream.sorted();
		return this;
	}

	@Override
	public RandomizeStream<T> sorted(Comparator<? super T> comparator) {
		stream = stream.sorted();
		return this;
	}

	@Override
	public RandomizeStream<T> peek(Consumer<? super T> action) {
		stream = stream.peek(action);
		return this;
	}

	@Override
	public RandomizeStream<T> limit(long maxSize) {
		stream = stream.limit(maxSize);
		return this;
	}

	@Override
	public RandomizeStream<T> skip(long n) {
		stream = stream.skip(n);
		return this;
	}

	@Override
	public void forEach(Consumer<? super T> action) {
		stream.forEach(action);
	}

	@Override
	public void forEachOrdered(Consumer<? super T> action) {
		stream.forEachOrdered(action);
	}

	@Override
	public Object[] toArray() {
		return stream.toArray();
	}

	@Override
	public <A> A[] toArray(IntFunction<A[]> generator) {
		return stream.toArray(generator);
	}

	@Override
	public T reduce(T identity, BinaryOperator<T> accumulator) {
		return stream.reduce(identity, accumulator);
	}

	@Override
	public Optional<T> reduce(BinaryOperator<T> accumulator) {
		return stream.reduce(accumulator);
	}

	@Override
	public <U> U reduce(U identity, BiFunction<U, ? super T, U> accumulator, BinaryOperator<U> combiner) {
		return stream.reduce(identity, accumulator, combiner);
	}

	@Override
	public <R> R collect(Supplier<R> supplier, BiConsumer<R, ? super T> accumulator, BiConsumer<R, R> combiner) {
		return stream.collect(supplier, accumulator, combiner);
	}

	@Override
	public <R, A> R collect(Collector<? super T, A, R> collector) {
		return stream.collect(collector);
	}

	@Override
	public Optional<T> min(Comparator<? super T> comparator) {
		return stream.min(comparator);
	}

	@Override
	public Optional<T> max(Comparator<? super T> comparator) {
		return stream.max(comparator);
	}

	@Override
	public long count() {
		return stream.count();
	}

	@Override
	public boolean anyMatch(Predicate<? super T> predicate) {
		return stream.anyMatch(predicate);
	}

	@Override
	public boolean allMatch(Predicate<? super T> predicate) {
		return stream.allMatch(predicate);
	}

	@Override
	public boolean noneMatch(Predicate<? super T> predicate) {
		return stream.noneMatch(predicate);
	}

	@Override
	public Optional<T> findFirst() {
		return stream.findFirst();
	}

	@Override
	public Optional<T> findAny() {
		return stream.findAny();
	}
}
