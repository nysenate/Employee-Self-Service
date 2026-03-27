package gov.nysenate.ess.core.dao.pec.everfi;

import gov.nysenate.ess.core.model.pec.everfi.EverfiEmployeeMapping;


public interface EverfiEmployeeMappingDao {

    /**
     * Get an {@Link EverfiUserIDs} for a given empID
     *
     * @param empID
     * @return
     */
    EverfiEmployeeMapping getEverfiUserIDsWithEmpID(int empID);

    /**
     * Get an {@Link EverfiUserIDs} for a given everfiUUID
     *
     * @param everfiUUID
     * @return
     */
    EverfiEmployeeMapping getEverfiUserIDsWithEverfiUUID(String everfiUUID);

    /**
     * Insert an everfi UUID - emp ID pair
     *
     * @param everfiUUID
     * @param empID
     * @return
     */
    int insertEverfiUserIDs(String everfiUUID, Integer empID);

    /**
     * Returns a count of all ID's in the DB. Useful for correction logic
     *
     * @return
     */
    int everfiUserIDCount();
}


