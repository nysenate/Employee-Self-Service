package gov.nysenate.ess.travel.application.route.destination;

import gov.nysenate.ess.core.dao.base.BaseHandler;
import gov.nysenate.ess.core.dao.base.BasicSqlQuery;
import gov.nysenate.ess.core.dao.base.DbVendor;
import gov.nysenate.ess.core.dao.base.SqlBaseDao;
import gov.nysenate.ess.travel.application.address.GoogleAddress;
import gov.nysenate.ess.travel.application.address.GoogleAddressRowMapper;
import gov.nysenate.ess.travel.application.address.SqlGoogleAddressDao;
import gov.nysenate.ess.travel.application.allowances.PerDiem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

@Repository
public class SqlDestinationDao extends SqlBaseDao implements DestinationDao {

    private SqlGoogleAddressDao googleAddressDao;

    @Autowired
    public SqlDestinationDao(SqlGoogleAddressDao googleAddressDao) {
        this.googleAddressDao = googleAddressDao;
    }

    @Override
    public void insertDestinations(Collection<Destination> destinations) {
        for (Destination dest : destinations) {
            insertDestination(dest);
        }
    }

    @Override
    public void insertDestination(Destination destination) {
        googleAddressDao.saveGoogleAddress(destination.getAddress());
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("arrivalDate", toDate(destination.arrivalDate()))
                .addValue("departureDate", toDate(destination.departureDate()))
                .addValue("googleAddressId", destination.getAddress().getId());
        String sql = SqlDestinationQuery.INSERT_DESTINATION.getSql(schemaMap());
        KeyHolder keyHolder = new GeneratedKeyHolder();
        localNamedJdbc.update(sql, params, keyHolder);
        destination.setId((Integer) keyHolder.getKeys().get("destination_id"));

        insertMealPerDiems(destination);
        insertLodgingPerDiems(destination);
    }

    private void insertMealPerDiems(Destination destination) {
        List<SqlParameterSource> paramList = new ArrayList<>();
        for (PerDiem mealPerDiem : destination.mealPerDiems) {
            MapSqlParameterSource params = new MapSqlParameterSource()
                    .addValue("destinationId", destination.getId())
                    .addValue("date", toDate(mealPerDiem.getDate()))
                    .addValue("value", mealPerDiem.getRate().toString())
                    .addValue("isReimbursementRequested", true); // FIXME
            paramList.add(params);
        }

        String sql = SqlDestinationQuery.INSERT_MEAL_PER_DIEMS.getSql(schemaMap());
        SqlParameterSource[] batchParams = new SqlParameterSource[paramList.size()];
        batchParams = paramList.toArray(batchParams);
        localNamedJdbc.batchUpdate(sql, batchParams);
    }

    private void insertLodgingPerDiems(Destination destination) {
        List<SqlParameterSource> paramList = new ArrayList<>();
        for (PerDiem lodgingPerDiem : destination.lodgingPerDiems){
            MapSqlParameterSource params = new MapSqlParameterSource()
                    .addValue("destinationId", destination.getId())
                    .addValue("date", toDate(lodgingPerDiem.getDate()))
                    .addValue("value", lodgingPerDiem.getRate().toString())
                    .addValue("isReimbursementRequested", true); // FIXME
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
        DestinationHandler handler = new DestinationHandler(new GoogleAddressRowMapper());
        localNamedJdbc.query(sql, params, handler);
        return handler.getDestination();
    }

    private enum SqlDestinationQuery implements BasicSqlQuery {
        INSERT_DESTINATION(
                "INSERT INTO ${travelSchema}.destination(arrival_date, departure_date, google_address_id)" +
                        " VALUES (:arrivalDate, :departureDate, :googleAddressId)"
        ),
        INSERT_MEAL_PER_DIEMS(
                "INSERT INTO ${travelSchema}.destination_meal_perdiem" +
                        " (destination_id, date, value, is_reimbursement_requested)\n" +
                        " VALUES (:destinationId, :date, :value, :isReimbursementRequested)"
        ),
        INSERT_LODGING_PER_DIEMS(
                "INSERT INTO ${travelSchema}.destination_lodging_perdiem" +
                        " (destination_id, date, value, is_reimbursement_requested)\n" +
                        " VALUES (:destinationId, :date, :value, :isReimbursementRequested)"
        ),
        SELECT_DESTINATION(
                "SELECT dest.destination_id, dest.arrival_date, dest.departure_date,\n" +
                        " addr.google_address_id, addr.street_1, addr.street_2, addr.city, addr.state,\n" +
                        " addr.zip_5, addr.zip_4, addr.county, addr.country,\n" +
                        " addr.place_id, addr.name, addr.formatted_address,\n" +
                        " m.date as meal_date, m.value as meal_value, m.is_reimbursement_requested as meal_requested,\n" +
                        " l.date as lodging_date, l.value as lodging_value, l.is_reimbursement_requested as lodging_requested\n" +
                        " FROM ${travelSchema}.destination dest\n" +
                        "   LEFT JOIN ${travelSchema}.google_address addr ON addr.google_address_id = dest.google_address_id\n" +
                        "   LEFT JOIN ${travelSchema}.destination_meal_perdiem m ON dest.destination_id = m.destination_id\n" +
                        "   LEFT JOIN ${travelSchema}.destination_lodging_perdiem l ON dest.destination_id = l.destination_id\n" +
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
        private GoogleAddress address;
        private TreeMap<LocalDate, PerDiem> mealPerDiems;
        private TreeMap<LocalDate, PerDiem> lodgingPerDiems;
        private GoogleAddressRowMapper addressRowMapper;

        DestinationHandler(GoogleAddressRowMapper addressRowMapper) {
            this.mealPerDiems = new TreeMap<>();
            this.lodgingPerDiems = new TreeMap<>();
            this.addressRowMapper = addressRowMapper;
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
                address = addressRowMapper.mapRow(rs, rs.getRow());
            }

            LocalDate mealDate = getLocalDate(rs, "meal_date");
            if (mealDate != null) {
                String mealDollarsString = rs.getString("meal_value");
                BigDecimal mealDollars = mealDollarsString == null ? new BigDecimal("0") : new BigDecimal(mealDollarsString);
                boolean isMealRequested = rs.getBoolean("meal_requested");
                mealPerDiems.put(mealDate, new PerDiem(mealDate, mealDollars));
            }

            LocalDate lodgingDate = getLocalDate(rs, "lodging_date");
            if (lodgingDate != null) {
                String lodgingDollarsString = rs.getString("lodging_value");
                BigDecimal lodgingDollars = lodgingDollarsString == null ? new BigDecimal("0") : new BigDecimal(lodgingDollarsString);
                boolean isLodgingRequested = rs.getBoolean("lodging_requested");
                lodgingPerDiems.put(lodgingDate, new PerDiem(lodgingDate, lodgingDollars));
            }
        }

        Destination getDestination() {
            return new Destination(id, address, arrivalDate, departureDate, mealPerDiems, lodgingPerDiems);
        }
    }
}
