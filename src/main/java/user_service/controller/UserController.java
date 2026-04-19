package user_service.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import user_service.dto.UserRequest;
import user_service.dto.UserResponse;
import user_service.service.UserService;

import java.util.List;

/**
 * UserController - REST API Layer
 *
 * This is the ENTRY POINT for all HTTP requests related to users
 * Handles incoming requests and delegates to UserService
 *
 * Base URL: http://localhost:8081/api/users
 *
 * Available endpoints:
 * POST   /api/users          → Create new user
 * GET    /api/users/{id}     → Get user by ID
 * GET    /api/users          → Get all users
 * PUT    /api/users/{id}     → Update user
 * DELETE /api/users/{id}     → Delete user
 */
@RestController             // Combines @Controller + @ResponseBody (returns JSON)
@RequestMapping("/api/users")   // Base path for all endpoints in this controller
@RequiredArgsConstructor        // Lombok: auto-injects UserService via constructor
public class UserController {

    private final UserService userService;

    /**
     * POST http://localhost:8081/api/users
     * Creates a new user
     * @Valid triggers validation annotations in UserRequest
     * Returns 201 CREATED status on success
     */
    @PostMapping
    public ResponseEntity<UserResponse> createUser(
            @Valid @RequestBody UserRequest request) {
        //      ↑ Validates request fields    ↑ Converts JSON body → UserRequest object
        return new ResponseEntity<>(userService.createUser(request), HttpStatus.CREATED);
    }

    /**
     * GET http://localhost:8081/api/users/1
     * Gets a single user by ID
     * Returns 200 OK with user data
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(
            @PathVariable Long id) {    // Extracts {id} from URL
        return ResponseEntity.ok(userService.getUserById(id));
    }

    /**
     * GET http://localhost:8081/api/users
     * Gets all users in the system
     * Returns 200 OK with list of users
     */
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    /**
     * PUT http://localhost:8081/api/users/1
     * Updates an existing user by ID
     * Returns 200 OK with updated user data
     */
    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserRequest request) {
        return ResponseEntity.ok(userService.updateUser(id, request));
    }

    /**
     * DELETE http://localhost:8081/api/users/1
     * Deletes a user by ID
     * Returns 200 OK with success message
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok("User deleted successfully");
    }
}
