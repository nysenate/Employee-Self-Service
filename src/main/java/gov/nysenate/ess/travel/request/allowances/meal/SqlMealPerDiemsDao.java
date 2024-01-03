package gov.nysenate.ess.travel.request.allowances.meal;

import gov.nysenate.ess.core.dao.base.BaseHandler;
import gov.nysenate.ess.core.dao.base.BasicSqlQuery;
import gov.nysenate.ess.core.dao.base.DbVendor;
import gov.nysenate.ess.core.dao.base.SqlBaseDao;
import gov.nysenate.ess.travel.request.address.TravelAddress;
import gov.nysenate.ess.travel.request.address.TravelAddressRowMapper;
import gov.nysenate.ess.travel.provider.senate.SenateMie;
import gov.nysenate.ess.travel.provider.senate.SenateMieRowMapper;
import gov.nysenate.ess.travel.utils.Dollars;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Repository
public class SqlMealPerDiemsDao extends SqlBaseDao {

    public MealPerDiems selectMealPerDiems(int appId) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("appId", appId);
        String sql = SqlMealPerDiemsQuery.SELECT_MEAL_PER_DIEMS.getSql(schemaMap());
        MealPerDiemsHandler handler = new MealPerDiemsHandler();
        localNamedJdbc.query(sql, params, handler);
        return handler.getResult();
    }

    public void updateMealPerDiems(MealPerDiems mpds, int appId) {
        deleteMealPerDiems(appId);
        insertMealPerDiems(mpds, appId);
        updateOverrideRate(mpds, appId);
    }

    private void deleteMealPerDiems(int appId) {
        MapSqlParameterSource params = new MapSqlParameterSource("appId", appId);
        String sql = SqlMealPerDiemsQuery.DELETE_MEAL_PER_DIEMS.getSql(schemaMap());
        localNamedJdbc.update(sql, params);
    }

    private void insertMealPerDiems(MealPerDiems mpds, int appId) {
        List<SqlParameterSource> paramList = new ArrayList<>();
        for (MealPerDiem mpd : mpds.allMealPerDiems()) {
            MapSqlParameterSource params = new MapSqlParameterSource()
                    .addValue("appId", appId)
                    .addValue("addressId", mpd.address().getId())
                    .addValue("senateMieId", mpd.mie().getId())
                    .addValue("date", toDate(mpd.date()))
                    .addValue("rate", mpd.rate().toString())
                    .addValue("qualifiesForBreakfast", mpd.qualifiesForBreakfast())
                    .addValue("qualifiesForDinner", mpd.qualifiesForDinner())
                    .addValue("isReimbursementRequested", mpd.isReimbursementRequested());
            paramList.add(params);
        }
        String sql = SqlMealPerDiemsQuery.INSERT_MEAL_PER_DIEM.getSql(schemaMap());
        SqlParameterSource[] batchParams = new SqlParameterSource[paramList.size()];
        batchParams = paramList.toArray(batchParams);
        localNamedJdbc.batchUpdate(sql, batchParams);
    }

    private void updateOverrideRate(MealPerDiems mpds, int appId) {
        deleteOverrideRate(appId);
        insertOverrideRate(mpds, appId);
    }

    private void deleteOverrideRate(int appId) {
        MapSqlParameterSource params = new MapSqlParameterSource("appId", appId);
        String sql = SqlMealPerDiemsQuery.DELETE_MEAL_OVERRIDE_RATE.getSql(schemaMap());
        localNamedJdbc.update(sql, params);
    }

    private void insertOverrideRate(MealPerDiems mpds, int appId) {
        if (!mpds.isOverridden()) {
            return;
        }

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("appId", appId)
                .addValue("overrideRate", mpds.overrideRate().toString());
        String sql = SqlMealPerDiemsQuery.INSERT_MEAL_OVERRIDE_RATE.getSql(schemaMap());
        localNamedJdbc.update(sql, params);
    }


    private enum SqlMealPerDiemsQuery implements BasicSqlQuery {
        SELECT_MEAL_PER_DIEMS("""
                SELECT mpd.app_meal_per_diem_id, mpd.date, mpd.rate, mpd.is_reimbursement_requested,
                  mpd.qualifies_for_breakfast, mpd.qualifies_for_dinner,
                  addr.street_1, addr.city, addr.state, addr.zip_5, addr.county, addr.country, addr.place_id, addr.name,
                  mie.fiscal_year, mie.total, mie.breakfast, mie.dinner,
                  override_rate
                FROM ${travelSchema}.app_meal_per_diem mpd
                LEFT JOIN ${travelSchema}.app_meal_per_diem_override USING (app_id)
                INNER JOIN ${travelSchema}.address addr USING (address_id)
                INNER JOIN ${travelSchema}.senate_mie mie USING (senate_mie_id)
                WHERE lpd.app_id = :appId
                """
        ),
        DELETE_MEAL_PER_DIEMS("""
                DELETE FROM ${travelSchema}.app_meal_per_diem
                WHERE app_id = :appId
                """
        ),
        INSERT_MEAL_PER_DIEM("""
                INSERT INTO ${travelSchema}.app_meal_per_diem
                  (app_id, address_id, date, rate, is_reimbursement_requested, senate_mie_id,
                  qualifies_for_breakfast, qualifies_for_dinner)
                VALUES (:appId, :addressId, :date, :rate, :isReimbursementRequested, :senateMieId,
                  :qualifiesForBreakfast, :qualifiesForDinner)
                """
        ),
        DELETE_MEAL_OVERRIDE_RATE("""
                DELETE FROM ${travelSchema}.app_meal_per_diem_override
                WHERE app_id = :appId
                """
        ),
        INSERT_MEAL_OVERRIDE_RATE("""
                INSERT INTO ${travelSchema}.app_meal_per_diem_override
                  (app_id, override_rate)
                VALUES (:appId, :overrideRate)
                """
        );

        private final String sql;

        SqlMealPerDiemsQuery(String sql) {
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

    public static class MealPerDiemsHandler extends BaseHandler {

        private Dollars overrideRate;
        private Set<MealPerDiem> mealPerDiems;
        private TravelAddressRowMapper addressRowMapper = new TravelAddressRowMapper();
        private SenateMieRowMapper senateMieRowMapper = new SenateMieRowMapper();

        public MealPerDiemsHandler() {
            this.mealPerDiems = new HashSet<>();
        }

        @Override
        public void processRow(ResultSet rs) throws SQLException {
            overrideRate = new Dollars(rs.getString("override_rate"));

            int mpdId = rs.getInt("app_meal_per_diem_id");
            TravelAddress address = addressRowMapper.mapRow(rs, rs.getRow());
            LocalDate date = getLocalDate(rs, "date");
            Dollars rate = new Dollars(rs.getString("rate"));
            SenateMie mie = senateMieRowMapper.mapRow(rs, rs.getRow()); // TODO what if mie data is missing?
            boolean isReimbursementRequested = rs.getBoolean("is_reimbursement_requested");
            boolean qualifiesForBreakfast = rs.getBoolean("qualifies_for_breakfast");
            boolean qualifiesForDinner = rs.getBoolean("qualifies_for_dinner");
            MealPerDiem mpd = new MealPerDiem(mpdId, address, date, rate, mie, isReimbursementRequested,
                    qualifiesForBreakfast, qualifiesForDinner);
            mealPerDiems.add(mpd);
        }

        public MealPerDiems getResult() {
            return new MealPerDiems(mealPerDiems, overrideRate);
        }
    }
}
