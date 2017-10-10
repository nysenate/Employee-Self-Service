package gov.nysenate.ess.core.model.unit;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Basic model for a street address
 */
public class Address {
    protected String addr1 = "";
    protected String addr2 = "";
    protected String city = "";
    protected String state = "";
    protected String zip5 = "";
    protected String zip4 = "";

    public Address() {}

    public Address(String addr1) {
        this(addr1, "", "", "", "", "");
    }

    public Address(String addr1, String city, String state, String postal) {
        this.setAddr1(addr1);
        this.setCity(city);
        this.setState(state);
        this.setPostal(postal);
    }

    public Address(String addr1, String addr2, String city, String state, String zip5, String zip4) {
        this.setAddr1(addr1);
        this.setAddr2(addr2);
        this.setCity(city);
        this.setState(state);
        this.setZip5(zip5);
        this.setZip4(zip4);
    }

    public boolean isParsed() {
        return !(addr2.trim().isEmpty() && city.trim().isEmpty() &&
                state.trim().isEmpty() && zip5.trim().isEmpty());
    }

    public boolean isEmpty() {
        return (addr1.trim().isEmpty() && !isParsed());
    }

    @Override
    public String toString() {
        if (isParsed()) {
            return ((!addr1.isEmpty() ? addr1 : "") + (!addr2.isEmpty() ? " " + addr2 + "" : "")
                    + (!addr1.isEmpty() || !addr2.isEmpty() ? "," : "")
                    + (!city.isEmpty() ? " " + city + "," : "") + (!state.isEmpty() ? " " + state : "")
                    + (!zip5.isEmpty() ? " " + zip5 : "") + (!zip4.isEmpty() ? "-" + zip4 : "")).trim();
        }
        else {
            return addr1;
        }
    }

    /**
     * Normalization applied:
     * - Remove the dash within the building number
     *
     * @return String
     */
    public String toStringStripBuildingNumber() {
        return toString().replaceFirst("^(\\d+)(-)(\\d+)", "$1$3");
    }

    public String getAddr1() {
        return addr1;
    }

    public void setAddr1(String addr1) {
        if (addr1 != null) {
            this.addr1 = addr1;
        }
    }

    public String getAddr2() {
        return addr2;
    }

    public void setAddr2(String addr2) {
        if (addr2 != null) {
            this.addr2 = addr2;
        }
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        if (city != null) {
            this.city = city;
        }
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        if (state != null) {
            this.state = state;
        }
    }

    public String getZip5() {
        return this.zip5;
    }

    public void setZip5(String zip5) {
        if (zip5 != null && !zip5.isEmpty() && !zip5.equalsIgnoreCase("null")) {
            this.zip5 = StringUtils.leftPad(zip5, 5, "0");
        }
    }

    public String getZip4() {
        return this.zip4;
    }

    public void setZip4(String zip4) {
        if (zip4 != null && !zip4.isEmpty() && !zip5.equalsIgnoreCase("null")) {
            this.zip4 = StringUtils.leftPad(zip4, 4, "0");
        }
    }

    /**
     * Stores 12345-1234 style postal codes into zip5 and zip4 parts
     */
    public void setPostal(String postal) {
        if (postal != null) {
            ArrayList<String> zipParts = new ArrayList<>(Arrays.asList(postal.split("-")));
            this.setZip5((zipParts.size() > 0) ? zipParts.get(0).trim() : "");
            this.setZip4((zipParts.size() > 1) ? zipParts.get(1).trim() : "");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }

        Address address = (Address) o;

        if (addr1 != null ? !addr1.equals(address.addr1) : address.addr1 != null) { return false; }
        if (addr2 != null ? !addr2.equals(address.addr2) : address.addr2 != null) { return false; }
        if (city != null ? !city.equals(address.city) : address.city != null) { return false; }
        if (state != null ? !state.equals(address.state) : address.state != null) { return false; }
        if (zip5 != null ? !zip5.equals(address.zip5) : address.zip5 != null) { return false; }
        return !(zip4 != null ? !zip4.equals(address.zip4) : address.zip4 != null);

    }

    @Override
    public int hashCode() {
        int result = addr1 != null ? addr1.hashCode() : 0;
        result = 31 * result + (addr2 != null ? addr2.hashCode() : 0);
        result = 31 * result + (city != null ? city.hashCode() : 0);
        result = 31 * result + (state != null ? state.hashCode() : 0);
        result = 31 * result + (zip5 != null ? zip5.hashCode() : 0);
        result = 31 * result + (zip4 != null ? zip4.hashCode() : 0);
        return result;
    }
}
