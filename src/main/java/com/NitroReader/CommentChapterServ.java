package com.NitroReader;

import com.NitroReader.services.CommentChapterService;
import com.NitroReader.services.ResBuilderService;
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
import java.util.stream.Collectors;

@WebServlet("/CommentChapterServ")
public class CommentChapterServ extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ObjectMapper objM = new ObjectMapper();
        objM.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objM.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        objM.setSerializationInclusion(JsonInclude.Include.NON_DEFAULT);
        Response<ChapterCommentsLikesModel> res = new Response<>();
        PrintWriter out = response.getWriter();
        ChapterCommentsLikesModel ChapterC = objM.readValue(request.getReader().lines().collect(Collectors.joining(System.lineSeparator())), ChapterCommentsLikesModel.class);
        CommentChapterService.createComment(ChapterC, res);
        ResBuilderService.BuildOk(res, out);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ObjectMapper objM = new ObjectMapper();
        objM.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objM.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        ChapterCommentsLikesModel ChapterC = new ChapterCommentsLikesModel();
        ChapterC.setChapter_id(Integer.parseInt(request.getParameter("chapter_id")));
        Response<ChapterCommentsLikesModel> res = new Response<>();
        PrintWriter out = response.getWriter();

        CommentChapterService.getAllComments(ChapterC, res, request);

        ResBuilderService.BuildOk(res, out);

    }
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ObjectMapper objM = new ObjectMapper();
        objM.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objM.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        ChapterCommentsLikesModel ChapterC = objM.readValue(request.getReader().lines().collect(Collectors.joining(System.lineSeparator())), ChapterCommentsLikesModel.class);
        Response<ChapterCommentsLikesModel> res = new Response<>();
        PrintWriter out = response.getWriter();

        CommentChapterService.deleteComment(ChapterC, res);

        ResBuilderService.BuildOk(res, out);
    }
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ObjectMapper objM = new ObjectMapper();
        objM.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objM.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        objM.setSerializationInclusion(JsonInclude.Include.NON_DEFAULT);
        ChapterCommentsLikesModel ChapterC = objM.readValue(request.getReader().lines().collect(Collectors.joining(System.lineSeparator())), ChapterCommentsLikesModel.class);
        Response<ChapterCommentsLikesModel> res = new Response<>();
        PrintWriter out = response.getWriter();

        CommentChapterService.updateComment(ChapterC, res);

        ResBuilderService.BuildOk(res, out);
    }
}
