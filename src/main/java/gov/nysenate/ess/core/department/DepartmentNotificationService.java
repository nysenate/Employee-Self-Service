package gov.nysenate.ess.core.department;

import com.google.common.collect.Sets;
import gov.nysenate.ess.core.service.mail.SendMailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class DepartmentNotificationService {

    private SendMailService sendMailService;
    private DepartmentDao departmentDao;
    private MissingDeptHdEmail missingDeptHdEmail;
    private String reportEmail;

    @Autowired
    public DepartmentNotificationService(SendMailService sendMailService,
                                         DepartmentDao departmentDao,
                                         MissingDeptHdEmail missingDeptHdEmail,
                                         @Value("${report.email}") String reportEmail) {
        this.sendMailService = sendMailService;
        this.departmentDao = departmentDao;
        this.missingDeptHdEmail = missingDeptHdEmail;
        this.reportEmail = reportEmail;
    }

    /**
     * Checks for departments in the ess postgresql database that contain employees, are active,
     * and are missing a department head. If any are found, a email is sent out to notify
     * staff that these department heads need to be manually updated.
     */
    @Scheduled(cron = "${scheduler.ess.department.check_dept_heads: 0 0 6 * * *}")
    public void checkDepartmentHeads() {
        Set<Department> missingHeads = departmentsMissingHead();
        if (!missingHeads.isEmpty()) {
            Set<DepartmentView> deptViews = missingHeads.stream()
                    .map(DepartmentView::new)
                    .collect(Collectors.toSet());
            Set<MimeMessage> emails = Sets.newHashSet(missingDeptHdEmail.createEmail(deptViews, reportEmail));
            sendMailService.sendMessages(emails);
        }
    }

    // Return a set of active departments with employees which do not have a department head.
    private Set<Department> departmentsMissingHead() {
        Set<Department> departments = departmentDao.getDepartments();
        return departments.stream()
                .filter(Department::isActive)
                .filter(Department::hasEmployees)
                .filter(d -> d.getHeadEmpId() == 0)
                .collect(Collectors.toSet());
    }
}
