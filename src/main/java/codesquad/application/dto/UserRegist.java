package codesquad.application.dto;

public class UserRegist {
    private String userId;
    private String password;
    private String nickname;

    public UserRegist() {
    }

    public UserRegist(String userId, String password, String nickname) {
        this();
        setUserId(userId);
        setPassword(password);
        setNickname(nickname);
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

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
