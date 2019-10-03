package gov.nysenate.ess.travel.application.route;

import gov.nysenate.ess.core.dao.base.*;
import gov.nysenate.ess.travel.application.allowances.PerDiem;
import gov.nysenate.ess.travel.application.route.destination.Destination;
import gov.nysenate.ess.travel.application.route.destination.DestinationDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Repository
public class SqlRouteDao extends SqlBaseDao implements RouteDao {

    @Autowired private DestinationDao destinationDao;

    @Override
    @Transactional(value = "localTxManager")
    public void saveRoute(Route route, int amendmentId) {
        // FIXME leg.to and nextLeg.from should reference same db row.
        List<Destination> destinations = route.getAllLegs().stream()
                .flatMap(leg -> Stream.of(leg.from(), leg.to()))
                .collect(Collectors.toList());
        destinationDao.insertDestinations(destinations);

        int sequenceNo = 0;
        for (Leg leg : route.getOutboundLegs()) {
            insertLeg(leg, true, sequenceNo);
            insertIntoJoinTable(leg, amendmentId);
            sequenceNo++;
        }
        for (Leg leg : route.getReturnLegs()) {
            insertLeg(leg, false, sequenceNo);
            insertIntoJoinTable(leg, amendmentId);
            sequenceNo++;
        }
    }

    private void insertLeg(Leg leg, boolean isOutbound, int sequenceNo) {
        MapSqlParameterSource params = legParams(leg, isOutbound, sequenceNo);
        String sql = SqlRouteQuery.INSERT_ROUTE.getSql(schemaMap());
        KeyHolder keyHolder = new GeneratedKeyHolder();
        localNamedJdbc.update(sql, params, keyHolder);
        leg.setId((Integer) keyHolder.getKeys().get("leg_id"));
    }

    private void insertIntoJoinTable(Leg leg, int amendmentId) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("amendmentId", amendmentId)
                .addValue("legId", leg.getId());
        String sql = SqlRouteQuery.INSERT_JOIN_TABLE.getSql(schemaMap());
        localNamedJdbc.update(sql, params);
    }

    private MapSqlParameterSource legParams(Leg leg, boolean isOutbound, int sequenceNo) {
        return new MapSqlParameterSource()
                .addValue("fromDestinationId", leg.from().getId())
                .addValue("toDestinationId", leg.to().getId())
                .addValue("travelDate", toDate(leg.travelDate()))
                .addValue("methodOfTravel", leg.methodOfTravel())
                .addValue("methodOfTravelDescription", leg.methodOfTravelDescription())
                .addValue("miles", String.valueOf(leg.miles()))
                .addValue("mileageRate", leg.mileageRate().toString())
                .addValue("isOutbound", isOutbound)
                .addValue("sequenceNo", sequenceNo)
                .addValue("isReimbursementRequested", leg.isReimbursementRequested());
    }

    @Override
    public Route selectRoute(int amendmentId) {
        MapSqlParameterSource params = new MapSqlParameterSource("amendmentId", amendmentId);
        String sql = SqlRouteQuery.SELECT_LEGS_FOR_VERSION.getSql(schemaMap());
        List<Integer> appVersionLegIds = localNamedJdbc.queryForList(sql, params, Integer.class);

        if (appVersionLegIds.isEmpty()) {
            return Route.EMPTY_ROUTE;
        }

        MapSqlParameterSource routeParams = new MapSqlParameterSource("legIds", appVersionLegIds);
        String routeSql = SqlRouteQuery.SELECT_ROUTE.getSql(schemaMap());
        RouteHandler handler = new RouteHandler(destinationDao);
        localNamedJdbc.query(routeSql, routeParams, handler);
        return handler.getRoute();
    }

    private enum SqlRouteQuery implements BasicSqlQuery {
        INSERT_ROUTE(
                "INSERT INTO ${travelSchema}.leg(from_destination_id, to_destination_id, travel_date," +
                        " method_of_travel, method_of_travel_description, miles, mileage_rate, is_outbound, sequence_no, is_reimbursement_requested) \n" +
                        " VALUES(:fromDestinationId, :toDestinationId, :travelDate," +
                        " :methodOfTravel, :methodOfTravelDescription, :miles, :mileageRate, :isOutbound, :sequenceNo, :isReimbursementRequested)"
        ),
        INSERT_JOIN_TABLE(
                "INSERT INTO ${travelSchema}.amendment_legs(amendment_id, leg_id)\n" +
                        " VALUES(:amendmentId, :legId)"
        ),
        SELECT_LEGS_FOR_VERSION(
                "SELECT leg_id\n" +
                        " FROM ${travelSchema}.amendment_legs\n" +
                        " WHERE amendment_id = :amendmentId"
        ),
        SELECT_ROUTE(
                "SELECT leg_id, from_destination_id, to_destination_id, travel_date, method_of_travel," +
                        " method_of_travel_description, miles, mileage_rate, is_outbound, is_reimbursement_requested\n" +
                        " FROM ${travelSchema}.leg\n" +
                        " WHERE leg_id IN (:legIds)\n" +
                        " ORDER BY sequence_no ASC"
        );

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

        private LegMapper legMapper;
        private List<Leg> outboundLegs = new ArrayList<>();
        private List<Leg> returnLegs = new ArrayList<>();

        public RouteHandler(DestinationDao destinationDao) {
            this.legMapper = new LegMapper(destinationDao);
        }

        @Override
        public void processRow(ResultSet rs) throws SQLException {
            Leg leg = legMapper.mapRow(rs, rs.getRow());
            if (isOutboundLeg(rs)) {
                outboundLegs.add(leg);
            } else {
                returnLegs.add(leg);
            }
        }

        private boolean isOutboundLeg(ResultSet rs) throws SQLException {
            return rs.getBoolean("is_outbound");
        }

        Route getRoute() {
            return new Route(outboundLegs, returnLegs);
        }
    }

    private class LegMapper extends BaseRowMapper<Leg> {

        private DestinationDao destinationDao;

        LegMapper(DestinationDao destinationDao) {
            this.destinationDao = destinationDao;
        }

        @Override
        public Leg mapRow(ResultSet rs, int rowNum) throws SQLException {
            PerDiem perDiem = new PerDiem(getLocalDate(rs, "travel_date"),
                    new BigDecimal(rs.getString("mileage_rate")));
            return new Leg(
                    rs.getInt("leg_id"),
                    destinationDao.selectDestination(rs.getInt("from_destination_id")),
                    destinationDao.selectDestination(rs.getInt("to_destination_id")),
                    new ModeOfTransportation(MethodOfTravel.of(rs.getString("method_of_travel")), rs.getString("method_of_travel_description")),
                    Double.valueOf(rs.getString("miles")),
                    perDiem,
                    rs.getBoolean("is_outbound"));
        }
    }
}
