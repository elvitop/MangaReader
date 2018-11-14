package com.NitroReader.services;

import com.NitroReader.utilities.DBAccess;
import com.NitroReader.utilities.PropertiesReader;
import models.ChapterCommentsLikesModel;
import models.Manga;
import models.Response;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public class LikeChapterService {
    //METHOD FOR LIKE A MANGA
    public static void likeChapter(ChapterCommentsLikesModel ChapterL, Response<HashMap<String,Object>> res){
        PropertiesReader props = PropertiesReader.getInstance();
        DBAccess dbAccess = DBAccess.getInstance();
        Connection con = dbAccess.createConnection();
        ResultSet rs = null;
        HashMap<String, Object> data = new HashMap<>();
        try(PreparedStatement pstm = con.prepareStatement(props.getValue("queryLChapter"), ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
            con.setAutoCommit(false);
            pstm.setInt(1, ChapterL.getUser_id());
            pstm.setInt(2, ChapterL.getChapter_id());
            pstm.setInt(3, ChapterL.getChapter_id());
            rs = pstm.executeQuery();
            String likes = null;
            if (rs.next()){
                data.put("likesChapter", rs.getInt(1) + 1);
                data.put("like", true);
                likes = props.getValue("chapterLike") + String.valueOf(rs.getInt(1));
            } else{
                ServiceMethods.setResponse(res, 404, props.getValue("errorChapterLike"), null);
            }
            con.commit();
            ServiceMethods.setResponse(res, 201, likes, data);
        } catch (SQLException | NullPointerException e) {
            System.out.println(props.getValue("errorMangaLike") + e.getMessage());
            ServiceMethods.setResponse(res, 404, props.getValue("errorMangaLike"), null);
        }finally {
            if (rs != null){
                try {
                    rs.close();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            }
            if (con != null){
                dbAccess.closeConnection(con);
            }
        }
    }
    //METHOD TO RETURN THE QUANTITY OF LIKES THAT HAVE A MANGA
    static int countLikesChapter(PreparedStatement pstm, int chapter_id) throws SQLException {
        ResultSet rs = null;
        int numLikes = 0;
        try {
            pstm.setInt(1, chapter_id);
            rs = pstm.executeQuery();
            if (rs.next()){
                numLikes = rs.getInt(1);
            }
        }finally {
            if (rs != null){
                rs.close();
            }
        }
        return numLikes;
    }
    //METHOD TO RETURN IF THE USER LIKES THE MANGA
    static boolean userLikeChapter(PreparedStatement pstm, int chapter_id, int user_id) throws SQLException {
        ResultSet rs = null;
        boolean isLike = false;
        try {
            pstm.setInt(1, chapter_id);
            pstm.setInt(2, user_id);
            rs = pstm.executeQuery();
            if (rs.next()){
                isLike = true;
            }
        }finally {
            if (rs != null){
                rs.close();
            }
        }
        return isLike;
    }
    //METHOD TO DELETE THE LIKE OF THE MANGA
    public static  void deleteLike(ChapterCommentsLikesModel ChapterL, Response<HashMap<String,Object>> res){
        PropertiesReader props = PropertiesReader.getInstance();
        DBAccess dbAccess = DBAccess.getInstance();
        Connection con = dbAccess.createConnection();
        HashMap<String, Object> data = new HashMap<>();

        try(PreparedStatement pstm = con.prepareStatement(props.getValue("queryDLChapter"))) {
            con.setAutoCommit(false);
            pstm.setInt(1, ChapterL.getUser_id());
            pstm.setInt(2, ChapterL.getChapter_id());
            pstm.executeUpdate();
            data.put("like", false);
            ServiceMethods.setResponse(res, 200, props.getValue("chapterDeleteLike"), data);
            con.commit();
        } catch (SQLException | NullPointerException e) {
            System.out.println(props.getValue("errorCDL") + e.getMessage());
            ServiceMethods.setResponse(res, 404, props.getValue("errorCDL"), null);
        }finally {
            if (con != null){
                dbAccess.closeConnection(con);
            }
        }
    }




}
