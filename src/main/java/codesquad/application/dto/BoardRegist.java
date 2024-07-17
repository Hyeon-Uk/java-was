package codesquad.application.dto;

public class BoardRegist {
    private String title;
    private String content;
    private String csrfToken;
    public BoardRegist() {}
    public BoardRegist(String title, String content, String csrfToken) {
        this.title = title;
        this.content = content;
        this.csrfToken = csrfToken;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCsrfToken() {
        return csrfToken;
    }

    public void setCsrfToken(String csrfToken) {
        this.csrfToken = csrfToken;
    }
}
