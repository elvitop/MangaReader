package com.NitroReader;

import com.NitroReader.services.CRUDManga;
import com.NitroReader.utilities.PropertiesReader;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import models.Manga;
import models.Response;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

@MultipartConfig
@WebServlet("/CRUDMangaServlet")
public class CRUDMangaServlet extends HttpServlet {
    private ObjectMapper objM = new ObjectMapper();

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        objM.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objM.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        Response<Manga> res = new Response<>();
        PrintWriter out = response.getWriter();
        System.out.println(request.getParameter("manga_request"));
        if(Boolean.parseBoolean(request.getParameter("manga_request"))){
            if (!((Boolean) request.getAttribute("mangaExist"))){
                CRUDManga.createManga(request, res);
            } else{
                res.setStatus(423);
                res.setMessage("Not ok");
                res.setData(null);
            }
        } else CRUDManga.updateManga(request, res);

        String r = objM.writeValueAsString(res);
        System.out.println(r);
        out.print(r);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        objM.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objM.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        Response<Manga> res = new Response<>();
        PrintWriter out = response.getWriter();
        CRUDManga.readManga(request, res);
        String r = objM.writeValueAsString(res);
        System.out.println(r);
        out.print(r);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        objM.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objM.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        Response<Manga> res = new Response<>();
        PrintWriter out = resp.getWriter();
        CRUDManga.deleteManga(req, res, objM);
        String r = objM.writeValueAsString(res);
        System.out.println(r);
        out.print(r);
    }
}
