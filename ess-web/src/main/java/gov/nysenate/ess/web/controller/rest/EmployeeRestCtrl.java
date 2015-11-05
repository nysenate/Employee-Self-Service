package gov.nysenate.ess.web.controller.rest;

import gov.nysenate.ess.core.client.view.DetailedEmployeeView;
import gov.nysenate.ess.core.client.view.EmployeeView;
import gov.nysenate.ess.core.dao.personnel.EmployeeDao;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.model.personnel.EmployeeException;
import gov.nysenate.ess.core.service.personnel.EmployeeInfoService;
import gov.nysenate.ess.web.client.response.base.BaseResponse;
import gov.nysenate.ess.web.client.response.base.ListViewResponse;
import gov.nysenate.ess.web.client.response.base.ViewObjectResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping(BaseRestCtrl.REST_PATH + "/employees")
public class EmployeeRestCtrl extends BaseRestCtrl
{
    private static final Logger logger = LoggerFactory.getLogger(EmployeeRestCtrl.class);

    @Autowired protected EmployeeDao employeeDao;
    @Autowired private EmployeeInfoService empInfoService;

    @RequestMapping(value = "")
    public BaseResponse getEmployeeById(@RequestParam(required = true) Integer empId[],
                                        @RequestParam(defaultValue = "false") boolean detail) throws EmployeeException {
        return getEmployeeResponse(
            Arrays.asList(empId).stream().map(empInfoService::getEmployee).collect(toList()), detail);

    }

    @RequestMapping(value = "/activeYears")
    public BaseResponse getEmployeeYearsActive(@RequestParam(required = true) Integer empId) {
        return ListViewResponse.ofIntList(empInfoService.getEmployeeActiveYearsService(empId), "activeYears");
    }

    private BaseResponse getEmployeeResponse(List<Employee> employeeList, boolean detail) {
        if (employeeList.size() == 1) {
            return new ViewObjectResponse<>((detail) ? new DetailedEmployeeView(employeeList.get(0))
                                                     : new EmployeeView(employeeList.get(0)), "employee");
        }
        else {
            return ListViewResponse.of(
                employeeList.stream().map((detail) ? DetailedEmployeeView::new : EmployeeView::new).collect(toList()),
                "employees");
        }
    }
}