package gov.nysenate.ess.core.client.view.pec;

import gov.nysenate.ess.core.model.pec.ExternalPersonnelTask;
import org.springframework.stereotype.Service;

@Service
public class ExternalPersonnelTaskViewFactory implements PersonnelTaskViewFactory<ExternalPersonnelTask> {
    @Override
    public PersonnelTaskView getView(ExternalPersonnelTask task) {
        return new ExternalPersonnelTaskView(task);
    }

    @Override
    public Class<ExternalPersonnelTask> getTaskClass() {
        return ExternalPersonnelTask.class;
    }
}
