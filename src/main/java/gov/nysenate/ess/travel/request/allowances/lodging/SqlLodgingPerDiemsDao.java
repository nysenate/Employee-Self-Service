package gov.nysenate.ess.travel.request.allowances.lodging;

import gov.nysenate.ess.core.dao.base.BaseHandler;
import gov.nysenate.ess.core.dao.base.BasicSqlQuery;
import gov.nysenate.ess.core.dao.base.DbVendor;
import gov.nysenate.ess.core.dao.base.SqlBaseDao;
import gov.nysenate.ess.travel.request.address.TravelAddress;
import gov.nysenate.ess.travel.request.address.SqlTravelAddressDao;
import gov.nysenate.ess.travel.request.address.TravelAddressRowMapper;
import gov.nysenate.ess.travel.request.allowances.PerDiem;
import gov.nysenate.ess.travel.utils.Dollars;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Repository
public class SqlLodgingPerDiemsDao extends SqlBaseDao {

    public LodgingPerDiems selectLodgingPerDiems(int appId) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("appId", appId);
        String sql = SqlLodgingPerDiemsQuery.SELECT_LODGING_PER_DIEMS.getSql(schemaMap());
        LodgingPerDiemsHandler handler = new LodgingPerDiemsHandler();
        localNamedJdbc.query(sql, params, handler);
        return handler.getResult();
    }

    @Transactional
    public void updateLodgingPerDiems(LodgingPerDiems lpds, int appId) {
        deleteLodgingPerDiems(appId);
        insertLodgingPerDiems(lpds, appId);
        updateOverrideRate(lpds, appId);
    }

    private void deleteLodgingPerDiems(int appId) {
        MapSqlParameterSource params = new MapSqlParameterSource("appId", appId);
        String sql = SqlLodgingPerDiemsQuery.DELETE_LODGING_PER_DIEMS.getSql(schemaMap());
        localNamedJdbc.update(sql, params);
    }

    private void insertLodgingPerDiems(LodgingPerDiems lpds, int appId) {
        List<SqlParameterSource> paramList = new ArrayList<>();
        for (LodgingPerDiem lpd : lpds.allLodgingPerDiems()) {
            MapSqlParameterSource params = new MapSqlParameterSource()
                    .addValue("appId", appId)
                    .addValue("addressId", lpd.address().getId())
                    .addValue("date", toDate(lpd.date()))
                    .addValue("rate", lpd.rate().toString())
                    .addValue("isReimbursementRequested", lpd.isReimbursementRequested());
            paramList.add(params);
        }
        String sql = SqlLodgingPerDiemsQuery.INSERT_LODGING_PER_DIEM.getSql(schemaMap());
        SqlParameterSource[] batchParams = new SqlParameterSource[paramList.size()];
        batchParams = paramList.toArray(batchParams);
        localNamedJdbc.batchUpdate(sql, batchParams);
    }

    private void updateOverrideRate(LodgingPerDiems lpds, int appId) {
        deleteOverrideRate(appId);
        insertOverrideRate(lpds, appId);
    }

    private void deleteOverrideRate(int appId) {
        MapSqlParameterSource params = new MapSqlParameterSource("appId", appId);
        String sql = SqlLodgingPerDiemsQuery.DELETE_LODGING_OVERRIDE_RATE.getSql(schemaMap());
        localNamedJdbc.update(sql, params);
    }

    private void insertOverrideRate(LodgingPerDiems lpds, int appId) {
        if (!lpds.isOverridden()) {
            return;
        }

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("appId", appId)
                .addValue("overrideRate", lpds.overrideRate().toString());
        String sql = SqlLodgingPerDiemsQuery.INSERT_LODGING_OVERRIDE_RATE.getSql(schemaMap());
        localNamedJdbc.update(sql, params);
    }


    private enum SqlLodgingPerDiemsQuery implements BasicSqlQuery {
        SELECT_LODGING_PER_DIEMS("""
                SELECT lpd.app_lodging_per_diem_id, lpd.address_id, lpd.date, lpd.rate, lpd.is_reimbursement_requested,
                  addr.street_1, addr.city, addr.state, addr.zip_5, addr.county, addr.country, addr.place_id, addr.name,
                  override_rate
                FROM ${travelSchema}.app_lodging_per_diem lpd
                LEFT JOIN ${travelSchema}.app_lodging_per_diem_override USING (app_id)
                INNER JOIN ${travelSchema}.address addr USING (address_id)
                WHERE lpd.app_id = :appId;
                """
        ),
        DELETE_LODGING_PER_DIEMS("""
                DELETE FROM ${travelSchema}.app_lodging_per_diem
                WHERE app_id = :appId
                """
        ),
        INSERT_LODGING_PER_DIEM("""
                INSERT INTO ${travelSchema}.app_lodging_per_diem
                  (address_id, date, rate, is_reimbursement_requested, app_id)
                VALUES (:addressId, :date, :rate, :isReimbursementRequested, :appId)
                """
        ),
        DELETE_LODGING_OVERRIDE_RATE("""
                DELETE FROM ${travelSchema}.app_lodging_per_diem_override
                WHERE app_id = :appId
                """
        ),
        INSERT_LODGING_OVERRIDE_RATE("""
                INSERT INTO ${travelSchema}.app_lodging_per_diem_override
                  (app_id, override_rate)
                VALUES (:appId, :overrideRate)
                """
        );

        private final String sql;

        SqlLodgingPerDiemsQuery(String sql) {
            this.sql = sql;
        }

        @Override
        public String getSql() {
            return this.sql;
        }

        @Override
        public DbVendor getVendor() {
            return DbVendor.POSTGRES;
        }
    }

    public static class LodgingPerDiemsHandler extends BaseHandler {

        private Dollars overrideRate;
        private Set<LodgingPerDiem> lodgingPerDiems;
        private TravelAddressRowMapper addressRowMapper = new TravelAddressRowMapper();

        public LodgingPerDiemsHandler() {
            this.lodgingPerDiems = new HashSet<>();
        }

        @Override
        public void processRow(ResultSet rs) throws SQLException {
            overrideRate = new Dollars(rs.getString("override_rate"));

            int lpdId = rs.getInt("app_lodging_per_diem_id");
            TravelAddress address = addressRowMapper.mapRow(rs, rs.getRow());
            PerDiem perDiem = new PerDiem(getLocalDate(rs, "date"), new BigDecimal(rs.getString("rate")));
            boolean isReimbursementRequested = rs.getBoolean("is_reimbursement_requested");
            LodgingPerDiem lpd = new LodgingPerDiem(lpdId, address, perDiem, isReimbursementRequested);
            lodgingPerDiems.add(lpd);
        }

        public LodgingPerDiems getResult() {
            return new LodgingPerDiems(lodgingPerDiems, overrideRate);
        }
    }
}
