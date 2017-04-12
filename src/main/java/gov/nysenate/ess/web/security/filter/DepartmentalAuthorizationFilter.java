package gov.nysenate.ess.web.security.filter;

import gov.nysenate.ess.core.model.auth.SimpleEssPermission;
import gov.nysenate.ess.core.service.security.authorization.DepartmentalWhitelistService;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authz.PermissionsAuthorizationFilter;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.stereotype.Component;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;


/**
 * An authorization filter that can be used to restrict access to the main web application.
 * Will only allow users with the {@link SimpleEssPermission#DEPARTMENT_ACCESS} permission.
 * Users without {@link SimpleEssPermission#DEPARTMENT_ACCESS} will be redirected to
 * an error page.
 * @see DepartmentalWhitelistService
 */
@Component("deptAuthz")
public class DepartmentalAuthorizationFilter extends PermissionsAuthorizationFilter {

    /** The departmental access permission */
    private static final String[] deptAccessPermissionStrings =
            new String[] {SimpleEssPermission.DEPARTMENT_ACCESS.getPermissionString()};

    /** Relative url to the 'access restricted' page */
    private static final String deptAccessDeniedUrl = "/error/restricted";


    /** {@inheritDoc} */
    @Override
    public boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) throws IOException {
        return super.isAccessAllowed(request, response, deptAccessPermissionStrings);
    }

    /** {@inheritDoc} */
    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws IOException {
        Subject subject = getSubject(request, response);
        // If the subject isn't identified, redirect to login URL
        if (subject.getPrincipal() == null) {
            saveRequestAndRedirectToLogin(request, response);
        } else {
            // Otherwise logout the subject and go to department access denied url
            WebUtils.issueRedirect(request, response, deptAccessDeniedUrl);
        }
        return false;
    }
}
