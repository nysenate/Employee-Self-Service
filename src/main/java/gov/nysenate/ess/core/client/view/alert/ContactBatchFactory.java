
package gov.nysenate.ess.core.client.view.alert;

import gov.nysenate.ess.core.client.view.alert.ContactBatch.BatchContactList.Contact;
import gov.nysenate.ess.core.client.view.alert.ContactBatch.BatchContactList.Contact.ContactField;
import gov.nysenate.ess.core.client.view.alert.ContactBatch.BatchContactList.Contact.ContactPointList;
import gov.nysenate.ess.core.client.view.alert.ContactBatch.BatchContactList.Contact.ContactPointList.ContactPoint;
import gov.nysenate.ess.core.client.view.alert.ContactBatch.BatchContactList.Contact.ContactPointList.ContactPoint.ContactPointField;
import gov.nysenate.ess.core.model.alert.AlertInfo;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.client.view.alert.ContactBatch.BatchContactList;
import gov.nysenate.ess.core.client.view.alert.ContactBatch.BatchProcessingDirectives;
import gov.nysenate.ess.core.client.view.alert.ContactBatch.BatchProcessingDirectives.BatchProcessingOption;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A Factory used to create a ContactBatch.
 */
public class ContactBatchFactory {

    /**
     * Creates a ContactBatch containing contact information for all given employees.
     * @param employees Employees to be included. Generally should be all active senate employees.
     * @param alertInfoMap A Mapping of employeeId to their AlertInfo for all employees given in {@code employees}.
     */
    public static ContactBatch create(Set<Employee> employees, Map<Integer, AlertInfo> alertInfoMap) {
        ContactBatch contactBatch = new ContactBatch("1.0.3");
        contactBatch.setBatchProcessingDirectives(createBatchProcessingDirectives());
        contactBatch.setBatchContactList(createBatchContactList(employees, alertInfoMap));
        return contactBatch;
    }

    private static BatchProcessingDirectives createBatchProcessingDirectives() {
        BatchProcessingDirectives directives = new BatchProcessingDirectives();
        directives.setAccountID(createAccountId());
        directives.addBatchProcessingOptions(createBatchProcessingOptions());

        return directives;
    }

    private static List<BatchProcessingOption> createBatchProcessingOptions() {
        List<BatchProcessingOption> options = new ArrayList<>();
        options.add(new BatchProcessingOption("DeleteContactsNotInBatch", "true"));
        options.add(new BatchProcessingOption("DeleteGroupsNotInBatch", "false"));
        options.add(new BatchProcessingOption("DeleteContactsWithoutUniqueID", "false"));
        options.add(new BatchProcessingOption("DeleteGroupsWithContactsWithoutUniqueID", "false"));
        options.add(new BatchProcessingOption("MergeContactsInBatch", "false"));
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
        id.setUsername("nyssenate");
        return id;
    }

    private static BatchContactList createBatchContactList(Set<Employee> employees, Map<Integer, AlertInfo> alertInfoMap) {
        BatchContactList contactList = new BatchContactList();
        for (Employee emp: employees) {
            if (emp.getUid() != null) {
                contactList.addContact(createContact(emp, alertInfoMap.get(emp.getEmployeeId())));
            }
        }
        return contactList;
    }

    private static Contact createContact(Employee emp, AlertInfo alertInfo) {
        Contact contact = new Contact("AddOrModify", emp.getUid());
        contact.setContactFields(createContactFields(emp));
        contact.setContactPointList(createContactPoints(emp, alertInfo));
        return contact;
    }

