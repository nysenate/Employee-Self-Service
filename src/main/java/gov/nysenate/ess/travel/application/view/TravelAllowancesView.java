package gov.nysenate.ess.travel.application.view;

import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.travel.allowance.gsa.GsaAllowanceView;
import gov.nysenate.ess.travel.application.model.TravelAllowances;

public class TravelAllowancesView implements ViewObject {

    private GsaAllowanceView gsa;
    private String mileage;
    private String tolls;
    private String parking;
    private String alternate;
    private String registrationFee;

    private TravelAllowancesView() {
    }

    public TravelAllowancesView(TravelAllowances allowances) {
       this.gsa = new GsaAllowanceView(allowances.getGsaAllowance());
       this.mileage = allowances.getMileage().toString();
       this.tolls = allowances.getTolls().toString();
       this.parking = allowances.getParking().toString();
       this.alternate = allowances.getAlternate().toString();
       this.registrationFee = allowances.getRegistrationFee().toString();
    }

    public TravelAllowances toTravelAllowances() {
        return new TravelAllowances(gsa.toGsaAllowance(), mileage,
                tolls, parking, alternate, registrationFee);
    }

    public GsaAllowanceView getGsa() {
        return gsa;
    }

    public String getMileage() {
        return mileage;
    }

    public String getTolls() {
        return tolls;
    }

    public String getParking() {
        return parking;
    }

    public String getAlternate() {
        return alternate;
    }

    public String getRegistrationFee() {
        return registrationFee;
    }

    @Override
    public String getViewType() {
        return "travel-app-allowances";
    }
}
