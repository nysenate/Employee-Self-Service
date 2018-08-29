package gov.nysenate.ess.travel.application.address;

import gov.nysenate.ess.core.dao.base.BasicSqlQuery;
import gov.nysenate.ess.core.dao.base.DbVendor;
import gov.nysenate.ess.core.dao.base.SqlBaseDao;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Repository
public class SqlTravelAddressDao extends SqlBaseDao implements TravelAddressDao {

    @Override
    @Transactional(value = "localTxManager")
    public void insertAddresses(List<TravelAddress> addresses) {
        List<SqlParameterSource> paramList = new ArrayList<>();
        for (TravelAddress address : addresses) {
            paramList.add(addressParams(address));
        }
        String sql = SqlTravelAddressQuery.INSERT_ADDRESS.getSql(schemaMap());
        SqlParameterSource[] batchParams = new SqlParameterSource[paramList.size()];
        batchParams = paramList.toArray(batchParams);
        localNamedJdbc.batchUpdate(sql, batchParams);
    }

    private MapSqlParameterSource addressParams(TravelAddress address) {
        return new MapSqlParameterSource()
                .addValue("id", address.getId().toString())
                .addValue("street1", address.getAddr1())
                .addValue("street2", address.getAddr2())
                .addValue("city", address.getCity())
                .addValue("county", address.getCounty())
                .addValue("state", address.getState())
                .addValue("zip5", address.getZip5())
                .addValue("zip4", address.getZip4());
    }

    private enum SqlTravelAddressQuery implements BasicSqlQuery {
        INSERT_ADDRESS(
                "INSERT INTO ${travelSchema}.app_address(id, street_1, street_2, city, county, state, zip_5, zip_4) \n" +
                        "VALUES (:id::uuid, :street1, :street2, :city, :county, :state, :zip5, :zip4)"
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
}
