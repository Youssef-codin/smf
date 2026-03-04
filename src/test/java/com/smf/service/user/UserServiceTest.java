package com.smf.service.user;

import com.smf.dto.user.UserResponse;
import com.smf.model.Role;
import com.smf.model.User;
import com.smf.repo.RoleRepository;
import com.smf.repo.UserRepository;
import com.smf.util.AppError;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private Role testRole;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        testRole = new Role();
        testRole.setId(1L);
        testRole.setRoleName("ROLE_USER");
        testRole.setAdmin(false);

        testUser = new User();
        testUser.setId(UUID.randomUUID());
        testUser.setUsername("testuser");
        testUser.setEmail("testuser@test.com");
        testUser.setRoles(new HashSet<>(Set.of(testRole)));
    }

    @Test
    void getUserById_success() {
        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));

        UserResponse result = userService.getUserById(testUser.getId());

        assertNotNull(result);
        assertEquals(testUser.getEmail(), result.getEmail());
        assertEquals(testUser.getUsername(), result.getFullName());
    }

    @Test
    void getUserById_notFound() {
        UUID id = UUID.randomUUID();
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        AppError exception = assertThrows(AppError.class, () -> userService.getUserById(id));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void getAllUsers_success() {
        when(userRepository.findAll()).thenReturn(List.of(testUser));

        List<UserResponse> users = userService.getAllUsers();  

        assertEquals(1, users.size());
        assertEquals("testuser@test.com", users.get(0).getEmail());
        assertEquals("testuser", users.get(0).getFullName());
    }
}