package gov.nysenate.ess.time.dao.attendance;

import gov.nysenate.ess.core.BaseTest;
import gov.nysenate.ess.core.annotation.IntegrationTest;
import gov.nysenate.ess.core.config.DatabaseConfig;
import gov.nysenate.ess.time.client.view.attendance.TimeOffRequestDayView;
import gov.nysenate.ess.time.client.view.attendance.TimeOffRequestView;
import gov.nysenate.ess.time.model.attendance.TimeOffRequest;
import gov.nysenate.ess.time.model.attendance.TimeOffRequestDay;
import gov.nysenate.ess.time.model.attendance.TimeOffStatus;
import gov.nysenate.ess.time.model.payroll.MiscLeaveType;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Covers the second misc leave slot on a time off request day, which used to be dropped in both
 * directions: {@link TimeOffRequestDayView#toTimeOffRequestDay()} never carried misc2Hours onto
 * the model, and the day row mapper never read misc2_hours or misc_type2 back out of the
 * database. Between them, a user could enter Misc 2 hours, watch them count toward the day's
 * total, save, and find both the hours and the type gone.
 *
 * The misc 1 slot is asserted alongside misc 2 throughout, so that a regression in the shared
 * hour handling is not mistaken for a misc 2 problem.
 */
@Category(IntegrationTest.class)
@Transactional(DatabaseConfig.localTxManager)
public class TimeOffRequestMisc2IT extends BaseTest {

    private static final int EMP_ID = 123;
    private static final int SUP_ID = 456;

    private static final BigDecimal MISC_HOURS = new BigDecimal("3.5");
    private static final BigDecimal MISC2_HOURS = new BigDecimal("2.25");
    private static final MiscLeaveType MISC_TYPE = MiscLeaveType.JURY_LEAVE;
    private static final MiscLeaveType MISC2_TYPE = MiscLeaveType.BLOOD_DONATION;

    @Autowired
    private SqlTimeOffRequestDao timeOffRequestDao;

    /**
     * Saving a day that uses both misc slots and reading it back must return both. This is the
     * half of the round trip that the day row mapper is responsible for.
     */
    @Test
    public void misc2HoursSurviveTheDatabase() {
        LocalDate date = LocalDate.now();
        int requestId = saveRequestWithMiscHours(date);

        TimeOffRequestDay saved = onlyDayOf(timeOffRequestDao.getRequestById(requestId));

        assertHoursEqual("misc hours", MISC_HOURS, saved.getMiscHours());
        assertEquals("misc type", MISC_TYPE, saved.getMiscType());
        assertHoursEqual("misc 2 hours", MISC2_HOURS, saved.getMisc2Hours());
        assertEquals("misc 2 type", MISC2_TYPE, saved.getMiscType2());
    }

    /**
     * The whole path a request takes when the front end saves one: the posted view is converted
     * to a model, persisted, read back, and rendered as a view again. Both conversions and the
     * row mapper have to preserve misc 2 for the hours to come back to the page they were
     * entered on.
     */
    @Test
    public void misc2HoursSurviveTheViewRoundTrip() {
        LocalDate date = LocalDate.now();

        // What the browser posts, expressed as the view the controller binds to.
        TimeOffRequestView posted = new TimeOffRequestView(requestWithMiscHours(date));

        TimeOffRequestDayView postedDay = onlyDayViewOf(posted);
        assertHoursEqual("posted misc 2 hours", MISC2_HOURS, Optional.ofNullable(postedDay.getMisc2Hours()));
        assertEquals("posted misc 2 type", MISC2_TYPE.name(), postedDay.getMiscType2());

        int requestId = timeOffRequestDao.updateRequest(posted.toTimeOffRequest());

        // What the page receives when it loads the request back.
        TimeOffRequestView reloaded = new TimeOffRequestView(timeOffRequestDao.getRequestById(requestId));
        TimeOffRequestDayView reloadedDay = onlyDayViewOf(reloaded);

        assertHoursEqual("misc hours", MISC_HOURS, Optional.ofNullable(reloadedDay.getMiscHours()));
        assertEquals("misc type", MISC_TYPE.name(), reloadedDay.getMiscType());
        assertHoursEqual("misc 2 hours", MISC2_HOURS, Optional.ofNullable(reloadedDay.getMisc2Hours()));
        assertEquals("misc 2 type", MISC2_TYPE.name(), reloadedDay.getMiscType2());
    }

    /**
     * A day that uses only the second misc slot is a real entry, not an empty one. This is the
     * case the old code lost entirely: with misc2Hours dropped, nothing distinguished the day
     * from one that was never filled in.
     */
    @Test
    public void aDayUsingOnlyMisc2IsNotEmpty() {
        LocalDate date = LocalDate.now();

        TimeOffRequestDay day = blankDay(date);
        day.setMisc2Hours(MISC2_HOURS);
        day.setMiscType2(MISC2_TYPE);

        int requestId = timeOffRequestDao.updateRequest(requestOf(date, day));
        TimeOffRequestDay saved = onlyDayOf(timeOffRequestDao.getRequestById(requestId));

        assertHoursEqual("misc 2 hours", MISC2_HOURS, saved.getMisc2Hours());
        assertEquals("misc 2 type", MISC2_TYPE, saved.getMiscType2());
        assertEquals("a day carrying misc 2 hours should not read back as empty",
                false, saved.isEmpty());
    }

    /**
     * Updating an existing request rewrites its days, so misc 2 has to survive that path too and
     * not just the initial insert.
     */
    @Test
    public void misc2HoursSurviveAnUpdate() {
        LocalDate date = LocalDate.now();
        int requestId = saveRequestWithMiscHours(date);

        TimeOffRequest request = timeOffRequestDao.getRequestById(requestId);
        TimeOffRequestDay day = onlyDayOf(request);
        BigDecimal revised = new BigDecimal("4.75");
        day.setMisc2Hours(revised);
        request.setDays(new ArrayList<>(Collections.singletonList(day)));

        timeOffRequestDao.updateRequest(request);
        TimeOffRequestDay updated = onlyDayOf(timeOffRequestDao.getRequestById(requestId));

        assertHoursEqual("revised misc 2 hours", revised, updated.getMisc2Hours());
        assertEquals("misc 2 type", MISC2_TYPE, updated.getMiscType2());
    }

    /* --- Helpers --- */

    private int saveRequestWithMiscHours(LocalDate date) {
        return timeOffRequestDao.updateRequest(requestWithMiscHours(date));
    }

    private TimeOffRequest requestWithMiscHours(LocalDate date) {
        TimeOffRequestDay day = blankDay(date);
        day.setMiscHours(MISC_HOURS);
        day.setMiscType(MISC_TYPE);
        day.setMisc2Hours(MISC2_HOURS);
        day.setMiscType2(MISC2_TYPE);
        return requestOf(date, day);
    }

    private TimeOffRequest requestOf(LocalDate date, TimeOffRequestDay day) {
        return new TimeOffRequest(EMP_ID, SUP_ID, TimeOffStatus.SUBMITTED, date, date,
                new ArrayList<>(), new ArrayList<>(Collections.singletonList(day)));
    }

    /** A day with every hour field zeroed, as a freshly added row on the request page is. */
    private TimeOffRequestDay blankDay(LocalDate date) {
        return new TimeOffRequestDay(-1, date,
                BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
                BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, null,
                BigDecimal.ZERO, null);
    }

    private TimeOffRequestDay onlyDayOf(TimeOffRequest request) {
        assertNotNull("the request should have been saved", request);
        List<TimeOffRequestDay> days = request.getDays();
        assertEquals("expected exactly one day on the request", 1, days.size());
        return days.get(0);
    }

    private TimeOffRequestDayView onlyDayViewOf(TimeOffRequestView request) {
        List<TimeOffRequestDayView> days = request.getDays();
        assertEquals("expected exactly one day on the request", 1, days.size());
        return days.get(0);
    }

    /**
     * Compares hours by value rather than by BigDecimal equality, which also compares scale and
     * would fail on an otherwise correct 2.25 read back as 2.250.
     */
    private void assertHoursEqual(String what, BigDecimal expected, Optional<BigDecimal> actual) {
        BigDecimal value = actual.orElseThrow(
                () -> new AssertionError(what + " was missing entirely"));
        assertEquals(what + " expected " + expected + " but was " + value,
                0, expected.compareTo(value));
    }
}
