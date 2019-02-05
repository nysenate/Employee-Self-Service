package gov.nysenate.ess.time.client.view.personnel;

import gov.nysenate.ess.time.model.personnel.SecondarySupEmpGroup;

public class SecondarySupEmpGroupView extends PrimarySupEmpGroupView {

    private int supSupId;

    public SecondarySupEmpGroupView(SecondarySupEmpGroup seg) {
        super(seg);
        this.supSupId = seg.getSupSupId();
    }

    public int getSupSupId() {
        return supSupId;
    }
}
