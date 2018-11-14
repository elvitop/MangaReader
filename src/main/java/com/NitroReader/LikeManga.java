package com.NitroReader;

import com.NitroReader.services.LikeMangaService;
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
import java.util.HashMap;
import java.util.stream.Collectors;

@WebServlet("/LikeManga")
public class LikeManga extends HttpServlet {


    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ObjectMapper objM = new ObjectMapper();
        objM.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objM.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        objM.setSerializationInclusion(JsonInclude.Include.NON_DEFAULT);

        PrintWriter out = response.getWriter();
        Response<HashMap<String,Object>> res = new Response<>();
        Manga manga = objM.readValue(request.getReader().lines().collect(Collectors.joining(System.lineSeparator())), Manga.class);
        LikeMangaService.likeManga(manga, res);

        String r = objM.writeValueAsString(res);
        System.out.println(r);
        out.print(r);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ObjectMapper objM = new ObjectMapper();
        objM.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objM.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        PrintWriter out = resp.getWriter();
        Response<HashMap<String,Object>> res = new Response<>();
        Manga manga = objM.readValue(req.getReader().lines().collect(Collectors.joining(System.lineSeparator())), Manga.class);

        LikeMangaService.deleteLike(manga, res);

        String r = objM.writeValueAsString(res);
        System.out.println(r);
        out.print(r);
    }
}
