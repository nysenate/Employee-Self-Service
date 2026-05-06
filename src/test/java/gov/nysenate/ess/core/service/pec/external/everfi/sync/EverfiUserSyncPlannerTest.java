package gov.nysenate.ess.core.service.pec.external.everfi.sync;

import gov.nysenate.ess.core.annotation.UnitTest;
import gov.nysenate.ess.core.model.pec.everfi.EverfiEmployeeMapping;
import gov.nysenate.ess.core.service.pec.external.everfi.category.EverfiCategoryLabel;
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(HierarchicalContextRunner.class)
@Category(UnitTest.class)
public class EverfiUserSyncPlannerTest {

    private final EverfiUserSyncPlanner planner = new EverfiUserSyncPlanner();

    @Test
    public void emptyUsers_returnsNoActions() {
        assertPlan(Set.of(), Set.of());
    }

    public class WhenDesiredUserUnresolved {

        // Desired Users can't be created in Everfi until they are granted an email.
        @Test
        public void andNoEmail_skips() {
            DesiredUser d = desiredUser()
                    .email(null)
                    .build();

            assertPlan(
                    Set.of(d),
                    Set.of(),
                    action(SyncAction.SKIP, d, null, SyncIssue.MISSING_EMAIL)
            );
        }

        @Test
        public void andNoFirstName_skips() {
            DesiredUser d = desiredUser()
                    .firstName(" ")
                    .build();

            assertPlan(
                    Set.of(d),
                    Set.of(),
                    action(SyncAction.SKIP, d, null, SyncIssue.MISSING_FIRST_NAME)
            );
        }

        @Test
        public void andNoLastName_skips() {
            DesiredUser d = desiredUser()
                    .lastName(" ")
                    .build();

            assertPlan(
                    Set.of(d),
                    Set.of(),
                    action(SyncAction.SKIP, d, null, SyncIssue.MISSING_LAST_NAME)
            );
        }

        @Test
        public void andMultipleMissingIdentityFields_skipsWithAllIssues() {
            DesiredUser d = desiredUser()
                    .email(null)
                    .firstName(" ")
                    .lastName(" ")
                    .build();

            assertPlan(
                    Set.of(d),
                    Set.of(),
                    action(SyncAction.SKIP, d, null,
                            SyncIssue.MISSING_EMAIL,
                            SyncIssue.MISSING_FIRST_NAME,
                            SyncIssue.MISSING_LAST_NAME)
            );
        }

        @Test
        public void andNoCandidateRemotes_creates() {
            DesiredUser d = desiredUser().build();
            assertPlan(
                    Set.of(d),
                    Set.of(),
                    action(SyncAction.CREATE, d, null)
            );
        }

        @Test
        public void andUnmatchedLocalMappingExists_flagsInsteadOfCreate() {
            DesiredUser d = desiredUser().build();
            RemoteUserIndex index = RemoteUserIndex.from(Set.of(), Set.of(d.employeeId()));

            assertThat(planner.plan(Set.of(d), index))
                    .containsExactly(action(SyncAction.FLAG, d, null, SyncIssue.MAPPING_WITHOUT_REMOTE_USER));
        }

        @Test
        public void andActiveCandidateRemote_flags() {
            DesiredUser d = desiredUser().build();
            RemoteUser r = candidateRemote()
                    .remoteEmail("second@nysenate.gov")
                    .build();

            assertPlan(
                    Set.of(d),
                    Set.of(r),
                    action(SyncAction.FLAG, d, r, SyncIssue.UNMAPPED_REMOTE_USER)
            );
        }

        @Test
        public void andInactiveCandidateRemote_flags() {
            DesiredUser d = desiredUser().build();
            RemoteUser r = candidateRemote()
                    .remoteActive(false)
                    .remoteEmail("second@nysenate.gov")
                    .build();

            assertPlan(
                    Set.of(d),
                    Set.of(r),
                    action(SyncAction.FLAG, d, r, SyncIssue.UNMAPPED_REMOTE_USER)
            );
        }

