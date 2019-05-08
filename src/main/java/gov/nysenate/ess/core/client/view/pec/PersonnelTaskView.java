package gov.nysenate.ess.core.client.view.pec;

import gov.nysenate.ess.core.client.view.base.ViewObject;

import java.util.Optional;

public interface PersonnelTaskView extends ViewObject {

    PersonnelTaskIdView getTaskId();
    String getTitle();

    @Override
    default String getViewType() {
        String typeString = Optional.ofNullable(getTaskId())
                .map(PersonnelTaskIdView::getTaskType)
                .map(Enum::name)
                .orElse(null);
        return "personnel-task-" + typeString;
    }
}
