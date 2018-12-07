package com.NitroReader.Builder;

public abstract class ResponseBuilder <T>{
    //este se llama el abstract builder
    protected Response<T> response;

    public Response<T> getResponse() {
        return response;
    }

    public abstract  void buildMessage();
    public abstract  void buildStatus();
    public  abstract void buildData(T data);
}
