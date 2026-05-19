package crm.chifco.com.dto;

public class AuthenticationResponse {

    private String token;
    private String refreshToken;
    private Object user;

    public AuthenticationResponse() {
    }

    public AuthenticationResponse(String token, String refreshToken, Object user) {
        this.token = token;
        this.refreshToken = refreshToken;
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public Object getUser() {
        return user;
    }

    public void setUser(Object user) {
        this.user = user;
    }
}
