package gov.nysenate.ess.core.service.pec.external.knowbe4;

import gov.nysenate.ess.core.dao.pec.assignment.PersonnelTaskAssignmentDao;
import gov.nysenate.ess.core.dao.pec.assignment.PersonnelTaskAssignmentNotFoundEx;
import gov.nysenate.ess.core.dao.pec.task.PersonnelTaskDao;
import gov.nysenate.ess.core.dao.personnel.EmployeeDao;
import gov.nysenate.ess.core.model.pec.PersonnelTask;
import gov.nysenate.ess.core.model.pec.PersonnelTaskAssignment;
import gov.nysenate.ess.core.model.pec.knowbe4.KnowBe4AssignmentID;
import gov.nysenate.ess.core.service.pec.external.knowbe4.assignment.KnowBe4AssignmentAndProgress;
import gov.nysenate.ess.core.service.pec.external.knowbe4.assignment.KnowBe4AssignmentAndProgressResponse;
import gov.nysenate.ess.core.service.pec.external.knowbe4.assignment.KnowBe4AssignmentUser;
import gov.nysenate.ess.core.service.pec.external.knowbe4.assignment.KnowBe4GetAllTrainingEnrollmentsRequest;
import gov.nysenate.ess.core.service.pec.task.PersonnelTaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class KnowBe4RecordService {

    private KnowBe4ApiClient apiClient;
    private EmployeeDao employeeDao;
    private PersonnelTaskAssignmentDao personnelTaskAssignmentDao;
    private PersonnelTaskService personnelTaskService;
    private PersonnelTaskDao personnelTaskDao;

    private List<KnowBe4AssignmentID> knowBe4AssignmentIDList;

    private static final Logger logger = LoggerFactory.getLogger(KnowBe4RecordService.class);

    @Value("${scheduler.knowbe4.sync.enabled:false}")
    private boolean knowBe4SyncEnabled;

    @Autowired
    public KnowBe4RecordService(KnowBe4ApiClient apiClient, EmployeeDao employeeDao,
                                PersonnelTaskAssignmentDao personnelTaskAssignmentDao,
                                PersonnelTaskService personnelTaskService,
                                PersonnelTaskDao personnelTaskDao) {
        this.apiClient = apiClient;
        this.employeeDao = employeeDao;
        this.personnelTaskAssignmentDao = personnelTaskAssignmentDao;
        this.personnelTaskService = personnelTaskService;
        this.personnelTaskDao = personnelTaskDao;
        refreshCaches();
    }

    public void refreshCaches() {
        this.knowBe4AssignmentIDList = personnelTaskDao.getKnowBe4AssignmentIDs();
    }

    @Scheduled(cron = "${scheduler.knowbe4.task.sync.cron}") //At the top of every hour every day
    public void getUpdatesFromKnowBe4() throws IOException {
        if (knowBe4SyncEnabled) {
            refreshCaches();
            contactKnowBe4ForRecords();
        }
    }

    public void contactKnowBe4ForRecords() throws IOException {
        logger.info("Contacting KnowBe4 for assignment records");

        for (KnowBe4AssignmentID knowBe4AssignmentID : this.knowBe4AssignmentIDList ) {
            PersonnelTask task = personnelTaskDao.getPersonnelTask(knowBe4AssignmentID.getTaskID());

                KnowBe4GetAllTrainingEnrollmentsRequest knowBe4RecordsRequest =
                        new KnowBe4GetAllTrainingEnrollmentsRequest(knowBe4AssignmentID.getID(), apiClient, "true");

                KnowBe4AssignmentAndProgressResponse response = knowBe4RecordsRequest.fetch();

                List<KnowBe4AssignmentAndProgress> assignmentAndProgressList = response.getAssignmentsAndProgress();

                while (assignmentAndProgressList != null) {

                    logger.info("Currently Processing KB4 Task " + task.getTaskId() + "-" + knowBe4AssignmentID.getID() + " response Page: " + response.getCusorValue());

                    if (assignmentAndProgressList.isEmpty()) {
                        logger.error("No KnowBe4 assignment records found for: {}", knowBe4AssignmentID );
                        continue;
                    }

                    processRecords(assignmentAndProgressList);

                    if (response.getNextCursor() != null) {
                        knowBe4RecordsRequest = knowBe4RecordsRequest.next(response);

                        response = knowBe4RecordsRequest.fetch();

                        if (response != null) {
                            assignmentAndProgressList = response.getAssignmentsAndProgress();
                        }
                        else {
                            assignmentAndProgressList = null;
                        }
                    }
                    else {
                        assignmentAndProgressList = null;
                    }

                }
        }
        logger.info("Finished Processing KnowBe4 assignment records");
    }

    private void processRecords(List<KnowBe4AssignmentAndProgress> assignmentAndProgressList) {
        for (KnowBe4AssignmentAndProgress assignmentAndProgress : assignmentAndProgressList) {
            int knowBe4UserEmpId = resolveEmployee( assignmentAndProgress.getUser() );
            if ( knowBe4UserEmpId != -1 ) {

                Integer knowBe4TaskID = getKnowbe4TaskID(assignmentAndProgress.getCampaign_id());
                boolean completed = false;

                if (assignmentAndProgress.getStatus().equalsIgnoreCase("passed")) {
                    completed = true;
                }

                LocalDateTime completedAt = assignmentAndProgress.getCompletionDate();

                try {
                    PersonnelTaskAssignment currentTaskAssignment =
                            personnelTaskAssignmentDao.getTaskForEmp(knowBe4UserEmpId,knowBe4TaskID);

                    if ( currentTaskAssignment.isCompleted() || currentTaskAssignment.wasManuallyOverridden()) {
                        continue;
                    }
                    else if ( currentTaskAssignment.getUpdateEmpId() != null &&
                            currentTaskAssignment.getEmpId() != currentTaskAssignment.getUpdateEmpId() ) {
                        continue;
                    }
                }
                catch (PersonnelTaskAssignmentNotFoundEx ex) {
                    //This means they dont have a task to insert so we dont need to do anything
                }

                PersonnelTaskAssignment taskToInsert = new PersonnelTaskAssignment(
                        knowBe4TaskID,
                        knowBe4UserEmpId,
                        knowBe4UserEmpId,
                        completedAt,
                        completed,
                        true,
                        LocalDateTime.now(),
                        null
                );
                personnelTaskAssignmentDao.updateAssignment(taskToInsert);


            }
            else {
                logger.warn("Error importing KB4 Records! Could not find employee with email: " + assignmentAndProgress.getUser().getEmail());
            }
        }
    }

    private Integer getKnowbe4TaskID(Integer assignmentID) {

        for (KnowBe4AssignmentID assignment : this.knowBe4AssignmentIDList) {
            if (assignment.getID() == assignmentID) {
                return assignment.getTaskID();
            }
        }
        return null;

    }

    private int resolveEmployee(KnowBe4AssignmentUser assignmentUser) {
        int empid = -1;

        try {
            empid = employeeDao.getEmployeeById(Integer.parseInt(assignmentUser.getEmployee_number())).getEmployeeId();
            return empid;
        } catch (Exception e) {
            logger.debug("Problem with KnowBe4 empid : " + e.getMessage());
        }

        try {
            empid = employeeDao.getEmployeeByEmail(assignmentUser.getEmail()).getEmployeeId();
        } catch (Exception e) {
            logger.debug("Problem with KnowBe4 email : " + e.getMessage());
        }
        return empid;
    }

}
