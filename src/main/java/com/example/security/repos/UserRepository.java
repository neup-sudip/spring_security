package com.example.security.repos;

import com.example.security.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByUsername(String username);

    @Query("select customer from Customer customer where customer.id <> :id AND username = :username")
    Customer findByNotIdAndUsername(long id, String username);

    Customer findByUsernameAndPassword(String username, String password);

}
