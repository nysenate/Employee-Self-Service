package gov.nysenate.ess.core.client.view.pec.ethicsLive;

import gov.nysenate.ess.core.client.view.pec.PersonnelTaskViewFactory;
import gov.nysenate.ess.core.model.pec.ethics.EthicsLiveCourseTask;

public class EthicsLiveCourseViewFactory implements PersonnelTaskViewFactory<EthicsLiveCourseTask> {

    @Override
    public EthicsLiveCourseTaskView getView(EthicsLiveCourseTask task) {return new EthicsLiveCourseTaskView(task);}

    @Override
    public Class<EthicsLiveCourseTask> getTaskClass() {return EthicsLiveCourseTask.class;}
}
