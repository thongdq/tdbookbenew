package td.book.tdbook.integration;


import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;

import td.book.tdbook.model.User;
import td.book.tdbook.util.HttpUtil;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application-test.properties")
public class UserIntegrationTest {

    @LocalServerPort
    private int port;

    TestRestTemplate restTemplate = new TestRestTemplate();
    HttpHeaders headers = new HttpHeaders();

    @Test
    public void testCreateUser() throws Exception {

        User user = new User.UserBuilder().withUserName("username1")
                                          .withEmail("email1")
                                          .withPassword("password1")
                                          .build();

        ResponseEntity<String> response = restTemplate.postForEntity(HttpUtil.createURLWithPort(port, "/users"), user, String.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    public void testAllUsers() throws Exception {
        // mockMvc.perform(get("/users"))
        //         .andExpect(status().isOk())
        //         .andExpect(jsonPath("$", Matchers.hasSize(1)));
    }

}
