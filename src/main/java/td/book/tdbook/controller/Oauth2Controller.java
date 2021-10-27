package td.book.tdbook.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import td.book.tdbook.payload.JwtResponse;
import td.book.tdbook.security.services.UserDetailsImpl;

@CrossOrigin(origins = "${tdbook.fe.url}", maxAge = 3600)
@RestController
@RequestMapping("/oauth2")
public class Oauth2Controller {

    @GetMapping("/user")
    public ResponseEntity<JwtResponse> getUser() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        List<String> roles = userDetails.getAuthorities().stream()
                                    .map(item -> item.getAuthority())
                                    .collect(Collectors.toList());

        return new ResponseEntity<>(new JwtResponse(userDetails.getId(),
                                                    userDetails.getUsername(),
                                                    userDetails.getEmail(),
                                                    roles), HttpStatus.OK);
    }
    
}
