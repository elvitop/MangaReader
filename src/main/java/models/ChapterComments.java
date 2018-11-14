package models;

import java.util.List;

public class ChapterComments {
    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    private String user_name;
    private String comment;
    private int user_id;
    private int manga_id;

    public int getChapter_id() {
        return chapter_id;
    }

    public void setChapter_id(int chapter_id) {
        this.chapter_id = chapter_id;
    }

    private int chapter_id;
    private String newComment;
    private List<CommentsManga> comments;


    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }


    public int getManga_id() {
        return manga_id;
    }

    public void setManga_id(int manga_id) {
        this.manga_id = manga_id;
    }

    public String getNewComment() {
        return newComment;
    }

    public void setNewComment(String newComment) {
        this.newComment = newComment;
    }

    public List<CommentsManga> getComments() {
        return comments;
    }

    public void setComments(List<CommentsManga> comments) {
        this.comments = comments;
    }


    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}

