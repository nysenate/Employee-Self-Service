package gov.nysenate.ess.travel.request.app.dao;

import gov.nysenate.ess.core.dao.base.BaseRowMapper;
import gov.nysenate.ess.travel.request.app.TravelApplicationStatus;
import org.apache.commons.lang3.StringUtils;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TravelApplicationRowMapper extends BaseRowMapper<TravelAppRepositoryView> {
    @Override
    public TravelAppRepositoryView mapRow(ResultSet rs, int i) throws SQLException {
        TravelAppRepositoryView view = new TravelAppRepositoryView();
        view.appId = rs.getInt("app_id");
        view.travelerEmpId = rs.getInt("traveler_id");
        view.travelerDeptHeadEmpId = rs.getInt("traveler_dept_head_emp_id");
        view.status = StringUtils.isBlank(rs.getString("status"))
                ? new TravelApplicationStatus()
                : new TravelApplicationStatus(rs.getString("status"), rs.getString("status_note"));
        return view;
    }
}
