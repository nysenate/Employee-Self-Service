package gov.nysenate.ess.time.dao.personnel;

import gov.nysenate.ess.core.dao.base.BasicSqlQuery;
import gov.nysenate.ess.core.dao.base.DbVendor;

public enum SqlSupervisorQuery implements BasicSqlQuery
{
    /**
     * This query returns a listing of all supervisor related transactions for employees that have at
     * one point been assigned the given 'supId'. The results of this query can be processed to determine
     * valid employee groups for a supervisor.
     */
    GET_SUP_EMP_TRANS_SQL(
        /* Subquery to get the the employee ids for all direct employees and overrides */
        "WITH empList AS (\n" +
        /*  Fetch the ids of the supervisor's direct employees. */
        "  SELECT DISTINCT 'PRIMARY' AS EMP_GROUP, NUXREFEM,\n" +
        "         NULL AS OVR_NUXREFSV, NULL AS OVR_DTSTART, NULL AS OVR_DTEND\n" +
        "  FROM ${masterSchema}.PM21PERAUDIT\n" +
        "  WHERE NUXREFSV = :supId AND CDSTATUS = 'A' \n" +
        /*  Combine that with the ids of the employees that are accessible through the sup overrides.
         *   The EMP_GROUP column will either be 'SUP_OVR' or 'EMP_OVR' to indicate the type of override. */
        "  UNION ALL\n" +
        "  SELECT DISTINCT 'OVERRIDE' AS EMP_GROUP, per.NUXREFEM,\n" +
        "                   ovr.NUXREFSVSUB, ovr.DTSTART AS OVR_DTSTART, ovr.DTEND AS OVR_DTEND\n" +
        "  FROM ${tsSchema}.PM23SUPOVRRD ovr\n" +
        "  JOIN ${masterSchema}.PM21PERAUDIT per\n" +
        "    ON ovr.NUXREFSVSUB = per.NUXREFSV OR ovr.NUXREFEMSUB = per.NUXREFEM\n" +
        "  WHERE ovr.CDSTATUS = 'A' AND per.CDSTATUS = 'A'\n" +
        "    AND ovr.NUXREFEM = :supId\n" +
        "    AND :endDate BETWEEN NVL(ovr.DTSTART, :endDate)\n" +
        "                     AND NVL(ovr.DTEND, :endDate)\n" +
        ")\n" +

        "SELECT empList.*, pers.FFNALAST, pers.FFNAFIRST,\n" +
        "       per.NUXREFSV, per.CDEMPSTATUS, per.CDSTATPER, per.DTTXNORIGIN,\n" +
        "       ptx.CDTRANS, ptx.CDTRANSTYP, ptx.DTEFFECT,\n" +
        "       ROW_NUMBER() OVER (\n" +
        "            PARTITION BY EMP_GROUP, NUXREFEM, OVR_NUXREFSV ORDER BY DTEFFECT DESC, DTTXNORIGIN DESC\n" +
        "       ) AS TRANS_RANK\n" +
        "FROM empList\n" +
        "JOIN ${masterSchema}.PM21PERAUDIT per ON empList.NUXREFEM = per.NUXREFEM\n" +
        "JOIN ${masterSchema}.PD21PTXNCODE ptx ON per.NUXREFEM = ptx.NUXREFEM AND per.NUCHANGE = ptx.NUCHANGE\n" +
        "JOIN ${masterSchema}.PM21PERSONN pers ON per.NUXREFEM = pers.NUXREFEM\n" +

        /**  Retrieve just the APP/RTP/SUP/EMP transactions unless the employee doesn't
         *   have any of them (some earlier employees may be missing APP for example). */
        "WHERE per.CDSTATUS = 'A' AND ptx.CDSTATUS = 'A'\n" +
        "  AND ptx.CDTRANSTYP = 'PER'\n" +
        "  AND ptx.DTEFFECT <= :endDate\n" +
        "  AND (\n" +
        "    ptx.CDTRANS IN ('APP', 'RTP', 'SUP', 'EMP')\n" +
        /** If the employee has no supervisor assigning transactions, allow their first personnel transaction */
        "    OR (\n" +
        "      NOT EXISTS (\n" +
        "        SELECT NUXREFEM FROM ${masterSchema}.PD21PTXNCODE\n" +
        "        WHERE CDSTATUS = 'A' AND NUXREFEM = per.NUXREFEM AND CDTRANS IN ('APP', 'RTP', 'SUP')\n" +
        "      )\n" +
        "      AND EXISTS (\n" +
        "        SELECT code.NUCHANGE\n" +
        "        FROM ${masterSchema}.PD21PTXNCODE code\n" +
        "        JOIN (\n" +
        "            SELECT NUXREFEM, NUCHANGE, DTTXNUPDATE,\n" +
        "              ROW_NUMBER() OVER (PARTITION BY NUXREFEM ORDER BY DTEFFECT ASC) AS rn\n" +
        "            FROM ${masterSchema}.PD21PTXNCODE\n" +
        "            WHERE CDSTATUS = 'A'\n" +
        "              AND CDTRANSTYP = 'PER'\n" +
        "        ) ot ON code.NUXREFEM = ot.NUXREFEM\n" +
        "          AND code.NUCHANGE = ot.NUCHANGE\n" +
        "          AND code.DTTXNUPDATE = ot.DTTXNUPDATE\n" +
        "          AND ot.rn = 1\n" +
        "        WHERE code.NUXREFEM = per.NUXREFEM AND code.NUCHANGE = per.NUCHANGE\n" +
        "      )\n" +
        "    )\n" +
        "  )" +
        "ORDER BY NUXREFEM, TRANS_RANK"),

