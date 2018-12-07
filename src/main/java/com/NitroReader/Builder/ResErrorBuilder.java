package com.NitroReader.Builder;

public class ResErrorBuilder<T> extends ResponseBuilder<T> {
    //este es un concreteBuilder
    public ResErrorBuilder(){super.response = new Response<T>();}
    public void buildMessage(){response.setMessage("error");}
    public void buildStatus(){response.setStatus(500);}
    public void buildData(T data){response.setData(data);}
}
