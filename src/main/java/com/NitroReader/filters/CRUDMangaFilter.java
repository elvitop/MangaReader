package com.NitroReader.filters;

import com.NitroReader.utilities.DBAccess;
import com.NitroReader.utilities.PropertiesReader;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@WebFilter(filterName = "CRUDMangaFilter")
public class CRUDMangaFilter implements Filter {
    public void destroy() {
    }

    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws ServletException, IOException {
        HttpServletRequest httpRequest = (HttpServletRequest) req;
        if (httpRequest.getMethod().equalsIgnoreCase("POST")){
            PropertiesReader props = PropertiesReader.getInstance();
            DBAccess dbAccess = DBAccess.getInstance();
            Connection con = dbAccess.createConnection();
            ResultSet rs = null;
            try(PreparedStatement pstm = con.prepareStatement(props.getValue("querySMangaId"), ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)){
                pstm.setString(1, httpRequest.getParameter("manga_name"));
                rs = pstm.executeQuery();
                if (rs.next()){
                    req.setAttribute("mangaExist", true);
                }else{
                    req.setAttribute("mangaExist", false);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }finally {
                if (rs != null){
                    try {
                        rs.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        chain.doFilter(req, resp);

    }

    public void init(FilterConfig config) throws ServletException {

    }

}
