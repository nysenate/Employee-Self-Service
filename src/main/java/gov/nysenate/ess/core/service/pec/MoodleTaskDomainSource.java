package gov.nysenate.ess.core.service.pec;

import gov.nysenate.ess.core.model.pec.ExternalPersonnelTask;
import gov.nysenate.ess.core.model.pec.PersonnelTaskId;
import gov.nysenate.ess.core.model.pec.PersonnelTaskType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import static gov.nysenate.ess.core.model.pec.PersonnelTaskType.MOODLE_COURSE;

/**
 * Provides all active tasks related to the Moodle Ethics Training Course.
 *
 * Currently, only the leg ethics course is in Moodle.
 */
@Service
public class MoodleTaskDomainSource implements PersonnelTaskDomainSource<ExternalPersonnelTask> {

    private static final PersonnelTaskId legEthicsTaskId = new PersonnelTaskId(MOODLE_COURSE, 1);
    private static final String legEthicsTitle = "LegEthics Training Course";

    private final URL legEthicsCourseUrl;

    @Autowired
    public MoodleTaskDomainSource(@Value("${legethics.url:}") String legEthicsUrl) {
        try {
            legEthicsCourseUrl = new URL(legEthicsUrl);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException(
                    "Invalid LegEthics url passed in via properties: \"" + legEthicsUrl + "\"", e);
        }
    }

    @Override
    public Set<PersonnelTaskId> getActiveTaskIds() {
        // Just return the leg ethics course, we don't have any other moodle tasks atm.
        Set<PersonnelTaskId> taskSet = new HashSet<>();
        taskSet.add(legEthicsTaskId);
        return taskSet;
    }

    @Override
    public PersonnelTaskType getTaskType() {
        return PersonnelTaskType.MOODLE_COURSE;
    }

    @Override
    public ExternalPersonnelTask getPersonnelTask(int taskNumber) throws PersonnelTaskNotFoundEx {
       return new ExternalPersonnelTask(legEthicsTaskId, legEthicsTitle, legEthicsCourseUrl);
    }
}
