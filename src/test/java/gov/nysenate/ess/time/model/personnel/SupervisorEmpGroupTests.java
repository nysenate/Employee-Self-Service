package gov.nysenate.ess.time.model.personnel;

import gov.nysenate.ess.core.annotation.ProperTest;
import org.junit.Test;

import java.time.LocalDate;

import static org.junit.Assert.*;

@ProperTest
public class SupervisorEmpGroupTests
{
    /**
     * Get all employees should return a set of all employees in the sup emp group.
     * @throws Exception
     */
    @Test
    public void testGetAllEmployees() throws Exception {
        SupervisorEmpGroup empGroup = new SupervisorEmpGroup();
        LocalDate date = LocalDate.now();
        for (int i = 0; i < 10; i++) {
            empGroup.getPrimaryEmployees().put(i, new EmployeeSupInfo(i, 1000, date, date));
            empGroup.getOverrideEmployees().put(1001 + i, new EmployeeSupInfo(1001 + i, 1000, date, date));
            empGroup.getSupOverrideEmployees().put(2000, 2001 + i, new EmployeeSupInfo(2001 + i, 2000, date, date));
            empGroup.getSupOverrideEmployees().put(3000, 3001 + i, new EmployeeSupInfo(3001 + i, 3000, date, date));
        }
        int totalEmployees = 40;
        assertEquals(totalEmployees, empGroup.getAllEmployees().size());
        assertTrue(empGroup.getAllEmployees().contains(new EmployeeSupInfo(9, 1000, date, date)));
        assertTrue(empGroup.getAllEmployees().contains(new EmployeeSupInfo(1002, 1000, date, date)));
        assertTrue(empGroup.getAllEmployees().contains(new EmployeeSupInfo(2002, 2000, date, date)));
        assertTrue(empGroup.getAllEmployees().contains(new EmployeeSupInfo(3002, 3000, date, date)));
    }
}