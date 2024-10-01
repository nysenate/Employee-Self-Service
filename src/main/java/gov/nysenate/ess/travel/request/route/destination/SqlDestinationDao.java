package gov.nysenate.ess.travel.request.route.destination;

import gov.nysenate.ess.core.dao.base.BaseHandler;
import gov.nysenate.ess.core.dao.base.BasicSqlQuery;
import gov.nysenate.ess.core.dao.base.DbVendor;
import gov.nysenate.ess.core.dao.base.SqlBaseDao;
import gov.nysenate.ess.travel.request.address.TravelAddress;
import gov.nysenate.ess.travel.request.address.TravelAddressRowMapper;
import gov.nysenate.ess.travel.request.address.SqlTravelAddressDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Collection;

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
                """
                INSERT INTO ${travelSchema}.destination(arrival_date, departure_date, address_id)
                        VALUES (:arrivalDate, :departureDate, :addressId)
                """
        ),
        SELECT_DESTINATION(
                """
                SELECT dest.destination_id, dest.arrival_date, dest.departure_date,
                        addr.address_id, addr.street_1, addr.city, addr.state,
                        addr.zip_5, addr.county, addr.country,
                        addr.place_id, addr.name
                        FROM ${travelSchema}.destination dest
                          LEFT JOIN ${travelSchema}.address addr ON addr.address_id = dest.address_id
                        WHERE dest.destination_id = :destinationId
                """
        );

        private final String sql;

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

    private static class DestinationHandler extends BaseHandler {

        private int id;
        private LocalDate arrivalDate;
        private LocalDate departureDate;
        private TravelAddress address;
        private TravelAddressRowMapper addressRowMapper;

        DestinationHandler(TravelAddressRowMapper addressRowMapper) {
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
        }

        Destination getDestination() {
            return new Destination(id, address, arrivalDate, departureDate);
        }
    }
}
