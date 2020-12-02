package ni.org.jug.exchangerate.model;

import com.fasterxml.jackson.annotation.JsonValue;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * @author aalaniz
 */
@Entity
@Table(name = "cookie")
@SequenceGenerator(name = "seq", sequenceName = "cookie_id_seq", allocationSize = 1)
public class Cookie extends IntegerSerialIdentifier {

    @NotEmpty
    @Size(min = 3, max = 50)
    @Column(name = "name")
    private String name;

    @NotEmpty
    @Size(min = 3, max = 150)
    @Column(name = "value")
    private String value;

    @Embedded
    private AuditTrail auditTrail = new AuditTrail();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public AuditTrail getAuditTrail() {
        return auditTrail;
    }

    public void setAuditTrail(AuditTrail auditTrail) {
        this.auditTrail = auditTrail;
    }

    @JsonValue
    public Map<String, Object> asMap() {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("id", id);
        dto.put("name", name);
        dto.put("value", value);
        dto.put("createdOn", auditTrail.getCreatedOn());
        dto.put("updatedOn", auditTrail.getUpdatedOn());
        return dto;
    }

    @Override
    public String toString() {
        return "Cookie{" +
                "name='" + name + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
