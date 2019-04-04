package gov.nysenate.ess.travel.application.route.destination;

import gov.nysenate.ess.core.dao.base.BaseHandler;
import gov.nysenate.ess.core.dao.base.BasicSqlQuery;
import gov.nysenate.ess.core.dao.base.DbVendor;
import gov.nysenate.ess.core.dao.base.SqlBaseDao;
import gov.nysenate.ess.core.model.unit.Address;
import gov.nysenate.ess.travel.application.allowances.PerDiem;
import gov.nysenate.ess.travel.utils.Dollars;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

@Repository
public class SqlDestinationDao extends SqlBaseDao implements DestinationDao {

    @Override
    public void insertDestinations(Collection<Destination> destinations) {
        for (Destination dest : destinations) {
            insertDestination(dest);
        }
    }

    @Override
    public void insertDestination(Destination destination) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("arrivalDate", toDate(destination.arrivalDate()))
                .addValue("departureDate", toDate(destination.departureDate()))
                .addValue("street1", destination.getAddress().getAddr1())
                .addValue("street2", destination.getAddress().getAddr2())
                .addValue("city", destination.getAddress().getCity())
                .addValue("county", destination.getAddress().getCounty())
                .addValue("state", destination.getAddress().getState())
                .addValue("zip5", destination.getAddress().getZip5())
                .addValue("zip4", destination.getAddress().getZip4())
                .addValue("country", destination.getAddress().getCountry());
        String sql = SqlDestinationQuery.INSERT_DESTINATION.getSql(schemaMap());
        KeyHolder keyHolder = new GeneratedKeyHolder();
        localNamedJdbc.update(sql, params, keyHolder);
        destination.setId((Integer) keyHolder.getKeys().get("destination_id"));

