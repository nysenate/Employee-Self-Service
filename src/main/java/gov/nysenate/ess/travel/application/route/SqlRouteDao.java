package gov.nysenate.ess.travel.application.route;

import gov.nysenate.ess.core.dao.base.*;
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
public class SqlRouteDao extends SqlBaseDao implements RouteDao {

    @Override
    @Transactional(value = "localTxManager")
    public void insertRoute(UUID versionId, Route route) {
        List<SqlParameterSource> paramList = new ArrayList<>();
        int sequenceNo = 0;
        for (Leg leg : route.getOutgoingLegs()) {
            paramList.add(legParams(leg, true, sequenceNo, versionId));
            sequenceNo++;
        }
        for (Leg leg : route.getReturnLegs()) {
            paramList.add(legParams(leg, false, sequenceNo, versionId));
            sequenceNo++;
        }
        String sql = SqlRouteQuery.INSERT_ROUTE.getSql(schemaMap());
        SqlParameterSource[] batchParams = new SqlParameterSource[paramList.size()];
        batchParams = paramList.toArray(batchParams);
        localNamedJdbc.batchUpdate(sql, batchParams);
    }

    private MapSqlParameterSource legParams(Leg leg, boolean isOutbound, int sequenceNo, UUID versionId) {
        return new MapSqlParameterSource()
                .addValue("id", leg.getId().toString())
                .addValue("versionId", versionId.toString())
                .addValue("fromAddressId", leg.getFrom().getId().toString())
                .addValue("toAddressId", leg.getTo().getId().toString())
                .addValue("methodOfTravel", leg.getModeOfTransportation().getMethodOfTravel().name())
                .addValue("methodOfTravelDescription", leg.getModeOfTransportation().getDescription())
                .addValue("travelDate", toDate(leg.getTravelDate()))
                .addValue("sequenceNo", sequenceNo)
                .addValue("isOutbound", isOutbound);
    }

    @Override
    public Route getRoute(UUID versionId) {
        MapSqlParameterSource params = new MapSqlParameterSource("versionId", versionId.toString());
        String sql = SqlRouteQuery.SELECT_ROUTE.getSql(schemaMap());
        RouteHandler handler = new RouteHandler();
        localNamedJdbc.query(sql, params, handler);
        return handler.getResults();
    }

    @Override
    public Leg getLeg(UUID legId) {
        MapSqlParameterSource params = new MapSqlParameterSource("legId", legId.toString());
        String sql = SqlRouteQuery.SELECT_LEG.getSql(schemaMap());
        return localNamedJdbc.queryForObject(sql, params, new LegRowMapper());
    }

    private enum SqlRouteQuery implements BasicSqlQuery {
        INSERT_ROUTE(
                "INSERT INTO ${travelSchema}.app_leg(id, version_id, from_address_id, to_address_id, method_of_travel, " +
                        "method_of_travel_description, travel_date, sequence_no, is_outbound) \n" +
                        "VALUES(:id::uuid, :versionId::uuid, :fromAddressId::uuid, :toAddressId::uuid, :methodOfTravel, " +
                        ":methodOfTravelDescription, :travelDate, :sequenceNo, :isOutbound)"
        ),
        LEG_COLUMNS(
                "SELECT leg.id, leg.version_id, leg.method_of_travel, leg.method_of_travel_description, leg.travel_date, leg.is_outbound,\n" +
                        "  from_addr.id as from_addr_id, from_addr.street_1 as from_addr_street_1, from_addr.street_2 as from_addr_street_2,\n" +
                        "  from_addr.city as from_addr_city, from_addr.county as from_addr_county, from_addr.state as from_addr_state,\n" +
                        "  from_addr.zip_5 as from_addr_zip_5, from_addr.zip_4 as from_addr_zip_4,\n" +
                        "  to_addr.id as to_addr_id, to_addr.street_1 as to_addr_street_1, to_addr.street_2 as to_addr_street_2,\n" +
                        "  to_addr.city as to_addr_city, to_addr.county as to_addr_county, to_addr.state as to_addr_state,\n" +
                        "  to_addr.zip_5 as to_addr_zip_5, to_addr.zip_4 as to_addr_zip_4 "
        ),
        LEG_TABLES(
                "FROM ${travelSchema}.app_leg leg \n" +
                        "INNER JOIN ${travelSchema}.app_address from_addr ON leg.from_address_id = from_addr.id \n" +
                        "INNER JOIN ${travelSchema}.app_address to_addr ON leg.to_address_id = to_addr.id \n"

        ),
        SELECT_ROUTE(
                LEG_COLUMNS.getSql() + "\n" + LEG_TABLES.getSql() + "\n" +
                        "WHERE leg.version_id = :versionId::uuid \n" +
                        "ORDER BY leg.sequence_no ASC"
        ),
        SELECT_LEG(
                LEG_COLUMNS.getSql() + "\n" + LEG_TABLES.getSql() + "\n" +
                        "WHERE leg.id = :legId::uuid"
        )
        ;

