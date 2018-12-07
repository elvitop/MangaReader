package com.NitroReader;

import com.NitroReader.services.ResBuilderService;
import com.NitroReader.utilities.DBAccess;
import com.NitroReader.utilities.PropertiesReader;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import models.Response;
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

@WebServlet("/TrackerChapterGETServl")
public class TrackerChapterGETServl extends HttpServlet {
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
        HashMap<String, Object> res = new HashMap<>();
        HashMap<String,Object> data = objM.readValue(request.getReader().lines().collect(Collectors.joining(System.lineSeparator())), HashMap.class);
        boolean chapterfinished;
        int LP;
        int manga_id =  (Integer)data.get("manga_id");
        int numchaps = (int) data.get("numchaps");
       // if((boolean)request.getAttribute("loggued")== true){
            if (session == null) {
                System.out.println(props.getValue("session_null"));
                for (int i= 0; i<numchaps; i++) {
                    int id = (Integer) data.get("id" + i);
                    chapterfinished = false;
                    res.put(""+id, chapterfinished);
                    LP = 1;
                    res.put("p"+id, LP);
                }
            } else {

                try{
                    pstm = con.prepareStatement(props.getValue("queryGetTracker_id"));
                    pstm.setInt(1,(Integer) session.getAttribute("id"));
                    pstm.setInt(2, manga_id);
                    rs = pstm.executeQuery();
                    if(rs.next()){
                        int tracker_id = rs.getInt(1);
                        for (int i= 0; i<numchaps; i++){
                            int id = (Integer) data.get("id" + i);
                            pstm = con.prepareStatement(props.getValue("queryGETChapterTracker"));
                            pstm.setInt(1, tracker_id);
                            pstm.setInt(2, id);
                            rs = pstm.executeQuery();
                            if(rs.next()){
                                chapterfinished = rs.getBoolean("chapter_finished");
                                res.put(""+id,chapterfinished);
                                LP = rs.getInt("lastpage_seen");
                                res.put("p"+id, LP);
                            }else{
                                chapterfinished = false;
                                res.put(""+id, chapterfinished);
                                LP = 1;
                                res.put("p"+id, LP);}
                        }
                    }else {
                        res.put("error",props.getValue("TrackerMNoExiste"));
                    }
                }catch (SQLException e){
                    e.printStackTrace();
                    ResBuilderService.BuildResError(out);
                }finally {
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

        ResBuilderService.BuildOk(res, out);
        //}

        }


    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
