package gov.nysenate.ess.core.service.pec.external.everfi.user;

import gov.nysenate.ess.core.service.pec.external.everfi.category.EverfiCategoryLabel;
import lombok.Builder;
import org.springframework.util.Assert;

import java.util.List;

@Builder
public record EverfiUpdateUserCommand(
        String uuid,
        int employeeId,
        String firstName,
        String lastName,
        String email,
        String ssoId,
        List<EverfiCategoryLabel> categoryLabels,
        boolean active) {

    public EverfiUpdateUserCommand {
        Assert.hasText(uuid, "uuid must not be empty");
        Assert.isTrue(employeeId > 0, "employeeId must be greater than 0");
        Assert.hasText(firstName, "firstName must not be empty");
        Assert.hasText(lastName, "lastName must not be empty");
        Assert.notNull(email, "email must not be null");
    }
}
