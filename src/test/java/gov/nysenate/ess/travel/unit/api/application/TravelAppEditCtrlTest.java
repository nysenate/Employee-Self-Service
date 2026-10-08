package gov.nysenate.ess.travel.unit.api.application;

import gov.nysenate.ess.core.annotation.UnitTest;
import gov.nysenate.ess.core.client.response.base.ViewObjectResponse;
import gov.nysenate.ess.core.client.response.error.ErrorCode;
import gov.nysenate.ess.core.client.response.error.ErrorResponse;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.service.personnel.EmployeeInfoService;
import gov.nysenate.ess.core.util.OutputUtils;
import gov.nysenate.ess.travel.api.application.TravelAppEditCtrl;
import gov.nysenate.ess.travel.authorization.permission.SimpleTravelPermission;
import gov.nysenate.ess.travel.authorization.permission.TravelAdminPermissionFactory;
import gov.nysenate.ess.travel.authorization.permission.TravelSosPermissionFactory;
import gov.nysenate.ess.travel.authorization.role.TravelRole;
import com.google.common.collect.ImmutableSet;
import gov.nysenate.ess.travel.employee.TravelEmployeeService;
import gov.nysenate.ess.travel.request.app.*;
import gov.nysenate.ess.travel.request.draft.DraftView;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.Permission;
import org.apache.shiro.subject.Subject;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@Category(UnitTest.class)
public class TravelAppEditCtrlTest {
    private final TravelApplication application = EditDraftFixture.application();
    private final TravelApplicationService appService = mock(TravelApplicationService.class);
    private final TravelAppUpdateService updateService = mock(TravelAppUpdateService.class);
    private final EmployeeInfoService employeeService = mock(EmployeeInfoService.class);
    private final TravelEmployeeService travelerService = mock(TravelEmployeeService.class);
    private final Subject subject = mock(Subject.class);
    private final TravelAppEditCtrl controller = new TravelAppEditCtrl() {
        @Override protected Subject getSubject() { return subject; }
        @Override protected int getSubjectEmployeeId() { return EditDraftFixture.CREATOR_ID; }
    };

    @Before
    public void setUp() throws Exception {
        ReflectionTestUtils.setField(controller, "appService", appService);
        ReflectionTestUtils.setField(controller, "appUpdateService", updateService);
        ReflectionTestUtils.setField(controller, "employeeInfoService", employeeService);
        ReflectionTestUtils.setField(controller, "travelEmployeeService", travelerService);
        when(appService.getTravelApplication(EditDraftFixture.APP_ID)).thenReturn(application);
        when(travelerService.loadTravelEmployee(application.getTraveler()))
                .thenReturn(EditDraftFixture.traveler());
        when(employeeService.getEmployee(EditDraftFixture.CREATOR_ID))
                .thenReturn(EditDraftFixture.employee(EditDraftFixture.CREATOR_ID));
    }

    @Test
    public void getEditInitializesUnsavedDraftFromStoredApplication() throws Exception {
        allowEmployee(EditDraftFixture.CREATOR_ID);
        DraftView view = (DraftView) ((ViewObjectResponse<?>) controller
                .editApplication(EditDraftFixture.APP_ID)).result;
        DraftView decoded = OutputUtils.jsonMapper.readValue(
                OutputUtils.jsonMapper.writeValueAsString(view), DraftView.class);
        assertEquals(0, decoded.getId());
        assertEquals(EditDraftFixture.CREATOR_ID, decoded.getUserEmpId());
        assertEquals(EditDraftFixture.TRAVELER_ID, decoded.getTraveler().getEmployeeId());
        assertEquals("Budget hearing", decoded.getAmendment().getPurposeOfTravel().getEventName());
        assertEquals(1, decoded.getAmendment().getRoute().getOutboundLegs().size());
        assertEquals(1.0, decoded.getAmendment().getAllowances().getTolls(), 0.0);
        assertEquals(1, decoded.getAmendment().getMealPerDiems().getAllMealPerDiems().size());
        assertEquals(1, decoded.getAmendment().getLodgingPerDiems().getAllLodgingPerDiems().size());
        assertEquals(1, decoded.getAmendment().getMileagePerDiems().getAllPerDiems().size());
        assertEquals(EditDraftFixture.ATTACHMENT_ID.toString(),
                decoded.getAmendment().getAttachments().get(0).getFilename());
    }

