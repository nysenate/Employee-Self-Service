package gov.nysenate.ess.core.util;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * A utility class that uses the thread pool configured in Spring to run a given {@link Runnable}
 * @see Async
 */
@Service
@Async(value = "essAsync")
public class AsyncRunner {
    public void run(Runnable runnable) {
        runnable.run();
    }
}
