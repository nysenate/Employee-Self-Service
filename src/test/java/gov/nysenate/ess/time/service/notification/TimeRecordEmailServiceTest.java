package gov.nysenate.ess.time.service.notification;

import gov.nysenate.ess.core.BaseTest;
import gov.nysenate.ess.core.annotation.SillyTest;
import gov.nysenate.ess.time.service.attendance.TimeRecordService;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

@Category(SillyTest.class)
public class TimeRecordEmailServiceTest extends BaseTest {

    private static final Logger logger = LoggerFactory.getLogger(TimeRecordEmailServiceTest.class);


    @Autowired TimeRecordService timeRecordService;
    @Autowired
    RecordReminderEmailService recordReminderEmailService;

    @Test
    public void timeRecordNotificationTest() {
        /**
         * Uncomment to run test
         * We do not want this test to run automatically because it will send somebody emails
         */
//        int empId = -1;
//
//        Collection<TimeRecord> activeRecords = timeRecordService.getActiveTimeRecords(empId);
//        logger.info("sending emails...");
//        timeRecordEmailService.sendEmailReminders(9896, activeRecords);
//        logger.info("sent");
    }

}
