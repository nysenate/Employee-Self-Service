package gov.nysenate.ess.web.security.filter;

import gov.nysenate.ess.core.model.auth.SimpleEssPermission;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authz.PermissionsAuthorizationFilter;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.stereotype.Component;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;

/**
 * Filter that tests to see that authorization worked correctly.
 * Redirects user to error page if not
 */
@Component("verifyAuthz")
public class VerifyAuthorizationFilter extends PermissionsAuthorizationFilter {

    /** Permission that should be present for all authorized users */
    private static final String[] authTestPermissionStrings =
            new String[] {SimpleEssPermission.SENATE_EMPLOYEE.getPermissionString()};

    /** Relative url to the 'authorization error' page */
    private static final String authzErrorUrl = "/error/authz";


    /** {@inheritDoc} */
    @Override
    public boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) throws IOException {
        return super.isAccessAllowed(request, response, authTestPermissionStrings);
    }

    /** {@inheritDoc} */
    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws IOException {
        Subject subject = getSubject(request, response);
        // If the subject isn't identified, redirect to login URL
        if (subject.getPrincipal() == null) {
            saveRequestAndRedirectToLogin(request, response);
        } else {
            // Otherwise logout the subject and go to authz error page url
            WebUtils.issueRedirect(request, response, authzErrorUrl);
        }
        return false;
    }
}
