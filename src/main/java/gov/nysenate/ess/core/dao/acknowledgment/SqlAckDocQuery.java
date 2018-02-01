package gov.nysenate.ess.core.dao.acknowledgment;

import gov.nysenate.ess.core.dao.base.BasicSqlQuery;
import gov.nysenate.ess.core.dao.base.DbVendor;

public enum SqlAckDocQuery implements BasicSqlQuery {

    GET_ALL_ACTIVE_ACK_DOCS_SQL(
        "SELECT * FROM ${essSchema}.ack_doc \n" +
                "WHERE active = true and effective_date_time < now()"
    ),

    GET_ACK_DOC_BY_ID_SQL(
            "SELECT * FROM ${essSchema}.ack_doc \n" +
                    "WHERE id = :ackDocId"
    ),

    INSERT_ACK_DOC_SQL(
        "INSERT INTO ${essSchema}.ack_doc (title, filename, active, effective_date_time)\n" +
         "VALUES (:title, :filename, :active, :effectiveDateTime)"
    ),

    GET_ALL_ACKNOWLEDGMENTS(
            "SELECT * FROM ${essSchema}.acknowledgment"
    ),

    GET_ALL_ACKNOWLEDGMENTS_FOR_EMPLOYEE(
            "SELECT * FROM ${essSchema}.acknowledgment \n" +
                    "WHERE emp_id = :empId"
    ),

    GET_ACK_BY_ID(
            "SELECT * FROM ${essSchema}.acknowledgment \n" +
                    "WHERE emp_id = :empId AND ack_doc_id = :ackDocId"
    ),

    INSERT_ACK_SQL(
        "INSERT INTO ${essSchema}.acknowledgment (emp_id, ack_doc_id, timestamp)\n" +
                "VALUES (:empId, :ack_doc, :timestamp)"
    ),

    GET_ALL_ACKS_WITH_TIMESTAMP_AND_DOC_REF(
      "select a.emp_id, d.title, d.id, a.timestamp\n" +
              "from ${essSchema}.acknowledgment a, ${essSchema}.ack_doc d\n" +
              "where a.ack_doc_id = d.id;"
    ),

    GET_ALL_ACKS_FOR_DOC_WITH_NAME_AND_YEAR(
      "select a.emp_id, a.timestamp, d.title\n" +
              "from ess.acknowledgment a, ess.ack_doc d\n" +
              "where a.ack_doc_id = d.id and\n" +
              "      date_part('year', d.effective_date_time) = :year and\n" +
              "      d.title = :title;"
    );


    private String sql;

    SqlAckDocQuery(String sql) {
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
