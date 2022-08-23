package gov.nysenate.ess.web.controller.page;

import gov.nysenate.ess.core.model.auth.EssRole;
import gov.nysenate.ess.core.model.auth.SenatePerson;
import gov.nysenate.ess.core.service.personnel.EmployeeInfoService;
import gov.nysenate.ess.core.util.OutputUtils;
import gov.nysenate.ess.time.model.auth.SimpleTimePermission;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.ModelMap;

import java.util.List;

@Component
public class PageCtrlUtils {

    private final EmployeeInfoService empInfoService;

    @Autowired
    public PageCtrlUtils(EmployeeInfoService empInfoService) {
        this.empInfoService = empInfoService;
    }

    public ModelMap commonPageData() {
        ModelMap modelMap = new ModelMap();
        Subject subject = SecurityUtils.getSubject();
        if (subject.isAuthenticated()) {
            SenatePerson person = (SenatePerson) subject.getPrincipal();
            modelMap.put("principal", person);
            modelMap.put("principalJson", OutputUtils.toJson(person));
            modelMap.put("principalIsSenator", subject.hasRole(EssRole.SENATOR.name()));
            modelMap.put("principalIsSupervisor", subject.isPermitted(SimpleTimePermission.MANAGEMENT_PAGES.getPermission()));
            List<Integer> employeeActiveYears =
                    empInfoService.getEmployeeActiveYearsService(person.getEmployeeId());
            modelMap.put("empActiveYears", OutputUtils.toJson(employeeActiveYears));
        }
        return modelMap;
    }
}
