package ni.org.jug.exchangerate.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDateTime;

/**
 *
 * @author aalaniz
 */
@Embeddable
public class AuditTrail {

    @NotNull
    @PastOrPresent
    @Column(name = "created_on", updatable = false)
    private LocalDateTime createdOn;

    @PastOrPresent
    @Column(name = "updated_on", insertable = false)
    private LocalDateTime updatedOn;

    public AuditTrail() {
    }

    public LocalDateTime getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(LocalDateTime createdOn) {
        this.createdOn = createdOn;
    }

    public LocalDateTime getUpdatedOn() {
        return updatedOn;
    }

    public void setUpdatedOn(LocalDateTime updatedOn) {
        this.updatedOn = updatedOn;
    }

    @PrePersist
    public void onPrePersist() {
        createdOn = LocalDateTime.now();
    }

    @PreUpdate
    public void onPreUpdate() {
        updatedOn = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "AuditTrail{" +
                "createdOn=" + createdOn +
                ", updatedOn=" + updatedOn +
                '}';
    }
}
