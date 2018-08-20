package gov.nysenate.ess.travel.application.allowances.lodging;

import gov.nysenate.ess.core.dao.base.BaseHandler;
import gov.nysenate.ess.core.dao.base.BasicSqlQuery;
import gov.nysenate.ess.core.dao.base.DbVendor;
import gov.nysenate.ess.core.dao.base.SqlBaseDao;
import gov.nysenate.ess.travel.application.address.TravelAddress;
import gov.nysenate.ess.travel.utils.Dollars;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
public class SqlLodgingAllowanceDao extends SqlBaseDao implements LodgingAllowanceDao {

    @Override
    @Transactional(value = "localTxManager")
    public void insertLodgingAllowances(UUID versionId, LodgingAllowances lodgingAllowances) {
        List<SqlParameterSource> paramList = new ArrayList<>();
        for (LodgingAllowance allowance : lodgingAllowances.getLodgingAllowances()) {
            paramList.add(lodgingAllowanceParams(versionId, allowance));
        }
        String sql = SqlLodgingAllowanceQuery.INSERT_LODGING_ALLOWANCE.getSql(schemaMap());
        SqlParameterSource[] batchParams = new SqlParameterSource[paramList.size()];
        batchParams = paramList.toArray(batchParams);
        localNamedJdbc.batchUpdate(sql, batchParams);
    }

    @Override
    public LodgingAllowances getLodgingAllowances(UUID versionId) {
        MapSqlParameterSource params = new MapSqlParameterSource("versionId", versionId.toString());
        String sql = SqlLodgingAllowanceQuery.SELECT_LODGING_ALLOWANCES.getSql(schemaMap());
        LodgingAllowancesHandler handler = new LodgingAllowancesHandler();
        localNamedJdbc.query(sql, params, handler);
        return handler.getResults();
    }

    private MapSqlParameterSource lodgingAllowanceParams(UUID versionId, LodgingAllowance allowance) {
        return new MapSqlParameterSource()
                .addValue("id", allowance.getId().toString())
                .addValue("versionId", versionId.toString())
                .addValue("addressId", allowance.getAddress().getId().toString())
                .addValue("date", toDate(allowance.getDate()))
                .addValue("lodgingRate", allowance.getLodgingRate().toString())
                .addValue("isLodgingRequested", allowance.isLodgingRequested());
    }


    private enum SqlLodgingAllowanceQuery implements BasicSqlQuery {
        INSERT_LODGING_ALLOWANCE(
                "INSERT INTO ${travelSchema}.app_lodging_allowance(id, version_id, address_id, " +
                        "date, lodging_rate, is_lodging_requested) \n" +
                        "VALUES (:id::uuid, :versionId::uuid, :addressId::uuid, :date, :lodgingRate, :isLodgingRequested)"
        ),
        SELECT_LODGING_ALLOWANCES(
                "SELECT l.id, l.date, l.lodging_rate, l.is_lodging_requested,\n" +
                        "  addr.id as addr_id, addr.street_1 as addr_street_1, addr.street_2 as addr_street_2,\n" +
                        "  addr.city as addr_city, addr.county as addr_county, addr.state as addr_state,\n" +
                        "  addr.zip_5 as addr_zip_5, addr.zip_4 as addr_zip_4\n" +
                        "FROM ${travelSchema}.app_lodging_allowance l\n" +
                        "INNER JOIN ${travelSchema}.app_address addr on l.address_id = addr.id\n" +
                        "WHERE version_id = :versionId::uuid \n" +
                        "ORDER BY date ASC"
        )
        ;

        private String sql;

        SqlLodgingAllowanceQuery(String sql) {
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

    private class LodgingAllowancesHandler extends BaseHandler {

        private List<LodgingAllowance> allowances = new ArrayList<>();

        @Override
        public void processRow(ResultSet rs) throws SQLException {
            TravelAddress address = new TravelAddress(UUID.fromString(rs.getString("addr_id")));
            address.setAddr1(rs.getString("addr_street_1"));
            address.setAddr2(rs.getString("addr_street_2"));
            address.setCity(rs.getString("addr_city"));
            address.setCounty(rs.getString("addr_county"));
            address.setState(rs.getString("addr_state"));
            address.setZip5(rs.getString("addr_zip_5"));
            address.setZip4(rs.getString("addr_zip_4"));

            UUID id = UUID.fromString(rs.getString("id"));
            LocalDate date = getLocalDateFromRs(rs, "date");
            Dollars lodgingRate = new Dollars(rs.getString("lodging_rate"));
            boolean isLodgingRequested = rs.getBoolean("is_lodging_requested");

            allowances.add(new LodgingAllowance(id, address, date, lodgingRate, isLodgingRequested));
        }

        protected LodgingAllowances getResults() {
            return new LodgingAllowances(allowances);
        }
    }
}
