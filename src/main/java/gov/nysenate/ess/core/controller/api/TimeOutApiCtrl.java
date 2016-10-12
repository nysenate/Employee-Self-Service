package gov.nysenate.ess.core.controller.api;

import gov.nysenate.ess.core.client.response.base.SimpleResponse;
import gov.nysenate.ess.core.dao.unit.SessionDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * This class uses to maintaining heart-beating
 * Created by Chenguang He on 6/30/2016.
 */
@RestController
@RequestMapping(BaseRestApiCtrl.REST_PATH + "/timeout")
public class TimeOutApiCtrl extends BaseRestApiCtrl {
    @Autowired
    private SessionDao sessionDao;

    @RequestMapping(value = "/ping", method = RequestMethod.GET)
    public SimpleResponse ping(@RequestParam String idleTime, HttpServletRequest request, HttpServletResponse response) throws IOException {
        SimpleResponse simpleResponse = new SimpleResponse(true, sessionDao.ping(idleTime, request, response) + "", "GET");
        return simpleResponse;
    }
}
