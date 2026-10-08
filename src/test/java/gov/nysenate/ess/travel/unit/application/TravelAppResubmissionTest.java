package gov.nysenate.ess.travel.unit.application;

import com.google.common.eventbus.EventBus;
import gov.nysenate.ess.core.annotation.UnitTest;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.travel.authorization.role.TravelRole;
import gov.nysenate.ess.travel.notifications.email.events.TravelPendingReviewEmailEvent;
import gov.nysenate.ess.travel.request.app.*;
import gov.nysenate.ess.travel.request.route.Route;
import gov.nysenate.ess.travel.review.Action;
import gov.nysenate.ess.travel.review.ApplicationReview;
import gov.nysenate.ess.travel.review.ApplicationReviewService;
import gov.nysenate.ess.travel.review.policy.ReviewPolicyRegistry;
import gov.nysenate.ess.travel.review.policy.ReviewPolicyType;
import gov.nysenate.ess.travel.review.view.ActionType;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.transaction.support.TransactionSynchronizationUtils;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@Category(UnitTest.class)
public class TravelAppResubmissionTest {
    private static final int APP_ID = 411;
    private final TravelApplicationService applications = mock(TravelApplicationService.class);
    private final ApplicationReviewService reviews = mock(ApplicationReviewService.class);
    private final EventBus events = mock(EventBus.class);
    private final TravelAppUpdateService service = new TravelAppUpdateService();

    public TravelAppResubmissionTest() {
        ReflectionTestUtils.setField(service, "travelApplicationService", applications);
        ReflectionTestUtils.setField(service, "appReviewService", reviews);
        ReflectionTestUtils.setField(service, "eventBus", events);
    }

    @Before
    public void beginSynchronization() {
        TransactionSynchronizationManager.initSynchronization();
    }

    @After
    public void clearSynchronization() {
        TransactionSynchronizationManager.clearSynchronization();
    }

    @Test
    public void appliesOnlyEditableFieldsToStoredApplicationAndRestartsExistingReview() {
        Employee creator = employee(101);
        Employee traveler = employee(102);
        Employee editor = employee(103);
        LocalDateTime created = LocalDateTime.of(2025, 1, 2, 3, 4);
        TravelApplication original = new TravelApplication.Builder(traveler, 700)
                .withAppId(APP_ID).withCreatedBy(creator).withCreatedDateTime(created)
                .withStatus(new TravelApplicationStatus(AppStatus.DISAPPROVED, "Keep this in the action"))
                .build();
        TravelApplication proposed = new TravelApplication.Builder(employee(999), 999)
                .withAppId(999).withCreatedBy(employee(998))
                .withCreatedDateTime(LocalDateTime.of(2030, 1, 1, 0, 0))
                .withStatus(new TravelApplicationStatus(AppStatus.APPROVED))
                .withPurposeOfTravel(new PurposeOfTravel(gov.nysenate.ess.travel.EventType.PUBLIC_HEARING,
                        "Updated hearing", "Travel"))
                .withRoute(new Route(List.of(), List.of(), false, true))
                .build();
        Action rejection = new Action(55, creator, TravelRole.DEPARTMENT_HEAD, ActionType.DISAPPROVE,
                "Needs details", LocalDateTime.of(2025, 2, 1, 12, 0));
        ApplicationReview review = new ApplicationReview(77, original,
                new ReviewPolicyRegistry().resolve(ReviewPolicyType.STANDARD, 1), TravelRole.NONE,
                List.of(rejection), true);
        when(reviews.getApplicationReviewByAppId(APP_ID)).thenReturn(review);

        assertSame(original, service.resubmitApp(APP_ID, proposed, editor));

        assertEquals(APP_ID, original.getAppId());
        assertSame(traveler, original.getTraveler());
        assertEquals(700, original.getTravelerDeptHeadEmpId());
        assertSame(creator, original.getCreatedBy());
        assertEquals(created, original.getCreatedDateTime());
        assertSame(editor, original.getModifiedBy());
        assertNotNull(original.getModifiedDateTime());
        assertSame(proposed.getPurposeOfTravel(), original.getPurposeOfTravel());
        assertSame(proposed.getRoute(), original.getRoute());
        assertSame(proposed.getAllowances(), original.getAllowances());
        assertSame(proposed.getMealPerDiems(), original.getMealPerDiems());
        assertSame(proposed.getLodgingPerDiems(), original.getLodgingPerDiems());
        assertSame(proposed.getMileagePerDiems(), original.getMileagePerDiems());
        assertSame(proposed.getAttachments(), original.getAttachments());
        assertEquals(AppStatus.DEPARTMENT_HEAD, original.getStatus().status());
        assertEquals("", original.getStatus().note());
        assertEquals(77, review.getAppReviewId());
        assertEquals(ReviewPolicyType.STANDARD, review.policyType());
        assertEquals(1, review.policyVersion());
        assertTrue(review.isShared());
        assertEquals(TravelRole.DEPARTMENT_HEAD, review.pendingReviewerRole());
        assertEquals(List.of(rejection), List.copyOf(review.actions()));
        verify(applications, times(1)).saveApplication(original);
        var order = inOrder(applications, reviews);
        order.verify(applications).lockApplication(APP_ID);
        order.verify(reviews).getApplicationReviewByAppId(APP_ID);
        order.verify(applications).saveApplication(original);
        order.verify(reviews).saveApplicationReview(review);
        verify(reviews).saveApplicationReview(review);
        verifyNoInteractions(events);
        assertEquals(1, TransactionSynchronizationManager.getSynchronizations().size());
        TransactionSynchronizationUtils.triggerAfterCommit();
        verify(events).post(argThat(event -> event instanceof TravelPendingReviewEmailEvent pending
                && pending.getAppReview() == review
                && pending.getAppReview().application() == original
                && pending.getAppReview().application().getPurposeOfTravel() == proposed.getPurposeOfTravel()
                && pending.getAppReview().application().getRoute() == proposed.getRoute()
                && pending.getAppReview().pendingReviewerRole() == TravelRole.DEPARTMENT_HEAD));
    }

