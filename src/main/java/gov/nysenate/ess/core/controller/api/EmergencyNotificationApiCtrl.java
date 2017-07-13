package gov.nysenate.ess.core.controller.api;

import gov.nysenate.ess.core.controller.api.BaseRestApiCtrl;
import gov.nysenate.ess.core.dao.emergency_notification.EmergencyNotificationInfoDao;
import gov.nysenate.ess.core.model.emergency_notification.EmergencyNotificationInfo;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.service.personnel.EmployeeInfoService;
import gov.nysenate.ess.core.client.view.emergency_notification.ContactBatch;
import gov.nysenate.ess.core.client.view.emergency_notification.ContactBatchFactory;
import org.apache.shiro.authz.permission.WildcardPermission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static gov.nysenate.ess.core.controller.api.BaseRestApiCtrl.ADMIN_REST_PATH;

@RestController
@RequestMapping(ADMIN_REST_PATH + "/emergency-notification/contact-dump")
public class EmergencyNotificationApiCtrl extends BaseRestApiCtrl {

    @Autowired private EmployeeInfoService employeeService;
    @Autowired private EmergencyNotificationInfoDao emergencyInfoDao;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public ContactBatch generateContactList() {
        checkPermission(new WildcardPermission("admin"));

        List<EmergencyNotificationInfo> enis = emergencyInfoDao.getAllEmergencyNotificationInfo();
        Map<Integer, EmergencyNotificationInfo> enisMap = enis.stream()
                .collect(Collectors.toMap(EmergencyNotificationInfo::getEmpId, Function.identity()));
        Set<Employee> employees = employeeService.getAllEmployees(true);
        return ContactBatchFactory.create(employees, enisMap);
    }
}
