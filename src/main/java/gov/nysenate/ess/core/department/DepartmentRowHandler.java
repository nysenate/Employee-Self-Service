package gov.nysenate.ess.core.department;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import gov.nysenate.ess.core.dao.base.BaseHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DepartmentRowHandler extends BaseHandler {

    private Map<Integer, Department> departments = new HashMap<>();
    private Multimap<Integer, Integer> deptIdToEmps = HashMultimap.create();

    @Override
    public void processRow(ResultSet rs) throws SQLException {
        int departmentId = rs.getInt("department_id");
        if (!departments.containsKey(departmentId)) {
            String name = rs.getString("name");
            int headEmpId = rs.getInt("head_emp_id");
            boolean isActive = rs.getBoolean("is_active");
            LdapDepartment ldapDepartment = new LdapDepartment(name);
            departments.put(departmentId, new Department(departmentId, ldapDepartment, headEmpId, isActive));
        }
        // Fix the behavior of rs.getInt which returns 0 if the column was null.
        int employeeId = rs.getInt("employee_id");
        if (employeeId != 0) {
            deptIdToEmps.put(departmentId, rs.getInt("employee_id"));
        }
    }

    Set<Department> getResults() {
        Set<Department> depts = new HashSet<>();
        for (Department d : departments.values()) {
            LdapDepartment ldapDepartment = new LdapDepartment(d.getName(), deptIdToEmps.get(d.getId()));
            depts.add(d.setLdapDepartment(ldapDepartment));
        }
        return depts;
    }
}