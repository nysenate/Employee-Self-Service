package gov.nysenate.ess.travel.allowance.mileage;

import gov.nysenate.ess.core.dao.base.*;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

@Repository
public class IrsRateDao extends SqlBaseDao {

    public void insertIrsRate(String startDate, String endDate, double rate) {
        MapSqlParameterSource params = new MapSqlParameterSource("rate", rate);
        params.addValue("startDate", Date.valueOf(startDate));
        params.addValue("endDate", Date.valueOf(endDate));
        String sql = IrsRateDao.SqlIrsRateQuery.INSERT_IRS_RATE.getSql(schemaMap());
        localNamedJdbc.update(sql, params);
    }

    public double getIrsRate(LocalDate queryDate) {
        MapSqlParameterSource params = new MapSqlParameterSource("queryDate", Date.valueOf(queryDate));
        String sql = IrsRateDao.SqlIrsRateQuery.GET_IRS_RATE.getSql(schemaMap());
        IrsRateDao.IrsRateMapper mapper = new IrsRateMapper();

        double rate;
        try {
            rate = localNamedJdbc.query(sql, params, mapper).get(0);
        }
        catch(IndexOutOfBoundsException e){
            rate = -2;
        }
        return rate;
    }

    private enum SqlIrsRateQuery implements BasicSqlQuery {

        INSERT_IRS_RATE(
                "INSERT INTO ${travelSchema}.irs_rate\n" +
                "VALUES (:startDate, :endDate, :rate)"
        ),
        GET_IRS_RATE(
                "SELECT irs_travel_rate\n" +
                "FROM ${travelSchema}.irs_rate\n" +
                "WHERE start_date <= :queryDate and end_date >= :queryDate"
        );

        SqlIrsRateQuery(String sql) {
            this.sql = sql;
        }

        private String sql;

        @Override
        public String getSql() {
            return this.sql;
        }

        @Override
        public DbVendor getVendor() {
            return DbVendor.POSTGRES;
        }
    }


    private class IrsRateMapper extends BaseRowMapper<Double> {

        @Override
        public Double mapRow(ResultSet resultSet, int i) throws SQLException {
            return resultSet.getDouble("irs_travel_rate");
        }
    }
}
