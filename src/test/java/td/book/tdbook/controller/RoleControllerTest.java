package td.book.tdbook.controller;

import org.hamcrest.Matchers;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import td.book.tdbook.enumtd.ERole;
import td.book.tdbook.model.Role;
import td.book.tdbook.security.jwt.AuthEntryPointJwt;
import td.book.tdbook.security.jwt.JwtUtils;
import td.book.tdbook.security.oauth2.CustomOAuth2UserService;
import td.book.tdbook.security.oauth2.HttpCookieOAuth2AuthorizationRequestRepository;
import td.book.tdbook.security.oauth2.OAuth2AuthenticationFailureHandler;
import td.book.tdbook.security.oauth2.OAuth2AuthenticationSuccessHandler;
import td.book.tdbook.security.services.UserDetailsServiceImpl;
import td.book.tdbook.service.RoleService;

@ExtendWith(SpringExtension.class)
@WebMvcTest(RoleController.class)
public class RoleControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    RoleService roleService;

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
    AuthEntryPointJwt unauthorizedHandler;

    @MockBean
    JwtUtils jwtUtils;

    @Test
    public void testGetAll() throws Exception {
        List<Role> roles = IntStream.range(0, 1).mapToObj(i -> new Role(ERole.ROLE_ADMIN)).collect(Collectors.toList());

        Mockito.when(roleService.findAll()).thenReturn(roles);

        mockMvc.perform(get("/roles"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", Matchers.hasSize(1)));
    }

}
