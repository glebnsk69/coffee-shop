
package com.example.coffeeshop.service;

import com.example.coffeeshop.entity.AppUser;
import com.example.coffeeshop.entity.Role;
import com.example.coffeeshop.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public AppUser createUser(String username, String password, Role role) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Имя пользователя не может быть пустым");
        }

        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("Пароль не может быть пустым");
        }

        if (role == null) {
            throw new IllegalArgumentException("Роль не выбрана");
        }

        String normalizedUsername = username.trim();

        if (userRepository.findByUsername(normalizedUsername).isPresent()) {
            throw new IllegalArgumentException("Такой пользователь уже существует");
        }

        String passwordHash = passwordEncoder.encode(password);

        AppUser user = new AppUser(normalizedUsername, passwordHash, role);

        return userRepository.save(user);
    }
}
