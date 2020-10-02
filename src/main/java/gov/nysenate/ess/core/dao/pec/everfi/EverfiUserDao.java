package gov.nysenate.ess.core.dao.pec.everfi;

import gov.nysenate.ess.core.model.pec.everfi.EverfiUserIDs;

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
    public int insertEverfiUserIDs(String everfiUUID, Integer empID);

}


