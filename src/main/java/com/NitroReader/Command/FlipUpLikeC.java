package com.NitroReader.Command;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.PrintWriter;

public class FlipUpLikeC implements Command{
    private LikeC thelike;

    public FlipUpLikeC(LikeC likeC){
        this.thelike = likeC;
    }

    @Override
    public void execute(int userid, int id, PrintWriter out) throws JsonProcessingException {
        thelike.on(userid, id, out);
    }
}
