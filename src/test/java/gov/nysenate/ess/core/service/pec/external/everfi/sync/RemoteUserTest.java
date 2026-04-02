package gov.nysenate.ess.core.service.pec.external.everfi.sync;

import de.bechte.junit.runners.context.HierarchicalContextRunner;
import gov.nysenate.ess.core.annotation.UnitTest;
import gov.nysenate.ess.core.model.pec.everfi.EverfiEmployeeMapping;
import gov.nysenate.ess.core.service.pec.external.everfi.category.EverfiCategoryLabel;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(HierarchicalContextRunner.class)
@Category(UnitTest.class)
public class RemoteUserTest {

    @Test
    public void matchingDesiredUser_isNotStale() {
        DesiredUser desiredUser = desiredUser().build();

        assertThat(remoteUser().build().isStale(desiredUser)).isFalse();
    }

    @Test
    public void nullDesiredUser_isStale() {
        assertThat(remoteUser().build().isStale(null)).isTrue();
    }

    @Test
    public void nullRemoteFields_doNotThrowAndAreNotStaleWhenDesiredAlsoNull() {
        RemoteUser remoteUser = remoteUser()
                .remoteFirstName(null)
                .remoteLastName(null)
                .build();
        DesiredUser desiredUser = desiredUser()
                .firstName(null)
                .lastName(null)
                .build();

        assertThat(remoteUser.isStale(desiredUser)).isFalse();
    }

    @Test
    public void inactiveRemoteUser_isStale() {
        assertThat(remoteUser().remoteActive(false).build().isStale(desiredUser().build())).isTrue();
    }

    @Test
    public void extraDepartmentLabel_isStale() {
        EverfiCategoryLabel currentDepartment = label(100, "Department", "HR");
        EverfiCategoryLabel staleDepartment = label(101, "Department", "Finance");
        DesiredUser desiredUser = desiredUser()
                .categoryLabels(List.of(currentDepartment))
                .build();
        RemoteUser remoteUser = remoteUser()
                .categoryLabels(List.of(currentDepartment, staleDepartment))
                .build();

        assertThat(remoteUser.isStale(desiredUser)).isTrue();
    }

    @Test
    public void extraUnmanagedCategoryLabel_isNotStale() {
        EverfiCategoryLabel currentDepartment = label(100, "Department", "HR");
        EverfiCategoryLabel customLabel = label(200, "Custom Category", "Added Elsewhere");
        DesiredUser desiredUser = desiredUser()
                .categoryLabels(List.of(currentDepartment))
                .build();
        RemoteUser remoteUser = remoteUser()
                .categoryLabels(List.of(currentDepartment, customLabel))
                .build();

        assertThat(remoteUser.isStale(desiredUser)).isFalse();
    }

    private DesiredUser.DesiredUserBuilder desiredUser() {
        return DesiredUser.builder()
                .employeeId(1)
                .email("example@nysenate.gov")
                .firstName("John")
                .lastName("Doe");
    }

    private RemoteUser.RemoteUserBuilder remoteUser() {
        return RemoteUser.builder()
                .mapping(new EverfiEmployeeMapping(1, "everfi-uuid-1"))
                .remoteUuid("everfi-uuid-1")
                .remoteEmployeeId(1)
                .remoteActive(true)
                .remoteFirstName("John")
                .remoteLastName("Doe")
                .remoteEmail("example@nysenate.gov");
    }

    private static EverfiCategoryLabel label(int labelId, String categoryName, String labelName) {
        EverfiCategoryLabel label = new EverfiCategoryLabel(labelId, labelName);
        label.setCategoryName(categoryName);
        return label;
    }
}