        @Test
        public void andMultipleCandidateRemotes_flagsAll() {
            DesiredUser d = desiredUser().build();
            RemoteUser r1 = candidateRemote()
                    .remoteEmail("second@nysenate.gov")
                    .build();
            RemoteUser r2 = candidateRemote()
                    .remoteUuid("everfi-uuid-3")
                    .remoteActive(false)
                    .remoteEmail("third@nysenate.gov")
                    .build();

            assertPlan(
                    Set.of(d),
                    Set.of(r1, r2),
                    action(SyncAction.FLAG, d, r1, SyncIssue.UNMAPPED_REMOTE_USER, SyncIssue.DUPLICATE_REMOTE_EMP_ID),
                    action(SyncAction.FLAG, d, r2, SyncIssue.UNMAPPED_REMOTE_USER, SyncIssue.DUPLICATE_REMOTE_EMP_ID)
            );
        }
    }

    public class WhenDesiredUserIsResolved {

        @Test
        public void skips() {
            DesiredUser d = desiredUser().build();
            RemoteUser r = authoritativeRemote().build();
            assertPlan(
                    Set.of(d),
                    Set.of(r),
                    action(SyncAction.SKIP, d, r)
            );
        }

        @Test
        public void withStaleData_updates() {
            DesiredUser d = desiredUser().build();
            RemoteUser r = authoritativeRemote()
                    .remoteFirstName("oldName")
                    .build();
            assertPlan(
                    Set.of(d),
                    Set.of(r),
                    action(SyncAction.UPDATE, d, r)
            );
        }

        @Test
        public void withCurrentAndStaleDepartmentLabels_updates() {
            EverfiCategoryLabel currentDepartment = label(100, "Department", "HR");
            EverfiCategoryLabel staleDepartment = label(101, "Department", "Finance");
            DesiredUser desired = desiredUser()
                    .categoryLabels(List.of(currentDepartment))
                    .build();
            RemoteUser remote = authoritativeRemote()
                    .categoryLabels(List.of(currentDepartment, staleDepartment))
                    .build();

            assertPlan(
                    Set.of(desired),
                    Set.of(remote),
                    action(SyncAction.UPDATE, desired, remote)
            );
        }

        @Test
        public void withPersonalEmailOverride_skipsWhenEmailIsOnlyDifference() {
            DesiredUser desired = desiredUser().build();
            RemoteUser remote = authoritativeRemote()
                    .remoteEmail("person@example.com")
                    .build();

            assertPlan(
                    Set.of(desired),
                    Set.of(remote),
                    action(SyncAction.SKIP, desired, remote)
            );
        }

        @Test
        public void withDesiredEmailDifferingOnlyByCase_skips() {
            DesiredUser desired = desiredUser()
                    .email("Example@NYSENATE.GOV")
                    .build();
            RemoteUser remote = authoritativeRemote().build();

            assertPlan(
                    Set.of(desired),
                    Set.of(remote),
                    action(SyncAction.SKIP, desired, remote)
            );
        }

        @Test
        public void withPersonalEmailOverrideAndOtherStaleData_updatesWithoutOverwritingEmail() {
            DesiredUser desired = desiredUser()
                    .firstName("Updated")
                    .build();
            RemoteUser remote = authoritativeRemote()
                    .remoteFirstName("Old")
                    .remoteEmail("person@example.com")
                    .build();

            assertPlan(
                    Set.of(desired),
                    Set.of(remote),
                    action(SyncAction.UPDATE, desired, remote)
            );
        }

        @Test
        public void withNullEmailAndRemoteEmailMismatch_skipsAndFlagsMissingEmail() {
            DesiredUser desired = desiredUser()
                    .email(null)
                    .build();
            RemoteUser remote = authoritativeRemote()
                    .remoteEmail("remote@nysenate.gov")
                    .build();

            assertPlan(
                    Set.of(desired),
                    Set.of(remote),
                    action(SyncAction.SKIP, desired, remote, SyncIssue.MISSING_EMAIL)
            );
        }

        @Test
        public void withNullFirstName_skipsAndFlagsMissingFirstName() {
            DesiredUser desired = desiredUser()
                    .firstName(" ")
                    .build();
            RemoteUser remote = authoritativeRemote()
                    .remoteFirstName("remote")
                    .build();

            assertPlan(
                    Set.of(desired),
                    Set.of(remote),
                    action(SyncAction.SKIP, desired, remote, SyncIssue.MISSING_FIRST_NAME)
            );
        }

