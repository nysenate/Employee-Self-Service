package gov.nysenate.ess.core.dao.transaction;

/** The options here have a mask associated with them that toggles a particular action based on the bits set.
 *
 *  Available Options: (set to 1 to enable)
 *
 *  Bit 0 : Earliest record should be the initial state.
 *  Bit 1 : Earliest record should be assigned a transaction code of APP if not already APP/RTP.
 */
public enum EmpTransDaoOption
{
    NONE                (0),
    INITIALIZE          (1),
    SET_AS_APP          (2),
    INITIALIZE_AS_APP   (3);

    int mask;

    EmpTransDaoOption(int mask) {
        this.mask = mask;
    }

    public boolean shouldInitialize() {
        return ((this.mask & 1) == 1);
    }

    public boolean shouldSetToApp() {
        return (((this.mask >> 1) & 1) == 1);
    }
}
