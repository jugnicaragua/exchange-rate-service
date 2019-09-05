package ni.org.jug.exchangerate.rest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 *
 * @author aalaniz
 */
public class Response {

    private final int size;
    private final Collection data;

    public Response(Collection data) {
        Objects.requireNonNull(data);
        this.size = data.size();
        this.data = data;
    }

    public Response(Iterable iterable) {
        Objects.requireNonNull(iterable);
        this.data = (Collection) StreamSupport.stream(iterable.spliterator(), false)
                .collect(Collectors.toCollection(ArrayList::new));
        this.size = this.data.size();
    }

    public int getSize() {
        return size;
    }

    public Collection getData() {
        return data;
    }

}
