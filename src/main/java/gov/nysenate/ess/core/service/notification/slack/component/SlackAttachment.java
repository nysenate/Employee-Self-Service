package gov.nysenate.ess.core.service.notification.slack.component;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.List;

public class SlackAttachment {
    private final String fallback;
    private final String text;
    private final String pretext;
    private final String color;
    private final String title;
    private final String titleLink;
    private final List<SlackField> fields;

    public SlackAttachment(SlackAttachment other) {
        this.fallback = other.fallback;
        this.text = other.text;
        this.pretext = other.pretext;
        this.color = other.color;
        this.title = other.title;
        this.titleLink = other.titleLink;
        this.fields = other.fields == null ? null :
                other.fields.stream().map(SlackField::new).toList();
    }

    private JsonArray prepareFields() {
        JsonArray data = new JsonArray();
        for (SlackField field : fields) {
            data.add(field.toJson());
        }
        return data;
    }

    public JsonObject toJson() {
        JsonObject data = new JsonObject();

        if (fallback == null) {
            throw new IllegalArgumentException("Missing Fallback @ SlackAttachment");
        } else {
            data.addProperty("fallback", fallback);
        }

        if (text != null) {
            data.addProperty("text", text);
        }

        if (pretext != null) {
            data.addProperty("pretext", pretext);
        }

        if (color != null) {
            data.addProperty("color", color);
        }

        if (title != null) {
            data.addProperty("title", title);
        }

        if (titleLink != null) {
            data.addProperty("title_link", titleLink);
        }

        if (fields != null && !fields.isEmpty()) {
            data.add("fields", prepareFields());
        }
        return data;
    }
}
