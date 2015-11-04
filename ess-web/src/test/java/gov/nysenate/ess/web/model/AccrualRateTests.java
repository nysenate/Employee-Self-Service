package gov.nysenate.ess.web.model;

import gov.nysenate.ess.seta.model.accrual.AccrualRate;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;

public class AccrualRateTests
{    private static final Logger logger = LoggerFactory.getLogger(AccrualRateTests.class);

    @Test
    public void testVacationRatesAreCorrect() throws Exception {
        assertEquals("0", AccrualRate.VACATION.getRate(3).toString());
        assertEquals("31.5", AccrualRate.VACATION.getRate(13).toString());
        assertEquals("3.5", AccrualRate.VACATION.getRate(14).toString());
        assertEquals("3.75", AccrualRate.VACATION.getRate(28).toString());
        assertEquals("4", AccrualRate.VACATION.getRate(53).toString());
        assertEquals("4", AccrualRate.VACATION.getRate(58).toString());
        assertEquals("5.5", AccrualRate.VACATION.getRate(89).toString());
    }

    @Test
    public void testSickRatesAreCorrect() throws Exception {
        assertEquals("3.5", AccrualRate.SICK.getRate(3).toString());
        assertEquals("3.5", AccrualRate.SICK.getRate(13).toString());
        assertEquals("3.5", AccrualRate.SICK.getRate(900).toString());
    }
    @Test
    public void testWorkDaysAreCorrect() throws Exception {
//        Date d = new LocalDate(2014,6,9).toDate();
//        int val =PeriodAccSummary.getWorkingDaysBetweenDates( new LocalDate(2015,6,9).toDate(),  new LocalDate(2014,6,9).toDate());
//        logger.debug("WorkDay Count:"+val);
/*        assertEquals(1, PeriodAccSummary.getWorkingDaysBetweenDates(new LocalDate(2014, 6, 9).toDate(), new LocalDate(2014, 6, 9).toDate()));
        assertEquals(2, PeriodAccSummary.getWorkingDaysBetweenDates(new LocalDate(2014, 6, 9).toDate(), new LocalDate(2014, 6, 10).toDate()));
        assertEquals(3, PeriodAccSummary.getWorkingDaysBetweenDates(new LocalDate(2014, 6, 9).toDate(), new LocalDate(2014, 6, 11).toDate()));
        assertEquals(4, PeriodAccSummary.getWorkingDaysBetweenDates(new LocalDate(2014, 6, 9).toDate(), new LocalDate(2014, 6, 12).toDate()));
        assertEquals(5, PeriodAccSummary.getWorkingDaysBetweenDates(new LocalDate(2014, 6, 9).toDate(), new LocalDate(2014, 6, 13).toDate()));
        assertEquals(5, PeriodAccSummary.getWorkingDaysBetweenDates(new LocalDate(2014, 6, 9).toDate(), new LocalDate(2014, 6, 14).toDate()));
        assertEquals(5, PeriodAccSummary.getWorkingDaysBetweenDates(new LocalDate(2014, 6, 9).toDate(), new LocalDate(2014, 6, 15).toDate()));
        assertEquals(6, PeriodAccSummary.getWorkingDaysBetweenDates(new LocalDate(2014, 6, 9).toDate(), new LocalDate(2014, 6, 16).toDate()));
        assertEquals(7, PeriodAccSummary.getWorkingDaysBetweenDates(new LocalDate(2014, 6, 9).toDate(), new LocalDate(2014, 6, 17).toDate()));

        assertEquals(0, PeriodAccSummary.getWorkingDaysBetweenDates(new LocalDate(2014, 6, 7).toDate(), new LocalDate(2014, 6, 7).toDate()));
        assertEquals(0, PeriodAccSummary.getWorkingDaysBetweenDates(new LocalDate(2014, 6, 7).toDate(), new LocalDate(2014, 6, 8).toDate()));
        assertEquals(1, PeriodAccSummary.getWorkingDaysBetweenDates(new LocalDate(2014, 6, 7).toDate(), new LocalDate(2014, 6, 9).toDate()));
        assertEquals(2, PeriodAccSummary.getWorkingDaysBetweenDates(new LocalDate(2014, 6, 7).toDate(), new LocalDate(2014, 6, 10).toDate()));

        assertEquals(10, PeriodAccSummary.getWorkingDaysBetweenDates(new LocalDate(2014, 6, 7).toDate(), new LocalDate(2014, 6, 20).toDate()));
        assertEquals(10, PeriodAccSummary.getWorkingDaysBetweenDates(new LocalDate(2014, 6, 8).toDate(), new LocalDate(2014, 6, 21).toDate()));
        assertEquals(10, PeriodAccSummary.getWorkingDaysBetweenDates(new LocalDate(2014, 6, 9).toDate(), new LocalDate(2014, 6, 22).toDate()));
        assertEquals(10, PeriodAccSummary.getWorkingDaysBetweenDates(new LocalDate(2014, 6, 10).toDate(), new LocalDate(2014, 6, 23).toDate()));
        assertEquals(10, PeriodAccSummary.getWorkingDaysBetweenDates(new LocalDate(2014, 6, 11).toDate(), new LocalDate(2014, 6, 24).toDate()));
        assertEquals(10, PeriodAccSummary.getWorkingDaysBetweenDates(new LocalDate(2014, 6, 12).toDate(), new LocalDate(2014, 6, 25).toDate()));
        assertEquals(10, PeriodAccSummary.getWorkingDaysBetweenDates(new LocalDate(2014, 6, 13).toDate(), new LocalDate(2014, 6, 26).toDate()));
   */ }
}
