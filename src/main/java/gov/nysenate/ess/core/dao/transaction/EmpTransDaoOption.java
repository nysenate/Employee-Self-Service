package gov.nysenate.ess.core.dao.transaction;

public enum EmpTransDaoOption {
    NONE                (false, false),
    INITIALIZE          (true, false),
    SET_AS_APP          (false, true),
    INITIALIZE_AS_APP   (true, true);

    private final boolean shouldInitialize, shouldSetToApp;

    EmpTransDaoOption(boolean shouldInitialize, boolean shouldSetToApp) {
        this.shouldInitialize = shouldInitialize;
        this.shouldSetToApp = shouldSetToApp;
    }

    public boolean shouldInitialize() {
        return shouldInitialize;
    }

    public boolean shouldSetToApp() {
        return shouldSetToApp;
    }
}
