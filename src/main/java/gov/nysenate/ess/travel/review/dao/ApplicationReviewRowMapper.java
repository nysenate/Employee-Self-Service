package gov.nysenate.ess.travel.review.dao;

import gov.nysenate.ess.core.dao.base.BaseRowMapper;
import gov.nysenate.ess.travel.authorization.role.TravelRole;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ApplicationReviewRowMapper extends BaseRowMapper<AppReviewRepositoryView> {


    public ApplicationReviewRowMapper() {
    }

    @Override
    public AppReviewRepositoryView mapRow(ResultSet rs, int rowNum) throws SQLException {
        AppReviewRepositoryView view = new AppReviewRepositoryView();
        view.appReviewId = rs.getInt("app_review_id");
        view.appId = rs.getInt("app_id");
        view.travelerRole = rs.getString("traveler_role") == null
                ? null
                : TravelRole.valueOf(rs.getString("traveler_role"));
        view.isShared = rs.getBoolean("is_shared");
        return view;
    }
}
