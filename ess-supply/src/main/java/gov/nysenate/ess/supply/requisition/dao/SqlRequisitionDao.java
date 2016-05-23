package gov.nysenate.ess.supply.requisition.dao;

import gov.nysenate.ess.core.dao.base.SqlBaseDao;
import gov.nysenate.ess.supply.requisition.Requisition;
import org.springframework.stereotype.Repository;

@Repository
public class SqlRequisitionDao extends SqlBaseDao implements RequisitionDao {

    @Override
    public void insertRequisition(Requisition requisition) {
    }

    @Override
    public void saveRequisition(Requisition requisition) {

    }

    @Override
    public Requisition getRequisition(int requisitionId) {
        return null;
    }
}
