package gov.nysenate.ess.core.service.pec.external.everfi.sync;

import gov.nysenate.ess.core.service.pec.external.everfi.category.EverfiCategoryLabel;
import org.jetbrains.annotations.Nullable;
import org.springframework.util.Assert;

import java.util.List;

/**
 * A {@link PlannedAction} enriched with the concrete Everfi labels needed to execute it.
 *
 * <p>The original planned action is retained so every executable action can produce the same
 * report-facing {@link SyncResult}. Label fields are populated only for actions that write labels:
 * {@code CREATE} carries desired labels plus the Upload List label, {@code UPDATE} and
 * {@code REACTIVATE} carry desired labels, and non-label-writing actions carry empty label data.
 */
record ExecutableAction(
        PlannedAction plannedAction,
        List<EverfiCategoryLabel> desiredLabels,
        @Nullable EverfiCategoryLabel uploadListLabel
) {
    ExecutableAction {
        desiredLabels = desiredLabels != null ? List.copyOf(desiredLabels) : List.of();
    }

    SyncAction action() {
        return plannedAction.action();
    }

    DesiredUser requireDesired() {
        return plannedAction.requireDesired();
    }

    RemoteUser requireRemote() {
        return plannedAction.requireRemote();
    }

    EverfiCategoryLabel requireUploadListLabel() {
        Assert.notNull(uploadListLabel, "Upload List label is required for CREATE actions");
        return uploadListLabel;
    }
}
