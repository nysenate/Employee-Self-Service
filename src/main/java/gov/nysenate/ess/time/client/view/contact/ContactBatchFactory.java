
package gov.nysenate.ess.time.client.view.contact;

import gov.nysenate.ess.core.model.emergency_notification.EmergencyNotificationInfo;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.time.client.view.contact.ContactBatch.BatchProcessingDirectives;
import gov.nysenate.ess.time.client.view.contact.ContactBatch.BatchProcessingDirectives.BatchProcessingOption;

import java.util.ArrayList;
import java.util.List;


public class ContactBatchFactory {

    /** Constants */
    private static final String accountId = "nyssenate";

    public static ContactBatch create(Employee employee, EmergencyNotificationInfo emergencyInfo) {
        ContactBatch contactBatch = new ContactBatch();
        BatchProcessingDirectives directives = createBatchProcessingDirectives();
        contactBatch.setBatchProcessingDirectives(directives);
        return contactBatch;
    }

    private static BatchProcessingDirectives createBatchProcessingDirectives() {
        BatchProcessingDirectives directives = new BatchProcessingDirectives();
        directives.setAccountID(createAccountId());
        directives.getBatchProcessingOption().addAll(createBatchProcessingOptions());

        return directives;
    }

    private static List<BatchProcessingOption> createBatchProcessingOptions() {
        List<BatchProcessingOption> options = new ArrayList<>();
        options.add(new BatchProcessingOption("DeleteContactsNotInBatch", "true"));
        options.add(new BatchProcessingOption("DeleteGroupsNotInBatch", "false"));
        options.add(new BatchProcessingOption("DeleteContactsWithoutUniqueID", "false"));
        options.add(new BatchProcessingOption("DeleteGroupsWithContactsWithoutUniqueID", "false"));
        options.add(new BatchProcessingOption("MergeContactsInBatch", "true"));
        options.add(new BatchProcessingOption("BatchContinueOnContactError", "true"));
        options.add(new BatchProcessingOption("BatchContinueOnGroupError", "true"));
        options.add(new BatchProcessingOption("BatchFailureOnMissingModify", "false"));
        options.add(new BatchProcessingOption("BatchFailureOnMissingRemove", "false"));
        options.add(new BatchProcessingOption("ReturnContactList", "false"));
        options.add(new BatchProcessingOption("ReturnContactListWithUniqueIDs", "false"));
        options.add(new BatchProcessingOption("ReturnContactListWithoutUniqueIDs", "false"));
        return options;
    }

    private static BatchProcessingDirectives.AccountID createAccountId() {
        BatchProcessingDirectives.AccountID id = new BatchProcessingDirectives.AccountID();
        id.setUsername(accountId);
        return id;
    }

}
