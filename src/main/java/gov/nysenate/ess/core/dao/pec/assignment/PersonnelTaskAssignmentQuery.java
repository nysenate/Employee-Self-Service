package gov.nysenate.ess.core.dao.pec.assignment;

import gov.nysenate.ess.core.dao.base.BasicSqlQuery;
import gov.nysenate.ess.core.dao.base.DbVendor;

public enum PersonnelTaskAssignmentQuery implements BasicSqlQuery {

    SELECT_TASKS_FOR_EMP("" +
            "SELECT *\n" +
            "FROM ${essSchema}.personnel_task_assignment\n" +
            "WHERE emp_id = :empId"
    ),

    SELECT_SPECIFIC_TASK_FOR_EMP("" +
            SELECT_TASKS_FOR_EMP.sql + "\n" +
            "  AND task_id = :taskId"
    ),

    SELECT_NOTIFIABLE_ASSIGNMENTS(
            "SELECT *\n" +
            "FROM ${essSchema}.personnel_task_assignment pta\n" +
            "JOIN ${essSchema}.personnel_task pt ON pt.task_id = pta.task_id\n" +
            "WHERE pt.active AND pt.notifiable AND pta.active AND NOT pta.completed"
    ),

    SELECT_TASKS_QUERY("" +
            "SELECT *\n" +
            "FROM ${essSchema}.personnel_task_assignment ta\n" +
            "JOIN ${essSchema}.personnel_task t USING (task_id)\n" +
            "WHERE (:active::boolean IS NULL OR ta.active = :active::boolean)\n" +
            "  AND (:empId::int IS NULL OR emp_id = :empId)\n" +
            "  AND (:taskType::ess.personnel_task_type IS NULL OR t.task_type = :taskType::ess.personnel_task_type)\n" +
            "  AND (:completed::boolean IS NULL OR completed = :completed::boolean)\n" +
            "  AND (:completed::boolean IS NULL OR :completed::boolean = FALSE OR\n" +
            "        (:completedFrom::TIMESTAMP WITHOUT TIME ZONE IS NULL OR\n" +
            "          timestamp >= :completedFrom::TIMESTAMP WITHOUT TIME ZONE)\n" +
            "        AND\n" +
            "        (:completedTo::TIMESTAMP WITHOUT TIME ZONE IS NULL OR\n" +
            "          timestamp <= :completedTo::TIMESTAMP WITHOUT TIME ZONE)\n" +
            "  )\n" +
            "  AND (:taskIdsPresent OR ta.task_id IN (:taskIds))"
    ),

    SELECT_NOT_IN_TASKS_QUERY("" +
            "SELECT t.task_id, t.task_type, t.title, t.effective_date_time, t.active, ta.completed,\n" +
            "       ta.emp_id, ta.assignment_date, ta.due_date, ta.update_user_id, ta.timestamp, ta.manual_override\n" +
            "FROM ${essSchema}.personnel_task_assignment ta\n" +
            "JOIN ${essSchema}.personnel_task t USING (task_id)\n" +
            "WHERE (:active::boolean IS NULL OR t.active = :active::boolean)\n" +
            "  AND (:empId::int IS NULL OR emp_id = :empId)\n" +
            "  AND (:taskType::ess.personnel_task_type IS NULL OR t.task_type = :taskType::ess.personnel_task_type)"
    ),

    SELECT_ACTIVE_TASKS(""+
            "SELECT task_id FROM ${essSchema}.personnel_task\n" +
            "WHERE active = :active"

    ),

    INSERT_TASK("" +
            "INSERT INTO ${essSchema}.personnel_task_assignment\n" +
            "        (emp_id, task_id, timestamp, update_user_id, completed, active, manual_override, assignment_date, due_date)\n" +
            "VALUES (:empId, :taskId, :timestamp, :updateUserId, :completed, :active, :manualOverride, :assignmentDate, :dueDate)"
    ),

    UPDATE_TASK("" +
            "UPDATE ${essSchema}.personnel_task_assignment\n" +
            "SET timestamp = :timestamp, update_user_id = :updateUserId, completed = :completed, active = :active\n" +
            "WHERE emp_id = :empId AND task_id = :taskId"
    ),

    UPDATE_TASK_DATES ("" +
            "UPDATE ${essSchema}.personnel_task_assignment\n" +
            "SET assignment_date = :assignmentDate, due_date = :dueDate\n" +
            "WHERE emp_id = :empId AND task_id = :taskId"
    ),

    GET_MANUAL_OVERRIDE_STATUS("" +
            "SELECT manual_override from ${essSchema}.personnel_task_assignment\n" +
            "WHERE emp_id = :empId AND task_id = :taskId"),

    INSERT_COMPLETE_TASK("" +
            "INSERT INTO ${essSchema}.personnel_task_assignment\n" +
            "        (emp_id, task_id, timestamp, update_user_id, completed, active, manual_override)\n" +
            "VALUES (:empId, :taskId, now(), :updateUserId, TRUE, TRUE, FALSE)"
    ),

    UPDATE_COMPLETE_TASK("" +
            "UPDATE ${essSchema}.personnel_task_assignment\n" +
            "SET timestamp = now(), update_user_id = :updateUserId, completed = TRUE, active = TRUE\n" +
            "WHERE emp_id = :empId AND task_id = :taskId"
    ),

    DEACTIVATE_TASK("" +
            "UPDATE ${essSchema}.personnel_task_assignment\n" +
            "SET active = FALSE\n" +
            "WHERE emp_id = :empId AND task_id = :taskId"
    ),

    ;

    private final String sql;

    PersonnelTaskAssignmentQuery(String sql) {
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
