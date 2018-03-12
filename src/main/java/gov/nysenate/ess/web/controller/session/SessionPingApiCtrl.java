package gov.nysenate.ess.web.controller.session;

import gov.nysenate.ess.core.controller.api.BaseRestApiCtrl;
import gov.nysenate.ess.core.model.auth.SimpleEssPermission;
import gov.nysenate.ess.web.client.response.PingResponse;
import gov.nysenate.ess.web.security.session.SessionTimeoutDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * This class uses to maintaining heart-beating.
 *
 * Provides an api method that can be used by the front end to track
 * the user's session timeout and keep the session alive.
 *
 * Created by Chenguang He on 6/30/2016.
 * Modified by Sam Stouffer on 3/7/2018
 */
@RestController
@RequestMapping(BaseRestApiCtrl.REST_PATH + "/timeout")
public class SessionPingApiCtrl extends BaseRestApiCtrl {

    private static final Logger logger = LoggerFactory.getLogger(SessionPingApiCtrl.class);

    private final SessionTimeoutDao sessionTimeoutDao;

    @Autowired
    public SessionPingApiCtrl(SessionTimeoutDao sessionTimeoutDao) {
        this.sessionTimeoutDao = sessionTimeoutDao;
    }

    @RequestMapping(value = "/ping", method = POST)
    public PingResponse ping(@RequestParam boolean active) {
        if (active || isTimeoutExempt()) {
            sessionTimeoutDao.registerActivePing();
        }
        long secondsRemaining = sessionTimeoutDao.getRemainingAllowedInactivity().getSeconds();
        return new PingResponse(secondsRemaining);
    }

    /* --- Internal Methods --- */

    private boolean isTimeoutExempt() {
        return getSubject().isPermitted(SimpleEssPermission.TIMEOUT_EXEMPT.getPermission());
    }
}
