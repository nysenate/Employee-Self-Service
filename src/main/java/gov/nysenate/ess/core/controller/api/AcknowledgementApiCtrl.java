package gov.nysenate.ess.core.controller.api;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import gov.nysenate.ess.core.client.response.base.ListViewResponse;
import gov.nysenate.ess.core.client.response.base.SimpleResponse;
import gov.nysenate.ess.core.client.response.base.ViewObjectResponse;
import gov.nysenate.ess.core.client.view.acknowledgement.AcknowledgementView;
import gov.nysenate.ess.core.client.view.acknowledgement.AckDocView;
import gov.nysenate.ess.core.dao.acknowledgement.AckDocDao;
import gov.nysenate.ess.core.model.auth.CorePermission;
import gov.nysenate.ess.core.model.auth.CorePermissionObject;
import gov.nysenate.ess.core.model.base.InvalidRequestParamEx;
import gov.nysenate.ess.core.model.acknowledgement.Acknowledgement;
import gov.nysenate.ess.core.model.acknowledgement.AckDoc;
import gov.nysenate.ess.core.util.ShiroUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.springframework.web.bind.annotation.RequestMethod.*;

@RestController
@RequestMapping(BaseRestApiCtrl.REST_PATH + "/acknowledgement")
public class AcknowledgementApiCtrl extends BaseRestApiCtrl {

    @Autowired AckDocDao ackDocDao;

    private static final ImmutableSet<Integer> dummyAckDocIds = ImmutableSet.of(1,2,3,4);
    private static AckDoc getDummyAckDoc(int id) {
        return new AckDoc("AckDoc " + id, "dummy.pdf", true, id, LocalDateTime.now());
    }

    // GET A LIST OF ALL DOCUMENTS
    @RequestMapping(value = "/documents", method = {GET, HEAD})
    public ListViewResponse<AckDocView> getActiveAckDocs() {

        List<AckDoc> ackDocs = dummyAckDocIds.stream()
                .map(AcknowledgementApiCtrl::getDummyAckDoc)
                .collect(toList());

        List<AckDocView> ackDocViews = ackDocs.stream()
                .map(AckDocView::new)
                .collect(toList());

        return ListViewResponse.of(ackDocViews, "documents");
    }

    //GET A SPECIFIC ACK DOK BY ID
    @RequestMapping(value = "/documents/{ackDocId:\\d+}", method = {GET, HEAD})
    public ViewObjectResponse<AckDocView> getAckDoc(@PathVariable int ackDocId) {
        if (!dummyAckDocIds.contains(ackDocId)) {
            throw new InvalidRequestParamEx(String.valueOf(ackDocId), "ackDocId", "int",
                    "testtttttttttt");
        }
        AckDoc ackDoc = getDummyAckDoc(ackDocId);
        AckDocView ackDocView = new AckDocView(ackDoc);
        return new ViewObjectResponse<>(ackDocView, "document");
    }

    //GET A LIST OF ACKS
    @RequestMapping(value = "/acks", method = {GET, HEAD})
    public ListViewResponse<AcknowledgementView> getAcknowledgements(@RequestParam int empId) {
        checkPermission(new CorePermission(empId, CorePermissionObject.ACKNOWLEDGEMENT, GET));

        List<Acknowledgement> acknowledgements = ImmutableList.<Acknowledgement>builder()
                .add(new Acknowledgement(empId, 1, LocalDateTime.now()))
                .add(new Acknowledgement(empId, 2, LocalDateTime.now()))
                .build();

        List<AcknowledgementView> ackViews = acknowledgements.stream()
                .map(AcknowledgementView::new)
                .collect(toList());

        return ListViewResponse.of(ackViews, "acknowledgements");
    }

    //POST AN ACK FROM THE USER ON AN ACK DOC
    @RequestMapping(value = "/acks", method = POST)
    public SimpleResponse acknowledgeDocument(@RequestParam int empId,
                                              @RequestParam int ackDocId) {
        checkPermission(new CorePermission(empId, CorePermissionObject.ACKNOWLEDGEMENT, POST));

        // TODO replace these verifications with security checks and better validation
        if (!dummyAckDocIds.contains(ackDocId)) {
            throw new InvalidRequestParamEx(String.valueOf(ackDocId), "ackDocId", "int",
                    "testtttttttttt");
        }

        return new SimpleResponse(true, "Document Acknowledged", "document-acknowledged");
    }

}
