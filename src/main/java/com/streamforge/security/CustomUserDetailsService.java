package com.streamforge.security;

import com.streamforge.entity.User;
import com.streamforge.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService
        implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(
            String credential
    ) throws UsernameNotFoundException {

        String value =
                credential == null
                        ? ""
                        : credential.trim();

        if (value.isBlank()) {
            throw new UsernameNotFoundException(
                    "Username or email is required"
            );
        }

        User user =
                userRepository
                        .findByUsername(value)
                        .or(() ->
                                userRepository
                                        .findByEmail(value)
                        )
                        .orElseThrow(() ->
                                new UsernameNotFoundException(
                                        "Invalid username or email"
                                )
                        );

        if (!Boolean.TRUE.equals(
                user.getIsActive()
        )) {

            throw new UsernameNotFoundException(
                    "User account is inactive"
            );
        }

        if (
                user.getRole() == null ||
                user.getRole().getRoleName() == null ||
                user.getRole().getRoleName().isBlank()
        ) {

            throw new UsernameNotFoundException(
                    "No role assigned to user"
            );
        }

        return new CustomUserDetails(user);
    }
}