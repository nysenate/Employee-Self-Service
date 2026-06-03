package gov.nysenate.ess.core.dao.pec.everfi;

import gov.nysenate.ess.core.model.pec.everfi.EverfiEmployeeMapping;

import java.util.List;
import java.util.Optional;


public interface EverfiEmployeeMappingDao {

    /**
     * Get all Everfi employee mappings.
     *
     * @return
     */
    List<EverfiEmployeeMapping> findAll();

    /**
     * Get an {@link EverfiEmployeeMapping} for a given empId
     *
     * @param empId
     * @return
     */
    Optional<EverfiEmployeeMapping> findByEmpId(int empId);

    /**
     * Get an {@link EverfiEmployeeMapping} for a given everfiUuid
     *
     * @param everfiUuid
     * @return
     */
    Optional<EverfiEmployeeMapping> findByEverfiUuid(String everfiUuid);

    /**
     * Insert an {@link EverfiEmployeeMapping}
     *
     * @param mapping
     * @return
     */
    int insert(EverfiEmployeeMapping mapping);

}


