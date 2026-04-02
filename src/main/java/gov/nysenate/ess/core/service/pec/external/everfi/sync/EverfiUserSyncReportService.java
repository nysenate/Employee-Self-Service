package gov.nysenate.ess.core.service.pec.external.everfi.sync;

import gov.nysenate.ess.core.service.mail.SendMailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Emails the rendered sync report to the configured PEC admin distribution list.
 */
@Service
public class EverfiUserSyncReportService {

    private static final DateTimeFormatter SUBJECT_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

    private final List<String> pecAdminReportEmails;
    private final SendMailService sendMailService;

    public EverfiUserSyncReportService(
            @Value("${pec.admin.report.emails}") String pecAdminReportEmails,
            SendMailService sendMailService
    ) {
        this.pecAdminReportEmails = Arrays.asList(pecAdminReportEmails.replace(" ", "").split(","));
        this.sendMailService = sendMailService;
    }

    public void sendSyncRunToPecAdmin(SyncRun run) {
        var subject = SUBJECT_DATE_FORMAT.format(run.ranAt())
                + " Everfi User Sync Report - "
                + (run.dryRun() ? "DRY RUN" : "LIVE RUN");
        var report = new EverfiUserSyncReport(run, true);
        var html = report.generateHtml();

        var messages = new ArrayList<MimeMessage>();
        for (var email : pecAdminReportEmails) {
            messages.add(sendMailService.newHtmlMessage(email, subject, html));
        }
        sendMailService.sendMessages(messages);
    }
}
