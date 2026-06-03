package gov.nysenate.ess.core.service.pec.external.everfi.sync;

import de.bechte.junit.runners.context.HierarchicalContextRunner;
import gov.nysenate.ess.core.annotation.UnitTest;
import gov.nysenate.ess.core.service.pec.external.everfi.category.EverfiCategory;
import gov.nysenate.ess.core.service.pec.external.everfi.category.EverfiCategoryLabel;
import gov.nysenate.ess.core.service.pec.external.everfi.category.EverfiCategoryRules;
import gov.nysenate.ess.core.service.pec.external.everfi.category.EverfiCategoryService;
import gov.nysenate.ess.core.service.pec.external.everfi.category.EverfiManagedCategory;
import gov.nysenate.ess.core.service.pec.external.everfi.sync.EverfiUserSyncExecutorTestSupport.StubCategoryService;
import org.assertj.core.api.AbstractThrowableAssert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@RunWith(HierarchicalContextRunner.class)
@Category(UnitTest.class)
public class EverfiLabelProvisionerTest {

    private static final LocalDate FIXED_DATE = LocalDate.of(2026, 5, 20);
    private static final LocalDate EXISTING_UPLOAD_LIST_DATE = LocalDate.of(2026, 5, 19);
    private static final Clock FIXED_CLOCK = Clock.fixed(
            Instant.parse("2026-05-20T12:00:00Z"),
            ZoneId.of("America/New_York")
    );
    private static final Clock EXISTING_UPLOAD_LIST_CLOCK = Clock.fixed(
            Instant.parse("2026-05-19T12:00:00Z"),
            ZoneId.of("America/New_York")
    );
    private static final DesiredLabel DEPARTMENT_HR = new DesiredLabel("Department", "HR");
    private static final DesiredLabel NEW_DEPARTMENT = new DesiredLabel("Department", "NEW_DEPT");
    private static final DesiredLabel OPS_DEPARTMENT = new DesiredLabel("Department", "OPS");
    private static final DesiredLabel ROLE_EMPLOYEE = new DesiredLabel("Role", "Employee");
    private static final EverfiCategoryLabel DEPARTMENT_HR_LABEL = categoryLabel(
            100, 1, EverfiManagedCategory.DEPARTMENT, "HR");
    private static final EverfiCategoryLabel ROLE_EMPLOYEE_LABEL = categoryLabel(
            200, 2, EverfiManagedCategory.ROLE, "Employee");


    public class WhenDesiredLabelExists {

        @Test
        public void liveRun_returnsExistingLabel() {
            StubCategoryService service = stubCategoryService(null);

            ResolvedLabels result = resolveLive(service, ROLE_EMPLOYEE);

            assertDesiredLabel(result, ROLE_EMPLOYEE, ROLE_EMPLOYEE_LABEL);
            assertNoUploadList(result);
            assertNoCreateCalls(service);
        }

        @Test
        public void dryRun_returnsExistingLabel() {
            StubCategoryService service = stubCategoryService(null);

            ResolvedLabels result = resolveDryRun(service, DEPARTMENT_HR);

            assertDesiredLabel(result, DEPARTMENT_HR, DEPARTMENT_HR_LABEL);
            assertNoUploadList(result);
            assertNoCreateCalls(service);
        }
    }

    public class WhenDesiredLabelIsMissing {

        @Test
        public void liveRun_createsAndReturnsApiResponse() {
            EverfiCategoryLabel createResponse = categoryLabel(250, 1, EverfiManagedCategory.DEPARTMENT, "NEW_DEPT");
            StubCategoryService service = stubCategoryService(createResponse);

            ResolvedLabels result = resolveLive(service, NEW_DEPARTMENT);

            assertDesiredLabel(result, NEW_DEPARTMENT, createResponse);
            assertNoUploadList(result);
            assertCreatedLabels(service, createResponse);
        }

        @Test
        public void dryRun_returnsSyntheticLabelsWithCategoryMetadata() {
            StubCategoryService service = stubCategoryService(null);

            ResolvedLabels result = resolveDryRun(service, NEW_DEPARTMENT, OPS_DEPARTMENT);

            assertDryRunLabel(result.desiredLabels().get(NEW_DEPARTMENT), -1, 1, "Department");
            assertDryRunLabel(result.desiredLabels().get(OPS_DEPARTMENT), -2, 1, "Department");
            assertNoUploadList(result);
            assertNoCreateCalls(service);
        }

        @Test
        public void andApiReturnsZeroId_throwsIllegalState() {
            EverfiCategoryLabel createResponse =
                    categoryLabel(0, 1, EverfiManagedCategory.DEPARTMENT, "NEW_DEPT");
            StubCategoryService service = stubCategoryService(createResponse);

            assertThatThrownBy(() -> resolveLive(service, NEW_DEPARTMENT))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("no ID after creating");
        }

