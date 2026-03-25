package gov.nysenate.ess.core.service.pec.external.everfi.user;

import gov.nysenate.ess.core.service.pec.external.everfi.EverfiApiClient;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Service
public class EverfiUserRemoteDao {

    private final EverfiApiClient everfiApiClient;

    public EverfiUserRemoteDao(EverfiApiClient everfiApiClient) {
        this.everfiApiClient = everfiApiClient;
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
}
