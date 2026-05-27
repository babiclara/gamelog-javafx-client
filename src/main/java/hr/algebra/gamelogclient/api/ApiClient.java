package hr.algebra.gamelogclient.api;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import hr.algebra.gamelogclient.model.Game;
import hr.algebra.gamelogclient.model.LoginRequest;
import hr.algebra.gamelogclient.model.TokenResponse;
import hr.algebra.gamelogclient.util.Session;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.Map;


public final class ApiClient {

    private static final String BASE_URL = "http://localhost:8080";

    private static final HttpClient HTTP = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    private static final ObjectMapper JSON = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    private ApiClient() {}

    public static void login(String username, String password) throws ApiException {
        LoginRequest body = new LoginRequest(username, password);
        TokenResponse tokens = postJson("/api/auth/login", body, TokenResponse.class, false);
        Session.set(tokens.getAccessToken(), tokens.getRefreshToken(), username);
    }

    public static void logout() {
        Session.clear();
    }

    public static List<Game> getAllGames() throws ApiException {
        return getList("/api/games", Game.class);
    }

    public static Game getGameById(Long id) throws ApiException {
        return get("/api/games/" + id, Game.class);
    }

    public static Game createGame(Game game) throws ApiException {
        return postJson("/api/games", game, Game.class, true);
    }

    public static Game updateGame(Long id, Game game) throws ApiException {
        return putJson("/api/games/" + id, game, Game.class);
    }

    public static void deleteGame(Long id) throws ApiException {
        delete("/api/games/" + id);
    }

    public static String createBackup() throws ApiException {
        Map<String, String> response = postEmpty("/api/admin/backup");
        return response.get("filename");
    }

    public static String restoreBackup(String filename) throws ApiException {
        Map<String, String> response = postEmpty("/api/admin/restore/" + filename);
        return response.get("message");
    }

    private static <T> T get(String path, Class<T> type) throws ApiException {
        HttpRequest req = baseRequest(path, true).GET().build();
        return send(req, type);
    }

    private static <T> List<T> getList(String path, Class<T> elementType) throws ApiException {
        HttpRequest req = baseRequest(path, true).GET().build();
        try {
            HttpResponse<String> resp = HTTP.send(req, HttpResponse.BodyHandlers.ofString());
            checkStatus(resp);
            return JSON.readValue(
                    resp.body(),
                    JSON.getTypeFactory().constructCollectionType(List.class, elementType)
            );
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException("Network error: " + e.getMessage(), e);
        }
    }

    private static <T> T postJson(String path, Object body, Class<T> type, boolean authRequired)
            throws ApiException {
        try {
            String json = JSON.writeValueAsString(body);
            HttpRequest req = baseRequest(path, authRequired)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();
            return send(req, type);
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException("Network error: " + e.getMessage(), e);
        }
    }

    @SuppressWarnings("unchecked")
    private static Map<String, String> postEmpty(String path) throws ApiException {
        HttpRequest req = baseRequest(path, true)
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();
        return send(req, Map.class);
    }

    private static <T> T putJson(String path, Object body, Class<T> type) throws ApiException {
        try {
            String json = JSON.writeValueAsString(body);
            HttpRequest req = baseRequest(path, true)
                    .header("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.ofString(json))
                    .build();
            return send(req, type);
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException("Network error: " + e.getMessage(), e);
        }
    }

    private static void delete(String path) throws ApiException {
        HttpRequest req = baseRequest(path, true).DELETE().build();
        try {
            HttpResponse<String> resp = HTTP.send(req, HttpResponse.BodyHandlers.ofString());
            checkStatus(resp);
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException("Network error: " + e.getMessage(), e);
        }
    }

    private static HttpRequest.Builder baseRequest(String path, boolean authRequired)
            throws ApiException {
        System.out.println("[ApiClient] " + path);
        HttpRequest.Builder b = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + path))
                .header("Accept", "application/json")
                .timeout(Duration.ofSeconds(30));

        if (authRequired) {
            String token = Session.getAccessToken();
            if (token == null) {
                throw new ApiException("Not logged in");
            }
            b.header("Authorization", "Bearer " + token);
        }
        return b;
    }

    private static <T> T send(HttpRequest req, Class<T> type) throws ApiException {
        try {
            HttpResponse<String> resp = HTTP.send(req, HttpResponse.BodyHandlers.ofString());
            checkStatus(resp);
            if (resp.body() == null || resp.body().isBlank()) {
                return null;
            }
            return JSON.readValue(resp.body(), type);
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException("Network error: " + e.getMessage(), e);
        }
    }

    private static void checkStatus(HttpResponse<String> resp) throws ApiException {
        int code = resp.statusCode();
        if (code < 200 || code >= 300) {
            String body = resp.body() == null ? "" : resp.body();
            throw new ApiException(
                    "HTTP " + code + ": " + (body.isBlank() ? "(empty response)" : body),
                    code
            );
        }
    }
}