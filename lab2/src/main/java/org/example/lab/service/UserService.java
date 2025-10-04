package org.example.lab.service;

import lombok.RequiredArgsConstructor;
import org.example.lab.repository.UserRepository;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
}
