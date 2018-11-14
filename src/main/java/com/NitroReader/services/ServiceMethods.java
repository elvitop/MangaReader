package com.NitroReader.services;

import models.Response;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;

public class ServiceMethods {
    static <T> void setResponse(Response<T> res, int status, String message, T object){
        res.setStatus(status);
        res.setMessage(message);
        res.setData(object);
    }

    static void insertGenres(String[] genres, int manga_id, PreparedStatement pstm) throws SQLException {
        for (String genre: genres) {
            pstm.setInt(1, Integer.valueOf(genre));
            pstm.setInt(2, manga_id);
            pstm.executeUpdate();
            pstm.clearParameters();
        }
    }

    public static java.sql.Date getDate(){
        java.util.Date date = new java.util.Date();
        return new java.sql.Date(date.getTime());
    }
}
