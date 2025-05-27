package gov.nysenate.ess.supply.requisition.dao;

import gov.nysenate.ess.core.dao.base.BasicSqlQuery;
import gov.nysenate.ess.core.dao.base.DbVendor;
import gov.nysenate.ess.core.util.LimitOffset;
import gov.nysenate.ess.core.util.OrderBy;

public enum SqlRequisitionQuery implements BasicSqlQuery {
        GET_NEXT_REVISION_ID(
            "SELECT nextval('${supplySchema}.requisition_content_revision_id_seq'::regclass)"
        ),

        INSERT_REQUISITION(
        """
            INSERT INTO ${supplySchema}.requisition(current_revision_id, ordered_date_time,
                processed_date_time, completed_date_time, approved_date_time, rejected_date_time, saved_in_sfms)
            VALUES (:revisionId, :orderedDateTime, :processedDateTime, :completedDateTime,
                :approvedDateTime, :rejectedDateTime, :savedInSfms)
        """),

        UPDATE_REQUISITION(
        """
            UPDATE ${supplySchema}.requisition SET current_revision_id = :revisionId, ordered_date_time = :orderedDateTime,
                processed_date_time = :processedDateTime, completed_date_time = :completedDateTime,
                approved_date_time = :approvedDateTime, rejected_date_time = :rejectedDateTime,
                saved_in_sfms = :savedInSfms
            WHERE requisition_id = :requisitionId
        """),

        /** Never insert the revision id, let it auto increment. */
        INSERT_REQUISITION_CONTENT(
        """
            INSERT INTO ${supplySchema}.requisition_content(requisition_id, revision_id, destination, status,
                issuing_emp_id, note, customer_id, modified_by_id, modified_date_time, special_instructions,
                delivery_method, is_reconciled)
            VALUES (:requisitionId, :revisionId, :destination, :status::${supplySchema}.requisition_status,
                :issuerId, :note, :customerId, :modifiedBy, :modifiedDateTime, :specialInstructions,
                :deliveryMethod::${supplySchema}.delivery_method, :isReconciled)
        """),

        GET_REQUISITION_BY_ID(
        """
            SELECT * from ${supplySchema}.requisition r INNER JOIN ${supplySchema}.requisition_content c
            ON r.current_revision_id = c.revision_id
            WHERE r.requisition_id = :requisitionId
        """),

        /** Must use {@link SqlRequisitionDao#generateSearchQuery(SqlRequisitionQuery, String, OrderBy, LimitOffset) generateSearchQuery}
         * to complete partial queries. */

        SEARCH_REQUISITIONS_PARTIAL(
        """
            SELECT *, count(*) OVER() as total_rows
            FROM ${supplySchema}.requisition as r
            INNER JOIN ${supplySchema}.requisition_content as c ON r.current_revision_id = c.revision_id
            WHERE c.destination LIKE :destination AND Coalesce(c.customer_id::text, '') LIKE :customerId
                AND Coalesce(c.issuing_emp_id::text, '') LIKE :issuerId
                AND c.revision_id IN (SELECT i.revision_id FROM ${supplySchema}.line_item i WHERE i.item_id::text LIKE :itemId)
                AND c.status::text IN (:statuses) AND r.saved_in_sfms::text LIKE :savedInSfms AND c.is_reconciled::text LIKE :isReconciled AND r.
        """),

        ORDER_HISTORY_PARTIAL(
        """
            SELECT *, count(*) OVER() as total_rows
            FROM ${supplySchema}.requisition as r
            INNER JOIN ${supplySchema}.requisition_content as c ON r.current_revision_id = c.revision_id
            WHERE (c.destination = :destination OR Coalesce(c.customer_id::text, '') LIKE :customerId)
                AND c.status::text IN (:statuses) AND r.
        """),
        GET_REQUISITION_HISTORY(
        """
            SELECT * from ${supplySchema}.requisition r INNER JOIN ${supplySchema}.requisition_content c
            ON r.requisition_id = c.requisition_id
            WHERE r.requisition_id = :requisitionId
        """),
        SET_SAVED_IN_SFMS(
        """
            UPDATE ${supplySchema}.requisition
            SET saved_in_sfms = :succeed, last_sfms_sync_date_time =  CURRENT_TIMESTAMP
            WHERE requisition_id = :requisitionId
        """),
        SET_RECONCILED(
        """
            UPDATE ${supplySchema}.requisition
            SET reconciled = :reconciled
            WHERE requisition_id = :requisitionId
        """)
        ;

        private final String sql;

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
