package gov.nysenate.ess.core.service.pec.external.everfi.sync;

import de.bechte.junit.runners.context.HierarchicalContextRunner;
import gov.nysenate.ess.core.annotation.UnitTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(HierarchicalContextRunner.class)
@Category(UnitTest.class)
public class DesiredUserTest {

    @Test
    public void blankEmail_isNormalizedToNull() {
        DesiredUser desiredUser = DesiredUser.builder()
                .employeeId(1)
                .email("   ")
                .firstName("John")
                .lastName("Doe")
                .build();

        assertThat(desiredUser.email()).isNull();
    }

    @Test
    public void nonBlankEmail_isTrimmed() {
        DesiredUser desiredUser = DesiredUser.builder()
                .employeeId(1)
                .email("  example@nysenate.gov  ")
                .firstName("John")
                .lastName("Doe")
                .build();

        assertThat(desiredUser.email()).isEqualTo("example@nysenate.gov");
    }

    @Test
    public void mixedCaseEmail_isNormalizedToLowerCase() {
        DesiredUser desiredUser = DesiredUser.builder()
                .employeeId(1)
                .email("Example@NYSENATE.GOV")
                .firstName("John")
                .lastName("Doe")
                .build();

        assertThat(desiredUser.email()).isEqualTo("example@nysenate.gov");
    }
}
