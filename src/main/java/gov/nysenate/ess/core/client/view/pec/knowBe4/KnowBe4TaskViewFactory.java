package gov.nysenate.ess.core.client.view.pec.knowBe4;

import gov.nysenate.ess.core.client.view.pec.PersonnelTaskViewFactory;
import gov.nysenate.ess.core.model.pec.knowbe4.KnowBe4CourseTask;

public class KnowBe4TaskViewFactory implements PersonnelTaskViewFactory<KnowBe4CourseTask> {

    @Override
    public KnowBe4TaskView getView(KnowBe4CourseTask task) {
        return new KnowBe4TaskView(task);
    }

    @Override
    public Class<KnowBe4CourseTask> getTaskClass() { return KnowBe4CourseTask.class; }

}
