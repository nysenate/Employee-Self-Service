package gov.nysenate.ess.core.service.pec.external.everfi.sync;

import gov.nysenate.ess.core.service.pec.external.everfi.category.EverfiCategoryLabel;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Turns planned actions into write-ready actions after required labels have been resolved.
 *
 * <p>This resolver preserves the planner's action list exactly: one {@link PlannedAction} in,
 * one {@link ExecutableAction} out, in the same order. That invariant keeps reporting complete
 * while allowing the executor to stay mechanical and consume concrete Everfi labels instead of
 * resolving {@link DesiredLabel}s itself.
 */
@Service
class EverfiExecutableActionResolver {

    List<ExecutableAction> resolve(List<PlannedAction> actions, ResolvedLabels labels) {
        return actions.stream()
                .map(action -> resolve(action, labels))
                .toList();
    }

    private ExecutableAction resolve(PlannedAction action, ResolvedLabels labels) {
        return switch (action.action()) {
            case CREATE -> new ExecutableAction(
                    action,
                    resolveDesiredLabels(action.requireDesired(), labels),
                    labels.requireUploadListLabel()
            );
            case UPDATE, REACTIVATE -> new ExecutableAction(
                    action,
                    resolveDesiredLabels(action.requireDesired(), labels),
                    null
            );
            case DEACTIVATE, SKIP, FLAG -> new ExecutableAction(action, List.of(), null);
        };
    }

    private List<EverfiCategoryLabel> resolveDesiredLabels(DesiredUser desired, ResolvedLabels labels) {
        return desired.desiredLabels().stream()
                .map(labels::requireDesiredLabel)
                .toList();
    }
}
