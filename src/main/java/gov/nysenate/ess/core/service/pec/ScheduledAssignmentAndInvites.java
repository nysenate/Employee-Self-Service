package gov.nysenate.ess.core.service.pec;

import gov.nysenate.ess.core.service.pec.assignment.PersonnelTaskAssigner;
import gov.nysenate.ess.core.service.pec.notification.PECNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class ScheduledAssignmentAndInvites {
    private final PersonnelTaskAssigner taskAssigner;
    private final PECNotificationService pecNotificationService;

    @Value("${scheduler.personnel_task.assignment.enabled:true}")
    private boolean scheduledAssignmentEnabled;

    @Autowired
    public ScheduledAssignmentAndInvites(PersonnelTaskAssigner taskAssigner,
                                         PECNotificationService pecNotificationService) {
        this.taskAssigner = taskAssigner;
        this.pecNotificationService = pecNotificationService;
    }

    /**
     * Handles scheduled task assignments.
     */
    @Scheduled(cron = "${scheduler.personnel_task.assignment.cron:0 0 3 * * *}")
    public void scheduledPersonnelTaskAssignment() {
        if (scheduledAssignmentEnabled) {
            pecNotificationService.sendInviteEmails(taskAssigner.assignTasks(true));
        }
    }
}
