package gov.nysenate.ess.travel.request.app.dao;

import gov.nysenate.ess.core.dao.base.BaseRowMapper;
import gov.nysenate.ess.travel.EventType;
import gov.nysenate.ess.travel.request.app.PurposeOfTravel;
import gov.nysenate.ess.travel.request.app.TravelApplicationStatus;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TravelApplicationRowMapper extends BaseRowMapper<TravelAppRepositoryView> {
    @Override
    public TravelAppRepositoryView mapRow(ResultSet rs, int i) throws SQLException {
        TravelAppRepositoryView view = new TravelAppRepositoryView();
        view.appId = rs.getInt("app_id");
        view.travelerEmpId = rs.getInt("traveler_id");
        view.travelerDeptHeadEmpId = rs.getInt("traveler_dept_head_emp_id");
        view.status = new TravelApplicationStatus(rs.getString("status"), rs.getString("status_note"));
        view.pot =  new PurposeOfTravel(
                EventType.valueOf(rs.getString("event_type")),
                rs.getString("event_name"),
                rs.getString("additional_purpose"));
        view.modifiedDateTime = getLocalDateTimeFromRs(rs, "modified_date_time");
        view.modifiedByEmpId = rs.getInt("created_by");
        return view;
    }
}
