package gov.nysenate.ess.core.service.pec.external.everfi.sync;

public record EverfiUserSyncJobResult(boolean success, String message) {

    public static EverfiUserSyncJobResult success(String message) {
        return new EverfiUserSyncJobResult(true, message);
    }

    public static EverfiUserSyncJobResult error(String message) {
        return new EverfiUserSyncJobResult(false, message);
    }
}
