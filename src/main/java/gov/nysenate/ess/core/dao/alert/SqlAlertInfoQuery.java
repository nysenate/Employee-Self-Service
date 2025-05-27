package gov.nysenate.ess.core.dao.alert;

import gov.nysenate.ess.core.dao.base.BasicSqlQuery;
import gov.nysenate.ess.core.dao.base.DbVendor;

public enum SqlAlertInfoQuery implements BasicSqlQuery {

    GET_ALERT_INFO(
        "SELECT employee_id, phone_home, phone_mobile, phone_alternate,\n" +
        "  mobile_options, alternate_options, email_personal, email_alternate\n" +
        "FROM ${essSchema}.alert_info"
    ),

    GET_ALERT_INFO_BY_EMP(
        GET_ALERT_INFO.getSql() + "\n" +
        "WHERE employee_id = :empId"
    ),

    INSERT_ALERT_INFO(
        "INSERT INTO ${essSchema}.alert_info\n" +
        "       ( employee_id, phone_home, phone_mobile, phone_alternate,\n" +
        "        mobile_options, alternate_options, email_personal, email_alternate)\n" +
        "VALUES (:empId,      :homePhone, :mobilePhone, :alternatePhone,\n" +
        "       :mobileOptions::${essSchema}.contact_options,\n" +
                ":alternateOptions::${essSchema}.contact_options, :personalEmail, :alternateEmail)"
    ),

    UPDATE_ALERT_INFO(
        "UPDATE ${essSchema}.alert_info\n" +
        "SET phone_home = :homePhone, phone_mobile = :mobilePhone, phone_alternate = :alternatePhone,\n" +
        "  mobile_options = :mobileOptions::${essSchema}.contact_options,\n" +
        "alternate_options = :alternateOptions::${essSchema}.contact_options,\n" +
        "email_personal = :personalEmail,\n" +
        "  email_alternate = :alternateEmail\n" +
        "WHERE employee_id = :empId"
    );

    private final String sql;

    SqlAlertInfoQuery(String sql) {
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
