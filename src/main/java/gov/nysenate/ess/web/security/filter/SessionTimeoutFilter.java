package gov.nysenate.ess.web.security.filter;

import gov.nysenate.ess.web.security.session.SessionTimeoutDao;
import org.apache.shiro.web.filter.AccessControlFilter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;

public class SessionTimeoutFilter extends AccessControlFilter {

    private final SessionTimeoutDao sessionTimeoutDao;

    public SessionTimeoutFilter(SessionTimeoutDao sessionTimeoutDao) {
        this.sessionTimeoutDao = sessionTimeoutDao;
    }

    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        return sessionTimeoutDao.isSessionActive();
    }

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws IOException {
        getSubject(request, response).logout();
        saveRequestAndRedirectToLogin(request, response);
        return false;
    }
}
