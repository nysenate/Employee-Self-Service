package gov.nysenate.ess.supply.security;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import gov.nysenate.ess.core.dao.base.BasicSqlQuery;
import gov.nysenate.ess.core.dao.base.DbVendor;
import gov.nysenate.ess.core.dao.base.SqlBaseDao;
import gov.nysenate.ess.core.model.auth.SenatePerson;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.service.personnel.EmployeeInfoService;
import gov.nysenate.ess.core.service.unit.LocationService;
import gov.nysenate.ess.supply.security.role.SupplyEmployee;
import gov.nysenate.ess.supply.security.role.SupplyManager;
import gov.nysenate.ess.supply.security.role.SupplyRole;
import gov.nysenate.ess.supply.security.role.SupplyUser;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


@Service
public class SqlSupplyRoleDao extends SqlBaseDao implements SupplyRoleDao {

    private static final Logger logger = LoggerFactory.getLogger(SqlSupplyRoleDao.class);

    @Autowired private EmployeeInfoService employeeService;
    @Autowired private LocationService locationService;

    @Override
    public ImmutableCollection<SupplyRole> getSupplyRoles(SenatePerson person) {
        MapSqlParameterSource params = new MapSqlParameterSource("uid", StringUtils.upperCase(person.getUid()));
        String sql = SqlSupplyRoleQuery.GET_ROLE_BY_UID.getSql(schemaMap());
        List<Integer> secLevels = remoteNamedJdbc.query(sql, params, (rs, i) -> {
            return rs.getInt("cdseclevel");
        });
        return mapSecLevelsToRoles(person, secLevels);
    }

    /** Maps sec levels from the SFMS database to {@link SupplyRole}'s. */
    private ImmutableCollection<SupplyRole> mapSecLevelsToRoles(SenatePerson person, List<Integer> secLevels) {
        Employee emp = employeeService.getEmployee(person.getEmployeeId());
        List<SupplyRole> roles = new ArrayList<>();

        /** Everyone gets supply user permissions. */
        roles.add(new SupplyUser(emp, emp.getWorkLocation()));

        for (Integer secLevel: secLevels) {
            switch(secLevel) {
                case 0:
                    roles.add(new SupplyEmployee());
                    break;
                case 1:
                    roles.add(new SupplyEmployee());
                    roles.add(new SupplyManager());
                    break;
            }
        }
        return ImmutableList.copyOf(roles);
    }

    @Override
    public ImmutableCollection<String> getUidsWithSupplyPermissions() {
        String sql = SqlSupplyRoleQuery.GET_ALL_UID.getSql(schemaMap());
        Collection<String> uids = remoteNamedJdbc.query(sql, (rs, i) -> rs.getString("NAUSER"));
        return ImmutableList.copyOf(uids);
    }

    private enum SqlSupplyRoleQuery implements BasicSqlQuery {

        GET_ROLE_BY_UID(
                "SELECT cdseclevel \n" +
                "FROM ${masterSchema}.im86modmenu \n" +
                "WHERE defrmint = 'FSSUPREQ12' \n" +
                "AND numodule = 'F01200' \n" +
                "AND cdstatus = 'A' \n" +
                "AND nauser = :uid"
        ),
        GET_ALL_UID(
                "SELECT nauser FROM ${masterSchema}.im86modmenu \n" +
                "WHERE defrmint = 'FSSUPREQ12' \n" +
                "AND numodule = 'F01200' \n" +
                "AND cdstatus = 'A' \n"
        );

        private String sql;

        SqlSupplyRoleQuery(String sql) {
            this.sql = sql;
        }

        @Override
        public String getSql() {
            return this.sql;
        }

        @Override
        public DbVendor getVendor() {
            return DbVendor.ORACLE_10g;
        }
    }
}
