package com.smf.util;

import com.smf.dto.auth.RegisterRequest;
import com.smf.model.Device;
import com.smf.model.Role;
import com.smf.model.User;
import com.smf.repo.DeviceRepository;
import com.smf.repo.RoleRepository;
import com.smf.repo.UserRepository;
import com.smf.service.auth.IAuthService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Component
public class SeedData implements CommandLineRunner {

    private final IAuthService authService;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final DeviceRepository deviceRepository;

    @Value("${app.is-dev-mode:false}")
    private boolean isDevMode;

    public SeedData(IAuthService authService, RoleRepository roleRepository, UserRepository userRepository, DeviceRepository deviceRepository) {
        this.authService = authService;
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.deviceRepository = deviceRepository;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        Role userRole = roleRepository.findByRoleName("USER")
                .orElseGet(() -> roleRepository.save(new Role("USER")));

        Role adminRole = roleRepository.findByRoleName("ADMIN")
                .orElseGet(() -> roleRepository.save(new Role("ADMIN")));

        User adminUser;
        if (!userRepository.existsByEmail("admin@smf.com")) {
            System.out.println("Seeding admin user...");
            RegisterRequest request = new RegisterRequest("admin@smf.com", "admin", "admin");

            User user = authService.register(request);

            Set<Role> roles = new HashSet<>(user.getRoles());
            roles.add(adminRole);
            user.setRoles(roles);

            adminUser = userRepository.save(user);
            System.out.println("Admin user seeded successfully: admin@smf.com / admin");
        } else {
            adminUser = userRepository.findByEmail("admin@smf.com").orElse(null);
        }

        if (isDevMode && adminUser != null) {
            String testDeviceMac = "00:11:22:33:44:55";
            if (deviceRepository.findByMacAddress(testDeviceMac).isEmpty()) {
                System.out.println("Seeding test device...");
                Device device = new Device(
                        testDeviceMac,
                        adminUser,
                        34.052235,
                        -118.243683,
                        Timestamp.from(Instant.now())
                );
                deviceRepository.save(device);
                System.out.println("Test device seeded successfully: " + testDeviceMac);
            }
        }
    }
}