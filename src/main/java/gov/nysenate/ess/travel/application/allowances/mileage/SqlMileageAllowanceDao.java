package gov.nysenate.ess.travel.application.allowances.mileage;

import gov.nysenate.ess.core.dao.base.BaseHandler;
import gov.nysenate.ess.core.dao.base.BasicSqlQuery;
import gov.nysenate.ess.core.dao.base.DbVendor;
import gov.nysenate.ess.core.dao.base.SqlBaseDao;
import gov.nysenate.ess.travel.application.route.Leg;
import gov.nysenate.ess.travel.application.route.RouteDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
public class SqlMileageAllowanceDao extends SqlBaseDao implements MileageAllowanceDao {

    @Autowired private RouteDao routeDao;

    @Override
    @Transactional(value = "localTxManager")
    public void insertMileageAllowances(UUID versionId, MileageAllowances mileageAllowances) {
        List<SqlParameterSource> paramList = new ArrayList<>();
        int sequenceNo = 0;
        for (MileageAllowance allowance : mileageAllowances.getOutboundAllowances()) {
            paramList.add(mileageAllowanceParams(versionId, true, sequenceNo, allowance));
            sequenceNo++;
        }
        for (MileageAllowance allowance : mileageAllowances.getReturnAllowances()) {
            paramList.add(mileageAllowanceParams(versionId, false, sequenceNo, allowance));
            sequenceNo++;
        }
        String sql = SqlMileageAllowanceQuery.INSERT_MILEAGE_ALLOWNCE.getSql(schemaMap());
        SqlParameterSource[] batchParams = new SqlParameterSource[paramList.size()];
        batchParams = paramList.toArray(batchParams);
        localNamedJdbc.batchUpdate(sql, batchParams);
    }

    @Override
    public MileageAllowances getMileageAllowance(UUID versionId) {
        MapSqlParameterSource params = new MapSqlParameterSource("versionId", versionId.toString());
        String sql = SqlMileageAllowanceQuery.SELECT_MILEAGE_ALLOWANCE.getSql(schemaMap());
        MileageAllowanceHandler handler = new MileageAllowanceHandler(routeDao);
        localNamedJdbc.query(sql, params, handler);
        return handler.getResults();
    }

    private MapSqlParameterSource mileageAllowanceParams(UUID versionId, boolean isOutbound, int sequenceNo, MileageAllowance allowance) {
        return new MapSqlParameterSource()
                .addValue("id", allowance.getId().toString())
                .addValue("versionId", versionId.toString())
                .addValue("legId", allowance.getLeg().getId().toString())
                .addValue("miles", String.valueOf(allowance.getMiles()))
                .addValue("mileageRate", allowance.getMileageRate().toString())
                .addValue("isOutbound", isOutbound)
                .addValue("sequenceNo", sequenceNo);
    }

    private enum SqlMileageAllowanceQuery implements BasicSqlQuery {
        INSERT_MILEAGE_ALLOWNCE(
                "INSERT INTO ${travelSchema}.app_mileage_allowance(id, version_id, leg_id, " +
                        "miles, mileage_rate, is_outbound, sequence_no) \n" +
                        "VALUES (:id::uuid, :versionId::uuid, :legId::uuid, :miles, :mileageRate, :isOutbound, :sequenceNo)"
        ),
        SELECT_MILEAGE_ALLOWANCE(
                "SELECT m.id, m.leg_id, m.miles, m.mileage_rate, m.is_outbound \n" +
                        "FROM ${travelSchema}.app_mileage_allowance m \n" +
                        "WHERE version_id = :versionId::uuid \n" +
                        "ORDER BY sequence_no ASC"
        )
        ;

        private String sql;

        SqlMileageAllowanceQuery(String sql) {
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

    private class MileageAllowanceHandler extends BaseHandler {

        private RouteDao routeDao;
        private List<MileageAllowance> outboundAllowances = new ArrayList<>();
        private List<MileageAllowance> returnAllowances = new ArrayList<>();

        public MileageAllowanceHandler(RouteDao routeDao) {
            this.routeDao = routeDao;
        }

        @Override
        public void processRow(ResultSet rs) throws SQLException {
            UUID id = UUID.fromString(rs.getString("id"));
            Leg leg = routeDao.getLeg(UUID.fromString(rs.getString("leg_id")));
            double miles = Double.valueOf(rs.getString("miles"));
            BigDecimal mileageRate = new BigDecimal(rs.getString("mileage_rate"));

            MileageAllowance allowance = new MileageAllowance(id, leg, miles, mileageRate);

            if (rs.getBoolean("is_outbound")) {
                outboundAllowances.add(allowance);
            }
            else {
                returnAllowances.add(allowance);
            }
        }

        protected MileageAllowances getResults() {
            return new MileageAllowances(outboundAllowances, returnAllowances);
        }
    }
}
