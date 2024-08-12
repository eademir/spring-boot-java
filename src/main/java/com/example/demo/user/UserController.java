package com.example.demo.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequestMapping("api/v1/users")
@RestController
public class UserController {
    private final UserService userService;

    // Constructor to initialize UserService.
    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Endpoint to retrieve all users.
    @Operation(summary = "Get all users")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users found"),
            @ApiResponse(responseCode = "404", description = "No users found")
    })
    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    // Endpoint to retrieve a user by their ID.
    @Operation(summary = "Get a user by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User found"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable UUID id) {
        User user = userService.getUserById(id);
        return user != null ? ResponseEntity.ok(user) : ResponseEntity.notFound().build();
    }

    // Endpoint to delete a user by their ID.
    @Operation(summary = "Delete a user by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User deleted"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable UUID id) {
        return userService.deleteUser(id) ? ResponseEntity.ok("User deleted") : ResponseEntity.notFound().build();
    }

    // Endpoint to update a user by their ID. Only accessible by ADMIN or the user themselves.
    @Operation(summary = "Update a user by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User updated"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PreAuthorize("hasAuthority('ADMIN') || #id == principal.id")
    @PutMapping("/{id}")
    public ResponseEntity<String> updateUser(@PathVariable("id") UUID id, @RequestBody User updatedUser) {
        return userService.updateUser(id, updatedUser) ? ResponseEntity.ok("User updated") : ResponseEntity.notFound().build();
    }
}
