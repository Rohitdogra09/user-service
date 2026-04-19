package user_service.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import user_service.dto.UserRequest;
import user_service.dto.UserResponse;
import user_service.entity.User;
import user_service.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * UserService - Business Logic Layer
 * This layer sits between Controller and Repository
 * Controller  →  Service  →  Repository  →  Database
 * All business rules live here:
 * → Check duplicate emails
 * → Map DTOs to Entities and back
 * → Handle errors with meaningful messages
 */


@Service                    // Marks this as a Spring Service bean
@RequiredArgsConstructor    // Lombok: generates constructor for all 'final' fields
// This is how Spring injects UserRepository automatically

public class UserService {

    // 'final' + @RequiredArgsConstructor = automatic constructor injection
    private final UserRepository userRepository;

    /**
     * CREATE a new user
     * Steps:
     * 1. Check if email already exists → throw error if yes
     * 2. Map UserRequest DTO → User Entity
     * 3. Save to database
     * 4. Map saved Entity → UserResponse DTO
     * 5. Return response
     */
    public UserResponse createUser(UserRequest request) {

        // Step 1: Check duplicate email
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists: " + request.getEmail());
        }

        // Step 2: Build User entity from request DTO using Builder pattern
        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(request.getPassword())    // ⚠️ Hash with BCrypt in production!
                .phone(request.getPhone())
                .role(request.getRole() != null     // If role not provided → default CUSTOMER
                        ? request.getRole()
                        : User.Role.CUSTOMER)
                .build();

        // Step 3 & 4: Save and return response
        User saved = userRepository.save(user);     // INSERT INTO users...
        return mapToResponse(saved);
    }

    /**
     * GET a single user by their ID
     * orElseThrow → if not found, throws exception with message
     */
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        return mapToResponse(user);
    }

    /**
     * GET all users from database
     * .stream()           → convert List to Stream for processing
     * .map(this::mapToResponse) → convert each User entity to UserResponse
     * .collect(...)       → collect results back into a List
     */
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * UPDATE an existing user
     * Note: email and password are NOT updated here (separate flows)
     * Only name, phone, and role can be updated
     */
    public UserResponse updateUser(Long id, UserRequest request) {

        // Find existing user or throw error
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        // Update only allowed fields
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPhone(request.getPhone());
        user.setRole(request.getRole());
        // @PreUpdate in entity auto-updates 'updatedAt' timestamp

        return mapToResponse(userRepository.save(user));    // UPDATE users SET...
    }

    /**
     * DELETE a user by ID
     * First checks if user exists, then deletes
     */
    public void deleteUser(Long id) {
        // Check existence before deleting
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found with id: " + id);
        }
        userRepository.deleteById(id);      // DELETE FROM users WHERE id = ?
    }

    /**
     * PRIVATE HELPER METHOD
     * Converts User Entity → UserResponse DTO
     * Used internally in every method above
     * Password is intentionally excluded from response
     */
    private UserResponse mapToResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .build();
    }
}


