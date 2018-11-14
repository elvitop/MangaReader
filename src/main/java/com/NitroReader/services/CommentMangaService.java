package com.NitroReader.services;

import com.NitroReader.utilities.DBAccess;
import com.NitroReader.utilities.PropertiesReader;
import models.CommentsManga;
import models.Manga;
import models.Response;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class CommentMangaService {

    //METHOD TO CREATE A COMMENT
    public static void createComment(Manga manga, Response<Manga> res){
        PropertiesReader props = PropertiesReader.getInstance();
        DBAccess dbAccess = DBAccess.getInstance();
        Connection con = dbAccess.createConnection();
        ResultSet rs = null;
        Manga data = new Manga();

        try(PreparedStatement pstm = con.prepareStatement(props.getValue("queryCManga"), ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
            con.setAutoCommit(false);
            pstm.setInt(1, manga.getUser_id());
            pstm.setInt(2, manga.getManga_id());
            pstm.setString(3, manga.getNewComment());
            pstm.setDate(4, ServiceMethods.getDate());
            pstm.setInt(5, manga.getUser_id());
            pstm.setInt(6, manga.getManga_id());
            rs = pstm.executeQuery();
            if (rs.next()){
                data.setComment(rs.getString("comment_content"));
                data.setUser_name(rs.getString("user_name"));
                ServiceMethods.setResponse(res, 201, props.getValue("commentManga") + rs.getString("manga_name"), data);
            }
            con.commit();
        } catch (SQLException | NullPointerException e) {
            ServiceMethods.setResponse(res, 404, props.getValue("errorCommentManga"), null);
            System.out.println(props.getValue("errorCommentManga"));
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

    //METHOD TO FETCH ALL THE COMMENTS OF ONE MANGA
    public static void getAllComments(Manga manga, Response<Manga> res){
        PropertiesReader props = PropertiesReader.getInstance();
        DBAccess dbAccess = DBAccess.getInstance();
        Connection con = dbAccess.createConnection();
        Manga data = new Manga();
        data.setComments(new ArrayList<>());

        ResultSet rs = null;

        try(PreparedStatement pstm = con.prepareStatement(props.getValue("querySCManga"), ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
            pstm.setInt(1, manga.getManga_id());
            rs = pstm.executeQuery();
            while (rs.next()){
                CommentsManga comments = new CommentsManga();
                comments.setName(rs.getString("user_name"));
                comments.setComment(rs.getString("comment_content"));
                comments.setId(rs.getInt("user_id"));
                data.getComments().add(comments);
            }
            ServiceMethods.setResponse(res, 200, props.getValue("allCommentsManga"), data);
        } catch (SQLException | NullPointerException e) {
            ServiceMethods.setResponse(res, 404, props.getValue("errorallCommentsManga"), data);
            System.out.println(props.getValue("errorallCommentsManga") + e.getMessage());
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
    public static void updateComment(Manga manga, Response<Manga> res){
        PropertiesReader props = PropertiesReader.getInstance();
        DBAccess dbAccess = DBAccess.getInstance();
        Connection con = dbAccess.createConnection();
        Manga data = new Manga();

        try(PreparedStatement pstm = con.prepareStatement(props.getValue("queryUCManga"))) {
            pstm.setString(1, manga.getNewComment());
            pstm.setInt(2, manga.getUser_id());
            pstm.setInt(3, manga.getManga_id());
            pstm.setString(4, manga.getComment());
            pstm.executeUpdate();

            ServiceMethods.setResponse(res, 200, props.getValue("cUpdated"), null);
        } catch (SQLException | NullPointerException e) {
            ServiceMethods.setResponse(res, 404, props.getValue("errorCUpdated"), null);
            System.out.println(props.getValue("errorCUpdated") + e.getMessage());
        }finally {
            dbAccess.closeConnection(con);
        }
    }

    //METHOD TO DELETE A COMMENT
    public static void deleteComment(Manga manga, Response<Manga> res){
        PropertiesReader props = PropertiesReader.getInstance();
        DBAccess dbAccess = DBAccess.getInstance();
        Connection con = dbAccess.createConnection();

        try(PreparedStatement pstm = con.prepareStatement(props.getValue("queryDCManga"), ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
            pstm.setInt(1, manga.getUser_id());
            pstm.setInt(2, manga.getManga_id());
            pstm.setString(3, manga.getComment());
            pstm.executeUpdate();

            ServiceMethods.setResponse(res, 200, props.getValue("commentDeleted"), null);
        } catch (SQLException | NullPointerException e) {
            ServiceMethods.setResponse(res, 404, props.getValue("errorCD"), null);
            System.out.println(props.getValue("errorCD") +  e.getMessage());
        }finally {
            dbAccess.closeConnection(con);
        }

    }
}
