package gov.nysenate.ess.core.service.pec.view;

import gov.nysenate.ess.core.client.view.EmployeeView;
import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.core.service.pec.notification.EmployeeEmail;

import java.util.stream.Collectors;

public class EmployeeEmailView implements ViewObject {
    private final EmployeeView employee;
    private final String tasks;

    public EmployeeEmailView(EmployeeEmail email) {
        this.employee = new EmployeeView(email.employee());
        this.tasks = email.dataList().stream().map(data -> data.task().getTitle())
                .collect(Collectors.joining(", "));
    }

    @Override
    public String getViewType() {
        return "employee email";
    }

    public EmployeeView getEmployee() {
        return employee;
    }

    public String getTasks() {
        return tasks;
    }
}
