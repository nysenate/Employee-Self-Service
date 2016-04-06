package gov.nysenate.ess.supply.security.role;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SupplyManager implements SupplyRole {

    private int empId;
    private String location;

    public SupplyManager(int empId, String location) {
        this.empId = empId;
        this.location = location;
    }

    @Override
    public Collection<String> getPermissions() {
        List<String> permissions = new ArrayList<>();
        permissions.add("supply:shipment:approve");
        return permissions;
    }
}
