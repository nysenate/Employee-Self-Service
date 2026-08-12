package gov.nysenate.ess.travel.request.app.dao;

import gov.nysenate.ess.core.dao.base.BasicSqlQuery;
import gov.nysenate.ess.core.dao.base.DbVendor;

enum SqlTravelApplicationQuery implements BasicSqlQuery {
    INSERT_APP("""
            INSERT INTO ${travelSchema}.app(traveler_id, submitted_by_id, status, status_note, traveler_dept_head_emp_id,
              event_type, event_name, additional_purpose, modified_by)
            VALUES (:travelerId, :submittedById, :status, :note, :travelerDeptHeadEmpId,
              :eventType, :eventName, :additionalPurpose, :modifiedBy)
            """
    ),
    UPDATE_APP("""
            UPDATE ${travelSchema}.app
              SET status = :status, status_note = :note, traveler_dept_head_emp_id = :travelerDeptHeadEmpId, 
              event_type = :eventType, event_name = :eventName, additional_purpose = :additionalPurpose,
              modified_by = :modifiedBy, modified_date_time = :modifiedDateTime
            WHERE app_id = :appId
            """
    ),
    UPDATE_APP_STATUS("""
            UPDATE ${travelSchema}.app
              SET status = :status, status_note = :note
            WHERE app_id = :appId
            """
    ),
    TRAVEL_APP_SELECT("""
            SELECT app_id, traveler_id, status, status_note, traveler_dept_head_emp_id, event_type, event_name,
              additional_purpose, submitted_by_id, created_date_time, modified_by, modified_date_time
            FROM ${travelSchema}.app
            """
    ),
    SELECT_APP_BY_ID(
            TRAVEL_APP_SELECT.getSql() + " \n" +
                    "WHERE app.app_id = :appId"
    ),
    SELECT_APPS_FOR_USER(TravelApplicationSearchSql.SELECT),
    COUNT_APPS_FOR_USER(TravelApplicationSearchSql.COUNT),
    SELECT_APPS_BY_FROM_AND_TO_DATES(
            TRAVEL_APP_SELECT.getSql() + "\n" +
                    "WHERE created_date_time BETWEEN :fromDate AND :toDate"
    );

    private String sql;

    SqlTravelApplicationQuery(String sql) {
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

/**
 * SQL shared by the paginated result and total-count queries.
 * Optional filters are expressed in SQL so the DAO only binds parameters and applies ordering/pagination.
 */
final class TravelApplicationSearchSql {

    private static final String FILTERED_APPS_CTE = """
            WITH app_dates AS (
                SELECT app_route.app_id,
                    MIN(app_route_leg.travel_date) FILTER (WHERE app_route_leg.is_outbound) AS start_date
                FROM ${travelSchema}.app_route
                    INNER JOIN ${travelSchema}.app_route_leg USING (app_route_id)
                GROUP BY app_route.app_id
            ),
            filtered_apps AS (
                SELECT app.*, app_dates.start_date
                FROM ${travelSchema}.app
                    LEFT JOIN app_dates USING (app_id)
                WHERE (app.traveler_id = :userId OR app.submitted_by_id = :userId)
                    AND app.created_date_time IS NOT NULL
                    AND app.status IN (:statuses)
                    AND (CAST(:fromDate AS date) IS NULL OR app_dates.start_date >= :fromDate)
                    AND (CAST(:toDate AS date) IS NULL OR app_dates.start_date <= :toDate)
            )
            """;

    static final String SELECT = FILTERED_APPS_CTE + """
            SELECT *
            FROM filtered_apps
            """;

    static final String COUNT = FILTERED_APPS_CTE + """
            SELECT COUNT(*)
            FROM filtered_apps
            """;

    private TravelApplicationSearchSql() {}
}
