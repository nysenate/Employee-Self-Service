package gov.nysenate.ess.core.service.pec.external.everfi.sync;

import gov.nysenate.ess.core.annotation.UnitTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.List;

import static gov.nysenate.ess.core.service.pec.external.everfi.sync.EverfiUserSyncExecutorTestSupport.desiredUser;
import static gov.nysenate.ess.core.service.pec.external.everfi.sync.EverfiUserSyncExecutorTestSupport.remoteUser;
import static org.assertj.core.api.Assertions.assertThat;

@Category(UnitTest.class)
public class LabelRequirementsTest {

    @Test
    public void from_includesOnlyLabelsForActionsThatWriteDesiredLabels() {
        DesiredLabel createLabel = new DesiredLabel("Department", "Finance");
        DesiredLabel updateLabel = new DesiredLabel("Role", "Employee");
        DesiredLabel reactivateLabel = new DesiredLabel("Attend Live", "Yes");
        DesiredLabel skippedLabel = new DesiredLabel("Department", "Skipped");
        DesiredLabel flaggedLabel = new DesiredLabel("Department", "Flagged");

        List<PlannedAction> actions = List.of(
                new PlannedAction(SyncAction.CREATE,
                        desiredUser().employeeId(1).desiredLabels(List.of(createLabel)).build(), null, List.of()),
                new PlannedAction(SyncAction.UPDATE,
                        desiredUser().employeeId(2).desiredLabels(List.of(updateLabel)).build(),
                        remoteUser().build(), List.of()),
                new PlannedAction(SyncAction.REACTIVATE,
                        desiredUser().employeeId(3).desiredLabels(List.of(reactivateLabel)).build(),
                        remoteUser().remoteActive(false).build(), List.of()),
                new PlannedAction(SyncAction.SKIP,
                        desiredUser().employeeId(4).desiredLabels(List.of(skippedLabel)).build(),
                        null, List.of()),
                new PlannedAction(SyncAction.FLAG,
                        desiredUser().employeeId(5).desiredLabels(List.of(flaggedLabel)).build(),
                        null, List.of(SyncIssue.UNMAPPED_REMOTE_USER)),
                new PlannedAction(SyncAction.DEACTIVATE, null, remoteUser().build(), List.of())
        );

        LabelRequirements requirements = LabelRequirements.from(actions);

        assertThat(requirements.desiredLabels())
                .containsExactly(createLabel, updateLabel, reactivateLabel);
        assertThat(requirements.uploadListRequired()).isTrue();
    }

    @Test
    public void from_requiresUploadListOnlyWhenCreateIsPlanned() {
        List<PlannedAction> actions = List.of(
                new PlannedAction(SyncAction.UPDATE, desiredUser().build(), remoteUser().build(), List.of()),
                new PlannedAction(SyncAction.SKIP, desiredUser().employeeId(2).build(), null, List.of())
        );

        LabelRequirements requirements = LabelRequirements.from(actions);

        assertThat(requirements.uploadListRequired()).isFalse();
    }
}
