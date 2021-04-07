package gov.nysenate.ess.core.service.pec.external;

import com.fasterxml.jackson.databind.JsonNode;
import gov.nysenate.ess.core.model.pec.moodle.MoodleEmployeeRecord;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;

public interface ESSMoodleRecordService {

    /**
     * This method is meant to convert the steam of input from the http request into a json node
     * @param inputStream
     * @return
     * @throws IOException
     */
    public JsonNode convertStreamtoJson(InputStream inputStream) throws IOException;

    /**
     * Parses a list of MoodleEmployeeRecords from a Json Node
     * @param jsonString
     * @return
     * @throws IOException
     */
    public List<MoodleEmployeeRecord> getMoodleRecordsFromJson(String jsonString) throws IOException;

    /**
     * Creates a PersonnelTask and then sends it to the PersonnelTaskDao for db insertion
     * @param moodleEmployeeRecords
     */
    public void processMoodleEmployeeRecords(List<MoodleEmployeeRecord> moodleEmployeeRecords);

    /**
     * Contacts the Moodle ethics app for data in a certain time range
     * @param from
     * @param to
     * @param organization
     * @return
     * @throws IOException
     */
    public JsonNode contactMoodleForRecords(LocalDateTime from, LocalDateTime to, String organization) throws IOException;
}
