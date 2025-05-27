package gov.nysenate.ess.travel.request.address;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TravelAddressRowMapper implements RowMapper<TravelAddress> {

    @Override
    public TravelAddress mapRow(ResultSet rs, int i) throws SQLException {
        return mapRow(rs, i, "");
    }

    public TravelAddress mapRow(ResultSet rs, int i, String prefix) throws SQLException {
        return new TravelAddress.Builder()
                .withId(rs.getInt(prefix + "address_id"))
                .withPlaceId(rs.getString(prefix + "place_id"))
                .withName(rs.getString(prefix + "name"))
                .withAddr1(rs.getString(prefix + "street_1"))
                .withCity(rs.getString(prefix + "city"))
                .withCounty(rs.getString(prefix + "county"))
                .withZip5(rs.getString(prefix + "zip_5"))
                .withState(rs.getString(prefix + "state"))
                .withCountry(rs.getString(prefix + "country"))
                .build();
    }
}
