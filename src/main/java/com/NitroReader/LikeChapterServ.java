package com.NitroReader;

import com.NitroReader.Command.*;
import com.NitroReader.services.LikeChapterService;
import com.NitroReader.services.ResBuilderService;
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
import javax.servlet.http.HttpSession;
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
        HttpSession session = request.getSession(false);
        objM.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objM.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        objM.setSerializationInclusion(JsonInclude.Include.NON_DEFAULT);
        PrintWriter out = response.getWriter();
        ChapterCommentsLikesModel ChapterL = objM.readValue(request.getReader().lines().collect(Collectors.joining(System.lineSeparator())), ChapterCommentsLikesModel.class);
        int userid = (int) session.getAttribute("id");
        int chapterid = ChapterL.getChapter_id();
        String switchState = ChapterL.getSwitchState();
        LikeC likeC = new LikeC();
        Command flipUplike = new FlipUpLikeC(likeC);
        Command flipDownlike = new FlipDownLikeC(likeC);
        SwitchLike mySwitch = new SwitchLike(flipUplike, flipDownlike);
        switch (switchState){
            case "OFF":
                mySwitch.FlipDown(userid, chapterid, out);
                break;
            case "ON":
                mySwitch.flipUp(userid, chapterid, out);
                break;
        }


    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        PropertiesReader props = PropertiesReader.getInstance();
        DBAccess dbAccess = DBAccess.getInstance();
        PrintWriter out = response.getWriter();
        HttpSession session = request.getSession(false);
        ChapterCommentsLikesModel data = new ChapterCommentsLikesModel();
        int chapter_id = Integer.valueOf(request.getParameter("Chapter_id"));
        int user_id = (int) session.getAttribute("id");
        Connection con = dbAccess.createConnection();
        try(
            PreparedStatement pstm3 = con.prepareStatement(props.getValue("querySLChapter"), ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            PreparedStatement pstm4 = con.prepareStatement(props.getValue("queryifLChapter"), ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)
        ) {
            data.setLikesChapter(LikeChapterService.countLikesChapter(pstm3, chapter_id));
            data.setLike(LikeChapterService.userLikeChapter(pstm4, chapter_id, user_id));
            ResBuilderService.BuildOk(data, out);
        } catch (SQLException e) {
            System.out.println(props.getValue("errorFetchManga") + e.getMessage());
            ResBuilderService.BuildResError(out);
        }finally {
            dbAccess.closeConnection(con);
        }


    }




}