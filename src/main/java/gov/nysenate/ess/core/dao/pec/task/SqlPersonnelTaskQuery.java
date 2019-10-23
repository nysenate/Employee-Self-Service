package gov.nysenate.ess.core.dao.pec.task;

import gov.nysenate.ess.core.dao.base.BasicSqlQuery;
import gov.nysenate.ess.core.dao.base.DbVendor;

public enum SqlPersonnelTaskQuery implements BasicSqlQuery {

    SELECT_ALL_TASKS("SELECT * FROM ${essSchema}.personnel_task"),
    ;

    private final String sql;

    SqlPersonnelTaskQuery(String sql) {
        this.sql = sql;
    }

    @Override
    public String getSql() {
        return sql;
    }

    @Override
    public DbVendor getVendor() {
        return DbVendor.POSTGRES;
    }
}
