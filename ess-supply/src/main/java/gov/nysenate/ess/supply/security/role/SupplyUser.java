package gov.nysenate.ess.supply.security.role;

import java.util.ArrayList;
import java.util.Collection;

public class SupplyUser implements SupplyRole {

    private int empId;
    private String location;

    public SupplyUser(int empId, String location) {
        this.empId = empId;
        this.location = location;
    }

    @Override
    public Collection<String> getPermissions() {
        Collection<String> permissions = new ArrayList<>();
        // Can view their own orders
        permissions.add("supply:order:view:" + String.valueOf(empId));
        // Can view orders for their location
        permissions.add("supply:order:view:" + location);
        return permissions;
    }
}
