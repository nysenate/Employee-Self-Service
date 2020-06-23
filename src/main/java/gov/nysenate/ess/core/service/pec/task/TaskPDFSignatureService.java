package gov.nysenate.ess.core.service.pec.task;

import gov.nysenate.ess.core.dao.pec.assignment.SqlPersonnelTaskAssignmentDao;
import gov.nysenate.ess.core.dao.pec.task.detail.AckDocTaskDetailDao;
import gov.nysenate.ess.core.model.pec.PersonnelTask;
import gov.nysenate.ess.core.model.pec.PersonnelTaskAssignment;
import gov.nysenate.ess.core.model.pec.acknowledgment.AckDoc;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.service.personnel.EmployeeInfoService;
import org.apache.commons.io.FileUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.PDSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Calendar;

import static gov.nysenate.ess.core.model.pec.PersonnelTaskType.DOCUMENT_ACKNOWLEDGMENT;

@Service
public class TaskPDFSignatureService {

    private static final Logger logger = LoggerFactory.getLogger(TaskPDFSignatureService.class);

    @Value("${data.dir}") String dataDir;
    @Value("${data.ackdoc_subdir}") String ackDocSubDir;
    @Value("${data.pdf_subdir}") String pdfSubDir;
    @Value("${pec.nysenate.seal}") String nysenateSeal;

    private final EmployeeInfoService empInfoService;
    private final SqlPersonnelTaskAssignmentDao taskAssignmentDao;
    private final CachedPersonnelTaskService personnelTaskService;
    private final AckDocTaskDetailDao ackDocTaskDetailDao;

    @Autowired
    public TaskPDFSignatureService(EmployeeInfoService empInfoService,
                                   SqlPersonnelTaskAssignmentDao taskAssignmentDao,
                                   CachedPersonnelTaskService personnelTaskService,
                                   AckDocTaskDetailDao ackDocTaskDetailDao) {
        this.empInfoService = empInfoService;
        this.taskAssignmentDao =  taskAssignmentDao;
        this.personnelTaskService = personnelTaskService;
        this.ackDocTaskDetailDao = ackDocTaskDetailDao;
    }

    public File createEmployeeSignatureForTask(int empID, int taskID) throws IOException {
        String ackDocDir = dataDir + ackDocSubDir;
        String pdfDir = dataDir + pdfSubDir;
        PersonnelTask task = personnelTaskService.getPersonnelTask(taskID);
        PersonnelTaskAssignment taskAssignment = taskAssignmentDao.getTaskForEmp(empID, taskID);
        Employee employee = empInfoService.getEmployee(empID);
        String reasonLine1 = "I understand that compliance is a condition of employment and ";
        String reasonLine2 = "that violation of any policy could subject me to penalties including, but not limited to, ";
        String reasonLine3 = "loss of privileges to use Senate technologies, demotion, suspension or termination. ";


        //ensure the task is actually a document and is completed by the employee
        if (taskAssignment.isCompleted() ) {

            String orignalFileName = "";
            String newFileName = "";
            File signatureDocument = null;
            PDDocument document = null;

            if (task.getTaskType().equals(DOCUMENT_ACKNOWLEDGMENT)) {
                AckDoc ackDoc = ackDocTaskDetailDao.getTaskDetails(task);
                newFileName = pdfDir + employee.getFirstName() + "_" + employee.getLastName() + "_" + ackDoc.getFilename().replaceAll(" ", "_");
                orignalFileName = ackDocDir + ackDoc.getFilename();
                File originalDoc = new File(orignalFileName);
                signatureDocument = new File(newFileName);
                copyFile(originalDoc, signatureDocument);
                document = PDDocument.load(signatureDocument);
            }
            else {
                newFileName = pdfDir + employee.getFirstName() + "_" + employee.getLastName() + "_" + task.getTitle().replaceAll(" ", "_") + ".pdf";
                // create new file
                document = new PDDocument();
            }

            try {
                //Create Page and Open Document
                PDPage sigPage = new PDPage();
                document.addPage(sigPage);
                PDPageContentStream pdPageContentStream = new PDPageContentStream(document, sigPage);

                //Write image to PDF
                PDImageXObject pdImage = PDImageXObject.createFromFile(nysenateSeal, document);
                pdPageContentStream.drawImage(pdImage, 230, 620, pdImage.getWidth() / 4, pdImage.getHeight() / 4);

                //Write Text to PDF
                writeToPDF(pdPageContentStream, 135, 556, PDType1Font.HELVETICA, 12, reasonLine1);
                writeToPDF(pdPageContentStream, 85, 542, PDType1Font.HELVETICA, 12, reasonLine2);
                writeToPDF(pdPageContentStream, 85, 528, PDType1Font.HELVETICA, 12, reasonLine3);
                writeToPDF(pdPageContentStream, 85, 514, PDType1Font.HELVETICA, 12, " ");
                writeToPDF(pdPageContentStream, 85, 500, PDType1Font.HELVETICA_BOLD, 12,
                        "Task: " + task.getTitle());
                writeToPDF(pdPageContentStream, 85, 486, PDType1Font.HELVETICA_BOLD, 12,
                        "Employee's Signature: " + employee.getFullName().toUpperCase());
                writeToPDF(pdPageContentStream, 85, 472, PDType1Font.HELVETICA_BOLD, 12,
                        "Date Signed: " + taskAssignment.getUpdateTime());

                // Set a Color for the Rectangle
                pdPageContentStream.setStrokingColor(Color.BLACK);
                pdPageContentStream.addRect(60, 464, 500, 108);
                pdPageContentStream.stroke();

                // Once all the content is written, close the stream
                pdPageContentStream.close();

                //Create Signature
                PDSignature signature = new PDSignature();
                signature.setFilter(PDSignature.FILTER_ADOBE_PPKLITE);
                signature.setSubFilter(PDSignature.SUBFILTER_ADBE_PKCS7_DETACHED);
                signature.setName("NY State Senate");
                signature.setLocation("Albany, NY");
                signature.setReason("");
                signature.setSignDate(localDateTimeToCalendar(taskAssignment.getUpdateTime()));
                document.addSignature(signature);

                //Save and return
                document.save(newFileName);
                document.close();

                if (signatureDocument == null) {
                    signatureDocument = new File(newFileName);
                }

                return signatureDocument;
            }
            catch (Exception e) {
                logger.error("There was an error creating the signature for an employee", e);
            }
        }
        return null;
    }

    public static Calendar localDateTimeToCalendar(LocalDateTime localDateTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(localDateTime.getYear(), localDateTime.getMonthValue()-1, localDateTime.getDayOfMonth(),
                localDateTime.getHour(), localDateTime.getMinute(), localDateTime.getSecond());
        return calendar;
    }

    public static void copyFile(File from, File to) throws IOException {
        FileUtils.copyFile(from, to);
    }

    private void writeToPDF(PDPageContentStream pdPageContentStream, int x, int y, PDType1Font font, int foztSize, String text) throws IOException {
        pdPageContentStream.beginText();
        pdPageContentStream.newLineAtOffset(x, y); //600
        pdPageContentStream.setFont(font, foztSize);
        pdPageContentStream.showText(text);
        pdPageContentStream.endText();
    }
}
