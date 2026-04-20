/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.resource;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.*;
import javax.ws.rs.core.Response;
/**
 *
 * @author lenovo
 */
@Path("/")
public class DiscoveryResource {
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDiscoveryInformation() {
        Map<String, Object> discoveryData = new HashMap<>();
        
        // 1. Versioning info 
        discoveryData.put("api_version", "1.0.0");
        
        // 2. Administrative contact details 
        discoveryData.put("admin_contact", "foobar@baafoor.iit.ac.lk");
        discoveryData.put("campus", "IIT Sri Lanka");

        // 3. Map of primary resource
        Map<String, String> links = new HashMap<>();
        links.put("rooms", "/api/v1/rooms");
        links.put("sensors", "/api/v1/sensors");
        
        discoveryData.put("links", links);
        return Response.ok(discoveryData).build();
    }

    
}
