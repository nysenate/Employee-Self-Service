package gov.nysenate.ess.seta.service.notification;

import gov.nysenate.ess.seta.SetaTests;
import gov.nysenate.ess.seta.model.attendance.TimeRecord;
import gov.nysenate.ess.seta.service.attendance.TimeRecordService;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;

public class TimeRecordEmailServiceTests extends SetaTests {

    private static final Logger logger = LoggerFactory.getLogger(TimeRecordEmailServiceTests.class);


    @Autowired TimeRecordService timeRecordService;
    @Autowired TimeRecordEmailService timeRecordEmailService;

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
