package gov.nysenate.ess.supply.reconcilation.dao;

import gov.nysenate.ess.core.dao.base.BasicSqlQuery;
import gov.nysenate.ess.core.dao.base.DbVendor;

public enum  OracleRecItemQuery implements BasicSqlQuery {


    REC_ORDER_QUERY(
            "SELECT NUXREFCO, AMQTYOHSTD \n" +
            "FROM FM12INVENTRY \n" +
            "WHERE CDLOCAT = 'LC100S' \n" +
            "AND CDLOCTYPE = 'P' \n" +
            "'AND CDSTATUS = 'A'; "
    ),

    GET_REC_ORDER_BY_ID(
            REC_ORDER_QUERY.getSql()+
            "WHERE AMQTYOHSTD = :id"
    ),

    GET_REC_ORDER_BY_IDS(
            REC_ORDER_QUERY.getSql()+
            "WHERE AMQTYOHSTD IN (:ids)"
    );

    private String sql;

    OracleRecItemQuery(String sql) {
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
