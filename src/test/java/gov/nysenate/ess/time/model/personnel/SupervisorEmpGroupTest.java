package gov.nysenate.ess.time.model.personnel;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Multimap;
import com.google.common.collect.Table;
import com.google.common.collect.HashMultimap;
import gov.nysenate.ess.core.annotation.UnitTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@Category(UnitTest.class)
public class SupervisorEmpGroupTest
{
    /**
     * Get all employees should return a set of all employees in the sup emp group.
     * @throws Exception
     */
    @Test
    public void testGetAllEmployees() throws Exception {
        SupervisorEmpGroup empGroup = new SupervisorEmpGroup();
        LocalDate date = LocalDate.now();

        Multimap<Integer, EmployeeSupInfo> primaryEmployees = HashMultimap.create();
        Multimap<Integer, EmployeeSupInfo> overrideEmployees = HashMultimap.create();
        Table<Integer, Integer, EmployeeSupInfo> supOverrideEmployees = HashBasedTable.create();
        for (int i = 0; i < 10; i++) {
            primaryEmployees.put(i, new EmployeeSupInfo(i, 1000, date, date));
            overrideEmployees.put(1001 + i, new EmployeeSupInfo(1001 + i, 1000, date, date));
            supOverrideEmployees.put(2000, 2001 + i, new EmployeeSupInfo(2001 + i, 2000, date, date));
            supOverrideEmployees.put(3000, 3001 + i, new EmployeeSupInfo(3001 + i, 3000, date, date));
        }
        empGroup.setPrimaryEmployees(primaryEmployees);
        empGroup.setOverrideEmployees(overrideEmployees);
        empGroup.setSupOverrideEmployees(supOverrideEmployees);

        int totalEmployees = 40;
        assertEquals(totalEmployees, empGroup.getDirectEmployeeSupInfos().size());

        assertTrue(empGroup.getDirectEmployeeSupInfos().contains(new EmployeeSupInfo(9, 1000, date, date)));
        assertTrue(empGroup.getDirectEmployeeSupInfos().contains(new EmployeeSupInfo(1002, 1000, date, date)));
        assertTrue(empGroup.getDirectEmployeeSupInfos().contains(new EmployeeSupInfo(2002, 2000, date, date)));
        assertTrue(empGroup.getDirectEmployeeSupInfos().contains(new EmployeeSupInfo(3002, 3000, date, date)));
    }
}