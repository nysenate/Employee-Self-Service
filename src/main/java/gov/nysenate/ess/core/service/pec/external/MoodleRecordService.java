package gov.nysenate.ess.core.service.pec.external;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.nysenate.ess.core.dao.pec.assignment.PersonnelTaskAssignmentDao;
import gov.nysenate.ess.core.dao.personnel.EmployeeDao;
import gov.nysenate.ess.core.model.pec.MoodleEmployeeRecord;
import gov.nysenate.ess.core.model.pec.PersonnelTask;
import gov.nysenate.ess.core.model.pec.PersonnelTaskAssignment;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.model.personnel.EmployeeException;
import gov.nysenate.ess.core.service.pec.task.PersonnelTaskService;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static gov.nysenate.ess.core.model.pec.PersonnelTaskType.MOODLE_COURSE;

@Service
public class MoodleRecordService implements ESSMoodleRecordService {

    //process moodle records into tasks for storage in the db
    private static final Logger logger = LoggerFactory.getLogger(MoodleRecordService.class);

    private ObjectMapper objectMapper = new ObjectMapper();
    private EmployeeDao employeeDao;
    private PersonnelTaskAssignmentDao personnelTaskAssignmentDao;
    private PersonnelTaskService taskService;

    @Value("${legethics.url:}") private String moodleUrl;
    @Value("${legethics.api.path:}") private String moodleApiPath;
    @Value("${legethics.key:}") private String moodleKey;
    @Value("${scheduler.moodle.sync.enabled:false}") private boolean moodleSyncEnabled;

    @Autowired
    public MoodleRecordService(EmployeeDao employeeDao,
                               PersonnelTaskService taskService,
                               PersonnelTaskAssignmentDao personnelTaskAssignmentDao) {
        this.employeeDao = employeeDao;
        this.taskService = taskService;
        this.personnelTaskAssignmentDao = personnelTaskAssignmentDao;
    }

    //input stream to json
    public JsonNode convertStreamtoJson(InputStream inputStream) throws IOException {
        return objectMapper.readTree(inputStream);
    }

    //get record list
    public List<MoodleEmployeeRecord> getMoodleRecordsFromJson(String jsonString) throws IOException {
        return  objectMapper.readValue(jsonString, new TypeReference<List<MoodleEmployeeRecord>>(){});
    }

    //process records into tasks for completion
    public void processMoodleEmployeeRecords(List<MoodleEmployeeRecord> moodleEmployeeRecords) {

        int moodleTaskId = getMoodleTaskId();

        for (MoodleEmployeeRecord moodleEmployeeRecord : moodleEmployeeRecords) {
            Employee employee;
            String filteredEmail = filterMoodleEmail( moodleEmployeeRecord.getEmail() );

            try {
                logger.debug("Attempting to find a match with: " + filteredEmail);
                employee = employeeDao.getEmployeeByEmail(filteredEmail);
            }
            catch (EmployeeException e) {
                logger.warn("No Employee exists with email: " + filteredEmail);
                continue;
            }

            PersonnelTaskAssignment taskToInsert = new PersonnelTaskAssignment(
                    employee.getEmployeeId(),
                    moodleTaskId,
                    employee.getEmployeeId(),
                    moodleEmployeeRecord.getCompletedTime(),
                    moodleEmployeeRecord.didEmployeeCompleteCourse(),
                    true
                    );
            personnelTaskAssignmentDao.updateAssignment(taskToInsert);
        }
    }

    private String filterMoodleEmail(String email) {
        String[] emailParts = email.toLowerCase().split("@");
        String firstHalf = emailParts[0];
        String secondHalf = emailParts[1];
        firstHalf = firstHalf.replaceAll(".nysenate","").replaceAll(".senate","");
        return firstHalf + "@" + secondHalf;
    }

    public JsonNode contactMoodleForRecords(LocalDateTime from, LocalDateTime to, String organization) throws IOException {
        ZoneId zoneId = ZoneId.systemDefault();
        final String USER_AGENT = "Mozilla/5.0";
        HttpClient client = HttpClientBuilder.create().build();
        HttpPost post = new HttpPost(moodleUrl + moodleApiPath);
        post.setHeader("User-Agent", USER_AGENT);
        List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
        urlParameters.add(new BasicNameValuePair("from_date", Long.toString(from.atZone(zoneId).toEpochSecond())));
        urlParameters.add(new BasicNameValuePair("to_date", Long.toString(to.atZone(zoneId).toEpochSecond())));
        urlParameters.add(new BasicNameValuePair("org", organization));
        urlParameters.add(new BasicNameValuePair("moodlewsrestformat", "json"));
        urlParameters.add(new BasicNameValuePair("token", moodleKey));
        post.setEntity(new UrlEncodedFormEntity(urlParameters));
        HttpResponse response = client.execute(post);

        return objectMapper.readTree(response.getEntity().getContent());
    }

    @Scheduled(fixedDelay = 50000, initialDelay = 20000) //Ensure 5 secs from the finish of the previous task before querying again
    public void getUpdatesFromMoodle() throws IOException {
        if (!moodleSyncEnabled) {
            return;
        }
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime pastTime = now.minusMinutes(5);
        JsonNode json = contactMoodleForRecords( pastTime, now, "Senate");
        processMoodleEmployeeRecords(getMoodleRecordsFromJson(json.toString()));
    }

    /**
     * Gets the id for the moodle course.
     * Assumes that there is just one course in the db.
     */
    private int getMoodleTaskId() {
        List<PersonnelTask> moodleTasks = taskService.getPersonnelTasks(true).stream()
                .filter(task -> MOODLE_COURSE == task.getTaskType())
                .collect(Collectors.toList());
        if (moodleTasks.size() != 1) {
            throw new IllegalStateException("Expected a single moodle course in the db, found " +
                    moodleTasks.size() + ": " + moodleTasks);
        }
        return moodleTasks.get(0).getTaskId();
    }

}
