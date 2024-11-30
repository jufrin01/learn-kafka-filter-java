package com.example.authservice.controller;

import com.example.authservice.dto.LoginRequest;
import com.example.authservice.dto.UserRequest;
import com.example.authservice.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/verif")
@AllArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping(value = "/register")
    public ResponseEntity<Object> register(@Valid @RequestBody UserRequest userRequest) {
        return ResponseEntity.ok(userService.register(userRequest));
    }

    @PostMapping(value = "/login")
    public ResponseEntity<Object> login(@Valid @RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(userService.login(loginRequest));
    }

    @GetMapping(value = "/validateAccessToken")
    public ResponseEntity<Object> validateAccessToken(@RequestParam(value = "accessToken", defaultValue = "")String accessToken) {
        return ResponseEntity.ok(userService.validateAccessToken(accessToken));
    }

    @GetMapping(value = "/test")
    public ResponseEntity<Object> doTest() {
        return ResponseEntity.ok("Success Test");
    }
}

