package net.myplayplanet.services.auth;

import com.google.gson.Gson;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import net.myplayplanet.services.cache.Cache;
import net.myplayplanet.services.logger.Log;
import org.mindrot.jbcrypt.BCrypt;

import java.io.*;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

@Getter
@Slf4j
public class AuthenticationManager {

    @Getter
    private static AuthenticationManager instance;

    @Setter
    private String secretToken;

    private Cache<UUID, String> authCache;

    @Getter(AccessLevel.PRIVATE)
    private String apiURL = "http://localhost:8080/v1/authenticate";

    public AuthenticationManager() {
        instance = this;

        authCache = new Cache<>(
                "auth-cache",
                600L,
                name -> null);
    }

    void updateCredentials(UUID username, String password) {
        authCache.update(username, BCrypt.hashpw(password, BCrypt.gensalt(10)));
    }

    String requestToken(UUID username, String password) {
        URL url;
        try {
            url = new URL(this.apiURL);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json; utf-8");
            con.setRequestProperty("Accept", "application/json");
            con.setDoOutput(true);
            String jsonInputString = new Gson().toJson(new UserRequest(username.toString(), password));
            try (OutputStream os = con.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(con.getInputStream(), "utf-8"))) {
                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                System.out.println(response.toString());
                return response.toString();
            }
        } catch (ConnectException e) {
            Log.getLog(log).warning("Connecting to {url} failed! [{errormessage}]", this.getApiURL(), e.getLocalizedMessage());
        } catch (IOException e){
            e.printStackTrace();
        }

        return null;
    }

}

@Data
@NoArgsConstructor
class UserRequest implements Serializable {
    private static final long serialVersionUID = 5926468583005150707L;
    private String username;
    private String password;

    public UserRequest(String username, String password) {
        this.setUsername(username);
        this.setPassword(password);
    }
}