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
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static gov.nysenate.ess.core.model.pec.PersonnelTaskType.MOODLE_COURSE;

@Service
public class MoodleRecordService implements ESSMoodleRecordService {

    //process moodle records into tasks for storage in the db

    private ObjectMapper objectMapper = new ObjectMapper();
    private EmployeeDao employeeDao;
    private PersonnelAssignedTaskDao personnelAssignedTaskDao;

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
            Employee employee = employeeDao.getEmployeeByEmail(moodleEmployeeRecord.getEmail());

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

    public JsonNode contactMoodleForNewRecords(LocalDateTime from, LocalDateTime to, String organization) throws IOException {
        final String USER_AGENT = "Mozilla/5.0";
        String moodleUrl
                = "http://localhost:8080/path/to/moodle"; //TODO key and url in app props?

        HttpClient client = HttpClientBuilder.create().build();
        HttpPost post = new HttpPost(moodleUrl);
        post.setHeader("User-Agent", USER_AGENT);
        List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
        urlParameters.add(new BasicNameValuePair("from", from.toString()));
        urlParameters.add(new BasicNameValuePair("to", to.toString()));
        urlParameters.add(new BasicNameValuePair("organization", organization));

        post.setEntity(new UrlEncodedFormEntity(urlParameters));
        HttpResponse response = client.execute(post);

        return objectMapper.readTree(response.getEntity().getContent());
    }

}
