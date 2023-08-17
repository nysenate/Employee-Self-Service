package gov.nysenate.ess.time.dao.accrual;

import gov.nysenate.ess.core.dao.base.BasicSqlQuery;
import gov.nysenate.ess.core.dao.base.DbVendor;

public enum SqlDonationQuery implements BasicSqlQuery {
    // TODO: may need different schema on prod, because inserts are needed
    SELECT_HOURS_DONATED_IN_RANGE("""
            SELECT SUM(NUTIMEADJ) as sum
            FROM SASS_OWNER.PM23TIMEPOOL
            WHERE :startDate <= DTEFFECT AND DTEFFECT <= :endDate AND NUXREFEM = :empId"""),

    SELECT_EMP_DONATION_RECORDS("""
            SELECT NUTIMEADJ as hours, DTEFFECT as date
            FROM SASS_OWNER.PM23TIMEPOOL
            WHERE NUXREFEM = :empId"""),

    INSERT_DONATION("""
            INSERT INTO SASS_OWNER.PM23TIMEPOOL
            (DTEFFECT, NUXREFEM, NUTIMEADJ, DTTXNORIGIN)
            VALUES (:effectiveDate, :empId, :hours, :entryDate)""");

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
