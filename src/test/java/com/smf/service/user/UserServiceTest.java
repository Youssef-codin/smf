package com.smf.service.user;

import com.smf.dto.user.UserRequest;
import com.smf.dto.user.UserResponse;
import com.smf.model.Role;
import com.smf.model.User;
import com.smf.repo.UserRepository;
import com.smf.service.role.IRoleService;
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
    private IRoleService roleService;

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
    void createUser_success_withDefaultRole() {

        UserRequest request = new UserRequest();
        request.setUsername("testuser");
        request.setEmail("testuser@test.com");
        request.setPassword("123456");

        when(passwordEncoder.encode("123456")).thenReturn("encoded");
        when(roleService.findRoleByName("ROLE_USER")).thenReturn(testRole);
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        UserResponse response = userService.createUser(request);

        assertNotNull(response);
        assertEquals("testuser@test.com", response.getEmail());
    }

    @Test
    void getUserById_success() {

        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));

        UserResponse result = userService.getUserById(testUser.getId());

        assertNotNull(result);
        assertEquals(testUser.getEmail(), result.getEmail());
    }

    @Test
    void getUserById_notFound() {

        UUID id = UUID.randomUUID();
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        AppError exception = assertThrows(AppError.class, () -> userService.getUserById(id));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Test
    void getAllUsers_success() {

        when(userRepository.findAll()).thenReturn(List.of(testUser));

        List<UserResponse> users = userService.getAllUsers();

        assertEquals(1, users.size());
    }

    @Test
    void updateUser_success() {

        UserRequest request = new UserRequest();
        request.setUsername("updated");
        request.setEmail("updated@test.com");
        request.setPassword("123");

        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));
        when(passwordEncoder.encode("123")).thenReturn("encoded");
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        UserResponse response = userService.updateUser(testUser.getId(), request);

        assertEquals("updated@test.com", response.getEmail());
    }

    @Test
    void updateUser_notFound() {

        UUID id = UUID.randomUUID();
        UserRequest request = new UserRequest();

        when(userRepository.findById(id)).thenReturn(Optional.empty());

        AppError exception = assertThrows(AppError.class,
                () -> userService.updateUser(id, request));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Test
    void deleteUser_success() {

        UUID id = UUID.randomUUID();

        when(userRepository.existsById(id)).thenReturn(true);

        assertDoesNotThrow(() -> userService.deleteUser(id));

        verify(userRepository).deleteById(id);
    }

    @Test
    void deleteUser_notFound() {

        UUID id = UUID.randomUUID();

        when(userRepository.existsById(id)).thenReturn(false);

        AppError exception = assertThrows(AppError.class,
                () -> userService.deleteUser(id));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }
}