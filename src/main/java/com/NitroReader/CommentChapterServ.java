package com.NitroReader;

import com.NitroReader.services.CommentMangaService;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import models.ChapterComments;
import models.Response;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.stream.Collectors;

@WebServlet(name = "CommentChapterServ")
public class CommentChapterServ extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ObjectMapper objM = new ObjectMapper();
        objM.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objM.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        objM.setSerializationInclusion(JsonInclude.Include.NON_DEFAULT);
        Response<ChapterComments> res = new Response<>();
        PrintWriter out = response.getWriter();
        ChapterComments ChapterC = objM.readValue(request.getReader().lines().collect(Collectors.joining(System.lineSeparator())), ChapterComments.class);
        CommentMangaService.createComment(manga, res);

        String r = objM.writeValueAsString(res);
        System.out.println(r);
        out.print(r);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
