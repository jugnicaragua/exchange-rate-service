package ni.org.jug.exchangerate.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDateTime;

/**
 *
 * @author aalaniz
 */
@Entity
@Table(name = "email_task")
@SequenceGenerator(name = "seq", sequenceName = "email_task_id_seq", allocationSize = 1)
public class EmailTask extends IntegerSerialIdentifier {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_subscription_id")
    private UserSubscription userSubscription;

    @NotNull
    @PastOrPresent
    @Column(name = "date")
    private LocalDateTime date;

    public UserSubscription getUserSubscription() {
        return userSubscription;
    }

    public void setUserSubscription(UserSubscription userSubscription) {
        this.userSubscription = userSubscription;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "EmailTask{" +
                "userSubscription=" + userSubscription +
                ", date=" + date +
                '}';
    }
}
