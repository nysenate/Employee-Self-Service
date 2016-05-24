package gov.nysenate.ess.supply.requisition.service;

import gov.nysenate.ess.supply.requisition.Requisition;
import gov.nysenate.ess.supply.requisition.RequisitionStatus;
import gov.nysenate.ess.supply.requisition.RequisitionVersion;
import gov.nysenate.ess.supply.requisition.dao.RequisitionDao;
import gov.nysenate.ess.supply.util.date.DateTimeFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.EnumSet;

@Service
public class SupplyRequisitionUpdateService implements RequisitionUpdateService {

    @Autowired private DateTimeFactory dateTimeFactory;
    @Autowired private RequisitionDao requisitionDao;

    @Override
    public void submitNewRequisition(RequisitionVersion version) {
        requisitionDao.saveRequisition(new Requisition(dateTimeFactory.now(), version));
    }

    @Override
    public void updateRequisition(int requisitionId, RequisitionVersion newVersion) {
        Requisition requisition = requisitionDao.getRequisitionById(requisitionId);
        requisition.addVersion(dateTimeFactory.now(), newVersion);
        requisitionDao.saveRequisition(requisition);
    }

    @Override
    public void undoRejection(int requisitionId) {
        Requisition requisition = requisitionDao.getRequisitionById(requisitionId);
        RequisitionVersion newVersion = requisition.getLatestVersionWithStatusIn(nonRejectedRequisitionStatuses());
        updateRequisition(requisitionId, newVersion);
    }

    private EnumSet<RequisitionStatus> nonRejectedRequisitionStatuses() {
        return EnumSet.complementOf(EnumSet.of(RequisitionStatus.REJECTED));
    }
}
