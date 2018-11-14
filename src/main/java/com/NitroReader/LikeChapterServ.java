package com.NitroReader;

import com.NitroReader.services.LikeChapterService;

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

    }



    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ObjectMapper objM = new ObjectMapper();
        objM.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objM.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        PrintWriter out = resp.getWriter();
        Response<HashMap<String,Object>> res = new Response<>();
        ChapterCommentsLikesModel ChapterL = objM.readValue(req.getReader().lines().collect(Collectors.joining(System.lineSeparator())), ChapterCommentsLikesModel.class);
        LikeChapterService.deleteLike(ChapterL, res);
        String r = objM.writeValueAsString(res);
        System.out.println(r);
        out.print(r);
    }
}