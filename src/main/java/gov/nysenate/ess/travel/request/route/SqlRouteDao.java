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
    public void saveRoute(Route route, int appId) {
        List<Destination> destinations = route.getAllLegs().stream()
                .flatMap(leg -> Stream.of(leg.from(), leg.to()))
                .collect(Collectors.toList());
        destinationDao.insertDestinations(destinations);

        deleteRoute(route, appId);
        insertRoute(route, appId);
    }

    private void deleteRoute(Route route, int appId) {
        MapSqlParameterSource params = new MapSqlParameterSource("appId", appId);
        String sql = SqlRouteQuery.DELETE_ROUTE.getSql(schemaMap());
        localNamedJdbc.update(sql, params);

        for (Leg leg : route.getAllLegs()) {
            params = new MapSqlParameterSource("legId", leg.getId());
            sql = SqlRouteQuery.DELETE_LEG.getSql(schemaMap());
            localNamedJdbc.update(sql, params);
        }
    }

    private void insertRoute(Route route, int appId) {
        int sequenceNo = 0;
        for (Leg leg : route.getOutboundLegs()) {
            insertLeg(leg, true, sequenceNo);
            insertIntoJoinTable(leg, appId, route.firstLegQualifiesForBreakfast(), route.lastLegQualifiesForDinner());
            sequenceNo++;
        }
        for (Leg leg : route.getReturnLegs()) {
            insertLeg(leg, false, sequenceNo);
            insertIntoJoinTable(leg, appId, route.firstLegQualifiesForBreakfast(), route.lastLegQualifiesForDinner());
            sequenceNo++;
        }
    }

    private void insertLeg(Leg leg, boolean isOutbound, int sequenceNo) {
        MapSqlParameterSource params = legParams(leg, isOutbound, sequenceNo);
        String sql = SqlRouteQuery.INSERT_LEG.getSql(schemaMap());
        KeyHolder keyHolder = new GeneratedKeyHolder();
        localNamedJdbc.update(sql, params, keyHolder);
        leg.setId((Integer) keyHolder.getKeys().get("app_route_leg_id"));
    }

    private void insertIntoJoinTable(Leg leg, int appId,
                                     boolean firstLegQualifiesForBreakfast, boolean lastLegQualifiesForDinner) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("appId", appId)
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
        MapSqlParameterSource params = new MapSqlParameterSource("appId", amendmentId);
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
        DELETE_ROUTE("""
                DELETE FROM ${travelSchema}.app_route
                WHERE app_id = :appId
                """
        ),
        DELETE_LEG("""
                DELETE FROM ${travelSchema}.app_route_leg
                WHERE app_route_leg_id = :legId
                """
        ),
        INSERT_LEG("""
                INSERT INTO ${travelSchema}.app_route_leg(from_destination_id, to_destination_id, travel_date,
                        method_of_travel, method_of_travel_description, is_outbound, sequence_no)
                        VALUES(:fromDestinationId, :toDestinationId, :travelDate,
                        :methodOfTravel, :methodOfTravelDescription, :isOutbound, :sequenceNo)
                """
        ),
        INSERT_JOIN_TABLE("""
                INSERT INTO ${travelSchema}.app_route(app_id, app_route_leg_id,
                    first_leg_qualifies_for_breakfast, last_leg_qualifies_for_dinner)
                VALUES(:appId, :legId, :firstLegQualifiesForBreakfast, :lastLegQualifiesForDinner)
                """
        ),
        SELECT_ROUTE("""
                SELECT first_leg_qualifies_for_breakfast, last_leg_qualifies_for_dinner,
                    app_route_leg.app_route_leg_id, app_route_leg.from_destination_id, app_route_leg.to_destination_id,
                    app_route_leg.travel_date, app_route_leg.method_of_travel,
                    app_route_leg.method_of_travel_description, app_route_leg.is_outbound
                FROM ${travelSchema}.app_route_leg
                    INNER JOIN ${travelSchema}.app_route
                    USING (app_route_leg_id)
                WHERE app_id = :appId
                ORDER BY sequence_no ASC
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
                    rs.getInt("app_route_leg_id"),
                    new Destination(rs.getInt("from_destination_id")),
                    new Destination(rs.getInt("to_destination_id")),
                    new ModeOfTransportation(MethodOfTravel.of(rs.getString("method_of_travel")), rs.getString("method_of_travel_description")),
                    rs.getBoolean("is_outbound"),
                    getLocalDate(rs, "travel_date"));
        }
    }
}
