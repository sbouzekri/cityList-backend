package co.thecodest.citylist.web.rest;

import co.thecodest.citylist.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@IntegrationTest
@AutoConfigureMockMvc
public class AccountResourceIT {

    private static final String ENTITY_API_URL = "/api/account";

    @Autowired
    private MockMvc restAccountMockMvc;

    @Test
    void authorizedUser() throws Exception {
        restAccountMockMvc
                .perform(get(ENTITY_API_URL).with(httpBasic("user", "user")))
                .andExpect(status().isOk());
    }

    @Test
    void unAuthorizedUser() throws Exception {
        restAccountMockMvc
                .perform(get(ENTITY_API_URL).with(httpBasic("user", "password")))
                .andExpect(status().isUnauthorized());
    }
}
