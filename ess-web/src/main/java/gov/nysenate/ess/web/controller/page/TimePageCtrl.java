package gov.nysenate.ess.web.controller.page;

import gov.nysenate.ess.core.model.auth.SenatePerson;
import gov.nysenate.ess.seta.service.attendance.TimeRecordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

/**
 * Handles requests to the Time and Attendance page.
 */
@Controller
@RequestMapping("/time/**")
public class TimePageCtrl extends BaseEssPageCtrl
{
    private static final Logger logger = LoggerFactory.getLogger(TimePageCtrl.class);

    private TimeRecordService timeRecordService;

    @RequestMapping(method = RequestMethod.GET)
    public String mainPage(ModelMap modelMap, HttpServletRequest request) {
        SenatePerson senatePerson = getUser();
        addCommonModelMapData(modelMap);
		return "time";
	}
}