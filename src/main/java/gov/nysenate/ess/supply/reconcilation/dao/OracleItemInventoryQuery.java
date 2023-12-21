package gov.nysenate.ess.supply.reconcilation.dao;

import gov.nysenate.ess.core.dao.base.BasicSqlQuery;
import gov.nysenate.ess.core.dao.base.DbVendor;

public enum OracleItemInventoryQuery implements BasicSqlQuery {
    REC_ORDER_QUERY(
            "SELECT inv.NUXREFCO, inv.AMQTYOHSTD, inv.CDLOCAT, inv.CDLOCTYPE, unit.AmStdUnit \n" +
                    "FROM ${masterSchema}.FM12INVENTRY inv \n" +
                    "  INNER JOIN ${masterSchema}.FM12COMMODTY com \n" +
                    "    ON com.NUXREFCO = inv.NUXREFCO \n" +
                    "  INNER JOIN ${masterSchema}.FL12STDUNIT unit \n" +
                    "    On com.CdIssUnit = unit.CdStdUnit \n" +
                    "WHERE com.CDSTOCKITEM = 'Y' \n" +
                    "  AND inv.CDLOCAT = :cdlocat \n" +
                    "  AND inv.CDLOCTYPE = :cdloctype"
    );

    private final String sql;

    OracleItemInventoryQuery(String sql) {
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
