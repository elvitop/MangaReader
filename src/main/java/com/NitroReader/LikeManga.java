package com.NitroReader;

import com.NitroReader.Command.*;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import models.Manga;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.stream.Collectors;

@WebServlet("/LikeManga")
public class LikeManga extends HttpServlet {


    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ObjectMapper objM = new ObjectMapper();
        HttpSession session = request.getSession(false);
        objM.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objM.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        objM.setSerializationInclusion(JsonInclude.Include.NON_DEFAULT);

        PrintWriter out = response.getWriter();

        Manga manga = objM.readValue(request.getReader().lines().collect(Collectors.joining(System.lineSeparator())), Manga.class);
        int userid = (int) session.getAttribute("id");
        int mangaid = manga.getManga_id();
        String switchState = manga.getSwitchState();

        LikeM likeM = new LikeM();
        Command flipUplike = new FlipUpLikeM(likeM);
        Command flipDownlike = new FlipDownLikeM(likeM);
        SwitchLike mySwitch = new SwitchLike(flipUplike, flipDownlike);
        switch (switchState) {
            case "ON":
                mySwitch.flipUp(userid, mangaid, out);
                break;
            case "OFF":
                mySwitch.FlipDown(userid, mangaid, out);
                break;

        }


    }
}
