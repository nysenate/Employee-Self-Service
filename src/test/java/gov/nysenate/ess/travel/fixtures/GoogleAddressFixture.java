package gov.nysenate.ess.travel.fixtures;

import gov.nysenate.ess.travel.application.address.GoogleAddress;

public class GoogleAddressFixture {

    public static GoogleAddress albany() {
        GoogleAddress addr = new GoogleAddress(0, "", "Albany", "100 State Street Albany New York 12222");
        addr.setAddr1("100 State Street");
        addr.setCity("Albany");
        addr.setState("New York");
        addr.setZip5("12222");
        return addr;
    }

    public static GoogleAddress cliftonPark() {
        GoogleAddress addr = new GoogleAddress(0, "", "Clifton Park", "1 Town Hall Plaza, Clifton Park, New York 12065");
        addr.setAddr1("1 Town Hall Plaza");
        addr.setCity("Clifton Park");
        addr.setState("New York");
        addr.setZip5("12065");
        return addr;
    }

    public static GoogleAddress nyc() {
        GoogleAddress addr = new GoogleAddress(0, "", "New York", "1 Town Hall Plaza, Clifton Park, New York 12065");
        addr.setAddr1("250 Broadway");
        addr.setCity("New York");
        addr.setState("New York");
        addr.setZip5("10007");
        return addr;
    }
}
