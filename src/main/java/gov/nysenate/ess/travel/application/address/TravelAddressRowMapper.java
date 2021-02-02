package gov.nysenate.ess.travel.application.address;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TravelAddressRowMapper implements RowMapper<TravelAddress> {

    @Override
    public TravelAddress mapRow(ResultSet rs, int i) throws SQLException {
        return new TravelAddress.Builder()
                .withId(rs.getInt("address_id"))
                .withPlaceId(rs.getString("place_id"))
                .withName(rs.getString("name"))
                .withAddr1(rs.getString("street_1"))
                .withCity(rs.getString("city"))
                .withCounty(rs.getString("county"))
                .withZip5(rs.getString("zip_5"))
                .withState(rs.getString("state"))
                .withCountry(rs.getString("country"))
                .build();
    }
}
