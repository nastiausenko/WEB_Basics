package org.example.lab.web;

import lombok.RequiredArgsConstructor;
import org.example.lab.service.UserService;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
}
