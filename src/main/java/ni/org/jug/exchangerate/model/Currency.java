package ni.org.jug.exchangerate.model;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.Valid;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author aalaniz
 */
@Entity
@Table(name = "currency")
@SequenceGenerator(name = "seq", sequenceName = "currency_id_seq", allocationSize = 1)
public class Currency extends IntegerSerialIdentifier {

    @NotNull
    @Digits(integer = 3, fraction = 0)
    @Size(min = 3, max = 3)
    @Column(name = "iso_numeric_code")
    private String isoNumericCode;

    @NotNull
    @Size(min = 3, max = 3)
    @Column(name = "iso_string_code")
    private String isoStringCode;

    @NotNull
    @Size(min = 1, max = 5)
    @Column(name = "symbol")
    private String symbol;

    @Valid
    @Embedded
    private Description description = new Description();

    @NotNull
    @Column(name = "is_domestic")
    private Boolean domestic;

    @NotNull
    @Column(name = "is_active")
    private Boolean active;

    @Embedded
    private AuditTrail auditTrail = new AuditTrail();

    public String getIsoNumericCode() {
        return isoNumericCode;
    }

    public void setIsoNumericCode(String isoNumericCode) {
        this.isoNumericCode = isoNumericCode;
    }

    public String getIsoStringCode() {
        return isoStringCode;
    }

    public void setIsoStringCode(String isoStringCode) {
        this.isoStringCode = isoStringCode;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public Description getDescription() {
        return description;
    }

    public void setDescription(Description description) {
        this.description = description;
    }

    public Boolean getDomestic() {
        return domestic;
    }

    public void setDomestic(Boolean domestic) {
        this.domestic = domestic;
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

    public String getShortDescriptionAndSymbol() {
        StringBuilder desc = new StringBuilder(description.getShortDescription());
        desc.append(" (");
        desc.append(symbol);
        desc.append(')');
        return desc.toString();
    }

    @Override
    public String toString() {
        return "Currency{" +
                "isoNumericCode='" + isoNumericCode + '\'' +
                ", isoStringCode='" + isoStringCode + '\'' +
                ", symbol='" + symbol + '\'' +
                ", description=" + description +
                ", domestic=" + domestic +
                ", active=" + active +
                '}';
    }
}
