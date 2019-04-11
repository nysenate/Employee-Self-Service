package gov.nysenate.ess.travel.authorization.role;

import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.service.security.authorization.role.RoleFactory;
import gov.nysenate.ess.time.service.personnel.SupervisorInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Service
public class TravelRoleFactory implements RoleFactory {

    @Autowired private SupervisorInfoService supervisorInfoService;

    @Override
    public Stream<Enum> getRoles(Employee employee) {
        List<Enum> roles = new ArrayList<>();
        if (supervisorInfoService.isSupervisor(employee.getEmployeeId())) {
            roles.add(TravelRole.SUPERVISOR);
        }
        if (employee.getJobTitle().equals("Deputy Executive Assistant")) {
            roles.add(TravelRole.DEPUTY_EXECUTIVE_ASSISTANT);
        }
        if (employee.getJobTitle().equals("Secretary of the Senate")) {
            roles.add(TravelRole.SECRETARY_OF_THE_SENATE);
        }
        if (employee.getEmployeeId() == 8944) {
            roles.add(TravelRole.MAJORITY_LEADER);
        }
        return roles.stream();
    }
}
