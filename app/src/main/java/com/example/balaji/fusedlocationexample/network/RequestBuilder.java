package com.example.balaji.fusedlocationexample.network;

import com.example.balaji.fusedlocationexample.network.RequestBlocks.MethodType;
import com.example.balaji.fusedlocationexample.network.RequestBlocks.MethodTypeParemeters;
import com.example.balaji.fusedlocationexample.network.RequestBlocks.ReqestFor;


public class RequestBuilder {
	
	public  static String PrepareRequest( MethodType methodType,String key, RequestBlocks.GetBy getBy, String value, RequestBlocks.Days ofDays) throws Exception
    {
        return MethodTypeParemeters.GetParameters(methodType)+"?key="+key+"&"+ ReqestFor.PrepareQueryParameter(getBy, value)+"&"+ ReqestFor.PrepareDays(ofDays);
    }

    public static String PrepareRequest(MethodType methodType, String key, RequestBlocks.GetBy getBy, String value) throws Exception
    {
        return MethodTypeParemeters.GetParameters(methodType)+ "?key="+ key+ "&"+ ReqestFor.PrepareQueryParameter(getBy, value);
    }

    public static String PrepareRequestByLatLong(MethodType methodType, String key, String latitude, String longitude, RequestBlocks.Days ofDays) throws Exception
    {
        return MethodTypeParemeters.GetParameters(methodType)+ "?key="+ key+ "&"+ ReqestFor.LatLong(latitude,longitude)+ "&"+ ReqestFor.PrepareDays(ofDays);
    }

    public static String PrepareRequestByLatLong(MethodType methodType, String key, String latitude, String longitude) throws Exception
    {
        return MethodTypeParemeters.GetParameters(methodType)+ "?key="+ key+ "&"+ ReqestFor.LatLong(latitude, longitude);
    }

    public static String PrepareRequestByAutoIP(MethodType methodType, String key, RequestBlocks.Days ofDays ) throws Exception 
    {
        return MethodTypeParemeters.GetParameters(methodType)+ "?key="+ key+ "&"+ ReqestFor.AutoIP()+ "&"+ ReqestFor.PrepareDays(ofDays);
    }

    public static String PrepareRequestByAutoIP(MethodType methodType, String key) throws Exception
    {
        return MethodTypeParemeters.GetParameters(methodType)+ "?key="+ key+ "&"+ ReqestFor.AutoIP();
    }
}