        private String sql;

        SqlRouteQuery(String sql) {
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

    private class RouteHandler extends BaseHandler {

        private List<Leg> outboundLegs = new ArrayList<>();
        private List<Leg> returnLegs= new ArrayList<>();

        @Override
        public void processRow(ResultSet rs) throws SQLException {
            TravelAddress from = new TravelAddress(UUID.fromString(rs.getString("from_addr_id")));
            from.setAddr1(rs.getString("from_addr_street_1"));
            from.setAddr2(rs.getString("from_addr_street_2"));
            from.setCity(rs.getString("from_addr_city"));
            from.setCounty(rs.getString("from_addr_county"));
            from.setState(rs.getString("from_addr_state"));
            from.setZip5(rs.getString("from_addr_zip_5"));
            from.setZip4(rs.getString("from_addr_zip_4"));

            TravelAddress to = new TravelAddress(UUID.fromString(rs.getString("to_addr_id")));
            to.setAddr1(rs.getString("to_addr_street_1"));
            to.setAddr2(rs.getString("to_addr_street_2"));
            to.setCity(rs.getString("to_addr_city"));
            to.setCounty(rs.getString("to_addr_county"));
            to.setState(rs.getString("to_addr_state"));
            to.setZip5(rs.getString("to_addr_zip_5"));
            to.setZip4(rs.getString("to_addr_zip_4"));

            UUID legId = UUID.fromString(rs.getString("id"));
            ModeOfTransportation mot = new ModeOfTransportation(MethodOfTravel.valueOf(rs.getString("method_of_travel")),
                    rs.getString("method_of_travel_description"));

            LocalDate travelDate = getLocalDateFromRs(rs, "travel_date");

            Leg leg = new Leg(legId, from, to, mot, travelDate);

            if (isOutboundLeg(rs)) {
                outboundLegs.add(leg);
            }
            else {
                returnLegs.add(leg);
            }
        }

        private boolean isOutboundLeg(ResultSet rs) throws SQLException {
            return rs.getBoolean("is_outbound");
        }

        protected Route getResults() {
            return new Route(outboundLegs, returnLegs);
        }
    }

    private class LegRowMapper extends BaseRowMapper<Leg> {

        @Override
        public Leg mapRow(ResultSet rs, int rowNum) throws SQLException {
            TravelAddress from = new TravelAddress(UUID.fromString(rs.getString("from_addr_id")));
            from.setAddr1(rs.getString("from_addr_street_1"));
            from.setAddr2(rs.getString("from_addr_street_2"));
            from.setCity(rs.getString("from_addr_city"));
            from.setCounty(rs.getString("from_addr_county"));
            from.setState(rs.getString("from_addr_state"));
            from.setZip5(rs.getString("from_addr_zip_5"));
            from.setZip4(rs.getString("from_addr_zip_4"));

            TravelAddress to = new TravelAddress(UUID.fromString(rs.getString("to_addr_id")));
            to.setAddr1(rs.getString("to_addr_street_1"));
            to.setAddr2(rs.getString("to_addr_street_2"));
            to.setCity(rs.getString("to_addr_city"));
            to.setCounty(rs.getString("to_addr_county"));
            to.setState(rs.getString("to_addr_state"));
            to.setZip5(rs.getString("to_addr_zip_5"));
            to.setZip4(rs.getString("to_addr_zip_4"));

            UUID legId = UUID.fromString(rs.getString("id"));
            ModeOfTransportation mot = new ModeOfTransportation(MethodOfTravel.valueOf(rs.getString("method_of_travel")),
                    rs.getString("method_of_travel_description"));

            LocalDate travelDate = getLocalDateFromRs(rs, "travel_date");

            return new Leg(legId, from, to, mot, travelDate);
        }
    }
}
