package gov.nysenate.ess.core.service.pec.external.everfi.sync;

import gov.nysenate.ess.core.model.pec.everfi.EverfiEmployeeMapping;

import java.util.List;

/**
 * The output of the Planner. Represents the classification of a single employee
 * into an action to be taken, along with all context the Resolver will need to
 * produce a SyncCommand.
 *
 * <p>Either {@code desired} or {@code current} may be null, but never both:
 * <ul>
 *   <li>{@code desired} is null when the employee is no longer active locally
 *       (DEACTIVATE, or FLAG on a locally-absent record).
 *   <li>{@code current} is null when no mapping or remote user exists yet (CREATE).
 * </ul>
 *
 * <p>When {@code action} is {@code FLAG}, the {@code issues} list will be non-empty
 * and the Resolver should produce a {@code FlagUser} command rather than
 * attempting any API operation.
 *
 * <p>When {@code action} is {@code SKIP}, the Resolver may still produce a bookkeeping
 * command so the executor can report the record as skipped.
 *
 * @param action  The classified action to take for this employee.
 * @param desired The desired end-state for this employee. Null for DEACTIVATE.
 * @param remote  The current known state from mapping + remote snapshot. Null for CREATE.
 * @param issues  Any anomalies detected during planning. Empty for clean records.
 */
public record PlannedAction(
        SyncAction action,
        DesiredUser desired,
        RemoteUser remote,
        List<SyncIssue> issues
) {
    public PlannedAction {
        if (desired == null && remote == null) {
            throw new IllegalArgumentException(
                    "PlannedAction must have at least one of desired or current"
            );
        }
        issues = issues != null ? List.copyOf(issues) : List.of();
    }

    /**
     * Fetches the mapping for invariants where it is expected to not be null. i.e. action=UPDATE.
     *
     * @return
     */
    public EverfiEmployeeMapping requireRemoteMapping() {
        if (remote == null || remote.mapping() == null) {
            throw new IllegalStateException("Expected planned action with mapped remote user: " + action);
        }
        return remote.mapping();
    }

    public DesiredUser requireDesired() {
        if (desired == null) {
            throw new IllegalStateException("Expected planned action with desired user: " + action);
        }
        return desired;
    }

    public RemoteUser requireRemote() {
        if (remote == null) {
            throw new IllegalStateException("Expected planned action with remote user: " + action);
        }
        return remote;
    }
}
