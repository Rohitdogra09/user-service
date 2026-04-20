package user_service.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import user_service.dto.*;
import user_service.entity.User;
import user_service.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import user_service.util.JwtUtil;

import java.time.LocalDateTime;

import java.util.UUID;


/**
 * UserService - handles registration, login, password reset
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final EmailService emailService;

    // BCrypt password encoder
    private final BCryptPasswordEncoder passwordEncoder =
            new BCryptPasswordEncoder();

    /**
     * REGISTER - hash password before saving
     */
    public AuthResponse register(UserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists: "
                    + request.getEmail());
        }

        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                // ✅ Hash password with BCrypt
                .password(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .role(request.getRole() != null
                        ? request.getRole() : User.Role.CUSTOMER)
                .build();

        User saved = userRepository.save(user);

        // Generate JWT token
        String token = jwtUtil.generateToken(
                saved.getId(),
                saved.getEmail(),
                saved.getRole().name()
        );

        return AuthResponse.builder()
                .token(token)
                .userId(saved.getId())
                .firstName(saved.getFirstName())
                .lastName(saved.getLastName())
                .email(saved.getEmail())
                .role(saved.getRole().name())
                .message("Registration successful!")
                .build();
    }

    /**
     * LOGIN - verify password with BCrypt
     */
    public AuthResponse login(LoginRequest request) {
        // Find user by email
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException(
                        "User not found with email: " + request.getEmail()));

        // ✅ Verify password against BCrypt hash
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        // Generate JWT token
        String token = jwtUtil.generateToken(
                user.getId(),
                user.getEmail(),
                user.getRole().name()
        );

        return AuthResponse.builder()
                .token(token)
                .userId(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .message("Login successful!")
                .build();
    }

    /**
     * FORGOT PASSWORD - generate reset token and send email
     */
    public String forgotPassword(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException(
                        "No account found with email: " + email));

        // Generate random token
        String resetToken = UUID.randomUUID().toString();

        // Set token and expiry (15 minutes)
        user.setPasswordResetToken(resetToken);
        user.setPasswordResetExpiry(LocalDateTime.now().plusMinutes(15));
        userRepository.save(user);

        // Send email with reset link
        emailService.sendPasswordResetEmail(email, resetToken);

        return "Password reset email sent to: " + email;
    }

    /**
     * RESET PASSWORD - verify token and update password
     */
    public String resetPassword(ResetPasswordRequest request) {
        // Find user by reset token
        User user = userRepository.findByPasswordResetToken(request.getToken())
                .orElseThrow(() -> new RuntimeException(
                        "Invalid or expired reset token"));

        // Check if token has expired
        if (user.getPasswordResetExpiry().isBefore(LocalDateTime.now())) {
            throw new RuntimeException(
                    "Reset token has expired. Please request again.");
        }

        // ✅ Hash new password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));

        // Clear reset token after use
        user.setPasswordResetToken(null);
        user.setPasswordResetExpiry(null);
        userRepository.save(user);

        return "Password reset successfully!";
    }

    /**
     * OAUTH LOGIN - login via Google/GitHub
     * Creates account if first time, logs in if existing
     */
    public AuthResponse oauthLogin(String provider,
                                   String oauthId,
                                   String email,
                                   String firstName,
                                   String lastName) {

        // Check if user already exists with this OAuth account
        User user = userRepository
                .findByOauthProviderAndOauthId(provider, oauthId)
                .orElseGet(() -> {
                    // Check if email already exists
                    if (userRepository.existsByEmail(email)) {
                        // Link OAuth to existing account
                        User existing = userRepository.findByEmail(email).get();
                        existing.setOauthProvider(provider);
                        existing.setOauthId(oauthId);
                        return userRepository.save(existing);
                    }
                    // Create new user from OAuth data
                    User newUser = User.builder()
                            .firstName(firstName)
                            .lastName(lastName)
                            .email(email)
                            .password(passwordEncoder.encode(
                                    UUID.randomUUID().toString())) // random password
                            .phone("")
                            .role(User.Role.CUSTOMER)
                            .oauthProvider(provider)
                            .oauthId(oauthId)
                            .build();
                    return userRepository.save(newUser);
                });

        String token = jwtUtil.generateToken(
                user.getId(),
                user.getEmail(),
                user.getRole().name()
        );

        return AuthResponse.builder()
                .token(token)
                .userId(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .message("OAuth login successful!")
                .build();
    }

    /**
     * GET user by ID
     */
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "User not found with id: " + id));
        return mapToResponse(user);
    }

    /**
     * GET all users
     */
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * UPDATE user
     */
    public UserResponse updateUser(Long id, UserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "User not found with id: " + id));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPhone(request.getPhone());
        user.setRole(request.getRole());
        return mapToResponse(userRepository.save(user));
    }

    /**
     * DELETE user
     */
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }

    /**
     * Map User entity → UserResponse DTO
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

