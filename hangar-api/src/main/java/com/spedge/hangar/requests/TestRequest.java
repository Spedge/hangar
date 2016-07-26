package com.spedge.hangar.requests;

import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/test")
public class TestRequest
{
    /**
     * Simple way of looking at the headers of a request.
     * Handy when working with Dropwizard.
     * 
     * @param httpHeaders Headers of the request
     * @return Responses
     */
    @PUT
    @Path("/headers")
    public Response testThing(@Context HttpHeaders httpHeaders)
    {
        for (MediaType mt : httpHeaders.getAcceptableMediaTypes())
        {
            System.out.println("MediaType : " + mt.getType());
        }

        return Response.ok().build();
    }
}
