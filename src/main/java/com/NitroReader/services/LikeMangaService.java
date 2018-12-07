package com.NitroReader.services;

import com.NitroReader.utilities.DBAccess;
import com.NitroReader.utilities.PropertiesReader;
import models.Manga;
import models.Response;

import java.sql.*;
import java.util.HashMap;


public class LikeMangaService {

    //METHOD FOR LIKE A MANGA
    public static void likeManga(Manga manga, Response<HashMap<String,Object>> res){

    }

    //METHOD TO RETURN THE QUANTITY OF LIKES THAT HAVE A MANGA
    static int countLikesManga(PreparedStatement pstm, int manga_id) throws SQLException {
        ResultSet rs = null;
        int numLikes = 0;
        try {
            pstm.setInt(1, manga_id);
            rs = pstm.executeQuery();
            if (rs.next()){
                numLikes = rs.getInt(1);
            }
        }finally {
            if (rs != null){
                rs.close();
            }
        }
        return numLikes;
    }

    //METHOD TO RETURN IF THE USER LIKES THE MANGA
    static boolean userLikeManga(PreparedStatement pstm, int manga_id, int user_id) throws SQLException {
        ResultSet rs = null;
        boolean isLike = false;
        try {
            pstm.setInt(1, manga_id);
            pstm.setInt(2, user_id);
            rs = pstm.executeQuery();
            if (rs.next()){
                isLike = true;
            }
        }finally {
            if (rs != null){
                rs.close();
            }
        }
        return isLike;
    }

    //METHOD TO DELETE THE LIKE OF THE MANGA
    public static  void deleteLike(Manga manga, Response<HashMap<String,Object>> res){

    }
}
