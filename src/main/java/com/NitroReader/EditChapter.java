package com.NitroReader;

import com.NitroReader.utilities.DBAccess;
import com.NitroReader.utilities.PropertiesReader;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FileUtils;
import org.json.JSONObject;

import javax.servlet.ServletContext;
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
        PrintWriter out = response.getWriter();
        DBAccess dbAccess = DBAccess.getInstance();
        Connection con = dbAccess.createConnection();
        try(PreparedStatement pstm = con.prepareStatement(props.getValue("queryIChapterPages"))) {
            String baseDir = props.getValue("direction")+ mangaid+"\\"+currentChap;
            FileUtils.cleanDirectory(new File(baseDir));
            int c = new File(baseDir).listFiles().length;
            c++;
            for (Part file : files) {
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
            c--;
            pstm.setInt(1, c);
            pstm.setInt(2, Integer.parseInt(mangaid));
            pstm.setInt(3,Integer.parseInt(currentChap));
            pstm.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
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
        try{
            String baseDir = props.getValue("direction")+ mangaid+"\\"+currentChap;
            String serveDir = props.getValue("dbMangaDirection")+mangaid+"\\"+currentChap;
            int numoffiles = new File(baseDir).listFiles().length;
            for(int i = 1; i<=numoffiles ; i++){
                item.put("direccion"+(i),serveDir+"\\"+i+".png");
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }finally {
            r= objM.writeValueAsString(item);
            System.out.println(r);
            out.print(r);
        }

    }
}
