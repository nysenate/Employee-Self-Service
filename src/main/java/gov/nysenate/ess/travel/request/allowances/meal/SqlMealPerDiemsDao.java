package gov.nysenate.ess.travel.request.allowances.meal;

import gov.nysenate.ess.core.dao.base.BaseHandler;
import gov.nysenate.ess.core.dao.base.BasicSqlQuery;
import gov.nysenate.ess.core.dao.base.DbVendor;
import gov.nysenate.ess.core.dao.base.SqlBaseDao;
import gov.nysenate.ess.travel.request.address.TravelAddress;
import gov.nysenate.ess.travel.request.address.SqlTravelAddressDao;
import gov.nysenate.ess.travel.request.address.TravelAddressRowMapper;
import gov.nysenate.ess.travel.provider.senate.SenateMie;
import gov.nysenate.ess.travel.provider.senate.SenateMieRowMapper;
import gov.nysenate.ess.travel.utils.Dollars;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Repository
public class SqlMealPerDiemsDao extends SqlBaseDao {

    @Autowired private SqlTravelAddressDao travelAddressDao;

    public MealPerDiems selectMealPerDiems(int amendmentId) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("amendmentId", amendmentId);
        String sql = SqlMealPerDiemsQuery.SELECT_MEAL_PER_DIEMS.getSql(schemaMap());
        MealPerDiemsHandler handler = new MealPerDiemsHandler();
        localNamedJdbc.query(sql, params, handler);
        return handler.getResult();
    }

    public void saveMealPerDiems(MealPerDiems mealPerDiems, int amendmentId) {
        // Update Address's ids first
        for (MealPerDiem mpd : mealPerDiems.allMealPerDiems()) {
            // Ensure the address is in the database and update its id.
            // Destination addresses are inserted earlier but they are different instances so these address's ids
            // do not get updated. We need to call saveTravelAddress here so the mealPerDiem addresses have the correct address_id.
            travelAddressDao.saveAddress(mpd.address());
            insertMealPerDiem(mpd);
            int id = insertIntoJoinTable(mpd, amendmentId, mealPerDiems.overrideRate(), mealPerDiems.isAllowedMeals());
            mealPerDiems.setId(id);
        }
    }

    private void insertMealPerDiem(MealPerDiem mpd) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("addressId", mpd.address().getId())
                .addValue("date", toDate(mpd.date()))
                .addValue("rate", mpd.rate().toString())
                .addValue("senateMieId", mpd.mie() == null ? null : mpd.mie().getId())
                .addValue("isReimbursementRequested", mpd.isReimbursementRequested());

        String sql = SqlMealPerDiemsQuery.INSERT_MEAL_PER_DIEM.getSql(schemaMap());
        KeyHolder keyHolder = new GeneratedKeyHolder();
        localNamedJdbc.update(sql, params, keyHolder);
        mpd.setId((Integer) keyHolder.getKeys().get("amendment_meal_per_diem_id"));
    }

    private int insertIntoJoinTable(MealPerDiem mpd, int amendmentId, Dollars overrideRate, boolean isAllowedMeals) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("amendmentId", amendmentId)
                .addValue("mpdId", mpd.id())
                .addValue("overrideRate", overrideRate.toString())
                .addValue("isAllowedMeals", isAllowedMeals);
        String sql = SqlMealPerDiemsQuery.INSERT_JOIN_TABLE.getSql(schemaMap());
        KeyHolder keyHolder = new GeneratedKeyHolder();
        localNamedJdbc.update(sql, params, keyHolder);
        return (Integer) keyHolder.getKeys().get("amendment_meal_per_diems_id");
    }

    private enum SqlMealPerDiemsQuery implements BasicSqlQuery {
        SELECT_MEAL_PER_DIEMS("""
                SELECT mpds.amendment_meal_per_diems_id, mpds.amendment_meal_per_diem_id, mpds.override_rate, mpds.is_allowed_meals,
                  mpd.address_id, mpd.date, mpd.rate, mpd.senate_mie_id, mpd.is_reimbursement_requested,
                  senate_mie.fiscal_year, senate_mie.total, senate_mie.breakfast, senate_mie.dinner,
                  addr.street_1, addr.city, addr.state, addr.zip_5, addr.county, addr.country, addr.place_id, addr.name
                FROM ${travelSchema}.amendment_meal_per_diems mpds
                  INNER JOIN ${travelSchema}.amendment_meal_per_diem mpd ON mpds.amendment_meal_per_diem_id = mpd.amendment_meal_per_diem_id
                  LEFT JOIN ${travelSchema}.senate_mie ON mpd.senate_mie_id = senate_mie.senate_mie_id
                  INNER JOIN ${travelSchema}.address addr ON mpd.address_id = addr.address_id
                WHERE mpds.amendment_id = :amendmentId;
                """
        ),
        INSERT_MEAL_PER_DIEM("""
                INSERT INTO ${travelSchema}.amendment_meal_per_diem
                  (address_id, date, rate, senate_mie_id, is_reimbursement_requested)
                VALUES (:addressId, :date, :rate, :senateMieId, :isReimbursementRequested)
                """
        ),
        INSERT_JOIN_TABLE("""
                INSERT INTO ${travelSchema}.amendment_meal_per_diems
                  (amendment_id, amendment_meal_per_diem_id, override_rate, is_allowed_meals)
                VALUES (:amendmentId, :mpdId, :overrideRate, :isAllowedMeals)
                """
        );

        private String sql;

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

    public class MealPerDiemsHandler extends BaseHandler {

        private int mealPerDiemsId;
        private Dollars overrideRate;
        private boolean isAllowedMeals;
        private Set<MealPerDiem> mealPerDiems;
        private TravelAddressRowMapper addressRowMapper = new TravelAddressRowMapper();
        private SenateMieRowMapper senateMieRowMapper = new SenateMieRowMapper();

        public MealPerDiemsHandler() {
            this.mealPerDiems = new HashSet<>();
        }

        @Override
        public void processRow(ResultSet rs) throws SQLException {
            mealPerDiemsId = rs.getInt("amendment_meal_per_diems_id");
            overrideRate = new Dollars(rs.getString("override_rate"));
            isAllowedMeals = rs.getBoolean("is_allowed_meals");

            int mpdId = rs.getInt("amendment_meal_per_diem_id");
            TravelAddress address = addressRowMapper.mapRow(rs, rs.getRow());
            LocalDate date = getLocalDate(rs, "date");
            Dollars rate = new Dollars(rs.getString("rate"));
            SenateMie mie = senateMieRowMapper.mapRow(rs, rs.getRow()); // TODO what if mie data is missing?

//            int senateMieId = rs.getInt("senate_mie_id");
//            if (senateMieId != 0) { // not null
//                mie = senateMieDao.selectSenateMie(senateMieId);
//            }
            boolean isReimbursementRequested = rs.getBoolean("is_reimbursement_requested");
            MealPerDiem mpd = new MealPerDiem(mpdId, address, date, rate, mie, isReimbursementRequested);
            mealPerDiems.add(mpd);
        }

        public MealPerDiems getResult() {
            return new MealPerDiems(mealPerDiemsId, mealPerDiems, overrideRate, isAllowedMeals);
        }
    }
}
