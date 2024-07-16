package gov.nysenate.ess.travel.request.allowances.meal;

import gov.nysenate.ess.core.dao.base.BaseHandler;
import gov.nysenate.ess.core.dao.base.BasicSqlQuery;
import gov.nysenate.ess.core.dao.base.DbVendor;
import gov.nysenate.ess.core.dao.base.SqlBaseDao;
import gov.nysenate.ess.travel.request.address.SqlTravelAddressDao;
import gov.nysenate.ess.travel.request.address.TravelAddress;
import gov.nysenate.ess.travel.request.address.TravelAddressRowMapper;
import gov.nysenate.ess.travel.provider.senate.SenateMie;
import gov.nysenate.ess.travel.provider.senate.SenateMieRowMapper;
import gov.nysenate.ess.travel.utils.Dollars;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

@Repository
public class SqlMealPerDiemsDao extends SqlBaseDao {

    @Autowired private SqlTravelAddressDao travelAddressDao;

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
        saveAdjustments(mpds.getAdjustments(), appId);
    }

    private void deleteMealPerDiems(int appId) {
        MapSqlParameterSource params = new MapSqlParameterSource("appId", appId);
        String sql = SqlMealPerDiemsQuery.DELETE_MEAL_PER_DIEMS.getSql(schemaMap());
        localNamedJdbc.update(sql, params);
    }

    private void insertMealPerDiems(MealPerDiems mpds, int appId) {
        List<SqlParameterSource> paramList = new ArrayList<>();
        for (MealPerDiem mpd : mpds.allMealPerDiems()) {
            travelAddressDao.saveAddress(mpd.address());
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

    private void saveAdjustments(MealPerDiemAdjustments adjustments, int appId) {
        if (updateAdjustments(adjustments, appId) == 0) {
            insertAdjustments(adjustments, appId);
        }
    }

    private int updateAdjustments(MealPerDiemAdjustments adjustments, int appId) {
        MapSqlParameterSource params = adjustmentParams(adjustments, appId);
        String sql = SqlMealPerDiemsQuery.UPDATE_MEAL_ADJUSTMENTS.getSql(schemaMap());
        return localNamedJdbc.update(sql, params);
    }

    private void insertAdjustments(MealPerDiemAdjustments adjustments, int appId) {
        MapSqlParameterSource params = adjustmentParams(adjustments, appId);
        String sql = SqlMealPerDiemsQuery.INSERT_MEAL_ADJUSTMENTS.getSql(schemaMap());
        localNamedJdbc.update(sql, params);
    }

    private MapSqlParameterSource adjustmentParams(MealPerDiemAdjustments adjustments, int appId) {
        return new MapSqlParameterSource()
                .addValue("appId", appId)
                .addValue("overrideRate", adjustments.overrideRate().toString())
                .addValue("isAllowedMeals", adjustments.isAllowedMeals());
    }


    private enum SqlMealPerDiemsQuery implements BasicSqlQuery {
        SELECT_MEAL_PER_DIEMS("""
                SELECT mpd.app_meal_per_diem_id, mpd.date, mpd.rate, mpd.is_reimbursement_requested,
                  mpd.qualifies_for_breakfast, mpd.qualifies_for_dinner,
                  addr.address_id, addr.street_1, addr.city, addr.state, addr.zip_5, addr.county, addr.country,
                  addr.place_id, addr.name,
                  mie.senate_mie_id, mie.fiscal_year, mie.total, mie.breakfast, mie.dinner,
                  override_rate, is_allowed_meals
                FROM ${travelSchema}.app_meal_per_diem mpd
                LEFT JOIN ${travelSchema}.app_meal_per_diem_adjustments USING (app_id)
                INNER JOIN ${travelSchema}.address addr USING (address_id)
                INNER JOIN ${travelSchema}.senate_mie mie USING (senate_mie_id)
                WHERE mpd.app_id = :appId
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
        UPDATE_MEAL_ADJUSTMENTS("""
                UPDATE ${travelSchema}.app_meal_per_diem_adjustments
                  SET override_rate = :overrideRate,
                  is_allowed_meals = :isAllowedMeals
                WHERE app_id = :appId
                """
        ),
        INSERT_MEAL_ADJUSTMENTS("""
                INSERT INTO ${travelSchema}.app_meal_per_diem_adjustments
                  (app_id, override_rate, is_allowed_meals)
                VALUES (:appId, :overrideRate, :isAllowedMeals)
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

        private MealPerDiemAdjustments adjustments;
        private Set<MealPerDiem> mealPerDiems;
        private TravelAddressRowMapper addressRowMapper = new TravelAddressRowMapper();
        private SenateMieRowMapper senateMieRowMapper = new SenateMieRowMapper();

        public MealPerDiemsHandler() {
            this.mealPerDiems = new HashSet<>();
        }

        @Override
        public void processRow(ResultSet rs) throws SQLException {
            if (adjustments == null) {
                adjustments = new MealPerDiemAdjustments.Builder()
                        .withOverrideRate(new Dollars(rs.getString("override_rate")))
                        .withIsAllowedMeals(rs.getBoolean("is_allowed_meals"))
                        .build();
            }

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
            return new MealPerDiems(mealPerDiems, adjustments);
        }
    }
}
