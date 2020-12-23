package gov.nysenate.ess.core.dao.pec.everfi;

import gov.nysenate.ess.core.dao.base.BasicSqlQuery;
import gov.nysenate.ess.core.dao.base.DbVendor;

public enum SqlEverfiUserQuery implements BasicSqlQuery {

    SELECT_EMP_BY_EVERFI_ID("SELECT * FROM ${essSchema}.everfi_user_ids WHERE everfi_uuid = :everfi_UUID"),

    SELECT_EMP_BY_EMP_ID("SELECT * FROM ${essSchema}.everfi_user_ids WHERE emp_id = :emp_id"),

    INSERT_EVERFI_USER_ID("INSERT INTO ${essSchema}.everfi_user_ids (everfi_uuid, emp_id) VALUES (:everfi_UUID,:emp_id)"),

    SELECT_IGNORED_EVERFI_USER_IDS("SELECT * FROM ${essSchema}.ignored_everfi_user_ids");

    private final String sql;

    SqlEverfiUserQuery(String sql) {
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