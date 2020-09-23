package gov.nysenate.ess.core.service.pec.external.everfi;

import java.io.IOException;

public interface ESSEverfiRecordService {

    /**
     * Get updates of user records from Everfi on a spring cron.
     * By default this is on the top of the hour every hours
     * @throws IOException
     */
    public void getUpdatesFromEverfi() throws IOException;

    /**
     * Contacts everfis user assignments and progress api and assigns / updates personnel tasks for employees
     * @param since
     * @throws IOException
     */
    public void contactEverfiForUserRecords(String since) throws IOException;

    /**
     * Refreshes the assignment id and content id caches.
     * This is important for adding a task to be processed by pec without restarting ess
     */
    public void refreshCaches();


}
