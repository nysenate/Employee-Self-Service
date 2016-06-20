package gov.nysenate.ess.core.service.notification.base.message.base;

/**
 * Created by senateuser on 6/14/2016.
 */
public interface Attachment extends Component {

    <T> Class<T> getAttachmentType();

    <T> T getAttachment();

    void setAttachmentCapacity(int capacity);

    int getAttachmentCapacity();

    int getAttachmentSize();

    boolean isOverSize();

    String getAttachmentName();

    String getAttachmentPath();

}
