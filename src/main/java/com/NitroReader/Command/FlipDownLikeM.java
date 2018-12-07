package com.NitroReader.Command;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.PrintWriter;

public class FlipDownLikeM implements Command {
    private LikeM thelike;

    public FlipDownLikeM(LikeM likeM){
        this.thelike = likeM;
    }


    public void execute(int userid, int id, PrintWriter out) throws JsonProcessingException {
        thelike.off(userid, id, out);
    }
}
