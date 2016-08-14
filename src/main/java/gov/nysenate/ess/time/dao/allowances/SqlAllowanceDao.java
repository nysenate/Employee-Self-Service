package gov.nysenate.ess.time.dao.allowances;

import gov.nysenate.ess.time.dao.allowances.mapper.AllowanceRowMapper;
import gov.nysenate.ess.time.dao.allowances.mapper.AmountExceedRowMapper;
import gov.nysenate.ess.core.dao.base.SqlBaseDao;
import gov.nysenate.ess.core.model.payroll.SalaryRec;
import gov.nysenate.ess.time.model.allowances.OldAllowanceUsage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

@Repository
public class SqlAllowanceDao extends SqlBaseDao implements AllowanceDao
{
    private static final Logger logger = LoggerFactory.getLogger(OldAllowanceUsage.class);

    /** --- SQL Queries --- */

    protected static final String GET_ALLOWANCE_USAGE_SQL =
        "WITH TE_PAY AS (\n" +
        "   SELECT /*+ MATERIALIZE */  paud.NUXREFEM, paud.NUDOCUMENT, paud.DTEFFECT, paud.DTENDTE, paud.NUHRHRSPD,\n" +
        "       paud.MOTOTHRSPD, paud.MOPRIORYRTE, pers.MOAMTEXCEED, paud.MOSALBIWKLY, paud.DTTXNORIGIN, period.DTEND,\n" +
        "       pers.DTAPPOINTFRM, pers.DTAPPOINT, pers.DTCONTSERV, paud.ROWID ROWIDCUR, paud.NULINE,\n" +
        "       LAST_VALUE(paud.DTENDTE) OVER (\n" +
        "           PARTITION BY paud.NUXREFEM,paud.NUDOCUMENT\n" +
        "           ORDER BY  paud.DTENDTE, paud.DTTXNORIGIN \n" +
        "           ROWS BETWEEN UNBOUNDED PRECEDING AND UNBOUNDED FOLLOWING) DTENDTELAST,\n" +
        "       LAST_VALUE(paud.ROWID) OVER (\n" +
        "           PARTITION BY paud.NUXREFEM,paud.NUDOCUMENT\n" +
        "           ORDER BY  paud.DTENDTE, paud.DTTXNORIGIN\n" +
        "           ROWS BETWEEN UNBOUNDED PRECEDING AND UNBOUNDED FOLLOWING) ROWIDMAX,\n" +
        "       LAST_VALUE(paud.NULINE) OVER (\n" +
        "           PARTITION BY paud.NUXREFEM\n" +
        "           ORDER BY  paud.DTTXNORIGIN\n" +
        "           ROWS BETWEEN UNBOUNDED PRECEDING AND UNBOUNDED FOLLOWING) NULINELAST\n"+
        "   FROM SASS_OWNER.PM21PERAUDIT paud\n"+
        "   JOIN SASS_OWNER.SL16PERIOD period ON (\n" +
        "       paud.DTEFFECT BETWEEN period.DTBEGIN AND period.DTEND\n"+
        "       AND period.CDPERIOD = 'PF'\n"+
        "       AND period.CDSTATUS = 'A')\n"+
        "   JOIN SASS_OWNER.PM21PERSONN pers ON (pers.NUXREFEM = paud.NUXREFEM)\n"+
        "   WHERE paud.NUXREFEM = :empId AND paud.NUDOCUMENT LIKE 'T%'\n"+
        "       AND paud.DTENDTE >= :janDate AND paud.CDSTATUS = 'A'\n"+
        ")\n"+
        "SELECT MIN(NUXREFEM) NUXREFEM, SUM(NUHRHRSPD) AS TE_HRS_PAID,\n" +
        "       SUM(NVL(MOTOTHRSPD, 0)) - SUM(NVL(MOPRIORYRTE,0)) AS TE_AMOUNT_PAID,\n" +
        "       (SUM(NVL(MOTOTHRSPD, 0)) -  SUM(NVL(MOPRIORYRTE,0)) ) MOTESPEND,  MAX(DTENDTE) DTENDTE\n"+
        "FROM TE_PAY\n"+
        "WHERE ROWIDMAX = ROWIDCUR AND NULINE = NULINELAST";

