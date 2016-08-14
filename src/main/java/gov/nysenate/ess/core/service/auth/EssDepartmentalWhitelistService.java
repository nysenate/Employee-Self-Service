package gov.nysenate.ess.core.service.auth;

import com.google.common.collect.ImmutableSet;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.model.personnel.ResponsibilityCenter;
import gov.nysenate.ess.core.model.personnel.ResponsibilityHead;
import gov.nysenate.ess.core.service.personnel.EmployeeInfoService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EssDepartmentalWhitelistService implements DepartmentalWhitelistService {

    private static final Logger logger = LoggerFactory.getLogger(EssDepartmentalWhitelistService.class);

    @Autowired private EmployeeInfoService empInfoService;

    /** A boolean value, true if the app is configured to restrict usage by department */
    @Value("${restrict.department.enabled:false}")
    private boolean restrictionEnabled;

    /** The unparsed String representation of the white list */
    @Value("${restrict.department.whitelist:}")
    private String whitelistProp;

    /** The white list of department names */
    private ImmutableSet<String> whitelist;

    @PostConstruct
    public void init() {
        String whiteListCsv = Optional.ofNullable(whitelistProp).orElse("");
        this.whitelist = Arrays.stream(
                StringUtils.split(whiteListCsv, ","))
                        .map(this::formatRCHString)
                        .collect(Collectors.collectingAndThen(
                                Collectors.toSet(),
                                ImmutableSet::copyOf
                        ));
        if (restrictionEnabled) {
            logger.info("   ***   DEPARTMENTAL RESTRICTIONS ARE IN EFFECT   ***");
            logger.info("Employees in the following departments may log in and are included in all ESS services:");
            whitelist.forEach(deptName ->  logger.info("\t{}", deptName));
        }
    }

    /** {@inheritDoc} */
    @Override
    public boolean isAllowed(Employee employee) {
        // short circuit to true if restriction is not enabled
        if (!restrictionEnabled) {
            return true;
        }

        Optional<ResponsibilityHead> responsibilityHead = Optional.ofNullable(employee)
                .map(Employee::getRespCenter)
                .map(ResponsibilityCenter::getHead);

        String respHeadCode = responsibilityHead
                .map(ResponsibilityHead::getCode)
                .map(this::formatRCHString)
                .orElse(null);
        String respHeadShortName = responsibilityHead
                .map(ResponsibilityHead::getShortName)
                .map(this::formatRCHString)
                .orElse(null);
        String respHeadName = responsibilityHead
                .map(ResponsibilityHead::getName)
                .map(this::formatRCHString)
                .orElse(null);

        // Return true if the whitelist contains the responsibility center head code, short or extended name
        return whitelist.contains(respHeadCode) ||
                whitelist.contains(respHeadShortName) ||
                whitelist.contains(respHeadName);
    }

    /** {@inheritDoc} */
    @Override
    public boolean isAllowed(int empId) {
        // short circuit to true if restriction is not enabled
        return !restrictionEnabled || isAllowed(empInfoService.getEmployee(empId));
    }


    /** {@inheritDoc} */
    @Override
    public ImmutableSet<String> getWhitelist() {
        return whitelist;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isRestrictionEnabled() {
        return restrictionEnabled;
    }

    /** --- Internal Methods --- */

    /**
     * Standardized way of formatting whitelist entries
     */
    private String formatRCHString(String rchString) {
        return Optional.ofNullable(rchString)
                .map(StringUtils::trim)
                .map(StringUtils::upperCase)
                .orElse(null);
    }
}
