package gov.nysenate.ess.core.service.pec.external.everfi.sync;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Decides what should happen for every employee and every remote user, with no side effects.
 * The planner is the home of the sync's business rules; the executor only carries them out.
 *
 * <p>For each desired user the planner walks a fixed precedence:
 * <ol>
 *   <li><b>Integrity failures</b> (e.g. a mapping pointing at a remote user with a different employee id) →
 *       always FLAG, regardless of any other state. These represent corruption and must not be auto-corrected.</li>
 *   <li><b>Orphaned authoritative remote</b> (no desired user) →
 *       DEACTIVATE if active, SKIP if already inactive.</li>
 *   <li><b>Sync-eligibility failures</b> (e.g. desired user has no email) → SKIP with an issue.</li>
 *   <li><b>Lifecycle</b> — inactive authoritative remote → REACTIVATE.</li>
 *   <li><b>Field drift</b> — fields differ from desired → UPDATE; otherwise SKIP.</li>
 * </ol>
 *
 * <p>For unresolved desired users (no authoritative remote) the planner picks between
 * FLAG (mapping or candidate ambiguity), SKIP (missing email — required to provision), and CREATE.
 *
 * <p>For remote users with no desired counterpart the planner picks based on identity confidence
 * and active state (see {@code package-info.java}'s "orphaned remote" section).
 *
 * <p>See {@code package-info.java} for the ubiquitous language behind these terms.
 */
@Service
public class EverfiUserSyncPlanner {

    /**
     * Produces one planned action per desired user plus one per orphaned remote user. Duplicate
     * candidate remotes for the same employee can produce additional FLAG entries beyond that base.
     * Pure: same inputs → same output, no IO.
     */
    List<PlannedAction> plan(@NotNull Set<DesiredUser> desiredUsers, @NotNull RemoteUserIndex remoteUserIndex) {
        List<PlannedAction> plannedActions = new ArrayList<>();
        Set<Integer> desiredEmployeeIds = desiredUsers.stream().map(DesiredUser::employeeId).collect(Collectors.toSet());

        for (DesiredUser desiredUser : desiredUsers) {
            Optional<RemoteUser> authoritativeRemote = remoteUserIndex.getAuthoritativeMatch(desiredUser.employeeId());
            if (authoritativeRemote.isPresent()) {
                planResolvedDesiredUser(
                        plannedActions,
                        desiredUser,
                        authoritativeRemote.get(),
                        remoteUserIndex.getCandidates(desiredUser.employeeId())
                );
            } else {
                planUnresolvedDesiredUser(
                        plannedActions,
                        desiredUser,
                        remoteUserIndex.getCandidates(desiredUser.employeeId()),
                        remoteUserIndex.empIdsWithUnmatchedMappings()
                );
            }
        }

        planOrphanedRemoteUsers(plannedActions, desiredEmployeeIds, remoteUserIndex);

        validateInputsUnmodified(plannedActions, desiredUsers, remoteUserIndex.allRemoteUsers());
        return plannedActions;
    }

    private void validateInputsUnmodified(List<PlannedAction> actions,
                                          Set<DesiredUser> desiredUsers,
                                          Set<RemoteUser> remoteUsers) {
        for (PlannedAction action : actions) {
            if (action.desired() != null && !desiredUsers.contains(action.desired())) {
                throw new IllegalStateException(
                        "PlannedAction contains a DesiredUser not present in the input set: " + action.desired()
                );
            }
            if (action.remote() != null && !remoteUsers.contains(action.remote())) {
                throw new IllegalStateException(
                        "PlannedAction contains a RemoteUser not present in the remote user index: " + action.remote()
                );
            }
        }
    }

    /**
     * Plans a desired user that has an authoritative remote match — we know exactly which remote user
     * represents this employee. Any additional candidate matches for the same employee ID indicate
     * duplicate remote data and are flagged for review.
     */
    private void planResolvedDesiredUser(List<PlannedAction> plannedActions,
                                         DesiredUser desiredUser,
                                         RemoteUser authoritativeRemote,
                                         Optional<Set<RemoteUser>> candidates) {
        plannedActions.add(classifyAuthoritativeRemoteUser(desiredUser, authoritativeRemote));
        flagDuplicateCandidateRemotes(plannedActions, desiredUser, candidates);
    }

    /**
     * Plans a desired user with no authoritative remote match. If a local mapping row exists but its
     * UUID is absent from the remote snapshot, the employee is flagged for manual review rather than
     * routed to CREATE (which would cause a duplicate mapping insert). If candidate matches exist
     * (remote users claiming the same employee ID but without a formal mapping), each is flagged for
     * human review. If no remote user exists at all, a new one is created.
     */
    private void planUnresolvedDesiredUser(List<PlannedAction> plannedActions,
                                           DesiredUser desiredUser,
                                           Optional<Set<RemoteUser>> candidates,
                                           Set<Integer> unmatchedMappingEmpIds) {
        if (unmatchedMappingEmpIds.contains(desiredUser.employeeId())) {
            plannedActions.add(plannedAction(SyncAction.FLAG, desiredUser, null, SyncIssue.MAPPING_WITHOUT_REMOTE_USER));
            if (candidates.isPresent()) {
                boolean hasDuplicateRemoteEmpId = candidates.get().size() > 1;
                for (RemoteUser candidate : candidates.get()) {
                    plannedActions.add(plannedAction(SyncAction.FLAG, desiredUser, candidate,
                            unresolvedCandidateIssues(hasDuplicateRemoteEmpId)));
                }
            }
            return;
        }
        if (candidates.isPresent()) {
            boolean hasDuplicateRemoteEmpId = candidates.get().size() > 1;
            for (RemoteUser candidate : candidates.get()) {
                plannedActions.add(plannedAction(
                        SyncAction.FLAG,
                        desiredUser,
                        candidate,
                        unresolvedCandidateIssues(hasDuplicateRemoteEmpId)
                ));
            }
        } else {
            List<SyncIssue> eligibilityIssues = desiredUserEligibilityIssues(desiredUser);
            if (!eligibilityIssues.isEmpty()) {
                plannedActions.add(new PlannedAction(SyncAction.SKIP, desiredUser, null, eligibilityIssues));
            } else {
                plannedActions.add(plannedAction(SyncAction.CREATE, desiredUser, null));
            }
        }
    }

    /**
     * Plans all remote users that have no corresponding desired user — i.e. employees no longer
     * expected to be active in Everfi. Covers authoritative, candidate, and unidentifiable remotes.
     */
    private void planOrphanedRemoteUsers(List<PlannedAction> plannedActions,
                                         Set<Integer> desiredEmployeeIds,
                                         RemoteUserIndex remoteUserIndex) {
        for (var entry : remoteUserIndex.authoritativeRemotes().entrySet()) {
            if (desiredEmployeeIds.contains(entry.getKey())) {
                continue;
            }

            planOrphanedAuthoritativeRemoteUser(plannedActions, entry.getValue());
        }

        for (var entry : remoteUserIndex.candidateRemotes().entrySet()) {
            if (desiredEmployeeIds.contains(entry.getKey())) {
                continue;
            }

            boolean hasDuplicateRemoteEmpId = entry.getValue().size() > 1;
            for (RemoteUser remoteUser : entry.getValue()) {
                planOrphanedNonAuthoritativeRemoteUser(plannedActions, remoteUser, hasDuplicateRemoteEmpId);
            }
        }

        for (RemoteUser remoteUser : remoteUserIndex.unidentifiableRemotes()) {
            planOrphanedNonAuthoritativeRemoteUser(plannedActions, remoteUser, false);
        }
    }

    private void planOrphanedAuthoritativeRemoteUser(List<PlannedAction> plannedActions, RemoteUser remoteUser) {
        plannedActions.add(classifyAuthoritativeRemoteUser(null, remoteUser));
    }

    private void planOrphanedNonAuthoritativeRemoteUser(List<PlannedAction> plannedActions,
                                                        RemoteUser remoteUser,
                                                        boolean hasDuplicateRemoteEmpId) {
        if (hasDuplicateRemoteEmpId) {
            plannedActions.add(remoteUser.remoteActive()
                    ? plannedAction(SyncAction.FLAG, null, remoteUser,
                    SyncIssue.UNRECOGNIZED_ACTIVE_REMOTE, SyncIssue.DUPLICATE_REMOTE_EMP_ID)
                    : plannedAction(SyncAction.FLAG, null, remoteUser, SyncIssue.DUPLICATE_REMOTE_EMP_ID));
            return;
        }

        plannedActions.add(remoteUser.remoteActive()
                ? plannedAction(SyncAction.FLAG, null, remoteUser, SyncIssue.UNRECOGNIZED_ACTIVE_REMOTE)
                : plannedAction(SyncAction.SKIP, null, remoteUser));
    }

    private PlannedAction classifyAuthoritativeRemoteUser(@Nullable DesiredUser desiredUser, RemoteUser authoritativeRemote) {
        PlannedAction integrityFailure = classifyAuthoritativeIntegrityFailure(desiredUser, authoritativeRemote);
        if (integrityFailure != null) {
            return integrityFailure;
        }

        PlannedAction orphanedRemoteAction = classifyOrphanedAuthoritativeRemote(desiredUser, authoritativeRemote);
        if (orphanedRemoteAction != null) {
            return orphanedRemoteAction;
        }

        PlannedAction syncEligibilityFailure = classifySyncEligibilityFailure(desiredUser, authoritativeRemote);
        if (syncEligibilityFailure != null) {
            return syncEligibilityFailure;
        }

        PlannedAction lifecycleAction = classifyAuthoritativeLifecycleAction(desiredUser, authoritativeRemote);
        if (lifecycleAction != null) {
            return lifecycleAction;
        }

        if (authoritativeRemote.isStale(desiredUser)) {
            return plannedAction(SyncAction.UPDATE, desiredUser, authoritativeRemote);
        }
        return plannedAction(SyncAction.SKIP, desiredUser, authoritativeRemote);
    }

    /**
     * Highest-priority failures indicate remote/mapping corruption and always require review.
     */
    private PlannedAction classifyAuthoritativeIntegrityFailure(@Nullable DesiredUser desiredUser,
                                                               RemoteUser authoritativeRemote) {
        if (hasMappingEmployeeIdMismatch(authoritativeRemote)) {
            return plannedAction(SyncAction.FLAG, desiredUser, authoritativeRemote, SyncIssue.MAPPING_EMPLOYEE_ID_MISMATCH);
        }
        return null;
    }

    /**
     * Orphaned authoritative remotes represent employees that no longer exist in the desired set.
     */
    private PlannedAction classifyOrphanedAuthoritativeRemote(@Nullable DesiredUser desiredUser,
                                                              RemoteUser authoritativeRemote) {
        if (desiredUser == null) {
            return plannedAction(
                    authoritativeRemote.remoteActive() ? SyncAction.DEACTIVATE : SyncAction.SKIP,
                    null,
                    authoritativeRemote
            );
        }
        return null;
    }

    /**
     * Lifecycle changes happen before field-level sync decisions.
     */
    private PlannedAction classifyAuthoritativeLifecycleAction(DesiredUser desiredUser, RemoteUser authoritativeRemote) {
        if (!authoritativeRemote.remoteActive()) {
            return plannedAction(SyncAction.REACTIVATE, desiredUser, authoritativeRemote);
        }
        return null;
    }

    /**
     * Some desired users are resolved to a remote user but are not eligible for content updates.
     */
    private PlannedAction classifySyncEligibilityFailure(DesiredUser desiredUser, RemoteUser authoritativeRemote) {
        List<SyncIssue> eligibilityIssues = desiredUserEligibilityIssues(desiredUser);
        if (!eligibilityIssues.isEmpty()) {
            return new PlannedAction(SyncAction.SKIP, desiredUser, authoritativeRemote, eligibilityIssues);
        }
        return null;
    }

    private boolean hasMappingEmployeeIdMismatch(RemoteUser authoritativeRemote) {
        return authoritativeRemote.mapping() != null
                && !Integer.valueOf(authoritativeRemote.mapping().employeeId()).equals(authoritativeRemote.remoteEmployeeId());
    }

    private List<SyncIssue> desiredUserEligibilityIssues(DesiredUser desiredUser) {
        List<SyncIssue> issues = new ArrayList<>();
        if (desiredUser.email() == null) {
            issues.add(SyncIssue.MISSING_EMAIL);
        }
        if (desiredUser.firstName() == null) {
            issues.add(SyncIssue.MISSING_FIRST_NAME);
        }
        if (desiredUser.lastName() == null) {
            issues.add(SyncIssue.MISSING_LAST_NAME);
        }
        return issues;
    }

    private void flagDuplicateCandidateRemotes(List<PlannedAction> plannedActions,
                                               DesiredUser desiredUser,
                                               Optional<Set<RemoteUser>> candidates) {
        if (candidates.isEmpty()) {
            return;
        }
        for (RemoteUser candidate : candidates.get()) {
            plannedActions.add(plannedAction(SyncAction.FLAG, desiredUser, candidate, SyncIssue.DUPLICATE_REMOTE_EMP_ID));
        }
    }

    private SyncIssue[] unresolvedCandidateIssues(boolean hasDuplicateRemoteEmpId) {
        return hasDuplicateRemoteEmpId
                ? new SyncIssue[]{SyncIssue.UNMAPPED_REMOTE_USER, SyncIssue.DUPLICATE_REMOTE_EMP_ID}
                : new SyncIssue[]{SyncIssue.UNMAPPED_REMOTE_USER};
    }

    private PlannedAction plannedAction(SyncAction action,
                                        @Nullable DesiredUser desiredUser,
                                        @Nullable RemoteUser remoteUser,
                                        SyncIssue... issues) {
        return new PlannedAction(action, desiredUser, remoteUser, List.of(issues));
    }
}
