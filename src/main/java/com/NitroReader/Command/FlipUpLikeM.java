package com.NitroReader.Command;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.PrintWriter;

public class FlipUpLikeM implements Command {
    private LikeM thelike;

    public FlipUpLikeM(LikeM likeM){
        this.thelike = likeM;
    }

    @Override
    public void execute(int userid, int id, PrintWriter out) throws JsonProcessingException {
        thelike.on(userid, id, out);
    }
}
