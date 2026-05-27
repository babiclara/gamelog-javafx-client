package hr.algebra.gamelogclient.util;


public final class Session {

    private static String accessToken;
    private static String refreshToken;
    private static String username;

    private Session() {}

    public static void set(String access, String refresh, String user) {
        accessToken = access;
        refreshToken = refresh;
        username = user;
    }

    public static void clear() {
        accessToken = null;
        refreshToken = null;
        username = null;
    }

    public static String getAccessToken() { return accessToken; }
    public static String getRefreshToken() { return refreshToken; }
    public static String getUsername() { return username; }

    public static boolean isLoggedIn() { return accessToken != null; }
}