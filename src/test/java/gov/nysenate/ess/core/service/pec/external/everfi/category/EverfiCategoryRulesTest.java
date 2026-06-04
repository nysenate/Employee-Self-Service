package gov.nysenate.ess.core.service.pec.external.everfi.category;

import gov.nysenate.ess.core.annotation.UnitTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Category(UnitTest.class)
public class EverfiCategoryRulesTest {

    @Test
    public void department_trimsWhitespace() {
        assertThat(EverfiCategoryRules.normalizeLabel(EverfiManagedCategory.DEPARTMENT, "  NEW_DEPT  "))
                .isEqualTo("NEW_DEPT");
    }

    @Test
    public void department_blankReturnsNull() {
        assertThat(EverfiCategoryRules.normalizeLabel(EverfiManagedCategory.DEPARTMENT, "   "))
                .isNull();
    }

    @Test
    public void role_canonicalizesManagedValue() {
        assertThat(EverfiCategoryRules.normalizeLabel(EverfiManagedCategory.ROLE, " employee "))
                .isEqualTo("Employee");
    }

    @Test
    public void attendedLive_canonicalizesManagedValue() {
        assertThat(EverfiCategoryRules.normalizeLabel(EverfiManagedCategory.ATTENDED_LIVE, " no "))
                .isEqualTo("No");
    }

    @Test
    public void invalidManagedValueThrows() {
        assertThatThrownBy(() -> EverfiCategoryRules.normalizeLabel(EverfiManagedCategory.ROLE, "manager"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid Everfi label value 'manager' for category: Role");
    }

    @Test
    public void uploadListLabelName_usesCanonicalDateFormat() {
        assertThat(EverfiCategoryRules.uploadListLabelName(LocalDate.of(2026, 5, 19)))
                .isEqualTo("May 19 2026");
    }
}
