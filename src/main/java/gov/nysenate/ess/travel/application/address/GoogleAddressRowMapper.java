package gov.nysenate.ess.travel.application.address;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class GoogleAddressRowMapper implements RowMapper<GoogleAddress> {

    @Override
    public GoogleAddress mapRow(ResultSet rs, int i) throws SQLException {
        GoogleAddress address = new GoogleAddress(rs.getInt("google_address_id"), rs.getString("place_id"), rs.getString("name"), rs.getString("formatted_address"));
        address.setAddr1(rs.getString("street_1"));
        address.setAddr2(rs.getString("street_2"));
        address.setCity(rs.getString("city"));
        address.setCounty(rs.getString("county"));
        address.setState(rs.getString("state"));
        address.setZip5(rs.getString("zip_5"));
        address.setZip4(rs.getString("zip_4"));
        address.setCountry(rs.getString("country"));
        return address;
    }
}
