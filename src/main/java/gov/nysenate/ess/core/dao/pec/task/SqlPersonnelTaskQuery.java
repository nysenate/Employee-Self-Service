package gov.nysenate.ess.core.dao.pec.task;

import gov.nysenate.ess.core.dao.base.BasicSqlQuery;
import gov.nysenate.ess.core.dao.base.DbVendor;

public enum SqlPersonnelTaskQuery implements BasicSqlQuery {

    SELECT_ALL_TASKS("SELECT * FROM ${essSchema}.personnel_task"),

    SELECT_TASK_BY_ID("" +
            "SELECT *\n" +
            "FROM ${essSchema}.personnel_task\n" +
            "WHERE task_id = :taskId"),
    INSERT_TASK_COMPLETION("" +
            "INSERT INTO ${essSchema}.personnel_task_assignment\n" +
            "        (emp_id, task_id, timestamp, update_user_id, completed, active)\n" +
            "VALUES (:empId, :taskId, now(), :updateUserId, :completed, true)"
    ),
    UPDATE_TASK_COMPLETION("" +
            "UPDATE ${essSchema}.personnel_task_assignment\n" +
            "SET timestamp = now(), update_user_id = :updateUserId, completed = :completed, manual_override = :manualOverride\n" +
            "WHERE emp_id = :empId AND task_id = :taskId"
    ),
    UPDATE_TASK_ASSIGNMENT(
            "update ${essSchema}.personnel_task_assignment set active = ?, " +
                    "update_user_id = ?, manual_override = ?, where emp_id = ? and task_id = ?"
    ),
    SELECT_EVERFI_CONTENT_IDS(
            "SELECT *\n" +
                    "FROM ${essSchema}.everfi_course_content_id"
    ),

    SELECT_EVERFI_ASSIGNMENT_IDS(
            "SELECT *\n" +
                    "FROM ${essSchema}.everfi_course_assignment_id"
    ),
    SELECT_ETHICS_LIVE_COURSE_CODE_INFO(
            "SELECT ethics_code_id\n" +
                    "FROM ${essSchema}.ethics_live_course where task_id = :taskId"
    ),
    UPDATE_ETHICS_CODE("" +
            "UPDATE ${essSchema}.ethics_code SET code = :code " +
            "WHERE ethics_code_id = :codeId AND sequence_no = :sequence_no");

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
