package websocket;

import java.util.*;

public class ConnectionManager {
    private final Map<Integer, Set<String>> gameConnections = new HashMap<>();

    public void add(int gameID, String connection) {
        gameConnections.computeIfAbsent(gameID, k -> new HashSet<>()).add(connection);
    }

    public void remove(int gameID, String username) {
        if (gameConnections.containsKey(gameID)) {
            gameConnections.get(gameID).remove(username);
        }
    }

    public Set<String> getConnections(int gameID) {
        return gameConnections.getOrDefault(gameID, new HashSet<>());
    }
}
