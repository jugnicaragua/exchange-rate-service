package ni.org.jug.exchangerate.model;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.util.Objects;

/**
 *
 * @author aalaniz
 */
@MappedSuperclass
public abstract class SerialIdentifier<T extends Number> {

    @Id
    @GeneratedValue(generator = "seq", strategy = GenerationType.SEQUENCE)
    protected T id;

    public T getId() {
        return id;
    }

    public void setId(T id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof SerialIdentifier)) {
            return false;
        }
        SerialIdentifier<?> other = (SerialIdentifier<?>) obj;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
