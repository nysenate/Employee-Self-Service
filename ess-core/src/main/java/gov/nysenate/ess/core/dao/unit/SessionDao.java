package gov.nysenate.ess.core.dao.unit;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.web.util.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
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
    @Value("${timeout}")
    private int TIMEOUT; // in sec changeable.
    private int COUNT_DOWN;// DO NOT CHANGE
    private int WARNING; // in sec

    @PostConstruct
    public void init() {
        COUNT_DOWN = 60;
        WARNING = TIMEOUT - COUNT_DOWN;
    }

    public Integer ping(String idelTime, HttpServletRequest request, HttpServletResponse response) throws IOException {
        int idle = Integer.valueOf(idelTime);
        if (idle == -1) {
            WebUtils.saveRequest(request);
            SecurityUtils.getSubject().logout(); // timeout user's session
            return -1;
        } else if (idle < WARNING) {
            return 0;//normal ping
        } else if (idle >= WARNING && idle < TIMEOUT) {
            return TIMEOUT - idle; // return allowed interaction time.
        } else {
            return -1;
        }
    }

}
