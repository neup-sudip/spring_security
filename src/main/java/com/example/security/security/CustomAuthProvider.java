package com.example.security.security;

import com.example.security.entity.UserEntity;
import com.example.security.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Optional;
import java.util.Set;

@RequiredArgsConstructor
@Component
public class CustomAuthProvider implements AuthenticationProvider {

    private final UserService userService;
    private final CustomPasswordEncoder customPasswordEncoder;
    private final HttpServletRequest request;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public Authentication authenticate(Authentication auth) throws AuthenticationException {
        logger.info("CustomAuthProvider :: {}", auth);
        boolean enabled = true;
        boolean accountNonExpired = true;
        boolean credentialNonExpired = true;
        boolean accountNonLocked = true;

        String userId = auth.getName();
        String password = auth.getCredentials().toString();
        LinkedHashMap<String, Object> details = (LinkedHashMap<String, Object>) auth.getDetails();

        String source = (String) details.get("source");
        Optional<UserEntity> optUser = userService.getUserByUsername(userId.trim());
        if (optUser.isPresent()) {
            UserEntity userDetails = optUser.get();

            if (!userDetails.isEnabled()) {
                throw new DisabledException("Account disabled !");
            } else if (userDetails.isLocked()) {
                throw new LockedException("Account locked !");
            } else if (!customPasswordEncoder.matches(password, userDetails.getPassword())) {
                throw new BadCredentialsException("Incorrect Password !");
            }

            UserDetails userdetails = new User(userDetails.getUsername(), userDetails.getPassword(),
                    enabled, accountNonExpired, credentialNonExpired, accountNonLocked, getAuthorities());

            return new UsernamePasswordAuthenticationToken(userdetails, password,
                    getAuthorities());
        } else {
            throw new BadCredentialsException("User does not exist!");
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }

    private Set<GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> authorities = new HashSet<>();
        authorities.add(new SimpleGrantedAuthority("CREATE"));
        return authorities;
    }
}
