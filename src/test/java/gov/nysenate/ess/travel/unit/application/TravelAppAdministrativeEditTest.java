package gov.nysenate.ess.travel.unit.application;

import com.google.common.eventbus.EventBus;
import gov.nysenate.ess.core.annotation.UnitTest;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.travel.EventType;
import gov.nysenate.ess.travel.request.app.*;
import gov.nysenate.ess.travel.review.ApplicationReviewService;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@Category(UnitTest.class)
public class TravelAppAdministrativeEditTest {
    @Test
    public void editsEveryStatusWithoutChangingIdentityReviewOrSendingNotifications() {
        for (AppStatus status : AppStatus.values()) {
            var applications = mock(TravelApplicationService.class);
            var reviews = mock(ApplicationReviewService.class);
            var events = mock(EventBus.class);
            var service = new TravelAppUpdateService();
            ReflectionTestUtils.setField(service, "travelApplicationService", applications);
            ReflectionTestUtils.setField(service, "appReviewService", reviews);
            ReflectionTestUtils.setField(service, "eventBus", events);
            Employee creator = employee(1), traveler = employee(2), editor = employee(3);
            LocalDateTime created = LocalDateTime.of(2024, 1, 1, 12, 0);
            var original = new TravelApplication.Builder(traveler, 700)
                    .withAppId(42).withCreatedBy(creator).withCreatedDateTime(created)
                    .withStatus(new TravelApplicationStatus(status, "Existing status note")).build();
            var proposed = new TravelApplication.Builder(employee(999), 999)
                    .withAppId(999).withCreatedBy(employee(999))
                    .withStatus(new TravelApplicationStatus(AppStatus.APPROVED))
                    .withPurposeOfTravel(new PurposeOfTravel(EventType.PUBLIC_HEARING, "Corrected", "Details"))
                    .build();
            when(applications.getTravelApplication(42)).thenReturn(original);

            assertSame(original, service.editTravelApp(42, proposed, editor));

            assertEquals(42, original.getAppId());
            assertSame(traveler, original.getTraveler());
            assertSame(creator, original.getCreatedBy());
            assertEquals(700, original.getTravelerDeptHeadEmpId());
            assertEquals(created, original.getCreatedDateTime());
            assertEquals(status, original.getStatus().status());
            assertEquals("Existing status note", original.getStatus().note());
            assertSame(editor, original.getModifiedBy());
            assertNotNull(original.getModifiedDateTime());
            assertSame(proposed.getPurposeOfTravel(), original.getPurposeOfTravel());
            assertSame(proposed.getRoute(), original.getRoute());
            assertSame(proposed.getAllowances(), original.getAllowances());
            assertSame(proposed.getMealPerDiems(), original.getMealPerDiems());
            assertSame(proposed.getLodgingPerDiems(), original.getLodgingPerDiems());
            assertSame(proposed.getMileagePerDiems(), original.getMileagePerDiems());
            assertSame(proposed.getAttachments(), original.getAttachments());
            var order = inOrder(applications);
            order.verify(applications).lockApplication(42);
            order.verify(applications).getTravelApplication(42);
            order.verify(applications).saveApplication(original);
            verifyNoInteractions(reviews, events);
        }
    }

    @Test
    public void editUsesLocalTransactionForLockAndAllApplicationWrites() throws Exception {
        var transaction = TravelAppUpdateService.class
                .getMethod("editTravelApp", int.class, TravelApplication.class, Employee.class)
                .getAnnotation(Transactional.class);
        assertNotNull(transaction);
        assertEquals("localTxManager", transaction.value());
    }

    private static Employee employee(int id) {
        Employee employee = new Employee();
        employee.setEmployeeId(id);
        return employee;
    }
}
