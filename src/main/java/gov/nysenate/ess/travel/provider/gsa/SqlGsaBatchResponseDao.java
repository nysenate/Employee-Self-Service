package gov.nysenate.ess.travel.provider.gsa;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.nysenate.ess.core.dao.base.BaseRowMapper;
import gov.nysenate.ess.core.dao.base.BasicSqlQuery;
import gov.nysenate.ess.core.dao.base.DbVendor;
import gov.nysenate.ess.core.dao.base.SqlBaseDao;
import gov.nysenate.ess.travel.provider.gsa.model.GsaInfo;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Month;
import java.util.List;
import java.util.Map;

@Repository
public class SqlGsaBatchResponseDao extends SqlBaseDao implements GsaBatchResponseDao {

    private ObjectMapper objectMapper = new ObjectMapper();

    public void handleNewData(GsaResponse gsaResponse) throws JsonProcessingException, NullPointerException, DataAccessException {
        try {
            insertGsaData(gsaResponse);
        } catch (DuplicateKeyException e) {
            updateGsaData(gsaResponse);
        }
    }

    private void insertGsaData(GsaResponse gsaResponse) throws JsonProcessingException {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("fiscalYear", gsaResponse.getId().getFiscalYear())
                .addValue("zipcode", gsaResponse.getId().getZipcode())
                .addValue("mealTier", gsaResponse.getMealTier())
                .addValue("lodgingRates", objectMapper.writeValueAsString(gsaResponse.getLodgingRates()))
                .addValue("city", gsaResponse.getCity())
                .addValue("county", gsaResponse.getCounty());
        localNamedJdbc.update(SqlGsaBatchResponseQuery.INSERT_GSA_DATA.getSql(), params);
    }

    public void insertGsaArchiveData(List<GsaInfo> archivedGsaData) throws DataAccessException, JsonProcessingException {
        for (GsaInfo gsaInfo : archivedGsaData) {
            try {
                MapSqlParameterSource params = new MapSqlParameterSource()
                        .addValue("fiscalYear", gsaInfo.getFiscalYear())
                        .addValue("zipcode", gsaInfo.getZipCode())
                        .addValue("city", gsaInfo.getCity() != null ? gsaInfo.getCity() : "")
                        .addValue("county", gsaInfo.getCounty() != null ? gsaInfo.getCounty() : "")
                        .addValue("mealTier", gsaInfo.getMeals())
                        .addValue("lodgingRates", objectMapper.writeValueAsString(gsaInfo.getLodgingRates()));

                localNamedJdbc.update(SqlGsaBatchResponseQuery.INSERT_GSA_ARCHIVE_DATA.getSql(), params);
            } catch (DataAccessException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public GsaResponse getGsaRow(String zip5) throws DataAccessException {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("zipcode", zip5);
        System.out.println(zip5);
        String sql = SqlGsaBatchResponseQuery.GET_ARCHIVED_GSA_DATA.getSql();

        List<GsaResponse> gsaResponseList = localNamedJdbc.query(sql, params, new GsaInfoRowMapper());
        return gsaResponseList.get(0);
    }

    private void updateGsaData(GsaResponse gsaResponse) throws JsonProcessingException {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("mealTier", gsaResponse.getMealTier())
                .addValue("lodgingRates", objectMapper.writeValueAsString(gsaResponse.getLodgingRates()))
                .addValue("fiscalYear", gsaResponse.getId().getFiscalYear())
                .addValue("zipcode", gsaResponse.getId().getZipcode());
        localNamedJdbc.update(SqlGsaBatchResponseQuery.UPDATE_GSA_DATA.getSql(), params);
    }

    public GsaResponse getGsaData(GsaResponseId gsaResponseId) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("zipcode", gsaResponseId.getZipcode())
                .addValue("fiscalYear", gsaResponseId.getFiscalYear());
        List<GsaResponse> gsaResponseList = localNamedJdbc.query(SqlGsaBatchResponseQuery.GET_GSA_DATA.getSql(), params,
                new GsaInfoRowMapper());

        if (gsaResponseList.isEmpty() || gsaResponseList == null) {
            List<GsaResponse> getGsaResponseCity = localNamedJdbc.query(SqlGsaBatchResponseQuery.GET_GSA_DATA.getSql(), params, new GsaInfoRowMapper());

            List<GsaResponse> gsaArchivedList = localNamedJdbc.query(SqlGsaBatchResponseQuery.GET_ARCHIVED_GSA_DATA.getSql(), params, new GsaInfoRowMapper());

            if (gsaArchivedList.isEmpty() || gsaArchivedList == null) {
                throw new IncorrectResultSizeDataAccessException(0);
            } else {
                return gsaArchivedList.get(0);
            }
        }

        return gsaResponseList.get(0);

    }

    private enum SqlGsaBatchResponseQuery implements BasicSqlQuery {

        INSERT_GSA_DATA("insert into travel.gsa_data (fiscalYear, zipcode, mealTier, lodgingRates, city, county)\n" +
                "    values (:fiscalYear, :zipcode, :mealTier, :lodgingRates, :city, :county);"),

        INSERT_GSA_ARCHIVE_DATA("INSERT INTO travel.gsa_archive (fiscalYear, zipcode, mealtier, lodgingRates, city, county)\n" +
                " VALUES (:fiscalYear, :zipcode, :mealTier, :lodgingRates, :city, :county)" +
                " ON CONFLICT (city, county, fiscalYear, zipcode) DO NOTHING;"),

        UPDATE_GSA_DATA("update travel.gsa_data\n" +
                "    set mealTier = :mealTier, lodgingRates = :lodgingRates\n" +
                "    where fiscalYear = :fiscalYear and zipcode = :zipcode;"),

        GET_GSA_DATA("select * from travel.gsa_data where zipcode = :zipcode and fiscalYear = :fiscalYear;"),

        GET_ARCHIVED_GSA_DATA("SELECT * FROM travel.gsa_archive WHERE zipcode = :zipcode"),
        ;

        private final String sql;

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
                        objectMapper.readValue(rs.getString("lodgingRates"),
                                new TypeReference<>() {
                                });
                gsaResponse = new GsaResponse(new GsaResponseId(fiscalYear, zipcode), lodgingRates, mealTier);
                gsaResponse.setCity(rs.getString("city"));
                gsaResponse.setCounty(rs.getString("county"));
            } catch (Exception e) {
                throw new SQLException(e);
            }
            return gsaResponse;
        }
    }
}
