package gov.nysenate.ess.core.department;

import gov.nysenate.ess.core.dao.base.BaseHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DepartmentRowHandler extends BaseHandler {
    private Map<Integer, Department> departments = new HashMap<>();

    @Override
    public void processRow(ResultSet rs) throws SQLException {
        int departmentId = rs.getInt("department_id");
        if (departments.containsKey(departmentId)) {
            departments.get(departmentId).addEmployee(rs.getInt("employee_id"));
        } else {
            String name = rs.getString("name");
            int headEmpId = rs.getInt("head_emp_id");
            boolean isActive = rs.getBoolean("is_active");
            departments.put(departmentId, new Department(departmentId, name, headEmpId, isActive));
        }
    }

    Set<Department> getResults() {
        return new HashSet<>(departments.values());
    }
}