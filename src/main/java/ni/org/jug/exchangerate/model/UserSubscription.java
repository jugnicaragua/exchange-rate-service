package ni.org.jug.exchangerate.model;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author aalaniz
 */
@Entity
@Table(name = "user_subscription")
@SequenceGenerator(name = "seq", sequenceName = "user_subscription_id_seq", allocationSize = 1)
public class UserSubscription extends IntegerSerialIdentifier {

    @NotEmpty
    @Size(min = 3, max = 250)
    @Column(name = "full_name")
    private String fullName;

    @NotEmpty
    @Size(min = 6, max = 100)
    @Column(name = "email")
    private String email;

    @NotEmpty
    @Size(min = 1, max = 100)
    @Column(name = "token")
    private String token;

    @NotNull
    @Column(name = "is_active")
    private Boolean active;

    @Embedded
    private AuditTrail auditTrail = new AuditTrail();

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public AuditTrail getAuditTrail() {
        return auditTrail;
    }

    public void setAuditTrail(AuditTrail auditTrail) {
        this.auditTrail = auditTrail;
    }

    @Override
    public String toString() {
        return "UserSubscription{" +
                "fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", active=" + active +
                '}';
    }
}
