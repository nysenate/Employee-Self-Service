package gov.nysenate.ess.core.service.pec.external.everfi.sync;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * The set of Everfi category labels that must be resolved before planned user writes can execute.
 *
 * <p>This is the only stage that intentionally filters by action type. It gathers desired labels
 * only for actions that write desired user state ({@code CREATE}, {@code UPDATE}, and
 * {@code REACTIVATE}) and separately records whether an Upload List label is needed for
 * {@code CREATE}. It does not filter the planned actions themselves; {@code SKIP}, {@code FLAG},
 * and {@code DEACTIVATE} still flow through execution/reporting unchanged.
 */
record LabelRequirements(
        Set<DesiredLabel> desiredLabels,
        boolean uploadListRequired
) {
    LabelRequirements {
        desiredLabels = Collections.unmodifiableSet(new LinkedHashSet<>(desiredLabels));
    }

    static LabelRequirements of(boolean uploadListRequired, DesiredLabel... desiredLabels) {
        return new LabelRequirements(new LinkedHashSet<>(List.of(desiredLabels)), uploadListRequired);
    }

    static LabelRequirements from(List<PlannedAction> actions) {
        Set<DesiredLabel> desiredLabels = actions.stream()
                .filter(LabelRequirements::writesDesiredLabels)
                .map(PlannedAction::requireDesired)
                .flatMap(desired -> desired.desiredLabels().stream())
                .collect(Collectors.toCollection(LinkedHashSet::new));

        boolean uploadListRequired = actions.stream()
                .anyMatch(action -> action.action() == SyncAction.CREATE);

        return new LabelRequirements(desiredLabels, uploadListRequired);
    }

    private static boolean writesDesiredLabels(PlannedAction action) {
        return switch (action.action()) {
            case CREATE, UPDATE, REACTIVATE -> true;
            case DEACTIVATE, SKIP, FLAG -> false;
        };
    }
}
