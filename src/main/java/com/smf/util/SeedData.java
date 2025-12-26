package com.smf.util;

import com.smf.dto.auth.RegisterRequest;
import com.smf.model.Role;
import com.smf.model.User;
import com.smf.repo.RoleRepository;
import com.smf.repo.UserRepository;
import com.smf.service.auth.IAuthService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Component
public class SeedData implements CommandLineRunner {

    private final IAuthService authService;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    public SeedData(IAuthService authService, RoleRepository roleRepository, UserRepository userRepository) {
        this.authService = authService;
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        Role userRole = roleRepository.findByRoleName("USER")
                .orElseGet(() -> roleRepository.save(new Role("USER")));

        Role adminRole = roleRepository.findByRoleName("ADMIN")
                .orElseGet(() -> roleRepository.save(new Role("ADMIN")));

        if (!userRepository.existsByEmail("admin@smf.com")) {
            System.out.println("Seeding admin user...");
            RegisterRequest request = new RegisterRequest("admin@smf.com", "admin", "admin");

            User user = authService.register(request);

            Set<Role> roles = new HashSet<>(user.getRoles());
            roles.add(adminRole);
            user.setRoles(roles);

            userRepository.save(user);
            System.out.println("Admin user seeded successfully: admin@smf.com / admin");
        }
    }
}
