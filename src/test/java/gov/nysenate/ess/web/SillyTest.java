package gov.nysenate.ess.web;

import gov.nysenate.ess.core.BaseTest;
import gov.nysenate.ess.core.dao.security.authentication.LdapAuthDao;
import gov.nysenate.ess.core.model.auth.SenateLdapPerson;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.service.pec.external.everfi.sync.EverfiUserSyncLoader;
import gov.nysenate.ess.core.service.pec.external.everfi.sync.SyncReportRenderer;
import gov.nysenate.ess.core.service.pec.external.everfi.sync.EverfiUserSyncService;
import gov.nysenate.ess.core.service.personnel.EmployeeInfoService;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.Set;

/**
 * A sample file to run misc tests.
 */
@Category(gov.nysenate.ess.core.annotation.SillyTest.class)

public class SillyTest extends BaseTest {
    private static final Logger logger = LoggerFactory.getLogger(SillyTest.class);

    @Autowired
    private EverfiUserSyncService everfiUserSyncService;

    @Test
    public void testSync() {
        var run = everfiUserSyncService.syncUsers(true);
        logger.info(new SyncReportRenderer(run).toText(true));
    }

    @Ignore
    @Test
    public void printRemoteUserCategoryLabels() {
        Set<RemoteUser> remoteUsers = everfiUserSyncLoader.loadRemoteUsers().remoteUsers();
        logger.info("Total remote users: {}", remoteUsers.size());

        for (RemoteUser user : remoteUsers) {
            List<EverfiCategoryLabel> labels = user.categoryLabels();
            logger.info("User {} ({}) - {} label(s):", user.remoteEmail(), user.remoteUuid(), labels.size());
            for (EverfiCategoryLabel label : labels) {
                logger.info("  category='{}' label='{}'", label.getCategoryName(), label.getLabelName());
            }

            List<EverfiCategoryLabel> uploadListLabels = labels.stream()
                    .filter(l -> "Upload List".equals(l.getCategoryName()))
                    .collect(Collectors.toList());
            if (uploadListLabels.size() > 1) {
                logger.warn("  *** MULTIPLE UPLOAD LIST LABELS ({}) for user {}: {}", uploadListLabels.size(),
                        user.remoteEmail(),
                        uploadListLabels.stream().map(EverfiCategoryLabel::getLabelName).collect(Collectors.joining(", ")));
            }
        }
    }
}
