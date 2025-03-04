package com.example.restapi.service.implementation;

import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class TokenBlackListService {
    private Set<String> blacklist = new HashSet<>();

    public void addTokenToBlacklist(String token) {
        blacklist.add(token);
    }

    public boolean isTokenBlacklisted(String token) {
        return blacklist.contains(token);
    }
}
