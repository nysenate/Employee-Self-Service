package gov.nysenate.ess.travel.provider.senate;

import gov.nysenate.ess.core.dao.base.BasicSqlQuery;
import gov.nysenate.ess.core.dao.base.DbVendor;
import gov.nysenate.ess.core.dao.base.SqlBaseDao;
import gov.nysenate.ess.travel.utils.Dollars;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Repository
public class SqlSenateMieDao extends SqlBaseDao {

    public SenateMie selectSenateMie(int senateMieId) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("senateMieId", senateMieId);
        String sql = SqlSenateMieQuery.SELECT_SENATE_MIE_BY_ID.getSql(schemaMap());
        List<SenateMie> senateMiles = localNamedJdbc.query(sql, params, new SenateMieRowMapper());
        if (senateMiles.isEmpty() || senateMiles == null) {
            throw new IncorrectResultSizeDataAccessException(0);
        }
        else {
            return senateMiles.get(0);
        }
    }

    public SenateMie selectSenateMie(int fiscalYear, Dollars total) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("fiscalYear", fiscalYear)
                .addValue("total", total.toString());
        String sql = SqlSenateMieQuery.SELECT_SENATE_MIE.getSql(schemaMap());
        List<SenateMie> senateMiles = localNamedJdbc.query(sql, params, new SenateMieRowMapper());
        if (senateMiles.isEmpty() || senateMiles == null) {
            throw new IncorrectResultSizeDataAccessException(0);
        }
        else {
            return senateMiles.get(0);
        }
    }

    /**
     * Save all SenateMie's in the given collection.
     */
    public void saveGsaMies(Collection<SenateMie> mies) {
        List<SqlParameterSource> paramList = new ArrayList<>();
        for (SenateMie mie : mies) {
            MapSqlParameterSource params = new MapSqlParameterSource()
                    .addValue("fiscalYear", mie.getFiscalYear())
                    .addValue("total", mie.total().toString())
                    .addValue("breakfast", mie.breakfast().toString())
                    .addValue("dinner", mie.dinner().toString());
            paramList.add(params);
        }
        String sql = SqlSenateMieQuery.INSERT_SENATE_MIE.getSql(schemaMap());
        SqlParameterSource[] batchParams = new SqlParameterSource[paramList.size()];
        batchParams = paramList.toArray(batchParams);
        localNamedJdbc.batchUpdate(sql, batchParams);
    }

    private enum SqlSenateMieQuery implements BasicSqlQuery {
        INSERT_SENATE_MIE(
                "INSERT INTO ${travelSchema}.senate_mie(fiscal_year, total, breakfast, dinner)\n" +
                        " VALUES (:fiscalYear, :total, :breakfast, :lunch)"
        ),
        SELECT_SENATE_MIE_BY_ID(
                "SELECT * FROM ${travelSchema}.senate_mie\n" +
                        "WHERE senate_mie_id = :senateMieId"
        ),
        SELECT_SENATE_MIE(
                "SELECT * FROM ${travelSchema}.senate_mie\n" +
                        "WHERE fiscal_year = :fiscalYear AND total = :total"
        );

        private final String sql;

        SqlSenateMieQuery(String sql) {
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

    private static class SenateMieRowMapper implements RowMapper<SenateMie> {

        @Override
        public SenateMie mapRow(ResultSet rs, int i) throws SQLException {
            return new SenateMie(
                    rs.getInt("senate_mie_id"),
                    rs.getInt("fiscal_year"),
                    new Dollars(rs.getString("total")),
                    new Dollars(rs.getString("breakfast")),
                    new Dollars(rs.getString("dinner"))
            );
        }
    }
}
