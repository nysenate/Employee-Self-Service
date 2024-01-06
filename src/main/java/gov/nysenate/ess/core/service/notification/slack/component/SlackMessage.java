package gov.nysenate.ess.core.service.notification.slack.component;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class SlackMessage {
    private final String text;
    private List<SlackAttachment> attach = null;
    private String channel = null;
    private String icon = null;
    private String username = null;
    private List<String> mentions = null;

    public SlackMessage(String text) {
        this.text = text;
    }

    public SlackMessage(SlackMessage other) {
        this.text = other.text;
        this.channel = other.channel;
        this.icon = other.icon;
        this.username = other.username;
        this.mentions = other.mentions != null ? new ArrayList<>(other.mentions) : null;
        this.attach = other.attach != null ? other.attach.stream()
                .map(SlackAttachment::new)
                .collect(Collectors.toList())
                : null;
    }

    public JsonObject prepare() {
        JsonObject slackMessage = new JsonObject();
        if (channel != null) {
            slackMessage.addProperty("channel", channel);
        }

        if (username != null) {
            slackMessage.addProperty("username", username);
        }

        if (icon != null) {
            if (icon.contains("http")) {
                slackMessage.addProperty("icon_url", icon);
            } else {
                slackMessage.addProperty("icon_emoji", icon);
            }
        }

        if (text == null) {
            throw new IllegalArgumentException("Missing Text field @ SlackMessage");
        }

        slackMessage.addProperty("text", addMentions(text, mentions));

        // Allows for '@' mentions
        slackMessage.addProperty("link_names", 1);

        if (attach != null && !attach.isEmpty()) {
            slackMessage.add("attachments", prepareAttach());
        }

        return slackMessage;
    }

    private JsonArray prepareAttach() {
        JsonArray attachs = new JsonArray();
        for (SlackAttachment attach : attach) {
            attachs.add(attach.toJson());
        }
        return attachs;
    }

    public SlackMessage setChannel(String channel) {
        this.channel = channel;
        return this;
    }

    public SlackMessage setMentions(Collection<String> mentions) {
        this.mentions = mentions == null ? null : new ArrayList<>(mentions);
        return this;
    }

    /**
     * Adds slack api formatted user name mentions to the front of a string message
     *
     * @param message  String
     * @param mentions Collection<String>
     * @return String - the message with mentions added
     */
    private String addMentions(String message, Collection<String> mentions) {
        if (mentions == null) {
            return message;
        }
        String mentionString = mentions.stream()
                .filter(StringUtils::isNotBlank)
                .reduce("", (a, b) -> a + "@" + b + " ");
        return mentionString + (message != null ? "\n" + message : "");
    }
}
