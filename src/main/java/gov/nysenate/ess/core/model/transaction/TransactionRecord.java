package gov.nysenate.ess.core.model.transaction;

import com.google.common.collect.ComparisonChain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * A TransactionRecord represents a single unit of change that was made to an
 * employee's personnel or payroll data and is identified by a TransactionCode.
 * A map of the values affected in the database are stored in this record.
 */
public class TransactionRecord extends TransactionInfo implements Comparable<TransactionRecord>
{
    private static final Logger logger = LoggerFactory.getLogger(TransactionRecord.class);

    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /** A mapping of the transaction's database column names to their values. */
    protected Map<String, String> valueMap;

    /** The date the actual audit record was created. */
    protected LocalDateTime auditDate;

    /** The date when this audit record was last updated */
    protected LocalDateTime auditUpdateDate;

    /** A note that is often associated with the record. */
    protected String note;

    /** --- Constructors --- */

    public TransactionRecord() {}

    public TransactionRecord(TransactionInfo info) {
        super(info);
    }

    /** --- Functional Getters/Setters --- */

    public boolean hasValues() {
        return valueMap != null && !valueMap.isEmpty();
    }

    /**
     * Delegate to retrieve the value associated with the given column name.
     * @param colName String
     * @return String if value exists, null if it doesn't or is set as null,
     *         or throws IllegalStateException if the value map was not initialized.
     */
    public String getValue(String colName) {
        if (valueMap != null) {
            return valueMap.get(colName);
        }
        throw new IllegalStateException("The value map for the transaction record was not set.");
    }

    /**
     * Checks if the map has a non null value for the given column name.
     * @param colName String
     * @return boolean
     */
    public boolean hasNonNullValue(String colName) {
        if (valueMap != null) {
            return (valueMap.containsKey(colName) && valueMap.get(colName) != null);
        }
        throw new IllegalStateException("The value map for the transaction record was not set.");
    }

    /**
     * Returns the value of the given column name, parsed into a LocalDate
     * @param colName String
     * @return LocalDate
     * @throws java.time.format.DateTimeParseException if the column value cannot be parsed into a date
     */
    public LocalDate getLocalDateValue(String colName) {
        String dateString = getValue(colName);
        return dateString != null ? LocalDate.parse(dateString, dateFormatter) : null;
    }

    /**
     * Returns the value of the given column name, parsed into a big decimal
     * @param colName String
     * @param returnZero boolean - if this is true, 0 will be returned when the column value is null, otherwise null
     * @return BigDecimal
     * @throws NumberFormatException if the value of the column cannot be parsed into a BigDecimal
     */
    public BigDecimal getBigDecimalValue(String colName, boolean returnZero) {
        String numString = getValue(colName);
        if (numString == null) {
            return returnZero ? BigDecimal.ZERO : null;
        }
        return new BigDecimal(numString);
    }
    public BigDecimal getBigDecimalValue(String colName) {
        return getBigDecimalValue(colName, true);
    }

    /**
     * @return Map<String, String> - a map containing only column names -> values that are explicitly set by this transaction's code
     */
    public Map<String, String> getValuesForCode() {
        return getValuesForCols(transCode.getDbColumnList());
    }

    /**
     * Get a map of column names -> values for the given column names
     * @param colNames Collection<String>
     * @return Map<String, String>
     */
    public Map<String, String> getValuesForCols(Set<String> colNames) {
        Map<String, String> subValueMap = new HashMap<>();
        colNames.stream()
                .filter(valueMap::containsKey)
                .forEach(colName -> subValueMap.put(colName, valueMap.get(colName)));
        return subValueMap;
    }

    /** --- Overridden Methods --- */

    @Override
    public int compareTo(TransactionRecord o) {
        return ComparisonChain.start()
                .compare(this.effectDate, o.effectDate)
                .compare(this.originalDate, o.originalDate)
                .result();
    }

    /** --- Basic Getters/Setters --- */


    public Map<String, String> getValueMap() {
        return valueMap;
    }

    public void setValueMap(Map<String, String> valueMap) {
        this.valueMap = valueMap;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public LocalDateTime getAuditDate() {
        return auditDate;
    }

    public void setAuditDate(LocalDateTime auditDate) {
        this.auditDate = auditDate;
    }

    public LocalDateTime getAuditUpdateDate() {
        return auditUpdateDate;
    }

    public void setAuditUpdateDate(LocalDateTime auditUpdateDate) {
        this.auditUpdateDate = auditUpdateDate;
    }
}