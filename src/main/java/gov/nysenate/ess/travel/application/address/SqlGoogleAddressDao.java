package gov.nysenate.ess.travel.application.address;

import gov.nysenate.ess.core.dao.base.BasicSqlQuery;
import gov.nysenate.ess.core.dao.base.DbVendor;
import gov.nysenate.ess.core.dao.base.SqlBaseDao;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class SqlGoogleAddressDao extends SqlBaseDao {

    public GoogleAddress selectGoogleAddress(int googleAddressId) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("googleAddressId", googleAddressId);
        String sql = SqlGoogleAddressQuery.SELECT_ADDRESS.getSql(schemaMap());
        List<GoogleAddress> googleAddressList = localNamedJdbc.query(sql, params, new GoogleAddressRowMapper());
        if (googleAddressList.isEmpty() || googleAddressList == null) {
            return null;
        }
        else {
            return googleAddressList.get(0);
        }
    }

    /**
     * Save a GoogleAddress.
     * If this address does not yet exist in the database it will be inserted and its id will
     * be updated with the generated id from the database.
     * If this address already exists in the database its id will be set to the matching address's id.
     *
     * @param address
     */
    public void saveGoogleAddress(GoogleAddress address) {
        try {
            GoogleAddress savedAddr = selectMatchingAddress(address);
            // Make sure the id is set so its available when inserting the destination.
            address.setId(savedAddr.getId());
        } catch (IncorrectResultSizeDataAccessException ex) {
            // A record did not exist, lets insert it.
            insertGoogleAddress(address);
        }
    }

    private GoogleAddress selectMatchingAddress(GoogleAddress address) {
        String sql = SqlGoogleAddressQuery.SELECT_MATCHING_ADDRESS.getSql(schemaMap());
        List<GoogleAddress> googleAddressList = localNamedJdbc.query(sql, googleAddressParams(address), new GoogleAddressRowMapper());
        if (googleAddressList.isEmpty()) {
            throw new IncorrectResultSizeDataAccessException(0);
        }
        else {
            return googleAddressList.get(0);
        }
    }

    // Attempts to insert the address. Returns the number of rows inserted. Updates the GoogleAddress with the auto generated database id.
    private int insertGoogleAddress(GoogleAddress address) {
        String sql = SqlGoogleAddressQuery.INSERT_ADDRESS.getSql(schemaMap());
        KeyHolder keyHolder = new GeneratedKeyHolder();
        int insertCount = localNamedJdbc.update(sql, googleAddressParams(address), keyHolder);
        address.setId((Integer) keyHolder.getKeys().get("google_address_id"));
        return insertCount;
    }

    private MapSqlParameterSource googleAddressParams(GoogleAddress address) {
        return new MapSqlParameterSource()
                .addValue("googleAddressId", address.getId())
                .addValue("street1", address.getAddr1())
                .addValue("street2", address.getAddr2())
                .addValue("city", address.getCity())
                .addValue("state", address.getState())
                .addValue("zip5", address.getZip5())
                .addValue("zip4", address.getZip4())
                .addValue("county", address.getCounty())
                .addValue("country", address.getCountry())
                .addValue("placeId", address.getPlaceId())
                .addValue("name", address.getName())
                .addValue("formattedAddress", address.getFormattedAddress());
    }

    private enum SqlGoogleAddressQuery implements BasicSqlQuery {
        SELECT_ADDRESS(
                "SELECT * from ${travelSchema}.google_address\n" +
                        "WHERE google_address_id = :googleAddressId"
        ),
        INSERT_ADDRESS(
                "INSERT INTO ${travelSchema}.google_address(street_1, street_2, city, state,\n" +
                        "zip_5, zip_4, county, country, place_id, name, formatted_address)\n" +
                        "VALUES(:street1, :street2, :city, :state, :zip5, :zip4,\n" +
                        ":county, :country, :placeId, :name, :formattedAddress)"
        ),
        SELECT_MATCHING_ADDRESS(
                "SELECT * from ${travelSchema}.google_address\n" +
                        "WHERE street_1 = :street1\n" +
                        "AND street_2 = :street2\n" +
                        "AND city = :city\n" +
                        "AND state = :state\n" +
                        "AND zip_5 = :zip5\n" +
                        "AND zip_4 = :zip4\n" +
                        "AND county = :county\n" +
                        "AND country = :country\n" +
                        "AND place_id = :placeId"
        );

        private final String sql;

        SqlGoogleAddressQuery(String sql) {
            this.sql = sql;
        }

        @Override
        public String getSql() {
            return this.sql;
        }

        @Override
        public DbVendor getVendor() {
            return DbVendor.POSTGRES;
        }
    }
}
