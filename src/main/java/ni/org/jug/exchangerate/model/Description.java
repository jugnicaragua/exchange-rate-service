package ni.org.jug.exchangerate.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

/**
 *
 * @author aalaniz
 */
@Embeddable
public class Description {

    @NotEmpty
    @Size(min = 3, max = 20)
    @Column(name = "short_description")
    private String shortDescription;

    @NotEmpty
    @Size(min = 3, max = 50)
    @Column(name = "description")
    private String description;

    public Description() {
    }

    public Description(String shortDescription, String description) {
        this.shortDescription = shortDescription;
        this.description = description;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Description{" +
                "shortDescription='" + shortDescription + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
