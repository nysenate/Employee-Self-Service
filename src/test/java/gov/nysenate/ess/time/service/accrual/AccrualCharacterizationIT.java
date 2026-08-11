package gov.nysenate.ess.time.service.accrual;

import com.google.common.collect.Range;
import gov.nysenate.ess.core.BaseTest;
import gov.nysenate.ess.core.annotation.IntegrationTest;
import gov.nysenate.ess.core.model.period.PayPeriod;
import gov.nysenate.ess.core.service.period.PayPeriodService;
import gov.nysenate.ess.core.util.DateUtils;
import gov.nysenate.ess.core.util.SortOrder;
import gov.nysenate.ess.time.model.accrual.AccrualsAvailable;
import gov.nysenate.ess.time.model.accrual.EmpAccrualState;
import gov.nysenate.ess.time.model.accrual.PeriodAccSummary;
import gov.nysenate.ess.time.model.accrual.PeriodAccUsage;
import gov.nysenate.ess.time.model.allowances.AllowanceUsage;
import gov.nysenate.ess.time.model.expectedhrs.ExpectedHours;
import gov.nysenate.ess.time.service.allowance.AllowanceService;
import gov.nysenate.ess.time.service.expectedhrs.ExpectedHoursService;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static gov.nysenate.ess.core.model.period.PayPeriodType.AF;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Golden master ("characterization") coverage for accrual computation.
 *
 * <p>This test asserts nothing about whether the accrual math is <em>correct</em>. It asserts only
 * that it has not <em>changed</em>. That is deliberate: the accrual code feeds timesheet validation
 * ({@code AccrualTRV}, {@code AllowanceTRV}), sick leave donation eligibility ({@code DonationCtrl})
 * and the SFMS reports, and before this test the entire path was guarded by a single assertion that
 * a map contained 27 entries. This exists so that performance work on
 * {@link EssAccrualComputeService}, {@code TxExpectedHoursService} and {@code EssAllowanceService}
 * can be shown to be behavior preserving.
 *
 * <p><b>How it works.</b> Each test renders service output to a stable, human readable text dump and
 * compares it against a committed fixture under {@code src/test/resources/characterization/accrual}.
 * When a fixture is missing it is written and the test fails, so a baseline can never be silently
 * accepted as a pass. Review a recorded baseline before committing it, and treat any later diff as a
 * behavior change to be explained rather than re-recorded.
 *
 * <p><b>Why the cases are all historical.</b> Accrual computation consults {@code LocalDate.now()}
 * when it decides which periods are still open, so any case covering the current year would drift
 * and the fixtures would rot. Every case below sits in a closed, fully posted year, which keeps the
 * dump reproducible while still exercising the per-period path through
 * {@code getRelevantAccruals -> getExpectedHours -> getAllowanceUsage}.
 *
 * <p><b>What this does not cover.</b> Because the cases are closed years, they exercise the stored
 * record path far more than the gap computation in {@code getGapAccruals}, which mostly runs for
 * open periods. Changes aimed specifically at gap computation need their own verification.
 *
 * <p>Employee ids are the ones already relied on by the existing tests in this package, so they are
 * known to be present in the SFMS data this suite runs against.
 */
@Category(IntegrationTest.class)
public class AccrualCharacterizationIT extends BaseTest {

    private static final Logger logger = LoggerFactory.getLogger(AccrualCharacterizationIT.class);

    private static final Path FIXTURE_DIR = Paths.get("src/test/resources/characterization/accrual");

    /** Employee / year pairs, all in closed years. See the class javadoc on why nothing is current. */
    private static final int[][] CASES = {
            {11423, 2017},
            {12195, 2017},
            {10976, 2015},
            {11303, 2015},
            {12250, 2017},
    };

    @Autowired private AccrualComputeService accrualComputeService;
    @Autowired private PayPeriodService payPeriodService;
    @Autowired private ExpectedHoursService expectedHoursService;
    @Autowired private AllowanceService allowanceService;

