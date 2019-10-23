package gov.nysenate.ess.core.client.view.pec;

import gov.nysenate.ess.core.model.pec.MoodleCourseTask;
import org.springframework.stereotype.Service;

@Service
public class MoodleCourseTaskViewFactory implements PersonnelTaskViewFactory<MoodleCourseTask> {

    @Override
    public MoodleTaskView getView(MoodleCourseTask task) {
        return new MoodleTaskView(task);
    }

    @Override
    public Class<MoodleCourseTask> getTaskClass() {
        return MoodleCourseTask.class;
    }
}
