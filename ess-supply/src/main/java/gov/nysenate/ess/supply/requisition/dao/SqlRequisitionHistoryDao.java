package gov.nysenate.ess.supply.requisition.dao;

import gov.nysenate.ess.core.dao.base.BaseHandler;
import gov.nysenate.ess.core.dao.base.BasicSqlQuery;
import gov.nysenate.ess.core.dao.base.DbVendor;
import gov.nysenate.ess.core.dao.base.SqlBaseDao;
import gov.nysenate.ess.supply.requisition.RequisitionVersion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.SortedMap;
import java.util.TreeMap;

@Repository
public class SqlRequisitionHistoryDao extends SqlBaseDao {

    @Autowired private SqlRequisitionVersionDao versionDao;

    protected void insertRequisitionHistory(int requisitionId, int versionId, LocalDateTime createdDateTime) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("requisitionId", requisitionId)
                .addValue("versionId", versionId)
                .addValue("createdDateTime", toDate(createdDateTime));
        String sql = SqlRequisitionHistoryQuery.INSERT_REQUISITION_HISTORY.getSql(schemaMap());
        localNamedJdbc.update(sql, params);
    }

    protected SortedMap<LocalDateTime, RequisitionVersion> getRequisitionHistory(int requisitionId) {
        MapSqlParameterSource params = new MapSqlParameterSource("requisitionId", requisitionId);
        String sql = SqlRequisitionHistoryQuery.GET_HISTORY_BY_ID.getSql(schemaMap());
        RequisitionHistoryHandler handler = new RequisitionHistoryHandler(versionDao);
        localNamedJdbc.query(sql, params, handler);
        return handler.getRequisitionHistory();
    }

    private enum SqlRequisitionHistoryQuery implements BasicSqlQuery {
        INSERT_REQUISITION_HISTORY(
                "INSERT INTO ${supplySchema}.requisition_history(requisition_id, version_id, created_date_time) \n" +
                "VALUES (:requisitionId, :versionId, :createdDateTime)"
        ),
        GET_HISTORY_BY_ID(
                "SELECT version_id, created_date_time \n" +
                "FROM ${supplySchema}.requisition_history \n" +
                "WHERE requisition_id = :requisitionId"
        );

        private String sql;

        SqlRequisitionHistoryQuery(String sql) {
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

    private class RequisitionHistoryHandler extends BaseHandler {

        private SqlRequisitionVersionDao versionDao;
        private SortedMap<LocalDateTime, RequisitionVersion> versionMap;

        RequisitionHistoryHandler(SqlRequisitionVersionDao versionDao) {
            this.versionDao = versionDao;
            this.versionMap = new TreeMap<>();
        }

        @Override
        public void processRow(ResultSet rs) throws SQLException {
            int versionId = rs.getInt("version_id");
            LocalDateTime dateTime = getLocalDateTimeFromRs(rs, "created_date_time");
            RequisitionVersion version = versionDao.getVersionById(versionId);
            versionMap.put(dateTime, version);
        }

        SortedMap<LocalDateTime, RequisitionVersion> getRequisitionHistory() {
            return versionMap;
        }
    }
}
