package gov.nysenate.ess.supply.requisition.service;

import com.google.common.collect.Range;
import gov.nysenate.ess.core.service.notification.email.simple.componet.SimpleEmailContent;
import gov.nysenate.ess.core.service.notification.email.simple.componet.SimpleEmailSubject;
import gov.nysenate.ess.core.service.notification.email.simple.componet.SimpleEmailTemplate;
import gov.nysenate.ess.core.service.notification.email.simple.header.SimpleEmailHeader;
import gov.nysenate.ess.core.util.LimitOffset;
import gov.nysenate.ess.core.util.PaginatedList;
import gov.nysenate.ess.supply.requisition.Requisition;
import gov.nysenate.ess.supply.requisition.RequisitionStatus;
import gov.nysenate.ess.supply.requisition.RequisitionVersion;
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

@Service
public class SupplyRequisitionService implements RequisitionService {

    @Autowired private DateTimeFactory dateTimeFactory;
    @Autowired private RequisitionDao requisitionDao;
    @Autowired
    private SendSimpleEmail sendSimpleEmail;

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
        int res = requisitionDao.saveRequisition(persistedRequisition);
        if (requisitionVersion.getStatus().equals(RequisitionStatus.REJECTED)) {
            /**
             * Subject
             */
            SimpleEmailSubject subject = new SimpleEmailSubject(Color.black, "Your order (" + requisitionId + ") has been reject");
            /**
             * elements
             */
            SimpleEmailContent detail = new SimpleEmailContent(Color.black, requisitionVersion.toOrderString(), "$detail$");
            SimpleEmailContent note = new SimpleEmailContent(Color.black, requisitionVersion.getNote().get(), "$note$");
            SimpleEmailContent rId = new SimpleEmailContent(Color.black, String.valueOf(requisitionId), "$requisitionId$");
            SimpleEmailContent rejecter = new SimpleEmailContent(Color.black, requisitionVersion.getIssuer().orElse(requisitionVersion.getCreatedBy()).getFullName() + "\n" + requisitionVersion.getIssuer().orElse(requisitionVersion.getCreatedBy()).getEmail(), "$rejecter$");
            SimpleEmailContent cname = new SimpleEmailContent(Color.black, String.valueOf(requisitionVersion.getCustomer().getFirstName() + " " + requisitionVersion.getCustomer().getLastName()), "$cname$");
            SimpleEmailTemplate reject = null;
            try {
                reject = new SimpleEmailTemplate(Color.black, "", "reject_email");
            } catch (IOException e) {
                e.printStackTrace();
            }
            ArrayList<gov.nysenate.ess.core.service.notification.base.message.base.Component> simpleEmailContentList = new ArrayList();
            simpleEmailContentList.add(note);
            simpleEmailContentList.add(rId);
            simpleEmailContentList.add(cname);
            simpleEmailContentList.add(reject);
            simpleEmailContentList.add(detail);
            simpleEmailContentList.add(rejecter);
            sendSimpleEmail.send(requisitionVersion.getIssuer().orElse(requisitionVersion.getCreatedBy()), requisitionVersion.getCustomer(), simpleEmailContentList, new SimpleEmailHeader(), subject, 1);
        }
        return res;
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
