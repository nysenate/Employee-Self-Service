package gov.nysenate.ess.supply.requisition.service;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import gov.nysenate.ess.core.util.PaginatedList;
import gov.nysenate.ess.supply.notification.SupplyEmailService;
import gov.nysenate.ess.supply.requisition.model.Requisition;
import gov.nysenate.ess.supply.requisition.model.RequisitionQuery;
import gov.nysenate.ess.supply.requisition.dao.RequisitionDao;
import gov.nysenate.ess.supply.requisition.exception.ConcurrentRequisitionUpdateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class SupplyRequisitionService implements RequisitionService {

    @Autowired private RequisitionDao requisitionDao;
    @Autowired private SupplyEmailService emailService;

    /** {@inheritDoc} */
    @Override
    public Requisition submitRequisition(Requisition requisition) {
        // Save requisition first to set it's id and check for errors.
        requisition = saveRequisition(requisition);
        emailService.sendNewRequisitionNotifications(requisition);
        return requisition;
    }

    @Override
    public synchronized Requisition saveRequisition(Requisition requisition) {
        checkPessimisticLocking(requisition);
        requisition = requisitionDao.saveRequisition(requisition);
        return requisition;
    }

    @Override
    public Requisition processRequisition(Requisition requisition) {
        requisition = requisition.process(LocalDateTime.now());
        return saveRequisition(requisition);
    }

    @Override
    public Requisition undoRequisition(Requisition requisition) {
        requisition = requisition.undoProcess(LocalDateTime.now());
        return saveRequisition(requisition);
    }

    @Override
    public Requisition rejectRequisition(Requisition requisition) {
        requisition = requisition.reject(LocalDateTime.now());
        requisition = saveRequisition(requisition);
        emailService.sendRejectEmail(requisition);
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
        // TODO: How to ensure modifiedDateTime is not updated by client?
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
    public PaginatedList<Requisition> searchRequisitions(RequisitionQuery query) {
        return requisitionDao.searchRequisitions(query);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PaginatedList<Requisition> searchOrderHistory(RequisitionQuery query) {
        return requisitionDao.searchOrderHistory(query);
    }

    @Override
    public ImmutableList<Requisition> getRequisitionHistory(int requisitionId) {
        return requisitionDao.getRequisitionHistory(requisitionId);
    }

    @Override
    public void savedInSfms(int requisitionId, boolean succeed) {
        requisitionDao.savedInSfms(requisitionId, succeed);
    }

    @Override
    public Requisition reconcileRequisition(Requisition requisition) {
        requisition = requisition.setReconiled(true);
        return saveRequisition(requisition);
    }
}
