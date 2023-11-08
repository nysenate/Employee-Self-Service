package gov.nysenate.ess.supply.requisition.model;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableSet;
import gov.nysenate.ess.core.util.LimitOffset;
import gov.nysenate.ess.core.util.OrderBy;
import gov.nysenate.ess.core.util.SortOrder;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;
import java.util.EnumSet;

public class RequisitionQuery {

    private static final String WILDCARD = "%";
    private static final ImmutableCollection<String> DATE_FIELDS = ImmutableSet.of(
            "ordered_date_time", "processed_date_time", "completed_date_time",
            "approved_date_time", "rejected_date_time");
    private String destination;
    private String customerId;
    private EnumSet<RequisitionStatus> statuses;
    private LocalDateTime fromDateTime;
    private LocalDateTime toDateTime;
    private String dateField;
    private String savedInSfms;
    private String issuerId;
    private String itemId;
    private LimitOffset limitOffset;
    private OrderBy orderBy;
    private String reconciled;

    public RequisitionQuery() {
        // Set default values
        this.destination = WILDCARD;
        this.customerId = WILDCARD;
        this.statuses = EnumSet.allOf(RequisitionStatus.class);
        this.fromDateTime = LocalDateTime.now().minusMonths(1);
        this.toDateTime = LocalDateTime.now();
        this.dateField = "ordered_date_time";
        this.savedInSfms = WILDCARD;
        this.issuerId = WILDCARD;
        this.itemId = WILDCARD;
        this.limitOffset = LimitOffset.TEN;
        this.orderBy = new OrderBy(this.dateField, SortOrder.DESC);
        this.reconciled = WILDCARD;
    }

    /**
     * Converts "All" to "%".
     *
     * This is a temporary fix, until {@code RequisitionRestApiCtrl.searchRequisitions}
     * stops using 'All' params.
     */
    private String useWildcard(String param) {
        if (param.equals("All")) {
            return WILDCARD;
        }
        return param;
    }

    public RequisitionQuery setDestination(String destination) {
        this.destination = destination;
        return this;
    }

    public RequisitionQuery setCustomerId(int customerId) {
        this.customerId = String.valueOf(customerId);
        return this;
    }

    public RequisitionQuery setCustomerId(String customerId) {
        this.customerId = customerId;
        return this;
    }

    public RequisitionQuery setStatuses(EnumSet<RequisitionStatus> statuses) {
        this.statuses = statuses;
        return this;
    }

    public RequisitionQuery setFromDateTime(LocalDateTime fromDateTime) {
        this.fromDateTime = fromDateTime;
        return this;
    }

    public RequisitionQuery setToDateTime(LocalDateTime toDateTime) {
        this.toDateTime = toDateTime;
        return this;
    }

    /**
     * Sets the dateField. Must be a valid date column in the requisition table,
     * throws a {@code IllegalArgumentException} if not.
     * @param dateField a date column which is filtered by from/to date time.
     */
    public RequisitionQuery setDateField(String dateField) {
        if (DATE_FIELDS.contains(dateField)) {
            this.dateField = dateField;
        }
        else {
            throw new IllegalArgumentException("datefield " + dateField +
                    " is not valid. Valid options are " + DATE_FIELDS);
        }
        return this;
    }

    public RequisitionQuery setSavedInSfms(boolean savedInSfms) {
        this.savedInSfms = String.valueOf(savedInSfms);
        return this;
    }

    public RequisitionQuery setSavedInSfms(String savedInSfms) {
        this.savedInSfms = savedInSfms;
        return this;
    }

    public RequisitionQuery setIssuerId(String issuerId) {
        this.issuerId = issuerId;
        return this;
    }

    public RequisitionQuery setItemId(String itemId) {
        this.itemId = itemId;
        return this;
    }

    public RequisitionQuery setLimitOffset(LimitOffset limitOffset) {
        this.limitOffset = limitOffset;
        return this;
    }

    public RequisitionQuery setOrderBy(OrderBy orderBy) {
        this.orderBy = orderBy;
        return this;
    }

    public RequisitionQuery setReconciled(String reconciled) {
        if(reconciled != null){
            if(reconciled.equals("t") || StringUtils.equalsIgnoreCase(reconciled, "true")){
                this.reconciled = "true";
            }
            if(reconciled.equals("f")|| StringUtils.equalsIgnoreCase(reconciled, "false")){
                this.reconciled = "false";
            }
        }
        return this;
    }

    public String getDestination() {
        return useWildcard(destination);
    }

    public String getCustomerId() {
        return useWildcard(customerId);
    }

    public EnumSet<RequisitionStatus> getStatuses() {
        return statuses;
    }

    public LocalDateTime getFromDateTime() {
        return fromDateTime;
    }

    public LocalDateTime getToDateTime() {
        return toDateTime;
    }

    public String getDateField() {
        return dateField;
    }

    public String getSavedInSfms() {
        return useWildcard(savedInSfms);
    }

    public String getIssuerId() {
        return useWildcard(issuerId);
    }

    public String getItemId() {
        return useWildcard(itemId);
    }

    public LimitOffset getLimitOffset() {
        return limitOffset;
    }

    public OrderBy getOrderBy() {
        return orderBy;
    }

    public String getReconciled() {
        return reconciled;
    }
}
