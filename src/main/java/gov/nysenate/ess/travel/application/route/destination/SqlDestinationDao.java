package gov.nysenate.ess.travel.application.route.destination;

import gov.nysenate.ess.core.dao.base.BaseHandler;
import gov.nysenate.ess.core.dao.base.BasicSqlQuery;
import gov.nysenate.ess.core.dao.base.DbVendor;
import gov.nysenate.ess.core.dao.base.SqlBaseDao;
import gov.nysenate.ess.travel.application.address.TravelAddress;
import gov.nysenate.ess.travel.application.address.TravelAddressRowMapper;
import gov.nysenate.ess.travel.application.address.SqlTravelAddressDao;
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.TreeMap;

@Repository
public class SqlDestinationDao extends SqlBaseDao implements DestinationDao {

    private SqlTravelAddressDao travelAddressDao;

    @Autowired
    public SqlDestinationDao(SqlTravelAddressDao travelAddressDao) {
        this.travelAddressDao = travelAddressDao;
    }

    @Override
    public void insertDestinations(Collection<Destination> destinations) {
        for (Destination dest : destinations) {
            insertDestination(dest);
        }
    }

    @Override
    public void insertDestination(Destination destination) {
        travelAddressDao.saveAddress(destination.getAddress());
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("arrivalDate", toDate(destination.arrivalDate()))
                .addValue("departureDate", toDate(destination.departureDate()))
                .addValue("addressId", destination.getAddress().getId());
        String sql = SqlDestinationQuery.INSERT_DESTINATION.getSql(schemaMap());
        KeyHolder keyHolder = new GeneratedKeyHolder();
        localNamedJdbc.update(sql, params, keyHolder);
        destination.setId((Integer) keyHolder.getKeys().get("destination_id"));

        insertMealPerDiems(destination);
        insertLodgingPerDiems(destination);
    }

    private void insertMealPerDiems(Destination destination) {
        List<SqlParameterSource> paramList = new ArrayList<>();
        for (PerDiem pd : destination.mealPerDiems()) {
            MapSqlParameterSource params = new MapSqlParameterSource()
                    .addValue("destinationId", destination.getId())
                    .addValue("date", toDate(pd.getDate()))
                    .addValue("value", pd.getRate().toString());
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
                    .addValue("value", lodgingPerDiem.getRate().toString());
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
        DestinationHandler handler = new DestinationHandler(new TravelAddressRowMapper());
        localNamedJdbc.query(sql, params, handler);
        return handler.getDestination();
    }

    private enum SqlDestinationQuery implements BasicSqlQuery {
        INSERT_DESTINATION(
                "INSERT INTO ${travelSchema}.destination(arrival_date, departure_date, address_id)" +
                        " VALUES (:arrivalDate, :departureDate, :addressId)"
        ),
        INSERT_MEAL_PER_DIEMS(
                "INSERT INTO ${travelSchema}.destination_meal_per_diem" +
                        " (destination_id, date, value)\n" +
                        " VALUES (:destinationId, :date, :value)"
        ),
        INSERT_LODGING_PER_DIEMS(
                "INSERT INTO ${travelSchema}.destination_lodging_per_diem" +
                        " (destination_id, date, value)\n" +
                        " VALUES (:destinationId, :date, :value)"
        ),
        SELECT_DESTINATION(
                "SELECT dest.destination_id, dest.arrival_date, dest.departure_date,\n" +
                        " addr.address_id, addr.street_1, addr.city, addr.state,\n" +
                        " addr.zip_5, addr.county, addr.country,\n" +
                        " addr.place_id, addr.name, \n" +
                        " m.date as meal_date, m.value as meal_value,\n" +
                        " l.date as lodging_date, l.value as lodging_value\n" +
                        " FROM ${travelSchema}.destination dest\n" +
                        "   LEFT JOIN ${travelSchema}.address addr ON addr.address_id = dest.address_id\n" +
                        "   LEFT JOIN ${travelSchema}.destination_meal_per_diem m ON dest.destination_id = m.destination_id\n" +
                        "   LEFT JOIN ${travelSchema}.destination_lodging_per_diem l ON dest.destination_id = l.destination_id\n" +
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
        private TravelAddress address;
        private TreeMap<LocalDate, PerDiem> mealPerDiems;
        private TreeMap<LocalDate, PerDiem> lodgingPerDiems;
        private TravelAddressRowMapper addressRowMapper;

        DestinationHandler(TravelAddressRowMapper addressRowMapper) {
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
                String mealValueString = rs.getString("meal_value");
                BigDecimal mealValue = mealValueString == null ? new BigDecimal("0") : new BigDecimal(mealValueString);
                mealPerDiems.put(mealDate, new PerDiem(mealDate, mealValue));
            }

            LocalDate lodgingDate = getLocalDate(rs, "lodging_date");
            if (lodgingDate != null) {
                String lodgingDollarsString = rs.getString("lodging_value");
                BigDecimal lodgingDollars = lodgingDollarsString == null ? new BigDecimal("0") : new BigDecimal(lodgingDollarsString);
                lodgingPerDiems.put(lodgingDate, new PerDiem(lodgingDate, lodgingDollars));
            }
        }

        Destination getDestination() {
            return new Destination(id, address, arrivalDate, departureDate, mealPerDiems, lodgingPerDiems);
        }
    }
}
