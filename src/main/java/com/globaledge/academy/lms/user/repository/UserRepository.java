package com.globaledge.academy.lms.user.repository;

import com.globaledge.academy.lms.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Optional<User> findById(Long id);

    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

//    Optional<User> findByEmployeeId(Long employeeId);
//
//    boolean existsByUsername(String username);
//
//    boolean existsByEmail(String email);
//
//    boolean existsByEmployeeId(Long employeeId);
}