    private static List<ContactField> createContactFields(Employee emp) {
        List<ContactField> contactFields = new ArrayList<>();
        contactFields.add(new ContactField("FirstName", emp.getFirstName()));
        contactFields.add(new ContactField("LastName", emp.getLastName()));
        contactFields.add(new ContactField("Language", "en-US"));
        contactFields.add(new ContactField("CustomField", "Title", emp.getJobTitle()));
        contactFields.add(new ContactField("CustomField", "Department", emp.getRespCenter().getHead().getName()));
        contactFields.add(new ContactField("CustomField", "LocationID", emp.getWorkLocation().getLocId().getCode()));
        contactFields.add(new ContactField("CustomField", "AgencyCode", emp.getRespCenter().getAgency().getCode()));
        contactFields.add(new ContactField("Address1", emp.getWorkLocation().getAddress().getAddr1()));
        contactFields.add(new ContactField("Address2", emp.getWorkLocation().getAddress().getAddr2()));
        contactFields.add(new ContactField("City", emp.getWorkLocation().getAddress().getCity()));
        contactFields.add(new ContactField("State", emp.getWorkLocation().getAddress().getState()));
        contactFields.add(new ContactField("PostalCode", emp.getWorkLocation().getAddress().getZip5()));
        contactFields.add(new ContactField("Country", "United States"));
        contactFields.add(new ContactField("TimeZone", "US/Eastern"));
        return contactFields;
    }

    private static ContactPointList createContactPoints(Employee emp, AlertInfo alertInfo) {
        ContactPointList cpl = new ContactPointList();
        if (emp.getWorkPhone() != null) {
            cpl.addContactPoint(voiceContactPoint("Work", emp.getWorkPhone()));
        }
        if (emp.getEmail() != null) {
            cpl.addContactPoint(emailContactPoint("Work", emp.getEmail()));
        }

        if (alertInfo != null) {
            if (StringUtils.isNotBlank(alertInfo.getHomePhone())) {
                cpl.addContactPoint(voiceContactPoint("Home", alertInfo.getHomePhone()));
            }
            if (StringUtils.isNotBlank(alertInfo.getAlternatePhone())) {
                cpl.addContactPoint(voiceContactPoint("Alternate", alertInfo.getAlternatePhone()));
            }
            if (StringUtils.isNotBlank(alertInfo.getMobilePhone()) &&
                    alertInfo.getMobileOptions().isCallable()) {
                cpl.addContactPoint(voiceContactPoint("Mobile", alertInfo.getMobilePhone()));
            }
            if (StringUtils.isNotBlank(alertInfo.getMobilePhone()) &&
                    alertInfo.getMobileOptions().isTextable()) {
                cpl.addContactPoint(textMessageContactPoint(alertInfo.getMobilePhone()));
            }
            if (StringUtils.isNotBlank(alertInfo.getPersonalEmail())) {
                cpl.addContactPoint(emailContactPoint("Personal", alertInfo.getPersonalEmail()));
            }
            if (StringUtils.isNotBlank(alertInfo.getAlternateEmail())) {
                cpl.addContactPoint(emailContactPoint("Alternate", alertInfo.getAlternateEmail()));
            }
        }
        return cpl;
    }

    private static ContactPoint voiceContactPoint(String label, String val) {
        ContactPoint cp = new ContactPoint("Voice");
        cp.addContactPointField(new ContactPointField("Label", label+" Phone"));
        cp.addContactPointField(new ContactPointField("CountryCode", "1"));
        cp.addContactPointField(new ContactPointField("Number", val));
        return cp;
    }

    private static ContactPoint textMessageContactPoint(String val) {
        ContactPoint cp = new ContactPoint("TextMessage");
        cp.addContactPointField(new ContactPointField("Label", "SMS"));
        cp.addContactPointField(new ContactPointField("Carrier", "SWN Global SMS"));
        // For SMS, need to include country code in phone number.
        cp.addContactPointField(new ContactPointField("Number", "1"+val));
        return cp;
    }

    private static ContactPoint emailContactPoint(String label, String val) {
        ContactPoint cp = new ContactPoint("Email");
        cp.addContactPointField(new ContactPointField("Label", label+" Email"));
        cp.addContactPointField(new ContactPointField("Address", val));
        return cp;
    }

}
