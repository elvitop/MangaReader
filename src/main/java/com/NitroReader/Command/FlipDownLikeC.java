package com.NitroReader.Command;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.PrintWriter;

public class FlipDownLikeC implements Command {
    private LikeC thelike;

    public FlipDownLikeC(LikeC likeC){
        this.thelike = likeC;
    }


    public void execute(int userid, int id, PrintWriter out) throws JsonProcessingException {
        thelike.off(userid, id, out);
    }
}
