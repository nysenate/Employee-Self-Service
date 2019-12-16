package gov.nysenate.ess.core.dao.pec.task;

import gov.nysenate.ess.core.dao.base.BasicSqlQuery;
import gov.nysenate.ess.core.dao.base.DbVendor;

public enum SqlPersonnelTaskQuery implements BasicSqlQuery {

    SELECT_ALL_TASKS("SELECT * FROM ${essSchema}.personnel_task"),

    SELECT_TASK_BY_ID("" +
            "SELECT *\n" +
            "FROM ${essSchema}.personnel_task\n" +
            "WHERE task_id = :taskId"),
    UPDATE_TASK_COMPLETION(
            "update ${essSchema}.personnel_task_assignment set completed = ?, " +
                    "update_user_id = ? where emp_id = ? and task_id = ?"
    ),
    UPDATE_TASK_ASSIGNMENT(
            "update ${essSchema}.personnel_task_assignment set active = ?, " +
                    "update_user_id = ? where emp_id = ? and task_id = ?"
    )
    ;

    private final String sql;

    SqlPersonnelTaskQuery(String sql) {
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
