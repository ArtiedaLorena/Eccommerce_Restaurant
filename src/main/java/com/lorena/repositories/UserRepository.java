package com.lorena.repositories;
import com.lorena.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,Long> {
    public User findByEmail(String username);

}
