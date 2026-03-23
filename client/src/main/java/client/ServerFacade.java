package client;

import java.io.*;
import java.net.*;
import com.google.gson.Gson;
import model.*;

//This class talks to the server and handles only HTTP
//1. Build URL
//2. Open Connection
//3. Set method
//4. Add headers
//5. Send JSON
//6. Get response
//7. Convert JSON -> java object
//8. Return it

public class ServerFacade {
    private final String serverUrl;
    private final Gson gson = new Gson();

    public ServerFacade(int port) {
        this.serverUrl = "http://localhost:" + port;
    }

    public AuthData register(String username, String password, String email) throws Exception {
        var request = new RegisterRequest(username, password, email);
        return makeRequest("POST", "/user", request, AuthData.class, null);
    }

    public void clear() throws Exception {
        makeRequest("DELETE", "/db", null, null, null);
    }

    private <T> T makeRequest(String method, String path, Object body, Class<T> responseClass, String authToken) throws Exception {
        URL url = new URL(serverUrl + path);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod(method);
        conn.setDoOutput(true);

        if (authToken != null) {
            conn.setRequestProperty("Authorization", authToken);
        }

        if (body != null) {
            conn.setRequestProperty("Content-Type", "application/json");
            try (OutputStream os = conn.getOutputStream()) {
                String json = gson.toJson(body);
                os.write(json.getBytes());
            }
        }

        int status = conn.getResponseCode();

        InputStream responseStream = (status >= 200 && status < 300)
                ? conn.getInputStream()
                : conn.getErrorStream();

        InputStreamReader reader = new InputStreamReader(responseStream);

        if (responseClass != null) {
            return gson.fromJson(reader, responseClass);
        } else {
            return null;
        }
    }
}
