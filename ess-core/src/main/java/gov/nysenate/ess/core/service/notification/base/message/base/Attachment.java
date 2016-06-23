package gov.nysenate.ess.core.service.notification.base.message.base;

/**
 *  attachment message
 * Created by Chenguang He  on 6/15/2016.
 */
public interface Attachment extends Component {

    /**
     * get the type of attachment
     *
     * @param <T> the type
     * @return the attachment class
     */
    <T> Class<T> getAttachmentType();

    /**
     * get the attachment
     * @param <T> attachment type
     * @return attachment
     */
    <T> T getAttachment();

    /**
     * set the max size of attachment
     * @param capacity the max size of attachment
     */
    void setAttachmentCapacity(int capacity);

    /**
     * get the max size of attachment
     * @return the size
     */
    int getAttachmentCapacity();

    /**
     * get attachment size
     * @return size of attachment
     */
    int getAttachmentSize();

    /**
     * return if the size of attachment is larger than the capacity
     * @return true if yes
     */
    boolean isOverSize();

    /**
     * get the name of attachment
     * @return name
     */
    String getAttachmentName();

    /**
     * get the file path of attachment
     * @return path
     */
    String getAttachmentPath();

}
