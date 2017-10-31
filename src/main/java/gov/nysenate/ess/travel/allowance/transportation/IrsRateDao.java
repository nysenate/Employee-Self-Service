package gov.nysenate.ess.travel.allowance.transportation;

import gov.nysenate.ess.core.dao.base.*;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class IrsRateDao extends SqlBaseDao {

    public void updateIrsRate(double rate) {
        MapSqlParameterSource params = new MapSqlParameterSource("rate", rate);
        String sql = IrsRateDao.SqlIrsRateQuery.UPDATE_IRS_RATE.getSql(schemaMap());
        localNamedJdbc.update(sql, params);
    }

    public double getIrsRate() {
        String sql = IrsRateDao.SqlIrsRateQuery.GET_IRS_RATE.getSql(schemaMap());
        IrsRateDao.IrsRateMapper mapper = new IrsRateMapper();
        return localNamedJdbc.query(sql, mapper).get(0);
    }

    private enum SqlIrsRateQuery implements BasicSqlQuery {

        UPDATE_IRS_RATE(
                "UPDATE ${travelSchema}.irs_rate\n" +
                "SET irs_travel_rate = :rate"
        ),
        GET_IRS_RATE(
                "SELECT irs_travel_rate\n" +
                "FROM ${travelSchema}.irs_rate"
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
