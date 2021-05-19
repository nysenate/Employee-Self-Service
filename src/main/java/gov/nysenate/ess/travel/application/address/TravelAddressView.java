package gov.nysenate.ess.travel.application.address;

import com.fasterxml.jackson.annotation.JsonIgnore;
import gov.nysenate.ess.core.client.view.base.ViewObject;

public class TravelAddressView implements ViewObject {

    private int id;
    private String placeId;
    private String name;
    private String addr1;
    private String city;
    private String zip5;
    private String county;
    private String state;
    private String country;
    private String summary;
    private String formattedAddressWithCounty;

    public TravelAddressView() {
    }

    public TravelAddressView(TravelAddress addr) {
        if (addr != null) {
            this.id = addr.getId();
            this.placeId = addr.getPlaceId();
            this.name = addr.getName();
            this.addr1 = addr.getAddr1();
            this.city = addr.getCity();
            this.zip5 = addr.getZip5();
            this.county = addr.getCounty();
            this.state = addr.getState();
            this.country = addr.getCountry();
            this.summary = addr.getSummary();
            this.formattedAddressWithCounty = addr.getFormattedAddressWithCounty();
        }
    }

    public TravelAddress toTravelAddress() {
        return new TravelAddress.Builder()
                .withId(id)
                .withPlaceId(placeId)
                .withName(name)
                .withAddr1(addr1)
                .withCity(city)
                .withZip5(zip5)
                .withCounty(county)
                .withState(state)
                .withCountry(country)
                .build();
    }

    public int getId() {
        return id;
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

    public String getSummary() {
        return summary;
    }

    public String getFormattedAddressWithCounty() {
        return formattedAddressWithCounty;
    }

    public String getCountry() {
        return country;
    }

    @Override
    public String getViewType() {
        return "travel-address";
    }
}
