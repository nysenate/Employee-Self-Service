package gov.nysenate.ess.core.dao.transaction.mapper;

import gov.nysenate.ess.core.dao.base.BaseHandler;
import gov.nysenate.ess.core.dao.transaction.EmpTransDaoOption;
import gov.nysenate.ess.core.model.transaction.TransactionCode;
import gov.nysenate.ess.core.model.transaction.TransactionHistory;
import gov.nysenate.ess.core.model.transaction.TransactionRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class TransHistoryHandler extends BaseHandler
{
    private static final Logger logger = LoggerFactory.getLogger(TransHistoryHandler.class);

    /** Keep a reference of the emp id since it's needed to create a TransactionHistory */
    protected int empId;

    /** Prefix for the columns */
    protected String pfx = "";

    /** Prefix for the audit columns, i.e. those that will be used in the record's value map. */
    protected String auditPfx = "";

    /** The set of transaction codes to restrict the history to. */
    protected Set<TransactionCode> transCodes;

    /** Options to indicate certain processing behaviors. */
    protected EmpTransDaoOption options;

    /** Stores the valid records. */
    protected List<TransactionRecord> records = new ArrayList<>();

    /** Records the code of the original transaction (in case it's overwritten as APP). */
    protected TransactionCode originalFirstCode = null;

    protected TransRecordRowMapper transRowMapper;

    /** --- Constructors --- */

    public TransHistoryHandler(int empId, String pfx, String auditPfx, Set<TransactionCode> transCodes,
                               EmpTransDaoOption options) {
        this.empId = empId;
        this.pfx = pfx;
        this.auditPfx = auditPfx;
        this.transCodes = transCodes;
        this.options = options;
        this.transRowMapper = new TransRecordRowMapper(pfx, auditPfx, options);
    }

    /** --- Handler --- */

    @Override
    public void processRow(ResultSet rs) throws SQLException {
        TransactionCode code = TransactionCode.valueOf(rs.getString(pfx + "CDTRANS"));

        // If this is the first record, the options may request it to be set as APP.
        if (rs.isFirst()) {
            originalFirstCode = code;
            if (options.shouldSetToApp() && !code.isAppointType()) {
                logger.debug("{} transaction will appear as 'APP' based on option: {}", code, options);
                code = TransactionCode.APP;
            }
        }

        // If initialization of earliest record was requested, the result set will not filter
        // by the code. Thus every record after the first should get filtered out here.
        // We can return null here but make sure to remove the null records afterwards
        if (!rs.isFirst() && options.shouldInitialize() && !transCodes.contains(code)) {
            return;
        }

        // Map the transaction record.
        TransactionRecord transRec = transRowMapper.mapRow(rs, 0);

        /** Add the record to the collection. */
        records.add(transRec);
    }

    /** --- Functional Getters --- */

    /**
     * Construct and returns a TransactionHistory generated from the result set.
     */
    public TransactionHistory getTransactionHistory() {
        return new TransactionHistory(empId, originalFirstCode, records);
    }
}