        insertMealPerDiems(destination);
        insertLodgingPerDiems(destination);
    }

    private void insertMealPerDiems(Destination destination) {
        List<SqlParameterSource> paramList = new ArrayList<>();
        for (Map.Entry<LocalDate, PerDiem> mealPerDiems : destination.getMealPerDiems().entrySet()) {
            MapSqlParameterSource params = new MapSqlParameterSource()
                    .addValue("destinationId", destination.getId())
                    .addValue("date", toDate(mealPerDiems.getKey()))
                    .addValue("dollars", mealPerDiems.getValue().toString())
                    .addValue("isReimbursementRequested", mealPerDiems.getValue().isReimbursementRequested());
            paramList.add(params);
        }

        String sql = SqlDestinationQuery.INSERT_MEAL_PER_DIEMS.getSql(schemaMap());
        SqlParameterSource[] batchParams = new SqlParameterSource[paramList.size()];
        batchParams = paramList.toArray(batchParams);
        localNamedJdbc.batchUpdate(sql, batchParams);
    }

    private void insertLodgingPerDiems(Destination destination) {
        List<SqlParameterSource> paramList = new ArrayList<>();
        for (Map.Entry<LocalDate, PerDiem> lodgingPerDiem : destination.getLodgingPerDiems().entrySet()) {
            MapSqlParameterSource params = new MapSqlParameterSource()
                    .addValue("destinationId", destination.getId())
                    .addValue("date", toDate(lodgingPerDiem.getKey()))
                    .addValue("dollars", lodgingPerDiem.getValue().toString())
                    .addValue("isReimbursementRequested", lodgingPerDiem.getValue().isReimbursementRequested());
            paramList.add(params);
        }

        String sql = SqlDestinationQuery.INSERT_LODGING_PER_DIEMS.getSql(schemaMap());
        SqlParameterSource[] batchParams = new SqlParameterSource[paramList.size()];
        batchParams = paramList.toArray(batchParams);
        localNamedJdbc.batchUpdate(sql, batchParams);
    }

    @Override
    public Destination selectDestination(int destinationId) {
        MapSqlParameterSource params = new MapSqlParameterSource("destinationId", destinationId);
        String sql = SqlDestinationQuery.SELECT_DESTINATION.getSql(schemaMap());
        DestinationHandler handler = new DestinationHandler();
        localNamedJdbc.query(sql, params, handler);
        return handler.getDestination();
    }

    private enum SqlDestinationQuery implements BasicSqlQuery {
        INSERT_DESTINATION(
                "INSERT INTO ${travelSchema}.app_leg_destination(arrival_date, departure_date," +
                        " street_1, street_2, city, county, state, zip_5, zip_4, country)\n" +
                        " VALUES (:arrivalDate, :departureDate," +
                        " :street1, :street2, :city, :county, :state, :zip5, :zip4, :country)"
        ),
        INSERT_MEAL_PER_DIEMS(
                "INSERT INTO ${travelSchema}.app_leg_destination_meal_per_diem" +
                        " (destination_id, date, dollars, is_reimbursement_requested)\n" +
                        " VALUES (:destinationId, :date, :dollars, :isReimbursementRequested)"
        ),
        INSERT_LODGING_PER_DIEMS(
                "INSERT INTO ${travelSchema}.app_leg_destination_lodging_per_diem" +
                        " (destination_id, date, dollars, is_reimbursement_requested)\n" +
                        " VALUES (:destinationId, :date, :dollars, :isReimbursementRequested)"
        ),
        SELECT_DESTINATION(
                "SELECT dest.destination_id, dest.arrival_date, dest.departure_date," +
                        " dest.street_1, dest.street_2, dest.city, dest.county," +
                        " dest.state, dest.zip_5, dest.zip_4, dest.country, " +
                        " m.date as meal_date, m.dollars as meal_dollars, m.is_reimbursement_requested as meal_requested," +
                        " l.date as lodging_date, l.dollars as lodging_dollars, l.is_reimbursement_requested as lodging_requested\n" +
                        " FROM ${travelSchema}.app_leg_destination dest\n" +
                        "   LEFT JOIN ${travelSchema}.app_leg_destination_meal_per_diem m ON dest.destination_id = m.destination_id" +
                        "   LEFT JOIN ${travelSchema}.app_leg_destination_lodging_per_diem l ON dest.destination_id = l.destination_id" +
                        " WHERE dest.destination_id = :destinationId"
        );

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

        private int id;
        private LocalDate arrivalDate;
        private LocalDate departureDate;
        private Address address;
        private TreeMap<LocalDate, PerDiem> mealPerDiems;
        private TreeMap<LocalDate, PerDiem> lodgingPerDiems;

        DestinationHandler() {
            this.mealPerDiems = new TreeMap<>();
            this.lodgingPerDiems = new TreeMap<>();
        }

        @Override
        public void processRow(ResultSet rs) throws SQLException {
            if (id == 0) {
                id = rs.getInt("destination_id");
            }
            if (arrivalDate == null) {
                arrivalDate = getLocalDateFromRs(rs, "arrival_date");
            }
            if (departureDate == null) {
                departureDate = getLocalDateFromRs(rs, "departure_date");
            }
            if (address == null) {
                address = new Address();
                address.setAddr1(rs.getString("street_1"));
                address.setAddr2(rs.getString("street_2"));
                address.setCity(rs.getString("city"));
                address.setCounty(rs.getString("county"));
                address.setState(rs.getString("state"));
                address.setZip5(rs.getString("zip_5"));
                address.setZip4(rs.getString("zip_4"));
                address.setCountry(rs.getString("country"));
            }

            LocalDate mealDate = getLocalDate(rs, "meal_date");
            Dollars mealDollars = new Dollars(rs.getString("meal_dollars"));
            boolean isMealRequested = rs.getBoolean("meal_requested");
            if (mealDate != null) {
                mealPerDiems.put(mealDate, new PerDiem(mealDate, mealDollars, isMealRequested));
            }

            LocalDate lodgingDate = getLocalDate(rs, "lodging_date");
            Dollars lodgingDollars = new Dollars(rs.getString("lodging_dollars"));
            boolean isLodgingRequested = rs.getBoolean("lodging_requested");
            if (lodgingDate != null) {
                lodgingPerDiems.put(lodgingDate, new PerDiem(lodgingDate, lodgingDollars, isLodgingRequested));
            }
        }

        Destination getDestination() {
            return new Destination(id, address, arrivalDate, departureDate, mealPerDiems, lodgingPerDiems);
        }
    }
}
