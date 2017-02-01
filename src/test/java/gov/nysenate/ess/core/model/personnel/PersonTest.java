package gov.nysenate.ess.core.model.personnel;

import gov.nysenate.ess.core.annotation.UnitTest;
import gov.nysenate.ess.core.model.unit.Address;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.time.LocalDate;
import java.time.temporal.ChronoField;

import static org.junit.Assert.*;

@Category(UnitTest.class)
public class PersonTest
{
    @Test
    public void testCopyConstructor() throws Exception {
        Person p = new Person();
        p.setDateOfBirth(LocalDate.now());
        p.setEmail("test@test.com");
        p.setFirstName("Sample");
        p.setGender(Gender.M);
        p.setHomeAddress(new Address("Sample Address"));
        p.setHomePhone("123123123");
        p.setMaritalStatus(MaritalStatus.SINGLE);
        p.setSuffix("Sr");
        p.setTitle("Mr");
        p.setWorkPhone("987987987");

        Person p2 = new Person(p);
        assertEquals(p2.getDateOfBirth(), p.getDateOfBirth());
        assertEquals(p2.getEmail(), p.getEmail());
        assertEquals(p2.getFirstName(), p.getFirstName());
        assertEquals(p2.getFullName(), p.getFullName());
        assertEquals(p2.getGender(), p.getGender());
        assertEquals(p2.getHomeAddress(), p.getHomeAddress());
        assertEquals(p2.getHomePhone(), p.getHomePhone());
        assertEquals(p2.getMaritalStatus(), p.getMaritalStatus());
        assertEquals(p2.getSuffix(), p.getSuffix());
        assertEquals(p2.getTitle(), p.getTitle());
        assertEquals(p2.getWorkPhone(), p.getWorkPhone());
    }

    @Test
    public void testGetAge() throws Exception {
        Person p = new Person();
        LocalDate dob = LocalDate.now().with(ChronoField.YEAR, 1991);
        p.setDateOfBirth(dob);
        long age = LocalDate.now().getYear() - 1991;
        assertEquals(age, (long) p.getAge());
        p.setDateOfBirth(dob.plusDays(1));
        assertEquals(age - 1, (long) p.getAge());
    }

    /** Throws IllegalStateException if dob is null. */
    @Test(expected = IllegalStateException.class)
    public void testGetAge_nullDOB() throws Exception {
        Person nullDob = new Person();
        nullDob.setDateOfBirth(null);
        assertEquals(0, nullDob.getAge());
    }
}