    @Test
    public void originalSubmitterCanPostResubmission() throws Exception {
        assertPostAllowedFor(EditDraftFixture.CREATOR_ID);
    }

    @Test
    public void travelerCanPostResubmissionWhenSomeoneElseSubmitted() throws Exception {
        assertPostAllowedFor(EditDraftFixture.TRAVELER_ID);
    }

    @Test
    public void unrelatedEmployeeIsDeniedBeforeConversionOrSave() {
        allowEmployee(999);
        try {
            controller.saveAndResubmitEditedApplication(EditDraftFixture.APP_ID, new DraftView());
            fail("Expected authorization failure");
        } catch (AuthorizationException expected) {
            verifyNoInteractions(employeeService, updateService);
        }
    }

    @Test
    public void resubmissionConflictMapsToHttp409AndTravelErrorCode() throws Exception {
        ResponseStatus status = TravelAppEditCtrl.class
                .getMethod("handleResubmissionConflict", TravelResubmissionConflictException.class)
                .getAnnotation(ResponseStatus.class);
        assertEquals(HttpStatus.CONFLICT, status.value());
        ErrorResponse response = controller.handleResubmissionConflict(
                new TravelResubmissionConflictException(EditDraftFixture.APP_ID));
        assertEquals(ErrorCode.TRAVEL_RESUBMISSION_CONFLICT, response.getErrorCode());
    }

    @Test
    public void administrativeSaveRequiresAdminPermissionEvenForOriginalSubmitter() {
        allowEmployee(EditDraftFixture.CREATOR_ID);
        doThrow(new AuthorizationException("Admin required")).when(subject)
                .checkPermission(SimpleTravelPermission.TRAVEL_UI_EDIT_APP.getPermission());
        try {
            controller.saveEditedApplication(EditDraftFixture.APP_ID, new DraftView());
            fail("Expected authorization failure");
        } catch (AuthorizationException expected) {
            verifyNoInteractions(updateService, employeeService);
        }
    }

    @Test
    public void adminCanSaveWithoutResubmitting() throws Exception {
        allowEmployee(EditDraftFixture.CREATOR_ID);
        DraftView draft = (DraftView) ((ViewObjectResponse<?>) controller
                .editApplication(EditDraftFixture.APP_ID)).result;
        controller.saveEditedApplication(EditDraftFixture.APP_ID, draft);
        verify(subject).checkPermission(SimpleTravelPermission.TRAVEL_UI_EDIT_APP.getPermission());
        verify(updateService).editTravelApp(eq(EditDraftFixture.APP_ID), any(), any());
        verify(updateService, never()).resubmitApp(anyInt(), any(), any());
    }

    @Test
    public void adminAndDelegatedAdminButNotSecretaryReceiveEditPermission() {
        Permission edit = SimpleTravelPermission.TRAVEL_UI_EDIT_APP.getPermission();
        var admin = new TravelAdminPermissionFactory();
        var secretary = new TravelSosPermissionFactory();
        Employee employee = new Employee();
        assertTrue(admin.getPermissions(employee, ImmutableSet.of(TravelRole.TRAVEL_ADMIN))
                .stream().anyMatch(permission -> permission.implies(edit)));
        assertTrue(admin.getPermissions(employee, ImmutableSet.of(TravelRole.TRAVEL_ADMIN, TravelRole.DELEGATE))
                .stream().anyMatch(permission -> permission.implies(edit)));
        assertFalse(secretary.getPermissions(employee, ImmutableSet.of(TravelRole.SECRETARY_OF_THE_SENATE))
                .stream().anyMatch(permission -> permission.implies(edit)));
    }

    private void assertPostAllowedFor(int employeeId) throws Exception {
        allowEmployee(employeeId);
        DraftView view = (DraftView) ((ViewObjectResponse<?>) controller
                .editApplication(EditDraftFixture.APP_ID)).result;
        controller.saveAndResubmitEditedApplication(EditDraftFixture.APP_ID, view);
        verify(updateService).resubmitApp(eq(EditDraftFixture.APP_ID), any(TravelApplication.class), any(Employee.class));
    }

    private void allowEmployee(int employeeId) {
        when(subject.isPermitted(any(Permission.class))).thenAnswer(invocation ->
                invocation.<Permission>getArgument(0).toString().contains(String.valueOf(employeeId)));
    }
}
