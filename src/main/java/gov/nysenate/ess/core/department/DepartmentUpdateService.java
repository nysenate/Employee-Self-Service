package gov.nysenate.ess.core.department;

import gov.nysenate.ess.core.dao.security.authentication.LdapDao;
import gov.nysenate.ess.core.model.auth.SenateLdapPerson;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.service.personnel.EmployeeInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class DepartmentUpdateService {

    private static final Logger logger = LoggerFactory.getLogger(DepartmentUpdateService.class);

    private EmployeeInfoService employeeInfoService;
    private LdapDao ldapDao;
    private DepartmentDao departmentDao;

    @Autowired
    public DepartmentUpdateService(EmployeeInfoService employeeInfoService, LdapDao ldapDao,
                                   DepartmentDao departmentDao) {
        this.employeeInfoService = employeeInfoService;
        this.ldapDao = ldapDao;
        this.departmentDao = departmentDao;
    }

    @Scheduled(cron = "${scheduler.department.ess.update:0 * 1 * * *}")
    public void updateDepartments() {
        logger.info("Starting department update");

        Set<Employee> activeEmployees = employeeInfoService.getAllEmployees(true);
        Set<SenateLdapPerson> ldapPeople = new HashSet<>();
        for (Employee emp : activeEmployees) {
            try {
                ldapPeople.add(ldapDao.getPersonByEmpId(emp.getEmployeeId()));
            } catch (IndexOutOfBoundsException ex) {
                logger.info("Unable to find ldap entry for employeeId: " + emp.getEmployeeId());
            }
        }

        Set<LdapDepartment> ldapDepartments = SenateLdapPersonDepartments.forPeople(ldapPeople);
        Set<Department> essDepartments = departmentDao.getDepartments();
        Map<LdapDepartment, Department> essLdapToDeptMap = essDepartments.stream()
                .collect(Collectors.toMap(Department::getLdapDepartment, Function.identity()));
        Map<String, Department> essNameToDeptMap = essDepartments.stream()
                .collect(Collectors.toMap(Department::getName, Function.identity()));

        Set<LdapDepartment> newLdapDepartments = LdapDepartmentComparer.newDepartments(
                essLdapToDeptMap.keySet(), ldapDepartments);
        Set<LdapDepartment> inactiveLdapDepartments = LdapDepartmentComparer.inactiveDepartments(
                essLdapToDeptMap.keySet(), ldapDepartments);
        Set<LdapDepartment> updatedLdapDepartments = LdapDepartmentComparer.updatedDepartments(
                essLdapToDeptMap.keySet(), ldapDepartments);

        // Create new departments, setting the headEmpId if its a new Senator's department.
        Set<Department> newDepartments = newLdapDepartments.stream()
                .map(Department::new)
                .map(d -> d.setHeadEmpId(GetDepartmentHeadId.forSenatorDepartment(d.getName(), activeEmployees)))
                .collect(Collectors.toSet());

        // Deactivate departments which are no longer in ldap.
        Set<Department> inactivatedDepartments = inactiveLdapDepartments.stream()
                .map(essLdapToDeptMap::get)
                .map(d -> d.setActive(false))
                .collect(Collectors.toSet());

        // Update employeeIds in updated departments.
        Set<Department> updatedDepartments = new HashSet<>();
        for (LdapDepartment ldapDept : updatedLdapDepartments) {
            Department essDept = essNameToDeptMap.get(ldapDept.getName());
            updatedDepartments.add(essDept.setLdapDepartment(ldapDept));
        }

        Set<Department> departmentUpdates = new HashSet<>();
        departmentUpdates.addAll(newDepartments);
        departmentUpdates.addAll(inactivatedDepartments);
        departmentUpdates.addAll(updatedDepartments);
        departmentDao.updateDepartments(departmentUpdates);

        logger.info(String.format("Completed department update. Found %s new departments, " +
                        "%s inactivated departments, and %s updated departments",
                newDepartments.size(), inactivatedDepartments.size(), updatedDepartments.size()));
    }
}
