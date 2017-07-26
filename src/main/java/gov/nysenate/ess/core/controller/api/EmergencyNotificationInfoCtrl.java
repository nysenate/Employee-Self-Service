package gov.nysenate.ess.core.controller.api;

import gov.nysenate.ess.core.client.response.base.BaseResponse;
import gov.nysenate.ess.core.client.response.base.SimpleResponse;
import gov.nysenate.ess.core.client.response.base.ViewObjectResponse;
import gov.nysenate.ess.core.client.response.error.ErrorCode;
import gov.nysenate.ess.core.client.response.error.ViewObjectErrorResponse;
import gov.nysenate.ess.core.client.view.EmergencyNotificationInfoView;
import gov.nysenate.ess.core.client.view.emergency_notification.ContactBatch;
import gov.nysenate.ess.core.client.view.emergency_notification.ContactBatchFactory;
import gov.nysenate.ess.core.dao.emergency_notification.EmergencyNotificationInfoDao;
import gov.nysenate.ess.core.model.auth.CorePermission;
import gov.nysenate.ess.core.model.auth.CorePermissionObject;
import gov.nysenate.ess.core.model.emergency_notification.EmergencyNotificationInfo;
import gov.nysenate.ess.core.model.emergency_notification.EmergencyNotificationInfoNotFound;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.model.personnel.EmployeeNotFoundEx;
import gov.nysenate.ess.core.service.personnel.EmployeeInfoService;
import org.apache.shiro.authz.permission.WildcardPermission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.HEAD;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * API controller responsible for viewing and saving emergency notification contact info
 */
@RestController
@RequestMapping(value = BaseRestApiCtrl.REST_PATH + "emergency-notification-info")
public class EmergencyNotificationInfoCtrl extends BaseRestApiCtrl {

    @Autowired private EmergencyNotificationInfoDao eniDao;
    @Autowired private EmployeeInfoService employeeInfoService;

    /**
     * Get Emergency Notification Info API
     * ------------------------------------
     *
     * Get emergency notification info for an employee:
     *      (POST) /api/v1/emergency-notification-info
     *
     * Request Params:
     * @param empId int - required - the employee id whose emerg. notif. info will be retrieved
     */
    @RequestMapping(value = "", method = {GET, HEAD})
    public ViewObjectResponse<EmergencyNotificationInfoView> getEmergencyNotificationInfo(
            @RequestParam int empId ) {

        checkPermission(new CorePermission(empId, CorePermissionObject.EMPLOYEE_INFO, GET));

        EmergencyNotificationInfo eni;
        try {
            eni = eniDao.getEmergencyNotificationInfo(empId);
        } catch (EmergencyNotificationInfoNotFound ex) {
            eni = getEmptyENI(empId);
        }
        Employee employee = employeeInfoService.getEmployee(empId);

        EmergencyNotificationInfoView eniView = new EmergencyNotificationInfoView(eni, employee);

        return new ViewObjectResponse<>(eniView);
    }

    /**
     * Save Emergency Notification Info API
     * ------------------------------------
     *
     * Save emergency notification info:
     *      (POST) /api/v1/emergency-notification-info
     *
     * Post Data: json {@link EmergencyNotificationInfoView}
     */
    @RequestMapping(value = "", method = POST)
    public BaseResponse saveEmergencyNotificationInfo(@RequestBody EmergencyNotificationInfoView eni) {

        checkPermission(new CorePermission(eni.getEmpId(), CorePermissionObject.EMPLOYEE_INFO, POST));

        EmergencyNotificationInfo emergencyNotificationInfo = eni.toEmergencyNotificationInfo();

        eniDao.updateEmergencyNotificationInfo(emergencyNotificationInfo);

        return new SimpleResponse(
                true,
                "emergency notification info updated",
                "eni-update-success");
    }

    /**
     * Get Emergency Notification Contact List Dump Api
     * ------------------------------------
     *
     * Requires Admin permissions.
     *
     * Get emergency notification info for all active senate employees:
     *      (GET) /api/v1/emergency-notification-info/contact-dump
     */
    @RequestMapping(value = "contact-dump", method = RequestMethod.GET)
    public ContactBatch generateContactList() {
        checkPermission(new WildcardPermission("admin"));

        List<EmergencyNotificationInfo> enis = eniDao.getAllEmergencyNotificationInfo();
        Map<Integer, EmergencyNotificationInfo> enisMap = enis.stream()
                .collect(Collectors.toMap(EmergencyNotificationInfo::getEmpId, Function.identity()));
        Set<Employee> employees = employeeInfoService.getAllEmployees(true);
        return ContactBatchFactory.create(employees, enisMap);
    }

    @ExceptionHandler(EmployeeNotFoundEx.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    @ResponseBody
    protected ViewObjectErrorResponse handleEmpNotFoundEx(EmployeeNotFoundEx ex) {
        return new ViewObjectErrorResponse(ErrorCode.EMPLOYEE_NOT_FOUND, ex.getEmpId());
    }

    /* --- Internal Methods --- */

    private EmergencyNotificationInfo getEmptyENI(int empId) {
        return EmergencyNotificationInfo.builder()
                .setEmpId(empId)
                .build();
    }

}
