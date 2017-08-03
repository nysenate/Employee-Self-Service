package gov.nysenate.ess.core.controller.api;

import gov.nysenate.ess.core.client.response.base.BaseResponse;
import gov.nysenate.ess.core.client.response.base.SimpleResponse;
import gov.nysenate.ess.core.client.response.base.ViewObjectResponse;
import gov.nysenate.ess.core.client.response.error.ErrorCode;
import gov.nysenate.ess.core.client.response.error.ViewObjectErrorResponse;
import gov.nysenate.ess.core.client.view.AlertInfoView;
import gov.nysenate.ess.core.client.view.alert.ContactBatch;
import gov.nysenate.ess.core.client.view.alert.ContactBatchFactory;
import gov.nysenate.ess.core.dao.alert.AlertInfoDao;
import gov.nysenate.ess.core.model.auth.CorePermission;
import gov.nysenate.ess.core.model.auth.CorePermissionObject;
import gov.nysenate.ess.core.model.alert.AlertInfo;
import gov.nysenate.ess.core.model.alert.AlertInfoNotFound;
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
 * API controller responsible for viewing and saving alert contact info
 */
@RestController
@RequestMapping(value = BaseRestApiCtrl.REST_PATH + "alert-info")
public class AlertInfoCtrl extends BaseRestApiCtrl {

    @Autowired private AlertInfoDao alertInfoDao;
    @Autowired private EmployeeInfoService employeeInfoService;

    /**
     * Get Alert Info API
     * ------------------------------------
     *
     * Get alert info for an employee:
     *      (POST) /api/v1/alert-info
     *
     * Request Params:
     * @param empId int - required - the employee id whose emerg. notif. info will be retrieved
     */
    @RequestMapping(value = "", method = {GET, HEAD})
    public ViewObjectResponse<AlertInfoView> getAlertInfo(
            @RequestParam int empId ) {

        checkPermission(new CorePermission(empId, CorePermissionObject.EMPLOYEE_INFO, GET));

        AlertInfo alertInfo;
        try {
            alertInfo = alertInfoDao.getAlertInfo(empId);
        } catch (AlertInfoNotFound ex) {
            alertInfo = getEmptyAlertInfo(empId);
        }
        Employee employee = employeeInfoService.getEmployee(empId);

        AlertInfoView alertInfoView = new AlertInfoView(alertInfo, employee);

        return new ViewObjectResponse<>(alertInfoView);
    }

    /**
     * Save Alert Info API
     * ------------------------------------
     *
     * Save alert info:
     *      (POST) /api/v1/-alert-info
     *
     * Post Data: json {@link AlertInfoView}
     */
    @RequestMapping(value = "", method = POST)
    public BaseResponse saveAlertInfo(@RequestBody AlertInfoView alertInfoView) {

        checkPermission(new CorePermission(alertInfoView.getEmpId(), CorePermissionObject.EMPLOYEE_INFO, POST));

        AlertInfo alertInfo = alertInfoView.toAlertInfo();

        alertInfoDao.updateAlertInfo(alertInfo);

        return new SimpleResponse(
                true,
                "alert info updated",
                "alert-info-update-success");
    }

    /**
     * Get Alert Contact List Dump Api
     * ------------------------------------
     *
     * Requires Admin permissions.
     *
     * Get alert info for all active senate employees:
     *      (GET) /api/v1/alert-info/contact-dump
     */
    @RequestMapping(value = "contact-dump", method = RequestMethod.GET)
    public ContactBatch generateContactList() {
        checkPermission(new WildcardPermission("admin"));

        List<AlertInfo> alertInfos = alertInfoDao.getAllAlertInfo();
        Map<Integer, AlertInfo> alertInfoMap = alertInfos.stream()
                .collect(Collectors.toMap(AlertInfo::getEmpId, Function.identity()));
        Set<Employee> employees = employeeInfoService.getAllEmployees(true);
        return ContactBatchFactory.create(employees, alertInfoMap);
    }

    @ExceptionHandler(EmployeeNotFoundEx.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    @ResponseBody
    protected ViewObjectErrorResponse handleEmpNotFoundEx(EmployeeNotFoundEx ex) {
        return new ViewObjectErrorResponse(ErrorCode.EMPLOYEE_NOT_FOUND, ex.getEmpId());
    }

    /* --- Internal Methods --- */

    private AlertInfo getEmptyAlertInfo(int empId) {
        return AlertInfo.builder()
                .setEmpId(empId)
                .build();
    }

}
