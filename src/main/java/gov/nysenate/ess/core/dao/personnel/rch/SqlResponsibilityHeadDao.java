package gov.nysenate.ess.core.dao.personnel.rch;

import com.google.common.base.Preconditions;
import gov.nysenate.ess.core.dao.base.PaginatedRowHandler;
import gov.nysenate.ess.core.dao.base.SqlBaseDao;
import gov.nysenate.ess.core.dao.personnel.mapper.RespHeadRowMapper;
import gov.nysenate.ess.core.model.personnel.ResponsibilityHead;
import gov.nysenate.ess.core.util.LimitOffset;
import gov.nysenate.ess.core.util.OrderBy;
import gov.nysenate.ess.core.util.PaginatedList;
import gov.nysenate.ess.core.util.SortOrder;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

import static gov.nysenate.ess.core.dao.personnel.rch.SqlResponsibilityHeadQuery.RCHS_BY_CODES;
import static gov.nysenate.ess.core.dao.personnel.rch.SqlResponsibilityHeadQuery.RCHS_SEARCH;

@Repository
public class SqlResponsibilityHeadDao extends SqlBaseDao implements ResponsibilityHeadDao {

    @Override
    public ResponsibilityHead rchForCode(String code) {
        Preconditions.checkArgument(code != null && !code.isEmpty());
        MapSqlParameterSource params = new MapSqlParameterSource("code", code);
        String sql = SqlResponsibilityHeadQuery.RCH_BY_CODE.getSql(schemaMap());
        return remoteNamedJdbc.queryForObject(sql, params, new RespHeadRowMapper(""));
    }

    @Override
    public List<ResponsibilityHead> rchsForCodes(List<String> codes) {
        if (codes.isEmpty()) {
            return new ArrayList<>();
        }
        MapSqlParameterSource params = new MapSqlParameterSource("codes", codes);
        String sql = RCHS_BY_CODES.getSql(schemaMap());
        return remoteNamedJdbc.query(sql, params, new RespHeadRowMapper(""));
    }

    @Override
    public PaginatedList<ResponsibilityHead> rchSearch(String term, LimitOffset limitOffset, SortOrder order) {
        MapSqlParameterSource params = new MapSqlParameterSource("term", term);
        OrderBy orderBy = new OrderBy("FFDERESPCTRHDF", order);
        String sql = RCHS_SEARCH.getSql(schemaMap(), orderBy, limitOffset);
        PaginatedRowHandler<ResponsibilityHead> rowHandler =
                new PaginatedRowHandler<>(limitOffset, "total", new RespHeadRowMapper(""));
        remoteNamedJdbc.query(sql, params, rowHandler);
        return rowHandler.getList();
    }

}
