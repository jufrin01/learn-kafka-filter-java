package com.example.authservice.repository;

import com.example.authservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    @Query(value = "select * from users where username =?1 and is_deleted = false ", nativeQuery = true)
    User findByUsername(String username);

    @Query(value = "select * from users where username = :username and is_deleted = false", nativeQuery = true)
    Optional<User> findByUsernameFalseAndIsDeletedFalse(@Param("username") String username);
}
