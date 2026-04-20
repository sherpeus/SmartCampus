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
public class LinkedResourceNotFoundExceptionMapper implements ExceptionMapper<
LinkedResourceNotFoundException> {

@Override
public Response toResponse(LinkedResourceNotFoundException exception) {
    
        ErrorMessage errorMessage = new ErrorMessage("The given room id is invalid",422);
        return Response.status(422).entity(errorMessage).build();
}
}