package com.NitroReader.Command;

import com.NitroReader.services.ResBuilderService;
import com.NitroReader.utilities.DBAccess;
import com.NitroReader.utilities.PropertiesReader;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public class LikeC {
    public LikeC(){}

    public void on (int userid, int chapterid, PrintWriter out) throws JsonProcessingException {
        PropertiesReader props = PropertiesReader.getInstance();
        DBAccess dbAccess = DBAccess.getInstance();
        Connection con = dbAccess.createConnection();
        ResultSet rs = null;
        HashMap<String, Object> data = new HashMap<>();
        try(PreparedStatement pstm = con.prepareStatement(props.getValue("queryLChapter"), ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
            con.setAutoCommit(false);
            pstm.setInt(1, userid);
            pstm.setInt(2, chapterid);
            pstm.setInt(3, chapterid);
            rs = pstm.executeQuery();

            if (rs.next()){
                data.put("likesChapter", rs.getInt(1) + 1);
                data.put("like", true);

            } else{
                ResBuilderService.BuildResError(out);
            }
            con.commit();
            ResBuilderService.BuildOk(data, out);
        } catch (SQLException | NullPointerException e) {
            System.out.println(props.getValue("errorChapterLike") + e.getMessage());
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


    public void off (int userid, int chapterid, PrintWriter out) throws JsonProcessingException {
        PropertiesReader props = PropertiesReader.getInstance();
        DBAccess dbAccess = DBAccess.getInstance();
        Connection con = dbAccess.createConnection();
        HashMap<String, Object> data = new HashMap<>();

        try(PreparedStatement pstm = con.prepareStatement(props.getValue("queryDLChapter"))) {
            con.setAutoCommit(false);
            pstm.setInt(1, userid);
            pstm.setInt(2, chapterid);
            pstm.executeUpdate();
            data.put("like", false);
            ResBuilderService.BuildOk(data, out);
            con.commit();
        } catch (SQLException | NullPointerException e) {
            System.out.println(props.getValue("errorCDL") + e.getMessage());
            ResBuilderService.BuildResError(out);
        }finally {
            if (con != null){
                dbAccess.closeConnection(con);
            }
        }
    }
}
