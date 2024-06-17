package com.example.security.repos;

import com.example.security.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    @Query("select role from Role role where role.roleId <> :id AND name = :name")
    Optional<Role> findByNotIdAndName(long id, String name);
}
