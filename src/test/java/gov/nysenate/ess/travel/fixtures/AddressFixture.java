package gov.nysenate.ess.travel.fixtures;

import gov.nysenate.ess.core.model.unit.Address;

public class AddressFixture {

    public static Address albany() {
        Address addr = new Address();
        addr.setAddr1("100 State Street");
        addr.setCity("Albany");
        addr.setState("New York");
        addr.setZip5("12222");
        return addr;
    }

    public static Address zip10008() {
        Address addr = new Address();
        addr.setZip5("10008");
        return addr;
    }
}
