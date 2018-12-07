package com.NitroReader;

import com.NitroReader.services.ResBuilderService;
import com.NitroReader.utilities.DBAccess;
import com.NitroReader.utilities.PropertiesReader;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import models.ResponseTracker;
import models.TrackerModel;
import org.postgresql.core.SqlCommand;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.stream.Collectors;

@WebServlet("/TrackerMangaServl")
public class TrackerMangaServl extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ObjectMapper objM = new ObjectMapper();
        objM.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objM.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        TrackerModel trackerMODEL = objM.readValue(request.getReader().lines().collect(Collectors.joining(System.lineSeparator())),TrackerModel.class);
        HttpSession session = request.getSession(false);
        PreparedStatement pstm = null;
        DBAccess dbAccess = DBAccess.getInstance();
        Connection con = dbAccess.createConnection();
        PropertiesReader props = PropertiesReader.getInstance();
        PrintWriter out = response.getWriter();
        ResultSet rs = null;
        //if((boolean)request.getAttribute("loggued")== true){
            if (session == null){
                System.out.println(props.getValue("session_null"));
            }else{

                try{
                    pstm = con.prepareStatement(props.getValue("queryGetTracker_id"));
                    pstm.setInt(1,(Integer) session.getAttribute("id"));
                    pstm.setInt(2, trackerMODEL.getManga_id());
                    rs = pstm.executeQuery();
                    if(rs.next()){
                        //actualiza el tracker del manga
                        int tracker_id = rs.getInt(1);
                        pstm = con.prepareStatement(props.getValue("queryUpdateTrackerManga"));
                        pstm.setBoolean(1,trackerMODEL.getFinished());
                        pstm.setInt(2, tracker_id);
                        pstm.executeUpdate();
                        ResBuilderService.BuildOKEmpty(out);
                    }
                    else{
                        //crear un trackermanga
                        createMangaTracker(con, props, trackerMODEL.getManga_id(), false, session);
                        ResBuilderService.BuildOKEmpty(out);
                    }

                }
                catch(SQLException e) {
                    ResBuilderService.BuildResError(out);
                    e.printStackTrace();}
                finally {
                    if (rs != null){
                        try {
                            rs.close();
                        } catch (SQLException e1) {
                            ResBuilderService.BuildResError(out);
                            e1.printStackTrace();
                        }
                    }
                    if (con != null){
                        dbAccess.closeConnection(con);
                    }
                }
            }
      //  }


    }

    //getTracker of a manga
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ObjectMapper objM = new ObjectMapper();
        objM.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objM.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        TrackerModel data = new TrackerModel();
        PreparedStatement pstm = null;
        DBAccess dbAccess = DBAccess.getInstance();
        Connection con = dbAccess.createConnection();
        ResultSet rs = null;
        HttpSession session = request.getSession(false);
        PropertiesReader props = PropertiesReader.getInstance();
        PrintWriter out = response.getWriter();
        boolean mangaTracker;
      //  if((boolean)request.getAttribute("loggued")== true){
            if(session == null){
                System.out.println(props.getValue("session_null"));
                mangaTracker = false;
                data.setFinished(mangaTracker);
            }else{
                int manga_id =  Integer.parseInt(request.getParameter("manga_id"));
                int user_id =  (Integer) session.getAttribute("id");
                try{
                    pstm = con.prepareStatement(props.getValue("queryMangaFinished"));
                    pstm.setInt(1, user_id);
                    pstm.setInt(2, manga_id);
                    rs = pstm.executeQuery();
                    if(rs.next()){
                        mangaTracker = rs.getBoolean(1);
                        data.setManga_id(manga_id);
                        data.setFinished(mangaTracker);
                    }else{
                        createMangaTracker(con, props, manga_id, false, session);
                        mangaTracker = false;
                        data.setFinished(mangaTracker);
                    }


                }catch (SQLException e){
                    ResBuilderService.BuildResError(out);
                    e.printStackTrace();}
                finally {
                    if (rs != null){
                        try {
                            rs.close();
                        } catch (SQLException e1) {
                            ResBuilderService.BuildResError(out);
                            e1.printStackTrace();
                        }
                    }
                    if (con != null){
                        dbAccess.closeConnection(con);
                    }
                }
            }
            ResBuilderService.BuildOk(data,out);
      //  }

    }

    private void createMangaTracker( Connection con, PropertiesReader props, int Manga_id, boolean finished, HttpSession session){
        try {
            PreparedStatement pstm = con.prepareStatement(props.getValue("queryTrackerManga"));
            pstm.setInt(1, (Integer) session.getAttribute("id"));
            pstm.setInt(2, Manga_id);
            pstm.setBoolean(3, finished);
            pstm.executeUpdate();
        }catch (SQLException e){ e.printStackTrace();}

    }
}
