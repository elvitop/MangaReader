package models;

import java.util.ArrayList;
import java.util.List;

public class Manga {
    private int user_id;
    private String user_name;
    private int manga_id;
    private String manga_name;
    private String manga_synopsis;
    private boolean manga_status;
    private String[] genre_id;
    private ArrayList<String> genres;
    private String location;
    private boolean like;
    private int likesManga;
    private String comment;
    private String newComment;
    private List<CommentsManga> comments;
    private List<MangaOuter> searchManga;
    private boolean logged;
    private boolean subscribe;

    private String switchState;

    public String getSwitchState() {
        return switchState;
    }

    public void setSwitchState(String switchState) {
        this.switchState = switchState;
    }

    private int comment_id;
    private boolean owner;

    public boolean isOwner() {
        return owner;
    }

    public void setOwner(boolean owner) {
        this.owner = owner;
    }

    public int getComment_id() {
        return comment_id;
    }

    public void setComment_id(int comment_id) {
        this.comment_id = comment_id;
    }


    public boolean isSubscribe() {
        return subscribe;
    }

    public void setSubscribe(boolean subscribe) {
        this.subscribe = subscribe;
    }

    public boolean isLogged() {
        return logged;
    }

    public void setLogged(boolean logged) {
        this.logged = logged;
    }

    public List<MangaOuter> getSearchManga() {
        return searchManga;
    }

    public void setSearchManga(List<MangaOuter> searchManga) {
        this.searchManga = searchManga;
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

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getLikesManga() {
        return likesManga;
    }

    public void setLikesManga(int likesManga) {
        this.likesManga = likesManga;
    }

    public boolean isLike() {
        return like;
    }

    public void setLike(boolean like) {
        this.like = like;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getManga_name() {
        return manga_name;
    }

    public void setManga_name(String manga_name) {
        this.manga_name = manga_name;
    }

    public String getManga_synopsis() {
        return manga_synopsis;
    }

    public void setManga_synopsis(String manga_synopsis) {
        this.manga_synopsis = manga_synopsis;
    }

    public boolean isManga_status() {
        return manga_status;
    }

    public void setManga_status(boolean manga_status) {
        this.manga_status = manga_status;
    }

    public String[] getGenre_id() {
        return genre_id;
    }

    public void setGenre_id(String[] genre_id) {
        this.genre_id = genre_id;
    }

    public ArrayList<String> getGenres() {
        return genres;
    }

    public void setGenres(ArrayList<String> genres) {
        this.genres = genres;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getManga_id() {
        return manga_id;
    }

    public void setManga_id(int manga_id) {
        this.manga_id = manga_id;
    }
}
