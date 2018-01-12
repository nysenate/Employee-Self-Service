package gov.nysenate.ess.core.controller.api;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import gov.nysenate.ess.core.client.response.base.ListViewResponse;
import gov.nysenate.ess.core.client.response.base.SimpleResponse;
import gov.nysenate.ess.core.client.response.base.ViewObjectResponse;
import gov.nysenate.ess.core.client.view.acknowledgement.AcknowledgementView;
import gov.nysenate.ess.core.client.view.acknowledgement.AckDocView;
import gov.nysenate.ess.core.model.base.InvalidRequestParamEx;
import gov.nysenate.ess.core.model.acknowledgement.Acknowledgement;
import gov.nysenate.ess.core.model.acknowledgement.AckDoc;
import gov.nysenate.ess.core.util.ShiroUtils;
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

    private static final ImmutableSet<Integer> dummyAckDocIds = ImmutableSet.of(1,2,3,4);
    private static AckDoc getDummyAckDoc(int id) {
        return new AckDoc("AckDoc " + id, "dummy.pdf", true, id, LocalDateTime.now());
    }

    @RequestMapping(value = "/documents", method = {GET, HEAD})
    public ListViewResponse<AckDocView> getActiveAckDocs() {

        List<AckDoc> policies = dummyAckDocIds.stream()
                .map(AcknowledgementApiCtrl::getDummyAckDoc)
                .collect(toList());

        List<AckDocView> ackDocViews = policies.stream()
                .map(AckDocView::new)
                .collect(toList());

        return ListViewResponse.of(ackDocViews, "documents");
    }

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

    @RequestMapping(value = "/acks", method = {GET, HEAD})
    public ListViewResponse<AcknowledgementView> getAcknowledgements(@RequestParam int empId) {
        List<Acknowledgement> acknowledgements = ImmutableList.<Acknowledgement>builder()
                .add(new Acknowledgement(empId, 1, LocalDateTime.now()))
                .add(new Acknowledgement(empId, 2, LocalDateTime.now()))
                .build();

        List<AcknowledgementView> ackViews = acknowledgements.stream()
                .map(AcknowledgementView::new)
                .collect(toList());

        return ListViewResponse.of(ackViews, "acknowledgements");
    }

    @RequestMapping(value = "/acks", method = POST)
    public SimpleResponse acknowledgeDocument(@RequestParam int empId,
                                              @RequestParam int ackDocId) {

        // TODO replace these verifications with security checks and better validation
        if (ShiroUtils.getAuthenticatedEmpId() != empId) {
            throw new InvalidRequestParamEx(String.valueOf(empId), "empId", "int",
                    "testtttttttttt");
        }
        if (!dummyAckDocIds.contains(ackDocId)) {
            throw new InvalidRequestParamEx(String.valueOf(ackDocId), "ackDocId", "int",
                    "testtttttttttt");
        }

        return new SimpleResponse(true, "Document Acknowledged", "document-acknowledged");
    }

}
