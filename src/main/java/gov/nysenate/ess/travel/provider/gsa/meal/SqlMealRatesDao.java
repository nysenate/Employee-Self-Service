package gov.nysenate.ess.travel.provider.gsa.meal;

import gov.nysenate.ess.core.dao.base.BaseHandler;
import gov.nysenate.ess.core.dao.base.BasicSqlQuery;
import gov.nysenate.ess.core.dao.base.DbVendor;
import gov.nysenate.ess.core.dao.base.SqlBaseDao;
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
public class SqlMealRatesDao extends SqlBaseDao {

    /**
     * Get the effective Meal Rates for a given date.
     */
    public MealRates getMealRates(LocalDate date) {
        MapSqlParameterSource params =  new MapSqlParameterSource()
                .addValue("date", toDate(date));
        String sql = SqlMealRatesQuery.GET_MEAL_RATES.getSql(schemaMap());
        MealRatesHandler handler = new MealRatesHandler();
        localNamedJdbc.query(sql, params, handler);
        return handler.results();
    }

    /**
     * Insert a new meal rate.
     */
    public synchronized void insertMealRates(MealRates mealRates) {
        MapSqlParameterSource params = mealRatesParams(mealRates);
        String sql = SqlMealRatesQuery.INSERT_MEAL_RATE.getSql(schemaMap());
        localNamedJdbc.update(sql, params);
        insertMealTiers(mealRates);
    }

    /**
     * Updates a meal rate.
     */
    public void updateMealRates(MealRates mealRates) {
        MapSqlParameterSource params = mealRatesParams(mealRates);
        String sql = SqlMealRatesQuery.UPDATE_MEAL_RATES.getSql(schemaMap());
        localNamedJdbc.update(sql, params);
    }

    private void insertMealTiers(MealRates mealRates) {
        List<SqlParameterSource> paramList = createBatchMealTierParams(mealRates);
        String sql = SqlMealRatesQuery.INSERT_MEAL_TIER.getSql(schemaMap());
        SqlParameterSource[] batchParams = new SqlParameterSource[paramList.size()];
        batchParams = paramList.toArray(batchParams);
        localNamedJdbc.batchUpdate(sql, batchParams);
    }

    private List<SqlParameterSource> createBatchMealTierParams(MealRates mealRates) {
        List<SqlParameterSource> paramList = new ArrayList<>();
        for (MealTier tier: mealRates.getTiers()) {
            MapSqlParameterSource params = new MapSqlParameterSource()
                    .addValue("id", tier.getId().toString())
                    .addValue("mealRateId", mealRates.getId().toString())
                    .addValue("tier", tier.getTier())
                    .addValue("total", tier.getTotal().toString())
                    .addValue("incidental", tier.getIncidental().toString());
            paramList.add(params);
        }
        return paramList;
    }

    private MapSqlParameterSource mealRatesParams(MealRates mealRates) {
        return new MapSqlParameterSource()
                .addValue("id", mealRates.getId().toString())
                .addValue("startDate", toDate(mealRates.getStartDate()))
                .addValue("endDate", toDate(mealRates.getEndDate()));
    }

    private enum SqlMealRatesQuery implements BasicSqlQuery {
        INSERT_MEAL_RATE(
                "INSERT INTO ${travelSchema}.meal_rate(id, start_date, end_date) \n" +
                        "VALUES (:id::uuid, :startDate, :endDate)"
        ),
        INSERT_MEAL_TIER(
                "INSERT INTO ${travelSchema}.meal_tier(id, meal_rate_id, tier, total, incidental) " +
                        "VALUES (:id::uuid, :mealRateId::uuid, :tier, :total, :incidental)"
        ),
        UPDATE_MEAL_RATES(
                "UPDATE ${travelSchema}.meal_rate \n" +
                        "SET start_date = :startDate, \n" +
                        "end_date = :endDate \n" +
                        "WHERE id = :id::uuid"
        ),
        GET_MEAL_RATES(
                "SELECT mr.id, mr.start_date, mr.end_date, \n" +
                        "mt.id as meal_tier_id, mt.tier, mt.total, mt.incidental \n" +
                        "FROM ${travelSchema}.meal_tier mt \n" +
                        "INNER JOIN ${travelSchema}.meal_rate mr on mr.id = mt.meal_rate_id " +
                        "WHERE :date BETWEEN mr.start_date AND mr.end_date"
        );

        private String sql;

        SqlMealRatesQuery(String sql) {
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

    private class MealRatesHandler extends BaseHandler {

        private UUID mealRateId;
        private LocalDate startDate;
        private LocalDate endDate;
        private Set<MealTier> tiers;

        public MealRatesHandler() {
            this.tiers = new HashSet<>();
        }

        @Override
        public void processRow(ResultSet rs) throws SQLException {
            if (mealRateId == null) {
                mealRateId = UUID.fromString(rs.getString("id"));
                startDate = getLocalDateFromRs(rs, "start_date");
                endDate = getLocalDateFromRs(rs, "end_date");
            }
            MealTier tier = new MealTier(UUID.fromString(rs.getString("meal_tier_id")), rs.getString("tier"), rs.getString("total"), rs.getString("incidental"));
            tiers.add(tier);
        }

        public MealRates results() {
            return new MealRates(mealRateId, startDate, endDate, tiers);
        }
    }
}
