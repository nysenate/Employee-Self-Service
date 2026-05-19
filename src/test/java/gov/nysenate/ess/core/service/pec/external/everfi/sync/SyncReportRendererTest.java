package gov.nysenate.ess.core.service.pec.external.everfi.sync;

import de.bechte.junit.runners.context.HierarchicalContextRunner;
import gov.nysenate.ess.core.annotation.UnitTest;
import gov.nysenate.ess.core.model.pec.everfi.EverfiEmployeeMapping;
import gov.nysenate.ess.core.service.pec.external.everfi.category.EverfiCategoryLabel;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(HierarchicalContextRunner.class)
@Category(UnitTest.class)
public class SyncReportRendererTest {

    @Test
    public void generate_includesHeaderSummaryAndKeyActionSections() {
        PlannedAction createSuccess = new PlannedAction(
                SyncAction.CREATE,
                desiredUser().build(),
                null,
                List.of()
        );
        PlannedAction createError = new PlannedAction(
                SyncAction.CREATE,
                desiredUser().employeeId(2).firstName("Casey").lastName("Jones").email("casey@nysenate.gov").build(),
                null,
                List.of()
        );
        PlannedAction reactivateSuccess = new PlannedAction(
                SyncAction.REACTIVATE,
                desiredUser().employeeId(3).firstName("Robin").lastName("Lee").email("robin@nysenate.gov").build(),
                mappedRemote(3, "everfi-uuid-3").remoteActive(false).build(),
                List.of()
        );
        PlannedAction reactivateError = new PlannedAction(
                SyncAction.REACTIVATE,
                desiredUser().employeeId(4).firstName("Jamie").lastName("Fox").email("jamie@nysenate.gov").build(),
                mappedRemote(4, "everfi-uuid-4").remoteActive(false).build(),
                List.of()
        );
        PlannedAction flagWithDesired = new PlannedAction(
                SyncAction.FLAG,
                desiredUser().employeeId(5).firstName("Avery").lastName("Stone").build(),
                candidateRemote(5, "flagged@nysenate.gov").build(),
                List.of(SyncIssue.UNMAPPED_REMOTE_USER)
        );
        PlannedAction flagWithoutDesired = new PlannedAction(
                SyncAction.FLAG,
                null,
                RemoteUser.builder()
                        .mapping(null)
                        .remoteUuid("everfi-uuid-6")
                        .remoteEmployeeId(null)
                        .remoteActive(true)
                        .remoteFirstName("Orphan")
                        .remoteLastName("Remote")
                        .remoteEmail("orphan@nysenate.gov")
                        .build(),
                List.of(SyncIssue.UNRECOGNIZED_ACTIVE_REMOTE)
        );
        PlannedAction skip = new PlannedAction(
                SyncAction.SKIP,
                desiredUser().employeeId(7).firstName("Skip").lastName("User").build(),
                null,
                List.of()
        );
        PlannedAction deactivateSuccess = new PlannedAction(
                SyncAction.DEACTIVATE,
                null,
                mappedRemote(9, "everfi-uuid-9")
                        .remoteFirstName("Taylor")
                        .remoteLastName("Gray")
                        .remoteEmail("taylor@nysenate.gov")
                        .build(),
                List.of()
        );
        PlannedAction deactivateError = new PlannedAction(
                SyncAction.DEACTIVATE,
                null,
                mappedRemote(10, "everfi-uuid-10")
                        .remoteFirstName("Morgan")
                        .remoteLastName("Vale")
                        .remoteEmail("morgan@nysenate.gov")
                        .build(),
                List.of()
        );
        PlannedAction updateError = new PlannedAction(
                SyncAction.UPDATE,
                desiredUser().employeeId(8).firstName("Up").lastName("Date").build(),
                mappedRemote(8, "everfi-uuid-8").build(),
                List.of()
        );

        SyncRun run = new SyncRun(List.of(
                SyncResult.success(createSuccess),
                SyncResult.error(createError, "create failed"),
                SyncResult.success(deactivateSuccess),
                SyncResult.error(deactivateError, "deactivate failed"),
                SyncResult.success(reactivateSuccess),
                SyncResult.error(reactivateError, "reactivate failed"),
                SyncResult.flagged(flagWithDesired),
                SyncResult.flagged(flagWithoutDesired),
                SyncResult.skipped(skip),
                SyncResult.error(updateError, "update failed")
        ), true, LocalDateTime.of(2026, 4, 22, 13, 45, 0));

        String report = new SyncReportRenderer(run).toText();

        assertThat(report).contains("2026-04-22 13:45:00  |  Mode: DRY RUN");
        assertThat(report).contains("Skip", "1");
        assertThat(report).contains("Update", "1", "(1 error)");
        assertThat(report).contains("Deactivate", "2", "(1 error)");
        assertThat(report).contains("Create", "2", "(1 error)");
        assertThat(report).contains("Reactivate", "2", "(1 error)");
        assertThat(report).contains("Flag", "2");
        assertThat(report).contains("Total", "10");
        assertThat(report).contains("DEACTIVATED USERS  (1 succeeded, 1 error)");
        assertThat(report).contains("9         Gray, Taylor                  taylor@nysenate.gov");
        assertThat(report).contains("[10] Vale, Morgan -- deactivate failed");
        assertThat(report).contains("CREATED USERS  (1 succeeded, 1 error)");
        assertThat(report).contains("1         User, Test                    user@nysenate.gov");
        assertThat(report).contains("[2] Jones, Casey -- create failed");
        assertThat(report).contains("REACTIVATED USERS  (1 succeeded, 1 error)");
        assertThat(report).contains("3         Lee, Robin                    robin@nysenate.gov");
        assertThat(report).contains("[4] Fox, Jamie -- reactivate failed");
        assertThat(report).contains("UPDATED USERS  (0 succeeded, 1 error)");
        assertThat(report).contains("[8] Date, Up -- update failed");
        assertThat(report).contains("FLAGGED USERS  (2)  -- REQUIRE MANUAL REVIEW");
        assertThat(report).contains("5         Stone, Avery                  Matching Everfi user exists without an ESS mapping");
        assertThat(report).contains("(unknown)  Remote, Orphan                Active Everfi user has no ESS match or mapping");
    }

