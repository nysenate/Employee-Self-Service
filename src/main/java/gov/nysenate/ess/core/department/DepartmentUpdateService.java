package gov.nysenate.ess.core.department;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Sets;
import gov.nysenate.ess.core.dao.security.authentication.LdapAuthDao;
import gov.nysenate.ess.core.model.auth.SenateLdapPerson;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.service.personnel.EmployeeInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class DepartmentUpdateService {

    private EmployeeInfoService employeeInfoService;
    private LdapAuthDao ldapDao;
    private DepartmentDao departmentDao;

    @Autowired
    public DepartmentUpdateService(EmployeeInfoService employeeInfoService, LdapAuthDao ldapDao,
                                   DepartmentDao departmentDao) {
        this.employeeInfoService = employeeInfoService;
        this.ldapDao = ldapDao;
        this.departmentDao = departmentDao;
    }

    /**
     * [ ] Handle senators
     */


    public void updateDepartments() {
        HashMultimap<String, Employee> ldapDepartments = ldapDepartments();
        Set<Department> ourDepartments = ourDepartments();

        Set<String> ldapDepartmentNames = ldapDepartments.keySet();
        Set<String> ourDepartmentNames = ourDepartments.stream()
                .map(Department::getName)
                .collect(Collectors.toSet());

        Set<String> newDepartmentNames = Sets.difference(ldapDepartmentNames, ourDepartmentNames);
        Set<String> removedDepartmentNames = Sets.difference(ourDepartmentNames, ldapDepartmentNames);
        Set<String> existingDepartmentNames = Sets.intersection(ldapDepartmentNames, ourDepartmentNames);

        Set<Department> newDepartments = createNewDepartments(newDepartmentNames, ldapDepartments);
        Set<Department> inactivatedDepartments = createInactivatedDepartments(removedDepartmentNames, ourDepartments);
        Set<Department> existingDepartments = createExistingDepartments(
                existingDepartmentNames, ourDepartments, ldapDepartments);

        Set<Department> departmentUpdates = new HashSet<>();
        departmentUpdates.addAll(newDepartments);
        departmentUpdates.addAll(inactivatedDepartments);
        departmentUpdates.addAll(existingDepartments);
        departmentDao.updateDepartments(departmentUpdates);
    }


    private HashMultimap<String, Employee> ldapDepartments() {
        HashMultimap<String, Employee> deptToEmployee = HashMultimap.create();
        Set<Employee> employees = employeeInfoService.getAllEmployees(true);
        for (Employee emp : employees) {
            SenateLdapPerson person = ldapDao.getPersonByEmpId(emp.getEmployeeId());
            deptToEmployee.put(person.getDepartment(), emp);
        }
        return deptToEmployee;
    }

    private Set<Department> ourDepartments() {
        return departmentDao.getDepartments();
    }

    private Set<Department> createNewDepartments(Set<String> newDepartmentNames,
                                                 HashMultimap<String, Employee> ldapDepartments) {
        Set<Department> departments = new HashSet<>();
        for (String name : newDepartmentNames) {
            Set<Integer> empIds = ldapDepartments.get(name).stream()
                    .map(Employee::getEmployeeId)
                    .collect(Collectors.toSet());
            departments.add(new Department(name, empIds));
        }
        return departments;
    }

    private Set<Department> createInactivatedDepartments(Set<String> removedDepartments,
                                                         Set<Department> ourDepartments) {
        return ourDepartments.stream()
                .filter(d -> removedDepartments.contains(d.getName()))
                .collect(Collectors.toSet());
    }

    private Set<Department> createExistingDepartments(Set<String> existingDepartmentNames,
                                                      Set<Department> ourDepartments,
                                                      HashMultimap<String, Employee> ldapDepartments) {
        Set<Department> existingDepartments = ourDepartments.stream()
                .filter(d -> existingDepartmentNames.contains(d.getName()))
                .collect(Collectors.toSet());

        for (Department dept : existingDepartments) {
            Set<Integer> empIds = ldapDepartments.get(dept.getName()).stream()
                    .map(Employee::getEmployeeId)
                    .collect(Collectors.toSet());
            dept.setEmployees(empIds);
        }
        return existingDepartments;
    }
}
