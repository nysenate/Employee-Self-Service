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
        "SELECT empList.*, per.NALAST, per.NUXREFSV, per.CDEMPSTATUS, " +
        "       ptx.CDTRANS, ptx.CDTRANSTYP, ptx.DTEFFECT, per.DTTXNORIGIN,\n" +
        "       ROW_NUMBER() " +
        "       OVER (PARTITION BY EMP_GROUP, NUXREFEM, OVR_NUXREFSV ORDER BY DTEFFECT DESC, DTTXNORIGIN DESC) AS TRANS_RANK\n" +
        "FROM (\n" +

        /**  Fetch the ids of the supervisor's direct employees. */
        "    SELECT DISTINCT 'PRIMARY' AS EMP_GROUP, NUXREFEM, NULL AS OVR_NUXREFSV\n" +
        "    FROM ${masterSchema}.PM21PERAUDIT WHERE NUXREFSV = :supId AND CDSTATUS = 'A' \n" +

        /**  Combine that with the ids of the employees that are accessible through the sup overrides.
         *   The EMP_GROUP column will either be 'SUP_OVR' or 'EMP_OVR' to indicate the type of override. */
        "    UNION ALL\n" +
        "    SELECT DISTINCT\n" +
        "    CASE \n" +
        "        WHEN ovr.NUXREFSVSUB IS NOT NULL THEN 'SUP_OVR' \n" +
        "        WHEN ovr.NUXREFEMSUB IS NOT NULL THEN 'EMP_OVR' " +
        "    END,\n" +
        "    per.NUXREFEM, ovr.NUXREFSVSUB\n" +
        "    FROM ${tsSchema}.PM23SUPOVRRD ovr\n" +
        "    LEFT JOIN ${masterSchema}.PM21PERAUDIT per ON \n" +
        "      per.CDSTATUS = 'A' AND\n" +
        "      CASE WHEN ovr.NUXREFSVSUB IS NOT NULL AND per.NUXREFSV = ovr.NUXREFSVSUB THEN 1\n" +
        "           WHEN ovr.NUXREFEMSUB IS NOT NULL AND per.NUXREFEM = ovr.NUXREFEMSUB THEN 1\n" +
        "           ELSE 0\n" +
        "      END = 1\n" +
        "    WHERE ovr.CDSTATUS = 'A' AND ovr.NUXREFEM = :supId\n" +
        "    AND :endDate BETWEEN NVL(ovr.DTSTART, :endDate) AND NVL(ovr.DTEND, :endDate)\n" +
        "    AND per.NUXREFEM IS NOT NULL\n" +
        "  ) empList\n" +
        "JOIN ${masterSchema}.PM21PERAUDIT per ON empList.NUXREFEM = per.NUXREFEM\n" +
        "JOIN ${masterSchema}.PD21PTXNCODE ptx ON per.NUXREFEM = ptx.NUXREFEM AND per.NUCHANGE = ptx.NUCHANGE\n" +

        /**  Retrieve just the APP/RTP/SUP/EMP transactions unless the employee doesn't
         *   have any of them (some earlier employees may be missing APP for example). */
        "WHERE \n" +
        "  (ptx.CDTRANS IN ('APP', 'RTP', 'SUP', 'EMP')\n" +
        /** If the employee has no supervisor assigning transactions, allow their first personnel transaction */
        "    OR (per.NUXREFEM NOT IN (\n" +
        "        SELECT DISTINCT NUXREFEM FROM ESS_MASTER.PD21PTXNCODE\n" +
        "          WHERE CDTRANS IN ('APP', 'RTP', 'SUP')\n" +
        "      )\n" +
        "      AND per.NUCHANGE IN (\n" +
        "        SELECT code.NUCHANGE\n" +
        "        FROM ESS_MASTER.PD21PTXNCODE code\n" +
        "        JOIN (\n" +
        "            SELECT NUXREFEM, NUCHANGE, DTTXNUPDATE,\n" +
        "              ROW_NUMBER() OVER (PARTITION BY NUXREFEM ORDER BY DTEFFECT ASC) AS rn\n" +
        "            FROM ESS_MASTER.PD21PTXNCODE\n" +
        "            WHERE CDSTATUS = 'A'\n" +
        "              AND CDTRANSTYP = 'PER'\n" +
        "        ) ot\n" +
        "          ON code.NUXREFEM = ot.NUXREFEM\n" +
        "            AND code.NUCHANGE = ot.NUCHANGE\n" +
        "            AND code.DTTXNUPDATE = ot.DTTXNUPDATE\n" +
        "            AND ot.rn = 1\n" +
        "          WHERE code.NUXREFEM = per.NUXREFEM\n" +
        "      )\n" +
        "    )\n" +
        "  )" +
        "  AND ptx.CDTRANSTYP = 'PER'\n" +
        "  AND ptx.CDSTATUS = 'A' AND ptx.DTEFFECT <= :endDate\n" +
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