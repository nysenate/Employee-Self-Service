package gov.nysenate.ess.supply.requisition.dao;

import gov.nysenate.ess.core.dao.base.BasicSqlQuery;
import gov.nysenate.ess.core.dao.base.DbVendor;
import gov.nysenate.ess.core.dao.base.SqlBaseDao;
import gov.nysenate.ess.supply.requisition.Requisition;
import gov.nysenate.ess.supply.requisition.RequisitionVersion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Map;

@Repository
public class SqlRequisitionDao extends SqlBaseDao implements RequisitionDao {

    @Autowired private SqlRequisitionHistoryDao historyDao;
    @Autowired private SqlRequisitionVersionDao versionDao;
    @Autowired private SqlReqLineItemDao lineItemDao;

    @Override
    public int saveRequisition(Requisition requisition) {
        int requisitionId = 0;
        for (Map.Entry<LocalDateTime, RequisitionVersion> entry: requisition.getHistory().entrySet()) {
            int versionId = versionDao.insertRequisitionVersion(entry.getValue());
            lineItemDao.insertVersionLineItems(entry.getValue(), versionId);
            requisitionId = saveRequisitionInfo(requisition, versionId);
            historyDao.insertRequisitionHistory(requisitionId, versionId, entry.getKey());
        }
        return requisitionId;
    }

    private int saveRequisitionInfo(Requisition requisition, int activeVersionId) {
        boolean isUpdated = updateRequisitionInfo(requisition, activeVersionId);
        if (isUpdated) {
            return requisition.getId();
        }
        else {
            return insertRequisitionInfo(requisition, activeVersionId);
        }
    }

    private int insertRequisitionInfo(Requisition requisition, int activeVersionId) {
        MapSqlParameterSource params = getRequisitionParams(requisition, activeVersionId);
        String sql = SqlRequisitionQuery.INSERT_REQUISITION.getSql(schemaMap());
        KeyHolder keyHolder = new GeneratedKeyHolder();
        localNamedJdbc.update(sql, params, keyHolder);
        return (Integer) keyHolder.getKeys().get("requisition_id");
    }

    private boolean updateRequisitionInfo(Requisition requisition, int activeVersionId) {
        MapSqlParameterSource params = getRequisitionParams(requisition, activeVersionId);
        String sql = SqlRequisitionQuery.UPDATE_REQUISITION.getSql(schemaMap());
        return localNamedJdbc.update(sql, params) != 0;
    }

    private MapSqlParameterSource getRequisitionParams(Requisition requisition, int activeVersionId) {
        return new MapSqlParameterSource()
                .addValue("activeVersionId", activeVersionId)
                .addValue("orderedDateTime", toDate(requisition.getOrderedDateTime()))
                .addValue("processedDateTime", requisition.getProcessedDateTime().map(SqlBaseDao::toDate).orElse(null))
                .addValue("completedDateTime", requisition.getCompletedDateTime().map(SqlBaseDao::toDate).orElse(null))
                .addValue("approvedDateTime", requisition.getApprovedDateTime().map(SqlBaseDao::toDate).orElse(null))
                .addValue("rejectedDateTime", requisition.getRejectedDateTime().map(SqlBaseDao::toDate).orElse(null));
    }

    @Override
    public Requisition getRequisition(int requisitionId) {
        return null;
    }

    private enum SqlRequisitionQuery implements BasicSqlQuery {
        INSERT_REQUISITION(
                "INSERT INTO ${supplySchema}.requisition(active_version_id, ordered_date_time, processed_date_time, \n" +
                "completed_date_time, approved_date_time, rejected_date_time) VALUES (:activeVersionId, :orderedDateTime, \n" +
                ":processedDateTime, :completedDateTime, :approvedDateTime, :rejectedDateTime)"
        ),
        UPDATE_REQUISITION(
                "UPDATE ${supplySchema}.requisition SET active_version_id = :activeVersionId, ordered_date_time = :orderedDateTime, \n" +
                "processed_date_time = :processedDateTime, completed_date_time = :completedDateTime, \n" +
                "approved_date_time = :approvedDateTime, rejected_date_time = :rejectedDateTime"
        );

        private String sql;

        SqlRequisitionQuery(String sql) {
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
}
