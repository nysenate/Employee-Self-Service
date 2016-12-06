package gov.nysenate.ess.web;

import gov.nysenate.ess.core.annotation.IntegrationTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.test.web.servlet.ResultMatcher;

import java.util.regex.Pattern;

import static junit.framework.TestCase.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@Category(IntegrationTest.class)
public class AppIT extends WebTest
{
    @Test
    public void simple() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(redirectedUrlPattern("/time/record/entry"));
    }

    private static ResultMatcher redirectedUrlPattern(final String expectedUrlPattern) {
        return result -> {
            Pattern pattern = Pattern.compile("\\A" + expectedUrlPattern + "\\z");
            String redirectedUrl = result.getResponse().getRedirectedUrl();
            boolean matches = redirectedUrl != null && pattern.matcher(redirectedUrl).find();
            assertTrue("Invalid redirect: got '" + redirectedUrl + "' expected '" + expectedUrlPattern + "'",
                    matches);
        };
    }
}
