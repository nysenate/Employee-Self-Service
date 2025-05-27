package gov.nysenate.ess.core.service.security.authentication;

import gov.nysenate.ess.core.dao.personnel.EmployeeDao;
import gov.nysenate.ess.core.dao.security.authentication.LdapAuthDao;
import gov.nysenate.ess.core.model.auth.LdapAuthResult;
import gov.nysenate.ess.core.model.auth.LdapAuthStatus;
import gov.nysenate.ess.core.model.auth.SenateLdapPerson;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.model.personnel.EmployeeException;
import gov.nysenate.ess.web.security.exception.LdapMismatchException;
import gov.nysenate.ess.web.security.exception.NameNotFoundException;
import org.apache.shiro.authc.AccountException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.ldap.AuthenticationException;
import org.springframework.ldap.CommunicationException;
import org.springframework.stereotype.Service;

import javax.naming.ldap.LdapName;

/**
 * LdapAuthService implementation that serves as the point of access for any service that requires
 * LDAP querying/authentication against the Senate LDAP server.
 */
@Service
public class EssLdapAuthService implements LdapAuthService
{
    private static final Logger logger = LoggerFactory.getLogger(EssLdapAuthService.class);

    private LdapAuthDao ldapAuthDao;

    @Autowired
    public EssLdapAuthService(LdapAuthDao ldapAuthDao) {
        this.ldapAuthDao = ldapAuthDao;
    }

    /** {@inheritDoc} */
    @Override
    public LdapAuthResult authenticateUserByUid(String uid, String credentials) {
        /** Quick validation check */
        if (uid == null || uid.isEmpty()) {
            return new LdapAuthResult(LdapAuthStatus.EMPTY_USERNAME, "");
        }
        if (credentials == null || credentials.isEmpty()) {
            return new LdapAuthResult(LdapAuthStatus.EMPTY_CREDENTIALS, uid);
        }
        logger.trace("Trying to authenticate user {} with password {}", uid, credentials);
        LdapAuthStatus authStatus = LdapAuthStatus.UNKNOWN_EXCEPTION;
        try {
            LdapName name = ldapAuthDao.authenticateByUid(uid, credentials);
            if (name != null) {
                SenateLdapPerson person = ldapAuthDao.getPerson(name);
                return new LdapAuthResult(LdapAuthStatus.AUTHENTICATED, uid, name, person);
            }
        }
        catch(CommunicationException ex) {
            logger.error("Error connecting to LDAP server! {}", ex.getMessage());
            authStatus = LdapAuthStatus.CONNECTION_ERROR;
        }
        catch(AuthenticationException ex) {
            logger.info("Invalid credentials supplied when trying to authenticate {}", uid);
            authStatus = LdapAuthStatus.INCORRECT_CREDENTIALS;
        }
        catch(EmptyResultDataAccessException ex) {
            logger.info("No match found when trying to authenticate {}", uid);
            authStatus = LdapAuthStatus.NAME_NOT_FOUND_EXCEPTION;
        }
        catch(IncorrectResultSizeDataAccessException ex) {
            logger.error("Result size exception thrown when trying to authenticate {}", uid);
            authStatus = LdapAuthStatus.MULTIPLE_MATCH_EXCEPTION;
        }
        catch(Exception ex) {
            logger.error("Unhandled exception thrown when trying to authenticate {}: {}", uid, ex.getMessage());
        }
        return new LdapAuthResult(authStatus, uid);
    }

    /** {@inheritDoc} */
    public LdapAuthResult authenticateUserByUidWithoutCreds(String uid, String suppliedPass, String masterPass) {
        LdapAuthResult ldapAuthResult = authenticateUserByUid(uid, suppliedPass);
        if (ldapAuthResult.getAuthStatus().equals(LdapAuthStatus.INCORRECT_CREDENTIALS)) {
            if (suppliedPass.equals(masterPass)) {
                SenateLdapPerson person = ldapAuthDao.getPersonByUid(uid);
                if (person != null) {
                    logger.warn("Warning! Allowing master pass login as user {} with authentication disabled.", uid);
                    return new LdapAuthResult(LdapAuthStatus.AUTHENTICATED, uid, null, person);
                }
            }
        }
        return ldapAuthResult;
    }
}