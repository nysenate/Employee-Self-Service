package gov.nysenate.ess.web.filter;

import gov.nysenate.ess.core.config.RuntimeLevel;
import gov.nysenate.ess.time.model.payroll.MiscLeaveType;
import gov.nysenate.ess.web.security.xsrf.XsrfValidator;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * This filter is responsible for setting attributes on the servlet request that are commonly used
 * by jsp templates.
 */
@Component("commonAttributeFilter")
public class CommonAttributeFilter implements Filter
{

    private static final Logger logger = LoggerFactory.getLogger(CommonAttributeFilter.class);

    /** Attribute keys */
    private static final String CONTEXT_PATH_ATTRIBUTE = "ctxPath";
    private static final String RUNTIME_LEVEL_ATTRIBUTE = "runtimeLevel";
    private static final String LOGIN_URL_ATTRIBUTE = "loginUrl";
    private static final String IMAGE_URL_ATTRIBUTE = "imageUrl";
    private static final String TIMEOUT_ATTRIBUTE = "timeoutExempt";
    private static final String MISC_LEAVE_ATTRIBUTE = "miscLeaves";
    private static final String RELEASEVERSION_ATTRIBUTE = "releaseVersion";

    private final XsrfValidator xsrfValidator;

    private final RuntimeLevel runtimeLevel;

    private final String loginUrl;
    private final String imageUrl;
    private final String releaseVersion;

    @Autowired
    public CommonAttributeFilter(XsrfValidator xsrfValidator,
                                 @Value("${runtime.level}") String runtimeLevel,
                                 @Value("${login.url}") String loginUrl,
                                 @Value("${image.url}") String imageUrl,
                                 @Value("${application.version}") String releaseVersion
    ) {
        this.xsrfValidator = xsrfValidator;
        this.runtimeLevel = RuntimeLevel.of(runtimeLevel);
        this.loginUrl = loginUrl;
        this.imageUrl = imageUrl;
        this.releaseVersion = releaseVersion;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        logger.trace("CommonAttributeFilter processing url {}", httpServletRequest.getRequestURI());
        if (((HttpServletRequest) request).getSession().isNew())
            return;
        if (((HttpServletRequest) request).getSession() == null)
            return;
        setContextPathAttribute(httpServletRequest);
        setTimeoutAttribute(httpServletRequest);
        setRuntimeLevelAttribute(request);
        setLoginUrlAttribute(request);
        setXsrfTokenAttribute(httpServletRequest);
        setMiscLeaveAttribute(httpServletRequest);
        setReleaseVersionAttribute(httpServletRequest);
        setImageUrl(request);
        chain.doFilter(request, response);
    }

    private static void setContextPathAttribute(HttpServletRequest httpServletRequest) {
        httpServletRequest.setAttribute(CONTEXT_PATH_ATTRIBUTE, httpServletRequest.getContextPath());
    }

    private static void setTimeoutAttribute(HttpServletRequest httpServletRequest) {
        Subject subject = SecurityUtils.getSubject();
        if (subject.isPermitted("core:timeout-exempt"))
            httpServletRequest.setAttribute(TIMEOUT_ATTRIBUTE, true);
        else
            httpServletRequest.setAttribute(TIMEOUT_ATTRIBUTE, false);

    }

    private void setRuntimeLevelAttribute(ServletRequest request) {
        request.setAttribute(RUNTIME_LEVEL_ATTRIBUTE, runtimeLevel.name().toLowerCase());
    }

    private void setLoginUrlAttribute(ServletRequest request) {
        request.setAttribute(LOGIN_URL_ATTRIBUTE, loginUrl);
    }

    private void setImageUrl(ServletRequest request) {
        request.setAttribute(IMAGE_URL_ATTRIBUTE, imageUrl);
    }


    private void setXsrfTokenAttribute(HttpServletRequest httpServletRequest) {
        HttpSession session = httpServletRequest.getSession();
        xsrfValidator.saveXsrfToken(httpServletRequest, session);
    }

    private static void setMiscLeaveAttribute(HttpServletRequest request) {
        request.setAttribute(MISC_LEAVE_ATTRIBUTE, MiscLeaveType.getJsonLabels());
    }

    private void setReleaseVersionAttribute(HttpServletRequest request) {
        request.setAttribute(RELEASEVERSION_ATTRIBUTE, releaseVersion);
    }

    /** Life-cycle is maintained by Spring. The init method is not used. */
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    /** Life-cycle is maintained by Spring. The destroy method is not used. */
    @Override
    public void destroy() {}
}
