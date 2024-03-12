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
    SELECT_APP_BY_TRAVELER(
            TRAVEL_APP_SELECT.getSql() + "\n" +
                    "WHERE (app.traveler_id = :userId OR app.submitted_by_id = :userId)"
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
