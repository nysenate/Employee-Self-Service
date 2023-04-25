package gov.nysenate.ess.core.dao.pec.notification;

import gov.nysenate.ess.core.dao.base.BasicSqlQuery;
import gov.nysenate.ess.core.dao.base.DbVendor;

public enum SqlPECNotificationQuery implements BasicSqlQuery {

    WAS_NOTIFICATION_SENT("SELECT first_notification_sent " +
            "FROM ${essSchema}.personnel_task_assignment " +
            "WHERE emp_id = :empId AND task_id = :taskId"),

    MARK_NOTIFICAION_SENT("UPDATE ${essSchema}.personnel_task_assignment " +
            "SET first_notification_sent = true, first_notification_time = now() " +
            "WHERE emp_id = :empId AND task_id = :taskId");

    private final String sql;

    SqlPECNotificationQuery(String sql) {
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
