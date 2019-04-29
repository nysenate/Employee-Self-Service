package gov.nysenate.ess.core.dao.pec;

import gov.nysenate.ess.core.dao.base.BasicSqlQuery;
import gov.nysenate.ess.core.dao.base.DbVendor;

public enum SqlPersonnelEmployeeTaskQuery implements BasicSqlQuery {

    SELECT_TASKS_FOR_EMP("" +
            "SELECT *\n" +
            "FROM ${essSchema}.personnel_employee_task\n" +
            "WHERE emp_id = :empId"
    ),

    INSERT_TASK("" +
            "INSERT INTO ${essSchema}.personnel_employee_task\n" +
            "        (emp_id, task_type, task_number, timestamp, update_user_id, completed)\n" +
            "VALUES (:empId, :taskType::ess.personnel_task_type, :taskNumber, :timestamp, :updateUserId, :completed)"
    ),

    UPDATE_TASK("" +
            "UPDATE ${essSchema}.personnel_employee_task\n" +
            "SET timestamp = :timestamp, update_user_id = :updateUserId, completed = :completed\n" +
            "WHERE emp_id = :empId AND task_type = :taskType::ess.personnel_task_type AND task_number = :taskNumber"
    ),

    ;

    private final String sql;

    SqlPersonnelEmployeeTaskQuery(String sql) {
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
