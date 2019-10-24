package gov.nysenate.ess.travel.application.allowances;

import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.travel.utils.Dollars;

public class AllowancesView implements ViewObject {

    double tolls;
    double parking;
    double trainAndPlane;
    double alternateTransportation;
    double registration;

    public AllowancesView() {
    }

    public AllowancesView(Allowances allowances) {
        this.tolls = Double.valueOf(allowances.tolls().toString());
        this.parking = Double.valueOf(allowances.parking().toString());
        this.trainAndPlane = Double.valueOf(allowances.trainAndPlane().toString());
        this.alternateTransportation = Double.valueOf(allowances.alternateTransportation().toString());
        this.registration = Double.valueOf(allowances.registration().toString());
    }

    public Allowances toAllowances() {
        Allowances a = new Allowances();
        a.setTolls(new Dollars(tolls));
        a.setParking(new Dollars(parking));
        a.setTrainAndPlane(new Dollars(trainAndPlane));
        a.setAlternateTransportation(new Dollars(alternateTransportation));
        a.setRegistration(new Dollars(registration));
        return a;
    }

    public double getTolls() {
        return tolls;
    }

    public double getParking() {
        return parking;
    }

    public double getTrainAndPlane() {
        return trainAndPlane;
    }

    public double getAlternateTransportation() {
        return alternateTransportation;
    }

    public double getRegistration() {
        return registration;
    }

    @Override
    public String getViewType() {
        return "allowances";
    }
}
