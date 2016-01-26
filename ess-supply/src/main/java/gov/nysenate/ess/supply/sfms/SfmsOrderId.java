package gov.nysenate.ess.supply.sfms;

import java.time.LocalDate;

/**
 * Set of fields that uniquely identifies an SfmsOrder.
 */
public class SfmsOrderId {

    private final int nuIssue;
    private final LocalDate issueDate;
    private final String toLocationCode;
    private final String toLocationType;

    public SfmsOrderId(int nuIssue, LocalDate issueDate, String locationCode, String locationType) {
        this.nuIssue = nuIssue;
        this.issueDate = issueDate;
        this.toLocationCode = locationCode;
        this.toLocationType = locationType;
    }

    public int getNuIssue() {
        return nuIssue;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public String getToLocationCode() {
        return toLocationCode;
    }

    public String getToLocationType() {
        return toLocationType;
    }

    @Override
    public String toString() {
        return "SfmsOrderId{" +
               "nuIssue=" + nuIssue +
               ", issueDate=" + issueDate +
               ", toLocationCode='" + toLocationCode + '\'' +
               ", toLocationType='" + toLocationType + '\'' +
               '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SfmsOrderId that = (SfmsOrderId) o;

        if (nuIssue != that.nuIssue) return false;
        if (issueDate != null ? !issueDate.equals(that.issueDate) : that.issueDate != null) return false;
        if (toLocationCode != null ? !toLocationCode.equals(that.toLocationCode) : that.toLocationCode != null) return false;
        return !(toLocationType != null ? !toLocationType.equals(that.toLocationType) : that.toLocationType != null);
    }

    @Override
    public int hashCode() {
        int result = nuIssue;
        result = 31 * result + (issueDate != null ? issueDate.hashCode() : 0);
        result = 31 * result + (toLocationCode != null ? toLocationCode.hashCode() : 0);
        result = 31 * result + (toLocationType != null ? toLocationType.hashCode() : 0);
        return result;
    }
}
