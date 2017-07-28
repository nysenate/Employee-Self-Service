package gov.nysenate.ess.core.dao.emergency_notification;

import gov.nysenate.ess.core.model.emergency_notification.EmergencyNotificationInfo;
import gov.nysenate.ess.core.model.emergency_notification.EmergencyNotificationInfoNotFound;

import java.util.List;

/**
 * Dao that can be used to save and retrieve {@link EmergencyNotificationInfo}
 */
public interface EmergencyNotificationInfoDao {

    /**
     * Gets {@link EmergencyNotificationInfo} for the given employee.
     *
     * @param empId int
     * @return {@link EmergencyNotificationInfo}
     * @throws EmergencyNotificationInfoNotFound if no emergency notification info exists for the employee
     */
    EmergencyNotificationInfo getEmergencyNotificationInfo(int empId) throws EmergencyNotificationInfoNotFound;

    /**
     * Retrieves all saved {@link EmergencyNotificationInfo}
     *
     * @return {@link List<EmergencyNotificationInfo>}
     */
    List<EmergencyNotificationInfo> getAllEmergencyNotificationInfo();

    /**
     * Saves the given {@link EmergencyNotificationInfo} to the data store,
     * or updates an existing notification info if one already exists for the employee.
     *
     * @param emergencyNotificationInfo {@link EmergencyNotificationInfo}
     */
    void updateEmergencyNotificationInfo(EmergencyNotificationInfo emergencyNotificationInfo);
}
