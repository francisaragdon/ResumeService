package com.backend.project.controller;

import com.backend.project.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
@RequestMapping("/api")
public class UserController {

    private final Map<Integer, User> users = new HashMap<>();
    private final AtomicInteger idCounter = new AtomicInteger();

    private static final String VALID_TOKEN = "FAKE-TOKEN-123";

    // ======================
    // LOGIN
    // ======================
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody Map<String, String> request) {

        String username = request.get("username");
        String password = request.get("password");

        if ("admin".equals(username) && "1234".equals(password)) {
            Map<String, String> response = new HashMap<>();
            response.put("token", VALID_TOKEN);
            System.out.println("token hash " + response);
            return ResponseEntity.ok(response);
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    // ======================
    // CREATE USER (Protected)
    // ======================
    @PostMapping("/users")
    public ResponseEntity<User> createUser(
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestBody User user) {

        if (! isAuthorized(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        int id = idCounter.incrementAndGet();
        user.setId(id);
        users.put(id, user);

        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    // ======================
    // GET ALL USERS (Protected)
    // ======================
    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers(
            @RequestHeader(value = "Authorization", required = false) String token) {
        System.out.println("token " + token);
        if (!isAuthorized(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return ResponseEntity.ok(new ArrayList<>(users.values()));
    }


    // ======================
    // DELETE USER (Protected)
    // ======================
    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(
            @RequestHeader(value = "Authorization", required = false) String token,
            @PathVariable int id) {

        if (!isAuthorized(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        users.remove(id);
        return ResponseEntity.noContent().build();
    }

    // ======================
    // AUTH CHECK
    // ======================
    private boolean isAuthorized(String token) {
        if (token == null) return false;
        System.out.println("isAuthorized " + token);
        if (token.startsWith("Bearer ")) {
            System.out.println("yes " + token);
            token = token.substring(7);
        }

        return VALID_TOKEN.equals(token);
    }
}