package gov.nysenate.ess.supply.requisition.service;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Range;
import gov.nysenate.ess.core.util.LimitOffset;
import gov.nysenate.ess.core.util.PaginatedList;
import gov.nysenate.ess.supply.notification.SupplyEmailService;
import gov.nysenate.ess.supply.requisition.Requisition;
import gov.nysenate.ess.supply.requisition.RequisitionStatus;
import gov.nysenate.ess.supply.requisition.dao.RequisitionDao;
import gov.nysenate.ess.supply.requisition.exception.ConcurrentRequisitionUpdateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.Optional;

@Service
public class SupplyRequisitionService implements RequisitionService {

    @Autowired private RequisitionDao requisitionDao;
    @Autowired private SupplyEmailService emailService;

    @Override
    public synchronized Requisition saveRequisition(Requisition requisition) {

        checkPessimisticLocking(requisition);
        requisition = requisitionDao.saveRequisition(requisition);
        if (requisition.getStatus() == RequisitionStatus.REJECTED) {
            emailService.sendRejectEmail(requisition);
        }
        return requisition;
    }

    /**
     * Ensure this requisition has not been updated behind the back of the user.
     * Gets the matching requisition from the database, and compares its modified date time
     * with that of the new {@code requisition}. If they do not match then the requisition
     * has been updated by someone else and we should not save it.
     * @param requisition
     */
    private void checkPessimisticLocking(Requisition requisition) {
        Optional<Requisition> previousRevision = requisitionDao.getRequisitionById(requisition.getRequisitionId());
        if (previousRevision.isPresent()) {
            if (!previousRevision.get().getModifiedDateTime().equals(requisition.getModifiedDateTime())) {
                throw new ConcurrentRequisitionUpdateException(requisition.getRequisitionId(),
                                                               requisition.getModifiedDateTime().orElse(null),
                                                               previousRevision.get().getModifiedDateTime().orElse(null));
            }
        }
    }

    @Override
    public Optional<Requisition> getRequisitionById(int requisitionId) {
        return requisitionDao.getRequisitionById(requisitionId);
    }

    @Override
    public PaginatedList<Requisition> searchRequisitions(String destination, String customerId, EnumSet<RequisitionStatus> statuses,
                                                         Range<LocalDateTime> dateRange, String dateField, String savedInSfms, LimitOffset limitOffset, String issuerID) {
        return requisitionDao.searchRequisitions(destination, customerId, statuses, dateRange, dateField, savedInSfms, limitOffset, issuerID);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PaginatedList<Requisition> searchOrderHistory(String destination, int customerId, EnumSet<RequisitionStatus> statuses,
                                                         Range<LocalDateTime> dateRange, String dateField, LimitOffset limitOffset) {
        return requisitionDao.searchOrderHistory(destination, customerId, statuses, dateRange, dateField, limitOffset);
    }

    @Override
    public ImmutableList<Requisition> getRequisitionHistory(int requisitionId) {
        return requisitionDao.getRequisitionHistory(requisitionId);
    }

    @Override
    public void savedInSfms(int requisitionId, boolean succeed) {
        requisitionDao.savedInSfms(requisitionId, succeed);
    }
}
