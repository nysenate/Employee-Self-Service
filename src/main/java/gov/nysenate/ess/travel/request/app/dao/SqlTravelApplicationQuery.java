package gov.nysenate.ess.travel.request.app.dao;

import gov.nysenate.ess.core.dao.base.BasicSqlQuery;
import gov.nysenate.ess.core.dao.base.DbVendor;

enum SqlTravelApplicationQuery implements BasicSqlQuery {
    INSERT_APP(
            "INSERT INTO ${travelSchema}.app(traveler_id, submitted_by_id, status, status_note, traveler_dept_head_emp_id) \n" +
                    "VALUES (:travelerId, :submittedById, :status, :note, :travelerDeptHeadEmpId)"
    ),
    UPDATE_APP(
            "UPDATE ${travelSchema}.app \n" +
                    "SET status = :status, status_note = :note, traveler_dept_head_emp_id = :travelerDeptHeadEmpId \n" +
                    "WHERE app_id = :appId"
    ),
    INSERT_AMENDMENT(
            "INSERT INTO ${travelSchema}.amendment \n" +
                    "(app_id, version, event_type, event_name, additional_purpose, created_by) \n" +
                    "VALUES (:appId, :version, :eventType, :eventName, :additionalPurpose, :createdBy)"
    ),
    TRAVEL_APP_SELECT(
            "SELECT app.app_id, app.traveler_id, app.status, app.status_note, app.traveler_dept_head_emp_id,\n" +
                    " amendment.amendment_id, amendment.app_id, amendment.version,\n" +
                    " amendment.event_type, amendment.event_name, amendment.additional_purpose,\n" +
                    " amendment.created_date_time, amendment.created_by\n" +
                    " FROM ${travelSchema}.app\n" +
                    " INNER JOIN ${travelSchema}.amendment amendment ON amendment.app_id = app.app_id \n"
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
