package gov.nysenate.ess.time.controller.api;

import gov.nysenate.ess.core.client.response.base.BaseResponse;
import gov.nysenate.ess.core.controller.api.BaseRestApiCtrl;
import gov.nysenate.ess.core.service.personnel.EmployeeInfoService;
import gov.nysenate.ess.time.client.view.contact.ContactBatch;
import gov.nysenate.ess.time.client.view.contact.ContactBatchFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import static gov.nysenate.ess.core.controller.api.BaseRestApiCtrl.ADMIN_REST_PATH;

@RestController
@RequestMapping(ADMIN_REST_PATH + "/emergency/notification/contacts")
public class EmergencyNotificationApiCtrl extends BaseRestApiCtrl {

    @Autowired private EmployeeInfoService employeeService;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public ContactBatch generateContactList() {
        return ContactBatchFactory.create(employeeService.getEmployee(11168), null);
    }

}
