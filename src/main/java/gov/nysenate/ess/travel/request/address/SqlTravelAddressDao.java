package gov.nysenate.ess.travel.request.address;

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
public class SqlTravelAddressDao extends SqlBaseDao {

    public TravelAddress selectAddress(int addressId) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("addressId", addressId);
        String sql = SqlTravelAddressQuery.SELECT_ADDRESS.getSql(schemaMap());
        return localNamedJdbc.queryForObject(sql, params, new TravelAddressRowMapper());
    }

    /**
     * Save a TravelAddress.
     * If this address does not yet exist in the database it will be inserted and its id will
     * be updated with the generated id from the database.
     * If this address already exists in the database its id will be set to the matching address's id.
     *
     * @param address
     */
    public void saveAddress(TravelAddress address) {
        try {
            TravelAddress savedAddr = selectMatchingAddress(address);
            // Make sure the id is set so its available when inserting the destination.
            address.setId(savedAddr.getId());
        } catch (IncorrectResultSizeDataAccessException ex) {
            // A record did not exist, lets insert it.
            insertAddress(address);
        }
    }

    private TravelAddress selectMatchingAddress(TravelAddress address) {
        String sql = SqlTravelAddressQuery.SELECT_MATCHING_ADDRESS.getSql(schemaMap());
        return localNamedJdbc.queryForObject(sql, addressParams(address), new TravelAddressRowMapper());
    }

    // Attempts to insert the address. Returns the number of rows inserted. Updates the address with the auto generated database id.
    private int insertAddress(TravelAddress address) {
        String sql = SqlTravelAddressQuery.INSERT_ADDRESS.getSql(schemaMap());
        KeyHolder keyHolder = new GeneratedKeyHolder();
        int insertCount = localNamedJdbc.update(sql, addressParams(address), keyHolder);
        address.setId((Integer) keyHolder.getKeys().get("address_id"));
        return insertCount;
    }

    private MapSqlParameterSource addressParams(TravelAddress address) {
        return new MapSqlParameterSource()
                .addValue("addressId", address.getId())
                .addValue("street1", address.getAddr1())
                .addValue("city", address.getCity())
                .addValue("state", address.getState())
                .addValue("zip5", address.getZip5())
                .addValue("county", address.getCounty())
                .addValue("country", address.getCountry())
                .addValue("placeId", address.getPlaceId())
                .addValue("name", address.getName());
    }

    private enum SqlTravelAddressQuery implements BasicSqlQuery {
        SELECT_ADDRESS(
                "SELECT * from ${travelSchema}.address\n" +
                        "WHERE address_id = :addressId"
        ),
        INSERT_ADDRESS(
                "INSERT INTO ${travelSchema}.address(street_1, city, state,\n" +
                        "zip_5, county, country, place_id, name)\n" +
                        "VALUES(:street1, :city, :state, :zip5," +
                        ":county, :country, :placeId, :name)"
        ),
        SELECT_MATCHING_ADDRESS(
                "SELECT * from ${travelSchema}.address\n" +
                        "WHERE street_1 = :street1\n" +
                        "AND city = :city\n" +
                        "AND state = :state\n" +
                        "AND zip_5 = :zip5\n" +
                        "AND county = :county\n" +
                        "AND country = :country\n" +
                        "AND place_id = :placeId"
        );

        private String sql;

        SqlTravelAddressQuery(String sql) {
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
