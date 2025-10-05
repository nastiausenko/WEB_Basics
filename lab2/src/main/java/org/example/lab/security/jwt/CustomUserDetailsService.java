package org.example.lab.security.jwt;

import lombok.RequiredArgsConstructor;
import org.example.lab.repository.UserRepository;
import org.example.lab.service.exceptions.UserNotFoundException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

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
                        .authorities(
                                userEntity.getRoles().stream()
                                        .map(SimpleGrantedAuthority::new)
                                        .collect(Collectors.toList()))
                        .build())
                .orElseThrow(() -> new UserNotFoundException(email));
    }
}
