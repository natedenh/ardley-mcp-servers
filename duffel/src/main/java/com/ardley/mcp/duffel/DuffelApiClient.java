package com.ardley.mcp.duffel;

import com.ardley.mcp.core.HttpClientUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * HTTP client for the Duffel API. Handles auth, versioning headers, and common request patterns.
 */
public class DuffelApiClient {
    private static final Logger log = LoggerFactory.getLogger(DuffelApiClient.class);
    private static final String BASE_URL = "https://api.duffel.com";
    private static final String DUFFEL_VERSION = "v2";
    private static final MediaType JSON_TYPE = MediaType.parse("application/json; charset=utf-8");
    private static final ObjectMapper mapper = new ObjectMapper();

    private final String apiToken;

    public DuffelApiClient() {
        this.apiToken = loadApiToken();
    }

    private static String loadApiToken() {
        try {
            Path credPath = Paths.get(System.getProperty("user.home"), ".openclaw", "duffel-credentials.json");
            if (!Files.exists(credPath)) {
                throw new RuntimeException("Duffel credentials not found at " + credPath +
                        ". Create it with: {\"api_token\": \"duffel_test_...\"}");
            }
            JsonNode creds = mapper.readTree(Files.readString(credPath));
            String token = creds.path("api_token").asText("");
            if (token.isEmpty()) {
                throw new RuntimeException("api_token is empty in " + credPath);
            }
            return token;
        } catch (IOException e) {
            throw new RuntimeException("Failed to read Duffel credentials: " + e.getMessage(), e);
        }
    }

    private Request.Builder baseRequest(String path) {
        return new Request.Builder()
                .url(BASE_URL + path)
                .header("Authorization", "Bearer " + apiToken)
                .header("Duffel-Version", DUFFEL_VERSION)
                .header("Content-Type", "application/json")
                .header("Accept", "application/json");
    }

    public JsonNode get(String path) throws IOException {
        Request request = baseRequest(path).build();
        return execute(request);
    }

    public JsonNode post(String path, JsonNode body) throws IOException {
        Request request = baseRequest(path)
                .post(RequestBody.create(mapper.writeValueAsString(body), JSON_TYPE))
                .build();
        return execute(request);
    }

    public JsonNode postEmpty(String path) throws IOException {
        Request request = baseRequest(path)
                .post(RequestBody.create("{}", JSON_TYPE))
                .build();
        return execute(request);
    }

    private JsonNode execute(Request request) throws IOException {
        log.debug("Duffel API: {} {}", request.method(), request.url());
        try (Response response = HttpClientUtil.getClient().newCall(request).execute()) {
            String responseBody = response.body() != null ? response.body().string() : "";
            if (!response.isSuccessful()) {
                throw new IOException("Duffel API HTTP " + response.code() + ": " + responseBody);
            }
            if (responseBody.isEmpty()) {
                return mapper.createObjectNode().put("status", "success");
            }
            return mapper.readTree(responseBody);
        }
    }
}
