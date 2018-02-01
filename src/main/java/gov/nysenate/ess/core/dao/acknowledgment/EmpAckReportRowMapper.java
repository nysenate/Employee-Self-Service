package gov.nysenate.ess.core.dao.acknowledgment;

import gov.nysenate.ess.core.dao.base.BaseRowMapper;
import gov.nysenate.ess.core.model.acknowledgment.EmpAckReport;

import java.sql.ResultSet;
import java.sql.SQLException;

public class EmpAckReportRowMapper extends BaseRowMapper<EmpAckReport> {

    private String pfx = "";

    public EmpAckReportRowMapper(String pfx) {
        this.pfx = pfx;
    }

    @Override
    public EmpAckReport mapRow(ResultSet rs, int rowNum) throws SQLException {
        EmpAckReport empAckReport = new EmpAckReport();
        empAckReport.setEmpId(rs.getInt(pfx + "emp_id"));
        empAckReport.getAckedTimeMap().put(getLocalDateTimeFromRs(rs, "timestamp"),rs.getString(pfx + "title"));
        return empAckReport;
    }
}
