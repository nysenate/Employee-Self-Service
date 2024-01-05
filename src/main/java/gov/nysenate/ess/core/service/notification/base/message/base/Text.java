package gov.nysenate.ess.core.service.notification.base.message.base;

/**
 * Created by Chenguang He on 6/14/2016.
 */
public interface Text extends Component {
    /**
     * get the content of text
     * @return the content
     */
    String getContent();

    /**
     * get the bind string of text
     *
     * @return the bind string
     */
    String getBind();
}