        @Test
        public void andApiFails_throwsIllegalState() {
            StubCategoryService service = failingCreateCategoryService();

            assertThatThrownBy(() -> resolveLive(service, NEW_DEPARTMENT))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Failed to create Everfi label")
                    .hasCauseInstanceOf(IOException.class);
        }

        @Test
        public void andCategoryIsMissing_throwsIllegalState() {
            StubCategoryService service = stubCategoryServiceWithoutCategories(null);

            assertThatThrownBy(() -> resolveLive(service, DEPARTMENT_HR))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("Everfi category not found for desired label: Department");
        }
    }

    public class WhenUploadListIsRequired {

        @Test
        public void andTodaysLabelExists_returnsIt() {
            StubCategoryService service = stubCategoryService(null);
            EverfiLabelProvisioner provisioner = provisioner(service, EXISTING_UPLOAD_LIST_CLOCK);

            ResolvedLabels result = provisioner.resolve(uploadListRequirements(), false);

            assertUploadListLabelName(result, EXISTING_UPLOAD_LIST_DATE);
            assertNoCreateCalls(service);
        }

        @Test
        public void andTodaysLabelIsMissing_createsAndReturnsApiResponse() {
            EverfiCategoryLabel createResponse = uploadListLabel(401, FIXED_DATE);
            StubCategoryService service = stubCategoryService(createResponse);

            ResolvedLabels result = provisioner(service).resolve(uploadListRequirements(), false);

            assertThat(result.uploadListLabel()).isSameAs(createResponse);
            assertCreatedLabels(service, createResponse);
        }

        @Test
        public void dryRunAndTodaysLabelIsMissing_returnsSyntheticLabel() {
            StubCategoryService service = stubCategoryService(null);

            ResolvedLabels result = provisioner(service).resolve(uploadListRequirements(), true);

            assertDryRunUploadListLabel(result, FIXED_DATE);
            assertNoCreateCalls(service);
        }

        @Test
        public void andApiReturnsNull_throwsLoadException() {
            StubCategoryService service = stubCategoryService(null);

            assertUploadListLoadFailure(service)
                    .hasRootCauseMessage("Everfi returned a label with no ID after creating: "
                            + EverfiCategoryRules.uploadListLabelName(FIXED_DATE));
        }

        @Test
        public void andApiReturnsZeroId_throwsLoadException() {
            EverfiCategoryLabel createResponse = uploadListLabel(0, FIXED_DATE);
            StubCategoryService service = stubCategoryService(createResponse);

            assertUploadListLoadFailure(service)
                    .hasRootCauseMessage("Everfi returned a label with no ID after creating: "
                            + EverfiCategoryRules.uploadListLabelName(FIXED_DATE));
        }

        @Test
        public void andCategoryIsMissing_throwsLoadException() {
            StubCategoryService service = stubCategoryServiceWithoutCategories(null);

            assertUploadListLoadFailure(service);
        }

        @Test
        public void dryRunAndCategoryIsMissing_throwsLoadException() {
            StubCategoryService service = stubCategoryServiceWithoutCategories(null);

            assertThatThrownBy(() -> provisioner(service).resolve(uploadListRequirements(), true))
                    .isInstanceOf(EverfiUserSyncLoadException.class)
                    .hasMessage("Failed to ensure today's Upload List label.")
                    .hasCauseInstanceOf(IllegalStateException.class);
        }
    }

    private EverfiLabelProvisioner provisioner(EverfiCategoryService service) {
        return new EverfiLabelProvisioner(service, FIXED_CLOCK);
    }

    private EverfiLabelProvisioner provisioner(EverfiCategoryService service, Clock clock) {
        return new EverfiLabelProvisioner(service, clock);
    }

    private ResolvedLabels resolveLive(EverfiCategoryService service, DesiredLabel... desiredLabels) {
        return provisioner(service).resolve(desiredLabelRequirements(desiredLabels), false);
    }

    private ResolvedLabels resolveDryRun(EverfiCategoryService service, DesiredLabel... desiredLabels) {
        return provisioner(service).resolve(desiredLabelRequirements(desiredLabels), true);
    }

    private LabelRequirements desiredLabelRequirements(DesiredLabel... desiredLabels) {
        return LabelRequirements.of(false, desiredLabels);
    }

    private LabelRequirements uploadListRequirements() {
        return LabelRequirements.of(true);
    }

    private void assertDesiredLabel(ResolvedLabels result,
                                    DesiredLabel desiredLabel,
                                    EverfiCategoryLabel expectedLabel) {
        assertThat(result.desiredLabels().get(desiredLabel)).isSameAs(expectedLabel);
    }

    private void assertNoUploadList(ResolvedLabels result) {
        assertThat(result.uploadListLabel()).isNull();
    }

