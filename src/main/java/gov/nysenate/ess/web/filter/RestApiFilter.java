package gov.nysenate.ess.web.filter;

import gov.nysenate.ess.core.util.HttpResponseUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component("restApiFilter")
public class RestApiFilter implements Filter
{
    private static final Logger logger = LoggerFactory.getLogger(RestApiFilter.class);

    @Override
    public void init(FilterConfig filterConfig) {}

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        setNoStoreHeaders(response);
        filterChain.doFilter(servletRequest, response);
    }

    @Override
    public void destroy() {}

    /**
     * REST API responses should disable browser caching (mostly affects IE) since the data is subject to
     * change frequently.
     */
    private void setNoStoreHeaders(HttpServletResponse response) {
        HttpResponseUtils.preventCaching(response);
    }
}
