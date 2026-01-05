package gov.nysenate.ess.time.client.view.attendance;

import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.time.model.attendance.TimeOffRequestComment;

import java.time.LocalDateTime;


public class TimeOffRequestCommentView implements ViewObject {

    protected int requestId;
    protected int authorId;
    protected LocalDateTime timestamp;
    protected String text;

    public TimeOffRequestCommentView() {}

    public TimeOffRequestCommentView(TimeOffRequestComment comment) {
        this.authorId = comment.getAuthorId();
        this.requestId = comment.getRequestId();
        this.text = comment.getText();
        this.timestamp = comment.getTimestamp();
    }


    public TimeOffRequestComment toTimeOffRequestComment() {
        TimeOffRequestComment comment = new TimeOffRequestComment();
        comment.setRequestId(requestId);
        comment.setAuthorId(authorId);
        comment.setTimestamp(timestamp);
        comment.setText(text);
        return comment;
    }

    public int getRequestId() {
        return requestId;
    }

    public int getAuthorId() {
        return authorId;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getText() {
        return text;
    }

    @Override
    public String getViewType() {
        return "time-off-request-comment";
    }
}
