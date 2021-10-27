package td.book.tdbook.controller;

import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;

import td.book.tdbook.enumtd.ERole;
import td.book.tdbook.model.Role;
import td.book.tdbook.model.User;
import td.book.tdbook.model.UserRole;
import td.book.tdbook.model.UserRoleId;
import td.book.tdbook.payload.JwtResponse;
import td.book.tdbook.payload.LoginRequest;
import td.book.tdbook.payload.MessageResponse;
import td.book.tdbook.payload.SignupRequest;
import td.book.tdbook.security.jwt.JwtUtils;
import td.book.tdbook.security.services.UserDetailsImpl;
import td.book.tdbook.service.RoleService;
import td.book.tdbook.service.UserRoleService;
import td.book.tdbook.service.UserService;

import org.springframework.transaction.annotation.Transactional;

@CrossOrigin(origins = "${tdbook.fe.url}",  maxAge = 3600)
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    UserService userService;

    @Autowired
    RoleService roleService;

    @Autowired
    UserRoleService userRoleService;

    @PostMapping("/signin")
    public ResponseEntity<?> signIn(@RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetailsImpl = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetailsImpl.getAuthorities().stream()
                                    .map(item -> item.getAuthority())
                                    .collect(Collectors.toList());

        return ResponseEntity.ok(new JwtResponse(jwt,
                                                 userDetailsImpl.getId(),
                                                 userDetailsImpl.getUsername(),
                                                 userDetailsImpl.getEmail(),
                                                 userDetailsImpl.getAvatar(),
                                                 roles));
    }

    @Transactional
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupRequest signupRequest) {
        if(userService.existsByuserName(signupRequest.getUsername())) {
            return ResponseEntity
                        .badRequest()
                        .body(new MessageResponse("Error: Username is already taken!"));
        }

        if (userService.existsByEmail(signupRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Email is already in use!"));
        }

        User user = new User.UserBuilder().withUserName(signupRequest.getUsername())
                                            .withEmail(signupRequest.getEmail())
                                            .withPassword(passwordEncoder.encode(signupRequest.getPassword()))
                                            .build();

        UserRole userRole;

        List<String> sRoles = signupRequest.getRole();
        Set<Role> roles = new HashSet<>();

        if(sRoles == null) {
            Role role = roleService.findByName(ERole.ROLE_USER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(role);
        } else {
            sRoles.forEach(role -> {
                switch(role) {
                    case "admin":
                        Role adminRole = roleService.findByName(ERole.ROLE_ADMIN)
                                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(adminRole);
                        break;
                    case "mod":
                        Role modRole = roleService.findByName(ERole.ROLE_MODERATOR)
                                        .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(modRole);

                        break;
                    default:
                        Role uRole = roleService.findByName(ERole.ROLE_USER)
                                        .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(uRole);
                }
            });
        }

        userService.save(user);

        Iterator<Role> iterator = roles.iterator();
        while(iterator.hasNext()) {
            Role role = iterator.next();

            userRole = new UserRole();
            userRole.setId(new UserRoleId(user.getId(), role.getId()));
            userRole.setUser(user);
            userRole.setRole(role);
            userRole.setCreatedOn(new Date());

            userRoleService.save(userRole);
        }

        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }

}
