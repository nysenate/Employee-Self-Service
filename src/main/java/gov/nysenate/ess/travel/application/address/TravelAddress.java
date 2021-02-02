package gov.nysenate.ess.travel.application.address;


import com.google.common.base.Preconditions;

import java.util.Objects;

public class TravelAddress {

    private int id;
    private final String placeId;
    private final String name;
    private final String addr1;
    private final String city;
    private final String zip5;
    private final String county;
    private final String state;
    private final String country;

    private TravelAddress(Builder builder) {
        Preconditions.checkNotNull(builder);
        this.id = builder.id;
        this.placeId = builder.placeId;
        this.name = builder.name.trim();
        this.addr1 = builder.addr1.trim();
        this.city = builder.city.trim();
        this.zip5 = builder.zip5.trim();
        this.county = builder.county.trim();
        this.state = builder.state.trim();
        this.country = builder.country.trim();
    }

    /**
     * A formatted version of this address including county info in parenthesis.
     * Usually just the address but for some types of addresses, like establishments, it will use
     * the name of the establishment instead of street1. This is handled automatically - name is
     * taking from google and they set it to street1 or the establishment name depending on what
     * is at that location.
     */
    public String getFormattedAddressWithCounty() {
        String desc = this.toString();
        desc += getCounty().isEmpty() ? "" : " (" + getCounty() + " County)";
        desc.trim();
        return desc;
    }

    /**
     * A short summary of this address, usually the name of the establishment or street1, unless they
     * are missing in which case the city is used. If all are missing an empty string is returned.
     */
    public String getSummary() {
        return getName().isEmpty() ? (getAddr1().isEmpty() ? getCity() : getAddr1()) : name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPlaceId() {
        return placeId;
    }

    public String getName() {
        return name;
    }

    public String getAddr1() {
        return addr1;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public String getZip5() {
        return zip5;
    }

    public String getCounty() {
        return county;
    }

    public String getCountry() {
        return country;
    }

    @Override
    public String toString() {
        String desc = getName().isEmpty() ? getAddr1() : getName();
        desc += getCity().isEmpty() ? "" : ", " + getCity();
        desc += getState().isEmpty() ? "" : ", " + getState();
        desc += getZip5().isEmpty() ? "" : " " + getZip5();
        return desc;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TravelAddress that = (TravelAddress) o;
        return id == that.id && Objects.equals(placeId, that.placeId) && Objects.equals(name, that.name) && Objects.equals(addr1, that.addr1) && Objects.equals(city, that.city) && Objects.equals(zip5, that.zip5) && Objects.equals(county, that.county) && Objects.equals(state, that.state) && Objects.equals(country, that.country);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, placeId, name, addr1, city, zip5, county, state, country);
    }

    public static class Builder {
        private int id;
        private String placeId = "";
        private String name = "";
        private String addr1 = "";
        private String city = "";
        private String zip5 = "";
        private String county = "";
        private String state = "";
        private String country = "";

        public Builder() {
        }

        public Builder withId(int id) {
            this.id = id;
            return this;
        }

        public Builder withPlaceId(String placeId) {
            this.placeId = placeId == null ? "" : placeId;
            return this;
        }

        public Builder withName(String name) {
            this.name = name == null ? "" : name;
            return this;
        }

        public Builder withAddr1(String addr1) {
            this.addr1 = addr1 == null ? "" : addr1;
            return this;
        }

        public Builder withCity(String city) {
            this.city = city == null ? "" : city;
            return this;
        }

        public Builder withState(String state) {
            this.state = state == null ? "" : state;
            return this;
        }

        public Builder withZip5(String zip5) {
            this.zip5 = zip5 == null ? "" : zip5;
            return this;
        }

        public Builder withCounty(String county) {
            this.county = county == null ? "" : county;
            return this;
        }

        public Builder withCountry(String country) {
            this.country = country == null ? "" : country;
            return this;
        }

        public TravelAddress build() {
            return new TravelAddress(this);
        }
    }
}
