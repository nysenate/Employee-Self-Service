package gov.nysenate.ess.core.client.view;

import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.core.model.unit.Address;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class AddressView implements ViewObject
{
    protected String addr1 = "";
    protected String addr2 = "";
    protected String city = "";
    protected String county = "";
    protected String country = "";
    protected String state = "";
    protected String zip5 = "";
    protected String zip4 = "";
    protected String formattedAddressWithCounty = "";

    public AddressView() {}

    public AddressView(Address address) {
        this.addr1 = address.getAddr1();
        this.addr2 = address.getAddr2();
        this.city = address.getCity();
        this.state = address.getState();
        this.county = address.getCounty();
        this.country = address.getCountry();
        this.zip5 = address.getZip5();
        this.zip4 = address.getZip4();
        formattedAddressWithCounty = address.getFormattedAddressWithCounty();
    }

    public Address toAddress() {
        Address address = new Address(addr1, addr2, city, state, zip5, zip4);
        address.setCounty(county);
        address.setCountry(country);
        return address;
    }

    @Override
    @XmlElement
    public String getViewType() {
        return "address";
    }

    @XmlElement
    public String getAddr1() {
        return addr1;
    }

    @XmlElement
    public String getAddr2() {
        return addr2;
    }

    @XmlElement
    public String getCity() {
        return city;
    }

    @XmlElement
    public String getCounty() {
        return county;
    }

    @XmlElement
    public String getState() {
        return state;
    }

    @XmlElement
    public String getZip5() {
        return zip5;
    }

    @XmlElement
    public String getZip4() {
        return zip4;
    }

    @XmlElement
    public String getCountry() {
        return country;
    }

    @XmlElement
    public String getFormattedAddressWithCounty() {
        return formattedAddressWithCounty;
    }
}