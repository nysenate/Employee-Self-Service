package gov.nysenate.ess.core.service.pec.external.everfi.sync;

import gov.nysenate.ess.core.annotation.UnitTest;
import gov.nysenate.ess.core.service.pec.external.everfi.category.EverfiCategory;
import gov.nysenate.ess.core.service.pec.external.everfi.category.EverfiCategoryLabel;
import gov.nysenate.ess.core.service.pec.external.everfi.category.EverfiCategoryRules;
import gov.nysenate.ess.core.service.pec.external.everfi.category.EverfiCategoryService;
import gov.nysenate.ess.core.service.pec.external.everfi.category.EverfiManagedCategory;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static gov.nysenate.ess.core.service.pec.external.everfi.sync.EverfiUserSyncExecutorTestSupport.desiredUser;
import static gov.nysenate.ess.core.service.pec.external.everfi.sync.EverfiUserSyncExecutorTestSupport.label;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Category(UnitTest.class)
public class EverfiLabelProvisionerTest {

    private static final DateTimeFormatter UPLOAD_LIST_LABEL_FORMAT = DateTimeFormatter.ofPattern("MMM d yyyy");

    @Test
    public void dryRun_resolvesExistingLabelsAndSkipsWrites() {
        var recordingService = new EverfiUserSyncExecutorTestSupport.RecordingCategoryService(null);
        EverfiCategoryLabel existingDepartment = label(100, "Department", "HR");
        recordingService.initialize(List.of(
                new EverfiCategory(1, "Department", List.of(existingDepartment)),
                new EverfiCategory(20, EverfiManagedCategory.UPLOAD_LIST.categoryName(), List.of())
        ));
        var provisioner = new EverfiLabelProvisioner(recordingService);
        PlannedAction createAction = new PlannedAction(
                SyncAction.CREATE,
                desiredUser().desiredLabels(List.of(new DesiredLabel("Department", "HR"))).build(),
                null,
                List.of());

        var result = provisioner.resolve(LabelRequirements.from(List.of(createAction)), true);

        assertThat(result.desiredLabels())
                .containsEntry(new DesiredLabel("Department", "HR"), existingDepartment);
        assertThat(result.uploadListLabel()).isNotNull();
        assertThat(result.uploadListLabel().getLabelId()).isNegative();
        assertThat(result.uploadListLabel().getCategoryName())
                .isEqualTo(EverfiManagedCategory.UPLOAD_LIST.categoryName());
        assertThat(recordingService.createdLabels).isEmpty();
    }

    @Test
    public void noActions_returnsEmptyResult() {
        var service = new EverfiUserSyncExecutorTestSupport.RecordingCategoryService(null);
        service.initialize(List.of());
        var provisioner = new EverfiLabelProvisioner(service);

        var result = provisioner.resolve(LabelRequirements.from(List.of()), false);

        assertThat(result.desiredLabels()).isEmpty();
        assertThat(result.uploadListLabel()).isNull();
    }

    @Test
    public void allLabelsFoundInSnapshot_returnsFoundLabels_noCreateCalls() {
        EverfiCategoryLabel roleEmployee = label(100, "Role", "Employee");
        var recordingService = new EverfiUserSyncExecutorTestSupport.RecordingCategoryService(null);
        recordingService.initialize(categoriesWithRole(roleEmployee));
        var provisioner = new EverfiLabelProvisioner(recordingService);
        PlannedAction action = new PlannedAction(SyncAction.UPDATE,
                desiredUser().desiredLabels(List.of(new DesiredLabel("Role", "Employee"))).build(),
                null, List.of());

        var result = provisioner.resolve(LabelRequirements.from(List.of(action)), false);

        assertThat(result.desiredLabels())
                .containsEntry(new DesiredLabel("Role", "Employee"), roleEmployee);
        assertThat(result.uploadListLabel()).isNull();
        assertThat(recordingService.createdLabels).isEmpty();
    }

