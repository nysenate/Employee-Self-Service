package gov.nysenate.ess.core.client.view.pec;

import gov.nysenate.ess.core.model.pec.everfi.EverfiCourseTask;
import org.springframework.stereotype.Service;

@Service
public class EverfiCourseTaskViewFactory implements PersonnelTaskViewFactory<EverfiCourseTask> {

    @Override
    public EverfiTaskView getView(EverfiCourseTask task) {
        return new EverfiTaskView(task);
    }

    @Override
    public Class<EverfiCourseTask> getTaskClass() {
        return EverfiCourseTask.class;
    }
}