package gov.nysenate.ess.supply.requisition.service;

import com.google.common.collect.Range;
import gov.nysenate.ess.core.util.LimitOffset;
import gov.nysenate.ess.core.util.PaginatedList;
import gov.nysenate.ess.supply.requisition.Requisition;
import gov.nysenate.ess.supply.requisition.RequisitionStatus;
import gov.nysenate.ess.supply.requisition.RequisitionVersion;
import gov.nysenate.ess.supply.requisition.dao.RequisitionDao;
import gov.nysenate.ess.supply.util.date.DateTimeFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.EnumSet;

@Service
public class SupplyRequisitionService implements RequisitionService {

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

    @Override
    public Requisition getRequisitionById(int requisitionId) {
        return requisitionDao.getRequisitionById(requisitionId);
    }

    @Override
    public PaginatedList<Requisition> searchRequisitions(String destination, String customerId, EnumSet<RequisitionStatus> statuses,
                                                         Range<LocalDateTime> dateRange, String dateField, LimitOffset limitOffset) {
        return requisitionDao.searchRequisitions(destination, customerId, statuses, dateRange, dateField, limitOffset);
    }
}
