package gov.nysenate.ess.core.dao.pec;

import gov.nysenate.ess.core.dao.base.BasicSqlQuery;
import gov.nysenate.ess.core.dao.base.DbVendor;

public enum SqlPersonnelAssignedTaskQuery implements BasicSqlQuery {

    SELECT_TASKS_FOR_EMP("" +
            "SELECT *\n" +
            "FROM ${essSchema}.personnel_assigned_task\n" +
            "WHERE emp_id = :empId\n" +
            "  AND active = TRUE"
    ),

    SELECT_SPECIFIC_TASK_FOR_EMP("" +
            SELECT_TASKS_FOR_EMP.sql + "\n" +
            "  AND task_type = :taskType::ess.personnel_task_type\n" +
            "  AND task_number = :taskNumber"
    ),

    SELECT_TASKS_QUERY("" +
            "SELECT *\n" +
            "FROM ${essSchema}.personnel_assigned_task\n" +
            "WHERE active = TRUE\n" +
            "  AND (:empId::int IS NULL OR emp_id = :empId)\n" +
            "  AND (:taskType::ess.personnel_task_type IS NULL OR task_type = :taskType::ess.personnel_task_type)\n" +
            "  AND (:taskNumber::int IS NULL OR task_number = :taskNumber)\n" +
            "  AND (:completed::boolean IS NULL OR completed = :completed::boolean)"
    ),

    INSERT_TASK("" +
            "INSERT INTO ${essSchema}.personnel_assigned_task\n" +
            "        (emp_id, task_type, task_number, timestamp, update_user_id, completed, active)\n" +
            "VALUES (:empId, :taskType::ess.personnel_task_type, :taskNumber, :timestamp, :updateUserId, :completed, TRUE)"
    ),

    UPDATE_TASK("" +
            "UPDATE ${essSchema}.personnel_assigned_task\n" +
            "SET timestamp = :timestamp, update_user_id = :updateUserId, completed = :completed, active = TRUE\n" +
            "WHERE emp_id = :empId AND task_type = :taskType::ess.personnel_task_type AND task_number = :taskNumber"
    ),

    DEACTIVATE_TASK("" +
            "UPDATE ${essSchema}.personnel_assigned_task\n" +
            "SET active = FALSE\n" +
            "WHERE emp_id = :empId AND task_type = :taskType::ess.personnel_task_type AND task_number = :taskNumber"
    ),

    ;

    private final String sql;

    SqlPersonnelAssignedTaskQuery(String sql) {
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