    @Test
    public void generate_detailed_showsFieldDiffsForUpdates() {
        PlannedAction update = new PlannedAction(
                SyncAction.UPDATE,
                desiredUser().firstName("John").lastName("Smith").email("new@nysenate.gov").build(),
                mappedRemote(1, "uuid-1")
                        .remoteFirstName("JOHN")
                        .remoteLastName("SMITH")
                        .remoteEmail("old@nysenate.gov")
                        .build(),
                List.of()
        );

        SyncRun run = new SyncRun(List.of(SyncResult.success(update)), false,
                LocalDateTime.of(2026, 4, 22, 13, 45, 0));
        String report = new SyncReportRenderer(run).toText(true);

        assertThat(report).contains("UPDATED USERS  (1 succeeded, 0 errors)");
        assertThat(report).contains("first_name:   \"JOHN\" -> \"John\"");
        assertThat(report).contains("last_name:    \"SMITH\" -> \"Smith\"");
        assertThat(report).contains("email:        \"old@nysenate.gov\" -> \"new@nysenate.gov\"");
    }

    @Test
    public void generate_detailed_showsFieldDiffsForReactivates() {
        PlannedAction reactivate = new PlannedAction(
                SyncAction.REACTIVATE,
                desiredUser().firstName("Robin").lastName("Lee").email("robin@nysenate.gov").build(),
                mappedRemote(1, "uuid-1")
                        .remoteActive(false)
                        .remoteFirstName("Robin")
                        .remoteLastName("LEE")
                        .remoteEmail("robin@nysenate.gov")
                        .build(),
                List.of()
        );

        SyncRun run = new SyncRun(List.of(SyncResult.success(reactivate)), false,
                LocalDateTime.of(2026, 4, 22, 13, 45, 0));
        String report = new SyncReportRenderer(run).toText(true);

        assertThat(report).contains("REACTIVATED USERS  (1 succeeded, 0 errors)");
        assertThat(report).contains("active:       false -> true");
        assertThat(report).contains("last_name:    \"LEE\" -> \"Lee\"");
        assertThat(report).doesNotContain("first_name:");
        assertThat(report).doesNotContain("email:");
    }

