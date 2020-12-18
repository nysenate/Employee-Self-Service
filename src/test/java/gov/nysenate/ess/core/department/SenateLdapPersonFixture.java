package gov.nysenate.ess.core.department;

import gov.nysenate.ess.core.model.auth.SenateLdapPerson;

public class SenateLdapPersonFixture {

    public static SenateLdapPerson LDAP_PERSON_STS_1;
    public static SenateLdapPerson LDAP_PERSON_STS_2;
    public static SenateLdapPerson LDAP_PERSON_SUPPLY_3;

    static {
        LDAP_PERSON_STS_1 = new SenateLdapPerson();
        LDAP_PERSON_STS_1.setDepartment("STS");
        LDAP_PERSON_STS_1.setEmployeeId("1");

        LDAP_PERSON_STS_2 = new SenateLdapPerson();
        LDAP_PERSON_STS_2.setDepartment("STS");
        LDAP_PERSON_STS_2.setEmployeeId("2");

        LDAP_PERSON_SUPPLY_3 = new SenateLdapPerson();
        LDAP_PERSON_SUPPLY_3.setDepartment("SUPPLY");
        LDAP_PERSON_SUPPLY_3.setEmployeeId("3");
    }
}
