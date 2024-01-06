package gov.nysenate.ess.supply.authorization.responsibilityhead;

import gov.nysenate.ess.core.dao.base.BasicSqlQuery;
import gov.nysenate.ess.core.dao.base.DbVendor;

public enum SqlTempResponsibilityHeadQuery implements BasicSqlQuery {

    ACTIVE_TEMP_RESP_HD(
            "SELECT id, employee_id, rch_code, start_date, end_date \n" +
                    "FROM ${supplySchema}.employee_temporary_rch \n" +
                    "WHERE :effectiveDateTime BETWEEN start_date AND end_date"
    ),
    ACTIVE_TEMP_RESP_HD_BY_EMP(
            ACTIVE_TEMP_RESP_HD.getSql() + " \n" +
                    "AND employee_id = :empId"
    ),
    INSERT_TEMP_RESP_HD(
            "INSERT INTO ${supplySchema}.employee_temporary_rch(employee_id, rch_code, start_date, end_date) \n" +
                    "VALUES(:empId, :rchCode, :startDate, :endDate)"
    ),
    UPDATE_TEMP_RESP_HD(
            "UPDATE ${supplySchema}.employee_temporary_rch \n" +
                    "SET employee_id = :empId, \n" +
                    "rch_code = :rchCode, \n" +
                    "start_date = :startDate, \n" +
                    "end_date = :endDate"
    ),
    DELETE_TEMP_RESP_HD(
            "DELETE FROM ${supplySchema}.employee_temporary_rch \n" +
                    "WHERE id = :id"
    );

    private final String sql;

    SqlTempResponsibilityHeadQuery(String sql) {
        this.sql = sql;
    }

    @Override
    public String getSql() {
        return this.sql;
    }

    @Override
    public DbVendor getVendor() {
        return DbVendor.POSTGRES;
    }
}
