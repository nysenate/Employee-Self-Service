package gov.nysenate.ess.travel.application.address;

import gov.nysenate.ess.core.dao.base.BaseRowMapper;
import gov.nysenate.ess.core.dao.base.BasicSqlQuery;
import gov.nysenate.ess.core.dao.base.DbVendor;
import gov.nysenate.ess.core.dao.base.SqlBaseDao;
import gov.nysenate.ess.core.model.unit.Address;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
public class SqlTravelAddressDao extends SqlBaseDao implements TravelAddressDao {

    @Override
    @Transactional(value = "localTxManager")
    public void insertAddress(TravelAddress address) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", address.getId().toString())
                .addValue("street1", address.getAddr1())
                .addValue("street2", address.getAddr2())
                .addValue("city", address.getCity())
                .addValue("county", address.getCounty())
                .addValue("state", address.getState())
                .addValue("zip5", address.getZip5())
                .addValue("zip4", address.getZip4())
                .addValue("country", address.getCountry());
        String sql = SqlTravelAddressQuery.INSERT_ADDRESS.getSql(schemaMap());
        localNamedJdbc.update(sql, params);
    }

    @Override
    public TravelAddress selectAddress(Address address) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("street1", address.getAddr1())
                .addValue("street2", address.getAddr2())
                .addValue("city", address.getCity())
                .addValue("county", address.getCounty())
                .addValue("state", address.getState())
                .addValue("zip5", address.getZip5())
                .addValue("zip4", address.getZip4())
                .addValue("country", address.getCountry());
        String sql = SqlTravelAddressQuery.SELECT_ADDRESS.getSql(schemaMap());
        return localNamedJdbc.queryForObject(sql, params, new TravelAddressRowMapper());
    }

    @Override
    public boolean doesAddressExist(Address address) {
        try {
            selectAddress(address);
        } catch (IncorrectResultSizeDataAccessException ex) {
            return false;
        }
        return true;
    }

    private enum SqlTravelAddressQuery implements BasicSqlQuery {
        INSERT_ADDRESS(
                "INSERT INTO ${travelSchema}.address(id, street_1, street_2, city, county, state, zip_5, zip_4, country) \n" +
                        "VALUES (:id::uuid, :street1, :street2, :city, :county, :state, :zip5, :zip4, :country)"
        ),
        SELECT_ADDRESS(
                "SELECT id, street_1, street_2, city, county, state, zip_5, zip_4, country\n" +
                        "FROM ${travelSchema}.address\n" +
                        "WHERE street_1 = :street1 AND street_2 = :street2 AND city = :city\n" +
                        "AND county = :county AND state = :state AND zip_5 = :zip5 AND zip_4 = :zip4 AND country = :country"
        )
        ;

        private String sql;

        SqlTravelAddressQuery(String sql) {
            this.sql = sql;
        }

        @Override
        public String getSql() {
            return sql;
        }

        @Override
        public DbVendor getVendor() {
            return DbVendor.POSTGRES;
        }
    }

    private class TravelAddressRowMapper extends BaseRowMapper<TravelAddress> {

        @Override
        public TravelAddress mapRow(ResultSet rs, int rowNum) throws SQLException {
            UUID id = UUID.fromString(rs.getString("id"));
            TravelAddress travelAddress = new TravelAddress(id);
            travelAddress.setAddr1(rs.getString("street_1"));
            travelAddress.setAddr2(rs.getString("street_2"));
            travelAddress.setCity(rs.getString("city"));
            travelAddress.setCounty(rs.getString("county"));
            travelAddress.setState(rs.getString("state"));
            travelAddress.setZip5(rs.getString("zip_5"));
            travelAddress.setZip4(rs.getString("zip_4"));
            travelAddress.setCountry(rs.getString("country"));
            return travelAddress;
        }
    }
}