    @Test
    public void missingDesiredLabel_createsIt_andReturnsInResolvedMap() {
        EverfiCategoryLabel created = label(250, "Department", "NEW_DEPT");
        var recordingService = new EverfiUserSyncExecutorTestSupport.RecordingCategoryService(created);
        recordingService.initialize(categoriesWithDepartment());
        var provisioner = new EverfiLabelProvisioner(recordingService);
        PlannedAction action = new PlannedAction(SyncAction.UPDATE,
                desiredUser().desiredLabels(List.of(new DesiredLabel("Department", "NEW_DEPT"))).build(),
                null, List.of());

        var result = provisioner.resolve(LabelRequirements.from(List.of(action)), false);

        assertThat(result.desiredLabels())
                .containsEntry(new DesiredLabel("Department", "NEW_DEPT"), created);
        assertThat(recordingService.createdLabels).containsExactly("Department:NEW_DEPT");
        assertThat(result.uploadListLabel()).isNull();
    }

    @Test
    public void dryRun_missingDesiredLabel_returnsTransientLabel_withoutCreateCall() {
        var recordingService = new EverfiUserSyncExecutorTestSupport.RecordingCategoryService(null);
        recordingService.initialize(categoriesWithDepartment());
        var provisioner = new EverfiLabelProvisioner(recordingService);
        PlannedAction action = new PlannedAction(SyncAction.UPDATE,
                desiredUser().desiredLabels(List.of(new DesiredLabel("Department", "NEW_DEPT"))).build(),
                null, List.of());

        var result = provisioner.resolve(LabelRequirements.from(List.of(action)), true);

        assertThat(result.desiredLabels()).containsKey(new DesiredLabel("Department", "NEW_DEPT"));
        assertThat(result.desiredLabels().get(new DesiredLabel("Department", "NEW_DEPT")).getLabelId()).isNegative();
        assertThat(result.desiredLabels().get(new DesiredLabel("Department", "NEW_DEPT")).getCategoryName())
                .isEqualTo("Department");
        assertThat(result.uploadListLabel()).isNull();
        assertThat(recordingService.createdLabels).isEmpty();
    }

    @Test
    public void missingDesiredLabel_withZeroIdFromApi_throwsIllegalState() {
        EverfiCategoryLabel zeroIdLabel = label(0, "Department", "NEW_DEPT");
        var service = new EverfiUserSyncExecutorTestSupport.RecordingCategoryService(zeroIdLabel);
        service.initialize(categoriesWithDepartment());
        var provisioner = new EverfiLabelProvisioner(service);
        PlannedAction action = new PlannedAction(SyncAction.UPDATE,
                desiredUser().desiredLabels(List.of(new DesiredLabel("Department", "NEW_DEPT"))).build(),
                null, List.of());

        assertThatThrownBy(() -> provisioner.resolve(LabelRequirements.from(List.of(action)), false))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("no ID after creating");
    }

    @Test
    public void missingDesiredLabel_withIOException_throwsIllegalState() {
        var failingService = new EverfiUserSyncExecutorTestSupport.RecordingCategoryService(null) {
            @Override
            public EverfiCategoryLabel createLabel(EverfiCategory category, String labelName) throws IOException {
                throw new IOException("api error");
            }
        };
        failingService.initialize(categoriesWithDepartment());
        var provisioner = new EverfiLabelProvisioner(failingService);
        PlannedAction action = new PlannedAction(SyncAction.UPDATE,
                desiredUser().desiredLabels(List.of(new DesiredLabel("Department", "NEW_DEPT"))).build(),
                null, List.of());

        assertThatThrownBy(() -> provisioner.resolve(LabelRequirements.from(List.of(action)), false))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Failed to create Everfi label")
                .hasCauseInstanceOf(IOException.class);
    }

