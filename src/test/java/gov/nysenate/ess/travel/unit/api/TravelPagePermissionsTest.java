package gov.nysenate.ess.travel.unit.api;

import gov.nysenate.ess.core.annotation.UnitTest;
import gov.nysenate.ess.travel.api.DraftCtrl;
import gov.nysenate.ess.travel.authorization.permission.SimpleTravelPermission;
import gov.nysenate.ess.travel.request.draft.*;
import gov.nysenate.ess.travel.review.controller.ApplicationReviewCtrl;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.Permission;
import org.apache.shiro.subject.Subject;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Category(UnitTest.class)
public class TravelPagePermissionsTest {
    private final Subject subject = mock(Subject.class);
    private final DraftService drafts = mock(DraftService.class);
    private final DraftCtrl controller = new DraftCtrl() {
        @Override protected Subject getSubject() { return subject; }
        @Override protected int getSubjectEmployeeId() { return 123; }
    };

    @Before
    public void setUp() {
        ReflectionTestUtils.setField(controller, "draftService", drafts);
    }

    @Test
    public void deniedDraftRequestsStopBeforeReadingBodiesOrCallingServices() {
        doThrow(new AuthorizationException()).when(subject).checkPermission(any(Permission.class));
        assertThrows(AuthorizationException.class, () -> controller.createDraft());
        assertThrows(AuthorizationException.class, () -> controller.getUsersDrafts());
        assertThrows(AuthorizationException.class, () -> controller.getDraft(42));
        assertThrows(AuthorizationException.class, () -> controller.deleteDraft(42));
        assertThrows(AuthorizationException.class, () -> controller.saveDraft(null));
        assertThrows(AuthorizationException.class, () -> controller.submitDraft(null));
        assertThrows(AuthorizationException.class, () -> controller.patchDraftApp(null));
        assertThrows(AuthorizationException.class, () -> controller.addAttachments(null));
        verifyNoInteractions(drafts);
    }

    @Test
    public void existingDraftMustBelongToAuthenticatedUserBeforeSaving() throws Exception {
        DraftView body = mock(DraftView.class);
        Draft draft = mock(Draft.class);
        when(body.toDraft()).thenReturn(draft);
        when(draft.getId()).thenReturn(42);
        doThrow(new AuthorizationException()).when(drafts).getDraft(42, 123);

        assertThrows(AuthorizationException.class, () -> controller.saveDraft(body));

        verify(subject).checkPermission(SimpleTravelPermission.TRAVEL_SUBMIT_APP.getPermission());
        verify(draft).setUserEmpId(123);
        verify(drafts, never()).saveDraft(any());
    }

    @Test
    public void allowedDraftDeletionIsScopedToAuthenticatedUser() {
        controller.deleteDraft(42);
        verify(subject).checkPermission(SimpleTravelPermission.TRAVEL_SUBMIT_APP.getPermission());
        verify(drafts).deleteDraft(42, 123);
    }

    @Test
    public void deniedReviewHistoryStopsBeforeLoadingReviews() {
        ApplicationReviewCtrl reviews = new ApplicationReviewCtrl() {
            @Override protected Subject getSubject() { return subject; }
        };
        doThrow(new AuthorizationException()).when(subject).checkPermission(any(Permission.class));
        assertThrows(AuthorizationException.class, () -> reviews.reviewHistory(null, null, null));
        verify(subject).checkPermission(SimpleTravelPermission.TRAVEL_UI_REVIEW_HISTORY.getPermission());
    }
}
