package gov.nysenate.ess.time.dao.accrual;

import gov.nysenate.ess.core.dao.base.BasicSqlQuery;
import gov.nysenate.ess.core.dao.base.DbVendor;

public enum SqlDonationQuery implements BasicSqlQuery {
    // TODO: may need different schema on prod
    SELECT_DONATIONS_IN_RANGE("""
            SELECT NUTIMEADJ AS donationHours, DTEFFECT AS donationDate
            FROM SASS_OWNER.PM23TIMEPOOL
            WHERE :startDate <= DTEFFECT AND DTEFFECT <= :endDate AND NUXREFEM = :empId"""
    ),

    GET_SICK_TYPE_ID("""
            SELECT NUXRTIMETYP
            FROM SASS_OWNER.PL23TIMETYP
            WHERE CDTIMETYP = 'SICK'"""
    ),

    // Used as a sub-query to be incremented.
    GET_MAX_NUPOOLTXN(
            "SELECT MAX(NUPOOLTXN)\n" +
            "FROM SASS_OWNER.PM23TIMEPOOL"
    ),

    INSERT_DONATION(
            "INSERT INTO SASS_OWNER.PM23TIMEPOOL\n" +
            "(DTEFFECT, NUXREFEM, NUTIMEADJ, NATXNORGUSER, NATXNUPDUSER,\n" +
            "NUXRTIMETYP, NUPOOLTXN) VALUES\n" +
            "(:effectiveDate, :empId, :donation, :uid, :uid,\n" +
            "(" + GET_SICK_TYPE_ID.sql + "), 1 + (" +   GET_MAX_NUPOOLTXN.sql + "))"
    );

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
