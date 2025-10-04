package org.example.lab.web;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.example.lab.entity.User;
import org.example.lab.service.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(adminService.getUsers());
    }

    @PostMapping("/users/{id}/grant-admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> grantAdmin(@PathVariable String id) {
        adminService.grantAdmin(new ObjectId(id));
        return ResponseEntity.ok("Користувач став адміністратором");
    }

    @PostMapping("/users/{id}/revoke-admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> revokeAdmin(@PathVariable String id) {
        adminService.revokeAdmin(new ObjectId(id));
        return ResponseEntity.ok("У користувача забрано права адміністратора");
    }
}