package gov.nysenate.ess.supply.security;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import gov.nysenate.ess.core.model.auth.SenatePerson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SupplyAuthorization {

    private static final Logger logger = LoggerFactory.getLogger(SupplyAuthorization.class);

    @Autowired private SupplyRoleDao supplyRoleDao;

    /**
     * @return All supply permissions for the given person.
     */
    public ImmutableCollection<String> getPermissions(SenatePerson person) {
        List<String> permissions = new ArrayList<>();
        supplyRoleDao.getSupplyRoles(person).forEach(r -> permissions.addAll(r.getPermissions()));
        return ImmutableList.copyOf(permissions);
    }
}
