package user_service.dto;
import lombok.*;
import user_service.entity.User.Role;

import java.time.LocalDateTime;

/**
 * UserResponse DTO (Data Transfer Object)
 * This is what our API SENDS BACK to the client (outgoing data)
 *
 *  Notice: password is NOT included here - never send password back!
 *
 * Example JSON sent to client:
 * {
 *   "id": 1,
 *   "firstName": "John",
 *   "lastName": "Doe",
 *   "email": "john@gmail.com",
 *   "phone": "9876543210",
 *   "role": "CUSTOMER",
 *   "createdAt": "2024-01-15T10:30:00"
 * }
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {

    private Long id;

    private String firstName;

    private String lastName;

    private String email;

    private String phone;

    private Role role;    // CUSTOMER/ ADMIN / HOTEL_MANAGER

    private LocalDateTime createdAt;   //when user registered
}
