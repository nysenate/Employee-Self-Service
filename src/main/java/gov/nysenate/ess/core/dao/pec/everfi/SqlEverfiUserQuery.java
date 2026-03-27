package gov.nysenate.ess.core.dao.pec.everfi;

import gov.nysenate.ess.core.dao.base.BasicSqlQuery;
import gov.nysenate.ess.core.dao.base.DbVendor;

public enum SqlEverfiUserQuery implements BasicSqlQuery {

    INSERT_MAPPING(
            """
            INSERT INTO ${essSchema}.everfi_user_ids
                (everfi_uuid, emp_id)
            VALUES
                (:everfiUuid, :employeeId)
            """),

    SELECT_ALL_MAPPINGS(
            """
            SELECT *
            FROM ${essSchema}.everfi_user_ids
            """),

    SELECT_MAPPING_BY_EMP_ID(
            """
            SELECT *
            FROM ${essSchema}.everfi_user_ids
            WHERE emp_id = :employeeId
            """),

    SELECT_MAPPING_BY_UUID(
            """
            SELECT *
            FROM ${essSchema}.everfi_user_ids
            WHERE everfi_uuid = :everfi_UUID
            """);

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
