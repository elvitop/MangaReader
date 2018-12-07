package com.NitroReader.Builder;

public class ResOKBuilder<T> extends ResponseBuilder<T> {
    //este es un concreteBuilder
    public ResOKBuilder(){super.response = new Response<T>();}
    public void buildMessage(){response.setMessage("ok");}
    public void buildStatus(){response.setStatus(200);}
    public void buildData(T data){response.setData(data);}
}
