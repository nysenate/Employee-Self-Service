package gov.nysenate.ess.web.controller.page;

import gov.nysenate.ess.web.BaseTests;
import org.junit.Test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

public class LoginPageControllerTests extends BaseTests
{
    @Test
    public void getReturnsLoginView() throws Exception {
        mockMvc.perform(get("/login"))
               .andExpect(status().isOk())
               .andExpect(view().name("login"));
    }
}
