package gov.nysenate.ess.core.controller.api;

import gov.nysenate.ess.core.client.response.base.ListViewResponse;
import gov.nysenate.ess.core.client.response.base.SimpleResponse;
import gov.nysenate.ess.core.client.response.base.ViewObjectResponse;
import gov.nysenate.ess.core.client.response.error.ErrorCode;
import gov.nysenate.ess.core.client.response.error.ViewObjectErrorResponse;
import gov.nysenate.ess.core.client.view.acknowledgement.AckDocView;
import gov.nysenate.ess.core.client.view.acknowledgement.AcknowledgementView;
import gov.nysenate.ess.core.client.view.acknowledgement.DetailedAckDocView;
import gov.nysenate.ess.core.dao.acknowledgement.AckDocDao;
import gov.nysenate.ess.core.model.acknowledgement.AckDoc;
import gov.nysenate.ess.core.model.acknowledgement.AckDocNotFoundEx;
import gov.nysenate.ess.core.model.acknowledgement.Acknowledgement;
import gov.nysenate.ess.core.model.acknowledgement.DuplicateAckEx;
import gov.nysenate.ess.core.model.auth.CorePermission;
import gov.nysenate.ess.core.model.auth.CorePermissionObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.web.bind.annotation.RequestMethod.*;

@RestController
@RequestMapping(BaseRestApiCtrl.REST_PATH + "/acknowledgement")
public class AcknowledgementApiCtrl extends BaseRestApiCtrl {


    /** The directory where ack docs are stored in the file system */
    private String ackDocDir;
    /** The uri path where ack docs are requested */
    private String ackDocResPath;

    private final AckDocDao ackDocDao;

    @Autowired
    public AcknowledgementApiCtrl(AckDocDao ackDocDao,
                                  @Value("${data.dir}") String dataDir,
                                  @Value("${data.ackdoc_subdir}") String ackDocSubdir,
                                  @Value("${resource.path}") String resPath
    ) {
        this.ackDocDao = ackDocDao;
        this.ackDocDir = dataDir + ackDocSubdir;
        this.ackDocResPath = resPath + ackDocSubdir;
    }

    // GET A LIST OF ALL DOCUMENTS
    @RequestMapping(value = "/documents", method = {GET, HEAD})
    public ListViewResponse<AckDocView> getActiveAckDocs() {

        List<AckDoc> ackDocs = ackDocDao.getActiveAckDocs();

        List<AckDocView> ackDocViews = ackDocs.stream()
                .map(ackDoc -> new AckDocView(ackDoc, ackDocResPath))
                .collect(toList());

        return ListViewResponse.of(ackDocViews, "documents");
    }

    //GET A SPECIFIC ACK DOK BY ID
    @RequestMapping(value = "/documents/{ackDocId:\\d+}", method = {GET, HEAD})
    public ViewObjectResponse<AckDocView> getAckDoc(@PathVariable int ackDocId) throws IOException {
        //check id exists
        AckDoc ackDoc = ackDocDao.getAckDoc(ackDocId);
        DetailedAckDocView detailedAckDocView = new DetailedAckDocView(ackDoc, ackDocResPath, ackDocDir);
        return new ViewObjectResponse<>(detailedAckDocView, "document");
    }

    //GET A LIST OF ACKS
    @RequestMapping(value = "/acks", method = {GET, HEAD})
    public ListViewResponse<AcknowledgementView> getAcknowledgements(@RequestParam int empId) {
        checkPermission(new CorePermission(empId, CorePermissionObject.ACKNOWLEDGEMENT, GET));

        List<Acknowledgement> acknowledgements = ackDocDao.getAllAcknowledgementsForEmp(empId);

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

        //check id if exists. Will throw an AckNotFoundEx if the document does not exist
        ackDocDao.getAckDoc(ackDocId);

        //cant ack same doc twice throw error
        List<Acknowledgement> acknowledgements = ackDocDao.getAllAcknowledgementsForEmp(empId);

        for (Acknowledgement acknowledgement : acknowledgements) {
            if (acknowledgement.getAckDocId() == ackDocId && acknowledgement.getEmpId() == empId) {
                throw new DuplicateAckEx(ackDocId);
            }
        }

        // Save the ack doc
        ackDocDao.insertAcknowledgement(new Acknowledgement(empId, ackDocId, LocalDateTime.now()));

        return new SimpleResponse(true, "Document Acknowledged", "document-acknowledged");
    }

    /* --- Exception Handlers --- */

    @ExceptionHandler(AckDocNotFoundEx.class)
    @ResponseStatus(value = NOT_FOUND)
    @ResponseBody
    public ViewObjectErrorResponse handleAckDocNotFoundEx(AckDocNotFoundEx ex) {
        return new ViewObjectErrorResponse(ErrorCode.ACK_DOC_NOT_FOUND, ex.getAckDocid());
    }

    @ExceptionHandler(DuplicateAckEx.class)
    @ResponseStatus(value = BAD_REQUEST)
    @ResponseBody
    public ViewObjectErrorResponse handleDuplicateAckEx(DuplicateAckEx ex) {
        return new ViewObjectErrorResponse(ErrorCode.DUPLICATE_ACK, ex.getAckDocid());
    }

}
