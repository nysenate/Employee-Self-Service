
package gov.nysenate.ess.core.client.view.emergency_notification;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;


/**
 * A ContactBatch is a xml dump which fits the sendwordnow schema.
 * It is a collection of names and contact information for senate employees.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "batchProcessingDirectives",
        "batchContactList",
        "batchGroupList"
})
@XmlRootElement(name = "contactBatch", namespace = "http://www.sendwordnow.com")
public class ContactBatch {

    @XmlElement(namespace = "http://www.sendwordnow.com", required = true)
    protected ContactBatch.BatchProcessingDirectives batchProcessingDirectives;
    @XmlElement(namespace = "http://www.sendwordnow.com")
    protected ContactBatch.BatchContactList batchContactList;
    @XmlElement(namespace = "http://www.sendwordnow.com")
    protected ContactBatch.BatchGroupList batchGroupList;
    @XmlAttribute(name = "version", required = true)
    protected String version;

    public ContactBatch.BatchProcessingDirectives getBatchProcessingDirectives() {
        return batchProcessingDirectives;
    }

    public void setBatchProcessingDirectives(ContactBatch.BatchProcessingDirectives value) {
        this.batchProcessingDirectives = value;
    }

    public ContactBatch.BatchContactList getBatchContactList() {
        return batchContactList;
    }

    public void setBatchContactList(ContactBatch.BatchContactList value) {
        this.batchContactList = value;
    }

    public ContactBatch.BatchGroupList getBatchGroupList() {
        return batchGroupList;
    }

    public void setBatchGroupList(ContactBatch.BatchGroupList value) {
        this.batchGroupList = value;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String value) {
        this.version = value;
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
            "contact"
    })
    public static class BatchContactList {

        @XmlElement(namespace = "http://www.sendwordnow.com", required = true)
        protected List<ContactBatch.BatchContactList.Contact> contact;

        public BatchContactList() {
            contact = new ArrayList<ContactBatch.BatchContactList.Contact>();
        }

        public void addContact(Contact contact) {
            this.contact.add(contact);
        }

        public List<ContactBatch.BatchContactList.Contact> getContact() {
            return this.contact;
        }

        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
                "contactField",
                "groupList",
                "contactPointList"
        })
        public static class Contact {

            @XmlElement(namespace = "http://www.sendwordnow.com")
            protected List<ContactBatch.BatchContactList.Contact.ContactField> contactField;
            @XmlElement(namespace = "http://www.sendwordnow.com")
            protected ContactBatch.BatchContactList.Contact.GroupList groupList;
            @XmlElement(namespace = "http://www.sendwordnow.com")
            protected ContactBatch.BatchContactList.Contact.ContactPointList contactPointList;
            @XmlAttribute(name = "contactID", required = true)
            protected String contactID;
            @XmlAttribute(name = "action", required = true)
            protected String action;

            public Contact() {}

            public Contact(String action, String contactID) {
                this.action = action;
                this.contactID = contactID;
                contactField = new ArrayList<>();
            }

            public void setContactFields(List<ContactField> contactFields) {
                this.contactField = contactFields;
            }

            public List<ContactBatch.BatchContactList.Contact.ContactField> getContactField() {
                return this.contactField;
            }

            public ContactBatch.BatchContactList.Contact.GroupList getGroupList() {
                return groupList;
            }

            public void setGroupList(ContactBatch.BatchContactList.Contact.GroupList value) {
                this.groupList = value;
            }

            public ContactBatch.BatchContactList.Contact.ContactPointList getContactPointList() {
                return contactPointList;
            }

            public void setContactPointList(ContactBatch.BatchContactList.Contact.ContactPointList value) {
                this.contactPointList = value;
            }

            public String getContactID() {
                return contactID;
            }

            public void setContactID(String value) {
                this.contactID = value;
            }

            public String getAction() {
                return action;
            }

            public void setAction(String value) {
                this.action = value;
            }


            @XmlAccessorType(XmlAccessType.FIELD)
            @XmlType(name = "", propOrder = {
                    "value"
            })
            public static class ContactField {
                @XmlValue
                protected String value;
                @XmlAttribute(name = "name", required = true)
                protected String name;
                @XmlAttribute(name = "customName")
                protected String customName;

                public ContactField() {};

                public ContactField(String name, String customeName, String value) {
                    this.name = name;
                    this.customName = customeName;
                    this.value = value;
                }


                public ContactField(String name, String value) {
                    this.name = name;
                    this.value = value;
                }

                public String getValue() {
                    return value;
                }

                public void setValue(String value) {
                    this.value = value;
                }

                public String getName() {
                    return name;
                }

                public void setName(String value) {
                    this.name = value;
                }

                public String getCustomName() {
                    return customName;
                }

                public void setCustomName(String value) {
                    this.customName = value;
                }

            }

            @XmlAccessorType(XmlAccessType.FIELD)
            @XmlType(name = "", propOrder = {
                    "contactPoint"
            })
            public static class ContactPointList {
                @XmlElement(namespace = "http://www.sendwordnow.com", required = true)
                protected List<ContactBatch.BatchContactList.Contact.ContactPointList.ContactPoint> contactPoint;

                public ContactPointList() {
                    this.contactPoint = new ArrayList<>();
                }

                public void addContactPoint(ContactPoint contactPoint) {
                    this.contactPoint.add(contactPoint);
                }

                public List<ContactBatch.BatchContactList.Contact.ContactPointList.ContactPoint> getContactPoint() {
                    return this.contactPoint;
                }

                @XmlAccessorType(XmlAccessType.FIELD)
                @XmlType(name = "", propOrder = {
                        "contactPointField"
                })
                public static class ContactPoint {
                    @XmlElement(namespace = "http://www.sendwordnow.com", required = true)
                    protected List<ContactBatch.BatchContactList.Contact.ContactPointList.ContactPoint.ContactPointField> contactPointField;
                    @XmlAttribute(name = "type", required = true)
                    protected String type;
                    @XmlAttribute(name = "action")
                    protected String action;

                    public ContactPoint() {
                        contactPointField = new ArrayList<>();
                    }

                    public ContactPoint(String type) {
                        this.type = type;
                        contactPointField = new ArrayList<>();
                    }

                    public void addContactPointField(ContactPointField field) {
                        this.contactPointField.add(field);
                    }
                    public List<ContactBatch.BatchContactList.Contact.ContactPointList.ContactPoint.ContactPointField> getContactPointField() {
                        return this.contactPointField;
                    }

                    public String getType() {
                        return type;
                    }

                    public void setType(String value) {
                        this.type = value;
                    }

                    public String getAction() {
                        return action;
                    }

                    public void setAction(String value) {
                        this.action = value;
                    }

                    @XmlAccessorType(XmlAccessType.FIELD)
                    @XmlType(name = "", propOrder = {
                            "value"
                    })
                    public static class ContactPointField {
                        @XmlValue
                        protected String value;
                        @XmlAttribute(name = "name", required = true)
                        protected String name;

                        public ContactPointField() {}

                        public ContactPointField(String name, String value) {
                            this.name = name;
                            this.value = value;
                        }

                        public String getValue() {
                            return value;
                        }

                        public void setValue(String value) {
                            this.value = value;
                        }

                        public String getName() {
                            return name;
                        }

                        public void setName(String value) {
                            this.name = value;
                        }
                    }
                }
            }

            @XmlAccessorType(XmlAccessType.FIELD)
            @XmlType(name = "", propOrder = {
                    "group",
                    "groupName"
            })
            public static class GroupList {
                @XmlElement(namespace = "http://www.sendwordnow.com")
                protected List<gov.nysenate.ess.core.client.view.emergency_notification.Group> group;
                @XmlElement(namespace = "http://www.sendwordnow.com")
                protected List<String> groupName;

                public List<gov.nysenate.ess.core.client.view.emergency_notification.Group> getGroup() {
                    if (group == null) {
                        group = new ArrayList<gov.nysenate.ess.core.client.view.emergency_notification.Group>();
                    }
                    return this.group;
                }

                public List<String> getGroupName() {
                    if (groupName == null) {
                        groupName = new ArrayList<String>();
                    }
                    return this.groupName;
                }
            }
        }
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
            "group"
    })
    public static class BatchGroupList {
        @XmlElement(namespace = "http://www.sendwordnow.com", required = true)
        protected List<ContactBatch.BatchGroupList.Group> group;

        public List<ContactBatch.BatchGroupList.Group> getGroup() {
            if (group == null) {
                group = new ArrayList<ContactBatch.BatchGroupList.Group>();
            }
            return this.group;
        }

        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
                "contactIdList"
        })
        public static class Group extends gov.nysenate.ess.core.client.view.emergency_notification.Group {
            @XmlElement(namespace = "http://www.sendwordnow.com")
            protected ContactBatch.BatchGroupList.Group.ContactIdList contactIdList;
            @XmlAttribute(name = "action", required = true)
            protected String action;

            public ContactBatch.BatchGroupList.Group.ContactIdList getContactIdList() {
                return contactIdList;
            }

            public void setContactIdList(ContactBatch.BatchGroupList.Group.ContactIdList value) {
                this.contactIdList = value;
            }

            public String getAction() {
                return action;
            }

            public void setAction(String value) {
                this.action = value;
            }

            @XmlAccessorType(XmlAccessType.FIELD)
            @XmlType(name = "", propOrder = {
                    "contact"
            })
            public static class ContactIdList {
                @XmlElement(namespace = "http://www.sendwordnow.com", required = true)
                protected List<ContactBatch.BatchGroupList.Group.ContactIdList.Contact> contact;
                @XmlAttribute(name = "action", required = true)
                protected String action;

                public List<ContactBatch.BatchGroupList.Group.ContactIdList.Contact> getContact() {
                    if (contact == null) {
                        contact = new ArrayList<ContactBatch.BatchGroupList.Group.ContactIdList.Contact>();
                    }
                    return this.contact;
                }

                public String getAction() {
                    return action;
                }

                public void setAction(String value) {
                    this.action = value;
                }

                @XmlAccessorType(XmlAccessType.FIELD)
                @XmlType(name = "")
                public static class Contact {
                    @XmlAttribute(name = "contactID", required = true)
                    protected String contactID;
                    @XmlAttribute(name = "action", required = true)
                    protected String action;

                    public String getContactID() {
                        return contactID;
                    }

                    public void setContactID(String value) {
                        this.contactID = value;
                    }

                    public String getAction() {
                        return action;
                    }

                    public void setAction(String value) {
                        this.action = value;
                    }
                }
            }
        }
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
            "accountID",
            "batchFile",
            "batchProcessingOption"
    })
    public static class BatchProcessingDirectives {
        @XmlElement(namespace = "http://www.sendwordnow.com", required = true)
        protected ContactBatch.BatchProcessingDirectives.AccountID accountID;
        @XmlElement(namespace = "http://www.sendwordnow.com")
        protected ContactBatch.BatchProcessingDirectives.BatchFile batchFile;
        @XmlElement(namespace = "http://www.sendwordnow.com", required = true)
        protected List<ContactBatch.BatchProcessingDirectives.BatchProcessingOption> batchProcessingOption;

//        protected BatchProcessingDirectives(ContactBatch.BatchProcessingDirectives.AccountID accountID) {
//            this.accountID = accountID;
//        }

        public ContactBatch.BatchProcessingDirectives.AccountID getAccountID() {
            return accountID;
        }

        public void setAccountID(ContactBatch.BatchProcessingDirectives.AccountID value) {
            this.accountID = value;
        }

        public ContactBatch.BatchProcessingDirectives.BatchFile getBatchFile() {
            return batchFile;
        }

        public void setBatchFile(ContactBatch.BatchProcessingDirectives.BatchFile value) {
            this.batchFile = value;
        }

        public List<ContactBatch.BatchProcessingDirectives.BatchProcessingOption> getBatchProcessingOption() {
            if (batchProcessingOption == null) {
                batchProcessingOption = new ArrayList<ContactBatch.BatchProcessingDirectives.BatchProcessingOption>();
            }
            return this.batchProcessingOption;
        }

        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "")
        public static class AccountID {

            @XmlAttribute(name = "username", required = true)
            protected String username;

            public String getUsername() {
                return username;
            }

            public void setUsername(String value) {
                this.username = value;
            }
        }


        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "")
        public static class BatchFile {

            @XmlAttribute(name = "requestFileName", required = true)
            protected String requestFileName;

            public String getRequestFileName() {
                return requestFileName;
            }

            public void setRequestFileName(String value) {
                this.requestFileName = value;
            }
        }

        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "")
        public static class BatchProcessingOption {
            @XmlAttribute(name = "name", required = true)
            protected String name;
            @XmlAttribute(name = "value", required = true)
            protected String value;

            public BatchProcessingOption() {}

            public BatchProcessingOption(String name, String value) {
                this.name = name;
                this.value = value;
            }

            public String getName() {
                return name;
            }

            public void setName(String value) {
                this.name = value;
            }

            public String getValue() {
                return value;
            }

            public void setValue(String value) {
                this.value = value;
            }
        }
    }
}
