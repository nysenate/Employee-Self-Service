package gov.nysenate.ess.core.view;

import com.fasterxml.jackson.annotation.JsonProperty;
import gov.nysenate.ess.core.client.view.base.ViewObject;

public class CheckPermissionView implements ViewObject {

    private int employeeId;
    private String permission;
    private boolean isPermitted;

    public CheckPermissionView(int employeeId, String permission, boolean isPermitted) {
        this.employeeId = employeeId;
        this.permission = permission;
        this.isPermitted = isPermitted;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    @JsonProperty("isPermitted")
    public boolean isPermitted() {
        return isPermitted;
    }

    public void setPermitted(boolean permitted) {
        isPermitted = permitted;
    }

    @Override
    public String getViewType() {
        return "check-permission-view";
    }
}
