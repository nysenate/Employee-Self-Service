package gov.nysenate.ess.supply.requisition.service;

import com.google.common.collect.Range;
import gov.nysenate.ess.core.service.notification.email.simple.component.SimpleEmailContent;
import gov.nysenate.ess.core.service.notification.email.simple.component.SimpleEmailSubject;
import gov.nysenate.ess.core.service.notification.email.simple.component.SimpleEmailTemplate;
import gov.nysenate.ess.core.service.notification.email.simple.header.SimpleEmailHeader;
import gov.nysenate.ess.core.util.LimitOffset;
import gov.nysenate.ess.core.util.PaginatedList;
import gov.nysenate.ess.supply.requisition.Requisition;
import gov.nysenate.ess.supply.requisition.RequisitionStatus;
import gov.nysenate.ess.supply.requisition.dao.RequisitionDao;
import gov.nysenate.ess.supply.requisition.exception.ConcurrentRequisitionUpdateException;
import gov.nysenate.ess.supply.util.date.DateTimeFactory;
import gov.nysenate.ess.supply.util.mail.SendSimpleEmail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Optional;

@Service
public class SupplyRequisitionService implements RequisitionService {

    @Autowired private RequisitionDao requisitionDao;
    @Autowired private SendSimpleEmail sendSimpleEmail;

    @Override
    public synchronized Requisition saveRequisition(Requisition requisition) {
        checkPessimisticLocking(requisition);
        requisition = requisitionDao.saveRequisition(requisition);
        // TODO: email
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

//    @Override
//    public synchronized int updateRequisition(int requisitionId, Requisition requisitionVersion, LocalDateTime lastModified) {
//        Requisition persistedRequisition = requisitionDao.getRequisitionById(requisitionId);
//        if (!persistedRequisition.getModifiedDateTime().equals(lastModified)) {
//            throw new ConcurrentRequisitionUpdateException(requisitionId, lastModified, persistedRequisition.getModifiedDateTime());
//        }
//        persistedRequisition.addVersion(dateTimeFactory.now(), requisitionVersion);
//        int res = requisitionDao.saveRequisition(persistedRequisition);
//        if (requisitionVersion.getStatus().equals(RequisitionStatus.REJECTED)) {
//            /**
//             * Subject
//             */
//            SimpleEmailSubject subject = new SimpleEmailSubject(Color.black, "Your order (" + requisitionId + ") has been reject");
//            /**
//             * elements
//             */
//            SimpleEmailContent detail = new SimpleEmailContent(Color.black, requisitionVersion.toOrderString(), "$detail$");
//            SimpleEmailContent note = new SimpleEmailContent(Color.black, requisitionVersion.getNote().get(), "$note$");
//            SimpleEmailContent rId = new SimpleEmailContent(Color.black, String.valueOf(requisitionId), "$requisitionId$");
//            SimpleEmailContent rejecter = new SimpleEmailContent(Color.black, requisitionVersion.getIssuer().orElse(requisitionVersion.getModifiedBy()).getFullName() + "\n" + requisitionVersion.getIssuer().orElse(requisitionVersion.getModifiedBy()).getEmail(), "$rejecter$");
//            SimpleEmailContent cname = new SimpleEmailContent(Color.black, String.valueOf(requisitionVersion.getCustomer().getFirstName() + " " + requisitionVersion.getCustomer().getLastName()), "$cname$");
//            SimpleEmailTemplate reject = null;
//            try {
//                reject = new SimpleEmailTemplate(Color.black, "", "reject_email");
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            ArrayList<gov.nysenate.ess.core.service.notification.base.message.base.Component> simpleEmailContentList = new ArrayList();
//            simpleEmailContentList.add(note);
//            simpleEmailContentList.add(rId);
//            simpleEmailContentList.add(cname);
//            simpleEmailContentList.add(reject);
//            simpleEmailContentList.add(detail);
//            simpleEmailContentList.add(rejecter);
//            sendSimpleEmail.send(requisitionVersion.getIssuer().orElse(requisitionVersion.getModifiedBy()), requisitionVersion.getCustomer(), simpleEmailContentList, new SimpleEmailHeader(), subject, 1);
//        }
//        return res;
//        return 0;
//    }

    @Override
    public Optional<Requisition> getRequisitionById(int requisitionId) {
        return requisitionDao.getRequisitionById(requisitionId);
    }

    @Override
    public PaginatedList<Requisition> searchRequisitions(String destination, String customerId, EnumSet<RequisitionStatus> statuses,
                                                         Range<LocalDateTime> dateRange, String dateField, LimitOffset limitOffset) {
        return requisitionDao.searchRequisitions(destination, customerId, statuses, dateRange, dateField, limitOffset);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PaginatedList<Requisition> searchOrderHistory(String destination, int customerId, EnumSet<RequisitionStatus> statuses,
                                                         Range<LocalDateTime> dateRange, String dateField, LimitOffset limitOffset) {
        return requisitionDao.searchOrderHistory(destination, customerId, statuses, dateRange, dateField, limitOffset);
    }
}
