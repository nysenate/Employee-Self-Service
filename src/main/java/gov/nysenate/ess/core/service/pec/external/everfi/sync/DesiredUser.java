package gov.nysenate.ess.core.service.pec.external.everfi.sync;

import lombok.Builder;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Locale;

/**
 * The intended end-state for an employee in Everfi, derived from local employee data.
 * Email is lowercased and trimmed; names are trimmed. These normalizations are what let
 * downstream comparisons (e.g. {@link RemoteUser#isStale}) be plain {@code Objects.equals}.
 */
@Builder
public record DesiredUser(
        int employeeId,
        @Nullable String email,
        @Nullable String firstName,
        @Nullable String lastName,
        List<DesiredLabel> desiredLabels
) {

    public DesiredUser {
        email = normalizeEmail(email);
        firstName = trimToNull(firstName);
        lastName = trimToNull(lastName);
        desiredLabels = desiredLabels != null ? List.copyOf(desiredLabels) : List.of();
    }

    private static String trimToNull(String value) {
        return value == null || value.trim().isEmpty() ? null : value.trim();
    }

    private static String normalizeEmail(String email) {
        String trimmedEmail = trimToNull(email);
        return trimmedEmail != null ? trimmedEmail.toLowerCase(Locale.ROOT) : null;
    }
}
