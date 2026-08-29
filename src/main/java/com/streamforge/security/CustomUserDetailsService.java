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
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                "User not found: " + username
                        )
                );

        if (!Boolean.TRUE.equals(user.getIsActive())) {
            throw new UsernameNotFoundException(
                    "User account is inactive: " + username
            );
        }

        if (user.getRole() == null) {
            throw new UsernameNotFoundException(
                    "No role assigned to user: " + username
            );
        }

        System.out.println(
                "User loaded: "
                        + user.getUsername()
                        + " | Role: "
                        + user.getRole().getRoleName()
        );

        return new CustomUserDetails(user);
    }
}