package gov.nysenate.ess.web.config;

import org.apache.shiro.realm.Realm;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * A service that wires all initialized realms into the application's security manager
 */
@Service
public class AuthRealmConfigurer {

    protected List<Realm> realmList;
    protected DefaultWebSecurityManager securityManager;

    @Autowired
    public AuthRealmConfigurer(List<Realm> realmList, DefaultWebSecurityManager securityManager) {
        this.realmList = realmList;
        this.securityManager = securityManager;
    }

    @PostConstruct
    public void setUp() {
        securityManager.setRealms(realmList);
    }
}
