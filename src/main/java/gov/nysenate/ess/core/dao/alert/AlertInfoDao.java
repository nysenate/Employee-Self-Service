package gov.nysenate.ess.core.dao.alert;

import gov.nysenate.ess.core.model.alert.AlertInfo;
import gov.nysenate.ess.core.model.alert.AlertInfoNotFound;

import java.util.List;

/**
 * Dao that can be used to save and retrieve {@link AlertInfo}
 */
public interface AlertInfoDao {

    /**
     * Gets {@link AlertInfo} for the given employee.
     *
     * @param empId int
     * @return {@link AlertInfo}
     * @throws AlertInfoNotFound if no alert info exists for the employee
     */
    AlertInfo getAlertInfo(int empId) throws AlertInfoNotFound;

    /**
     * Retrieves all saved {@link AlertInfo}
     *
     * @return {@link List<AlertInfo>}
     */
    List<AlertInfo> getAllAlertInfo();

    /**
     * Saves the given {@link AlertInfo} to the data store,
     * or updates an existing alert info if one already exists for the employee.
     *
     * @param alertInfo {@link AlertInfo}
     */
    void updateAlertInfo(AlertInfo alertInfo);
}
