package td.book.tdbook.controller;

import org.hamcrest.Matchers;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import td.book.tdbook.model.User;
import td.book.tdbook.security.jwt.AuthEntryPointJwt;
import td.book.tdbook.security.jwt.JwtUtils;
import td.book.tdbook.security.oauth2.CustomOAuth2UserService;
import td.book.tdbook.security.oauth2.HttpCookieOAuth2AuthorizationRequestRepository;
import td.book.tdbook.security.oauth2.OAuth2AuthenticationFailureHandler;
import td.book.tdbook.security.oauth2.OAuth2AuthenticationSuccessHandler;
import td.book.tdbook.security.services.UserDetailsServiceImpl;
import td.book.tdbook.service.UserService;
import td.book.tdbook.util.JsonUtil;

@ExtendWith(SpringExtension.class)
@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    MockMvc mockMvc;

    //Annotate each required bean via @MockBean annotation.
    //Because this controller does not need to be protected by Spring Application
    @MockBean
    UserDetailsServiceImpl userDetailsServiceImpl;

    @MockBean
    CustomOAuth2UserService customOAuth2UserService;

    @MockBean
    OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;

    @MockBean
    OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler;

    @MockBean
    HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;

    @MockBean
    AuthEntryPointJwt authEntryPointJwt;

    @MockBean
    JwtUtils jwtUtils;

    @MockBean
    UserService userService;

    @Test
    public void testCreateUser() throws Exception {
        mockMvc.perform(post("/users")
                .content(JsonUtil.asJsonString(new User.UserBuilder().withUserName("username1")
                                                                     .withEmail("email1")
                                                                     .build()))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    public void findAll() throws Exception {

        List<User> users = IntStream.range(0, 1)
                            .mapToObj(i -> new User.UserBuilder().withUserName("username1")
                                                                 .withEmail("email1")
                                                                 .build())
                            .collect(Collectors.toList());

        Mockito.when(userService.findAll()).thenReturn(users);

        mockMvc.perform(get("/users"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", Matchers.hasSize(1)));
    }
}