    @Test
    public void otherStatusesConflictBeforeAnyWriteOrEvent() {
        for (AppStatus status : AppStatus.values()) {
            if (status == AppStatus.DISAPPROVED) continue;
            reset(reviews, applications, events);
            TravelApplication original = new TravelApplication.Builder(employee(102), 700)
                    .withAppId(APP_ID).withStatus(new TravelApplicationStatus(status)).build();
            ApplicationReview review = new ApplicationReview(original,
                    new ReviewPolicyRegistry().resolve(ReviewPolicyType.STANDARD, 1));
            when(reviews.getApplicationReviewByAppId(APP_ID)).thenReturn(review);
            try {
                service.resubmitApp(APP_ID, new TravelApplication.Builder(employee(999), 999).build(), employee(103));
                fail("Expected conflict for " + status);
            } catch (TravelResubmissionConflictException expected) {
                assertEquals(status, original.getStatus().status());
                verify(applications).lockApplication(APP_ID);
                verify(applications, never()).saveApplication(any());
                verifyNoInteractions(events);
                verify(reviews, never()).saveApplicationReview(any());
                assertTrue(TransactionSynchronizationManager.getSynchronizations().isEmpty());
            }
        }
    }

    @Test
    public void secretaryFirstPolicyReturnsToTravelUnit() {
        TravelApplication original = new TravelApplication.Builder(employee(102), 700)
                .withAppId(APP_ID).withStatus(new TravelApplicationStatus(AppStatus.DISAPPROVED, "reason"))
                .build();
        ApplicationReview review = new ApplicationReview(78, original,
                new ReviewPolicyRegistry().resolve(ReviewPolicyType.SECRETARY_ONLY, 1),
                TravelRole.NONE, List.of(), false);
        when(reviews.getApplicationReviewByAppId(APP_ID)).thenReturn(review);

        service.resubmitApp(APP_ID, new TravelApplication.Builder(employee(999), 999).build(), employee(103));

        assertEquals(TravelRole.SECRETARY_OF_THE_SENATE, review.pendingReviewerRole());
        assertEquals(AppStatus.TRAVEL_UNIT, original.getStatus().status());
        assertEquals("", original.getStatus().note());
    }

    @Test
    public void rollbackDoesNotDispatchNotification() {
        ApplicationReview review = rejectedReview();
        when(reviews.getApplicationReviewByAppId(APP_ID)).thenReturn(review);

        service.resubmitApp(APP_ID, proposed(), employee(103));
        verifyNoInteractions(events);
        TransactionSynchronizationUtils.triggerAfterCompletion(TransactionSynchronization.STATUS_ROLLED_BACK);
        verifyNoInteractions(events);
    }

    @Test
    public void reviewSaveFailureRegistersNoNotification() {
        ApplicationReview review = rejectedReview();
        when(reviews.getApplicationReviewByAppId(APP_ID)).thenReturn(review);
        doThrow(new IllegalStateException("save failed")).when(reviews).saveApplicationReview(review);

        try {
            service.resubmitApp(APP_ID, proposed(), employee(103));
            fail("Expected review save failure");
        } catch (IllegalStateException expected) {
            assertEquals("save failed", expected.getMessage());
        }
        assertTrue(TransactionSynchronizationManager.getSynchronizations().isEmpty());
        verifyNoInteractions(events);
    }

    @Test
    public void dispatchFailureAfterCommitDoesNotThrow() {
        ApplicationReview review = rejectedReview();
        when(reviews.getApplicationReviewByAppId(APP_ID)).thenReturn(review);
        doThrow(new IllegalStateException("mail unavailable")).when(events).post(any());

        assertSame(review.application(), service.resubmitApp(APP_ID, proposed(), employee(103)));
        verifyNoInteractions(events);
        TransactionSynchronizationUtils.triggerAfterCommit();
        verify(events, times(1)).post(any(TravelPendingReviewEmailEvent.class));
    }

    private static ApplicationReview rejectedReview() {
        TravelApplication original = new TravelApplication.Builder(employee(102), 700)
                .withAppId(APP_ID).withStatus(new TravelApplicationStatus(AppStatus.DISAPPROVED, "reason"))
                .build();
        return new ApplicationReview(77, original,
                new ReviewPolicyRegistry().resolve(ReviewPolicyType.STANDARD, 1),
                TravelRole.NONE, List.of(), false);
    }

    private static TravelApplication proposed() {
        return new TravelApplication.Builder(employee(102), 700)
                .withPurposeOfTravel(new PurposeOfTravel(gov.nysenate.ess.travel.EventType.PUBLIC_HEARING,
                        "Edited", "Details"))
                .withRoute(new Route(List.of(), List.of(), false, true))
                .build();
    }

    private static Employee employee(int id) {
        Employee employee = new Employee();
        employee.setEmployeeId(id);
        return employee;
    }
}
