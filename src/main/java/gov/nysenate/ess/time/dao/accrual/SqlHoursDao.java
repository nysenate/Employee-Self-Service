package gov.nysenate.ess.time.dao.accrual;

import gov.nysenate.ess.core.annotation.WorkInProgress;
import gov.nysenate.ess.core.dao.base.SqlBaseDao;
import gov.nysenate.ess.time.dao.accrual.mapper.HoursRowMapper;
import gov.nysenate.ess.time.dao.accrual.mapper.LastSFMSHoursRowMapper;
import gov.nysenate.ess.time.model.accrual.Hours;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;
import java.util.LinkedList;

@Repository
@WorkInProgress(desc = "Make this conform to code style, no Dates etc.")
public class SqlHoursDao extends SqlBaseDao implements HoursDao
{
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(SqlHoursDao.class);

    /** --- SQL Queries --- */

    protected static final String LAST_SFMS_HOURS_SQL =
        "SELECT a.DTEND, a.nutothrslast, b.nutotalhrs\n" +
        "FROM PD23ACCUSAGE a, PD23ATTEND b\n" +
        "WHERE a.NUXREFEM = :empId\n" +
        "AND a.nuxrefem = b.nuxrefem\n" +
        "AND a.dtend = b.dtend\n" +
        "AND b.cdstatus = 'A'\n" +
        "AND a.dtend >= :beginDate\n" +
        "AND a.dtend < :endDate\n" +
/*                  "AND EXISTS (SELECT 1\n" +
                    "              FROM pv23tatyp a2\n" +
                    "             WHERE a2.nuxrefem = a.nuxrefem\n" +
                    "               AND a2.cdpaytype IN ('RA', 'SA')\n" +
                    "               AND (   a2.dtend BETWEEN a.dtbegin AND a.dtend\n" +
                    "                    OR a2.dtbegin BETWEEN a.dtbegin AND a.dtend)\n" +
                    "           )\n" +*/
        "ORDER BY a.DTEND DESC";

    protected static final String ATTENDANCE_ONLY_HOURS_SQL =
        "SELECT MAX(dtend) dtendmax, SUM(nutotalhrs)\n" +
        "FROM pd23attend a\n" +
        "WHERE nuxrefem = :empId\n" +
        "AND dtend >= :beginDate\n" +
        "AND a.dtend < :endDate\n" +
        "AND cdstatus = 'A'\n" +
/*                  "AND EXISTS (SELECT 1\n" +
                    "              FROM pv23tatyp a2\n" +
                    "             WHERE a2.nuxrefem = a.nuxrefem\n" +
                    "               AND a2.cdpaytype IN ('RA', 'SA')\n" +
                    "               AND (   a2.dtend BETWEEN a.dtbegin AND a.dtend\n" +
                    "                    OR a2.dtbegin BETWEEN a.dtbegin AND a.dtend)\n" +
                    "           )\n" +*/
        "AND NOT EXISTS (SELECT 1\n" +
        "                FROM pd23accusage b\n" +
        "                WHERE b.dtend = a.dtend\n" +
        "                AND b.nuxrefem = a.nuxrefem)\n";

    protected static final String TIMESHEET_HOURS_SQL =
        "SELECT  MAX(dtend), SUM(NVL(NUWORK,0)) + SUM(NVL(NUTRAVEL,0)) + SUM(NVL(NUHOLIDAY,0)) + SUM(NVL(nuvacation,0)) + SUM(NVL(nupersonal, 0)) + SUM(NVL(nusickemp,0)) + SUM(NVL(nusickfam,0)) + SUM(NVL(numisc, 0)) nutotal\n" +
        "FROM pd23timesheet a, pm23timesheet b\n" +
        "WHERE b.nuxrtimesheet = a.nuxrtimesheet\n" +
        "AND b.nuxrefem = :empId\n" +
        "AND a.cdstatus = 'A'\n" +
        "AND b.cdstatus = 'A'\n" +
        "AND b.dtend < :endDate\n" +
        "AND b.dtend >= :beginDate\n" +
        "AND NOT EXISTS (SELECT 1\n" +
        "FROM PD23ACCUSAGE c\n" +
        "WHERE c.nuxrefem = b.nuxrefem\n" +
        "AND c.dtend < :endDate\n" +
        "AND b.dtend BETWEEN c.dtend-13 AND c.dtend)\n" +
        "AND NOT EXISTS (SELECT 1\n" +
        "FROM PD23ATTEND c\n" +
        "WHERE c.nuxrefem = b.nuxrefem\n" +
        "AND c.dtend < :endDate\n" +
        "AND b.dtend BETWEEN c.dtend-13 AND c.dtend\n" +
        "AND c.cdstatus = 'A')\n";

