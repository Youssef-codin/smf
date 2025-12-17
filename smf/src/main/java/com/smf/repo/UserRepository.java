package com.smf.repo;

import java.util.Optional;
import java.util.UUID;
import com.smf.model.User;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, UUID> {
	Optional<User> findByEmail(String string);

	boolean existsByEmail(String string);
}
