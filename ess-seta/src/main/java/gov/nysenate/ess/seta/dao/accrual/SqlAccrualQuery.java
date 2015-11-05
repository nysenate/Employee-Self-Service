package gov.nysenate.ess.seta.dao.accrual;

import gov.nysenate.ess.core.dao.base.BasicSqlQuery;
import gov.nysenate.ess.core.dao.base.DbVendor;

public enum SqlAccrualQuery implements BasicSqlQuery
{
    GET_ANNUAL_ACC_SUMMARIES_TMPL(
        "SELECT \n" +
        "    NUXREFEM, DTPERIODYEAR AS YEAR, DTCLOSE AS CLOSE_DATE, DTPERLSTPOST AS DTEND, " +
        "    DTCONTSERV AS CONT_SERVICE_DATE, NUWORKHRSTOT AS WORK_HRS, NUTRVHRSTOT AS TRV_HRS_USED, \n" +
        "    NUVACHRSTOT AS VAC_HRS_USED, NUVACHRSYTD AS VAC_HRS_ACCRUED, NUVACHRSBSD AS VAC_HRS_BANKED,\n" +
        "    NUPERHRSTOT AS PER_HRS_USED, NUPERHRSYTD AS PER_HRS_ACCRUED,\n" +
        "    NUEMPHRSTOT AS EMP_HRS_USED, NUFAMHRSTOT AS FAM_HRS_USED, NUEMPHRSYTD AS EMP_HRS_ACCRUED, \n" +
        "    NUEMPHRSBSD AS EMP_HRS_BANKED, NUHOLHRSTOT AS HOL_HRS_USED, NUMISCHRSTOT AS MISC_HRS_USED, \n" +
        "    NUPAYCTRYTD AS PAY_PERIODS_YTD, NUPAYCTRBSD AS PAY_PERIODS_BANKED\n" +
        "FROM ${masterSchema}.PM23ATTEND \n" +
        // Where clause
        "%s"
    ),

    GET_ANNUAL_ACC_SUMMARIES_BY_EMP(
        String.format(GET_ANNUAL_ACC_SUMMARIES_TMPL.sql,
            "WHERE NUXREFEM = :empId AND DTPERIODYEAR <= :endYear"
        )
    ),

    GET_ANNUAL_ACC_SUMMARIES_UPDATED_SINCE(
        String.format(GET_ANNUAL_ACC_SUMMARIES_TMPL.sql,
            "WHERE DTTXNUPDATE >= :updateDateTime"
        )
    ),

    GET_PERIOD_ACC_SUMMARIES(
        "SELECT \n" +
        "    NUXREFEM, acc.DTPERIODYEAR AS YEAR, NUTOTHRSLAST AS PREV_TOTAL_HRS," +
        "    NUHRSEXPECT AS EXPECTED_TOTAL_HRS, NUVACHRSUSE AS VAC_HRS_USED, NUPERHRSUSE AS PER_HRS_USED, " +
        "    NUEMPHRSUSE AS EMP_HRS_USED, NUFAMHRSUSE AS FAM_HRS_USED, NUHOLHRSUSE AS HOL_HRS_USED, " +
        "    NUMISCHRSUSE AS MISC_HRS_USED, NUTRVHRSUSE AS TRV_HRS_USED, NUWRKHRSTOT AS WORK_HRS, \n" +
        "    NUVACHRSACC AS VAC_HRS_ACCRUED, NUPERHRSACC AS PER_HRS_ACCRUED, NUEMPHRSACC AS EMP_HRS_ACCRUED, \n" +
        "    NUVACHRSBSD AS VAC_HRS_BANKED, NUEMPHRSBSD AS EMP_HRS_BANKED, NUBIWHRSEXP AS EXPECTED_BIWEEK_HRS, " +
        "    NUBIWSICRATE AS SICK_RATE, NUBIWVACRATE AS VAC_RATE,\n" +
        "    per.CDPERIOD, per.CDSTATUS, per.DTBEGIN, per.DTEND, per.DTPERIODYEAR, per.NUPERIOD\n" +
        "FROM ${masterSchema}.PD23ACCUSAGE acc\n" +
        "JOIN (SELECT * FROM ${masterSchema}.SL16PERIOD WHERE CDPERIOD = 'AF') per ON acc.DTEND = per.DTEND\n" +
        "WHERE acc.NUXREFEM = :empId AND acc.DTEND < :beforeDate\n"
    ),

    GET_PERIOD_ACCRUAL_USAGE(
        "SELECT \n" +
        "    NUXREFEM, NUWORKHRS AS WORK_HRS, NUTRVHRS AS TRV_HRS_USED, NUHOLHRS AS HOL_HRS_USED, NUPERHRS AS PER_HRS_USED,\n" +
        "    NUEMPHRS AS EMP_HRS_USED, NUFAMHRS AS FAM_HRS_USED, NUVACHRS AS VAC_HRS_USED,\n" +
        "    NUMISCHRS AS MISC_HRS_USED, att.DTPERIODYEAR AS YEAR,\n" +
        "    per.CDPERIOD, per.CDSTATUS, per.DTBEGIN, per.DTEND, per.DTPERIODYEAR, per.NUPERIOD\n" +
        "FROM ${masterSchema}.PD23ATTEND att\n" +
        "JOIN (SELECT * FROM ${masterSchema}.SL16PERIOD WHERE CDPERIOD = 'AF') per ON att.DTEND = per.DTEND\n" +
        "WHERE att.NUXREFEM = :empId AND per.DTBEGIN >= :startDate AND per.DTEND <= :endDate\n"
    );

    private String sql;

    SqlAccrualQuery(String sql) {
        this.sql = sql;
    }

    @Override
    public String getSql() {
        return this.sql;
    }

    @Override
    public DbVendor getVendor() {
        return DbVendor.ORACLE_10g;
    }
}