    GET_SUP_EMP_TRANS_UPDATED_SINCE(
        "SELECT PTX.NUXREFEM, PTX.NUCHANGE, PTX.CDSTATUS, PTX.CDTRANS, PTX.NUDOCUMENT,\n" +
        "    PTX.DTEFFECT, PTX.DTTXNORIGIN, GREATEST(AUD.DTTXNUPDATE, PTX.DTTXNUPDATE) AS DTTXNUPDATE\n" +
        "FROM ${masterSchema}.PM21PERAUDIT AUD\n" +
        "JOIN ${masterSchema}.PD21PTXNCODE PTX ON AUD.NUCHANGE = PTX.NUCHANGE AND AUD.NUXREFEM = PTX.NUXREFEM\n" +
        "WHERE AUD.CDSTATUS = 'A' AND PTX.CDSTATUS = 'A'\n" +
        "    AND PTX.CDTRANS IN ('APP', 'RTP', 'SUP', 'EMP')\n" +
        "    AND (AUD.DTTXNUPDATE > :fromDate OR PTX.DTTXNUPDATE > :fromDate)"
    ),

    GET_LATEST_SUP_UPDATE_DATE(
        "SELECT MAX(DTTXNUPDATE) AS DTTXNUPDATE\n" +
        "FROM (\n" +
        "    SELECT GREATEST(AUD.DTTXNUPDATE, PTX.DTTXNUPDATE) AS DTTXNUPDATE\n" +
        "    FROM ${masterSchema}.PM21PERAUDIT AUD\n" +
        "    JOIN ${masterSchema}.PD21PTXNCODE PTX ON AUD.NUCHANGE = PTX.NUCHANGE AND AUD.NUXREFEM = PTX.NUXREFEM\n" +
        "    WHERE AUD.CDSTATUS = 'A' AND PTX.CDSTATUS = 'A'\n" +
        "        AND PTX.CDTRANS IN ('APP', 'RTP', 'SUP', 'EMP')\n" +
        "    UNION\n" +
        "    SELECT DTTXNUPDATE FROM ${tsSchema}.PM23SUPOVRRD\n" +
        ")"
    ),

    GET_SUP_OVR_IDS_UPDATED_SINCE(
        "SELECT *\n" +
        "FROM ${tsSchema}.PM23SUPOVRRD\n" +
        "WHERE DTTXNUPDATE > :fromDate"
    ),

    GET_SUP_CHAIN_EXCEPTIONS(
        "SELECT NUXREFEM, NUXREFSV, CDTYPE, CDSTATUS FROM ${masterSchema}.PM23SPCHNEX\n" +
        "WHERE CDSTATUS = 'A' AND NUXREFEM = :empId"),

    GET_SUP_OVERRIDES(
        "SELECT NUXREFEM, NUXREFSVSUB, CDSTATUS, DTSTART, DTEND, DTTXNORIGIN, DTTXNUPDATE\n" +
        "FROM ${tsSchema}.PM23SUPOVRRD\n" +
        "WHERE NUXREFEM = :empId AND NUXREFSVSUB IS NOT NULL AND CDSTATUS = 'A'"
    ),
    GET_SUP_GRANTS(
        "SELECT NUXREFEM, NUXREFSVSUB, CDSTATUS, DTSTART, DTEND, DTTXNORIGIN, DTTXNUPDATE\n" +
        "FROM ${tsSchema}.PM23SUPOVRRD\n" +
        "WHERE NUXREFSVSUB = :empId AND CDSTATUS = 'A'"
    ),

    TEST_IF_SUPERVISOR(
        "SELECT DISTINCT 1\n" +
        "FROM ${masterSchema}.PM21PERAUDIT\n" +
        "WHERE CDSTATUS = 'A' AND NUXREFSV = :empId"
    ),

    UPDATE_SUP_GRANT(
        "UPDATE ${tsSchema}.PM23SUPOVRRD\n" +
        "SET CDSTATUS = :status, DTSTART = :startDate, DTEND = :endDate\n" +
        "WHERE NUXREFEM = :granteeSupId AND NUXREFSVSUB = :granterSupId"
    ),
    INSERT_SUP_GRANT(
        "INSERT INTO ${tsSchema}.PM23SUPOVRRD (NUXREFEM, NUXREFSVSUB, CDSTATUS, DTSTART, DTEND)\n" +
        "VALUES(:granteeSupId, :granterSupId, :status, :startDate, :endDate)"
    )
    ;

    private String sql;

    SqlSupervisorQuery(String sql) {
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