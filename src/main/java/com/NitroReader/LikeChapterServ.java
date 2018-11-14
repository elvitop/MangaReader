package com.NitroReader;

import com.NitroReader.services.LikeChapterService;
import com.NitroReader.services.ServiceMethods;
import com.NitroReader.utilities.DBAccess;
import com.NitroReader.utilities.PropertiesReader;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import models.ChapterCommentsLikesModel;
import models.Response;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.stream.Collectors;

@WebServlet("/LikeChapterServ")
public class LikeChapterServ extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ObjectMapper objM = new ObjectMapper();
        objM.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objM.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        objM.setSerializationInclusion(JsonInclude.Include.NON_DEFAULT);
        PrintWriter out = response.getWriter();
        Response<HashMap<String,Object>> res = new Response<>();
        ChapterCommentsLikesModel ChapterL = objM.readValue(request.getReader().lines().collect(Collectors.joining(System.lineSeparator())), ChapterCommentsLikesModel.class);
        LikeChapterService.likeChapter(ChapterL, res);
        String r = objM.writeValueAsString(res);
        System.out.println(r);
        out.print(r);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        PropertiesReader props = PropertiesReader.getInstance();
        DBAccess dbAccess = DBAccess.getInstance();
        Response<ChapterCommentsLikesModel> res = new Response<>();
        ObjectMapper objM = new ObjectMapper();
        PrintWriter out = response.getWriter();
        ChapterCommentsLikesModel data = new ChapterCommentsLikesModel();
        int chapter_id = Integer.valueOf(request.getParameter("Chapter_id"));
        int user_id = Integer.valueOf(request.getParameter("user_id"));
        Connection con = dbAccess.createConnection();
        try(
            PreparedStatement pstm3 = con.prepareStatement(props.getValue("querySLChapter"), ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            PreparedStatement pstm4 = con.prepareStatement(props.getValue("queryifLChapter"), ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)
        ) {
            data.setLikesChapter(LikeChapterService.countLikesChapter(pstm3, chapter_id));
            data.setLike(LikeChapterService.userLikeChapter(pstm4, chapter_id, user_id));
            ServiceMethods.setResponse(res, 200, "OK", data);
        } catch (SQLException e) {
            System.out.println(props.getValue("errorFetchManga") + e.getMessage());
            ServiceMethods.setResponse(res, 404, props.getValue("errorFetchManga"), null);
        }finally {
            dbAccess.closeConnection(con);
        }
        String r = objM.writeValueAsString(res);
        System.out.println(r);
        out.print(r);

    }



    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ObjectMapper objM = new ObjectMapper();
        objM.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objM.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        PrintWriter out = response.getWriter();
        Response<HashMap<String,Object>> res = new Response<>();
        ChapterCommentsLikesModel ChapterL = objM.readValue(request.getReader().lines().collect(Collectors.joining(System.lineSeparator())), ChapterCommentsLikesModel.class);
        LikeChapterService.deleteLike(ChapterL, res);
        String r = objM.writeValueAsString(res);
        System.out.println(r);
        out.print(r);
    }
}