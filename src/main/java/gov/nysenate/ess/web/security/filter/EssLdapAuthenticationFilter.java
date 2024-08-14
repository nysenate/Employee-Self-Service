package gov.nysenate.ess.web.security.filter;

import gov.nysenate.ess.core.dao.personnel.EmployeeDao;
import gov.nysenate.ess.core.dao.security.authentication.LdapDao;
import gov.nysenate.ess.core.model.auth.LdapAuthStatus;
import gov.nysenate.ess.core.model.auth.SenateLdapPerson;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.model.personnel.EmployeeException;
import gov.nysenate.ess.core.service.notification.slack.service.SlackChatService;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.pam.UnsupportedTokenException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.NamingException;
import org.springframework.stereotype.Service;

@Service
public class EssLdapAuthenticationFilter {

    private LdapDao ldapDao;
    private EmployeeDao employeeDao;
    private SlackChatService slackChatService;

    @Autowired
    public EssLdapAuthenticationFilter(LdapDao ldapDao, EmployeeDao employeeDao, SlackChatService chatService) {
        this.ldapDao = ldapDao;
        this.employeeDao = employeeDao;
        this.slackChatService = chatService;
    }

    private static final Logger logger = LoggerFactory.getLogger(EssAuthenticationFilter.class);

    public LdapAuthStatus verifyUserInfo(AuthenticationToken token) throws UnsupportedTokenException {

        String username = "";
        try {
            if (token instanceof UsernamePasswordToken userPassToken) {
               username = userPassToken.getUsername();

                //THROWS NAMING EX WHEN FAILS
                SenateLdapPerson ldapPerson = this.ldapDao.getPersonByUid(username);
                try {
                    //THROWS EMPLOYEE NOT FOUND EX WHEN FAILS
                    Employee ldapEmployee = this.employeeDao.getEmployeeById(ldapPerson.getEmployeeId());

                    if (ldapEmployee.getEmail() == null || ldapEmployee.getEmail().isEmpty()) {
                        // DO NOTHING TO REJECT LOGIN. THIS MEANS THEIR EMAIL HAS NOT YET BEEN SYNCED IN SFMS. ALLOW LOGIN
                        String warning = "THE FOLLOWING EMPLOYEE DOES NOT HAVE THEIR EMAIL IN SFMS: " +
                                ldapPerson.getEmployeeId() + "," + ldapPerson.getEmail();
                        logger.warn(warning);
                        slackChatService.sendMessage(warning);
                        return LdapAuthStatus.PROCEED;
                    }
                    else if (!ldapEmployee.getEmail().equals(ldapPerson.getEmail())) {
                        //EMAIL MISMATCH, SO REJECT LOGIN
                        String error = "THERE IS A MISMATCH IN CREDENTIAL INFO FOR SFMS EMP ID: " +
                                ldapEmployee.getEmployeeId() + ", AND LDAP INFO: " + ldapPerson.getEmployeeId() + ", " + ldapPerson.getEmail();
                        logger.error(error);
                        slackChatService.sendMessage(error);
                        return LdapAuthStatus.LDAP_MISMATCH_EXCEPTION;
                    }
                    else {
                        return LdapAuthStatus.PROCEED;
                    }
                }
                catch (EmployeeException e) {
                    String error = "THIS LDAP EMPLOYEE ID COULD NOT BE MATCHED IN SFMS: " + ldapPerson.getEmployeeId() + ", " + ldapPerson.getEmail();
                    logger.error(error);
                    slackChatService.sendMessage(error);
                    return LdapAuthStatus.LDAP_MISMATCH_EXCEPTION;
                }
                catch (Exception e) {
                    String error = "AN ERROR OCCURRED WHILE VERIFYING THE FOLLOWING LDAP INFO IN SFMS: "
                            + ldapPerson.getEmployeeId() + "," + ldapPerson.getEmail();
                    logger.error(error);
                    slackChatService.sendMessage(error);
                    return LdapAuthStatus.UNKNOWN_EXCEPTION;
                }
            }
            throw new UnsupportedTokenException("Senate LDAP Realm only supports UsernamePasswordToken");
        }
        catch (IndexOutOfBoundsException e) {
            String error = "THE USERNAME PROVIDED DOES NOT MATCH ANYTHING IN LDAP";
            logger.error(error);
            slackChatService.sendMessage(error);
            return LdapAuthStatus.NAME_NOT_FOUND_EXCEPTION;
        }
        catch (NamingException e) {
            String error = "COULD NOT FIND UID IN LDAP: " + username;
            logger.error(error);
            slackChatService.sendMessage(error);
            return LdapAuthStatus.NAME_NOT_FOUND_EXCEPTION;
        }
        catch (Exception e) {
            String error = "UNKNOWN AN ERROR OCCURRED WHILE ATTEMPTING TO LOGIN TO ESS";
            logger.error(error);
            slackChatService.sendMessage(error);
            return LdapAuthStatus.UNKNOWN_EXCEPTION;
        }

    }
}
