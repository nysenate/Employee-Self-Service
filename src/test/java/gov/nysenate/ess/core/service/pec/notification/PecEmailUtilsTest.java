package gov.nysenate.ess.core.service.pec.notification;

import gov.nysenate.ess.core.annotation.UnitTest;
import gov.nysenate.ess.core.model.pec.PersonnelTask;
import gov.nysenate.ess.core.model.pec.TaskAssignmentDetails;
import gov.nysenate.ess.core.model.personnel.Employee;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Category(UnitTest.class)
public class PecEmailUtilsTest {
    @Test
    public void reactInvitationsAndRemindersLinkDirectlyToAssignments() {
        assertNotificationLinks("react", "https://ess.example", "/myinfo/personnel/tasks/assignments");
        assertNotificationLinks(" React ", "https://ess.example/", "/myinfo/personnel/tasks/assignments");
    }

    @Test
    public void angularJsAndFallbackSettingsMatchThePageController() {
        for (String framework : new String[]{"angularjs", "", null, "invalid"}) {
            assertNotificationLinks(framework, "https://ess.example", "/myinfo/personnel/todo");
        }
    }

    @Test
    public void preservesTheDeploymentContextPath() {
        assertNotificationLinks("react", "https://ess.example/ess/", "/myinfo/personnel/tasks/assignments");
    }

    private void assertNotificationLinks(String framework, String domainUrl, String path) {
        var utils = new PecEmailUtils(null, null, null, domainUrl, framework);
        var employee = mock(Employee.class);
        when(employee.getFullName()).thenReturn("Test Employee");
        var task = mock(PersonnelTask.class);
        when(task.getTitle()).thenReturn("Required training");
        var assignment = new TaskAssignmentDetails(1, task);
        String expectedLink = "href=\"" + domainUrl.replaceAll("/+$", "") + path + "\"";

        var invite = utils.getEmail(PecEmailType.INVITE, Optional.of(employee), assignment);
        var singleReminder = utils.getEmail(PecEmailType.REMINDER, employee, List.of(assignment));
        var reminder = utils.getEmail(PecEmailType.REMINDER, employee, List.of(assignment, assignment));

        assertEquals(PecEmailType.INVITE, invite.type());
        assertEquals(PecEmailType.SINGLE_REMINDER, singleReminder.type());
        assertEquals(PecEmailType.REMINDER, reminder.type());
        for (var email : List.of(invite, singleReminder, reminder)) {
            assertTrue(email.html(), email.html().contains(expectedLink));
            assertTrue(email.html().contains("Required training"));
        }
    }
}
