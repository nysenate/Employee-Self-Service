package gov.nysenate.ess.travel.review.dao;

import gov.nysenate.ess.core.dao.base.BasicSqlQuery;
import gov.nysenate.ess.core.dao.base.DbVendor;

enum SqlApplicationReviewQuery implements BasicSqlQuery {
    INSERT_APPLICATION_REVIEW(
            "INSERT INTO ${travelSchema}.app_review \n" +
                    " (app_id, traveler_role, next_reviewer_role, is_shared) \n" +
                    " VALUES (:appId, :travelerRole, :nextReviewerRole, :isShared)"
    ),
    UPDATE_APPLICATION_REVIEW(
            "UPDATE ${travelSchema}.app_review\n" +
                    " SET next_reviewer_role = :nextReviewerRole, is_shared = :isShared\n" +
                    " WHERE app_review_id = :appReviewId"
    ),
    PENDING_REVIEWS_FOR_ROLE("""
            SELECT app_review.app_review_id, app_review.app_id, app_review.traveler_role,
                   app_review.next_reviewer_role, is_shared
            FROM ${travelSchema}.app_review
              JOIN ${travelSchema}.app ON app_review.app_id = app.app_id
            WHERE app.status = 'PENDING'
            AND app_review.next_reviewer_role = :role
            """
    ),
    PENDING_REVIEWS_FOR_DEPT_IDS("""
            SELECT app_review.app_review_id, app_review.app_id, app_review.traveler_role,
                   app_review.next_reviewer_role, is_shared, app.status, app.traveler_department_id
            FROM ${travelSchema}.app_review
                     JOIN ${travelSchema}.app ON app_review.app_id = app.app_id
            WHERE app.status = 'PENDING'
              AND app_review.next_reviewer_role = 'DEPARTMENT_HEAD'
              AND app.traveler_department_id IN (:departmentIds) 
            """
    ),
    APP_REVIEW_SELECT(
            "SELECT app_review.app_review_id, app_review.app_id, app_review.traveler_role,\n" +
                    " app_review.next_reviewer_role, is_shared\n "
    ),
    SELECT_ACTIVE_SHARED_REVIEWS(
            APP_REVIEW_SELECT.getSql() +
                    " FROM ${travelSchema}.app_review\n" +
                    " WHERE app_review.next_reviewer_role != :role" +
                    " AND (SELECT type FROM ${travelSchema}.app_review_action\n" +
                    "     WHERE app_review_action.app_review_id = app_review.app_review_id\n" +
                    "     ORDER BY date_time desc\n" +
                    "     Limit 1) != :disapproval\n" +
                    " AND is_shared = true"
    ),
    SELECT_APP_REVIEW_HISTORY_FOR_ROLE(
            APP_REVIEW_SELECT.getSql() +
                    " FROM ${travelSchema}.app_review\n" +
                    " WHERE EXISTS \n" +
                    "  (SELECT DISTINCT(action.app_review_id) \n" +
                    "  FROM ${travelSchema}.app_review_action action \n" +
                    "  WHERE action.role = :role \n" +
                    "  AND action.app_review_id = app_review.app_review_id)"
    ),
    SELECT_APP_REVIEW_HISTORY_FOR_DEPT_HD(
            APP_REVIEW_SELECT.getSql() +
                    "FROM ${travelSchema}.app_review\n" +
                    "  JOIN ${travelSchema}.app ON app.app_id = app_review.app_id\n" +
                    "WHERE EXISTS\n" +
                    "  (SELECT DISTINCT(action.app_review_id)\n" +
                    "  FROM ${travelSchema}.app_review_action action\n" +
                    "  WHERE action.role = :role\n" +
                    "AND action.app_review_id = app_review.app_review_id)\n" +
                    "AND app.traveler_department_id IN\n" +
                    "  (SELECT department_id\n" +
                    "  FROM ${essSchema}.department\n" +
                    "  WHERE head_emp_id = :headEmpId)"
    ),
    SELECT_APPLICATION_REVIEW_BY_ID(
            APP_REVIEW_SELECT.getSql() +
                    " FROM ${travelSchema}.app_review\n" +
                    " WHERE app_review_id = :appReviewId"
    ),
    SELECT_APPLICATION_REVIEW_BY_APP_ID(
            APP_REVIEW_SELECT.getSql() +
                    " FROM ${travelSchema}.app_review\n" +
                    " WHERE app_id = :appId"
    ),
    SELECT_APP_REVIEWS_FOR_RECONCILIATION(
            APP_REVIEW_SELECT.getSql() + """
                    FROM ${travelSchema}.app_review
                             JOIN ${travelSchema}.app ON app.app_id = app_review.app_id
                             JOIN (SELECT app_id, max(amendment_id) as active_amendment_id
                                   FROM ${travelSchema}.amendment
                                   GROUP BY app_id) active_amendment
                                  ON active_amendment.app_id = app.app_id
                             JOIN (SELECT amendment_id, min(leg_id) as first_leg_id
                                   FROM ${travelSchema}.amendment_legs
                                   GROUP BY amendment_id) first_leg
                                  ON first_leg.amendment_id = active_amendment.active_amendment_id
                             JOIN ${travelSchema}.leg ON leg.leg_id = first_leg.first_leg_id
                    WHERE app.status = 'APPROVED'
                      AND leg.travel_date >= :from
                      AND leg.travel_date <= :to
                    """
    );

    private String sql;

    SqlApplicationReviewQuery(String sql) {
        this.sql = sql;
    }

    @Override
    public String getSql() {
        return sql;
    }

    @Override
    public DbVendor getVendor() {
        return DbVendor.POSTGRES;
    }
}
