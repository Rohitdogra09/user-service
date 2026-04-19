package user_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import user_service.entity.User.Role;


/**
 * UserRequest DTO (Data transfer object)
 * THis is what the client sends to our API (Incoming data)
 * We never expose the entity directly to outside world
 * Example JSON from client:
 *  * {
 *     "firstName": "John",
 *    "lastName": "Doe",
 *     "email": "john@gmail.com",
 *     "password": "secret123",
 *  "phone": "9876543210",
 *   "role": "CUSTOMER"
 */

@Data   // getter+ setter
@NoArgsConstructor  // empty constructor
@AllArgsConstructor
@Builder // build pattern

public class UserRequest {

    @NotBlank(message="First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @Email(message="Valid email is required")
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message="Password is required")
    private String password;

    private String phone;  //Optional field,

    private Role role;   // Optional: defaults to CUSTOMER if not provided
















}
