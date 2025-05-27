package gov.nysenate.ess.core.dao.pec.everfi;

import gov.nysenate.ess.core.model.pec.everfi.EverfiUserIDs;

import java.util.List;

public interface EverfiUserDao {

    /**
     * Get an {@Link EverfiUserIDs} for a given empID
     * @param empID
     * @return
     */
    EverfiUserIDs getEverfiUserIDsWithEmpID(int empID);

    /**
     * Get an {@Link EverfiUserIDs} for a given everfiUUID
     * @param everfiUUID
     * @return
     */
    EverfiUserIDs getEverfiUserIDsWithEverfiUUID(String everfiUUID);

    /**
     * Insert an everfi UUID - emp ID pair
     * @param everfiUUID
     * @param empID
     * @return
     */
    int insertEverfiUserIDs(String everfiUUID, Integer empID);

    /**
     * Returns a list of everfi ids that we want to ignore
     * @return
     */
    List<EverfiUserIDs> getIgnoredEverfiUserIDs();

    /**
     * Returns a count of all ID's in the DB. Useful for correction logic
     * @return
     */
    int everfiUserIDCount();

    /**
     * Insert a UUID into the ignored everfi uuid table
     * @param everfiUUID
     * @param empID
     */
    void insertIgnoredID(String everfiUUID, Integer empID);

    /**
     * Remove a UUID from the ignored everfi uuid table
     * @param everfiUUID
     */
    void removeIgnoredID(String everfiUUID);

}


