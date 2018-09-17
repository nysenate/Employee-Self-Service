package gov.nysenate.ess.travel.application.allowances.meal;

import gov.nysenate.ess.core.dao.base.BaseHandler;
import gov.nysenate.ess.core.dao.base.BasicSqlQuery;
import gov.nysenate.ess.core.dao.base.DbVendor;
import gov.nysenate.ess.core.dao.base.SqlBaseDao;
import gov.nysenate.ess.travel.application.address.TravelAddress;
import gov.nysenate.ess.travel.utils.Dollars;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
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
        MapSqlParameterSource params = new MapSqlParameterSource("versionId", versionId.toString());
        String sql = SqlMealAllowanceQuery.SELECT_MEAL_TIER.getSql(schemaMap());
        MealAllowanceHandler handler = new MealAllowanceHandler();
        localNamedJdbc.query(sql, params, handler);
        return handler.getResults();
    }

    private MapSqlParameterSource mealAllowanceParams(UUID versionId, MealAllowance mealAllowance) {
        return new MapSqlParameterSource()
                .addValue("id", mealAllowance.getId().toString())
                .addValue("versionId", versionId.toString())
                .addValue("addressId", mealAllowance.getAddress().getId().toString())
                .addValue("mealRate", mealAllowance.getMealRate().toString())
                .addValue("date", toDate(mealAllowance.getDate()))
                .addValue("isMealsRequested", mealAllowance.isMealsRequested());
    }

    private enum SqlMealAllowanceQuery implements BasicSqlQuery {
        INSERT_MEAL_ALLOWANCE(
                "INSERT INTO ${travelSchema}.app_meal_allowance(id, version_id, address_id, " +
                        "meal_rate, date, is_meals_requested) \n" +
                        "VALUES(:id::uuid, :versionId::uuid, :addressId::uuid, :mealRate, :date, :isMealsRequested)"
        ),
        SELECT_MEAL_TIER(
                "SELECT m.id, m.meal_rate, m.date, m.is_meals_requested,\n" +
                        "  addr.id as addr_id, addr.street_1 as addr_street_1, addr.street_2 as addr_street_2,\n" +
                        "  addr.city as addr_city, addr.county as addr_county, addr.state as addr_state,\n" +
                        "  addr.zip_5 as addr_zip_5, addr.zip_4 as addr_zip_4 \n" +
                        "FROM ${travelSchema}.app_meal_allowance m\n" +
                        "  INNER JOIN ${travelSchema}.address addr on m.address_id = addr.id\n" +
                        "WHERE m.version_id = :versionId::uuid\n" +
                        "ORDER BY date ASC;\n"
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

    private class MealAllowanceHandler extends BaseHandler {

        private List<MealAllowance> mealAllowances = new ArrayList<>();

        @Override
        public void processRow(ResultSet rs) throws SQLException {
            TravelAddress address = new TravelAddress(UUID.fromString(rs.getString("addr_id")));
            address.setAddr1(rs.getString("addr_street_1"));
            address.setAddr2(rs.getString("addr_street_2"));
            address.setCity(rs.getString("addr_city"));
            address.setCounty(rs.getString("addr_county"));
            address.setState(rs.getString("addr_state"));
            address.setZip5(rs.getString("addr_zip_5"));
            address.setZip4(rs.getString("addr_zip_4"));

            MealAllowance mealAllowance = new MealAllowance(UUID.fromString(rs.getString("id")),
                    address, getLocalDateFromRs(rs, "date"), new Dollars(rs.getString("meal_rate")),
                    rs.getBoolean("is_meals_requested"));

            mealAllowances.add(mealAllowance);
        }

        public MealAllowances getResults() {
            return new MealAllowances(mealAllowances);
        }
    }
}
