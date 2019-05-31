package gov.nysenate.ess.core.service.pec;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.nysenate.ess.core.dao.pec.PersonnelAssignedTaskDao;
import gov.nysenate.ess.core.dao.personnel.EmployeeDao;
import gov.nysenate.ess.core.model.pec.MoodleEmployeeRecord;
import gov.nysenate.ess.core.model.pec.PersonnelAssignedTask;
import gov.nysenate.ess.core.model.pec.PersonnelTaskId;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.model.personnel.EmployeeException;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import static gov.nysenate.ess.core.model.pec.PersonnelTaskType.MOODLE_COURSE;

@Service
public class MoodleRecordService implements ESSMoodleRecordService {

    //process moodle records into tasks for storage in the db
    private static final Logger logger = LoggerFactory.getLogger(MoodleRecordService.class);

    private ObjectMapper objectMapper = new ObjectMapper();
    private EmployeeDao employeeDao;
    private PersonnelAssignedTaskDao personnelAssignedTaskDao;

    @Value("${legethics.url:}") String moodleUrl;
    @Value("${legethics.key:}") String moodleKey;

    @Autowired
    public MoodleRecordService(EmployeeDao employeeDao, PersonnelAssignedTaskDao personnelAssignedTaskDao) {
        this.employeeDao = employeeDao;
        this.personnelAssignedTaskDao = personnelAssignedTaskDao;
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

        for (MoodleEmployeeRecord moodleEmployeeRecord : moodleEmployeeRecords) {
            Employee employee;
            try {
                employee = employeeDao.getEmployeeByEmail(moodleEmployeeRecord.getEmail());
            }
            catch (EmployeeException e) {
                logger.warn("No Employee exists with email: " + moodleEmployeeRecord.getEmail());
                continue;
            }

            PersonnelAssignedTask taskToInsert = new PersonnelAssignedTask(
                    employee.getEmployeeId(),
                    new PersonnelTaskId(MOODLE_COURSE,1), //Only 1 task in moodle
                    moodleEmployeeRecord.getCompletedTime(),
                    employee.getEmployeeId(),
                    moodleEmployeeRecord.didEmployeeCompleteCourse()
                    );
            personnelAssignedTaskDao.updatePersonnelAssignedTask(taskToInsert);
        }
    }

    public JsonNode contactMoodleForRecords(LocalDateTime from, LocalDateTime to, String organization) throws IOException {
        ZoneId zoneId = ZoneId.systemDefault();
        final String USER_AGENT = "Mozilla/5.0";
        HttpClient client = HttpClientBuilder.create().build();
        HttpPost post = new HttpPost(moodleUrl);
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
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime pastTime = now.minusMinutes(5);
        JsonNode json = contactMoodleForRecords( pastTime, now, "Senate");
        processMoodleEmployeeRecords(getMoodleRecordsFromJson(json.toString()));
    }

}
