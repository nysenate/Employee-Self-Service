package gov.nysenate.ess.web.controller.page;

import gov.nysenate.ess.core.annotation.SillyTest;
import gov.nysenate.ess.web.WebTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@Category(SillyTest.class)
public class LoginPageControllerTest extends WebTest
{
    @Test
    public void getReturnsLoginView() throws Exception {
        mockMvc.perform(get("/login"))
               .andExpect(status().isOk())
               .andExpect(view().name("login"));
    }
}
