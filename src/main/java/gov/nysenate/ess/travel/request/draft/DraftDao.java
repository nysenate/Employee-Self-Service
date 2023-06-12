package gov.nysenate.ess.travel.request.draft;

import gov.nysenate.ess.core.dao.base.BasicSqlQuery;
import gov.nysenate.ess.core.dao.base.DbVendor;
import gov.nysenate.ess.core.dao.base.SqlBaseDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class DraftDao extends SqlBaseDao {

    private static final Logger logger = LoggerFactory.getLogger(DraftDao.class);

    /**
     * Fetch the current users drafts records.
     *
     * @param userId The employee id of the logged in user.
     */
    public List<DraftRecord> find(int userId) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("userEmpId", userId);
        String sql = SqlDraftQuery.FIND.getSql(schemaMap());
        DraftRecordHandler handler = new DraftRecordHandler();
        localNamedJdbc.query(sql, params, handler);
        return handler.getResults();
    }

    /**
     * Persists the given DraftRecord.
     */
    public DraftRecord save(DraftRecord draftRecord) {
        MapSqlParameterSource params = draftParams(draftRecord);
        if (!update(params)) {
            int id = insert(params);
            draftRecord.id = id;
        }
        return draftRecord;
    }

    // Attempts to update a draft. Returns true if update was successful, false otherwise.
    private boolean update(MapSqlParameterSource params) {
        String sql = SqlDraftQuery.UPDATE.getSql(schemaMap());
        return localNamedJdbc.update(sql, params) == 1;
    }

    private Integer insert(MapSqlParameterSource params) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        String sql = SqlDraftQuery.INSERT.getSql(schemaMap());
        localNamedJdbc.update(sql, params);
        return (Integer) keyHolder.getKeys().get("draft_id");
    }

    /**
     * Delete the DraftRecord with the given id.
     */
    public void delete(int draftId) {
        if (draftId == 0) {
            return;
        }
        MapSqlParameterSource params = new MapSqlParameterSource("draftId", draftId);
        String sql = SqlDraftQuery.DELETE.getSql(schemaMap());
        localNamedJdbc.update(sql, params);
    }

    private MapSqlParameterSource draftParams(DraftRecord d) {
        return new MapSqlParameterSource()
                .addValue("draftId", d.id)
                .addValue("userEmpId", d.userEmpId)
                .addValue("travelerEmpId", d.travelerEmpId)
                .addValue("amendmentJson", d.amendmentJson)
                .addValue("updatedDateTime", toDate(d.updatedDateTime));
    }


    private enum SqlDraftQuery implements BasicSqlQuery {
        FIND("""
                SELECT draft_id, user_emp_id, amendment_json, traveler_emp_id, updated_date_time
                FROM ${travelSchema}.draft
                WHERE user_emp_id = :userEmpId
                ORDER BY update_date_time desc
                """
        ),
        UPDATE("""
                UPDATE ${travelSchema}.draft
                SET user_emp_id = :userEmpId,
                traveler_emp_id = :travelerEmpId,
                amendment_json = :amendmentJson
                updated_date_time = :updatedDateTime
                WHERE draft_id = draftId;
                """
        ),
        INSERT("""
                INSERT INTO ${travelSchema}.draft(user_emp_id, traveler_emp_id, amendment_json, updated_date_time)
                VALUES(:userEmpId, :travelerEmpId, :amendmentJson, :updatedDateTime)
                """
        ),
        DELETE("""
                DELETE FROM ${travelSchema}.draft
                WHERE draft_id = :draftId;
                """
        )
        ;

        private String sql;

        SqlDraftQuery(String sql) {
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

    private class DraftRecordHandler implements RowCallbackHandler {

        private List<DraftRecord> draftRecords = new ArrayList<>();

        @Override
        public void processRow(ResultSet rs) throws SQLException {
            DraftRecord d = new DraftRecord();
            d.id = rs.getInt("draft_id");
            d.userEmpId = rs.getInt("user_emp_id");
            d.travelerEmpId = rs.getInt("traveler_emp_id");
            d.amendmentJson = rs.getString("amendment_json");
            d.updatedDateTime = getLocalDateTime(rs, "updated_date_time");
            draftRecords.add(d);
        }

        public List<DraftRecord> getResults() {
            return draftRecords;
        }
    }
}
