package gov.nysenate.ess.core.model.personnel;

import gov.nysenate.ess.core.annotation.UnitTest;
import gov.nysenate.ess.core.model.payroll.PayType;
import gov.nysenate.ess.core.model.unit.Address;
import gov.nysenate.ess.core.model.unit.Location;
import gov.nysenate.ess.core.model.unit.LocationId;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@Category(UnitTest.class)
public class EmployeeTest
{
    /** A senator is an employee with an agency code of 04210 */
    @Test
    public void testIsSenator() throws Exception {
        Employee emp = new Employee();
        assertFalse(emp.isSenator());

        ResponsibilityCenter resp = new ResponsibilityCenter();
        Agency agency = new Agency();
        resp.setAgency(agency);
        emp.setRespCenter(resp);

        agency.setCode("04210");
        assertTrue(emp.isSenator());

        agency.setCode("XXXXX");
        assertFalse(emp.isSenator());
    }

    /** Return false for isSenator if the resp ctr and/or agency is null. */
    @Test
    public void testIsSenator_returnsFalseIfRespCtrOrAgencyIsNull() throws Exception {
        Employee emp = new Employee();
        emp.setRespCenter(null);
        assertFalse(emp.isSenator());

        ResponsibilityCenter resp = new ResponsibilityCenter();
        resp.setAgency(null);
        emp.setRespCenter(resp);
        assertFalse(emp.isSenator());
    }

    /** Copy constructor should make identical employee object */
    @Test
    public void testCopyConstructor() throws Exception {
        Employee emp1 = new Employee();
        emp1.setActive(true);
        emp1.setDateOfBirth(LocalDate.now());
        emp1.setEmail("test@test.com");
        emp1.setEmployeeId(1);
        emp1.setFirstName("Sample");
        emp1.setGender(Gender.M);
        emp1.setHomeAddress(new Address("Sample Address"));
        emp1.setHomePhone("123123123");
        emp1.setJobTitle("Test Case");
        emp1.setMaritalStatus(MaritalStatus.SINGLE);
        emp1.setNid("N10101");
        emp1.setPayType(PayType.RA);
        emp1.setRespCenter(new ResponsibilityCenter());
        emp1.setSuffix("Sr");
        emp1.setSupervisorId(2);
        emp1.setTitle("Mr");
        emp1.setUid("sample");
        emp1.setUpdateDateTime(LocalDateTime.now());
        emp1.setWorkLocation(new Location(new LocationId("A42FB", 'W')));
        emp1.setWorkPhone("987987987");

        Employee emp2 = new Employee(emp1);
        assertEquals(emp2.isActive(), emp1.isActive());
        assertEquals(emp2.getDateOfBirth(), emp1.getDateOfBirth());
        assertEquals(emp2.getEmail(), emp1.getEmail());
        assertEquals(emp2.getEmployeeId(), emp1.getEmployeeId());
        assertEquals(emp2.getFirstName(), emp1.getFirstName());
        assertEquals(emp2.getFullName(), emp1.getFullName());
        assertEquals(emp2.getGender(), emp1.getGender());
        assertEquals(emp2.getHomeAddress(), emp1.getHomeAddress());
        assertEquals(emp2.getHomePhone(), emp1.getHomePhone());
        assertEquals(emp2.getJobTitle(), emp1.getJobTitle());
        assertEquals(emp2.getMaritalStatus(), emp1.getMaritalStatus());
        assertEquals(emp2.getNid(), emp1.getNid());
        assertEquals(emp2.getPayType(), emp1.getPayType());
        assertEquals(emp2.getRespCenter(), emp1.getRespCenter());
        assertEquals(emp2.getSuffix(), emp1.getSuffix());
        assertEquals(emp2.getSupervisorId(), emp1.getSupervisorId());
        assertEquals(emp2.getTitle(), emp1.getTitle());
        assertEquals(emp2.getUid(), emp1.getUid());
        assertEquals(emp2.getWorkLocation(), emp1.getWorkLocation());
        assertEquals(emp2.getWorkPhone(), emp1.getWorkPhone());
    }
}
