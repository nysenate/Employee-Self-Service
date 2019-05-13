package gov.nysenate.ess.core.service.pec;

import gov.nysenate.ess.core.model.pec.PersonnelTaskId;
import gov.nysenate.ess.core.model.pec.PersonnelTaskType;
import gov.nysenate.ess.core.model.pec.SimplePersonnelTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

import static gov.nysenate.ess.core.model.pec.PersonnelTaskType.MOODLE_COURSE;

/**
 * Provides all active tasks related to the Moodle Ethics Training Course.
 */

@Service
public class MooldeTaskDomainSource implements PersonnelTaskDomainSource<SimplePersonnelTask> {

    private final PersonnelTaskId personnelTaskId = new PersonnelTaskId(MOODLE_COURSE, 1);

    @Autowired
    public MooldeTaskDomainSource() {}

    @Override
    public Set<PersonnelTaskId> getActiveTaskIds() {

        Set<PersonnelTaskId> taskSet = new HashSet<>();
        taskSet.add(personnelTaskId);
        return taskSet;
    }

    @Override
    public PersonnelTaskType getTaskType() {
        return PersonnelTaskType.MOODLE_COURSE;
    }

    @Override
    public SimplePersonnelTask getPersonnelTask(int taskNumber) throws PersonnelTaskNotFoundEx {
       return new SimplePersonnelTask(personnelTaskId,
               "Moodle Ethics Training Course");
    }
}
