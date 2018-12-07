package com.NitroReader.Command;

import com.NitroReader.services.ResBuilderService;
import com.NitroReader.services.ServiceMethods;
import com.NitroReader.utilities.DBAccess;
import com.NitroReader.utilities.PropertiesReader;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public class LikeM {
    public LikeM(){}

    public void on (int userid, int mangaid, PrintWriter out) throws JsonProcessingException {
        PropertiesReader props = PropertiesReader.getInstance();
        DBAccess dbAccess = DBAccess.getInstance();
        Connection con = dbAccess.createConnection();
        ResultSet rs = null;
        HashMap<String, Object> data = new HashMap<>();
        try(PreparedStatement pstm = con.prepareStatement(props.getValue("queryLManga"), ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
            pstm.setInt(1, userid);
            pstm.setInt(2, mangaid);
            pstm.setInt(3, mangaid);
            rs = pstm.executeQuery();
            if (rs.next()){
                data.put("likesManga", rs.getInt(1) + 1);
                data.put("like", true);
            } else{
                ResBuilderService.BuildResError(out);
            }
            ResBuilderService.BuildOk(data, out);
        } catch (SQLException | NullPointerException e) {
            System.out.println(props.getValue("errorMangaLike") + e.getMessage());
            ResBuilderService.BuildResError(out);
            e.printStackTrace();
        }finally {
            if (rs != null){
                try {
                    rs.close();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                    ResBuilderService.BuildResError(out);
                }
            }
            if (con != null){
                dbAccess.closeConnection(con);
            }
        }

    }

    public void off (int userid, int mangaid, PrintWriter out) throws JsonProcessingException {
        PropertiesReader props = PropertiesReader.getInstance();
        DBAccess dbAccess = DBAccess.getInstance();
        Connection con = dbAccess.createConnection();
        HashMap<String, Object> data = new HashMap<>();

        try(PreparedStatement pstm = con.prepareStatement(props.getValue("queryDLManga"))) {
            con.setAutoCommit(false);
            pstm.setInt(1, userid);
            pstm.setInt(2, mangaid);
            pstm.executeUpdate();
            data.put("like", false);
            ResBuilderService.BuildOk(data, out);
            con.commit();
        } catch (SQLException | NullPointerException | JsonProcessingException e) {
            e.printStackTrace();
            ResBuilderService.BuildResError(out);
        }finally {
            if (con != null){
                dbAccess.closeConnection(con);
            }
        }
    }

}
