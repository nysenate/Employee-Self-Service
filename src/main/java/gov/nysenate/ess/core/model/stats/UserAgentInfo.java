package gov.nysenate.ess.core.model.stats;

import java.time.LocalDateTime;

/**
 * Represents user agent info collected when an employee logs in
 */
public class UserAgentInfo {

    private int empId;
    private String userAgent;
    private LocalDateTime loginTime;

    public UserAgentInfo(int empId, String userAgent, LocalDateTime loginTime) {
        this.empId = empId;
        this.userAgent = userAgent;
        this.loginTime = loginTime;
    }

    public int getEmpId() {
        return empId;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public LocalDateTime getLoginTime() {
        return loginTime;
    }
}