    /**
     * The {@code /accruals/history} path: every pay period of a year, which is what the Accrual
     * History and Accrual Projections pages read.
     */
    @Test
    public void accrualHistoryMatchesBaseline() {
        Dump dump = new Dump();
        for (int[] testCase : CASES) {
            int empId = testCase[0];
            int year = testCase[1];
            dump.section("empId=" + empId + " year=" + year);

            List<PayPeriod> periods = payPeriodService.getPayPeriods(
                    AF, DateUtils.yearDateRange(year), SortOrder.ASC);
            TreeMap<PayPeriod, PeriodAccSummary> accruals = accrualComputeService.getAccruals(empId, periods);

            dump.field("periodCount", accruals.size());
            for (Map.Entry<PayPeriod, PeriodAccSummary> entry : accruals.entrySet()) {
                dump.section("  period=" + entry.getKey().getPayPeriodNum()
                        + " " + entry.getKey().getStartDate() + ".." + entry.getKey().getEndDate());
                appendSummary(dump, entry.getValue());
            }
        }
        verify("accrual-history.txt", dump.toString());
    }

    /** The {@code /accruals} path, also used for donation eligibility and timesheet validation. */
    @Test
    public void accrualsAvailableMatchesBaseline() {
        Dump dump = new Dump();
        for (int[] testCase : CASES) {
            int empId = testCase[0];
            int year = testCase[1];

            // The last period of the year: enough history behind it to exercise the running totals.
            List<PayPeriod> periods = payPeriodService.getPayPeriods(
                    AF, DateUtils.yearDateRange(year), SortOrder.ASC);
            PayPeriod period = periods.get(periods.size() - 1);

            dump.section("empId=" + empId + " period=" + period.getStartDate() + ".." + period.getEndDate());
            AccrualsAvailable available = accrualComputeService.getAccrualsAvailable(empId, period);

            dump.field("personalAvailable", available.getPersonalAvailable());
            dump.field("vacationAvailable", available.getVacationAvailable());
            dump.field("sickAvailable", available.getSickAvailable());
            dump.field("serviceYtd", available.getServiceYtd());
            dump.field("serviceYtdExpected", available.getServiceYtdExpected());
            dump.field("biWeekHrsExpected", available.getBiWeekHrsExpected());
        }
        verify("accruals-available.txt", dump.toString());
    }

    /**
     * {@code TxExpectedHoursService} directly. This is the method the accrual page calls once per
     * pay period, and the one whose allowance lookup is the target of the N+1 work.
     */
    @Test
    public void expectedHoursMatchesBaseline() {
        Dump dump = new Dump();
        for (int[] testCase : CASES) {
            int empId = testCase[0];
            int year = testCase[1];
            dump.section("empId=" + empId + " year=" + year);

            List<PayPeriod> periods = payPeriodService.getPayPeriods(
                    AF, DateUtils.yearDateRange(year), SortOrder.ASC);
            for (PayPeriod period : periods) {
                ExpectedHours expected = expectedHoursService.getExpectedHours(empId, period);
                dump.section("  period=" + period.getPayPeriodNum() + " " + period.getStartDate());
                dump.field("yearlyHoursExpected", expected.getYearlyHoursExpected());
                dump.field("ytdHoursExpected", expected.getYtdHoursExpected());
                dump.field("periodHoursExpected", expected.getPeriodHoursExpected());
                dump.field("periodEndHoursExpected", expected.getPeriodEndHoursExpected());
            }
        }
        verify("expected-hours.txt", dump.toString());
    }

    /**
     * {@code EssAllowanceService} directly, by year and by date. The by-date overload is the one
     * {@code TxExpectedHoursService} calls per pay period.
     */
    @Test
    public void allowanceUsageMatchesBaseline() {
        Dump dump = new Dump();
        for (int[] testCase : CASES) {
            int empId = testCase[0];
            int year = testCase[1];

            dump.section("empId=" + empId + " year=" + year);
            appendAllowance(dump, allowanceService.getAllowanceUsage(empId, year));

            List<PayPeriod> periods = payPeriodService.getPayPeriods(
                    AF, DateUtils.yearDateRange(year), SortOrder.ASC);
            for (PayPeriod period : periods) {
                dump.section("  asOf=" + period.getStartDate());
                appendAllowance(dump, allowanceService.getAllowanceUsage(empId, period.getStartDate()));
            }
        }
        verify("allowance-usage.txt", dump.toString());
    }

    /* --- Dump helpers --- */

