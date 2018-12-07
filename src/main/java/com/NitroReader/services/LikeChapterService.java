package com.NitroReader.services;

import com.NitroReader.utilities.DBAccess;
import com.NitroReader.utilities.PropertiesReader;
import models.ChapterCommentsLikesModel;

import models.Response;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public class LikeChapterService {

    //METHOD TO RETURN THE QUANTITY OF LIKES THAT HAVE A MANGA
    public static int countLikesChapter(PreparedStatement pstm, int chapter_id) throws SQLException {
        ResultSet rs = null;
        int numLikes = 0;
        try {
            pstm.setInt(1, chapter_id);
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
    public static boolean userLikeChapter(PreparedStatement pstm, int chapter_id, int user_id) throws SQLException {
        ResultSet rs = null;
        boolean isLike = false;
        try {
            pstm.setInt(1, chapter_id);
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





}
