package user_service.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import user_service.dto.*;
import user_service.service.UserService;

import java.util.List;
/**
 * UserController - REST API endpoints
 * Added: /login, /forgot-password, /reset-password, /oauth-login
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * POST /api/users
     * Register new user
     */
    @PostMapping
    public ResponseEntity<AuthResponse> register(
            @Valid @RequestBody UserRequest request) {
        return new ResponseEntity<>(
                userService.register(request), HttpStatus.CREATED);
    }

    /**
     * POST /api/users/login
     * Login with email + password
     * Returns JWT token
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(userService.login(request));
    }

    /**
     * POST /api/users/forgot-password
     * Send password reset email
     */
    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(
            @RequestBody ForgotPasswordRequest request) {
        return ResponseEntity.ok(
                userService.forgotPassword(request.getEmail()));
    }

    /**
     * POST /api/users/reset-password
     * Reset password using token from email
     */
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request) {
        return ResponseEntity.ok(userService.resetPassword(request));
    }

    /**
     * POST /api/users/oauth-login
     * Login via Google/GitHub
     */
    @PostMapping("/oauth-login")
    public ResponseEntity<AuthResponse> oauthLogin(
            @RequestBody OAuthRequest request) {
        return ResponseEntity.ok(userService.oauthLogin(
                request.getProvider(),
                request.getOauthId(),
                request.getEmail(),
                request.getFirstName(),
                request.getLastName()
        ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserRequest request) {
        return ResponseEntity.ok(userService.updateUser(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok("User deleted successfully");
    }
}
