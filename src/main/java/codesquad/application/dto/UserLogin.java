package codesquad.application.dto;

public class UserLogin {
    private String userId;
    private String password;
    public UserLogin(){
    }
    public UserLogin(String userId, String password) {
        setUserId(userId);
        setPassword(password);
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
