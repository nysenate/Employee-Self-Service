package gov.nysenate.ess.supply.employee.service;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import gov.nysenate.ess.core.dao.personnel.EmployeeDao;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.supply.security.SupplyRoleDao;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class EssSupplyEmployeeService implements SupplyEmployeeService {

    @Autowired private SupplyRoleDao supplyRoleDao;
    @Autowired private EmployeeDao employeeDao;

    @Override
    public ImmutableCollection<Employee> getSupplyEmployees() {
        ImmutableCollection<String> uids = supplyRoleDao.getUidsWithSupplyPermissions();
        ImmutableCollection<String> emails = uids.stream()
                                                 .map(uid -> StringUtils.lowerCase(uid) + "@nysenate.gov")
                                                 .collect(Collectors.collectingAndThen(Collectors.toList(), ImmutableList::copyOf));
        return emails.stream()
                     .map(employeeDao::getEmployeeByEmail)
                     .collect(Collectors.collectingAndThen(Collectors.toList(), ImmutableList::copyOf));
    }
}
