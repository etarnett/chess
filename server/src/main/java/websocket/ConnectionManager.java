package websocket;

import org.eclipse.jetty.websocket.api.*;
import java.util.*;

public class ConnectionManager {

    private final Map<Integer, Map<String, Session>> connections = new HashMap<>();

    public void add(int gameID, String username, Session session) {
        connections
                .computeIfAbsent(gameID, k -> new HashMap<>())
                .put(username, session);
    }

    public void remove(int gameID, String username) {
        if (connections.containsKey(gameID)) {
            connections.get(gameID).remove(username);
        }
    }

    public void broadcast(int gameID, String message) throws Exception {
        if (!connections.containsKey(gameID)) return;

        for (Session session : connections.get(gameID).values()) {
            session.getRemote().sendString(message);
        }
    }

    public void broadcastExcept(int gameID, String excludeUser, String message) throws Exception {
        if (!connections.containsKey(gameID)) return;

        for (var entry : connections.get(gameID).entrySet()) {
            if (!entry.getKey().equals(excludeUser)) {
                entry.getValue().getRemote().sendString(message);
            }
        }
    }
}