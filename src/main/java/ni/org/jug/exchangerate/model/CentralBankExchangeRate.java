package ni.org.jug.exchangerate.model;

import com.fasterxml.jackson.annotation.JsonValue;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author aalaniz
 */
@Entity
@Table(name = "ncb_exchange_rate")
public class CentralBankExchangeRate extends Identifier<Integer> {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "currency_id")
    private Currency currency;

    @NotNull
    @Column(name = "exchange_rate_date")
    private LocalDate date;

    @NotNull
    @Column(name = "exchange_rate_amount")
    private BigDecimal amount;

    @Embedded
    private AuditTrail auditTrail = new AuditTrail();

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public AuditTrail getAuditTrail() {
        return auditTrail;
    }

    public void setAuditTrail(AuditTrail auditTrail) {
        this.auditTrail = auditTrail;
    }

    public void calculateId() {
        if (date == null) {
            throw new IllegalStateException("La [fecha] es un dato requerido para calcular el ID");
        }
        id = calculateId(date);
    }

    @JsonValue
    public Map<String, Object> asMap() {
        Map<String, Object> dto = new HashMap<>();
        dto.put("id", id);
        dto.put("currency", currency.getShortDescriptionAndSymbol());
        dto.put("date", date);
        dto.put("amount", amount);
        dto.put("createdOn", auditTrail.getCreatedOn());
        dto.put("updatedOn", auditTrail.getUpdatedOn());
        return dto;
    }

    public static Integer calculateId(int year, int month, int day) {
        Integer id = (year*100 + month)*100 + day;
        return id;
    }

    public static Integer calculateId(LocalDate date) {
        int id = (date.getYear()*100 + date.getMonthValue())*100 + date.getDayOfMonth();
        return id;
    }

    @Override
    public String toString() {
        return "CentralBankExchangeRate{" +
                "currency=" + currency +
                ", date=" + date +
                ", amount=" + amount +
                '}';
    }
}
