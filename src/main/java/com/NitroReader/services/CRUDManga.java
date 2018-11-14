package com.NitroReader.services;

import com.NitroReader.utilities.DBAccess;
import com.NitroReader.utilities.PropertiesReader;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonAppend;
import models.Manga;
import models.Response;
import org.apache.commons.io.FileUtils;


import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class CRUDManga {

    //METHOD TO CREATE THE MANGA
    public static void createManga(HttpServletRequest request, Response<Manga> res) throws ServletException {
        PropertiesReader props = PropertiesReader.getInstance();
        Manga manga = new Manga();
        manga.setUser_id(Integer.valueOf(request.getParameter("user_id")));
        manga.setManga_name(request.getParameter("manga_name"));
        manga.setManga_synopsis(request.getParameter("manga_synopsis"));
        manga.setGenre_id(request.getParameterValues("genres_id"));
        manga.setLocation(props.getValue("direction"));
        manga.setManga_status(Boolean.parseBoolean(request.getParameter("manga_status")));
        DBAccess dbAccess = DBAccess.getInstance();
        ResultSet rs = null;
        Connection con = dbAccess.createConnection();
        Savepoint savepoint = null;

        try(PreparedStatement pstm = con.prepareStatement(props.getValue("queryIManga"));
            PreparedStatement pstm2 = con.prepareStatement(props.getValue("queryIMangaGenre"));
            PreparedStatement pstm3 = con.prepareStatement(props.getValue("querySMangaId"), ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            PreparedStatement pstm4 = con.prepareStatement(props.getValue("queryIMangaDirection"))) {
                con.setAutoCommit(false);
                savepoint = con.setSavepoint("savepoint");
                pstm.setInt(1, manga.getUser_id());
                pstm.setString(2, manga.getManga_name());
                pstm.setString(3, manga.getManga_synopsis());
                pstm.setBoolean(4, manga.isManga_status());
                pstm.setDate(5, ServiceMethods.getDate());
                pstm.setString(6, "direction");
                pstm.executeUpdate();

                pstm3.setString(1, manga.getManga_name());
                rs = pstm3.executeQuery();
                if (rs.next()) {
                    int manga_id = rs.getInt("manga_id");
                    ServiceMethods.insertGenres(manga.getGenre_id(), manga_id, pstm2);
                    uploadManga(request.getPart("file"), manga.getLocation(), manga_id);
                    pstm4.setString(1, String.valueOf(manga_id) + "/" + String.valueOf(manga_id) + ".jpg");
                    pstm4.setInt(2, manga_id);
                    pstm4.executeUpdate();
                }
                ServiceMethods.setResponse(res, 201, props.getValue("mangaCreated"), null);
                con.commit();
        } catch (SQLException | IOException | NullPointerException  e) {
            e.printStackTrace();
            try {
                con.rollback(savepoint);
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            ServiceMethods.setResponse(res, 404, props.getValue("mangaError"), null);
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

    //METHOD TO GET THE MANGA
    public static void readManga(HttpServletRequest request, Response<Manga> res) {
        PropertiesReader props = PropertiesReader.getInstance();
        DBAccess dbAccess = DBAccess.getInstance();
        ResultSet rs = null;
        Manga data = new Manga();
        data.setGenres(new ArrayList<>());
        data.setGenre_id(null);
        int manga = Integer.valueOf(request.getParameter("manga"));
        int user_id = Integer.valueOf(request.getParameter("user_id"));
        Connection con = dbAccess.createConnection();
        try(PreparedStatement pstm = con.prepareStatement(props.getValue("querySManga"), ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            PreparedStatement pstm2 = con.prepareStatement(props.getValue("querySMangaGenres"), ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            PreparedStatement pstm3 = con.prepareStatement(props.getValue("querySLManga"), ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            PreparedStatement pstm4 = con.prepareStatement(props.getValue("queryifLManga"), ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)
            ) {
            pstm.setInt(1, manga);
            rs = pstm.executeQuery();

            if (rs.next()){
                data.setManga_name(rs.getString("manga_name"));
                data.setManga_status(rs.getBoolean("manga_status"));
                data.setManga_synopsis(rs.getString("manga_synopsis"));
                data.setLocation(rs.getString("manga_location"));
                pstm2.setInt(1, manga);
            }
            rs = pstm2.executeQuery();
            while (rs.next()){
                data.getGenres().add(rs.getString("genre_des"));
            }

            data.setLikesManga(LikeMangaService.countLikesManga(pstm3, manga));
            data.setLike(LikeMangaService.userLikeManga(pstm4, manga, user_id));

            ServiceMethods.setResponse(res, 200, "OK", data);
        } catch (SQLException e) {
            System.out.println(props.getValue("errorFetchManga") + e.getMessage());
            ServiceMethods.setResponse(res, 404, props.getValue("errorFetchManga"), null);
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

    //METHOD FOR UPLOAD MANGA IMAGE
    private static void uploadManga(Part file, String location, int manga_id) throws IOException {
        File directory = new File(location + "/" + String.valueOf(manga_id));
        if(directory.exists()){} else directory.mkdir();
        InputStream filecontent = file.getInputStream();
        OutputStream os = new FileOutputStream(location  + String.valueOf(manga_id) + "/" + String.valueOf(manga_id)+ ".jpg");
        int read = 0;
        byte[] bytes = new byte[1024];

        while ((read = filecontent.read(bytes)) != -1) {
            os.write(bytes, 0, read);
        }
            filecontent.close();
            os.close();
    }

    //METHOD FOR UPDATE MANGA
    public static void updateManga(HttpServletRequest request, Response<Manga> res) throws IOException, ServletException {
        PropertiesReader props = PropertiesReader.getInstance();
        Manga manga = new Manga();
        int manga_id = Integer.valueOf(request.getParameter("manga_id"));
        manga.setManga_name(request.getParameter("manga_name"));
        manga.setManga_synopsis(request.getParameter("manga_synopsis"));
        manga.setManga_status(Boolean.parseBoolean(request.getParameter("manga_status")));
        Part file = request.getPart("file");
        DBAccess dbAccess = DBAccess.getInstance();
        Connection con = dbAccess.createConnection();
        try(PreparedStatement pstm = con.prepareStatement(props.getValue("queryUManga"))) {
             pstm.setString(1, manga.getManga_name());
             pstm.setString(2, manga.getManga_synopsis());
             pstm.setBoolean(3, manga.isManga_status());
             pstm.setInt(4, manga_id);
             pstm.executeUpdate();
             uploadManga(file, props.getValue("direction"), manga_id);
             ServiceMethods.setResponse(res, 200, props.getValue("mangaUpdated"), null);

        } catch (SQLException e) {
            e.printStackTrace();
            ServiceMethods.setResponse(res, 404, props.getValue("errorMangaUpdated"), null);
        }finally {
            dbAccess.closeConnection(con);
        }
    }

    //METHOD FOR DELETE MANGA
    public static void deleteManga(HttpServletRequest request, Response<Manga> res, ObjectMapper objM) throws IOException {
        PropertiesReader props = PropertiesReader.getInstance();
        DBAccess dbAccess = DBAccess.getInstance();
        Connection con = dbAccess.createConnection();
        Manga manga = objM.readValue(request.getReader().lines().collect(Collectors.joining(System.lineSeparator())), Manga.class);
        try(PreparedStatement pstm = con.prepareStatement(props.getValue("queryDManga")) ){
            pstm.setInt(1, manga.getManga_id());
            pstm.executeUpdate();
            File fileToDelete = new File(props.getValue("direction") + String.valueOf(manga.getManga_id()));
            FileUtils.deleteDirectory(fileToDelete);
            ServiceMethods.setResponse(res, 200, props.getValue("mangaDeleted"), null);
        } catch (SQLException | IOException e) {
            e.printStackTrace();
            ServiceMethods.setResponse(res , 404, props.getValue("errorMangaDeleted"), null);
        }finally {
            dbAccess.closeConnection(con);
        }
    }
}
