package com.example.security.repos;

import com.example.security.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Component
public interface RoleRepository extends JpaRepository<Role, Long>{

    Optional<Role> findByName(String name);

    @Query(value = "SELECT * FROM roles WHERE role_id <> :id AND name = :name LIMIT 1", nativeQuery = true)
    Role findByNotIdAndName(long id, String name);
}
