package gov.nysenate.ess.supply.requisition.dao;

import com.google.common.collect.ImmutableList;
import gov.nysenate.ess.core.dao.base.*;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.model.unit.LocationId;
import gov.nysenate.ess.core.service.base.LocationService;
import gov.nysenate.ess.core.service.personnel.EmployeeInfoService;
import gov.nysenate.ess.core.util.LimitOffset;
import gov.nysenate.ess.core.util.OrderBy;
import gov.nysenate.ess.core.util.PaginatedList;
import gov.nysenate.ess.core.util.SortOrder;
import gov.nysenate.ess.supply.requisition.model.*;
import gov.nysenate.ess.supply.util.date.DateTimeFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
public class SqlRequisitionDao extends SqlBaseDao implements RequisitionDao {

    private Logger logger = LoggerFactory.getLogger(SqlRequisitionDao.class);

    @Autowired private SqlLineItemDao lineItemDao;
    @Autowired private EmployeeInfoService employeeInfoService;
    @Autowired private LocationService locationService;
    @Autowired private DateTimeFactory dateTimeFactory;

    @Override
    @Transactional(value = "localTxManager")
    public synchronized Requisition saveRequisition(Requisition requisition) {
        requisition = requisition.setModifiedDateTime(dateTimeFactory.now());
        // Get the next revision id and set it in the requisition.
        requisition = requisition.setRevisionId(getNextRevisionId());
        requisition = saveRequisitionInfo(requisition);
        insertRequisitionContent(requisition);
        lineItemDao.insertRequisitionLineItems(requisition);
        return requisition;
    }

    /**
     * Inserts a new requisition revision into the requisition_content table.
     * @return Requisition with its revisionId set.
     */
    private void insertRequisitionContent(Requisition requisition) {
        MapSqlParameterSource params = requisitionParams(requisition);
        String sql = SqlRequisitionQuery.INSERT_REQUISITION_CONTENT.getSql(schemaMap());
        localNamedJdbc.update(sql, params);
    }

    /**
     * Saves Requisition global information to the requisition table.
     * Updates the row if it exists, otherwise inserts a new row.
     * @return the requisition with its requisitionId set.
     */
    private Requisition saveRequisitionInfo(Requisition requisition) {
        // Try to update
        MapSqlParameterSource params = requisitionParams(requisition);
        String sql = SqlRequisitionQuery.UPDATE_REQUISITION.getSql(schemaMap());
        boolean updated = localNamedJdbc.update(sql, params) == 1;
        if (!updated) {
            // If not updated, then Insert.
            MapSqlParameterSource params1 = requisitionParams(requisition);
            String sql1 = SqlRequisitionQuery.INSERT_REQUISITION.getSql(schemaMap());
            KeyHolder keyHolder = new GeneratedKeyHolder();
            localNamedJdbc.update(sql1, params1, keyHolder);
            requisition = requisition.setRequisitionId((Integer) keyHolder.getKeys().get("requisition_id"));
        }
        return requisition;
    }

    @Override
    public synchronized Optional<Requisition> getRequisitionById(int requisitionId) {
        MapSqlParameterSource params = new MapSqlParameterSource("requisitionId", requisitionId);
        String sql = SqlRequisitionQuery.GET_REQUISITION_BY_ID.getSql(schemaMap());
        RequisitionRowMapper rowMapper = new RequisitionRowMapper(employeeInfoService, locationService, lineItemDao);
        List<Requisition> results = localNamedJdbc.query(sql, params, rowMapper);
        return results.size() == 1 ? Optional.of(results.get(0)) : Optional.empty();
    }

