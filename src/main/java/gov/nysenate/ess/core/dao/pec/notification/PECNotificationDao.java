package gov.nysenate.ess.core.dao.pec.notification;

public interface PECNotificationDao {

    /**
     * Find out if a notification was sent for a specific task for an employee
     * @param empId
     * @param taskId
     * @return
     */
    boolean wasNotificationSent(int empId, int taskId);

    /**
     * Update the database to show that the notification has been sent and with a timestamp
     * @param empId
     * @param taskId
     */
    void markNotificationSent(int empId, int taskId);
}
