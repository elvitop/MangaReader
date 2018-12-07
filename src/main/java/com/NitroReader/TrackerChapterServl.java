package com.NitroReader;

import com.NitroReader.services.ResBuilderService;
import com.NitroReader.utilities.DBAccess;
import com.NitroReader.utilities.PropertiesReader;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import models.ResponseTracker;
import models.TrackerModel;

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

@WebServlet("/TrackerChapterServl")
public class TrackerChapterServl extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ObjectMapper objM = new ObjectMapper();
        objM.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objM.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        HttpSession session = request.getSession(false);
        PreparedStatement pstm = null;
        DBAccess dbAccess = DBAccess.getInstance();
        Connection con = dbAccess.createConnection();
        PropertiesReader props = PropertiesReader.getInstance();
        ResultSet rs = null;
        PrintWriter out = response.getWriter();
        ResponseTracker res = new ResponseTracker();
        TrackerModel trackerMODEL = objM.readValue(request.getReader().lines().collect(Collectors.joining(System.lineSeparator())), TrackerModel.class);
       // if((boolean)request.getAttribute("loggued")== true){
            if (session == null) {
                System.out.println(props.getValue("session_null"));
            } else {
                try {
                    //buscamos y verificamos si existe el tracker del manga
                    PreparedStatement pstmGetTracker = con.prepareStatement(props.getValue("queryGetTracker_id"));
                    pstmGetTracker.setInt(1, (Integer) session.getAttribute("id"));
                    pstmGetTracker.setInt(2, trackerMODEL.getManga_id());
                    rs = pstmGetTracker.executeQuery();
                    if (rs.next()) {
                        //buscamos y verificamos si existe el tracker del capitulo
                        int tracker_id = rs.getInt(1);
                        pstmGetTracker = con.prepareStatement(props.getValue("queryGetChapterTracker_id"));
                        pstmGetTracker.setInt(1, tracker_id);
                        pstmGetTracker.setInt(2, trackerMODEL.getChapter_id());
                        rs = pstmGetTracker.executeQuery();
                        if (rs.next()) {
                            //Actualizar el tracker chapter
                            pstm = con.prepareStatement(props.getValue("queryUpdateTrackerChapter"));
                            pstm.setBoolean(1, trackerMODEL.getFinished());
                            pstm.setInt(2, trackerMODEL.getPage_tracker());
                            pstm.setInt(3, tracker_id);
                            pstm.setInt(4, trackerMODEL.getChapter_id());
                            pstm.executeUpdate();
                            res.setMessage(props.getValue("TrackerCactualizado"));
                            res.setStatus(200);
                            ResBuilderService.BuildOk(res, out);
                        } else {
                            //crear un tracker chapter
                            pstm = con.prepareStatement(props.getValue("queryTrackerChapter"));
                            pstm.setInt(1, tracker_id);
                            pstm.setInt(2, trackerMODEL.getChapter_id());
                            pstm.setBoolean(3, trackerMODEL.getFinished());
                            pstm.setInt(4, trackerMODEL.getPage_tracker());
                            pstm.executeUpdate();
                            res.setMessage(props.getValue("TrackerCcreado"));
                            res.setStatus(200);
                            ResBuilderService.BuildOk(res, out);
                        }

                    } else{
                        ResBuilderService.BuildResError(out);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                } finally {
                    if (rs != null){
                        try {
                            rs.close();
                        } catch (SQLException e1) {
                            ResBuilderService.BuildResError(out);
                            e1.printStackTrace();
                        }
                    }
                    if (con != null) {
                        dbAccess.closeConnection(con);
                    }
                }
            }
        //}

        }
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
