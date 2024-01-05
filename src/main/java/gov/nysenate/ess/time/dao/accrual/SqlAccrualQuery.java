package gov.nysenate.ess.time.dao.accrual;

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
        "    NUPAYCTRYTD AS PAY_PERIODS_YTD, NUPAYCTRBSD AS PAY_PERIODS_BANKED,\n" +
        "    DTTXNUPDATE\n" +
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
            "WHERE DTTXNUPDATE > :updateDateTime"
        )
    ),

    GET_PERIOD_ACC_SUMMARIES(
        "SELECT \n" +
        "    acc.NUXREFEM, acc.DTPERIODYEAR AS YEAR, acc.NUTOTHRSLAST AS PREV_TOTAL_HRS," +
        "    acc.NUHRSEXPECT AS EXPECTED_TOTAL_HRS, acc.NUVACHRSUSE AS VAC_HRS_USED, acc.NUPERHRSUSE AS PER_HRS_USED, " +
        "    acc.NUEMPHRSUSE AS EMP_HRS_USED, acc.NUFAMHRSUSE AS FAM_HRS_USED, acc.NUHOLHRSUSE AS HOL_HRS_USED, " +
        "    acc.NUMISCHRSUSE AS MISC_HRS_USED, acc.NUTRVHRSUSE AS TRV_HRS_USED, acc.NUWRKHRSTOT AS WORK_HRS, \n" +
        "    acc.NUVACHRSACC AS VAC_HRS_ACCRUED, acc.NUPERHRSACC AS PER_HRS_ACCRUED, acc.NUEMPHRSACC AS EMP_HRS_ACCRUED, \n" +
        "    acc.NUVACHRSBSD AS VAC_HRS_BANKED, acc.NUEMPHRSBSD AS EMP_HRS_BANKED, acc.NUBIWHRSEXP AS EXPECTED_BIWEEK_HRS, " +
        "    acc.NUBIWSICRATE AS SICK_RATE, acc.NUBIWVACRATE AS VAC_RATE,\n" +
        "    per.CDPERIOD, per.CDSTATUS, per.DTBEGIN, per.DTEND, per.DTPERIODYEAR, per.NUPERIOD,\n" +
        "    att.NUXREFEM AS BW_NUXREFEM,\n" +
        "    att.NUWORKHRS AS BW_WORK_HRS, att.NUTRVHRS AS BW_TRV_HRS_USED, att.NUHOLHRS AS BW_HOL_HRS_USED, att.NUPERHRS AS BW_PER_HRS_USED,\n" +
        "    att.NUEMPHRS AS BW_EMP_HRS_USED, att.NUFAMHRS AS BW_FAM_HRS_USED, att.NUVACHRS AS BW_VAC_HRS_USED,\n" +
        "    att.NUMISCHRS AS BW_MISC_HRS_USED, att.DTPERIODYEAR AS BW_YEAR\n" +
        "FROM ${masterSchema}.PD23ACCUSAGE acc\n" +
        "JOIN (SELECT * FROM ${masterSchema}.SL16PERIOD WHERE CDPERIOD = 'AF' AND CDSTATUS = 'A') per ON acc.DTEND = per.DTEND\n" +
        "JOIN ${masterSchema}.PD23ATTEND att\n" +
        "  ON acc.NUXREFEM = att.NUXREFEM AND acc.DTEND = att.DTEND\n" +
        "WHERE acc.NUXREFEM = :empId AND acc.DTEND < :beforeDate\n" +
        "  AND att.cdstatus = 'A'\n"
    ),

    GET_PERIOD_ACCRUAL_USAGE(
        "SELECT \n" +
        "    att.NUXREFEM, NUWORKHRS AS WORK_HRS, att.NUTRVHRS AS TRV_HRS_USED, NUHOLHRS AS HOL_HRS_USED, NUPERHRS AS PER_HRS_USED,\n" +
        "    att.NUEMPHRS AS EMP_HRS_USED, att.NUFAMHRS AS FAM_HRS_USED, NUVACHRS AS VAC_HRS_USED,\n" +
        "    att.NUMISCHRS AS MISC_HRS_USED, att.DTPERIODYEAR AS YEAR,\n" +
        "    per.CDPERIOD, per.CDSTATUS, per.DTBEGIN, per.DTEND, per.DTPERIODYEAR, per.NUPERIOD\n" +
        "FROM ${masterSchema}.PD23ATTEND att\n" +
        "JOIN (SELECT * FROM ${masterSchema}.SL16PERIOD WHERE CDPERIOD = 'AF') per ON att.DTEND = per.DTEND\n" +
        "WHERE att.NUXREFEM = :empId AND per.DTEND BETWEEN :startDate AND :endDate\n" +
        "  AND att.cdstatus = 'A'\n"
    ),

    GET_SA_NUMINTOTEND(
            "SELECT NUMINTOTEND\n" +
                    "FROM ${masterSchema}.pm21personn where NUXREFEM = :empId"
    );

    private final String sql;

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
