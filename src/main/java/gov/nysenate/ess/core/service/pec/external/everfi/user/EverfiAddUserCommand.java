package gov.nysenate.ess.core.service.pec.external.everfi.user;

import gov.nysenate.ess.core.service.pec.external.everfi.category.EverfiCategoryLabel;
import lombok.Builder;
import org.springframework.util.Assert;

import java.util.List;

@Builder
public record EverfiAddUserCommand(
        int employeeId,
        String firstName,
        String lastName,
        String email,
        List<EverfiCategoryLabel> categoryLabels) {

    public EverfiAddUserCommand {
        Assert.isTrue(employeeId > 0, "employeeId must be greater than 0");
        Assert.hasText(firstName, "firstName must not be empty");
        Assert.hasText(lastName, "lastName must not be empty");
        Assert.notNull(email, "email must not be null");
        Assert.notNull(categoryLabels, "categoryLabels must not be null");
    }
}
