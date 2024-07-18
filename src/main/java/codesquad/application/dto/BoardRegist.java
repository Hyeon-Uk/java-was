package codesquad.application.dto;

import codesquad.was.http.message.vo.HttpFile;

public class BoardRegist {
    private String title;
    private String content;
    private String csrfToken;
    private HttpFile file;
    public BoardRegist() {}
    public BoardRegist(String title, String content, String csrfToken) {
        this();
        this.title = title;
        this.content = content;
        this.csrfToken = csrfToken;
    }
    public BoardRegist(String title, String content, String csrfToken, HttpFile file) {
        this();
        this.title = title;
        this.content = content;
        this.csrfToken = csrfToken;
        this.file = file;
    }

    public String getTitle() {
        return title;
    }


    public String getContent() {
        return content;
    }


    public String getCsrfToken() {
        return csrfToken;
    }


    public HttpFile getFile(){
        return file;
    }
}
