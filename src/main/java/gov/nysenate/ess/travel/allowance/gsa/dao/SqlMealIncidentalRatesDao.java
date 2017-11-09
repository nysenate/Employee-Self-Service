package gov.nysenate.ess.travel.allowance.gsa.dao;

import gov.nysenate.ess.core.dao.base.BaseRowMapper;
import gov.nysenate.ess.core.dao.base.BasicSqlQuery;
import gov.nysenate.ess.core.dao.base.DbVendor;
import gov.nysenate.ess.core.dao.base.SqlBaseDao;
import gov.nysenate.ess.travel.allowance.gsa.model.MealIncidentalRate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
@Transactional
public class SqlMealIncidentalRatesDao extends SqlBaseDao implements MealIncidentalRatesDao {

    public MealIncidentalRate[] getMealIncidentalRates(){
        String sql = SqlMealIncidentalRateQuery.GET_RATES.getSql(schemaMap());

        return null;
    }

    @Override
    public void insertMealIncidentalRates(MealIncidentalRate[] mealIncidentalRates) {
        List<SqlParameterSource> paramList = new ArrayList<>();
        for (MealIncidentalRate mealIncidentalRate : mealIncidentalRates) {
            MapSqlParameterSource params = new MapSqlParameterSource()
                    .addValue("totalCost", mealIncidentalRate.getTotalCost())
                    .addValue("breakfastCost", mealIncidentalRate.getBreakfastCost())
                    .addValue("dinnerCost", mealIncidentalRate.getDinnerCost())
                    .addValue("incidentalCost", mealIncidentalRate.getIncidentalCost());
            paramList.add(params);
        }

        String sql = SqlMealIncidentalRateQuery.INSERT_RATE.getSql(schemaMap());
        SqlParameterSource[] batchParams = new SqlParameterSource[paramList.size()];
        batchParams = paramList.toArray(batchParams);
        localNamedJdbc.batchUpdate(sql, batchParams);
    }

    @Override
    @Transactional(value = "localTxManager")
    public synchronized void updateMealIncidentalRates(MealIncidentalRate[] mealIncidentalRates) {
        String sql = SqlMealIncidentalRateQuery.TRUNCATE_TABLE.getSql(schemaMap());
        localNamedJdbc.query(sql, new MealIncidentalRateRowMapper());
    }

    private enum SqlMealIncidentalRateQuery implements BasicSqlQuery {
        GET_RATES(
                "SELECT * FROM ${travelSchema}.meal_incidental_rates"),

        INSERT_RATE(
                "INSERT INTO ${travelSchema}.meal_incidental_rates \n" +
                        "VALUES (:totalCost, :breakfastCost, :dinnerCost, :incidentalCost)"
        ),
        TRUNCATE_TABLE(
                "TRUNCATE ${travelSchema}.meal_incidental_rates"
        );

        private String sql;

        SqlMealIncidentalRateQuery(String sql) {
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

    private class MealIncidentalRateRowMapper extends BaseRowMapper<MealIncidentalRate> {

        @Override
        public MealIncidentalRate mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new MealIncidentalRate(rs.getInt("totalCost"), rs.getInt("breakfastCost"),
                    rs.getInt("dinnerCost"), rs.getInt("incidentalCost"));
        }
    }
}
