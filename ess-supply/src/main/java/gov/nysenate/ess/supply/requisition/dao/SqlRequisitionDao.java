package gov.nysenate.ess.supply.requisition.dao;

import com.google.common.collect.Range;
import gov.nysenate.ess.core.dao.base.*;
import gov.nysenate.ess.core.util.DateUtils;
import gov.nysenate.ess.core.util.LimitOffset;
import gov.nysenate.ess.core.util.PaginatedList;
import gov.nysenate.ess.supply.requisition.Requisition;
import gov.nysenate.ess.supply.requisition.RequisitionStatus;
import gov.nysenate.ess.supply.requisition.RequisitionVersion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class SqlRequisitionDao extends SqlBaseDao implements RequisitionDao {

    @Autowired private SqlRequisitionHistoryDao historyDao;
    @Autowired private SqlRequisitionVersionDao versionDao;
    @Autowired private SqlLineItemDao lineItemDao;

    @Override
    @Transactional(value = "localTxManager")
    public int saveRequisition(Requisition requisition) {
        int requisitionId = 0;
        for (Map.Entry<LocalDateTime, RequisitionVersion> entry: requisition.getHistory().entrySet()) {
            if (entry.getValue().getId() == 0) {
                int versionId = versionDao.insertRequisitionVersion(entry.getValue());
                lineItemDao.insertVersionLineItems(entry.getValue(), versionId);
                requisitionId = saveRequisitionInfo(requisition, versionId);
                historyDao.insertRequisitionHistory(requisitionId, versionId, entry.getKey());
            }
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
                .addValue("requisitionId", requisition.getId())
                .addValue("activeVersionId", activeVersionId)
                .addValue("orderedDateTime", toDate(requisition.getOrderedDateTime()))
                .addValue("processedDateTime", requisition.getProcessedDateTime().map(SqlBaseDao::toDate).orElse(null))
                .addValue("completedDateTime", requisition.getCompletedDateTime().map(SqlBaseDao::toDate).orElse(null))
                .addValue("approvedDateTime", requisition.getApprovedDateTime().map(SqlBaseDao::toDate).orElse(null))
                .addValue("rejectedDateTime", requisition.getRejectedDateTime().map(SqlBaseDao::toDate).orElse(null))
                .addValue("modifiedDateTime", toDate(requisition.getModifiedDateTime()));
    }

    @Override
    public synchronized Requisition getRequisitionById(int requisitionId) {
        MapSqlParameterSource params = new MapSqlParameterSource("requisitionId", requisitionId);
        String sql = SqlRequisitionQuery.GET_REQUISITION_BY_ID.getSql(schemaMap());
        RequisitionRowMapper rowMapper = new RequisitionRowMapper(historyDao);
        return localNamedJdbc.queryForObject(sql, params, rowMapper);
    }

    @Override
    public synchronized PaginatedList<Requisition> searchRequisitions(String destination, String customerId, EnumSet<RequisitionStatus> statuses,
                                                         Range<LocalDateTime> dateRange, String dateField, LimitOffset limitOffset) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("destination", formatSearchString(destination))
                .addValue("customerId", formatSearchString(customerId))
                .addValue("statuses", extractEnumSetParams(statuses))
                .addValue("fromDate", toDate(DateUtils.startOfDateTimeRange(dateRange)))
                .addValue("toDate", toDate(DateUtils.endOfDateTimeRange(dateRange)));
        String sql = SqlRequisitionQuery.SEARCH_REQUISITIONS.getSql(schemaMap(), limitOffset);
        PaginatedRowHandler<Requisition> paginatedRowHandler = new PaginatedRowHandler<>(limitOffset, "total_rows", new RequisitionRowMapper(historyDao));
        localNamedJdbc.query(sql, params, paginatedRowHandler);
        return paginatedRowHandler.getList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PaginatedList<Requisition> searchOrderHistory(String destinationId, int customerId, EnumSet<RequisitionStatus> statuses,
                                                         Range<LocalDateTime> dateRange, String dateField, LimitOffset limitOffset) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("destination", formatSearchString(destinationId))
                .addValue("customerId", customerId)
                .addValue("statuses", extractEnumSetParams(statuses))
                .addValue("fromDate", toDate(DateUtils.startOfDateTimeRange(dateRange)))
                .addValue("toDate", toDate(DateUtils.endOfDateTimeRange(dateRange)));
        String sql = SqlRequisitionQuery.ORDER_HISTORY.getSql(schemaMap(), limitOffset);
        PaginatedRowHandler<Requisition> paginatedRowHandler = new PaginatedRowHandler<>(limitOffset, "total_rows", new RequisitionRowMapper(historyDao));
        localNamedJdbc.query(sql, params, paginatedRowHandler);
        return paginatedRowHandler.getList();
    }


    private String formatSearchString(String param) {
        return param != null && param.equals("all") ? "%" : param;
    }

    /** Convert an EnumSet into a Set containing each enum's name. */
    private Set<String> extractEnumSetParams(EnumSet<RequisitionStatus> statuses) {
        return statuses.stream().map(Enum::name).collect(Collectors.toSet());
    }

    private enum SqlRequisitionQuery implements BasicSqlQuery {
        INSERT_REQUISITION(
                "INSERT INTO ${supplySchema}.requisition(active_version_id, ordered_date_time, processed_date_time, \n" +
                "completed_date_time, approved_date_time, rejected_date_time, modified_date_time) \n" +
                " VALUES (:activeVersionId, :orderedDateTime, :processedDateTime, :completedDateTime, \n" +
                ":approvedDateTime, :rejectedDateTime, :modifiedDateTime)"
        ),
        UPDATE_REQUISITION(
                "UPDATE ${supplySchema}.requisition SET active_version_id = :activeVersionId, ordered_date_time = :orderedDateTime, \n" +
                "processed_date_time = :processedDateTime, completed_date_time = :completedDateTime, \n" +
                "approved_date_time = :approvedDateTime, rejected_date_time = :rejectedDateTime, modified_date_time = :modifiedDateTime \n" +
                "WHERE requisition_id = :requisitionId"
        ),
        GET_REQUISITION_BY_ID(
                "SELECT requisition_id, processed_date_time, completed_date_time, approved_date_time, \n" +
                "rejected_date_time, modified_date_time \n" +
                "FROM ${supplySchema}.requisition \n" +
                "WHERE requisition_id = :requisitionId"
        ),
        SEARCH_REQUISITIONS_BODY(
                "FROM ${supplySchema}.requisition as r \n" +
                "INNER JOIN ${supplySchema}.requisition_version as v ON r.active_version_id = v.version_id \n" +
                "WHERE v.destination LIKE :destination AND Coalesce(v.customer_id::text, '') LIKE :customerId \n" +
                "AND v.status::text IN (:statuses) AND r.modified_date_time BETWEEN :fromDate AND :toDate"
        ),
        SEARCH_REQUISITIONS_TOTAL(
                "SELECT count(r.requisition_id) " + SEARCH_REQUISITIONS_BODY.getSql()
        ),
        SEARCH_REQUISITIONS(
                "SELECT r.requisition_id, r.processed_date_time, r.completed_date_time, r.approved_date_time, \n" +
                "r.rejected_date_time, r.modified_date_time, \n" +
                "(" + SEARCH_REQUISITIONS_TOTAL.getSql() + ") as total_rows " + SEARCH_REQUISITIONS_BODY.getSql()
        ),
        ORDER_HISTORY_BODY(
                "FROM ${supplySchema}.requisition as r \n" +
                "INNER JOIN ${supplySchema}.requisition_version as v ON r.active_version_id = v.version_id \n" +
                "WHERE (v.destination = :destination OR v.customer_id = :customerId) \n" +
                "AND v.status::text IN (:statuses) AND r.ordered_date_time BETWEEN :fromDate AND :toDate"
        ),
        ORDER_HISTORY_TOTAL(
                "SELECT count(r.requisition_id) " + ORDER_HISTORY_BODY.getSql()
        ),
        ORDER_HISTORY(
                "SELECT r.requisition_id, r.processed_date_time, r.completed_date_time, r.approved_date_time, \n" +
                "r.rejected_date_time, r.modified_date_time, \n" +
                "(" + ORDER_HISTORY_TOTAL.getSql() + ") as total_rows " + ORDER_HISTORY_BODY.getSql()
        )
        ;

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

    private class RequisitionRowMapper extends BaseRowMapper<Requisition> {

        private SqlRequisitionHistoryDao historyDao;

        public RequisitionRowMapper(SqlRequisitionHistoryDao historyDao) {
            this.historyDao = historyDao;
        }

        @Override
        public Requisition mapRow(ResultSet rs, int i) throws SQLException {
            Requisition requisition = new Requisition(historyDao.getRequisitionHistory(rs.getInt("requisition_id")));
            requisition.setId(rs.getInt("requisition_id"));
            requisition.setProcessedDateTime(getLocalDateTimeFromRs(rs, "processed_date_time"));
            requisition.setCompletedDateTime(getLocalDateTimeFromRs(rs, "completed_date_time"));
            requisition.setApprovedDateTime(getLocalDateTimeFromRs(rs, "approved_date_time"));
            requisition.setRejectedDateTime(getLocalDateTimeFromRs(rs, "rejected_date_time"));
            return requisition;
        }
    }
}
