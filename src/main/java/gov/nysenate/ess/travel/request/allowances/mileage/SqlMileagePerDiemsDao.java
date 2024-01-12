package gov.nysenate.ess.travel.request.allowances.mileage;

import gov.nysenate.ess.core.dao.base.BaseHandler;
import gov.nysenate.ess.core.dao.base.BasicSqlQuery;
import gov.nysenate.ess.core.dao.base.DbVendor;
import gov.nysenate.ess.core.dao.base.SqlBaseDao;
import gov.nysenate.ess.travel.request.address.SqlTravelAddressDao;
import gov.nysenate.ess.travel.request.address.TravelAddressRowMapper;
import gov.nysenate.ess.travel.request.allowances.PerDiem;
import gov.nysenate.ess.travel.request.route.MethodOfTravel;
import gov.nysenate.ess.travel.request.route.ModeOfTransportation;
import gov.nysenate.ess.travel.utils.Dollars;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class SqlMileagePerDiemsDao extends SqlBaseDao {

    @Autowired private SqlTravelAddressDao travelAddressDao;

    public MileagePerDiems selectMileagePerDiems(int amendmentId) {
        MapSqlParameterSource params = new MapSqlParameterSource("amendmentId", amendmentId);
        String sql = SqlMileagePerDiemQuery.SELECT_MILEAGE_PER_DIEMS.getSql(schemaMap());
        MileagePerDiemsHandler handler = new MileagePerDiemsHandler();
        localNamedJdbc.query(sql, params, handler);
        return handler.getResults();
    }

    public void saveMileagePerDiems(MileagePerDiems mpds, int amendmentId) {
        for (int seqNo = 0; seqNo < mpds.allPerDiems().size(); seqNo++) {
            MileagePerDiem mpd = mpds.allPerDiems().get(seqNo);
            travelAddressDao.saveAddress(mpd.getFrom());
            travelAddressDao.saveAddress(mpd.getTo());
            insertMileagePerDiem(mpd, seqNo);
            insertJoinTable(mpd, amendmentId, mpds.getOverrideRate());
        }
    }

    private void insertMileagePerDiem(MileagePerDiem mpd, int sequenceNo) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("fromAddressId", mpd.getFrom().getId())
                .addValue("toAddressId", mpd.getTo().getId())
                .addValue("travelDate", toDate(mpd.getTravelDate()))
                .addValue("methodOfTravel", mpd.getModeOfTransportation().getMethodOfTravel().name())
                .addValue("methodOfTravelDescription", mpd.getModeOfTransportation().getDescription())
                .addValue("miles", String.valueOf(mpd.getMiles()))
                .addValue("mileageRate", mpd.getMileageRate().toString())
                .addValue("isOutbound", mpd.isOutbound())
                .addValue("isReimbursementRequested", mpd.isReimbursementRequested())
                .addValue("sequenceNo", sequenceNo);

        String sql = SqlMileagePerDiemQuery.INSERT_MILEAGE_PER_DIEM.getSql(schemaMap());
        KeyHolder keyHolder = new GeneratedKeyHolder();
        localNamedJdbc.update(sql, params, keyHolder);
        mpd.setId((Integer) keyHolder.getKeys().get("amendment_mileage_per_diem_id"));
    }

    private void insertJoinTable(MileagePerDiem mpd, int amendmentId, Dollars overrideRate) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("amendmentId", amendmentId)
                .addValue("amendmentMileagePerDiemId", mpd.getId())
                .addValue("overrideRate", overrideRate.toString());
        String sql = SqlMileagePerDiemQuery.INSERT_JOIN_TABLE.getSql(schemaMap());
        localNamedJdbc.update(sql, params);
    }

    private enum SqlMileagePerDiemQuery implements BasicSqlQuery {
        SELECT_MILEAGE_PER_DIEMS("""
                SELECT mpds.override_rate, mpd.amendment_mileage_per_diem_id,
                  mpd.travel_date, mpd.method_of_travel, mpd.method_of_travel_description, mpd.miles,
                  mpd.mileage_rate, mpd.is_outbound, mpd.is_reimbursement_requested,
                  from_addr.address_id as from_address_id, from_addr.street_1 as from_street_1, from_addr.city as from_city,
                  from_addr.state as from_state, from_addr.zip_5 as from_zip_5, from_addr.county as from_county,
                  from_addr.country as from_country, from_addr.place_id as from_place_id, from_addr.name as from_name,
                  to_addr.address_id as to_address_id, to_addr.street_1 as to_street_1, to_addr.city as to_city,
                  to_addr.state as to_state, to_addr.zip_5 as to_zip_5, to_addr.county as to_county,
                  to_addr.country as to_country, to_addr.place_id as to_place_id, to_addr.name as to_name
                FROM ${travelSchema}.amendment_mileage_per_diems mpds
                  INNER JOIN ${travelSchema}.amendment_mileage_per_diem mpd
                    ON mpds.amendment_mileage_per_diem_id = mpd.amendment_mileage_per_diem_id
                  INNER JOIN ${travelSchema}.address from_addr
                    ON mpd.from_address_id = from_addr.address_id
                  INNER JOIN ${travelSchema}.address to_addr
                    ON mpd.to_address_id = to_addr.address_id
                WHERE mpds.amendment_id = :amendmentId;
                """
        ),
        INSERT_MILEAGE_PER_DIEM("""
                INSERT INTO ${travelSchema}.amendment_mileage_per_diem(from_address_id, to_address_id, travel_date,
                  method_of_travel, method_of_travel_description, miles, mileage_rate, is_outbound,
                  is_reimbursement_requested, sequence_no)
                VALUES(:fromAddressId, :toAddressId, :travelDate,
                  :methodOfTravel, :methodOfTravelDescription, :miles, :mileageRate, :isOutbound,
                  :isReimbursementRequested, :sequenceNo);
                """
        ),
        INSERT_JOIN_TABLE("""
                INSERT INTO ${travelSchema}.amendment_mileage_per_diems(amendment_id, amendment_mileage_per_diem_id,
                  override_rate)
                VALUES(:amendmentId, :amendmentMileagePerDiemId, :overrideRate);
                """
        );

        private String sql;

        SqlMileagePerDiemQuery(String sql) {
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

    private class MileagePerDiemsHandler extends BaseHandler {

        private TravelAddressRowMapper addressRowMapper = new TravelAddressRowMapper();
        private Dollars overrideRate;
        private List<MileagePerDiem> mileagePerDiemList = new ArrayList();

        @Override
        public void processRow(ResultSet rs) throws SQLException {
            if (overrideRate == null) {
                overrideRate = new Dollars(rs.getString("override_rate"));
            }
            mileagePerDiemList.add(new MileagePerDiem(
                    rs.getInt("amendment_mileage_per_diem_id"),
                    addressRowMapper.mapRow(rs, rs.getRow(), "from_"),
                    addressRowMapper.mapRow(rs, rs.getRow(), "to_"),
                    new ModeOfTransportation(MethodOfTravel.of(rs.getString("method_of_travel")), rs.getString("method_of_travel_description")),
                    rs.getDouble("miles"),
                    new PerDiem(getLocalDate(rs, "travel_date"), new BigDecimal(rs.getString("mileage_rate"))),
                    rs.getBoolean("is_outbound"),
                    rs.getBoolean("is_reimbursement_requested")
                    ));
        }

        public MileagePerDiems getResults() {
            return new MileagePerDiems(mileagePerDiemList, overrideRate);
        }
    }
}