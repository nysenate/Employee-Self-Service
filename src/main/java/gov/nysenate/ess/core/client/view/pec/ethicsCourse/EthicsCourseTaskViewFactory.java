package gov.nysenate.ess.core.client.view.pec.ethicsCourse;

import gov.nysenate.ess.core.client.view.pec.PersonnelTaskViewFactory;
import gov.nysenate.ess.core.model.pec.ethics.EthicsCourseTask;
import org.springframework.stereotype.Service;

@Service
public class EthicsCourseTaskViewFactory implements PersonnelTaskViewFactory<EthicsCourseTask> {

    @Override
    public EthicsCourseTaskView getView(EthicsCourseTask task) {
        return new EthicsCourseTaskView(task);
    }

    @Override
    public Class<EthicsCourseTask> getTaskClass() {
        return EthicsCourseTask.class;
    }
}