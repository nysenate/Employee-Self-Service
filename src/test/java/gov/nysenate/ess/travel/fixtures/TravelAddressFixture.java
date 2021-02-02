package gov.nysenate.ess.travel.fixtures;

import gov.nysenate.ess.travel.application.address.TravelAddress;

public class TravelAddressFixture {

    public static TravelAddress albany() {
        return new TravelAddress.Builder()
                .withAddr1("100 State Street")
                .withCity("Albany")
                .withState("New York")
                .withZip5("12222")
                .build();
    }

    public static TravelAddress cliftonPark() {
        return new TravelAddress.Builder()
                .withAddr1("1 Town Hall Plaza")
                .withCity("Clifton Park")
                .withState("New York")
                .withZip5("12065")
                .build();
    }

    public static TravelAddress nyc() {
        return new TravelAddress.Builder()
                .withAddr1("250 Broadway")
                .withCity("New York")
                .withState("New York")
                .withZip5("10007")
                .build();
    }

    public static TravelAddress capital() {
        return new TravelAddress.Builder()
                .withAddr1("100 State Street")
                .withCity("Albany")
                .withState("New York")
                .withZip5("12208")
                .build();
    }

    public static TravelAddress agencyBuilding() {
        return new TravelAddress.Builder()
                .withAddr1("South Mall Arterial")
                .withCity("Albany")
                .withState("New York")
                .withZip5("12210")
                .build();
    }
}
