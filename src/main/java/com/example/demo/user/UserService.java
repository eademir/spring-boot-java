package com.example.demo.user;

import com.example.demo.repository.UserAuthRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final UserAuthRepository userAuthRepository;

    // Constructor to initialize UserRepository and UserAuthRepository.
    public UserService(UserRepository userRepository, UserAuthRepository userAuthRepository) {
        this.userRepository = userRepository;
        this.userAuthRepository = userAuthRepository;
    }

    // Retrieve all users from the repository.
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Retrieve a user by their ID.
    public User getUserById(UUID id) {
        return userRepository.findById(id).orElse(null);
    }

    // Delete a user by their ID, return true if successful.
    public boolean deleteUser(UUID id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // Update a user by their ID with the provided updated user details.
    public boolean updateUser(UUID id, User updatedUser) {
        return userRepository.findById(id)
                .map(user -> {
                    updatedUser.setId(id); // Ensure the ID is not changed
                    userRepository.save(updatedUser);
                    return true;
                }).orElse(false);
    }

    // Load user details by their email address.
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userAuthRepository.findByEmail(email);
    }
}
