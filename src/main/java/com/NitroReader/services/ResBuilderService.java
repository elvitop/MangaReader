package com.NitroReader.services;

import com.NitroReader.Builder.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.PrintWriter;

public class ResBuilderService<T> {
    public static void BuildResError( PrintWriter out) throws JsonProcessingException {
        ObjectMapper objM = new ObjectMapper();
        Response rf;
        String r;
        String data = "No data";
        ResponseBuilder<String> reserr = new ResErrorBuilder<>();
        Director<String> director = new Director<>();
        director.setResponseBuilder(reserr);
        director.construirResponse(data);
        rf = director.getResponse();
        r= objM.writeValueAsString(rf);
        out.print(r);
    }

    public static void BuildOKEmpty(PrintWriter out) throws JsonProcessingException {
        ObjectMapper objM = new ObjectMapper();
        Response rf;
        String r;
        String data = "No data";
        ResponseBuilder<String> resok = new ResOKBuilder<>();
        Director<String> director = new Director<>();
        director.setResponseBuilder(resok);
        director.construirResponse(data);
        rf = director.getResponse();
        r= objM.writeValueAsString(rf);
        out.print(r);
    }

    public static <T> void BuildOk(T data, PrintWriter out) throws JsonProcessingException {
        ObjectMapper objM = new ObjectMapper();
        Response rf;
        String r;
        ResponseBuilder<T> resok = new ResOKBuilder();
        Director<T> director = new Director<>();
        director.setResponseBuilder(resok);
        director.construirResponse(data);
        rf = director.getResponse();
        r= objM.writeValueAsString(rf);
        out.print(r);
    }
}
