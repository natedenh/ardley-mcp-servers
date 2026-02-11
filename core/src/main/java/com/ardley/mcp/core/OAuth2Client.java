package com.ardley.mcp.core;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

/**
 * Shared OAuth2 utilities for token management.
 * Supports authorization code flow with refresh tokens.
 */
public class OAuth2Client {
    private static final Logger log = LoggerFactory.getLogger(OAuth2Client.class);
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final OkHttpClient httpClient = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build();

    private final String clientId;
    private final String clientSecret;
    private final String tokenUrl;
    private final String authUrl;
    private final String redirectUri;
    private final Path tokenStorePath;

    private String accessToken;
    private String refreshToken;
    private long expiresAt;

    public OAuth2Client(String clientId, String clientSecret, String authUrl,
                        String tokenUrl, String redirectUri, Path tokenStorePath) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.authUrl = authUrl;
        this.tokenUrl = tokenUrl;
        this.redirectUri = redirectUri;
        this.tokenStorePath = tokenStorePath;
        loadTokens();
    }

    /**
     * Get a valid access token, refreshing if necessary.
     */
    public synchronized String getAccessToken() throws IOException {
        if (accessToken != null && System.currentTimeMillis() < expiresAt - 60_000) {
            return accessToken;
        }
        if (refreshToken != null) {
            refreshAccessToken();
            return accessToken;
        }
        throw new IOException("No valid token available. Authorization required.");
    }

    /**
     * Exchange an authorization code for tokens.
     */
    public synchronized void exchangeCode(String code) throws IOException {
        RequestBody body = new FormBody.Builder()
                .add("grant_type", "authorization_code")
                .add("client_id", clientId)
                .add("client_secret", clientSecret)
                .add("redirect_uri", redirectUri)
                .add("code", code)
                .build();

        Request request = new Request.Builder().url(tokenUrl).post(body).build();
        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Token exchange failed: " + response.code());
            }
            parseTokenResponse(mapper.readTree(response.body().string()));
            saveTokens();
        }
    }

    private void refreshAccessToken() throws IOException {
        RequestBody body = new FormBody.Builder()
                .add("grant_type", "refresh_token")
                .add("client_id", clientId)
                .add("client_secret", clientSecret)
                .add("refresh_token", refreshToken)
                .build();

        Request request = new Request.Builder().url(tokenUrl).post(body).build();
        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Token refresh failed: " + response.code());
            }
            parseTokenResponse(mapper.readTree(response.body().string()));
            saveTokens();
        }
    }

    private void parseTokenResponse(JsonNode json) {
        accessToken = json.path("access_token").asText(null);
        if (json.has("refresh_token")) {
            refreshToken = json.path("refresh_token").asText(null);
        }
        int expiresIn = json.path("expires_in").asInt(3600);
        expiresAt = System.currentTimeMillis() + (expiresIn * 1000L);
    }

    private void loadTokens() {
        try {
            if (tokenStorePath != null && Files.exists(tokenStorePath)) {
                JsonNode stored = mapper.readTree(tokenStorePath.toFile());
                accessToken = stored.path("access_token").asText(null);
                refreshToken = stored.path("refresh_token").asText(null);
                expiresAt = stored.path("expires_at").asLong(0);
            }
        } catch (Exception e) {
            log.warn("Could not load stored tokens", e);
        }
    }

    private void saveTokens() {
        try {
            if (tokenStorePath != null) {
                ObjectNode node = mapper.createObjectNode();
                node.put("access_token", accessToken);
                node.put("refresh_token", refreshToken);
                node.put("expires_at", expiresAt);
                Files.createDirectories(tokenStorePath.getParent());
                mapper.writerWithDefaultPrettyPrinter().writeValue(tokenStorePath.toFile(), node);
            }
        } catch (Exception e) {
            log.warn("Could not save tokens", e);
        }
    }

    public String getAuthUrl() {
        return authUrl;
    }

    public String getClientId() {
        return clientId;
    }

    public String getRedirectUri() {
        return redirectUri;
    }
}
