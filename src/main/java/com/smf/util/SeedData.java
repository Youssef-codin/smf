package com.smf.util;

import com.smf.dto.auth.RegisterRequest;
import com.smf.model.Device;
import com.smf.model.Role;
import com.smf.model.User;
import com.smf.model.Zone;
import com.smf.repo.DeviceRepository;
import com.smf.repo.RoleRepository;
import com.smf.repo.UserRepository;
import com.smf.repo.ZoneRepository;
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
    private final ZoneRepository zoneRepository;

    @Value("${app.is-dev-mode:false}")
    private boolean isDevMode;

    public SeedData(IAuthService authService, RoleRepository roleRepository, UserRepository userRepository, DeviceRepository deviceRepository, ZoneRepository zoneRepository) {
        this.authService = authService;
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.deviceRepository = deviceRepository;
        this.zoneRepository = zoneRepository;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // Seed roles
        Role adminRole = seedRole("ADMIN", true);
        Role engineerRole = seedRole("ENGINEER", false);
        Role managerRole = seedRole("MANAGER", false);
        Role workerRole = seedRole("WORKER", false);

        System.out.println("Roles seeded: ADMIN, ENGINEER, MANAGER, WORKER");

        // Seed admin user
        User adminUser = seedAdminUser(adminRole);

        // Seed test users with different roles
        User engineerUser = seedTestUser("engineer", "engineer@test.com", "password123", new HashSet<>(Set.of(engineerRole)));
        User managerUser = seedTestUser("manager", "manager@test.com", "password123", new HashSet<>(Set.of(managerRole)));
        User workerUser = seedTestUser("worker", "worker@test.com", "password123", new HashSet<>(Set.of(workerRole)));

        // Seed zones with role restrictions
        Zone zoneA = seedZone("Zone A - Engineering Only", new HashSet<>(Set.of(engineerRole)));
        Zone zoneB = seedZone("Zone B - Engineering & Manager", new HashSet<>(Set.of(engineerRole, managerRole)));
        Zone zoneC = seedZone("Zone C - Open Access", new HashSet<>());

        System.out.println("Zones seeded: Zone A, Zone B, Zone C");

        // Seed devices in dev mode
        if (isDevMode) {
            seedTestDevices(adminUser, engineerUser, managerUser, workerUser);
        }
    }

    private Role seedRole(String roleName, boolean isAdmin) {
        return roleRepository.findByRoleName(roleName)
                .orElseGet(() -> {
                    Role role = new Role();
                    role.setRoleName(roleName);
                    role.setAdmin(isAdmin);
                    return roleRepository.save(role);
                });
    }

    private User seedAdminUser(Role adminRole) {
        if (!userRepository.existsByEmail("admin@smf.com")) {
            System.out.println("Seeding admin user...");
            RegisterRequest request = new RegisterRequest("admin@smf.com", "admin", "admin");
            User user = authService.register(request);
            Set<Role> roles = new HashSet<>(Set.of(adminRole));
            user.setRoles(roles);
            System.out.println("Admin user seeded: admin@smf.com / admin");
            return userRepository.save(user);
        }
        return userRepository.findByEmail("admin@smf.com").orElse(null);
    }

    private User seedTestUser(String username, String email, String password, Set<Role> roles) {
        if (!userRepository.existsByEmail(email)) {
            System.out.println("Seeding " + username + " user...");
            RegisterRequest request = new RegisterRequest(email, username, password);
            User user = authService.register(request);
            user.setRoles(roles);
            System.out.println(username + " user seeded: " + email + " / " + password);
            return userRepository.save(user);
        }
        return userRepository.findByEmail(email).orElse(null);
    }

    private Zone seedZone(String name, Set<Role> allowedRoles) {
        return zoneRepository.findByName(name)
                .orElseGet(() -> {
                    Zone zone = new Zone();
                    zone.setName(name);
                    zone.setAllowedRoles(allowedRoles);
                    return zoneRepository.save(zone);
                });
    }

    private void seedTestDevices(User adminUser, User engineerUser, User managerUser, User workerUser) {
        seedDevice("00:11:22:33:44:AA", adminUser, "Admin Device");
        seedDevice("00:11:22:33:44:BB", engineerUser, "Engineer Device");
        seedDevice("00:11:22:33:44:CC", managerUser, "Manager Device");
        seedDevice("00:11:22:33:44:DD", workerUser, "Worker Device");
    }

    private void seedDevice(String macAddress, User owner, String description) {
        if (deviceRepository.findByMacAddress(macAddress).isEmpty()) {
            System.out.println("Seeding device: " + description + " (" + macAddress + ")");
            Device device = new Device();
            device.setMacAddress(macAddress);
            device.setOwner(owner);
            device.setLastLocationLat(34.052235);
            device.setLastLocationLon(-118.243683);
            device.setLastSeenTimestamp(Timestamp.from(Instant.now()));
            deviceRepository.save(device);
        }
    }
}