package gov.nysenate.ess.supply.shipment.dao;

import gov.nysenate.ess.core.dao.base.BaseHandler;
import gov.nysenate.ess.core.dao.base.BasicSqlQuery;
import gov.nysenate.ess.core.dao.base.DbVendor;
import gov.nysenate.ess.core.dao.base.SqlBaseDao;
import gov.nysenate.ess.supply.shipment.Shipment;
import gov.nysenate.ess.supply.shipment.ShipmentHistory;
import gov.nysenate.ess.supply.shipment.ShipmentVersion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.SortedMap;
import java.util.TreeMap;

@Repository
public class SqlShipmentHistoryDao extends SqlBaseDao implements ShipmentHistoryDao {

    @Autowired private SqlShipmentVersionDao versionDao;

    @Override
    public void insertHistory(int shipmentId, int versionId, LocalDateTime modifiedDateTime) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("shipmentId", shipmentId)
                .addValue("versionId", versionId)
                .addValue("createdDateTime", toDate(modifiedDateTime));
        String sql = SqlShipmentHistoryQuery.INSERT_HISTORY.getSql(schemaMap());
        localNamedJdbc.update(sql, params);
    }

    @Override
    public ShipmentHistory getHistoryByShipmentId(int shipmentId) {
        MapSqlParameterSource params = new MapSqlParameterSource("shipmentId", shipmentId);
        String sql = SqlShipmentHistoryQuery.GET_HISTORY_BY_SHIP_ID.getSql(schemaMap());
        ShipmentHistoryHandler handler = new ShipmentHistoryHandler(versionDao);
        localNamedJdbc.query(sql, params, handler);
        return handler.getHistory();
    }

    private enum SqlShipmentHistoryQuery implements BasicSqlQuery {
        INSERT_HISTORY(
                "INSERT INTO ${supplySchema}.shipment_history(shipment_id, version_id, created_date_time) \n" +
                "VALUES (:shipmentId, :versionId, :createdDateTime)"
        ),
        GET_HISTORY_BY_SHIP_ID(
                "SELECT version_id, created_date_time \n" +
                "FROM ${supplySchema}.shipment_history \n" +
                "WHERE shipment_id = :shipmentId"
        );

        SqlShipmentHistoryQuery(String sql) {
            this.sql = sql;
        }

        private String sql;

        @Override
        public String getSql() {
            return this.sql;
        }

        @Override
        public DbVendor getVendor() {
            return DbVendor.POSTGRES;
        }
    }

    private class ShipmentHistoryHandler extends BaseHandler {

        private SqlShipmentVersionDao versionDao;
        private SortedMap<LocalDateTime, ShipmentVersion> versionMap;

        public ShipmentHistoryHandler(SqlShipmentVersionDao versionDao) {
            this.versionDao = versionDao;
            versionMap = new TreeMap<>();
        }

        @Override
        public void processRow(ResultSet rs) throws SQLException {
            ShipmentVersion version = versionDao.getVersionById(rs.getInt("version_id"));
            LocalDateTime dateTime = getLocalDateTimeFromRs(rs, "created_date_time");
            versionMap.put(dateTime, version);
        }

        public ShipmentHistory getHistory() {
            return ShipmentHistory.of(versionMap);
        }
    }
}
