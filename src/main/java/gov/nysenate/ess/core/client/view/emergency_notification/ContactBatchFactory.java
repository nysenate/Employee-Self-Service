
package gov.nysenate.ess.core.client.view.emergency_notification;

import gov.nysenate.ess.core.client.view.emergency_notification.ContactBatch.BatchContactList.Contact;
import gov.nysenate.ess.core.client.view.emergency_notification.ContactBatch.BatchContactList.Contact.ContactField;
import gov.nysenate.ess.core.client.view.emergency_notification.ContactBatch.BatchContactList.Contact.ContactPointList;
import gov.nysenate.ess.core.client.view.emergency_notification.ContactBatch.BatchContactList.Contact.ContactPointList.ContactPoint;
import gov.nysenate.ess.core.client.view.emergency_notification.ContactBatch.BatchContactList.Contact.ContactPointList.ContactPoint.ContactPointField;
import gov.nysenate.ess.core.model.emergency_notification.EmergencyNotificationInfo;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.client.view.emergency_notification.ContactBatch.BatchContactList;
import gov.nysenate.ess.core.client.view.emergency_notification.ContactBatch.BatchProcessingDirectives;
import gov.nysenate.ess.core.client.view.emergency_notification.ContactBatch.BatchProcessingDirectives.BatchProcessingOption;

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
     * @param emergencyInfoMap A Mapping of employeeId to their EmergencyNotificationInfo for all employees given in {@code employees}.
     */
    public static ContactBatch create(Set<Employee> employees, Map<Integer, EmergencyNotificationInfo> emergencyInfoMap) {
        ContactBatch contactBatch = new ContactBatch("1.0.3");
        contactBatch.setBatchProcessingDirectives(createBatchProcessingDirectives());
        contactBatch.setBatchContactList(createBatchContactList(employees, emergencyInfoMap));
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
        id.setUsername("nyssenate");
        return id;
    }

    private static BatchContactList createBatchContactList(Set<Employee> employees, Map<Integer, EmergencyNotificationInfo> emergencyInfoMap) {
        BatchContactList contactList = new BatchContactList();
        for (Employee emp: employees) {
            if (emp.getUid() != null) {
                contactList.addContact(createContact(emp, emergencyInfoMap.get(emp.getEmployeeId())));
            }
        }
        return contactList;
    }

    private static Contact createContact(Employee emp, EmergencyNotificationInfo emergencyNotificationInfo) {
        Contact contact = new Contact("AddOrModify", emp.getUid());
        contact.setContactFields(createContactFields(emp));
        contact.setContactPointList(createContactPoints(emp, emergencyNotificationInfo));
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
        contactFields.add(new ContactField("CustomField", "EmployeeType", "SenateEmployee"));
        contactFields.add(new ContactField("Address1", emp.getWorkLocation().getAddress().getAddr1()));
        contactFields.add(new ContactField("Address2", emp.getWorkLocation().getAddress().getAddr2()));
        contactFields.add(new ContactField("City", emp.getWorkLocation().getAddress().getCity()));
        contactFields.add(new ContactField("State", emp.getWorkLocation().getAddress().getState()));
        contactFields.add(new ContactField("PostalCode", emp.getWorkLocation().getAddress().getZip5()));
        contactFields.add(new ContactField("Country", "United States"));
        contactFields.add(new ContactField("TimeZone", "US/Eastern"));
        return contactFields;
    }

    private static ContactPointList createContactPoints(Employee emp, EmergencyNotificationInfo emergencyNotificationInfo) {
        ContactPointList cpl = new ContactPointList();
        if (emp.getWorkPhone() != null) {
            cpl.addContactPoint(workPhoneContactPoint(emp));
        }
        if (emp.getEmail() != null) {
            cpl.addContactPoint(workEmailContactPoint(emp));
        }

        if (emergencyNotificationInfo != null) {
            if (emergencyNotificationInfo.getHomePhone() != null) {
                cpl.addContactPoint(homePhoneContactPoint(emergencyNotificationInfo));
            }
            if (emergencyNotificationInfo.getMobilePhone() != null) {
                cpl.addContactPoint(mobilePhoneContactPoint(emergencyNotificationInfo));
            }
            if (emergencyNotificationInfo.isSmsSubscribed()) {
                cpl.addContactPoint(textMessageContactPoint(emergencyNotificationInfo));
            }
            if (emergencyNotificationInfo.getPersonalEmail() != null) {
                cpl.addContactPoint(personalEmailContactPoint(emergencyNotificationInfo));
            }
            if (emergencyNotificationInfo.getAlternateEmail() != null) {
                cpl.addContactPoint(alternateEmailContactPoint(emergencyNotificationInfo));
            }
        }
        return cpl;
    }

    private static ContactPoint workPhoneContactPoint(Employee emp) {
        ContactPoint workPhone = new ContactPoint("Voice");
        workPhone.addContactPointField(new ContactPointField("Label", "Work Phone"));
        workPhone.addContactPointField(new ContactPointField("CountryCode", "1"));
        workPhone.addContactPointField(new ContactPointField("Number", emp.getWorkPhone()));
        return workPhone;
    }

    private static ContactPoint homePhoneContactPoint(EmergencyNotificationInfo emergencyNotificationInfo) {
        ContactPoint homePhone = new ContactPoint("Voice");
        homePhone.addContactPointField(new ContactPointField("Label", "Home Phone"));
        homePhone.addContactPointField(new ContactPointField("CountryCode", "1"));
        homePhone.addContactPointField(new ContactPointField("Number", emergencyNotificationInfo.getHomePhone()));
        return homePhone;
    }

    private static ContactPoint mobilePhoneContactPoint(EmergencyNotificationInfo emergencyNotificationInfo) {
        ContactPoint homePhone = new ContactPoint("Voice");
        homePhone.addContactPointField(new ContactPointField("Label", "Mobile Phone"));
        homePhone.addContactPointField(new ContactPointField("CountryCode", "1"));
        homePhone.addContactPointField(new ContactPointField("Number", emergencyNotificationInfo.getMobilePhone()));
        return homePhone;
    }

    private static ContactPoint textMessageContactPoint(EmergencyNotificationInfo emergencyNotificationInfo) {
        ContactPoint homePhone = new ContactPoint("TextMessage");
        homePhone.addContactPointField(new ContactPointField("Label", "SMS"));
        homePhone.addContactPointField(new ContactPointField("Carrier", "SWN Global SMS"));
        // For SMS, need to include country code in phone number.
        homePhone.addContactPointField(new ContactPointField("Number", "1" + emergencyNotificationInfo.getMobilePhone()));
        return homePhone;
    }

    private static ContactPoint workEmailContactPoint(Employee emp) {
        ContactPoint workEmail = new ContactPoint("Email");
        workEmail.addContactPointField(new ContactPointField("Label", "Work Email"));
        workEmail.addContactPointField(new ContactPointField("Address", emp.getEmail()));
        return workEmail;
    }

    private static ContactPoint personalEmailContactPoint(EmergencyNotificationInfo emergencyNotificationInfo) {
        ContactPoint workEmail = new ContactPoint("Email");
        workEmail.addContactPointField(new ContactPointField("Label", "Personal Email"));
        workEmail.addContactPointField(new ContactPointField("Address", emergencyNotificationInfo.getPersonalEmail()));
        return workEmail;
    }

    private static ContactPoint alternateEmailContactPoint(EmergencyNotificationInfo emergencyNotificationInfo) {
        ContactPoint workEmail = new ContactPoint("Email");
        workEmail.addContactPointField(new ContactPointField("Label", "Alternate Email"));
        workEmail.addContactPointField(new ContactPointField("Address", emergencyNotificationInfo.getAlternateEmail()));
        return workEmail;
    }
}
