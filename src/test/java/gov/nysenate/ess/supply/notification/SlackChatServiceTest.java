package gov.nysenate.ess.supply.notification;

import com.google.common.collect.ImmutableList;
import gov.nysenate.ess.core.BaseTest;
import gov.nysenate.ess.core.annotation.SillyTest;
import gov.nysenate.ess.core.service.notification.slack.service.SlackChatService;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

@Category(SillyTest.class)
public class SlackChatServiceTest extends BaseTest {

    @Autowired
    SlackChatService slackChatService;

    private static final String testMessageText = "This is a test message from ";

    private static final ImmutableList<String> slackers =
            ImmutableList.of("readman");

    @Test
    public void sendMessageTest() {
        slackChatService.sendMessage(testMessageText);
    }

}

