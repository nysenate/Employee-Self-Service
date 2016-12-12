package gov.nysenate.ess.time.service.notification;

import com.google.common.collect.ImmutableMultimap;
import gov.nysenate.ess.core.BaseTest;
import gov.nysenate.ess.core.annotation.SillyTest;
import gov.nysenate.ess.time.model.attendance.TimeRecord;
import gov.nysenate.ess.time.service.attendance.TimeRecordService;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.Collection;

@Category(SillyTest.class)
public class TimeRecordEmailServiceTest extends BaseTest {

    private static final Logger logger = LoggerFactory.getLogger(TimeRecordEmailServiceTest.class);


    @Autowired TimeRecordService timeRecordService;
    @Autowired
    RecordReminderEmailService recordReminderEmailService;

    @Test(timeout = 10000)
    public void timeRecordNotificationTest() {
        /**
         * Uncomment to run test
         * We do not want this test to run automatically because it will send somebody emails
         */
//        int supId = 9896;
//        int empId = 11423;
//        LocalDate recordDate = LocalDate.of(2016, 10, 20);
//        ImmutableMultimap<Integer, LocalDate> recordDateMap = ImmutableMultimap.of(empId, recordDate);
//
//        logger.info("sending emails...");
//        recordReminderEmailService.sendEmailReminders(supId, recordDateMap);
//        logger.info("sent");
    }

}
