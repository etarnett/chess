package websocket;

import org.eclipse.jetty.websocket.api.Session;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {

    // gameID -> (username -> session)
    private final ConcurrentHashMap<Integer, ConcurrentHashMap<String, Session>> connections = new ConcurrentHashMap<>();

    public void add(int gameID, String username, Session session) {
        connections
                .computeIfAbsent(gameID, k -> new ConcurrentHashMap<>())
                .put(username, session);
    }

    public void remove(int gameID, String username) {
        if (connections.containsKey(gameID)) {
            connections.get(gameID).remove(username);
        }
    }

    public void broadcast(int gameID, String excludeUser, String message) throws IOException {
        if (!connections.containsKey(gameID)) return;

        for (var entry : connections.get(gameID).entrySet()) {
            Session s = entry.getValue();
            if (s.isOpen() && !entry.getKey().equals(excludeUser)) {
                s.getRemote().sendString(message);
            }
        }
    }

    public void broadcastAll(int gameID, String message) throws IOException {
        if (!connections.containsKey(gameID)) return;

        for (Session s : connections.get(gameID).values()) {
            if (s.isOpen()) {
                s.getRemote().sendString(message);
            }
        }
    }
}