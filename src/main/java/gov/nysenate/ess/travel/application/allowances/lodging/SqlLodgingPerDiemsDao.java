package gov.nysenate.ess.travel.application.allowances.lodging;

import gov.nysenate.ess.core.dao.base.BaseHandler;
import gov.nysenate.ess.core.dao.base.BasicSqlQuery;
import gov.nysenate.ess.core.dao.base.DbVendor;
import gov.nysenate.ess.core.dao.base.SqlBaseDao;
import gov.nysenate.ess.travel.application.address.TravelAddress;
import gov.nysenate.ess.travel.application.address.SqlTravelAddressDao;
import gov.nysenate.ess.travel.application.allowances.PerDiem;
import gov.nysenate.ess.travel.utils.Dollars;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

@Repository
public class SqlLodgingPerDiemsDao extends SqlBaseDao {

    @Autowired private SqlTravelAddressDao travelAddressDao;

    public LodgingPerDiems selectLodgingPerDiems(int amendmentId) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("amendmentId", amendmentId);
        String sql = SqlLodgingPerDiemsQuery.SELECT_LODGING_PER_DIEMS.getSql(schemaMap());
        LodgingPerDiemsHandler handler = new LodgingPerDiemsHandler(travelAddressDao);
        localNamedJdbc.query(sql, params, handler);
        return handler.getResult();
    }

    public void saveLodgingPerDiems(LodgingPerDiems lodgingPerDiems, int amendmentId) {
        for (LodgingPerDiem lpd : lodgingPerDiems.allLodgingPerDiems()) {
            // Ensure the address is in the database and update its id.
            // Destination addresses are inserted earlier but they are different instances so these address's ids
            // do not get updated. We need to call saveGoogleAddress here so the lodgingPerDiem addresses have the correct address_id.
            travelAddressDao.saveAddress(lpd.address());
            insertLodgingPerDiem(lpd);
            int id = insertIntoJoinTable(lpd, amendmentId, lodgingPerDiems.overrideRate());
            lodgingPerDiems.setId(id);
        }
    }

    private void insertLodgingPerDiem(LodgingPerDiem lpd) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("addressId", lpd.address().getId())
                .addValue("date", toDate(lpd.date()))
                .addValue("rate", lpd.rate().toString())
                .addValue("isReimbursementRequested", lpd.isReimbursementRequested());

        String sql = SqlLodgingPerDiemsQuery.INSERT_LODGING_PER_DIEM.getSql(schemaMap());
        KeyHolder keyHolder = new GeneratedKeyHolder();
        localNamedJdbc.update(sql, params, keyHolder);
        lpd.setId((Integer) keyHolder.getKeys().get("amendment_lodging_per_diem_id"));
    }

    private int insertIntoJoinTable(LodgingPerDiem lpd, int amendmentId, Dollars overrideRate) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("amendmentId", amendmentId)
                .addValue("lpdId", lpd.id())
                .addValue("overrideRate", overrideRate.toString());
        String sql = SqlLodgingPerDiemsQuery.INSERT_JOIN_TABLE.getSql(schemaMap());
        KeyHolder keyHolder = new GeneratedKeyHolder();
        localNamedJdbc.update(sql, params, keyHolder);
        return (Integer) keyHolder.getKeys().get("amendment_lodging_per_diems_id");
    }

    private enum SqlLodgingPerDiemsQuery implements BasicSqlQuery {
        SELECT_LODGING_PER_DIEMS(
                "SELECT lpds.amendment_lodging_per_diems_id, lpds.amendment_lodging_per_diem_id, lpds.override_rate,\n" +
                        " lpd.address_id, lpd.date, lpd.rate, lpd.is_reimbursement_requested\n" +
                        " FROM ${travelSchema}.amendment_lodging_per_diems lpds\n" +
                        " INNER JOIN ${travelSchema}.amendment_lodging_per_diem lpd ON lpds.amendment_lodging_per_diem_id = lpd.amendment_lodging_per_diem_id\n" +
                        " WHERE lpds.amendment_id = :amendmentId"
        ),
        INSERT_LODGING_PER_DIEM(
                "INSERT INTO ${travelSchema}.amendment_lodging_per_diem(address_id," +
                        " date, rate, is_reimbursement_requested) \n" +
                        "VALUES (:addressId, :date, :rate, :isReimbursementRequested)"
        ),
        INSERT_JOIN_TABLE(
                "INSERT INTO ${travelSchema}.amendment_lodging_per_diems(amendment_id," +
                        " amendment_lodging_per_diem_id, override_rate) \n" +
                        "VALUES (:amendmentId, :lpdId, :overrideRate)"
        );

        private String sql;

        SqlLodgingPerDiemsQuery(String sql) {
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

    public class LodgingPerDiemsHandler extends BaseHandler {

        private int lodgingPerDiemsId;
        private Dollars overrideRate;
        private Set<LodgingPerDiem> lodgingPerDiems;

        private SqlTravelAddressDao addressDao;

        public LodgingPerDiemsHandler(SqlTravelAddressDao addressDao) {
            this.addressDao = addressDao;
            this.lodgingPerDiems = new HashSet<>();
        }

        @Override
        public void processRow(ResultSet rs) throws SQLException {
            lodgingPerDiemsId = rs.getInt("amendment_lodging_per_diems_id");
            overrideRate = new Dollars(rs.getString("override_rate"));

            int lpdId = rs.getInt("amendment_lodging_per_diem_id");
            TravelAddress address = addressDao.selectAddress(rs.getInt("address_id"));
            PerDiem perDiem = new PerDiem(getLocalDate(rs, "date"), new BigDecimal(rs.getString("rate")));
            boolean isReimbursementRequested = rs.getBoolean("is_reimbursement_requested");
            LodgingPerDiem lpd = new LodgingPerDiem(lpdId, address, perDiem, isReimbursementRequested);
            lodgingPerDiems.add(lpd);
        }

        public LodgingPerDiems getResult() {
            return new LodgingPerDiems(lodgingPerDiemsId, lodgingPerDiems, overrideRate);
        }
    }
}
