package gov.nysenate.ess.core.department;

import com.google.common.collect.Sets;

public class LdapDepartmentFixture {

    public static LdapDepartment STS = new LdapDepartment("STS", Sets.newHashSet(1, 2));
    public static LdapDepartment SUPPLY = new LdapDepartment("SUPPLY", Sets.newHashSet(3));
}
