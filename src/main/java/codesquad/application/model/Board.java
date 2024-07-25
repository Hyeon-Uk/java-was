package codesquad.application.model;

import java.util.Objects;

public class Board {
    private Long boardId;
    private String title;
    private String content;
    private String writer;
    private String imagePath;

    public Board(String title, String content, String writer) {
        this.title = title;
        this.content = content;
        this.writer = writer;
    }

    public Board(Long boardId, String title, String content, String writer) {
        this.boardId = boardId;
        this.title = title;
        this.content = content;
        this.writer = writer;
    }
    public Board(Long boardId, String title, String content, String writer,String imagePath) {
        this.boardId = boardId;
        this.title = title;
        this.content = content;
        this.writer = writer;
        this.imagePath = imagePath;
    }

    public Board(String title, String content, String writer, String imagePath) {
        this.title = title;
        this.content = content;
        this.writer = writer;
        this.imagePath = imagePath;
    }

    //TODO : 추후 setter를 없애야함. why? boardId를 함부로 수정할 수 있으면 저장된 데이터들의 정합성이 크게 깨질 수 있기때문에
    public void setBoardId(Long boardId) {
        this.boardId = boardId;
    }

    public Long getBoardId() {
        return this.boardId;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getWriter() {
        return writer;
    }

    public String getImagePath() {
        return imagePath;
    }
}
