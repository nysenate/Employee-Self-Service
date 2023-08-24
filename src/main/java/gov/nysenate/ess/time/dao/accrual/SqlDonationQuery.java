package gov.nysenate.ess.time.dao.accrual;

import gov.nysenate.ess.core.dao.base.BasicSqlQuery;
import gov.nysenate.ess.core.dao.base.DbVendor;

public enum SqlDonationQuery implements BasicSqlQuery {
    // TODO: may need different schema on prod

    SELECT_HOURS_DONATED_IN_RANGE("""
            SELECT SUM(NUTIMEADJ) AS sum
            FROM SASS_OWNER.PM23TIMEPOOL
            WHERE :startDate <= DTEFFECT AND DTEFFECT <= :endDate AND NUXREFEM = :empId"""),

    SELECT_EMP_DONATION_RECORDS("""
            SELECT NUTIMEADJ AS donationHours, DTEFFECT AS donationDate
            FROM SASS_OWNER.PM23TIMEPOOL
            WHERE NUXREFEM = :empId AND extract(year FROM DTEFFECT) = :year"""),

    GET_SICK_TYPE_ID("""
            SELECT NUXRTIMETYP
            FROM SASS_OWNER.PL23TIMETYP
            WHERE CDTIMETYP = 'SICK'"""),

    GET_MAX_NUPOOLTXN(
            "SELECT MAX(NUPOOLTXN)\n" +
            "FROM SASS_OWNER.PM23TIMEPOOL"),

    // TODO: use TRUNC if needed for insert and update date. may need :entryDate to make them equal
    INSERT_DONATION("" +
            "INSERT INTO SASS_OWNER.PM23TIMEPOOL\n" +
            "(       DTEFFECT,       NUXREFEM,   NUTIMEADJ,     NUXRTIMETYP,                    NUPOOLTXN)\n" +
            "VALUES (:effectiveDate, :empId,     :donation, (" + GET_SICK_TYPE_ID.sql + "), 1 + (" +   GET_MAX_NUPOOLTXN.sql + "))");

    private final String sql;

    SqlDonationQuery(String sql) {
        this.sql = sql;
    }

    @Override
    public String getSql() {
        return sql;
    }

    @Override
    public DbVendor getVendor() {
        return DbVendor.ORACLE_10g;
    }
}
