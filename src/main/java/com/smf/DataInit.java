package com.smf;

import com.smf.model.Role;
import com.smf.model.User;
import com.smf.repo.RoleRepository;
import com.smf.repo.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.UUID;

@Component
public class DataInit implements CommandLineRunner {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passEncoder;
    private final RoleRepository roleRepository;

    public DataInit(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder,
            RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.passEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (roleRepository.count() == 0) {
            roleRepository.save(new Role("ADMIN"));
            roleRepository.save(new Role("USER"));
        }

        if (userRepository.count() == 0) {
            Role adminRole = roleRepository.findByRoleName("ADMIN").get();
            User admin = new User(UUID.randomUUID(), "admin@smf.com", "admin", passEncoder.encode("admin"));
            admin.setRoles(Set.of(adminRole));
            userRepository.save(admin);

            Role userRole = roleRepository.findByRoleName("USER").get();
            User user = new User(UUID.randomUUID(), "user@smf.com", "user", passEncoder.encode("user"));
            user.setRoles(Set.of(userRole));
            userRepository.save(user);
        }
    }
}