    private void assertUploadListLabelName(ResolvedLabels result, LocalDate date) {
        assertThat(result.uploadListLabel().getLabelName())
                .isEqualTo(EverfiCategoryRules.uploadListLabelName(date));
    }

    private void assertDryRunUploadListLabel(ResolvedLabels result, LocalDate date) {
        assertThat(result.uploadListLabel()).isNotNull();
        assertThat(result.uploadListLabel().getLabelId()).isNegative();
        assertThat(result.uploadListLabel().getCategoryName())
                .isEqualTo(EverfiManagedCategory.UPLOAD_LIST.categoryName());
        assertThat(result.uploadListLabel().getLabelName())
                .isEqualTo(EverfiCategoryRules.uploadListLabelName(date));
    }

    private void assertNoCreateCalls(StubCategoryService service) {
        assertThat(service.createdLabels).isEmpty();
    }

    private void assertCreatedLabels(StubCategoryService service, EverfiCategoryLabel... expectedCreateCalls) {
        var labels = Arrays.stream(expectedCreateCalls)
                .map(l -> l.getCategoryName() + ":" + l.getLabelName())
                .toList();
        assertThat(service.createdLabels).containsExactlyElementsOf(labels);
    }

    private AbstractThrowableAssert<?, ? extends Throwable> assertUploadListLoadFailure(
            EverfiCategoryService service) {
        return assertThatThrownBy(() -> provisioner(service).resolve(uploadListRequirements(), false))
                .isInstanceOf(EverfiUserSyncLoadException.class)
                .hasMessage("Failed to ensure today's Upload List label.")
                .hasCauseInstanceOf(IllegalStateException.class);
    }

    private void assertDryRunLabel(EverfiCategoryLabel label,
                                   int expectedLabelId,
                                   int expectedCategoryId,
                                   String expectedCategoryName) {
        assertThat(label.getLabelId()).isEqualTo(expectedLabelId);
        assertThat(label.getCategoryId()).isEqualTo(expectedCategoryId);
        assertThat(label.getCategoryName()).isEqualTo(expectedCategoryName);
    }

    private StubCategoryService failingCreateCategoryService() {
        StubCategoryService service =
                new StubCategoryService(null) {
                    @Override
                    public EverfiCategoryLabel createLabel(EverfiCategory category, String labelName) throws IOException {
                        throw new IOException("api error");
                    }
                };
        service.initialize(defaultCategories());
        return service;
    }

    private StubCategoryService stubCategoryService(
            EverfiCategoryLabel createResponse) {
        return stubCategoryService(createResponse, defaultCategories());
    }

    private StubCategoryService stubCategoryService(
            EverfiCategoryLabel createResponse,
            List<EverfiCategory> categories) {
        StubCategoryService service =
                new StubCategoryService(createResponse);
        service.initialize(categories);
        return service;
    }

    private StubCategoryService stubCategoryServiceWithoutCategories(
            EverfiCategoryLabel createResponse) {
        return stubCategoryService(createResponse, List.of());
    }

    private List<EverfiCategory> defaultCategories() {
        return List.of(
                new EverfiCategory(1, EverfiManagedCategory.DEPARTMENT.categoryName(), List.of(
                        DEPARTMENT_HR_LABEL,
                        categoryLabel(101, 1, EverfiManagedCategory.DEPARTMENT, "Finance")
                )),
                new EverfiCategory(2, EverfiManagedCategory.ROLE.categoryName(), List.of(
                        ROLE_EMPLOYEE_LABEL,
                        categoryLabel(201, 2, EverfiManagedCategory.ROLE, "Senator")
                )),
                new EverfiCategory(3, EverfiManagedCategory.ATTENDED_LIVE.categoryName(), List.of(
                        categoryLabel(300, 3, EverfiManagedCategory.ATTENDED_LIVE, "Yes"),
                        categoryLabel(301, 3, EverfiManagedCategory.ATTENDED_LIVE, "No")
                )),
                new EverfiCategory(20, EverfiManagedCategory.UPLOAD_LIST.categoryName(), List.of(
                        uploadListLabel(400, EXISTING_UPLOAD_LIST_DATE)
                ))
        );
    }

    private static EverfiCategoryLabel uploadListLabel(int labelId, LocalDate date) {
        return categoryLabel(
                labelId,
                20,
                EverfiManagedCategory.UPLOAD_LIST,
                EverfiCategoryRules.uploadListLabelName(date)
        );
    }

    private static EverfiCategoryLabel categoryLabel(int labelId,
                                                     int categoryId,
                                                     EverfiManagedCategory category,
                                                     String labelName) {
        EverfiCategoryLabel label = new EverfiCategoryLabel(labelId, labelName);
        label.setCategoryId(categoryId);
        label.setCategoryName(category.categoryName());
        return label;
    }
}
