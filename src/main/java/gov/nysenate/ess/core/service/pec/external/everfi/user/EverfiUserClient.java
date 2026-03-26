package gov.nysenate.ess.core.service.pec.external.everfi.user;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.nysenate.ess.core.service.pec.external.everfi.EverfiApiException;
import gov.nysenate.ess.core.service.pec.external.everfi.EverfiApiClient;
import gov.nysenate.ess.core.util.OutputUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Service
public class EverfiUserClient {

    private static final String USER_ENDPOINT = "/v1/admin/users";
    private static final String SINGLE_USER_FIELDS =
            "email,first_name,last_name,sso_id,employee_id,student_id,active,user_rule_set_roles,category_labels";
    private static final String REGISTRATION_SETS_ENDPOINT = "/v1/admin/registration_sets";

    private final EverfiApiClient everfiApiClient;
    private final EverfiUserPayloadFactory payloadFactory;

    public EverfiUserClient(EverfiApiClient everfiApiClient, EverfiUserPayloadFactory payloadFactory) {
        this.everfiApiClient = everfiApiClient;
        this.payloadFactory = payloadFactory;
    }

    public List<EverfiUser> fetchAll() throws IOException {
        return fetchAll(EverfiUsersRequest.DEFAULT_PAGE_SIZE);
    }

    public List<EverfiUser> fetchAll(int pageSize) throws IOException {
        List<EverfiUser> users = new ArrayList<>();
        forEachPage(pageSize, users::addAll);
        return users;
    }

    public void forEachPage(Consumer<List<EverfiUser>> pageConsumer) throws IOException {
        forEachPage(EverfiUsersRequest.DEFAULT_PAGE_SIZE, pageConsumer);
    }

    public void forEachPage(int pageSize, Consumer<List<EverfiUser>> pageConsumer) throws IOException {
        Assert.notNull(pageConsumer, "pageConsumer must not be null");

        EverfiUsersRequest request = new EverfiUsersRequest(everfiApiClient, 1, pageSize);
        while (request != null) {
            pageConsumer.accept(request.getUsers());
            request = request.next();
        }
    }

    public EverfiUser findByUuid(String uuid) throws IOException {
        Assert.hasText(uuid, "uuid must not be empty");

        String endpoint = USER_ENDPOINT + "/" + uuid + "?fields[users]=" + SINGLE_USER_FIELDS;
        try {
            String data = everfiApiClient.get(endpoint);
            return parseUserFromData(data);
        } catch (EverfiApiException ex) {
            if (ex.getStatusCode() == 404) {
                return null;
            }
            throw ex;
        }
    }

    public EverfiUser addUser(EverfiAddUserCommand command) throws IOException {
        Assert.notNull(command, "command must not be null");

        String payload = payloadFactory.buildAddUserPayload(command);
        String data = everfiApiClient.post(REGISTRATION_SETS_ENDPOINT, payload);
        return parseUserFromData(data);
    }

    public EverfiUser updateUser(EverfiUpdateUserCommand command) throws IOException {
        Assert.notNull(command, "command must not be null");

        String payload = payloadFactory.buildUpdateUserPayload(command);
        String data = everfiApiClient.patch(REGISTRATION_SETS_ENDPOINT + "/" + command.uuid(), payload);
        return parseUserFromData(data);
    }

    private EverfiUser parseUserFromData(String data) throws IOException {
        if (data == null) {
            return null;
        }

        ObjectMapper mapper = OutputUtils.jsonMapper;
        JsonNode rootNode = mapper.readTree(data);
        JsonNode userNode = rootNode.get("data");
        return userNode == null ? null : mapper.treeToValue(userNode, EverfiUser.class);
    }
}
