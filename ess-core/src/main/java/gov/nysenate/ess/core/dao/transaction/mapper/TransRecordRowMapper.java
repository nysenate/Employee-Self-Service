package gov.nysenate.ess.core.dao.transaction.mapper;

import gov.nysenate.ess.core.dao.transaction.EmpTransDaoOption;
import gov.nysenate.ess.core.model.transaction.TransactionCode;
import gov.nysenate.ess.core.model.transaction.TransactionRecord;
import gov.nysenate.ess.core.model.transaction.TransactionType;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static gov.nysenate.ess.core.dao.base.BaseMapper.getLocalDateTimeFromRs;

public class TransRecordRowMapper implements RowMapper<TransactionRecord>
{
    /** Prefix for the columns */
    protected String pfx = "";

    /** Prefix for the audit columns, i.e. those that will be used in the record's value map. */
    protected String auditPfx = "";

    /** Options to indicate certain processing behaviors. */
    protected EmpTransDaoOption options;

    /** A RowMapper to extract the transaction metadata */
    protected TransInfoRowMapper infoRowMapper;

    public TransRecordRowMapper(String pfx, String auditPfx, EmpTransDaoOption options) {
        this.pfx = pfx;
        this.auditPfx = auditPfx;
        this.options = options;
        this.infoRowMapper = new TransInfoRowMapper(pfx);
    }

    @Override
    public TransactionRecord mapRow(ResultSet rs, int i) throws SQLException {
        TransactionRecord transRec = new TransactionRecord(infoRowMapper.mapRow(rs, i));
        transRec.setAuditDate(getLocalDateTimeFromRs(rs, auditPfx + "DTTXNORIGIN"));
        transRec.setAuditUpdateDate(getLocalDateTimeFromRs(rs, auditPfx + "DTTXNUPDATE"));
        transRec.setNote((transRec.getTransCode().getType().equals(TransactionType.PER))
                ? rs.getString(pfx + "DETXNNOTE50") : rs.getString(pfx + "DETXNNOTEPAY"));

        /**
         * The value map will contain the column -> value mappings for the db columns associated with the
         * transaction code. The appointment transactions (APP/RTP) will have value maps containing every column
         * since they represent the initial snapshot of the data.
         */
        Map<String, String> valueMap = new HashMap<>();
        Set<String> columns = (transRec.getTransCode().isAppointType() || (options.shouldInitialize() && rs.isFirst()))
                ? TransactionCode.getAllDbColumnsList()
                : TransactionCode.getTypeDbColumnsList(transRec.getTransCode().getType());
        for (String col : columns) {
            valueMap.put(col.trim(), rs.getString(col.trim()));
        }
        transRec.setValueMap(valueMap);
        return transRec;
    }
}
