package gov.nysenate.ess.travel.request.route;

import gov.nysenate.ess.core.dao.base.*;
import gov.nysenate.ess.travel.request.route.destination.Destination;
import gov.nysenate.ess.travel.request.route.destination.DestinationDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

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
            insertIntoJoinTable(leg, amendmentId, route.firstLegQualifiesForBreakfast(), route.lastLegQualifiesForDinner());
            sequenceNo++;
        }
        for (Leg leg : route.getReturnLegs()) {
            insertLeg(leg, false, sequenceNo);
            insertIntoJoinTable(leg, amendmentId, route.firstLegQualifiesForBreakfast(), route.lastLegQualifiesForDinner());
            sequenceNo++;
        }
    }

    private void insertLeg(Leg leg, boolean isOutbound, int sequenceNo) {
        MapSqlParameterSource params = legParams(leg, isOutbound, sequenceNo);
        String sql = SqlRouteQuery.INSERT_LEG.getSql(schemaMap());
        KeyHolder keyHolder = new GeneratedKeyHolder();
        localNamedJdbc.update(sql, params, keyHolder);
        leg.setId((Integer) keyHolder.getKeys().get("leg_id"));
    }

    private void insertIntoJoinTable(Leg leg, int amendmentId,
                                     boolean firstLegQualifiesForBreakfast, boolean lastLegQualifiesForDinner) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("amendmentId", amendmentId)
                .addValue("legId", leg.getId())
                .addValue("firstLegQualifiesForBreakfast", firstLegQualifiesForBreakfast)
                .addValue("lastLegQualifiesForDinner", lastLegQualifiesForDinner);
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
                .addValue("isOutbound", isOutbound)
                .addValue("sequenceNo", sequenceNo);
    }

    @Override
    public Route selectRoute(int amendmentId) {
        MapSqlParameterSource params = new MapSqlParameterSource("amendmentId", amendmentId);
        String routeSql = SqlRouteQuery.SELECT_ROUTE.getSql(schemaMap());
        RouteHandler handler = new RouteHandler();
        localNamedJdbc.query(routeSql, params, handler);
        Route route = handler.getRoute();
        // Populate from and to destinations
        for (Leg l : route.getAllLegs()) {
            l.setFromDestination(destinationDao.selectDestination(l.from().getId()));
            l.setToDestination(destinationDao.selectDestination(l.to().getId()));
        }
        return route;
    }

    private enum SqlRouteQuery implements BasicSqlQuery {
        INSERT_LEG(
                "INSERT INTO ${travelSchema}.leg(from_destination_id, to_destination_id, travel_date," +
                        " method_of_travel, method_of_travel_description, is_outbound, sequence_no) \n" +
                        " VALUES(:fromDestinationId, :toDestinationId, :travelDate," +
                        " :methodOfTravel, :methodOfTravelDescription, :isOutbound, :sequenceNo)"
        ),
        INSERT_JOIN_TABLE("""
                INSERT INTO ${travelSchema}.amendment_legs(amendment_id, leg_id,
                    first_leg_qualifies_for_breakfast, last_leg_qualifies_for_dinner)
                VALUES(:amendmentId, :legId, :firstLegQualifiesForBreakfast, :lastLegQualifiesForDinner)
                """
        ),
        SELECT_ROUTE("""
                SELECT first_leg_qualifies_for_breakfast, last_leg_qualifies_for_dinner,
                    leg.leg_id, leg.from_destination_id, leg.to_destination_id, leg.travel_date, leg.method_of_travel,
                    leg.method_of_travel_description, leg.is_outbound
                FROM ${travelSchema}.leg
                    INNER JOIN ${travelSchema}.amendment_legs
                    ON amendment_legs.leg_id = leg.leg_id
                WHERE amendment_id = :amendmentId
                ORDER BY sequence_no ASC\s
                """
        );

        private final String sql;

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

        private LegMapper legMapper = new LegMapper();
        private List<Leg> outboundLegs = new ArrayList<>();
        private List<Leg> returnLegs = new ArrayList<>();
        private Boolean firstLegQualifiesForBreakfast;
        private Boolean lastLegQualifiesForDinner;

        @Override
        public void processRow(ResultSet rs) throws SQLException {
            Leg leg = legMapper.mapRow(rs, rs.getRow());
            if (isOutboundLeg(rs)) {
                outboundLegs.add(leg);
            } else {
                returnLegs.add(leg);
            }
            if (firstLegQualifiesForBreakfast == null) {
                firstLegQualifiesForBreakfast = rs.getBoolean("first_leg_qualifies_for_breakfast");
            }
            if (lastLegQualifiesForDinner == null) {
                lastLegQualifiesForDinner = rs.getBoolean("last_leg_qualifies_for_dinner");
            }
        }

        private boolean isOutboundLeg(ResultSet rs) throws SQLException {
            return rs.getBoolean("is_outbound");
        }

        Route getRoute() {
            return new Route(outboundLegs, returnLegs, firstLegQualifiesForBreakfast, lastLegQualifiesForDinner);
        }
    }

    private static class LegMapper extends BaseRowMapper<Leg> {

        @Override
        public Leg mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Leg(
                    rs.getInt("leg_id"),
                    new Destination(rs.getInt("from_destination_id")),
                    new Destination(rs.getInt("to_destination_id")),
                    new ModeOfTransportation(MethodOfTravel.of(rs.getString("method_of_travel")), rs.getString("method_of_travel_description")),
                    rs.getBoolean("is_outbound"),
                    getLocalDate(rs, "travel_date"));
        }
    }
}