    /** --- Public Interface --- */

    @Override
    public BigDecimal getTotalHours(int empId, int year) {
        logger.info("SqlHoursDao", "~~~~~~~~~~~~~~~~~~~~~~~(a)getTotalHoursUsed start");
        return getTotalHours(empId, toDate(LocalDate.of(year, 1, 1)), new Date());
    }

    @Override
    public BigDecimal getTotalHours(int empId, Date beginDate, Date endDate) {
        logger.debug("~~~~~~~~~~~~~~~~~~~~~~~(b)getTotalHoursUsed start");
        Hours lastSFMSHours = getLastSFMSHours(empId, beginDate, endDate);
        logger.debug("~~~~~~~~~~~~~~~~~~~~~~lastSFMSHours:"+lastSFMSHours.getHours());
        Hours attendanceOnlyHours = getAttendanceOnlyHours(empId, beginDate, endDate);
        logger.debug("attendanceOnlyHours:"+attendanceOnlyHours.getHours());
        Hours timesheetHours = getTimesheetHours(empId, beginDate, endDate);
        logger.debug("timesheetHours:"+timesheetHours.getHours());
        BigDecimal totalHours = new BigDecimal("0");

        if (lastSFMSHours!=null && lastSFMSHours.getHours()!= null) {
            totalHours = totalHours.add(lastSFMSHours.getHours());
            logger.debug("totalHours(a):" + totalHours + " (+" + lastSFMSHours.getHours() + ")");
        }

        if (attendanceOnlyHours!=null && attendanceOnlyHours.getHours()!= null) {
            totalHours = totalHours.add(attendanceOnlyHours.getHours());
            logger.debug("totalHours(b):" + totalHours + " (+" + attendanceOnlyHours.getHours() + ")");
        }

        if (timesheetHours!=null && timesheetHours.getHours()!= null) {
            totalHours = totalHours.add(timesheetHours.getHours());
            logger.debug("totalHours(c):" + totalHours + " (+" + timesheetHours.getHours() + ")");
        }

        logger.debug("totalHours(done):"+totalHours);

        return totalHours;
    }

    /** --- Processing Methods --- */
    /**
     * (from PD23ATTEND)
     *
     * @param empId int - Employee id
     * @param beginDate Date - Start date (inclusive)
     * @param endDate Date - End Date (inclusive)
     * @return LinkedList<PeriodAccUsage>
     */

    protected Hours getLastSFMSHours(int empId, Date beginDate, Date endDate) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("empId", empId);
        params.addValue("beginDate", beginDate);
        params.addValue("endDate", endDate);
        LinkedList<Hours> hoursList = new LinkedList<Hours>(remoteNamedJdbc.query(LAST_SFMS_HOURS_SQL, params,
                new LastSFMSHoursRowMapper()));
        Hours hours = null;
        if (hoursList.size()>0) {
            hours = hoursList.get(0);
        }
        return hours;
    }

    protected Hours getAttendanceOnlyHours(int empId, Date beginDate, Date endDate) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("empId", empId);
        params.addValue("beginDate", beginDate);
        params.addValue("endDate", endDate);
        LinkedList<Hours> hoursList;
        hoursList =  new LinkedList<Hours>(remoteNamedJdbc.query(ATTENDANCE_ONLY_HOURS_SQL, params,
                 new HoursRowMapper()));

        Hours hours = null;

        if (hoursList.size()>0) {
            hours = hoursList.get(0);
        }

        return hours;
    }

    protected Hours getTimesheetHours(int empId, Date beginDate, Date endDate) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("empId", empId);
        params.addValue("beginDate", beginDate);
        params.addValue("endDate", endDate);
        LinkedList<Hours> hoursList;
        hoursList =  new LinkedList<Hours>(remoteNamedJdbc.query(TIMESHEET_HOURS_SQL, params,
                new HoursRowMapper()));

        Hours hours = null;
        if (hoursList.size()>0) {
            hours = hoursList.get(0);
        }
        return hours;
    }

}