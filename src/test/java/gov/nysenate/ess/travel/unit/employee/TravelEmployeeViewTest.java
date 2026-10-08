package gov.nysenate.ess.travel.unit.employee;

import gov.nysenate.ess.core.annotation.UnitTest;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.model.personnel.Gender;
import gov.nysenate.ess.core.model.personnel.ResponsibilityCenter;
import gov.nysenate.ess.core.model.personnel.ResponsibilityHead;
import gov.nysenate.ess.core.model.unit.Address;
import gov.nysenate.ess.core.model.unit.Location;
import gov.nysenate.ess.core.model.unit.LocationId;
import gov.nysenate.ess.core.util.OutputUtils;
import gov.nysenate.ess.travel.department.Department;
import gov.nysenate.ess.travel.department.TravelDepartmentAssigner;
import gov.nysenate.ess.travel.employee.TravelEmployee;
import gov.nysenate.ess.travel.employee.TravelEmployeeView;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;

@Category(UnitTest.class)
public class TravelEmployeeViewTest {

    @Test
    public void submittedTravelerRetainsSupervisorNeededForDepartmentLookup() throws Exception {
        Employee departmentHead = employee(7048, 0);
        Employee supervisor = employee(500, departmentHead.getEmployeeId());
        Employee employee = employee(11168, supervisor.getEmployeeId());
        ResponsibilityHead responsibilityHead = new ResponsibilityHead();
        ResponsibilityCenter responsibilityCenter = new ResponsibilityCenter();
        responsibilityCenter.setCode(300);
        responsibilityCenter.setHead(responsibilityHead);
        employee.setRespCenter(responsibilityCenter);
        employee.setWorkLocation(new Location(new LocationId("A42FB", 'W'),
                new Address("100 State Street"), responsibilityHead, "Office", true));

        TravelEmployee traveler = new TravelEmployee(employee, new Department(departmentHead, Set.of()));
        String json = OutputUtils.toJson(new TravelEmployeeView(traveler));
        TravelEmployee restored = OutputUtils.jsonToObject(json, TravelEmployeeView.class).toTravelEmployee();

        assertEquals(supervisor.getEmployeeId(), restored.getSupervisorId());
        assertEquals(departmentHead.getEmployeeId(), restored.getDeptHeadId());
        Department calculated = new TravelDepartmentAssigner(
                Set.of(supervisor, departmentHead), Set.of(departmentHead.getEmployeeId()), Map.of())
                .getDepartment(restored);
        assertEquals(departmentHead.getEmployeeId(), calculated.getHead().getEmployeeId());
    }

    private static Employee employee(int employeeId, int supervisorId) {
        Employee employee = new Employee();
        employee.setEmployeeId(employeeId);
        employee.setSupervisorId(supervisorId);
        employee.setGender(Gender.M);
        return employee;
    }
}