        @Test
        public void withNullLastName_skipsAndFlagsMissingLastName() {
            DesiredUser desired = desiredUser()
                    .lastName(" ")
                    .build();
            RemoteUser remote = authoritativeRemote()
                    .remoteLastName("remote")
                    .build();

            assertPlan(
                    Set.of(desired),
                    Set.of(remote),
                    action(SyncAction.SKIP, desired, remote, SyncIssue.MISSING_LAST_NAME)
            );
        }

        @Test
        public void withMissingEmailAndFirstName_skipsWithBothIssuesBeforeUpdateOrReactivate() {
            DesiredUser desired = desiredUser()
                    .email(null)
                    .firstName(" ")
                    .build();
            RemoteUser remote = authoritativeRemote()
                    .remoteActive(false)
                    .remoteEmail("remote@nysenate.gov")
                    .remoteFirstName("remote")
                    .build();

            assertPlan(
                    Set.of(desired),
                    Set.of(remote),
                    action(SyncAction.SKIP, desired, remote,
                            SyncIssue.MISSING_EMAIL,
                            SyncIssue.MISSING_FIRST_NAME)
            );
        }

        @Test
        public void inactiveRemoteWithMissingEmailMismatch_skipsAndFlagsMissingEmail() {
            DesiredUser desired = desiredUser()
                    .email(null)
                    .build();
            RemoteUser remote = authoritativeRemote()
                    .remoteActive(false)
                    .remoteEmail("remote@nysenate.gov")
                    .build();

            assertPlan(
                    Set.of(desired),
                    Set.of(remote),
                    action(SyncAction.SKIP, desired, remote, SyncIssue.MISSING_EMAIL)
            );
        }

        @Test
        public void andCandidateRemoteExists_authoritativeTakesPrecedence() {
            DesiredUser d = desiredUser().build();
            RemoteUser authoritative = authoritativeRemote().build();
            RemoteUser candidate = candidateRemote().build();

            assertPlan(
                    Set.of(d),
                    Set.of(authoritative, candidate),
                    action(SyncAction.SKIP, d, authoritative),
                    action(SyncAction.FLAG, d, candidate, SyncIssue.DUPLICATE_REMOTE_EMP_ID)
            );
        }

        @Test
        public void andInactive_reactivates() {
            DesiredUser d = desiredUser().build();
            RemoteUser r = authoritativeRemote().remoteActive(false).build();

            assertPlan(
                    Set.of(d),
                    Set.of(r),
                    action(SyncAction.REACTIVATE, d, r)
            );
        }

        @Test
        public void andEmployeeIdMismatch_flags() {
            DesiredUser desired = desiredUser().build();
            RemoteUser remote = authoritativeRemote()
                    .remoteEmployeeId(2)
                    .build();

            assertPlan(
                    Set.of(desired),
                    Set.of(remote),
                    action(SyncAction.FLAG, desired, remote, SyncIssue.MAPPING_EMPLOYEE_ID_MISMATCH)
            );
        }

        @Test
        public void andEmployeeIdNullMismatch_flags() {
            DesiredUser desired = desiredUser().build();
            RemoteUser remote = authoritativeRemote()
                    .remoteEmployeeId(null)
                    .build();

            assertPlan(
                    Set.of(desired),
                    Set.of(remote),
                    action(SyncAction.FLAG, desired, remote, SyncIssue.MAPPING_EMPLOYEE_ID_MISMATCH)
            );
        }
    }

    public class WhenOrphaned {

        @Test
        public void andActiveAuthoritativeRemote_deactivates() {
            RemoteUser r = authoritativeRemote().build();

            assertPlan(
                    Set.of(),
                    Set.of(r),
                    action(SyncAction.DEACTIVATE, null, r)
            );
        }

        @Test
        public void andInactiveAuthoritativeRemote_skips() {
            RemoteUser r = authoritativeRemote()
                    .remoteActive(false)
                    .build();

            assertPlan(
                    Set.of(),
                    Set.of(r),
                    action(SyncAction.SKIP, null, r)
            );
        }

        @Test
        public void andAuthoritativeRemoteEmployeeIdMismatch_flags() {
            RemoteUser remote = authoritativeRemote()
                    .remoteEmployeeId(2)
                    .build();

            assertPlan(
                    Set.of(),
                    Set.of(remote),
                    action(SyncAction.FLAG, null, remote, SyncIssue.MAPPING_EMPLOYEE_ID_MISMATCH)
            );
        }

