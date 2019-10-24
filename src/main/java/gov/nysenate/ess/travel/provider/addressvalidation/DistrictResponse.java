package gov.nysenate.ess.travel.provider.addressvalidation;

public class DistrictResponse {

    private String districtName;
    private int districtNumber;

    public DistrictResponse() {}

    public DistrictResponse(String districtName, int districtNumber) {
        this.districtName = districtName;
        this.districtNumber = districtNumber;
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
}