    @Override
    public synchronized PaginatedList<Requisition> searchRequisitions(RequisitionQuery query) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("destination", query.getDestination())
                .addValue("customerId", query.getCustomerId())
                .addValue("statuses", extractEnumSetParams(query.getStatuses()))
                .addValue("fromDate", toDate(query.getFromDateTime()))
                .addValue("toDate", toDate(query.getToDateTime()))
                .addValue("issuerId", query.getIssuerId())
                .addValue("itemId", query.getItemId())
                .addValue("savedInSfms", query.getSavedInSfms())
                .addValue("isReconciled", query.getReconciled());
        String sql = generateSearchQuery(SqlRequisitionQuery.SEARCH_REQUISITIONS_PARTIAL,
                query.getDateField(), query.getOrderBy(), query.getLimitOffset());
        PaginatedRowHandler<Requisition> paginatedRowHandler = new PaginatedRowHandler<>(query.getLimitOffset(),
                "total_rows", new RequisitionRowMapper(employeeInfoService, locationService, lineItemDao));
        localNamedJdbc.query(sql, params, paginatedRowHandler);
        return paginatedRowHandler.getList();
    }

    /** Dynamically generates query date range filter using the supplied {@code dateField}.
     * Then completes the query by adding Order by and Limit Offset information. */
    private String generateSearchQuery(SqlRequisitionQuery baseSearchQuery, String dateField, OrderBy orderBy, LimitOffset limoff) {
        String sql = baseSearchQuery.getSql(schemaMap()) + dateField + " BETWEEN :fromDate AND :toDate";
        sql = SqlQueryUtils.withOrderByClause(sql, orderBy);
        return SqlQueryUtils.withLimitOffsetClause(sql, limoff, baseSearchQuery.getVendor());
    }

    /**
     * {@inheritDoc}
     * @param query
     */
    @Override
    public synchronized PaginatedList<Requisition> searchOrderHistory(RequisitionQuery query) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("destination", query.getDestination())
                .addValue("customerId", query.getCustomerId())
                .addValue("statuses", extractEnumSetParams(query.getStatuses()))
                .addValue("fromDate", toDate(query.getFromDateTime()))
                .addValue("toDate", toDate(query.getToDateTime()))
                .addValue("isReconciled", query.getReconciled());
        String sql = generateSearchQuery(SqlRequisitionQuery.ORDER_HISTORY_PARTIAL, query.getDateField(),
                query.getOrderBy(), query.getLimitOffset());
        PaginatedRowHandler<Requisition> paginatedRowHandler = new PaginatedRowHandler<>(query.getLimitOffset(),
                "total_rows", new RequisitionRowMapper(employeeInfoService, locationService, lineItemDao));
        localNamedJdbc.query(sql, params, paginatedRowHandler);
        return paginatedRowHandler.getList();
    }

    @Override
    public ImmutableList<Requisition> getRequisitionHistory(int requisitionId) {
        MapSqlParameterSource params = new MapSqlParameterSource("requisitionId", requisitionId);
        String sql = SqlRequisitionQuery.GET_REQUISITION_HISTORY.getSql(schemaMap(), new OrderBy("modified_date_time", SortOrder.ASC));
        List<Requisition> requisitions =  localNamedJdbc.query(sql, params, new RequisitionRowMapper(employeeInfoService, locationService, lineItemDao));
        return ImmutableList.copyOf(requisitions);
    }

    @Override
    public void savedInSfms(int requisitionId, boolean succeed) {
        MapSqlParameterSource params = new MapSqlParameterSource("requisitionId", requisitionId);
        params.addValue("succeed", succeed);
        String sql = SqlRequisitionQuery.SET_SAVED_IN_SFMS.getSql(schemaMap());
        localNamedJdbc.update(sql, params);
    }

    public void reconcile(int requisitionId, boolean reconciled){
        MapSqlParameterSource params = new MapSqlParameterSource("requisitionId", requisitionId);
        params.addValue("reconciled", reconciled);
        String sql = SqlRequisitionQuery.SET_RECONCILED.getSql(schemaMap());
        localNamedJdbc.update(sql, params);
    }

    private MapSqlParameterSource requisitionParams(Requisition requisition) {
        return new MapSqlParameterSource()
                .addValue("requisitionId", requisition.getRequisitionId())
                .addValue("revisionId", requisition.getRevisionId())
                .addValue("customerId", requisition.getCustomer().getEmployeeId())
                .addValue("destination", requisition.getDestination().getLocId().toString())
                .addValue("deliveryMethod", requisition.getDeliveryMethod().name())
                .addValue("specialInstructions", requisition.getSpecialInstructions().orElse(null))
                .addValue("status", requisition.getStatus().toString())
                .addValue("issuerId", requisition.getIssuer().map(Employee::getEmployeeId).orElse(null))
                .addValue("note", requisition.getNote().orElse(null))
                .addValue("modifiedBy", requisition.getModifiedBy().getEmployeeId())
                .addValue("modifiedDateTime", requisition.getModifiedDateTime().map(SqlBaseDao::toDate).orElse(null))
                .addValue("orderedDateTime", toDate(requisition.getOrderedDateTime()))
                .addValue("processedDateTime", requisition.getProcessedDateTime().map(SqlBaseDao::toDate).orElse(null))
                .addValue("completedDateTime", requisition.getCompletedDateTime().map(SqlBaseDao::toDate).orElse(null))
                .addValue("approvedDateTime", requisition.getApprovedDateTime().map(SqlBaseDao::toDate).orElse(null))
                .addValue("rejectedDateTime", requisition.getRejectedDateTime().map(SqlBaseDao::toDate).orElse(null))
                .addValue("last_sfms_sync_date_time", requisition.getLastSfmsSyncDateTime().map(SqlBaseDao::toDate).orElse(null))
                .addValue("savedInSfms", requisition.getSavedInSfms())
                .addValue("isReconciled", requisition.getReconciled());
    }

    /** Convert an EnumSet into a Set containing each enum's name. */
    private Set<String> extractEnumSetParams(EnumSet<RequisitionStatus> statuses) {
        return statuses.stream().map(Enum::name).collect(Collectors.toSet());
    }

    private int getNextRevisionId() {
        String sql = SqlRequisitionQuery.GET_NEXT_REVISION_ID.getSql(schemaMap());
        return localNamedJdbc.query(sql, (rs, i) -> { return rs.getInt("nextval"); }).get(0);
    }

    private enum SqlRequisitionQuery implements BasicSqlQuery {
        GET_NEXT_REVISION_ID(
                "SELECT nextval('${supplySchema}.requisition_content_revision_id_seq'::regclass)"
        ),

        INSERT_REQUISITION(
                "INSERT INTO ${supplySchema}.requisition(current_revision_id, ordered_date_time, \n" +
                "processed_date_time, completed_date_time, approved_date_time, rejected_date_time, saved_in_sfms) \n" +
                "VALUES (:revisionId, :orderedDateTime, :processedDateTime, :completedDateTime, \n" +
                ":approvedDateTime, :rejectedDateTime, :savedInSfms)"
        ),

        UPDATE_REQUISITION(
                "UPDATE ${supplySchema}.requisition SET current_revision_id = :revisionId, ordered_date_time = :orderedDateTime, \n" +
                "processed_date_time = :processedDateTime, completed_date_time = :completedDateTime, \n" +
                "approved_date_time = :approvedDateTime, rejected_date_time = :rejectedDateTime, \n" +
                "saved_in_sfms = :savedInSfms \n" +
                "WHERE requisition_id = :requisitionId"
        ),

        /** Never insert the revision id, let it auto increment. */
        INSERT_REQUISITION_CONTENT(
                "INSERT INTO ${supplySchema}.requisition_content(requisition_id, revision_id, destination, status, \n" +
                "issuing_emp_id, note, customer_id, modified_by_id, modified_date_time, special_instructions,\n" +
                "delivery_method, is_reconciled) \n" +
                "VALUES (:requisitionId, :revisionId, :destination, :status::${supplySchema}.requisition_status, \n" +
                ":issuerId, :note, :customerId, :modifiedBy, :modifiedDateTime, :specialInstructions,\n" +
                ":deliveryMethod::${supplySchema}.delivery_method, :isReconciled)"
        ),

        GET_REQUISITION_BY_ID(
                "SELECT * from ${supplySchema}.requisition r INNER JOIN ${supplySchema}.requisition_content c \n" +
                "ON r.current_revision_id = c.revision_id \n" +
                "WHERE r.requisition_id = :requisitionId"
        ),

        /** Must use {@link #generateSearchQuery(SqlRequisitionQuery, String, OrderBy, LimitOffset) generateSearchQuery}
         * to complete this query. */
        SEARCH_REQUISITIONS_PARTIAL(
                "SELECT *, count(*) OVER() as total_rows \n" +
                        "FROM ${supplySchema}.requisition as r \n" +
                        "INNER JOIN ${supplySchema}.requisition_content as c ON r.current_revision_id = c.revision_id \n" +
                        "WHERE c.destination LIKE :destination AND Coalesce(c.customer_id::text, '') LIKE :customerId \n" +
                        "AND Coalesce(c.issuing_emp_id::text, '') LIKE :issuerId \n" +
                        "AND c.revision_id IN (SELECT i.revision_id FROM ${supplySchema}.line_item i WHERE i.item_id::text LIKE :itemId) \n" +
                        "AND c.status::text IN (:statuses) AND r.saved_in_sfms::text LIKE :savedInSfms AND c.is_reconciled::text LIKE :isReconciled AND r."
        ),

        /** Must use {@link #generateSearchQuery(SqlRequisitionQuery, String, OrderBy, LimitOffset) generateSearchQuery}
         * to complete this query. */
        ORDER_HISTORY_PARTIAL(
                "SELECT *, count(*) OVER() as total_rows \n" +
                "FROM ${supplySchema}.requisition as r \n" +
                "INNER JOIN ${supplySchema}.requisition_content as c ON r.current_revision_id = c.revision_id \n" +
                "WHERE (c.destination = :destination OR Coalesce(c.customer_id::text, '') LIKE :customerId) \n" +
                "AND c.status::text IN (:statuses) AND r."
        ),
        GET_REQUISITION_HISTORY(
                "SELECT * from ${supplySchema}.requisition r INNER JOIN ${supplySchema}.requisition_content c \n" +
                "ON r.requisition_id = c.requisition_id \n" +
                "WHERE r.requisition_id = :requisitionId \n"
        ),
        SET_SAVED_IN_SFMS(
                "UPDATE ${supplySchema}.requisition \n" +
                        "SET saved_in_sfms = :succeed, last_sfms_sync_date_time =  CURRENT_TIMESTAMP" + "\n" +
                "WHERE requisition_id = :requisitionId"
        ),
        SET_RECONCILED(
                "UPDATE ${supplySchema}.requisition \n" +
                        "SET reconciled = :reconciled" + "\n" +
                "WHERE requisition_id = :requisitionId"
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

        private EmployeeInfoService employeeInfoService;
        private LocationService locationService;
        private SqlLineItemDao lineItemDao;

        protected RequisitionRowMapper(EmployeeInfoService employeeInfoService, LocationService locationService,
                                    SqlLineItemDao lineItemDao) {
            this.employeeInfoService = employeeInfoService;
            this.locationService = locationService;
            this.lineItemDao = lineItemDao;
        }

        @Override
        public Requisition mapRow(ResultSet rs, int i) throws SQLException {
            return new Requisition.Builder()
                    .withRequisitionId(rs.getInt("requisition_id"))
                    .withRevisionId(rs.getInt("revision_id"))
                    .withCustomer(employeeInfoService.getEmployee(rs.getInt("customer_id")))
                    .withDestination(locationService.getLocation(LocationId.ofString(rs.getString("destination"))))
                    .withDeliveryMethod(DeliveryMethod.valueOf(rs.getString("delivery_method")))
                    .withLineItems(lineItemDao.getLineItems(rs.getInt("revision_id")))
                    .withSpecialInstructions(rs.getString("special_instructions"))
                    .withState(RequisitionState.of(RequisitionStatus.valueOf(rs.getString("status"))))
                    .withIssuer(rs.getInt("issuing_emp_id") == 0 ? null : employeeInfoService.getEmployee(rs.getInt("issuing_emp_id")))
                    .withNote(rs.getString("note"))
                    .withModifiedBy(employeeInfoService.getEmployee(rs.getInt("modified_by_id")))
                    .withModifiedDateTime(getLocalDateTimeFromRs(rs, "modified_date_time"))
                    .withOrderedDateTime(getLocalDateTimeFromRs(rs, "ordered_date_time"))
                    .withProcessedDateTime(getLocalDateTimeFromRs(rs, "processed_date_time"))
                    .withCompletedDateTime(getLocalDateTimeFromRs(rs, "completed_date_time"))
                    .withApprovedDateTime(getLocalDateTimeFromRs(rs, "approved_date_time"))
                    .withRejectedDateTime(getLocalDateTimeFromRs(rs, "rejected_date_time"))
                    .withLastSfmsSyncDateTimeDateTime(getLocalDateTimeFromRs(rs, "last_sfms_sync_date_time"))
                    .withSavedInSfms(rs.getBoolean("saved_in_sfms"))
                    .withReconciled(rs.getBoolean("is_reconciled"))
                    .build();
        }
    }
}