    protected static final String GET_AMOUNT_EXCEED_POT_SQL =
        "SELECT b.moamtexceed, b.dteffect, b.dttxnorigin, a.nuxrefem\n" +
        "FROM SASS_OWNER.pd21ptxncode a\n" +
        "   JOIN SASS_OWNER.PM21PERAUDIT b ON (a.nuxrefem = b.nuxrefem AND a.nuchange = b.nuchange)\n" +
        "WHERE A.NUXREFEM = :empId\n" +
        "   AND a.dteffect <= :decDate\n" +
        "   AND a.cdtrans = 'EXC'\n" +
        "   AND a.cdstatus = 'A'\n" +
        "   AND b.cdstatus = 'A'\n" +
        "ORDER BY b.dteffect DESC, b.dttxnorigin DESC";

    protected static final String GET_SALARY_POT_SQL =
        "SELECT b.mosalbiwkly, b.dteffect, b.dttxnorigin, a.nuxrefem\n" +
        "   FROM SASS_OWNER.pd21ptxncode a\n" +
        "   JOIN SASS_OWNER.PM21PERAUDIT b ON (a.nuxrefem = b.nuxrefem AND a.nuchange = b.nuchange)\n" +
        "WHERE A.NUXREFEM = :empId\n" +
        "   AND a.cdtrans = 'SAL'\n" +
        "   AND a.cdstatus = 'A'\n" +
        "   AND b.cdstatus = 'A'\n" +
        "ORDER BY b.dteffect DESC, b.dttxnorigin DESC";

//    protected static final String GET_ALLOWANCE_USAGE_SQL =

    /** {@inheritDoc} */
    @Override
     public OldAllowanceUsage getAllowanceUsage(int empId, int year) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        LocalDate janDate = LocalDate.of(year, 1, 1);
        params.addValue("empId", empId);
        params.addValue("janDate", toDate(janDate));
        LinkedList<OldAllowanceUsage> annualAllowanceRecs;
        annualAllowanceRecs = new LinkedList<>(remoteNamedJdbc.query(GET_ALLOWANCE_USAGE_SQL, params,
                new AllowanceRowMapper("")));

        params = new MapSqlParameterSource();
        LocalDate decDate = LocalDate.of(year, 12, 31);
        params.addValue("empId", empId);
        params.addValue("decDate", toDate(decDate));

        LinkedList<BigDecimal> amountExceedRecs = null;
        amountExceedRecs = new LinkedList<>(remoteNamedJdbc.query(GET_AMOUNT_EXCEED_POT_SQL, params,
                new AmountExceedRowMapper("")));

        if (amountExceedRecs==null || amountExceedRecs.size()==0) {
            annualAllowanceRecs.get(0).setMoneyAllowed(null);
        }
        else {
            annualAllowanceRecs.get(0).setMoneyAllowed(amountExceedRecs.get(0));
        }

        LinkedList<SalaryRec> salaries = null;
        //params = new MapSqlParameterSource();
        //params.addValue("empId", empId);
        //salaries = new LinkedList<>(remoteNamedJdbc.query(GET_SALARY_POT_SQL, params,
              //new SalaryRowMapper("")));
        Map<String, String> matchValues = new HashMap<String, String>();
        matchValues.put("CDPAYTYPE", "TE");
        String[] columnChangeFilter =  {"MOSALBIWKLY"};

//        salaries = setSalaryRecs(auditHistory.getMatchedAuditRecords(matchValues, true, columnChangeFilter));

        annualAllowanceRecs.get(0).setSalaryRecs(salaries);

        return annualAllowanceRecs.get(0);
    }

//    protected LinkedList<SalaryRec> setSalaryRecs(List<Map<String, String>> auditRecs) {
//        LinkedList<SalaryRec> salaries = null;
//        salaries = new LinkedList<SalaryRec>();
//        Map<String, String> currentAuditRec = null;
//
//        for (int x=0;x <auditRecs.size(); x++) {
//            SalaryRec salaryRec = new SalaryRec();
//            currentAuditRec = auditRecs.get(x);
//            logger.debug(OutputUtils.toJson(currentAuditRec));
//
//            salaryRec.setSalary(new BigDecimal(currentAuditRec.get("MOSALBIWKLY")));
//            try {
//                salaryRec.setEffectDate(new SimpleDateFormat("MM/dd/yyyy").parse(currentAuditRec.get("EffectDate")));
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
//            salaries.add(salaryRec);
//        }
//
//        return salaries;
//    }

}


