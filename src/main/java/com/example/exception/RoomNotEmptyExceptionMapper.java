/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.exception;

/**
 *
 * @author lenovo
 */
import com.example.model.ErrorMessage;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider 
public class RoomNotEmptyExceptionMapper implements ExceptionMapper<
RoomNotEmptyException> {

@Override
public Response toResponse(RoomNotEmptyException exception) {
    
        ErrorMessage errorMessage = new ErrorMessage("The room is currently occupied by active hardware and cannot be deleted.",409);
        return Response.status(Response.Status.CONFLICT).entity(errorMessage).build();
}
}