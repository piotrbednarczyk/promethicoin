package util;

import java.util.Iterator;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Created by Piotr Bednarczyk on 2017-03-05.
 */
public class IteratorToStream<T> implements Function<Iterator<T>, Stream<T>> {

    @Override
    public Stream<T> apply(Iterator<T> iterator) {
        Iterable<T> iterable = () -> iterator;
        return StreamSupport.stream(iterable.spliterator(), false);
    }
}
