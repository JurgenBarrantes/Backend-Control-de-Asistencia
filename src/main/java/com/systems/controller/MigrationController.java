package com.systems.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.systems.model.User;
import com.systems.service.IUserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/migration")
@RequiredArgsConstructor
public class MigrationController {

    private final IUserService userService;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/encrypt-passwords")
    public ResponseEntity<String> encryptExistingPasswords() {
        try {
            List<User> users = userService.findAll();
            int updatedUsers = 0;

            for (User user : users) {
                String currentPassword = user.getPassword();

                // Verificar si la password ya está encriptada (las de BCrypt empiezan con $2a$)
                if (!currentPassword.startsWith("$2a$") && !currentPassword.startsWith("$2b$")) {
                    // Encriptar la password actual
                    String encryptedPassword = passwordEncoder.encode(currentPassword);
                    user.setPassword(encryptedPassword);
                    userService.update(user, user.getIdUser());
                    updatedUsers++;
                    System.out.println("Password encriptada para usuario: " + user.getUsername());
                }
            }

            return ResponseEntity.ok("Se encriptaron las passwords de " + updatedUsers + " usuarios exitosamente");

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al encriptar passwords: " + e.getMessage());
        }
    }
}
