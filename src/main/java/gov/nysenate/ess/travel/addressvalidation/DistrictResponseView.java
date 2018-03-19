package gov.nysenate.ess.travel.addressvalidation;

import gov.nysenate.ess.core.client.view.base.ViewObject;

public class DistrictResponseView implements ViewObject {

    private String districtName;
    private int districtNumber;

    public DistrictResponseView() {}

    public DistrictResponseView (DistrictResponse districtResponse) {
        this.districtName = districtResponse.getDistrictName();
        this.districtNumber = districtResponse.getDistrictNumber();
    }

    public String getDistrictName() {
        return districtName;
    }

    public void setDistrictName(String districtName) {
        this.districtName = districtName;
    }

    public int getDistrictNumber() {
        return districtNumber;
    }

    public void setDistrictNumber(int districtNumber) {
        this.districtNumber = districtNumber;
    }

    @Override
    public String getViewType() {
        return "District-Response-View";
    }
}
