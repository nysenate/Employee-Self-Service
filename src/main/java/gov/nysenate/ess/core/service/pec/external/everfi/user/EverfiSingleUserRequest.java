package gov.nysenate.ess.core.service.pec.external.everfi.user;

import gov.nysenate.ess.core.service.pec.external.everfi.EverfiApiClient;
import gov.nysenate.ess.core.util.OutputUtils;

import java.io.IOException;
import java.util.List;

public class EverfiSingleUserRequest {

    private static final String USER_END_POINT =
            "/v1/admin/users/:id?fields[users]=email,first_name,last_name,sso_id,employee_id,student_id,active,user_rule_set_roles,category_labels";
    private final EverfiApiClient everfiClient;
    private EverfiSingleUserResponse response;
    private String UUID = "";

    public EverfiSingleUserRequest(EverfiApiClient everfiClient, String UUID) {
        this.everfiClient = everfiClient;
        this.UUID = UUID;
    }

    private String endpoint() {
        return USER_END_POINT.replace(":id",this.UUID);
    }

    public EverfiUser getUser() throws IOException {
        String data = everfiClient.get(endpoint());
        if (data == null) {
            return null;
        }
        response = OutputUtils.jsonToObject(data, EverfiSingleUserResponse.class);
        return response.getUser();
    }


}
