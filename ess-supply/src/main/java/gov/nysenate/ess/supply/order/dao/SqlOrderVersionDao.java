package gov.nysenate.ess.supply.order.dao;

import gov.nysenate.ess.core.dao.base.BaseRowMapper;
import gov.nysenate.ess.core.dao.base.BasicSqlQuery;
import gov.nysenate.ess.core.dao.base.DbVendor;
import gov.nysenate.ess.core.dao.base.SqlBaseDao;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.model.unit.Location;
import gov.nysenate.ess.core.service.personnel.EmployeeInfoService;
import gov.nysenate.ess.core.service.unit.LocationService;
import gov.nysenate.ess.supply.item.LineItem;
import gov.nysenate.ess.supply.order.OrderStatus;
import gov.nysenate.ess.supply.order.OrderVersion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;

@Repository
public class SqlOrderVersionDao extends SqlBaseDao implements OrderVersionDao {

    @Autowired private SqlLineItemDao lineItemDao;
    @Autowired private EmployeeInfoService employeeInfoService;
    @Autowired private LocationService locationService;

    @Override
    public int insertOrderVersion(OrderVersion version) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("customerId", version.getCustomer().getEmployeeId())
                .addValue("destination", generateLocationId(version.getDestination()))
                .addValue("status", version.getStatus().toString())
                .addValue("note", version.getNote().orElse(null))
                .addValue("modifiedById", version.getModifiedBy().getEmployeeId());
        String sql = SqlOrderVersionQuery.INSERT_ORDER_VERSION.getSql(schemaMap());
        KeyHolder keyHolder = new GeneratedKeyHolder();
        localNamedJdbc.update(sql, params, keyHolder);
        return (Integer) keyHolder.getKeys().get("version_id");
    }

    @Override
    public OrderVersion getOrderVersion(int versionId) {
        MapSqlParameterSource params = new MapSqlParameterSource("versionId", versionId);
        String sql = SqlOrderVersionQuery.GET_ORDER_VERSION.getSql(schemaMap());
        return localNamedJdbc.queryForObject(sql, params, new OrderVersionRowMapper(lineItemDao, employeeInfoService, locationService));
    }

    private enum SqlOrderVersionQuery implements BasicSqlQuery {

        INSERT_ORDER_VERSION(
                "INSERT INTO ${supplySchema}.order_version(customer_id, destination, status, note, modified_by) \n" +
                "VALUES (:customerId, :destination, :status::${supplySchema}.order_status, :note, :modifiedById)"
        ),
        GET_ORDER_VERSION(
                "SELECT version_id, customer_id, destination, status, note, modified_by \n" +
                "FROM ${supplySchema}.order_version \n" +
                "WHERE version_id = :versionId"
        );

        SqlOrderVersionQuery(String sql) {
            this.sql = sql;
        }

        private String sql;

        @Override
        public String getSql() {
            return sql;
        }

        @Override
        public DbVendor getVendor() {
            return DbVendor.POSTGRES;
        }
    }

    private class OrderVersionRowMapper extends BaseRowMapper<OrderVersion> {

        private SqlLineItemDao lineItemDao;
        private EmployeeInfoService employeeInfoService;
        private LocationService locationService;

        public OrderVersionRowMapper(SqlLineItemDao lineItemDao, EmployeeInfoService employeeInfoService,
                                     LocationService locationService) {
            this.lineItemDao = lineItemDao;
            this.employeeInfoService = employeeInfoService;
            this.locationService = locationService;
        }

        @Override
        public OrderVersion mapRow(ResultSet rs, int rowNum) throws SQLException {
            int id = rs.getInt("version_id");
            Employee customer = employeeInfoService.getEmployee(rs.getInt("customer_id"));
            Location destination = locationService.getLocation(rs.getString("destination"));
            OrderStatus status = OrderStatus.valueOf(rs.getString("status"));
            String note = rs.getString("note");
            Employee modifiedBy = employeeInfoService.getEmployee(rs.getInt("modified_by"));
            Set<LineItem> lineItems = lineItemDao.getLineItems(id);
            return new OrderVersion.Builder().withId(id).withCustomer(customer).withDestination(destination)
                    .withStatus(status).withNote(note).withModifiedBy(modifiedBy).withLineItems(lineItems).build();
        }
    }
}
