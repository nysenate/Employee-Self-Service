package gov.nysenate.ess.travel.review.dao;

import gov.nysenate.ess.core.dao.base.BaseRowMapper;
import gov.nysenate.ess.travel.application.TravelApplication;
import gov.nysenate.ess.travel.application.TravelApplicationDao;
import gov.nysenate.ess.travel.authorization.role.TravelRole;
import gov.nysenate.ess.travel.review.Action;
import gov.nysenate.ess.travel.review.ApplicationReview;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class ApplicationReviewRowMapper extends BaseRowMapper<ApplicationReview> {

    private TravelApplicationDao travelApplicationDao;
    private SqlActionDao actionDao;

    public ApplicationReviewRowMapper(TravelApplicationDao travelApplicationDao,
                               SqlActionDao actionDao) {
        this.travelApplicationDao = travelApplicationDao;
        this.actionDao = actionDao;
    }

    @Override
    public ApplicationReview mapRow(ResultSet rs, int rowNum) throws SQLException {
        int appReviewId = rs.getInt("app_review_id");
        TravelApplication application = travelApplicationDao.selectTravelApplication(rs.getInt("app_id"));
        TravelRole travelRole = rs.getString("traveler_role") == null
                ? null
                : TravelRole.valueOf(rs.getString("traveler_role"));
        List<Action> actions = actionDao.selectActionsByReviewId(appReviewId);
        boolean isShared = rs.getBoolean("is_shared");
        return new ApplicationReview(appReviewId, application, travelRole, actions, isShared);
    }
}
