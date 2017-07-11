package gov.nysenate.ess.core.dao.emergency_notification;

import gov.nysenate.ess.core.dao.base.BasicSqlQuery;
import gov.nysenate.ess.core.dao.base.DbVendor;

public enum SqlEmergencyNotificationInfoQuery implements BasicSqlQuery {

    GET_EMERGENCY_NOTIFICATION_INFO (
        "SELECT employee_id, phone_home, phone_mobile, phone_alternate,\n" +
        "  sms_subscribed, email_personal, email_alternate\n" +
        "FROM ${essSchema}.emergency_notification_info"
    ),

    GET_EMERGENCY_NOTIFICATION_INFO_BY_EMP (
        GET_EMERGENCY_NOTIFICATION_INFO.getSql() + "\n" +
        "WHERE employee_id = :empId"
    ),

    INSERT_EMERGENCY_NOTIFICATION_INFO (
        "INSERT INTO ${essSchema}.emergency_notification_info\n" +
        "       ( employee_id, phone_home, phone_mobile, phone_alternate,\n" +
        "        sms_subscribed, email_personal, email_alternate)\n" +
        "VALUES (:empId,      :homePhone, :mobilePhone, :alternatePhone,\n" +
        "       :smsSubscribed, :personalEmail, :alternateEmail)"
    ),

    UPDATE_EMERGENCY_NOTIFICATION_INFO(
        "UPDATE ${essSchema}.emergency_notification_info\n" +
        "SET phone_home = :homePhone, phone_mobile = :mobilePhone, phone_alternate = :alternatePhone,\n" +
        "  sms_subscribed = :smsSubscribed, email_personal = :personalEmail, email_alternate = :alternateEmail\n" +
        "WHERE employee_id = :empId"
    ),

    ;

    private String sql;

    SqlEmergencyNotificationInfoQuery(String sql) {
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
