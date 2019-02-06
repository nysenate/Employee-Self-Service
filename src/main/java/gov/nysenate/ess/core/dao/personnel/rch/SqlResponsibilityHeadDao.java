package gov.nysenate.ess.core.dao.personnel.rch;

import com.google.common.base.Preconditions;
import gov.nysenate.ess.core.dao.base.BasicSqlQuery;
import gov.nysenate.ess.core.dao.base.DbVendor;
import gov.nysenate.ess.core.dao.base.SqlBaseDao;
import gov.nysenate.ess.core.dao.personnel.mapper.RespHeadRowMapper;
import gov.nysenate.ess.core.model.personnel.ResponsibilityHead;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class SqlResponsibilityHeadDao extends SqlBaseDao {

    /**
     * Retrieves the {@link ResponsibilityHead} identified by the given {@code code}.
     *
     * @param code The code which uniquely identifies a ResponsibilityHead. Cannot be null or empty.
     * @return The {@link ResponsibilityHead} with the given {@code code}.
     */
    public ResponsibilityHead rchForCode(String code) {
        Preconditions.checkArgument(code != null && !code.isEmpty());
        MapSqlParameterSource params = new MapSqlParameterSource("code", code);
        String sql = SqlResponsibilityHeadQuery.RCH_BY_CODE.getSql(schemaMap());
        return remoteNamedJdbc.queryForObject(sql, params, new RespHeadRowMapper(""));
    }

    /**
     * Retrieves all {@link ResponsibilityHead}'s identified by the {@code codes} provided.
     *
     * @param codes A List of rch codes. Invalid rch codes are ignored.
     * @return A List of ResponsibilityHead's matching the provided codes. An empty list is returned
     * if {@code codes} is empty or contained all invalid codes.
     */
    public List<ResponsibilityHead> rchsForCodes(List<String> codes) {
        if (codes.isEmpty()) {
            return new ArrayList<>();
        }
        MapSqlParameterSource params = new MapSqlParameterSource("codes", codes);
        String sql = SqlResponsibilityHeadQuery.RCHS_BY_CODES.getSql(schemaMap());
        return remoteNamedJdbc.query(sql, params, new RespHeadRowMapper(""));
    }

    private enum SqlResponsibilityHeadQuery implements BasicSqlQuery {
        RCH_BY_CODE(
                "SELECT CDRESPCTRHD, CDSTATUS, CDAFFILIATE, DERESPCTRHDS, FFDERESPCTRHDF \n" +
                        "FROM ${masterSchema}.SL16RSPCTRHD \n" +
                        "WHERE CDRESPCTRHD = :code"
        ),
        RCHS_BY_CODES(
                "SELECT CDRESPCTRHD, CDSTATUS, CDAFFILIATE, DERESPCTRHDS, FFDERESPCTRHDF \n" +
                        "FROM ${masterSchema}.SL16RSPCTRHD \n" +
                        "WHERE CDRESPCTRHD IN (:codes)"
        );

        private String sql;

        SqlResponsibilityHeadQuery(String sql) {
            this.sql = sql;
        }

        @Override
        public String getSql() {
            return sql;
        }

        @Override
        public DbVendor getVendor() {
            return DbVendor.ORACLE_10g;
        }
    }
}
