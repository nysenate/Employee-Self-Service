package gov.nysenate.ess.time.model.personnel;

/**
 * A sup emp group that contains the supervisor's supervisor id.
 *
 * Used in the context of {@link ExtendedSupEmpGroup}
 */
public class SecondarySupEmpGroup extends PrimarySupEmpGroup {

    private int supSupId;

    public SecondarySupEmpGroup(PrimarySupEmpGroup supEmpGroup, int supSupId) {
        super(supEmpGroup);
        this.supSupId = supSupId;
    }

    public int getSupSupId() {
        return supSupId;
    }
}
