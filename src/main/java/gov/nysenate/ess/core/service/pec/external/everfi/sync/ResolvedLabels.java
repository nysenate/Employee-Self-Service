package gov.nysenate.ess.core.service.pec.external.everfi.sync;

import gov.nysenate.ess.core.service.pec.external.everfi.category.EverfiCategoryLabel;
import org.jetbrains.annotations.Nullable;
import org.springframework.util.Assert;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A resolved label catalog for one sync run.
 *
 * <p>{@link EverfiLabelProvisioner} builds this from {@link LabelRequirements}. In live mode the
 * labels are backed by real Everfi IDs; in dry-run mode missing labels are represented by transient
 * in-memory labels. Callers use the {@code require*} methods so missing prerequisite labels fail
 * before the executor attempts a user write.
 */
record ResolvedLabels(
        Map<DesiredLabel, EverfiCategoryLabel> desiredLabels,
        @Nullable EverfiCategoryLabel uploadListLabel
) {
    ResolvedLabels {
        desiredLabels = Collections.unmodifiableMap(new LinkedHashMap<>(desiredLabels));
    }

    static ResolvedLabels empty() {
        return new ResolvedLabels(Map.of(), null);
    }

    EverfiCategoryLabel requireDesiredLabel(DesiredLabel desiredLabel) {
        EverfiCategoryLabel label = desiredLabels.get(desiredLabel);
        Assert.notNull(label, "Missing resolved Everfi label: " + desiredLabel);
        return label;
    }

    EverfiCategoryLabel requireUploadListLabel() {
        Assert.notNull(uploadListLabel, "Upload List label is required for CREATE actions");
        return uploadListLabel;
    }
}
