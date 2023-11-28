package gov.nysenate.ess.core.controller.api;

import gov.nysenate.ess.core.client.response.base.BaseResponse;
import gov.nysenate.ess.core.client.response.base.SimpleResponse;
import gov.nysenate.ess.core.client.response.base.ViewObjectResponse;
import gov.nysenate.ess.core.client.response.error.ErrorCode;
import gov.nysenate.ess.core.client.response.error.ViewObjectErrorResponse;
import gov.nysenate.ess.core.client.view.AlertInfoView;
import gov.nysenate.ess.core.client.view.InvalidAlertInfoView;
import gov.nysenate.ess.core.client.view.alert.ContactBatch;
import gov.nysenate.ess.core.client.view.alert.ContactBatchFactory;
import gov.nysenate.ess.core.client.view.alert.SendWordNowCsv;
import gov.nysenate.ess.core.dao.alert.AlertInfoDao;
import gov.nysenate.ess.core.model.auth.CorePermission;
import gov.nysenate.ess.core.model.auth.CorePermissionObject;
import gov.nysenate.ess.core.model.alert.AlertInfo;
import gov.nysenate.ess.core.model.alert.AlertInfoNotFound;
import gov.nysenate.ess.core.model.auth.SimpleEssPermission;
import gov.nysenate.ess.core.model.base.InvalidRequestParamEx;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.model.personnel.EmployeeNotFoundEx;
import gov.nysenate.ess.core.model.alert.InvalidAlertInfoEx;
import gov.nysenate.ess.core.service.alert.AlertInfoValidation;
import gov.nysenate.ess.core.service.personnel.EmployeeInfoService;
import gov.nysenate.ess.core.util.OutputUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
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
     * ------------------
     * <p>
     * Get alert info for an employee:
     * (POST) /api/v1/alert-info
     * <p>
     * Request Params:
     *
     * @param empId int - required - the employee id whose alert info will be retrieved
     */
    @RequestMapping(value = "", method = {GET, HEAD})
    public ViewObjectResponse<AlertInfoView> getAlertInfo(
            @RequestParam int empId) {

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
     * -------------------
     * <p>
     * Save alert info:
     * (POST) /api/v1/-alert-info
     * <p>
     * Post Data: json {@link AlertInfoView}
     */
    @RequestMapping(value = "", method = POST)
    public BaseResponse saveAlertInfo(@RequestBody AlertInfoView alertInfoView) throws InvalidAlertInfoEx {

        checkPermission(new CorePermission(alertInfoView.getEmpId(), CorePermissionObject.EMPLOYEE_INFO, POST));

        AlertInfo alertInfo = alertInfoView.toAlertInfo();
        Employee employee = employeeInfoService.getEmployee(alertInfo.getEmpId());
        AlertInfoValidation.validateAlertInfo(alertInfo, employee);
        alertInfoDao.updateAlertInfo(alertInfo);

        return new SimpleResponse(
                true,
                "alert info updated",
                "alert-info-update-success");
    }

    /**
     * Get Alert Contact List Dump Api
     * -------------------------------
     * <p>
     * Requires Admin permissions.
     * <p>
     * Get an alert info csv dump for all active senate employees:
     * (GET) /api/v1/alert-info/contact-dump
     * <p>
     * Request Param:
     *
     * @param format string - Specifies which format the dump should be returned in. One of "CSV" or "XML".
     */
    @RequestMapping(value = "contact-dump", method = RequestMethod.GET)
    public void generateContactList(@RequestParam(defaultValue = "CSV") String format, WebRequest request,
                                    HttpServletResponse response) throws IOException {
        checkPermission(SimpleEssPermission.ADMIN.getPermission());

        List<AlertInfo> alertInfos = alertInfoDao.getAllAlertInfo();
        Map<Integer, AlertInfo> alertInfoMap = alertInfos.stream()
                .collect(Collectors.toMap(AlertInfo::getEmpId, Function.identity()));
        Set<Employee> employees = employeeInfoService.getAllEmployees(true);

        if (format.equalsIgnoreCase("CSV")) {
            response.setContentType("text/plain");
            SendWordNowCsv csv = new SendWordNowCsv();
            csv.createCsv(response, employees, alertInfoMap);
            response.setStatus(200);
        } else if (format.equalsIgnoreCase("XML")) {
            ContactBatch batch = ContactBatchFactory.create(employees, alertInfoMap);
            response.setContentType("text/xml");
            response.getWriter().print(OutputUtils.toXml(batch));
            response.setStatus(200);
        } else {
            throw new InvalidRequestParamEx(format, "format", "String", "format must be either CSV or XML.");
        }
    }

    @ExceptionHandler(EmployeeNotFoundEx.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    @ResponseBody
    protected ViewObjectErrorResponse handleEmpNotFoundEx(EmployeeNotFoundEx ex) {
        return new ViewObjectErrorResponse(ErrorCode.EMPLOYEE_NOT_FOUND, ex.getEmpId());
    }

    /**
     * Handle submissions of invalid alert info
     */
    @ExceptionHandler(InvalidAlertInfoEx.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public BaseResponse handleInvalidTimeRecordException(InvalidAlertInfoEx ex) {
        Employee employee = employeeInfoService.getEmployee(ex.getAlertInfo().getEmpId());
        return new ViewObjectErrorResponse(ErrorCode.INVALID_ALERT_INFO, new InvalidAlertInfoView(ex, employee));
    }

    /* --- Internal Methods --- */

    private AlertInfo getEmptyAlertInfo(int empId) {
        return AlertInfo.builder()
                .setEmpId(empId)
                .build();
    }

}
