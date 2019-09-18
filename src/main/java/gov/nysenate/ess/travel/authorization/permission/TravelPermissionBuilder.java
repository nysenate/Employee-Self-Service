package gov.nysenate.ess.travel.authorization.permission;

import com.google.common.base.Preconditions;
import org.apache.shiro.authz.permission.WildcardPermission;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Collection;
import java.util.Stack;
import java.util.stream.Collectors;

/**
 * Constructs Wildcard Permissions in a standardized format using
 * {@link TravelPermissionObject} and {@link RequestMethod} as configuration.
 * <p>
 * Utilizes the builder pattern for construction of permissions.
 * <p>
 * Uses shiro multiple parts to combine multiple permissions into a single WildcardPermission.
 * <p>
 * Format: travel:{object}:{empId}:{rest method}
 * Example: travel:travel_application:221,412,2811,3211:get,post
 */
public class TravelPermissionBuilder {

    private Stack<Integer> empIds = new Stack<>();
    private boolean isForAllEmps = false;
    private Stack<TravelPermissionObject> objects = new Stack<>();
    private Stack<RequestMethod> actions = new Stack<>();

    /**
     * Adds an employeeId to this permission.
     */
    public TravelPermissionBuilder forEmpId(int empId) {
        empIds.add(empId);
        return this;
    }

    public TravelPermissionBuilder forEmpIds(Collection<Integer> empIds) {
        this.empIds.addAll(empIds);
        return this;
    }

    /**
     * Use this if you want to give permissions on all employees.
     * This will use a wildcard in pace of the empId part of the permission string.
     */
    public TravelPermissionBuilder forAllEmps() {
        isForAllEmps = true;
        return this;
    }

    /**
     * Adds a {@link TravelPermissionObject} to this permission.
     */
    public TravelPermissionBuilder forObject(TravelPermissionObject object) {
        objects.add(object);
        return this;
    }

    /**
     * Adds a {@link RequestMethod}/action to this permission.
     */
    public TravelPermissionBuilder forAction(RequestMethod action) {
        actions.add(action);
        return this;
    }

    /**
     * Constructs and returns a {@link WildcardPermission}
     * based on the values set in this class.
     */
    public WildcardPermission buildPermission() {
        Preconditions.checkArgument(isForAllEmps || !empIds.isEmpty());
        Preconditions.checkArgument(!objects.isEmpty());
        Preconditions.checkArgument(!actions.isEmpty());

        Stack<String> empIdStrings = empIds.stream().map(String::valueOf).collect(Collectors.toCollection(Stack::new));
        Stack<String> objectStrings = objects.stream().map(Enum::name).collect(Collectors.toCollection(Stack::new));
        Stack<String> actionStrings = actions.stream().map(Enum::name).collect(Collectors.toCollection(Stack::new));

        String permissionString =
                "travel"
                        + ":" + generatePart(objectStrings)
                        + ":" + (isForAllEmps ? "*" : generatePart(empIdStrings))
                        + ":" + generatePart(actionStrings);

        return new WildcardPermission(permissionString);
    }

    private String generatePart(Stack<String> parts) {
        String part = "";
        while (!parts.isEmpty()) {
            part += parts.pop() + ",";
        }
        return part;
    }
}
