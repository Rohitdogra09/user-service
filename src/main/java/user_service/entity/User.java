package user_service.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.management.relation.Role;
import java.time.LocalDateTime;

@Entity    // Tells JPA this is a database table
@Table(name="users")    //tabel name in mysql
@Data                  //Lombok: Generates getters, setters, toString
@NoArgsConstructor     //lombok: generates empty constructor
@AllArgsConstructor       //lombok: generates constructor with all fields
@Builder    // Lombok: enables builder pattern -> User.builder().name("Rohit").build()

public class User {

    @Id                  // Primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY) ///Auto increment(1,2,3,...)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String firstName;

    @NotBlank
    @Column(nullable = false)
    private String lastName;

    @NotBlank
    @Column(nullable = false)
    private String email;

    @NotBlank
    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String phone;

    @Enumerated(EnumType.STRING)  //saves roles as String in DB(eg, "ADMIN")
    private Role role;

    @Column(updatable = false)   // once set this column never updates
    private LocalDateTime createdAt;

    private LocalDateTime updateAt;

    /**
     * Roles available in the system
     * CUSTOMER  -> regular user who books hotels
     * ADMIN   -> System admin
     * HOTEL_MANAGER   -> maages hotel listing and rooms
     */

    public enum Role{
        CUSTOMER,
        ADMIN,
        HOTEL_MANAGER
    }

    /**
     * Automatically called by JPA BEFORE saving a new record
     * Sets both createdAt and updatedAt to current time
     */
    @PrePersist
    protected void onCreate(){
        createdAt= LocalDateTime.now();
        updateAt=LocalDateTime.now();
    }

    /**
     * Automatically called by JPA BEFORE updating an existing record
     * Only updates the updatedAt timestamp
     */
    @PreUpdate
    protected void onUpdate(){
        updateAt=LocalDateTime.now();
    }



}
