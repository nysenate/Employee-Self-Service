package gov.nysenate.ess.travel.request.app.dao;

import gov.nysenate.ess.core.dao.base.BaseRowMapper;
import gov.nysenate.ess.travel.EventType;
import gov.nysenate.ess.travel.request.amendment.Version;
import gov.nysenate.ess.travel.request.app.PurposeOfTravel;

import java.sql.ResultSet;
import java.sql.SQLException;

class AmendmentRowMapper extends BaseRowMapper<AmendmentRepositoryView> {

    public AmendmentRowMapper() {
    }

    @Override
    public AmendmentRepositoryView mapRow(ResultSet rs, int i) throws SQLException {
        AmendmentRepositoryView view = new AmendmentRepositoryView();
        view.amendmentId = rs.getInt("amendment_id");
        view.appId = rs.getInt("app_id");
        view.version = Version.valueOf(rs.getString("version"));
        view.pot = new PurposeOfTravel(
                EventType.valueOf(rs.getString("event_type")),
                rs.getString("event_name"),
                rs.getString("additional_purpose")
        );
        view.createdDateTime = getLocalDateTimeFromRs(rs, "created_date_time");
        view.createdByEmpId = rs.getInt("created_by");
        return view;
    }
}
