package org.example.lab4.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/v2")
public class RegisterViewController {

    @GetMapping("/register")
    public String registerPage() {
        return "register"; // без .html, Spring Boot автоматично підставить
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login"; // аналогічно для login.html
    }

    @GetMapping("/home")
    public String homePage() {
        return "index";
    }
}