    @Test
    public void desiredLabel_withMissingCategory_throwsIllegalArgumentException() {
        var service = new EverfiUserSyncExecutorTestSupport.RecordingCategoryService(null);
        service.initialize(List.of());
        var provisioner = new EverfiLabelProvisioner(service);
        PlannedAction action = new PlannedAction(SyncAction.UPDATE,
                desiredUser().desiredLabels(List.of(new DesiredLabel("Department", "HR"))).build(),
                null, List.of());

        assertThatThrownBy(() -> provisioner.resolve(LabelRequirements.from(List.of(action)), false))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Everfi category not found for desired label: Department");
    }

    @Test
    public void createAction_existingUploadListLabel_returnsIt() {
        LocalDate today = LocalDate.now();
        EverfiCategoryLabel todaysLabel = new EverfiCategoryLabel(
                200, today.format(UPLOAD_LIST_LABEL_FORMAT));
        var recordingService = new EverfiUserSyncExecutorTestSupport.RecordingCategoryService(null);
        recordingService.initialize(List.of(
                new EverfiCategory(20, EverfiManagedCategory.UPLOAD_LIST.categoryName(), List.of(todaysLabel))
        ));
        var provisioner = new EverfiLabelProvisioner(recordingService);
        PlannedAction action = new PlannedAction(SyncAction.CREATE, desiredUser().build(), null, List.of());

        var result = provisioner.resolve(LabelRequirements.from(List.of(action)), false);

        assertThat(result.uploadListLabel()).isSameAs(todaysLabel);
        assertThat(recordingService.createdLabels).isEmpty();
    }

    @Test
    public void createAction_missingUploadListLabel_createsAndReturnsIt() {
        EverfiCategoryLabel created = new EverfiCategoryLabel(201, "May 20 2026");
        var recordingService = new EverfiUserSyncExecutorTestSupport.RecordingCategoryService(created);
        recordingService.initialize(List.of(
                new EverfiCategory(20, EverfiManagedCategory.UPLOAD_LIST.categoryName(), List.of())
        ));
        var provisioner = new EverfiLabelProvisioner(recordingService);
        PlannedAction action = new PlannedAction(SyncAction.CREATE, desiredUser().build(), null, List.of());

        var result = provisioner.resolve(LabelRequirements.from(List.of(action)), false);

        assertThat(result.uploadListLabel()).isSameAs(created);
        assertThat(recordingService.createdLabels).hasSize(1);
    }

    @Test
    public void dryRun_createAction_missingUploadListLabel_returnsTransientLabel_withoutCreateCall() {
        var recordingService = new EverfiUserSyncExecutorTestSupport.RecordingCategoryService(null);
        recordingService.initialize(List.of(
                new EverfiCategory(20, EverfiManagedCategory.UPLOAD_LIST.categoryName(), List.of())
        ));
        var provisioner = new EverfiLabelProvisioner(recordingService);
        PlannedAction action = new PlannedAction(SyncAction.CREATE, desiredUser().build(), null, List.of());

        var result = provisioner.resolve(LabelRequirements.from(List.of(action)), true);

        assertThat(result.uploadListLabel()).isNotNull();
        assertThat(result.uploadListLabel().getLabelId()).isNegative();
        assertThat(result.uploadListLabel().getCategoryName())
                .isEqualTo(EverfiManagedCategory.UPLOAD_LIST.categoryName());
        assertThat(result.uploadListLabel().getLabelName())
                .isEqualTo(EverfiCategoryRules.uploadListLabelName(LocalDate.now()));
        assertThat(recordingService.createdLabels).isEmpty();
    }

    @Test
    public void createAction_missingUploadListLabel_withNullResponse_throwsLoadException() {
        var service = new EverfiUserSyncExecutorTestSupport.RecordingCategoryService(null);
        service.initialize(List.of(
                new EverfiCategory(20, EverfiManagedCategory.UPLOAD_LIST.categoryName(), List.of())
        ));
        var provisioner = new EverfiLabelProvisioner(service);
        PlannedAction action = new PlannedAction(SyncAction.CREATE, desiredUser().build(), null, List.of());

        assertThatThrownBy(() -> provisioner.resolve(LabelRequirements.from(List.of(action)), false))
                .isInstanceOf(EverfiUserSyncLoadException.class)
                .hasMessage("Failed to ensure today's Upload List label.")
                .hasCauseInstanceOf(IllegalStateException.class)
                .hasRootCauseMessage("Everfi returned a label with no ID after creating: "
                        + EverfiCategoryRules.uploadListLabelName(LocalDate.now()));
    }

