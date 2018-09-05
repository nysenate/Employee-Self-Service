package gov.nysenate.ess.travel.application.destination;

import gov.nysenate.ess.core.dao.base.BaseHandler;
import gov.nysenate.ess.core.dao.base.BasicSqlQuery;
import gov.nysenate.ess.core.dao.base.DbVendor;
import gov.nysenate.ess.core.dao.base.SqlBaseDao;
import gov.nysenate.ess.travel.application.address.TravelAddress;
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
public class SqlDestinationDao extends SqlBaseDao implements DestinationDao {

    @Override
    @Transactional(value = "localTxManager")
    public void insertDestinations(UUID versionId, Destinations destinations) {
        List<SqlParameterSource> paramList = new ArrayList<>();
        for (int i = 0; i < destinations.getDestinations().size(); i++) {
            Destination dest = destinations.getDestinations().get(i);
            paramList.add(destinationParams(versionId, i, dest));
        }
        String sql = SqlDestinationQuery.INSERT_DESTINATION.getSql(schemaMap());
        SqlParameterSource[] batchParams = new SqlParameterSource[paramList.size()];
        batchParams = paramList.toArray(batchParams);
        localNamedJdbc.batchUpdate(sql, batchParams);
    }

    @Override
    public Destinations getDestinations(UUID versionId) {
        MapSqlParameterSource params = new MapSqlParameterSource("versionId", versionId.toString());
        String sql = SqlDestinationQuery.SELECT_DESTINATIONS.getSql(schemaMap());
        DestinationHandler handler = new DestinationHandler();
        localNamedJdbc.query(sql, params, handler);
        return handler.getResults();
    }

    private MapSqlParameterSource destinationParams(UUID versionId, int i, Destination dest) {
        return new MapSqlParameterSource()
                .addValue("id", dest.getId().toString())
                .addValue("versionId", versionId.toString())
                .addValue("addressId", dest.getAddress().getId().toString())
                .addValue("arrivalDate", toDate(dest.arrivalDate()))
                .addValue("departureDate", toDate(dest.departureDate()))
                .addValue("sequenceNo", i);
    }

    private enum SqlDestinationQuery implements BasicSqlQuery {
        INSERT_DESTINATION(
                "INSERT INTO ${travelSchema}.app_destination(id, version_id, address_id, arrival_date, " +
                        "departure_date, sequence_no) \n" +
                        "VALUES (:id::uuid, :versionId::uuid, :addressId::uuid, :arrivalDate, :departureDate, :sequenceNo)"
        ),
        SELECT_DESTINATIONS(
                "SELECT dest.id, dest.version_id, dest.arrival_date, dest.departure_date,\n" +
                        "  addr.id as addr_id, addr.street_1 as addr_street_1, addr.street_2 as addr_street_2,\n" +
                        "  addr.city as addr_city, addr.county as addr_county, addr.state as addr_state,\n" +
                        "  addr.zip_5 as addr_zip_5, addr.zip_4 as addr_zip_4\n" +
                        "FROM ${travelSchema}.app_destination dest\n" +
                        "  INNER JOIN ${travelSchema}.address addr ON dest.address_id = addr.id\n" +
                        "WHERE dest.version_id = :versionId::uuid \n" +
                        "ORDER BY dest.sequence_no ASC"
        )
        ;

        private String sql;

        SqlDestinationQuery(String sql) {
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

    private class DestinationHandler extends BaseHandler {

        private List<Destination> destinations = new ArrayList<>();

        @Override
        public void processRow(ResultSet rs) throws SQLException {
            TravelAddress addr = new TravelAddress(UUID.fromString(rs.getString("addr_id")));
            addr.setAddr1(rs.getString("addr_street_1"));
            addr.setAddr2(rs.getString("addr_street_2"));
            addr.setCity(rs.getString("addr_city"));
            addr.setCounty(rs.getString("addr_county"));
            addr.setState(rs.getString("addr_state"));
            addr.setZip5(rs.getString("addr_zip_5"));
            addr.setZip4(rs.getString("addr_zip_4"));

            LocalDate arrivalDate = getLocalDateFromRs(rs, "arrival_date");
            LocalDate departureDate = getLocalDateFromRs(rs, "departure_date");
            UUID id = UUID.fromString(rs.getString("id"));

            destinations.add(new Destination(id, addr, arrivalDate, departureDate));
        }

        protected Destinations getResults() {
            return new Destinations(destinations);
        }
    }
}
