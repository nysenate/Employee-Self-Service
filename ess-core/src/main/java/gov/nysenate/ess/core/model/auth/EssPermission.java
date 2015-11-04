package gov.nysenate.ess.core.model.auth;

import org.apache.shiro.authz.Permission;

public class EssPermission implements Permission {

    @Override
    public boolean implies(Permission p) {
        return false;
    }
}
