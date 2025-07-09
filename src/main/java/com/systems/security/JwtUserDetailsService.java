package com.systems.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.systems.model.User;
import com.systems.repo.IUserRepo;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

//Clase S4
@Service
@RequiredArgsConstructor
public class JwtUserDetailsService implements UserDetailsService {
    private final IUserRepo repo;

    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        // Buscar por username o email
        Optional<User> userOpt = repo.findByUsernameOrPersonEmail(usernameOrEmail);

        if (userOpt.isEmpty()) {
            throw new UsernameNotFoundException("Usuario no encontrado con username o email: " + usernameOrEmail);
        }

        User user = userOpt.get();

        List<GrantedAuthority> roles = new ArrayList<>();
        user.getRoles().forEach(rol -> {
            roles.add(new SimpleGrantedAuthority(rol.getName()));
        });

        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), roles);
    }
}
