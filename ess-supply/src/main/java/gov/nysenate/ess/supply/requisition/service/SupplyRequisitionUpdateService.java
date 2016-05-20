package gov.nysenate.ess.supply.requisition.service;

import gov.nysenate.ess.supply.requisition.Requisition;
import gov.nysenate.ess.supply.requisition.RequisitionVersion;
import gov.nysenate.ess.supply.requisition.dao.RequisitionDao;
import gov.nysenate.ess.supply.requisition.dao.SqlRequisitionDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class SupplyRequisitionUpdateService implements RequisitionUpdateService {

    @Autowired private RequisitionDao requisitionDao;

    @Override
    public void submitNewRequisition(LocalDateTime orderedDateTime, RequisitionVersion version) {
        requisitionDao.insertRequisition(new Requisition(orderedDateTime, version));
    }
}
