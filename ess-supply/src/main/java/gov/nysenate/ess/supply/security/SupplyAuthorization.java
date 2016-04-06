package gov.nysenate.ess.supply.security;

import gov.nysenate.ess.core.model.auth.SenatePerson;
import gov.nysenate.ess.supply.security.role.SupplyRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SupplyAuthorization {

    @Autowired private SupplyRoleDao supplyRoleDao;

    public List<String> getPermissions(SenatePerson person) {
        List<String> permissions = new ArrayList<>();
        List<SupplyRole> roles = supplyRoleDao.getSupplyRoles(person);
        for(SupplyRole role: roles) {
            permissions.addAll(role.getPermissions());
        }
        return permissions;
    }
}
