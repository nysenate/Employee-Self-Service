package gov.nysenate.ess.web.dao.personnel;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import gov.nysenate.ess.core.util.DateUtils;
import gov.nysenate.ess.core.dao.base.BaseHandler;
import gov.nysenate.ess.web.model.personnel.EmployeeSupInfo;
import gov.nysenate.ess.web.model.personnel.SupervisorEmpGroup;
import gov.nysenate.ess.core.model.transaction.TransactionCode;
import gov.nysenate.ess.core.model.transaction.TransactionType;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

import static gov.nysenate.ess.core.model.transaction.TransactionCode.*;

public class SupervisorEmpGroupHandler extends BaseHandler
{
    private final Set<TransactionCode> supTransCodes = new HashSet<>(Arrays.asList(SUP,APP,RTP));
    private final Set<TransactionCode> checkedTransCodes = new HashSet<>(Arrays.asList(EMP, SUP, APP, RTP));

    private static class EmpSupTransaction
    {
        int transRank;
        String empGroup;
        Integer empId;
        Integer supOvrId;
        Integer supId;
        boolean empStatus;
        String lastName;
        TransactionCode transCode;
        TransactionType transType;
        LocalDate effectDate;

        public EmpSupTransaction(ResultSet rs) throws SQLException {
            this.transRank = rs.getInt("TRANS_RANK");
            this.empGroup = rs.getString("EMP_GROUP");
            this.empId = rs.getInt("NUXREFEM");
            this.lastName = rs.getString("NALAST");
            this.supOvrId = rs.getInt("OVR_NUXREFSV");
            this.supId = rs.getInt("NUXREFSV");
            this.empStatus = rs.getString("CDEMPSTATUS").equals("A");
            this.transCode = TransactionCode.valueOf(rs.getString("CDTRANS"));
            this.transType = TransactionType.valueOf(rs.getString("CDTRANSTYP"));
            this.effectDate = DateUtils.getLocalDate(rs.getDate("DTEFFECT"));
        }

        public boolean isFirstRec() {
            return transRank == 0;
        }

        public boolean isEmpTermRec() {
            return transCode.equals(EMP);
        }
    }

    private Multimap<Integer, EmpSupTransaction> empSupTransactions = LinkedHashMultimap.create();
    private Map<Integer, EmployeeSupInfo> primaryEmployees = new HashMap<>();
    private Map<Integer, EmployeeSupInfo> overrideEmployees = new HashMap<>();

    @Override
    public void processRow(ResultSet resultSet) throws SQLException {
        if (resultSet.getInt("NUXREFSV") != 0) {
            EmpSupTransaction empTrans = new EmpSupTransaction(resultSet);
            empSupTransactions.put(empTrans.empId, empTrans);
        }
    }

    public SupervisorEmpGroup getSupervisorEmpGroup(int supId, LocalDate startDate, LocalDate endDate) {
        SupervisorEmpGroup empGroup = new SupervisorEmpGroup(supId, startDate, endDate);
        for (Integer empId : empSupTransactions.keySet()) {
            EmployeeSupInfo employeeSupInfo = new EmployeeSupInfo(empId, supId, startDate, endDate);
            LinkedList<EmpSupTransaction> transRecs = new LinkedList<>(empSupTransactions.get(empId));
            if (transRecs.get(0).empGroup.equals("PRIMARY")) {
                boolean hasSupTrans = transRecs.stream().anyMatch(rec -> supTransCodes.contains(rec.transCode));
                if (hasSupTrans) {
                    for (EmpSupTransaction supTrans : transRecs) {
                        boolean effectDateIsPrior = supTrans.effectDate.compareTo(startDate) <= 0;
                        if (supTrans.isFirstRec()) {
                            if (supTrans.supId == supId && !supTrans.isEmpTermRec()) {
                                primaryEmployees.put(empId, employeeSupInfo);
                            }
                            else if (!effectDateIsPrior) {

                            }
                        }
                    }
                }
                else {

                }

            }
        }

        return empGroup;
    }
}
