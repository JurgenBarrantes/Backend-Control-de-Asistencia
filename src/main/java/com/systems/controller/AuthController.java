package com.systems.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.systems.dto.LoginRequest;
import com.systems.dto.LoginResponse;
import com.systems.dto.PersonDTO;
import com.systems.dto.RefreshRequest;
import com.systems.dto.RegisterRequest;
import com.systems.dto.RoleDTO;
import com.systems.dto.TokenResponse;
import com.systems.dto.UserDTO;
import com.systems.model.Person;
import com.systems.model.Role;
import com.systems.model.User;
import com.systems.service.IPersonService;
import com.systems.service.IRoleService;
import com.systems.service.IUserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final IUserService userService;
    private final IPersonService personService;
    private final IRoleService roleService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            // Buscar usuario por username o email
            Optional<User> userOpt = userService.findByUsernameOrPersonEmail(loginRequest.getUsername());

            if (userOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Usuario no encontrado");
            }

            User user = userOpt.get();

            // Por ahora verificación simple de password (sin encriptación)
            if (!user.getPassword().equals(loginRequest.getPassword())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Credenciales inválidas");
            }

            // Generar tokens simples (sin JWT por ahora)
            String accessToken = "fake-access-token-" + user.getUsername();
            String refreshToken = "fake-refresh-token-" + user.getUsername();

            // Convertir User a UserDTO
            UserDTO userDTO = convertUserToDto(user);

            LoginResponse response = new LoginResponse(accessToken, refreshToken, userDTO);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error interno del servidor: " + e.getMessage());
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest) {
        try {
            // 1. Validar que username y email no existan
            if (userService.existsByUsername(registerRequest.getUsername())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("El nombre de usuario ya existe");
            }

            if (userService.existsByPersonEmail(registerRequest.getEmail())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("El email ya está registrado");
            }

            // 2. Buscar rol ANTES de crear cualquier entidad
            String roleName = registerRequest.getRoleName() != null ? registerRequest.getRoleName() : "USER";
            Optional<Role> roleOpt = roleService.findByName(roleName);

            if (roleOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Rol no encontrado: " + roleName);
            }

            // 3. Crear User primero (sin la persona)
            User user = new User();
            user.setUsername(registerRequest.getUsername());
            user.setPassword(registerRequest.getPassword()); // Sin encriptar por ahora
            user.setEnabled(true);

            List<Role> roles = new ArrayList<>();
            roles.add(roleOpt.get());
            user.setRoles(roles);
            User savedUser = userService.save(user); // Guardar User para obtener ID

            // 4. Crear y guardar Person, estableciendo la relación
            Person person = new Person();
            person.setFirstName(registerRequest.getFirstName());
            person.setLastName(registerRequest.getLastName());
            person.setDni(registerRequest.getDni());
            person.setEmail(registerRequest.getEmail());
            person.setPhone(registerRequest.getPhone());
            person.setGender(registerRequest.getGender());
            person.setAddress(registerRequest.getAddress());

            if (registerRequest.getBirthdate() != null) {
                person.setBirthdate(LocalDate.parse(registerRequest.getBirthdate()));
            }
            person.setUser(savedUser); // Asignar el User ya persistido
            Person savedPerson = personService.save(person); // Guardar Person

            // Para la respuesta, necesitamos la relación en el objeto User
            savedUser.setPerson(savedPerson);

            // 5. Generar tokens y responder
            String accessToken = "fake-access-token-" + savedUser.getUsername();
            String refreshToken = "fake-refresh-token-" + savedUser.getUsername();

            UserDTO userDTO = convertUserToDto(savedUser);

            LoginResponse response = new LoginResponse(accessToken, refreshToken, userDTO);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al registrar usuario: " + e.getMessage());
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshRequest refreshRequest) {
        try {
            String refreshToken = refreshRequest.getRefreshToken();

            // Validación simple del refresh token
            if (!refreshToken.startsWith("fake-refresh-token-")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Refresh token inválido");
            }

            // Extraer username del token
            String username = refreshToken.replace("fake-refresh-token-", "");

            // Verificar que el usuario aún existe
            Optional<User> userOpt = userService.findByUsername(username);
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Usuario no encontrado");
            }

            // Generar nuevos tokens
            String newAccessToken = "fake-access-token-" + username;
            String newRefreshToken = "fake-refresh-token-" + username;

            TokenResponse response = new TokenResponse(newAccessToken, newRefreshToken);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al refrescar token: " + e.getMessage());
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        // Por ahora solo retornamos éxito
        return ResponseEntity.ok("Logout exitoso");
    }

    // Método helper para convertir User a UserDTO
    private UserDTO convertUserToDto(User user) {
        UserDTO dto = new UserDTO();
        dto.setIdUser(user.getIdUser());
        dto.setUsername(user.getUsername());
        dto.setEnabled(user.getEnabled());

        // Convertir Person
        if (user.getPerson() != null) {
            PersonDTO personDTO = new PersonDTO();
            personDTO.setIdPerson(user.getPerson().getIdPerson());
            personDTO.setFirstName(user.getPerson().getFirstName());
            personDTO.setLastName(user.getPerson().getLastName());
            personDTO.setDni(user.getPerson().getDni());
            personDTO.setEmail(user.getPerson().getEmail());
            personDTO.setPhone(user.getPerson().getPhone());
            personDTO.setBirthdate(
                    user.getPerson().getBirthdate() != null ? user.getPerson().getBirthdate().toString() : null);
            personDTO.setGender(user.getPerson().getGender());
            personDTO.setAddress(user.getPerson().getAddress());
            dto.setPerson(personDTO);
        }

        // Convertir roles
        if (user.getRoles() != null && !user.getRoles().isEmpty()) {
            List<RoleDTO> roleDTOs = new ArrayList<>();
            for (Role role : user.getRoles()) {
                RoleDTO roleDTO = new RoleDTO();
                roleDTO.setIdRole(role.getIdRole());
                roleDTO.setName(role.getName());
                roleDTO.setDescription(role.getDescription());
                roleDTOs.add(roleDTO);
            }
            dto.setRoles(roleDTOs);
        }

        return dto;
    }
}