    @Test
    public void generate_detailed_showsLabelsForCreates() {
        PlannedAction create = new PlannedAction(
                SyncAction.CREATE,
                DesiredUser.builder()
                        .employeeId(1)
                        .firstName("Test")
                        .lastName("User")
                        .email("user@nysenate.gov")
                        .desiredLabels(List.of(new DesiredLabel("Upload List", "Apr 22 2026")))
                        .build(),
                null,
                List.of()
        );

        SyncRun run = new SyncRun(List.of(SyncResult.success(create)), false,
                LocalDateTime.of(2026, 4, 22, 13, 45, 0));
        String report = new SyncReportRenderer(run).toText(true);

        assertThat(report).contains("CREATED USERS  (1 succeeded, 0 errors)");
        assertThat(report).contains("labels:       Upload List: Apr 22 2026");
    }

    @Test
    public void generate_includesSkippedIssueSectionWithBlockedStatus() {
        PlannedAction createBlocked = new PlannedAction(
                SyncAction.SKIP,
                desiredUser().employeeId(11).firstName("Create").lastName("Blocked").email(null).build(),
                null,
                List.of(SyncIssue.MISSING_EMAIL)
        );
        PlannedAction reactivateBlocked = new PlannedAction(
                SyncAction.SKIP,
                desiredUser().employeeId(12).firstName("Reactivate").lastName("Blocked").email(null).build(),
                mappedRemote(12, "uuid-12")
                        .remoteActive(false)
                        .remoteFirstName("Reactivate")
                        .remoteLastName("Blocked")
                        .build(),
                List.of(SyncIssue.MISSING_EMAIL)
        );
        PlannedAction plainSkip = new PlannedAction(
                SyncAction.SKIP,
                desiredUser().employeeId(13).firstName("Plain").lastName("Skip").build(),
                null,
                List.of()
        );

        SyncRun run = new SyncRun(List.of(
                SyncResult.skipped(createBlocked),
                SyncResult.skipped(reactivateBlocked),
                SyncResult.skipped(plainSkip)
        ), true, LocalDateTime.of(2026, 4, 22, 13, 45, 0));

        String report = new SyncReportRenderer(run).toText();

        assertThat(report).contains("SKIPPED USERS WITH ISSUES  (2)");
        assertThat(report).contains("11", "Blocked, Create", "CREATE blocked", "Missing local email");
        assertThat(report).contains("12", "Blocked, Reactivate", "REACTIVATE blocked", "Missing local email");
        assertThat(report).doesNotContain("Skip, Plain");
    }

    private DesiredUser.DesiredUserBuilder desiredUser() {
        return DesiredUser.builder()
                .employeeId(1)
                .email("user@nysenate.gov")
                .firstName("Test")
                .lastName("User");
    }

    private RemoteUser.RemoteUserBuilder mappedRemote(int employeeId, String uuid) {
        return RemoteUser.builder()
                .mapping(new EverfiEmployeeMapping(employeeId, uuid))
                .remoteUuid(uuid)
                .remoteEmployeeId(employeeId)
                .remoteActive(true)
                .remoteFirstName("Remote")
                .remoteLastName("User")
                .remoteEmail("remote@nysenate.gov");
    }

    private RemoteUser.RemoteUserBuilder candidateRemote(int employeeId, String email) {
        return RemoteUser.builder()
                .mapping(null)
                .remoteUuid("candidate-uuid-" + employeeId)
                .remoteEmployeeId(employeeId)
                .remoteActive(true)
                .remoteFirstName("Candidate")
                .remoteLastName("Remote")
                .remoteEmail(email);
    }

    private EverfiCategoryLabel labelWithCategory(int labelId, String labelName, String categoryName) {
        EverfiCategoryLabel label = new EverfiCategoryLabel(labelId, labelName);
        label.setCategoryName(categoryName);
        return label;
    }
}
