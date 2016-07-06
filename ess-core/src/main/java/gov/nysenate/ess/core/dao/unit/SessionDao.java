package gov.nysenate.ess.core.dao.unit;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.web.util.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * This class use for timeout user's  session by maintaining a heartbeat ping.
 * Created by Chenguang He on 6/30/2016.
 */
@Repository
public class SessionDao {
    private final Logger logger = LoggerFactory.getLogger(SessionDao.class);
    private static SessionDao sessionDao;
    @Value("${timeout}")
    private int TIMEOUT; // in sec changeable.
    private final int COUNT_DOWN = 60;// DO NOT CHANGE
    private final int WARNING = TIMEOUT - COUNT_DOWN; // in sec

    public Integer ping(String idelTime, HttpServletRequest request, HttpServletResponse response) throws IOException {
        int idel = Integer.valueOf(idelTime);
        if (idel == -1) {
            WebUtils.saveRequest(request);
            SecurityUtils.getSubject().logout(); // timeout user's session
            return -1;
        } else if (idel < WARNING) {
            return 0;//normal ping
        } else if (idel >= WARNING && idel < TIMEOUT) {
            return TIMEOUT - idel; // return allowed interaction time.
        } else {
            return -1;
        }
    }

    private SessionDao() {
    }

    /**
     * Singleton class
     *
     * @return
     */
    public static SessionDao getInstance() {
        if (sessionDao == null)
            sessionDao = new SessionDao();
        return sessionDao;
    }
}
