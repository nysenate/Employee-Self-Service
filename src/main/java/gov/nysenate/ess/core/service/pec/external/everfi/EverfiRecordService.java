package gov.nysenate.ess.core.service.pec.external.everfi;

import com.fasterxml.jackson.databind.JsonNode;
import gov.nysenate.ess.core.controller.api.EverfiApiCtrl;
import gov.nysenate.ess.core.dao.pec.assignment.PersonnelTaskAssignmentDao;
import gov.nysenate.ess.core.dao.personnel.EmployeeDao;
import gov.nysenate.ess.core.model.pec.PersonnelTask;
import gov.nysenate.ess.core.model.pec.PersonnelTaskAssignment;
import gov.nysenate.ess.core.model.personnel.EmployeeNotFoundEx;
import gov.nysenate.ess.core.service.pec.external.everfi.assignment.EverfiAssignmentAndProgress;
import gov.nysenate.ess.core.service.pec.external.everfi.assignment.EverfiAssignmentProgress;
import gov.nysenate.ess.core.service.pec.external.everfi.assignment.EverfiAssignmentUser;
import gov.nysenate.ess.core.service.pec.external.everfi.assignment.EverfiAssignmentsAndProgressRequest;
import gov.nysenate.ess.core.service.pec.task.PersonnelTaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static gov.nysenate.ess.core.model.pec.PersonnelTaskType.EVERFI_COURSE;

@Service
public class EverfiRecordService implements ESSEverfiRecordService {
    //TODO must be eventually updated to handle multiple tasks. It only supports Sexual Harrassment 2020 training for now

    final static int sexualHarrassment2020EverfiCourseID = 42954;
    final static String sexualHarrassment2020EverfiContentID = "9cd9c7d9-8f20-4e8a-8509-da07fc2ae3a2";
    private EverfiApiClient everfiApiClient;
    private EmployeeDao employeeDao;
    private PersonnelTaskAssignmentDao personnelTaskAssignmentDao;
    private PersonnelTaskService taskService;

    private static final Logger logger = LoggerFactory.getLogger(EverfiRecordService.class);

    @Value("${scheduler.everfi.sync.enabled:false}") private boolean everfiSyncEnabled;

    @Autowired
    public EverfiRecordService(EverfiApiClient everfiApiClient, EmployeeDao employeeDao,
                               PersonnelTaskAssignmentDao personnelTaskAssignmentDao,
                               PersonnelTaskService taskService) {
        this.everfiApiClient = everfiApiClient;
        this.employeeDao = employeeDao;
        this.personnelTaskAssignmentDao = personnelTaskAssignmentDao;
        this.taskService = taskService;
    }

    @Scheduled(cron="${scheduler.everfi.task.sync.cron}") //At the top of every hour every day
    public void getUpdatesFromEverfi() throws IOException {
        if (!everfiSyncEnabled) {
            return;
        }
        final LocalDateTime jan1970 = LocalDateTime.of(1970,1,1,0,0,0,0);
        contactEverfiForUserRecords(jan1970.toString() + ":00.000");
    }

    public void contactEverfiForUserRecords(String since) throws IOException {

        EverfiAssignmentsAndProgressRequest request =
                EverfiAssignmentsAndProgressRequest.allUserAssignments(everfiApiClient, since, 100);
        List<EverfiAssignmentAndProgress> assignmentsAndProgress;
        logger.info("Contacting Everfi for records");
        while (request != null) {
            // Call method on the request and get back the model object created from Everfi json.
            // Authentication and deserialization is handled by the request object.
            assignmentsAndProgress = request.fetch();
            handleRecords(assignmentsAndProgress);
            // Move on to the next 'page' of results.
            request = request.next();
        }
        logger.info("Handled Everfi records");
    }

    private void handleRecords(List<EverfiAssignmentAndProgress> assignmentAndProgresses) {
        int everfiTaskID = getEverfiTaskID();

        for (EverfiAssignmentAndProgress assignmentAndProgress : assignmentAndProgresses) {

            EverfiAssignmentUser user = assignmentAndProgress.getUser();

            try {
                int empID = getEmployeeId(user);
                if (empID != 99999) {
                    int assignmentID = assignmentAndProgress.getAssignment().getId();
                    EverfiAssignmentProgress progress = getCorrectProgress(assignmentAndProgress.getProgress());
                    LocalDateTime completedAt = null;
                    if (progress != null) {
                        try {
                            completedAt = progress.getCompletedAt().toLocalDateTime();
                        }
                        catch (NullPointerException e) {
                            //Do nothing completedAt is already null
                        }
                        boolean completed = progress.getContentStatus().equals("completed");

                        if (assignmentID == sexualHarrassment2020EverfiCourseID ) {
                            PersonnelTaskAssignment taskToInsert = new PersonnelTaskAssignment(
                                    everfiTaskID,
                                    empID,
                                    empID,
                                    completedAt,
                                    completed,
                                    true
                            );
                            personnelTaskAssignmentDao.updateAssignment(taskToInsert);
                        }
                    }
                }
            }
            catch (EmployeeNotFoundEx e) {
                logger.error("Could not match employee " + e.getMessage());
            }
        }
    }

    private int getEmployeeId(EverfiAssignmentUser everfiAssignmentUser) throws EmployeeNotFoundEx {
        int empid = 99999;

        if (everfiAssignmentUser.employeeId != null && !everfiAssignmentUser.employeeId.isEmpty() ) {
            try {
                empid = employeeDao.getEmployeeById(Integer.parseInt( everfiAssignmentUser.employeeId )).getEmployeeId();
            }
            catch (Exception e) {
                logger.error("Problem with Everfi EMP ID : " + e.getMessage());
            }
        }
        else if (everfiAssignmentUser.email != null && !everfiAssignmentUser.email.isEmpty() ) {

            try {
                empid = employeeDao.getEmployeeByEmail(everfiAssignmentUser.email ).getEmployeeId();
            }
            catch (Exception e) {
                logger.error("Problem with Everfi email : " + e.getMessage());
            }
        }
        else {
            throw new EmployeeNotFoundEx("Everfi user record cannot be matched" + everfiAssignmentUser.toString());
        }
        return empid;
    }

    private EverfiAssignmentProgress getCorrectProgress(List<EverfiAssignmentProgress> progresses) {
        EverfiAssignmentProgress correctProgress = null;
        for (EverfiAssignmentProgress progress: progresses) {
            if (progress.getContentId().equals(sexualHarrassment2020EverfiContentID)
                    && progress.getDueOn().equals(LocalDate.of(2020,10,1))) {
                correctProgress = progress;
            }
        }
        return correctProgress;
    }

    private int getEverfiTaskID() {
        List<PersonnelTask> everfiTasks = taskService.getPersonnelTasks(true).stream()
                .filter(task -> EVERFI_COURSE == task.getTaskType())
                .collect(Collectors.toList());
        if (everfiTasks.size() != 1) {
            throw new IllegalStateException("Expected a single moodle course in the db, found " +
                    everfiTasks.size() + ": " + everfiTasks);
        }
        return everfiTasks.get(0).getTaskId();
    }
}
