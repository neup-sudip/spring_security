package com.example.security.role;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long>{

    Optional<Role> findByName(String name);

//    List<Role> findByName(String name);

    @Query(value = "SELECT * FROM roles WHERE name = :name AND authority_id = :authorityId LIMIT 1", nativeQuery = true)
    Role findByNameAndAuthority(String name, long authorityId);

    @Query(value = "SELECT * FROM roles WHERE role_id <> :id AND name = :name LIMIT 1", nativeQuery = true)
    Role findByNotIdAndName(long id, String name);
}