    private static void appendSummary(Dump dump, PeriodAccSummary summary) {
        dump.field("computed", summary.isComputed());
        dump.field("submitted", summary.isSubmitted());
        dump.field("year", summary.getYear());
        dump.field("endDate", summary.getEndDate());
        dump.field("refPayPeriod", summary.getRefPayPeriod() == null
                ? null : summary.getRefPayPeriod().getStartDate());

        dump.field("prevTotalHoursYtd", summary.getPrevTotalHoursYtd());
        dump.field("totalHoursYtd", summary.getTotalHoursYtd());
        dump.field("expectedTotalHours", summary.getExpectedTotalHours());
        dump.field("expectedBiweekHours", summary.getExpectedBiweekHours());
        dump.field("sickRate", summary.getSickRate());
        dump.field("vacRate", summary.getVacRate());

        dump.field("vacHoursAccrued", summary.getVacHoursAccrued());
        dump.field("totalVacHoursAccrued", summary.getTotalVacHoursAccrued());
        dump.field("vacHoursBanked", summary.getVacHoursBanked());
        dump.field("perHoursAccrued", summary.getPerHoursAccrued());
        dump.field("empHoursAccrued", summary.getEmpHoursAccrued());
        dump.field("totalEmpHoursAccrued", summary.getTotalEmpHoursAccrued());
        dump.field("empHoursBanked", summary.getEmpHoursBanked());
        dump.field("priorYearDonations", summary.getPriorYearDonations());
        dump.field("currentYearDonations", summary.getCurrentYearDonations());

        // Year to date usage.
        dump.field("ytd.workHours", summary.getWorkHours());
        dump.field("ytd.vacHoursUsed", summary.getVacHoursUsed());
        dump.field("ytd.perHoursUsed", summary.getPerHoursUsed());
        dump.field("ytd.empHoursUsed", summary.getEmpHoursUsed());
        dump.field("ytd.famHoursUsed", summary.getFamHoursUsed());
        dump.field("ytd.holHoursUsed", summary.getHolHoursUsed());
        dump.field("ytd.miscHoursUsed", summary.getMiscHoursUsed());
        dump.field("ytd.misc2HoursUsed", summary.getMisc2HoursUsed());
        dump.field("ytd.travelHoursUsed", summary.getTravelHoursUsed());
        dump.field("ytd.totalHoursUsed", summary.getTotalHoursUsed());
        dump.field("ytd.totalSickHoursUsed", summary.getTotalSickHoursUsed());

        PeriodAccUsage usage = summary.getPeriodAccUsage();
        if (usage == null) {
            dump.field("biweek", null);
        }
        else {
            dump.field("biweek.workHours", usage.getWorkHours());
            dump.field("biweek.vacHoursUsed", usage.getVacHoursUsed());
            dump.field("biweek.perHoursUsed", usage.getPerHoursUsed());
            dump.field("biweek.empHoursUsed", usage.getEmpHoursUsed());
            dump.field("biweek.famHoursUsed", usage.getFamHoursUsed());
            dump.field("biweek.holHoursUsed", usage.getHolHoursUsed());
            dump.field("biweek.miscHoursUsed", usage.getMiscHoursUsed());
            dump.field("biweek.misc2HoursUsed", usage.getMisc2HoursUsed());
            dump.field("biweek.travelHoursUsed", usage.getTravelHoursUsed());
        }

        EmpAccrualState state = summary.getEmpAccrualState();
        if (state == null) {
            dump.field("empState", null);
        }
        else {
            dump.field("empState.payPeriodCount", state.getPayPeriodCount());
            dump.field("empState.employeeAccruing", state.isEmployeeAccruing());
            dump.field("empState.payType", state.getPayType());
            dump.field("empState.minTotalHours", state.getMinTotalHours());
            dump.field("empState.minHoursToEnd", state.getMinHoursToEnd());
        }
    }

    private static void appendAllowance(Dump dump, AllowanceUsage usage) {
        dump.field("year", usage.getYear());
        dump.field("yearlyAllowance", usage.getYearlyAllowance());
        dump.field("hoursUsed", usage.getHoursUsed());
        dump.field("moneyUsed", usage.getMoneyUsed());
        dump.field("baseHoursUsed", usage.getBaseHoursUsed());
        dump.field("baseMoneyUsed", usage.getBaseMoneyUsed());
        dump.field("recordHoursUsed", usage.getRecordHoursUsed());
        dump.field("recordMoneyUsed", usage.getRecordMoneyUsed());
        dump.field("startDate", usage.getStartDate());
        dump.field("toDate", usage.getToDate());
    }

