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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Repository
public class SqlMealRatesDao extends SqlBaseDao {

    public MealRates getMealRates(LocalDate date) {
        MapSqlParameterSource params =  new MapSqlParameterSource()
                .addValue("date", toDate(date));
        String sql = SqlMealRatesQuery.GET_MEAL_RATES.getSql(schemaMap());
        MealRatesHandler handler = new MealRatesHandler();
        localNamedJdbc.query(sql, params, handler);
        return handler.results();
    }

    // TODO Transactional requires interface
//    @Transactional(value = "localTxManager")
    public synchronized void insertMealRates(MealRates mealRates, LocalDate date) {
        updateCurrentRatesEndDate(date);
        Integer id = insertMealRate(date);
        insertMealTiers(mealRates, id);
    }

    private void updateCurrentRatesEndDate(LocalDate endDate) {
        MapSqlParameterSource params =  new MapSqlParameterSource()
                .addValue("endDate", toDate(endDate));
        String sql = SqlMealRatesQuery.UPDATE_MEAL_RATE_END_DATE.getSql(schemaMap());
        localNamedJdbc.update(sql, params);
    }

    private Integer insertMealRate(LocalDate startDate) {
        MapSqlParameterSource params =  new MapSqlParameterSource()
                .addValue("startDate", toDate(startDate));
        String sql = SqlMealRatesQuery.INSERT_MEAL_RATE.getSql(schemaMap());
        KeyHolder keyHolder = new GeneratedKeyHolder();
        localNamedJdbc.update(sql, params, keyHolder);
        return (Integer) keyHolder.getKeys().get("id");
    }

    private void insertMealTiers(MealRates mealRates, Integer id) {
        List<SqlParameterSource> paramList = createBatchParams(mealRates, id);
        String sql = SqlMealRatesQuery.INSERT_MEAL_TIER.getSql(schemaMap());
        SqlParameterSource[] batchParams = new SqlParameterSource[paramList.size()];
        batchParams = paramList.toArray(batchParams);
        localNamedJdbc.batchUpdate(sql, batchParams);
    }

    private List<SqlParameterSource> createBatchParams(MealRates mealRates, Integer id) {
        List<SqlParameterSource> paramList = new ArrayList<>();
        for (MealTier tier: mealRates.getTiers()) {
            MapSqlParameterSource params = new MapSqlParameterSource()
                    .addValue("id", id)
                    .addValue("tier", tier.getTier())
                    .addValue("breakfast", tier.getBreakfast().toString())
                    .addValue("lunch", tier.getLunch().toString())
                    .addValue("dinner", tier.getDinner().toString())
                    .addValue("incidental", tier.getIncidental().toString());
            paramList.add(params);
        }
        return paramList;
    }


    private enum SqlMealRatesQuery implements BasicSqlQuery {
        INSERT_MEAL_RATE(
                "INSERT INTO ${travelSchema}.meal_rate(start_date) \n" +
                        "VALUES (:startDate)"
        ),
        INSERT_MEAL_TIER(
                "INSERT INTO ${travelSchema}.meal_tier(id, tier, breakfast, lunch, dinner, incidental) " +
                        "VALUES (:id, :tier, :breakfast, :lunch, :dinner, :incidental)"
        ),
        UPDATE_MEAL_RATE_END_DATE(
                "UPDATE ${travelSchema}.meal_rate " +
                        "SET end_date = :endDate " +
                        "WHERE end_date IS NULL"
        ),
        GET_MEAL_RATES(
                "SELECT tier, breakfast, lunch, dinner, incidental " +
                        "FROM ${travelSchema}.meal_tier mt " +
                        "INNER JOIN ${travelSchema}.meal_rate mr on mr.id = mt.id " +
                        "WHERE mr.start_date <= :date " +
                        "AND (mr.end_date IS NULL OR mr.end_date >= :date)"
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

        private Set<MealTier> tiers;

        public MealRatesHandler() {
            this.tiers = new HashSet<>();
        }

        @Override
        public void processRow(ResultSet rs) throws SQLException {
            MealTier tier = new MealTier(rs.getString("tier"), rs.getString("breakfast"),
                    rs.getString("lunch"), rs.getString("dinner"), rs.getString("incidental"));
            tiers.add(tier);
        }

        public MealRates results() {
            return new MealRates(tiers);
        }
    }
}
