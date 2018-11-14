package com.NitroReader;

import com.NitroReader.services.CommentMangaService;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import models.Manga;
import models.Response;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.stream.Collectors;

@WebServlet("/CommentManga")
public class CommentManga extends HttpServlet {


    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ObjectMapper objM = new ObjectMapper();
        objM.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objM.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        objM.setSerializationInclusion(JsonInclude.Include.NON_DEFAULT);
        Response<Manga> res = new Response<>();
        PrintWriter out = response.getWriter();
        Manga manga = objM.readValue(request.getReader().lines().collect(Collectors.joining(System.lineSeparator())), Manga.class);
        CommentMangaService.createComment(manga, res);

        String r = objM.writeValueAsString(res);
        System.out.println(r);
        out.print(r);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ObjectMapper objM = new ObjectMapper();
        objM.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objM.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        objM.setSerializationInclusion(JsonInclude.Include.NON_DEFAULT);
        Manga manga = objM.readValue(request.getReader().lines().collect(Collectors.joining(System.lineSeparator())), Manga.class);
        Response<Manga> res = new Response<>();
        PrintWriter out = response.getWriter();

        CommentMangaService.getAllComments(manga, res);

        String r = objM.writeValueAsString(res);
        System.out.println(r);
        out.print(r);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ObjectMapper objM = new ObjectMapper();
        objM.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objM.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        objM.setSerializationInclusion(JsonInclude.Include.NON_DEFAULT);
        Manga manga = objM.readValue(req.getReader().lines().collect(Collectors.joining(System.lineSeparator())), Manga.class);
        Response<Manga> res = new Response<>();
        PrintWriter out = resp.getWriter();

        CommentMangaService.updateComment(manga, res);

        String r = objM.writeValueAsString(res);
        System.out.println(r);
        out.print(r);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ObjectMapper objM = new ObjectMapper();
        objM.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objM.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        Manga manga = objM.readValue(req.getReader().lines().collect(Collectors.joining(System.lineSeparator())), Manga.class);
        Response<Manga> res = new Response<>();
        PrintWriter out = resp.getWriter();

        CommentMangaService.deleteComment(manga, res);

        String r = objM.writeValueAsString(res);
        System.out.println(r);
        out.print(r);
    }
}