    /**
     * Accumulates {@code key = value} lines. Values are normalized so the output depends only on the
     * numbers, not on how they happen to be scaled: {@code 8.00} and {@code 8} render identically.
     */
    private static final class Dump {
        private final StringBuilder sb = new StringBuilder();

        void section(String heading) {
            sb.append('[').append(heading).append("]\n");
        }

        void field(String name, Object value) {
            sb.append(name).append(" = ").append(render(value)).append('\n');
        }

        private static String render(Object value) {
            if (value == null) {
                return "null";
            }
            if (value instanceof BigDecimal) {
                BigDecimal decimal = (BigDecimal) value;
                // Strip scale so a representation change alone cannot fail the comparison, and
                // normalize negative zero, which stripTrailingZeros leaves as "0E-1" style output.
                BigDecimal stripped = decimal.stripTrailingZeros();
                return stripped.compareTo(BigDecimal.ZERO) == 0 ? "0" : stripped.toPlainString();
            }
            return String.valueOf(value);
        }

        @Override
        public String toString() {
            return sb.toString();
        }
    }

    /* --- Fixture comparison --- */

    /**
     * Compares the dump against its committed fixture, recording the fixture and failing if it does
     * not exist yet.
     */
    private static void verify(String fixtureName, String actual) {
        Path fixture = FIXTURE_DIR.resolve(fixtureName);

        if (!Files.exists(fixture)) {
            write(fixture, actual);
            fail("No baseline existed for " + fixtureName + ", so one was recorded at " + fixture
                    + ". Review it, confirm the values are what production currently produces, commit it,"
                    + " and re-run. This failure is intentional: a baseline must never be recorded and"
                    + " passed in the same run.");
        }

        String expected = read(fixture);
        if (expected.equals(actual)) {
            logger.info("{} matches baseline ({} lines)", fixtureName, actual.split("\n", -1).length);
            return;
        }

        // A raw assertEquals on a dump this size is unreadable, so point at the first difference.
        String[] expectedLines = expected.split("\n", -1);
        String[] actualLines = actual.split("\n", -1);
        int differing = 0;
        int firstDiff = -1;
        for (int i = 0; i < Math.max(expectedLines.length, actualLines.length); i++) {
            String e = i < expectedLines.length ? expectedLines[i] : "<missing>";
            String a = i < actualLines.length ? actualLines[i] : "<missing>";
            if (!e.equals(a)) {
                differing++;
                if (firstDiff < 0) {
                    firstDiff = i;
                }
            }
        }

        Path actualPath = FIXTURE_DIR.resolve(fixtureName + ".actual");
        write(actualPath, actual);

        String context = context(expectedLines, firstDiff);
        assertEquals(fixtureName + ": accrual output changed against the baseline. "
                        + differing + " line(s) differ, first at line " + (firstDiff + 1)
                        + ". Full output written to " + actualPath
                        + ". Section: " + context
                        + "\nIf this change is intended, delete the fixture and re-record it, but only"
                        + " after confirming every moved value is explainable.",
                expectedLines[Math.min(firstDiff, expectedLines.length - 1)],
                actualLines[Math.min(firstDiff, actualLines.length - 1)]);
    }

    /** The nearest preceding section heading, so a failure says which employee and period moved. */
    private static String context(String[] lines, int index) {
        for (int i = Math.min(index, lines.length - 1); i >= 0; i--) {
            if (lines[i].startsWith("[")) {
                return lines[i];
            }
        }
        return "<none>";
    }

    private static String read(Path path) {
        try {
            return new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
        }
        catch (IOException ex) {
            throw new UncheckedIOException("Could not read fixture " + path, ex);
        }
    }

    private static void write(Path path, String content) {
        try {
            Files.createDirectories(path.getParent());
            Files.write(path, content.getBytes(StandardCharsets.UTF_8));
        }
        catch (IOException ex) {
            throw new UncheckedIOException("Could not write fixture " + path, ex);
        }
    }
}
