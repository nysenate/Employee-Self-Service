package gov.nysenate.ess.web.controller.page;

import gov.nysenate.ess.core.util.OutputUtils;
import gov.nysenate.ess.core.model.auth.SenatePerson;
import gov.nysenate.ess.core.service.personnel.EmployeeInfoService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.UnauthenticatedException;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public abstract class BaseEssPageCtrl
{
    private static final Logger logger = LoggerFactory.getLogger(BaseEssPageCtrl.class);

    @Autowired EmployeeInfoService empInfoService;

    abstract String mainPage(ModelMap modelMap, HttpServletRequest request);

    protected void addCommonModelMapData(ModelMap modelMap) {
        Subject subject = SecurityUtils.getSubject();
        if (subject.isAuthenticated()) {
            SenatePerson person = (SenatePerson) subject.getPrincipal();
            modelMap.put("principal", person);
            modelMap.put("principalJson", OutputUtils.toJson(person));
            List<Integer> employeeActiveYears =
                empInfoService.getEmployeeActiveYearsService(person.getEmployeeId());
            modelMap.put("empActiveYears", OutputUtils.toJson(employeeActiveYears));
        }
    }

    protected SenatePerson getUser() {
        Subject subject = SecurityUtils.getSubject();
        if (subject.isAuthenticated()) {
            return (SenatePerson) subject.getPrincipal();
        }
        throw new UnauthenticatedException("User has not been authenticated.");
    }
}