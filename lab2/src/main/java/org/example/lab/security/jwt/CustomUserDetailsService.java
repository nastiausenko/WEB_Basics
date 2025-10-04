package org.example.lab.security.jwt;

import lombok.RequiredArgsConstructor;
import org.example.lab.repository.UserRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .map(userEntity -> User.builder()
                        .username(userEntity.getEmail())
                        .password(userEntity.getPassword())
                        .authorities("ROLE_USER")
                        .build())
                .orElseThrow(() -> new RuntimeException(email));
    }
}
