package com.NitroReader;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

import com.NitroReader.services.ResBuilderService;
import com.NitroReader.services.ServiceMethods;
import com.NitroReader.utilities.AsyncThread;
import com.NitroReader.utilities.DBAccess;
import com.NitroReader.utilities.PropertiesReader;
import com.NitroReader.utilities.SendEmails;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import models.ChapterModel;
import org.apache.commons.io.FileUtils;

import static java.lang.Integer.*;


@WebServlet("/Chapter")
public class Chapter extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        PropertiesReader props = PropertiesReader.getInstance();
        ObjectMapper objM = new ObjectMapper();
        objM.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objM.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        String r;
        PrintWriter out = response.getWriter();
        ChapterModel res = objM.readValue(request.getReader().lines().collect(Collectors.joining(System.lineSeparator())), ChapterModel.class);
        String baseDir = props.getValue("direction")+ res.getMangaid();
        DBAccess dbAccess = DBAccess.getInstance();
        Connection con = dbAccess.createConnection();
        PreparedStatement pstm = null;
        int manga_id = Integer.parseInt(res.getMangaid());
        int chapter_num = Integer.parseInt(res.getChapternum());
        ResultSet rs = null;
        try{
            pstm = con.prepareStatement(props.getValue("queryChapterInfo"));
            pstm.setInt(1, chapter_num);
            pstm.setInt(2, manga_id);
            rs = pstm.executeQuery();
            if(rs.next()){
                ResBuilderService.BuildOk(res, out);
            }
            else{
                //LIMPIAR LOS TRACKERS
                pstm = con.prepareStatement(props.getValue("queryResetTracker"));
                pstm.setInt(1, manga_id);
                pstm.executeUpdate();
                pstm = con.prepareStatement(props.getValue("queryIChapter"));
                File directory = new File(baseDir + "\\" + chapter_num);
                if(directory.exists()){} else{
                    directory.mkdir();
                    AsyncThread.executeThread(new SendEmails(manga_id));
                }
                pstm.setInt(1, manga_id);
                pstm.setInt(2, chapter_num);
                pstm.setString(3, "wip");
                pstm.setDate(4, ServiceMethods.getDate());
                pstm.setString(5, manga_id + "/" + chapter_num);
                pstm.setInt(6, 0);
                pstm.executeUpdate();
                ResBuilderService.BuildOk(res, out);
            }

        } catch (Error | Exception e) {
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
        String option = request.getParameter("option");
        String currentChap = request.getParameter("currentChap");
        String mangaid = request.getParameter("mangaid");
        ChapterModel res = new ChapterModel();
        ObjectMapper objM = new ObjectMapper();
        objM.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objM.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        String r;
        DBAccess dbAccess = DBAccess.getInstance();
        Connection con = dbAccess.createConnection();
        PrintWriter out = response.getWriter();
        ResultSet rs = null;
        PreparedStatement pstm = null;
        switch (option) {
            case "getchapter":
                int chapterid;
                String manga_name;
                String chapter_title;
                String baseDir = props.getValue("direction")+mangaid+"\\"+currentChap;
                String serveDir = props.getValue("dbMangaDirection")+mangaid+"\\"+currentChap;
                try{
                    pstm = con.prepareStatement(props.getValue("querySChapterid"));
                    pstm.setInt(1, Integer.parseInt(mangaid));
                    pstm.setInt(2, Integer.parseInt(currentChap));
                    rs = pstm.executeQuery();
                    rs.next();
                    chapterid = rs.getInt(1);
                    pstm = con.prepareStatement(props.getValue("querySmangaName"));
                    pstm.setInt(1, Integer.parseInt(mangaid));
                    rs = pstm.executeQuery();
                    rs.next();
                    manga_name = rs.getString(1);
                    pstm = con.prepareStatement(props.getValue("querySchapterName"));
                    pstm.setInt(1,chapterid);
                    rs = pstm.executeQuery();
                    rs.next();
                    chapter_title = rs.getString(1);
                    res.setChapterid(chapterid);
                    res.setManganame(manga_name);
                    res.setChaptertitle(chapter_title);
                    int c = new File(baseDir).listFiles().length;
                    res.setMax(c);
                    res.setFiledir(serveDir);
                    ResBuilderService.BuildOk(res, out);
                }catch (Error e){
                    e.printStackTrace();
                    ResBuilderService.BuildResError(out);
                } catch (SQLException e) {
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
                break;
            case "getnumchapters":
                String dirManga = props.getValue("direction")+mangaid;
                try{

                    FileFilter directoryFilter = new FileFilter() {
                        public boolean accept(File file) {
                            return file.isDirectory();
                        }
                    };

                    File folder = new File(dirManga);
                    File[] listOfFiles = folder.listFiles(directoryFilter);
                    List<Integer> listnames = new ArrayList<>();
                    for (int i= 0; i<listOfFiles.length; i++){
                        if(listOfFiles[i].isDirectory()){
                            listnames.add(parseInt(listOfFiles[i].getName()));
                        }}
                    listnames.sort(Comparator.naturalOrder());
                    HashMap<String , String> item = new HashMap<>();
                    for (int i= 0; i<listnames.size(); i++){
                        try{
                            pstm = con.prepareStatement(props.getValue("querygetchapter_id"));
                            pstm.setInt(1,Integer.parseInt(mangaid));
                            pstm.setInt(2,listnames.get(i));
                            rs = pstm.executeQuery();
                            if(rs.next()){
                                item.put("id"+ (i), String.valueOf(rs.getInt(1)));
                                item.put("nombre"+(i),(listnames.get(i)).toString());
                            }

                        }catch (SQLException e){e.printStackTrace();}
                    }
                    ResBuilderService.BuildOk(item, out);
                }catch (Error e){
                    e.printStackTrace();
                    ResBuilderService.BuildResError(out);
                }finally {
                    if (con != null){
                        dbAccess.closeConnection(con);
                    }
                    if (rs != null){
                        try {
                            rs.close();
                        } catch (SQLException e1) {
                            ResBuilderService.BuildResError(out);
                            e1.printStackTrace();
                        }
                    }
                }
                break;


        }

    }
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        PropertiesReader props = PropertiesReader.getInstance();
        DBAccess dbAccess = DBAccess.getInstance();
        Connection con = dbAccess.createConnection();
        String currentChap = request.getParameter("currentChap");
        String mangaid = request.getParameter("mangaid");
        ChapterModel res = new ChapterModel();
        ObjectMapper objM = new ObjectMapper();
        objM.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objM.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        PrintWriter out = response.getWriter();
        try(PreparedStatement pstm = con.prepareStatement(props.getValue("queryDChapter"))){
            FileUtils.deleteDirectory(new File(props.getValue("direction")+mangaid+"\\"+currentChap));
            pstm.setInt(1, Integer.parseInt(mangaid));
            pstm.setInt(2, Integer.parseInt(currentChap));
            pstm.executeUpdate();
            ResBuilderService.BuildOKEmpty(out);
        }
        catch (Error | SQLException e){
            e.printStackTrace();
            ResBuilderService.BuildResError(out);
        }finally {
            if (con != null){
                dbAccess.closeConnection(con);
            }

        }

    }
}