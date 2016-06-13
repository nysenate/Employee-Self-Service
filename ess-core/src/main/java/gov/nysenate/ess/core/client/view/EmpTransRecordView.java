package gov.nysenate.ess.core.client.view;

import com.ctc.wstx.util.StringUtil;
import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.core.model.transaction.TransactionColumn;
import gov.nysenate.ess.core.model.transaction.TransactionRecord;
import org.apache.commons.lang3.StringUtils;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

@XmlRootElement
public class EmpTransRecordView implements ViewObject
{
    protected int employeeId;
    protected boolean active;
    protected String transCode;
    protected String transDesc;
    protected String transType;
    protected Map<String, EmpTransItemView> values;
    protected LocalDate effectDate;
    protected LocalDateTime originalDate;
    protected LocalDateTime updateDate;
    protected String note;

    public EmpTransRecordView(TransactionRecord record, boolean restrictValues) {
        if (record != null) {
            this.employeeId = record.getEmployeeId();
            this.active = record.isActive();
            this.transCode = record.getTransCode().name();
            this.transDesc = record.getTransCode().getDesc();
            this.transType = Objects.toString(record.getTransType());
            this.values = new TreeMap<>();
            record.getValueMap().entrySet().stream()
                    .filter(entry -> !restrictValues ||
                                    record.getTransCode().getDbColumnList().contains(entry.getKey()))
                    .filter(entry -> TransactionColumn.isValidColumn(entry.getKey()))
                    .filter(entry -> StringUtils.isNotBlank(entry.getValue()))
                    .forEach(entry -> this.values.put(entry.getKey(),
                                    new EmpTransItemView(entry.getKey(), entry.getValue())));
            this.effectDate = record.getEffectDate();
            this.originalDate = record.getOriginalDate();
            this.updateDate = record.getUpdateDate();
            this.note = record.getNote();
        }
    }

    public EmpTransRecordView(TransactionRecord record) {
        this(record, false);
    }

    @Override
    public String getViewType() {
        return "emp-transaction-record";
    }

    @XmlElement
    public int getEmployeeId() {
        return employeeId;
    }

    @XmlElement
    public boolean isActive() {
        return active;
    }

    @XmlElement
    public String getTransCode() {
        return transCode;
    }

    @XmlElement
    public String getTransDesc() {
        return transDesc;
    }

    @XmlElement
    public String getTransType() {
        return transType;
    }

    @XmlElement
    public Map<String, EmpTransItemView> getValues() {
        return values;
    }

    @XmlElement
    public LocalDate getEffectDate() {
        return effectDate;
    }

    @XmlElement
    public LocalDateTime getOriginalDate() {
        return originalDate;
    }

    @XmlElement
    public LocalDateTime getUpdateDate() {
        return updateDate;
    }

    @XmlElement
    public String getNote() {
        return note;
    }
}
