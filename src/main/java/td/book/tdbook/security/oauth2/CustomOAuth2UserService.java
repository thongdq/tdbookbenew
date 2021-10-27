package td.book.tdbook.security.oauth2;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import td.book.tdbook.enumtd.ERole;
import td.book.tdbook.model.Role;
import td.book.tdbook.model.User;
import td.book.tdbook.model.UserRole;
import td.book.tdbook.model.UserRoleId;
import td.book.tdbook.repository.UserRepository;
import td.book.tdbook.security.oauth2.exception.OAuth2AuthenticationProcessingException;
import td.book.tdbook.security.services.UserDetailsImpl;
import td.book.tdbook.service.RoleService;
import td.book.tdbook.service.UserRoleService;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    UserRoleService userRoleService;

    @Autowired
    RoleService roleService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(oAuth2UserRequest);

        try {
            return processOAuth2User(oAuth2UserRequest, oAuth2User);
        } catch(AuthenticationException ex) {
            throw ex;
        } catch(Exception e) {
            // Throwing an instance of AuthenticationException will trigger the OAuth2AuthenticationFailureHandler
            throw new InternalAuthenticationServiceException(e.getMessage(), e.getCause());
        }
    }

    private OAuth2User processOAuth2User(OAuth2UserRequest oAuth2UserRequest, OAuth2User oAuth2User) {
        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(
            oAuth2UserRequest.getClientRegistration().getRegistrationId(), oAuth2User.getAttributes());

        if(StringUtils.isEmpty(oAuth2UserInfo.getEmail())) {
            throw new OAuth2AuthenticationProcessingException("Email not found from OAuth2 provider");
        }

        Optional<User> uOptional = userRepository.findByEmail(oAuth2UserInfo.getEmail());
        User user;

        if(uOptional.isPresent()) {
            user = uOptional.get();
            if(user.getProvider() == null) {
                user = updateExistingUser(user, oAuth2UserInfo, oAuth2UserRequest.getClientRegistration().getRegistrationId());
            } else if(!user.getProvider().equals(AuthProvider.valueOf(oAuth2UserRequest.getClientRegistration().getRegistrationId()))) {
                throw new OAuth2AuthenticationProcessingException("Looks like you're signed up with " +
                    user.getProvider() + " account. Please use your " + user.getProvider() +
                    " account to login.");
            }
            user = updateExistingUser(user, oAuth2UserInfo, oAuth2UserRequest.getClientRegistration().getRegistrationId());
        } else {
            user = registerNewUser(oAuth2UserRequest, oAuth2UserInfo);
        }

        return UserDetailsImpl.build(user, oAuth2User.getAttributes());
    }

    private User registerNewUser(OAuth2UserRequest oAuth2UserRequest, OAuth2UserInfo oAuth2UserInfo) {
        List<ERole> roles = new ArrayList<>();
        roles.add(ERole.ROLE_USER);
        User user = new User.UserBuilder().withProvider(AuthProvider.valueOf(oAuth2UserRequest.getClientRegistration().getRegistrationId()))
                                          //.withProviderId(oAuth2UserInfo.getId())
                                          .withUserName(oAuth2UserInfo.getEmail())
                                          .withEmail(oAuth2UserInfo.getEmail())
                                          .withPassword(passwordEncoder.encode("admin123"))
                                          .isEmailVerified(true)
                                          .build();

        userRepository.save(user);
        Optional<Role> oRole = roleService.findByName(ERole.ROLE_USER);
        UserRole userRole = new UserRole();
            userRole.setId(new UserRoleId(user.getId(), oRole.get().getId()));
            userRole.setUser(user);
            userRole.setRole(oRole.get());
        userRoleService.save(userRole);

        List<UserRole> userRoles = new ArrayList<>();
        userRoles.add(userRole);

        user.setUserRoles(userRoles);

        return userRepository.save(user);
    }

    private User updateExistingUser(User existingUser, OAuth2UserInfo oAuth2UserInfo, String provider) {
        existingUser.setProvider(AuthProvider.valueOf(provider));
        existingUser.setEmailVerified(true);
        return userRepository.save(existingUser);
    }
    
}
