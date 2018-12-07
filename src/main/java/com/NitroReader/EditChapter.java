package com.NitroReader;

import com.NitroReader.Builder.*;
import com.NitroReader.services.ResBuilderService;
import com.NitroReader.utilities.DBAccess;
import com.NitroReader.utilities.PropertiesReader;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FileUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;

@MultipartConfig
@WebServlet("/EditChapter")
public class EditChapter extends HttpServlet {
    String currentChap ;
    String mangaid;
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        PropertiesReader props = PropertiesReader.getInstance();
        Collection<Part> files = request.getParts();
        InputStream filecontent = null;
        OutputStream os = null;
        String title = request.getParameter("title");
        PrintWriter out = response.getWriter();
        DBAccess dbAccess = DBAccess.getInstance();
        Connection con = dbAccess.createConnection();
        try(PreparedStatement pstm = con.prepareStatement(props.getValue("queryIChapterPages"))) {
            String baseDir = props.getValue("direction")+ mangaid+"\\"+currentChap;
            FileUtils.cleanDirectory(new File(baseDir));
            int c = new File(baseDir).listFiles().length;
            c++;
            for (Part file : files) {
                if (this.getFileName(file) != null){
                    filecontent = file.getInputStream();
                    os = new FileOutputStream(baseDir + "/" + c+".png");
                    c++;
                    int read = 0;
                    byte[] bytes = new byte[1024];
                    while ((read = filecontent.read(bytes)) != -1) {
                        os.write(bytes, 0, read);
                    }
                    if (filecontent != null) {
                        filecontent.close();
                    }
                    if (os != null) {
                        os.close();
                    }
                }

            }
            c--;
            pstm.setInt(1, c);
            pstm.setString(2, title);
            pstm.setInt(3, Integer.parseInt(mangaid));
            pstm.setInt(4,Integer.parseInt(currentChap));
            pstm.executeUpdate();
            ResBuilderService.BuildOKEmpty(out);

        } catch (Exception e) {
            ResBuilderService.BuildResError(out);
            e.printStackTrace();
        }finally {

            if (con != null){
                dbAccess.closeConnection(con);
            }
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        PropertiesReader props = PropertiesReader.getInstance();
        currentChap = request.getParameter("currentChap");
        mangaid = request.getParameter("mangaid");
        PrintWriter out = response.getWriter();
        HashMap<String , String> item = new HashMap<>();
        ObjectMapper objM = new ObjectMapper();
        String r;
        DBAccess dbAccess = DBAccess.getInstance();
        Connection con = dbAccess.createConnection();
        ResultSet rs = null;
        PreparedStatement pstm = null;
        int manga_id = Integer.parseInt(request.getParameter("mangaid"));
        int chapter_num = Integer.parseInt(request.getParameter("currentChap"));
        Response rf;
        try{
            String baseDir = props.getValue("direction")+ mangaid+"\\"+currentChap;
            String serveDir = props.getValue("dbMangaDirection")+mangaid+"\\"+currentChap;
            int numoffiles = new File(baseDir).listFiles().length;
            for(int i = 1; i<=numoffiles ; i++){
                item.put("direccion"+(i),serveDir+"\\"+i+".png");
            }
            pstm = con.prepareStatement(props.getValue("queryChapterInfo"));
            pstm.setInt(1, chapter_num);
            pstm.setInt(2, manga_id);
            rs = pstm.executeQuery();
            if(rs.next()){
                item.put("title", rs.getString("chapter_title"));
                item.put("chapter_id", rs.getString("chapter_id"));
            }

            ResBuilderService.BuildOk(item,out);
        }
        catch(Exception e ){
            ResBuilderService.BuildResError(out);
            e.printStackTrace();
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

    private String getFileName(Part part) {
        for (String content : part.getHeader("content-disposition").split(";")) {
            if (content.trim().startsWith("filename")) {
                return content.substring(content.indexOf('=') + 1).trim().replace("\"", "");
            }
        }
        return null;
    }
}
