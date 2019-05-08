package gov.nysenate.ess.core.client.view.pec;

import gov.nysenate.ess.core.model.pec.PersonnelTask;

/**
 * Defines a service that can create {@link PersonnelTaskView} for a {@link PersonnelTask}
 */
public interface PersonnelTaskViewFactory<T extends PersonnelTask> {

    PersonnelTaskView getView(T task);

    Class<T> getTaskClass();
}
