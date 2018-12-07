package com.NitroReader.services;


import com.NitroReader.utilities.DBAccess;
import com.NitroReader.utilities.PropertiesReader;
import models.ChapterCommentsLikesModel;
import models.CommentsManga;
import models.Response;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class CommentChapterService {
    public static void createComment(ChapterCommentsLikesModel ChapterC, Response<ChapterCommentsLikesModel> res) {
        PropertiesReader props = PropertiesReader.getInstance();
        DBAccess dbAccess = DBAccess.getInstance();
        Connection con = dbAccess.createConnection();
        ResultSet rs = null;
        ChapterCommentsLikesModel data = new ChapterCommentsLikesModel();

        try (PreparedStatement pstm = con.prepareStatement(props.getValue("queryCChapter"), ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
            con.setAutoCommit(false);
            pstm.setInt(1, ChapterC.getUser_id());
            pstm.setInt(2, ChapterC.getChapter_id());
            pstm.setString(3, ChapterC.getNewComment());
            pstm.setDate(4, ServiceMethods.getDate());
            pstm.setInt(5, ChapterC.getUser_id());
            rs = pstm.executeQuery();
            if (rs.next()) {
                data.setComment(rs.getString("comment_content"));
                data.setUser_name(rs.getString("user_name"));
                ServiceMethods.setResponse(res, 201, "OK", data);
            }
            con.commit();
        } catch (SQLException | NullPointerException e) {
            ServiceMethods.setResponse(res, 404, props.getValue("ERROR"), null);
            System.out.println(props.getValue("ERROR"));
            e.printStackTrace();
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            dbAccess.closeConnection(con);
        }
    }
    //METHOD TO FETCH ALL THE COMMENTS OF ONE MANGA
    public static void getAllComments(ChapterCommentsLikesModel ChapterC, Response<ChapterCommentsLikesModel> res, HttpServletRequest request){
        PropertiesReader props = PropertiesReader.getInstance();
        DBAccess dbAccess = DBAccess.getInstance();
        Connection con = dbAccess.createConnection();
        ChapterCommentsLikesModel data = new ChapterCommentsLikesModel();
        HttpSession session = request.getSession(false);
        boolean logged = (Boolean) request.getAttribute("logged");
        data.setComments(new ArrayList<>());

        ResultSet rs = null;

        try(PreparedStatement pstm = con.prepareStatement(props.getValue("querySCChapter"), ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
            pstm.setInt(1, ChapterC.getChapter_id());
            rs = pstm.executeQuery();
            if (logged){
                data.setLogged(true);
            }else data.setLogged(false);
            while (rs.next()){
                CommentsManga comments = new CommentsManga();
                comments.setName(rs.getString("user_name"));
                comments.setComment(rs.getString("comment_content"));
                if (logged){
                    if (rs.getInt("user_id") == (int) session.getAttribute("id")) {
                        comments.setOwned(true);
                    } else {
                        comments.setOwned(false);
                    }
                }else{
                    comments.setOwned(false);
                }
                data.getComments().add(comments);
            }
            ServiceMethods.setResponse(res, 200, "OK", data);
        } catch (SQLException | NullPointerException e) {
            ServiceMethods.setResponse(res, 404, "ERROR", data);
            System.out.println("ERROR" + e.getMessage());
        }finally {
            if (rs != null){
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            dbAccess.closeConnection(con);
        }
    }
    //METHOD TO EDIT A COMMENT
    public static void updateComment(ChapterCommentsLikesModel ChapterC, Response<ChapterCommentsLikesModel> res){
        PropertiesReader props = PropertiesReader.getInstance();
        DBAccess dbAccess = DBAccess.getInstance();
        Connection con = dbAccess.createConnection();

        try(PreparedStatement pstm = con.prepareStatement(props.getValue("queryUCChapter"))) {
            pstm.setString(1, ChapterC.getNewComment());
            pstm.setInt(2, ChapterC.getUser_id());
            pstm.setInt(3, ChapterC.getChapter_id());
            pstm.setString(4, ChapterC.getComment());
            pstm.executeUpdate();

            ServiceMethods.setResponse(res, 200, "OK", null);
        } catch (SQLException | NullPointerException e) {
            ServiceMethods.setResponse(res, 404, "ERROR", null);
            System.out.println("ERROR" + e.getMessage());
        }finally {
            dbAccess.closeConnection(con);
        }
    }
    //METHOD TO DELETE A COMMENT
    public static void deleteComment(ChapterCommentsLikesModel ChapterC, Response<ChapterCommentsLikesModel> res){
        PropertiesReader props = PropertiesReader.getInstance();
        DBAccess dbAccess = DBAccess.getInstance();
        Connection con = dbAccess.createConnection();

        try(PreparedStatement pstm = con.prepareStatement(props.getValue("queryDCChapter"), ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
            pstm.setInt(1, ChapterC.getUser_id());
            pstm.setInt(2, ChapterC.getChapter_id());
            pstm.setString(3, ChapterC.getComment());
            pstm.executeUpdate();

            ServiceMethods.setResponse(res, 200, "OK", null);
        } catch (SQLException | NullPointerException e) {
            ServiceMethods.setResponse(res, 404, "ERROR", null);
            System.out.println("ERROR" +  e.getMessage());
        }finally {
            dbAccess.closeConnection(con);
        }

    }

}
