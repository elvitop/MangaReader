package com.NitroReader.services;

import com.NitroReader.utilities.DBAccess;
import com.NitroReader.utilities.PropertiesReader;
import com.fasterxml.jackson.core.JsonProcessingException;
import models.CommentsManga;
import models.Manga;
import models.Response;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class CommentMangaService {

    //METHOD TO CREATE A COMMENT
    public static void createComment(Manga manga, PrintWriter out) throws JsonProcessingException {
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
            rs = pstm.executeQuery();
            if (rs.next()){
                data.setComment(rs.getString("comment_content"));
                data.setUser_name(rs.getString("user_name"));
                data.setComment_id(rs.getInt("comment_id"));
                ResBuilderService.BuildOk(data, out);
            }
            con.commit();
        } catch (SQLException | NullPointerException e) {
            ResBuilderService.BuildResError(out);
            System.out.println(props.getValue("errorCommentManga") + " " + e.getMessage() );
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

    //METHOD TO FETCH ALL THE COMMENTS OF A MANGA
    public static void getAllComments(Manga manga, Response<Manga> res, HttpServletRequest request, PrintWriter out) throws JsonProcessingException {

        PropertiesReader props = PropertiesReader.getInstance();
        DBAccess dbAccess = DBAccess.getInstance();
        Connection con = dbAccess.createConnection();
        Manga data = new Manga();
        data.setComments(new ArrayList<>());
        HttpSession session = request.getSession(false);
        boolean logged = (Boolean) request.getAttribute("logged");
        ResultSet rs = null;

        try(PreparedStatement pstm = con.prepareStatement(props.getValue("querySCManga"), ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
            pstm.setInt(1, manga.getManga_id());
            rs = pstm.executeQuery();
            if (logged){
                data.setLogged(true);
            }else data.setLogged(false);
            while (rs.next()){
                CommentsManga comments = new CommentsManga();
                comments.setName(rs.getString("user_name"));
                comments.setComment(rs.getString("comment_content"));
                comments.setComment_id(rs.getInt("comment_id"));
                if (logged){
                    if (rs.getInt("user_id") == (int) session.getAttribute("id")) {
                        comments.setOwned(true);
                    } else if((boolean) session.getAttribute("admin")) {
                        comments.setOwned(true);
                    } else {
                        comments.setOwned(false);
                    }
                }else{
                    comments.setOwned(false);
                }
                data.getComments().add(comments);
            }
            ResBuilderService.BuildOk(data, out);
        } catch (SQLException | NullPointerException e) {
            ResBuilderService.BuildResError(out);
            System.out.println(props.getValue("errorAllCommentsManga") + e.getMessage());
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
    public static void updateComment(Manga manga, PrintWriter out) throws JsonProcessingException {
        PropertiesReader props = PropertiesReader.getInstance();
        DBAccess dbAccess = DBAccess.getInstance();
        Connection con = dbAccess.createConnection();
        try(PreparedStatement pstm = con.prepareStatement(props.getValue("queryUCManga"))) {
            pstm.setString(1, manga.getNewComment());
            pstm.setInt(2, manga.getComment_id());
            pstm.executeUpdate();
            ResBuilderService.BuildOKEmpty(out);
        } catch (SQLException | NullPointerException e) {
            ResBuilderService.BuildResError(out);
            System.out.println(props.getValue("errorCUpdated") + e.getMessage());
        }finally {
            dbAccess.closeConnection(con);
        }
    }

    //METHOD TO DELETE A COMMENT
    public static void deleteComment(Manga manga, PrintWriter out) throws JsonProcessingException {
        PropertiesReader props = PropertiesReader.getInstance();
        DBAccess dbAccess = DBAccess.getInstance();
        Connection con = dbAccess.createConnection();

        try(PreparedStatement pstm = con.prepareStatement(props.getValue("queryDCManga"), ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
            pstm.setInt(1, manga.getComment_id());
            pstm.executeUpdate();
            ResBuilderService.BuildOKEmpty(out);
        } catch (SQLException | NullPointerException e) {
            ResBuilderService.BuildResError(out);
            System.out.println(props.getValue("errorCD") +  e.getMessage());
        }finally {
            dbAccess.closeConnection(con);
        }

    }
}
