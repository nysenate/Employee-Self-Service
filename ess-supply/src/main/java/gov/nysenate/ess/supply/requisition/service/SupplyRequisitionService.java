package gov.nysenate.ess.supply.requisition.service;

import com.google.common.collect.Range;
import gov.nysenate.ess.core.util.LimitOffset;
import gov.nysenate.ess.core.util.PaginatedList;
import gov.nysenate.ess.supply.requisition.Requisition;
import gov.nysenate.ess.supply.requisition.RequisitionStatus;
import gov.nysenate.ess.supply.requisition.RequisitionVersion;
import gov.nysenate.ess.supply.requisition.dao.RequisitionDao;
import gov.nysenate.ess.supply.requisition.exception.ConcurrentRequisitionUpdateException;
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
    public int saveRequisition(Requisition requisition) {
        return requisitionDao.saveRequisition(requisition);
    }

    @Override
    public synchronized int updateRequisition(int requisitionId, RequisitionVersion requisitionVersion, LocalDateTime lastModified) {
        Requisition persistedRequisition = requisitionDao.getRequisitionById(requisitionId);
        if (!persistedRequisition.getModifiedDateTime().equals(lastModified)) {
            throw new ConcurrentRequisitionUpdateException(requisitionId, lastModified, persistedRequisition.getModifiedDateTime());
        }
        persistedRequisition.addVersion(dateTimeFactory.now(), requisitionVersion);
        return requisitionDao.saveRequisition(persistedRequisition);
    }

    @Override
    public void undoRejection(Requisition requisition) {
        RequisitionVersion newVersion = requisition.getLatestVersionWithStatusIn(nonRejectedRequisitionStatuses());
        requisition.addVersion(dateTimeFactory.now(), newVersion);
        saveRequisition(requisition);
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

    /** {@inheritDoc} */
    @Override
    public PaginatedList<Requisition> searchOrderHistory(String destination, int customerId, EnumSet<RequisitionStatus> statuses,
                                                         Range<LocalDateTime> dateRange, String dateField, LimitOffset limitOffset) {
        return requisitionDao.searchOrderHistory(destination, customerId, statuses, dateRange, dateField, limitOffset);
    }
}
