package gov.nysenate.ess.core.service.pec.external.everfi.sync;

/**
 * An intent for a category label to apply to a user in Everfi: a (categoryName, labelName) pair.
 * Has no ID — the ID is an Everfi implementation detail resolved before execution by
 * {@link EverfiLabelProvisioner}.
 */
public record DesiredLabel(String categoryName, String labelName) {}
