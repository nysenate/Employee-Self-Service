package gov.nysenate.ess.core.service.pec.external.everfi.sync;

import gov.nysenate.ess.core.annotation.UnitTest;
import gov.nysenate.ess.core.service.pec.external.everfi.category.EverfiCategoryLabel;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.List;
import java.util.Map;

import static gov.nysenate.ess.core.service.pec.external.everfi.sync.EverfiUserSyncExecutorTestSupport.desiredUser;
import static gov.nysenate.ess.core.service.pec.external.everfi.sync.EverfiUserSyncExecutorTestSupport.label;
import static gov.nysenate.ess.core.service.pec.external.everfi.sync.EverfiUserSyncExecutorTestSupport.remoteUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Category(UnitTest.class)
public class EverfiExecutableActionResolverTest {

    private final EverfiExecutableActionResolver resolver = new EverfiExecutableActionResolver();

    @Test
    public void resolve_preservesOneExecutableActionPerPlannedActionInOrder() {
        DesiredLabel role = new DesiredLabel("Role", "Employee");
        EverfiCategoryLabel roleLabel = label(100, "Role", "Employee");
        EverfiCategoryLabel uploadListLabel = label(200, "Upload List", "May 21 2026");
        PlannedAction create = new PlannedAction(
                SyncAction.CREATE,
                desiredUser().desiredLabels(List.of(role)).build(),
                null,
                List.of());
        PlannedAction skip = new PlannedAction(SyncAction.SKIP, desiredUser().employeeId(2).build(), null, List.of());
        PlannedAction flag = new PlannedAction(
                SyncAction.FLAG,
                null,
                remoteUser().build(),
                List.of(SyncIssue.UNRECOGNIZED_ACTIVE_REMOTE));

        List<ExecutableAction> executable = resolver.resolve(
                List.of(create, skip, flag),
                new ResolvedLabels(Map.of(role, roleLabel), uploadListLabel));

        assertThat(executable).hasSize(3);
        assertThat(executable.stream().map(ExecutableAction::plannedAction))
                .containsExactly(create, skip, flag);
        assertThat(executable.get(0).desiredLabels()).containsExactly(roleLabel);
        assertThat(executable.get(0).uploadListLabel()).isSameAs(uploadListLabel);
        assertThat(executable.get(1).desiredLabels()).isEmpty();
        assertThat(executable.get(2).desiredLabels()).isEmpty();
    }

    @Test
    public void resolve_failsWhenWritingActionHasUnresolvedDesiredLabel() {
        DesiredLabel role = new DesiredLabel("Role", "Employee");
        PlannedAction update = new PlannedAction(
                SyncAction.UPDATE,
                desiredUser().desiredLabels(List.of(role)).build(),
                remoteUser().build(),
                List.of());

        assertThatThrownBy(() -> resolver.resolve(List.of(update), ResolvedLabels.empty()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Missing resolved Everfi label");
    }
}
