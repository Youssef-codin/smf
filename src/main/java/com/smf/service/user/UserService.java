package com.smf.service.user;

import com.smf.dto.user.UserRequest;
import com.smf.dto.user.UserResponse;
import com.smf.model.Role;
import com.smf.model.User;
import com.smf.repo.UserRepository;
import com.smf.service.role.IRoleService;
import com.smf.util.AppError;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {

  private final UserRepository userRepository;
  private final IRoleService roleService;
  private final PasswordEncoder passwordEncoder;

  @Override
  @Transactional
  public UserResponse createUser(UserRequest request) {
    User user =
        new User(
            request.getEmail(),
            request.getUsername(),
            passwordEncoder.encode(request.getPassword()));

    Set<Role> roles = new HashSet<>();
    if (request.getRoles() == null || request.getRoles().isEmpty()) {
      Role userRole = roleService.findRoleByName("ROLE_USER");
      roles.add(userRole);
    } else {
      for (String roleName : request.getRoles()) {
        Role role = roleService.findRoleByName(roleName);
        roles.add(role);
      }
    }
    user.setRoles(roles);

    user = userRepository.save(user);
    return mapToResponse(user);
  }

  @Override
  public UserResponse getUserById(UUID userId) {
    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new AppError(HttpStatus.NOT_FOUND, "User not found"));
    return mapToResponse(user);
  }

  @Override
  public List<UserResponse> getAllUsers() {
    return userRepository.findAll().stream().map(this::mapToResponse).toList();
  }

  @Override
  @Transactional
  public UserResponse updateUser(UUID userId, UserRequest request) {
    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new AppError(HttpStatus.NOT_FOUND, "User not found"));

    user.setUsername(request.getUsername());
    user.setEmail(request.getEmail());
    user.setPassword(passwordEncoder.encode(request.getPassword()));

    user = userRepository.save(user);
    return mapToResponse(user);
  }

  @Override
  @Transactional
  public void deleteUser(UUID userId) {
    if (!userRepository.existsById(userId)) {
      throw new AppError(HttpStatus.NOT_FOUND, "User not found");
    }
    userRepository.deleteById(userId);
  }

  @Override
  public User findUserById(UUID userId) {
    return userRepository
        .findById(userId)
        .orElseThrow(() -> new AppError(HttpStatus.NOT_FOUND, "User not found"));
  }

  private UserResponse mapToResponse(User user) {
    return new UserResponse(user.getId(), user.getUsername(), user.getEmail());
  }
}