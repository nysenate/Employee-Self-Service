package gov.nysenate.ess.supply.sfms;

import gov.nysenate.ess.supply.item.LineItem;
import gov.nysenate.ess.supply.order.Order;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents the order structure from the Sfms database.
 * Should only be used to verify Sfms data.
 */
public class SfmsOrder {

    private int nuIssue;
    private LocalDate issueDate;
    private String locCode;
    private String locType;
    private String issuedBy;
    private Set<LineItem> items = new HashSet<>();

    public static SfmsOrder fromOrder(Order order) {
        SfmsOrder sfmsOrder = new SfmsOrder();
        sfmsOrder.setIssueDate(order.getCompletedDateTime().toLocalDate());
        sfmsOrder.setLocCode(order.getLocation().getCode());
        sfmsOrder.setLocType(String.valueOf(order.getLocation().getType().getCode()));
        sfmsOrder.setIssuedBy(order.getIssuingEmployee().getLastName());
        sfmsOrder.setItems(order.getItems());
        return sfmsOrder;
    }

    public int getNuIssue() {
        return nuIssue;
    }

    public void setNuIssue(int nuIssue) {
        this.nuIssue = nuIssue;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(LocalDate issueDate) {
        this.issueDate = issueDate;
    }

    public String getLocCode() {
        return locCode;
    }

    public void setLocCode(String locCode) {
        this.locCode = locCode;
    }

    public String getLocType() {
        return locType;
    }

    public void setLocType(String locType) {
        this.locType = locType;
    }

    public String getIssuedBy() {
        return issuedBy;
    }

    public void setIssuedBy(String issuedBy) {
        this.issuedBy = issuedBy;
    }

    public Set<LineItem> getItems() {
        return items;
    }

    public void setItems(Set<LineItem> items) {
        this.items = items;
    }

    public void addItem(LineItem item) {
        this.items.add(item);
    }

    /**
     * nuIssue, issueDate, locCode, and LocType define a unique order in the sfms table.
     * @return
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SfmsOrder sfmsOrder = (SfmsOrder) o;

        if (nuIssue != sfmsOrder.nuIssue) return false;
        if (issueDate != null ? !issueDate.equals(sfmsOrder.issueDate) : sfmsOrder.issueDate != null) return false;
        if (locCode != null ? !locCode.equals(sfmsOrder.locCode) : sfmsOrder.locCode != null) return false;
        return !(locType != null ? !locType.equals(sfmsOrder.locType) : sfmsOrder.locType != null);

    }

    @Override
    public int hashCode() {
        int result = nuIssue;
        result = 31 * result + (issueDate != null ? issueDate.hashCode() : 0);
        result = 31 * result + (locCode != null ? locCode.hashCode() : 0);
        result = 31 * result + (locType != null ? locType.hashCode() : 0);
        return result;
    }
}
