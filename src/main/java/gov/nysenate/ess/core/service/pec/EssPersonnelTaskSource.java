package gov.nysenate.ess.core.service.pec;


import com.google.common.collect.Maps;
import gov.nysenate.ess.core.model.pec.PersonnelTask;
import gov.nysenate.ess.core.model.pec.PersonnelTaskId;
import gov.nysenate.ess.core.model.pec.PersonnelTaskType;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class EssPersonnelTaskSource implements PersonnelTaskSource {

    private final Map<PersonnelTaskType, PersonnelTaskDomainSource<?>> taskSourceMap;

    public EssPersonnelTaskSource(List<PersonnelTaskDomainSource<?>> taskSources) {
        this.taskSourceMap = Maps.uniqueIndex(taskSources, PersonnelTaskDomainSource::getTaskType);
    }

    @Override
    public Set<PersonnelTaskId> getAllPersonnelTaskIds() {
        return taskSourceMap.values().stream()
                .map(PersonnelTaskDomainSource::getActiveTaskIds)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
    }

    @Override
    public PersonnelTask getPersonnelTask(PersonnelTaskId taskId) throws PersonnelTaskNotFoundEx {
        PersonnelTaskType taskType = taskId.getTaskType();
        if (!taskSourceMap.containsKey(taskType)) {
            throw new IllegalArgumentException("No task source exists for personnel task type: " + taskType);
        }
        return taskSourceMap.get(taskType).getPersonnelTask(taskId.getTaskNumber());
    }

    @Override
    public List<PersonnelTask> getActivePersonnelTasks() {
        return taskSourceMap.values().stream()
                .map(PersonnelTaskDomainSource::getActiveTasks)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }
}
