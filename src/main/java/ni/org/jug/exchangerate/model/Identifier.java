package ni.org.jug.exchangerate.model;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotNull;
import java.util.Objects;

/**
 *
 * @author aalaniz
 */
@MappedSuperclass
public abstract class Identifier<T> {

    @NotNull
    @Id
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
        if (!(obj instanceof Identifier)) {
            return false;
        }
        Identifier<?> other = (Identifier<?>) obj;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
