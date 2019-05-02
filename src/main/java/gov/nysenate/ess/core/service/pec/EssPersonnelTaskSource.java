package gov.nysenate.ess.core.service.pec;


import gov.nysenate.ess.core.model.pec.PersonnelTaskId;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class EssPersonnelTaskSource implements PersonnelTaskSource {

    private final List<PersonnelTaskDomainSource> taskSources;

    public EssPersonnelTaskSource(List<PersonnelTaskDomainSource> taskSources) {
        this.taskSources = taskSources;
    }

    @Override
    public Set<PersonnelTaskId> getAllPersonnelTaskIds() {
        return taskSources.stream()
                .map(PersonnelTaskDomainSource::getActiveTasks)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
    }
}
