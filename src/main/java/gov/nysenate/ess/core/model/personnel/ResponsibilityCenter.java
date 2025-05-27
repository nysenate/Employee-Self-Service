package gov.nysenate.ess.core.model.personnel;

import com.google.common.base.Objects;

import java.time.LocalDate;
import java.util.Optional;

/**
 * A responsibility center is basically an organizational unit that is headed by a manager.
 * The ResponsibilityCenter is primarily defined by the agency and resp center head codes and
 * therefore contains those objects as well.
 */
public class ResponsibilityCenter
{
    protected boolean active;
    protected int code;
    protected String name;
    protected LocalDate effectiveDateBegin;
    protected LocalDate effectiveDateEnd;
    protected Agency agency;
    protected ResponsibilityHead head;

    public ResponsibilityCenter() {}

    public ResponsibilityCenter(ResponsibilityCenter other) {
        this.active = other.active;
        this.code = other.code;
        this.name = other.name;
        this.effectiveDateBegin = other.effectiveDateBegin;
        this.effectiveDateEnd = other.effectiveDateEnd;
        this.agency = Optional.ofNullable(other.agency)
                .map(Agency::new)
                .orElse(null);
        this.head = Optional.ofNullable(other.head)
                .map(ResponsibilityHead::new)
                .orElse(null);
    }

    public boolean isAdministrativeOffice() {
        int firstDigit = Integer.parseInt(String.valueOf(this.code).substring(0, 1));
        return firstDigit == 3;
    }

    /* --- Overridden Methods --- */

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ResponsibilityCenter)) return false;
        ResponsibilityCenter that = (ResponsibilityCenter) o;
        return active == that.active &&
                code == that.code &&
                Objects.equal(name, that.name) &&
                Objects.equal(effectiveDateBegin, that.effectiveDateBegin) &&
                Objects.equal(effectiveDateEnd, that.effectiveDateEnd) &&
                Objects.equal(agency, that.agency) &&
                Objects.equal(head, that.head);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(active, code, name, effectiveDateBegin, effectiveDateEnd, agency, head);
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getEffectiveDateBegin() {
        return effectiveDateBegin;
    }

    public void setEffectiveDateBegin(LocalDate effectiveDateBegin) {
        this.effectiveDateBegin = effectiveDateBegin;
    }

    public LocalDate getEffectiveDateEnd() {
        return effectiveDateEnd;
    }

    public void setEffectiveDateEnd(LocalDate effectiveDateEnd) {
        this.effectiveDateEnd = effectiveDateEnd;
    }

    public Agency getAgency() {
        return agency;
    }

    public void setAgency(Agency agency) {
        this.agency = agency;
    }

    public ResponsibilityHead getHead() {
        return head;
    }

    public void setHead(ResponsibilityHead head) {
        this.head = head;
    }
}