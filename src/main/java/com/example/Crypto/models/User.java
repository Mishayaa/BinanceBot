package com.example.Crypto.models;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "users")
@Data
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long chatId;

    @Column(nullable = false)
    private String name;

    private String apiKey;
    private String secretKey;

    private boolean isWaitingForInput;
    private Command previousCommand;
    private boolean registered;

    public User() {
        registered = false;
        isWaitingForInput = false;
        previousCommand = null;
    }


}