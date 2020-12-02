package ni.org.jug.exchangerate.model;

import com.fasterxml.jackson.annotation.JsonValue;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 *
 * @author aalaniz
 */
@Entity
@Table(name = "bank")
@SequenceGenerator(name = "seq", sequenceName = "bank_id_seq", allocationSize = 1)
public class Bank extends IntegerSerialIdentifier {

    @Valid
    @Embedded
    private Description description = new Description();

    @NotEmpty
    @Size(min = 1, max = 200)
    @Column(name = "url")
    private String url;

    @NotNull
    @Column(name = "is_active")
    private Boolean active;

    @Embedded
    private AuditTrail auditTrail = new AuditTrail();

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "bank_id", nullable = false)
    private Set<Cookie> cookies = new HashSet<>();

    public Description getDescription() {
        return description;
    }

    public void setDescription(Description description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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

    public Set<Cookie> getCookies() {
        return cookies;
    }

    public void setCookies(Set<Cookie> cookies) {
        this.cookies = cookies;
    }

    public void addCookie(Cookie cookie) {
        this.cookies.add(cookie);
    }

    public void removeCookie(Cookie cookie) {
        this.cookies.remove(cookie);
    }

    public Optional<Cookie> getCookie(Integer id) {
        Objects.requireNonNull(id);
        return cookies.stream().filter(cookie -> id.equals(cookie.getId())).findFirst();
    }

    public Optional<Cookie> getCookie(String name) {
        Objects.requireNonNull(name);
        return cookies.stream().filter(cookie -> cookie.getName().equals(name)).findFirst();
    }

    @JsonValue
    public Map<String, Object> asMap() {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("id", id);
        dto.put("shortDescription", description.getShortDescription());
        dto.put("description", description.getDescription());
        dto.put("url", url);
        dto.put("active", active);
        dto.put("createdOn", auditTrail.getCreatedOn());
        dto.put("updatedOn", auditTrail.getUpdatedOn());
        dto.put("cookies", cookies);
        return dto;
    }

    @Override
    public String toString() {
        return "Bank{" +
                "description=" + description +
                ", active=" + active +
                '}';
    }
}