    @Test
    public void createAction_missingUploadListLabel_withZeroIdResponse_throwsLoadException() {
        EverfiCategoryLabel zeroIdLabel = new EverfiCategoryLabel(0, "ignored");
        var service = new EverfiUserSyncExecutorTestSupport.RecordingCategoryService(zeroIdLabel);
        service.initialize(List.of(
                new EverfiCategory(20, EverfiManagedCategory.UPLOAD_LIST.categoryName(), List.of())
        ));
        var provisioner = new EverfiLabelProvisioner(service);
        PlannedAction action = new PlannedAction(SyncAction.CREATE, desiredUser().build(), null, List.of());

        assertThatThrownBy(() -> provisioner.resolve(LabelRequirements.from(List.of(action)), false))
                .isInstanceOf(EverfiUserSyncLoadException.class)
                .hasMessage("Failed to ensure today's Upload List label.")
                .hasCauseInstanceOf(IllegalStateException.class)
                .hasRootCauseMessage("Everfi returned a label with no ID after creating: "
                        + EverfiCategoryRules.uploadListLabelName(LocalDate.now()));
    }

    @Test
    public void createAction_missingUploadListCategory_throwsLoadException() {
        var service = new EverfiUserSyncExecutorTestSupport.RecordingCategoryService(null);
        service.initialize(List.of());
        var provisioner = new EverfiLabelProvisioner(service);
        PlannedAction action = new PlannedAction(SyncAction.CREATE, desiredUser().build(), null, List.of());

        assertThatThrownBy(() -> provisioner.resolve(LabelRequirements.from(List.of(action)), false))
                .isInstanceOf(EverfiUserSyncLoadException.class)
                .hasMessage("Failed to ensure today's Upload List label.")
                .hasCauseInstanceOf(IllegalStateException.class);
    }

    @Test
    public void dryRun_createAction_missingUploadListCategory_throwsLoadException() {
        var service = new EverfiUserSyncExecutorTestSupport.RecordingCategoryService(null);
        service.initialize(List.of());
        var provisioner = new EverfiLabelProvisioner(service);
        PlannedAction action = new PlannedAction(SyncAction.CREATE, desiredUser().build(), null, List.of());

        assertThatThrownBy(() -> provisioner.resolve(LabelRequirements.from(List.of(action)), true))
                .isInstanceOf(EverfiUserSyncLoadException.class)
                .hasMessage("Failed to ensure today's Upload List label.")
                .hasCauseInstanceOf(IllegalStateException.class);
    }

    @Test
    public void nonCreateActions_uploadListLabelIsNull() {
        var service = new EverfiUserSyncExecutorTestSupport.RecordingCategoryService(null);
        service.initialize(List.of());
        var provisioner = new EverfiLabelProvisioner(service);
        List<PlannedAction> actions = List.of(
                new PlannedAction(SyncAction.UPDATE, desiredUser().build(), null, List.of()),
                new PlannedAction(SyncAction.SKIP, desiredUser().build(), null, List.of())
        );

        var result = provisioner.resolve(LabelRequirements.from(actions), false);

        assertThat(result.uploadListLabel()).isNull();
    }

    private static List<EverfiCategory> categoriesWithDepartment() {
        return List.of(
                new EverfiCategory(1, "Department", List.of())
        );
    }

    private static List<EverfiCategory> categoriesWithRole(EverfiCategoryLabel roleLabel) {
        return List.of(
                new EverfiCategory(2, "Role", List.of(roleLabel))
        );
    }
}
