package com.systems.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.systems.dto.AuthUserDTO;
import com.systems.dto.LoginRequest;
import com.systems.dto.LoginResponse;
import com.systems.dto.PersonDTO;
import com.systems.dto.RefreshRequest;
import com.systems.dto.RegisterRequest;
import com.systems.dto.TokenInfo;
import com.systems.dto.TokenResponse;
import com.systems.model.Person;
import com.systems.model.Role;
import com.systems.model.Student;
import com.systems.model.Teacher;
import com.systems.model.User;
import com.systems.repo.IStudentRepo;
import com.systems.repo.ITeacherRepo;
import com.systems.security.JwtTokenUtil;
import com.systems.security.JwtUserDetailsService;
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
    private final PasswordEncoder passwordEncoder;
    private final IStudentRepo studentRepo;
    private final ITeacherRepo teacherRepo;

    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;
    private final JwtUserDetailsService jwtUserDetailsService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            // Autenticación usando Spring Security
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()));

            // Cargar detalles del usuario
            UserDetails userDetails = jwtUserDetailsService.loadUserByUsername(loginRequest.getUsername());

            // Generar token JWT
            String accessToken = jwtTokenUtil.generateToken(userDetails);

            // Para el refresh token, generamos otro token con mayor duración
            String refreshToken = "refresh-" + jwtTokenUtil.generateToken(userDetails);

            // Definir tiempos de expiración basados en la configuración JWT
            LocalDateTime accessTokenExpiry = LocalDateTime.now().plusHours(5); // Según JWT_TOKEN_VALIDITY
            LocalDateTime refreshTokenExpiry = LocalDateTime.now().plusDays(7); // 7 días para refresh

            // Crear objeto TokenInfo
            TokenInfo tokenInfo = new TokenInfo(accessToken, refreshToken, accessTokenExpiry, refreshTokenExpiry);

            // Buscar el usuario en la base de datos para los detalles completos
            Optional<User> userOpt = userService.findByUsernameOrPersonEmail(loginRequest.getUsername());

            if (userOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Usuario no encontrado");
            }

            User user = userOpt.get();

            // Convertir User a AuthUserDTO
            AuthUserDTO userDTO = convertUserToDto(user);

            LoginResponse response = new LoginResponse(tokenInfo, userDTO);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Credenciales inválidas: " + e.getMessage());
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
            String roleName = registerRequest.getRoleName() != null ? registerRequest.getRoleName() : "STUDENT";
            Optional<Role> roleOpt = roleService.findByName(roleName);

            if (roleOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Rol no encontrado: " + roleName);
            }

            // 3. Crear User primero (sin la persona)
            User user = new User();
            user.setUsername(registerRequest.getUsername());
            user.setPassword(passwordEncoder.encode(registerRequest.getPassword())); // Encriptar password
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

            // -----------------------------------------------------------------
            // PASO ADICIONAL: Crear Student o Teacher según el rol
            // -----------------------------------------------------------------
            if ("STUDENT".equalsIgnoreCase(roleName)) {
                Student student = new Student();
                student.setPerson(savedPerson);
                studentRepo.save(student);
            } else if ("TEACHER".equalsIgnoreCase(roleName)) {
                Teacher teacher = new Teacher();
                teacher.setPerson(savedPerson);
                teacherRepo.save(teacher);
            }
            // Si es ADMIN, no se crea entidad adicional.
            // -----------------------------------------------------------------

            // Para la respuesta, necesitamos la relación en el objeto User
            savedUser.setPerson(savedPerson);

            // 5. Generar tokens JWT reales
            UserDetails userDetails = jwtUserDetailsService.loadUserByUsername(savedUser.getUsername());
            String accessToken = jwtTokenUtil.generateToken(userDetails);
            String refreshToken = "refresh-" + jwtTokenUtil.generateToken(userDetails);

            // Definir tiempos de expiración
            LocalDateTime accessTokenExpiry = LocalDateTime.now().plusHours(5); // Según JWT_TOKEN_VALIDITY
            LocalDateTime refreshTokenExpiry = LocalDateTime.now().plusDays(7); // 7 días

            // Crear objeto TokenInfo
            TokenInfo tokenInfo = new TokenInfo(accessToken, refreshToken, accessTokenExpiry, refreshTokenExpiry);

            AuthUserDTO userDTO = convertUserToDto(savedUser);

            LoginResponse response = new LoginResponse(tokenInfo, userDTO);

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

            // Validar que el refresh token sea válido
            if (refreshToken == null || !refreshToken.startsWith("refresh-")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Refresh token inválido");
            }

            // Extraer el token JWT real (sin el prefijo "refresh-")
            String actualToken = refreshToken.substring(8); // Remove "refresh-"

            // Extraer username del token JWT
            String username = jwtTokenUtil.getUsernameFromToken(actualToken);

            // Cargar detalles del usuario
            UserDetails userDetails = jwtUserDetailsService.loadUserByUsername(username);

            // Validar el token
            if (!jwtTokenUtil.validateToken(actualToken, userDetails)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Refresh token expirado o inválido");
            }

            // Generar nuevos tokens JWT
            String newAccessToken = jwtTokenUtil.generateToken(userDetails);
            String newRefreshToken = "refresh-" + jwtTokenUtil.generateToken(userDetails);

            // Definir tiempos de expiración para los nuevos tokens
            LocalDateTime accessTokenExpiry = LocalDateTime.now().plusHours(5); // Según JWT_TOKEN_VALIDITY
            LocalDateTime refreshTokenExpiry = LocalDateTime.now().plusDays(7); // 7 días

            // Crear objeto TokenInfo
            TokenInfo tokenInfo = new TokenInfo(newAccessToken, newRefreshToken, accessTokenExpiry, refreshTokenExpiry);

            TokenResponse response = new TokenResponse(tokenInfo);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Error al refrescar token: " + e.getMessage());
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        // Por ahora solo retornamos éxito
        return ResponseEntity.ok("Logout exitoso");
    }

    // Método helper para convertir User a AuthUserDTO
    private AuthUserDTO convertUserToDto(User user) {
        AuthUserDTO dto = new AuthUserDTO();
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

        // Convertir roles a lista de strings
        if (user.getRoles() != null && !user.getRoles().isEmpty()) {
            List<String> roleNames = new ArrayList<>();
            for (Role role : user.getRoles()) {
                roleNames.add(role.getName());
            }
            dto.setRoles(roleNames);
        }

        return dto;
    }
}
