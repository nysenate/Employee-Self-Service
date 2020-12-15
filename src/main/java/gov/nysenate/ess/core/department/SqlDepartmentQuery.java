package gov.nysenate.ess.core.department;

import gov.nysenate.ess.core.dao.base.BasicSqlQuery;
import gov.nysenate.ess.core.dao.base.DbVendor;

public enum SqlDepartmentQuery implements BasicSqlQuery {
    SELECT_DEPARTMENT_ID_FOR_HEAD(
            "SELECT department_id \n" +
                    "FROM ${essSchema}.department \n" +
                    "WHERE head_emp_id = :headEmpId"
    ),
    SELECT_DEPARTMENTS(
            "SELECT (dep.department_id, dep.name, dep.head_emp_id, dep.is_active, \n" +
                    "emp_dep.employee_id) \n" +
                    "FROM ${essSchema}.department dep \n" +
                    "JOIN ${essSchema}.employee_department emp_dep \n" +
                    "ON dep.department_id = emp_dep.department_id \n"
    ),
    SELECT_DEPARTMENT_BY_ID(
            SELECT_DEPARTMENTS.getSql() +
                    "WHERE department_id = :departmentId"
    ),
    SELECT_EMPLOYEE_DEPARTMENT_ID(
            "SELECT (department_id) \n" +
                    "FROM ${essSchema}.employee_department \n" +
                    "WHERE employee_id = :empId"
    ),
    INSERT_DEPARTMENT(
            "INSERT INTO ${essSchema}.department(name, head_emp_id, is_active) \n" +
                    "VALUES (:name, :headEmpId, :isActive)"
    ),
    UPDATE_DEPARTMENT(
            "UPDATE ${essSchema}.department \n" +
                    "SET head_emp_id = :headEmpId, \n" +
                    "is_active = :isActive \n" +
                    "WHERE department_id = :departmentId"
    ),
    UPDATE_EMPLOYEE_DEPARTMENT(
            "UPDATE ${ess.schema}.employee_department \n" +
                    "SET department_id = :departmentId \n" +
                    "WHERE employee_id = :empId"
    ),
    INSERT_EMPLOYEE_DEPARTMENT(
            "INSERT INTO ${essSchema}.employee_department(department_id, employee_id) \n" +
                    "VALUES (:departmentId, :empId)"
    )
    ;

    private String sql;

    SqlDepartmentQuery(String sql) {
        this.sql = sql;
    }

    @Override
    public String getSql() {
        return this.sql;
    }

    @Override
    public DbVendor getVendor() {
        return DbVendor.POSTGRES;
    }
}
