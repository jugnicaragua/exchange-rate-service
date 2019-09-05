package ni.org.jug.exchangerate.model;

import com.fasterxml.jackson.annotation.JsonValue;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author aalaniz
 */
@Entity
@Table(name = "cb_exchange_rate")
@SequenceGenerator(name = "seq", sequenceName = "cb_exchange_rate_id_seq", allocationSize = 1)
public class CommercialBankExchangeRate extends IntegerSerialIdentifier {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "currency_id")
    private Currency currency;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "bank_id")
    private Bank bank;

    @NotNull
    @PastOrPresent
    @Column(name = "exchange_rate_date")
    private LocalDate date;

    @NotNull
    @Column(name = "sell")
    private BigDecimal sell;

    @NotNull
    @Column(name = "buy")
    private BigDecimal buy;

    @NotNull
    @Column(name = "is_best_sell_price")
    private Boolean bestSellPrice;

    @NotNull
    @Column(name = "is_best_buy_price")
    private Boolean bestBuyPrice;

    @Embedded
    private AuditTrail auditTrail = new AuditTrail();

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public Bank getBank() {
        return bank;
    }

    public void setBank(Bank bank) {
        this.bank = bank;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public BigDecimal getSell() {
        return sell;
    }

    public void setSell(BigDecimal sell) {
        this.sell = sell;
    }

    public BigDecimal getBuy() {
        return buy;
    }

    public void setBuy(BigDecimal buy) {
        this.buy = buy;
    }

    public Boolean getBestSellPrice() {
        return bestSellPrice;
    }

    public void setBestSellPrice(Boolean bestSellPrice) {
        this.bestSellPrice = bestSellPrice;
    }

    public Boolean getBestBuyPrice() {
        return bestBuyPrice;
    }

    public void setBestBuyPrice(Boolean bestBuyPrice) {
        this.bestBuyPrice = bestBuyPrice;
    }

    public AuditTrail getAuditTrail() {
        return auditTrail;
    }

    public void setAuditTrail(AuditTrail auditTrail) {
        this.auditTrail = auditTrail;
    }

    @JsonValue
    public Map<String, Object> asMap() {
        Map<String, Object> dto = new HashMap<>();
        dto.put("id", id);
        dto.put("currency", currency.getShortDescriptionAndSymbol());
        dto.put("bank", bank.getDescription().getShortDescription());
        dto.put("date", date);
        dto.put("sell", sell);
        dto.put("buy", buy);
        dto.put("bestSellPrice", bestSellPrice);
        dto.put("bestBuyPrice", bestBuyPrice);
        dto.put("createdOn", auditTrail.getCreatedOn());
        dto.put("updatedOn", auditTrail.getUpdatedOn());
        return dto;
    }

    @Override
    public String toString() {
        return "CommercialBankExchangeRate{" +
                "currency=" + currency +
                ", bank=" + bank +
                ", date=" + date +
                ", sell=" + sell +
                ", buy=" + buy +
                ", bestSellPrice=" + bestSellPrice +
                ", bestBuyPrice=" + bestBuyPrice +
                '}';
    }
}
