package gov.nysenate.ess.travel.application.allowances.meal;

import gov.nysenate.ess.core.dao.base.BaseHandler;
import gov.nysenate.ess.core.dao.base.BasicSqlQuery;
import gov.nysenate.ess.core.dao.base.DbVendor;
import gov.nysenate.ess.core.dao.base.SqlBaseDao;
import gov.nysenate.ess.travel.application.address.GoogleAddress;
import gov.nysenate.ess.travel.application.address.SqlGoogleAddressDao;
import gov.nysenate.ess.travel.provider.gsa.meal.GsaMie;
import gov.nysenate.ess.travel.provider.gsa.meal.SqlGsaMieDao;
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

    @Autowired private SqlGoogleAddressDao googleAddressDao;
    @Autowired private SqlGsaMieDao gsaMieDao;

    public MealPerDiems selectMealPerDiems(int amendmentId) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("amendmentId", amendmentId);
        String sql = SqlMealPerDiemsQuery.SELECT_MEAL_PER_DIEM.getSql(schemaMap());
        MealPerDiemsHandler handler = new MealPerDiemsHandler(googleAddressDao, gsaMieDao);
        localNamedJdbc.query(sql, params, handler);
        return handler.getResult();
    }

    public void saveMealPerDiems(MealPerDiems mealPerDiems, int amendmentId) {
        // Update Address's ids first
        for (MealPerDiem mpd : mealPerDiems.allMealPerDiems()) {
            // Ensure the address is in the database and update its id.
            // Destination addresses are inserted earlier but they are different instances so these address's ids
            // do not get updated. We need to call saveGoogleAddress here so the mealPerDiem addresses have the correct google_address_id.
            googleAddressDao.saveGoogleAddress(mpd.address());
            insertMealPerDiem(mpd);
            int id = insertIntoJoinTable(mpd, amendmentId, mealPerDiems.overrideRate());
            mealPerDiems.setId(id);
        }
    }

    private void insertMealPerDiem(MealPerDiem mpd) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("googleAddressId", mpd.address().getId())
                .addValue("date", toDate(mpd.date()))
                .addValue("gsaMieId", mpd.mie().getId())
                .addValue("isReimbursementRequested", mpd.isReimbursementRequested());

        String sql = SqlMealPerDiemsQuery.INSERT_MEAL_PER_DIEM.getSql(schemaMap());
        KeyHolder keyHolder = new GeneratedKeyHolder();
        localNamedJdbc.update(sql, params, keyHolder);
        mpd.setId((Integer) keyHolder.getKeys().get("amendment_meal_per_diem_id"));
    }

    private int insertIntoJoinTable(MealPerDiem mpd, int amendmentId, Dollars overrideRate) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("amendmentId", amendmentId)
                .addValue("mpdId", mpd.id())
                .addValue("overrideRate", overrideRate.toString());
        String sql = SqlMealPerDiemsQuery.INSERT_JOIN_TABLE.getSql(schemaMap());
        KeyHolder keyHolder = new GeneratedKeyHolder();
        localNamedJdbc.update(sql, params, keyHolder);
        return (Integer) keyHolder.getKeys().get("amendment_meal_per_diems_id");
    }

    private enum SqlMealPerDiemsQuery implements BasicSqlQuery {
        SELECT_MEAL_PER_DIEM(
                "SELECT mpds.amendment_meal_per_diems_id, mpds.amendment_meal_per_diem_id, mpds.override_rate,\n" +
                        " mpd.google_address_id, mpd.date, mpd.gsa_mie_id, mpd.is_reimbursement_requested\n" +
                        " FROM ${travelSchema}.amendment_meal_per_diems mpds\n" +
                        " INNER JOIN ${travelSchema}.amendment_meal_per_diem mpd ON mpds.amendment_meal_per_diem_id = mpd.amendment_meal_per_diem_id\n" +
                        " WHERE mpds.amendment_id = :amendmentId"
        ),
        INSERT_MEAL_PER_DIEM(
                "INSERT INTO ${travelSchema}.amendment_meal_per_diem(google_address_id," +
                        " date, gsa_mie_id, is_reimbursement_requested) \n" +
                        "VALUES (:googleAddressId, :date, :gsaMieId, :isReimbursementRequested)"
        ),
        INSERT_JOIN_TABLE(
                "INSERT INTO ${travelSchema}.amendment_meal_per_diems(amendment_id," +
                        " amendment_meal_per_diem_id, override_rate) \n" +
                        "VALUES (:amendmentId, :mpdId, :overrideRate)"
        )
        ;

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
        private Set<MealPerDiem> mealPerDiems;

        private SqlGoogleAddressDao addressDao;
        private SqlGsaMieDao gsaMieDao;

        public MealPerDiemsHandler(SqlGoogleAddressDao addressDao, SqlGsaMieDao gsaMieDao) {
            this.addressDao = addressDao;
            this.gsaMieDao = gsaMieDao;
            this.mealPerDiems = new HashSet<>();
        }

        @Override
        public void processRow(ResultSet rs) throws SQLException {
            mealPerDiemsId = rs.getInt("amendment_meal_per_diems_id");
            overrideRate = new Dollars(rs.getString("override_rate"));

            int mpdId = rs.getInt("amendment_meal_per_diem_id");
            GoogleAddress address = addressDao.selectGoogleAddress(rs.getInt("google_address_id"));
            LocalDate date = getLocalDate(rs, "date");
            GsaMie mie = gsaMieDao.selectGsaMie(rs.getInt("gsa_mie_id"));
            boolean isReimbursementRequested = rs.getBoolean("is_reimbursement_requested");
            MealPerDiem mpd = new MealPerDiem(mpdId, address, date, mie, isReimbursementRequested);
            mealPerDiems.add(mpd);
        }

        public MealPerDiems getResult() {
            return new MealPerDiems(mealPerDiemsId, mealPerDiems, overrideRate);
        }
    }
}
