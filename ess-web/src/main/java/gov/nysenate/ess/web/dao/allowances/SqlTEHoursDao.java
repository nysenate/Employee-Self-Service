package gov.nysenate.ess.web.dao.allowances;

import gov.nysenate.ess.web.dao.allowances.mapper.TEHoursRowMapper;
import gov.nysenate.ess.core.dao.base.SqlBaseDao;
import gov.nysenate.ess.web.model.allowances.OldAllowanceUsage;
import gov.nysenate.ess.web.model.allowances.TEHours;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Repository
public class SqlTEHoursDao extends SqlBaseDao implements TEHoursDao
{
    protected int empId = -1;
    protected static ArrayList<TEHours> teHourses;
    protected Date beginDate;
    protected Date endDate;
    protected boolean parametersChanged = false;
    protected static final Logger logger = LoggerFactory.getLogger(OldAllowanceUsage.class);

    /** --- SQL Queries --- */

    protected static final String GET_TE_HOURS_PAID_SQL =
        "SELECT  nuxrefem, dteffect, dtendte, nuhrhrspd  " +
        "FROM pm21peraudit " +
        "WHERE nudocument like 'T%' " +
        " AND nuxrefem = :empId " +
        " AND dtendte >= :beginDate " +
        " AND dtendte <= :endDate " +
        " AND cdstatus = 'A'";

    /** --- Constructors --- */

    public SqlTEHoursDao () {

    }

    /** --- Functional Setters and Getters --- */

    public ArrayList<TEHours> getTEHours(int empId, int year) {
        beginDate = toDate(LocalDate.of(year, 1, 1));
        endDate = toDate(LocalDate.of(year, 12, 31));
        return getTEHours(empId, beginDate, endDate);
    }

    // @Override
    public  ArrayList<TEHours> getTEHours(int empId, Date beginDate, Date endDate) {
            MapSqlParameterSource params = new MapSqlParameterSource();
            logger.debug("empId:"+empId);
            params.addValue("empId", empId);
            params.addValue("beginDate", beginDate);
            params.addValue("endDate", endDate);

            teHourses = new ArrayList<>(remoteNamedJdbc.query(GET_TE_HOURS_PAID_SQL, params,
                    new TEHoursRowMapper("")));

        return teHourses;
    }

    @Override
    public TEHours sumTEHours(List<TEHours> teHourses) {

        TEHours tEHours = new TEHours();

        tEHours.setEmpId(empId);
        BigDecimal totalHours = new BigDecimal(0.0);

        for (TEHours curTEHours : teHourses) {
            totalHours = totalHours.add(curTEHours.getTEHours());

            if (tEHours.getBeginDate()==null||curTEHours.getBeginDate().before(tEHours.getBeginDate())) {
                tEHours.setBeginDate(curTEHours.getBeginDate());
            }

            if (tEHours.getEndDate()==null||curTEHours.getEndDate().after(tEHours.getEndDate())) {
                tEHours.setEndDate(curTEHours.getEndDate());
            }
        }
        tEHours.setTEHours(totalHours);
        return tEHours;
    }
}

