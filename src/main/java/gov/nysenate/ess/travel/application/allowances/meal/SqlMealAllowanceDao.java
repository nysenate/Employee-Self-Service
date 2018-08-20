package gov.nysenate.ess.travel.application.allowances.meal;

import gov.nysenate.ess.core.dao.base.BasicSqlQuery;
import gov.nysenate.ess.core.dao.base.DbVendor;
import gov.nysenate.ess.core.dao.base.SqlBaseDao;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
public class SqlMealAllowanceDao extends SqlBaseDao implements MealAllowanceDao {

    @Override
    @Transactional(value = "localTxManager")
    public void insertMealAllowances(UUID versionId, MealAllowances mealAllowances) {
        List<SqlParameterSource> paramList = new ArrayList<>();
        for (MealAllowance mealAllowance : mealAllowances.getMealAllowances()) {
            paramList.add(mealAllowanceParams(versionId, mealAllowance));
        }
        String sql = SqlMealAllowanceQuery.INSERT_MEAL_ALLOWANCE.getSql(schemaMap());
        SqlParameterSource[] batchParams = new SqlParameterSource[paramList.size()];
        batchParams = paramList.toArray(batchParams);
        localNamedJdbc.batchUpdate(sql, batchParams);
    }

    @Override
    public MealAllowances getMealAllowances(UUID versionId) {
        // TODO do this after meal tier changes are merged in?
        return null;
    }

    private MapSqlParameterSource mealAllowanceParams(UUID versionId, MealAllowance mealAllowance) {
        return new MapSqlParameterSource()
                .addValue("id", mealAllowance.getId().toString())
                .addValue("versionId", versionId.toString())
                .addValue("addressId", mealAllowance.getAddress().getId().toString())
                .addValue("mealTierId", mealAllowance.getMealTier().getId().toString())
                .addValue("date", toDate(mealAllowance.getDate()))
                .addValue("isMealsRequested", mealAllowance.isMealsRequested());
    }

    private enum SqlMealAllowanceQuery implements BasicSqlQuery {
        INSERT_MEAL_ALLOWANCE(
                "INSERT INTO ${travelSchema}.app_meal_allowance(id, version_id, address_id, " +
                        "meal_tier_id, date, is_meals_requested) \n" +
                        "VALUES(:id::uuid, :versionId::uuid, :addressId::uuid, :mealTierId::uuid, :date, :isMealsRequested)"
        )
        ;

        private String sql;

        SqlMealAllowanceQuery(String sql) {
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
}
