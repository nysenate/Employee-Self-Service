package gov.nysenate.ess.core.department;

import gov.nysenate.ess.core.dao.base.*;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class DepartmentDao extends SqlBaseDao {

    public boolean isEmployeeADepartmentHead(int empId) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("headEmpId", empId);
        String sql = SqlDepartmentQuery.SELECT_DEPARTMENT_ID_FOR_HEAD.getSql(schemaMap());
        List<Integer> departmentIds = localNamedJdbc.queryForList(sql, params, Integer.class);
        return !departmentIds.isEmpty();
    }

    public Department getDepartment(int departmentId) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("departmentId", departmentId);
        String sql = SqlDepartmentQuery.SELECT_DEPARTMENT_BY_ID.getSql(schemaMap());
        DepartmentRowHandler handler = new DepartmentRowHandler();
        localNamedJdbc.query(sql, params, handler);
        return handler.getResults().stream().findFirst().orElse(null);
    }

    /**
     * Get all active departments
     */
    public Set<Department> getDepartments() {
        String sql = SqlDepartmentQuery.SELECT_ACTIVE_DEPARTMENTS.getSql(schemaMap());
        DepartmentRowHandler handler = new DepartmentRowHandler();
        localNamedJdbc.query(sql, handler);
        return handler.getResults();
    }

    public Department getEmployeeDepartment(int empId) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("empId", empId);
        String sql = SqlDepartmentQuery.SELECT_EMPLOYEE_DEPARTMENT_ID.getSql(schemaMap());
        try {
            Integer departmentId = localNamedJdbc.queryForObject(sql, params, Integer.class);
            return getDepartment(departmentId);
        } catch (IncorrectResultSizeDataAccessException ex) {
            // No department data for the given employee.
            return null;
        }
    }

    public Department updateDepartment(Department department) {
        if (!doUpdateDepartment(department)) {
            department = insertDepartment(department);
        }
        updateDepartmentEmployees(department);
        return department;
    }

    /**
     * Update or insert all given departments includes setting the department employees.
     * @param departments
     * @return The updated departments, any inserted department will now have its id set.
     */
    public Set<Department> updateDepartments(Set<Department> departments) {
        Set<Department> updatedDepartments = new HashSet<>();
        for (Department dept : departments) {
            updatedDepartments.add(updateDepartment(dept));
        }
        return updatedDepartments;
    }

    private boolean doUpdateDepartment(Department department) {
        MapSqlParameterSource params = departmentParams(department);
        String sql = SqlDepartmentQuery.UPDATE_DEPARTMENT.getSql(schemaMap());
        return localNamedJdbc.update(sql, params) > 0;
    }

    private Department insertDepartment(Department department) {
        MapSqlParameterSource params = departmentParams(department);
        String sql = SqlDepartmentQuery.INSERT_DEPARTMENT.getSql(schemaMap());
        KeyHolder keyHolder = new GeneratedKeyHolder();
        localNamedJdbc.update(sql, params, keyHolder);
        return department.setId((Integer) keyHolder.getKeys().get("department_id"));
    }

    private void updateDepartmentEmployees(Department dept) {
        for (int empId : dept.getEmployeeIds()) {
            if (!updateEmployeeDepartment(dept.getId(), empId)) {
                insertEmployeeDepartment(dept.getId(), empId);
            }
        }
    }

    private boolean updateEmployeeDepartment(int departmentId, int empId) {
        MapSqlParameterSource params = empDepartmentParams(departmentId, empId);
        String sql = SqlDepartmentQuery.UPDATE_EMPLOYEE_DEPARTMENT.getSql(schemaMap());
        return localNamedJdbc.update(sql, params) > 0;
    }

    private void insertEmployeeDepartment(int departmentId, int empId) {
        MapSqlParameterSource params = empDepartmentParams(departmentId, empId);
        String sql = SqlDepartmentQuery.INSERT_EMPLOYEE_DEPARTMENT.getSql(schemaMap());
        localNamedJdbc.update(sql, params);
    }

    private MapSqlParameterSource empDepartmentParams(int deptId, int empId) {
        return new MapSqlParameterSource()
                .addValue("departmentId", deptId)
                .addValue("empId", empId);
    }

    private MapSqlParameterSource departmentParams(Department department) {
        return new MapSqlParameterSource()
                .addValue("departmentId", department.getId())
                .addValue("name", department.getName())
                .addValue("headEmpId", department.getHeadEmpId())
                .addValue("isActive", department.isActive());
    }
}
