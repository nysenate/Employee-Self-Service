package gov.nysenate.ess.travel.review.dao;

import gov.nysenate.ess.core.dao.base.BaseRowMapper;
import gov.nysenate.ess.travel.authorization.role.TravelRole;
import gov.nysenate.ess.travel.review.policy.ReviewPolicyType;

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
        view.policyType = ReviewPolicyType.valueOf(rs.getString("policy_type"));
        view.policyVersion = rs.getInt("policy_version");
        view.pendingReviewerRole = TravelRole.valueOf(rs.getString("pending_reviewer_role"));
        view.isShared = rs.getBoolean("is_shared");
        return view;
    }
}
