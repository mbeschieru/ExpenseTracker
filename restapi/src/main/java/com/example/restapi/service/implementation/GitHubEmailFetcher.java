package com.example.restapi.service.implementation;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
public record GitHubEmailFetcher(RestClient restClient) {
    private static final String EMAILS_URL = "https://api.github.com/user/emails";
    private static final String BEARER_PREFIX = "Bearer ";

    private static final String JSON_FORMAT = "application/vnd.github+json";

    public String fetchPrimaryEmail(String token) {
        List<GitHubEmail> gitHubEmailList = restClient
                .get()
                .uri(EMAILS_URL)
                .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + token)
                .header(HttpHeaders.ACCEPT,JSON_FORMAT)
                .retrieve()
                .body(new ParameterizedTypeReference<List<GitHubEmail>>() {
                });
        if (gitHubEmailList == null || gitHubEmailList.isEmpty())
            return null;
        return gitHubEmailList.stream()
                .filter(GitHubEmail::primary)
                .findFirst()
                .map(GitHubEmail::email)
                .orElse(null);
    }

    private record GitHubEmail(String email , Boolean primary){}
}
