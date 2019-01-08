package gov.nysenate.ess.travel.provider.miles;

import gov.nysenate.ess.core.dao.base.BaseRowMapper;
import gov.nysenate.ess.core.dao.base.BasicSqlQuery;
import gov.nysenate.ess.core.dao.base.DbVendor;
import gov.nysenate.ess.core.dao.base.SqlBaseDao;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

@Repository
public class IrsMileageRateDao extends SqlBaseDao {

    public void insertIrsRate(MileageRate mileageRate) {
        MapSqlParameterSource params = new MapSqlParameterSource("rate", mileageRate.getRate());
        params.addValue("startDate", toDate(mileageRate.getStartDate()));
        params.addValue("endDate", toDate(mileageRate.getEndDate()));
        String sql = IrsMileageRateDao.SqlIrsRateQuery.INSERT_MILEAGE_RATE.getSql(schemaMap());
        localNamedJdbc.update(sql, params);
    }

    public void updateEndDate(LocalDate oldStartDate, LocalDate newEndDate) {
        MapSqlParameterSource params = new MapSqlParameterSource("old_start_date", Date.valueOf(oldStartDate));
        params.addValue("new_end_date", toDate(newEndDate));
        String sql = SqlIrsRateQuery.UPDATE_END_DATE.getSql(schemaMap());
        localNamedJdbc.update(sql, params);
    }

    public MileageRate getMileageRate(LocalDate startDate) {
        MapSqlParameterSource params = new MapSqlParameterSource("date", toDate(startDate));
        String sql = SqlIrsRateQuery.GET_MILEAGE_RATE.getSql(schemaMap());
        IrsMileageRateDao.MileageRateMapper mapper = new MileageRateMapper();
        return localNamedJdbc.queryForObject(sql, params, mapper);
    }

    private enum SqlIrsRateQuery implements BasicSqlQuery {

        INSERT_MILEAGE_RATE(
                "INSERT INTO ${travelSchema}.irs_mileage_rate\n" +
                        "VALUES (:startDate, :endDate, :rate)"
        ),
        UPDATE_END_DATE (
                "UPDATE ${travelSchema}.irs_mileage_rate\n" +
                        "set end_date = :new_end_date\n" +
                        "where start_date = :old_start_date;"
        ),
        GET_MILEAGE_RATE(
                "SELECT m.start_date, m.end_date, m.rate \n " +
                        "FROM ${travelSchema}.irs_mileage_rate m \n " +
                        "WHERE :date BETWEEN m.start_date and m.end_date"
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

    private class MileageRateMapper extends BaseRowMapper<MileageRate> {

        @Override
        public MileageRate mapRow(ResultSet resultSet, int i) throws SQLException {
            return new MileageRate(
                    resultSet.getDate("start_date").toLocalDate(),
                    resultSet.getDate("end_date").toLocalDate(),
                    resultSet.getString("rate").replaceAll("$", ""));
        }
    }
}
