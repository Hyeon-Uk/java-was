package codesquad.application.model;

import java.util.Objects;

public class Board {
    private Long boardId;
    private String title;
    private String content;
    private String writer;

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

    @Override
    public String toString() {
        return "Board{" +
                "title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", writer=" + writer +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Board board = (Board) o;
        return Objects.equals(title, board.title) && Objects.equals(content, board.content) && Objects.equals(writer, board.writer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, content, writer);
    }
}
