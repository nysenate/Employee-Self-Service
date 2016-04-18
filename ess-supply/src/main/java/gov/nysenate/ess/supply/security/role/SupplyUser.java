package gov.nysenate.ess.supply.security.role;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.model.unit.Location;

import java.util.ArrayList;
import java.util.Collection;

public class SupplyUser implements SupplyRole {

    private Employee emp;
    private Location location;

    public SupplyUser(Employee emp, Location location) {
        this.emp = emp;
        this.location = location;
    }

    @Override
    public ImmutableCollection<String> getPermissions() {
        Collection<String> permissions = new ArrayList<>();
        // Can view their own orders
        permissions.add("supply:order:view:" + String.valueOf(emp.getEmployeeId()));
        // Can view orders for their location
        permissions.add("supply:order:view:" + location.getLocId().toString());
        return ImmutableList.copyOf(permissions);
    }
}
