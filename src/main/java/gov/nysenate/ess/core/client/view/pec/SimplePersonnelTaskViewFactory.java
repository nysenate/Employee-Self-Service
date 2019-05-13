package gov.nysenate.ess.core.client.view.pec;

import gov.nysenate.ess.core.model.pec.SimplePersonnelTask;
import org.springframework.stereotype.Service;

@Service
public class SimplePersonnelTaskViewFactory implements PersonnelTaskViewFactory<SimplePersonnelTask> {

    @Override
    public PersonnelTaskView getView(SimplePersonnelTask task) {
        return new SimplePersonnelTaskView(task);
    }

    @Override
    public Class<SimplePersonnelTask> getTaskClass() {
        return SimplePersonnelTask.class;
    }
}
