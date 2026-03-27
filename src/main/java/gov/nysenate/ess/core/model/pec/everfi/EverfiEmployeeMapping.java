package gov.nysenate.ess.core.model.pec.everfi;

import org.jetbrains.annotations.NotNull;
import org.springframework.util.Assert;

/**
 * Maps an employee's id to their associated Everfi UUID.
 *
 * @param employeeId
 * @param everfiUuid
 */
public record EverfiEmployeeMapping(int employeeId, @NotNull String everfiUuid) {

    public EverfiEmployeeMapping {
        Assert.hasText(everfiUuid, "everfiUuid must not be empty");
    }
}
