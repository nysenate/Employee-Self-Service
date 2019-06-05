package gov.nysenate.ess.travel.provider.gsa;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.nysenate.ess.core.dao.base.BaseRowMapper;
import gov.nysenate.ess.core.dao.base.BasicSqlQuery;
import gov.nysenate.ess.core.dao.base.DbVendor;
import gov.nysenate.ess.core.dao.base.SqlBaseDao;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Month;
import java.util.Map;

@Repository
public class SqlGsaBatchResponseDao extends SqlBaseDao implements GsaBatchResponseDao {

    private ObjectMapper objectMapper = new ObjectMapper();

    public void handleNewData(GsaResponse gsaResponse) throws JsonProcessingException, NullPointerException, DataAccessException {
        try {
            insertGsaData(gsaResponse);
        }
        catch (DuplicateKeyException e) {
            updateGsaData(gsaResponse);
        }
    }

    private void insertGsaData(GsaResponse gsaResponse) throws JsonProcessingException {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("fiscalYear", gsaResponse.getId().getFiscalYear())
                .addValue("zipcode", gsaResponse.getId().getZipcode())
                .addValue("mealTier", gsaResponse.getMealTier())
                .addValue("lodgingRates", objectMapper.writeValueAsString(gsaResponse.getLodgingRates()) )
                .addValue("city", gsaResponse.getCity())
                .addValue("county", gsaResponse.getCounty());
        localNamedJdbc.update(SqlGsaBatchResponseQuery.INSERT_GSA_DATA.getSql(), params);
    }

    private void updateGsaData(GsaResponse gsaResponse) throws JsonProcessingException {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("mealTier", gsaResponse.getMealTier() )
                .addValue("lodgingRates", objectMapper.writeValueAsString(gsaResponse.getLodgingRates() ))
                .addValue("fiscalYear",gsaResponse.getId().getFiscalYear())
                .addValue("zipcode",gsaResponse.getId().getZipcode());
        localNamedJdbc.update(SqlGsaBatchResponseQuery.UPDATE_GSA_DATA.getSql(), params);
    }

    public GsaResponse getGsaData(GsaResponseId gsaResponseId) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("zipcode", gsaResponseId.getZipcode())
                .addValue("fiscalYear", gsaResponseId.getFiscalYear());
        return localNamedJdbc.queryForObject(SqlGsaBatchResponseQuery.GET_GSA_DATA.getSql(), params,
                new GsaInfoRowMapper());
    }

    private enum SqlGsaBatchResponseQuery implements BasicSqlQuery {

        INSERT_GSA_DATA ("insert into travel.gsa_data (fiscalYear, zipcode, mealTier, lodgingRates, city, county)\n" +
                "    values (:fiscalYear, :zipcode, :mealTier, :lodgingRates, :city, :county);"),

        UPDATE_GSA_DATA("update travel.gsa_data\n" +
                "    set mealTier = :mealTier, lodgingRates = :lodgingRates\n" +
                "    where fiscalYear = :fiscalYear and zipcode = :zipcode;"),

        GET_GSA_DATA("select * from travel.gsa_data where zipcode = :zipcode and fiscalYear = :fiscalYear;"),
        ;

        private String sql;

        SqlGsaBatchResponseQuery(String sql) {
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

    private class GsaInfoRowMapper extends BaseRowMapper<GsaResponse> {
        @Override
        public GsaResponse mapRow(ResultSet rs, int rowNum) throws SQLException {
            GsaResponse gsaResponse;
            int fiscalYear = rs.getInt("fiscalYear");
            String zipcode = rs.getString("zipcode");
            String mealTier = rs.getString("mealTier");
            try {
                Map<Month, BigDecimal> lodgingRates =
                        objectMapper.readValue( rs.getString("lodgingRates"),
                                new TypeReference<Map<Month, BigDecimal>>() {} );
                gsaResponse = new GsaResponse(new GsaResponseId(fiscalYear, zipcode), lodgingRates, mealTier);
                gsaResponse.setCity(rs.getString("city"));
                gsaResponse.setCounty(rs.getString("county"));
            }
            catch (Exception e) {
                throw new SQLException(e);
            }
            return gsaResponse;
        }
    }
}
