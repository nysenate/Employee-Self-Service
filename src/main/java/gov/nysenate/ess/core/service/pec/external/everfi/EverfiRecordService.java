package gov.nysenate.ess.core.service.pec.external.everfi;

import gov.nysenate.ess.core.dao.pec.assignment.PersonnelTaskAssignmentDao;
import gov.nysenate.ess.core.dao.pec.task.PersonnelTaskDao;
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
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import static gov.nysenate.ess.core.model.pec.PersonnelTaskType.EVERFI_COURSE;

@Service
public class EverfiRecordService implements ESSEverfiRecordService {
    
    private EverfiApiClient everfiApiClient;
    private EmployeeDao employeeDao;
    private PersonnelTaskAssignmentDao personnelTaskAssignmentDao;
    private PersonnelTaskService taskService;
    private PersonnelTaskDao personnelTaskDao;
    private HashMap<Integer, Integer> everfiAssignmentIDMap;
    private HashMap<String, Integer> everfiContentIDMap;

    private static final Logger logger = LoggerFactory.getLogger(EverfiRecordService.class);

    @Value("${scheduler.everfi.sync.enabled:false}")
    private boolean everfiSyncEnabled;

    @Autowired
    public EverfiRecordService(EverfiApiClient everfiApiClient, EmployeeDao employeeDao,
                               PersonnelTaskAssignmentDao personnelTaskAssignmentDao,
                               PersonnelTaskService taskService, PersonnelTaskDao personnelTaskDao) {
        this.everfiApiClient = everfiApiClient;
        this.employeeDao = employeeDao;
        this.personnelTaskAssignmentDao = personnelTaskAssignmentDao;
        this.taskService = taskService;
        this.personnelTaskDao = personnelTaskDao;
        this.everfiAssignmentIDMap = personnelTaskDao.getEverfiAssignmentIDs();
        this.everfiContentIDMap = personnelTaskDao.getEverfiContentIDs();
    }

    public void refreshCaches() {
        this.everfiAssignmentIDMap = personnelTaskDao.getEverfiAssignmentIDs();
        this.everfiContentIDMap = personnelTaskDao.getEverfiContentIDs();
    }

    @Scheduled(cron = "${scheduler.everfi.task.sync.cron}") //At the top of every hour every day
    public void getUpdatesFromEverfi() throws IOException {
        if (!everfiSyncEnabled) {
            return;
        }
        final LocalDateTime jan1970 = LocalDateTime.of(1970, 1, 1, 0, 0, 0, 0);
        contactEverfiForUserRecords(jan1970.toString() + ":00.000");
    }

    public void contactEverfiForUserRecords(String since) throws IOException {

        EverfiAssignmentsAndProgressRequest request =
                EverfiAssignmentsAndProgressRequest.allUserAssignments(everfiApiClient, since, 1000);
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

        List<PersonnelTask> everfiPersonnelTasks = getEverfiPersonnelTasks();

        for (EverfiAssignmentAndProgress assignmentAndProgress : assignmentAndProgresses) {

            EverfiAssignmentUser user = assignmentAndProgress.getUser();

            try {
                int empID = getEmployeeId(user);
                if (empID != 99999) {
                    //assignment id from the json object
                    int assignmentID = assignmentAndProgress.getAssignment().getId();

                    //this is personnel taskid that should correspond with the everfi assignment id
                    Integer everfiTaskID = getEverfiTaskID(assignmentID);

                    //There is a max of 1 progress object in the progress array at any point

                    if (!assignmentAndProgress.getProgress().isEmpty()) {
                        EverfiAssignmentProgress progress = assignmentAndProgress.getProgress().get(0);
                        String contentID = progress.getContentId();
                        Integer potentialTaskID = everfiContentIDMap.get(contentID);

                        //Each progress has a content id which should suggest a certain task.
                        // We check here that the progress and the assignment both correspond to the same task
                        if (potentialTaskID != null && everfiTaskID != null
                                && potentialTaskID.intValue() == everfiTaskID.intValue()) {

                            LocalDateTime completedAt = null; //not completed by default
                            boolean active = true; //true by default

                            try {
                                completedAt = progress.getCompletedAt().toLocalDateTime();

                                //for loop to get task active status
                                for (PersonnelTask everfiPersonnelTask : everfiPersonnelTasks) {
                                    if (everfiPersonnelTask.getTaskId() == everfiTaskID) {
                                        active = everfiPersonnelTask.isActive();
                                    }
                                }
                            } catch (NullPointerException e) {
                                //Do nothing completedAt is already null
                            }
                            boolean completed = progress.getContentStatus().equals("completed");

                            PersonnelTaskAssignment taskToInsert = new PersonnelTaskAssignment(
                                    everfiTaskID,
                                    empID,
                                    empID,
                                    completedAt,
                                    completed,
                                    active
                            );
                            personnelTaskAssignmentDao.updateAssignment(taskToInsert);
                        }
                    }
                }
            } catch (EmployeeNotFoundEx e) {
                logger.error("Could not match employee " + e.getMessage());
            }
        }
    }

    private int getEmployeeId(EverfiAssignmentUser everfiAssignmentUser) throws EmployeeNotFoundEx {
        int empid = 99999;

        if (everfiAssignmentUser.employeeId != null && !everfiAssignmentUser.employeeId.isEmpty()) {
            try {
                empid = employeeDao.getEmployeeById(Integer.parseInt(everfiAssignmentUser.employeeId)).getEmployeeId();
            } catch (Exception e) {
                logger.error("Problem with Everfi EMP ID : " + e.getMessage());
            }
        } else if (everfiAssignmentUser.email != null && !everfiAssignmentUser.email.isEmpty()) {

            try {
                empid = employeeDao.getEmployeeByEmail(everfiAssignmentUser.email).getEmployeeId();
            } catch (Exception e) {
                logger.error("Problem with Everfi email : " + e.getMessage());
            }
        } else {
            throw new EmployeeNotFoundEx("Everfi user record cannot be matched" + everfiAssignmentUser.toString());
        }
        return empid;
    }

    //TODO TEST
    //TODO API integration for caches

    private Integer getEverfiTaskID(Integer assignmentID) {
        return everfiAssignmentIDMap.get(assignmentID);
    }

    private List<PersonnelTask> getEverfiPersonnelTasks() {
        return taskService.getPersonnelTasks(false).stream()
                .filter(task -> EVERFI_COURSE == task.getTaskType())
                .collect(Collectors.toList());
    }
}
