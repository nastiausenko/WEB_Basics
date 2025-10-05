package org.example.lab.service;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.example.lab.entity.User;
import org.example.lab.repository.UserRepository;
import org.example.lab.service.exceptions.UserNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final UserRepository userRepository;

    public List<User> getUsers() {
        return userRepository.findAll();
    }

    public void grantAdmin(ObjectId id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        List<String> roles = new ArrayList<>(user.getRoles());

        if (!roles.contains("ROLE_ADMIN")) {
            roles.add("ROLE_ADMIN");
        }

        user.setRoles(roles);
        userRepository.save(user);
    }

    public void revokeAdmin(ObjectId id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        List<String> roles = new ArrayList<>(user.getRoles());

        roles.remove("ROLE_ADMIN");

        user.setRoles(roles);
        userRepository.save(user);
    }
}
