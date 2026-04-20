/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.exception;
import com.example.model.ErrorMessage;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
/**
 *
 * @author lenovo
 */


@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Throwable> {

    @Override
    public Response toResponse(Throwable exception) {
        
        exception.printStackTrace(); 

        
        ErrorMessage errorMessage = new ErrorMessage("An unexpected server error occurred. We will look into it soon :D",500);

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                       .entity(errorMessage)
                       .build();
    }
}