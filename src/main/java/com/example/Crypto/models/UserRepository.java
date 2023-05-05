package com.example.Crypto.models;

import com.example.Crypto.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    User findByChatId(Long chatId);
}