        @Test
        public void andAuthoritativeRemoteEmployeeIdNullMismatch_flags() {
            RemoteUser remote = authoritativeRemote()
                    .remoteEmployeeId(null)
                    .build();

            assertPlan(
                    Set.of(),
                    Set.of(remote),
                    action(SyncAction.FLAG, null, remote, SyncIssue.MAPPING_EMPLOYEE_ID_MISMATCH)
            );
        }

        @Test
        public void andActiveCandidateRemote_flags() {
            RemoteUser r = candidateRemote().build();

            assertPlan(
                    Set.of(),
                    Set.of(r),
                    action(SyncAction.FLAG, null, r, SyncIssue.UNRECOGNIZED_ACTIVE_REMOTE)
            );
        }

        @Test
        public void andInactiveCandidateRemote_skips() {
            RemoteUser r = candidateRemote()
                    .remoteActive(false)
                    .build();

            assertPlan(
                    Set.of(),
                    Set.of(r),
                    action(SyncAction.SKIP, null, r)
            );
        }

        @Test
        public void andMultipleCandidateRemotes_flagDuplicateEmployeeId() {
            RemoteUser active = candidateRemote().build();
            RemoteUser inactive = candidateRemote()
                    .remoteUuid("everfi-uuid-3")
                    .remoteEmail("third@nysenate.gov")
                    .remoteActive(false)
                    .build();

            assertPlan(
                    Set.of(),
                    Set.of(active, inactive),
                    action(SyncAction.FLAG, null, active,
                            SyncIssue.UNRECOGNIZED_ACTIVE_REMOTE, SyncIssue.DUPLICATE_REMOTE_EMP_ID),
                    action(SyncAction.FLAG, null, inactive, SyncIssue.DUPLICATE_REMOTE_EMP_ID)
            );
        }

        @Test
        public void andActiveUnidentifiableRemote_flags() {
            RemoteUser r = unidentifiableRemote().build();

            assertPlan(
                    Set.of(),
                    Set.of(r),
                    action(SyncAction.FLAG, null, r, SyncIssue.UNRECOGNIZED_ACTIVE_REMOTE)
            );
        }

        @Test
        public void andInactiveUnidentifiableRemote_skips() {
            RemoteUser r = unidentifiableRemote()
                    .remoteActive(false)
                    .build();

            assertPlan(
                    Set.of(),
                    Set.of(r),
                    action(SyncAction.SKIP, null, r)
            );
        }

    }

    private DesiredUser.DesiredUserBuilder desiredUser() {
        return DesiredUser.builder()
                .employeeId(1)
                .email("example@nysenate.gov")
                .firstName("John")
                .lastName("Doe");
    }

    private RemoteUser.RemoteUserBuilder authoritativeRemote() {
        return RemoteUser.builder()
                .mapping(new EverfiEmployeeMapping(1, "everfi-uuid-1"))
                .remoteUuid("everfi-uuid-1")
                .remoteEmployeeId(1)
                .remoteActive(true)
                .remoteFirstName("John")
                .remoteLastName("Doe")
                .remoteEmail("example@nysenate.gov");
    }

    private RemoteUser.RemoteUserBuilder candidateRemote() {
        return authoritativeRemote()
                .mapping(null)
                .remoteUuid("everfi-uuid-2")
                .remoteEmail("candidate@nysenate.gov");
    }

    private RemoteUser.RemoteUserBuilder unidentifiableRemote() {
        return candidateRemote()
                .remoteEmployeeId(null);
    }

    private static EverfiCategoryLabel label(int labelId, String categoryName, String labelName) {
        EverfiCategoryLabel label = new EverfiCategoryLabel(labelId, labelName);
        label.setCategoryName(categoryName);
        return label;
    }

    private void assertPlan(Set<DesiredUser> desiredUsers, Set<RemoteUser> remoteUsers, PlannedAction... expectedActions) {
        assertThat(planner.plan(desiredUsers, RemoteUserIndex.from(remoteUsers)))
                .containsExactlyInAnyOrder(expectedActions);
    }

    private PlannedAction action(SyncAction action, DesiredUser desired, RemoteUser remote, SyncIssue... issues) {
        return new PlannedAction(action, desired, remote, List.of(issues));
    }
}
