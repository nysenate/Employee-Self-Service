package gov.nysenate.ess.web.security.session;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Contains methods to check the remaining allowed inactivity time for the current session,
 * and also register active pings to keep the session alive.
 */
@Service
public class SessionTimeoutDao {

    private static final String lastActivePingAttr = "LAST_ACTIVE_PING";

    /** The duration of allowed inactivity before timeout */
    private final Duration sessionTimeoutDuration;

    public SessionTimeoutDao(@Value("${timeout}") int timeout) {
        sessionTimeoutDuration = Duration.ofSeconds(timeout);
    }

    /**
     * Gets the remaining allowed inactivity for the current session in seconds.
     *
     * @return Duration
     */
    public Duration getRemainingAllowedInactivity() {
        LocalDateTime lastActivePing = getLastActivePing();
        Objects.requireNonNull(lastActivePing, "User's session does not have a last ping timestamp");

        Duration inactivity = Duration.between(lastActivePing, LocalDateTime.now());
        return sessionTimeoutDuration.minus(inactivity);
    }

    /**
     * Register the last active ping as now for the current session.
     */
    public void registerActivePing() {
        Session session = SecurityUtils.getSubject().getSession();
        session.setAttribute(lastActivePingAttr, LocalDateTime.now());
    }

    /**
     * Checks that the session is not timed out.
     * If the session does not have a last ping registered, it is set to now.
     *
     * @return boolean
     */
    public boolean isSessionActive() {
        ensureLastActivePing();
        return !getRemainingAllowedInactivity().isNegative();
    }

    /* --- Internal Methods --- */

    /**
     * Checks the last active ping.  If it is null, the last active ping attribute is set to now.
     */
    private void ensureLastActivePing() {
        LocalDateTime lastActivePing = getLastActivePing();
        if (lastActivePing == null) {
            registerActivePing();
        }
    }

    /**
     * @return LocalDateTime - the last active ping registered to the current session
     */
    private LocalDateTime getLastActivePing() {
        Session session = SecurityUtils.getSubject().getSession();
        return (LocalDateTime) session.getAttribute(lastActivePingAttr);
    }
}
